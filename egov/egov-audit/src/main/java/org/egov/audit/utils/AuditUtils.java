package org.egov.audit.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.model.bills.DocumentUpload;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditUtils {
	
	@Autowired
    private FileStoreService fileStoreService;
	@Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;
	@Autowired
	protected MicroserviceUtils microserviceUtils;
	
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
                    doc.getContentType(), AuditConstants.FILESTORE_MODULECODE));
            documentDetailsList.add(documentDetails);

        }
        return documentDetailsList;
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public EgwStatus getStatusByModuleAndCode(final String moduleType, final String code) {
        return egwStatusHibernateDAO.getStatusByModuleAndCode(moduleType, code);
    }
	
	
	public List<HashMap<String, Object>> getHistory(final State state, final List<StateHistory> history) {
        User user = null;
        EmployeeInfo ownerobj = null;
        final List<HashMap<String, Object>> historyTable = new ArrayList<>();
        final HashMap<String, Object> map = new HashMap<>(0);
        if (null != state) {
            if (!history.isEmpty() && history != null)
                Collections.reverse(history);
            for (final StateHistory stateHistory : history) {
                final HashMap<String, Object> workflowHistory = new HashMap<>(0);
                workflowHistory.put("date", stateHistory.getDateInfo());
                workflowHistory.put("comments", stateHistory.getComments());
                workflowHistory.put("updatedBy", stateHistory.getLastModifiedBy() + "::"
                        + stateHistory.getLastModifiedBy());
                workflowHistory.put("status", stateHistory.getValue());
                final Long owner = stateHistory.getOwnerPosition();
                final State _sowner = stateHistory.getState();
               ownerobj=    this.microserviceUtils.getEmployee(owner, null, null, null).get(0);
                if (null != ownerobj) {
                    workflowHistory.put("user",ownerobj.getUser().getUserName()+"::"+ownerobj.getUser().getName());
                    Department department=   this.microserviceUtils.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
                    if(null != department)
                        workflowHistory.put("department", department.getName());
                } else if (null != _sowner && null != _sowner.getDeptName()) {
                    user = microserviceUtils.getEmployee(owner, null, null, null).get(0).getUser();
                    workflowHistory
                            .put("user", null != user.getUserName() ? user.getUserName() + "::" + user.getName() : "");
                    workflowHistory.put("department", null != _sowner.getDeptName() ? _sowner.getDeptName() : "");
                }
                historyTable.add(workflowHistory);
            }
            map.put("date", state.getDateInfo());
            map.put("comments", state.getComments() != null ? state.getComments() : "");
            map.put("updatedBy", state.getLastModifiedBy() + "::" + state.getLastModifiedBy());
            map.put("status", state.getValue());
            final Long ownerPosition = state.getOwnerPosition();
            ownerobj=    this.microserviceUtils.getEmployee(ownerPosition, null, null, null).get(0);
            if(null != ownerobj){
                map.put("user", ownerobj.getUser().getUserName() + "::" + ownerobj.getUser().getName());
              Department department=   this.microserviceUtils.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
              if(null != department)
                  map.put("department", department.getName());
              //                map.put("department", null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
//                        .getDepartmentForUser(user.getId()).getName() : "");
            } else if (null != ownerPosition && null != state.getDeptName()) {
                user = microserviceUtils.getEmployee(ownerPosition, null, null, null).get(0).getUser();
                map.put("user", null != user.getUserName() ? user.getUserName() + "::" + user.getName() : "");
                map.put("department", null != state.getDeptName() ? state.getDeptName() : "");
            }
            historyTable.add(map);
            Collections.sort(historyTable, new Comparator<Map<String, Object>> () {

                public int compare(Map<String, Object> mapObject1, Map<String, Object> mapObject2) {

                    return ((java.sql.Timestamp) mapObject1.get("date")).compareTo((java.sql.Timestamp) mapObject2.get("date")); //ascending order
                }

            });
        }
        return historyTable;
    }

}
