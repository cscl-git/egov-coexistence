package org.egov.egf.web.controller.refund;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CGeneralLedgerDetail;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.AccountdetailtypeService;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.commons.VoucherSearchUtil;
import org.egov.egf.contract.model.RefundRequest;
import org.egov.egf.contract.model.RefundResponse;
import org.egov.egf.contract.model.VoucherDetailsResponse;
import org.egov.egf.contract.model.VoucherRequest;
import org.egov.egf.contract.model.VoucherSearch;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.EgBillSubType;
import org.egov.model.bills.EgBillregister;
import org.egov.model.voucher.PreApprovedVoucher;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.utils.PaymentRefundUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/refund")
public class PaymentRefundController {
	private final String datePattern="dd/MM/yyyy";
	private final String VOUCHER_SEARCH="voucherSearch";
	private final String PR_VOUCHER_SEARCH="pr-voucher-search";
	private final String PR_VOUCHER_VIEW="pr-voucher-view";
	private final String PR_REQUEST_FORM="pr-request-form";
	private final Map<String, String> VOUCHER_TYPES = new HashMap<String, String>();
	
	@Autowired
	protected AppConfigValueService appConfigValuesService;
	@Autowired
	protected MicroserviceUtils microserviceUtils;
	@Autowired
	protected EgovMasterDataCaching masterDataCache;	
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
	private FunctionDAO functionDAO;
    
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
				
				if(!voucherMap.containsKey("payeeName")) {
					voucherMap.put("payeeName", "-");
				}
				voucherList.add(voucherMap);
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
	public String paymentRequestForm(@RequestParam(name = "vhid") final Long vhid, final Model model) {
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
			
			populateDropDownValues(model);
		}
		
		model.addAttribute("voucherDetails", voucherDetails);
		model.addAttribute("accountDetails", tempList);
		model.addAttribute("subLedgerlist", payeeList);
		model.addAttribute("dbAmount", dbAmount);
		model.addAttribute("crAmount", crAmount);
		return PR_REQUEST_FORM;
	}
	
    private void populateDropDownValues(final Model model) {
        model.addAttribute("billNumberGenerationAuto", expenseBillService.isBillNumberGenerationAuto());
        model.addAttribute("billSubTypes", getBillSubTypes());
        model.addAttribute("subLedgerTypes", accountdetailtypeService.findAll());
        model.addAttribute("cFunctions", functionDAO.getAllActiveFunctions());
    }
    
    public List<EgBillSubType> getBillSubTypes() {
        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT);
    }
    
    @PostMapping(value = "/_processRefund")
	@ResponseBody
    public RefundResponse processRefund (@RequestBody RefundRequest refundRequest)
    {
    	System.out.println("1");
    	RefundResponse response=new RefundResponse();
    	try
    	{
    		
        	response.setResponseStatus(refundRequest.getReceipt().getReceiptNumber());
        	response.setResponseInfo(MicroserviceUtils.getResponseInfo(refundRequest.getRequestInfo(),
    				HttpStatus.SC_CREATED, null));
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return response;
    }
}
