package org.egov.works.web.controller.estimatepreparationapproval;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;												   
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.repository.DepartmentRepository;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.notification.service.NotificationService;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.BoqNewDetails;
import org.egov.works.boq.entity.BoqUploadDocument;
import org.egov.works.boq.service.BoqDetailsPopService;
//import org.egov.works.estimatepreparationapproval.autonumber.AuditNumberGenerator;
import org.egov.works.estimatepreparationapproval.autonumber.EstimateNoGenerator;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.entity.Subdivisionworks;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.egov.works.estimatepreparationapproval.repository.SudivisionRepository;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.utils.ExcelGenerator;
import org.egov.works.workestimate.service.WorkEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/estimatePreparation")
public class EstimatePreparationApprovalController extends GenericWorkFlowController {

	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;

	@Autowired
	private AutonumberServiceBeanResolver beanResolver;

	@Autowired
	public MicroserviceUtils microserviceUtils;
	@Autowired
	private DepartmentRepository departmentrepository;
	
	@Autowired
	private SudivisionRepository subdivision;

	@Autowired
	WorkEstimateService workEstimateService;
	
	private static final String STATE_TYPE = "stateType";
	private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    @Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;
    private static final String MODULE_FULLNAME = "Council Management";
    private static final String SENDSMSFORCOUNCIL = "SENDSMSFORCOUNCILMEMBER";
    private static final String ROLE_MEETING_SENIOR_OFFICER = "WORKS_NOTIFICATION";
    private static final String SENDEMAILFORCOUNCIL = "SENDEMAILFORCOUNCILMEMBER";
    
    private static final int BUFFER_SIZE = 4096;
    public static final Locale LOCALE = new Locale("en", "IN");
    public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
    
    @Autowired
	private FileStoreService fileStoreService;
    
    @Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
    
    @Autowired
	private AppConfigValueService appConfigValuesService;
    
    @Autowired
	private DocumentUploadRepository documentUploadRepository;

	@Autowired
    BoqDetailsPopService boqDetailsPopService;

