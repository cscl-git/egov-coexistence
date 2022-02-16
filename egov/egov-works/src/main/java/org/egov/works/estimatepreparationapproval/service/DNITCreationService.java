package org.egov.works.estimatepreparationapproval.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.eis.service.DesignationService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.notification.service.NotificationService;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.pims.commons.Position;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.DNITCreationRepository;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DNITCreationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(DNITCreationService.class);
	public static final String MODULE_FULLNAME = "Council Management";
	 public static final String SENDSMSFORCOUNCIL = "SENDSMSFORCOUNCILMEMBER";
	 private static final String DATE_FORMAT = "yyyy-MM-dd";
	 private static final String ROLE_MEETING_SENIOR_OFFICER = "LEGAL_NODAL_OFFICER";
	 public static final Locale LOCALE = new Locale("en", "IN");
	    public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;
	
	@Autowired
	protected MicroserviceUtils microserviceUtils;
	@Autowired
	DNITCreationRepository dNITCreationRepository;
	
	@Autowired
    private SecurityUtils securityUtils;
	@Autowired
    @Qualifier("workflowService")
    private SimpleWorkflowService<EstimatePreparationApproval> estimateWorkflowService;
	@Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
    private FileStoreService fileStoreService;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@Autowired
    private DesignationService designationService;
	@Autowired
    private AppConfigValueService appConfigValuesService;
	
	 @Autowired
	    private NotificationService notificationService;

	@Transactional
	public DNITCreation saveEstimatePreparationData(HttpServletRequest request,
			DNITCreation dNITCreation,Long approvalPosition,String approvalComment,String approvalDesignation,String workFlowAction) {
		// TODO Auto-generated method stub

		List<BoQDetails> list = new ArrayList<BoQDetails>();
		List<BoQDetails> list2 =  dNITCreation.getBoQDetailsList();
			for (BoQDetails boq : dNITCreation.getBoQDetailsList()) {
				//System.out.println("++++++++++++ "+boq.getItem_description()+"++++++++");
				//if(boq.getMilestone() !=null && boq.getItem_description() !=null && boq.getRef_dsr() !=null && boq.getQuantity() != null && boq.getRate() !=null && boq.getUnit() != null) 
				
				boq.setDnitCreation(dNITCreation);
				
				list.add(boq);
				
			}
			dNITCreation.setNewBoQDetailsList(list);
		
		if((workFlowAction.equalsIgnoreCase("Forward/Reassign") || workFlowAction.equalsIgnoreCase("Save as Draft")) && dNITCreation.getStatus() == null)
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
			
		}
		
		else if (workFlowAction.equalsIgnoreCase("Save as Draft") && dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
		}
		else if ((workFlowAction.equalsIgnoreCase("Forward/Reassign") )&& dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		//edited
		else if ((workFlowAction.equalsIgnoreCase("Forward/Reassign") )&& dNITCreation.getStatus().getCode().equals("Approved"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Approve"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Approved"));
		}
		
		
		
		DNITCreation savedDNITCreation = dNITCreationRepository
				.save(dNITCreation);
		List<DocumentUpload> files = dNITCreation.getDocumentDetail() == null ? null
				: dNITCreation.getDocumentDetail();
		List<DocumentUpload> documentDetails = new ArrayList<>();
		
		
			documentDetails = getDocumentDetails(files, dNITCreation,
					"Works_Dnit");
			
		
		
		if (!documentDetails.isEmpty()) {
			savedDNITCreation.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		createEstimateWorkflowTransition(savedDNITCreation, approvalPosition, approvalComment, null,
                workFlowAction,approvalDesignation);
		DNITCreation savedDNITCreation1=dNITCreationRepository
		.save(savedDNITCreation);
		//updateBoqdetails(list2,savedDNITCreation1);
		return savedDNITCreation1;
	}
	

	@Transactional
	public DNITCreation saveDnitByEstimatePreparationData(HttpServletRequest request,
			DNITCreation dNITCreation,Long approvalPosition,String approvalComment,String approvalDesignation,String workFlowAction) {
		// TODO Auto-generated method stub

		List<BoQDetails> list = new ArrayList<BoQDetails>();
		
			for (BoQDetails boq : dNITCreation.getBoQDetailsList()) {
				boq.setEstimatePreparationApproval(dNITCreation.getEstimatePreparationApproval());
				boq.setDnitCreation(dNITCreation);
				list.add(boq);
			}
			dNITCreation.setNewBoQDetailsList(list);
		System.out.println("::::::Dnit status::: "+dNITCreation.getStatus());
		if((workFlowAction.equalsIgnoreCase("Forward/Reassign") || workFlowAction.equalsIgnoreCase("Save as Draft")) && dNITCreation.getStatus() == null)
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
			
		}
		else if (workFlowAction.equalsIgnoreCase("Save as Draft") && dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Created"));
		}
		else if ((workFlowAction.equalsIgnoreCase("Forward/Reassign"))&& dNITCreation.getStatus().getCode().equals("Created"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}
		else if((workFlowAction.equalsIgnoreCase("Forward/Reassign"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Pending for Approval"));
		}else if((workFlowAction.equalsIgnoreCase("Approve"))&& dNITCreation.getStatus().getCode().equals("Pending for Approval"))
		{
			dNITCreation.setStatus(egwStatusDAO.getStatusByModuleAndCode("DNITCreation", "Approved"));
		}
		
		DNITCreation savedDNITCreation = dNITCreationRepository
				.save(dNITCreation);
		List<DocumentUpload> files = dNITCreation.getDocumentDetail() == null ? null
				: dNITCreation.getDocumentDetail();
		List<DocumentUpload> documentDetails = new ArrayList<>();
		
		
			documentDetails = getDocumentDetails(files, dNITCreation,
					"Works_Dnit");
			
		
		
		if (!documentDetails.isEmpty()) {
			savedDNITCreation.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		createEstimateWorkflowTransition(savedDNITCreation, approvalPosition, approvalComment, null,
                workFlowAction,approvalDesignation);
		DNITCreation savedDNITCreation1=dNITCreationRepository
		.save(savedDNITCreation);
		return savedDNITCreation1;
	}
	
	
	
		
	public void createEstimateWorkflowTransition(final DNITCreation estimatePreparationApproval,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction,final String approvalDesignation) {
		LOG.info(" Create WorkFlow Transition Started  ...");
		System.out.println("::::Workflow::::: "+workFlowAction);
		final User user = securityUtils.getCurrentUser();
		System.out.println(":::::::User Name:: "+user.getUsername());
        final DateTime currentDate = new DateTime();
        Map<String, String> finalDesignationNames = new HashMap<>();
        final String currState = "";
        String stateValue = "";
        WorkFlowMatrix wfmatrix;
        Position owenrPos = new Position();
        org.egov.pims.commons.Designation designation=null;
        if( approvalDesignation != null &&  !approvalDesignation.isEmpty())
        {
        	designation = designationService.getDesignationById(Long.parseLong(approvalDesignation));
        }
         
        if(designation != null)
        {
     	   System.out.println("Designation:::::::::::"+designation.getName().toUpperCase());
        }
        if(workFlowAction.equalsIgnoreCase("Save As Draft"))
        {
        	owenrPos.setId(user.getId());
        }
        else
        {
        	owenrPos.setId(approvalPosition);
        }
        System.out.println("<<<<<before work flow <<<<<"+owenrPos.getId()+"<<>>>>>>>>>>>Current user   >>>>>>>>>>"+user.getName()+" <<ID>> "+user.getId());
        if (null == estimatePreparationApproval.getState() && workFlowAction.equals("Save As Draft")) {
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, "NEW", null);
        	estimatePreparationApproval.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("DNIT")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        
        }
        
        else if (null == estimatePreparationApproval.getState() && !workFlowAction.equals("Save As Draft"))
        {
        	System.out.println("<<<<<State null forward/reasign <<<<<"+owenrPos.getId()+"<<>>>>>>>>>>>Current user   >>>>>>>>>>"+user.getName()+" <<ID>> "+user.getId());
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, "NEW", null);
        	String statetype="Pending With "+designation.getName().toUpperCase();
        	estimatePreparationApproval.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
            .withComments(approvalComent)
            .withStateValue(statetype).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
            .withNextAction(wfmatrix.getNextAction())
            .withNatureOfTask("DNIT")
            .withCreatedBy(user.getId())
            .withtLastModifiedBy(user.getId());
        }
        else
        {
        	wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                    null, additionalRule, estimatePreparationApproval.getCurrentState().getValue(), null);
        	if(workFlowAction.equalsIgnoreCase("Save As Draft"))
        	{
        		wfmatrix = estimateWorkflowService.getWfMatrix(estimatePreparationApproval.getStateType(), null,
                        null, additionalRule, "SaveAsDraft", null);
            	estimatePreparationApproval.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue("SaveAsDraft").withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("DNIT");
        	}
        	else if(workFlowAction.equalsIgnoreCase("Forward/Reassign"))
        	{
        		System.out.println("<<<<<Forward/Reassign<<<<<"+owenrPos.getId()+"<<>>>>>>>>>>>Current user   >>>>>>>>>>"+user.getName()+" <<ID>> "+user.getId());
        		String statetype="Pending With "+designation.getName().toUpperCase();
        		estimatePreparationApproval.transition().progressWithStateCopy().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue(statetype).withDateInfo(new Date()).withOwner(owenrPos).withOwnerName((owenrPos.getId() != null && owenrPos.getId() > 0L) ? getEmployeeName(owenrPos.getId()):"")
                .withNextAction(wfmatrix.getNextAction())
                .withNatureOfTask("DNIT");
        		

        	}
        	else if(workFlowAction.equalsIgnoreCase("Approve"))
        	{
        		estimatePreparationApproval.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
                .withComments(approvalComent)
                .withStateValue("END").withDateInfo(new Date())
                .withNextAction("DNIT Approved")
                .withNatureOfTask("DNIT");

        	}
        }
        
	}
	
	@Transactional
	public EstimatePreparationApproval editEstimatePreparationData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub

		// List<BoQDetails> list = new ArrayList<BoQDetails>();
		//for (BoQDetails boq : estimatePreparationApproval.getBoQDetailsList()) {
			//boq.setEstimatePreparationApproval(estimatePreparationApproval);
			// list.add(boq);
		//}
		// estimatePreparationApproval.setNewBoQDetailsList(null);
		 
		/*EstimatePreparationApproval e = new EstimatePreparationApproval();
		
		e.setWorkLocation(estimatePreparationApproval.getWorkLocation());
		e.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		e.setId(estimatePreparationApproval.getId());
		applyAuditing(e);*/
		
		/*EstimatePreparationApproval savedEstimatePreparationApproval1 = estimatePreparationApprovalRepository
				.save(e);*/

		Long id = estimatePreparationApproval.getId();
		EstimatePreparationApproval estimate =estimatePreparationApprovalRepository
				.findById(estimatePreparationApproval.getId());
		
		estimate.setWorkLocation(estimatePreparationApproval.getWorkLocation());
		applyAuditing(estimate);

		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalRepository
				.save(estimate);
		persistenceService.getSession().flush();
		return savedEstimatePreparationApproval;
	}
	
	public void applyAuditing(AbstractAuditable auditable) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(ApplicationThreadLocals.getUserId());
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(ApplicationThreadLocals.getUserId());
		auditable.setLastModifiedDate(currentDate);
	}
	
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
           
            if(doc.getObjectType()!=null) {
	            if(doc.getObjectType().equals("roughWorkFile")) {
	            	
	            	 documentDetails.setObjectType("roughWorkFile");
	            	 documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
	                         doc.getContentType(), "roughWorkFile"));
	            }
            }
            else {
            	 documentDetails.setObjectType(objectType);
            	 documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
                         doc.getContentType(), "Works_Dnit"));
            }
           
            documentDetailsList.add(documentDetails);

        }
        return documentDetailsList;
    }
	
	
	
	
	
	
	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}
	
	public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
		return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	}
	
	public String getEmployeeName(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	    }
	
	//saving document before 
		public DocumentUpload savedocebefore(DNITCreation estimatePreparationApproval) {
			List<DocumentUpload> files = estimatePreparationApproval.getDocumentDetail() == null ? null
					: estimatePreparationApproval.getDocumentDetail();
			List<DocumentUpload> documentDetails = new ArrayList<>();
			DocumentUpload documentUpload=new DocumentUpload();
			
			documentDetails = getDocumentDetailsbefore(files, estimatePreparationApproval,
					"Works_Est");
				
			
			
			if (!documentDetails.isEmpty()) {
				
			 documentUpload=	persistDocument1(documentDetails);
			//System.out.println("::::::::::: "+ documentUpload.getId()+":::::::::: " +documentUpload.getObjectType()+":::::::::::: "+documentUpload.getFileStore().getId());
			//System.out.println(":::::::::::Changed:::::::::");
			}
		return documentUpload;	
		}
		
		public List<DocumentUpload> getDocumentDetailsbefore(final List<DocumentUpload> files, final Object object,
	            final String objectType) {
	        final List<DocumentUpload> documentDetailsList = new ArrayList<>();

	        Long id;
	        Method method;
	        try {
	            method = object.getClass().getMethod("getId", null);
	            id = (Long) method.invoke(object, null);
	            System.out.println("::::::::::ID:::::::: "+id);
	        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
	                | InvocationTargetException e) {
	            throw new ApplicationRuntimeException("error.expense.bill.document.error", e);
	        }

	        for (DocumentUpload doc : files) {
	            final DocumentUpload documentDetails = new DocumentUpload();
	            //documentDetails.setObjectId(id);
	            
	        
	          
	           
	            if(doc.getObjectType()!=null) {
	            	System.out.println(":::::::::: "+doc.getObjectType());
		            if(doc.getObjectType().equals("roughWorkFileDnit")) {
		            	System.out.println(":::getObjectType():::::+ "+doc.getFileName());
		            	 documentDetails.setObjectType("roughWorkFileDnit");
		            	 documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
		                         doc.getContentType(), "roughWorkFileDnit"));
		            	 documentDetails.setComments(doc.getComments());
		            	 documentDetails.setUsername(doc.getUsername());
		            }
	            }
	            else {
	            documentDetails.setObjectType(objectType);
	            System.out.println("::::::::+ "+doc.getFileName());
	            documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
	                    doc.getContentType(), "Works_Est"));
	            documentDetails.setComments(doc.getComments());
	            documentDetails.setUsername(doc.getUsername());
	            }
	           
	            documentDetailsList.add(documentDetails);

	        }
	        return documentDetailsList;
	    }
		public DocumentUpload persistDocument1(final List<DocumentUpload> documentDetailsList) {
			DocumentUpload docs= new DocumentUpload();
			if (documentDetailsList != null && !documentDetailsList.isEmpty()) {
				for (final DocumentUpload doc : documentDetailsList)
				{
					docs=documentUploadRepository.save(doc);
				}
			}
			return docs;
		}
		
		public void updateDocuments(Long id,Long uploadId)
		{
		
				documentUploadRepository.updateDoc(id,uploadId);
		
		}
		
		
		public void deleteBoqUploadData(Long id) {
			// TODO Auto-generated method stub
			documentUploadRepository.deleteDataDnit(id);
		}
		
		 public void sendSmsNotice(DNITCreation dnitCreation, String customMessage) {
		    	LOG.info("A");
		        
		        String smsMsg="";
		        String templateId="1007761530335615496";
		        String date =DDMMYYYYFORMAT1.format(new Date());
		        Boolean smsEnabled = isSmsEnabled();
		     final List<AppConfigValues> appList = appConfigValuesService
		                .getConfigValuesByModuleAndKey("EGF",
		                        "LEGAL_HEARING_TEMPLATE_ID");
		        if(appList.size()!=0)
		        templateId = appList.get(0).getValue();
		        
		        smsMsg="Dear,"+dnitCreation.getCreatedbyuser()+" Your DNIT Number "+dnitCreation.getEstimateNumber()+" has been approved by "+securityUtils.getCurrentUser().getName()+" on "+date+". Chandigarh Smart City Ltd";
		        if (smsEnabled) {
		        	try {
			             List<org.egov.infra.microservice.models.User> userListForDnit = getUserListForDnit();
			             for(org.egov.infra.microservice.models.User user:userListForDnit)
			             {
			            	 if(null!=user.getMobileNumber() && !user.getMobileNumber().isEmpty()) {
			            		 sendSMSOnSewerageForMeeting(user.getMobileNumber(), smsMsg, templateId);
			            	 }
			            	 
			            	 
			             }
			            
		        	}catch(Exception e) {
		            	LOG.error("Unable to send SMS to council members of meeting number "+dnitCreation.getEstimateNumber());
		            }
					
		            
		        }
		    }
		    
		 public Boolean isSmsEnabled() {

		        return getAppConfigValueByPassingModuleAndType(MODULE_FULLNAME, SENDSMSFORCOUNCIL);
		    }
		 private Boolean getAppConfigValueByPassingModuleAndType(String moduleName, String sendsmsoremail) {
		        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(moduleName,
		                sendsmsoremail);

		        return "YES".equalsIgnoreCase(
		                appConfigValue != null && !appConfigValue.isEmpty() ? appConfigValue.get(0).getValue() : "NO");
		    }
		 
		 
		 public void sendSMSOnSewerageForMeeting(final String mobileNumber, final String smsBody,String templateId) {
		    	LOG.info("C");
		        notificationService.sendSMS(mobileNumber, smsBody,templateId);
		    } 
		 public List<org.egov.infra.microservice.models.User> getUserListForDnit() {
		        Set<org.egov.infra.microservice.models.User> usersListResult = new HashSet<>();
		        List<String> roles = new ArrayList<String>();
		        roles.add(ROLE_MEETING_SENIOR_OFFICER);
		        List<EmployeeInfo> employees = microserviceUtils.getEmployeesByRoles(roles);
		    	if(!CollectionUtils.isEmpty(employees)) {
		    		for(EmployeeInfo info : employees) {
		    			usersListResult.add(info.getUser());
		    		}
		    	}
		        return new ArrayList<>(usersListResult);
		    }
}
