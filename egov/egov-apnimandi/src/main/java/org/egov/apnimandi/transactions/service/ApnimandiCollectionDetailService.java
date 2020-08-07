package org.egov.apnimandi.transactions.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.egov.apnimandi.masters.entity.vo.AttachedDocument;
import org.egov.apnimandi.reports.entity.ApnimandiCollectionSearchResult;
import org.egov.apnimandi.transactions.entity.ApnimandiCollectionAmountDetails;
import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;
import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDocument;
import org.egov.apnimandi.transactions.repository.ApnimandiCollectionAmountDetailsRepository;
import org.egov.apnimandi.transactions.repository.ApnimandiCollectionDetailsRepository;
import org.egov.apnimandi.transactions.repository.ApnimandiCollectionDocumentRepository;
import org.egov.apnimandi.transactions.service.workflow.ApnimandiCollectionWorkflowCustomImpl;
import org.egov.apnimandi.utils.ApnimandiUtil;
import org.egov.apnimandi.utils.constants.ApnimandiConstants;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.BillDetailAdditional;
import org.egov.infra.microservice.models.CollectionType;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Instrument;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.models.ReceiptResponse;
import org.egov.infra.microservice.models.TaxHeadMaster;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.instrument.InstrumentType;
import org.egov.utils.FinancialConstants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.ReceiptDetail;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.entity.ReceiptMisc;
import org.egov.collection.service.ReceiptHeaderService;
import org.egov.collection.utils.CollectionCommon;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.collection.utils.FinancialsUtil;
import org.egov.commons.Bank;
import org.egov.commons.Bankaccount;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.dao.SchemeHibernateDAO;
import org.egov.commons.dao.SubSchemeHibernateDAO;
import org.egov.commons.entity.Source;

@Service
@Transactional(readOnly = true)
public class ApnimandiCollectionDetailService extends PersistenceService<ApnimandiCollectionDetails, Long>{
	@Autowired
    private FileStoreService fileStoreService;
	
	@Autowired
    private ApnimandiUtil apnimandiUtil;
	