    @Autowired
    private NotificationService notificationService;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    	
    	System.out.println(":::Limit:::Auto grow::: "+binder.getAutoGrowCollectionLimit());
    	//dataBinder.setAutoGrowCollectionLimit(autoGrowCollectionLimit);
    	//binder.setAutoGrowCollectionLimit(1000);
    	System.out.println("::::::Auto grow::: "+binder.getAutoGrowCollectionLimit());
    }
    
    private static Map<String, String> map;
    
    // Instantiating the static map 
    static
    { 
        map = new HashMap<>(); 
        map.put("Created", "Under Rough Estate approval process"); 
        map.put("Pending for Approval", "Under Rough Estate approval process");
        map.put("AA Initiated", "Rough cost Estimate Approved");
        map.put("AA Pending for Approval", "Estimate under Administrative approval process");
        map.put("TS Initiated", "Administrative Approval Received");
        map.put("TS Pending for Approval", "Under Detailed Estate approval process");
        map.put("Approved", "Detailed cost Estimate Approved");
    }

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setDesignations(getDesignationsFromMs());
		//edited...
		estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
		
		model.addAttribute(STATE_TYPE, estimatePreparationApproval.getClass().getSimpleName());
        prepareWorkflow(model, estimatePreparationApproval, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        model.addAttribute("estimatePreparationApproval", estimatePreparationApproval);
//		model.addAttribute("showTableHeaderFirst","Y");
		System.out.println("before -upload ");
        System.out.println("Starting");
		return "estimatepreparationapproval-form";
	}


	
	private void prepareValidActionListByCutOffDate(Model model) {
            model.addAttribute("validActionList",
                    Arrays.asList("Forward/Reassign","Save As Draft"));
	}

	@RequestMapping(value = "/estimate", params = "Forward/Reassign", method = RequestMethod.POST)
	public String saveBoQDetailsData(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file1") MultipartFile[] files,@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {

		String workFlowAction=estimatePreparationApproval.getWorkFlowAction();
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				if(files[i] == null || files[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if(fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload2.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(estimatePreparationApproval.getObjectType());
				list.add(upload2);
			}
		
		estimatePreparationApproval.setDocumentDetail(list);
		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
			estimatePreparationApproval
					.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		}
		
		String deptCode = "";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = estimatePreparationApproval.getDepartment();
		//String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
			//	"works_div_"+deptCode).get(0).getValue();
		String deptShortCode=populateShortCode(deptCode,estimatePreparationApproval.getWorksWing(),estimatePreparationApproval.getSubdivision());
	    String estimateNumber = v.getEstimateNumber(deptShortCode);
		estimatePreparationApproval.setEstimateNumber(estimateNumber);
		String aaNumber=v.getAANumber(deptShortCode);
		estimatePreparationApproval.setAanumber(aaNumber);
		estimatePreparationApproval.setDepartment(estimatePreparationApproval.getDepartment());
		//start of workflow
		Long approvalPosition = 0l;
        String approvalComment = "";
        String approvalDesignation = "";
        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");
        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
        {
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
        }
        
        if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
            approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
        System.out.println(":::::workFlowAction::::::: "+workFlowAction +"::::::estimate::::::::: "+estimatePreparationApproval.getStatus());
       try {
        EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalService
				.saveEstimatePreparationData(request, estimatePreparationApproval,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

	
		Long id=savedEstimatePreparationApproval.getId();
		if(estimatePreparationApproval.getDocUpload()!=null) {
			for(BoqUploadDocument boq:estimatePreparationApproval.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				if(boq.getObjectId()!=null) {
					Long update=boq.getObjectId();
					estimatePreparationApprovalService.updateDocuments(id,update);
				}
			}
		}
																							  
		return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;
       }catch(Exception e) {
    	   e.printStackTrace();
       }
			//estimatePreparationApprovalService.updateDocuments(id,uploadId);
        
        
		/*return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;*/
       String msg="Estimate Not Forwarded .";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
       
      // estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
       estimatePreparationApproval.setWorksWing(estimatePreparationApproval.getWorksWing());
		estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimatePreparationApproval.setDepartment(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimatePreparationApproval.getWorksWing())));
		estimatePreparationApproval.setSubdivision(estimatePreparationApproval.getSubdivision());
		estimatePreparationApproval.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(estimatePreparationApproval.getDepartment())));
		estimatePreparationApproval.setDesignations(getDesignationsFromMs());
		//edited...
		
		model.addAttribute(STATE_TYPE, estimatePreparationApproval.getClass().getSimpleName());
       prepareWorkflow(model, estimatePreparationApproval, new WorkflowContainer());
       prepareValidActionListByCutOffDate(model);
		
		return "estimatepreparationapproval-form";
		
	}
	
	private String populateShortCode(String deptCode, String worksWing, Long subdivision2) {
		
		String deptPart=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					"works_div_"+deptCode).get(0).getValue();
		String worksWingPart="";
		if(worksWing.equalsIgnoreCase("1"))
		{
			worksWingPart="BR";
		}
		else if(worksWing.equalsIgnoreCase("2"))
		{
			worksWingPart="PH";
		}
		else
		{
			worksWingPart="HE";
		}
		String subPart="";
		if(subdivision2 ==1)
		{
			subPart="S1";
		}
		else if(subdivision2 ==2)
		{
			subPart="S2";
		}
		else if(subdivision2 ==3)
		{
			subPart="S22";
		}
		else if(subdivision2 ==4)
		{
			subPart="S3";
		}
		else if(subdivision2 ==5)
		{
			subPart="S4";
		}
		else if(subdivision2 ==6)
		{
			subPart="S7";
		}
		else if(subdivision2 ==7)
		{
			subPart="S8";
		}
		else if(subdivision2 ==8)
		{
			subPart="S9";
		}
		else if(subdivision2 ==9)
		{
			subPart="S14";
		}
		else if(subdivision2 ==10)
		{
			subPart="S15";
		}
		else if(subdivision2 ==11)
		{
			subPart="S20";
		}
		else if(subdivision2 ==12)
		{
			subPart="S1";
		}
		else if(subdivision2 ==13)
		{
			subPart="S10";
		}
		else if(subdivision2 ==14)
		{
			subPart="S11";
		}
		else if(subdivision2 ==15)
		{
			subPart="S16";
		}
		else if(subdivision2 ==16)
		{
			subPart="S17";
		}
		else if(subdivision2 ==17)
		{
			subPart="S21";
		}
		else if(subdivision2 ==18)
		{
			subPart="S12";
		}
		else if(subdivision2 ==19)
		{
			subPart="S13";
		}
		else if(subdivision2 ==20)
		{
			subPart="S18";
		}
		else if(subdivision2 ==21)
		{
			subPart="S6";
		}
		else if(subdivision2 ==22)
		{
			subPart="S1";
		}
		else if(subdivision2 ==23)
		{
			subPart="S3";
		}
		else if(subdivision2 ==24)
		{
			subPart="S6";
		}
		else if(subdivision2 ==25)
		{
			subPart="S5";
		}
		else if(subdivision2 ==26)
		{
			subPart="S14";
		}
		else if(subdivision2 ==27)
		{
			subPart="S2";
		}
		else if(subdivision2 ==28)
		{
			subPart="S4";
		}
		else if(subdivision2 ==29)
		{
			subPart="S5";
		}
		else if(subdivision2 ==30)
		{
			subPart="S19";
		}
		else if(subdivision2 ==31)
		{
			subPart="S1";
		}
		else if(subdivision2 ==32)
		{
			subPart="S2";
		}
		else if(subdivision2 ==33)
		{
			subPart="S3";
		}
		else if(subdivision2 ==34)
		{
			subPart="S9";
		}
		else if(subdivision2 ==35)
		{
			subPart="S10";
		}
		else if(subdivision2 ==36)
		{
			subPart="S12";
		}
		else if(subdivision2 ==37)
		{
			subPart="S13";
		}
		else if(subdivision2 ==38)
		{
			subPart="S5";
		}
		else if(subdivision2 ==39)
		{
			subPart="S6";
		}
		else if(subdivision2 ==40)
		{
			subPart="S7";
		}
		else if(subdivision2 ==41)
		{
			subPart="S8";
		}
		else if(subdivision2 ==42)
		{
			subPart="S15";
		}
		else if(subdivision2 ==43)
		{
			subPart="S1";
		}
		else if(subdivision2 ==44)
		{
			subPart="S3";
		}
		else if(subdivision2 ==45)
		{
			subPart="S34";
		}
		
		return worksWingPart+deptPart+subPart;
	}



	@RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("approverDetails") final String approverDetails,@RequestParam("workflowaction") final String workflowaction, final Model model,
                                  final HttpServletRequest request,@RequestParam("estId") final String estId) {
		
		EstimatePreparationApproval savedEstimatePreparationApproval=estimatePreparationApprovalRepository.getOne(Long.parseLong(estId));
		final String message = getMessageByStatus(savedEstimatePreparationApproval, approverDetails,workflowaction);

        model.addAttribute("message", message);

        return "works-success";
    }

	
	private String getMessageByStatus(EstimatePreparationApproval savedEstimatePreparationApproval,
			String approverDetails, String workflowaction) {
		String approverName="";
		String msg="";
		boolean estimateSms=false;
		String notMsg="";
		if(workflowaction.equalsIgnoreCase("Save As Draft"))
		{
			msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" is Saved as draft";
		}
		else if(( workflowaction.equalsIgnoreCase("Approve")) && savedEstimatePreparationApproval.getStatus().getCode().equalsIgnoreCase("Approved"))
		{
			//sms
			
			msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" TS is approved";
			
			 
		}
		else 
		{
			approverName=getEmployeeName(Long.parseLong(approverDetails));
			if(savedEstimatePreparationApproval.getStatus().getCode().equals("AA Initiated"))
			{
				msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" has been approved and forwarded to "+approverName +" for Administrative Approval process";
				notMsg="Dear ? ,Estimate Id : "+savedEstimatePreparationApproval.getEstimateNumber()+"has been approved";
				estimateSms=sendSmsAndEmailDetailsForApproval(notMsg);
				if(estimateSms) {
					System.out.println("+++++++++Approved Message sent Successfully+++++++");
				}
			}
			else if(savedEstimatePreparationApproval.getStatus().getCode().equals("TS Initiated"))
			{
				
				msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" Administrative Approval has been approved and  forwarded to "+approverName +" for Detailed Cost Estimate Approval process";
			}
			else
			{
				msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" has been forwarded to "+approverName;
			}
			
		}
		return msg;
	}

	 //SMS and Email Service
    public Boolean sendSmsAndEmailDetailsForApproval(String msg) {
    	
          boolean successStatus=false;
       
        try {
        	System.out.println("+++++++++++"+msg+"+++++++++++++++++");
        	Boolean status=sendSmsForAgendaInvitation(msg);
        	if(status) {
        		successStatus=true;
        	}
        }catch(Exception e) {
        	e.printStackTrace();
        	//LOGGER.error("Unable to send SMS to for agenda invitation ");
        	System.out.println("+++++++++++++Unable to send SMS to for agenda invitation+++++++++");
        }
            
	        
       
        return successStatus;
    }
    //------------------------------------------------EMAIL-------------------------------------------------------------------
    
    public Boolean sendEmailForAgendaInvitation(String customMessage) {
        Boolean emailEnabled = isEmailEnabled();
        Boolean emailStatus=false;
        if (emailEnabled) {
        	try {
	            List<User> listOfUsers = getUserListForAgendaInvitation();
	            for (User user : listOfUsers) {
	                if (user.getEmailId() != null) {
	                	customMessage=customMessage.replace("?", user.getName());
	                	buildEmailForAgendaInvitation(user.getUserName(), user.getEmailId(), customMessage);
	                }
	                emailStatus=true;
	            }
	        }catch(Exception e) {
	        	//LOGGER.error("Unable to send EMAIL to agenda invitation");
	        }
        }
        return emailStatus;
    }
    
    public Boolean isEmailEnabled() {

        return getAppConfigValueByPassingModuleAndType(MODULE_FULLNAME, SENDEMAILFORCOUNCIL);

    }
    
    public void buildEmailForAgendaInvitation(final String userName, final String email,
            final String customMessage) {
        String body = customMessage;
        String subject;
        subject = "Approval Notice:";
        body = customMessage;
        //System.out.println(customMessage);
        if (email != null && body != null)
            sendEmailOnSewerageForMeetingWithAttachment(email, body, subject);
    }
    
    public void sendEmailOnSewerageForMeetingWithAttachment(final String email, final String emailBody, final String emailSubject) {
        if(!StringUtils.isBlank(email) && !StringUtils.isBlank(emailBody)) {
        	notificationService.sendEmail(email, emailSubject, emailBody);
        	
        }else {
        	System.out.println("++++++++Email not Sent+++++++");
        }
    }

    
    //-----------------------------------------------SMS----------------------------------------------------------------
    public Boolean sendSmsForAgendaInvitation(String customMessage) {
        Boolean smsEnabled = isSmsEnabled();
         Boolean smsStatus=false;
        if (smsEnabled) {
            try {
	            List<User> listOfUsers = getUserListForAgendaInvitation();
	            for (User user : listOfUsers) {
	                if (user.getMobileNumber() != null) {
	                	
	                	customMessage=customMessage.replace("?", user.getName());
	                	buildSmsForAgendaInvitation(user.getUserName(), user.getMobileNumber(), customMessage);
	                }
	                smsStatus=true;
	            }
            }catch(Exception e) {
            	e.printStackTrace();
            	//LOGGER.error("Unable to send SMS to agenda invitation");
            	System.out.println("Unable to send SMS to works");
            }
        }
        return smsStatus;
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
    
    public void buildSmsForAgendaInvitation(final String userName, final String mobileNumber,
            final String customMessage) {
    	sendSMSOnSewerageForMeeting(mobileNumber, customMessage);            
    }
    public void sendSMSOnSewerageForMeeting(final String mobileNumber, final String smsBody) {
        notificationService.sendSMS(mobileNumber, smsBody,null);
    }
    public List<User> getUserListForAgendaInvitation() {
        Set<User> usersListResult = new HashSet<>();
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
    //------------------end--------------------------
	
	@RequestMapping(value = "/estimate", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsDataDraft(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file1") MultipartFile[] files, @RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request)
			throws Exception {
		
		String workFlowAction=estimatePreparationApproval.getWorkFlowAction();
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				if(files[i] == null || files[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if(fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload2.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(estimatePreparationApproval.getObjectType());
				list.add(upload2);
			}
		estimatePreparationApproval.setDocumentDetail(list);
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (estimatePreparationApproval.getEstimateDt() != null && estimatePreparationApproval.getEstimateDt() != "") {
		Date estimateDate = inputFormat.parse(estimatePreparationApproval.getEstimateDt());
		estimatePreparationApproval.setEstimateDate(estimateDate);
		}

		if (estimatePreparationApproval.getDt() != null && estimatePreparationApproval.getDt() != "") {
		Date date = inputFormat.parse(estimatePreparationApproval.getDt());
		estimatePreparationApproval.setDate(date);
		}
		Long department = null;
		if (estimatePreparationApproval.getDepartment() != null || estimatePreparationApproval.getDepartment() != "") {
			
		
		 department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}

		String deptCode = "";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = estimatePreparationApproval.getDepartment();
		//String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
			//	"works_div_"+deptCode).get(0).getValue();
		String deptShortCode=populateShortCode(deptCode,estimatePreparationApproval.getWorksWing(),estimatePreparationApproval.getSubdivision());
	    String estimateNumber = v.getEstimateNumber(deptShortCode);
		estimatePreparationApproval.setEstimateNumber(estimateNumber);
		String aaNumber=v.getAANumber(deptShortCode);
		estimatePreparationApproval.setAanumber(aaNumber);
		estimatePreparationApproval.setDepartment(estimatePreparationApproval.getDepartment());
		//start of workflow
		Long approvalPosition = 0l;
        String approvalComment = "";
        String approvalDesignation = "";
        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");
        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
        {
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
        }
        
        if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
            approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
        try {
        EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalService
				.saveEstimatePreparationData(request, estimatePreparationApproval,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

			Long id=savedEstimatePreparationApproval.getId();
		if(estimatePreparationApproval.getDocUpload()!=null) {
			for(BoqUploadDocument boq:estimatePreparationApproval.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				if(boq.getObjectId()!=null) {
					Long update=boq.getObjectId();
					estimatePreparationApprovalService.updateDocuments(id,update);
				}
			}
		}
        											
		return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
		/*return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;*/
        //estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setDesignations(getDesignationsFromMs());
		estimatePreparationApproval.setWorksWing(estimatePreparationApproval.getWorksWing());
		estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimatePreparationApproval.setDepartment(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimatePreparationApproval.getWorksWing())));
		estimatePreparationApproval.setSubdivision(estimatePreparationApproval.getSubdivision());
		estimatePreparationApproval.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(estimatePreparationApproval.getDepartment())));
		//edited...
		String msg="Estimate Not Saved as Draft.";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		
		model.addAttribute(STATE_TYPE, estimatePreparationApproval.getClass().getSimpleName());
        prepareWorkflow(model, estimatePreparationApproval, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		return "estimatepreparationapproval-form";

	}


	@RequestMapping(value = "/estimate", params = "save", method = RequestMethod.POST)
	public String uploadBoqFileData(
			@ModelAttribute("estimatePreparationApproval")  EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files, final HttpServletRequest request,final Long id)
			throws Exception {
//		estimatePreparationApproval=workEstimateService.searchEstimateData(id);
		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		String userName = estimatePreparationApprovalService.getUserName();
		Long userId = estimatePreparationApprovalService.getUserId();
		System.out.println(":::::User Name:::: "+userName+"::::UserID:::: "+userId);
		 List<String> uom=new ArrayList<String>();
		 List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					"works_uom");
		 for(AppConfigValues row :appConfigValuesList)
		 {
			 uom.add(row.getValue());
		 }
		//List<BoqDetailsPop> arrayboqDetailsPop =new ArrayList();
		HashSet<String> milesstoneList=new HashSet<>();
		String refNo=null;
		//file upload info
		List<DocumentUpload> docup = new ArrayList<>();
		int count = 0;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		int erow=1;
		List<Integer> rowerror=new ArrayList<>();
		Boolean error=true;
		String msg="";
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		Double estAmt= 0.0;
		String comments =request.getParameter("comments");											
		// String documentPath = "D://Upload/";

		String documentPath = FILE_PATH_PROPERTIES + FILE_PATH_SEPERATOR;

		long currentTime = new Date().getTime();
		if (file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
			fileName = file.getOriginalFilename().toString().split("\\.")[0];
			extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			fileName = fileName.replace(" ", "") + "_" + currentTime + extension;
			filePath = documentPath + fileName;
			fileToUpload = new File(filePath);
			byte[] bytes = file.getBytes();
			Path Path = null;
			Path = Paths.get(filePath);

	 /*
			Path doc = Paths.get(documentPath);
			if (!Files.exists(doc)) {
				Files.createDirectories(doc);
			}

			Files.write(Path, bytes);
	  */
		}
		byte[] bytes = file.getBytes();
        String completeData = new String(bytes);
        String[] rows = completeData.split("#");
        String[] columns = rows[0].split(",");
		//File xlsFile = new File(fileToUpload.toString());
		//if (xlsFile.exists()) {
		
			//FileInputStream inputStream = new FileInputStream(new File(filePath));
			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
		error=false;
			}else{
				msg="Please check the uploaded document as there is an issue in AOR detail sheet.";
				}
			}
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println("firstSheet;;"+firstSheet.getSheetName());
	//			Sheet firstSheet = workbook.getSheetAt(0);
			if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
				//error=false;
				if (files != null)
					for (int j = 0; j< files.length; j++) {
						DocumentUpload upload = new DocumentUpload();
						if(files[j] == null || files[j].getOriginalFilename().isEmpty())
						{
							continue;
						}
						upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
						upload.setFileName(files[j].getOriginalFilename());
						System.out.println("files[i].getOriginalFilename():;;;;;;;;"+files[j].getOriginalFilename());
						upload.setContentType(files[j].getContentType());
						upload.setObjectType("roughWorkFile");
						upload.setComments(comments);
						upload.setUsername(userName);
//						upload.setComments(comments);
						//System.out.println("comments--------"+comments);
						docup.add(upload);
					}
				
			Iterator<Row> iterator = firstSheet.iterator();
				//List<BoqDetailsPop> array1boqDetailsPop = new ArrayList<BoqDetailsPop>(); 
				
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				BoQDetails aBoQDetails = new BoQDetails();
				//BoqDetailsPop boqDetailsPop =new BoqDetailsPop();	

				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();
if(cell.getCellType()==cell.CELL_TYPE_BLANK) {
						//System.out.println("::Cell Type::: "+cell.getCellType()+"::::::Blank:: "+cell.CELL_TYPE_BLANK); 
						error=true;
						msg="Please check the uploaded document as there is an issue in AOR detail sheet.";

					   }
					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

						if (cell.getColumnIndex() == 0) {
							aBoQDetails.setMilestone(cell.getStringCellValue());
	   
						}
	  
						else if (cell.getColumnIndex() == 1) {
	   
							aBoQDetails.setItem_description(cell.getStringCellValue());
							//boqDetailsPop.setItem_description(cell.getStringCellValue());
	   
						} else if (cell.getColumnIndex() == 2) {
	   
							aBoQDetails.setRef_dsr(cell.getStringCellValue());
							//boqDetailsPop.setRef_dsr(cell.getStringCellValue());
							refNo =cell.getStringCellValue();
						}else if (cell.getColumnIndex() == 3) {
	   
							aBoQDetails.setUnit(cell.getStringCellValue());
							//boqDetailsPop.setUnit(cell.getStringCellValue());
	   
						}else if (cell.getColumnIndex() == 5) {
							if (aBoQDetails.getRate() != null)  {
								
								if(aBoQDetails.getQuantity()==null ) {
									error=true;
									msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
								}
								
							}
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
						}
						 if (cell.getColumnIndex() == 4) {
		
							aBoQDetails.setRate(cell.getNumericCellValue());
							//boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
	   						Double d = cell.getNumericCellValue();
	   
							String[] splitter = d.toString().split("\\.");
							
							if(splitter[1].length()>2) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
						} else if (cell.getColumnIndex() == 5) {
	   
							aBoQDetails.setQuantity(cell.getNumericCellValue());
	   Double d = cell.getNumericCellValue();
							String[] splitter = d.toString().split("\\.");
	   
							//System.out.println("::::Quantity::"+cell.getNumericCellValue()+"::oa: "+aBoQDetails.getQuantity());
							if(splitter[1].length()>2) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
							System.out.println("::unit:: "+uom.contains(aBoQDetails.getUnit()));
							if(!uom.contains(aBoQDetails.getUnit())) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet.";	
							}
							if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
								
	
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
							
							}else {
							rowerror.add(erow);	
							error=true;
							msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
						}

					}


					if (aBoQDetails.getMilestone()!=null && aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
							&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
							&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
						count=boQDetailsList.size();
						aBoQDetails.setSlNo(Long.valueOf(count));
						boQDetailsList.add(aBoQDetails);
						//arrayboqDetailsPop.add(boqDetailsPop);
	 

					}
				}
				erow=erow+1;
	
			}

			// workbook.close();
			//inputStream.close();
			}
		}	

		//} else {
			// response = "Please choose a file.";
		//}
  int nextcount=1;
		
		
		List<BoqUploadDocument> docUpload=new ArrayList<>();
		if(estimatePreparationApproval.getDocUpload()!=null) {
			for(BoqUploadDocument boq:estimatePreparationApproval.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				BoqUploadDocument boqUploadDocument=new BoqUploadDocument();
				if(boq.getObjectId()!=null) {
					boqUploadDocument.setId(Long.valueOf(nextcount));
					boqUploadDocument.setObjectId(boq.getObjectId());
					boqUploadDocument.setObjectType(boq.getObjectType());
					boqUploadDocument.setFilestoreid(boq.getFilestoreid());
					boqUploadDocument.setComments(boq.getComments());
					boqUploadDocument.setUsername(boq.getUsername());
					docUpload.add(boqUploadDocument);
					nextcount=nextcount+1;
				}
			}
		}
		estimatePreparationApproval.setDocumentDetail(docup);  
		estimatePreparationApproval.setRoughCostdocumentDetail(docup);
		for(Integer r:rowerror) {
			System.out.println("::Error in Rows::"+r);
		}
		if(!error) {
		DocumentUpload savedocebefore = estimatePreparationApprovalService.savedocebefore(estimatePreparationApproval);		
		
		
		System.out.println("::OBJECT ID:: "+savedocebefore.getId()+" ::ObjectType:::: "+savedocebefore.getFileStore().getFileName()+" :::::FileStore():::: "+savedocebefore.getFileStore().getId());
		
  BoqUploadDocument boqUploadDocument2=new BoqUploadDocument();
		//adding
		boqUploadDocument2.setId(Long.valueOf(nextcount));
		boqUploadDocument2.setObjectId(savedocebefore.getId());
		boqUploadDocument2.setObjectType(savedocebefore.getFileStore().getFileName());
		boqUploadDocument2.setFilestoreid(savedocebefore.getFileStore().getId());
		boqUploadDocument2.setComments(comments);
		boqUploadDocument2.setUsername(savedocebefore.getUsername());
		docUpload.add(boqUploadDocument2);
		
		
		 Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		 estimatePreparationApproval.setDocUpload(docUpload);
		 List<DocumentUpload> uploadDoc= new ArrayList<DocumentUpload>();
			System.out.println("::::::::filesStoreId::::::::::::: "+savedocebefore.getId()+"-----"+savedocebefore.getFileStore().getId()+"------"+savedocebefore.getObjectType()+"------"+savedocebefore.getFileStore().getFileName());
			DocumentUpload doc1= new DocumentUpload();
			Long fileStoreId = savedocebefore.getFileStore().getId();
			doc1.setId(savedocebefore.getId());
			doc1.setFileStore(savedocebefore.getFileStore());
			uploadDoc.add(doc1);
			model.addAttribute("milestoneList",groupByMilesToneMap);
			model.addAttribute("uploadDocID",uploadDoc);
			
			model.addAttribute("fileuploadAllowed","Y");
			
			BigDecimal  bgestAmt = BigDecimal.valueOf(estAmt);
			if(estAmt >= 10000000){
				BigDecimal  pct = new BigDecimal(3);
				BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
				estimatePreparationApproval.setContingentPercentage(3.0);
				estimatePreparationApproval.setContingentAmount(ContingentAmt);
				
				BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
				Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
				estimatePreparationApproval.setEstimateAmount(dobestAmtPlusContingentAmt);
			}
			else {
				BigDecimal  pct = new BigDecimal(5);
				BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
				estimatePreparationApproval.setContingentPercentage(5.0);
				estimatePreparationApproval.setContingentAmount(ContingentAmt);
				
				BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
				Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
				estimatePreparationApproval.setEstimateAmount(dobestAmtPlusContingentAmt);
			}
			
		}else {
			
			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}
		
		Map<String, List<BoqUploadDocument>> uploadDocument = 
				docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		 
		 System.out.println("::::::::: "+estimatePreparationApproval.getRoughCostdocumentDetail().size());
		//estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setBoQDetailsList(boQDetailsList);
		estimatePreparationApproval.setDesignations(getDesignationsFromMs());
		estimatePreparationApproval.setWorksWing(estimatePreparationApproval.getWorksWing());
		estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimatePreparationApproval.setDepartment(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimatePreparationApproval.getWorksWing())));
		estimatePreparationApproval.setSubdivision(estimatePreparationApproval.getSubdivision());
		estimatePreparationApproval.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(estimatePreparationApproval.getDepartment())));
		model.addAttribute("uploadDocument", uploadDocument);
		
		
	
		
		/*try {
			
			List<BoqDetailsPop> boQCheck= boqDetailsPopService.checkRecordsExists(refNo);
			if(boQCheck.size()>0)
			{
				System.out.println("Already Exists");
				
			}else {
				boqDetailsPopService.saveExcelDate(arrayboqDetailsPop);
				System.out.println("Excel data saved successfully.. ");
			}
		
		}catch(Exception e) {
			e.getMessage();
		}
		System.out.println("id--"+id);*/
		

		model.addAttribute(STATE_TYPE, estimatePreparationApproval.getClass().getSimpleName());
        prepareWorkflow(model, estimatePreparationApproval, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		model.addAttribute("estimatePreparationApproval", estimatePreparationApproval);
		
		
		//model.addAttribute("uploadDocuments", uploadDocuments);
		model.addAttribute("mode", "create");
		
		
		return "estimatepreparationapproval-form";

	}
	
	public  BigDecimal percentage(BigDecimal base, BigDecimal pct){
		BigDecimal  bg100 = new BigDecimal(100);
	    return base.multiply(pct).divide(bg100);
	}
	public Workbook getWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;
		if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);

		} else if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}
		return workbook;
	}

	public List<Department> getDepartmentsFromMs() {
		List<Department> departments = microserviceUtils.getDepartments();
		return departments;
	}
	
	public List<Designation> getDesignationsFromMs() {
		List<Designation> designations = microserviceUtils.getDesignations();
        return designations;
    }

	@RequestMapping(value = "/formnew", method = RequestMethod.POST)
	public String showEstimateNewFormGet(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-estimate-form";
	}
	
	@RequestMapping(value = "/formnew1", method = RequestMethod.POST)
	public String showEstimateNewFormGet1(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-estimate-form1";
	}
	
	@RequestMapping(value = "/workEstimateSearch1", params = "workEstimateSearch1", method = RequestMethod.POST)
	public String searchWorkEstimateData1(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request,StateHistory stateHistory) throws Exception {
		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
		long department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}
		EstimatePreparationApproval estimate=null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		Date date;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select es.id,es.workName,es.workCategory,es.executingDivision,es.worksWing,es.expHead_est,es.estimateAmount,es.estimateDate,es.createdDate  from EstimatePreparationApproval es where es.executingDivision = ? ")
	        .append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
	        .append(getMisQuery(estimatePreparationApproval));
		 System.out.println("Query :: "+query.toString());
         list = persistenceService.findAllBy(query.toString(),
        		 estimatePreparationApproval.getExecutingDivision());
		 
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new EstimatePreparationApproval();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setWorkName(object[1].toString());
        		 }
        		 if(object[2] != null)
        		 {
        			 estimate.setWorkCategry(object[2].toString());
        		 }
        		 if(object[3] != null)
        		 {
        			 estimate.setExecutingDivision(Long.parseLong(object[3].toString()));
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setWorksWing(object[4].toString());
        		 }
        		 if(object[5] != null)
        		 {
        			 estimate.setExpHead_est(object[5].toString());
        		 }
        		 if(object[6] != null)
        		 {
        			 estimate.setEstimateAmount(Double.parseDouble(object[6].toString()));
        		 }
        		 if(object[7] != null)
        		 {
        			 estimate.setEstimateDt(object[7].toString());
        		 }
        		 if(object[8] != null)
        		 {
        			 String createDate=object[8].toString();
//        			 date = (Date) format.parse(createDate);
//        			 date = new Date(createDate);
        			 estimate.setCreatedDt(createDate);
        		 }
        		 
        		 
        		 if(estimate.getStatusDescription() != null && !estimate.getStatusDescription().equalsIgnoreCase("Approved"))
        		 {
//        			 estimate.setPendingWith(populatePendingWith(estimate.getId()));
        		 }
        		 approvalList.add(estimate);
        	 }
        	 
         }
        estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-estimate-form1";

	}
	
	
	
	@RequestMapping(value = "/createDetailedEstimate", method = RequestMethod.POST)
	public String createDetailedEstimate(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-detailed-estimate-form";
	}

	@RequestMapping(value = "/workEstimateSearch", params = "workEstimateSearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
		long department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}
		EstimatePreparationApproval estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from EstimatePreparationApproval es where es.executingDivision = ? ")
	        .append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
	        .append(getMisQuery(estimatePreparationApproval));
		 System.out.println("Query :: "+query.toString());
         list = persistenceService.findAllBy(query.toString(),
        		 estimatePreparationApproval.getExecutingDivision());
		 
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new EstimatePreparationApproval();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setWorkName(object[1].toString());
        		 }
        		 if(object[2] != null)
        		 {
        			 estimate.setWorkCategry(object[2].toString());
        		 }
        		 if(object[3] != null)
        		 {
        			 estimate.setEstimateNumber(object[3].toString());
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setEstimateDt(object[4].toString());
        		 }
        		 if(object[5] != null)
        		 {
        			 estimate.setEstimateAmount(Double.parseDouble(object[5].toString()));
        		 }
        		 String status=null;
        		 if(object[6] != null)
        		 {
        			 status=object[6].toString();
        			 estimate.setStatusDescription(map.get(status));
        		 }
        		 if(status != null && !status.equalsIgnoreCase("Approved"))
        		 {
        			 estimate.setPendingWith(populatePendingWith(estimate.getId()));
        		 }
        		 approvalList.add(estimate);
        	 }
        	 
         }
        estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-estimate-form";

	}
	
	private String populatePendingWith(Long id) {
		String pendingWith="";
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		if(estimateDetails != null && estimateDetails.getState() != null && estimateDetails.getState().getOwnerPosition() != null && estimateDetails.getState().getOwnerPosition() != 0L)
		{
			try
			{
				pendingWith=getEmployeeName(estimateDetails.getState().getOwnerPosition());
			}catch (Exception e) {
				pendingWith="";
			}
			
		}
		return pendingWith;
	}

	@RequestMapping(value = "/workEstimateDetailedSearch",  method = RequestMethod.POST)
	public String searchWorkDetailedEstimateData(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
		long department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}
		EstimatePreparationApproval estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from EstimatePreparationApproval es where es.status.description='TS Initiated' and  es.executingDivision = ? ")
	        .append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
	        .append(getMisQuery(estimatePreparationApproval));
		 System.out.println("Query :: "+query.toString());
         list = persistenceService.findAllBy(query.toString(),
        		 estimatePreparationApproval.getExecutingDivision());
		 
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new EstimatePreparationApproval();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setWorkName(object[1].toString());
        		 }
        		 if(object[2] != null)
        		 {
        			 estimate.setWorkCategry(object[2].toString());
        		 }
        		 if(object[3] != null)
        		 {
        			 estimate.setEstimateNumber(object[3].toString());
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setEstimateDt(object[4].toString());
        		 }
        		 if(object[5] != null)
        		 {
        			 estimate.setEstimateAmount(Double.parseDouble(object[5].toString()));
        		 }
        		 if(object[6] != null)
        		 {
        			 estimate.setStatusDescription(map.get(object[6].toString()));
        		 }
        		 approvalList.add(estimate);
        	 }
        	 
         }
        estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-detailed-estimate-form";

	}


	
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

	
		List<BoQDetails> boQDetailsList = new ArrayList();
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Est",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFile",estimateDetails.getId());

		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		//estimateDetails.setDepartment(dept);

		//estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setDepartment(dept);
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		
		
		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList.size());
				boq.setMilestone("BoqDetails");
				boQDetailsList.add(boq);
			}
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		model.addAttribute("milestoneList",groupByMilesToneMap);
		
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		model.addAttribute("mode", "view");
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));

		return "view-estimate-form";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<DocumentUpload> documentsall=new ArrayList<>();
		
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Est",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFile",estimateDetails.getId());

		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));

		
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setExpHead(estimateDetails.getExpHead_est());
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		
		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList.size());
				boq.setMilestone("BoqMilestone");
				boQDetailsList.add(boq);
			}
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-estimate-form";
	}
