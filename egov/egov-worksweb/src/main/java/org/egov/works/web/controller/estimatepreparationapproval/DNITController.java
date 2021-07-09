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
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.BoqNewDetails;
import org.egov.works.boq.entity.BoqUploadDocument;
//import org.egov.works.estimatepreparationapproval.autonumber.AuditNumberGenerator;
import org.egov.works.estimatepreparationapproval.autonumber.EstimateNoGenerator;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.entity.Workswing;
import org.egov.works.estimatepreparationapproval.repository.DNITCreationRepository;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.egov.works.estimatepreparationapproval.service.DNITCreationService;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.workestimate.service.WorkDnitService;
import org.egov.works.workestimate.service.WorkEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javaxt.utils.string;

@Controller
@RequestMapping(value = "/dnit")
public class DNITController extends GenericWorkFlowController {

	@Autowired
	DNITCreationService dnitCreationService;
	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;

	@Autowired
	private AutonumberServiceBeanResolver beanResolver;

	@Autowired
	public MicroserviceUtils microserviceUtils;

	@Autowired
	WorkEstimateService workEstimateService;
	
	@Autowired
	WorkDnitService workDnitService;
	
	@Autowired
	private AppConfigValueService appConfigValuesService;
	
	
	
	
	private static final String STATE_TYPE = "stateType";
	private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    @Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;
    @Autowired
    private DNITCreationRepository    dNITCreationRepository;
    private static final int BUFFER_SIZE = 4096;
    public static final Locale LOCALE = new Locale("en", "IN");
    public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
    
    @Autowired
	private FileStoreService fileStoreService;
    
    @Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
    
    @Autowired
	private DocumentUploadRepository documentUploadRepository;
    
private static Map<String, String> map; 
    
    // Instantiating the static map 
    static
    { 
        map = new HashMap<>(); 
        map.put("Created", "Under DNIT approval process"); 
        map.put("Pending for Approval", "Under DNIT approval process");
        map.put("Approved", "DNIT Approved");
    }

	@RequestMapping(value = "/createDnit", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, HttpServletRequest request) {
		List<Workswing> worskwing = estimatePreparationApprovalService.getworskwing();
		for(Workswing w:worskwing) {
			System.out.println("::::: "+w.getWorkswingname());
		}
		dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
        prepareWorkflow(model, dnitCreation, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        
		return "dnitCreation-form";
	}

	private void prepareValidActionListByCutOffDate(Model model) {
            model.addAttribute("validActionList",
                    Arrays.asList("Forward/Reassign","Save As Draft"));
	}

	@RequestMapping(value = "/dnitCreation", params = "Forward/Reassign", method = RequestMethod.POST)
	public String saveBoQDetailsData(
			@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files,@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {
		String workFlowAction=dnitCreation.getWorkFlowAction();
		
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != "" && !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation
					.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}
		
		String deptCode = "";
		String estimateNumber ="";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = dnitCreation.getDepartment();
		String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"works_div_"+deptCode).get(0).getValue();
		if(dnitCreation.getEstimateNumber() == null || (dnitCreation.getEstimateNumber()!=null && dnitCreation.getEstimateNumber().isEmpty()))
		{
			estimateNumber = v.getDNITNumber(deptShortCode);
		    dnitCreation.setEstimateNumber(estimateNumber);
		}
	     
		
	    dnitCreation.setDepartment(dnitCreation.getDepartment());
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
        DNITCreation savedEstimatePreparationApproval = dnitCreationService
				.saveEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		Long id=savedEstimatePreparationApproval.getId();
		if(dnitCreation.getDocUpload()!=null) {
			for(BoqUploadDocument boq:dnitCreation.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				if(boq.getObjectId()!=null) {
					Long update=boq.getObjectId();
					dnitCreationService.updateDocuments(id,update);
				}
			}
		}
		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;
        }catch (Exception e) {
        	e.printStackTrace();
			
		}
        String msg="Dnit Not Forwarded .";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		//dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartment(dnitCreation.getDepartment());
		dnitCreation.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		dnitCreation.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
        prepareWorkflow(model, dnitCreation, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);

