package org.egov.works.estimatepreparationapproval.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
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

	@Transactional
	public EstimatePreparationApproval saveEstimatePreparationData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval,Long approvalPosition,String approvalComment,String approvalDesignation,String workFlowAction) {
		// TODO Auto-generated method stub

		List<BoQDetails> list = new ArrayList<BoQDetails>();
		if(estimatePreparationApproval.getBoQDetailsList() != null && !estimatePreparationApproval.getBoQDetailsList().isEmpty())
		{
			for (BoQDetails boq : estimatePreparationApproval.getBoQDetailsList()) {
				boq.setEstimatePreparationApproval(estimatePreparationApproval);
				list.add(boq);
			}
			estimatePreparationApproval.setNewBoQDetailsList(list);
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
        owenrPos.setId(approvalPosition);
        if (null == estimatePreparationApproval.getState()) {
        	estimatePreparationApproval.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos)
            .withNextAction("")
            .withNatureOfTask("Works Estimate")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        
        }
        else
        {
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, estimatePreparationApproval.getCurrentState().getValue(), null);

        	if(workFlowAction.equalsIgnoreCase("Forward"))
        	{
        		estimatePreparationApproval.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos)
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Estimate");

        	}
        	else if(workFlowAction.equalsIgnoreCase("Approve"))
        	{
        		estimatePreparationApproval.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(stateValue).withDateInfo(new Date())
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("Works Estimate");

        	}
        }
        
	}

}
