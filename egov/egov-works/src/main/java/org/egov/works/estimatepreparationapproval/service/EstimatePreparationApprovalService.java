package org.egov.works.estimatepreparationapproval.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.commons.Position;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EstimatePreparationApprovalService {
	
	private static final Logger LOG = LoggerFactory.getLogger(EstimatePreparationApprovalService.class);

	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;
	@Autowired
    private SecurityUtils securityUtils;
	@Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<EstimatePreparationApproval> estimateWorkflowService;
	@Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@Transactional
	public EstimatePreparationApproval saveEstimatePreparationData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval,Long approvalPosition,String approvalComment,String approvalDesignation,String workFlowAction) {
		// TODO Auto-generated method stub

		List<BoQDetails> list = new ArrayList<BoQDetails>();
			for (BoQDetails boq : estimatePreparationApproval.getBoQDetailsList()) {
				boq.setEstimatePreparationApproval(estimatePreparationApproval);
				list.add(boq);
			}
			estimatePreparationApproval.setNewBoQDetailsList(list);
		
		if((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equalsIgnoreCase("Save as Draft")) && estimatePreparationApproval.getStatus() == null)
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "Created"));
		}
		else if (workFlowAction.equalsIgnoreCase("Save as Draft") && estimatePreparationApproval.getStatus().getCode().equals("Created"))
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "Created"));
		}
		else if ((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equalsIgnoreCase("Approve"))&& estimatePreparationApproval.getStatus().getCode().equals("Created"))
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equalsIgnoreCase("Approve"))&& estimatePreparationApproval.getStatus().getCode().equals("Pending for Approval"))
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "AA Initiated"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equalsIgnoreCase("Approve"))&& estimatePreparationApproval.getStatus().getCode().equals("AA Initiated"))
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "AA Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equalsIgnoreCase("Approve"))&& estimatePreparationApproval.getStatus().getCode().equals("AA Pending for Approval"))
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "TS Initiated"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equalsIgnoreCase("Approve"))&& estimatePreparationApproval.getStatus().getCode().equals("TS Initiated"))
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "TS Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward") || workFlowAction.equalsIgnoreCase("Approve"))&& estimatePreparationApproval.getStatus().getCode().equals("TS Pending for Approval"))
		{
			estimatePreparationApproval.setStatus(egwStatusDAO.getStatusByModuleAndCode("EstimatePreparationApproval", "Approved"));
		}
		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalRepository
				.save(estimatePreparationApproval);
		
		createEstimateWorkflowTransition(savedEstimatePreparationApproval, approvalPosition, approvalComment, null,
                workFlowAction,approvalDesignation);
		EstimatePreparationApproval savedEstimatePreparationApproval1=estimatePreparationApprovalRepository
		.save(savedEstimatePreparationApproval);
		return savedEstimatePreparationApproval1;
	}
	
	public void createEstimateWorkflowTransition(final EstimatePreparationApproval estimatePreparationApproval,
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
            .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos)
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("Works Estimate")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        
        }
        else if (null == estimatePreparationApproval.getState() && !workFlowAction.equals("Save As Draft"))
        {
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, "NEW", null);
        	estimatePreparationApproval.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue(wfmatrix.getNextState()).withDateInfo(new Date()).withOwner(owenrPos)
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("Works Estimate")
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
                        null, additionalRule, "NEW", null);
            	estimatePreparationApproval.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos)
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Estimate")
                .withCreatedBy(user.getId())
                .withtLastModifiedBy(user.getId());
        	}
        	else if(workFlowAction.equalsIgnoreCase("Forward") || (workFlowAction.equalsIgnoreCase("Approve") && !wfmatrix.getNextState().equals("END")))
        	{
        		estimatePreparationApproval.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(wfmatrix.getNextState()).withDateInfo(new Date()).withOwner(owenrPos)
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Estimate");

        	}
        	else if(workFlowAction.equalsIgnoreCase("Approve") && wfmatrix.getNextState().equals("END"))
        	{
        		estimatePreparationApproval.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Estimate");

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

}
