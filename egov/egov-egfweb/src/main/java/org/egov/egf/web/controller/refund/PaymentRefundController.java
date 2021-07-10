package org.egov.egf.web.controller.refund;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bank;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CGeneralLedgerDetail;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.CheckListService;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.egf.autonumber.ExpenseBillNumberGenerator;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.budget.model.BudgetControlType;
import org.egov.egf.budget.service.BudgetControlTypeService;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.commons.VoucherSearchUtil;
import org.egov.egf.commons.bank.service.CreateBankService;
import org.egov.egf.contract.model.RefundLedger;
import org.egov.egf.contract.model.RefundRequest;
import org.egov.egf.contract.model.RefundResponse;
import org.egov.egf.contract.model.VoucherDetailsResponse;
import org.egov.egf.contract.model.VoucherResponse;
import org.egov.egf.contract.model.VoucherSearch;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.expensebill.service.RefundBillService;
import org.egov.egf.expensebill.service.VouchermisService;
import org.egov.egf.masters.services.OtherPartyService;
import org.egov.egf.utils.FinancialUtils;
import org.egov.egf.web.controller.expensebill.BaseBillController;
import org.egov.egf.web.controller.expensebill.CreateExpenseBillController;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.ChartOfAccounts;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infstr.models.EgChecklists;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.BillType;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBillSubType;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.EgBillregistermis;
import org.egov.model.masters.OtherParty;
import org.egov.model.voucher.PreApprovedVoucher;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.PaymentRefundUtils;
import org.geotools.filter.IsNullImpl;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.python.netty.util.internal.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exilant.eGov.src.common.SubDivision;

@Controller
@RequestMapping("/refund")
public class PaymentRefundController extends BaseBillController {
	
	private static final Logger LOGGER = Logger.getLogger(PaymentRefundController.class);
	private CVoucherHeader voucherHeader = new CVoucherHeader();
	 private static final String ACCDETAILTYPEQUERY = " from Accountdetailtype where id=?";
	private static final String STATE_TYPE = "stateType";
	private static final String BILL_TYPES = "billTypes";
	private static final String EG_BILLREGISTER = "egBillregister";
	
	private static final String NET_PAYABLE_ID = "netPayableId";
	private static final String DESIGNATION = "designation";
    private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    private static final String EXPENSEBILL_VIEW = "expensebill-view";
    private static final String NET_PAYABLE_AMOUNT = "netPayableAmount";
    private static final String REFUNDFOROLDVOUCHER_FORM = "refund-requestoldvoucher-form";

    private static final int BUFFER_SIZE = 4096;
	
	private final String datePattern="dd/MM/yyyy";
	private final String VOUCHER_SEARCH="voucherSearch";
	private final String PR_VOUCHER_SEARCH="pr-voucher-search";
	private final String PR_VOUCHER_VIEW="pr-voucher-view";
	private final String PR_REQUEST_FORM="pr-request-form";
	
	private final String RE_PR_APPROVEDVOUCHER="re-pr-approvedvoucher";
	
	
	
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	
	@Autowired
	private CheckListService checkListService;
	
	@Autowired
	private MicroserviceUtils microServiceUtil;
	
	
	@Qualifier("chartOfAccountsService")
	@Autowired
	private ChartOfAccountsService chartOfAccountsService;
	 
	 private void prepareCheckList(final EgBillregister egBillregister) {
	        final List<EgChecklists> checkLists = checkListService.getByObjectId(egBillregister.getId());
	        egBillregister.getCheckLists().addAll(checkLists);
	    }
	
	    private String cutOffDate;
	    protected DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	    DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
	    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    private boolean finanicalYearAndClosedPeriodCheckIsClosed=false;
	    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
	    Date date;
	
	private final Map<String, String> VOUCHER_TYPES = new HashMap<String, String>();
	
	@Autowired
	 private EgovMasterDataCaching masterDataCache;
	 @Autowired
	 private EgovCommon egovCommon;
	@Autowired
	protected AppConfigValueService appConfigValuesService;
	@Autowired
	protected MicroserviceUtils microserviceUtils;
		
	@Autowired
	private PaymentRefundUtils paymentRefundUtils;
	@Autowired
	private VoucherSearchUtil voucherSearchUtil;
	@Autowired
    private ChartOfAccountDetailService chartOfAccountDetailService;	
	@Autowired
    private EgBillSubTypeService egBillSubTypeService;
    @Autowired
    private AccountdetailtypeService accountdetailtypeService;
    @Autowired
    private ExpenseBillService expenseBillService;
    @Autowired
    private RefundBillService refundBillService;
    @Autowired
	private FunctionDAO functionDAO;
    @Autowired
    private FinancialUtils financialUtils;
    @Autowired
	private AutonumberServiceBeanResolver beanResolver;
    
    @Autowired
    private VouchermisService vouchermisService;
    @Autowired
    private ApplicationContext applicationContext;
    
    private static final String BANK = "bank";

    @Autowired
    private CreateBankService createBankService;
    @Autowired
    private OtherPartyService otherPartyService;
    @Autowired
    private BudgetControlTypeService budgetControlTypeService;

    @Autowired 
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;
    
