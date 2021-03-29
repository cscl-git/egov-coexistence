/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.audit.web.controller.preAudit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.egov.audit.autonumber.AuditNumberGenerator;
import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.entity.AuditChecklistHistory;
import org.egov.audit.entity.AuditDetails;
import org.egov.audit.entity.AuditPostBillMpng;
import org.egov.audit.entity.AuditPostVoucherMpng;
import org.egov.audit.entity.BillTypeCheckList;
import org.egov.audit.model.AuditBillDetails;
import org.egov.audit.model.AuditDetail;
import org.egov.audit.model.AuditEmployee;
import org.egov.audit.model.ManageAuditor;
import org.egov.audit.model.PostAuditResult;
import org.egov.audit.repository.AuditRepository;
import org.egov.audit.service.AuditService;
import org.egov.audit.service.BillTypeCheckListService;
import org.egov.audit.service.ManageAuditorService;
import org.egov.audit.utils.AuditConstants;
import org.egov.audit.utils.AuditUtils;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.service.FundService;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.Miscbilldetail;
import org.egov.pims.commons.Position;
import org.egov.services.payment.MiscbilldetailService;
import org.egov.utils.FinancialConstants;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author venki
 */

@Controller
@RequestMapping(value = "/createAudit")
public class CreateAuditController extends GenericWorkFlowController {

	private static final Logger LOGGER = Logger.getLogger(CreateAuditController.class);
	private static final int BUFFER_SIZE = 4096;
	private static final String STATE_TYPE = "stateType";
	private static final String APPROVAL_POSITION = "approvalPosition";
	private static final String APPROVAL_DESIGNATION = "approvalDesignation";

	@Autowired
	@Qualifier("messageSource")
	private MessageSource messageSource;

	@Autowired
	private FileStoreService fileStoreService;
	
	@Autowired
	 private BillTypeCheckListService billTypeCheckListService;

	@Autowired
	private AuditService auditService;
	
	@Autowired
	private DocumentUploadRepository documentUploadRepository;

	private List<AuditCheckList> checkList = null;

	@Autowired
	private AppConfigValueService appConfigValuesService;
	
	@Autowired
	private AuditUtils auditUtils;
	
	@Autowired
    private FundService fundService;
	
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	 
	private List<PostAuditResult>  resultSearchList = null;
	
	private List<AuditDetails>  resultsDtlsList = null;
	
	@Autowired
    private SecurityUtils securityUtils;
	
	@Autowired
    private ExpenseBillService expenseBillService;
	
	@Autowired
	private AutonumberServiceBeanResolver beanResolver;
	
	@Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
	
