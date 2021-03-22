package org.egov.works.boq.service;

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
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.model.bills.DocumentUpload;
import org.egov.pims.commons.Position;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.PaymentDistribution;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.entity.WorkOrderAgreementRESTPOJO;
import org.egov.works.boq.repository.WorkOrderAgreementRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BoQDetailsService {
	
	private static final Logger LOG = LoggerFactory.getLogger(BoQDetailsService.class);

	@Autowired
	private WorkOrderAgreementRepository workOrderAgreementRepository;
	@Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
	
	@Autowired
    private SecurityUtils securityUtils;
	@Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<WorkOrderAgreement> workAgreementWorkflowService;
	@Autowired
    private FileStoreService fileStoreService;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@Autowired
    private DesignationService designationService;
	@Autowired
	protected MicroserviceUtils microserviceUtils;
	

	@Transactional
	public WorkOrderAgreement saveBoQDetailsData(HttpServletRequest request, WorkOrderAgreement workOrderAgreement,Long approvalPosition,String approvalComment,String approvalDesignation,String workFlowAction) {
		// TODO Auto-generated method stub
		List<BoQDetails> list = new ArrayList<BoQDetails>();
		for (BoQDetails boq : workOrderAgreement.getBoQDetailsList()) {
			boq.setWorkOrderAgreement(workOrderAgreement);
			list.add(boq);
		}
		List<PaymentDistribution> PaymentDistributionlist = new ArrayList<PaymentDistribution>();
		for (PaymentDistribution boq : workOrderAgreement.getPaymentDistribution()) {
			boq.setWorkOrderAgreement(workOrderAgreement);
			PaymentDistributionlist.add(boq);
		}
		workOrderAgreement.setPaymentDistribution(PaymentDistributionlist);
		workOrderAgreement.setNewBoQDetailsList(list);
		if((workFlowAction.equalsIgnoreCase("Forward/Reassign") || workFlowAction.equalsIgnoreCase("Save as Draft")) && workOrderAgreement.getStatus() == null)
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Pending for Approval"));
		}
		else if (workFlowAction.equalsIgnoreCase("Save as Draft") && workOrderAgreement.getStatus().getCode().equals("Pending for Approval"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign")) && workOrderAgreement.getStatus().getCode().equals("Pending for Approval"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve")) && workOrderAgreement.getStatus().getCode().equals("Pending for Approval"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Approved"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign")) && (workOrderAgreement.getProject_closure_comments() == null || workOrderAgreement.getProject_closure_comments().isEmpty()) && workOrderAgreement.getStatus().getCode().equals("Approved"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Project Modification Initiated"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve")) && (workOrderAgreement.getProject_closure_comments() == null || workOrderAgreement.getProject_closure_comments().isEmpty()) && workOrderAgreement.getStatus().getCode().equals("Approved"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Approved"));
		}
		
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign")) && (workOrderAgreement.getProject_closure_comments() != null && !workOrderAgreement.getProject_closure_comments().isEmpty()) && workOrderAgreement.getStatus().getCode().equals("Approved"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Project Closure Initiated"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve")) && (workOrderAgreement.getProject_closure_comments() != null && !workOrderAgreement.getProject_closure_comments().isEmpty()) && workOrderAgreement.getStatus().getCode().equals("Approved"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Project Closed"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign")) && workOrderAgreement.getStatus().getCode().equals("Project Modification Initiated"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Project Modification Initiated"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve")) && workOrderAgreement.getStatus().getCode().equals("Project Modification Initiated"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Approved"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign")) && workOrderAgreement.getStatus().getCode().equals("Project Closure Initiated"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Project Closure Initiated"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve")) && workOrderAgreement.getStatus().getCode().equals("Project Closure Initiated"))
		{
			workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Project Closed"));
		}

		WorkOrderAgreement savedWorkOrderAgreement = workOrderAgreementRepository.save(workOrderAgreement);
		List<DocumentUpload> files = workOrderAgreement.getDocumentDetail() == null ? null
				: workOrderAgreement.getDocumentDetail();
		final List<DocumentUpload> documentDetails;
		
		documentDetails = getDocumentDetails(files, workOrderAgreement,
				"Works_Agreement");
		if (!documentDetails.isEmpty()) {
			savedWorkOrderAgreement.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		createEstimateWorkflowTransition(savedWorkOrderAgreement, approvalPosition, approvalComment, null,
                workFlowAction,approvalDesignation);
		WorkOrderAgreement savedWorkOrderAgreement1 = workOrderAgreementRepository.save(savedWorkOrderAgreement);
		return savedWorkOrderAgreement1;

	}

	@Transactional
	public WorkOrderAgreement viewWorkData(Long id) {
		// TODO Auto-generated method stub
		WorkOrderAgreement viewWorkDataResponse = workOrderAgreementRepository.findById(id);

		return viewWorkDataResponse;
	}

	@Transactional
	public List<WorkOrderAgreement> searchWorkOrderAgreement(HttpServletRequest request,
			WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub

		List<WorkOrderAgreement> workDetails = workOrderAgreementRepository.findByParams(
				workOrderAgreement.getWork_start_date(), workOrderAgreement.getWork_end_date(),
				workOrderAgreement.getExecuting_department(), workOrderAgreement.getWork_agreement_status(),
				workOrderAgreement.getWork_number());
		return workDetails;
	}

	@Transactional
	public WorkOrderAgreement saveBoqData(HttpServletRequest request, WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub

		WorkOrderAgreement boqList = workOrderAgreementRepository.findById(workOrderAgreement.getId());

		for (BoQDetails boqDb : boqList.getNewBoQDetailsList()) {
			for (BoQDetails boqUI : workOrderAgreement.getNewBoQDetailsList()) {
				if (boqDb.getSlNo() == boqUI.getSlNo()) {
					boqDb.setAmount(boqUI.getAmount());
					boqDb.setItem_description(boqUI.getItem_description());
					boqDb.setQuantity(boqUI.getQuantity());
					boqDb.setRate(boqUI.getRate());
					boqDb.setRef_dsr(boqUI.getRef_dsr());
					boqDb.setUnit(boqUI.getUnit());
				}
			}
		}
		WorkOrderAgreement saveBoqDetails = workOrderAgreementRepository.save(boqList);

		return saveBoqDetails;
	}

	@Transactional
	public List<WorkOrderAgreement> searchclosure(HttpServletRequest request, WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub
		workOrderAgreement.setWork_agreement_status("Approved");
		List<WorkOrderAgreement> searchclosure = workOrderAgreementRepository.findByParam(
				workOrderAgreement.getWork_start_date(), workOrderAgreement.getWork_end_date(),
				 workOrderAgreement.getWork_agreement_status());
		return searchclosure;
	}
	public void createEstimateWorkflowTransition(WorkOrderAgreement savedWorkOrderAgreement,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction,final String approvalDesignation) {
		LOG.info(" Create WorkFlow Transition Started  ...");
		
		final User user = securityUtils.getCurrentUser();
        final DateTime currentDate = new DateTime();
        Map<String, String> finalDesignationNames = new HashMap<>();
        final String currState = "";
        String stateValue = "";
        WorkFlowMatrix wfmatrix;
        org.egov.pims.commons.Designation designation = null;
        if( approvalDesignation != null &&  !approvalDesignation.isEmpty())
        {
        	designation = designationService.getDesignationById(Long.parseLong(approvalDesignation));
        }
         
        if(designation != null)
        {
     	   System.out.println("Designation:::::::::::"+designation.getName().toUpperCase());
        }
        Position owenrPos = new Position();
        if(workFlowAction.equalsIgnoreCase("Save As Draft"))
        {
        	owenrPos.setId(user.getId());
        }
        else
        {
        	owenrPos.setId(approvalPosition);
        }
        
        if (null == savedWorkOrderAgreement.getState() && workFlowAction.equals("Save As Draft")) {
        	wfmatrix = workAgreementWorkflowService.getWfMatrix(savedWorkOrderAgreement.getStateType(), null,
                    null, additionalRule, "NEW", null);
        	savedWorkOrderAgreement.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("Works Agreement")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        
        }
        else if (null == savedWorkOrderAgreement.getState() && !workFlowAction.equals("Save As Draft"))
        {
        	wfmatrix = workAgreementWorkflowService.getWfMatrix(savedWorkOrderAgreement.getStateType(), null,
                    null, additionalRule, "NEW", null);
        	String statetype="Pending With "+designation.getName().toUpperCase();
        	savedWorkOrderAgreement.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue(statetype).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("Works Agreement")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        }
        else
        {
        	wfmatrix = workAgreementWorkflowService.getWfMatrix(savedWorkOrderAgreement.getStateType(), null,
                    null, additionalRule, savedWorkOrderAgreement.getCurrentState().getValue(), null);
        	System.out.println("savedWorkOrderAgreement.getCurrentState().getValue() :: "+savedWorkOrderAgreement.getCurrentState().getValue());
        	if(savedWorkOrderAgreement.getCurrentState().getValue().equals("END") && !workFlowAction.equalsIgnoreCase("Approve"))
        	{
        		wfmatrix = workAgreementWorkflowService.getWfMatrix(savedWorkOrderAgreement.getStateType(), null,
                        null, additionalRule, "NEW", null);
        		String statetype="Pending With "+designation.getName().toUpperCase();
            	savedWorkOrderAgreement.transition().startNext().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(statetype).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Agreement")
                .withCreatedBy(user.getId())
                .withtLastModifiedBy(user.getId());
        	}
        	else if(workFlowAction.equalsIgnoreCase("Save As Draft"))
        	{
        		wfmatrix = workAgreementWorkflowService.getWfMatrix(savedWorkOrderAgreement.getStateType(), null,
                        null, additionalRule, "SaveAsDraft", null);
            	savedWorkOrderAgreement.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Agreement");
        	}
        	else if(workFlowAction.equalsIgnoreCase("Forward/Reassign"))
        	{
        		String statetype="Pending With "+designation.getName().toUpperCase();
        		savedWorkOrderAgreement.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(statetype).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Agreement");

        	}
        	else if(workFlowAction.equalsIgnoreCase("Approve") && !savedWorkOrderAgreement.getCurrentState().getValue().equals("END"))
        	{
        		savedWorkOrderAgreement.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue("END").withDateInfo(new Date())
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Agreement");

        	}
        }
        
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
            documentDetails.setObjectType(objectType);
            documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
                    doc.getContentType(), "Works_Agreement"));
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
	
	@Transactional
	public WorkOrderAgreement saveProgress(HttpServletRequest request, WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub
		List<BoQDetails> list = new ArrayList<BoQDetails>();
		for (BoQDetails boq : workOrderAgreement.getBoQDetailsList()) {
			boq.setWorkOrderAgreement(workOrderAgreement);
			list.add(boq);
		}
		workOrderAgreement.setNewBoQDetailsList(list);
		workOrderAgreement.setStatus(egwStatusDAO.getStatusByModuleAndCode("WorkOrderAgreement", "Project Closed"));
		WorkOrderAgreement savedWorkOrderAgreement = workOrderAgreementRepository.save(workOrderAgreement);
		
		return savedWorkOrderAgreement;

	}
	
	public String getEmployeeName(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	    }

	public List<WorkOrderAgreementRESTPOJO>getAllWorkOrderAgreementRest(){
		return workOrderAgreementRepository.getAllWorkOrderAgreement();
	}

}
