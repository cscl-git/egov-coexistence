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
package org.egov.egf.web.controller.voucher;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.egov.commons.Accountdetailkey;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CGeneralLedgerDetail;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Vouchermis;
import org.egov.deduction.model.EgRemittance;
import org.egov.egf.commons.VoucherSearchUtil;
import org.egov.egf.model.BillRegisterReportBean;
import org.egov.egf.utils.FinancialUtils;
import org.egov.egf.voucher.service.JournalVoucherService;
import org.egov.egf.web.actions.report.BillRegisterReportAction;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.ChartOfAccounts;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregistermis;
import org.egov.model.bills.Miscbilldetail;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.instrument.InstrumentVoucher;
import org.egov.model.payment.Paymentheader;
import org.egov.utils.FinancialConstants;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.python.antlr.PythonParser.for_stmt_return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author venki
 *
 */

@Controller
@RequestMapping(value = "/journalvoucher")
public class CreateJournalVoucherController extends BaseVoucherController {
	 private static final Logger LOGGER = Logger.getLogger(CreateJournalVoucherController.class);
    private static final String JOURNALVOUCHER_FORM = "journalvoucher-form";
    private static final String JOURNALVOUCHER_SEARCH = "journalvoucher-search";
    private static final String VOUCHER_NUMBER_GENERATION_AUTO = "voucherNumberGenerationAuto";
    private static  final String chqdelimitSP = "/";
    private static final String chqdelimitDP = "//";
    public static final Locale LOCALE = new Locale("en", "IN");
    public static final SimpleDateFormat DDMMYYYYFORMATS = new SimpleDateFormat("dd/MM/yyyy", LOCALE);
    public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
    private static final String STATE_TYPE = "stateType";
    private static Map<String, List<String>> netAccountCode = new HashMap<String, List<String>>(); // have list of all net payable
    @Autowired	
    private  AppConfigValueService appConfigValueService;
    private static final String APPROVAL_POSITION = "approvalPosition";

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;
    @Autowired
	private MicroserviceUtils microserviceUtils;

    @Autowired
    private JournalVoucherService journalVoucherService;

    @Autowired
    private FinancialUtils financialUtils;
    @Autowired
    private VoucherSearchUtil voucherSearchUtil;
    @Autowired
    @Qualifier("persistenceService")
    protected transient PersistenceService persistenceService;

    public CreateJournalVoucherController(final AppConfigValueService appConfigValuesService) {
        super(appConfigValuesService);
    }

    @Override
    protected void setDropDownValues(final Model model) {
        super.setDropDownValues(model);
        model.addAttribute("voucherSubTypes", FinancialUtils.VOUCHER_SUBTYPES);
    }

    @RequestMapping(value = "/newform", method = RequestMethod.GET)
    public String showNewForm(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) {
        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        setDropDownValues(model);
        model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
        prepareWorkflow(model, voucherHeader, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        voucherHeader.setVoucherDate(new Date());
        model.addAttribute(VOUCHER_NUMBER_GENERATION_AUTO, isVoucherNumberGenerationAuto(voucherHeader, model));
        return JOURNALVOUCHER_FORM;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,
            final BindingResult resultBinder, final HttpServletRequest request, @RequestParam final String workFlowAction) {

        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        voucherHeader.setEffectiveDate(voucherHeader.getVoucherDate());

        populateVoucherName(voucherHeader);
        populateAccountDetails(voucherHeader);

        if (resultBinder.hasErrors()) {
            setDropDownValues(model);
            model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
            prepareWorkflow(model, voucherHeader, new WorkflowContainer());
            prepareValidActionListByCutOffDate(model);
            voucherHeader.setVoucherDate(new Date());
            model.addAttribute(VOUCHER_NUMBER_GENERATION_AUTO, isVoucherNumberGenerationAuto(voucherHeader, model));

            return JOURNALVOUCHER_FORM;
        } else {
            Long approvalPosition = 0l;
            String approvalComment = "";
            if (request.getParameter("approvalComment") != null)
                approvalComment = request.getParameter("approvalComent");
            if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
                approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
            CVoucherHeader savedVoucherHeader;
            try {
                savedVoucherHeader = journalVoucherService.create(voucherHeader, approvalPosition, approvalComment, null,
                        workFlowAction);
            } catch (final ValidationException e) {
                setDropDownValues(model);
                model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
                prepareWorkflow(model, voucherHeader, new WorkflowContainer());
                prepareValidActionListByCutOffDate(model);
                voucherHeader.setVoucherDate(new Date());
                model.addAttribute(VOUCHER_NUMBER_GENERATION_AUTO, isVoucherNumberGenerationAuto(voucherHeader, model));
                resultBinder.reject("", e.getErrors().get(0).getMessage());
                return JOURNALVOUCHER_FORM;
            }

            final String approverDetails = financialUtils.getApproverDetails(workFlowAction,
                    savedVoucherHeader.getState(), savedVoucherHeader.getId(), approvalPosition,"");

            return "redirect:/journalvoucher/success?approverDetails= " + approverDetails + "&voucherNumber="
                    + savedVoucherHeader.getVoucherNumber() + "&workFlowAction=" + workFlowAction;

        }
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("voucherNumber") final String voucherNumber, final Model model,
            final HttpServletRequest request) {
        final String workFlowAction = request.getParameter("workFlowAction");
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
                currentUserDesgn = keyNameArray[2];
            } else {
                id = Long.parseLong(keyNameArray[0].trim());
                approverName = keyNameArray[1];
                currentUserDesgn = keyNameArray[2];
                nextDesign = keyNameArray[3];
            }

        if (id != null)
            model.addAttribute("approverName", approverName);
        model.addAttribute("currentUserDesgn", currentUserDesgn);
        model.addAttribute("nextDesign", nextDesign);

        final CVoucherHeader voucherHeader = journalVoucherService.getByVoucherNumber(voucherNumber);

        final String message = getMessageByStatus(voucherHeader, approverName, nextDesign, workFlowAction);

        model.addAttribute("message", message);

        return "expensebill-success";
    }

    
    