	@Autowired
	private AuditRepository auditRepository;
	 @Autowired
	 ManageAuditorService manageAuditorService;
	@Autowired
    @Qualifier("miscbilldetailService")
    private MiscbilldetailService miscbilldetailService;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	
	@RequestMapping(value = "/create/{auditId}", method = RequestMethod.GET)
	public String showNewFormGet(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request, @PathVariable final String auditId) {
		LOGGER.info("Test");
		AuditCheckList checklistDetail = null;
		//List<AppConfigValues> appConfigValuesList =null;
		List<BillTypeCheckList> checkListbill= null;
		checkList=new ArrayList<AuditCheckList>();
		List<AuditBillDetails> auditBillDetails=new ArrayList<AuditBillDetails>();
		AuditBillDetails billDetails=null;
		AuditEmployee emp=null;
		List<AuditEmployee> auditEmployees= new ArrayList<AuditEmployee>();
		List<ManageAuditor> auditorList=manageAuditorService.getAudiorsByType("RSA");
		for (ManageAuditor value : auditorList) {
		 emp=new AuditEmployee();
		 emp.setEmpCode(Long.valueOf(value.getEmployeeid()));
		 emp.setEmpName(getEmployeeName(emp.getEmpCode()));
		 auditEmployees.add(emp);
		 }
		auditDetail.setAuditEmployees(auditEmployees);
		AuditDetails auditDetails = auditService.getById(Long.parseLong(auditId));
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("auditDetails",Long.valueOf(auditId));
		auditDetail.setDocumentDetail(documents);
		auditDetail.setAuditNumber(auditDetails.getAuditno());
		auditDetail.setAuditScheduledDate(auditDetails.getAudit_sch_date());
		auditDetail.setAuditType(auditDetails.getType());
		auditDetail.setPassUnderobjection(auditDetails.getPassUnderobjection());
		EgBillregister bill = null;
		auditDetail.setAuditId(Long.parseLong(auditId));
		auditDetail.setAuditStatus(auditDetails.getStatus().getCode());
		List<AuditPostBillMpng> billDetailsMpngLIst=null;
		List<AuditPostVoucherMpng> voucherDetailsMpngList=null;
		if(auditDetails.getType() != null && auditDetails.getType().equalsIgnoreCase("Pre-Audit"))
		{
			bill = auditDetails.getEgBillregister();
			auditDetail.setBillId(bill.getId());
			model.addAttribute("billSource", "/services/EGF/expensebill/view/" + bill.getId());
		}
		else
		{
			billDetailsMpngLIst=auditDetails.getPostBillMpng();
			voucherDetailsMpngList=auditDetails.getPostVoucherMpng();
			if(billDetailsMpngLIst != null && !billDetailsMpngLIst.isEmpty())
			{
			bill = billDetailsMpngLIst.get(0).getEgBillregister();
			for(AuditPostBillMpng row : billDetailsMpngLIst)
			{
				List<Miscbilldetail> miscBillList = miscbilldetailService.findAllBy(
	                    " from Miscbilldetail where billnumber = ? ",
	                    row.getEgBillregister().getBillnumber());
		        if (miscBillList.size() != 0) {

		            for (Miscbilldetail misc : miscBillList) {
		            	billDetails =new AuditBillDetails();
		            	billDetails.setBillId(row.getEgBillregister().getId());
		            	billDetails.setBillNumber(row.getEgBillregister().getBillnumber());
		            	billDetails.setVoucherId(misc.getBillVoucherHeader().getId());
		            	billDetails.setVoucherNumber(misc.getBillVoucherHeader().getVoucherNumber());
		            	billDetails.setPaymentVoucherId(misc.getPayVoucherHeader().getId());
		            	billDetails.setPaymentVoucherNumber(misc.getPayVoucherHeader().getVoucherNumber());
		            	auditBillDetails.add(billDetails);
		            }
		        }
			}
			auditDetail.setAuditBillDetails(auditBillDetails);
			}
			if(voucherDetailsMpngList != null && !voucherDetailsMpngList.isEmpty())
			{
				for(AuditPostVoucherMpng row : voucherDetailsMpngList)
				{
					billDetails =new AuditBillDetails();
	            	billDetails.setVoucherId(row.getVoucherheader().getId());
	            	billDetails.setVoucherNumber(row.getVoucherheader().getVoucherNumber());
	            	auditBillDetails.add(billDetails);
				}
				auditDetail.setAuditBillDetails(auditBillDetails);
			}
			
			
		}
		if(auditDetails.getStatus().getCode().equalsIgnoreCase("Created") || auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Auditor") || auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Section Officer") || auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Examiner"))
		{
			System.out.println("status :::"+auditDetails.getStatus().getCode());
			if(auditDetails.getType() != null && auditDetails.getType().equalsIgnoreCase("Pre-Audit"))
			{
				checkListbill=billTypeCheckListService.findByBillType(bill.getEgBillregistermis().getEgBillSubType().getName());
			}
			else
			{
				checkListbill=billTypeCheckListService.findByBillType("Post Audit");
				
			}
			if(auditDetails.getStatus().getCode().equalsIgnoreCase("Created"))
			{
				System.out.println("created");
				for (BillTypeCheckList row : checkListbill) {
					checklistDetail = new AuditCheckList();
					if(!auditDetails.getStatus().getCode().equalsIgnoreCase("Created"))
					{
						checklistDetail.setCheckListId(getAuditCheckList(auditDetails,row.getBilltypedescrip(),"ID"));
						checklistDetail.setSeverity(getAuditCheckList(auditDetails,row.getBilltypedescrip(),"Sev"));
						checklistDetail.setStatus(getAuditCheckList(auditDetails,row.getBilltypedescrip(),"Stat"));
						checklistDetail.setChecklist_date(getAuditCheckListDate(auditDetails,row.getBilltypedescrip(),"Date"));
					}
					else
					{
						checklistDetail.setCheckListId("0");
					}
					checklistDetail.setChecklist_description(row.getBilltypedescrip());
					checkList.add(checklistDetail);
				}
			}
			else
			{
				System.out.println("no creteed");
				for (AuditCheckList value : auditDetails.getCheckList()) {
					checklistDetail = new AuditCheckList();
					if(!auditDetails.getStatus().getCode().equalsIgnoreCase("Created"))
					{
						checklistDetail.setCheckListId(getAuditCheckList(auditDetails,value.getChecklist_description(),"ID"));
						checklistDetail.setSeverity(getAuditCheckList(auditDetails,value.getChecklist_description(),"Sev"));
						checklistDetail.setStatus(getAuditCheckList(auditDetails,value.getChecklist_description(),"Stat"));
						checklistDetail.setChecklist_date(getAuditCheckListDate(auditDetails,value.getChecklist_description(),"Date"));
					}
					else
					{
						checklistDetail.setCheckListId("0");
					}
					checklistDetail.setChecklist_description(value.getChecklist_description());
					checkList.add(checklistDetail);
				}
			}
				
				auditDetail.setCheckList(checkList);
		}
		else if(auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Department"))
		{
			if(auditDetails.getCheckList() != null && !auditDetails.getCheckList().isEmpty())
			{
				for(AuditCheckList row : auditDetails.getCheckList())
				{
					row.setUser_comments("");
					row.setCheckListId(String.valueOf(row.getId()));
				}
				auditDetail.setCheckList(auditDetails.getCheckList());
			}
		}
		model.addAttribute("workflowHistory",
				auditUtils.getHistory(auditDetails.getState(), auditDetails.getStateHistory()));
		
		model.addAttribute(STATE_TYPE, auditDetails.getClass().getSimpleName());
		if(auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Department"))
		{
			if (auditDetails.getState() != null)
	            model.addAttribute("currentState", auditDetails.getState().getValue());
	        prepareWorkflow(model, auditDetails, new WorkflowContainer());
		}
		model.addAttribute("auditDetail", auditDetail);
		
		return "create";
	}


	private String getAuditCheckList(AuditDetails auditDetails, String value,String type) {
		String res="";
		for(AuditCheckList row:auditDetails.getCheckList())
		{
			if(value.equalsIgnoreCase(row.getChecklist_description()))
			{
				if(type.equals("ID"))
				{
					res=String.valueOf(row.getId());
				}
				else if(type.equals("Sev"))
				{
					res=row.getSeverity();
				}
				else
				{
					res=row.getStatus();
				}
			}
		}
		return res;
	}

	private Date getAuditCheckListDate(AuditDetails auditDetails, String value,String type) {
		Date res=null;
		for(AuditCheckList row:auditDetails.getCheckList())
		{
			if(value.equalsIgnoreCase(row.getChecklist_description()))
			{
				if(type.equals("Date"))
				{
					res=row.getChecklist_date();
				}
				
			}
		}
		return res;
	}

	@RequestMapping(value = "/create/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			final BindingResult resultBinder, 
			final HttpServletRequest request,@RequestParam("file") MultipartFile[] files) throws IOException {
		LOGGER.info("Save");
		AuditDetails auditDetails = null;
		String workFlowAction=auditDetail.getWorkFlowAction();
		System.out.println("workFlowAction :::"+workFlowAction);
		if(auditDetail.getAuditStatus().equalsIgnoreCase("Created") && !workFlowAction.equalsIgnoreCase("reject"))
		{
			auditDetails = populateDetails(auditDetail);
		}
		else if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Department") && !workFlowAction.equalsIgnoreCase("reject"))
		{
			auditDetails = populateDetailsDept(auditDetail);
		}
		else if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Auditor") &&  workFlowAction.equalsIgnoreCase("department"))
		{
			auditDetails = populateDetailsAudit(auditDetail);
		}
		else if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Auditor") &&  workFlowAction.equalsIgnoreCase("sectionOfficer"))
		{
			auditDetails = populateDetailsSO(auditDetail);
		}
		else if(workFlowAction.equalsIgnoreCase("reject") || auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Section Officer") || auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Examiner"))
		{
			 auditDetails = auditService.getById(auditDetail.getAuditId());
		}
		 
		
		
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				if(files[i] == null || files[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		 
		if (resultBinder.hasErrors()) {

			return "create";
		} else {

			AuditDetails savedAuditDetails=null;
			auditDetails.setDocumentDetail(list);
			if(auditDetail.getPassUnderobjection() !=null) {
			auditDetails.setPassUnderobjection(auditDetail.getPassUnderobjection());
			}
			else if (auditDetails.getPassUnderobjection() != null)
			{
				auditDetails.setPassUnderobjection(auditDetails.getPassUnderobjection());
			}
			else {
				auditDetails.setPassUnderobjection(0);
			}
			Long approvalPosition = 0l;
	        String approvalComment = "";
	        String apporverDesignation = "";
	        if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Department") )
	        {
	        	if (request.getParameter("approvalComent") != null)
		            approvalComment = request.getParameter("approvalComent");
		        System.out.println("approval comment : "+approvalComment);
		        System.out.println("auditDetail.getApprovalComent() ::"+auditDetail.getApprovalComent());
		        if(approvalComment != null &&  !approvalComment.isEmpty())
		        {
		        	auditDetail.setApprovalComent(approvalComment);
		        }
		        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
		            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));

		        if ((approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
		                && request.getParameter(APPROVAL_POSITION) != null
		                && !request.getParameter(APPROVAL_POSITION).isEmpty())
		            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
		        System.out.println("approval position :::"+approvalPosition);
		        if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
		            apporverDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
		        System.out.println("Approval designation :: "+apporverDesignation);
	        }
			try {
				savedAuditDetails = auditService.create(auditDetails,workFlowAction,auditDetail.getApprovalComent(),auditDetail.getLeadAuditorEmpNo(),approvalPosition,apporverDesignation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			final String message = getMessageByStatus(workFlowAction,savedAuditDetails);
			model.addAttribute("message", message);

			return "audit-success";

		}

	}

	private AuditDetails populateDetailsSO(AuditDetail auditDetail) {
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		AuditChecklistHistory checkListHistory=null;
		List<AuditChecklistHistory> checkListHistoryList=null;
		for(AuditCheckList checkListDb:auditDetails.getCheckList())
		{
			for(AuditCheckList checkListUI : auditDetail.getCheckList())
			{
				if(checkListDb.getChecklist_description().equalsIgnoreCase(checkListUI.getChecklist_description()))
				{
					if(checkListDb.getStatus().equalsIgnoreCase("Seen/Checked")) {
						continue;
					}
					checkListDb.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListDb.setUser_comments("N/A");
					checkListDb.setStatus(checkListUI.getStatus());
					checkListDb.setChecklist_date(checkListUI.getChecklist_date());
					applyAuditing(checkListDb);
					checkListHistoryList=new ArrayList<AuditChecklistHistory>();
					checkListHistory=new AuditChecklistHistory();
					checkListHistory.setAuditCheckList(checkListDb);
					checkListHistory.setChecklist_description(checkListUI.getChecklist_description());
					checkListHistory.setChecklist_date(checkListUI.getChecklist_date());
					checkListHistory.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListHistory.setUser_comments("N/A");
					checkListHistory.setStatus(checkListUI.getStatus());
					checkListHistory.setAuditDetails(auditDetails);
					applyAuditing(checkListHistory);
					checkListHistoryList.add(checkListHistory);
					checkListDb.getCheckList_history().addAll(checkListHistoryList);
				}
			}
		}
		applyAuditing(auditDetails);
		return auditDetails;
	}

	private AuditDetails populateDetailsAudit(AuditDetail auditDetail) {
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		AuditChecklistHistory checkListHistory=null;
		List<AuditChecklistHistory> checkListHistoryList=null;
		for(AuditCheckList checkListDb:auditDetails.getCheckList())
		{
			for(AuditCheckList checkListUI : auditDetail.getCheckList())
			{
				
				if(checkListDb.getChecklist_description().equalsIgnoreCase(checkListUI.getChecklist_description()))
				{
					if(checkListDb.getStatus().equalsIgnoreCase("Seen/Checked"))
					{
						continue;
					}
					checkListDb.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListDb.setStatus(checkListUI.getStatus());
					//checkListDb.setSeverity(checkListUI.getSeverity());
					checkListDb.setChecklist_date(checkListUI.getChecklist_date());
					applyAuditing(checkListDb);
					checkListHistoryList=new ArrayList<AuditChecklistHistory>();
					checkListHistory=new AuditChecklistHistory();
					checkListHistory.setAuditCheckList(checkListDb);
					checkListHistory.setChecklist_date(checkListUI.getChecklist_date());
					checkListHistory.setChecklist_description(checkListUI.getChecklist_description());
					checkListHistory.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListHistory.setStatus(checkListUI.getStatus());
					checkListHistory.setSeverity(checkListUI.getSeverity());
					checkListHistory.setAuditDetails(auditDetails);
					applyAuditing(checkListHistory);
					checkListHistoryList.add(checkListHistory);
					checkListDb.getCheckList_history().addAll(checkListHistoryList);
				}
			}
		}
		applyAuditing(auditDetails);
		return auditDetails;
	}

	private AuditDetails populateDetailsDept(AuditDetail auditDetail) {
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		AuditChecklistHistory checkListHistory=null;
		List<AuditChecklistHistory> checkListHistoryList=new ArrayList<AuditChecklistHistory>();
		for(AuditCheckList checkListDb:auditDetails.getCheckList())
		{
			for(AuditCheckList checkListUI : auditDetail.getCheckList())
			{
				if(checkListDb.getChecklist_description().equalsIgnoreCase(checkListUI.getChecklist_description()))
				{
					if(checkListDb.getStatus().equalsIgnoreCase("Seen/Checked"))
					{
						continue;
					}
					checkListDb.setUser_comments(checkListUI.getUser_comments());
					applyAuditing(checkListDb);
					/*
					 * for(AuditChecklistHistory history : checkListDb.getCheckList_history()) {
					 * if(history.getUser_comments() == null ||
					 * history.getUser_comments().isEmpty()) {
					 * history.setUser_comments(checkListUI.getUser_comments());
					 * applyAuditing(history); } }
					 */
					checkListHistoryList=new ArrayList<AuditChecklistHistory>();
					checkListHistory=new AuditChecklistHistory();
					checkListHistory.setAuditCheckList(checkListDb);
					checkListHistory.setChecklist_date(checkListDb.getChecklist_date());
					checkListHistory.setChecklist_description(checkListDb.getChecklist_description());
					checkListHistory.setAuditor_comments(checkListDb.getAuditor_comments());
					checkListHistory.setUser_comments(checkListUI.getUser_comments());
					checkListHistory.setStatus(checkListDb.getStatus());
					checkListHistory.setSeverity(checkListDb.getSeverity());
					checkListHistory.setAuditDetails(auditDetails);
					applyAuditing(checkListHistory);
					checkListHistoryList.add(checkListHistory);
					checkListDb.getCheckList_history().addAll(checkListHistoryList);;
				}
			}
		}
		applyAuditing(auditDetails);
		return auditDetails;
	}

	private AuditDetails populateDetails(AuditDetail auditDetail) {
		List<AuditCheckList> checkList=new ArrayList<AuditCheckList>();
		AuditChecklistHistory checkListHistory=null;
		List<AuditChecklistHistory> checkListHistoryList=null;
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		auditDetails.setAudit_sch_date(auditDetail.getAuditScheduledDate());
		for(AuditCheckList row : auditDetail.getCheckList())
		{
			row.setAuditDetails(auditDetails);
			checkListHistoryList=new ArrayList<AuditChecklistHistory>();
			checkListHistory=new AuditChecklistHistory();
			checkListHistory.setAuditCheckList(row);
			checkListHistory.setChecklist_date(row.getChecklist_date());
			checkListHistory.setChecklist_description(row.getChecklist_description());
			checkListHistory.setUser_comments(row.getUser_comments());
			checkListHistory.setAuditor_comments(row.getAuditor_comments());
			checkListHistory.setStatus(row.getStatus());
			checkListHistory.setSeverity(row.getSeverity());
			checkListHistory.setAuditDetails(auditDetails);
			applyAuditing(checkListHistory);
			checkListHistoryList.add(checkListHistory);
			row.setCheckList_history(checkListHistoryList);
			applyAuditing(row);
			checkList.add(row);
		}
		auditDetails.setCheckList(checkList);
		applyAuditing(auditDetails);
		return auditDetails;

	}

	@RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, AuditConstants.FILESTORE_MODULECODE);
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		AuditDetails auditDetails = auditService.getById(Long.parseLong(request.getParameter("auditDetailsId")));
		auditDetails = getBillDocuments(auditDetails);

		for (final DocumentUpload doc : auditDetails.getDocumentDetail())
			if (doc.getFileStore().getFileStoreId().equalsIgnoreCase(fileStoreId))
				fileName = doc.getFileStore().getFileName();

		// get MIME type of the file
		String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
		if (mimeType == null)
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";

		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());

		// set headers for the response
		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		final OutputStream outStream = response.getOutputStream();

		final byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1)
			outStream.write(buffer, 0, bytesRead);

		inputStream.close();
		outStream.close();
	}

