package org.egov.audit.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.egov.audit.entity.AuditDetails;
import org.egov.audit.entity.AuditPostBillMpng;
import org.egov.audit.entity.AuditPostVoucherMpng;
import org.egov.audit.model.AuditDetail;
import org.egov.audit.model.ManageAuditor;
import org.egov.audit.repository.AuditRepository;
import org.egov.audit.utils.AuditConstants;
import org.egov.audit.utils.AuditUtils;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.eis.entity.Assignment;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.RetrachmentDetails;
import org.egov.pims.commons.Position;
import org.egov.utils.FinancialConstants;
import org.hibernate.Session;
import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditService {
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("yyyy-MM-dd", LOCALE);
	private static final Logger LOGGER = Logger.getLogger(AuditService.class);
	@Autowired
	private AuditRepository auditRepository;
	private final ScriptService scriptExecutionService;
	@Autowired
	protected AppConfigValueService appConfigValuesService;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	private AuditUtils auditUtils;
	@Autowired
	private SecurityUtils securityUtils;
	@Autowired
	@Qualifier("workflowService")
	private SimpleWorkflowService<AuditDetails> auditRegisterWorkflowService;
	@Autowired
	private MicroserviceUtils microServiceUtil;
	@Autowired
	private ExpenseBillService expenseBillService;
	@Autowired
	private FinancialUtils financialUtils;
	@Autowired
	private EgwStatusHibernateDAO egwStatusDAO;
	@Autowired
	private ManageAuditorService manageAuditorService;
	@Autowired
	protected MicroserviceUtils microserviceUtils;

	@Autowired
	public AuditService(final ScriptService scriptExecutionService) {
		this.scriptExecutionService = scriptExecutionService;
	}

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public AuditDetails getById(final Long id) {
		return auditRepository.findOne(id);
	}

	public AuditDetails findByid(final Long id) {
		return auditRepository.findById(id);
	}
	
	public AuditDetails findByEgBillregister(EgBillregister egBillregister) {
		return auditRepository.findByEgBillregister(egBillregister);
	}

	public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
		return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	}

	@Transactional
	public AuditDetails create(final AuditDetails auditDetails, final String workFlowAction, String comment,
			Long leadEmpNo, Long approvalPosition, String apporverDesignation) {

		AuditDetails savedAuditDetails = auditRepository.save(auditDetails);
		EgBillregister bill = null;

		if (workFlowAction.equalsIgnoreCase("department")) {
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_DEPARTMENT));
		} else if (workFlowAction.equalsIgnoreCase("sectionOfficer")) {
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_SECTION_OFFICER));
		} else if (workFlowAction.equalsIgnoreCase("auditor") || workFlowAction.equals("Approve")
				|| workFlowAction.equalsIgnoreCase("saveAsDraft")) {
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_AUDITOR));
		} else if (workFlowAction.equalsIgnoreCase("examiner")) {
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_EXAMINER));
		} else if (savedAuditDetails.getState() != null && workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONVERIFY))
        {//added abhishek for verify audit status
			savedAuditDetails.setStatus(financialUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_DEPARTMENT));
        } 
		else if (workFlowAction.equals("approve")) {
			savedAuditDetails.setStatus(
					auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT, AuditConstants.AUDIT_APPROVED_STATUS));
			if (savedAuditDetails.getType().equals("Pre-Audit")) {
				bill = savedAuditDetails.getEgBillregister();
				if (bill.getRefundable() != null && bill.getRefundable().equalsIgnoreCase("Y")) {
					bill.setStatus(financialUtils.getStatusByModuleAndCode("REFUNDBILL",
							FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS));
				} else {
					bill.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.CONTINGENCYBILL_FIN,
							FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS));
				}

			} else {
				for (AuditPostBillMpng row : savedAuditDetails.getPostBillMpng()) {
					bill = expenseBillService.getById(row.getEgBillregister().getId());
					bill.setStatus(egwStatusDAO.getStatusByModuleAndCode("EXPENSEBILL", "Bill Payment Approved"));
					expenseBillService.create(bill);
				}

				for (AuditPostVoucherMpng row : savedAuditDetails.getPostVoucherMpng()) {
					persistenceService.getSession()
							.createSQLQuery("update voucherheader set postauditprocessing = null where id =:vhId")
							.setLong("vhId", row.getVoucherheader().getId()).executeUpdate();
				}
			}

		} else if (workFlowAction.equalsIgnoreCase("reject")) {
			final User user = securityUtils.getCurrentUser();
			final DateTime currentDate = new DateTime();
			savedAuditDetails.setStatus(
					auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT, AuditConstants.AUDIT_REJECTED_STATUS));
			bill = savedAuditDetails.getEgBillregister();
			bill.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.CONTINGENCYBILL_FIN,
					FinancialConstants.CONTINGENCYBILL_REJECTED_STATUS));
			Position owenrPos = new Position();
			owenrPos.setId(auditDetails.getCreatedBy());
			bill.transition().startNext().withSenderName(user.getUsername() + "::" + user.getName())
					.withComments(comment).withStateValue("Rejected").withDateInfo(currentDate.toDate())
					.withOwner(owenrPos)
					.withOwnerName(
							(owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId())
									: "")
					.withNextAction("").withNatureOfTask(FinancialConstants.WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME)
					.withCreatedBy(user.getId()).withtLastModifiedBy(user.getId());
		}

		createAuditWorkflowTransition(savedAuditDetails, workFlowAction, comment, leadEmpNo, approvalPosition,
				apporverDesignation);
		List<DocumentUpload> files = auditDetails.getDocumentDetail() == null ? null : auditDetails.getDocumentDetail();
		final List<DocumentUpload> documentDetails;

		documentDetails = auditUtils.getDocumentDetails(files, savedAuditDetails,
				AuditConstants.FILESTORE_MODULEOBJECT);
		if (!documentDetails.isEmpty()) {
			savedAuditDetails.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}

		AuditDetails auditReg = auditRepository.save(savedAuditDetails);
		if (workFlowAction.equalsIgnoreCase("approve") || workFlowAction.equalsIgnoreCase("reject")) {
			if (bill != null) {
				expenseBillService.create(bill);
			}
		}

		persistenceService.getSession().flush();
		return auditReg;
	}

	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}

	public void createAuditWorkflowTransition(final AuditDetails auditDetails, final String workFlowAction,
			String comment, Long leadEmpNo, Long approvalPosition, String apporverDesignation) {
		LOGGER.info(" Create WorkFlow Transition Started  ...");
		final User user = securityUtils.getCurrentUser();
		final DateTime currentDate = new DateTime();
		String actionName = "";
		String natureOfTask = "";

		String expType = "";
		if (auditDetails.getType() != null && auditDetails.getType().equals("Pre-Audit")) {
			actionName = "Pre Audit pending";
			natureOfTask = "Pre-Audit";
		} else {
			if (auditDetails.getPostVoucherMpng() != null && !auditDetails.getPostVoucherMpng().isEmpty()) {
				expType = "Receipt";
			}

			if (auditDetails.getPostBillMpng() != null && !auditDetails.getPostBillMpng().isEmpty()) {
				expType = "Post";
			}
			actionName = "Post Audit pending";
			natureOfTask = "Post-Audit";
		}
		if ((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equals("Verify") || workFlowAction.equals("Approve"))) {
			System.out.println("logic for workflow ::: " + workFlowAction);
			String stateValue = "";
			WorkFlowMatrix wfmatrix;
			Map<String, String> finalDesignationNames = new HashMap<>();
			Designation designation = this.getDesignationDetails(apporverDesignation);
			Position owenrPos = new Position();
			owenrPos.setId(approvalPosition);

			wfmatrix = auditRegisterWorkflowService.getWfMatrix(auditDetails.getStateType(), null, null, null,
					FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING, null);
			if (wfmatrix != null && wfmatrix.getCurrentDesignation() != null) {
				final List<String> finalDesignationName = Arrays.asList(wfmatrix.getCurrentDesignation().split(","));
				for (final String desgName : finalDesignationName)
					if (desgName != null && !"".equals(desgName.trim()))
						finalDesignationNames.put(desgName.toUpperCase(), desgName.toUpperCase());
			}
			if (workFlowAction.equals("Approve")) {
				System.out.println("Approve");
				owenrPos.setId(auditDetails.getLead_auditor());
				auditDetails.transition().progressWithStateCopy()
						.withSenderName(user.getUsername() + ":" + user.getName()).withComments(comment)
						.withStateValue("Pending with Auditor").withDateInfo(new Date()).withOwner(owenrPos)
						.withOwnerName(
								(owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId())
										: "")
						.withNextAction(actionName).withNatureOfTask(natureOfTask).withCreatedBy(user.getId())
						.withtLastModifiedBy(user.getId());
			}
			else if(workFlowAction.contentEquals("Verify")) {//added by abhishek
					System.out.println("auditDetails.getStateType()--- "+auditDetails.getStateType());
					System.out.println("auditDetails.getCurrentState().getValue()--- "+auditDetails.getCurrentState().getValue());
				 wfmatrix = auditRegisterWorkflowService.getWfMatrix(auditDetails.getStateType(), null, null, null,
							auditDetails.getCurrentState().getValue(), null);
		            if (stateValue.isEmpty())
		                stateValue = wfmatrix.getNextState();
		            if(auditDetails.getCurrentState().getValue().equalsIgnoreCase("Final Approval Pending")) {
		            	stateValue="Pending With Audit";
		            	auditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
		    					AuditConstants.AUDIT_PENDING_WITH_DEPARTMENT));
		            }
		            if(auditDetails.getCurrentState().getValue().equalsIgnoreCase("Pending With Audit")) {
		            	stateValue="Pending With Auditor";
		            	auditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
		    					AuditConstants.AUDIT_PENDING_WITH_AUDITOR));
		            }
				List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
	                       FinancialConstants.MODULE_NAME_APPCONFIG, "AUDIT_DEFAULT");
	            owenrPos=new Position();
	            owenrPos.setId(Long.valueOf(configValuesByModuleAndKey.get(0).getValue()));
	            auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
	              .withComments(comment)
	              .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
	              .withNextAction(actionName)
	               .withNatureOfTask(natureOfTask);
			}
			else {
				System.out.println("Forward");
				if (designation != null && finalDesignationNames.get(designation.getName().toUpperCase()) != null)
					stateValue = FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING;

				System.out.println("stateValue before::: " + stateValue);

				wfmatrix = auditRegisterWorkflowService.getWfMatrix(auditDetails.getStateType(), null, null, null,
						auditDetails.getCurrentState().getValue(), null);

				if (stateValue.isEmpty()) {
					if (!wfmatrix.getNextState().equalsIgnoreCase(FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING)
							&& !wfmatrix.getNextState().equalsIgnoreCase("NEW")) {
						stateValue = wfmatrix.getNextState() + " " + designation.getName().toUpperCase();
					} else if (wfmatrix.getNextState().equalsIgnoreCase("NEW")) {
						stateValue = "Pending With " + designation.getName().toUpperCase();
					} else {
						stateValue = wfmatrix.getNextState();

					}

				}
				System.out.println("stateValue before::: " + stateValue);

				auditDetails.transition().progressWithStateCopy()
						.withSenderName(user.getUsername() + "::" + user.getName()).withComments(comment)
						.withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos)
						.withOwnerName(
								(owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId())
										: "")
						.withNextAction(actionName).withNatureOfTask(natureOfTask).withCreatedBy(user.getId())
						.withtLastModifiedBy(user.getId());

			}
		}
		if (workFlowAction.equalsIgnoreCase("department")) {
			Position owenrPos = new Position();
			owenrPos.setId(auditDetails.getCreatedBy());
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
					.withComments(comment).withStateValue("NEW").withDateInfo(new Date()).withOwner(owenrPos)
					.withOwnerName(
							(owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId())
									: "")
					.withNextAction(actionName).withNatureOfTask(natureOfTask).withCreatedBy(user.getId())
					.withtLastModifiedBy(user.getId());
		} else if (workFlowAction.equalsIgnoreCase("sectionOfficer")
				|| workFlowAction.equalsIgnoreCase("saveAsDraft")) {

			List<ManageAuditor> auditorList = manageAuditorService
					.getAudiorsDepartmentByType(Integer.parseInt(auditDetails.getDepartment()), "RSA");
			Position owenrPos = new Position();
			String state = "Pending with Section Officer";
			if (workFlowAction.equalsIgnoreCase("saveAsDraft")) {
				owenrPos.setId(user.getId());
				state = "Created";
			} else if (leadEmpNo != null) {
				owenrPos.setId(leadEmpNo);
			} else if (auditorList != null && !auditorList.isEmpty()) {
				owenrPos.setId(Long.valueOf(auditorList.get(0).getEmployeeid()));
			} else {
				owenrPos.setId(null);
			}
			auditDetails.setRsa_id(owenrPos.getId());
			auditDetails.setRsa_name(getEmployeeName(owenrPos.getId()));
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
					.withComments(comment).withStateValue(state).withDateInfo(new Date()).withOwner(owenrPos)
					.withOwnerName(
							(owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId())
									: "")
					.withNextAction(actionName).withNatureOfTask(natureOfTask).withCreatedBy(user.getId())
					.withtLastModifiedBy(user.getId());
		} else if (workFlowAction.equalsIgnoreCase("auditor")) {
			List<ManageAuditor> auditorList = null;
			if (expType.equalsIgnoreCase("Receipt")) {

				auditorList = manageAuditorService.getAudiorsDepartmentByType(Integer.parseInt(auditDetails
						.getPostVoucherMpng().get(0).getVoucherheader().getVouchermis().getDepartmentcode()),
						"Auditor");
			} else if (expType.equalsIgnoreCase("Post")) {
				auditorList = manageAuditorService.getAudiorsDepartmentByType(Integer.parseInt(auditDetails
						.getPostBillMpng().get(0).getEgBillregister().getEgBillregistermis().getDepartmentcode()),
						"Auditor");
			} else {
				auditorList = manageAuditorService.getAudiorsDepartmentByType(
						Integer.parseInt(auditDetails.getEgBillregister().getEgBillregistermis().getDepartmentcode()),
						"Auditor");
			}

			Position owenrPos = new Position();
			if (auditorList != null && !auditorList.isEmpty()) {
				owenrPos.setId(Long.valueOf(auditorList.get(0).getEmployeeid()));
			} else {
				owenrPos.setId(null);
			}
			auditDetails.setLead_auditor(owenrPos.getId());
			auditDetails.setAuditor_name(getEmployeeName(owenrPos.getId()));
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
					.withComments(comment).withStateValue("Pending with Auditor").withDateInfo(new Date())
					.withOwner(owenrPos)
					.withOwnerName(
							(owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId())
									: "")
					.withNextAction(actionName).withNatureOfTask(natureOfTask).withCreatedBy(user.getId())
					.withtLastModifiedBy(user.getId());
		} else if (workFlowAction.equalsIgnoreCase("examiner")) {
			List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
					FinancialConstants.MODULE_NAME_APPCONFIG, AuditConstants.AUDIT_EXAMINER);
			Position owenrPos = new Position();
			if (configValuesByModuleAndKey != null && !configValuesByModuleAndKey.isEmpty()) {
				owenrPos.setId(Long.parseLong(configValuesByModuleAndKey.get(0).getValue()));
			} else {
				owenrPos.setId(null);
			}
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
					.withComments(comment).withStateValue("Pending with Examiner").withDateInfo(new Date())
					.withOwner(owenrPos)
					.withOwnerName(
							(owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId())
									: "")
					.withNextAction(actionName).withNatureOfTask(natureOfTask).withCreatedBy(user.getId())
					.withtLastModifiedBy(user.getId());
		} else if (workFlowAction.equals("approve")) {
			auditDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
					.withComments(comment).withStateValue("Approved").withDateInfo(new Date())
					.withNextAction(natureOfTask + " Approved").withNatureOfTask(natureOfTask);
		} else if (workFlowAction.equalsIgnoreCase("reject")) {
			auditDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
					.withComments(comment).withStateValue("Rejected").withDateInfo(new Date())
					.withNextAction(natureOfTask + " Rejected").withNatureOfTask(natureOfTask);
		}
		LOGGER.info(" WorkFlow Transition Completed  ...");
	}

	public EgBillregister getBillDetails(long billId) {
		return expenseBillService.getById(billId);
	}

	public AuditDetails getByAudit_no(final String auditNumber) {
		return auditRepository.findByAuditno(auditNumber);
	}

	private Designation getDesignationDetails(String desgnCode) {
		List<Designation> desgnList = microServiceUtil.getDesignation(desgnCode);
		return desgnList.get(0);
	}

	public List<AuditDetails> getAllAudit() {
		return auditRepository.findAll();
	}

	public String getEmployeeName(Long empId) {

		return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	}

	public List<RetrachmentDetails> getRetranchmentRegisterData(AuditDetail auditDetail) {
		 List<RetrachmentDetails> lst = new ArrayList<RetrachmentDetails>();
			
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
	        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			/*
			 * String fromDateNew="", toDateNew=""; try { fromDateNew =
			 * sdf2.format(sdf1.parse(auditDetail.getFromDate())); toDateNew =
			 * sdf2.format(sdf1.parse(auditDetail.getToDate())); } catch (ParseException e)
			 * { // TODO Auto-generated catch block e.printStackTrace(); }
			 */
		      
		     	List<Object[]> list= null;//,to_char(r.date, 'dd-Mon-yyyy') as rdate
				String sql="select to_char(r.retrachmentdate,'dd-Mon-yyyy') as rdate,r.department_name,r.amountofbill,"+
		     	" r.amountbyaudit,r.amountretrached,r.billdetail,r.remarks, r.auditid from RetrachmentDetails r "+
				" where r.retrachmentdate >= '"+AuditConstants.DDMMYYYYFORMAT1.format(auditDetail.getFromDate())+"' and date(r.retrachmentdate) <= '"+AuditConstants.DDMMYYYYFORMAT1.format(auditDetail.getToDate())+"'";
			/*
			 * String
			 * sql="select to_char(r.retrachmentdate,'dd-Mon-yyyy') as rdate,r.department_name,r.amountofbill,r.amountbyaudit,r.amountretrached,r.billdetail,r.remarks, r.auditid from RetrachmentDetails r where r.retrachmentdate >= to_date('"
			 * +fromDateNew+"','dd/mm/yyyy') and date(r.retrachmentdate) <= to_date('"
			 * +toDateNew+"','dd/mm/yyyy')";
			 */		
				if(auditDetail.getDepartment() != null) {
						sql+=" and r.department_name ='"+auditDetail.getDepartment()+"'";
				}
				System.out.println("query::: "+sql);
				org.hibernate.Query query = persistenceService.getSession().createSQLQuery(sql);
				list = query.list();
				if(list!=null) {
					for (final Object[] o : list) {
						RetrachmentDetails r=new RetrachmentDetails();
						r.setRetdate(o[0]!=null?o[0].toString():null);
						r.setDepartment_name(o[1]!=null?o[1].toString():"");
						r.setAmountofbill(o[2]!=null?new BigDecimal(o[2].toString()):new BigDecimal("0"));
						r.setAmountbyaudit(o[3]!=null?new BigDecimal(o[3].toString()):new BigDecimal("0"));
						r.setAmountretrached(o[4]!=null?new BigDecimal(o[4].toString()):new BigDecimal("0"));
						r.setBilldetail(o[5]!=null?o[5].toString():"");
						r.setRemarks(o[6]!=null?o[6].toString():"");
						r.setAuditid(o[7]!=null?o[7].toString():"");
						String statusQuery = "select s.description from egw_status s, audit_details a where s.id=a.status_id and a.id="+r.getAuditid();
						org.hibernate.Query statusdesc = persistenceService.getSession().createSQLQuery(statusQuery);
						List<String> statusList = null;
						statusList = statusdesc.list(); 
						if(statusList != null) {
							for(String d : statusList) {
								r.setStatus(d != null? d: null );
							}
						}
						lst.add(r);
					}
				}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	public Date getFormattedDateasDate(Date fDate) throws ParseException {
		String dateString = getFormattedDate(fDate);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date date = sdf.parse(dateString);
	    return date;
	}
	
	public String getFormattedDate(final Date date) {
		final SimpleDateFormat formatter = DDMMYYYYFORMAT1;
		return formatter.format(date);
	}
}
