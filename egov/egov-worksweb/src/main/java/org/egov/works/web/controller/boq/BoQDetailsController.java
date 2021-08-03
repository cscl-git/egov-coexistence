package org.egov.works.web.controller.boq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.masters.services.ContractorService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.masters.Contractor;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.BoqDateUpdate;
import org.egov.works.boq.entity.BoqNewDetails;
import org.egov.works.boq.entity.BoqUploadDocument;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.repository.WorkOrderAgreementRepository;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.boq.service.BoqDateUpdateService;
import org.egov.works.boq.service.BoqDetailsPopService;
import org.egov.works.estimatepreparationapproval.autonumber.WorkNoGenerator;
import org.egov.works.estimatepreparationapproval.entity.Workswing;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/boq")
public class BoQDetailsController extends GenericWorkFlowController{

	@Autowired
	BoQDetailsService boQDetailsService;

	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;

	@Autowired
	public MicroserviceUtils microserviceUtils;
	private static final String STATE_TYPE = "stateType";
	private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    @Autowired
	private WorkOrderAgreementRepository workOrderAgreementRepository;
    @Autowired
	private AutonumberServiceBeanResolver beanResolver;
    private static final int BUFFER_SIZE = 4096;
    public static final Locale LOCALE = new Locale("en", "IN");
    public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
    @Autowired
	private FileStoreService fileStoreService;
    
    @Autowired
	private DocumentUploadRepository documentUploadRepository;
    
    @Autowired
	private ContractorService contractorService;
    
    @Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
    
    @Autowired
	private AppConfigValueService appConfigValuesService;
    
    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    BoqDateUpdateService boqDateUpdateService;
    
    @Autowired
    BoqDetailsPopService boqDetailsPopService;
    
private static Map<String, String> map; 
    