    @RequestMapping(value = "/searchVoucher", method = RequestMethod.POST)
    public String searchVoucher(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) {
       
        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
        prepareWorkflow(model, voucherHeader, new WorkflowContainer());
       
        
        voucherHeader.setVoucherDate(new Date());
        return JOURNALVOUCHER_SEARCH;
    }
    
    @RequestMapping(value = "/searchVoucherResult",params = "search", method = RequestMethod.POST)
    public String searchVoucherResult(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) {
       
        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
        prepareWorkflow(model, voucherHeader, new WorkflowContainer());
        voucherHeader.setVoucherDate(new Date());
        Vouchermis vouchermis=new Vouchermis();
        vouchermis.setDepartmentcode("");
         Long preVoucherId;
         boolean isCompleteBillRegisterReport = false;
         List<BillRegisterReportBean> billRegReportList = new CopyOnWriteArrayList<>()  ;
        SQLQuery querye =  null;
        List<Object[]> list =null;
        StringBuffer query = new StringBuffer(1000);
        query=getQuery(voucherHeader);
        List<Integer> cancelledChequeStatus = new ArrayList<Integer>();
        
        querye = this.persistenceService.getSession().createSQLQuery(query.toString());
        list=querye.list();
        
        //list = persistenceService.findAllBy(query.toString());
		
        if (list.size() != 0) {
       	 System.out.println(" data present");
       	 for (final Object[] object : list) {
       		
       		if(object[1] != null)
       		{
try {

                final BillRegisterReportBean billRegReport = new BillRegisterReportBean();
                billRegReport.setVoucherNumber(object[0] != null ? object[0].toString() : "");
                billRegReport.setDeducVoucherNumber(getDeducVoucherNumber(billRegReport.getVoucherNumber()));
                billRegReport.setDeducVhId(getDeducVoucId(billRegReport.getVoucherNumber()));
                billRegReport.setVhId(getVoucId(billRegReport.getVoucherNumber()));
                billRegReport.setPhId(getPayId(billRegReport.getVoucherNumber()));
                

               billRegReport.setPartyName(getPartyName(billRegReport.getVoucherNumber()));
                billRegReport.setGrossAmount(getgrossAmount(Long.valueOf(object[2].toString())));
                
                billRegReport.setNetAmount(getnetAmount(Long.valueOf(object[2].toString()),billRegReport.getGrossAmount()));
               
                //billRegReport.setDeductionAmount(billRegReport.getGrossAmount().subtract(billRegReport.getNetAmount()));
              //  billRegReport.setStatus(null != object[5] ? object[5].toString().toUpperCase() : "");
              //  billRegReport.setBillDate(DDMMYYYYFORMATS.format((Date) object[6]));
                billRegReport.setDepartmentCode(getDepartmentcode(object[1].toString()));
                billRegReport.setBillDetailList(getbillDetails(Long.valueOf(object[2].toString()),billRegReport.getNetAmount()));
                
                List<Miscbilldetail> miscbilldetailList=getbillnum(billRegReport.getVoucherNumber());
               // List<InstrumentHeader> instrumentHeaderList=getPexNumber(Long.valueOf(object[2].toString()));
              
                
             for (Miscbilldetail miscbilldetail : miscbilldetailList) {
                
                	List<InstrumentHeader> instrumentHeaderList=getPexNumber(miscbilldetail.getId());
                	
                	for (InstrumentHeader instrumentHeader : instrumentHeaderList) {
						
                	BillRegisterReportBean billRegReport2 = new BillRegisterReportBean();
                	
                	billRegReport2.setVoucherNumber(billRegReport.getVoucherNumber());
                	billRegReport2.setPartyName(billRegReport.getPartyName());
                	billRegReport2.setDepartmentCode(billRegReport.getDepartmentCode());
                	billRegReport2.setGrossAmount(billRegReport.getGrossAmount());
                	billRegReport2.setNetAmount(billRegReport.getNetAmount());
                	billRegReport2.setBillDetailList(billRegReport.getBillDetailList());
                	
                    	billRegReport2.setBillNumber(miscbilldetail.getBillnumber());
                    	billRegReport2.setPaidAmount(miscbilldetail.getPaidamount());
                    	billRegReport2.setPexNo(instrumentHeader.getTransactionNumber());
                    	billRegReport2.setPexNodate(instrumentHeader.getVoucherNumber());
                		
                    	billRegReportList.add(billRegReport2);
					
                	
                	}
                	
                
     				
                	}
     				
              /*  if(miscbilldetailList.isEmpty())
                {
                	for (InstrumentHeader instrumentHeader : instrumentHeaderList) {
                    	 
                		 BillRegisterReportBean billRegReport2 = new BillRegisterReportBean();
                     	
                     	billRegReport2.setVoucherNumber(billRegReport.getVoucherNumber());
                     	billRegReport2.setPartyName(billRegReport.getPartyName());
                     	billRegReport2.setDepartmentCode(billRegReport.getDepartmentCode());
                     	billRegReport2.setGrossAmount(billRegReport.getGrossAmount());
                     	billRegReport2.setNetAmount(billRegReport.getNetAmount());
                     	billRegReport2.setBillDetailList(billRegReport.getBillDetailList());
                     	
                     	
                		billRegReport2.setPexNo(instrumentHeader.getTransactionNumber());
                		billRegReport2.setPexNodate(instrumentHeader.getVoucherNumber());
                     	billRegReportList.add(billRegReport2);
          				}
                                }
                if(instrumentHeaderList.isEmpty())
                {
                	for (Miscbilldetail miscbilldetail : miscbilldetailList) {
                       
                       	 
                		BillRegisterReportBean billRegReport2 = new BillRegisterReportBean();

                    	billRegReport2.setVoucherNumber(billRegReport.getVoucherNumber());
                    	billRegReport2.setPartyName(billRegReport.getPartyName());
                    	billRegReport2.setDepartmentCode(billRegReport.getDepartmentCode());
                    	billRegReport2.setGrossAmount(billRegReport.getGrossAmount());
                    	billRegReport2.setNetAmount(billRegReport.getNetAmount());
                    	billRegReport2.setBillDetailList(billRegReport.getBillDetailList());
                               	
                               	billRegReport2.setBillNumber(miscbilldetail.getBillnumber());
                               	billRegReport2.setPaidAmount(miscbilldetail.getPaidamount());
                	billRegReportList.add(billRegReport2);

                }
                }*/
             Boolean voucherPresent=true;
             for (BillRegisterReportBean billRegisterReportBean : billRegReportList) {
            	if(billRegisterReportBean.getVoucherNumber().equals(billRegReport.getVoucherNumber()))
            		{
            		voucherPresent=false;
            			}
			}
                

             if(voucherPresent) {
                billRegReportList.add(billRegReport);
             }
             
             
                model.addAttribute("billRegReportList",billRegReportList);
            } catch (final Exception e) {
            	e.printStackTrace();
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Failed while processing bill number :" + object[0].toString());
                throw e;
            }
       		}
        
       	 }
        }
        
        return JOURNALVOUCHER_SEARCH;
    }
    
    
    @RequestMapping(value = "/searchVoucherResult",params = "export", method = RequestMethod.POST)
    public String voucherExport(@ModelAttribute("voucherHeader") final CVoucherHeader voucherHeader, final Model model,final HttpServletRequest request) {
       
        voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);
        model.addAttribute(STATE_TYPE, voucherHeader.getClass().getSimpleName());
        prepareWorkflow(model, voucherHeader, new WorkflowContainer());
        voucherHeader.setVoucherDate(new Date());
        Vouchermis vouchermis=new Vouchermis();
        vouchermis.setDepartmentcode("");
         Long preVoucherId;
         boolean isCompleteBillRegisterReport = false;
         List<BillRegisterReportBean> billRegReportList = new CopyOnWriteArrayList<>()  ;
        SQLQuery querye =  null;
        List<Object[]> list =null;
        StringBuffer query = new StringBuffer(1000);
        query=getQuery(voucherHeader);
        List<Integer> cancelledChequeStatus = new ArrayList<Integer>();
        
