package org.egov.apnimandi.transactions.service;

import org.apache.commons.collections.CollectionUtils;
import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;
import org.egov.apnimandi.transactions.entity.ApnimandiContractor;
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
public class ApnimandiThirdPartyService {
	private static final Logger LOG = LoggerFactory.getLogger(ApnimandiThirdPartyService.class);
    private static final String DEPARTMENT = "department";
    
    @Autowired
    private EgovMasterDataCaching masterDataCache;
    @Autowired
    private MicroserviceUtils microserviceUtils;
    
    public List<HashMap<String, Object>> getHistory(final Object obj) {
        final List<HashMap<String, Object>> historyTable = new ArrayList<>();
        State workflowState = null;
        final HashMap<String, Object> workFlowHistory = new HashMap<>();
        List<StateHistory> historyList = new ArrayList<StateHistory>();
        if(obj instanceof ApnimandiContractor) {
        	workflowState = ((ApnimandiContractor)obj).getState();
        	historyList = ((ApnimandiContractor)obj).getStateHistory();
        }
        else if(obj instanceof ApnimandiCollectionDetails) {
        	workflowState = ((ApnimandiCollectionDetails)obj).getState();
        	historyList = ((ApnimandiCollectionDetails)obj).getStateHistory();
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
