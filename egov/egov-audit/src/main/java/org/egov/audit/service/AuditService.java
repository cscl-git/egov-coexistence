package org.egov.audit.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.entity.AuditDetails;
import org.egov.audit.repository.AuditRepository;
import org.egov.audit.utils.AuditConstants;
import org.egov.audit.utils.AuditUtils;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.eis.entity.Assignment;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillregister;
import org.egov.pims.commons.Position;
import org.egov.utils.FinancialConstants;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditService {

	private static final Logger LOG = LoggerFactory.getLogger(AuditService.class);
	@Autowired
	private AuditRepository auditRepository;
	private final ScriptService scriptExecutionService;
	@Autowired
	protected AppConfigValueService appConfigValuesService;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	private AuditUtils auditUtils;
	@Autowired
    private SecurityUtils securityUtils;
	@Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<AuditDetails> auditRegisterWorkflowService;
	@Autowired
    private MicroserviceUtils microServiceUtil;
	@Autowired
	private ExpenseBillService expenseBillService;

	@Autowired
	public AuditService(final ScriptService scriptExecutionService) {
		this.scriptExecutionService = scriptExecutionService;
	}

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public AuditDetails getById(final Long id) {
		return auditRepository.findOne(id);
	}

	public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
		return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	}

	@Transactional
	public AuditDetails create(final AuditDetails auditDetails, final Long approvalPosition,
			final String approvalComent, final String additionalRule, final String workFlowAction,
			final String approvalDesignation) {

		final List<AuditCheckList> checkLists = auditDetails.getCheckList();

		final AuditDetails savedAuditDetails = auditRepository.save(auditDetails);

		// createCheckList(savedEgBillregister, checkLists);

		savedAuditDetails.setStatus(auditUtils.getStatusByModuleAndCode(FinancialConstants.CONTINGENCYBILL_FIN,
				FinancialConstants.CONTINGENCYBILL_CREATED_STATUS));
		createAuditWorkflowTransition(savedAuditDetails, approvalPosition, approvalComent, additionalRule,
				workFlowAction, approvalDesignation);
		List<DocumentUpload> files = auditDetails.getDocumentDetail() == null ? null
				: auditDetails.getDocumentDetail();
		final List<DocumentUpload> documentDetails;
		documentDetails = auditUtils.getDocumentDetails(files, savedAuditDetails,
				AuditConstants.FILESTORE_MODULEOBJECT);
		if (!documentDetails.isEmpty()) {
			savedAuditDetails.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}

		AuditDetails auditReg = auditRepository.save(savedAuditDetails);
		persistenceService.getSession().flush();
		return auditReg;
	}

	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}

	public void createAuditWorkflowTransition(final AuditDetails auditDetails,
			final Long approvalPosition, final String approvalComent, final String additionalRule,
			final String workFlowAction, final String approvalDesignation) {
		if (LOG.isDebugEnabled())
			LOG.debug(" Create WorkFlow Transition Started  ...");
		final User user = securityUtils.getCurrentUser();
		final DateTime currentDate = new DateTime();
		Assignment wfInitiator = null;
		Map<String, String> finalDesignationNames = new HashMap<>();
		final String currState = "";
		String stateValue = "";
		if (null != auditDetails.getId())
			wfInitiator = this.getCurrentUserAssignmet(auditDetails.getCreatedBy());
		if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workFlowAction)) {
			stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
			auditDetails.transition().progressWithStateCopy()
					.withSenderName(user.getUsername() + "::" + user.getName()).withComments(approvalComent)
					.withStateValue(stateValue).withDateInfo(currentDate.toDate()).withOwner(wfInitiator.getPosition())
					.withNextAction("").withNatureOfTask(FinancialConstants.WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME);
		} else {
			WorkFlowMatrix wfmatrix;
			Designation designation = this.getDesignationDetails(approvalDesignation);
			Position owenrPos = new Position();
			owenrPos.setId(approvalPosition);

			wfmatrix = auditRegisterWorkflowService.getWfMatrix(auditDetails.getStateType(), null, null,
					additionalRule, FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING, null);

			if (wfmatrix != null && wfmatrix.getCurrentDesignation() != null) {
				final List<String> finalDesignationName = Arrays.asList(wfmatrix.getCurrentDesignation().split(","));
				for (final String desgName : finalDesignationName)
					if (desgName != null && !"".equals(desgName.trim()))
						finalDesignationNames.put(desgName.toUpperCase(), desgName.toUpperCase());
			}

			if (null == auditDetails.getState()) {

				if (designation != null && finalDesignationNames.get(designation.getName().toUpperCase()) != null)
					stateValue = FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING;

				wfmatrix = auditRegisterWorkflowService.getWfMatrix(auditDetails.getStateType(), null, null,
						additionalRule, currState, null);

				if (stateValue.isEmpty())
					stateValue = wfmatrix.getNextState();

				auditDetails.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
						.withComments(approvalComent).withStateValue(stateValue).withDateInfo(new Date())
						.withOwner(owenrPos).withNextAction(wfmatrix.getNextAction())
						.withNatureOfTask(FinancialConstants.WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME)
						.withCreatedBy(user.getId()).withtLastModifiedBy(user.getId());
			} else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workFlowAction)) {
				stateValue = FinancialConstants.WORKFLOW_STATE_CANCELLED;
				auditDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
						.withComments(approvalComent).withStateValue(stateValue).withDateInfo(currentDate.toDate())
						.withNextAction("").withNatureOfTask(FinancialConstants.WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME);
			} else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workFlowAction)) {
				wfmatrix = auditRegisterWorkflowService.getWfMatrix(auditDetails.getStateType(), null, null,
						additionalRule, auditDetails.getCurrentState().getValue(), null);

				if (stateValue.isEmpty())
					stateValue = wfmatrix.getNextState();

				auditDetails.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
						.withComments(approvalComent).withStateValue(stateValue).withDateInfo(new Date())
						.withNextAction(wfmatrix.getNextAction())
						.withNatureOfTask(FinancialConstants.WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME);
			} else {
				if (designation != null && finalDesignationNames.get(designation.getName().toUpperCase()) != null)
					stateValue = FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING;

				wfmatrix = auditRegisterWorkflowService.getWfMatrix(auditDetails.getStateType(), null, null,
						additionalRule, auditDetails.getCurrentState().getValue(), null);

				if (stateValue.isEmpty())
					stateValue = wfmatrix.getNextState();

				auditDetails.transition().progressWithStateCopy()
						.withSenderName(user.getUsername() + "::" + user.getName()).withComments(approvalComent)
						.withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos)
						.withNextAction(wfmatrix.getNextAction())
						.withNatureOfTask(FinancialConstants.WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME);
			}
		}
		if (LOG.isDebugEnabled())
			LOG.debug(" WorkFlow Transition Completed  ...");
	}
	
	private Assignment getCurrentUserAssignmet(Long userId){
//    	Long userId = ApplicationThreadLocals.getUserId();
    	List<EmployeeInfo> emplist = microServiceUtil.getEmployee(userId, null, null, null);
    	Assignment assignment =new Assignment();
    	if(null!=emplist && emplist.size()>0 && emplist.get(0).getAssignments().size()>0){
    		Position position = new Position();
    		position.setId(emplist.get(0).getAssignments().get(0).getPosition());
    		assignment.setPosition(position);
            
    		org.egov.pims.commons.Designation designation = new org.egov.pims.commons.Designation();
            Designation _desg = this.getDesignationDetails(emplist.get(0).getAssignments().get(0).getDesignation());
            designation.setCode(_desg.getCode());
            designation.setName(_desg.getName());
            assignment.setDesignation(designation);
            
            org.egov.infra.admin.master.entity.Department department = new org.egov.infra.admin.master.entity.Department();
            Department _dept = this.getDepartmentDetails(emplist.get(0).getAssignments().get(0).getDepartment());
            department.setCode(_dept.getCode());
            department.setName(_dept.getName());
            
            return assignment;
    	}
    	return null;
    }
	
private Department getDepartmentDetails(String deptCode){
    	
    	Department dept = microServiceUtil.getDepartmentByCode(deptCode);
    	return dept;
    	
    }
    
    private Designation getDesignationDetails(String desgnCode){
    	List<Designation> desgnList = microServiceUtil.getDesignation(desgnCode);
    	return desgnList.get(0);
    }

	public EgBillregister getBillDetails(long billId) {
		return expenseBillService.getById(billId);
	}

}
