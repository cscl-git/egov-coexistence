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
package org.egov.egf.web.controller.expensebill;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.audit.autonumber.AuditNumberGenerator;
import org.egov.audit.entity.AuditDetails;
import org.egov.audit.model.ManageAuditor;
import org.egov.audit.repository.AuditRepository;
import org.egov.audit.service.ManageAuditorService;
import org.egov.audit.service.AuditService;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bank;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CGeneralLedgerDetail;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.CheckListService;
import org.egov.egf.commons.bank.service.CreateBankService;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.expensebill.repository.RetrachmentRepository;																 
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.expensebill.service.RefundBillService;
import org.egov.egf.expensebill.service.RetrachmentService;														   
import org.egov.egf.utils.FinancialUtils;
import org.egov.eis.repository.DesignationRepository;													 
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.AppConfig;													
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigService;															
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.models.EgChecklists;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.BillType;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.RetrachmentDetails;
import org.egov.model.voucher.PreApprovedVoucher;
import org.egov.pims.commons.Position;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.PaymentRefundUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exilant.eGov.src.common.SubDivision;

@Controller
@RequestMapping(value = "/expensebill")
public class UpdateExpenseBillController extends BaseBillController {
	private static final String INITIATED_PRE_AUDIT = "Initiated Pre-Audit";

	private static final String PRE_AUDIT = "Pre-Audit";

	private static final String PRE_AUDIT_PENDING = "Pre Audit pending";

	private static final String NEW = "NEW";

	private static final String STRING = "::";

	private static final String CREATED = "Created";

	private static final String AUDIT2 = "Audit";

	private static final String PRE = "Pre-Audit";

	private static final String AUDIT001 = "Audit001";

	private static final String SUPPORTING_DOCS = "supportingDocs";

    private static final String NET_PAYABLE_AMOUNT = "netPayableAmount";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";

    private static final String EG_BILLREGISTER = "egBillregister";

    private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String EXPENSEBILL_VIEW = "expensebill-view";

    private static final String EXPENSEBILL_UPDATE_WORKFLOW = "expensebill-update-Workflow";
    
    private static final String NET_PAYABLE_ID = "netPayableId";
    private static final String BILL_TYPES = "billTypes";
    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
    protected FileStoreService fileStoreService;   
    //private List<FileStoreMapper> originalFiles = new ArrayList<FileStoreMapper>();
    
    @Autowired
	 private ManageAuditorService manageAuditorService;
    
    @Autowired
    private DocumentUploadRepository documentUploadRepository;
    @Autowired
    private ExpenseBillService expenseBillService;
    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;
    @Autowired
    private FinancialUtils financialUtils;
    @Autowired
    private CheckListService checkListService;
    @Autowired
    private MicroserviceUtils microServiceUtil;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
	private AuditRepository auditRepository;
    @Autowired
    private RefundBillService refundBillService;
    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
    @Autowired
	private FundHibernateDAO fundHibernateDAO;
    @Autowired
    protected AppConfigValueService appConfigValuesService;
    @Autowired
    private CreateBankService createBankService;
    @Autowired
	private AutonumberServiceBeanResolver beanResolver;
    @Autowired
	private PaymentRefundUtils paymentRefundUtils;
    @Autowired
    private ChartOfAccountDetailService chartOfAccountDetailService;
    @Autowired
	private RetrachmentService retrachmentService;
	@Autowired
	private RetrachmentRepository retrachmentRepository;
	@Autowired
	private AuditService auditService;
	
    public UpdateExpenseBillController(final AppConfigValueService appConfigValuesService) {
		super(appConfigValuesService);
    }

    @ModelAttribute(EG_BILLREGISTER)
    public EgBillregister getEgBillregister(@PathVariable String billId) {
        if (billId.contains("showMode")) {
            String[] billIds = billId.split("\\&");
            billId = billIds[0];
        }
        return expenseBillService.getById(Long.parseLong(billId));
    }

    @RequestMapping(value = "/update/{billId}", method = RequestMethod.GET)
    public String updateForm(final Model model, @PathVariable final String billId,
            final HttpServletRequest request) throws ApplicationException {
    	
    	System.out.println("from update controller"+  billId);
    	
        final EgBillregister egBillregister = expenseBillService.getById(Long.parseLong(billId));
        
        if(egBillregister.getRefundable() != null && !egBillregister.getRefundable().isEmpty()) {
              model.addAttribute("refundable", egBillregister.getRefundable());
        }else {
			model.addAttribute("refundable", null);
        }
        if (egBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_PURCHASE)) {
            return "redirect:/supplierbill/update/" + billId;
        }
		/*
		 * if (egBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.
		 * STANDARD_EXPENDITURETYPE_REFUND)) { if (egBillregister.getState() != null &&
		 * (FinancialConstants.BUTTONSAVEASDRAFT.equals(egBillregister.getState().
		 * getValue()) )) { return "redirect:/refund/updateForm/" + billId; } }
		 */
        
