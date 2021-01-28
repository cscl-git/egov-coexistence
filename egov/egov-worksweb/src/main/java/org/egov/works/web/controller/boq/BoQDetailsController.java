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
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.repository.WorkOrderAgreementRepository;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.autonumber.EstimateNoGenerator;
import org.egov.works.estimatepreparationapproval.autonumber.WorkNoGenerator;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
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

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
        prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		return "boqDetails";
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
		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId="
        + savedWorkOrderAgreement.getId()+"&workflowaction="+workFlowAction;

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
		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId="
        + savedWorkOrderAgreement.getId()+"&workflowaction="+workFlowAction;

	}
	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
    public String showSuccessPage(@RequestParam("approverDetails") final String approverDetails,@RequestParam("workflowaction") final String workflowaction, final Model model,
                                  final HttpServletRequest request,@RequestParam("estId") final String estId) {
		
		WorkOrderAgreement savedWorkOrderAgreement=workOrderAgreementRepository.getOne(Long.parseLong(estId));
		final String message = getMessageByStatus(savedWorkOrderAgreement, approverDetails,workflowaction);

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
			final Model model, @RequestParam("file") MultipartFile file, final HttpServletRequest request)
			throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		HashSet<String> milesstoneList=new HashSet<>();
		int count = 0;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		String FILE_PATH_PROPERTIES = "D:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		Double estAmt= 0.0;

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
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				BoQDetails aBoQDetails = new BoQDetails();

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

						 if (cell.getColumnIndex() == 4) {
							aBoQDetails.setRate(cell.getNumericCellValue());
						} else if (cell.getColumnIndex() == 5) {
							aBoQDetails.setQuantity(cell.getNumericCellValue());
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
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

		} else {
			// response = "Please choose a file.";
		}
		 Map<String, List<BoQDetails>> groupByMilesToneMap = 
				  boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setBoQDetailsList(boQDetailsList);
		workOrderAgreement.setWork_amount(String.valueOf(estAmt));
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
        prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
        model.addAttribute("milestoneList",groupByMilesToneMap);
        prepareValidActionListByCutOffDate(model);
        model.addAttribute("fileuploadAllowed","Y");
		return "boqDetails";

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
			System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"+workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		
		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		
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
       		if(object[7] != null)
       		{
       			agreement.setStatusDescp(object[7].toString());
       		}
       		if(agreement.getStatusDescp() != null && !agreement.getStatusDescp().equalsIgnoreCase("Approved") && !agreement.getStatusDescp().equalsIgnoreCase("Project Closed"))
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
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
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
            map.put("updatedBy", state.getLastModifiedBy() + "::" + getEmployeeName(state.getLastModifiedBy()));
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

	@RequestMapping(value = "/closure", method = RequestMethod.POST)
	public String closureForm(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		
		return "closure-work-agreement";
	}
	
	@RequestMapping(value = "/closuresearch", method = RequestMethod.POST)
	public String closuresearch(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		return "search-closure-work-agreement-form";
	}
	
	@RequestMapping(value = "/closuresearchPage", method = RequestMethod.POST)
	public String closuresearchPage(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		return "search-closure-work-agreement-page-form";
	}
	
	@RequestMapping(value = "/progressUpdate", method = RequestMethod.POST)
	public String progressUpdate(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		return "search-progress-work-agreement-page-form";
	}
	
	@RequestMapping(value = "/searchclosure", method = RequestMethod.POST)
	public String searchclosure(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
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
       			agreement.setStatusDescp(object[7].toString());
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
       			agreement.setStatusDescp(object[7].toString());
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
       			agreement.setStatusDescp(object[7].toString());
       		}
       		workList.add(agreement);
       		
       	 }
        }
		
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
