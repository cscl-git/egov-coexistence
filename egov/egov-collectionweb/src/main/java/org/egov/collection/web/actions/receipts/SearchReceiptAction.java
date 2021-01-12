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
package org.egov.collection.web.actions.receipts;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.codehaus.jackson.map.ObjectMapper;
import org.egov.collection.bean.ReceiptReportBean;
import org.egov.collection.bean.SubDivison;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.eis.service.AssignmentService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.BillDetailAdditional;
import org.egov.infra.microservice.models.BusinessService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.models.RemittanceDepositWorkDetail;
import org.egov.infra.microservice.models.RemittanceResponseDepositWorkDetails;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.web.struts.actions.SearchFormAction;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQueryHQL;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ParentPackage("egov")
@Results({
        @Result(name = SearchReceiptAction.SUCCESS, location = "searchReceipt.jsp"),
        @Result(name = "XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
                "application/xls", "contentDisposition", "no-cache;filename=ReceiptReport.xls" }),
        @Result(name = SearchReceiptAction.DAYBOOKREPORT, location = "dayBookReport.jsp")
})
public class SearchReceiptAction extends SearchFormAction {

    private static final long serialVersionUID = 1L;
    protected static final String DAYBOOKREPORT = "dayBookReport";
    private String serviceTypeId = null;
    private String serviceTypeIdforExcel =null;
    private String deptId = null;
    private Long userId = (long) -1;
    private String instrumentType;
    private String receiptNumber;
    private String collectedBy="";
    private String modeOfPayment="";
    private String searchAmount="0";
    private Date fromDate;
    private Date toDate;
    private Integer searchStatus = -1;
    private String target = "new";
    private String manualReceiptNumber;
    private List resultList = new ArrayList();
    private String serviceClass = "-1";
    private TreeMap<String, String> serviceClassMap = new TreeMap<String, String>();
    private CollectionsUtil collectionsUtil;
    private Integer branchId;
    private String reportId;
    private ReportService reportService;
    private InputStream inputStream;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    protected EgovMasterDataCaching masterDataCache;

    @Autowired
    private MicroserviceUtils microserviceUtils;
    
    private String collectionVersion;
    
    private String subdivison;
    
    @Autowired
	private AppConfigValueService appConfigValuesService;