@RequestMapping(value = "/edit/saveestimate1", params="save", method = RequestMethod.POST)
	public String editEstimateData1(
			@ModelAttribute("estimatePreparationApproval")  EstimatePreparationApproval estimatePreparationApproval,@RequestParam("file1") MultipartFile[] file1,Model model,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,@RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files, final HttpServletRequest request) throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<DocumentUpload> documentsall=new ArrayList<>();
		Long id = estimatePreparationApproval.getId();
		System.out.println(":::::ID:::::: "+id);
		String userName = estimatePreparationApprovalService.getUserName();
		Long userId = estimatePreparationApprovalService.getUserId();
		System.out.println(":::::User Name:::: "+userName+"::::UserID:::: "+userId);
		List<String> uom=new ArrayList<String>();
		 List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					"works_uom");
		 for(AppConfigValues row :appConfigValuesList)
		 {
			 uom.add(row.getValue());
		 }
		//New File UPload....
		List<DocumentUpload> docup = new ArrayList<>();
		String refNo=null;
		int count = 0;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		Boolean error=true;
		String msg="";
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		Double estAmt= 0.0;
		String comments =request.getParameter("comments");
		// String documentPath = "D://Upload/";

		String documentPath = FILE_PATH_PROPERTIES + FILE_PATH_SEPERATOR;

		long currentTime = new Date().getTime();
		if (file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
			fileName = file.getOriginalFilename().toString().split("\\.")[0];
			extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			fileName = fileName.replace(" ", "") + "_" + currentTime + extension;
			filePath = documentPath + fileName;
			fileToUpload = new File(filePath);
			byte[] bytes = file.getBytes();
			Path Path = null;
			Path = Paths.get(filePath);

			Path doc = Paths.get(documentPath);
			if (!Files.exists(doc)) {
				Files.createDirectories(doc);
			}

			Files.write(Path, bytes);
		}
		
		
		File xlsFile = new File(fileToUpload.toString());
		if (xlsFile.exists()) {

			
			FileInputStream inputStream = new FileInputStream(new File(filePath));
			Workbook workbook = getWorkbook(inputStream, filePath);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
		error=false;
			}else{
				msg="Please check the uploaded document as there is an issue in AOR detail sheet.";
				}
			}
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println("firstSheet;;"+firstSheet.getSheetName());
	//			Sheet firstSheet = workbook.getSheetAt(0);
			if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
				error=false;
				if (files != null)
					for (int j = 0; j< files.length; j++) {
						DocumentUpload upload = new DocumentUpload();
						if(files[j] == null || files[j].getOriginalFilename().isEmpty())
						
						{
							continue;
	   
						}
						upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
						upload.setFileName(files[j].getOriginalFilename());
						System.out.println("files[i].getOriginalFilename():;;;;;;;;"+files[j].getOriginalFilename());
						upload.setContentType(files[j].getContentType());
						upload.setObjectType("roughWorkFile");
						upload.setComments(comments);
						upload.setUsername(userName);
						//System.out.println("comments--------"+comments);
						docup.add(upload);
																																							
					}
				
				Iterator<Row> iterator = firstSheet.iterator();
				//List<BoqDetailsPop> array1boqDetailsPop = new ArrayList<BoqDetailsPop>(); 
				
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				BoQDetails aBoQDetails = new BoQDetails();
				//BoqDetailsPop boqDetailsPop =new BoqDetailsPop();	
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();
if(cell.getCellType()==cell.CELL_TYPE_BLANK) {
						System.out.println("::Cell Type::: "+cell.getCellType()+"::::::Blank:: "+cell.CELL_TYPE_BLANK); 
						error=true;
						msg="Please check the uploaded document as there is an issue in AOR detail sheet.";
					   }
					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

						if (cell.getColumnIndex() == 0) {
							aBoQDetails.setMilestone(cell.getStringCellValue());
	   
						}
	  
						else if (cell.getColumnIndex() == 1) {
	   
							aBoQDetails.setItem_description(cell.getStringCellValue());
							//boqDetailsPop.setItem_description(cell.getStringCellValue());
	   
						} else if (cell.getColumnIndex() == 2) {
	   
							aBoQDetails.setRef_dsr(cell.getStringCellValue());
							//boqDetailsPop.setRef_dsr(cell.getStringCellValue());
							refNo =cell.getStringCellValue();
						}else if (cell.getColumnIndex() == 3) {
	   
							aBoQDetails.setUnit(cell.getStringCellValue());
							//boqDetailsPop.setUnit(cell.getStringCellValue());
	   
						}else if (cell.getColumnIndex() == 5) {
							if (aBoQDetails.getRate() != null)  {
								if(aBoQDetails.getQuantity()==null) {
									error=true;
									msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
								}
							}
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
						}
						 if (cell.getColumnIndex() == 4) {
		
							aBoQDetails.setRate(cell.getNumericCellValue());
Double d = cell.getNumericCellValue();
							
							String[] splitter = d.toString().split("\\.");
							
							if(splitter[1].length()>2) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
							//boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
	   
						} else if (cell.getColumnIndex() == 5) {
	   
							aBoQDetails.setQuantity(cell.getNumericCellValue());
	   Double d = cell.getNumericCellValue();
							
							String[] splitter = d.toString().split("\\.");
	   
							if(splitter[1].length()>2) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
							if(!uom.contains(aBoQDetails.getUnit())) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet.";	
							}
							if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
							}else {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
						}

					}


					if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
							&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
							&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
						count=boQDetailsList.size();
						aBoQDetails.setSizeIndex(count);
						boQDetailsList.add(aBoQDetails);
						//arrayboqDetailsPop.add(boqDetailsPop);
	 

					}
				}
	
			}

			// workbook.close();
			inputStream.close();
			}else {
				//error=true;
				//msg="Uploaded document must contain Sheet with name Abst. with AOR";
			}
		}	

	} else {
			// response = "Please choose a file.";
		}
		int nextcount=1;
		
		
		List<BoqUploadDocument> docUpload=new ArrayList<>();
		if(estimatePreparationApproval.getDocUpload()!=null) {
			for(BoqUploadDocument boq:estimatePreparationApproval.getDocUpload()) {
				//System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				BoqUploadDocument boqUploadDocument=new BoqUploadDocument();
				if(boq.getObjectId()!=null) {
					boqUploadDocument.setId(Long.valueOf(nextcount));
					boqUploadDocument.setObjectId(boq.getObjectId());
					boqUploadDocument.setObjectType(boq.getObjectType());
					boqUploadDocument.setFilestoreid(boq.getFilestoreid());
					boqUploadDocument.setComments(boq.getComments());
					boqUploadDocument.setUsername(boq.getUsername());
					docUpload.add(boqUploadDocument);
					nextcount=nextcount+1;
				}
			}
		}
		estimatePreparationApproval.setDocumentDetail(docup);  
		estimatePreparationApproval.setRoughCostdocumentDetail(docup);
		if(!error) {
		DocumentUpload savedocebefore = estimatePreparationApprovalService.savedocebefore(estimatePreparationApproval);		
		estimatePreparationApprovalService.updateDocuments(id, savedocebefore.getId());
		
		System.out.println("::OBJECT ID:: "+savedocebefore.getId()+" ::ObjectType:::: "+savedocebefore.getUsername()+" :::::FileStore():::: "+savedocebefore.getFileStore().getId());
		
		BoqUploadDocument boqUploadDocument2=new BoqUploadDocument();
		//adding
		boqUploadDocument2.setId(Long.valueOf(nextcount));
		boqUploadDocument2.setObjectId(savedocebefore.getId());
		boqUploadDocument2.setObjectType(savedocebefore.getFileStore().getFileName());
		boqUploadDocument2.setFilestoreid(savedocebefore.getFileStore().getId());
		boqUploadDocument2.setComments(comments);
		boqUploadDocument2.setUsername(savedocebefore.getUsername());
		docUpload.add(boqUploadDocument2);
		//End of document Upload
		
		List<DocumentUpload> uploadDoc= new ArrayList<DocumentUpload>();
		//System.out.println("::::::::filesStoreId::::::::::::: "+savedocebefore.getId()+"-----"+savedocebefore.getFileStore().getId()+"------"+savedocebefore.getObjectType()+"------"+savedocebefore.getFileStore().getFileName());
		DocumentUpload doc1= new DocumentUpload();
		doc1.setId(savedocebefore.getId());
		doc1.setFileStore(savedocebefore.getFileStore());
		uploadDoc.add(doc1);
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList",groupByMilesToneMap);
		
		}else {
			
			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		Map<String, List<BoqUploadDocument>> uploadDocument = 
				docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		model.addAttribute("uploadDocument", uploadDocument);
		if(!error) {
		estimateDetails.setDocUpload(docUpload);
		estimateDetails.setBoQDetailsList(boQDetailsList);
		BigDecimal  bgestAmt = BigDecimal.valueOf(estAmt);
		if(estAmt >= 10000000){
			BigDecimal  pct = new BigDecimal(3);
			BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
			estimateDetails.setContingentPercentage(3.0);
			estimateDetails.setContingentAmount(ContingentAmt);
			
			BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
			Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			estimateDetails.setEstimateAmount(dobestAmtPlusContingentAmt);
		}
		else {
			BigDecimal  pct = new BigDecimal(5);
			BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
			estimateDetails.setContingentPercentage(5.0);
			estimateDetails.setContingentAmount(ContingentAmt);
			
			BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
			Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			estimateDetails.setEstimateAmount(dobestAmtPlusContingentAmt);
		}
		
		}
		
		
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Est",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFile",estimateDetails.getId());

		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		//estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setExpHead(estimateDetails.getExpHead_est());
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setDepartment(estimateDetails.getDepartment());
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimatePreparationApproval.getSubdivision());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(estimateDetails.getDepartment())));
		List<BoQDetails> boQDetailsList1 = new ArrayList();
		BoQDetails boq = new BoQDetails();
		if(error) {
			
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList1.size());
				System.out.println("::: "+boq.getSlNo()+"::: "+boq.getRef_dsr());
				boQDetailsList1.add(boq);
			}
		Map<String, List<BoQDetails>> groupByMilesToneMap1 = 
				  boQDetailsList1.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList",groupByMilesToneMap1);
		}
		
		
		
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-estimate-form";
	}
	
	@RequestMapping(value = "/edit/saveestimate1",  method = RequestMethod.POST)
	public String editEstimateData(
			@ModelAttribute("estimatePreparationApproval")  EstimatePreparationApproval estimatePreparationApproval,@RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request) throws Exception {

		String workFlowAction=estimatePreparationApproval.getWorkFlowAction();
		Long id=estimatePreparationApproval.getId();
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		BoQDetails boq = new BoQDetails();
		String error="Y";
		if(estimatePreparationApproval.getDocUpload()!=null) {
			
			Long ids =estimatePreparationApproval.getId();
			System.out.println("ids------"+ids);
			
			estimatePreparationApprovalService.deleteBoqUploadData(ids);
			
//		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
//			
//				boq = estimateDetails.getBoQDetailsList().get(j);
//				Long slNo = boq.id();
//				System.out.println("::::::::   "+slNo);
//				estimatePreparationApprovalService.deleteBoq(slNo);
//			}
		}
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				if(files[i] == null || files[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if(fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload2.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(estimatePreparationApproval.getObjectType());
				list.add(upload2);
			}
		estimatePreparationApproval.setDocumentDetail(list);
		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			estimatePreparationApproval
					.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		}
		Long approvalPosition = 0l;
        String approvalComment = "";
        String approvalDesignation = "";
        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");
        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
        {
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
        }
        
        if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
            approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
        
        System.out.println(":::::::::STATUS:::::: "+estimatePreparationApproval.getStatus().getCode()+"::::::::Workflow::::::::::: "+estimatePreparationApproval.getWorkFlowAction());
        try {
        EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalService
				.saveEstimatePreparationData(request, estimatePreparationApproval,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;
        
       
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
        return "redirect:/estimatePreparation/edits/"+id;
	}
	@RequestMapping(value = "/edits/{id}", method = RequestMethod.GET)
	public String editerr(@PathVariable("id") final Long id, Model model ) {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<DocumentUpload> documentsall=new ArrayList<>();
		
		
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Est",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFile",estimateDetails.getId());

		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setExpHead(estimateDetails.getExpHead_est());
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		
		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList.size());
									 
				boQDetailsList.add(boq);
			}
		String msg="Unable to Update Something Went Wrong !! please try Again.";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-estimate-form";
	}
	@RequestMapping(value = "/edits/saveestimate1", params="save", method = RequestMethod.POST)
	public String editEstimateData1err(
			@ModelAttribute("estimatePreparationApproval")  EstimatePreparationApproval estimatePreparationApproval,@RequestParam("file1") MultipartFile[] file1,Model model,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,@RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files, final HttpServletRequest request) throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<DocumentUpload> documentsall=new ArrayList<>();
		Long id = estimatePreparationApproval.getId();
		System.out.println(":::::ID:::::: "+id);
		String userName = estimatePreparationApprovalService.getUserName();
		Long userId = estimatePreparationApprovalService.getUserId();
		System.out.println(":::::User Name:::: "+userName+"::::UserID:::: "+userId);
		List<String> uom=new ArrayList<String>();
		 List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					"works_uom");
		 for(AppConfigValues row :appConfigValuesList)
		 {
			 uom.add(row.getValue());
		 }
		//New File UPload....
		List<DocumentUpload> docup = new ArrayList<>();
		String refNo=null;
		int count = 0;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		Boolean error=true;
		String msg="";
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		Double estAmt= 0.0;
		String comments =request.getParameter("comments");
		// String documentPath = "D://Upload/";

		String documentPath = FILE_PATH_PROPERTIES + FILE_PATH_SEPERATOR;

		long currentTime = new Date().getTime();
		if (file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
			fileName = file.getOriginalFilename().toString().split("\\.")[0];
			extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			fileName = fileName.replace(" ", "") + "_" + currentTime + extension;
			filePath = documentPath + fileName;
			fileToUpload = new File(filePath);
			byte[] bytes = file.getBytes();
			Path Path = null;
			Path = Paths.get(filePath);

			Path doc = Paths.get(documentPath);
			if (!Files.exists(doc)) {
				Files.createDirectories(doc);
			}

			Files.write(Path, bytes);
		}
		
		
		File xlsFile = new File(fileToUpload.toString());
		if (xlsFile.exists()) {

			
			FileInputStream inputStream = new FileInputStream(new File(filePath));
			Workbook workbook = getWorkbook(inputStream, filePath);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
		error=false;
			}else{
				msg="Please check the uploaded document as there is an issue in AOR detail sheet.";
				}
			}
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println("firstSheet;;"+firstSheet.getSheetName());
	//			Sheet firstSheet = workbook.getSheetAt(0);
			if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
				error=false;
				if (files != null)
					for (int j = 0; j< files.length; j++) {
						DocumentUpload upload = new DocumentUpload();
						if(files[j] == null || files[j].getOriginalFilename().isEmpty())
						
						{
							continue;
	   
						}
						upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
						upload.setFileName(files[j].getOriginalFilename());
						System.out.println("files[i].getOriginalFilename():;;;;;;;;"+files[j].getOriginalFilename());
						upload.setContentType(files[j].getContentType());
						upload.setObjectType("roughWorkFile");
						upload.setComments(comments);
						upload.setUsername(userName);
						//System.out.println("comments--------"+comments);
						docup.add(upload);
																																							
					}
				
				Iterator<Row> iterator = firstSheet.iterator();
				//List<BoqDetailsPop> array1boqDetailsPop = new ArrayList<BoqDetailsPop>(); 
				
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				BoQDetails aBoQDetails = new BoQDetails();
				//BoqDetailsPop boqDetailsPop =new BoqDetailsPop();	
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();
					if(cell.getCellType()==cell.CELL_TYPE_BLANK) {
						System.out.println("::Cell Type::: "+cell.getCellType()+"::::::Blank:: "+cell.CELL_TYPE_BLANK); 
						error=true;
						msg="Please check the uploaded document as there is an issue in AOR detail sheet. ";
					   }
					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

						if (cell.getColumnIndex() == 0) {
							aBoQDetails.setMilestone(cell.getStringCellValue());
	   
						}
	  
						else if (cell.getColumnIndex() == 1) {
	   
							aBoQDetails.setItem_description(cell.getStringCellValue());
							//boqDetailsPop.setItem_description(cell.getStringCellValue());
	   
						} else if (cell.getColumnIndex() == 2) {
	   
							aBoQDetails.setRef_dsr(cell.getStringCellValue());
							//boqDetailsPop.setRef_dsr(cell.getStringCellValue());
							refNo =cell.getStringCellValue();
						}else if (cell.getColumnIndex() == 3) {
	   
							aBoQDetails.setUnit(cell.getStringCellValue());
							//boqDetailsPop.setUnit(cell.getStringCellValue());
	   
						}else if (cell.getColumnIndex() == 5) {
							if (aBoQDetails.getRate() != null)  {
								if(aBoQDetails.getQuantity()==null) {
									error=true;
									msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
								}
							}
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
						}
						 if (cell.getColumnIndex() == 4) {
		
							aBoQDetails.setRate(cell.getNumericCellValue());
							//boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
							Double d = cell.getNumericCellValue();
							
							String[] splitter = d.toString().split("\\.");
	   
							if(splitter[1].length()>2) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
						} else if (cell.getColumnIndex() == 5) {
	   
							aBoQDetails.setQuantity(cell.getNumericCellValue());
							Double d = cell.getNumericCellValue();
							
							String[] splitter = d.toString().split("\\.");
	   
							if(splitter[1].length()>2) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
							if(!uom.contains(aBoQDetails.getUnit())) {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet.";	
							}
							if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
							}else {
								error=true;
								msg="Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.";
							}
						}

					}


					if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
							&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
							&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
						count=boQDetailsList.size();
						aBoQDetails.setSizeIndex(count);
						boQDetailsList.add(aBoQDetails);
						//arrayboqDetailsPop.add(boqDetailsPop);
	 

					}
				}
	
			}

			// workbook.close();
			inputStream.close();
			}else {
				//error=true;
				//msg="Uploaded document must contain Sheet with name Abst. with AOR";
			}
		}	

	} else {
			// response = "Please choose a file.";
		}
		int nextcount=1;
		
		
		List<BoqUploadDocument> docUpload=new ArrayList<>();
		
		if(estimatePreparationApproval.getDocUpload()!=null) {
			for(BoqUploadDocument boq:estimatePreparationApproval.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				BoqUploadDocument boqUploadDocument=new BoqUploadDocument();
				if(boq.getObjectId()!=null) {
					boqUploadDocument.setId(Long.valueOf(nextcount));
					boqUploadDocument.setObjectId(boq.getObjectId());
					boqUploadDocument.setObjectType(boq.getObjectType());
					boqUploadDocument.setFilestoreid(boq.getFilestoreid());
					boqUploadDocument.setComments(boq.getComments());
					boqUploadDocument.setUsername(boq.getUsername());
					docUpload.add(boqUploadDocument);
					nextcount=nextcount+1;
				}
			}
		}
		estimatePreparationApproval.setDocumentDetail(docup);  
		estimatePreparationApproval.setRoughCostdocumentDetail(docup);
		if(!error) {
		DocumentUpload savedocebefore = estimatePreparationApprovalService.savedocebefore(estimatePreparationApproval);		
		estimatePreparationApprovalService.updateDocuments(id, savedocebefore.getId());
		
		System.out.println("::OBJECT ID:: "+savedocebefore.getId()+" ::ObjectType:::: "+savedocebefore.getUsername()+" :::::FileStore():::: "+savedocebefore.getFileStore().getId());
		
		BoqUploadDocument boqUploadDocument2=new BoqUploadDocument();
		//adding
		boqUploadDocument2.setId(Long.valueOf(nextcount));
		boqUploadDocument2.setObjectId(savedocebefore.getId());
		boqUploadDocument2.setObjectType(savedocebefore.getFileStore().getFileName());
		boqUploadDocument2.setFilestoreid(savedocebefore.getFileStore().getId());
		boqUploadDocument2.setComments(comments);
		boqUploadDocument2.setUsername(savedocebefore.getUsername());
		docUpload.add(boqUploadDocument2);
		//End of document Upload
		
		List<DocumentUpload> uploadDoc= new ArrayList<DocumentUpload>();
		//System.out.println("::::::::filesStoreId::::::::::::: "+savedocebefore.getId()+"-----"+savedocebefore.getFileStore().getId()+"------"+savedocebefore.getObjectType()+"------"+savedocebefore.getFileStore().getFileName());
		DocumentUpload doc1= new DocumentUpload();
		doc1.setId(savedocebefore.getId());
		doc1.setFileStore(savedocebefore.getFileStore());
		uploadDoc.add(doc1);
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList",groupByMilesToneMap);
		
		}else {
			
			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		Map<String, List<BoqUploadDocument>> uploadDocument = 
				docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		model.addAttribute("uploadDocument", uploadDocument);
		if(!error) {
		estimateDetails.setDocUpload(docUpload);
		estimateDetails.setBoQDetailsList(boQDetailsList);
		BigDecimal  bgestAmt = BigDecimal.valueOf(estAmt);
		if(estAmt >= 10000000){
			BigDecimal  pct = new BigDecimal(3);
			BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
			estimateDetails.setContingentPercentage(3.0);
			estimateDetails.setContingentAmount(ContingentAmt);
			
			BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
			Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			estimateDetails.setEstimateAmount(dobestAmtPlusContingentAmt);
		}
		else {
			BigDecimal  pct = new BigDecimal(5);
			BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
			estimateDetails.setContingentPercentage(5.0);
			estimateDetails.setContingentAmount(ContingentAmt);
			
			BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
			Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			estimateDetails.setEstimateAmount(dobestAmtPlusContingentAmt);
		}
		
		}
		
		
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Est",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFile",estimateDetails.getId());

		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		//estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setExpHead(estimateDetails.getExpHead_est());
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setDepartment(estimateDetails.getDepartment());
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimatePreparationApproval.getSubdivision());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(estimateDetails.getDepartment())));
		List<BoQDetails> boQDetailsList1 = new ArrayList();
		BoQDetails boq = new BoQDetails();
		if(error) {
			
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList1.size());
				//System.out.println("::: "+boq.getSlNo()+"::: "+boq.getRef_dsr());
				boQDetailsList1.add(boq);
			}
		Map<String, List<BoQDetails>> groupByMilesToneMap1 = 
				  boQDetailsList1.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList",groupByMilesToneMap1);
		}
		
		
		
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-estimate-form";
	}
	
	
	
	public String getEmployeeName(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
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
                        + getEmployeeName(stateHistory.getLastModifiedBy()));
                workflowHistory.put("status", stateHistory.getValue());
                final Long owner = stateHistory.getOwnerPosition();
                final State _sowner = stateHistory.getState();
               ownerobj=    this.microserviceUtils.getEmployee(owner, null, null, null).get(0);
                if (null != ownerobj) {
                    workflowHistory.put("user",ownerobj.getUser().getUserName()+"::"+ownerobj.getUser().getName());
                    
                    //edited....
                    if(ownerobj.getAssignments().get(0).getDepartment()!=null) {
                   
                    Department department=   this.microserviceUtils.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
                
                    if(null != department)
                        workflowHistory.put("department", department.getName());
                }
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
            map.put("updatedBy", state.getLastModifiedBy() + "::" + getEmployeeName(state.getLastModifiedBy()));
            map.put("status", state.getValue());
            final Long ownerPosition = state.getOwnerPosition();
            ownerobj=    this.microserviceUtils.getEmployee(ownerPosition, null, null, null).get(0);
            if(null != ownerobj){
                map.put("user", ownerobj.getUser().getUserName() + "::" + ownerobj.getUser().getName());
                //edited...
               if(ownerobj.getAssignments().get(0).getDepartment()!=null) {
              Department department=   this.microserviceUtils.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
             // Department department=   this.microserviceUtils.getDepartmentByCode(state.getDeptCode());
              if(null != department)
                  map.put("department", department.getName());
              //
              //                map.put("department", null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
//                        .getDepartmentForUser(user.getId()).getName() : "");
               }
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
	
	@RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "Works_Est");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		EstimatePreparationApproval estDetails = estimatePreparationApprovalRepository.findById(Long.parseLong(request.getParameter("estDetailsId")));
		estDetails = getBillDocuments(estDetails);

		for (final DocumentUpload doc : estDetails.getDocumentDetail())
			if (doc.getFileStore().getFileStoreId().equalsIgnoreCase(fileStoreId))
				fileName = doc.getFileStore().getFileName();

		// get MIME type of the file
		String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
		if (mimeType == null)
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";

		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());

		// set headers for the response
		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		final OutputStream outStream = response.getOutputStream();

		final byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1)
			outStream.write(buffer, 0, bytesRead);

		inputStream.close();
		outStream.close();
	}
	
	private EstimatePreparationApproval getBillDocuments(final EstimatePreparationApproval estDetails) {
		List<DocumentUpload> documentDetailsList = estimatePreparationApprovalService.findByObjectIdAndObjectType(estDetails.getId(),
				"Works_Est");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}
	
	private  String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and es.estimateDate>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and es.estimateDate<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
	
	public String getMisQuery( EstimatePreparationApproval estimate) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != estimate) {
			
			if ( estimate.getWorkStatusSearch() != null && !estimate.getWorkStatusSearch().isEmpty())
			{
				misQuery.append(" and es.status.description='")
						.append(estimate.getWorkStatusSearch()).append("'");
			}
			if ( estimate.getWorkName() != null && !estimate.getWorkName().isEmpty())
			{
				misQuery.append(" and es.workName like '%")
						.append(estimate.getWorkName()).append("%'");
			}
			if ( estimate.getFundSource() != null && !estimate.getFundSource().isEmpty())
			{
				misQuery.append(" and es.fundSource='")
						.append(estimate.getFundSource()).append("'");
			}
			
			
			if ( estimate.getEstimateNumber() != null && !estimate.getEstimateNumber().isEmpty())
			{
				misQuery.append(" and es.estimateNumber='")
						.append(estimate.getEstimateNumber()).append("'");
			}
			if(estimate.getWorksWing() != null)
			{
				misQuery.append(" and es.worksWing='")
				.append(estimate.getWorksWing()).append("'");
			}
			if(estimate.getWorkLocation() != null && !estimate.getWorkLocation().isEmpty())
			{
				misQuery.append(" and es.workLocation='")
				.append(estimate.getWorkLocation()).append("'");
			}
			if(estimate.getSectorNumber() != null)
			{
				misQuery.append(" and es.sectorNumber='")
				.append(estimate.getSectorNumber()).append("'");
			}
			if(estimate.getWardNumber() != null)
			{
				misQuery.append(" and es.wardNumber='")
				.append(estimate.getWardNumber()).append("'");
			}
			if(estimate.getWorkCategory() != null)
			{
				misQuery.append(" and es.workCategory='")
				.append(estimate.getWorkCategory()).append("'");
			}
			
			if(estimate.getExpHead_est() != null)
			{
				misQuery.append(" and es.expHead_est='")
				.append(estimate.getExpHead_est()).append("'");
			}
			if(estimate.getExecutingDivision() != null)
			{
				misQuery.append(" and es.executingDivision='")
				.append(estimate.getExecutingDivision()).append("'");
			}
			
			
			if(estimate.getEstimateAmount() != null)
			{
				misQuery.append(" and es.estimateAmount=")
				.append(estimate.getEstimateAmount());
			}
			
			
			
		}
		return misQuery.toString();

	}
	private EstimatePreparationApproval getRoughWorkBillDocuments(final EstimatePreparationApproval estDetails) {
		List<DocumentUpload> documentDetailsList = estimatePreparationApprovalService.findByObjectIdAndObjectType(estDetails.getId(),
				"roughWorkFile");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}
	
	@RequestMapping(value = "/downloadRoughWorkBillDoc", method = RequestMethod.GET)
	public void getBillDocRoughWork(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "roughWorkFile");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		EstimatePreparationApproval estDetails = estimatePreparationApprovalRepository.findById(Long.parseLong(request.getParameter("estDetailsId")));
		estDetails = getRoughWorkBillDocuments(estDetails);

		for (final DocumentUpload doc : estDetails.getDocumentDetail())
			if (doc.getFileStore().getFileStoreId().equalsIgnoreCase(fileStoreId))
				fileName = doc.getFileStore().getFileName();

		// get MIME type of the file
		String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
		if (mimeType == null)
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";

		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());

		// set headers for the response
		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		final OutputStream outStream = response.getOutputStream();

		final byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1)
			outStream.write(buffer, 0, bytesRead);

		inputStream.close();
		outStream.close();
	}
	
	// for Download estimate result written by sonu prajapati
	@RequestMapping(value = "/workEstimateSearch",params="workEstimateSearchResult" ,method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> excelEstimateResult(@ModelAttribute("workEstimateDetails")
	          final EstimatePreparationApproval estimatePreparationApproval,
	             final Model model, final HttpServletRequest request) throws Exception {
		
             List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

         // Convert input string into a date
         if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
         long department = Long.parseLong(estimatePreparationApproval.getDepartment());
         estimatePreparationApproval.setExecutingDivision(department);
         }
         EstimatePreparationApproval estimate=null;

        final StringBuffer query = new StringBuffer(500);
        List<Object[]> list =null;
        query
        .append(
            "select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from EstimatePreparationApproval es where es.executingDivision = ? ")
        .append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
        .append(getMisQuery(estimatePreparationApproval));
        System.out.println("Query :: "+query.toString());
        list = persistenceService.findAllBy(query.toString(),
		 estimatePreparationApproval.getExecutingDivision());
 
       if (list.size() != 0) {
	 
	   for (final Object[] object : list) {
		 estimate = new EstimatePreparationApproval();
		 estimate.setId(Long.parseLong(object[0].toString()));
		 if(object[1] != null)
		 {
			 estimate.setWorkName(object[1].toString());
		 }
		 if(object[2] != null)
		 {
			 estimate.setWorkCategry(object[2].toString());
		 }
		 if(object[3] != null)
		 {
			 estimate.setEstimateNumber(object[3].toString());
		 }
		 if(object[4] != null)
		 {
			 estimate.setEstimateDt(object[4].toString());
		 }
		 if(object[5] != null)
		 {
			 estimate.setEstimateAmount(Double.parseDouble(object[5].toString()));
		 }
		 String status=null;
		 if(object[6] != null)
		 {
			 status=object[6].toString();
			 estimate.setStatusDescription(map.get(status));
		 }
		 if(status != null && !status.equalsIgnoreCase("Approved"))
		 {
			 estimate.setPendingWith(populatePendingWith(estimate.getId()));
		 }
		 approvalList.add(estimate);
	   }
	 
    }
      estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
      estimatePreparationApproval.setEstimateList(approvalList);
		
		String[] COLUMNS = {"Name Of Work", "Estimate Number", "Estimate Date", "Estimate Ammount", "Work Status", "Pending With"};
		
		ByteArrayInputStream in = ExcelGenerator.estimateResultToExcel(approvalList, COLUMNS);
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=EstimateResult.xlsx");
		return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
		
	    }
		@RequestMapping(value = "/ajaxexecutivedivision", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public List<org.egov.infra.admin.master.entity.Department> ajaxexecutivedivision(@RequestParam final Long id) {

	    	
	    	System.out.println(":::::::  "+id);
	    	 List<org.egov.infra.admin.master.entity.Department> department = estimatePreparationApprovalService.getdepartment(id);
	    	for(org.egov.infra.admin.master.entity.Department d:department) {
	    		System.out.println(":::::: "+d.getName()+"::::: "+d.getCode());
	    	}
	   
	    	
	        
	        return department;

	    }
	 @RequestMapping(value = "/ajaxsubdivision", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public List<Subdivisionworks> ajaxsubdivision(@RequestParam final Long id) {

	    	
	    	System.out.println(":::::::  "+id);
	    	List<Subdivisionworks> Division= subdivision.findByDivisionid(id);
	    	for(Subdivisionworks s:Division) {
	    		System.out.println(":::: "+s.getSubdivision()+":::::: "+s.getId());
	    	}
	        //if (designationList.isEmpty())
	          //  designationList = designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
	        return Division;

	    }

}