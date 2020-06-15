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
 */
package org.egov.council.service.workflow;

import java.util.Date;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.utils.constants.CouncilConstants;
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
public class MeetingMomWorkflowCustomImpl implements MeetingMomWorkflowCustom {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingMomWorkflowCustomImpl.class);

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<CouncilMeeting> CouncilMeetingWorkflowService;

    @Override
    public void createCommonWorkflowTransition(CouncilMeeting councilMeeting, Long approvalPosition,
            String approvalComent, String workFlowAction) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(" Create WorkFlow Transition Started  ...");
        }

        final User user = securityUtils.getCurrentUser();
        
        WorkFlowMatrix wfmatrix = null;
        final DateTime currentDate = new DateTime();
        
        // New Entry
        if (councilMeeting!=null &&  councilMeeting.getState()==null
        		||CouncilConstants.REJECTED.equalsIgnoreCase(councilMeeting.getStatus().getCode())){
            
            // IF REJECTED APPLICATION GOT CANCELLED THEN TRANSITION OCCUR HERE
            if (CouncilConstants.CANCEL
                    .equalsIgnoreCase(workFlowAction)) {
                cancelworkflow(councilMeeting, approvalComent, user, currentDate);
            }            
            else {

                wfmatrix = CouncilMeetingWorkflowService.getWfMatrix(councilMeeting.getStateType(), null, null, "CouncilCommonWorkflow",
                        CouncilConstants.WF_NEW_STATE, null);
                councilMeeting.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
                if (null == councilMeeting.getState()
                        || CouncilConstants.REJECTED.equalsIgnoreCase(councilMeeting.getStatus().getCode())) {
                    councilMeeting.transition().start()
                            .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(CouncilConstants.NATURE_OF_WORK_MOM)
                            .withInitiator(user.getId());
                } else {
                    councilMeeting.transition().progressWithStateCopy()
                            .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(CouncilConstants.NATURE_OF_WORK_MOM);
                }
            }
        } else if (CouncilConstants.WF_STATE_REJECT
                .equalsIgnoreCase(workFlowAction)) {
            councilMeeting.setStatus(getStatusByPassingStatusCode(CouncilConstants.REJECTED));
            rejectionWorkflowTransition(councilMeeting, approvalComent, user, currentDate);
        }
        else if (CouncilConstants.WF_APPROVE_BUTTON.equalsIgnoreCase(workFlowAction)) {
            wfmatrix = CouncilMeetingWorkflowService.getWfMatrix(councilMeeting.getStateType(), null, null, null,
                    councilMeeting.getCurrentState().getValue(), councilMeeting.getCurrentState().getNextAction());
            councilMeeting.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            if ("END".equalsIgnoreCase(wfmatrix.getNextAction())) {
                cancelworkflow(councilMeeting, approvalComent, user, currentDate);
            } else {
                councilMeeting.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                        .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(CouncilConstants.NATURE_OF_WORK_MOM);
            }
        } 
        else {
            wfmatrix = CouncilMeetingWorkflowService.getWfMatrix(councilMeeting.getStateType(), null, null, null,
                    councilMeeting.getCurrentState().getValue(), null);
            councilMeeting.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            councilMeeting.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                    .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                    .withNatureOfTask(CouncilConstants.NATURE_OF_WORK_MOM);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(" WorkFlow Transition Completed  ...");
        }
    }

    private void cancelworkflow(CouncilMeeting councilMeeting, String approvalComent, final User user,
            final DateTime currentDate) {
        councilMeeting.transition().end()
                .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                .withComments(approvalComent).withDateInfo(currentDate.toDate())
                .withNatureOfTask(CouncilConstants.NATURE_OF_WORK_MOM)
                .withNextAction("END");
    }

    private void rejectionWorkflowTransition(CouncilMeeting councilMeeting, String approvalComent, final User user,
            final DateTime currentDate) {
       
        WorkFlowMatrix wfmatrix = CouncilMeetingWorkflowService.getWfMatrix(councilMeeting.getStateType(), null, null, "CouncilCommonWorkflow",
                CouncilConstants.WF_REJECT_STATE, null);
        councilMeeting.transition().progressWithStateCopy()
                .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                .withComments(approvalComent).withStateValue(CouncilConstants.WF_REJECT_STATE)
                .withDateInfo(currentDate.toDate())
                .withOwner(councilMeeting.getState().getInitiatorPosition())
                .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(CouncilConstants.NATURE_OF_WORK_MOM);
    }

    private EgwStatus getStatusByPassingModuleAndCode(WorkFlowMatrix wfmatrix) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(CouncilConstants.COUNCIL_MEETING_MODULE_NAME,
                wfmatrix.getNextStatus());
    }

    private EgwStatus getStatusByPassingStatusCode(String statusCode) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(CouncilConstants.COUNCIL_MEETING_MODULE_NAME, statusCode);
    }
}