    public PaymentRefundController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
       
    }
  
    
	private void prepareSearchForm(final Model model) {
		VoucherSearch voucherSearch = new VoucherSearch(); 
		voucherSearch.setFromDate(DateUtils.getFormattedDate(paymentRefundUtils.finYearDate(), datePattern));
		VOUCHER_TYPES.put(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL, FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
		VOUCHER_TYPES.put(FinancialConstants.STANDARD_VOUCHER_TYPE_RECEIPT, FinancialConstants.STANDARD_VOUCHER_TYPE_RECEIPT);		
		model.addAttribute("voucherTypeList", VOUCHER_TYPES);
		model.addAttribute("departmentList", masterDataCache.get("egi-department"));
		model.addAttribute("fundList",	paymentRefundUtils.getAllFunds());
		model.addAttribute("serviceTypeList", microserviceUtils.getBusinessService(null));
		model.addAttribute(VOUCHER_SEARCH, voucherSearch);
    }
	
	@RequestMapping(value = "/_searchForm", method = {RequestMethod.GET,RequestMethod.POST})
    public String prVoucherSearch(final Model model) {
		prepareSearchForm(model);
        return PR_VOUCHER_SEARCH;
    }
	
	@RequestMapping(value = "/ajax/_voucherSearch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JSONObject getVouchers(final Model model, @ModelAttribute final VoucherSearch voucherSearch) {
		List<Map<String, Object>> voucherList = new ArrayList<Map<String, Object>>();		
		Map<String, Object> voucherMap = null;
		
		CVoucherHeader voucherHeader = new CVoucherHeader();		
		voucherHeader.setVouchermis(new Vouchermis());
				
		Date fromDate = DateUtils.getDate(voucherSearch.getFromDate(), datePattern);
		Date toDate = DateUtils.getDate(voucherSearch.getToDate(), datePattern);
		
		if(!StringUtils.isEmpty(voucherSearch.getVoucherType())) {
			voucherHeader.setType(voucherSearch.getVoucherType());
		}		
		if(!StringUtils.isEmpty(voucherSearch.getVoucherName())) {
			voucherHeader.setName(voucherSearch.getVoucherName());
		}		
		if(!StringUtils.isEmpty(voucherSearch.getVoucherNumber())) {
			voucherHeader.setVoucherNumber(voucherSearch.getVoucherNumber());
		}
		if(!StringUtils.isEmpty(voucherSearch.getDeptCode())) {
			voucherHeader.getVouchermis().setDepartmentcode(voucherSearch.getDeptCode());
		}
		if(!StringUtils.isEmpty(voucherSearch.getReceiptNumber())) {
			voucherHeader.getVouchermis().setRecieptNumber(voucherSearch.getReceiptNumber());
		}
		if(!StringUtils.isEmpty(voucherSearch.getFundId())) {
			Fund fund = new Fund();
			fund.setId(Integer.valueOf(voucherSearch.getFundId()));
			voucherHeader.setFundId(fund);
		}
		if(!StringUtils.isEmpty(voucherSearch.getServiceType())) {
			voucherHeader.getVouchermis().setServiceName(voucherSearch.getServiceType());
		}
		System.out.println("test");
		List<CVoucherHeader> list = null;
		try {
			list = voucherSearchUtil.search(voucherHeader, fromDate, toDate, "");
		} catch (ApplicationException | ParseException e) {
			e.printStackTrace();
		}
		
		if(null!=list) {
			List<Receipt> receipts = null;
			if(!StringUtils.isEmpty(voucherSearch.getPartyName())) {
				receipts = microserviceUtils.searchRecieptsFinance("MISCELLANEOUS", fromDate, toDate, null,
		                (voucherSearch.getReceiptNumber() != null && !voucherSearch.getReceiptNumber().isEmpty() && !"".equalsIgnoreCase(voucherSearch.getReceiptNumber()))
		                        ? voucherSearch.getReceiptNumber() : null,"search");
			}			
			
			Map<Integer, String> sourceMap = paymentRefundUtils.populateSourceMap();
			Map<Long,String> paymentVoucherMap = paymentRefundUtils.populateVoucherMap(list);
			boolean isReceiptNoExist=false;
			for (final CVoucherHeader voucherheader : list) {				
				if(null!=receipts && !receipts.isEmpty()) {	
					isReceiptNoExist=false;
					for (Receipt receipt : receipts) {
						if(!StringUtils.isEmpty(voucherheader.getVouchermis().getRecieptNumber())
								&& !StringUtils.isEmpty(receipt.getReceiptNumber())
									&& receipt.getReceiptNumber().equalsIgnoreCase(voucherheader.getVouchermis().getRecieptNumber())) {							
							for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {
								if((!StringUtils.isEmpty(bill.getPayerName()) && bill.getPayerName().toLowerCase().contains(voucherSearch.getPartyName().toLowerCase()))
										|| (!StringUtils.isEmpty(bill.getPayerAddress()) &&  bill.getPayerAddress().toLowerCase().contains(voucherSearch.getPartyName().toLowerCase()))) {
									voucherMap.put("payeeName", bill.getPayerName());
									isReceiptNoExist=true;
								}							
							}
						}
					}					
					if(!isReceiptNoExist) {
						continue;
					}
				}
				
				voucherMap = new HashMap<String, Object>();
				final BigDecimal amt = voucherheader.getTotalAmount();
				voucherMap.put("id", voucherheader.getId());
				voucherMap.put("vouchernumber", voucherheader.getVoucherNumber());
				voucherMap.put("type", voucherheader.getType());
				voucherMap.put("name", voucherheader.getName());
				if (voucherheader.getVouchermis() != null && voucherheader.getVouchermis().getDepartmentcode() != null
						&& !voucherheader.getVouchermis().getDepartmentcode().equals("-1")) {
					org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(voucherheader.getVouchermis().getDepartmentcode());
					voucherMap.put("deptName", depList.getName());
				}else {
					voucherMap.put("deptName", "-");
				}
				voucherMap.put("voucherdate", voucherheader.getVoucherDate());
				voucherMap.put("fundname", voucherheader.getFundId().getName());
				if (voucherheader.getModuleId() == null)
					voucherMap.put("source", "Internal");
				else
					voucherMap.put("source", sourceMap.get(voucherheader.getModuleId()));

				voucherMap.put("amount", amt.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
				System.out.println(paymentRefundUtils.getVoucherStatus(voucherheader.getStatus()));
				voucherMap.put("status", paymentRefundUtils.getVoucherStatus(voucherheader.getStatus()));
				if(!(voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() != null) {
					//voucherMap.put("pendingWith", paymentRefundUtils.getEmployeeName(voucherheader.getState().getOwnerPosition()));
					voucherMap.put("pendingWith", "NA");
				}else if((voucherheader.getName().equals("Remittance Payment") || voucherheader.getName().equals("Bill Payment") || voucherheader.getName().equals("Direct Bank Payment")) && voucherheader.getStatus()!=4 && voucherheader.getStatus()!=0 && voucherheader.getState() == null) {
					if(paymentVoucherMap.get(voucherheader.getId()) != null) {
						voucherMap.put("pendingWith", paymentVoucherMap.get(voucherheader.getId()));
					}
					else {
						voucherMap.put("pendingWith", "-");
					}					
				}else {
					voucherMap.put("pendingWith", "-");
				}
				if(!ObjectUtils.isEmpty(voucherheader.getVouchermis())) {
				if(!StringUtils.isEmpty(voucherheader.getVouchermis().getRecieptNumber())) {
					voucherMap.put("receiptNumber", voucherheader.getVouchermis().getRecieptNumber());
				}else {
					voucherMap.put("receiptNumber", "-");
				}
				if(!StringUtils.isEmpty(voucherHeader.getVouchermis().getServiceName())) {
					voucherMap.put("service", voucherheader.getVouchermis().getServiceName());
				}else {
					voucherMap.put("service", "-");
				}
				
				}
				
								
				if(!voucherMap.containsKey("payeeName")) {
					voucherMap.put("payeeName", "-");
				}
				
				//voucherList.add(voucherMap);
				if(paymentRefundUtils.getVoucherStatus(voucherheader.getStatus())=="Approved"){
				voucherList.add(voucherMap);
				}
			}
			
			JSONArray jsonArr=new JSONArray();
		    for (Map<String, Object> map : voucherList) {
		        JSONObject jsonObj=new JSONObject();
		        for (Map.Entry<String, Object> entry : map.entrySet()) {
		            String key = entry.getKey();
		            Object value = entry.getValue();
		            jsonObj.put(key,value);                           
		        }
		        jsonArr.add(jsonObj);
		    }
		    
		    JSONObject data=new JSONObject();
		    data.put("data", jsonArr);
		    return data;
		}
		
		JSONObject data=new JSONObject();
	    data.put("data", "[]");
	    return data;
	}
	
	@RequestMapping(value = "/ajax/_getVoucherNameByType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getVoucherNameByType(@RequestParam final String type, final HttpServletResponse response) throws IOException{
		return paymentRefundUtils.getVoucherNamesByType(type);
	}
	
	@RequestMapping(value = "/_viewVoucher", method = {RequestMethod.GET, RequestMethod.POST})
	public String prViewVoucher(@RequestParam(name = "vhid") final Long vhid, final Model model) {
		VoucherDetailsResponse voucherDetails = new VoucherDetailsResponse();
		final List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
        final List<PreApprovedVoucher> payeeList = new ArrayList<PreApprovedVoucher>();
        Map<String, Object> temp = null;
        Map<String, Object> payeeMap = null;
        PreApprovedVoucher subledger = null;
        CChartOfAccounts coa = null;
        final List<Long> glcodeIdList = new ArrayList<Long>();
		voucherDetails.setId(vhid);
		CVoucherHeader voucherHeader = paymentRefundUtils.getVoucherHeader(vhid);
		final List<Accountdetailtype> detailtypeIdList = new ArrayList<Accountdetailtype>();
		BigDecimal dbAmount = BigDecimal.ZERO;
		BigDecimal crAmount = BigDecimal.ZERO;
		
		if(null!=voucherHeader) {
			voucherDetails.setName(voucherHeader.getName());
			voucherDetails.setType(voucherHeader.getType());
			voucherDetails.setDescription(voucherHeader.getDescription());
			voucherDetails.setEffectiveDate(voucherHeader.getEffectiveDate());
			voucherDetails.setVoucherNumber(voucherHeader.getVoucherNumber());
			voucherDetails.setVoucherDate(voucherHeader.getVoucherDate());
			voucherDetails.setFund(voucherHeader.getFundId());
			voucherDetails.setFiscalPeriodId(voucherHeader.getFiscalPeriodId());
			voucherDetails.setStatus(voucherHeader.getStatus());			
			voucherDetails.setOriginalvcId(voucherHeader.getOriginalvcId());
			voucherDetails.setIsConfirmed(voucherHeader.getIsConfirmed());
			voucherDetails.setRefvhId(voucherHeader.getRefvhId());
			voucherDetails.setCgvn(voucherHeader.getCgvn());
			voucherDetails.setModuleId(voucherHeader.getModuleId());
			voucherDetails.setVouchermis(voucherHeader.getVouchermis());
			
			EgBillregister egBillregister = paymentRefundUtils.getEgBillregister(voucherHeader);
			
			if(null != egBillregister && null != egBillregister.getEgBillregistermis()) {
				if (egBillregister.getEgBillregistermis().getFund() != null) {
					voucherDetails.setFundName(egBillregister.getEgBillregistermis().getFund().getName());
				}
		        if (egBillregister.getEgBillregistermis().getDepartmentcode() != null) {
		            org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(egBillregister.getEgBillregistermis().getDepartmentcode());
		            voucherDetails.setDeptName(depList != null ? depList.getName() : "");
		        }
		        if (egBillregister.getEgBillregistermis().getScheme() != null) {
		        	voucherDetails.setScheme(egBillregister.getEgBillregistermis().getScheme().getName());
		        }
		        if (egBillregister.getEgBillregistermis().getSubScheme() != null) {
		        	voucherDetails.setSubScheme(egBillregister.getEgBillregistermis().getSubScheme().getName());
		        }
		        voucherDetails.setNarration(egBillregister.getEgBillregistermis().getNarration());
		        voucherDetails.setBanNumber(egBillregister.getEgBillregistermis().getBudgetaryAppnumber());
		        if (egBillregister.getEgBillregistermis().getFundsource() != null) {
		        	voucherDetails.setFinanceSource(egBillregister.getEgBillregistermis().getFundsource().getName());
		        }
		        if (egBillregister.getEgBillregistermis().getSubdivision() != null) {
		        	voucherDetails.setSubdivision(egBillregister.getEgBillregistermis().getSubdivision());
		        	
		        }
			}
			
			final List<CGeneralLedger> gllist = paymentRefundUtils.getAccountDetails(vhid);
			for (final CGeneralLedger gl : gllist) {
                temp = new HashMap<String, Object>();
                if (gl.getFunctionId() != null) {
                    temp.put(Constants.FUNCTION, paymentRefundUtils.getFunction(Long.valueOf(gl.getFunctionId())).getName());
                    temp.put("functionid", gl.getFunctionId());
                }
                else if (voucherHeader.getVouchermis() != null && voucherHeader.getVouchermis().getFunction() !=null && voucherHeader.getVouchermis().getFunction().getName() != null)
                {
                	temp.put(Constants.FUNCTION, voucherHeader.getVouchermis().getFunction().getName());
                    temp.put("functionid", voucherHeader.getVouchermis().getFunction().getId());
                }
                coa = paymentRefundUtils.getChartOfAccount(gl.getGlcode());
                temp.put("glcodeid", coa.getId());
                glcodeIdList.add(coa.getId());
                temp.put(Constants.GLCODE, coa.getGlcode());
                temp.put("accounthead", coa.getName());
                temp.put(Constants.DEBITAMOUNT, gl.getDebitAmount() == null ? 0 : gl.getDebitAmount());
                temp.put(Constants.CREDITAMOUNT, gl.getCreditAmount() == null ? 0 : gl.getCreditAmount());
                temp.put("billdetailid", gl.getId());
                tempList.add(temp);
                for (CGeneralLedgerDetail gldetail : gl.getGeneralLedgerDetails()) {
                    if (chartOfAccountDetailService.getByGlcodeIdAndDetailTypeId(gl.getGlcodeId().getId(), gldetail.getDetailTypeId().getId().intValue()) != null) {
                        subledger = new PreApprovedVoucher();
                        subledger.setGlcode(coa);
                        final Accountdetailtype detailtype = paymentRefundUtils.getAccountdetailtype(gldetail.getDetailTypeId().getId());
                        detailtypeIdList.add(detailtype);
                        subledger.setDetailType(detailtype);
                        payeeMap = new HashMap<>();
                        payeeMap = paymentRefundUtils.getAccountDetails(gldetail.getDetailTypeId().getId(), gldetail.getDetailKeyId(), payeeMap);
                        subledger.setDetailKey(payeeMap.get(Constants.DETAILKEY) + "");
                        subledger.setDetailCode(payeeMap.get(Constants.DETAILCODE) + "");
                        subledger.setDetailKeyId(gldetail.getDetailKeyId());
                        subledger.setAmount(gldetail.getAmount());
                        subledger.setFunctionDetail(temp.get("function") != null ? temp.get("function").toString() : "");
                        if (gl.getDebitAmount() == null || gl.getDebitAmount().compareTo(BigDecimal.ZERO) == 0) {
                            subledger.setDebitAmount(BigDecimal.ZERO);
                            subledger.setCreditAmount(gldetail.getAmount());
                            crAmount = crAmount.add(gldetail.getAmount());
                        } else {
                            subledger.setDebitAmount(gldetail.getAmount());
                            subledger.setCreditAmount(BigDecimal.ZERO);
                            dbAmount = dbAmount.add(gldetail.getAmount());
                        }
                        payeeList.add(subledger);
                    }
                }
			}
		}
		
		model.addAttribute("voucherDetails", voucherDetails);
		model.addAttribute("accountDetails", tempList);
		model.addAttribute("subLedgerlist", payeeList);
		model.addAttribute("dbAmount", dbAmount);
		model.addAttribute("crAmount", crAmount);
		
		return PR_VOUCHER_VIEW;
	}
	
	@RequestMapping(value = "/_paymentRequestForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String paymentRequestForm(@RequestParam(name = "vhid") final String vhid, final Model model,@ModelAttribute("message") String message) {
		
		if(message!=null) {
			model.addAttribute("glcodedetailIdmsg", message);
		}else {
			message="";
		}
		model.addAttribute("glCodeDetailIdList","");
		 List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
		 EgBillregister egBillregister = new EgBillregister();
		 
		VoucherDetailsResponse voucherDetails = new VoucherDetailsResponse();
		final List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
        final List<PreApprovedVoucher> payeeList = new ArrayList<PreApprovedVoucher>();
        Map<String, Object> temp = null;
        Map<String, Object> payeeMap = null;
        PreApprovedVoucher subledger = null;
        CChartOfAccounts coa = null;
        final List<Long> glcodeIdList = new ArrayList<Long>();
		voucherDetails.setId(Long.valueOf(vhid));
		CVoucherHeader voucherHeader = paymentRefundUtils.getVoucherHeader(Long.valueOf(vhid));
		final List<Accountdetailtype> detailtypeIdList = new ArrayList<Accountdetailtype>();
		BigDecimal dbAmount = BigDecimal.ZERO;
		BigDecimal crAmount = BigDecimal.ZERO;
		SQLQuery queryMain =  null;
		final StringBuffer query1 = new StringBuffer(500);
    	
    	List<Object[]> list= null;
    	query1
        .append("select id,billid from eg_billregistermis eb where paymentvoucherheaderid ="+voucherHeader.getId());
    	queryMain=this.persistenceService.getSession().createSQLQuery(query1.toString());
    	list = queryMain.list();
    	EgBillregister bill = null;
    	List<EgBilldetails> egBilldetailsList = new ArrayList<EgBilldetails>();
    	if (list.size() != 0) {
    		LOGGER.info("size ::: "+list.size());
    		for (final Object[] object : list)
    		{
    			bill=new EgBillregister();
    			bill= expenseBillService.getById(Long.parseLong(object[1].toString()));
    			if(bill != null)
    			{
    				egBilldetailsList.addAll(bill.getEgBilldetailes());
    			}
    		}
    	}
    	
		
		//egBilldetailsList.add(egBill);
		//egBilldetailsList.add(egBill2);
		
		
		HashMap<BigDecimal,BigDecimal> hs = new HashMap<BigDecimal,BigDecimal>();
		for(EgBilldetails eg : egBilldetailsList) {
			
			if(hs.containsKey(eg.getGlcodeid())) {
				hs.put(eg.getGlcodeid(), (eg.getDebitamount()).add(hs.get(eg.getGlcodeid())));
			}else {
				hs.put(eg.getGlcodeid(), eg.getDebitamount());
			}
			
		}
		
		if(null!=voucherHeader) {
			voucherDetails.setName(voucherHeader.getName());
			voucherDetails.setType(voucherHeader.getType());
			voucherDetails.setDescription(voucherHeader.getDescription());
			voucherDetails.setEffectiveDate(voucherHeader.getEffectiveDate());
			voucherDetails.setVoucherNumber(voucherHeader.getVoucherNumber());
			voucherDetails.setVoucherDate(voucherHeader.getVoucherDate());
			voucherDetails.setFund(voucherHeader.getFundId());
			voucherDetails.setFiscalPeriodId(voucherHeader.getFiscalPeriodId());
			voucherDetails.setStatus(voucherHeader.getStatus());			
			voucherDetails.setOriginalvcId(voucherHeader.getOriginalvcId());
			voucherDetails.setIsConfirmed(voucherHeader.getIsConfirmed());
			voucherDetails.setRefvhId(voucherHeader.getRefvhId());
			voucherDetails.setCgvn(voucherHeader.getCgvn());
			voucherDetails.setModuleId(voucherHeader.getModuleId());
			voucherDetails.setVouchermis(voucherHeader.getVouchermis());
			
			
			//EgBillregister egBillregister2 =paymentRefundUtils.getEgBillregisterByVoucherHeaderId(voucherHeader);			
			//List<EgBillregister> egBillregisterList = expenseBillService.findByVoucherHeaderId(Long.valueOf(vhid));
			
			
			
			if(paymentRefundUtils.getEgBillregister(voucherHeader)!=null) {
			 egBillregister = paymentRefundUtils.getEgBillregister(voucherHeader);
			}else {
				
				if(voucherDetails.getVouchermis().getDepartmentcode()!=null) {
				 org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(voucherDetails.getVouchermis().getDepartmentcode());
				 voucherDetails.setDeptName(depList != null ? depList.getName() : "");
				model.addAttribute("departcode",voucherDetails.getVouchermis().getDepartmentcode());
				}
			}
			
			
			if(null != egBillregister && null != egBillregister.getEgBillregistermis()) {
				
			}else {
				voucherDetails.setFundName(voucherDetails.getFund().getName());
				model.addAttribute("fundid", voucherDetails.getFund().getId());
			}
			
			if(null != egBillregister && null != egBillregister.getEgBillregistermis()) {
				if (egBillregister.getEgBillregistermis().getFund() != null) {
					voucherDetails.setFundName(egBillregister.getEgBillregistermis().getFund().getName());
					model.addAttribute("fundid", egBillregister.getEgBillregistermis().getFund().getId());
				}
		        if (egBillregister.getEgBillregistermis().getDepartmentcode() != null) {
		            org.egov.infra.microservice.models.Department depList = microserviceUtils.getDepartmentByCode(egBillregister.getEgBillregistermis().getDepartmentcode());
		            voucherDetails.setDeptName(depList != null ? depList.getName() : "");
		            model.addAttribute("departcode", egBillregister.getEgBillregistermis().getDepartmentcode());
		        }
		        if (egBillregister.getEgBillregistermis().getScheme() != null) {
		        	voucherDetails.setScheme(egBillregister.getEgBillregistermis().getScheme().getName());
		        }
		        if (egBillregister.getEgBillregistermis().getSubScheme() != null) {
		        	voucherDetails.setSubScheme(egBillregister.getEgBillregistermis().getSubScheme().getName());
		        }
		        voucherDetails.setNarration(egBillregister.getEgBillregistermis().getNarration());
		        voucherDetails.setBanNumber(egBillregister.getEgBillregistermis().getBudgetaryAppnumber());
		        if (egBillregister.getEgBillregistermis().getFundsource() != null) {
		        	voucherDetails.setFinanceSource(egBillregister.getEgBillregistermis().getFundsource().getName());
		        	model.addAttribute("fundsource", egBillregister.getEgBillregistermis().getFundsource());
		        }
		        if (egBillregister.getEgBillregistermis().getSubdivision() != null) {
		        	voucherDetails.setSubdivision(egBillregister.getEgBillregistermis().getSubdivision());
		        	
		        }
		        
		        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
				prepareWorkflow(model, egBillregister, new WorkflowContainer());
			}
			
			final List<CGeneralLedger> gllist = paymentRefundUtils.getAccountDetails(Long.valueOf(vhid));
			for (final CGeneralLedger gl : gllist) {
                temp = new HashMap<String, Object>();
                if (gl.getFunctionId() != null) {
                    temp.put(Constants.FUNCTION, paymentRefundUtils.getFunction(Long.valueOf(gl.getFunctionId())).getName());
                    temp.put("functionid", gl.getFunctionId());
                    //temp.put("functionObj",paymentRefundUtils.getFunction(Long.valueOf(gl.getFunctionId())));
                }
                else if (voucherHeader.getVouchermis() != null && voucherHeader.getVouchermis().getFunction() !=null && voucherHeader.getVouchermis().getFunction().getName() != null)
                {
                	temp.put(Constants.FUNCTION, voucherHeader.getVouchermis().getFunction().getName());
                    temp.put("functionid", voucherHeader.getVouchermis().getFunction().getId());
                    //temp.put("functionObj",voucherHeader.getVouchermis().getFunction());
                }
                coa = paymentRefundUtils.getChartOfAccount(gl.getGlcode());
                temp.put("glcodeid", coa.getId());
                glcodeIdList.add(coa.getId());
                temp.put(Constants.GLCODE, coa.getGlcode());
                temp.put("accounthead", coa.getName());
                temp.put(Constants.DEBITAMOUNT, gl.getDebitAmount() == null ? 0 : gl.getDebitAmount());
                temp.put(Constants.CREDITAMOUNT, gl.getCreditAmount() == null ? 0 : gl.getCreditAmount());
                temp.put("billdetailid", gl.getId());
                
                for(Entry<BigDecimal,BigDecimal>bb : hs.entrySet()) {
					System.out.println(BigDecimal.valueOf(coa.getId())+""+bb.getKey());
					if(BigDecimal.valueOf(coa.getId()).equals(bb.getKey())) {
						temp.put("previousAmount", bb.getValue());
						break;
					}else {
						temp.put("previousAmount", 0);
					}
				}
                tempList.add(temp);
                for (CGeneralLedgerDetail gldetail : gl.getGeneralLedgerDetails()) {
                    if (chartOfAccountDetailService.getByGlcodeIdAndDetailTypeId(gl.getGlcodeId().getId(), gldetail.getDetailTypeId().getId().intValue()) != null) {
                        subledger = new PreApprovedVoucher();
                        subledger.setGlcode(coa);
                        //subledger.setGlcodeIdDetail(coa.getId());
                        final Accountdetailtype detailtype = paymentRefundUtils.getAccountdetailtype(gldetail.getDetailTypeId().getId());
                        detailtypeIdList.add(detailtype);
                        subledger.setDetailType(detailtype);
                        payeeMap = new HashMap<>();
                        payeeMap = paymentRefundUtils.getAccountDetails(gldetail.getDetailTypeId().getId(), gldetail.getDetailKeyId(), payeeMap);
                        subledger.setDetailKey(payeeMap.get(Constants.DETAILKEY) + "");
                        subledger.setDetailCode(payeeMap.get(Constants.DETAILCODE) + "");
                        subledger.setDetailKeyId(gldetail.getDetailKeyId());
                        subledger.setAmount(gldetail.getAmount());
                        subledger.setFunctionDetail(temp.get("function") != null ? temp.get("function").toString() : "");
                        if (gl.getDebitAmount() == null || gl.getDebitAmount().compareTo(BigDecimal.ZERO) == 0) {
                            subledger.setDebitAmount(BigDecimal.ZERO);
                            subledger.setCreditAmount(gldetail.getAmount());
                            crAmount = crAmount.add(gldetail.getAmount());
                        } else {
                            subledger.setDebitAmount(gldetail.getAmount());
                            subledger.setCreditAmount(BigDecimal.ZERO);
                            dbAmount = dbAmount.add(gldetail.getAmount());
                        }
                        payeeList.add(subledger);
                    }
                }
			}
			
			
/*			for(int i=0; i<tempList.size();i++) {
				
				for(HashMap<String, Object> entry :  tempList.get(i))
		        {
					for(Entry<BigDecimal,BigDecimal>bb : hs.entrySet()) {
						
						if(entry.getValue() == bb.getKey() ) {
							((HashMap) entry).put("previousAmount",bb.getValue());
							continue;
						}else {
							((HashMap) entry).put("previousAmount",0);
						}
					}
		            //String key = entry.getKey();
		            //Object value = entry.getValue();
		            // ...
		        } 
				
				
			}*/
			
			//model.addAttribute("EgBilldetailsList", hs);
			
			populateDropDownValues(model);
		
			//System.out.println("hello sonu state type is:   "+egBillregister.getClass().getSimpleName());
			
			  model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
			  prepareWorkflow(model, egBillregister, new WorkflowContainer());
			  model.addAttribute("validActionList", validActions);
			  model.addAttribute(BILL_TYPES, BillType.values());
			  prepareValidActionListByCutOffDate(model);
		}
		final List<Bank> banks = createBankService.getAll();
		EgBillSubType egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
		System.out.println(egbillSubtype.getId());
		model.addAttribute("voucherDetails", voucherDetails);
		model.addAttribute("accountDetails", tempList);
		model.addAttribute("subLedgerlist", payeeList);
		model.addAttribute("dbAmount", dbAmount);
		model.addAttribute("crAmount", crAmount);
		model.addAttribute("vhid", vhid);
		model.addAttribute("banks", banks);
		model.addAttribute("billsubtype", egbillSubtype.getId());
		//return PR_REQUEST_FORM;
		return "payRefund-request-form";
	}
	
    private void populateDropDownValues(final Model model) {
    	
    	List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"RefundBillType");
    	List<String> billNames=new ArrayList<String>();
    	for(AppConfigValues row:appConfigValuesList)
    	{
    		billNames.add(row.getValue());
    	}
    	List<EgBillSubType> billSubtypes=new ArrayList<EgBillSubType>();
    	for(EgBillSubType row:egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT))
    	{
    		if(billNames.contains(row.getName()))
    		{
    			billSubtypes.add(row);
    		}
    	}
		appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
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
        //addDropdownData("subdivisionList", subdivisionList);
    	
        model.addAttribute("billNumberGenerationAuto", refundBillService.isBillNumberGenerationAuto());
        model.addAttribute("billSubTypes", billSubtypes);
        model.addAttribute("subLedgerTypes", accountdetailtypeService.findAll());
        model.addAttribute("cFunctions", functionDAO.getAllActiveFunctions());
		model.addAttribute("subdivisionList", subdivisionList);
    }
    
    public List<EgBillSubType> getBillSubTypesRef() {
        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);
    }
    
    
    public List<EgBillSubType> getBillSubTypes() {
        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT);
    }
    
    public BigDecimal checknull(BigDecimal value) {
    	if(value==null) {
    		return BigDecimal.ZERO;
    	}
    	else {
		return value;
    	}
    }
    public boolean checknullBigDecimal(BigDecimal value) {
    	if(value==null) {
    		return false;
    	}
    	else {
		return true;
    	}
    }
    
    @RequestMapping(value = "/refundCreate", method = RequestMethod.POST)
    public String createRefund(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,
         final BindingResult resultBinder, final HttpServletRequest request, @RequestParam final String workFlowAction,RedirectAttributes redirectAttributes)
         throws IOException {
            LOGGER.info("RefundBill is creating with user ::"+ApplicationThreadLocals.getUserId());
         //User createdBy = new User();
        // createdBy.setId(ApplicationThreadLocals.getUserId());
            String vhid=request.getParameter("vhid");
            System.out.println(vhid);
            
           
            
            
           egBillregister.setRefundable("Y"); 
           egBillregister.setBilldate(new Date());
           BigDecimal totalCrAmt = BigDecimal.ZERO;
           
           //egBillregister.getState().setNatureOfTask("Refund Bill");
		
		 for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
			 
			 totalCrAmt=totalCrAmt.add(checknull(egbilldetail.getDebitamount()));
			 }
		 //BigDecimal bg1 = egBillregister.getBillDetails().get(0).getFunctionid();
		 
		 Long bg1 = egBillregister.getEgBillregistermis().getFunction().getId();
		 
		 List<EgBilldetails> egbilldetailCusList=new ArrayList<EgBilldetails>();
           for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
			  
        	   if(checknullBigDecimal(egbilldetail.getDebitamount())==true || checknullBigDecimal(egbilldetail.getCreditamount())==true) {
        		   egbilldetailCusList.add(egbilldetail);
        	   }
			 }
		  
		  egBillregister.setBillamount(totalCrAmt);
		  egBillregister.setBillDetails(egbilldetailCusList);
		
		 System.out.println(totalCrAmt);

		 EgBillSubType egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
           
           
         egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());
         ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);
         
		
		 
		  CFunction function= paymentRefundUtils.getFunction(bg1.longValue());
		  egBillregister.getEgBillregistermis().setFunction(function);
		  egBillregister.getEgBillregistermis().setEgBillSubType(egbillSubtype);
		  //egbillregistermis.setFunction(function);
		 
        
         
         //egBillregister.setEgBillregistermis(egbillregistermis);
        
        String billNumber = v.getNextNumber(egBillregister);
        System.out.println(billNumber);
        
        
        	egBillregister.setBillnumber(billNumber);
        
         //String billNumber="refund-test-2021";
        
        if (StringUtils.isEmpty(egBillregister.getExpendituretype()))
        egBillregister.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);

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
        upload.setContentType(contentType[i]);
        list.add(upload);
        }
        
        egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
        populateBillDetails(egBillregister);
        
        validateBillNumber(egBillregister, resultBinder);
        
		
		  if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
			  refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
		  }
		 
		  validateSubledgeDetails(egBillregister);  
		  System.out.println("------------------------------"+egBillregister.getBillPayeeDetailsNotLink().isEmpty());	  
	if(egBillregister.getBillPayeeDetailsNotLink().isEmpty()) {

        if (resultBinder.hasErrors()) {
        	System.out.println("from ResultBinder Error");
        	for (Object object : resultBinder.getAllErrors()) {
        	    if(object instanceof FieldError) {
        	        FieldError fieldError = (FieldError) object;

        	        System.out.println(fieldError.getCode());
        	    }

        	    if(object instanceof ObjectError) {
        	        ObjectError objectError = (ObjectError) object;

        	        System.out.println(objectError.getCode());
        	    }
        	}
          return "redirect:/refund/_paymentRequestForm?vhid=" + vhid;
         //return "redirect:/refund/_paymentRequestForm";
          } else {
                Long approvalPosition = 0l;
                String approvalComment = "";
                String approvalDesignation = "";
                if (request.getParameter("approvalComent") != null)
                 approvalComment = request.getParameter("approvalComent");
                if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
                  {
                   	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                      	{            		
                         approvalPosition =populatePosition();            		
                        }
                      else
                         approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
                    }
                 else {
                    if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                     {            		
                      approvalPosition =populatePosition();            		
                     }		            	
                  }
                 if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
                     approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
                        		            
                 EgBillregister savedEgBillregister;
                 egBillregister.setDocumentDetail(list);
                      try {
                    	  System.out.println("From egbillregister Save method calling");
                           savedEgBillregister = refundBillService.create(egBillregister, approvalPosition, approvalComment, null, 
                           workFlowAction,approvalDesignation,vhid);
                           
                           
                         } catch (ValidationException e) {
                        	 System.out.println("From Exception saving time");
                        	 e.printStackTrace();
                        
                        return "redirect:/refund/_paymentRequestForm?vhid=" + vhid;
                        //return "redirect:/refund/_paymentRequestForm";
                        }
                       String approverName =null;
                       if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                        {        		
                        approverName =populateEmpName();        		
                        }
                       else
                        approverName = String.valueOf(request.getParameter("approverName"));
                        final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                                     savedEgBillregister.getState(), savedEgBillregister.getId(), approvalPosition,approverName);
                     		              
                        return "redirect:/refund/successRefund?approverDetails=" + approverDetails + "&billNumber="
                        		                    + savedEgBillregister.getBillnumber()+"&billId="
                        		                            + savedEgBillregister.getId();
                       }
                 
	}else {
		StringBuilder message = new StringBuilder();
		for(int i=0;i<egBillregister.getBillPayeeDetailsNotLink().size();i++) {
			
			message.append("account detial key "+egBillregister.getBillPayeeDetailsNotLink().get(i).getAccountDetailTypeId()+" not mapped with  glcodeid "+egBillregister.getBillPayeeDetailsNotLink().get(i).getEgBilldetailsId().getGlcodeid()+"\n");
			
		}
		redirectAttributes.addFlashAttribute("message",message);
		return "redirect:/refund/_paymentRequestForm?vhid=" + vhid;
	}
                 
            }
    
    
    @RequestMapping(value = "/successRefund", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("billNumber") final String billNumber, final Model model,
                                  final HttpServletRequest request, @RequestParam("billId") final String billId) {
        final String[] keyNameArray = request.getParameter("approverDetails").split(",");
        Long id = 0L;
        String approverName = "";
        String currentUserDesgn = "";
        String nextDesign = "";
        if (keyNameArray.length != 0 && keyNameArray.length > 0)
            if (keyNameArray.length == 1)
                id = Long.parseLong(keyNameArray[0].trim());
            else if (keyNameArray.length == 3) {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
//                currentUserDesgn = keyNameArray[2];
            } else {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
//                currentUserDesgn = keyNameArray[2];
//                nextDesign = keyNameArray[3];
            }
//        approverName= keyNameArray[0];
        if (id != null)
            model.addAttribute("approverName", approverName);
//        model.addAttribute("currentUserDesgn", currentUserDesgn);
//        model.addAttribute("nextDesign", nextDesign);
        model.addAttribute("billd", billId);
        model.addAttribute("type", "refund");
        final EgBillregister expenseBill = refundBillService.getByBillnumber(billNumber);

        final String message = getMessageByStatus(expenseBill, approverName, nextDesign);

        model.addAttribute("message", message);
        System.out.println(message);
        return "expensebill-success";
    }
    
    
    private String getMessageByStatus(final EgBillregister expenseBill, final String approverName, final String nextDesign) {
        String message = "";
          System.out.println(expenseBill.getStatus().getCode());
          
        if (FinancialConstants.CONTINGENCYBILL_CREATED_STATUS.equals(expenseBill.getStatus().getCode())
                                     || FinancialConstants.CONTINGENCYBILL_PENDING_FINANCE.equals(expenseBill.getStatus().getCode())) {
            if (org.apache.commons.lang.StringUtils
                    .isNotBlank(expenseBill.getEgBillregistermis().getBudgetaryAppnumber())
                    && !BudgetControlType.BudgetCheckOption.NONE.toString()
                    .equalsIgnoreCase(budgetControlTypeService.getConfigValue())) {
                                message = messageSource.getMessage("msg.expense.refund.bill.create.success.with.budgetappropriation",
                                                    new String[]{expenseBill.getBillnumber(), approverName, nextDesign,
                                expenseBill.getEgBillregistermis().getBudgetaryAppnumber()},
                        null);
               }
            else if(expenseBill.getState().getValue()!=null && expenseBill.getState().getValue().equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
                System.out.println(expenseBill.getState().getValue());
            	message = messageSource.getMessage("msg.expense.refund.bill.saveasdraft.success",
                            new String[]{expenseBill.getBillnumber()}, null);
            }
            else {
                message = messageSource.getMessage("msg.expense.refund.bill.create.success",
                        new String[]{expenseBill.getBillnumber(), approverName, nextDesign}, null);
            }

        } else if (FinancialConstants.CONTINGENCYBILL_PENDING_AUDIT.equals(expenseBill.getStatus().getCode())) {
            message = messageSource.getMessage("msg.expense.refund.bill.approved.success",
                    new String[]{expenseBill.getBillnumber()}, null);
        }
            
        else if (FinancialConstants.WORKFLOW_STATE_REJECTED.equals(expenseBill.getState().getValue())) {
            message = messageSource.getMessage("msg.expense.refund.bill.reject",
                    new String[]{expenseBill.getBillnumber(), approverName, nextDesign}, null);
        }
        else if (FinancialConstants.WORKFLOW_STATE_CANCELLED.equals(expenseBill.getStatus().getCode())) {
        	expenseBill.setState(null);
            refundBillService .saveEgBillregister_afterStateNull(expenseBill);
            message = messageSource.getMessage("msg.expense.refund.bill.cancel",
                    new String[]{expenseBill.getBillnumber()}, null);
        }
        else if ("Pending for Cancellation".equals(expenseBill.getStatus().getCode())) {
        	message = messageSource.getMessage("msg.expense.refund.bill.cancel.success",
                    new String[]{expenseBill.getBillnumber(), approverName, nextDesign}, null);
        }
        return message;
    }
    
    
	
	@RequestMapping(value = "/refundBill", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public RefundResponse refundBill(@RequestBody RefundRequest refundRequest, final Model model,
			final BindingResult resultBinder, final HttpServletRequest request) throws IOException {

		ApplicationThreadLocals.setUserId(refundRequest.getRequestInfo().getUserInfo().getId());
		ApplicationThreadLocals.setUserToken(refundRequest.getRequestInfo().getAuthToken());
		ApplicationThreadLocals.setTenantID(refundRequest.getTenantId());
		LOGGER.info("RefundBill is creating with user ::" + ApplicationThreadLocals.getUserId());

		List<EgBilldetails> billDetails = new ArrayList<EgBilldetails>();

		VoucherDetailsResponse voucherDetails = new VoucherDetailsResponse();

		final List<String> entityNames = new ArrayList<>();
		List<EntityType> entitiesList = new ArrayList<>();

		final Vouchermis vouchermis = vouchermisService
				.getVouchermisByReceiptNumber(refundRequest.getReceipt().getReceiptNumber());

		final List<PreApprovedVoucher> payeeList = new ArrayList<PreApprovedVoucher>();

		PreApprovedVoucher subledger = null;
		CChartOfAccounts coa = null;
		voucherDetails.setId(Long.valueOf(vouchermis.getVoucherheaderid().getId()));
		CVoucherHeader voucherHeader = paymentRefundUtils
				.getVoucherHeader(Long.valueOf(vouchermis.getVoucherheaderid().getId()));
		final List<Accountdetailtype> detailtypeIdList = new ArrayList<Accountdetailtype>();
		BigDecimal dbAmount = BigDecimal.ZERO;
		BigDecimal crAmount = BigDecimal.ZERO;

		EgBillregister egBillregister = new EgBillregister();

		if (null != egBillregister && null != egBillregister.getEgBillregistermis()) {
			prepareWorkflow(model, egBillregister, new WorkflowContainer());
		}

		egBillregister.setRefundable("Y");
		egBillregister.setIsCitizenRefund(refundRequest.getReceipt().getIsCitizenRefund());
		egBillregister.setBilldate(new Date());
		BigDecimal totalCrAmt = BigDecimal.ZERO;
		for (RefundLedger ledgers : refundRequest.getReceipt().getLedgers()) {
			totalCrAmt = totalCrAmt.add(checknull(ledgers.getDebitAmount()));
		}
		egBillregister.setBillamount(totalCrAmt);

		final List<CGeneralLedger> gllist = paymentRefundUtils
				.getAccountDetails(Long.valueOf(vouchermis.getVoucherheaderid().getId()));
		for (final CGeneralLedger gl : gllist) {
			for (RefundLedger ledgers : refundRequest.getReceipt().getLedgers()) {
				if (gl.getGlcode().equalsIgnoreCase(ledgers.getGlcode())) {
					EgBilldetails billdetail = new EgBilldetails();
					CChartOfAccounts chartOfAccounts = new CChartOfAccounts();
					coa = paymentRefundUtils.getChartOfAccount(ledgers.getGlcode());
					billdetail.setGlcodeid(BigDecimal.valueOf(coa.getId()));
					billdetail.setFunctionid(BigDecimal.valueOf(gl.getFunctionId()));
					chartOfAccounts.setName(coa.getName());
					chartOfAccounts.setGlcode(coa.getGlcode());
					billdetail.setChartOfAccounts(chartOfAccounts);
					billdetail.setDebitamount(ledgers.getDebitAmount());
					billdetail.setCreditamount(ledgers.getCreditAmount());
					billDetails.add(billdetail);
				}
			}

		}
		egBillregister.setBillDetails(billDetails);
		egBillregister.setBillamount(totalCrAmt);

		EgBillSubType egbillSubtype = (EgBillSubType) getBillSubTypes().stream()
				.filter(e -> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
		BigDecimal bg1 = egBillregister.getBillDetails().get(0).getFunctionid();
		CFunction function = paymentRefundUtils.getFunction(bg1.longValue());
		EgBillregistermis egbillregistermis = egBillregister.getEgBillregistermis();
		egbillregistermis.setFunction(function);
		egbillregistermis.setDepartmentcode(egBillregister.getApprovalDepartment());
		egbillregistermis.setNarration(egBillregister.getApprovalComent());
		egbillregistermis.setEgBillSubType(egbillSubtype);
		egBillregister.setEgBillregistermis(egbillregistermis);

		ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);

		String billNumber = v.getNextNumber(egBillregister);
		System.out.println(billNumber);
		egBillregister.setBillnumber(billNumber);

		egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());

		List<Accountdetailtype> accountdetailtypelist = accountdetailtypeService.findAll();
		Accountdetailtype accountdetailtype = (Accountdetailtype) accountdetailtypeService.findAll().stream()
				.filter(e -> e.getName().equalsIgnoreCase("OtherParty")).findFirst().orElse(null);
		try {
			final String table = accountdetailtype.getFullQualifiedName();
			final Class<?> service = Class.forName(table);
			String simpleName = service.getSimpleName();
			simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

			final EntityTypeService entityService = (EntityTypeService) applicationContext.getBean(simpleName);
			entitiesList = (List<EntityType>) entityService.filterActiveEntities(accountdetailtype.getName(), 20,
					accountdetailtype.getId());
		} catch (final Exception e) {
			e.printStackTrace();
			entitiesList = new ArrayList<>();
		}
		for (final EntityType entity : entitiesList) {
			entityNames.add(entity.getCode() + " - " + entity.getName() + "~" + entity.getEntityId());
		}
		// System.out.println(name); //System.out.println(accountDetailType);
		for (String responcename : entityNames) {
			System.out.println(responcename);
		}

		return new RefundResponse();
	}
	 
    
    
    
    

    private Long populatePosition() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	Long pos=null;
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    {
    		pos=employs.get(0).getAssignments().get(0).getPosition();
    		
    	}
    	
		return pos;
	}
    private String populateEmpName() {
    	Long empId = ApplicationThreadLocals.getUserId();
    	String empName=null;
    	Long pos=null;
    	List<EmployeeInfo> employs = microserviceUtils.getEmployee(empId, null,null, null);
    	if(null !=employs && employs.size()>0 )
    	{
    		//pos=employs.get(0).getAssignments().get(0).getPosition();
    		empName=employs.get(0).getUser().getName();
    		
    	}
		return empName;
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
                    resultBinder.reject("msg.expense.bill.accdetail.amountzero",
                            new String[] { details.getChartOfAccounts().getGlcode() }, null);
                }
          
            

            if (details.getDebitamount() != null && details.getCreditamount() != null
                    && details.getDebitamount().compareTo(BigDecimal.ZERO) == 1
                    && details.getCreditamount().compareTo(BigDecimal.ZERO) == 1)
                resultBinder.reject("msg.expense.bill.accdetail.amount",
                        new String[] { details.getChartOfAccounts().getGlcode() }, null);
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
                        resultBinder.reject("msg.expense.bill.subledger.amountzero",
                                new String[] { details.getChartOfAccounts().getGlcode() }, null);

                    if (payeeDetails.getDebitAmount() != null && payeeDetails.getCreditAmount() != null
                            && payeeDetails.getDebitAmount().compareTo(BigDecimal.ZERO) == 1
                            && payeeDetails.getCreditAmount().compareTo(BigDecimal.ZERO) == 1)
                        resultBinder.reject("msg.expense.bill.subledger.amount",
                                new String[] { details.getChartOfAccounts().getGlcode() }, null);

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
    
    
    
	@RequestMapping(value="/saveotherParty", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
	@ResponseBody
    public OtherParty submittedFromData(@RequestBody OtherParty otherParty, HttpServletRequest request) {
		
		System.out.println(otherParty.getBankname());
		System.out.println(otherParty.getName());
		System.out.println(otherParty.getBankAccount());
		//System.out.println(request.getParameter("bank"));
		OtherParty other_party=otherPartyService.getByNameOrAccount(otherParty.getName(),otherParty.getBankAccount());
		if(other_party==null) {
			Bank bank=createBankService.getById(otherParty.getBank().getId());
			otherParty.setBank(bank);
			otherParty.setCode(otherParty.getName());
			other_party=otherPartyService.create(otherParty);
		}
		
		return otherParty;
	}	
    
    
	
	
	
	
	
	 public void setupDropDownForSL(final List<Long> glcodeIdList,final Model model) {
	        List<CChartOfAccounts> glcodeList = null;
	        if (!glcodeIdList.isEmpty())
	        {
	        final Query glcodeListQuery = persistenceService.getSession().createQuery(
	                " from CChartOfAccounts where id in (select glCodeId from CChartOfAccountDetail) and id in  ( :IDS )");
	        glcodeListQuery.setParameterList("IDS", glcodeIdList);
	        glcodeList = glcodeListQuery.list();
	        }
	        if (glcodeIdList.isEmpty())
	        	model.addAttribute("glcodeList", Collections.EMPTY_LIST);
	            //dropdownData.put("glcodeList", Collections.EMPTY_LIST);
	        else
	        	model.addAttribute("glcodeList", glcodeList);
	            //dropdownData.put("glcodeList", glcodeList);
		}
    	
	    public void setupDropDownForSLDetailtype(final List<Accountdetailtype> detailtypeIdList,final Model model) {
	    	model.addAttribute("detailTypeList", detailtypeIdList);
	    	//dropdownData.put("detailTypeList", detailtypeIdList);
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
	        model.addAttribute("type", "refundbill");
	        model.addAttribute(BILL_TYPES, BillType.values());
	        prepareBillDetailsForView(egBillregister);
	        prepareCheckList(egBillregister);
	        
	        final List<CChartOfAccounts> expensePayableAccountList = chartOfAccountsService.getNetPayableCodesByAccountDetailType(0);
	        
	        for (final EgBilldetails details : egBillregister.getBillDetails())
	            if (expensePayableAccountList != null && !expensePayableAccountList.isEmpty()
	                    && expensePayableAccountList.contains(details.getChartOfAccounts()))
	                model.addAttribute(NET_PAYABLE_AMOUNT, details.getCreditamount());
	        model.addAttribute(EG_BILLREGISTER, egBillregister);
	        return EXPENSEBILL_VIEW;
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
		  
		    public void validateSubledgeDetails(EgBillregister egBillregister) {
		        final List<EgBillPayeedetails> payeeDetails = new ArrayList<>();
		        final List<EgBillPayeedetails> payeeDetailsNotMatched = new ArrayList<>();
		        for (final EgBillPayeedetails payeeDetail : egBillregister.getBillPayeedetails()) {
		            CChartOfAccountDetail coaDetail = chartOfAccountDetailService
		                    .getByGlcodeIdAndDetailTypeId(payeeDetail.getEgBilldetailsId().getGlcodeid().longValue(),
		                            payeeDetail.getAccountDetailTypeId().intValue());
		            if (coaDetail != null) {
		                payeeDetails.add(payeeDetail);
		        }else {
		        	payeeDetailsNotMatched.add(payeeDetail);
		        }
		        }
		        egBillregister.getBillPayeedetails().clear();
		        egBillregister.setBillPayeedetails(payeeDetails);
		        
		        egBillregister.getBillPayeeDetailsNotLink().clear();
		        egBillregister.setBillPayeeDetailsNotLink(payeeDetailsNotMatched);
		    }
		    
		    
		    @RequestMapping(value = "/newform", method = RequestMethod.POST)
		    public String showNewForm(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,HttpServletRequest request) {
		        LOGGER.info("New expensebill creation request created");
		        Cookie[] cookies = request.getCookies();
		       List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
		    	
		    	if(null!=cookies && cookies.length>0)
		    	{
		    	   for(Cookie ck:cookies) {
		    		   System.out.println("Name:"+ck.getName()+" value"+ck.getValue());
		    	   }
		    	}
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
				
		        
		        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
		        prepareWorkflow(model, egBillregister, new WorkflowContainer());
		       model.addAttribute("validActionList", validActions);
		       model.addAttribute(BILL_TYPES, BillType.values());
		        prepareValidActionListByCutOffDate(model);
		        if(isBillDateDefaultValue){
		            egBillregister.setBilldate(new Date());            
		        }
//		        User createdBy = new User();
//		        createdBy.setId(ApplicationThreadLocals.getUserId());
//		        egBillregister.setCreatedBy(createdBy);
		        return REFUNDFOROLDVOUCHER_FORM;
		    }
		    
		    @RequestMapping(value = "/_paymentRequestblankvoucherForm", method = {RequestMethod.GET, RequestMethod.POST})
			public String paymentRequestFormBlankVoucher(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,@ModelAttribute("message") String message) {
				setDropDownValues(model);
		    	List<String>  validActions = Arrays.asList("Forward","SaveAsDraft");
		        List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF","receipt_sub_divison");
		        List<SubDivision> subdivisionList=new ArrayList<SubDivision>();
		        SubDivision subdivision=null;
		        for(AppConfigValues value:appConfigValuesList)
		        {
		        	subdivision = new SubDivision();
		        	subdivision.setSubdivisionCode(value.getValue());
		        	subdivision.setSubdivisionName(value.getValue());
		        	subdivisionList.add(subdivision);
		        }		       
		        prepareWorkflow(model, egBillregister, new WorkflowContainer());       
		        prepareValidActionListByCutOffDate(model);
		        if(isBillDateDefaultValue){
		            egBillregister.setBilldate(new Date());            
		        }
		        model.addAttribute("validActionList", validActions);
			    model.addAttribute(BILL_TYPES, BillType.values());
		        model.addAttribute("subdivision", subdivisionList);	        
		        model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
		    	
		        if(message!=null) {
					model.addAttribute("glcodedetailIdmsg", message);
				}else {
					message="";
				}
		    	
		    	
		    	
		    	
				
					populateDropDownValues(model);
				
					//System.out.println("hello sonu state type is:   "+egBillregister.getClass().getSimpleName());
					
					  model.addAttribute(STATE_TYPE, egBillregister.getClass().getSimpleName());
					  prepareWorkflow(model, egBillregister, new WorkflowContainer());
					  model.addAttribute("validActionList", validActions);
					  model.addAttribute(BILL_TYPES, BillType.values());
					  model.addAttribute("subLedgerTypes", accountdetailtypeService.findAll());
					  prepareValidActionListByCutOffDate(model);
				
				//final List<Bank> banks = createBankService.getAll();
				//EgBillSubType egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
				/*System.out.println(egbillSubtype.getId());
				model.addAttribute("voucherDetails", voucherDetails);
				model.addAttribute("accountDetails", tempList);
				model.addAttribute("subLedgerlist", payeeList);
				model.addAttribute("dbAmount", dbAmount);
				model.addAttribute("crAmount", crAmount);
				model.addAttribute("vhid", vhid);
				model.addAttribute("banks", banks);
				model.addAttribute("billsubtype", egbillSubtype.getId());*/
				//return PR_REQUEST_FORM;
				return "ol-payRefund-request-form";
}
			


@RequestMapping(value = "/refundCreateBlank", method = RequestMethod.POST)
public String createRefundBYBlank(@ModelAttribute("egBillregister") final EgBillregister egBillregister, final Model model,
     final BindingResult resultBinder, final HttpServletRequest request, @RequestParam final String workFlowAction,RedirectAttributes redirectAttributes)
     throws IOException {
        LOGGER.info("RefundBill is creating with user ::"+ApplicationThreadLocals.getUserId());
     //User createdBy = new User();
    // createdBy.setId(ApplicationThreadLocals.getUserId());
        String vhid=request.getParameter("vhid");
        System.out.println(vhid);
        
       
        
        
       egBillregister.setRefundable("Y"); 
       egBillregister.setBilldate(new Date());
       BigDecimal totalCrAmt = BigDecimal.ZERO;
       
       //egBillregister.getState().setNatureOfTask("Refund Bill");
	
	 for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
		 
		 totalCrAmt=totalCrAmt.add(checknull(egbilldetail.getDebitamount()));
		 }
	 //BigDecimal bg1 = egBillregister.getBillDetails().get(0).getFunctionid();
	 
	 Long bg1 = egBillregister.getEgBillregistermis().getFunction().getId();
	 
	 List<EgBilldetails> egbilldetailCusList=new ArrayList<EgBilldetails>();
       for(EgBilldetails egbilldetail:egBillregister.getBillDetails()) { 
		  
    	   if(checknullBigDecimal(egbilldetail.getDebitamount())==true || checknullBigDecimal(egbilldetail.getCreditamount())==true) {
    		   egbilldetailCusList.add(egbilldetail);
    	   }
		 }
	  
	  egBillregister.setBillamount(totalCrAmt);
	  egBillregister.setBillDetails(egbilldetailCusList);
	
	 System.out.println(totalCrAmt);

	 EgBillSubType egbillSubtype=(EgBillSubType) getBillSubTypes().stream().filter(e-> e.getName().equalsIgnoreCase("Refund")).findFirst().orElse(null);
       
       
     egBillregister.setCreatedBy(ApplicationThreadLocals.getUserId());
     ExpenseBillNumberGenerator v = beanResolver.getAutoNumberServiceFor(ExpenseBillNumberGenerator.class);
     
	
	 
	  CFunction function= paymentRefundUtils.getFunction(bg1.longValue());
	  egBillregister.getEgBillregistermis().setFunction(function);
	  egBillregister.getEgBillregistermis().setEgBillSubType(egbillSubtype);
	  //egbillregistermis.setFunction(function);
	 
    
     
     //egBillregister.setEgBillregistermis(egbillregistermis);
    
    String billNumber = v.getNextNumber(egBillregister);
    System.out.println(billNumber);
    
    
    	egBillregister.setBillnumber(billNumber);
    
     //String billNumber="refund-test-2021";
    
    if (StringUtils.isEmpty(egBillregister.getExpendituretype()))
    egBillregister.setExpendituretype(FinancialConstants.STANDARD_EXPENDITURETYPE_REFUND);

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
    upload.setContentType(contentType[i]);
    list.add(upload);
    }
    
    egBillregister.getEgBilldetailes().addAll(egBillregister.getBillDetails());
    populateBillDetails(egBillregister);
    
    validateBillNumber(egBillregister, resultBinder);
    
	
	  if(!workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT)) {
		  refundvalidateLedgerAndSubledger(egBillregister, resultBinder);
	  }
	 
	  validateSubledgeDetails(egBillregister);  
	  System.out.println("------------------------------"+egBillregister.getBillPayeeDetailsNotLink().isEmpty());	  
