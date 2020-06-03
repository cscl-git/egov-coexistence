package org.egov.audit.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.workflow.entity.State;
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
    MicroserviceUtils microServiceUtil;
	
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
	
	public String getApproverDetails(final String workFlowAction, final State state, final Long id, final Long approvalPosition,
            final String approverName) {


        String approverDetails="";
        if (!FinancialConstants.BUTTONREJECT.toString().equalsIgnoreCase(workFlowAction))
            approverDetails = id + "," + approverName;
        else
            approverDetails = id + "," + getInitiatorName(state.getCreatedBy());
        return approverDetails;
    }
	
	public String getInitiatorName(Long employeeId){
        
	      List<EmployeeInfo>empList =  microServiceUtil.getEmployee(employeeId, new Date(),null, null);
	      if(null!=empList && !empList.isEmpty())  
	      return empList.get(0).getUser().getName();
	      else
	          return "";
	    }

}
