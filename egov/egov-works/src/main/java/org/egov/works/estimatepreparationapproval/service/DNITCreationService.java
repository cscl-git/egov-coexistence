package org.egov.works.estimatepreparationapproval.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.eis.service.DesignationService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.pims.commons.Position;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.DNITCreationRepository;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DNITCreationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(DNITCreationService.class);

	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;
	
	@Autowired
	protected MicroserviceUtils microserviceUtils;
	@Autowired
	DNITCreationRepository dNITCreationRepository;
	
	@Autowired
    private SecurityUtils securityUtils;
	@Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<EstimatePreparationApproval> estimateWorkflowService;
	@Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
    private FileStoreService fileStoreService;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@Autowired
    private DesignationService designationService;
	

	@Transactional
	public DNITCreation saveEstimatePreparationData(HttpServletRequest request,
			DNITCreation dNITCreation,Long approvalPosition,String approvalComment,String approvalDesignation,String workFlowAction) {
		// TODO Auto-generated method stub

		List<BoQDetails> list = new ArrayList<BoQDetails>();
		List<BoQDetails> list2 =  dNITCreation.getBoQDetailsList();
			for (BoQDetails boq : dNITCreation.getBoQDetailsList()) {
				boq.setDnitCreation(dNITCreation);
				list.add(boq);
			}
			dNITCreation.setNewBoQDetailsList(list);
		
		if((workFlowAction.equalsIgnoreCase("Forward/Reassign") || workFlowAction.equalsIgnoreCase("Save as Draft")) && dNITCreation.getStatus() == null)
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
			
		}
		else if (workFlowAction.equalsIgnoreCase("Save as Draft") && dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
		}
		else if ((workFlowAction.equalsIgnoreCase("Forward/Reassign") )&& dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		//edited
		else if ((workFlowAction.equalsIgnoreCase("Forward/Reassign") )&& dNITCreation.getStatus().getCode().equals("Approved"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Approved"));
		}
		
		
		
		DNITCreation savedDNITCreation = dNITCreationRepository
				.save(dNITCreation);
		List<DocumentUpload> files = dNITCreation.getDocumentDetail() == null ? null
				: dNITCreation.getDocumentDetail();
		List<DocumentUpload> documentDetails = new ArrayList<>();
		
		
			documentDetails = getDocumentDetails(files, dNITCreation,
					"Works_Dnit");
			
		
		
		if (!documentDetails.isEmpty()) {
			savedDNITCreation.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		createEstimateWorkflowTransition(savedDNITCreation, approvalPosition, approvalComment, null,
                workFlowAction,approvalDesignation);
		DNITCreation savedDNITCreation1=dNITCreationRepository
		.save(savedDNITCreation);
		//updateBoqdetails(list2,savedDNITCreation1);
		return savedDNITCreation1;
	}
	

	@Transactional
	public DNITCreation saveDnitByEstimatePreparationData(HttpServletRequest request,
			DNITCreation dNITCreation,Long approvalPosition,String approvalComment,String approvalDesignation,String workFlowAction) {
		// TODO Auto-generated method stub

		List<BoQDetails> list = new ArrayList<BoQDetails>();
		
			for (BoQDetails boq : dNITCreation.getBoQDetailsList()) {
				boq.setEstimatePreparationApproval(dNITCreation.getEstimatePreparationApproval());
				boq.setDnitCreation(dNITCreation);
				list.add(boq);
			}
			dNITCreation.setNewBoQDetailsList(list);
		
		if((workFlowAction.equalsIgnoreCase("Forward/Reassign") || workFlowAction.equalsIgnoreCase("Save as Draft")) && dNITCreation.getStatus() == null)
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
			
		}
		else if (workFlowAction.equalsIgnoreCase("Save as Draft") && dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
		}
		else if ((workFlowAction.equalsIgnoreCase("Forward/Reassign"))&& dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Approved"));
		}
		
		DNITCreation savedDNITCreation = dNITCreationRepository
				.save(dNITCreation);
		List<DocumentUpload> files = dNITCreation.getDocumentDetail() == null ? null
				: dNITCreation.getDocumentDetail();
		List<DocumentUpload> documentDetails = new ArrayList<>();
		
		
			documentDetails = getDocumentDetails(files, dNITCreation,
					"Works_Dnit");
			
		
		
		if (!documentDetails.isEmpty()) {
			savedDNITCreation.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		createEstimateWorkflowTransition(savedDNITCreation, approvalPosition, approvalComment, null,
                workFlowAction,approvalDesignation);
		DNITCreation savedDNITCreation1=dNITCreationRepository
		.save(savedDNITCreation);
		return savedDNITCreation1;
	}
	
	
	
		
	public void createEstimateWorkflowTransition(final DNITCreation estimatePreparationApproval,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction,final String approvalDesignation) {
		LOG.info(" Create WorkFlow Transition Started  ...");
		
		final User user = securityUtils.getCurrentUser();
        final DateTime currentDate = new DateTime();
        Map<String, String> finalDesignationNames = new HashMap<>();
        final String currState = "";
        String stateValue = "";
        WorkFlowMatrix wfmatrix;
        Position owenrPos = new Position();
        org.egov.pims.commons.Designation designation=null;
        if( approvalDesignation != null &&  !approvalDesignation.isEmpty())
        {
        	designation = designationService.getDesignationById(Long.parseLong(approvalDesignation));
        }
         
        if(designation != null)
        {
     	   System.out.println("Designation:::::::::::"+designation.getName().toUpperCase());
        }
        if(workFlowAction.equalsIgnoreCase("Save As Draft"))
        {
        	owenrPos.setId(user.getId());
        }
        else
        {
        	owenrPos.setId(approvalPosition);
        }
        
        if (null == estimatePreparationApproval.getState() && workFlowAction.equals("Save As Draft")) {
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, "NEW", null);
        	estimatePreparationApproval.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("DNIT")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        
        }
        else if (null == estimatePreparationApproval.getState() && !workFlowAction.equals("Save As Draft"))
        {
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, "NEW", null);
        	String statetype="Pending With "+designation.getName().toUpperCase();
        	estimatePreparationApproval.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue(statetype).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("DNIT")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        }
        else
        {
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, estimatePreparationApproval.getCurrentState().getValue(), null);
        	if(workFlowAction.equalsIgnoreCase("Save As Draft"))
        	{
        		wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                        null, additionalRule, "SaveAsDraft", null);
            	estimatePreparationApproval.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("DNIT");
        	}
        	else if(workFlowAction.equalsIgnoreCase("Forward/Reassign"))
        	{
        		String statetype="Pending With "+designation.getName().toUpperCase();
        		estimatePreparationApproval.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(statetype).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("DNIT");
        		

        	}
        	else if(workFlowAction.equalsIgnoreCase("Approve"))
        	{
        		estimatePreparationApproval.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue("END").withDateInfo(new Date())
                .withNextAction("DNIT Approved")
                .withNatureOfTask("DNIT");

        	}
        }
        
	}
	
	@Transactional
	public EstimatePreparationApproval editEstimatePreparationData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub

		// List<BoQDetails> list = new ArrayList<BoQDetails>();
		//for (BoQDetails boq : estimatePreparationApproval.getBoQDetailsList()) {
			//boq.setEstimatePreparationApproval(estimatePreparationApproval);
			// list.add(boq);
		//}
		// estimatePreparationApproval.setNewBoQDetailsList(null);
		 
		/*EstimatePreparationApproval e = new EstimatePreparationApproval();
		
		e.setWorkLocation(estimatePreparationApproval.getWorkLocation());
		e.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		e.setId(estimatePreparationApproval.getId());
		applyAuditing(e);*/
		
		/*EstimatePreparationApproval savedEstimatePreparationApproval1 = estimatePreparationApprovalRepository
				.save(e);*/

		Long id = estimatePreparationApproval.getId();
		EstimatePreparationApproval estimate =estimatePreparationApprovalRepository
				.findById(estimatePreparationApproval.getId());
		
		estimate.setWorkLocation(estimatePreparationApproval.getWorkLocation());
		applyAuditing(estimate);

		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalRepository
				.save(estimate);
		persistenceService.getSession().flush();
		return savedEstimatePreparationApproval;
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
	
	public List<DocumentUpload> getDocumentDetails(final List<DocumentUpload> files, final Object object,
            final String objectType) {
        final List<DocumentUpload> documentDetailsList = new ArrayList<>();

        Long id;
        Method method;
        try {
            method = object.getClass().getMethod("getId", null);
            id = (Long) method.invoke(object, null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new ApplicationRuntimeException("error.expense.bill.document.error", e);
        }

        for (DocumentUpload doc : files) {
            final DocumentUpload documentDetails = new DocumentUpload();
            documentDetails.setObjectId(id);
           
            if(doc.getObjectType()!=null) {
	            if(doc.getObjectType().equals("roughWorkFile")) {
	            	
	            	 documentDetails.setObjectType("roughWorkFile");
	            	 documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
	                         doc.getContentType(), "roughWorkFile"));
	            }
            }
            else {
            	 documentDetails.setObjectType(objectType);
            	 documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
                         doc.getContentType(), "Works_Dnit"));
            }
           
            documentDetailsList.add(documentDetails);

        }
        return documentDetailsList;
    }
	
	
	
	
	
	
	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}
	
	public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
		return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	}
	
	public String getEmployeeName(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	    }
}