    // Instantiating the static map 
    static
    { 
        map = new HashMap<>(); 
        map.put("Created", "Under Work Agreement Approval"); 
        map.put("Pending for Approval", "Under Work Agreement Approval");
        map.put("Approved", "Work agreement approved");
        map.put("Project Modification Initiated", "Work monitoring in progress");
        map.put("Project Closed", "Work completed");
        map.put("Project Closure Initiated", "Work monitoring in progress");
        map.put("Project Progress Initiated", "Work monitoring in progress");
        
    }

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		List<Workswing> worskwing = estimatePreparationApprovalService.getworskwing();
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
        prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		return "boqDetails";
	}

	@RequestMapping(value = "/newboqform", method = RequestMethod.POST)
	public String newBoqFormGet(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails,
			final Model model, HttpServletRequest request) {
	
		
		return "newboqDetails-form";
	}
	@RequestMapping(value = "/saveboqform", method = RequestMethod.POST)
	public String savenewBoqFormGet(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails,
			final Model model, HttpServletRequest request) {
		
		BoqNewDetails boqdetail = boQDetailsService.saveNewBoqData(request, boqNewDetails);
		
		return "redirect:/boq/savesuccess?ref_dsr=" + boqNewDetails.getRef_dsr();
	}
	@RequestMapping(value = "/editboqnew",  method = RequestMethod.POST)
	public String editBoqData(
			@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails,
			final Model model, HttpServletRequest request) throws Exception {
	
		return "editnew-boq-details";
	}
	@RequestMapping(value = "/editboqnew", params = "editboqnew", method = RequestMethod.POST)
	public String editnewBoqData(
			@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails,
			final Model model, HttpServletRequest request) throws Exception {
		List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();

		// Convert input string into a date

		
		BoqNewDetails estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");
		 if (boqNewDetails.getRef_dsr() != null && boqNewDetails.getRef_dsr() != "" && !boqNewDetails.getRef_dsr().isEmpty()) {
				query.append("where bq.ref_dsr = ? ");
				
				System.out.println("Query :: "+query.toString());
				list = persistenceService.findAllBy(query.toString(),
		        		 boqNewDetails.getRef_dsr());
			}
		 else {
			 list = persistenceService.findAllBy(query.toString());
		 }
		 
        
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new BoqNewDetails();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setItem_description(object[1].toString());
        		 }
        		 if(object[2] != null)
        		 {
        			 estimate.setRef_dsr(object[2].toString());
        		 }
        		 if(object[3] != null)
        		 {
        			 estimate.setUnit(object[3].toString());
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setRate(Double.parseDouble(object[4].toString()));
        		 }
        		 
        		 approvalList.add(estimate);
        	 }
        	 
         }
       boqNewDetails.setEstimateList(approvalList);
		

		model.addAttribute("boqNewDetails",boqNewDetails);

		return "editnew-boq-details";

	}
	@RequestMapping(value = "/searchboqnew",  method = RequestMethod.POST)
	public String searcBoqData(
			@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails,
			final Model model, HttpServletRequest request) throws Exception {
	
		return "search-boq-details";
	}
	@RequestMapping(value = "/searchboqnew", params = "searchboqnew", method = RequestMethod.POST)
	public String searchBoqData(
			@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails,
			final Model model, HttpServletRequest request) throws Exception {
		List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();

		// Convert input string into a date

		
		BoqNewDetails estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");
		 if (boqNewDetails.getRef_dsr() != null && boqNewDetails.getRef_dsr() != "" && !boqNewDetails.getRef_dsr().isEmpty()) {
				query.append("where bq.ref_dsr = ? ");
				
				System.out.println("Query :: "+query.toString());
				list = persistenceService.findAllBy(query.toString(),
		        		 boqNewDetails.getRef_dsr());
			}
		 else {
			 list = persistenceService.findAllBy(query.toString());
		 }
		 
        
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new BoqNewDetails();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setItem_description(object[1].toString());
        		 }
        		 if(object[2] != null)
        		 {
        			 estimate.setRef_dsr(object[2].toString());
        		 }
        		 if(object[3] != null)
        		 {
        			 estimate.setUnit(object[3].toString());
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setRate(Double.parseDouble(object[4].toString()));
        		 }
        		 
        		 approvalList.add(estimate);
        	 }
        	 
         }
       boqNewDetails.setEstimateList(approvalList);
		

		model.addAttribute("boqNewDetails",boqNewDetails);

		return "search-boq-details";

	}
	@RequestMapping(value = "/work", params = "Forward/Reassign", method = RequestMethod.POST)
	public String saveBoQDetailsData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model,@RequestParam("file1") MultipartFile[] files, final HttpServletRequest request) throws Exception {
		String workFlowAction=workOrderAgreement.getWorkFlowAction();
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
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
		workOrderAgreement.setDocumentDetail(list);
		String deptCode = "";
		WorkNoGenerator v = beanResolver.getAutoNumberServiceFor(WorkNoGenerator.class);
		deptCode = workOrderAgreement.getDepartment();
		String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"works_div_"+deptCode).get(0).getValue();
		String estimateNumber ="";
		if(workOrderAgreement.getWork_agreement_number() == null || (workOrderAgreement.getWork_agreement_number() != null && workOrderAgreement.getWork_agreement_number().isEmpty()))
		{
			estimateNumber = v.getWorkNumber(deptShortCode);
		    workOrderAgreement.setWork_agreement_number(estimateNumber);
		}
	     
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
		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,approvalPosition,approvalComment,approvalDesignation,workFlowAction);
		Long id=savedWorkOrderAgreement.getId();
		if(workOrderAgreement.getDocUpload()!=null) {
			for(BoqUploadDocument boq:workOrderAgreement.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				if(boq.getObjectId()!=null) {
					Long update=boq.getObjectId();
					boQDetailsService.updateDocuments(id,update);
				}
			}
		}
		return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId="
        + savedWorkOrderAgreement.getId()+"&workflowaction="+workFlowAction;
	 }catch (Exception e) {
			e.printStackTrace();
		}
		        workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		        workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		        workOrderAgreement.setDepartment(workOrderAgreement.getDepartment());
		        workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		        workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		        workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getDepartment())));
     workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
     prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
     prepareValidActionListByCutOffDate(model);
		return "boqDetails";

	}
	
	
	@RequestMapping(value = "/work", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsDataDraft(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model,@RequestParam("file1") MultipartFile[] files, final HttpServletRequest request) throws Exception {
		String workFlowAction=workOrderAgreement.getWorkFlowAction();
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
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
		workOrderAgreement.setDocumentDetail(list);
		String deptCode = "";
		WorkNoGenerator v = beanResolver.getAutoNumberServiceFor(WorkNoGenerator.class);
		deptCode = workOrderAgreement.getDepartment();
		String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"works_div_"+deptCode).get(0).getValue();
		String estimateNumber ="";
		if(workOrderAgreement.getWork_agreement_number() == null || (workOrderAgreement.getWork_agreement_number() != null && workOrderAgreement.getWork_agreement_number().isEmpty()))
		{
			estimateNumber = v.getWorkNumber(deptShortCode);
		    workOrderAgreement.setWork_agreement_number(estimateNumber);
		}
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
		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,approvalPosition,approvalComment,approvalDesignation,workFlowAction);
		Long id=savedWorkOrderAgreement.getId();
		if(workOrderAgreement.getDocUpload()!=null) {
			for(BoqUploadDocument boq:workOrderAgreement.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				if(boq.getObjectId()!=null) {
					Long update=boq.getObjectId();
					boQDetailsService.updateDocuments(id,update);
				}
			}
		}
		return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId="
        + savedWorkOrderAgreement.getId()+"&workflowaction="+workFlowAction;
		        }catch (Exception e) {
					e.printStackTrace();
				}
		        workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		        workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		        workOrderAgreement.setDepartment(workOrderAgreement.getDepartment());
		        workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		        workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		        workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getDepartment())));
		        workOrderAgreement.setDepartments(getDepartmentsFromMs());
				workOrderAgreement.setContractors(getAllActiveContractors());
				model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		        prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		        prepareValidActionListByCutOffDate(model);
				return "boqDetails";

	}
	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("approverDetails") final String approverDetails,@RequestParam("workflowaction") final String workflowaction, final Model model,
                                  final HttpServletRequest request,@RequestParam("estId") final String estId) {
		
		WorkOrderAgreement savedWorkOrderAgreement=workOrderAgreementRepository.getOne(Long.parseLong(estId));
		final String message = getMessageByStatus(savedWorkOrderAgreement, approverDetails,workflowaction);

        model.addAttribute("message", message);

        return "works-success";
    }
	@RequestMapping(value = "/savesuccess", method = RequestMethod.GET)
    public String showSavePage(@RequestParam("ref_dsr") final String ref_dsr, final Model model,final HttpServletRequest request) {
		
		String message="BOQ Detail is successfully saved with Ref_Dsr/NS id : " +ref_dsr;

        model.addAttribute("message", message);

        return "works-success";
    }
	
	@RequestMapping(value = "/successProgress", method = RequestMethod.GET)
    public String successProgress(final Model model,final HttpServletRequest request) {
		
		final String message = "Work Agreement Closure has been completed";

        model.addAttribute("message", message);

        return "works-success";
    }
	
	private String getMessageByStatus(WorkOrderAgreement savedWorkOrderAgreement,
			String approverDetails, String workflowaction) {
		String approverName="";
		String msg="";
		
		if(workflowaction.equalsIgnoreCase("Save As Draft"))
		{
			msg="Work Agreement Number "+savedWorkOrderAgreement.getWork_agreement_number()+" is Saved as draft";
		}
		else if((workflowaction.equalsIgnoreCase("Forward/Reassign") || workflowaction.equalsIgnoreCase("Approve")) && savedWorkOrderAgreement.getStatus().getCode().equalsIgnoreCase("Approved"))
		{
			msg="Work Agreement Number "+savedWorkOrderAgreement.getWork_agreement_number()+" is approved";
		}
		else if((workflowaction.equalsIgnoreCase("Forward/Reassign") || workflowaction.equalsIgnoreCase("Approve")) && savedWorkOrderAgreement.getStatus().getCode().equalsIgnoreCase("Project Closed"))
		{
			msg="Work Agreement Number "+savedWorkOrderAgreement.getWork_agreement_number()+" is closed";
		}
		else 
		{
			approverName=getEmployeeName(Long.parseLong(approverDetails));
			msg="Work Agreement Number "+savedWorkOrderAgreement.getWork_agreement_number()+" has been forwarded to "+approverName;
		}
		return msg;
	}
	public String getEmployeeName(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	    }
	
	public String getEmployeeDesig(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getAssignments().get(0).getDesignation();
	    }

	@RequestMapping(value = "/work", params = "save", method = RequestMethod.POST)
	public String saveBoqFileData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, @RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files, final HttpServletRequest request)
			throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		HashSet<String> milesstoneList=new HashSet<>();
		int count = 0;
		String comments =request.getParameter("comments");
		String userName = boQDetailsService.getUserName();
		Boolean error=true;
		String msg="";
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		Double estAmt= 0.0;
		// for testing only
		List<DocumentUpload> docup = new ArrayList<>();
		/*for (int i = 0; i < workOrderAgreement.getBoQDetailsList().size(); i++) {
			BoQDetails aBoQDetails1 = new BoQDetails();
			List<BoQDetails> boqq = workOrderAgreement.getBoQDetailsList();
			
			
			if(boqq.get(i).getMilestone() != null && boqq.get(i).getItem_description() !=null && boqq.get(i).getRef_dsr() !=null && boqq.get(i).getRate() !=null && boqq.get(i).getAmount() !=null && boqq.get(i).getQuantity() !=null && boqq.get(i).getUnit() !=null ) {
			
				aBoQDetails1.setMilestone(boqq.get(i).getMilestone());

				aBoQDetails1.setItem_description(boqq.get(i).getItem_description());
				
				aBoQDetails1.setRef_dsr(boqq.get(i).getRef_dsr());
				
				aBoQDetails1.setUnit(boqq.get(i).getUnit());
				
				aBoQDetails1.setRate(boqq.get(i).getRate());
				
				aBoQDetails1.setQuantity(boqq.get(i).getQuantity());
				aBoQDetails1.setAmount(aBoQDetails1.getRate() * aBoQDetails1.getQuantity());
				estAmt=estAmt+aBoQDetails1.getAmount();
				count=boQDetailsList.size();
				
				aBoQDetails1.setSlNo(Long.valueOf(count));
				
				aBoQDetails1.setSizeIndex(count);
				
				
				boQDetailsList.add(aBoQDetails1);
			}
		}
*/
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
				msg="Uploaded document must contain Sheet with name Abst. with AOR";
				}
			}
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println("firstSheet;;"+firstSheet.getSheetName());
	//			Sheet firstSheet = workbook.getSheetAt(0);
			if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
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
						upload.setObjectType("roughWorkAgreementFile");
						upload.setComments(comments);
						upload.setUsername(userName);;
						//System.out.println("comments--------"+comments);
						docup.add(upload);
																																							
					}
			Iterator<Row> iterator = firstSheet.iterator();
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				BoQDetails aBoQDetails = new BoQDetails();
				//int rowNum = nextRow.getRowNum();
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();

					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

						if (cell.getColumnIndex() == 0) {
							aBoQDetails.setMilestone(cell.getStringCellValue());
							
						}
						
						else if (cell.getColumnIndex() == 1) {
							
							aBoQDetails.setItem_description(cell.getStringCellValue());
							
							
						} else if (cell.getColumnIndex() == 2) {
							
							aBoQDetails.setRef_dsr(cell.getStringCellValue());
							
							
						}else if (cell.getColumnIndex() == 3) {
							
							aBoQDetails.setUnit(cell.getStringCellValue());
							
							
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
						}
						 if (cell.getColumnIndex() == 4) {

							aBoQDetails.setRate(cell.getNumericCellValue());
							
							
						} else if (cell.getColumnIndex() == 5) {
							
							aBoQDetails.setQuantity(cell.getNumericCellValue());
							
								if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
								}else {
									error=true;
									msg="Please Check the upload Document,Error in Document Rate and Quantity must be number.";
									}
						}

					}

					if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
							&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
							&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
						count=boQDetailsList.size();
						aBoQDetails.setSlNo(Long.valueOf(count));
						aBoQDetails.setSizeIndex(count);
						boQDetailsList.add(aBoQDetails);
						
					

					}
				}
            
			}

			// workbook.close();
			inputStream.close();
			}else {
				
			}
			}

		} else {
			// response = "Please choose a file.";
		}
		int nextcount=1;
		List<BoqUploadDocument> docUpload=new ArrayList<>();
		
		if(workOrderAgreement.getDocUpload()!=null) {
			for(BoqUploadDocument boq:workOrderAgreement.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getUsername()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
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
		
		workOrderAgreement.setDocumentDetail(docup);
		if(!error) {
		DocumentUpload savedocebefore = boQDetailsService.savedocebefore(workOrderAgreement);
		System.out.println("::::username"+savedocebefore.getUsername()+":::::: "+savedocebefore.getObjectType()+" :::   "+savedocebefore.getFileStore().getId());
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
		workOrderAgreement.setWork_amount(String.valueOf(estAmt));
		 model.addAttribute("milestoneList",groupByMilesToneMap);
		 model.addAttribute("fileuploadAllowed","Y");
		}else {
					
				model.addAttribute("error", "Y");
				model.addAttribute("message", msg);
				}
		Map<String, List<BoqUploadDocument>> uploadDocument = 
				docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
	  model.addAttribute("uploadDocument", uploadDocument);
		 
		/*Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));*/
	  workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
	  workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
	  workOrderAgreement.setDepartment(workOrderAgreement.getDepartment());
	  workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
	  workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
	  workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getDepartment()))); 
		
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setBoQDetailsList(boQDetailsList);
		workOrderAgreement.setWork_amount(String.valueOf(estAmt));
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
        prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
        //model.addAttribute("milestoneList",groupByMilesToneMap);
        prepareValidActionListByCutOffDate(model);
       // model.addAttribute("fileuploadAllowed","Y");
		return "boqDetails";

	}

