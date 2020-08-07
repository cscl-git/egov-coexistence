package org.egov.apnimandi.transactions.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.egov.apnimandi.masters.entity.vo.AttachedDocument;
import org.egov.apnimandi.masters.repository.DocumentsTypeMasterRepository;
import org.egov.apnimandi.reports.entity.ApnimandiContractorSearchResult;
import org.egov.apnimandi.transactions.entity.ApnimandiContractor;
import org.egov.apnimandi.transactions.entity.ContractorDocument;
import org.egov.apnimandi.transactions.repository.ContractorDocumentsRepository;
import org.egov.apnimandi.transactions.repository.ContractorsRepository;
import org.egov.apnimandi.transactions.service.workflow.ApnimandiContractorWorkflowCustomImpl;
import org.egov.apnimandi.utils.ApnimandiUtil;
import org.egov.apnimandi.utils.constants.ApnimandiConstants;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.utils.DateUtils;
import org.egov.infstr.services.PersistenceService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContractorsService extends PersistenceService<ApnimandiContractor, Long>{
	@Autowired
    private FileStoreService fileStoreService;
	
	@Autowired
    private ContractorsRepository contractorsRepository;
	
	@Autowired
    private DocumentsTypeMasterRepository documentsTypeMasterRepository;
	
	@Autowired
    private ContractorDocumentsRepository contractorDocumentsRepository;
	
	@Autowired
    private ApnimandiUtil apnimandiUtil;
	
	@Autowired
    private ApnimandiContractorWorkflowCustomImpl apnimandiContractorWorkflowCustomImpl;
	
	@PersistenceContext
    private EntityManager entityManager;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }
	
	public ContractorsService() {
		super(ApnimandiContractor.class);
	}
	
	public ContractorsService(Class<ApnimandiContractor> type) {
		super(type);
	}
	
	public List<ApnimandiContractor> getContractorList() {
        return contractorsRepository.findAll();
    }

    public List<ApnimandiContractor> getActiveContractorList() {
        return contractorsRepository.findByActiveTrue();
    }

    public List<ApnimandiContractor> findAll() {
        return contractorsRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public ApnimandiContractor findOne(final Long id) {
        return contractorsRepository.findOne(id);
    }
    
    @Transactional
    public ApnimandiContractor persist(final ApnimandiContractor contractor, final List<AttachedDocument> files, Long approvalPosition, String approvalComment,
            String workFlowAction)
            throws IOException, ParseException {
    	if (approvalPosition != null && StringUtils.isNotEmpty(workFlowAction)) {
    		apnimandiContractorWorkflowCustomImpl.createCommonWorkflowTransition(contractor, approvalPosition, approvalComment, workFlowAction);
    	}
        applyAuditing(contractor);
        applyAuditing(contractor.getState());
        final ApnimandiContractor savedContractor = contractorsRepository.save(contractor);
        final List<ContractorDocument> contractorDocuments = getContractorDocumentDetails(contractor, files);
        if (!contractorDocuments.isEmpty()) {
        	savedContractor.setContractorDocuments(contractorDocuments);
        	persistContractorDocuments(contractorDocuments);
        }
        return savedContractor;
    }
    
    public List<ContractorDocument> getContractorDocumentDetails(final ApnimandiContractor contractor, final List<AttachedDocument> files) throws IOException {
        final List<ContractorDocument> documentDetailsList = new ArrayList<ContractorDocument>();
        if (null != files) {        
        	boolean isDocumentExist=false;
            for (AttachedDocument attachedDocument:files) {
            	isDocumentExist=false;
            	if(null!=contractor.getContractorDocuments() && !contractor.getContractorDocuments().isEmpty()) {
            		for (final ContractorDocument doc : contractor.getContractorDocuments()) { 
            			if(attachedDocument.getDocumentCode().equalsIgnoreCase(doc.getDocumentType().getCode())) {            				
            				doc.setFilestoreid(fileStoreService.store(attachedDocument.getFileStream(), attachedDocument.getFileName(),
            														  attachedDocument.getMimeType(), ApnimandiConstants.MODULE_NAME));
            				doc.setReffileid(doc.getFilestoreid().getFileStoreId());
            				applyAuditing(doc);
            				documentDetailsList.add(doc);
            				isDocumentExist=true;
            				
            			}
            		}
            	}
            	if(!isDocumentExist) {
	                final ContractorDocument contractorDocument = new ContractorDocument();
	                contractorDocument.setContractor(contractor);
	                contractorDocument.setDocumentType(documentsTypeMasterRepository.findByCode(attachedDocument.getDocumentCode()));
	                contractorDocument.setFilestoreid(fileStoreService.store(attachedDocument.getFileStream(), attachedDocument.getFileName(),
	                														 attachedDocument.getMimeType(), ApnimandiConstants.MODULE_NAME));
	                contractorDocument.setReffileid(contractorDocument.getFilestoreid().getFileStoreId());
	                applyAuditing(contractorDocument);
	                documentDetailsList.add(contractorDocument);
            	}
            }
        }
        return documentDetailsList;
    }
    
    public void persistContractorDocuments(final List<ContractorDocument> documentDetailsList) {
        if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
            for (final ContractorDocument doc : documentDetailsList) {            	
            	contractorDocumentsRepository.save(doc);
            }
        }
    }
    
    public List<ApnimandiContractorSearchResult> search(final ApnimandiContractor contractor) {
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct conObj as apnimandiContractor, zoneMaster.name as zoneName, egwStatus.description as statusName");
        queryStr.append(" from ApnimandiContractor conObj,EgwStatus egwStatus,ZoneMaster zoneMaster");
        queryStr.append(" where conObj.status.id=egwStatus.id and conObj.zone.id=zoneMaster.id");
        getAppendQuery(contractor, queryStr);
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult = setParametersToQuery(contractor, queryResult);
        final List<ApnimandiContractorSearchResult> contractorSearchList = queryResult.list();
        return contractorSearchList;

    }
    
    private Query setParametersToQuery(final ApnimandiContractor contractor, final Query queryResult) {
    	if (null!=contractor.getZone())
           queryResult.setLong("zoneid", contractor.getZone().getId());
    	if (StringUtils.isNotBlank(contractor.getName()))
            queryResult.setString("name", "%" + contractor.getName() + "%");
    	if (null!=contractor.getValidFromDate())
    		queryResult.setDate("fromDate", contractor.getValidFromDate());
        if (null!=contractor.getValidToDate())
        	queryResult.setDate("toDate", contractor.getValidToDate());
        if (StringUtils.isNotBlank(contractor.getStatusCode()))
        	queryResult.setString("statuscode", contractor.getStatusCode());
        if (null!=contractor.getActive())
        	queryResult.setBoolean("active", contractor.getActive());
    	queryResult.setResultTransformer(new AliasToBeanResultTransformer(ApnimandiContractorSearchResult.class));
        return queryResult;
    }
    
    private void getAppendQuery(final ApnimandiContractor contractor, final StringBuilder queryStr) {
        if (null!=contractor.getZone())
            queryStr.append(" and zoneMaster.id =:zoneid");
        if (StringUtils.isNotBlank(contractor.getName()))
            queryStr.append(" and conObj.name like :name ");
        if (null!=contractor.getValidFromDate())
            queryStr.append(" and conObj.validFromDate >=:fromDate ");
        if (null!=contractor.getValidToDate())
            queryStr.append(" and conObj.validToDate <=:toDate ");
        if (StringUtils.isNotBlank(contractor.getStatusCode()))
            queryStr.append(" and egwStatus.code =:statuscode ");
        if (null!=contractor.getActive())
            queryStr.append(" and conObj.active =:active ");
    }
    
    public List<ApnimandiContractorSearchResult> getAllExistedContractor(final ApnimandiContractor contractor) {
    	if (null==contractor.getValidFromDate() || null==contractor.getValidToDate()) {
    		return null;
    	}    	   	
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct conObj as apnimandiContractor, zoneMaster.name as zoneName, egwStatus.description as statusName");
        queryStr.append(" from ApnimandiContractor conObj,EgwStatus egwStatus,ZoneMaster zoneMaster");
        queryStr.append(" where conObj.status.id=egwStatus.id and conObj.zone.id=zoneMaster.id");
        queryStr.append(" and zoneMaster.id =:zoneid");
        queryStr.append(" and ((conObj.validFromDate <=:fromDate and conObj.validToDate >=:fromDate) or (conObj.validFromDate <=:toDate and conObj.validToDate >=:toDate))");
        queryStr.append(" and egwStatus.code <>:statuscode ");
        if(null!=contractor.getId()) {
        	queryStr.append(" and conObj.id <>:id ");
        }
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult.setLong("zoneid", contractor.getZone().getId());
        queryResult.setDate("fromDate", contractor.getValidFromDate());
        queryResult.setDate("toDate", contractor.getValidToDate());
        queryResult.setString("statuscode", ApnimandiConstants.APNIMANDI_STATUS_CONTRACT_TERMINATED);
        if(null!=contractor.getId()) {
        	queryResult.setLong("id", contractor.getId());
        }
        queryResult.setResultTransformer(new AliasToBeanResultTransformer(ApnimandiContractorSearchResult.class));
        final List<ApnimandiContractorSearchResult> contractorSearchList = queryResult.list();
        return contractorSearchList;
    }
    
    public List<ApnimandiContractorSearchResult> getAllExistedContractorByZone(final ApnimandiContractor contractor) {
    	if (null==contractor.getValidFromDate() || null==contractor.getValidToDate()) {
    		return null;
    	}    	   	
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct conObj as apnimandiContractor, zoneMaster.name as zoneName, egwStatus.description as statusName");
        queryStr.append(" from ApnimandiContractor conObj,EgwStatus egwStatus,ZoneMaster zoneMaster");
        queryStr.append(" where conObj.status.id=egwStatus.id and conObj.zone.id=zoneMaster.id");
        queryStr.append(" and zoneMaster.id =:zoneid");
        queryStr.append(" and ((conObj.validFromDate <=:fromDate and conObj.validToDate >=:fromDate) or (conObj.validFromDate <=:toDate and conObj.validToDate >=:toDate))");
        queryStr.append(" and egwStatus.code =:statuscode ");
        queryStr.append(" and conObj.active =:active ");
        if(null!=contractor.getId()) {
        	queryStr.append(" and conObj.id <>:id ");
        }
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult.setLong("zoneid", contractor.getZone().getId());
        queryResult.setDate("fromDate", contractor.getValidFromDate());
        queryResult.setDate("toDate", contractor.getValidToDate());
        queryResult.setString("statuscode", ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_APPROVED);
        queryResult.setBoolean("active", true);
        if(null!=contractor.getId()) {
        	queryResult.setLong("id", contractor.getId());
        }
        queryResult.setResultTransformer(new AliasToBeanResultTransformer(ApnimandiContractorSearchResult.class));
        final List<ApnimandiContractorSearchResult> contractorSearchList = queryResult.list();
        return contractorSearchList;
    }
    
    public List<ApnimandiContractorSearchResult> getContractorByZoneMonthAndYear(final long zoneid, final int month, final int year){
    	final ApnimandiContractor apnimandiContractor = new ApnimandiContractor();
		final ZoneMaster zone = new ZoneMaster();
		zone.setId(zoneid);
		apnimandiContractor.setZone(zone);
		apnimandiContractor.setValidFromDate(DateUtils.getDate(year, month, 1));
		apnimandiContractor.setValidToDate(ApnimandiUtil.getLastDateOfMonth(month, year));
        final List<ApnimandiContractorSearchResult> searchResultList = getAllExistedContractorByZone(apnimandiContractor);
        return searchResultList;
    }
}