	@Autowired
    private ApnimandiCollectionWorkflowCustomImpl apnimandiCollectionWorkflowCustomImpl;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }
	
	@Autowired
    private ApnimandiCollectionDetailsRepository apnimandiCollectionDetailsRepository;
	
	@Autowired
    private ApnimandiCollectionDocumentRepository apnimandiCollectionDocumentRepository;
	
	@Autowired
	private CollectionsUtil collectionsUtil;
	
	@Autowired
	private FinancialsUtil financialsUtil;
	
	@Autowired
	private ReceiptHeaderService receiptHeaderService;
	
	@Autowired
    private MicroserviceUtils microserviceUtils;
	
	@Autowired
    private SchemeHibernateDAO schemeDAO;
	
	@Autowired
    private SubSchemeHibernateDAO subSchemeDAO;
	
	@Autowired
	private CollectionCommon collectionCommon;
	
	public ApnimandiCollectionDetailService() {
		super(ApnimandiCollectionDetails.class);
	}
	
	public ApnimandiCollectionDetailService(Class<ApnimandiCollectionDetails> type) {
		super(type);
	}
	
	public List<ApnimandiCollectionDetails> getApnimandiCollectionDetailsList() {
        return apnimandiCollectionDetailsRepository.findAll();
    }

    public List<ApnimandiCollectionDetails> getActiveApnimandiCollectionDetailsList() {
        return apnimandiCollectionDetailsRepository.findByActiveTrue();
    }

    public List<ApnimandiCollectionDetails> findAll() {
        return apnimandiCollectionDetailsRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public ApnimandiCollectionDetails findOne(final Long id) {
        return apnimandiCollectionDetailsRepository.findOne(id);
    }
    
    @Transactional
    public ApnimandiCollectionDetails persist(final ApnimandiCollectionDetails apnimandiCollectionDetails, final List<AttachedDocument> files, 
    										  final List<ApnimandiCollectionAmountDetails> collectionAmountDetails, Long approvalPosition, String approvalComment,
    								          String workFlowAction)
            throws IOException, ParseException {
    	if (approvalPosition != null && StringUtils.isNotEmpty(workFlowAction)) {
    		apnimandiCollectionWorkflowCustomImpl.createCommonWorkflowTransition(apnimandiCollectionDetails, approvalPosition, approvalComment, workFlowAction);
    	}
        applyAuditing(apnimandiCollectionDetails);
        applyAuditing(apnimandiCollectionDetails.getState());        
        prepareCollectionAmounts(apnimandiCollectionDetails, collectionAmountDetails);        
        final List<ApnimandiCollectionDocument> apnimandiCollectionDocuments = getCollectionDocumentDetails(apnimandiCollectionDetails, files);        
        ApnimandiCollectionDetails savedApnimandiCollection = apnimandiCollectionDetailsRepository.save(apnimandiCollectionDetails);
        if (!apnimandiCollectionDocuments.isEmpty()) {
        	apnimandiCollectionDetails.setApnimandiCollectionDocuments(apnimandiCollectionDocuments);
        	persistCollectionDocuments(apnimandiCollectionDocuments);
        }
        if (null != workFlowAction) {
        	if (ApnimandiConstants.APPROVE.equalsIgnoreCase(workFlowAction)) {
        		ReceiptResponse receiptResponse = new ReceiptResponse();
        		receiptResponse = receiptHeaderService.populateAndPersistReceipts(getReceiptHeader(savedApnimandiCollection), getReceiptInstrumentList(savedApnimandiCollection));
        		String receiptNumber = receiptResponse.getReceipts().get(0).getBill().get(0).getBillDetails().get(0).getReceiptNumber();
        		String paymentId = receiptResponse.getReceipts().get(0).getPaymentId();
        		savedApnimandiCollection.setReceiptNo(receiptNumber);
        		savedApnimandiCollection.setPaymentId(paymentId);
        		savedApnimandiCollection = apnimandiCollectionDetailsRepository.save(savedApnimandiCollection);
        	}
        }        
        return savedApnimandiCollection;
    }
    
    private ReceiptHeader getReceiptHeader(final ApnimandiCollectionDetails apnimandiCollectionDetails) {
    	ReceiptHeader receiptHeader = new ReceiptHeader();
    	receiptHeader.setCallbackForApportioning(Boolean.FALSE);
    	receiptHeader.setCollectiontype(CollectionConstants.COLLECTION_TYPE_COUNTER);
    	receiptHeader.setIsReconciled(Boolean.TRUE);
    	receiptHeader.setModOfPayment(apnimandiCollectionDetails.getPaymentMode());
    	receiptHeader.setOverrideAccountHeads(Boolean.FALSE);
    	receiptHeader.setPaidBy(apnimandiCollectionDetails.getPayeeName());
    	receiptHeader.setPartPaymentAllowed(Boolean.FALSE);
    	if(ApnimandiConstants.DAY_MARKET.equalsIgnoreCase(apnimandiCollectionDetails.getCollectiontype().getCode())) {
    		receiptHeader.setPayeeAddress(apnimandiCollectionDetails.getContractor().getAddress());
    	}else {
    		receiptHeader.setPayeeAddress("");
    	}    	
    	receiptHeader.setPayeeName(apnimandiCollectionDetails.getPayeeName());
    	receiptHeader.setReasonForCancellation(StringUtils.EMPTY);
    	receiptHeader.setReceipttype(CollectionConstants.RECEIPT_TYPE_ADHOC);
    	receiptHeader.setReferenceDesc(apnimandiCollectionDetails.getComment());
    	receiptHeader.setService(ApnimandiConstants.SERVICE_PREFIX + "_" + apnimandiCollectionDetails.getZone().getRoadDivision() + "." + apnimandiCollectionDetails.getServiceCategory());
    	receiptHeader.setServiceCategory(apnimandiCollectionDetails.getServiceCategory());
    	receiptHeader.setServiceIdText(StringUtils.EMPTY);
    	receiptHeader.setSource(Source.SYSTEM.toString());
    	receiptHeader.setStatus(collectionsUtil.getStatusForModuleAndCode(CollectionConstants.MODULE_NAME_RECEIPTHEADER, CollectionConstants.RECEIPT_STATUS_CODE_TO_BE_SUBMITTED));
    	receiptHeader.setTotalAmount(BigDecimal.valueOf(apnimandiCollectionDetails.getAmount()));
    	receiptHeader.setTotalcramount(BigDecimal.valueOf(apnimandiCollectionDetails.getAmount()));
    	receiptHeader.setWorkflowUserName("NA");
    	long orderNumber = 0;
    	ReceiptDetail receiptDetail;
    	for(ApnimandiCollectionAmountDetails amountDetails:apnimandiCollectionDetails.getApnimandiCollectionAmountDetails()) {
    		if(amountDetails.getCreditAmountDetail()>0) {
    			receiptDetail = new ReceiptDetail();
    			receiptDetail.setCramount(BigDecimal.valueOf(amountDetails.getCreditAmountDetail()));
    			receiptDetail.setCramountToBePaid(BigDecimal.valueOf(amountDetails.getCreditAmountDetail()));
    			receiptDetail.setDramount(BigDecimal.ZERO);
    			receiptDetail.setIsActualDemand(Boolean.TRUE);
    			receiptDetail.setOrdernumber(orderNumber);
    			receiptDetail.setPurpose("OTHERS");    			
    			receiptDetail.setTaxheadCode(amountDetails.getGlCodeIdDetail());
    			receiptDetail.setReceiptHeader(receiptHeader);
    			receiptHeader.getReceiptDetails().add(receiptDetail);
    		}
    		orderNumber++;
    	}
    	if(CollectionConstants.INSTRUMENTTYPE_CASH.equalsIgnoreCase(apnimandiCollectionDetails.getPaymentMode())) {
    		InstrumentHeader instrumentHeader = new InstrumentHeader();
    		instrumentHeader.setInstrumentType(financialsUtil.getInstrumentTypeByType(CollectionConstants.INSTRUMENTTYPE_CASH));
    		instrumentHeader.setInstrumentAmount(BigDecimal.valueOf(apnimandiCollectionDetails.getAmount()));
    		instrumentHeader.setIsPayCheque(CollectionConstants.ZERO_INT);
    		receiptHeader.getReceiptInstrument().add(instrumentHeader);
    	}else {
    		Bank bank = new Bank();
        	bank.setName(apnimandiCollectionDetails.getBankName());
        	bank.setCode(apnimandiCollectionDetails.getBankCode());
        	
        	InstrumentHeader instrumentHeader = new InstrumentHeader();
        	instrumentHeader.setBankBranchName(apnimandiCollectionDetails.getBranchName());
        	instrumentHeader.setBankId(bank);
        	instrumentHeader.setIfscCode(apnimandiCollectionDetails.getIfscCode());
        	instrumentHeader.setInstrumentAmount(BigDecimal.valueOf(apnimandiCollectionDetails.getAmount()));
        	instrumentHeader.setInstrumentDate(apnimandiCollectionDetails.getDdOrChequeDate());
        	instrumentHeader.setInstrumentNumber(apnimandiCollectionDetails.getDdOrChequeNo());
        	if(FinancialConstants.INSTRUMENT_TYPE_CHEQUE.equalsIgnoreCase(apnimandiCollectionDetails.getPaymentMode())) {
        		instrumentHeader.setInstrumentType(financialsUtil.getInstrumentTypeByType(FinancialConstants.INSTRUMENT_TYPE_CHEQUE));
        	}else {
        		instrumentHeader.setInstrumentType(financialsUtil.getInstrumentTypeByType(FinancialConstants.INSTRUMENT_TYPE_DD));
        	}
        	instrumentHeader.setIsPayCheque(CollectionConstants.ZERO_INT);
    		receiptHeader.getReceiptInstrument().add(instrumentHeader);
    	}
    	return receiptHeader;
    }
    
    private List<InstrumentHeader> getReceiptInstrumentList(final ApnimandiCollectionDetails apnimandiCollectionDetails) {
    	List<InstrumentHeader> receiptInstrList = new ArrayList<>(0);
    	if(CollectionConstants.INSTRUMENTTYPE_CASH.equalsIgnoreCase(apnimandiCollectionDetails.getPaymentMode())) {
    		InstrumentHeader instrumentHeader = new InstrumentHeader();
    		instrumentHeader.setInstrumentType(financialsUtil.getInstrumentTypeByType(CollectionConstants.INSTRUMENTTYPE_CASH));
    		instrumentHeader.setInstrumentAmount(BigDecimal.valueOf(apnimandiCollectionDetails.getAmount()));
    		instrumentHeader.setIsPayCheque(CollectionConstants.ZERO_INT);
    		receiptInstrList.add(instrumentHeader);
    	}else {
    		Bank bank = new Bank();
        	bank.setName(apnimandiCollectionDetails.getBankName());
        	bank.setCode(apnimandiCollectionDetails.getBankCode());
        	
        	InstrumentHeader instrumentHeader = new InstrumentHeader();
        	instrumentHeader.setBankBranchName(apnimandiCollectionDetails.getBranchName());
        	instrumentHeader.setBankId(bank);
        	instrumentHeader.setIfscCode(apnimandiCollectionDetails.getIfscCode());
        	instrumentHeader.setInstrumentAmount(BigDecimal.valueOf(apnimandiCollectionDetails.getAmount()));
        	instrumentHeader.setInstrumentDate(apnimandiCollectionDetails.getDdOrChequeDate());
        	instrumentHeader.setInstrumentNumber(apnimandiCollectionDetails.getDdOrChequeNo());
        	if(FinancialConstants.INSTRUMENT_TYPE_CHEQUE.equalsIgnoreCase(apnimandiCollectionDetails.getPaymentMode())) {
        		instrumentHeader.setInstrumentType(financialsUtil.getInstrumentTypeByType(FinancialConstants.INSTRUMENT_TYPE_CHEQUE));
        	}else {
        		instrumentHeader.setInstrumentType(financialsUtil.getInstrumentTypeByType(FinancialConstants.INSTRUMENT_TYPE_DD));
        	}
        	instrumentHeader.setIsPayCheque(CollectionConstants.ZERO_INT);
        	receiptInstrList.add(instrumentHeader);
    	}
    	return receiptInstrList;
    }
    
    public List<ApnimandiCollectionDocument> getCollectionDocumentDetails(final ApnimandiCollectionDetails apnimandiCollectionDetails, final List<AttachedDocument> files) throws IOException {
        final List<ApnimandiCollectionDocument> documentDetailsList = new ArrayList<ApnimandiCollectionDocument>();
        if (null != files) { 
            for (AttachedDocument attachedDocument:files) {
            	final ApnimandiCollectionDocument apnimandiCollectionDocument = new ApnimandiCollectionDocument();
                apnimandiCollectionDocument.setApnimandicollectiondetails(apnimandiCollectionDetails);
                apnimandiCollectionDocument.setFilestoreid(fileStoreService.store(attachedDocument.getFileStream(), attachedDocument.getFileName(),
                														 attachedDocument.getMimeType(), ApnimandiConstants.MODULE_NAME));
                apnimandiCollectionDocument.setReffileid(apnimandiCollectionDocument.getFilestoreid().getFileStoreId());
                applyAuditing(apnimandiCollectionDocument);
                documentDetailsList.add(apnimandiCollectionDocument);
            }
        }
        return documentDetailsList;
    }
    
    public void persistCollectionDocuments(final List<ApnimandiCollectionDocument> documentDetailsList) {
        if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
            for (final ApnimandiCollectionDocument doc : documentDetailsList) {  
            	apnimandiCollectionDocumentRepository.save(doc);
            }
        }
    }
    
    private void prepareCollectionAmounts(final ApnimandiCollectionDetails apnimandiCollectionDetails, final List<ApnimandiCollectionAmountDetails> collectionAmountDetails) {        
        if(null!=collectionAmountDetails) {
	        if(!collectionAmountDetails.isEmpty()) {
	        	if (apnimandiCollectionDetails.getApnimandiCollectionAmountDetails() != null && !apnimandiCollectionDetails.getApnimandiCollectionAmountDetails().isEmpty()) {
	            	apnimandiCollectionDetails.getApnimandiCollectionAmountDetails().clear();
	            	apnimandiCollectionDetailsRepository.flush();        	
	            }	        	
	        	for (final ApnimandiCollectionAmountDetails collectionAmountDetail : collectionAmountDetails) {        		
	        		collectionAmountDetail.setApnimandicollectiondetails(apnimandiCollectionDetails);
	        		applyAuditing(collectionAmountDetail);
	        	}
	        	apnimandiCollectionDetails.setApnimandiCollectionAmountDetails(collectionAmountDetails);    	        	
	        }
        }
    }
    
    public String getReceipt(String selectedReceiptId) {
    	ReceiptHeader[] receipts = new ReceiptHeader[1];
    	List<Receipt> receiptlist = microserviceUtils.searchReciepts(null, null, null, null, Arrays.asList(selectedReceiptId));
    	ReceiptHeader receiptHeader = new ReceiptHeader();
    	receiptlist.stream().forEach(receipt -> {
            receipt.getBill().forEach(bill -> {
                bill.getBillDetails().forEach(billDetail -> {
                    ReceiptHeader header = new ReceiptHeader();
                    receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
                    receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
                    String businessServiceCode = billDetail.getBusinessService();
                    receiptHeader.setService(microserviceUtils.getBusinessServiceNameByCode(businessServiceCode));
                    receiptHeader.setReferencenumber(billDetail.getBillNumber());
                    receiptHeader.setReferenceDesc(billDetail.getBillDescription());
                    receiptHeader.setPaidBy(bill.getPaidBy());
                    receiptHeader.setPayeeName(bill.getPayerName());
                    receiptHeader.setPayeeAddress(bill.getPayerAddress());
                    receiptHeader.setTotalAmount(billDetail.getTotalAmount());
                    receiptHeader.setCurretnStatus(billDetail.getStatus());
                    receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
                    receiptHeader.setManualreceiptnumber(billDetail.getManualReceiptNumber());
                    receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());
                    receiptHeader.setConsumerCode(billDetail.getConsumerCode());
                    receiptHeader.setManualreceiptnumber(billDetail.getManualReceiptNumber());
                    if (billDetail.getManualReceiptDate() != 0)
                        receiptHeader.setManualreceiptdate(new Date(billDetail.getManualReceiptDate()));
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
                        ReceiptMisc receiptMisc = new ReceiptMisc();
                        if (null != additional.getScheme()) {
                            Scheme scheme = schemeDAO.getSchemeByCode(additional.getScheme());
                            receiptMisc.setScheme(scheme);
                        }

                        if (null != additional.getSubScheme()) {
                            SubScheme subScheme = subSchemeDAO.getSubSchemeByCode(additional.getSubScheme());
                            receiptMisc.setSubscheme(subScheme);
                        }
                        receiptHeader.setReceiptMisc(receiptMisc);
                        if (null != additional.getNarration())
                            receiptHeader.setReferenceDesc(additional.getNarration());
                        if (null != additional.getPayeeaddress())
                            receiptHeader.setPayeeAddress(additional.getPayeeaddress());
                    }

                    if(ApplicationThreadLocals.getCollectionVersion().toUpperCase().equalsIgnoreCase("V1")){
                        if (billDetail.getCollectionType().equals(CollectionType.COUNTER))
                            receiptHeader.setCollectiontype(CollectionConstants.COLLECTION_TYPE_COUNTER);
                        else if (billDetail.getCollectionType().equals(CollectionType.FIELD))
                            receiptHeader.setCollectiontype(CollectionConstants.COLLECTION_TYPE_FIELDCOLLECTION);
                        else if (billDetail.getCollectionType().equals(CollectionType.ONLINE))
                            receiptHeader.setCollectiontype(CollectionConstants.COLLECTION_TYPE_ONLINECOLLECTION);
                    }
                    
                    if (billDetail.getReceiptType().equalsIgnoreCase(CollectionConstants.RECEIPT_M_TYPE_MISCELLANEOUS) ||
                            billDetail.getReceiptType().equalsIgnoreCase(CollectionConstants.RECEIPT_M_TYPE_ADHOC))
                        receiptHeader.setReceipttype(CollectionConstants.RECEIPT_TYPE_ADHOC);
                    else if (billDetail.getReceiptType().equalsIgnoreCase(CollectionConstants.RECEIPT_M_TYPE_BILLBASED))
                        receiptHeader.setReceipttype(CollectionConstants.RECEIPT_TYPE_BILL);

                    Set<ReceiptDetail> receiptdetailslist = new HashSet<>();
                    billDetail.getBillAccountDetails().forEach(billAccountDetail -> {
                        ReceiptDetail receiptDetail = new ReceiptDetail();
                        receiptDetail.setAccounthead(new CChartOfAccounts());

                        switch (ApplicationThreadLocals.getCollectionVersion().toUpperCase()) {
                        case "V2":
                        case "VERSION2":
                            receiptDetail.setDramount(billAccountDetail.getAmount().compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ZERO : billAccountDetail.getAmount());
                            receiptDetail.setCramount(billAccountDetail.getAmount().compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : billAccountDetail.getAmount());
                            break;

                        default:
                            receiptDetail.setDramount(billAccountDetail.getDebitAmount());
                            receiptDetail.setCramount(billAccountDetail.getCreditAmount());
                            break;
                        }
                        receiptDetail.setOrdernumber(billAccountDetail.getOrder().longValue());
                        receiptDetail.setPurpose(billAccountDetail.getPurpose()!=null?billAccountDetail.getPurpose().toString():"");
                        receiptdetailslist.add(receiptDetail);
                    });
                    receiptHeader.setReceiptDetails(receiptdetailslist);
                    receiptHeader.setReceiptHeader(header);
                    InstrumentHeader instrumentHeader = new InstrumentHeader();

                    Instrument _instrument = receipt.getInstrument();
                    instrumentHeader.setInstrumentNumber(_instrument.getInstrumentNumber() != null ? _instrument.getInstrumentNumber() : _instrument.getTransactionNumber());
                    instrumentHeader.setInstrumentDate(new Date(_instrument.getTransactionDateInput() != null ? _instrument.getTransactionDateInput() : _instrument.getInstrumentDate()));

                    InstrumentType instrumentType = new InstrumentType();
                    instrumentType.setType(_instrument.getInstrumentType().getName().toLowerCase());
                    instrumentHeader.setInstrumentType(instrumentType);

                    instrumentHeader.setInstrumentAmount(_instrument.getAmount());
                    if (instrumentType.getType().equalsIgnoreCase(CollectionConstants.INSTRUMENTTYPE_CHEQUE) ||
                            instrumentType.getType().equalsIgnoreCase(CollectionConstants.INSTRUMENTTYPE_DD)) {
                        Bankaccount account = new Bankaccount();
                        if (null != _instrument.getBankAccount())
                            account.setAccountnumber(_instrument.getBankAccount().getAccountNumber());
                        instrumentHeader.setBankAccountId(account);

                        if(_instrument.getBank() != null){
                            Bank bank = new Bank();
                            bank.setName(_instrument.getBank().getName());
                            instrumentHeader.setBankId(bank);                            
                        }
                        instrumentHeader.setIfscCode(_instrument.getIfscCode());
                        instrumentHeader.setBankBranchName(_instrument.getBranchName());
                    }

                    receiptHeader.addInstrument(instrumentHeader);
                    EmployeeInfo empInfo = microserviceUtils.getEmployeeById(Long.parseLong(receipt.getAuditDetails().getCreatedBy()));
                    if (null != empInfo && empInfo.getUser().getUserName() != null)
                        receiptHeader.setCreatedUser(empInfo.getUser().getName());
                    receipts[0] = receiptHeader;

                });
            });

        });
    	String reportId = "";
        try {
            reportId = collectionCommon.generateReport(receipts, false);
        } catch (final Exception e) {
            final String errMsg = "Error during report generation!";
            throw new ApplicationRuntimeException(errMsg, e);
        }
        
        return reportId;
	}
    
    public List<ApnimandiCollectionSearchResult> getAllExistedCollections(final ApnimandiCollectionDetails apnimandiCollectionDetails) {
    	if (null==apnimandiCollectionDetails.getCollectionForMonth() 
    			|| null==apnimandiCollectionDetails.getCollectionForYear()
    				|| null == apnimandiCollectionDetails.getZone()) {
    		return null;
    	}    	   	
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct collectionObj as apnimandiCollections, zoneMaster.name as zoneName, egwStatus.description as statusName");
        queryStr.append(" from ApnimandiCollectionDetails collectionObj,EgwStatus egwStatus,ZoneMaster zoneMaster,ApnimandiContractor conObj");
        queryStr.append(" where collectionObj.status.id=egwStatus.id and collectionObj.zone.id=zoneMaster.id and collectionObj.contractor.id=conObj.id");
        queryStr.append(" and zoneMaster.id =:zoneid");
        queryStr.append(" and collectionObj.collectionForMonth =:collectionMonth");
        queryStr.append(" and collectionObj.collectionForYear =:collectionYear");
        queryStr.append(" and conObj.id =:contid");
        queryStr.append(" and egwStatus.code <>:statuscode ");
        if(null!=apnimandiCollectionDetails.getId()) {
        	queryStr.append(" and collectionObj.id <>:id ");
        }
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult.setLong("zoneid", apnimandiCollectionDetails.getZone().getId());
        queryResult.setInteger("collectionMonth", apnimandiCollectionDetails.getCollectionForMonth());
        queryResult.setInteger("collectionYear", apnimandiCollectionDetails.getCollectionForYear());
        queryResult.setString("statuscode", ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_DELETED);
        queryResult.setLong("contid", apnimandiCollectionDetails.getContractor().getId());
        if(null!=apnimandiCollectionDetails.getId()) {
        	queryResult.setLong("id", apnimandiCollectionDetails.getId());
        }
        queryResult.setResultTransformer(new AliasToBeanResultTransformer(ApnimandiCollectionSearchResult.class));
        final List<ApnimandiCollectionSearchResult> collectionSearchList = queryResult.list();
        return collectionSearchList;
    }
    
    public List<ApnimandiCollectionSearchResult> search(final ApnimandiCollectionDetails apnimandiCollectionDetails) {
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct collectionObj as apnimandiCollections, zoneMaster.name as zoneName, siteMaster.name as siteName, egwStatus.description as statusName");
        queryStr.append(" from ApnimandiCollectionDetails collectionObj,EgwStatus egwStatus,ZoneMaster zoneMaster, SiteMaster siteMaster, ApnimandiCollectionType collType");
        queryStr.append(" where collectionObj.status.id=egwStatus.id and collectionObj.zone.id=zoneMaster.id and collectionObj.site.id=siteMaster.id and collectionObj.collectiontype.id=collType.id");
        getAppendQuery(apnimandiCollectionDetails, queryStr);
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult = setParametersToQuery(apnimandiCollectionDetails, queryResult);
        final List<ApnimandiCollectionSearchResult> collectionSearchList = queryResult.list();
        return collectionSearchList;
    }
    
    private Query setParametersToQuery(final ApnimandiCollectionDetails apnimandiCollectionDetails, final Query queryResult) {
    	if (null!=apnimandiCollectionDetails.getCollectiontype())
            queryResult.setLong("colltypeid", apnimandiCollectionDetails.getCollectiontype().getId());
    	if (null!=apnimandiCollectionDetails.getZone())
           queryResult.setLong("zoneid", apnimandiCollectionDetails.getZone().getId());
    	if (null!=apnimandiCollectionDetails.getCollectionForMonth())
    		queryResult.setInteger("collectionMonth", apnimandiCollectionDetails.getCollectionForMonth());
        if (null!=apnimandiCollectionDetails.getCollectionForYear())
        	queryResult.setInteger("collectionYear", apnimandiCollectionDetails.getCollectionForYear());
        if (StringUtils.isNotBlank(apnimandiCollectionDetails.getStatusCode()))
        	queryResult.setString("statuscode", apnimandiCollectionDetails.getStatusCode());
        if (null!=apnimandiCollectionDetails.getActive())
        	queryResult.setBoolean("active", apnimandiCollectionDetails.getActive());
    	queryResult.setResultTransformer(new AliasToBeanResultTransformer(ApnimandiCollectionSearchResult.class));
        return queryResult;
    }
    
    private void getAppendQuery(final ApnimandiCollectionDetails apnimandiCollectionDetails, final StringBuilder queryStr) {
    	if (null!=apnimandiCollectionDetails.getCollectiontype())
    		queryStr.append(" and collType.id =:colltypeid");
        if (null!=apnimandiCollectionDetails.getZone())
            queryStr.append(" and zoneMaster.id =:zoneid");
        if (null!=apnimandiCollectionDetails.getCollectionForMonth())
            queryStr.append(" and collectionObj.collectionForMonth =:collectionMonth ");
        if (null!=apnimandiCollectionDetails.getCollectionForYear())
            queryStr.append(" and collectionObj.collectionForYear =:collectionYear ");
        if (StringUtils.isNotBlank(apnimandiCollectionDetails.getStatusCode()))
            queryStr.append(" and egwStatus.code =:statuscode ");
        if (null!=apnimandiCollectionDetails.getActive())
            queryStr.append(" and collectionObj.active =:active ");
    }
    
    public List<ApnimandiCollectionSearchResult> getCollectionsByContractor(final ApnimandiCollectionDetails apnimandiCollectionDetails) {
    	if (null==apnimandiCollectionDetails.getCollectionForMonth() 
    			|| null==apnimandiCollectionDetails.getCollectionForYear()
    				|| null == apnimandiCollectionDetails.getContractor()) {
    		return null;
    	}    	   	
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct collectionObj as apnimandiCollections, zoneMaster.name as zoneName, egwStatus.description as statusName");
        queryStr.append(" from ApnimandiCollectionDetails collectionObj,EgwStatus egwStatus,ZoneMaster zoneMaster,ApnimandiContractor apnimandiContractor");
        queryStr.append(" where collectionObj.status.id=egwStatus.id and collectionObj.zone.id=zoneMaster.id and collectionObj.contractor.id=apnimandiContractor.id");
        queryStr.append(" and apnimandiContractor.id =:contractorId");
        queryStr.append(" and collectionObj.collectionForMonth =:collectionMonth");
        queryStr.append(" and collectionObj.collectionForYear =:collectionYear");
        queryStr.append(" and egwStatus.code <>:statuscode ");
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult.setLong("contractorId", apnimandiCollectionDetails.getContractor().getId());
        queryResult.setInteger("collectionMonth", apnimandiCollectionDetails.getCollectionForMonth());
        queryResult.setInteger("collectionYear", apnimandiCollectionDetails.getCollectionForYear());
        queryResult.setString("statuscode", ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_DELETED);
        queryResult.setResultTransformer(new AliasToBeanResultTransformer(ApnimandiCollectionSearchResult.class));
        final List<ApnimandiCollectionSearchResult> collectionSearchList = queryResult.list();
        return collectionSearchList;
    }
    
    public List<TaxHeadMaster> getAccountHeadMasterByService(String serviceId) {
        List<TaxHeadMaster> accountHeadMasters = microserviceUtils.getTaxheadsByServiceCode(serviceId);
        return accountHeadMasters;
    }
}