        querye = this.persistenceService.getSession().createSQLQuery(query.toString());
        list=querye.list();
        
        //list = persistenceService.findAllBy(query.toString());
		
        if (list.size() != 0) {
       	 System.out.println(" data present");
       	 for (final Object[] object : list) {
       		
       		if(object[1] != null)
       		{
try {

                final BillRegisterReportBean billRegReport = new BillRegisterReportBean();
                billRegReport.setVoucherNumber(object[0] != null ? object[0].toString() : "");
                billRegReport.setDeducVoucherNumber(getDeducVoucherNumber(billRegReport.getVoucherNumber()));
                billRegReport.setDeducVhId(getDeducVoucId(billRegReport.getVoucherNumber()));
                billRegReport.setVhId(getVoucId(billRegReport.getVoucherNumber()));
                billRegReport.setPhId(getPayId(billRegReport.getVoucherNumber()));
                
                
               billRegReport.setPartyName(getPartyName(billRegReport.getVoucherNumber()));
                billRegReport.setGrossAmount(getgrossAmount(Long.valueOf(object[2].toString())));

                billRegReport.setNetAmount(getnetAmount(Long.valueOf(object[2].toString()),billRegReport.getGrossAmount()));
                
                //billRegReport.setDeductionAmount(billRegReport.getGrossAmount().subtract(billRegReport.getNetAmount()));
              //  billRegReport.setStatus(null != object[5] ? object[5].toString().toUpperCase() : "");
              //  billRegReport.setBillDate(DDMMYYYYFORMATS.format((Date) object[6]));
                billRegReport.setDepartmentCode(getDepartmentcode(object[1].toString()));
                billRegReport.setBillDetailList(getbillDetails(Long.valueOf(object[2].toString()),billRegReport.getNetAmount()));
                
                List<Miscbilldetail> miscbilldetailList=getbillnum(billRegReport.getVoucherNumber());
               // List<InstrumentHeader> instrumentHeaderList=getPexNumber(Long.valueOf(object[2].toString()));
                
                
             for (Miscbilldetail miscbilldetail : miscbilldetailList) {
				
                	List<InstrumentHeader> instrumentHeaderList=getPexNumber(miscbilldetail.getId());
                	
                	for (InstrumentHeader instrumentHeader : instrumentHeaderList) {
                
                	BillRegisterReportBean billRegReport2 = new BillRegisterReportBean();
                	
                	billRegReport2.setVoucherNumber(billRegReport.getVoucherNumber());
                	billRegReport2.setPartyName(billRegReport.getPartyName());
                	billRegReport2.setDepartmentCode(billRegReport.getDepartmentCode());
                	billRegReport2.setGrossAmount(billRegReport.getGrossAmount());
                	billRegReport2.setNetAmount(billRegReport.getNetAmount());
                	billRegReport2.setBillDetailList(billRegReport.getBillDetailList());
                	
                    	billRegReport2.setBillNumber(miscbilldetail.getBillnumber());
                    	billRegReport2.setPaidAmount(miscbilldetail.getPaidamount());
                    	billRegReport2.setPexNo(instrumentHeader.getTransactionNumber());
                    	billRegReport2.setPexNodate(instrumentHeader.getVoucherNumber());
                		
                	billRegReportList.add(billRegReport2);
     				
                	
				}
                	
                
     				
                }
                
              /*  if(miscbilldetailList.isEmpty())
                {
                	for (InstrumentHeader instrumentHeader : instrumentHeaderList) {
                    	 
                		 BillRegisterReportBean billRegReport2 = new BillRegisterReportBean();
                     	
                     	billRegReport2.setVoucherNumber(billRegReport.getVoucherNumber());
                     	billRegReport2.setPartyName(billRegReport.getPartyName());
                     	billRegReport2.setDepartmentCode(billRegReport.getDepartmentCode());
                     	billRegReport2.setGrossAmount(billRegReport.getGrossAmount());
                     	billRegReport2.setNetAmount(billRegReport.getNetAmount());
                     	billRegReport2.setBillDetailList(billRegReport.getBillDetailList());
                     	
                     	
                		billRegReport2.setPexNo(instrumentHeader.getTransactionNumber());
                		billRegReport2.setPexNodate(instrumentHeader.getVoucherNumber());
                     	billRegReportList.add(billRegReport2);
          				}
                                }
                if(instrumentHeaderList.isEmpty())
                {
                	for (Miscbilldetail miscbilldetail : miscbilldetailList) {
                       
                       	 
                		BillRegisterReportBean billRegReport2 = new BillRegisterReportBean();
                    	
                    	billRegReport2.setVoucherNumber(billRegReport.getVoucherNumber());
                    	billRegReport2.setPartyName(billRegReport.getPartyName());
                    	billRegReport2.setDepartmentCode(billRegReport.getDepartmentCode());
                    	billRegReport2.setGrossAmount(billRegReport.getGrossAmount());
                    	billRegReport2.setNetAmount(billRegReport.getNetAmount());
                    	billRegReport2.setBillDetailList(billRegReport.getBillDetailList());

                               	billRegReport2.setBillNumber(miscbilldetail.getBillnumber());
                               	billRegReport2.setPaidAmount(miscbilldetail.getPaidamount());
                	billRegReportList.add(billRegReport2);

                }
                }*/
             Boolean voucherPresent=true;
             for (BillRegisterReportBean billRegisterReportBean : billRegReportList) {
            	if(billRegisterReportBean.getVoucherNumber().equals(billRegReport.getVoucherNumber()))
            		{
            		voucherPresent=false;
                }
			}
                

             if(voucherPresent) {
                billRegReportList.add(billRegReport);
             }
             
             
                model.addAttribute("billRegReportList",billRegReportList);
            } catch (final Exception e) {
            	e.printStackTrace();
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Failed while processing bill number :" + object[0].toString());
                throw e;
            }
       		}
        
       	 }
        }
        
