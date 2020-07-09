package org.egov.apnimandi.transactions.service.workflow;

import java.util.Date;

import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;
import org.egov.apnimandi.utils.constants.ApnimandiConstants;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ApnimandiCollectionWorkflowCustomImpl implements ApnimandiCollectionWorkflowCustom{
	private static final Logger LOG = LoggerFactory.getLogger(ApnimandiCollectionWorkflowCustomImpl.class);
	
	@Autowired
    private SecurityUtils securityUtils;
	
	@Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;
    
    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<ApnimandiCollectionDetails> apnimandiCollectionWorkflowService;

	@Override
	public void createCommonWorkflowTransition(ApnimandiCollectionDetails apnimandiCollectionDetails, Long approvalPosition, String approvalComent, String workFlowAction) {
		if (LOG.isDebugEnabled()) {
            LOG.debug(" Create WorkFlow Transition Started  ...");
        }
		final User user = securityUtils.getCurrentUser();
		WorkFlowMatrix wfmatrix = null;
        final DateTime currentDate = new DateTime();	
        
        if (apnimandiCollectionDetails!=null && 
        		(apnimandiCollectionDetails.getState()==null || ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_REJECTED.equalsIgnoreCase(apnimandiCollectionDetails.getStatus().getCode()))){            
            // IF REJECTED APPLICATION GOT DELETED THEN TRANSITION OCCUR HERE
            if (ApnimandiConstants.DELETE.equalsIgnoreCase(workFlowAction)) {
            	endWorkflow(apnimandiCollectionDetails, approvalComent, user, currentDate);
            } else {
            	if(ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_REJECTED.equalsIgnoreCase(apnimandiCollectionDetails.getStatus().getCode())) {
            		wfmatrix = apnimandiCollectionWorkflowService.getWfMatrix(apnimandiCollectionDetails.getStateType(), null, null, null, apnimandiCollectionDetails.getCurrentState().getValue(), null);
            		apnimandiCollectionDetails.setStatus(getStatusByPassingStatusCode(ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_RESUBMITTED));
            	}else {
            		wfmatrix = apnimandiCollectionWorkflowService.getWfMatrix(apnimandiCollectionDetails.getStateType(), null, null, null, ApnimandiConstants.WF_NEW_STATE, null);
                    apnimandiCollectionDetails.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            	}                
                if (null == apnimandiCollectionDetails.getState()
                        || ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_REJECTED.equalsIgnoreCase(apnimandiCollectionDetails.getStatus().getCode())) {
                    apnimandiCollectionDetails.transition().start()
                            .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_COLLECTION)
                            .withInitiator(user.getId());
                } else {
                    apnimandiCollectionDetails.transition().progressWithStateCopy()
                            .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_COLLECTION);
                }
            }
        } else if (ApnimandiConstants.REJECT.equalsIgnoreCase(workFlowAction)) {
            apnimandiCollectionDetails.setStatus(getStatusByPassingStatusCode(ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_REJECTED));
            rejectionWorkflowTransition(apnimandiCollectionDetails, approvalComent, user, currentDate);
        } else if (ApnimandiConstants.APPROVE.equalsIgnoreCase(workFlowAction)) {
            wfmatrix = apnimandiCollectionWorkflowService.getWfMatrix(apnimandiCollectionDetails.getStateType(), null, null, null,
                    apnimandiCollectionDetails.getCurrentState().getValue(), apnimandiCollectionDetails.getCurrentState().getNextAction());
            apnimandiCollectionDetails.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            if ("END".equalsIgnoreCase(wfmatrix.getNextAction())) {
            	endWorkflow(apnimandiCollectionDetails, approvalComent, user, currentDate);
            } else {
                apnimandiCollectionDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                        .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_COLLECTION);
            }
        }else {
            wfmatrix = apnimandiCollectionWorkflowService.getWfMatrix(apnimandiCollectionDetails.getStateType(), null, null, null,
                    apnimandiCollectionDetails.getCurrentState().getValue(), null);
            apnimandiCollectionDetails.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            apnimandiCollectionDetails.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                    .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                    .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_COLLECTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(" WorkFlow Transition Completed  ...");
        }
	}
	
	private void rejectionWorkflowTransition(ApnimandiCollectionDetails apnimandiCollectionDetails, String approvalComent, final User user,
            final DateTime currentDate) {
        WorkFlowMatrix wfmatrix = apnimandiCollectionWorkflowService.getWfMatrix(apnimandiCollectionDetails.getStateType(), null, null, null,
        		ApnimandiConstants.WF_REJECT_STATE, null);
        apnimandiCollectionDetails.transition().progressWithStateCopy()
                .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                .withComments(approvalComent).withStateValue(ApnimandiConstants.WF_REJECT_STATE)
                .withDateInfo(currentDate.toDate())
                .withOwner(apnimandiCollectionDetails.getState().getInitiatorPosition())
                .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_COLLECTION);
    }
	
	private void endWorkflow(ApnimandiCollectionDetails apnimandiCollectionDetails, String approvalComent, final User user,
            final DateTime currentDate) {
		apnimandiCollectionDetails.transition().end()
                .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                .withComments(approvalComent).withDateInfo(currentDate.toDate())
                .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_COLLECTION)
                .withNextAction("END");
    }
	
	private EgwStatus getStatusByPassingModuleAndCode(WorkFlowMatrix wfmatrix) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_COLLECTION, wfmatrix.getNextStatus());
    }
	
	private EgwStatus getStatusByPassingStatusCode(String statusCode) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_COLLECTION, statusCode);
    }
}