        final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(billId));
        egBillregister.setDocumentDetail(documents);
        List<Map<String, Object>> budgetDetails = null;
        setDropDownValues(model);
		
		 List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					"receipt_sub_divison");
	        List<SubDivision> subdivisionList=new ArrayList<SubDivision>();
	        SubDivision subdivision=null;
	        for(AppConfigValues value:appConfigValuesList)
	        {
	        	subdivision = new SubDivision();
	        	subdivision.setSubdivisionCode(value.getValue());
	        	subdivision.setSubdivisionName(value.getValue());
	        	subdivisionList.add(subdivision);
	        }
	        model.addAttribute("subdivision", subdivisionList);
		
        model.addAttribute(BILL_TYPES, BillType.values());
        model.addAttribute("stateType", egBillregister.getClass().getSimpleName());
        if (egBillregister.getState() != null)
            model.addAttribute("currentState", egBillregister.getState().getValue());
        
        	System.out.println("egBillregister.getState() "+egBillregister.getState());
        	System.out.println("egBillregister.getStateHistory() "+egBillregister.getStateHistory());
        
        	if(egBillregister.getIsCitizenRefund()==null||egBillregister.getIsCitizenRefund()=="")
        model.addAttribute("workflowHistory",
                financialUtils.getWorkflowHistory(egBillregister.getState(), egBillregister.getStateHistory()));
        
        List<String>  validActions =null;
        if(!egBillregister.getStatus().getDescription().equals("Pending for Cancellation"))
        {
        	validActions = Arrays.asList("Forward","SaveAsDraft");
            prepareWorkflow(model, egBillregister, new WorkflowContainer());
        }
          
        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
        prepareBillDetailsForView(egBillregister);
        expenseBillService.validateSubledgeDetails(egBillregister);
        final List<CChartOfAccounts> expensePayableAccountList = chartOfAccountsService
                .getNetPayableCodes();
        for (final EgBilldetails details : egBillregister.getBillDetails())
            if (expensePayableAccountList != null && !expensePayableAccountList.isEmpty()
                    && expensePayableAccountList.contains(details.getChartOfAccounts())) {
                model.addAttribute(NET_PAYABLE_ID, details.getChartOfAccounts().getId());
                model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
            }
        prepareCheckListForEdit(egBillregister, model);

        String department = this.getDepartmentName(egBillregister.getEgBillregistermis().getDepartmentcode());

        if (department != null)
            egBillregister.getEgBillregistermis().setDepartmentName(department);
        model.addAttribute(EG_BILLREGISTER, egBillregister);
        /*originalFiles = (List<FileStoreMapper>) persistenceService.getSession().createQuery(
                "from FileStoreMapper where fileName like '%"+egBillregister.getBillnumber()+"%' order by id desc ").setMaxResults(10).list();
        model.addAttribute(SUPPORTING_DOCS,originalFiles);*/
        if(egBillregister.getStatus().getDescription().equals("Pending for Cancellation"))
        {
        	model.addAttribute("mode", "cancel");
        	prepareWorkflow(model, egBillregister, new WorkflowContainer());
        	return "expensebill-view";
        }
        model.addAttribute("paId",egBillregister.getId());
        if (egBillregister.getState() != null
                && (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())
                        || financialUtils.isBillEditable(egBillregister.getState()))) {
            model.addAttribute("mode", "edit");
            model.addAttribute("viewBudget", "Y");
			model.addAttribute("egBillregister", egBillregister);
            return "expensebill-update";
        } 
        else if (egBillregister.getState() != null
                && (FinancialConstants.BUTTONSAVEASDRAFT.equals(egBillregister.getState().getValue()) )) {
            model.addAttribute("mode", "edit");
            model.addAttribute("egBillregister", egBillregister);
            //model.addAttribute("accountDetails", egBillregister.getBillDetails());
            model.addAttribute("validActionList", validActions);
            model.addAttribute("viewBudget", "Y");
			
			  if(egBillregister.getRefundable() != null && !egBillregister.getRefundable().isEmpty()) 
			  {
				  Map<String, Object> payeeMap = null;
				  BigDecimal dbAmount = BigDecimal.ZERO;
				  BigDecimal crAmount = BigDecimal.ZERO;
				  final List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
			      final List<PreApprovedVoucher> payeeList = new ArrayList<PreApprovedVoucher>();
			      Map<String, Object> temp = null;
				  if(egBillregister.getIsCitizenRefund()!=null && egBillregister.getIsCitizenRefund().equalsIgnoreCase("Y")){
				  PreApprovedVoucher subledger = null;
				  CChartOfAccounts coa = null;	
				  
				  final List<Accountdetailtype> detailtypeIdList = new ArrayList<Accountdetailtype>();
				  final List<Long> glcodeIdList = new ArrayList<Long>();
				  List<Object[]> list1= null;
	    			final StringBuffer query1 = new StringBuffer(500);
	    			SQLQuery queryMain =  null;
	    			query1
	    		    .append("select eb2.glcodeid,eb3.accountdetailkeyid,eb3.accountdetailtypeid ,eb3.creditamount,eop.code, eop.\"name\" from eg_billregister eb ,eg_billdetails eb2, eg_billpayeedetails eb3,egf_other_party eop  " + 
	    		    		" where eb3.billdetailid =eb2.id and eb2.billid =eb.id and eop.id=eb3.accountdetailkeyid and eb.id ="+egBillregister.getId());
	    			System.out.println("Query 1 :: "+query1.toString());
	    			queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
	    			list1 = queryMain.list();
	    			int i=0;
	    			System.out.println(":::list size::::: "+list1.size());
	    			int detailkeyid=0,detailtypeid=0;
	    			String code="",name="";
	    			if(list1!=null)
	    			{	
	    				for (final Object[] object : list1)
	    				{
	    					System.out.println("i "+i);
	    					detailkeyid=Integer.parseInt(object[1].toString());
	    					detailtypeid=Integer.parseInt(object[2].toString());
	    					code=object[4].toString();
	    					name=object[5].toString();
	    				}
	    			}
				  final List<CGeneralLedger> gllist = paymentRefundUtils.getAccountDetails(egBillregister.getEgBillregistermis().getPaymentvoucherheaderid());
				  for (final CGeneralLedger gl : gllist) {
					  System.out.println("i "+i);
					  temp = new HashMap<String, Object>();
		                if (gl.getFunctionId() != null) {
		                    temp.put(Constants.FUNCTION, paymentRefundUtils.getFunction(Long.valueOf(gl.getFunctionId())).getName());
		                    temp.put("functionid", gl.getFunctionId());
		                }
		                else if (egBillregister.getEgBillregistermis() != null && egBillregister.getEgBillregistermis().getFunction() !=null && egBillregister.getEgBillregistermis().getFunction().getName() != null)
		                {
		                	temp.put(Constants.FUNCTION, egBillregister.getEgBillregistermis().getFunction().getName());
		                    temp.put("functionid", egBillregister.getEgBillregistermis().getFunction().getId());
		                }
		                coa = paymentRefundUtils.getChartOfAccount(gl.getGlcode());
		                System.out.println("coa "+coa);
		                temp.put("glcodeid", coa.getId());
		                glcodeIdList.add(coa.getId());
		                temp.put(Constants.GLCODE, coa.getGlcode());
		                temp.put("accounthead", coa.getName());
		                temp.put(Constants.DEBITAMOUNT, gl.getDebitAmount() == null ? 0 : gl.getDebitAmount());
		                temp.put(Constants.CREDITAMOUNT, gl.getCreditAmount() == null ? 0 : gl.getCreditAmount());
		                temp.put("billdetailid", gl.getId());
		                tempList.add(temp);
		                	    	
		    			
		    					//if (chartOfAccountDetailService.getByGlcodeIdAndDetailTypeId(gl.getGlcodeId().getId(), detailtypeid) != null) {
			    					subledger = new PreApprovedVoucher();
			    					subledger.setGlcode(coa);
			    					final Accountdetailtype detailtype = paymentRefundUtils.getAccountdetailtype(detailtypeid);
			                        detailtypeIdList.add(detailtype);
			                        subledger.setDetailType(detailtype);
			                        payeeMap = new HashMap<>();
			                        //payeeMap = paymentRefundUtils.getAccountDetails(detailkeyid,detailtypeid, payeeMap);
			                        subledger.setDetailKey(name);
			                        subledger.setDetailCode(code);
			                        subledger.setDetailKeyId(detailkeyid);
			                        //subledger.setAmount(gldetail.getAmount());
			                        subledger.setFunctionDetail(temp.get("function") != null ? temp.get("function").toString() : "");
			                        payeeList.add(subledger);
			                        i++;
		    					//}
		    				
		    			final List<Bank> banks = createBankService.getAll();
		    			model.addAttribute("banks", banks);
		    			model.addAttribute("accountDetails", tempList);
		    			model.addAttribute("subLedgerlist", payeeList);
		    			model.addAttribute("dbAmount", dbAmount);
		    			model.addAttribute("crAmount", crAmount);
		    			if(egBillregister.getIsCitizenRefund()!=null && egBillregister.getIsCitizenRefund().equalsIgnoreCase("Y"))
		    			{
		    				model.addAttribute("citizen", egBillregister.getIsCitizenRefund());
		    			}
		    			else {
		    				model.addAttribute("citizen", egBillregister.getIsCitizenRefund());
		    			}
				  }
			  }else {
				  final List<Bank> banks = createBankService.getAll();
	    			model.addAttribute("banks", banks);
	    			model.addAttribute("accountDetails", tempList);
	    			model.addAttribute("subLedgerlist", payeeList);
	    			model.addAttribute("dbAmount", dbAmount);
	    			model.addAttribute("crAmount", crAmount);
	    			if(egBillregister.getIsCitizenRefund()!=null && egBillregister.getIsCitizenRefund().equalsIgnoreCase("Y"))
	    			{
	    				model.addAttribute("citizen", egBillregister.getIsCitizenRefund());
	    			}
	    			else {
	    				model.addAttribute("citizen", egBillregister.getIsCitizenRefund());
	    			}
			  }
				//return "ol-payRefund-request-form-update";
				  return "payRefund-request-form-update"; 
			  }else {
            return "expensebill-update";
        }
			 
            //return "expensebill-update";
        }
        else {
            model.addAttribute("mode", "edit");
            if (egBillregister.getEgBillregistermis().getBudgetaryAppnumber() != null
                    && !egBillregister.getEgBillregistermis().getBudgetaryAppnumber().isEmpty()) {
                budgetDetails = expenseBillService.getBudgetDetailsForBill(egBillregister);
            }

            model.addAttribute("budgetDetails", budgetDetails);
            model.addAttribute("viewBudget", "Y");
			model.addAttribute("egBillregister", egBillregister);
            return EXPENSEBILL_UPDATE_WORKFLOW;
        }
    }

    @RequestMapping(value = "/update/{billId}", method = RequestMethod.POST)
    public String update(@ModelAttribute(EG_BILLREGISTER) final EgBillregister egBillregister,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes, final Model model,
            final HttpServletRequest request, @RequestParam final String workFlowAction)
            throws ApplicationException, IOException {

    	System.out.println("In update controller");
    	
        String mode = "";
        EgBillregister updatedEgBillregister = null;
        System.out.println("scheme----> "+egBillregister.getEgBillregistermis().getScheme());
        System.out.println("schemeId----> "+egBillregister.getEgBillregistermis().getSchemeId());
        System.out.println("subscheme----> "+egBillregister.getEgBillregistermis().getSubScheme());
        System.out.println("subschemeId----> "+egBillregister.getEgBillregistermis().getSubSchemeId());
        if (request.getParameter("mode") != null)
            mode = request.getParameter("mode");
        
        
        if(egBillregister.getStatus().getDescription().equals("Pending for Cancellation"))
        {
        	mode="cancel";
        }
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        List<DocumentUpload> list = new ArrayList<>();
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
        if(uploadedFiles!=null)
        for (int i = 0; i < uploadedFiles.length; i++) {

            Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
            byte[] fileBytes = Files.readAllBytes(path);
            ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
            DocumentUpload upload = new DocumentUpload();
            upload.setInputStream(bios);
            upload.setFileName(fileName[i]);
            System.out.println("File Name : "+fileName[i]);
            upload.setContentType(contentType[i]);
            list.add(upload);
        }

        Long approvalPosition = 0l;
        String approvalComment = "";
        String apporverDesignation = "";

        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");

        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));

        if ((approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                && request.getParameter(APPROVAL_POSITION) != null
                && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
        
       
    	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
    	{
    		approvalPosition =populatePosition();    		
    	}
        if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
            apporverDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
        System.out.println("Approval designation :: "+apporverDesignation);

        if (egBillregister.getState() != null
                && (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())
                        || financialUtils.isBillEditable(egBillregister.getState()))) {
            populateBillDetails(egBillregister);
            validateBillNumber(egBillregister, resultBinder);
            if(egBillregister.getRefundable()!= null && egBillregister.getRefundable().equalsIgnoreCase("Y")
            		&& egBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND)) {
            	refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
            }else {
            validateLedgerAndSubledger(egBillregister, resultBinder);
        }
        }
        
        if(!egBillregister.getBillPayeedetails().isEmpty() && !workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONCANCEL))
    	{
        populateBillDetails(egBillregister);
        validateBillNumber(egBillregister, resultBinder);
           if(egBillregister.getRefundable()!= null && egBillregister.getRefundable().equalsIgnoreCase("Y")
        		&& egBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND)) {
        	 refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
        }else {
        validateLedgerAndSubledger(egBillregister, resultBinder);
    	}
    	}
        List<EgBilldetails> detail=new ArrayList();
        BigDecimal amount= new BigDecimal(0);
        List<EgBilldetails> billDetails = new ArrayList<EgBilldetails>();
        List<EgBillPayeedetails> billPayeeDetails = new ArrayList<EgBillPayeedetails>();
        if(egBillregister.getIsCitizenRefund()!=null)
        {	
        	for (EgBilldetails details : egBillregister.getBillDetails()) {
        		System.out.println("details.getDebitamount() "+details.getDebitamount());
	        	if(details.getDebitamount()!=null)
	        	{
	        		EgBilldetails billdetail = new EgBilldetails();
	        		billdetail.setFunctionid(new BigDecimal(egBillregister.getEgBillregistermis().getFunction().getId()));
	        		billdetail.setGlcodeid(details.getGlcodeid());
	        		billdetail.setDebitamount(details.getDebitamount());
	        		billdetail.setEgBillregister(egBillregister);
	        		billDetails.add(billdetail);
	        		amount=amount.add(details.getDebitamount());
	        		
                    for (final EgBillPayeedetails payeeDetails : egBillregister.getBillPayeedetails()) {
                		EgBillPayeedetails payeeDetail = new EgBillPayeedetails();
                        payeeDetail.setEgBilldetailsId(billdetail);
                        payeeDetail.setAccountDetailTypeId(payeeDetails.getAccountDetailTypeId());
                        payeeDetail.setAccountDetailKeyId(payeeDetails.getAccountDetailKeyId());
                        payeeDetail.setCreditAmount(details.getDebitamount());
                        payeeDetail.setLastUpdatedTime(new Date());
                        billdetail.getEgBillPaydetailes().add(payeeDetail);
                        billPayeeDetails.add(payeeDetail);
                	}
	        	}
        	}
        
        	egBillregister.setBillamount(amount);
        	egBillregister.setPassedamount(amount);
        	egBillregister.getEgBilldetailes().addAll(billDetails);
        	egBillregister.getBillPayeedetails().addAll(billPayeeDetails);
        }
		if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT) && !mode.equalsIgnoreCase("cancel"))
    	{ 
        	  populateEgBillregistermisDetails(egBillregister);
    	}
		
        if (resultBinder.hasErrors()) {
        	
            setDropDownValues(model);
            model.addAttribute("stateType", egBillregister.getClass().getSimpleName());
            prepareWorkflow(model, egBillregister, new WorkflowContainer());
            model.addAttribute(APPROVAL_DESIGNATION, request.getParameter(APPROVAL_DESIGNATION));
            model.addAttribute(APPROVAL_POSITION, request.getParameter(APPROVAL_POSITION));
            model.addAttribute(NET_PAYABLE_ID, request.getParameter(NET_PAYABLE_ID));
            model.addAttribute(NET_PAYABLE_AMOUNT, request.getParameter(NET_PAYABLE_AMOUNT));
            model.addAttribute("designation", request.getParameter("designation"));
            if (egBillregister.getState() != null
                    && (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())
                            || financialUtils.isBillEditable(egBillregister.getState()))) {
                prepareValidActionListByCutOffDate(model);
                model.addAttribute("mode", "edit");
                return "expensebill-update";
            } else {
                model.addAttribute("mode", "view");
                return EXPENSEBILL_VIEW;
            }
        } else {
            try {
                if (null != workFlowAction)
                {
                	egBillregister.setDocumentDetail(list);
                	  if(egBillregister.getRefundable()!= null && egBillregister.getRefundable().equalsIgnoreCase("Y")
                	    && egBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND)) {
                		 updatedEgBillregister = refundBillService.update(egBillregister, approvalPosition, approvalComment, null,
                                 workFlowAction, mode, apporverDesignation);
                	 }else {
                    updatedEgBillregister = expenseBillService.update(egBillregister, approvalPosition, approvalComment, null,
                            workFlowAction, mode, apporverDesignation);
                	 }
                    
                    if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONVERIFY))
                    {
                    	if(updatedEgBillregister.getZone() != null && updatedEgBillregister.getZone().equalsIgnoreCase("Y"))
                    	{
                    		populateauditWorkFlow(updatedEgBillregister);
                    	}
                    	else
                    	{
                    		updatedEgBillregister.setZone("Y");
                    		expenseBillService.saveEgBillregister_afterStateNull(updatedEgBillregister);
                            persistenceService.getSession().flush();
                    	}
                    	
                    }
                }   
                
            } catch (final ValidationException e) {
                setDropDownValues(model);
                model.addAttribute("stateType", egBillregister.getClass().getSimpleName());
                prepareWorkflow(model, egBillregister, new WorkflowContainer());
                model.addAttribute(APPROVAL_DESIGNATION, request.getParameter(APPROVAL_DESIGNATION));
                model.addAttribute(APPROVAL_POSITION, request.getParameter(APPROVAL_POSITION));
                model.addAttribute(NET_PAYABLE_ID, request.getParameter(NET_PAYABLE_ID));
                model.addAttribute(NET_PAYABLE_AMOUNT, request.getParameter(NET_PAYABLE_AMOUNT));
                model.addAttribute("designation", request.getParameter("designation"));
                if (egBillregister.getState() != null
                        && (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(egBillregister.getState().getValue())
                                || financialUtils.isBillEditable(egBillregister.getState()))) {
                    prepareValidActionListByCutOffDate(model);
                    model.addAttribute("mode", "edit");
                    return "expensebill-update";
                } else {
                    model.addAttribute("mode", "view");
                    return EXPENSEBILL_VIEW;
                }
            }

            redirectAttributes.addFlashAttribute(EG_BILLREGISTER, updatedEgBillregister);

            try
            {
            	// For Get Configured ApprovalPosition from workflow history
            	if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONCANCEL))
            	{
	                if (approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
	                    approvalPosition = expenseBillService.getApprovalPositionByMatrixDesignation(
	                    		updatedEgBillregister, null, mode, workFlowAction);
            	}
            }catch (Exception e) {
				e.printStackTrace();
			}
            

           // final String approverName = String.valueOf(request.getParameter("approverName"));
            String approverName = String.valueOf(request.getParameter("approverName"));
            if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
        	{
        		
        		approverName =populateEmpName();
        		
        	}
            if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONREJECT) && approverName.equalsIgnoreCase(""))
        	{	
            	System.out.println("reject name :::"+approverName+"-----"+approverName);
            	if(approvalPosition != null && approvalPosition != 0l)
            	{
            		approverName =getEmployeeName(approvalPosition);
            	}
            	else
            	{
            		approvalPosition=updatedEgBillregister.getState().getOwnerPosition();
            		approverName =getEmployeeName(approvalPosition);
            	}
        		
        	}
            model.addAttribute(BILL_TYPES, BillType.values());
            final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                    updatedEgBillregister.getState(), updatedEgBillregister.getId(), approvalPosition, approverName);
            
	     if(updatedEgBillregister.getRefundable()!= null && updatedEgBillregister.getRefundable().equalsIgnoreCase("Y")
            	    && updatedEgBillregister.getExpendituretype().equalsIgnoreCase(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND)) {
            	return "redirect:/refund/successRefund?approverDetails=" + approverDetails + "&billNumber="
                 + updatedEgBillregister.getBillnumber()+"&billId="
                    + updatedEgBillregister.getId();
             }else {
            return "redirect:/expensebill/success?approverDetails=" + approverDetails + "&billNumber="
                    + updatedEgBillregister.getBillnumber()+"&billId="
                    + updatedEgBillregister.getId();
             }		
            
            /*return "redirect:/expensebill/success?approverDetails=" + approverDetails + "&billNumber="
                    + updatedEgBillregister.getBillnumber()+"&billId="
                    + updatedEgBillregister.getId();*/
        }
    }

    @RequestMapping(value = "/updateCreditDebit/{billId}/{auditId}", method = RequestMethod.GET)
	public String updateCreditDebitJSPOpen(@ModelAttribute("egBillregister") final EgBillregister egBillregister,
			final Model model, @PathVariable String billId, @PathVariable String auditId,
			final BindingResult resultBinder, final HttpServletRequest request) {
		System.out.println(billId);
		egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
		setDropDownValues(model);
		model.addAttribute("mode", "readOnly");
		model.addAttribute(BILL_TYPES, BillType.values());
		prepareBillDetailsForView(egBillregister);
		prepareCheckList(egBillregister);
		final List<CChartOfAccounts> expensePayableAccountList = chartOfAccountsService
				.getNetPayableCodesByAccountDetailType(0);
		for (final EgBilldetails details : egBillregister.getBillDetails())
			if (expensePayableAccountList != null && !expensePayableAccountList.isEmpty()
					&& expensePayableAccountList.contains(details.getChartOfAccounts()))
				model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());

		model.addAttribute("egBillregister", egBillregister);
		return "update_Credit_Debit";
	}

	@RequestMapping(value = "/updateCreditDebit/{billId}/{auditId}", params = "saveCreditDebitDetails", method = RequestMethod.POST)
	public String updateCreditDebitSaved(@ModelAttribute("egBillregister") final EgBillregister egBillregister,
			final Model model, @PathVariable String billId, @PathVariable String auditId,
			final BindingResult resultBinder, final HttpServletRequest request) {
		
		String[] debit_Amount = request.getParameterValues("accountDebitAmount_Value");
		String[] credit_Amount = request.getParameterValues("accountCreditAmount_Value");
		String[] subledger_Amount = request.getParameterValues("accountSubLedgerAmount_Value");
		egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
		setDropDownValues(model);
		model.addAttribute(BILL_TYPES, BillType.values());
		prepareBillDetailsForView(egBillregister);
		prepareCheckList(egBillregister);
		final List<CChartOfAccounts> expensePayableAccountList = chartOfAccountsService
				.getNetPayableCodesByAccountDetailType(0);
		for (final EgBilldetails details : egBillregister.getBillDetails())
			if (expensePayableAccountList != null && !expensePayableAccountList.isEmpty()
					&& expensePayableAccountList.contains(details.getChartOfAccounts()))
				model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
		
		BigDecimal actual_Credit_sum = new BigDecimal("0.0"), actual_Debit_sum = new BigDecimal("0.0"), actual_Subledger_sum = new BigDecimal("0.0");
		BigDecimal update_Credit_sum = new BigDecimal("0.0"), update_Debit_sum = new BigDecimal("0.0"), update_Subledger_sum = new BigDecimal("0.0");
		int i = 0;
		for (String cred : credit_Amount) {
			try {
				if (egBillregister.getBillDetails().get(i).getCreditamount() != (new BigDecimal("0"))) {
					actual_Credit_sum = actual_Credit_sum.add(egBillregister.getBillDetails().get(i).getCreditamount());
					egBillregister.getBillDetails().get(i).setCreditamount(new BigDecimal(cred));
					update_Credit_sum = update_Credit_sum.add(egBillregister.getBillDetails().get(i).getCreditamount());
				}
				i++;
			} catch (Exception e) {
				model.addAttribute("message", "Failure : Please enter correct details");
				return "success_saved";
			}
		}
		int j = 0;
		for (String cred : debit_Amount) {
			try {
				if (egBillregister.getBillDetails().get(j).getDebitamount() != (new BigDecimal("0"))) {
					actual_Debit_sum = actual_Debit_sum.add(egBillregister.getBillDetails().get(j).getDebitamount());
					egBillregister.getBillDetails().get(j).setDebitamount(new BigDecimal(cred));
					update_Debit_sum = update_Debit_sum.add(egBillregister.getBillDetails().get(j).getDebitamount());
				}
				j++;
			} catch (Exception e) {
				model.addAttribute("message", "Failure : Please enter correct details");
				return "success_saved";
			}																	
		}
		egBillregister.setBillamount(update_Debit_sum);
		egBillregister.setPassedamount(update_Debit_sum);
		EgBillPayeedetails payee = new EgBillPayeedetails();
		j = 0;
		if(subledger_Amount!=null)
		{
			for (String sub : subledger_Amount) {
				try {
					if (payee.getCreditAmount() != (new BigDecimal("0"))) {
						actual_Subledger_sum = actual_Subledger_sum.add(egBillregister.getBillPayeedetails().get(j).getCreditAmount());
						egBillregister.getBillPayeedetails().get(j).setCreditAmount(new BigDecimal(sub));
						update_Subledger_sum = update_Subledger_sum.add(egBillregister.getBillPayeedetails().get(j).getCreditAmount());
					}
					else {
						actual_Subledger_sum = actual_Subledger_sum.add(egBillregister.getBillPayeedetails().get(j).getDebitAmount());
						egBillregister.getBillPayeedetails().get(j).setDebitAmount(new BigDecimal(sub));
						update_Subledger_sum = update_Subledger_sum.add(egBillregister.getBillPayeedetails().get(j).getDebitAmount());
					}
					j++;
				} catch (Exception e) {
					model.addAttribute("message", "Failure : Please enter correct details");
					return "success_saved";
				}																	
			}
		}
		
		if (actual_Debit_sum.compareTo(update_Debit_sum) == -1) {
			model.addAttribute("message",
					"Failure : Updated Debit Details must not be greater than or equal to Previous record");
			return "success_saved";
		} else if (actual_Credit_sum.compareTo(update_Credit_sum) == -1) {
			model.addAttribute("message",
					"Failure : Updated Credit Details must not be greater than or equal to Previous record");
			return "success_saved";
		} else if (actual_Subledger_sum.compareTo(update_Subledger_sum) == -1) {
			model.addAttribute("message",
					"Failure : Updated Subledger Details must not be greater than or equal to Previous record");
			return "success_saved";
		} else if (update_Credit_sum.compareTo(update_Debit_sum) == -1
				|| update_Credit_sum.compareTo(update_Debit_sum) == 1) {
			String s = "Failure : Updated Credit Details and Debit Details must be equal. Credit Sum is "
					+ update_Credit_sum + " Debit Sum is " + update_Debit_sum;
			model.addAttribute("message", s);
			return "success_saved";
		} else if (subledger_Amount!=null && ( update_Subledger_sum.compareTo(update_Credit_sum) == -1
				|| update_Subledger_sum.compareTo(update_Credit_sum) == 1)) {
			String s = "Failure : Updated Subledger Details and Credit Details must be equal. Credit Sum is "
					+ update_Credit_sum + " Subledger Sum is " + update_Subledger_sum;
			model.addAttribute("message", s);
			return "success_saved";
		}

		try {
			Date d = new Date();
			AuditDetails audit = auditService.findByid(Long.parseLong(auditId));
			org.egov.infra.admin.master.entity.Department department = departmentService
					.getDepartmentByCode(audit.getDepartment());
			RetrachmentDetails retrachmentDetails = retrachmentService.findByAuditId(auditId);
			if (retrachmentDetails == null) {
				RetrachmentDetails retrachmentDetail = new RetrachmentDetails();
				retrachmentDetail.setAuditid(auditId);
				retrachmentDetail.setRetrachmentdate(d);
				retrachmentDetail.setAmountofbill(actual_Debit_sum);
				retrachmentDetail.setAmountbyaudit(update_Credit_sum);
				BigDecimal retra = actual_Credit_sum.subtract(update_Credit_sum);
				retrachmentDetail.setAmountretrached(retra);
				retrachmentDetail.setBilldetail(egBillregister.getEgBillregistermis().getNarration());
				retrachmentDetail.setDepartment_name(department.getName());
				retrachmentService.createRetrachment(retrachmentDetail);
			} else {
				retrachmentDetails.setAmountofbill(actual_Debit_sum);
				retrachmentDetails.setAmountbyaudit(update_Credit_sum);
				BigDecimal retra = actual_Credit_sum.subtract(update_Credit_sum);
				retrachmentDetails.setAmountretrached(retra);
				retrachmentService.createRetrachment(retrachmentDetails);
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "Failure : Data is not saved.");
			return "success_saved";
		}

		model.addAttribute("message", "Success : Your Credit and Debit Details are successfully saved.");
		return "success_saved";
	}
	
    private void populateauditWorkFlow(EgBillregister updatedEgBillregister) {
    	AuditDetails audit=new AuditDetails();
    	final User user = securityUtils.getCurrentUser();
    	Position owenrPos = new Position();
    	//List<ManageAuditor> auditorList=manageAuditorService.getAudiorsDepartmentByType(Integer.parseInt(updatedEgBillregister.getEgBillregistermis().getDepartmentcode()), "Auditor");
    	Integer id = validateAuditor(updatedEgBillregister);
    	if(id != null && id > 0)
    	{
    		owenrPos.setId(Long.valueOf(id));
    	}
    	else
    	{
    		List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
                    FinancialConstants.MODULE_NAME_APPCONFIG, "AUDIT_TASK_DEFAULT");
        	owenrPos.setId(Long.valueOf(configValuesByModuleAndKey.get(0).getValue()));
    	}
    	AuditNumberGenerator v = beanResolver.getAutoNumberServiceFor(AuditNumberGenerator.class);

		final String preAuditNumber = v.getNextPreAuditNumber(updatedEgBillregister.getEgBillregistermis().getDepartmentcode());
    	audit.setAuditno(preAuditNumber);
    	audit.setType(PRE);
    	audit.setDepartment(updatedEgBillregister.getEgBillregistermis().getDepartmentcode());
    	audit.setLead_auditor(owenrPos.getId());
    	audit.setAuditor_name(getEmployeeName(owenrPos.getId()));
    	audit.setEgBillregister(updatedEgBillregister);
    	audit.setStatus(egwStatusDAO.getStatusByModuleAndCode(AUDIT2, CREATED));
    	audit.setPassUnderobjection(0);
    	audit.transition().start().withSenderName(user.getUsername() + STRING + user.getName())
        .withComments(INITIATED_PRE_AUDIT)
        .withStateValue(CREATED).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
        .withNextAction(PRE_AUDIT_PENDING)
        .withNatureOfTask(PRE_AUDIT)
        .withCreatedBy(user.getId())
        .withtLastModifiedBy(user.getId());
    	applyAuditing(audit,updatedEgBillregister.getCreatedBy());
    	 auditRepository.save(audit);
		persistenceService.getSession().flush();
		
	}

	@RequestMapping(value = "/view/{billId}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable String billId,
            final HttpServletRequest request) throws ApplicationException {
        if (billId.contains("showMode")) {
            String[] billIds = billId.split("\\&");
            billId = billIds[0];
        }
        final EgBillregister egBillregister = expenseBillService.getById(Long.parseLong(billId));
        final List<DocumentUpload> documents = documentUploadRepository.findByObjectId(Long.valueOf(billId));
        egBillregister.setDocumentDetail(documents);
        String departmentCode = this.getDepartmentName(egBillregister.getEgBillregistermis().getDepartmentcode());
        egBillregister.getEgBillregistermis().setDepartmentName(departmentCode);
        setDropDownValues(model);
        egBillregister.getBillDetails().addAll(egBillregister.getEgBilldetailes());
        model.addAttribute("mode", "readOnly");
        model.addAttribute(BILL_TYPES, BillType.values());
        prepareBillDetailsForView(egBillregister);
        prepareCheckList(egBillregister);
        final List<CChartOfAccounts> expensePayableAccountList = chartOfAccountsService
                .getNetPayableCodesByAccountDetailType(0);
        for (final EgBilldetails details : egBillregister.getBillDetails())
            if (expensePayableAccountList != null && !expensePayableAccountList.isEmpty()
                    && expensePayableAccountList.contains(details.getChartOfAccounts()))
                model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
        model.addAttribute(EG_BILLREGISTER, egBillregister);
        return EXPENSEBILL_VIEW;
    }

    private void prepareCheckList(final EgBillregister egBillregister) {
        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
        egBillregister.getCheckLists().addAll(checkLists);
    }

    private void prepareCheckListForEdit(final EgBillregister egBillregister, final Model model) {
        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
        egBillregister.getCheckLists().addAll(checkLists);
        final StringBuilder selectedCheckList = new StringBuilder();
        for (final EgChecklists checkList : egBillregister.getCheckLists()) {
            selectedCheckList.append(checkList.getAppconfigvalue().getId());
            selectedCheckList.append("-");
            selectedCheckList.append(checkList.getChecklistvalue());
            selectedCheckList.append(",");
        }
        if (!checkLists.isEmpty())
            model.addAttribute("selectedCheckList", selectedCheckList.toString().substring(0, selectedCheckList.length() - 1));
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
        model.addAttribute("fundList", fundHibernateDAO.findAllActiveFunds());
    }

    private String getDepartmentName(String departmentCode) {

        List<Department> deptlist = this.masterDataCache.get("egi-department");
        String departmentName = null;

        if (null != deptlist && !deptlist.isEmpty()) {

            List<Department> dept = deptlist.stream()
                    .filter(department -> departmentCode.equalsIgnoreCase(department.getCode()))
                    .collect(Collectors.toList());
            if (null != dept && dept.size() > 0)
                departmentName = dept.get(0).getName();
        }

        if (null == departmentName) {
            Department dept = this.microServiceUtil.getDepartmentByCode(departmentCode);
            if (null != dept)
                departmentName = dept.getName();
        }

        return departmentName;
    }
    
    /*public List<FileStoreMapper> getOriginalFiles() {
		return originalFiles;
	}

	public void setOriginalFiles(List<FileStoreMapper> originalFiles) {
		this.originalFiles = originalFiles;
	}*/
    
    public void applyAuditing(AbstractAuditable auditable, Long createdBy) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(createdBy);
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(createdBy);
		auditable.setLastModifiedDate(currentDate);
	}
	
	private Long populatePosition() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	Long pos=null;
    	List<EmployeeInfo> employs = microServiceUtil.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		pos=employs.get(0).getAssignments().get(0).getPosition();
    		
    	}
    	//System.out.println("pos-----populatePosition---()----------------------"+pos);
		return pos;
	}
    private String populateEmpName() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	String empName=null;
    	Long pos=null;
    	List<EmployeeInfo> employs = microServiceUtil.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		//pos=employs.get(0).getAssignments().get(0).getPosition();
    		empName=employs.get(0).getUser().getName();
    		
    	}
		return empName;
	}
    
    public String getEmployeeName(Long empId){
        
        return microServiceUtil.getEmployee(empId, null, null, null).get(0).getUser().getName();
     }
 	
    
    
    private void refundvalidateLedgerAndSubledger(final EgBillregister egBillregister, final BindingResult resultBinder) {
        BigDecimal totalDrAmt = BigDecimal.ZERO;
        BigDecimal totalCrAmt = BigDecimal.ZERO;
        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {
            if (details.getDebitamount() != null)
                totalDrAmt = totalDrAmt.add(details.getDebitamount());
            if (details.getCreditamount() != null)
                totalCrAmt = totalCrAmt.add(details.getCreditamount());
            if (details.getGlcodeid() == null)
                resultBinder.reject("msg.expense.bill.accdetail.accmissing", new String[] {}, null);

            /*
             * if (details.getDebitamount() != null && details.getCreditamount()
             * != null && details.getDebitamount().equals(BigDecimal.ZERO) &&
             * details.getCreditamount().equals(BigDecimal.ZERO) &&
             * details.getGlcodeid() != null)
             * resultBinder.reject("msg.expense.bill.accdetail.amountzero", new
             * String[] { details.getChartOfAccounts().getGlcode() }, null);
             */
           
            	boolean isDebitCreditAmountEmpty = (details.getDebitamount() == null
                        || (details.getDebitamount() != null && details.getDebitamount().compareTo(BigDecimal.ZERO) == 0))
                        && (details.getCreditamount() == null || (details.getCreditamount() != null
                                && details.getCreditamount().compareTo(BigDecimal.ZERO) == 0));
                if (isDebitCreditAmountEmpty) {
                	if(egBillregister.getRefundable()!=null && !egBillregister.getRefundable().equalsIgnoreCase("Y")) {
                    resultBinder.reject("msg.expense.bill.accdetail.amountzero",
                            new String[] { details.getChartOfAccounts().getGlcode() }, null);
                }
                }
          
            

            if (details.getDebitamount() != null && details.getCreditamount() != null
                    && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1
                    && details.getCreditamount().compareTo(BigDecimal.ZERO) == 1) {
            	if(egBillregister.getRefundable()!=null && !egBillregister.getRefundable().equalsIgnoreCase("Y")) {
                resultBinder.reject("msg.expense.bill.accdetail.amount",
                        new String[] { details.getChartOfAccounts().getGlcode() }, null);
        }
            }
        }
       // if (totalDrAmt.compareTo(totalCrAmt) != 0)
            //resultBinder.reject("msg.expense.bill.accdetail.drcrmatch", new String[] {}, null);
        refundvalidateSubledgerDetails(egBillregister, resultBinder);
    }

    private void refundvalidateSubledgerDetails(final EgBillregister egBillregister, final BindingResult resultBinder) {
        Boolean check;
        BigDecimal detailAmt;
        BigDecimal payeeDetailAmt;
        for (final EgBilldetails details : egBillregister.getEgBilldetailes()) {

            detailAmt = BigDecimal.ZERO;
            payeeDetailAmt = BigDecimal.ZERO;

            if (details.getDebitamount() != null && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1)
                detailAmt = details.getDebitamount();
            else if (details.getCreditamount() != null &&
                    details.getCreditamount().compareTo(BigDecimal.ZERO) == 1)
                detailAmt = details.getCreditamount();

            for (final EgBillPayeedetails payeeDetails : details.getEgBillPaydetailes()) {
                if (payeeDetails != null) {
                    if (payeeDetails.getDebitAmount() != null && payeeDetails.getCreditAmount() != null
                            && payeeDetails.getDebitAmount().equals(BigDecimal.ZERO)
                            && payeeDetails.getCreditAmount().equals(BigDecimal.ZERO))
                    {
                    	if(egBillregister.getRefundable()!=null && !egBillregister.getRefundable().equalsIgnoreCase("Y")) {
                        resultBinder.reject("msg.expense.bill.subledger.amountzero",
                                new String[] { details.getChartOfAccounts().getGlcode() }, null);
                    	}
                    }

                    if (payeeDetails.getDebitAmount() != null && payeeDetails.getCreditAmount() != null
                            && payeeDetails.getDebitAmount().compareTo(BigDecimal.ZERO) == 1
                            && payeeDetails.getCreditAmount().compareTo(BigDecimal.ZERO) == 1) {
                    	if(egBillregister.getRefundable()!=null && !egBillregister.getRefundable().equalsIgnoreCase("Y")) {
                        resultBinder.reject("msg.expense.bill.subledger.amount",
                                new String[] { details.getChartOfAccounts().getGlcode() }, null);
                    	}
                    }

                    if (payeeDetails.getDebitAmount() != null && payeeDetails.getDebitAmount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetailAmt = payeeDetailAmt.add(payeeDetails.getDebitAmount());
                    else if (payeeDetails.getCreditAmount() != null
                            && payeeDetails.getCreditAmount().compareTo(BigDecimal.ZERO) == 1)
                        payeeDetailAmt = payeeDetailAmt.add(payeeDetails.getCreditAmount());

                    check = false;
                    for (final CChartOfAccountDetail coaDetails : details.getChartOfAccounts().getChartOfAccountDetails())
                        if (payeeDetails.getAccountDetailTypeId() == coaDetails.getDetailTypeId().getId())
                            check = true;
                    //if (!check)
                      //  resultBinder.reject("msg.expense.bill.subledger.mismatch",
                        //        new String[] { details.getChartOfAccounts().getGlcode() }, null);

                }

            }

           // if (detailAmt.compareTo(payeeDetailAmt) != 0 && !details.getEgBillPaydetailes().isEmpty())
               // resultBinder.reject("msg.expense.bill.subledger.amtnotmatchinng",
                        //new String[] { details.getChartOfAccounts().getGlcode() }, null);
        }
    }
    
    public Integer validateAuditor(EgBillregister updatedEgBillregister) {

		List<ManageAuditor> auditorList = manageAuditorService.getAudiorsDepartmentByType(
				Integer.parseInt(updatedEgBillregister.getEgBillregistermis().getDepartmentcode()), "Auditor");

		String billtype1 = updatedEgBillregister.getEgBillregistermis().getEgBillSubType().getName();
		String subDivision1 = updatedEgBillregister.getEgBillregistermis().getSubdivision();
		// String departmentName1 =
		// updatedEgBillregister.getEgBillregistermis().getDepartmentName();

		String pre_audit = "Pre Audit";
		Integer id = 0;
		if (auditorList != null && !auditorList.isEmpty()) {

			for (ManageAuditor manageAuditor : auditorList) {
				if (manageAuditor.getBilltype() != null && manageAuditor.getSubdivision() != null
						&& manageAuditor.getAudittype().equalsIgnoreCase(pre_audit)) {
					if (manageAuditor.getSubdivision().equalsIgnoreCase(subDivision1)
							&& manageAuditor.getBilltype().equalsIgnoreCase(billtype1)) {
						return (manageAuditor.getEmployeeid());
					}
				}
			}

			if (id == 0) {

				for (ManageAuditor manageAuditor : auditorList) {
					if (manageAuditor.getSubdivision() != null
							&& manageAuditor.getAudittype().equalsIgnoreCase(pre_audit)
							&& manageAuditor.getBilltype() == null) {
						if (manageAuditor.getSubdivision().equalsIgnoreCase(subDivision1)) {
							return (manageAuditor.getEmployeeid());

						}
					}
				}
			}

			if (id == 0) {

				for (ManageAuditor manageAuditor : auditorList) {
					if (manageAuditor.getBilltype() != null) {
						if (manageAuditor.getBilltype().equalsIgnoreCase(billtype1)
								&& manageAuditor.getAudittype().equalsIgnoreCase(pre_audit)
								&& manageAuditor.getSubdivision() == null) {
							return (manageAuditor.getEmployeeid());
						}
					}
				}
			}

			if (id == 0) {
				for (ManageAuditor manageAuditor : auditorList) {
					if (manageAuditor.getAudittype().equalsIgnoreCase(pre_audit)
							&& manageAuditor.getSubdivision() == null && manageAuditor.getBilltype() == null) {
						return (manageAuditor.getEmployeeid());
					}
				}
			}

		} else {
			return id;
		}
		return id;
	}
}
