package org.egov.apnimandi.transactions.service.workflow;

import java.util.Date;
import org.egov.apnimandi.transactions.entity.ApnimandiContractor;
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
public class ApnimandiContractorWorkflowCustomImpl implements ApnimandiContractorWorkflowCustom{
	private static final Logger LOG = LoggerFactory.getLogger(ApnimandiContractorWorkflowCustomImpl.class);
	
	@Autowired
    private SecurityUtils securityUtils;
	
	@Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;
    
    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<ApnimandiContractor> apnimandiContractorWorkflowService;
	
	@Override
	public void createCommonWorkflowTransition(ApnimandiContractor apnimandiContractor, Long approvalPosition, String approvalComent, String workFlowAction) {
		if (LOG.isDebugEnabled()) {
            LOG.debug(" Create WorkFlow Transition Started  ...");
        }
		final User user = securityUtils.getCurrentUser();
		WorkFlowMatrix wfmatrix = null;
        final DateTime currentDate = new DateTime();	
        
        if (apnimandiContractor!=null && 
        		(apnimandiContractor.getState()==null || ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_REJECTED.equalsIgnoreCase(apnimandiContractor.getStatus().getCode()))){            
            // IF REJECTED APPLICATION GOT DELETED THEN TRANSITION OCCUR HERE
            if (ApnimandiConstants.DELETE.equalsIgnoreCase(workFlowAction)) {
            	endWorkflow(apnimandiContractor, approvalComent, user, currentDate);
            } else {
            	if(ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_REJECTED.equalsIgnoreCase(apnimandiContractor.getStatus().getCode())) {
            		wfmatrix = apnimandiContractorWorkflowService.getWfMatrix(apnimandiContractor.getStateType(), null, null, null, apnimandiContractor.getCurrentState().getValue(), null);
            		apnimandiContractor.setStatus(getStatusByPassingStatusCode(ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_RESUBMITTED));
            	}else {
            		wfmatrix = apnimandiContractorWorkflowService.getWfMatrix(apnimandiContractor.getStateType(), null, null, null, ApnimandiConstants.WF_NEW_STATE, null);
                    apnimandiContractor.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            	}                
                if (null == apnimandiContractor.getState()
                        || ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_REJECTED.equalsIgnoreCase(apnimandiContractor.getStatus().getCode())) {
                    apnimandiContractor.transition().start()
                            .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_CONTRACTOR)
                            .withInitiator(user.getId());
                } else {
                    apnimandiContractor.transition().progressWithStateCopy()
                            .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_CONTRACTOR);
                }
            }
        } else if (ApnimandiConstants.REJECT.equalsIgnoreCase(workFlowAction)) {
            apnimandiContractor.setStatus(getStatusByPassingStatusCode(ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_REJECTED));
            rejectionWorkflowTransition(apnimandiContractor, approvalComent, user, currentDate);
        } else if (ApnimandiConstants.APPROVE.equalsIgnoreCase(workFlowAction)) {
            wfmatrix = apnimandiContractorWorkflowService.getWfMatrix(apnimandiContractor.getStateType(), null, null, null,
                    apnimandiContractor.getCurrentState().getValue(), apnimandiContractor.getCurrentState().getNextAction());
            apnimandiContractor.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            if ("END".equalsIgnoreCase(wfmatrix.getNextAction())) {
            	endWorkflow(apnimandiContractor, approvalComent, user, currentDate);
            } else {
                apnimandiContractor.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                        .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_CONTRACTOR);
            }
        }else {
            wfmatrix = apnimandiContractorWorkflowService.getWfMatrix(apnimandiContractor.getStateType(), null, null, null,
                    apnimandiContractor.getCurrentState().getValue(), null);
            apnimandiContractor.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            apnimandiContractor.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                    .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                    .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_CONTRACTOR);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(" WorkFlow Transition Completed  ...");
        }
	}
	
	private void rejectionWorkflowTransition(ApnimandiContractor apnimandiContractor, String approvalComent, final User user,
            final DateTime currentDate) {
        WorkFlowMatrix wfmatrix = apnimandiContractorWorkflowService.getWfMatrix(apnimandiContractor.getStateType(), null, null, null, ApnimandiConstants.WF_REJECT_STATE, null);
        apnimandiContractor.transition().progressWithStateCopy()
                .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                .withComments(approvalComent).withStateValue(ApnimandiConstants.WF_REJECT_STATE)
                .withDateInfo(currentDate.toDate())
                .withOwner(apnimandiContractor.getState().getInitiatorPosition())
                .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_CONTRACTOR);
    }
	
	private void endWorkflow(ApnimandiContractor apnimandiContractor, String approvalComent, final User user,
            final DateTime currentDate) {
		apnimandiContractor.transition().end()
                .withSenderName(user.getUsername() + ApnimandiConstants.COLON_CONCATE + user.getName())
                .withComments(approvalComent).withDateInfo(currentDate.toDate())
                .withNatureOfTask(ApnimandiConstants.NATURE_OF_WORK_CONTRACTOR)
                .withNextAction("END");
    }
	
	private EgwStatus getStatusByPassingModuleAndCode(WorkFlowMatrix wfmatrix) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_CONTRACTOR, wfmatrix.getNextStatus());
    }
	
	private EgwStatus getStatusByPassingStatusCode(String statusCode) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_CONTRACTOR, statusCode);
    }
}