if(egBillregister.getBillPayeeDetailsNotLink().isEmpty()) {

    if (resultBinder.hasErrors()) {
    	System.out.println("from ResultBinder Error");
    	for (Object object : resultBinder.getAllErrors()) {
    	    if(object instanceof FieldError) {
    	        FieldError fieldError = (FieldError) object;

    	        System.out.println(fieldError.getCode());
    	    }

    	    if(object instanceof ObjectError) {
    	        ObjectError objectError = (ObjectError) object;

    	        System.out.println(objectError.getCode());
    	    }
    	}
    	return "redirect:/refund/_paymentRequestblankvoucherForm";
     //return "redirect:/refund/_paymentRequestForm";
      } else {
            Long approvalPosition = 0l;
            String approvalComment = "";
            String approvalDesignation = "";
            if (request.getParameter("approvalComent") != null)
             approvalComment = request.getParameter("approvalComent");
            if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
              {
               	if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                  	{            		
                     approvalPosition =populatePosition();            		
                    }
                  else
                     approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
                }
             else {
                if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                 {            		
                  approvalPosition =populatePosition();            		
                 }		            	
              }
             if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
                 approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
                    		            
             EgBillregister savedEgBillregister;
             egBillregister.setDocumentDetail(list);
                  try {
                	  System.out.println("From egbillregister Save method calling");
                       savedEgBillregister = refundBillService.createByBlankVoucher(egBillregister, approvalPosition, approvalComment, null, 
                       workFlowAction,approvalDesignation,vhid);
                       
                       
                     } catch (ValidationException e) {
                    	 System.out.println("From Exception saving time");
                    	 e.printStackTrace();
                    
                    	 return "redirect:/refund/_paymentRequestblankvoucherForm";
                    //return "redirect:/refund/_paymentRequestForm";
                    }
                   String approverName =null;
                   if(workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONSAVEASDRAFT))
                    {        		
                    approverName =populateEmpName();        		
                    }
                   else
                    approverName = String.valueOf(request.getParameter("approverName"));
                    final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                                 savedEgBillregister.getState(), savedEgBillregister.getId(), approvalPosition,approverName);
                 		              
                    return "redirect:/refund/successRefund?approverDetails=" + approverDetails + "&billNumber="
                    		                    + savedEgBillregister.getBillnumber()+"&billId="
                    		                            + savedEgBillregister.getId();
                   }
    
}else {
	StringBuilder message = new StringBuilder();
	for(int i=0;i<egBillregister.getBillPayeeDetailsNotLink().size();i++) {
		
		message.append("account detial key "+egBillregister.getBillPayeeDetailsNotLink().get(i).getAccountDetailTypeId()+" not mapped with  glcodeid "+egBillregister.getBillPayeeDetailsNotLink().get(i).getEgBilldetailsId().getGlcodeid()+"\n");
		
	}
	redirectAttributes.addFlashAttribute("message",message);
	return "redirect:/refund/_paymentRequestblankvoucherForm";
}
             
        }

}