public List<BoqNewDetails> checkAvailableBoq(final String ref) {
	
	List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();
	if(ref!=null && ref !="")
	{
	
		BoqNewDetails estimate=null;
		
	final StringBuffer query = new StringBuffer(500);
	 List<Object[]> list =null;
	 query
        .append(
                "select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");
	
			query.append("where bq.ref_dsr = ? ");
			
			System.out.println("Query :: "+query.toString());
			list = persistenceService.findAllBy(query.toString(),ref);
		
		
		 
        
     if (list.size() != 0) {
    	 
        	 for (final Object[] object : list) {
        		 estimate = new BoqNewDetails();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setItem_description(object[1].toString());
     }
        		 if(object[2] != null)
        		 {
        			 estimate.setRef_dsr(object[2].toString());
        		 }
        		 if(object[3] != null)
        		 {
        			 estimate.setUnit(object[3].toString());
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setRate(Double.parseDouble(object[4].toString()));
        		 }
        		 
        		 approvalList.add(estimate);
    	 
    	  
     }}
        	 return approvalList; 
     }
	return approvalList;
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
	@RequestMapping(value = "/viewBoq/{id}", method = RequestMethod.GET)
	public String viewBoq(@PathVariable("id") final Long id, Model model) {
		
		System.out.println(id+"+++++++++++++++++++++++++++");
		BoqNewDetails boqNewDetails=boQDetailsService.viewBoqData(id);
		
		model.addAttribute("boqNewDetails",boqNewDetails);
		return "view-edit-page";
	}
	@RequestMapping(value = "/updateBoq/{id}", method = RequestMethod.GET)
	public String viewupdateBoq(@PathVariable("id") final Long id, Model model) {
		
		BoqNewDetails boqNewDetails=boQDetailsService.viewBoqData(id);
		
		model.addAttribute("boqNewDetails",boqNewDetails);
		return "update-edit-page";
	}
	@RequestMapping(value = "/updateBoq/updateBoq",method = RequestMethod.POST)
	public String updateBoq(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails, Model model,HttpServletRequest request) {
		
		/*System.out.println(boqNewDetails.getId()+"+++++++++++++++++++++++++++++++==");
		System.out.println(boqNewDetails.getItem_description()+"++++++++++++++");
		System.out.println(boqNewDetails.getRef_dsr()+"++++++++++++++++++");*/
		
		
	boQDetailsService.updateBoqData(boqNewDetails);
		
		
		return "redirect:/boq/savesuccess?ref_dsr=" + boqNewDetails.getRef_dsr();
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
			System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"+workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		
		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getExecuting_department())));
		
		workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		
		BoQDetails boq = new BoQDetails();
        for (int i = 0; i < workOrderAgreement.getNewBoQDetailsList().size(); i++) {
        		boq = workOrderAgreement.getNewBoQDetailsList().get(i);
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);
				
			}
        workOrderAgreement.setBoQDetailsList(responseList);
        Map<String, List<BoQDetails>> groupByMilesToneMap = 
        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
        model.addAttribute("milestoneList",groupByMilesToneMap);
		
		
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		if (workOrderAgreement.getState() != null)
            model.addAttribute("currentState", workOrderAgreement.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement.getState(), workOrderAgreement.getStateHistory()));
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed","Y");
		model.addAttribute("mode","view");
	
		return "view-work-agreement";
	}
	
	@RequestMapping(value = "/closureDetails/{id}", method = RequestMethod.GET)
	public String closureDetails(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
			System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"+workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getExecuting_department())));
		
		workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		if (workOrderAgreement.getState() != null)
            model.addAttribute("currentState", workOrderAgreement.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement.getState(), workOrderAgreement.getStateHistory()));
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
        prepareValidActionListByCutOffDate(model);
        
       
		BoQDetails boq = new BoQDetails();
        for (int i = 0; i < workOrderAgreement.getNewBoQDetailsList().size(); i++) {
        		boq = workOrderAgreement.getNewBoQDetailsList().get(i);
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);
				
			}
        workOrderAgreement.setBoQDetailsList(responseList);
        Map<String, List<BoQDetails>> groupByMilesToneMap = 
        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
        model.addAttribute("milestoneList",groupByMilesToneMap);
        
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed","Y");
	
		return "view-work-agreement-closure";
	}
	
	@RequestMapping(value = "/progress/{id}", method = RequestMethod.GET)
	public String progress(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
			//System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"+workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		
		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getExecuting_department())));
		
		workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		
		
		BoQDetails boq = new BoQDetails();
        for (int i = 0; i < workOrderAgreement.getNewBoQDetailsList().size(); i++) {
        		boq = workOrderAgreement.getNewBoQDetailsList().get(i);
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);
				
			}
        workOrderAgreement.setBoQDetailsList(responseList);
        Map<String, List<BoQDetails>> groupByMilesToneMap = 
        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
        model.addAttribute("milestoneList",groupByMilesToneMap);
		
		
		
		
		
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		if (workOrderAgreement.getState() != null)
            model.addAttribute("currentState", workOrderAgreement.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement.getState(), workOrderAgreement.getStateHistory()));
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed","Y");
		model.addAttribute("mode","view");
		System.out.println("test ending");
		return "view-work-agreement-progress";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String showEstimateNewFormGet(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-work-agreement-form";
	}

	@RequestMapping(value = "/workOrderAgreementSearch1", method = RequestMethod.POST)
	public String searchWorkOrderAgreement(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement=null;
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description from WorkOrderAgreement wo where wo.executing_department = ? ")
	        .append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
	        .append(getMisQuery(workOrderAgreement));
		 System.out.println("Query :: "+query.toString());
        list = persistenceService.findAllBy(query.toString(),
        		workOrderAgreement.getDepartment());
		
        if (list.size() != 0) {
       	 System.out.println(" data present");
       	 for (final Object[] object : list) {
       		agreement=new WorkOrderAgreement();
       		agreement.setId(Long.parseLong(object[0].toString()));
       		if(object[1] != null)
       		{
       			agreement.setName_work_order(object[1].toString());
       		}
       		if(object[2] != null)
       		{
       			agreement.setWork_number(object[2].toString());
       		}
       		if(object[3] != null)
       		{
       			agreement.setWork_agreement_number(object[3].toString());
       		}
       		if(object[4] != null)
       		{
       			agreement.setStartDate(object[4].toString());
       		}
       		if(object[5] != null)
       		{
       			agreement.setEndDate(object[5].toString());
       		}
       		if(object[6] != null)
       		{
       			agreement.setWork_amount(object[6].toString());
       		}
       		String status=null;
       		if(object[7] != null)
       		{
       			status=object[7].toString();
       			agreement.setStatusDescp(map.get(status));
       		}
       		if(status != null && !status.equalsIgnoreCase("Approved") && !status.equalsIgnoreCase("Project Closed"))
   		 {
       			agreement.setPendingWith(populatePendingWith(agreement.getId()));
   		 }
       		workList.add(agreement);
       		
       	 }
        }
		
		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-work-agreement-form";

	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		String result="";

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
			System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"+workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		
		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		String dept = workOrderAgreement.getExecuting_department();
		System.out.println("::::::Department::: "+dept);
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		System.out.println("::::::::asdads  "+workOrderAgreement.getWorksWing());
		workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		System.out.println(":::::  state::::"+workOrderAgreement.getState().getValue());
		if (workOrderAgreement.getState() != null)
            model.addAttribute("currentState", workOrderAgreement.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement.getState(), workOrderAgreement.getStateHistory()));
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed","Y");
		org.egov.infra.admin.master.entity.User user =null;
		
		BoQDetails boq = new BoQDetails();
        for (int i = 0; i < workOrderAgreement.getNewBoQDetailsList().size(); i++) {
        		boq = workOrderAgreement.getNewBoQDetailsList().get(i);
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);
				
			}
        workOrderAgreement.setBoQDetailsList(responseList);
        Map<String, List<BoQDetails>> groupByMilesToneMap = 
        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
        model.addAttribute("milestoneList",groupByMilesToneMap);
		
		if((workOrderAgreement.getProject_closure_comments() == null || workOrderAgreement.getProject_closure_comments().isEmpty()) && workOrderAgreement.getStatus().getDescription().equals("Approved"))
		{
			  user = securityUtils.getCurrentUser();
			  String desig=getEmployeeDesig(user.getId());
			  if(desig.equals("243"))
			  {
				  model.addAttribute("currentState", "Pending With SDO");  
			  }
			result="modify-work-agreement";
		}
		else if((workOrderAgreement.getProject_closure_comments() == null || workOrderAgreement.getProject_closure_comments().isEmpty()) && !workOrderAgreement.getStatus().getDescription().equals("Approved"))
		{
			result="edit-work-agreement";
		}
		else if(workOrderAgreement.getProject_closure_comments() != null && !workOrderAgreement.getProject_closure_comments().isEmpty())
		{
			result="view-work-agreement-closure";
		}
		return result;
	}
	@RequestMapping(value = "/edit/work1", params="save", method = RequestMethod.POST)
	public String editsaveBoqFileData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, @RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files, final HttpServletRequest request)
			throws Exception {
		
		Long id = workOrderAgreement.getId();
		System.out.println("::::::ID::: "+id);
		WorkOrderAgreement workOrderAgreement1 = boQDetailsService.viewWorkData(id);
		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		HashSet<String> milesstoneList=new HashSet<>();
		int count = 0;
		String comments =request.getParameter("comments");
		String userName = boQDetailsService.getUserName();
		Boolean error=true;
		String msg="";
		String result="";
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		Double estAmt= 0.0;
		// for testing only
		List<DocumentUpload> docup = new ArrayList<>();
		

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
				msg="Uploaded document must contain Sheet with name Abst. with AOR";
				}
			}
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println("firstSheet;;"+firstSheet.getSheetName());
	//			Sheet firstSheet = workbook.getSheetAt(0);
			if(firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
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
						upload.setObjectType("roughWorkAgreementFile");
						upload.setComments(comments);
						upload.setUsername(userName);;
						//System.out.println("comments--------"+comments);
						docup.add(upload);
																																							
					}
			Iterator<Row> iterator = firstSheet.iterator();
		while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				BoQDetails aBoQDetails = new BoQDetails();
				//int rowNum = nextRow.getRowNum();
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();

					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

						if (cell.getColumnIndex() == 0) {
							aBoQDetails.setMilestone(cell.getStringCellValue());
							
						}
						
						else if (cell.getColumnIndex() == 1) {
							
								aBoQDetails.setItem_description(cell.getStringCellValue());
							
							
						} else if (cell.getColumnIndex() == 2) {
							
								aBoQDetails.setRef_dsr(cell.getStringCellValue());
							
							
						}else if (cell.getColumnIndex() == 3) {
							
								aBoQDetails.setUnit(cell.getStringCellValue());
							
							
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
						}
						 if (cell.getColumnIndex() == 4) {
							 
								 aBoQDetails.setRate(cell.getNumericCellValue());
							
							
						} else if (cell.getColumnIndex() == 5) {
							
								aBoQDetails.setQuantity(cell.getNumericCellValue());
							
								if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
								}else {
									error=true;
									msg="Please Check the upload Document,Error in Document Rate and Quantity must be number.";
									}
						}

					}

					if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
							&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
							&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
						count=boQDetailsList.size();
						aBoQDetails.setSlNo(Long.valueOf(count));
						aBoQDetails.setSizeIndex(count);
						boQDetailsList.add(aBoQDetails);
						
					

					}
				}
            
			}

			// workbook.close();
			inputStream.close();
			}else {
				
			}
			}

		} else {
			// response = "Please choose a file.";
		}
		int nextcount=1;
		List<BoqUploadDocument> docUpload=new ArrayList<>();
		
		if(workOrderAgreement.getDocUpload()!=null) {
			for(BoqUploadDocument boq:workOrderAgreement.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getUsername()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
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
		
		workOrderAgreement.setDocumentDetail(docup);
		if(!error) {
		DocumentUpload savedocebefore = boQDetailsService.savedocebefore(workOrderAgreement);
		boQDetailsService.updateDocuments(id, savedocebefore.getId());
		System.out.println("::::username :: "+savedocebefore.getUsername()+":::::: "+savedocebefore.getObjectType()+" :::   "+savedocebefore.getFileStore().getId());
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
		workOrderAgreement1.setWork_amount(String.valueOf(estAmt));
		 model.addAttribute("milestoneList",groupByMilesToneMap);
		 model.addAttribute("fileuploadAllowed","Y");
		}else {
			  
				model.addAttribute("error", "Y");
				model.addAttribute("message", msg);
		  }
		Map<String, List<BoqUploadDocument>> uploadDocument = 
				docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
	  model.addAttribute("uploadDocument", uploadDocument);
		
	  if(error) {
			
		  BoQDetails boq = new BoQDetails();
	        for (int i = 0; i < workOrderAgreement1.getNewBoQDetailsList().size(); i++) {
	        		boq = workOrderAgreement1.getNewBoQDetailsList().get(i);
					boq.setSizeIndex(responseList.size());
					responseList.add(boq);
					
				}
	        Map<String, List<BoQDetails>> groupByMilesToneMap = 
	        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
	        model.addAttribute("milestoneList",groupByMilesToneMap);
			}
	  final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",workOrderAgreement1.getId());
		workOrderAgreement1.setDocumentDetail(documents);
	  workOrderAgreement1.setWorksWing(workOrderAgreement1.getWorksWing());
	  workOrderAgreement1.setWorkswings(estimatePreparationApprovalService.getworskwing());
	  workOrderAgreement1.setDepartment(workOrderAgreement1.getDepartment());
	  workOrderAgreement1.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement1.getWorksWing())));
	  workOrderAgreement1.setSubdivision(workOrderAgreement.getSubdivision());
	  workOrderAgreement1.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement1.getDepartment()))); 
		
	  workOrderAgreement1.setDepartments(getDepartmentsFromMs());
	  workOrderAgreement1.setContractors(getAllActiveContractors());
	  workOrderAgreement1.setBoQDetailsList(boQDetailsList);
	  workOrderAgreement1.setWork_amount(String.valueOf(estAmt));
		 model.addAttribute("fileuploadAllowed","Y");
		model.addAttribute("workOrderAgreement", workOrderAgreement1);
		model.addAttribute(STATE_TYPE, workOrderAgreement1.getClass().getSimpleName());
    prepareWorkflow(model, workOrderAgreement1, new WorkflowContainer());
        //model.addAttribute("milestoneList",groupByMilesToneMap);
        //prepareValidActionListByCutOffDate(model);
       /* if (workOrderAgreement1.getState() != null)
            model.addAttribute("currentState", workOrderAgreement1.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement1.getState(), workOrderAgreement1.getStateHistory()));*/
	
		
        
		//System.out.println("::::::::state:::: "+workOrderAgreement1.getState().getValue());
		prepareValidActionListByCutOffDate(model);
        org.egov.infra.admin.master.entity.User user =null;
        if((workOrderAgreement1.getProject_closure_comments() == null || workOrderAgreement1.getProject_closure_comments().isEmpty()) && workOrderAgreement1.getStatus().getDescription().equals("Approved"))
		{
			  user = securityUtils.getCurrentUser();
			  String desig=getEmployeeDesig(user.getId());
			  if(desig.equals("243"))
			  {
				  model.addAttribute("currentState", "Pending With SDO");  
			  }
			result="modify-work-agreement";
		}
		else if((workOrderAgreement1.getProject_closure_comments() == null || workOrderAgreement1.getProject_closure_comments().isEmpty()) && !workOrderAgreement1.getStatus().getDescription().equals("Approved"))
		{
			result="edit-work-agreement";
		}
		else if(workOrderAgreement1.getProject_closure_comments() != null && !workOrderAgreement1.getProject_closure_comments().isEmpty())
		{
			result="view-work-agreement-closure";
		}
		return result;

	}
	
	@RequestMapping(value = "/edit/work1", method = RequestMethod.POST)
	public String saveEditData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request,@RequestParam("file1") MultipartFile[] files) throws Exception {
		String workFlowAction=workOrderAgreement.getWorkFlowAction();
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
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
if(workOrderAgreement.getDocUpload()!=null) {
			
			Long ids =workOrderAgreement.getId();
			System.out.println("ids------"+ids);
			
			boQDetailsService.deleteBoqUploadData(ids);
		}
		workOrderAgreement.setDocumentDetail(list);
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
		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId="
        + savedWorkOrderAgreement.getId()+"&workflowaction="+workFlowAction;

	}
	
	/* Added By Kundan Kumar for save Modify Date on date change */	
	public void saveModifyDateDate(long id,HttpServletRequest request)
	{
		org.egov.infra.admin.master.entity.User user = securityUtils.getCurrentUser();
		String desig=getEmployeeName(user.getId()); 
		System.out.println("desig............................"+desig);
		BoqDateUpdate boqDateUpdate = new BoqDateUpdate();
		boqDateUpdate.setCreatedby(desig);
		boqDateUpdate.setActualEndDate(request.getParameter("actual_end_date"));
		boqDateUpdate.setReason(request.getParameter("reason"));
		boqDateUpdate.setSl_no(id);
		Date date=new Date();
		boqDateUpdate.setCreatedDate(date);
		boqDateUpdateService.saveUpdateDate(boqDateUpdate);
	}
