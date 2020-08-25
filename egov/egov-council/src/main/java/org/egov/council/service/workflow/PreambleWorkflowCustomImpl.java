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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.service.CouncilRouterService;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Role;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.pims.commons.Position;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PreambleWorkflowCustomImpl implements PreambleWorkflowCustom {
    private static final String ANONYMOUS = "Anonymous";

    private static final Logger LOG = LoggerFactory.getLogger(PreambleWorkflowCustomImpl.class);

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<CouncilPreamble> councilPreambleWorkflowService;

    @Autowired
    private CouncilRouterService councilRouterService;
    
    @Autowired
    private MicroserviceUtils microserviceUtils;

    @Override
    public void createCommonWorkflowTransition(CouncilPreamble councilPreamble, Long approvalPosition,
            String approvalComent, String workFlowAction) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(" Create WorkFlow Transition Started  ...");
        }

        final User user = securityUtils.getCurrentUser();

        /*EmployeeInfo info = null;
        if (user != null && user.getId() != null)
            info = microserviceUtils.getEmployeeById(user.getId());*/
        
        /*EmployeeInfo approverInfo = null;
        if (null != approvalPosition && approvalPosition != -1 && !approvalPosition.equals(Long.valueOf(0))) {
        	approverInfo = microserviceUtils.getEmployeeById(approvalPosition);
        }*/
        
        WorkFlowMatrix wfmatrix = null;
        final DateTime currentDate = new DateTime();
        //Position pos = null;
        //Assignment wfInitiator = null;
        //List<Assignment> activeAssignment=Collections.emptyList();
        
        /*if (null != councilPreamble.getId()) {
            if (ANONYMOUS.equalsIgnoreCase(assignmentService
                    .getAssignmentsForPosition(councilPreamble.getCreatedBy()).get(0).getEmployee().getName())) {
                wfInitiator = assignmentService
                        .getPrimaryAssignmentForPositon(councilPreamble.getState().getInitiatorPosition());
                if (wfInitiator == null) {
                    activeAssignment = assignmentService
                            .getAssignmentsForPosition(councilPreamble.getState().getInitiatorPosition());
                    wfInitiator = !activeAssignment.isEmpty() ? activeAssignment.get(0) : null;
                }
            } else {
                wfInitiator = assignmentService.getPrimaryAssignmentForUser(councilPreamble.getCreatedBy());

                if (wfInitiator == null) {
                    activeAssignment = assignmentService.getAllActiveEmployeeAssignmentsByEmpId(councilPreamble
                            .getCreatedBy());
                    wfInitiator = !activeAssignment.isEmpty() ? activeAssignment.get(0) : null;
                }
            }

        }
        if (wfInitiator == null && !ANONYMOUS.equalsIgnoreCase(user.getUsername())) {
            activeAssignment = assignmentService.getAllActiveEmployeeAssignmentsByEmpId(user.getId());
            wfInitiator = !activeAssignment.isEmpty() ? activeAssignment.get(0) : null;
        }
        if (null != approvalPosition && approvalPosition != -1 && !approvalPosition.equals(Long.valueOf(0))) {
            pos = positionMasterService.getPositionById(approvalPosition);
        } else {
            pos = wfInitiator != null ? wfInitiator.getPosition() : null;
        }*/
        
        // New Entry
        if (councilPreamble!=null &&  councilPreamble.getState()==null||CouncilConstants.REJECTED.equalsIgnoreCase(councilPreamble.getStatus().getCode())
                || CouncilConstants.WF_ANONYMOUSPREAMBLE_STATE.equalsIgnoreCase(councilPreamble.getState().getValue())
                        && null == councilPreamble.getState().getPreviousOwner()) {
            // IF APPLICATION IS CREATED AND DIRECTLY FORWARDED TO MAYOR
            /*if (CouncilConstants.DESIGNATION_MAYOR.equalsIgnoreCase("")) {
                wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                        CouncilConstants.WF_NEW_STATE, "Application Creation");
                councilPreamble.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
                if (null == councilPreamble.getState() ||CouncilConstants.REJECTED.equalsIgnoreCase(councilPreamble.getStatus().getCode())) {
                    councilPreamble.transition().start()
                            .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(CouncilConstants.NATURE_OF_WORK)
                            .withInitiator(user.getId());
                } else {
                    councilPreamble.transition().progressWithStateCopy()
                            .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(CouncilConstants.NATURE_OF_WORK)
                            .withInitiator(user.getId());
                }
            } 
            // IF REJECTED APPLICATION GOT CANCELLED THEN TRANSITION OCCUR HERE
            else*/
        	if (CouncilConstants.CANCEL
                    .equalsIgnoreCase(workFlowAction)) {
                cancelworkflow(councilPreamble, approvalComent, user, currentDate);
            }            
            else {
            	if(isAgendaAdmin(getEmployee(user.getId()))) {
            		wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, CouncilConstants.COUNCIL_ABA_WORKFLOW,
	                        CouncilConstants.WF_NEW_STATE, null);
            	}else {
	                wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, CouncilConstants.COUNCIL_COMMON_WORKFLOW,
	                        CouncilConstants.WF_NEW_STATE, null);
            	}
                councilPreamble.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
                if (null == councilPreamble.getState()
                        || CouncilConstants.REJECTED.equalsIgnoreCase(councilPreamble.getStatus().getCode())) {
                    councilPreamble.transition().start()
                            .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(CouncilConstants.NATURE_OF_WORK)
                            .withInitiator(user.getId());
                } else {
                    councilPreamble.transition().progressWithStateCopy()
                            .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                            .withComments(approvalComent).withStateValue(wfmatrix.getNextState()).withDateInfo(new Date())
                            .withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                            .withNatureOfTask(CouncilConstants.NATURE_OF_WORK);
                }
            }
        } else if (CouncilConstants.WF_STATE_REJECT
                .equalsIgnoreCase(workFlowAction)) {
            councilPreamble.setStatus(getStatusByPassingStatusCode(CouncilConstants.REJECTED));
            rejectionWorkflowTransition(councilPreamble, approvalComent, user, currentDate);
        }
        else if (CouncilConstants.WF_APPROVE_BUTTON.equalsIgnoreCase(workFlowAction)) {
            wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                    councilPreamble.getCurrentState().getValue(), councilPreamble.getCurrentState().getNextAction());
            councilPreamble.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            if ("END".equalsIgnoreCase(wfmatrix.getNextAction())) {
                cancelworkflow(councilPreamble, approvalComent, user, currentDate);
            } else {
                councilPreamble.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                        .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                        .withNatureOfTask(CouncilConstants.NATURE_OF_WORK);
            }
        } 
        /*else if (CouncilConstants.WF_PROVIDE_INFO_BUTTON.equalsIgnoreCase(workFlowAction)) {
            if (ApplicationThreadLocals.getUserId().equals(
                    wfInitiator != null && wfInitiator.getEmployee() != null ? wfInitiator.getEmployee().getId() : 0)) {
                councilPreamble.setStatus(getStatusByPassingStatusCode("REJECTED"));
                cancelworkflow(councilPreamble, approvalComent, user, currentDate);
            } else {
                rejectionWorkflowTransition(councilPreamble, approvalComent, user, currentDate, wfInitiator);
            }
        }*/
        // IF HOD FORWARD TO MANAGER THEN TRANSITION OCCUR HERE
        /*else if (CouncilConstants.CREATED.equalsIgnoreCase(councilPreamble.getStatus().getCode())
                && CouncilConstants.DESIGNATION_MANAGER.equalsIgnoreCase(
                        pos == null || null == pos.getDeptDesig() ? "" : pos.getDeptDesig().getDesignation().getName())) {
            wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                    CouncilConstants.APPROVED, CouncilConstants.MANAGER_APPROVALPENDING);
            councilPreamble.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            councilPreamble.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComent).withStateValue(wfmatrix.getCurrentState())
                    .withDateInfo(currentDate.toDate()).withOwner(pos).withNextAction(wfmatrix.getPendingActions())
                    .withNatureOfTask(CouncilConstants.NATURE_OF_WORK);
        }*/
        // IF HOD FORWARD TO Commissioner THEN TRANSITION OCCUR HERE
        /*else if (("HODAPPROVED".equalsIgnoreCase(councilPreamble.getStatus().getCode())
                || CouncilConstants.CREATED.equalsIgnoreCase(councilPreamble.getStatus().getCode()))
                        && CouncilConstants.DESIGNATION_COMMISSIONER.equalsIgnoreCase(
                                pos == null || null == pos.getDeptDesig() ? ""
                                        : pos.getDeptDesig().getDesignation().getName())) {
            if (CouncilConstants.CREATED.equalsIgnoreCase(councilPreamble.getStatus().getCode())) {
                wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                        CouncilConstants.APPROVED, CouncilConstants.COMMISSIONER_APPROVALPENDING);
            } else {
                wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                        councilPreamble.getCurrentState().getValue(), CouncilConstants.COMMISSIONER_APPROVALPENDING);
            }
            councilPreamble.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            councilPreamble.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComent).withStateValue(wfmatrix.getCurrentState())
                    .withDateInfo(currentDate.toDate()).withOwner(pos).withNextAction(wfmatrix.getPendingActions())
                    .withNatureOfTask(CouncilConstants.NATURE_OF_WORK);
        }*/
        else {
            wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                    councilPreamble.getCurrentState().getValue(), null);
            councilPreamble.setStatus(getStatusByPassingModuleAndCode(wfmatrix));
            councilPreamble.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                    .withComments(approvalComent).withStateValue(wfmatrix.getNextState())
                    .withDateInfo(currentDate.toDate()).withOwner(approvalPosition).withNextAction(wfmatrix.getNextAction())
                    .withNatureOfTask(CouncilConstants.NATURE_OF_WORK);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(" WorkFlow Transition Completed  ...");
        }
    }

    private void cancelworkflow(CouncilPreamble councilPreamble, String approvalComent, final User user,
            final DateTime currentDate) {
        councilPreamble.transition().end()
                .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                .withComments(approvalComent).withDateInfo(currentDate.toDate())
                .withNatureOfTask(CouncilConstants.NATURE_OF_WORK)
                .withNextAction("END");
    }

    private void rejectionWorkflowTransition(CouncilPreamble councilPreamble, String approvalComent, final User user,
            final DateTime currentDate) {
        WorkFlowMatrix wfmatrix;
        /*if (CouncilConstants.CREATED.equalsIgnoreCase(councilPreamble.getState().getValue())
                && CouncilConstants.COMMISSIONER_APPROVALPENDING.equalsIgnoreCase(councilPreamble.getState().getNextAction())) {
            wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                    CouncilConstants.WF_REJECT_STATE, "Application Rejection");
            councilPreamble.transition().progressWithStateCopy()
                    .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                    .withComments(approvalComent).withStateValue(CouncilConstants.WF_REJECT_STATE)
                    .withDateInfo(currentDate.toDate())
                    .withOwner(wfInitiator != null ? wfInitiator.getPosition() : null)
                    .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(CouncilConstants.NATURE_OF_WORK);
        } else {*/
        
        	if(isAgendaAdmin(getEmployee(councilPreamble.getState().getInitiatorPosition()))) {
        		wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, CouncilConstants.COUNCIL_ABA_WORKFLOW,
                    CouncilConstants.WF_REJECT_STATE, null);
        	}else {
        		wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, CouncilConstants.COUNCIL_COMMON_WORKFLOW,
                        CouncilConstants.WF_REJECT_STATE, null);
        	}
            councilPreamble.transition().progressWithStateCopy()
                    .withSenderName(user.getUsername() + CouncilConstants.COLON_CONCATE + user.getName())
                    .withComments(approvalComent).withStateValue(CouncilConstants.WF_REJECT_STATE)
                    .withDateInfo(currentDate.toDate())
                    .withOwner(councilPreamble.getState().getInitiatorPosition())
                    .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(CouncilConstants.NATURE_OF_WORK);
        //}
    }

    private EgwStatus getStatusByPassingModuleAndCode(WorkFlowMatrix wfmatrix) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(CouncilConstants.PREAMBLE_MODULE_TYPE,
                wfmatrix.getNextStatus());
    }

    private EgwStatus getStatusByPassingStatusCode(String statusCode) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(CouncilConstants.PREAMBLE_MODULE_TYPE, statusCode);
    }

    public void onCreatePreambleAPI(CouncilPreamble councilPreamble) {
        WorkFlowMatrix wfmatrix = councilPreambleWorkflowService.getWfMatrix(councilPreamble.getStateType(), null, null, null,
                CouncilConstants.WF_ANONYMOUSPREAMBLE_STATE, null);
        Position assignee = councilRouterService.getCouncilAssignee(councilPreamble);

        councilPreamble.transition()
                .start()
                .withStateValue(CouncilConstants.WF_ANONYMOUSPREAMBLE_STATE)
                .withOwner(assignee).withNextAction(wfmatrix.getNextAction()).withDateInfo(new Date())
                .withNatureOfTask(CouncilConstants.NATURE_OF_WORK).withInitiator(assignee);
    }
    
    private EmployeeInfo getEmployee(Long userId){
    	if(userId !=0) {
    		EmployeeInfo info = microserviceUtils.getEmployeeById(userId);
	    	return info;
    	}
    	return null;
    }
    
    private boolean isAgendaAdmin(EmployeeInfo info){
    	boolean isAgendaAdmin= false;
    	if(null != info) {
			Optional<Role> adminRole = info.getUser().getRoles().stream()
                    .filter(role -> CouncilConstants.ROLE_AGENDA_BRANCH_ADMIN.equals(role.getCode()))
                    .findFirst();

			if(adminRole.isPresent()) {
				isAgendaAdmin = true;
			}
		}
    	return isAgendaAdmin;
    }
    
    private boolean isAgendaAdmin(User user){
    	boolean isAgendaAdmin= false;
    	if(null != user) {
			Optional<org.egov.infra.admin.master.entity.Role> adminRole = user.getRoles().stream()
                    .filter(role -> CouncilConstants.ROLE_AGENDA_BRANCH_ADMIN.equals(role.getName()))
                    .findFirst();

			if(adminRole.isPresent()) {
				isAgendaAdmin = true;
			}
		}
    	return isAgendaAdmin;
    }
}
