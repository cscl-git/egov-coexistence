package org.egov.audit.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.egov.audit.entity.AuditDetails;
import org.egov.audit.entity.AuditPostBillMpng;
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
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillregister;
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
	public AuditService(final ScriptService scriptExecutionService) {
		this.scriptExecutionService = scriptExecutionService;
	}

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public AuditDetails getById(final Long id) {
		return auditRepository.findOne(id);
	}

	public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
		return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	}

	@Transactional
	public AuditDetails create(final AuditDetails auditDetails , final String workFlowAction, String comment) {


		final AuditDetails savedAuditDetails = auditRepository.save(auditDetails);
		EgBillregister bill=null;

		if(workFlowAction.equalsIgnoreCase("department"))
		{
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_DEPARTMENT));
		}
		else if(workFlowAction.equalsIgnoreCase("sectionOfficer"))
		{
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_SECTION_OFFICER));
		}
		else if(workFlowAction.equalsIgnoreCase("auditor"))
		{
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_AUDITOR));
		}
		else if(workFlowAction.equalsIgnoreCase("examiner"))
		{
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_PENDING_WITH_EXAMINER));
		}
		else if(workFlowAction.equalsIgnoreCase("approve"))
		{
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_APPROVED_STATUS));
			if(savedAuditDetails.getType().equals("Pre-Audit"))
			{
				bill=savedAuditDetails.getEgBillregister();
				bill.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.CONTINGENCYBILL_FIN,
	                    FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS));
			}
			else
			{
				for(AuditPostBillMpng row:savedAuditDetails.getPostBillMpng())
		    	 {
		    		 bill = expenseBillService.getById(row.getEgBillregister().getId());
		    		 bill.setStatus(egwStatusDAO.getStatusByModuleAndCode("EXPENSEBILL", "Bill Payment Approved"));
		    		 expenseBillService.create(bill);
		    	 }
			}
			
		}
		else if(workFlowAction.equalsIgnoreCase("reject"))
		{
			final User user = securityUtils.getCurrentUser();
	        final DateTime currentDate = new DateTime();
			savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(AuditConstants.AUDIT,
					AuditConstants.AUDIT_REJECTED_STATUS));
			bill=savedAuditDetails.getEgBillregister();
			bill.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.CONTINGENCYBILL_FIN,
                    FinancialConstants.CONTINGENCYBILL_REJECTED_STATUS));
			Position owenrPos = new Position();
			owenrPos.setId(auditDetails.getCreatedBy());
            bill.transition().startNext().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(comment)
                    .withStateValue("Rejected").withDateInfo(currentDate.toDate())
                    .withOwner(owenrPos)
                    .withNextAction("")
                    .withNatureOfTask(FinancialConstants.WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME)
                    .withCreatedBy(user.getId())
                    .withtLastModifiedBy(user.getId());
		}
		
		
		createAuditWorkflowTransition(savedAuditDetails, workFlowAction,comment);
		List<DocumentUpload> files = auditDetails.getDocumentDetail() == null ? null
				: auditDetails.getDocumentDetail();
		final List<DocumentUpload> documentDetails;
		
		documentDetails = auditUtils.getDocumentDetails(files, savedAuditDetails,
				AuditConstants.FILESTORE_MODULEOBJECT);
		if (!documentDetails.isEmpty()) {
			savedAuditDetails.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		 

		AuditDetails auditReg  = auditRepository.save(savedAuditDetails);
		if(workFlowAction.equalsIgnoreCase("approve") || workFlowAction.equalsIgnoreCase("reject"))
		{
			expenseBillService.create(bill);
		}
		
		persistenceService.getSession().flush();
		return auditReg;
	}

	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}

	public void createAuditWorkflowTransition(final AuditDetails auditDetails,final String workFlowAction, String comment) {
			LOGGER.info(" Create WorkFlow Transition Started  ...");
		final User user = securityUtils.getCurrentUser();
		final DateTime currentDate = new DateTime();
		String actionName="";
		String natureOfTask="";
		if(auditDetails.getType() !=null && auditDetails.getType().equals("Pre-Audit"))
		{
			actionName = "Pre Audit pending";
			natureOfTask = "Pre-Audit";
		}
		else
		{
			actionName = "Post Audit pending";
			natureOfTask = "Post-Audit";
		}
		if(workFlowAction.equalsIgnoreCase("department"))
		{
			Position owenrPos = new Position();
			owenrPos.setId(auditDetails.getCreatedBy());
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
	        .withComments(comment)
	        .withStateValue("Pending with Department").withDateInfo(new Date()).withOwner(owenrPos)
	        .withNextAction(actionName)
	        .withNatureOfTask(natureOfTask)
	        .withCreatedBy(user.getId())
	        .withtLastModifiedBy(user.getId());
		}
		else if(workFlowAction.equalsIgnoreCase("sectionOfficer"))
		{
			List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
	                FinancialConstants.MODULE_NAME_APPCONFIG, AuditConstants.AUDIT_SECTION_OFFICER);
	    	Position owenrPos = new Position();
	    	if(configValuesByModuleAndKey != null && !configValuesByModuleAndKey.isEmpty())
	    	{
	    		owenrPos.setId(Long.parseLong(configValuesByModuleAndKey.get(0).getValue()));
	    	}
	    	else
	    	{
	    		owenrPos.setId(null);
	    	}
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
	        .withComments(comment)
	        .withStateValue("Pending with Section Officer").withDateInfo(new Date()).withOwner(owenrPos)
	        .withNextAction(actionName)
	        .withNatureOfTask(natureOfTask)
	        .withCreatedBy(user.getId())
	        .withtLastModifiedBy(user.getId());
		}
		else if(workFlowAction.equalsIgnoreCase("auditor"))
		{
			List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
	                FinancialConstants.MODULE_NAME_APPCONFIG, FinancialConstants.AUDIT_ + auditDetails.getEgBillregister().getEgBillregistermis().getDepartmentcode());
	    	Position owenrPos = new Position();
	    	if(configValuesByModuleAndKey != null && !configValuesByModuleAndKey.isEmpty())
	    	{
	    		owenrPos.setId(Long.parseLong(configValuesByModuleAndKey.get(0).getValue()));
	    	}
	    	else
	    	{
	    		owenrPos.setId(null);
	    	}
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
	        .withComments(comment)
	        .withStateValue("Pending with Auditor").withDateInfo(new Date()).withOwner(owenrPos)
	        .withNextAction(actionName)
	        .withNatureOfTask(natureOfTask)
	        .withCreatedBy(user.getId())
	        .withtLastModifiedBy(user.getId());
		}
		else if(workFlowAction.equalsIgnoreCase("examiner"))
		{
			List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
	                FinancialConstants.MODULE_NAME_APPCONFIG, AuditConstants.AUDIT_EXAMINER);
	    	Position owenrPos = new Position();
	    	if(configValuesByModuleAndKey != null && !configValuesByModuleAndKey.isEmpty())
	    	{
	    		owenrPos.setId(Long.parseLong(configValuesByModuleAndKey.get(0).getValue()));
	    	}
	    	else
	    	{
	    		owenrPos.setId(null);
	    	}
			auditDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + ":" + user.getName())
	        .withComments(comment)
	        .withStateValue("Pending with Examiner").withDateInfo(new Date()).withOwner(owenrPos)
	        .withNextAction(actionName)
	        .withNatureOfTask(natureOfTask)
	        .withCreatedBy(user.getId())
	        .withtLastModifiedBy(user.getId());
		}
		else if(workFlowAction.equalsIgnoreCase("approve"))
		{
			auditDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(comment)
            .withStateValue("Approved").withDateInfo(new Date())
            .withNextAction(natureOfTask+" Approved")
            .withNatureOfTask(natureOfTask);
		}
		else if(workFlowAction.equalsIgnoreCase("reject"))
		{
			auditDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(comment)
            .withStateValue("Rejected").withDateInfo(new Date())
            .withNextAction(natureOfTask+" Rejected")
            .withNatureOfTask(natureOfTask);
		}
			LOGGER.info(" WorkFlow Transition Completed  ...");
	}
	
	private Assignment getCurrentUserAssignmet(Long userId){
//    	Long userId = ApplicationThreadLocals.getUserId();
    	List<EmployeeInfo> emplist = microServiceUtil.getEmployee(userId, null, null, null);
    	Assignment assignment =new Assignment();
    	if(null!=emplist && emplist.size()>0 && emplist.get(0).getAssignments().size()>0){
    		Position position = new Position();
    		position.setId(emplist.get(0).getAssignments().get(0).getPosition());
    		assignment.setPosition(position);
            
    		org.egov.pims.commons.Designation designation = new org.egov.pims.commons.Designation();
            Designation _desg = this.getDesignationDetails(emplist.get(0).getAssignments().get(0).getDesignation());
            designation.setCode(_desg.getCode());
            designation.setName(_desg.getName());
            assignment.setDesignation(designation);
            
            org.egov.infra.admin.master.entity.Department department = new org.egov.infra.admin.master.entity.Department();
            Department _dept = this.getDepartmentDetails(emplist.get(0).getAssignments().get(0).getDepartment());
            department.setCode(_dept.getCode());
            department.setName(_dept.getName());
            
            return assignment;
    	}
    	return null;
    }
	
	private Department getDepartmentDetails(String deptCode){
    	Department dept = microServiceUtil.getDepartmentByCode(deptCode);
    	return dept;
    	
    }
    
    private Designation getDesignationDetails(String desgnCode){
    	List<Designation> desgnList = microServiceUtil.getDesignation(desgnCode);
    	return desgnList.get(0);
    }

	public EgBillregister getBillDetails(long billId) {
		return expenseBillService.getById(billId);
	}
	
	public AuditDetails getByAudit_no(final String auditNumber) {
        return auditRepository.findByAuditno(auditNumber);
    }

}
