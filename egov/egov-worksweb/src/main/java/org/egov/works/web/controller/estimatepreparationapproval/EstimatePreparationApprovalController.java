package org.egov.works.web.controller.estimatepreparationapproval;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
//import org.egov.works.estimatepreparationapproval.autonumber.AuditNumberGenerator;
import org.egov.works.estimatepreparationapproval.autonumber.EstimateNoGenerator;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.workestimate.service.WorkEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	WorkEstimateService workEstimateService;
	
	private static final String STATE_TYPE = "stateType";
	private static final String APPROVAL_POSITION = "approvalPosition";

    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    @Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;
    
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

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setDesignations(getDesignationsFromMs());
		model.addAttribute(STATE_TYPE, estimatePreparationApproval.getClass().getSimpleName());
        prepareWorkflow(model, estimatePreparationApproval, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        
		return "estimatepreparationapproval-form";
	}

	private void prepareValidActionListByCutOffDate(Model model) {
            model.addAttribute("validActionList",
                    Arrays.asList("Forward","Save As Draft"));
	}

	@RequestMapping(value = "/estimate", params = "Forward", method = RequestMethod.POST)
	public String saveBoQDetailsData(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file1") MultipartFile[] files, final HttpServletRequest request)
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
		estimatePreparationApproval.setDocumentDetail(list);
		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
			estimatePreparationApproval
					.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		}
		
		String deptCode = "";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = estimatePreparationApproval.getDepartment();
	    String estimateNumber = v.getEstimateNumber(deptCode);
		estimatePreparationApproval.setEstimateNumber(estimateNumber);
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
        
		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalService
				.saveEstimatePreparationData(request, estimatePreparationApproval,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;

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
		
		if(workflowaction.equalsIgnoreCase("Save As Draft"))
		{
			msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" is Saved as draft";
		}
		else if((workflowaction.equalsIgnoreCase("Forward") || workflowaction.equalsIgnoreCase("Approve")) && savedEstimatePreparationApproval.getStatus().getCode().equalsIgnoreCase("Approved"))
		{
			msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" TS is approved";
		}
		else 
		{
			approverName=getEmployeeName(Long.parseLong(approverDetails));
			if(savedEstimatePreparationApproval.getStatus().getCode().equals("AA Initiated"))
			{
				msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" has been approved and forwarded to "+approverName +" for AA inititation";
			}
			else if(savedEstimatePreparationApproval.getStatus().getCode().equals("TS Initiated"))
			{
				msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" AA has been approved and  forwarded to "+approverName +" for TS inititation";
			}
			else
			{
				msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+" has been forwarded to "+approverName;
			}
			
		}
		return msg;
	}

	@RequestMapping(value = "/estimate", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsDataDraft(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file1") MultipartFile[] files, final HttpServletRequest request)
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
	    String estimateNumber = v.getEstimateNumber(deptCode);
		estimatePreparationApproval.setEstimateNumber(estimateNumber);
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
        
		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalService
				.saveEstimatePreparationData(request, estimatePreparationApproval,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;

	}


	@RequestMapping(value = "/estimate", params = "save", method = RequestMethod.POST)
	public String saveBoqFileData(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file") MultipartFile file, final HttpServletRequest request)
			throws Exception {

		List boQDetailsList = new ArrayList();
		Long count = 0L;
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
							aBoQDetails.setItem_description(cell.getStringCellValue());
						} else if (cell.getColumnIndex() == 1) {
							aBoQDetails.setRef_dsr(cell.getStringCellValue());
						}else if (cell.getColumnIndex() == 2) {
							aBoQDetails.setUnit(cell.getStringCellValue());
						} 

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {

						 if (cell.getColumnIndex() == 3) {
							aBoQDetails.setRate(cell.getNumericCellValue());
						} else if (cell.getColumnIndex() == 4) {
							aBoQDetails.setQuantity(cell.getNumericCellValue());
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
							estAmt=estAmt+aBoQDetails.getAmount();
							
						}

					}

					if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
							&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
							&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
						count++;
						aBoQDetails.setSlNo(count);
						boQDetailsList.add(aBoQDetails);

					}
				}
			}

			// workbook.close();
			inputStream.close();

		} else {
			// response = "Please choose a file.";
		}
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setBoQDetailsList(boQDetailsList);
		estimatePreparationApproval.setDesignations(getDesignationsFromMs());
		estimatePreparationApproval.setEstimateAmount(estAmt);
		model.addAttribute(STATE_TYPE, estimatePreparationApproval.getClass().getSimpleName());
        prepareWorkflow(model, estimatePreparationApproval, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		model.addAttribute("estimatePreparationApproval", estimatePreparationApproval);
		model.addAttribute("fileuploadAllowed","Y");

		return "estimatepreparationapproval-form";

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
        			 Long wkCat=Long.parseLong(object[0].toString());
        			 if(wkCat == 1)
        			 {
        				 estimate.setWorkCategry("Road Work");
        			 }
        			 else if(wkCat == 2)
        			 {
        				 estimate.setWorkCategry("Bridge Work");
        			 }
        			 else
        			 {
        				 estimate.setWorkCategry("Maintenance Work");
        			 }
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
        		 approvalList.add(estimate);
        	 }
        	 
         }
        estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-estimate-form";

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Est",estimateDetails.getId());
		estimateDetails.setDocumentDetail(documents);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
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


		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Est",estimateDetails.getId());
		estimateDetails.setDocumentDetail(documents);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
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
			final HttpServletRequest request) throws Exception {

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
        
		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalService
				.saveEstimatePreparationData(request, estimatePreparationApproval,approvalPosition,approvalComment,approvalDesignation,workFlowAction);

		return "redirect:/estimatePreparation/success?approverDetails=" + approvalPosition + "&estId="
        + savedEstimatePreparationApproval.getId()+"&workflowaction="+workFlowAction;

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
			if ( estimate.getEstimateNumber() != null && !estimate.getEstimateNumber().isEmpty())
			{
				misQuery.append(" and es.estimateNumber='")
						.append(estimate.getEstimateNumber()).append("'");
			}
			if(estimate.getWorksWing() != null)
			{
				misQuery.append(" and es.worksWing=")
				.append(estimate.getWorksWing());
			}
			if(estimate.getWorkLocation() != null && !estimate.getWorkLocation().isEmpty())
			{
				misQuery.append(" and es.workLocation='")
				.append(estimate.getWorkLocation()).append("'");
			}
			if(estimate.getSectorNumber() != null)
			{
				misQuery.append(" and es.sectorNumber=")
				.append(estimate.getSectorNumber());
			}
			if(estimate.getWardNumber() != null)
			{
				misQuery.append(" and es.wardNumber=")
				.append(estimate.getWardNumber());
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

}