        return JOURNALVOUCHER_SEARCH;
    }
    
    
    
    protected StringBuffer getQuery(CVoucherHeader voucherHeader) {
        final StringBuffer query = new StringBuffer(1000);
        final StringBuffer whereQuery = new StringBuffer(200);
        new StringBuffer(50);

        /*
         * if(null != voucherHeader.getVoucherNumber() && !StringUtils.isEmpty(voucherHeader.getVoucherNumber())){
         * whereQuery.append(" and vh.vouchernumber like '%"+voucherHeader.getVoucherNumber()+"%'"); }
         */

        if (null != voucherHeader.getFundId())
            whereQuery.append(" and vh.fundid=" + voucherHeader.getFundId().getId());
        if (null != voucherHeader.getVouchermis().getDepartmentcode() && !voucherHeader.getVouchermis().getDepartmentcode().equals("-1"))
            whereQuery.append(" and mis.departmentcode='" + voucherHeader.getVouchermis().getDepartmentcode()+"'");
       
      /*  if (null != voucherHeader.getBillFrom())
            whereQuery.append(" and vh.voucherdate >= to_date('" + DDMMYYYYFORMATS.format(voucherHeader.getBillFrom()) + "','dd/MM/yyyy')");
        if (null != voucherHeader.getBillTo())
            whereQuery.append(" and vh.voucherdate <= to_date('" + DDMMYYYYFORMATS.format(voucherHeader.getBillTo()) + "','dd/MM/yyyy')");
       */
       
        if (null != voucherHeader.getBillFrom())
        	whereQuery.append(" and vh.voucherdate >='")
					.append(DDMMYYYYFORMAT1.format(voucherHeader.getBillFrom()))
					.append("'");
        if (null != voucherHeader.getBillTo())
        	whereQuery.append(" and vh.voucherdate <='")
					.append(DDMMYYYYFORMAT1.format(voucherHeader.getBillTo()))
					.append("'");
        
        
        query.append(getQueryByExpndType("Expense", whereQuery.toString(), voucherHeader));
      

        return query;
    }
   private String getDepartmentcode(String departmentCode) {
	   String departname="";

	   Department department=microserviceUtils.getDepartmentByCode(departmentCode);
	   if(department!=null)
		  departname=department.getName();
	   
	   return departname;
    
   }
    protected String getQueryByExpndType(final String expndType, final String whereQuery,CVoucherHeader voucherHeader) {

    	
        String voucherQry = "";
       
        // voucher header condition for complete bill register report
        if (voucherHeader.getVoucherNumber() != null && !StringUtils.isEmpty(voucherHeader.getVoucherNumber()))
            voucherQry = " and vh.vouchernumber like '%" + voucherHeader.getVoucherNumber() + "%'";
        final StringBuffer query = new StringBuffer(500);

        query.append(
                " select  vh.vouchernumber, mis.departmentcode, vh.id as voucherid")
                .
        		append(" from   voucherheader vh,vouchermis mis ")
                .
        		append(" where vh.type='Journal Voucher'   and mis.voucherheaderid =vh.id ")
                .
                append(voucherQry)
              
             //   append("  and vh.status = 4")
                .append(whereQuery);
              

        if (voucherHeader.getVoucherNumber() == null || StringUtils.isEmpty(voucherHeader.getVoucherNumber())) {
            query.append(" UNION ");

            // query to get bills for voucher is not created
            
            query.append(
            		 " select  vh.vouchernumber, mis.departmentcode, vh.id as voucherid")
            		.
    		append(" from   voucherheader vh,vouchermis mis ")
            		.
    		append(" where vh.type='Journal Voucher'   and mis.voucherheaderid =vh.id ").
              append(voucherQry)
            		.
               append(whereQuery);
                   
        }

        return query.toString();
    }
 
 
 
 private List<InstrumentHeader> getPexNumber(Long deducVhId) {
 	SQLQuery query =  null;
 	List<Object[]> rows = null;
 	String deducvh="";
 	
 	List<InstrumentHeader> instrumentHeaderList=new ArrayList<>();
 	
 	try
 	{
 		 query = this.persistenceService.getSession().createSQLQuery("select ei.id,ei.transactionnumber,ei.transactiondate from egf_instrumentheader ei where ei.id_status =2 and ei.id in (select ei2.instrumentheaderid from egf_instrumentvoucher ei2  where ei2.voucherheaderid =:deducVhId)");
 	    query.setLong("deducVhId", deducVhId);
 	    rows = query.list();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		InstrumentHeader instrumentHeader=new InstrumentHeader();
 	    		if(element[1] !=null)
 	    		{
 	    			deducvh= element[1].toString();
 	    			instrumentHeader.setTransactionNumber(deducvh);
 	    		}
 	    		else
 	    		{
 	    			deducvh= "";
 	    			instrumentHeader.setTransactionNumber(deducvh);
 	    		}
 	    		if(element[2] !=null)
 	    		{
 	    			deducvh= element[2].toString();
 	    			instrumentHeader.setVoucherNumber(deducvh);
 	    		}
 	    		else
 	    		{
 	    			deducvh= "";
 	    			instrumentHeader.setVoucherNumber(deducvh);
 	    		}
 	    		
 	    		instrumentHeaderList.add(instrumentHeader);
 	    		
 	    	}
 	    	
 	    	
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return instrumentHeaderList;
 }

 
 private BigDecimal getnetAmount( Long voucherheaderid,BigDecimal grossamt) {
	 SQLQuery query =  null;
	 	List<Object[]> rows = null;
	 	String deducvh="";
	 	
	 	 BigDecimal totaltax=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	 BigDecimal netamount=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	 BigDecimal otherDeductionAmount=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	 
	 	 
	 	try
	 	{
	 		LOGGER.debug("...........g..Query........");
	 		 query = this.persistenceService.getSession().createSQLQuery("select egb.id,egb.glcode,egb.creditamount from generalledger egb  where egb.voucherheaderid =:voucherheaderid");
	 	    query.setLong("voucherheaderid", voucherheaderid);
	 	    rows = query.list();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		BillDetail billDetail=new BillDetail();
	 	    		if(element[0] !=null)
	 	    		{
	 	    			billDetail.setId(null != element[0] ? element[0].toString(): "");
	 	    		}
	 	    		if(element[1] !=null)
	 	    		{
	 	    			billDetail.setConsumerCode(null != element[1] ? element[1].toString() : "");
	 	    			
	 	    			if(!billDetail.getConsumerCode().equalsIgnoreCase("")) {
	 	    				if(billDetail.getConsumerCode().equals("3502007")||billDetail.getConsumerCode().equals("3502009")||
	 	    						billDetail.getConsumerCode().equals("3502010")||billDetail.getConsumerCode().equals("3502011")||
	 	    						billDetail.getConsumerCode().equals("3502012")) {
	 	    					
	 	    					totaltax =totaltax.add(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 	    					billDetail.setConsumerType("Tax");
	 	    					
	 	    				}
	 	    				
	 	    			}
	 	    			
	 	    			
	 	    			
	 	    			
	 	    		}
	 	    	}
	 	    	
	 	    	otherDeductionAmount=otherdeductionAmt(voucherheaderid);
	 	    	totaltax=totaltax.add(otherDeductionAmount);
	 	    	
	 	    	netamount=grossamt.subtract(totaltax);
	 	    }
	 	}catch(Exception e) {e.printStackTrace();}
	 	
	 	return netamount;
 }

 
 private BigDecimal getgrossAmount( Long voucherheaderid) {
	 SQLQuery query =  null;
	 	List<Object[]> rows = null;
	 	String deducvh="";
	 	
	 	 BigDecimal grossamt=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	try
	 	{
	 		LOGGER.debug("...........ggetgrossAmount..Query........");
	 		 query = this.persistenceService.getSession().createSQLQuery(" select egb.id,sum(egb.debitamount) from generalledger egb  where egb.voucherheaderid = :voucherheaderid and debitamount > 0 group by id;\r\n" + 
	 		 		"");
	 	    query.setLong("voucherheaderid", voucherheaderid);
	 	    rows = query.list();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		
	 	    		if(element[1] !=null)
	 	    		{
	 	    			grossamt =null != element[1] ? new BigDecimal(element[1].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	    					
	 	    				}
	 	    			}
	 	    			
	 	    		}
	 	    	
	 	}catch(Exception e) {e.printStackTrace();}
	 	
	 	return grossamt;
 }
 
 
 private List<BillDetail> getbillDetails(Long billid,BigDecimal netAmount) {
	 	SQLQuery query =  null;
	 	List<Object[]> rows = null;
	 	String deducvh="";
	 	List<ChartOfAccounts> ChartOfAccountsList=new ArrayList<>();
	 	ChartOfAccounts ChartOfAccounts=new ChartOfAccounts();
	 	
	 	List<BillDetail> billDetailList=new ArrayList<>();
	 	
	 BigDecimal totaltax=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	try
	 	{
	 		LOGGER.debug("...........g..Query........");
	 		 query = this.persistenceService.getSession().createSQLQuery("select egb.id,egb.glcode,egb.creditamount from generalledger egb  where egb.voucherheaderid =:billid");
	 	    query.setLong("billid", billid);
	 	    rows = query.list();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		BillDetail billDetail=new BillDetail();
	 	    		if(element[0] !=null)
	 	    		{
	 	    			billDetail.setId(null != element[0] ? element[0].toString(): "");
	 	    		}
	 	    		if(element[1] !=null)
	 	    		{
	 	    			billDetail.setConsumerCode(null != element[1] ? element[1].toString() : "");
	 	    			
	 	    			if(!billDetail.getConsumerCode().equalsIgnoreCase("")) {
	 	    				if(billDetail.getConsumerCode().equals("3502007")||billDetail.getConsumerCode().equals("3502009")||
	 	    						billDetail.getConsumerCode().equals("3502010")||billDetail.getConsumerCode().equals("3502011")||
	 	    						billDetail.getConsumerCode().equals("3502012")) {
	 	    					
	 	    					totaltax =totaltax.add(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 	    					billDetail.setConsumerType("Tax");
	 	    					
	 	    				}
	 	    				
 	    		}
 	    		
	 	    		}
	 	    		if(element[2] !=null)
	 	    		{
	 	    			netAmount.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	    			BigDecimal anyotherDeduction=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	    			if(!billDetail.getConsumerCode().equalsIgnoreCase("")) {
	 	    				if(!(billDetail.getConsumerCode().equals("3502007")||billDetail.getConsumerCode().equals("3502009")||
	 	    						billDetail.getConsumerCode().equals("3502010")||billDetail.getConsumerCode().equals("3502011")||
	 	    						billDetail.getConsumerCode().equals("3502012")||
	 	    						
	 	    						billDetail.getConsumerCode().equals("3502055")||billDetail.getConsumerCode().equals("3502054")||
	 	    						billDetail.getConsumerCode().equals("3502018")||billDetail.getConsumerCode().equals("1408055")||
	 	    						billDetail.getConsumerCode().equals("1405014")||billDetail.getConsumerCode().equals("3502058")||billDetail.getConsumerCode().equals("1402003")||
	 	    						billDetail.getConsumerCode().equals("3401004"))) {
	 	    					
	 	    					 LOGGER.debug("...........beforeotherDeductionAmount........");
	 	    					billDetail.setAmountPaid(otherdeductionAmt(billid));
	 	    					billDetail.setConsumerType("AnyOtherDeduction");
	 	    				}
	 	    				else {
	 		 	    			
	 		 	    			
	 		 	    			billDetail.setAmountPaid(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 		 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 		 	    			}
	 	    				
	 	    			}
	 	    			
	 	    		}
	 	    		
	 	    	
	 	    		
	 	    		billDetailList.add(billDetail);
	 	    		
 	    	}
 	    }
	 	    
	 	    for (BillDetail billDetail : billDetailList) {
	 	    	billDetail.setTotalAmount(totaltax);
			}
 	}catch (Exception e) {
			e.printStackTrace();
		}
	 	
		    return billDetailList;
	 }

 private BigDecimal otherdeductionAmt(Long voucherid) {
	 SQLQuery query =  null;
	 	List<Object[]> rows = null;
	 	 BigDecimal totaltax=	BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	 	try
	 	{
	 		 LOGGER.debug("...........beforeotherDeductionAmount..Query........");
	 		 
	 query = this.persistenceService.getSession().createSQLQuery("select g.id,sum(g.creditamount) from generalledger g, tds t where	g.glcodeid = t.glcodeid	and g.voucherheaderid =:voucherid 	and g.creditamount > 0	and g.glcodeid not in (	select	id	from chartofaccounts c2 where	glcode in ('3502054','3502007',	'3502009','3502010','3502011','3502012','3502055','3502054','3502018','1408055','1405014','3502058','1402003','3401004')) group by g.id;\r\n" + 
	 		"");
	    query.setLong("voucherid", voucherid);
	    rows = query.list();
	   
	    if(rows != null && !rows.isEmpty())
	    {
	    	for(Object[] element : rows)
	    	{
	    		if(element[1] !=null)
 	    		{
	    			totaltax=null != element[1] ? new BigDecimal(element[1].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN);
 	    		}
	    	}
	    }
	    
	 	}
	 	catch(Exception e) {e.printStackTrace();}
		return totaltax;
 }

 
	private Long getPayId(String voucherNumber) {
 	SQLQuery query =  null;
 	List<Object[]> rows = null;
 	Long deducvh=0L;
 	try
 	{
 		 query = this.persistenceService.getSession().createSQLQuery("select id,payvhid,billnumber from miscbilldetail m where m.billvhid in (select v.id from voucherheader v where v.vouchernumber =:vouchernumber)");
 	    query.setString("vouchernumber", voucherNumber);
 	    rows = query.list();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		if(element[1] !=null)
 	    		{
 	    			deducvh= Long.parseLong(element[1].toString());
 	    		}
 	    		else
 	    		{
 	    			deducvh= 0L;
 	    		}
 	    		
 	    	}
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return deducvh;
 }

	private List<Miscbilldetail>   getbillnum(String voucherNumber) {
	 	SQLQuery query =  null;
	 	List<Object[]> rows = null;
	 	String deducvh="";
	 	
		List<Miscbilldetail> miscbilldetailList=new ArrayList<>();
	
	 	
	 	try
	 	{
	 		 query = this.persistenceService.getSession().createSQLQuery("select m.id,m.billnumber,m.paidamount,m.payvhid from miscbilldetail m where m.billvhid in (select v.id from voucherheader v where v.vouchernumber =:vouchernumber)");
	 	    query.setString("vouchernumber", voucherNumber);
	 	    rows = query.list();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		Miscbilldetail miscbilldetail=new Miscbilldetail();
	 	    		if(element[3] !=null)
	 	    		{
	 	    			deducvh= getBPVNumber(element[3].toString());
	 	    			miscbilldetail.setBillnumber(deducvh);
	 	    			miscbilldetail.setId(Long.valueOf(element[3].toString()));
	 	    		}
	 	    		else
	 	    		{
	 	    			deducvh= "";
	 	    		}
	 	    		if(element[2] !=null)
	 	    		{
	 	    			deducvh= element[2].toString();
	 	    			miscbilldetail.setPaidamount(null != element[2] ? new BigDecimal(element[2].toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN)
	 		 	                        : BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_EVEN));
	 	    		}
	 	    		else
	 	    		{
	 	    			deducvh= "";
	 	    		}
	 	    		
	 	    		miscbilldetailList.add(miscbilldetail);
	 	    		
	 	    	}
	 	    }
	 	}catch (Exception e) {
				e.printStackTrace();
			}
		    return miscbilldetailList;
	 }	
	
	
	private String  getBPVNumber(String voucherNumber) {
	 	SQLQuery query =  null;
	 	List<Object[]> rows = null;
	 	String deducvh="";
	 	Long pavhid=0L;
	 	if(voucherNumber.equals(""))
	 	{
	 		pavhid=0L;
	 	}
	 	else {
	 		pavhid=Long.valueOf(voucherNumber);
	 		}
	 	
		List<Miscbilldetail> miscbilldetailList=new ArrayList<>();
	
	 	
	 	try
	 	{
	 		 query = this.persistenceService.getSession().createSQLQuery(" select v2.id,v2.vouchernumber from voucherheader v2 where v2.id =:pavhid");
	 	    query.setLong("pavhid", pavhid);
	 	    rows = query.list();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		//Miscbilldetail miscbilldetail=new Miscbilldetail();
	 	    		if(element[1] !=null)
	 	    		{
	 	    			deducvh= element[1].toString();
	 	    			//miscbilldetail.setBillnumber(deducvh);
	 	    		}
	 	    		else
	 	    		{
	 	    			deducvh= "";
	 	    		}
	 	    		
	 	    		
	 	    		//miscbilldetailList.add(miscbilldetail);
	 	    		
	 	    	}
	 	    }
	 	}catch (Exception e) {
				e.printStackTrace();
			}
		    return deducvh;
	 }	
	
	
	
	private String getPartyName(String vouchernumber) {
	 	SQLQuery query =  null;
	 	List<Object[]> rows = null;
	 	String deducvh="";
	 	Long deailskey=0L;
	 	
	 	try
	 	{
	 		 query = this.persistenceService.getSession().createSQLQuery(" select actd.id,actd.detailname from accountdetailkey actd where actd.detailkey in (select glt.detailkeyid from generalledgerdetail glt where glt.generalledgerid in (select gl.id from generalledger gl where gl.voucherheaderid in (select v.id from voucherheader v where v.vouchernumber =:vouchernumber)))\r\n" + 
	 		 		"");
	 	    query.setString("vouchernumber", vouchernumber);
	 	    rows = query.list();
	 	    
	 	    if(rows != null && !rows.isEmpty())
	 	    {
	 	    	for(Object[] element : rows)
	 	    	{
	 	    		if(element[1] !=null)
	 	    		{
	 	    			deducvh= element[1].toString();
	 	    		}
	 	    		else
	 	    		{
	 	    			deducvh= "";
	 	    		}
	 	    		
	 	    	}
	 	    }
	 	}catch (Exception e) {
				e.printStackTrace();
			}
		    return deducvh;
	 }	
	
	
	private Long getVoucId(String voucherNumber) {
 	SQLQuery query =  null;
 	List<Object[]> rows = null;
 	Long deducvh=0L;
 	try
 	{
 		 query = this.persistenceService.getSession().createSQLQuery("select vh.id,vh.vouchernumber from voucherheader vh where vh.vouchernumber=:vouchernumber");
 	    query.setString("vouchernumber", voucherNumber);
 	    rows = query.list();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		if(element[0] !=null)
 	    		{
 	    			deducvh= Long.parseLong(element[0].toString());
 	    		}
 	    		else
 	    		{
 	    			deducvh= 0L;
 	    		}
 	    		
 	    	}
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return deducvh;
 }

	private Long getDeducVoucId(String voucherNumber) {
 	SQLQuery query =  null;
 	List<Object[]> rows = null;
 	Long deducvh=0L;
 	try
 	{
 		 query = this.persistenceService.getSession().createSQLQuery("select dvm.ph_id,(select vouchernumber from voucherheader v where v.id=dvm.ph_id) from deduc_voucher_mpng dvm where dvm.vh_id in (select vh.id from voucherheader vh where vh.vouchernumber=:vouchernumber)");
 	    query.setString("vouchernumber", voucherNumber);
 	    rows = query.list();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		if(element[0] !=null)
 	    		{
 	    			deducvh= Long.parseLong(element[0].toString());
 	    		}
 	    		else
 	    		{
 	    			deducvh= 0L;
 	    		}
 	    		
 	    	}
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return deducvh;
 }

	private String getDeducVoucherNumber(String voucherNumber) {
 	SQLQuery query =  null;
 	List<Object[]> rows = null;
 	String deducvh="";
 	try
 	{
 		 query = this.persistenceService.getSession().createSQLQuery("select dvm.id,(select vouchernumber from voucherheader v where v.id=dvm.ph_id) from deduc_voucher_mpng dvm where dvm.vh_id in (select vh.id from voucherheader vh where vh.vouchernumber=:vouchernumber)");
 	    query.setString("vouchernumber", voucherNumber);
 	    rows = query.list();
 	    
 	    if(rows != null && !rows.isEmpty())
 	    {
 	    	for(Object[] element : rows)
 	    	{
 	    		if(element[1] !=null)
 	    		{
 	    			deducvh= element[1].toString();
 	    		}
 	    		else
 	    		{
 	    			deducvh= "";
 	    		}
 	    		
 	    	}
 	    }
 	}catch (Exception e) {
			e.printStackTrace();
		}
	    return deducvh;
 }

	/*
  * Get remittance payment detail for the voucher Below lines to get the cheque and cheque date for the voucher /* In case
  * where for single payment multiple cheque are assigned we use chqdelimitSP / single slash separate cheque nos In case where
  * for a voucher multiple BPVs are issued and for the BPVs different cheques are issued we seperate them with chqdelimitDP //
  * double slash
  */
 private void getRemittancePaymentDetail(final BillRegisterReportBean billRegReport) {
	 
	 StringBuffer getRemiitPaymentVoucherQry = new StringBuffer("");
	 List<Integer> cancelledChequeStatus = new ArrayList<Integer>();
	 List<String> chequeStatusCheckList = new ArrayList<String>();
	   chequeStatusCheckList.add(FinancialConstants.INSTRUMENT_DISHONORED_STATUS);
       chequeStatusCheckList.add(FinancialConstants.INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS);
       chequeStatusCheckList.add(FinancialConstants.INSTRUMENT_SURRENDERED_STATUS);
       chequeStatusCheckList.add(FinancialConstants.INSTRUMENT_CANCELLED_STATUS);
	 
	  getRemiitPaymentVoucherQry.append("select  distinct rm from EgRemittance rm join rm.egRemittanceDetail rdtl  " +
              "where rdtl.egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.voucherNumber =?" +
              "and rdtl.egRemittanceGldtl.generalledgerdetail.generalLedgerId.voucherHeaderId.status!=?" +
              " and rm.voucherheader.status!=?")
              .append(" order by rm.voucherheader.id");
	  
	  final Query query = persistenceService.getSession().createQuery("select status.id from EgwStatus status where " +
              "status.description in (:surrenderedList) and status.moduletype='Instrument'");
      query.setParameterList("surrenderedList", chequeStatusCheckList);
      cancelledChequeStatus = query.list();
	 
     if (LOGGER.isDebugEnabled())
         LOGGER.debug("...........Getting Remitance Payment details........");
     List<EgRemittance> remittancePaymentItem = new ArrayList<EgRemittance>();
     final StringBuffer remmitPaymentVoucherNumber = new StringBuffer("");

     StringBuffer remittanceChequeNoAndDate = new StringBuffer("");
     Long paymentVhId = null;
     List<InstrumentVoucher> instrumentVoucherList = new ArrayList<InstrumentVoucher>();

     if (billRegReport.getVoucherNumber() != null) {

         remittancePaymentItem = persistenceService.findAllBy(getRemiitPaymentVoucherQry.toString()
                 , billRegReport.getVoucherNumber(), FinancialConstants.CANCELLEDVOUCHERSTATUS,
                 FinancialConstants.CANCELLEDVOUCHERSTATUS);

         if (remittancePaymentItem.size() > 0) {
             paymentVhId = remittancePaymentItem.get(0).getVoucherheader().getId();
             remittanceChequeNoAndDate = new StringBuffer("");
             for (int i = 0; i < remittancePaymentItem.size(); i++) {

                 // if(remittancePaymentItem.get(i).getVoucherheader().getStatus())
                 remmitPaymentVoucherNumber.append(remittancePaymentItem.get(i).getVoucherheader().getVoucherNumber() + "|");
                 final Query qry = persistenceService.getSession().createQuery(
                         "from InstrumentVoucher iv where iv.voucherHeaderId.id=:vhId and" +
                         " iv.instrumentHeaderId.statusId.id not in(:cancelledChequeList)");
                 qry.setLong("vhId", remittancePaymentItem.get(i).getVoucherheader().getId());
                 qry.setParameterList("cancelledChequeList", cancelledChequeStatus);
                 instrumentVoucherList = qry.list();

                 if (instrumentVoucherList.size() > 0)
                     for (final InstrumentVoucher inst : instrumentVoucherList)
                         if (!StringUtils.isEmpty(remittanceChequeNoAndDate.toString())) {
                             if (paymentVhId != null && paymentVhId.equals(inst.getVoucherHeaderId().getId()))
                                 remittanceChequeNoAndDate
                                 .append(chqdelimitSP)
                                 .append(inst.getInstrumentHeaderId().getInstrumentNumber())
                                 .append(" ")
                                 .append(inst.getInstrumentHeaderId().getInstrumentDate() != null ? DDMMYYYYFORMATS
                                         .format(inst.getInstrumentHeaderId().getInstrumentDate()) : "");
                             else
                                 remittanceChequeNoAndDate
                                 .append(chqdelimitDP)
                                 .append(inst.getInstrumentHeaderId().getInstrumentNumber())
                                 .append(" ")
                                 .append(inst.getInstrumentHeaderId().getInstrumentDate() != null ? DDMMYYYYFORMATS
                                         .format(inst.getInstrumentHeaderId().getInstrumentDate()) : "");
                         } else
                             remittanceChequeNoAndDate
                             .append(inst.getInstrumentHeaderId().getInstrumentNumber())
                             .append(" ")
                             .append(inst.getInstrumentHeaderId().getInstrumentDate() != null ? DDMMYYYYFORMATS
                                     .format(inst.getInstrumentHeaderId().getInstrumentDate()) : "");
                 paymentVhId = remittancePaymentItem.get(i).getVoucherheader().getId();
             }
         }
         billRegReport.setRemittanceVoucherNumber(remmitPaymentVoucherNumber.length() > 0 ? remmitPaymentVoucherNumber
                 .substring(0, remmitPaymentVoucherNumber.length() - 1) : " ");
         billRegReport.setRemittanceChequeNumberAndDate(remittanceChequeNoAndDate.toString());
     }
 } 
 
    private String getMessageByStatus(final CVoucherHeader voucherHeader, final String approverName, final String nextDesign,
            final String workFlowAction) {
        String message;

        if (FinancialConstants.PREAPPROVEDVOUCHERSTATUS.equals(voucherHeader.getStatus()))
            message = messageSource.getMessage("msg.journal.voucher.create.success",
                    new String[] { voucherHeader.getVoucherNumber(), approverName, nextDesign }, null);
        else if (FinancialConstants.CREATEDVOUCHERSTATUS.equals(voucherHeader.getStatus()))
            message = messageSource.getMessage("msg.journal.voucher.approved.success",
                    new String[] { voucherHeader.getVoucherNumber() }, null);
        else if (FinancialConstants.WORKFLOW_STATE_CANCELLED.equals(workFlowAction))
            message = messageSource.getMessage("msg.journal.voucher.cancel",
                    new String[] { voucherHeader.getVoucherNumber() }, null);
        else
            message = messageSource.getMessage("msg.journal.voucher.reject",
                    new String[] { voucherHeader.getVoucherNumber(), approverName, nextDesign }, null);

        return message;
    }
}