		return "dnitCreation-form";
	}
		
		
	/*DNIT creation by EstimateApproval*/
	@RequestMapping(value = "/dnitCreationEstApproval", params = "Forward/Reassign", method = RequestMethod.POST)
	public String dnitCreationEstApproval(
			@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files,@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {
		String workFlowAction=dnitCreation.getWorkFlowAction();
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != "" && !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation
					.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}
		
		String deptCode = "";
		String estimateNumber ="";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = dnitCreation.getDepartment();
		String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"works_div_"+deptCode).get(0).getValue();
		if(dnitCreation.getEstimateNumber() == null || (dnitCreation.getEstimateNumber() !=null && dnitCreation.getEstimateNumber().isEmpty()))
		{
			estimateNumber = v.getDNITNumber(deptShortCode);
		    dnitCreation.setEstimateNumber(estimateNumber);
		}
	    dnitCreation.setDepartment(dnitCreation.getDepartment());
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
        
        DNITCreation savedEstimatePreparationApproval = dnitCreationService
				.saveDnitByEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;

        
	}
	//save as draft from estimate dnit by anshuman
	@RequestMapping(value = "/dnitCreationEstApproval", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsfromestimateDataDraft(
			@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files, @RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request)
			throws Exception {
		
		String workFlowAction=dnitCreation.getWorkFlowAction();
		System.out.println(":::::Works Wing:  "+dnitCreation.getWorksWing());
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (dnitCreation.getEstimateDt() != null && dnitCreation.getEstimateDt() != "") {
		Date estimateDate = inputFormat.parse(dnitCreation.getEstimateDt());
		dnitCreation.setEstimateDate(estimateDate);
		}

		if (dnitCreation.getDt() != null && dnitCreation.getDt() != "") {
		Date date = inputFormat.parse(dnitCreation.getDt());
		dnitCreation.setDate(date);
		}
		Long department = null;
		if (dnitCreation.getDepartment() != null || dnitCreation.getDepartment() != "") {
			
		
		 department = Long.parseLong(dnitCreation.getDepartment());
		 dnitCreation.setExecutingDivision(department);
		}

		String deptCode = "";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = dnitCreation.getDepartment();
		String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"works_div_"+deptCode).get(0).getValue();
	    String estimateNumber = v.getDNITNumber(deptShortCode);
	    dnitCreation.setEstimateNumber(estimateNumber);
	    dnitCreation.setDepartment(dnitCreation.getDepartment());
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
        DNITCreation saveddnitCreation = dnitCreationService
				.saveDnitByEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

        Long id=saveddnitCreation.getId();
		if(dnitCreation.getDocUpload()!=null) {
			for(BoqUploadDocument boq:dnitCreation.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				if(boq.getObjectId()!=null) {
					Long update=boq.getObjectId();
					dnitCreationService.updateDocuments(id,update);
				}
			}
		}
        
		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + saveddnitCreation.getId()+"&workflowaction="+workFlowAction;
	}catch (Exception e) {
		e.printStackTrace();
	}
        String msg="Dnit Not Saved As Draft .";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		//dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartment(dnitCreation.getDepartment());
		dnitCreation.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		dnitCreation.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		dnitCreation.setDesignations(getDesignationsFromMs());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
        prepareWorkflow(model, dnitCreation, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        model.addAttribute("estimatePreparationApproval", dnitCreation);
	
		return "dnitboqDetails";
	}
	
	@RequestMapping(value = "/dnitCreationEstApproval", params = "Approve", method = RequestMethod.POST)
	public String dnitCreationEstApprovalAprover(
			@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files,@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {
		String workFlowAction=dnitCreation.getWorkFlowAction();
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != "" && !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation
					.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
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
        
        DNITCreation savedEstimatePreparationApproval = dnitCreationService
				.saveDnitByEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;

	}
	
	
	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("approverDetails") final String approverDetails,@RequestParam("workflowaction") final String workflowaction, final Model model,
                                  final HttpServletRequest request,@RequestParam("estId") final String estId) {
		
		DNITCreation dnitCreation=dNITCreationRepository.getOne(Long.parseLong(estId));
		final String message = getMessageByStatus(dnitCreation, approverDetails,workflowaction);

        model.addAttribute("message", message);

        return "works-success";
    }

	
	private String getMessageByStatus(DNITCreation dnitCreation,
			String approverDetails, String workflowaction) {
		String approverName="";
		String msg="";
		
		if(workflowaction.equalsIgnoreCase("Save As Draft"))
		{
			msg="DNIT Number "+dnitCreation.getEstimateNumber()+" is Saved as draft";
		}
		else if(workflowaction.equalsIgnoreCase("Approve") && dnitCreation.getStatus().getCode().equalsIgnoreCase("Approved"))
		{
			msg="DNIT Number "+dnitCreation.getEstimateNumber()+"  is approved";
		}
		else 
		{
			approverName=getEmployeeName(Long.parseLong(approverDetails));
			
				msg="DNIT Number "+dnitCreation.getEstimateNumber()+" has been forwarded to "+approverName;
			
		}
		return msg;
	}

	@RequestMapping(value = "/dnitCreation", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsDataDraft(
			@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files, @RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request)
			throws Exception {
		
		String workFlowAction=dnitCreation.getWorkFlowAction();
		System.out.println(":::::Works Wing:  "+dnitCreation.getWorksWing());
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (dnitCreation.getEstimateDt() != null && dnitCreation.getEstimateDt() != "") {
		Date estimateDate = inputFormat.parse(dnitCreation.getEstimateDt());
		dnitCreation.setEstimateDate(estimateDate);
		}

		if (dnitCreation.getDt() != null && dnitCreation.getDt() != "") {
		Date date = inputFormat.parse(dnitCreation.getDt());
		dnitCreation.setDate(date);
		}
		Long department = null;
		if (dnitCreation.getDepartment() != null || dnitCreation.getDepartment() != "") {
			
		
		 department = Long.parseLong(dnitCreation.getDepartment());
		 dnitCreation.setExecutingDivision(department);
		}

		String deptCode = "";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = dnitCreation.getDepartment();
		String deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"works_div_"+deptCode).get(0).getValue();
	    String estimateNumber = v.getDNITNumber(deptShortCode);
	    dnitCreation.setEstimateNumber(estimateNumber);
	    dnitCreation.setDepartment(dnitCreation.getDepartment());
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
        DNITCreation saveddnitCreation = dnitCreationService
				.saveEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

        Long id=saveddnitCreation.getId();
		if(dnitCreation.getDocUpload()!=null) {
			for(BoqUploadDocument boq:dnitCreation.getDocUpload()) {
				System.out.println(":::: "+boq.getId()+":::::::"+boq.getComments()+":::::::::"+boq.getObjectId()+":::::"+boq.getFilestoreid());
				if(boq.getObjectId()!=null) {
					Long update=boq.getObjectId();
					dnitCreationService.updateDocuments(id,update);
				}
			}
		}
        
		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + saveddnitCreation.getId()+"&workflowaction="+workFlowAction;
	}catch (Exception e) {
		e.printStackTrace();
	}
        String msg="Dnit Not Saved As Draft .";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		//dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartment(dnitCreation.getDepartment());
		dnitCreation.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		dnitCreation.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		dnitCreation.setDesignations(getDesignationsFromMs());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
        prepareWorkflow(model, dnitCreation, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);

		return "dnitCreation-form";
	}


	@RequestMapping(value = "/dnitCreation", params = "save", method = RequestMethod.POST)
	public String uploadBoqFileData(
			@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files, final HttpServletRequest request)
			throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		List<DocumentUpload> docup = new ArrayList<>();
		String comments =request.getParameter("comments");
		HashSet<String> milesstoneList=new HashSet<>();
		String userName = estimatePreparationApprovalService.getUserName();
		Long userId = estimatePreparationApprovalService.getUserId();
		System.out.println(":::::User Name:::: "+userName+"::::UserID:::: "+userId);
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
						upload.setObjectType("roughWorkFileDnit");
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
							
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
						}
						 if (cell.getColumnIndex() == 4) {
							 
							aBoQDetails.setRate(cell.getNumericCellValue());
							//boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
								 
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
						boQDetailsList.add(aBoQDetails);
						//arrayboqDetailsPop.add(boqDetailsPop);
					

					}
				}
				
			}

			// workbook.close();
			inputStream.close();
			}else {
				//msg="Uploaded document must contain Sheet with name Abst. with AOR";
				//error=true;
			}
		}	

		} else {
			// response = "Please choose a file.";
		}
		int nextcount=1;
		List<BoqUploadDocument> docUpload=new ArrayList<>();
		
		if(dnitCreation.getDocUpload()!=null) {
			for(BoqUploadDocument boq:dnitCreation.getDocUpload()) {
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
		dnitCreation.setDocumentDetail(docup);
		dnitCreation.setRoughCostdocumentDetail(docup);
		if(!error) {
DocumentUpload savedocebefore = dnitCreationService.savedocebefore(dnitCreation);		
		
		
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
		
		
		
		  Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		  List<DocumentUpload> uploadDoc= new ArrayList<DocumentUpload>();
			
			DocumentUpload doc1= new DocumentUpload();
			Long fileStoreId = savedocebefore.getFileStore().getId();
			doc1.setId(savedocebefore.getId());
			doc1.setFileStore(savedocebefore.getFileStore());
			uploadDoc.add(doc1);
			model.addAttribute("milestoneList",groupByMilesToneMap);
			model.addAttribute("uploadDocID",uploadDoc);
			model.addAttribute("fileuploadAllowed","Y");
		
		}
		  
		  //dnitCreation.setDepartments(getDepartmentsFromMs());
		  dnitCreation.setBoQDetailsList(boQDetailsList);
		  dnitCreation.setDesignations(getDesignationsFromMs());
		  dnitCreation.setEstimateAmount(estAmt);
		  dnitCreation.setDocUpload(docUpload);
		  Map<String, List<BoqUploadDocument>> uploadDocument = 
					docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		  model.addAttribute("uploadDocument", uploadDocument);
		  dnitCreation.setBoQDetailsList(boQDetailsList);
		  dnitCreation.setDesignations(getDesignationsFromMs());
		  dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		  dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		  dnitCreation.setDepartment(dnitCreation.getDepartment());
		  dnitCreation.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		  dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		  dnitCreation.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		  if(!error) {
		  BigDecimal  bgestAmt = BigDecimal.valueOf(estAmt);
			if(estAmt >= 10000000){
				BigDecimal  pct = new BigDecimal(3);
				BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
				dnitCreation.setContingentPercentage(3.0);
				dnitCreation.setContingentAmount(ContingentAmt);
				
				BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
				Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
				dnitCreation.setEstimateAmount(dobestAmtPlusContingentAmt);
			}
			else {
				BigDecimal  pct = new BigDecimal(5);
				BigDecimal  ContingentAmt=percentage(bgestAmt,pct);
				dnitCreation.setContingentPercentage(5.0);
				dnitCreation.setContingentAmount(ContingentAmt);
				
				BigDecimal  estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt);
				Double dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
				dnitCreation.setEstimateAmount(dobestAmtPlusContingentAmt);
			}
		  }else {
		  
				model.addAttribute("error", "Y");
				model.addAttribute("message", msg);
		  }
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
        prepareWorkflow(model, dnitCreation, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		model.addAttribute("estimatePreparationApproval", dnitCreation);
		
		

		return "dnitCreation-form";

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

	@RequestMapping(value = "/searchDnit", method = RequestMethod.POST)
	public String showEstimateNewFormGet(
			@ModelAttribute("workdnitDetails") final DNITCreation dnitCreation,
			final Model model, HttpServletRequest request) {

		dnitCreation.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workdnitDetails", dnitCreation);

		return "search-dnit-form";
	}
	//edited
	@RequestMapping(value = "/workEditDnit", method = RequestMethod.POST)
	public String showEditEstimateNewFormGet(
			@ModelAttribute("workdnitDetails") final DNITCreation dnitCreation,
			final Model model, HttpServletRequest request) {

		dnitCreation.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workdnitDetails", dnitCreation);

		return "search-edit-dnit-form";
	}
	
	@RequestMapping(value = "/searchEditDnit", params = "searchEditDnit", method = RequestMethod.POST)
	public String searchWorkEditEstimateData(
			@ModelAttribute("workdnitDetails") final DNITCreation estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<DNITCreation> approvalList = new ArrayList<DNITCreation>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
		long department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}
		DNITCreation estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from DNITCreation es where es.executingDivision = ? and es.status='686' or es.status='625'")
	        .append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
	        .append(getMisQuery(estimatePreparationApproval));
		 System.out.println("Query :: "+query.toString());
         list = persistenceService.findAllBy(query.toString(),
        		 estimatePreparationApproval.getExecutingDivision());
		 
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new DNITCreation();
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
        			 estimate.setStatusDescription(object[6].toString());
        		 }
        		 if(estimate.getStatusDescription() != null && !estimate.getStatusDescription().equalsIgnoreCase("Approved"))
        		 {
        			 estimate.setPendingWith(populatePendingWith(estimate.getId()));
        		 }
        		 approvalList.add(estimate);
        	 }
        	 
         }
        estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-edit-dnit-form";

	}

	@RequestMapping(value = "/workDnitSearch", params = "workDnitSearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(
			@ModelAttribute("workdnitDetails") final DNITCreation estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<DNITCreation> approvalList = new ArrayList<DNITCreation>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
		long department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}
		DNITCreation estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from DNITCreation es where es.executingDivision = ? ")
	        .append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
	        .append(getMisQuery(estimatePreparationApproval));
		 System.out.println("Query :: "+query.toString());
         list = persistenceService.findAllBy(query.toString(),
        		 estimatePreparationApproval.getExecutingDivision());
		 
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 estimate = new DNITCreation();
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

		return "search-dnit-form";

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFileDnit",estimateDetails.getId());

		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		//estimateDetails.setDepartment(dept);
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setDepartment(dept);
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		
		
		
		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList.size());
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

		return "view-dnit-form";
	}
	
	@RequestMapping(value = "/editdnit/{id}", method = RequestMethod.GET)
	public String editDnit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();
		
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFileDnit",estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment()+"++++++"+estimateDetails.getSectorNumber()+"++++++++");
		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		//estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		//System.out.println(estimateDetails.getStatus().getCode()+"+++++++++++++++++++++++++++++++++++++++++++");
		
		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList.size());
				boQDetailsList.add(boq);
			}
		
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 //System.out.println(groupByMilesToneMap+"+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "edit-dnit-form";
	}
	@RequestMapping(value = "/deleteBoqdata", method = RequestMethod.POST,produces = "application/json")
	public @ResponseBody String deleteBoqdata(@RequestParam("id") final String id,@RequestParam("slno") final String slno ) {
		
if (id !=null && id != "" && slno!= null && slno!="" ) {
	
	Long id1=Long.parseLong(id);
	Long slno1=Long.parseLong(slno);
	System.out.println("+++++ID+==++++++"+id+"+++++++slno==++++++"+slno+"+++++++++++++++++++");
			
			final StringBuffer query = new StringBuffer(500);
			query.append("update BoQDetails bq set bq.dnitCreation.id=null where bq.slNo=? and bq.dnitCreation.id=? ");
			persistenceService.deleteAllBy(query.toString(),slno1, id1);
		return "success";
}
		
		return "fail";
		
		}
		
	@RequestMapping(value = "/deletednit/{id}/{slno}", method = RequestMethod.GET)
	public String deleteDnit(@PathVariable("id") final Long id,@PathVariable("slno") final Long slno, Model model) {

		if (id !=null && id != 0 && slno!= null && slno!=0 ) {
			
			final StringBuffer query = new StringBuffer(500);
			query.append("update BoQDetails bq set bq.dnitCreation.id=null where bq.slNo=? and bq.dnitCreation.id=? ");
			persistenceService.deleteAllBy(query.toString(),slno, id);
		}
		
	List<BoQDetails> boQDetailsList = new ArrayList();
		
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFileDnit",estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment()+"++++++"+estimateDetails.getSectorNumber()+"++++++++");
		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		
		System.out.println(estimateDetails.getStatus().getCode()+"+++++++++++++++++++++++++++++++++++++++++++");
		
		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList.size());
				boQDetailsList.add(boq);
			}
		
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 System.out.println(groupByMilesToneMap+"+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "edit-dnit-form";
	}
	@RequestMapping(value = "/editdnit/updateDnit",  method = RequestMethod.POST)
	public String editDnitEstimateData(
			@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,@RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request) throws Exception {

		String workFlowAction="Forward/Reassign";
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation
					.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
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
        
		DNITCreation savedDNITCreation = dnitCreationService
				.saveEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + savedDNITCreation.getId()+"&workflowaction="+workFlowAction;

	}
	
	@RequestMapping(value = "/deletednit/{id}/updateDnit",  method = RequestMethod.POST)
	public String updateDnitEstimateData(
			@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,@PathVariable("id") final Long id,@RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request) throws Exception {

		String workFlowAction="Forward/Reassign";
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation
					.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
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
        
		DNITCreation savedDNITCreation = dnitCreationService
				.saveEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + savedDNITCreation.getId()+"&workflowaction="+workFlowAction;

	}
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();
		
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFileDnit",estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment()+"++++++"+estimateDetails.getSectorNumber()+"++++++++");
		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		//estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		System.out.println("::::::::asdads  "+estimateDetails.getWorksWing());
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		
		System.out.println(estimateDetails.getStatus().getCode()+"+++++++++++++++++++++++++++++++++++++++++++");
		
		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {
			
				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList.size());
				boQDetailsList.add(boq);
			}
		
		
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 System.out.println(groupByMilesToneMap+"+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-dnit-form";
	}
	@RequestMapping(value = "/edits/{id}", method = RequestMethod.GET)
	public String editerr(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();
		
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFileDnit",estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment()+"++++++"+estimateDetails.getSectorNumber()+"++++++++");
		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		//estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		
		System.out.println(estimateDetails.getStatus().getCode()+"+++++++++++++++++++++++++++++++++++++++++++");
		
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
		 System.out.println(groupByMilesToneMap+"+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-dnit-form";
				}
	@RequestMapping(value = "/edit/updateDnit", params="save", method = RequestMethod.POST)
	public String editEstimateData1(
			@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,@RequestParam("file1") MultipartFile[] file1,Model model,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,@RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files,final HttpServletRequest request) throws Exception {

List<BoQDetails> boQDetailsList = new ArrayList();
		Long id = dnitCreation.getId();
System.out.println(":::::ID:::::: "+id);
String userName = estimatePreparationApprovalService.getUserName();
Long userId = estimatePreparationApprovalService.getUserId();
//System.out.println(":::::User Name:::: "+userName+"::::UserID:::: "+userId);
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
				msg="Uploaded document must contain Sheet with name Abst. with AOR";
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
						upload.setObjectType("roughWorkFileDnit");
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

						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
	}
						 if (cell.getColumnIndex() == 4) {
	
							aBoQDetails.setRate(cell.getNumericCellValue());
							//boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
	   
						} else if (cell.getColumnIndex() == 5) {
	   
							aBoQDetails.setQuantity(cell.getNumericCellValue());
	   
							if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
							}else {
								error=true;
								msg="Please Check the uploaded Document,Error in Document Rate and Quantity must be number.";
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
		if(dnitCreation.getDocUpload()!=null) {
			for(BoqUploadDocument boq:dnitCreation.getDocUpload()) {
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
		dnitCreation.setDocumentDetail(docup);  
		dnitCreation.setRoughCostdocumentDetail(docup);
		if(!error) {
		DocumentUpload savedocebefore = dnitCreationService.savedocebefore(dnitCreation);		
		dnitCreationService.updateDocuments(id, savedocebefore.getId());
		
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
		
		DocumentUpload doc1= new DocumentUpload();
		//Long fileStoreId = savedocebefore.getFileStore().getId();
		doc1.setId(savedocebefore.getId());
		doc1.setFileStore(savedocebefore.getFileStore());
		uploadDoc.add(doc1);
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute("uploadDocID",uploadDoc);
		}
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		Map<String, List<BoqUploadDocument>> uploadDocument = 
				docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		
		estimateDetails.setDocUpload(docUpload);
		if(!error) {
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
			
			estimateDetails.setBoQDetailsList(boQDetailsList);
		}
		}else {
			
			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}
		
		
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFileDnit",estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment()+"++++++"+estimateDetails.getSectorNumber()+"++++++++");
		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);
estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setBoQDetailsList(boQDetailsList);
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		//estimateDetails.setDepartment(estimateDetails.getDepartment());
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
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
		model.addAttribute("uploadDocument", uploadDocument);
		
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-dnit-form";
		

	}
	@RequestMapping(value = "/edits/updateDnit", params="save", method = RequestMethod.POST)
	public String editEstimateData1err(
			@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,@RequestParam("file1") MultipartFile[] file1,Model model,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,@RequestParam("file") MultipartFile file,@RequestParam("file") MultipartFile[] files,final HttpServletRequest request) throws Exception {

List<BoQDetails> boQDetailsList = new ArrayList();
		Long id = dnitCreation.getId();
System.out.println(":::::ID:::::: "+id);
String userName = estimatePreparationApprovalService.getUserName();
Long userId = estimatePreparationApprovalService.getUserId();
System.out.println(":::::User Name:::: "+userName+"::::UserID:::: "+userId);
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
				msg="Uploaded document must contain Sheet with name Abst. with AOR";
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
						upload.setObjectType("roughWorkFileDnit");
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
	   
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
						if(cell.getColumnIndex() == 2) {
							aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
							//System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
						}
						 if (cell.getColumnIndex() == 4) {
		
							aBoQDetails.setRate(cell.getNumericCellValue());
							//boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
	   
						} else if (cell.getColumnIndex() == 5) {
	   
							aBoQDetails.setQuantity(cell.getNumericCellValue());
	   
							if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
							}else {
								error=true;
								msg="Please Check the uploaded Document,Error in Document Rate and Quantity must be number.";
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
		if(dnitCreation.getDocUpload()!=null) {
			for(BoqUploadDocument boq:dnitCreation.getDocUpload()) {
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
		dnitCreation.setDocumentDetail(docup);  
		dnitCreation.setRoughCostdocumentDetail(docup);
		if(!error) {
		DocumentUpload savedocebefore = dnitCreationService.savedocebefore(dnitCreation);		
		dnitCreationService.updateDocuments(id, savedocebefore.getId());
		
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
		
		DocumentUpload doc1= new DocumentUpload();
		//Long fileStoreId = savedocebefore.getFileStore().getId();
		doc1.setId(savedocebefore.getId());
		doc1.setFileStore(savedocebefore.getFileStore());
		uploadDoc.add(doc1);
		Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute("uploadDocID",uploadDoc);
		}
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		Map<String, List<BoqUploadDocument>> uploadDocument = 
				docUpload.stream().collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		
		estimateDetails.setDocUpload(docUpload);
		if(!error) {
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
			
			estimateDetails.setBoQDetailsList(boQDetailsList);
		}
		}else {
			
			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}
		
		
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository.findByobjectTypeAndObjectId("roughWorkFileDnit",estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment()+"++++++"+estimateDetails.getSectorNumber()+"++++++++");
		
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);
estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		//estimateDetails.setDepartment(estimateDetails.getDepartment());
		estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
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
		model.addAttribute("uploadDocument", uploadDocument);
		
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
            model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-dnit-form";
		

	}
	
	@RequestMapping(value = "/edits/updateDnit",  method = RequestMethod.POST)
	public String editEstimateDataerr(
			@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,@RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request) throws Exception {

		String workFlowAction=dnitCreation.getWorkFlowAction();
		List<DocumentUpload> list = new ArrayList<>();
		
		Long id=dnitCreation.getId();
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		BoQDetails boq = new BoQDetails();
		
		if(dnitCreation.getDocUpload()!=null) {
			
			Long ids =dnitCreation.getId();
			System.out.println("ids------"+ids);
			
			dnitCreationService.deleteBoqUploadData(ids);
		}
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation
					.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
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
        try {
		DNITCreation savedDNITCreation = dnitCreationService
				.saveEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + savedDNITCreation.getId()+"&workflowaction="+workFlowAction;
		
        }catch (Exception e) {
			e.printStackTrace();
		}
        return "redirect:/dnit/edits/"+id;
	}
	@RequestMapping(value = "/edit/updateDnit",  method = RequestMethod.POST)
	public String editEstimateData(
			@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,@RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,final HttpServletRequest request) throws Exception {

		String workFlowAction=dnitCreation.getWorkFlowAction();
		List<DocumentUpload> list = new ArrayList<>();
		
		Long id=dnitCreation.getId();
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		BoQDetails boq = new BoQDetails();
		
		if(dnitCreation.getDocUpload()!=null) {
			
			Long ids =dnitCreation.getId();
			System.out.println("ids------"+ids);
			
			dnitCreationService.deleteBoqUploadData(ids);
		}
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
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation
					.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
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
        try {
		DNITCreation savedDNITCreation = dnitCreationService
				.saveEstimatePreparationData(request, dnitCreation,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
        + savedDNITCreation.getId()+"&workflowaction="+workFlowAction;
		
        }catch (Exception e) {
			e.printStackTrace();
		}
        return "redirect:/dnit/edits/"+id;
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
                System.out.println(owner+"++++++++++++++owner+++++++++++++++++++++");
               /* if(owner!=0) {
                if(microserviceUtils.getEmployee(owner, null, null, null)!=null) {
               ownerobj=    this.microserviceUtils.getEmployee(owner, null, null, null).get(0);
                }}*/
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
            System.out.println(ownerPosition+"++++++++++++++++++++++++++++++++++++++++++++++++");
         /* if(ownerPosition!=null) {
            	if((this.microserviceUtils.getEmployee(ownerPosition, null, null, null).get(0))!=null)
            	{
            ownerobj=    this.microserviceUtils.getEmployee(ownerPosition, null, null, null).get(0);
            }}
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
                */
               // map.put("user", null != user.getUserName() ? user.getUserName() + "::" + user.getName() : "");
                map.put("department", null != state.getDeptName() ? state.getDeptName() : "");
       // }
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
		final File downloadFile = fileStoreService.fetch(fileStoreId, "Works_Dnit");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		DNITCreation estDetails = dNITCreationRepository.findById(Long.parseLong(request.getParameter("estDetailsId")));
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
	
	private DNITCreation getBillDocuments(final DNITCreation estDetails) {
		List<DocumentUpload> documentDetailsList = dnitCreationService.findByObjectIdAndObjectType(estDetails.getId(),
				"Works_Dnit");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}
	private DNITCreation getRoughWorkBillDocuments(final DNITCreation estDetails) {
		List<DocumentUpload> documentDetailsList = dnitCreationService.findByObjectIdAndObjectType(estDetails.getId(),
				"roughWorkFileDnit");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}
	@RequestMapping(value = "/downloadRoughWorkBillDoc", method = RequestMethod.GET)
	public void getBillDocRoughWork(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "roughWorkFileDnit");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		//EstimatePreparationApproval estDetails = estimatePreparationApprovalRepository.findById(Long.parseLong(request.getParameter("estDetailsId")));
		DNITCreation estDetails = dNITCreationRepository.findById(Long.parseLong(request.getParameter("estDetailsId")));
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
	
	public String getMisQuery( DNITCreation estimate) {

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
				misQuery.append(" and es.workCategory=")
				.append(estimate.getWorkCategory());
			}
			if(estimate.getEstimateAmount() != null)
			{
				misQuery.append(" and es.estimateAmount=")
				.append(estimate.getEstimateAmount());
			}
			
		}
		return misQuery.toString();

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

}
