package org.egov.audit.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.audit.model.AuditDetail;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.model.bills.DocumentUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditUtils {
	
	private static final Logger LOGGER = Logger.getLogger(AuditUtils.class);
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
	
	public  String getBillDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and br.billdate>='")
						.append(AuditConstants.DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and br.billdate<='")
						.append(AuditConstants.DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			LOGGER.error(e);
			throw new ApplicationRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
	}
	
	
	public  String getReceiptDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and v.voucherDate>='")
						.append(AuditConstants.DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and v.voucherDate<='")
						.append(AuditConstants.DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			LOGGER.error(e);
			throw new ApplicationRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
	}
	public  String getAuditDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and ad.audit_sch_date>='")
						.append(AuditConstants.DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and ad.audit_sch_date<='")
						.append(AuditConstants.DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			LOGGER.error(e);
			throw new ApplicationRuntimeException("Error occured while executing search instrument query");
		}
		return numDateQuery.toString();
	}
	
	public String getBillMisQuery(final AuditDetail auditDetail) {

		final StringBuffer misQuery = new StringBuffer(300);
		EmployeeInfo ownerobj = null;
		if (null != auditDetail) {
			ownerobj= microserviceUtils.getEmployee(ApplicationThreadLocals.getUserId(), null, null, null).get(0);
			Department department=microserviceUtils.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
			if ( auditDetail.getFund() > 0)
			{
				misQuery.append(" and billmis.fund.id=")
						.append(auditDetail.getFund());
			}		
			if (null != department.getCode() && !department.getCode().equals("-1")) {
				misQuery.append(" and billmis.departmentcode='");
				misQuery.append(department.getCode()+"'");
			}
		}
		return misQuery.toString();

	}
	
	public String getAuditMisQuery(final AuditDetail auditDetail) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != auditDetail) {
			if ( auditDetail.getAuditNumber() != null)
			{
				misQuery.append(" and ad.auditno='")
						.append(auditDetail.getAuditNumber()).append("'");
			}
			if(auditDetail.getDepartment() != null && !auditDetail.getDepartment().isEmpty())
			{
				misQuery.append(" and ad.department='")
				.append(auditDetail.getDepartment()).append("'");
			}
			if(auditDetail.getLeadAuditorEmpNo() != null && auditDetail.getLeadAuditorEmpNo() != -1)
			{
				misQuery.append(" and ad.lead_auditor=")
				.append(auditDetail.getLeadAuditorEmpNo());
			}
			if(auditDetail.getPassUnderobjection() != null && auditDetail.getPassUnderobjection() != 0)
			{
				misQuery.append(" and ad.passUnderobjection=")
				.append(auditDetail.getPassUnderobjection());
			}
			
		}
		return misQuery.toString();

	}
	
	public String getAuditTaskMisQuery(final AuditDetail auditDetail) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != auditDetail) {
			if ( auditDetail.getAuditNumber() != null)
			{
				misQuery.append(" and ad.auditno='")
						.append(auditDetail.getAuditNumber()).append("'");
			}
			if(auditDetail.getDepartment() != null && !auditDetail.getDepartment().isEmpty())
			{
				misQuery.append(" and ad.department='")
				.append(auditDetail.getDepartment()).append("'");
			}
			
			
		}
		return misQuery.toString();

	}

	public String getDeptQuery() {
		final StringBuffer misQuery = new StringBuffer(300);
		EmployeeInfo ownerobj = null;
		ownerobj= microserviceUtils.getEmployee(ApplicationThreadLocals.getUserId(), null, null, null).get(0);
		Department department=microserviceUtils.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
		
		if (null != department.getCode() && !department.getCode().equals("-1")) {
			misQuery.append(" and v.id in (select vmis.voucherheaderid from Vouchermis vmis where vmis.departmentcode ='");
			misQuery.append(department.getCode()+"')");
		}
		
		return misQuery.toString();
	}

}