//	@RequestMapping("/boq/viewdata")
//	public String viewModifyDate()
//	{
//		System.out.println("kundan view data here ............");
//		return "view-data";
//	}
	/* Ended By Kundan Kumar for save Modify Date on date change */	
	
	@RequestMapping(value = "/progress/updateProgress", method = RequestMethod.POST)
	public String updateProgress(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}
		
		//start of workflow
		
		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveProgress(request, workOrderAgreement);

		return "redirect:/boq/successProgress";

	}
	
	private void prepareValidActionListByCutOffDate(Model model) {
        model.addAttribute("validActionList",
                Arrays.asList("Forward/Reassign","Save As Draft"));
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
                if(ownerobj.getAssignments().get(0).getDepartment()!=null) {
              Department department=   this.microserviceUtils.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
              if(null != department)
                  map.put("department", department.getName());
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

	@RequestMapping(value = "/closure", method = RequestMethod.POST)
	public String closureForm(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		
		return "closure-work-agreement";
	}
	
	@RequestMapping(value = "/closuresearch", method = RequestMethod.POST)
	public String closuresearch(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		return "search-closure-work-agreement-form";
	}
	
	@RequestMapping(value = "/closuresearchPage", method = RequestMethod.POST)
	public String closuresearchPage(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		return "search-closure-work-agreement-page-form";
	}
	
	@RequestMapping(value = "/progressUpdate", method = RequestMethod.POST)
	public String progressUpdate(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		return "search-progress-work-agreement-page-form";
	}
	
	@RequestMapping(value = "/searchclosure", method = RequestMethod.POST)
	public String searchclosure(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement=null;
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description from WorkOrderAgreement wo where wo.status.description ='Approved' and wo.executing_department = ? ")
	        .append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
	        .append(getMisQuery(workOrderAgreement));
		 System.out.println("Query :: "+query.toString());
        list = persistenceService.findAllBy(query.toString(),
        		workOrderAgreement.getDepartment());
		
        if (list.size() != 0) {
       	 System.out.println(" data present");
       	 for (final Object[] object : list) {
       		agreement=new WorkOrderAgreement();
       		agreement.setId(Long.parseLong(object[0].toString()));
       		if(object[1] != null)
       		{
       			agreement.setName_work_order(object[1].toString());
       		}
       		if(object[2] != null)
       		{
       			agreement.setWork_number(object[2].toString());
       		}
       		if(object[3] != null)
       		{
       			agreement.setWork_agreement_number(object[3].toString());
       		}
       		if(object[4] != null)
       		{
       			agreement.setStartDate(object[4].toString());
       		}
       		if(object[5] != null)
       		{
       			agreement.setEndDate(object[5].toString());
       		}
       		if(object[6] != null)
       		{
       			agreement.setWork_amount(object[6].toString());
       		}
       		if(object[7] != null)
       		{
       			agreement.setStatusDescp(map.get(object[7].toString()));
       		}
       		workList.add(agreement);
       		
       	 }
        }
		
		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-closure-work-agreement-form";

	}
	
	@RequestMapping(value = "/searchclosurePage", method = RequestMethod.POST)
	public String searchclosurePage(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement=null;
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description from WorkOrderAgreement wo where wo.status.description ='Approved' and wo.executing_department = ? ")
	        .append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
	        .append(getMisQuery(workOrderAgreement));
        list = persistenceService.findAllBy(query.toString(),
        		workOrderAgreement.getDepartment());
		
        if (list.size() != 0) {
       	 for (final Object[] object : list) {
       		agreement=new WorkOrderAgreement();
       		agreement.setId(Long.parseLong(object[0].toString()));
       		if(object[1] != null)
       		{
       			agreement.setName_work_order(object[1].toString());
       		}
       		if(object[2] != null)
       		{
       			agreement.setWork_number(object[2].toString());
       		}
       		if(object[3] != null)
       		{
       			agreement.setWork_agreement_number(object[3].toString());
       		}
       		if(object[4] != null)
       		{
       			agreement.setStartDate(object[4].toString());
       		}
       		if(object[5] != null)
       		{
       			agreement.setEndDate(object[5].toString());
       		}
       		if(object[6] != null)
       		{
       			agreement.setWork_amount(object[6].toString());
       		}
       		if(object[7] != null)
       		{
       			agreement.setStatusDescp(map.get(object[7].toString()));
       		}
       		workList.add(agreement);
       		
       	 }
        }
		
		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-closure-work-agreement-page-form";

	}
	
	@RequestMapping(value = "/searchboqProgress", method = RequestMethod.POST)
	public String searchboqProgress(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement=null;
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description,wo.milestonestatus from WorkOrderAgreement wo where wo.status.description ='Approved' and wo.executing_department = ? ")
	        .append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
	        .append(getMisQuery(workOrderAgreement));
        list = persistenceService.findAllBy(query.toString(),
        		workOrderAgreement.getDepartment());
		
        if (list.size() != 0) {
       	 for (final Object[] object : list) {
       		agreement=new WorkOrderAgreement();
       		agreement.setId(Long.parseLong(object[0].toString()));
       		if(object[1] != null)
       		{
       			agreement.setName_work_order(object[1].toString());
       		}
       		if(object[2] != null)
       		{
       			agreement.setWork_number(object[2].toString());
       		}
       		if(object[3] != null)
       		{
       			agreement.setWork_agreement_number(object[3].toString());
       		}
       		if(object[4] != null)
       		{
       			agreement.setStartDate(object[4].toString());
       		}
       		if(object[5] != null)
       		{
       			agreement.setEndDate(object[5].toString());
       		}
       		if(object[6] != null)
       		{
       			agreement.setWork_amount(object[6].toString());
       		}
       		if(object[7] != null)
       		{
       			agreement.setStatusDescp(map.get(object[7].toString()));
       		}
       		if(object[8] != null)
       		{
       			agreement.setMilestonestatus(object[8].toString());;
       		}
       		workList.add(agreement);
       		
       	 }
        }
		//System.out.println("========++++++++++========");
		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-progress-work-agreement-page-form";

	}
	
	@RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "Works_Agreement");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		WorkOrderAgreement estDetails = workOrderAgreementRepository.findById(Long.parseLong(request.getParameter("workDetailsId")));
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
	
	private WorkOrderAgreement getBillDocuments(final WorkOrderAgreement estDetails) {
		List<DocumentUpload> documentDetailsList = boQDetailsService.findByObjectIdAndObjectType(estDetails.getId(),
				"Works_Agreement");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}
	@RequestMapping(value = "/checkref/{id}", method = RequestMethod.GET,produces = "application/json")
	public @ResponseBody String searchRef(@PathVariable("id") final String ref) {
		
		System.out.println("+++++++++++++++"+ref+"++++++++++++++++++++");
		System.out.println("id :: "+ref);
		if(ref!=null && ref !="")
		{
		List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();

		BoqNewDetails estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");
		
				query.append("where bq.ref_dsr = ? ");
				
				System.out.println("Query :: "+query.toString());
				list = persistenceService.findAllBy(query.toString(),ref);
			
		
		 
        
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new BoqNewDetails();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setItem_description(object[1].toString());
        		 }
        		 if(object[2] != null)
        		 {
        			 estimate.setRef_dsr(object[2].toString());
        		 }
        		 if(object[3] != null)
        		 {
        			 estimate.setUnit(object[3].toString());
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setRate(Double.parseDouble(object[4].toString()));
        		 }
        		 
        		 approvalList.add(estimate);
        	 }
        	 
        	 return "success"; 
         }
         
		}
		return "fail";
		
	}
	@RequestMapping(value = "/deleteBoq", method = RequestMethod.POST,produces = "application/json")
	public @ResponseBody String deleteBoq(@RequestParam("id") final String id,@RequestParam("slno") final String slno ) {
		
if (id !=null && id != "" && slno!= null && slno!="" ) {
	
	Long id1=Long.parseLong(id);
	Long slno1=Long.parseLong(slno);
	System.out.println("+++++ID+==++++++"+id+"+++++++slno==++++++"+slno+"+++++++++++++++++++");
			
			final StringBuffer query = new StringBuffer(500);
			query.append("update BoQDetails bq set bq.workOrderAgreement.id=null where bq.slNo=? and bq.workOrderAgreement.id=? ");
			persistenceService.deleteAllBy(query.toString(),slno1, id1);
		return "success";
}
		
		return "fail";
		
	}
	
	
	@RequestMapping(value = "/contractorid/{id}", method = RequestMethod.GET,produces = "application/json")
	public @ResponseBody Contractor getById(@PathVariable("id") final Long id) {
		System.out.println("id :: "+id);
		Contractor contractor = null;
		try
		{
			contractor = contractorService.getById(id);
			System.out.println("con ::"+contractor.getCode());
		}catch (Exception e) {
			e.printStackTrace();
		}

		return contractor;
	}
	
	public List<Contractor> getAllActiveContractors() {
		List<Contractor> contractors = contractorService.getAllActiveContractors();
		return contractors;
	}
	
	private  String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and wo.work_start_date>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and wo.work_start_date<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
			
			if (null != billDateFrom)
				numDateQuery.append(" and wo.work_intended_date>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and wo.work_intended_date<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
	
	public String getMisQuery( WorkOrderAgreement agreement) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != agreement) {
			if ( agreement.getName_work_order_search() != null && !agreement.getName_work_order_search().isEmpty())
			{
				misQuery.append(" and wo.name_work_order  like '%")
						.append(agreement.getName_work_order_search()).append("%'");
			}
			if(agreement.getWork_number_search() != null && !agreement.getWork_number_search().isEmpty())
			{
				misQuery.append(" and wo.work_number='")
				.append(agreement.getWork_number_search()).append("'");
			}
			if(agreement.getWork_agreement_number_search() != null && !agreement.getWork_agreement_number_search().isEmpty())
			{
				misQuery.append(" and wo.work_agreement_number='")
				.append(agreement.getWork_agreement_number_search()).append("'");
			}
			if(agreement.getWorksWing() != null && !agreement.getWorksWing().isEmpty())
			{
				misQuery.append(" and wo.worksWing='")
				.append(agreement.getWorksWing()).append("'");
			}
			if(agreement.getSubdivision() != null )
			{
				misQuery.append(" and wo.subdivision='")
				.append(agreement.getSubdivision()).append("'");
			}
			
		}
		return misQuery.toString();

	}
	private String populatePendingWith(Long id) {
		String pendingWith="";
		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		if(workOrderAgreement != null && workOrderAgreement.getState() != null && workOrderAgreement.getState().getOwnerName() != null && !workOrderAgreement.getState().getOwnerName().isEmpty())
		{
			try
			{
				pendingWith=workOrderAgreement.getState().getOwnerName();
			}catch (Exception e) {
				pendingWith="";
			}
			
		}
		return pendingWith;
	}

	
	
}