	private AuditDetails getBillDocuments(final AuditDetails auditDetails) {
		List<DocumentUpload> documentDetailsList = auditService.findByObjectIdAndObjectType(auditDetails.getId(),
				AuditConstants.FILESTORE_MODULEOBJECT);
		auditDetails.setDocumentDetail(documentDetailsList);
		return auditDetails;
	}

	public List<AuditCheckList> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<AuditCheckList> checkList) {
		this.checkList = checkList;
	}

	

	private String getMessageByStatus(String workflowAction,AuditDetails audit) {
		String message = "";
		if(workflowAction.equalsIgnoreCase("department") || workflowAction.equalsIgnoreCase("Forward"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to Department for clarification";
		}
		else if(workflowAction.equalsIgnoreCase("sectionOfficer"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to RSA for Verification";
		}
		else if(workflowAction.equalsIgnoreCase("auditor") || workflowAction.equals("Approve"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to Auditor for Verification";
		}
		else if(workflowAction.equalsIgnoreCase("examiner"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to Examiner for Approval";
		}
		else if(workflowAction.equals("approve"))
		{
			message="Audit No : "+audit.getAuditno()+" is approved";
		}
		else if(workflowAction.equalsIgnoreCase("post-audit-initiate"))
		{
			message="Selected Bills have been sent to Audit Branch for Post-Audit processing";
		}
		else if(workflowAction.equalsIgnoreCase("reject"))
		{
			message="Audit No : "+audit.getAuditno()+" is rejected";
		}

		return message;
	}
	public void applyAuditing(AbstractAuditable auditable) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(ApplicationThreadLocals.getUserId());
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(ApplicationThreadLocals.getUserId());
		auditable.setLastModifiedDate(currentDate);
	}
	
	@RequestMapping(value = "/history/{auditId}/{checkListId}", method = RequestMethod.GET)
	public String history(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request, @PathVariable final String auditId,@PathVariable final String checkListId) {
		
		AuditDetails auditDetails = auditService.getById(Long.parseLong(auditId));
		String checkListDesc="";
		List<AuditChecklistHistory> history =null;
		for(AuditCheckList row : auditDetails.getCheckList())
		{
			if(row.getId() == Long.parseLong(checkListId))
			{
				history=row.getCheckList_history();
				checkListDesc=row.getChecklist_description();
			}
			
		}
		model.addAttribute("history", history);
		model.addAttribute("checkListDesc", checkListDesc);
		
			return "history";
	}
	
	@RequestMapping(value = "/post/bill", method = RequestMethod.POST)
	public String showPostBill(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request) {
		LOGGER.info("Post");
		model.addAttribute("fundList",populateFundList());
		model.addAttribute("auditDetail", auditDetail);
		return "postAudit";
	}
	
	private List<Fund> populateFundList()
	{
		List<Fund> fundList=new ArrayList<Fund>();
		Fund fund=new Fund();
		fund.setId(-1);
		fund.setName("--Select--");
		fundList.add(fund);
		fundList.addAll(fundService.getByIsActive(true));
		
		return fundList;
	}
	
	@RequestMapping(value = "/post/search",params="search",method = RequestMethod.POST)
	public String search(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			final BindingResult resultBinder, 
			final HttpServletRequest request) throws IOException {
		LOGGER.info("Search");
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list= null;
		List<Object[]> list1= null;
		if(auditDetail.getExpenditureType().equalsIgnoreCase("Receipt"))
		{
			query
	        .append(
	                "select v.id,v.type,v.voucherNumber,v.voucherDate from CVoucherHeader v  where v.type =? and v.postauditprocessing is null ")
	        		.append(auditUtils
                            .getReceiptDateQuery(auditDetail.getBillFrom(), auditDetail.getBillTo())).append(" and v.fundId.id ="+auditDetail.getFund())
	        		.append(auditUtils.getDeptQuery());
		}
		else
		{
        query
        .append(
                "select br.expendituretype , br.billtype ,br.billnumber , br.billdate , br.billamount , br.passedamount ,egwstatus.description,billmis.sourcePath,")
                .append(" br.id ,br.status.id,egwstatus.description ,br.state.id,br.lastModifiedBy.id ")
                .append(
                        " from EgBillregister br, EgBillregistermis billmis , EgwStatus egwstatus where   billmis.egBillregister.id = br.id and egwstatus.id = br.status.id  ")
                        .append(" and br.expendituretype=? and egwstatus.code = 'Bill Payment Approved' ").append(
                        		auditUtils
                                .getBillDateQuery(auditDetail.getBillFrom(), auditDetail.getBillTo()))
                                .append(auditUtils.getBillMisQuery(auditDetail));
		}
        
        LOGGER.info("Query :: "+query.toString());
        
          list = persistenceService.findAllBy(query.toString(),
        		auditDetail.getExpenditureType());
        PostAuditResult result = null;
        if (list.size() != 0) {
        	//resultSearchList.clear();
        	resultSearchList = new ArrayList<PostAuditResult>();
        	if(auditDetail.getExpenditureType().equalsIgnoreCase("Receipt"))
        	{
        		for (final Object[] object : list) {
        			result = new PostAuditResult();
        			result.setVoucherId(object[0].toString());
        			result.setExpendituretype(object[1].toString());
        			result.setBillnumber(object[2].toString());
        			result.setBilldate(object[3].toString());
        			result.setSourcepath("/services/EGF/voucher/preApprovedVoucher-loadvoucherview.action?vhid="+object[0].toString());
        			result.setBillamount(getReceiptAmount(object[0].toString()));
        			resultSearchList.add(result);
        		}
        	}
        	else
        	{
            for (final Object[] object : list) {
            	result = new PostAuditResult();
            	result.setExpendituretype(object[0].toString());
                String billtype = "";
                if (object[1] != null)
                    billtype = object[1].toString();
                result.setBilltype(billtype);
                result.setBillnumber(object[2].toString());
                result.setBilldate(object[3].toString());
                result.setBillamount(object[4].toString());
                result.setPassedamount(object[5].toString());
                result.setBillstatus(object[6].toString());
                if (null != object[7])
                	result.setSourcepath(object[7].toString());
                else
                	result.setSourcepath("/services/EGF/bill/billView-view.action?billId=" + object[8].toString());
                resultSearchList.add(result);
            }
        }
            
        }
        auditDetail.setPostAuditResultList(resultSearchList);
		model.addAttribute("fundList",populateFundList());
		model.addAttribute("auditDetail", auditDetail);
		return "postAudit";
		
	}
	
	private String getReceiptAmount(String vhId) {
		SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String amt="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select voucherheaderid,debitamount from generalledger g where g.voucherheaderid =:voucherHeaderId and g.debitamount <> 0");
    	    query.setLong("voucherHeaderId", Long.parseLong(vhId));
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		amt= element[1].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return amt;
	}


	@RequestMapping(value = "/post/search",params="save",method = RequestMethod.POST)
	public String save1(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			final BindingResult resultBinder, 
			final HttpServletRequest request,@RequestParam("file") MultipartFile[] files) throws IOException {
		LOGGER.info("Save");
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				if(files[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		auditDetail.setDocumentDetail(list);
		populateauditWorkFlow(auditDetail);
		final String message = getMessageByStatus("post-audit-initiate",null);
		model.addAttribute("message", message);

		return "audit-success";
	}
	
	private void populateauditWorkFlow(AuditDetail auditDetail) {
    	AuditDetails audit=new AuditDetails();
    	final User user = securityUtils.getCurrentUser();
    	List<AuditPostBillMpng> postBillMpngList=new ArrayList<AuditPostBillMpng>();
    	AuditPostBillMpng postBillMpng=null;
    	EgBillregister bill = null;
    	String deptCode= "";
    	String type=auditDetail.getExpenditureType();
    	List<AuditPostVoucherMpng> postVoucherMpngList=new ArrayList<AuditPostVoucherMpng>();
    	AuditPostVoucherMpng postVoucherMpng = null;
    	CVoucherHeader vh=null;
    	for(PostAuditResult result : auditDetail.getPostAuditResultList())
    	{
    		if(result.getChecked())
    		{	
    			if(type.equalsIgnoreCase("Receipt"))
    			{
    				postVoucherMpng=new AuditPostVoucherMpng();
    				deptCode=getReceipDeptCode(Long.parseLong(result.getVoucherId()));
    				postVoucherMpng.setVoucherheader((CVoucherHeader) persistenceService.find("from CVoucherHeader where id=?",Long.parseLong(result.getVoucherId())));
    				postVoucherMpng.setAuditDetails(audit);
    				applyAuditing(postVoucherMpng);
    				postVoucherMpngList.add(postVoucherMpng);
    			}
    			else
    			{
    			postBillMpng = new AuditPostBillMpng();
    			 bill = expenseBillService.getByBillnumber(result.getBillnumber());
    			 deptCode=bill.getEgBillregistermis().getDepartmentcode();
    			postBillMpng.setEgBillregister(bill);
    			postBillMpng.setAuditDetails(audit);
    			applyAuditing(postBillMpng);
    			postBillMpngList.add(postBillMpng);
    		}
    			
    		}
    	}
    	List<ManageAuditor> auditorList=manageAuditorService.getAudiorsDepartmentByType(Integer.parseInt(deptCode), "Auditor");
    	Position owenrPos = new Position();
    	if(auditorList != null && !auditorList.isEmpty())
    	{
    		owenrPos.setId(Long.valueOf(auditorList.get(0).getEmployeeid()));
    	}
    	else
    	{
    		owenrPos.setId(null);
    	}
    	AuditNumberGenerator v = beanResolver.getAutoNumberServiceFor(AuditNumberGenerator.class);

		final String postAuditNumber = v.getNextPostAuditNumber(deptCode);
    	audit.setAuditno(postAuditNumber);
    	audit.setType("Post-Audit");
    	audit.setEgBillregister(null);
    	audit.setDepartment(deptCode);
    	audit.setPassUnderobjection(0);
    	audit.setStatus(egwStatusDAO.getStatusByModuleAndCode("Audit", "Created"));
    	if(type.equalsIgnoreCase("Receipt"))
    	{
    		audit.setPostVoucherMpng(postVoucherMpngList);
    	}
    	else
    	{
    	audit.setPostBillMpng(postBillMpngList);
    	}
    	audit.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
        .withComments("Initiated Post-Audit")
        .withStateValue("NEW").withDateInfo(new Date()).withOwner(owenrPos)
        .withNextAction("Post Audit pending")
        .withNatureOfTask("Post-Audit")
        .withCreatedBy(user.getId())
        .withtLastModifiedBy(user.getId());
    	 applyAuditing(audit);
    	 AuditDetails savedAuditDetails =auditRepository.save(audit);
    	 List<DocumentUpload> files = auditDetail.getDocumentDetail() == null ? null
 				: auditDetail.getDocumentDetail();
 		final List<DocumentUpload> documentDetails;
 		documentDetails = auditUtils.getDocumentDetails(files, savedAuditDetails,
				AuditConstants.FILESTORE_MODULEOBJECT);
 		if (!documentDetails.isEmpty()) {
			savedAuditDetails.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
 		auditRepository.save(savedAuditDetails);
    	 for(AuditPostBillMpng row:postBillMpngList)
    	 {
    		 bill = expenseBillService.getById(row.getEgBillregister().getId());
    		 bill.setStatus(egwStatusDAO.getStatusByModuleAndCode("EXPENSEBILL", "Pending with Post-Audit"));
    		 expenseBillService.create(bill);
    	 }
    	 for(AuditPostVoucherMpng row:postVoucherMpngList)
    	 {
    		 persistenceService
             .getSession()
             .createSQLQuery(
                     "update voucherheader set postauditprocessing = 'Y' where id =:vhId").setLong("vhId", row.getVoucherheader().getId()).executeUpdate();
    	 }
		persistenceService.getSession().flush();
		
	}
	

	private String getReceipDeptCode(Long vhId) {
		SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String dept="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select  vm.voucherheaderid , vm.departmentcode from vouchermis vm where vm.voucherheaderid =:voucherHeaderId");
    	    query.setLong("voucherHeaderId", vhId);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		dept= element[1].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return dept;
	}


	@RequestMapping(value = "/post/auditSearch", method = RequestMethod.POST)
	public String auditSearch(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request) {
		LOGGER.info("Audit Search");
		auditDetail.setDepartments(this.getDepartmentsFromMs());
		List<AppConfigValues> appConfigValuesEmpList =null;
		AuditEmployee emp=null;
		List<AuditEmployee> auditEmployees= new ArrayList<AuditEmployee>();
		List<ManageAuditor> auditorList=manageAuditorService.getAudiorsByType("Auditor");
		for (ManageAuditor value : auditorList) {
		 emp=new AuditEmployee();
		 emp.setEmpCode(Long.valueOf(value.getEmployeeid()));
		 emp.setEmpName(getEmployeeName(emp.getEmpCode()));
		 auditEmployees.add(emp);
		 }
		auditDetail.setAuditEmployees(auditEmployees);
		model.addAttribute("auditDetail", auditDetail);
		return "auditSearch";
	}
	
	@RequestMapping(value = "/post/searchResult",params="search",method = RequestMethod.POST)
	public String searchResult(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			final BindingResult resultBinder, 
			final HttpServletRequest request) throws IOException {
		LOGGER.info("Search");
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
        query
        .append(
                "select ad.id,ad.auditno,ad.type,ad.audit_sch_date,ad.status.description from AuditDetails ad where ad.type = ? ")
                .append(auditUtils
                                .getAuditDateQuery(auditDetail.getBillFrom(), auditDetail.getBillTo()))
                                .append(auditUtils.getAuditMisQuery(auditDetail));
        LOGGER.info("Query :: "+query.toString());
          list = persistenceService.findAllBy(query.toString(),
        		auditDetail.getAuditType());
        AuditDetails result = null;
        if (list.size() != 0) {
        	//resultsDtlsList.clear();
        	resultsDtlsList = new ArrayList<AuditDetails>();

            for (final Object[] object : list) {
            	result = new AuditDetails();
            	result.setId(Long.parseLong(object[0].toString()));
            	result.setAuditno(object[1].toString());
            	result.setType(object[2].toString());
            	if(object[3] != null)
            	{
            		result.setSchdDate(object[3].toString());
            	}
            	else
            	{
            		result.setSchdDate(null);
            	}
            	result.setStatusDescription(object[4].toString());
            	resultsDtlsList.add(result);
            }
        }
        auditDetail.setAuditSearchList(resultsDtlsList);
        auditDetail.setDepartments(this.getDepartmentsFromMs());
		List<AppConfigValues> appConfigValuesEmpList =null;
		AuditEmployee emp=null;
		List<AuditEmployee> auditEmployees= new ArrayList<AuditEmployee>();
		List<ManageAuditor> auditorList=manageAuditorService.getAudiorsByType("Auditor");
		for (ManageAuditor value : auditorList) {
		 emp=new AuditEmployee();
		 emp.setEmpCode(Long.valueOf(value.getEmployeeid()));
		 emp.setEmpName(getEmployeeName(emp.getEmpCode()));
		 auditEmployees.add(emp);
		 }
		auditDetail.setAuditEmployees(auditEmployees);
		model.addAttribute("fundList",populateFundList());
		model.addAttribute("auditDetail", auditDetail);
		return "auditSearch";
		
	}
	
	@RequestMapping(value = "/view/{auditId}", method = RequestMethod.GET)
	public String view(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request, @PathVariable final String auditId) {
		LOGGER.info("View");
		AuditCheckList checklistDetail = null;
		checkList=new ArrayList<AuditCheckList>();
		String mode="view";
		List<AuditBillDetails> auditBillDetails=new ArrayList<AuditBillDetails>();
		AuditBillDetails billDetails=null;
		AuditDetails auditDetails = auditService.getById(Long.parseLong(auditId));
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("auditDetails",Long.valueOf(auditId));
		auditDetail.setDocumentDetail(documents);
		auditDetail.setAuditNumber(auditDetails.getAuditno());
		auditDetail.setAuditScheduledDate(auditDetails.getAudit_sch_date());
		auditDetail.setAuditType(auditDetails.getType());
		auditDetail.setPassUnderobjection(auditDetails.getPassUnderobjection());
		EgBillregister bill = null;
		auditDetail.setAuditId(Long.parseLong(auditId));
		auditDetail.setAuditStatus("Pending with Examiner");
		List<AuditPostBillMpng> billDetailsMpngLIst=null;
		if(auditDetails.getType() != null && auditDetails.getType().equalsIgnoreCase("Pre-Audit"))
		{
			bill = auditDetails.getEgBillregister();
			auditDetail.setBillId(bill.getId());
			model.addAttribute("billSource", "/services/EGF/expensebill/view/" + bill.getId());
		}
		else
		{
			billDetailsMpngLIst=auditDetails.getPostBillMpng();
			bill = billDetailsMpngLIst.get(0).getEgBillregister();
			for(AuditPostBillMpng row : billDetailsMpngLIst)
			{
				List<Miscbilldetail> miscBillList = miscbilldetailService.findAllBy(
	                    " from Miscbilldetail where billnumber = ? ",
	                    row.getEgBillregister().getBillnumber());
		        if (miscBillList.size() != 0) {

		            for (Miscbilldetail misc : miscBillList) {
		            	billDetails =new AuditBillDetails();
		            	billDetails.setBillId(row.getEgBillregister().getId());
		            	billDetails.setBillNumber(row.getEgBillregister().getBillnumber());
		            	billDetails.setVoucherId(misc.getBillVoucherHeader().getId());
		            	billDetails.setVoucherNumber(misc.getBillVoucherHeader().getVoucherNumber());
		            	billDetails.setPaymentVoucherId(misc.getPayVoucherHeader().getId());
		            	billDetails.setPaymentVoucherNumber(misc.getPayVoucherHeader().getVoucherNumber());
		            	auditBillDetails.add(billDetails);
		            }
		        }
			}
			auditDetail.setAuditBillDetails(auditBillDetails);
			
		}
		if(mode.equals("view"))
		{
				for (AuditCheckList value : auditDetails.getCheckList()) {
					checklistDetail = new AuditCheckList();
					if(!auditDetails.getStatus().getCode().equalsIgnoreCase("Created"))
					{
						checklistDetail.setCheckListId(getAuditCheckList(auditDetails,value.getChecklist_description(),"ID"));
						checklistDetail.setSeverity(getAuditCheckList(auditDetails,value.getChecklist_description(),"Sev"));
						checklistDetail.setStatus(getAuditCheckList(auditDetails,value.getChecklist_description(),"Stat"));
					}
					else
					{
						checklistDetail.setCheckListId("0");
					}
					checklistDetail.setChecklist_description(value.getChecklist_description());
					checkList.add(checklistDetail);
				}
				auditDetail.setCheckList(checkList);
		}
		model.addAttribute("mode","view");
		model.addAttribute("workflowHistory",
				auditUtils.getHistory(auditDetails.getState(), auditDetails.getStateHistory()));
		model.addAttribute("auditDetail", auditDetail);
		model.addAttribute(STATE_TYPE, auditDetails.getClass().getSimpleName());
		return "create";
	}

	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}
	
	
	
	@RequestMapping(value = "/post/auditStateSearch", method = RequestMethod.POST)
	public String auditStateSearch(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request) {
		LOGGER.info("Audit Search");
		auditDetail.setDepartments(this.getDepartmentsFromMs());
		AuditEmployee emp=null;
		List<AuditEmployee> auditEmployees= new ArrayList<AuditEmployee>();
		List<ManageAuditor> auditorList=manageAuditorService.getAudiorsByType("Auditor");
		for (ManageAuditor value : auditorList) {
		 emp=new AuditEmployee();
		 emp.setEmpCode(Long.valueOf(value.getEmployeeid()));
		 emp.setEmpName(getEmployeeName(emp.getEmpCode()));
		 auditEmployees.add(emp);
		 }
		auditDetail.setAuditEmployees(auditEmployees);
		model.addAttribute("auditDetail", auditDetail);
		return "auditStateSearch";
	}
	
	
	@RequestMapping(value = "/post/searchStateResult",params="search",method = RequestMethod.POST)
	public String searchStateResult(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			final BindingResult resultBinder, 
			final HttpServletRequest request) throws IOException {
		LOGGER.info("Search");
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
        query
        .append(
                "select ad.id,ad.auditno,ad.type,ad.audit_sch_date,ad.status.description from AuditDetails ad where ad.status.description not in ('Approved') and  ad.type = ? ")
                .append(auditUtils
                                .getAuditDateQuery(auditDetail.getBillFrom(), auditDetail.getBillTo()))
                                .append(auditUtils.getAuditTaskMisQuery(auditDetail));
        LOGGER.info("Query :: "+query.toString());
          list = persistenceService.findAllBy(query.toString(),
        		auditDetail.getAuditType());
        AuditDetails result = null;
        List<ManageAuditor> auditorListAudit=manageAuditorService.getAudiorsByType("Auditor");
        System.out.println("Size Auditor:: "+auditorListAudit.size() );
        List<ManageAuditor> auditorListRSA=manageAuditorService.getAudiorsByType("RSA");
        System.out.println("Size RSA:: "+auditorListRSA.size() );
        AuditDetails auditDetails =null;
        if (list.size() != 0) {
        	resultsDtlsList = new ArrayList<AuditDetails>();
        	System.out.println("Size :: "+list.size() );
            for (final Object[] object : list) {
            	result = new AuditDetails();
            	result.setId(Long.parseLong(object[0].toString()));
            	 auditDetails = auditService.getById(result.getId());
            	if(auditDetail.getLeadAuditorEmpNo() != null && auditDetail.getLeadAuditorEmpNo() != -1 )
            	{
            		System.out.println("auditDetail.getLeadAuditorEmpNo() ::"+auditDetail.getLeadAuditorEmpNo());
            		System.out.println("auditDetails.getCurrentState().getOwnerPosition() ::"+auditDetails.getCurrentState().getOwnerPosition());
            		if(String.valueOf(auditDetails.getCurrentState().getOwnerPosition()).equals(String.valueOf(auditDetail.getLeadAuditorEmpNo())))
            		{
            			System.out.println("not continue");
            		}
            		else
            		{
            			System.out.println("continue");
            			continue;
            		}
            		
            	}
            	else
            	{
            		System.out.println("auditDetail.getType() :: "+auditDetail.getType());
            		if(auditDetail.getType().equalsIgnoreCase("Auditor") )
            		{
            			System.out.println("Auditor :"+auditDetails.getCurrentState().getOwnerPosition());
            			if(!checkAuditor(auditDetails.getCurrentState().getOwnerPosition(),auditorListAudit))
            			{
            				System.out.println("continue Auditor");
            				continue;
            			}
            		}
            		else
            		{
            			System.out.println("RSA :"+auditDetails.getCurrentState().getOwnerPosition());
            			if(!checkRSA(auditDetails.getCurrentState().getOwnerPosition(),auditorListRSA))
            			{
            				System.out.println("continue RSA");
            				continue;
            			}
            		}
            	}
            	result.setAuditno(object[1].toString());
            	result.setType(object[2].toString());
            	if(object[3] != null)
            	{
            		result.setSchdDate(object[3].toString());
            	}
            	else
            	{
            		result.setSchdDate(null);
            	}
            	result.setStatusDescription(object[4].toString());
            	resultsDtlsList.add(result);
            	System.out.println("Added");
            }
        }
        auditDetail.setAuditSearchList(resultsDtlsList);
        System.out.println("resultsDtlsList :: "+resultsDtlsList.size());
        auditDetail.setDepartments(this.getDepartmentsFromMs());
		AuditEmployee emp=null;
		List<AuditEmployee> auditEmployees= new ArrayList<AuditEmployee>();
		List<ManageAuditor> auditorList=manageAuditorService.getAudiorsByType("Auditor");
		for (ManageAuditor value : auditorList) {
		 emp=new AuditEmployee();
		 emp.setEmpCode(Long.valueOf(value.getEmployeeid()));
		 emp.setEmpName(getEmployeeName(emp.getEmpCode()));
		 auditEmployees.add(emp);
		 }
		auditDetail.setAuditEmployees(auditEmployees);
		model.addAttribute("fundList",populateFundList());
		model.addAttribute("auditDetail", auditDetail);
		return "auditStateSearch";
		
	}
	
	
	
	 private boolean checkRSA(Long ownerPosition, List<ManageAuditor> auditorListRSA) {
		boolean result=false;
		for(ManageAuditor row:auditorListRSA)
		{
			if(String.valueOf((Long.valueOf(row.getEmployeeid()))).equals( String.valueOf(ownerPosition)))
			{
				result=true;
				break;
			}
		}
		return result;
	}


	private boolean checkAuditor(Long ownerPosition, List<ManageAuditor> auditorListAudit) {
		boolean result=false;
		for(ManageAuditor row:auditorListAudit)
		{
			if(String.valueOf((Long.valueOf(row.getEmployeeid()))).equals(String.valueOf(ownerPosition)))
			{
				result=true;
				break;
			}
		}
		return result;
	}


	@RequestMapping(value = "/employee/{type}", method = RequestMethod.GET)
	    @ResponseBody
	    public List<EmployeeInfo> getApprovers(@PathVariable(name = "type") String type) {

	        List<EmployeeInfo> approvers=new ArrayList<>();

	       List<ManageAuditor> manageAuditorsSave=manageAuditorService.getAudiorsByType(type);
	       for (ManageAuditor manageAuditor2 : manageAuditorsSave) {
			
	    	   EmployeeInfo employeeInfo=microserviceUtils.getEmployeeById(Long.valueOf(manageAuditor2.getEmployeeid()));
	    	   approvers.add(employeeInfo);
		}
	        
	        return approvers;
	    }
	

	 @RequestMapping(value="/updateAuditorScreen/{auditId}/{type}")    
	    public String updateAuditOwnerscreen(@PathVariable Long auditId,@PathVariable String type, Model model){
		 AuditDetails auditDetails=new AuditDetails();
		 AuditDetail auditDetail=new AuditDetail();
		  auditDetails = auditService.findByid(auditId);
		 
		 if(auditDetails!=null) {
  	   EmployeeInfo employeeInfo=microserviceUtils.getEmployeeById(auditDetails.getState().getOwnerPosition());
  	   auditDetails.setEmployeeName(employeeInfo.getUser().getName());
		 }
		 
		 auditDetail.setAuditId(auditId);
		 auditDetail.setAuditType(auditDetails.getType());
		 auditDetail.setAuditNumber(auditDetails.getAuditno());
		 auditDetail.setAuditScheduledDate(auditDetails.getAudit_sch_date());
		 auditDetail.setAuditStatus(auditDetails.getStatus().getDescription());
		 auditDetail.setLeadAuditorName(auditDetails.getEmployeeName());
		 auditDetail.setLeadAuditorEmpNo(auditDetails.getState().getOwnerPosition());
		 auditDetail.setStateId(auditDetails.getState().getId());
		 
		/*add employye list to drop down*/
		  List<EmployeeInfo> approvers=new ArrayList<>();

	       List<ManageAuditor> manageAuditorsSave=manageAuditorService.getAudiorsByType(type);
	       for (ManageAuditor manageAuditor2 : manageAuditorsSave) {
			
	    	   EmployeeInfo employeeInfo=microserviceUtils.getEmployeeById(Long.valueOf(manageAuditor2.getEmployeeid()));
	    	   approvers.add(employeeInfo);
		}
		 
	 model.addAttribute("approverList", approvers);       
	model.addAttribute("auditDetail", auditDetail);

	return "auditStateUpdate"; 
	 
	 }
	 
	 
	 @RequestMapping(value="/updateauditorOwner",method = RequestMethod.POST)    
	    public String updateAuditOwner(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
				final BindingResult resultBinder, 
				final HttpServletRequest request) throws IOException{
		 
		// AuditDetails auditDetails = auditService.getById(auditId);
		 AuditDetails auditDetails=new AuditDetails();
		
		 
		persistenceService
      .getSession()
      .createSQLQuery(
              "update eg_wf_states set owner_pos = :empid where id =:stateid").setLong("empid", auditDetail.getLeadAuditorEmpNo()).setLong("stateid", auditDetail.getStateId()).executeUpdate();
	persistenceService.getSession().flush();
	
		model.addAttribute("auditDetail", auditDetail);
	model.addAttribute("message", "Updated Successfully");

	return "audit-success"; 
	 
	 }
	 
	 
	 
	public String getEmployeeName(Long empId){
	
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	    }

}