    @Override
    public Object getModel() {
        return null;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(final String serviceType) {
        serviceTypeId = serviceType;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(final String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(final String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    Map<String,String> serviceCategoryNames = new HashMap<String,String>();
    Map<String,Map<String,String>> serviceTypeMap = new HashMap<>();
    @Action(value = "/receipts/searchReceipt-reset")
    public String reset() {
        setPage(1);
        serviceTypeId = null;
        userId = (long) -1;
        receiptNumber = "";
        fromDate = null;
        toDate = null;
        instrumentType = "";
        searchStatus = -1;
        manualReceiptNumber = "";
        serviceClass = "-1";
        branchId = -1;
        return SUCCESS;
    }

    @Override
    public void prepare() {
        super.prepare();
        // if(searchResult==null)
        // searchResult = new EgovPaginatedList();
        this.getServiceCategoryList();
        setupDropdownDataExcluding();
        // addDropdownData("instrumentTypeList",
        // getPersistenceService().findAllBy("from InstrumentType i where i.isActive = true order by type"));
        // addDropdownData("userList",
        // getPersistenceService().findAllByNamedQuery(CollectionConstants.QUERY_CREATEDBYUSERS_OF_RECEIPTS));

        // serviceClassMap.putAll(CollectionConstants.SERVICE_TYPE_CLASSIFICATION);
        // serviceClassMap.remove(CollectionConstants.SERVICE_TYPE_PAYMENT);
        // addDropdownData("serviceTypeList", Collections.EMPTY_LIST);
//        addDropdownData("businessCategorylist", microserviceUtils.getBusinessCategories());
        addDropdownData("serviceTypeList", microserviceUtils.getBusinessService(null));
        addDropdownData("departmentList", masterDataCache.get("egi-department"));
        // addDropdownData("bankBranchList", collectionsUtil.getBankCollectionBankBranchList());
        List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"receipt_sub_divison");
        List<SubDivison> subdivisonList=new ArrayList<SubDivison>();
        SubDivison subdivison=null;
        for(AppConfigValues value:appConfigValuesList)
        {
        	subdivison = new SubDivison();
        	subdivison.setSubdivisonCode(value.getValue());
        	subdivison.setSubdivisonName(value.getValue());
        	subdivisonList.add(subdivison);
        }
        addDropdownData("subdivisonList", subdivisonList);
    }
    private void getServiceCategoryList() {
        List<BusinessService> businessService = microserviceUtils.getBusinessService(null);
        for(BusinessService bs : businessService){
            String[] splitServName = bs.getBusinessService().split(Pattern.quote("."));
            String[] splitSerCode = bs.getCode().split(Pattern.quote("."));
            if(splitServName.length==2 && splitSerCode.length == 2){
                if(!serviceCategoryNames.containsKey(splitSerCode[0])){
                    serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
                }
                if(serviceTypeMap.containsKey(splitSerCode[0])){
                    Map<String, String> map = serviceTypeMap.get(splitSerCode[0]);
                    map.put(splitSerCode[1], splitServName[1]);
                    serviceTypeMap.put(splitSerCode[0], map);
                }else{
                    Map<String, String> map = new HashMap<>();
                    map.put(splitSerCode[1], splitServName[1]);
                    serviceTypeMap.put(splitSerCode[0],map);
                }
            }else{
                serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
            }
        }
    }
    @Override
    @Action(value = "/receipts/searchReceipt")
    public String execute() {
        return SUCCESS;
    }

    
    @Action(value = "/receipts/daybookReport")
    public String dayBookReport() {
        return DAYBOOKREPORT;
    }

    public List getReceiptStatuses() {
        return persistenceService.findAllBy(
                "from EgwStatus s where moduletype=? and code != ? order by description",
                ReceiptHeader.class.getSimpleName(), CollectionConstants.RECEIPT_STATUS_CODE_PENDING);
    }

    @Override
    @Action(value = "/receipts/searchReceipt-search")
    public String search() {
        target = "searchresult";
        collectionVersion = ApplicationThreadLocals.getCollectionVersion();

        List<ReceiptHeader> receiptList = new ArrayList<>();
        System.out.println("receipt :::"+ getReceiptNumber());
        String type="search";
        if(getServiceTypeId() != null && getServiceTypeId().equalsIgnoreCase("-1"))
        {
        	setServiceTypeId(null);
        }
        List<Receipt> receipts = microserviceUtils.searchRecieptsFinance("MISCELLANEOUS", getFromDate(), getToDate(), getServiceTypeId(),
                (getReceiptNumber() != null && !getReceiptNumber().isEmpty() && !"".equalsIgnoreCase(getReceiptNumber()))
                        ? getReceiptNumber() : null,type);
        
        
        for (Receipt receipt : receipts) {

            for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                for (BillDetail billDetail : bill.getBillDetails()) {

                    ReceiptHeader receiptHeader = new ReceiptHeader();
                    receiptHeader.setPaymentId(receipt.getPaymentId());
                    receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
                    populateBifurcationAmount(receiptHeader);
                    receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
                    receiptHeader.setService(microserviceUtils.getBusinessServiceNameByCode(billDetail.getBusinessService()));
                    receiptHeader.setReferencenumber(billDetail.getBillNumber());
                    receiptHeader.setReferenceDesc(bill.getNarration());
                    receiptHeader.setPayeeAddress(bill.getPayerAddress());
                    receiptHeader.setPaidBy(bill.getPaidBy());
                    receiptHeader.setPayeeName(bill.getPayerName());
                    System.out.println("subdivison ::: "+receipt.getSubdivison());
                    System.out.println("gst ::: "+receipt.getGstNo());
                    receiptHeader.setSubdivison(receipt.getSubdivison());
                    receiptHeader.setGstno(receipt.getGstNo());
                    //receiptHeader.setTotalAmount(billDetail.getTotalAmount());
                    receiptHeader.setCurretnStatus(receipt.getPaymentStatus());
                    receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
                    if (null != billDetail.getManualReceiptNumber()) {
                        receiptHeader.setManualreceiptnumber(billDetail.getManualReceiptNumber());
                        receiptHeader.setG8data(billDetail.getManualReceiptNumber());
                    }
                    if (billDetail.getManualReceiptDate() != null && billDetail.getManualReceiptDate() != 0) {
                        receiptHeader.setManualreceiptdate(new Date(billDetail.getManualReceiptDate()));
                        if (null != billDetail.getManualReceiptNumber()) {
                            receiptHeader.setG8data(billDetail.getManualReceiptNumber()+"/"+new Date(billDetail.getManualReceiptDate()).toString()); 
                        }
                        else
                            receiptHeader.setG8data(new Date(billDetail.getManualReceiptDate()).toString());
                    }
                    receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());

                    JsonNode jsonNode = billDetail.getAdditionalDetails();
                    BillDetailAdditional additional = null;
                    try {
                        if (null != jsonNode)
                            additional = (BillDetailAdditional) new ObjectMapper().readValue(jsonNode.toString(),
                                    BillDetailAdditional.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != additional) {
//                        if (null != additional.getBusinessReason()) {
//                            if (additional.getBusinessReason().contains("-")) {
//                                receiptHeader.setService(additional.getBusinessReason().split("-")[0]);
//                            } else {
//                                receiptHeader.setService(additional.getBusinessReason());
//                            }
//                        }

                        //if (null != additional.getNarration())
                          //  receiptHeader.setReferenceDesc(additional.getNarration());
                        //if (null != additional.getPayeeaddress())
                          //  receiptHeader.setPayeeAddress(additional.getPayeeaddress());
                    }

                    receiptList.add(receiptHeader);

                    List<ReceiptHeader> beerDrinkers = receiptList.stream()
                    	    .filter(p -> p.getModOfPayment() == "").collect(Collectors.toList());
                }
            }

        }

        if (searchResult == null) {
            Page page = new Page<ReceiptHeader>(1, receiptList.size(), receiptList);
            searchResult = new EgovPaginatedList(page, receiptList.size());
        } else {
            searchResult.getList().clear();
            searchResult.getList().addAll(receiptList);
        }

        resultList = searchResult.getList();
        return SUCCESS;
    }

    
    @Action(value = "/receipts/searchReceipt-searchDayBookReport")
    public String searchDayBookReport() {
        target = "searchresult";
        collectionVersion = ApplicationThreadLocals.getCollectionVersion();

        List<ReceiptHeader> receiptList = new ArrayList<>();
        System.out.println("from date ::"+getFromDate());
        System.out.println("to date :::"+getToDate());
        System.out.println("getServiceTypeId() :::"+getServiceTypeId());
        if(getServiceTypeId().equalsIgnoreCase("")) {
        setServiceTypeId(null);
        }
        serviceTypeIdforExcel=getServiceTypeId();
        List<Receipt> receipts = microserviceUtils.searchReciepts("MISCELLANEOUS", getFromDate(), getToDate(), getServiceTypeId(),getDeptId(),
                (getReceiptNumber() != null && !getReceiptNumber().isEmpty() && !"".equalsIgnoreCase(getReceiptNumber()))
                        ? getReceiptNumber() : null);
      
       System.out.println("XXXXX");
        List<RemittanceDepositWorkDetail> remittanceResponselist=null;
       
        
        BigDecimal totalReciptAmount=BigDecimal.ZERO;
        BigDecimal totalReciptAmount2= BigDecimal.ZERO; 
        totalReciptAmount2= totalReciptAmount.setScale(2, BigDecimal.ROUND_DOWN);
        System.out.println("check total amount="+totalReciptAmount2);
        
        
        BigDecimal totalDepositAmount=BigDecimal.ZERO;
        BigDecimal totalDepositAmount2=BigDecimal.ZERO;
        totalDepositAmount2= totalDepositAmount2.setScale(2, BigDecimal.ROUND_DOWN);
        
        
        if(receipts!=null && !receipts.isEmpty()) {
         remittanceResponselist =  getDepositWork(receipts);
        	}                   

        for (Receipt receipt : receipts) {

            for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                for (BillDetail billDetail : bill.getBillDetails()) {
                	
                	
                	

                    ReceiptHeader receiptHeader = new ReceiptHeader();
                    receiptHeader.setPaymentId(receipt.getPaymentId());
                    receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
                    populateBifurcationAmount(receiptHeader);
                    receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
                    receiptHeader.setService(microserviceUtils.getBusinessServiceNameByCode(billDetail.getBusinessService()));
                    receiptHeader.setReferencenumber("");
                    receiptHeader.setReferenceDesc(bill.getNarration());
                    receiptHeader.setPayeeName(bill.getPayerName());
                    receiptHeader.setPayeeAddress(bill.getPayerAddress());
                    receiptHeader.setPaidBy(bill.getPaidBy().split("&")[0]);
                    if(subdivison != null && !subdivison.isEmpty() && !subdivison.equalsIgnoreCase("-1") && !subdivison.equalsIgnoreCase(receipt.getSubdivison()))
                    {
                    	continue;
                    }
                    System.out.println("subdivison ::: "+receipt.getSubdivison());
                    System.out.println("gst ::: "+receipt.getGstNo());
                    receiptHeader.setSubdivison(receipt.getSubdivison());
                    receiptHeader.setGstno(receipt.getGstNo());
                    //receiptHeader.setTotalAmount(billDetail.getTotalAmount());
                   // totalReciptAmount2= totalReciptAmount2.add(billDetail.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
                    
                    //receiptHeader.setTotalreciptAmount(totalReciptAmount2);
                    receiptHeader.setCurretnStatus(billDetail.getStatus());
                    receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
                    if (null != billDetail.getManualReceiptNumber()) {
                        receiptHeader.setManualreceiptnumber(billDetail.getManualReceiptNumber());
                        receiptHeader.setG8data(billDetail.getManualReceiptNumber());
                    }
                    if (billDetail.getManualReceiptDate() != null && billDetail.getManualReceiptDate() != 0) {
                        receiptHeader.setManualreceiptdate(new Date(billDetail.getManualReceiptDate()));
                        if (null != billDetail.getManualReceiptNumber()) {
                            receiptHeader.setG8data(billDetail.getManualReceiptNumber()+"/"+new Date(billDetail.getManualReceiptDate()).toString()); 
                        }
                        else
                            receiptHeader.setG8data(new Date(billDetail.getManualReceiptDate()).toString());
                    }
                    receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());
                    EmployeeInfo empInfo = null;
                    try
                    {
                    	empInfo =microserviceUtils.getEmployee(Long.parseLong(receipt.getAuditDetails().getCreatedBy()), null, null, null).get(0);
                    }catch (Exception e) {
					}
                     
                    if (null != empInfo && empInfo.getUser().getUserName() != null && !empInfo.getUser().getUserName().isEmpty())
                    {
                    	receiptHeader.setCreatedUser(empInfo.getUser().getName());
                    }
                    else
                    {
                    	receiptHeader.setCreatedUser("");
                    }
                        
                    JsonNode jsonNode = billDetail.getAdditionalDetails();
                    BillDetailAdditional additional = null;
                    try {
                        if (null != jsonNode)
                            additional = (BillDetailAdditional) new ObjectMapper().readValue(jsonNode.toString(),
                                    BillDetailAdditional.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != additional) {
//                        if (null != additional.getBusinessReason()) {
//                            if (additional.getBusinessReason().contains("-")) {
//                                receiptHeader.setService(additional.getBusinessReason().split("-")[0]);
//                            } else {
//                                receiptHeader.setService(additional.getBusinessReason());
//                            }
//                        }

                        if (null != additional.getNarration())
                            receiptHeader.setReferenceDesc(additional.getNarration());
                        if (null != additional.getPayeeaddress())
                            receiptHeader.setPayeeAddress(additional.getPayeeaddress());
                    }
                    
                    //add Work Deposit
                    
                    if(remittanceResponselist!=null && remittanceResponselist!=null) {
	                   for (RemittanceDepositWorkDetail remittance : remittanceResponselist) {
	                    	
	                    	if(billDetail.getReceiptNumber().equals(remittance.getReciptNumber())) 
	                    	{
	                    		receiptHeader.setReferencenumber(remittance.getReferenceNumber());
	                    		receiptHeader.setRrDate(new Date(remittance.getReferenceDate()));
	                    		receiptHeader.setDepositAmount(remittance.getCreditAmount());
	                    		
	                    		BigDecimal depositAmount = new BigDecimal(remittance.getCreditAmount());
	                    		receiptHeader.setTotalDepositAmount(depositAmount);
	                    		//totalDepositAmount2=totalDepositAmount2.add(depositAmount.setScale(4, BigDecimal.ROUND_DOWN));
	                    		
	                    		
	                    	}
	                    	else
	                    	{
	                    		receiptHeader.setReferencenumber("");
	                    	}
	                    		
						}
                    }

                    
                    receiptList.add(receiptHeader);

                    
                }
            }

        }
        ///Bhushan :added filter to list
       List<ReceiptHeader> receiptListfilterList=receiptList;
       List<ReceiptHeader> receiptListnew=new ArrayList<>();
      // receiptList.clear();
       
       
        for (ReceiptHeader receiptHeader2 : receiptListfilterList) {
			
			
        	 BigDecimal searchamt = new BigDecimal(searchAmount);
             
             BigDecimal  searchamtbig = receiptHeader2.getTotalAmount().setScale(4, BigDecimal.ROUND_DOWN);
             searchamt = searchamt.setScale(4, BigDecimal.ROUND_DOWN);
        	
        	
        
        if(searchAmount!=null && !searchAmount.equals("0")){
        	
        	if(collectedBy!=null && !collectedBy.equals("")){
        		
        		if(modeOfPayment!=null && !modeOfPayment.equals("")) {
        			
        			if(searchamtbig.equals(searchamt) && (receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()) && receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
                    {
        				if(receiptHeader2.getTotalDepositAmount()!=null) {
                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				if(receiptHeader2.getTotalAmount()!=null) {
                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				receiptListnew.add(receiptHeader2);
                   
                    }
        		}
        		else {
        			if(searchamtbig.equals(searchamt) && (receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()))
                    {
        				if(receiptHeader2.getTotalDepositAmount()!=null) {
                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				if(receiptHeader2.getTotalAmount()!=null) {
                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				receiptListnew.add(receiptHeader2);
                   
                    }
        			
        		}
        	}
        	else if(modeOfPayment!=null && !modeOfPayment.equals("")) {
    			
	        		if(searchamtbig.equals(searchamt) && receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
	                {
	        			if(receiptHeader2.getTotalDepositAmount()!=null) {
                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				if(receiptHeader2.getTotalAmount()!=null) {
                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				receiptListnew.add(receiptHeader2);
	               
	                }
	    		}else{
	        		
	    			if(searchamtbig.equals(searchamt))
	                {
	    				if(receiptHeader2.getTotalDepositAmount()!=null) {
                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				if(receiptHeader2.getTotalAmount()!=null) {
                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
        				}
        				receiptListnew.add(receiptHeader2);
	               
	                }
	        	}
        }
        else if(collectedBy!=null && !collectedBy.equals("")) {
        	
			if(modeOfPayment!=null && !modeOfPayment.equals("")) {
			    			
			        		if((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()) && receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
			                {
			        			if(receiptHeader2.getTotalDepositAmount()!=null) {
		                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
                }
		        				if(receiptHeader2.getTotalAmount()!=null) {
		                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
		        				}
		        				receiptListnew.add(receiptHeader2);
			               
			                }
			    		}else {
			    			
			    			if((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()))
			                {
			    				if(receiptHeader2.getTotalDepositAmount()!=null) {
		                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
		        				}
		        				if(receiptHeader2.getTotalAmount()!=null) {
		                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
		        				}
		        				receiptListnew.add(receiptHeader2);
			               
			                }
			    		}
        	
        }
        else if(modeOfPayment!=null && !modeOfPayment.equals("")){
        	
        	if(receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
            {
        		if(receiptHeader2.getTotalDepositAmount()!=null) {
            		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
				}
				if(receiptHeader2.getTotalAmount()!=null) {
                totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
				}
				receiptListnew.add(receiptHeader2);
           
            }
        	
            }
        else {

        	if(receiptHeader2.getTotalDepositAmount()!=null) {
        		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
			}
			if(receiptHeader2.getTotalAmount()!=null) {
            totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
        }
			receiptListnew.add(receiptHeader2);
        }
        
       
        
        
        }
        
        
        
        
     //   totalReciptAmount2=totalReciptAmount;
      //  totalDepositAmount2=totalDepositAmount;
        for (ReceiptHeader receiptHeadern : receiptListnew) {
        	receiptHeadern.setTotalreciptAmount(totalReciptAmount2);
        	receiptHeadern.setTotalDepositAmount(totalDepositAmount2);
		}
        

        if (searchResult == null) {
            Page page = new Page<ReceiptHeader>(1, receiptListnew.size(), receiptListnew);
            searchResult = new EgovPaginatedList(page, receiptListnew.size());
        } else {
            searchResult.getList().clear();
            searchResult.getList().addAll(receiptListnew);
        }

        resultList = searchResult.getList();
        return DAYBOOKREPORT;
    }

    
    
    
    

	 private void populateBifurcationAmount(ReceiptHeader receiptHeader) {
		 SQLQuery query =  null;
	    	List<Object[]> rows = null;
	    	BigDecimal pAmount=new BigDecimal("0");
	    	BigDecimal gstAmount=new BigDecimal("0");
	    	BigDecimal totalAmount=new BigDecimal("0");
	    	try
	    	{
	    		 query = this.persistenceService.getSession().createSQLQuery("select gl.id,gl.glcode,gl.debitamount,gl.creditamount from generalledger gl where voucherheaderid =(select vmis.voucherheaderid from vouchermis vmis where vmis.reciept_number =:receipt_no)");
	    	    query.setString("receipt_no", receiptHeader.getReceiptnumber());
	    	    rows = query.list();
	    	    
	    	    if(rows != null && !rows.isEmpty())
	    	    {
	    	    	for(Object[] element : rows)
	    	    	{
	    	    		if(!element[3].toString().equalsIgnoreCase("0.00"))
	    	    		{
	    	    			if(element[1].toString().equalsIgnoreCase("3502020") || element[1].toString().equalsIgnoreCase("3502019"))
	    	    			{
	    	    				gstAmount=gstAmount.add(new BigDecimal(element[3].toString()));
	    	    			}
	    	    			else
	    	    			{
	    	    				pAmount=pAmount.add(new BigDecimal(element[3].toString()));
	    	    			}
	    	    		}
	    	    		else
	    	    		{
	    	    			totalAmount=totalAmount.add(new BigDecimal(element[2].toString()));
	    	    		}
	    	    		
	    	    	}
	    	    	receiptHeader.setPrincipalAmount(pAmount);
	    	    	receiptHeader.setGstAmount(gstAmount);
	    	    	receiptHeader.setTotalAmount(totalAmount);
	    	    }
	    	}catch (Exception e) {
				e.printStackTrace();
			}
		
	}

	@Action(value = "/receipts/searchReceipt-downloadDayBookReport")
    public String downloadDayBookReport() {
        target = "searchresult";
        collectionVersion = ApplicationThreadLocals.getCollectionVersion();
        System.out.println("from date ::"+getFromDate());
        System.out.println("to date :::"+getToDate());
        System.out.println("getServiceTypeId() :::"+getServiceTypeId());
        if(getServiceTypeId().equalsIgnoreCase("") || getServiceTypeId().contains("0") || getServiceTypeId().contains("-1")) {
            setServiceTypeId(null);
            }
        List<ReceiptHeader> receiptList = new ArrayList<>();
        List<Receipt> receipts = microserviceUtils.searchReciepts("MISCELLANEOUS", getFromDate(), getToDate(), getServiceTypeId(),getDeptId(),
                (getReceiptNumber() != null && !getReceiptNumber().isEmpty() && !"".equalsIgnoreCase(getReceiptNumber()))
                        ? getReceiptNumber() : null);
      
        
        List<RemittanceDepositWorkDetail> remittanceResponselist=null;
        
        BigDecimal totalReciptAmount=BigDecimal.ZERO;
        BigDecimal totalReciptAmount2= BigDecimal.ZERO; 
        totalReciptAmount2= totalReciptAmount.setScale(2, BigDecimal.ROUND_DOWN);
        
        
        BigDecimal totalDepositAmount=BigDecimal.ZERO;
        BigDecimal totalDepositAmount2=BigDecimal.ZERO;
        totalDepositAmount2= totalDepositAmount2.setScale(2, BigDecimal.ROUND_DOWN);
        if(receipts!=null && !receipts.isEmpty()) {
         remittanceResponselist =  getDepositWork(receipts);
        	}                   


        for (Receipt receipt : receipts) {

            for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                for (BillDetail billDetail : bill.getBillDetails()) {
                	
                	
                	

                    ReceiptHeader receiptHeader = new ReceiptHeader();
                    receiptHeader.setPaymentId(receipt.getPaymentId());
                    receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
                    populateBifurcationAmount(receiptHeader);
                    receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
                    receiptHeader.setService(billDetail.getBusinessService());
                    receiptHeader.setReferencenumber("");
                    receiptHeader.setReferenceDesc(bill.getNarration());
                    receiptHeader.setPayeeAddress(bill.getPayerAddress());
                    receiptHeader.setPaidBy(bill.getPaidBy().split("&")[0]);
                    if(subdivison != null && !subdivison.isEmpty() && !subdivison.equalsIgnoreCase("-1") && !subdivison.equalsIgnoreCase(receipt.getSubdivison()))
                    {
                    	continue;
                    }
                    receiptHeader.setSubdivison(receipt.getSubdivison());
                    receiptHeader.setGstno(receipt.getGstNo());
                    //receiptHeader.setTotalAmount(billDetail.getTotalAmount());
                   // totalReciptAmount.add(billDetail.getTotalAmount());
                    receiptHeader.setTotalreciptAmount(totalReciptAmount);
                    receiptHeader.setCurretnStatus(billDetail.getStatus());
                    receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
                    if (null != billDetail.getManualReceiptNumber()) {
                        receiptHeader.setManualreceiptnumber(billDetail.getManualReceiptNumber());
                        receiptHeader.setG8data(billDetail.getManualReceiptNumber());
                    }
                    if (billDetail.getManualReceiptDate() != null && billDetail.getManualReceiptDate() != 0) {
                        receiptHeader.setManualreceiptdate(new Date(billDetail.getManualReceiptDate()));
                        if (null != billDetail.getManualReceiptNumber()) {
                            receiptHeader.setG8data(billDetail.getManualReceiptNumber()+"/"+new Date(billDetail.getManualReceiptDate()).toString()); 
                        }
                        else
                            receiptHeader.setG8data(new Date(billDetail.getManualReceiptDate()).toString());
                    }
                    receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());
                    EmployeeInfo empInfo = null;
                    try
                    {
                    	empInfo =microserviceUtils.getEmployee(Long.parseLong(receipt.getAuditDetails().getCreatedBy()), null, null, null).get(0);
                    }catch (Exception e) {
					}
                     
                    if (null != empInfo && empInfo.getUser().getUserName() != null && !empInfo.getUser().getUserName().isEmpty())
                    {
                    	receiptHeader.setCreatedUser(empInfo.getUser().getName());
                    }
                    else
                    {
                    	receiptHeader.setCreatedUser("");
                    }
                    JsonNode jsonNode = billDetail.getAdditionalDetails();
                    BillDetailAdditional additional = null;
                    try {
                        if (null != jsonNode)
                            additional = (BillDetailAdditional) new ObjectMapper().readValue(jsonNode.toString(),
                                    BillDetailAdditional.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != additional) {
//                        if (null != additional.getBusinessReason()) {
//                            if (additional.getBusinessReason().contains("-")) {
//                                receiptHeader.setService(additional.getBusinessReason().split("-")[0]);
//                            } else {
//                                receiptHeader.setService(additional.getBusinessReason());
//                            }
//                        }

                        if (null != additional.getNarration())
                            receiptHeader.setReferenceDesc(additional.getNarration());
                        if (null != additional.getPayeeaddress())
                            receiptHeader.setPayeeAddress(additional.getPayeeaddress());
                    }
                    
                    //add Work Deposit
                    
                    if(remittanceResponselist!=null && remittanceResponselist!=null) {
	                   for (RemittanceDepositWorkDetail remittance : remittanceResponselist) {
	                    	
	                    	if(billDetail.getReceiptNumber().equals(remittance.getReciptNumber())) 
	                    	{
	                    		receiptHeader.setReferencenumber(remittance.getReferenceNumber());
	                    		receiptHeader.setRrDate(new Date(remittance.getReferenceDate()));
	                    		receiptHeader.setDepositAmount(remittance.getCreditAmount());
	                    		
	                    		BigDecimal depositAmount = new BigDecimal(remittance.getCreditAmount());
	                    		receiptHeader.setTotalDepositAmount(depositAmount);
	                    		
	                    	}
	                    	else
	                    	{
	                    		receiptHeader.setReferencenumber("");
	                    	}
	                    		
						}
                    }

                    receiptList.add(receiptHeader);

                }
            }

        }
        
        ///Bhushan
        List<ReceiptHeader> receiptListfilterList=receiptList;
        List<ReceiptHeader> receiptListnew=new ArrayList<>();
       // receiptList.clear();
        
        
         
         for (ReceiptHeader receiptHeader2 : receiptListfilterList) {
 			
 			
         	 BigDecimal searchamt = new BigDecimal(searchAmount);
              
              BigDecimal  searchamtbig = receiptHeader2.getTotalAmount().setScale(4, BigDecimal.ROUND_DOWN);
              searchamt = searchamt.setScale(4, BigDecimal.ROUND_DOWN);
         	
         	
         
         if(searchAmount!=null && !searchAmount.equals("0")){
         	
         	if(collectedBy!=null && !collectedBy.equals("")){
         		
         		if(modeOfPayment!=null && !modeOfPayment.equals("")) {
         			
         			if(searchamtbig.equals(searchamt) && (receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()) && receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
                     {
         				if(receiptHeader2.getTotalDepositAmount()!=null) {
                     		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				if(receiptHeader2.getTotalAmount()!=null) {
                         totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				receiptListnew.add(receiptHeader2);
                    
                     }
         		}
         		else {
         			if(searchamtbig.equals(searchamt) && (receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()))
                     {
         				if(receiptHeader2.getTotalDepositAmount()!=null) {
                     		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				if(receiptHeader2.getTotalAmount()!=null) {
                         totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				receiptListnew.add(receiptHeader2);
                    
                     }
         			
         		}
         	}
         	else if(modeOfPayment!=null && !modeOfPayment.equals("")) {
     			
 	        		if(searchamtbig.equals(searchamt) && receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
 	                {
 	        			if(receiptHeader2.getTotalDepositAmount()!=null) {
                     		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				if(receiptHeader2.getTotalAmount()!=null) {
                         totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				receiptListnew.add(receiptHeader2);
 	               
 	                }
 	    		}else{
 	        		
 	    			if(searchamtbig.equals(searchamt))
 	                {
 	    				if(receiptHeader2.getTotalDepositAmount()!=null) {
                     		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				if(receiptHeader2.getTotalAmount()!=null) {
                         totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
         				}
         				receiptListnew.add(receiptHeader2);
 	               
 	                }
 	        	}
         }
         else if(collectedBy!=null && !collectedBy.equals("")) {
         	
 			if(modeOfPayment!=null && !modeOfPayment.equals("")) {
 			    			
 			        		if((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()) && receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
 			                {
 			        			if(receiptHeader2.getTotalDepositAmount()!=null) {
 		                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
 		        				}
 		        				if(receiptHeader2.getTotalAmount()!=null) {
 		                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
 		        				}
 		        				receiptListnew.add(receiptHeader2);
 			               
 			                }
 			    		}else {
 			    			
 			    			if((receiptHeader2.getCreatedUser().toLowerCase()).contains(collectedBy.toLowerCase()))
 			                {
 			    				if(receiptHeader2.getTotalDepositAmount()!=null) {
 		                    		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
 		        				}
 		        				if(receiptHeader2.getTotalAmount()!=null) {
 		                        totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
 		        				}
 		        				receiptListnew.add(receiptHeader2);
 			               
 			                }
 			    		}
         	
         }
         else if(modeOfPayment!=null && !modeOfPayment.equals("")){
         	
         	if(receiptHeader2.getModOfPayment().equalsIgnoreCase(modeOfPayment))
             {
         		if(receiptHeader2.getTotalDepositAmount()!=null) {
             		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
 				}
 				if(receiptHeader2.getTotalAmount()!=null) {
                 totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
 				}
 				receiptListnew.add(receiptHeader2);
            
             }
         	
         }
         else {
         	
         	if(receiptHeader2.getTotalDepositAmount()!=null) {
         		totalDepositAmount2=totalDepositAmount2.add(receiptHeader2.getTotalDepositAmount().setScale(2, BigDecimal.ROUND_DOWN));
 			}
 			if(receiptHeader2.getTotalAmount()!=null) {
             totalReciptAmount2= totalReciptAmount2.add(receiptHeader2.getTotalAmount().setScale(2, BigDecimal.ROUND_DOWN));
 			}
 			receiptListnew.add(receiptHeader2);
         }
         
        
         
         
         }
         
        
        
        
        List<ReceiptReportBean> receiptReportList = new ArrayList<ReceiptReportBean>();
        ReceiptReportBean bean=null;
        int i=1;
        if(receiptListnew != null && !receiptListnew.isEmpty())
        {
        	System.out.println("receipt non empty");
        	for(ReceiptHeader header:receiptListnew)
            {
        		bean=new ReceiptReportBean();
        		bean.setSlNo(String.valueOf(i++));
        		bean.setParamDate(getDateString(header.getReceiptdate()));
        		bean.setReceiptNo(header.getReceiptnumber());
        		bean.setCollectedBy(header.getCreatedUser());
        		bean.setPayeeName(header.getPaidBy().split("&")[0]);
        		bean.setServiceType(header.getService());
        		bean.setModeOfPayment(header.getModOfPayment());
        		bean.setParticulars(header.getReferenceDesc());
        		bean.setTotalReceiptAmount(header.getTotalAmount());
        		if(header.getPrincipalAmount() != null)
        		{
        			bean.setPrincipalAmt(header.getPrincipalAmount());
        		}
        		else
        		{
        			bean.setPrincipalAmt(new BigDecimal("0"));
        		}
        		if(header.getGstAmount() != null)
        		{
        			bean.setGstAmount(header.getGstAmount());
        		}
        		else
        		{
        			bean.setPrincipalAmt(new BigDecimal("0"));
        		}
        		if(header.getRrDate() != null)
        		{
        			bean.setDateOfDeposite(getDateString(header.getRrDate()));
        		}
        		else
        		{
        			bean.setDateOfDeposite("");
        		}
        		if(header.getRemittanceReferenceNumber() != null)
        		{
        			bean.setRemitanceNo(String.valueOf(header.getReferencenumber()));
        		}
        		else
        		{
        			bean.setRemitanceNo("");
        		}
        		if(header.getBankAccountNumber() != null)
        		{
        			bean.setBankAccountNo(header.getBankAccountNumber());
        		}
        		else
        		{
        			bean.setBankAccountNo("");
        		}
        		
        		if(header.getDepositAmount() != null)
        		{
        			bean.setDepositAmount(new BigDecimal(header.getDepositAmount()));
        		}
        		else
        		{
        			bean.setDepositAmount(new BigDecimal("0"));
        		}
        		receiptReportList.add(bean);
            }
            
        }
        System.out.println("report size ::"+receiptReportList.size());
        Map<String, Object> paramMap = new HashMap<String, Object>();
    	String jasperName="CollectionReport";
    	paramMap.put("CollectionReportDataSource",getDataSource(receiptReportList));
    	paramMap.put("HeaderParameter", "Detail of Collection Report between "+getDateString(getFromDate())+" and "+getDateString(getToDate()));
    	ReportRequest reportInput = null;
        ReportOutput reportOutput = null;
        System.out.println("Start of report" );
        try
        {
        	reportInput = new ReportRequest(jasperName, receiptReportList, paramMap);
            reportInput.setReportFormat(ReportFormat.XLS);
            if(reportService != null)
            {
            	System.out.println("report service not null");
            }
            reportOutput = reportService.createReport(reportInput);
            inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
        }catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("END");
        return "XLS";
    }
	
	
    private List<RemittanceDepositWorkDetail> getDepositWork(List<Receipt> receipts) {
		
    	Set<String> reciptNumber = new HashSet<>();
    	
    	for (Receipt receipt : receipts) {

            for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                for (BillDetail billDetail : bill.getBillDetails()) {
                	
                	reciptNumber.add(billDetail.getReceiptNumber());
                	
                	
                }
                
                }
            }
    	
    	RemittanceResponseDepositWorkDetails remittanceResponse=microserviceUtils.getDayWorkHistory(reciptNumber);
    	return remittanceResponse.getRemittanceDepositWorkDetail();
	}

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }
    public Map<String, String> getServiceCategoryNames() {
        return serviceCategoryNames;
    }

    public void setServiceCategoryNames(Map<String, String> serviceCategoryNames) {
        this.serviceCategoryNames = serviceCategoryNames;
    }
    public Map<String, Map<String, String>> getServiceTypeMap() {
        return serviceTypeMap;
    }
    public void setServiceTypeMap(Map<String, Map<String, String>> serviceTypeMap) {
        this.serviceTypeMap = serviceTypeMap;
    }

    @Override
    public SearchQuery prepareQuery(final String sortField, final String sortDir) {
        final ArrayList<Object> params = new ArrayList<Object>(0);
        final StringBuilder searchQueryString = new StringBuilder("select distinct receipt ");
        final StringBuilder countQueryString = new StringBuilder("select count(distinct receipt) ");
        final StringBuilder fromString = new StringBuilder(" from org.egov.collection.entity.ReceiptHeader receipt ");
        final String orderByString = " group by receipt.receiptdate,receipt.id  order by receipt.receiptdate desc";

        // Get only those receipts whose status is NOT PENDING
        final StringBuilder criteriaString = new StringBuilder(" where receipt.status.code != ? ");
        params.add(CollectionConstants.RECEIPT_STATUS_CODE_PENDING);

        if (StringUtils.isNotBlank(getInstrumentType())) {
            fromString.append(" inner join receipt.receiptInstrument as instruments ");
            criteriaString.append(" and instruments.instrumentType.type = ? ");
            params.add(getInstrumentType());
        }

        if (StringUtils.isNotBlank(getReceiptNumber())) {
            criteriaString.append(" and upper(receiptNumber) like ? ");
            params.add("%" + getReceiptNumber().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(getManualReceiptNumber())) {
            criteriaString.append(" and upper(receipt.manualreceiptnumber) like ? ");
            params.add("%" + getManualReceiptNumber().toUpperCase() + "%");
        }
        if (getSearchStatus() != -1) {
            criteriaString.append(" and receipt.status.id = ? ");
            params.add(getSearchStatus());
        }
        if (getFromDate() != null) {
            criteriaString.append(" and receipt.receiptdate >= ? ");
            params.add(fromDate);
        }
       /* if (getToDate() != null) {
            criteriaString.append(" and receipt.receiptdate < ? ");
            params.add(DateUtils.add(toDate, Calendar.DATE, 1));
        }*/
        if (getToDate() != null) {
            criteriaString.append(" and receipt.receiptdate < ? ");
            params.add(toDate);
        }
        if (getServiceTypeId() != null) {
            criteriaString.append(" and receipt.service.id = ? ");
            params.add(Long.valueOf(getServiceTypeId()));
        }

        if (!getServiceClass().equals("-1")) {
            criteriaString.append(" and receipt.service.serviceType = ? ");
            params.add(getServiceClass());
        }

        if (getUserId() != -1) {
            criteriaString.append(" and receipt.createdBy.id = ? ");
            params.add(userId);
        }
        if (getBranchId() != -1) {
            criteriaString.append(" and receipt.receiptMisc.depositedBranch.id = ? ");
            params.add(getBranchId());
        }

        final String searchQuery = searchQueryString.append(fromString).append(criteriaString).append(orderByString).toString();
        final String countQuery = countQueryString.append(fromString).append(criteriaString).toString();

        return new SearchQueryHQL(searchQuery, countQuery, params);
    }

    public Integer getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(final Integer searchStatus) {
        this.searchStatus = searchStatus;
    }

    public SearchQuery prepareQuery() {

        return null;
    }

    public String getManualReceiptNumber() {
        return manualReceiptNumber;
    }

    public void setManualReceiptNumber(final String manualReceiptNumber) {
        this.manualReceiptNumber = manualReceiptNumber;
    }

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public TreeMap<String, String> getServiceClassMap() {
        return serviceClassMap;
    }

    public void setServiceClassMap(TreeMap<String, String> serviceClassMap) {
        this.serviceClassMap = serviceClassMap;
    }

    /**
     * @param collectionsUtil the collectionsUtil to set
     */
    public void setCollectionsUtil(final CollectionsUtil collectionsUtil) {
        this.collectionsUtil = collectionsUtil;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
    
    public String getCollectionVersion() {
        return collectionVersion;
    }
    
    public void setCollectionVersion(String collectionVersion) {
        this.collectionVersion = collectionVersion;
    }

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	
	private static JRBeanCollectionDataSource getDataSource(List<ReceiptReportBean> reportList) {
        return new JRBeanCollectionDataSource(reportList); 
    }

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getServiceTypeIdforExcel() {
		return serviceTypeIdforExcel;
	}

	public void setServiceTypeIdforExcel(String serviceTypeIdforExcel) {
		this.serviceTypeIdforExcel = serviceTypeIdforExcel;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	private String getDateString(Date date){
        Date d=new Date();
       String dateString = null;
  SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");
  
  try{
	dateString = sdfr.format( date );
  }catch (Exception ex ){
	  ex.printStackTrace();
  }
  return dateString;
    }

	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public String getSearchAmount() {
		return searchAmount;
	}

	public void setSearchAmount(String searchAmount) {
		this.searchAmount = searchAmount;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getSubdivison() {
		return subdivison;
	}

	public void setSubdivison(String subdivison) {
		this.subdivison = subdivison;
	}
}
