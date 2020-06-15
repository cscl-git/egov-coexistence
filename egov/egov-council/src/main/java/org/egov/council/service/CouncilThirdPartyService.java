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
package org.egov.council.service;

import org.apache.commons.collections.CollectionUtils;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.entity.CouncilPreamble;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.ApplicationConstant;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CouncilThirdPartyService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CouncilThirdPartyService.class);
    private static final String DEPARTMENT = "department";
    @Autowired
    private EisCommonService eisCommonService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private EgovMasterDataCaching masterDataCache;
    @Autowired
    private MicroserviceUtils microserviceUtils;

    public List<HashMap<String, Object>> getHistory(final Object obj) {
        final List<HashMap<String, Object>> historyTable = new ArrayList<>();
        State workflowState = null;
        final HashMap<String, Object> workFlowHistory = new HashMap<>();
        List<StateHistory> historyList = new ArrayList<StateHistory>();
        if(obj instanceof CouncilPreamble) {
        	workflowState = ((CouncilPreamble)obj).getState();
        	historyList = ((CouncilPreamble)obj).getStateHistory();
        }else if(obj instanceof CouncilMeeting) {
        	workflowState = ((CouncilMeeting)obj).getState();
        	historyList = ((CouncilMeeting)obj).getStateHistory();
        }
        
        if (null != workflowState) {
            if (!CollectionUtils.isEmpty(historyList)) {
                Collections.reverse(historyList);
            }

            Map<String,String> departmentMap = masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_GENERIC);
            Map<Long,EmployeeInfo> userMap = new HashMap<Long, EmployeeInfo>();
            
            workFlowHistory.put("date", workflowState.getDateInfo());
            try {
            	if(!userMap.containsKey(workflowState.getLastModifiedBy())) {
            		EmployeeInfo info = microserviceUtils.getEmployeeById(workflowState.getLastModifiedBy());
            		if(null != info) {
            			userMap.put(workflowState.getLastModifiedBy(), info);
                	}
            	}
            	EmployeeInfo emp = userMap.get(workflowState.getLastModifiedBy());
            	workFlowHistory.put("updatedBy",emp.getUser().getUserName()+"::"+emp.getUser().getName());
            	
            }catch(Exception e) {
            	LOG.error("No user found with id "+workflowState.getLastModifiedBy());
            }
            workFlowHistory.put("status", workflowState.getValue());
            try {
            	if(!userMap.containsKey(workflowState.getOwnerPosition())) {
            		EmployeeInfo info = microserviceUtils.getEmployeeById(workflowState.getOwnerPosition());
            		if(null != info) {
            			userMap.put(workflowState.getOwnerPosition(), info);
                	}
            	}
            	EmployeeInfo emp = userMap.get(workflowState.getOwnerPosition());
            	workFlowHistory.put("user",emp.getUser().getUserName()+"::"+emp.getUser().getName());
            	workFlowHistory.put(DEPARTMENT,departmentMap.get(emp.getAssignments().get(0).getDepartment()));
            }catch(Exception e) {
            	LOG.error("No user found with id "+workflowState.getOwnerPosition());
            }
            
            workFlowHistory.put("comments", workflowState.getComments() != null ? workflowState.getComments() : "");
            historyTable.add(workFlowHistory);
            
            for (final StateHistory stateHistory : historyList) {
                final HashMap<String, Object> historyMap = new HashMap<>();
                historyMap.put("date", stateHistory.getDateInfo());
                try {
                	if(!userMap.containsKey(stateHistory.getLastModifiedBy())) {
                		EmployeeInfo info = microserviceUtils.getEmployeeById(stateHistory.getLastModifiedBy());
                		if(null != info) {
                			userMap.put(stateHistory.getLastModifiedBy(), info);
                    	}
                	}
                	EmployeeInfo emp = userMap.get(stateHistory.getLastModifiedBy());
                	historyMap.put("updatedBy",emp.getUser().getUserName()+"::"+emp.getUser().getName());
                	
                }catch(Exception e) {
                	LOG.error("No user found with id "+stateHistory.getLastModifiedBy());
                }
                historyMap.put("status", stateHistory.getValue());
                try {
                	if(!userMap.containsKey(stateHistory.getOwnerPosition())) {
                		EmployeeInfo info = microserviceUtils.getEmployeeById(stateHistory.getOwnerPosition());
                		if(null != info) {
                			userMap.put(stateHistory.getOwnerPosition(), info);
                    	}
                	}
                	EmployeeInfo emp = userMap.get(stateHistory.getOwnerPosition());
                	historyMap.put("user",emp.getUser().getUserName()+"::"+emp.getUser().getName());
                	historyMap.put(DEPARTMENT,departmentMap.get(emp.getAssignments().get(0).getDepartment()));
                }catch(Exception e) {
                	LOG.error("No user found with id "+stateHistory.getOwnerPosition());
                }
                historyMap.put("comments", stateHistory.getComments() != null ? stateHistory.getComments() : "");
                
                historyTable.add(historyMap);
            }
        }
        return historyTable;
    }
}
