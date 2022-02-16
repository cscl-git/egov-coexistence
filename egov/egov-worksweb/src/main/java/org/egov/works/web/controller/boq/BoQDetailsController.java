package org.egov.works.web.controller.boq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
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
import java.util.LinkedHashMap;
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
import org.egov.infra.admin.master.entity.AppConfigValues;
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
import org.egov.works.boq.entity.BoqNewDetails;
import org.egov.works.boq.entity.BoqUploadDocument;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.repository.WorkOrderAgreementRepository;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.autonumber.WorkNoGenerator;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.Workswing;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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

@Controller
@RequestMapping(value = "/boq")
public class BoQDetailsController extends GenericWorkFlowController {

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

	private static Map<String, String> map;

	// Instantiating the static map
	static {
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
	public String newBoqFormGet(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails, final Model model,
			HttpServletRequest request) {

		return "newboqDetails-form";
	}

	@RequestMapping(value = "/saveboqform", method = RequestMethod.POST)
	public String savenewBoqFormGet(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails,
			final Model model, HttpServletRequest request) {

		BoqNewDetails boqdetail = boQDetailsService.saveNewBoqData(request, boqNewDetails);

		return "redirect:/boq/savesuccess?ref_dsr=" + boqNewDetails.getRef_dsr();
	}

	@RequestMapping(value = "/editboqnew", method = RequestMethod.POST)
	public String editBoqData(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails, final Model model,
			HttpServletRequest request) throws Exception {

		return "editnew-boq-details";
	}

	@RequestMapping(value = "/editboqnew", params = "editboqnew", method = RequestMethod.POST)
	public String editnewBoqData(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails, final Model model,
			HttpServletRequest request) throws Exception {
		List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();

		// Convert input string into a date

		BoqNewDetails estimate = null;

		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append("select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");
		if (boqNewDetails.getRef_dsr() != null && boqNewDetails.getRef_dsr() != ""
				&& !boqNewDetails.getRef_dsr().isEmpty()) {
			query.append("where bq.ref_dsr = ? ");

			System.out.println("Query :: " + query.toString());
			list = persistenceService.findAllBy(query.toString(), boqNewDetails.getRef_dsr());
		} else {
			list = persistenceService.findAllBy(query.toString());
		}

		if (list.size() != 0) {

			for (final Object[] object : list) {
				estimate = new BoqNewDetails();
				estimate.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					estimate.setItem_description(object[1].toString());
				}
				if (object[2] != null) {
					estimate.setRef_dsr(object[2].toString());
				}
				if (object[3] != null) {
					estimate.setUnit(object[3].toString());
				}
				if (object[4] != null) {
					estimate.setRate(Double.parseDouble(object[4].toString()));
				}

				approvalList.add(estimate);
			}

		}
		boqNewDetails.setEstimateList(approvalList);

		model.addAttribute("boqNewDetails", boqNewDetails);

		return "editnew-boq-details";

	}

	@RequestMapping(value = "/searchboqnew", method = RequestMethod.POST)
	public String searcBoqData(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails, final Model model,
			HttpServletRequest request) throws Exception {

		return "search-boq-details";
	}

	@RequestMapping(value = "/searchboqnew", params = "searchboqnew", method = RequestMethod.POST)
	public String searchBoqData(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails, final Model model,
			HttpServletRequest request) throws Exception {
		List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();

		// Convert input string into a date

		BoqNewDetails estimate = null;

		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append("select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");
		if (boqNewDetails.getRef_dsr() != null && boqNewDetails.getRef_dsr() != ""
				&& !boqNewDetails.getRef_dsr().isEmpty()) {
			query.append("where bq.ref_dsr = ? ");

			System.out.println("Query :: " + query.toString());
			list = persistenceService.findAllBy(query.toString(), boqNewDetails.getRef_dsr());
		} else {
			list = persistenceService.findAllBy(query.toString());
		}

		if (list.size() != 0) {

			for (final Object[] object : list) {
				estimate = new BoqNewDetails();
				estimate.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					estimate.setItem_description(object[1].toString());
				}
				if (object[2] != null) {
					estimate.setRef_dsr(object[2].toString());
				}
				if (object[3] != null) {
					estimate.setUnit(object[3].toString());
				}
				if (object[4] != null) {
					estimate.setRate(Double.parseDouble(object[4].toString()));
				}

				approvalList.add(estimate);
			}

		}
		boqNewDetails.setEstimateList(approvalList);

		model.addAttribute("boqNewDetails", boqNewDetails);

		return "search-boq-details";

	}

	@RequestMapping(value = "/work", params = "Forward/Reassign", method = RequestMethod.POST)
	public String saveBoQDetailsData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, @RequestParam("file1") MultipartFile[] files, final HttpServletRequest request)
			throws Exception {
		String workFlowAction = workOrderAgreement.getWorkFlowAction();
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}
		System.out.println("::::::::: " + workOrderAgreement.getWorkfrom());
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				if (files[i] == null || files[i].getOriginalFilename().isEmpty()) {
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
		// String
		// deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
		// "works_div_"+deptCode).get(0).getValue();a
		String deptShortCode = populateShortCode(deptCode, workOrderAgreement.getWorksWing(),
				workOrderAgreement.getSubdivision());
		String estimateNumber = "";
		if (workOrderAgreement.getWork_agreement_number() == null
				|| (workOrderAgreement.getWork_agreement_number() != null
						&& workOrderAgreement.getWork_agreement_number().isEmpty())) {
			estimateNumber = v.getWorkNumber(deptShortCode);
			workOrderAgreement.setWork_agreement_number(estimateNumber);
		}

		// start of workflow
		Long approvalPosition = 0l;
		String approvalComment = "";
		String approvalDesignation = "";
		if (request.getParameter("approvalComent") != null)
			approvalComment = request.getParameter("approvalComent");
		if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty()) {
			approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
		}

		if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
			approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
		try {
			WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request,
					workOrderAgreement, approvalPosition, approvalComment, approvalDesignation, workFlowAction);
			Long id = savedWorkOrderAgreement.getId();
			if (workOrderAgreement.getDocUpload() != null) {
				for (BoqUploadDocument boq : workOrderAgreement.getDocUpload()) {
					System.out.println(":::: " + boq.getId() + ":::::::" + boq.getComments() + ":::::::::"
							+ boq.getObjectId() + ":::::" + boq.getFilestoreid());
					if (boq.getObjectId() != null) {
						Long update = boq.getObjectId();
						boQDetailsService.updateDocuments(id, update);
					}
				}
			}
			return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId="
					+ savedWorkOrderAgreement.getId() + "&workflowaction=" + workFlowAction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setDepartment(workOrderAgreement.getDepartment());
		workOrderAgreement.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getDepartment())));
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		prepareValidActionListByCutOffDate(model);
		return "boqDetails";

	}

	@RequestMapping(value = "/work", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsDataDraft(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			@RequestParam("file1") MultipartFile[] files, final HttpServletRequest request) throws Exception {
		String workFlowAction = workOrderAgreement.getWorkFlowAction();
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}
		System.out.println("::::::::: " + workOrderAgreement.getWorkfrom());
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				if (files[i] == null || files[i].getOriginalFilename().isEmpty()) {
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
		// String
		// deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
		// "works_div_"+deptCode).get(0).getValue();
		String deptShortCode = populateShortCode(deptCode, workOrderAgreement.getWorksWing(),
				workOrderAgreement.getSubdivision());
		String estimateNumber = "";
		if (workOrderAgreement.getWork_agreement_number() == null
				|| (workOrderAgreement.getWork_agreement_number() != null
						&& workOrderAgreement.getWork_agreement_number().isEmpty())) {
			estimateNumber = v.getWorkNumber(deptShortCode);
			workOrderAgreement.setWork_agreement_number(estimateNumber);
		}
		// start of workflow
		Long approvalPosition = 0l;
		String approvalComment = "";
		String approvalDesignation = "";
		if (request.getParameter("approvalComent") != null)
			approvalComment = request.getParameter("approvalComent");
		if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty()) {
			approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
		}

		if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
			approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
		try {
			WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request,
					workOrderAgreement, approvalPosition, approvalComment, approvalDesignation, workFlowAction);
			Long id = savedWorkOrderAgreement.getId();
			if (workOrderAgreement.getDocUpload() != null) {
				for (BoqUploadDocument boq : workOrderAgreement.getDocUpload()) {
					System.out.println(":::: " + boq.getId() + ":::::::" + boq.getComments() + ":::::::::"
							+ boq.getObjectId() + ":::::" + boq.getFilestoreid());
					if (boq.getObjectId() != null) {
						Long update = boq.getObjectId();
						boQDetailsService.updateDocuments(id, update);
					}
				}
			}
			return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId="
					+ savedWorkOrderAgreement.getId() + "&workflowaction=" + workFlowAction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setDepartment(workOrderAgreement.getDepartment());
		workOrderAgreement.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getDepartment())));
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		prepareValidActionListByCutOffDate(model);
		return "boqDetails";

	}

	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String showSuccessPage(@RequestParam("approverDetails") final String approverDetails,
			@RequestParam("workflowaction") final String workflowaction, final Model model,
			final HttpServletRequest request, @RequestParam("estId") final String estId) {

		WorkOrderAgreement savedWorkOrderAgreement = workOrderAgreementRepository.getOne(Long.parseLong(estId));
		final String message = getMessageByStatus(savedWorkOrderAgreement, approverDetails, workflowaction);

		model.addAttribute("message", message);

		return "works-success";
	}

	@RequestMapping(value = "/savesuccess", method = RequestMethod.GET)
	public String showSavePage(@RequestParam("ref_dsr") final String ref_dsr, final Model model,
			final HttpServletRequest request) {

		String message = "BOQ Detail is successfully saved with Ref_Dsr/NS id : " + ref_dsr;

		model.addAttribute("message", message);

		return "works-success";
	}

	@RequestMapping(value = "/successProgress", method = RequestMethod.GET)
	public String successProgress(final Model model, final HttpServletRequest request) {

		final String message = "Work Agreement Closure has been completed";

		model.addAttribute("message", message);

		return "works-success";
	}

	private String getMessageByStatus(WorkOrderAgreement savedWorkOrderAgreement, String approverDetails,
			String workflowaction) {
		String approverName = "";
		String msg = "";

		if (workflowaction.equalsIgnoreCase("Save As Draft")) {
			msg = "Work Agreement Number " + savedWorkOrderAgreement.getWork_agreement_number() + " is Saved as draft";
		} else if ((workflowaction.equalsIgnoreCase("Forward/Reassign") || workflowaction.equalsIgnoreCase("Approve"))
				&& savedWorkOrderAgreement.getStatus().getCode().equalsIgnoreCase("Approved")) {
			msg = "Work Agreement Number " + savedWorkOrderAgreement.getWork_agreement_number() + " is approved";
		} else if ((workflowaction.equalsIgnoreCase("Forward/Reassign") || workflowaction.equalsIgnoreCase("Approve"))
				&& savedWorkOrderAgreement.getStatus().getCode().equalsIgnoreCase("Project Closed")) {
			msg = "Work Agreement Number " + savedWorkOrderAgreement.getWork_agreement_number() + " is closed";
		} else {
			approverName = getEmployeeName(Long.parseLong(approverDetails));
			msg = "Work Agreement Number " + savedWorkOrderAgreement.getWork_agreement_number()
					+ " has been forwarded to " + approverName;
		}
		return msg;
	}

	public String getEmployeeName(Long empId) {

		return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	}

	public String getEmployeeDesig(Long empId) {

		return microserviceUtils.getEmployee(empId, null, null, null).get(0).getAssignments().get(0).getDesignation();
	}

	@RequestMapping(value = "/work", params = "save", method = RequestMethod.POST)
	public String saveBoqFileData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, @RequestParam("file") MultipartFile file, @RequestParam("file") MultipartFile[] files,
			final HttpServletRequest request) throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		HashSet<String> milesstoneList = new HashSet<>();
		int count = 0;
		String comments = request.getParameter("comments");
		String userName = boQDetailsService.getUserName();
		Boolean error = true;
		String msg = "";
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		BigDecimal estAmt = new BigDecimal(0.);
		// for testing only
		List<DocumentUpload> docup = new ArrayList<>();

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
				if (firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
					error = false;
				} else {
					msg = "Please check the uploaded document as there is an issue in AOR detail sheet.";
				}
			}
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println("firstSheet;;" + firstSheet.getSheetName());
				// Sheet firstSheet = workbook.getSheetAt(0);
				if (firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
					// error=false;
					Row row = firstSheet.getRow(0);
					System.out.println("00 " + row.getCell(0).getStringCellValue() + " 111  "
							+ row.getCell(1).getStringCellValue() + "22 " + row.getCell(2).getStringCellValue() + " 33 "
							+ row.getCell(3).getStringCellValue() + " 44: " + row.getCell(4).getStringCellValue()
							+ " dd " + row.getCell(5).getStringCellValue());
					if (!row.getCell(0).getStringCellValue().equalsIgnoreCase("Scope/Milestone")
							|| !row.getCell(1).getStringCellValue().equalsIgnoreCase("Item Description")
							|| !row.getCell(2).getStringCellValue().equalsIgnoreCase("Ref DSR/NS")
							|| !row.getCell(3).getStringCellValue().equalsIgnoreCase("Unit")
							|| !row.getCell(4).getStringCellValue().equalsIgnoreCase("Rate")
							|| !row.getCell(5).getStringCellValue().equalsIgnoreCase("Quantity")) {
						error = true;
						msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Sequence of columns does not match.";
						break;
					}

					System.out.println("Physical number of rows  " + firstSheet.getPhysicalNumberOfRows());
					if (files != null)
						for (int j = 0; j < files.length; j++) {
							DocumentUpload upload = new DocumentUpload();
							if (files[j] == null || files[j].getOriginalFilename().isEmpty())

							{
								continue;

							}
							upload.setInputStream(
									new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
							upload.setFileName(files[j].getOriginalFilename());
							System.out.println(
									"files[i].getOriginalFilename():;;;;;;;;" + files[j].getOriginalFilename());
							upload.setContentType(files[j].getContentType());
							upload.setObjectType("roughWorkAgreementFile");
							upload.setComments(comments);
							upload.setUsername(userName);
							;
							// System.out.println("comments--------"+comments);
							docup.add(upload);

						}
					Iterator<Row> iterator = firstSheet.iterator();

					first: while (iterator.hasNext()) {
						Row nextRow = iterator.next();
						Iterator<Cell> cellIterator = nextRow.cellIterator();
						BoQDetails aBoQDetails = new BoQDetails();
						// erow=erow+1;
						// int rowNum = nextRow.getRowNum();
						while (cellIterator.hasNext()) {
							System.out.println("nextRow.getRowNum()" + nextRow.getRowNum());
							int erow = nextRow.getRowNum() + 1;
							if (erow > 250) {
								error = true;
								msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 250 items are allowed. ";
								break first;
							}
							Cell cell = (Cell) cellIterator.next();
							if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
								System.out.println("::Cell Type::: " + cell.getCellType() + "::::::Blank:: "
										+ cell.CELL_TYPE_BLANK);
								error = true;
								msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Blank column check row "
										+ erow;
								break first;

							}
							if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

								if (cell.getColumnIndex() == 0) {
									aBoQDetails.setMilestone(cell.getStringCellValue());

								}

								else if (cell.getColumnIndex() == 1) {

									aBoQDetails.setItem_description(cell.getStringCellValue());
									// boqDetailsPop.setItem_description(cell.getStringCellValue());
									int length = cell.getStringCellValue().length();
									System.out.println("item Description length   " + length);
									if (cell.getStringCellValue().length() > 10000) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Item Description exceeds 10000 character limit.Check Row "
												+ erow;
										break first;
									}

								} else if (cell.getColumnIndex() == 2) {

									aBoQDetails.setRef_dsr(cell.getStringCellValue());

								} else if (cell.getColumnIndex() == 3) {

									aBoQDetails.setUnit(cell.getStringCellValue());

								} else if (cell.getColumnIndex() == 5) {
									if (aBoQDetails.getRate() != null) {

										if (aBoQDetails.getQuantity() == null) {
											error = true;
											msg = "Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.Check Row number "
													+ erow;
											break first;
										}

									}
								}

							} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
								if (cell.getColumnIndex() == 2) {
									aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
									// System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
								}
								if (cell.getColumnIndex() == 4) {

									aBoQDetails.setRate(new BigDecimal(String.valueOf(cell.getNumericCellValue())));
									Double d = cell.getNumericCellValue();

									String[] splitter = d.toString().split("\\.");

									if (splitter[1].length() > 4) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 4 decimal place are allowed in Rate and Quantity.Check row number "
												+ erow;
										break first;
									}
								} else if (cell.getColumnIndex() == 5) {

									aBoQDetails.setQuantity(new BigDecimal(String.valueOf(cell.getNumericCellValue())));
									Double d = cell.getNumericCellValue();
									String[] splitter = d.toString().split("\\.");

									// System.out.println("::::Quantity::"+cell.getNumericCellValue()+"::oa:
									// "+aBoQDetails.getQuantity());
									if (splitter[1].length() > 4) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 4 decimal place are allowed in Rate and Quantity.Check row number "
												+ erow;
										break first;
									}
									if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
										aBoQDetails
												.setAmount(aBoQDetails.getRate().multiply(aBoQDetails.getQuantity()));
										estAmt = estAmt.add(aBoQDetails.getAmount());
									} else {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.Check row number "
												+ erow;
										break first;
									}
								}

							}

							if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
									&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
									&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
								count = boQDetailsList.size();
								aBoQDetails.setSlNo(Long.valueOf(count));
								aBoQDetails.setSizeIndex(count);
								boQDetailsList.add(aBoQDetails);

							}
						}

					}

					// workbook.close();
					inputStream.close();

				} else {

				}
			}

		} else {
			// response = "Please choose a file.";
		}
		int nextcount = 1;
		List<BoqUploadDocument> docUpload = new ArrayList<>();

		if (workOrderAgreement.getDocUpload() != null) {
			for (BoqUploadDocument boq : workOrderAgreement.getDocUpload()) {
				System.out.println(":::: " + boq.getId() + ":::::::" + boq.getUsername() + ":::::::::"
						+ boq.getObjectId() + ":::::" + boq.getFilestoreid());
				BoqUploadDocument boqUploadDocument = new BoqUploadDocument();
				if (boq.getObjectId() != null) {
					boqUploadDocument.setId(Long.valueOf(nextcount));
					boqUploadDocument.setObjectId(boq.getObjectId());
					boqUploadDocument.setObjectType(boq.getObjectType());
					boqUploadDocument.setFilestoreid(boq.getFilestoreid());
					boqUploadDocument.setComments(boq.getComments());
					boqUploadDocument.setUsername(boq.getUsername());
					docUpload.add(boqUploadDocument);
					nextcount = nextcount + 1;
				}
			}
		}

		workOrderAgreement.setDocumentDetail(docup);
		if (!error) {
			DocumentUpload savedocebefore = boQDetailsService.savedocebefore(workOrderAgreement);
			System.out.println("::::username" + savedocebefore.getUsername() + ":::::: "
					+ savedocebefore.getObjectType() + " :::   " + savedocebefore.getFileStore().getId());
			BoqUploadDocument boqUploadDocument2 = new BoqUploadDocument();
			// adding
			boqUploadDocument2.setId(Long.valueOf(nextcount));
			boqUploadDocument2.setObjectId(savedocebefore.getId());
			boqUploadDocument2.setObjectType(savedocebefore.getFileStore().getFileName());
			boqUploadDocument2.setFilestoreid(savedocebefore.getFileStore().getId());
			boqUploadDocument2.setComments(comments);
			boqUploadDocument2.setUsername(savedocebefore.getUsername());
			docUpload.add(boqUploadDocument2);
			Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
					.collect(Collectors.groupingBy(BoQDetails::getMilestone));
			BigDecimal bd = estAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
			System.out.println("::::::::work amount  :" + bd);
			workOrderAgreement.setWork_amount(String.valueOf(bd));
			model.addAttribute("milestoneList", groupByMilesToneMap);
			model.addAttribute("fileuploadAllowed", "Y");
		} else {

			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}
		Map<String, List<BoqUploadDocument>> uploadDocument = docUpload.stream()
				.collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		model.addAttribute("uploadDocument", uploadDocument);
		workOrderAgreement.setDocUpload(docUpload);
		/*
		 * Map<String, List<BoQDetails>> groupByMilesToneMap =
		 * boQDetailsList.stream().collect(Collectors.groupingBy(BoQDetails::
		 * getMilestone));
		 */
		workOrderAgreement.setSectorlist(getsectorlist());
		workOrderAgreement.setWardnumber(getwardlist());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setDepartment(workOrderAgreement.getDepartment());
		workOrderAgreement.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement.getDepartment())));

		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setBoQDetailsList(boQDetailsList);
		// workOrderAgreement.setWork_amount(String.valueOf(estAmt));
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		// model.addAttribute("milestoneList",groupByMilesToneMap);
		prepareValidActionListByCutOffDate(model);
		// model.addAttribute("fileuploadAllowed","Y");
		return "boqDetails";

	}

	public List<BoqNewDetails> checkAvailableBoq(final String ref) {

		List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();
		if (ref != null && ref != "") {

			BoqNewDetails estimate = null;

			final StringBuffer query = new StringBuffer(500);
			List<Object[]> list = null;
			query.append("select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");

			query.append("where bq.ref_dsr = ? ");

			System.out.println("Query :: " + query.toString());
			list = persistenceService.findAllBy(query.toString(), ref);

			if (list.size() != 0) {

				for (final Object[] object : list) {
					estimate = new BoqNewDetails();
					estimate.setId(Long.parseLong(object[0].toString()));
					if (object[1] != null) {
						estimate.setItem_description(object[1].toString());
					}
					if (object[2] != null) {
						estimate.setRef_dsr(object[2].toString());
					}
					if (object[3] != null) {
						estimate.setUnit(object[3].toString());
					}
					if (object[4] != null) {
						estimate.setRate(Double.parseDouble(object[4].toString()));
					}

					approvalList.add(estimate);

				}
			}
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

		System.out.println(id + "+++++++++++++++++++++++++++");
		BoqNewDetails boqNewDetails = boQDetailsService.viewBoqData(id);

		model.addAttribute("boqNewDetails", boqNewDetails);
		return "view-edit-page";
	}

	@RequestMapping(value = "/updateBoq/{id}", method = RequestMethod.GET)
	public String viewupdateBoq(@PathVariable("id") final Long id, Model model) {

		BoqNewDetails boqNewDetails = boQDetailsService.viewBoqData(id);

		model.addAttribute("boqNewDetails", boqNewDetails);
		return "update-edit-page";
	}

	@RequestMapping(value = "/updateBoq/updateBoq", method = RequestMethod.POST)
	public String updateBoq(@ModelAttribute("boqNewDetails") final BoqNewDetails boqNewDetails, Model model,
			HttpServletRequest request) {

		/*
		 * System.out.println(boqNewDetails.getId()+"+++++++++++++++++++++++++++++++==")
		 * ; System.out.println(boqNewDetails.getItem_description()+"++++++++++++++");
		 * System.out.println(boqNewDetails.getRef_dsr()+"++++++++++++++++++");
		 */

		boQDetailsService.updateBoqData(boqNewDetails);

		return "redirect:/boq/savesuccess?ref_dsr=" + boqNewDetails.getRef_dsr();
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"
				+ workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",
				workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService
				.getsubdivision(Long.valueOf(workOrderAgreement.getExecuting_department())));

		workOrderAgreement.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));

		BoQDetails boq = new BoQDetails();
		for (int i = 0; i < workOrderAgreement.getNewBoQDetailsList().size(); i++) {
			boq = workOrderAgreement.getNewBoQDetailsList().get(i);
			boq.setSizeIndex(responseList.size());
			responseList.add(boq);

		}
		workOrderAgreement.setBoQDetailsList(responseList);
		Map<String, List<BoQDetails>> groupByMilesToneMap = responseList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList", groupByMilesToneMap);

		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		if (workOrderAgreement.getState() != null)
			model.addAttribute("currentState", workOrderAgreement.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement.getState(), workOrderAgreement.getStateHistory()));
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed", "Y");
		model.addAttribute("mode", "view");

		return "view-work-agreement";
	}

	@RequestMapping(value = "/closureDetails/{id}", method = RequestMethod.GET)
	public String closureDetails(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"
				+ workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",
				workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService
				.getsubdivision(Long.valueOf(workOrderAgreement.getExecuting_department())));

		workOrderAgreement.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));

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
		Map<String, List<BoQDetails>> groupByMilesToneMap = responseList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList", groupByMilesToneMap);

		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed", "Y");

		return "view-work-agreement-closure";
	}

	@RequestMapping(value = "/progress/{id}", method = RequestMethod.GET)
	public String progress(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		// System.out.println("workOrderAgreement.getNewBoQDetailsList().size()
		// :"+workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",
				workOrderAgreement.getId());
		workOrderAgreement.setDocumentDetail(documents);

		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService
				.getsubdivision(Long.valueOf(workOrderAgreement.getExecuting_department())));

		workOrderAgreement.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));

		BoQDetails boq = new BoQDetails();
		for (int i = 0; i < workOrderAgreement.getNewBoQDetailsList().size(); i++) {
			boq = workOrderAgreement.getNewBoQDetailsList().get(i);
			boq.setSizeIndex(responseList.size());
			responseList.add(boq);

		}
		workOrderAgreement.setBoQDetailsList(responseList);
		Map<String, List<BoQDetails>> groupByMilesToneMap = responseList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList", groupByMilesToneMap);

		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		if (workOrderAgreement.getState() != null)
			model.addAttribute("currentState", workOrderAgreement.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement.getState(), workOrderAgreement.getStateHistory()));
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed", "Y");
		model.addAttribute("mode", "view");
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
		WorkOrderAgreement agreement = null;
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description from WorkOrderAgreement wo where 1=1")
				.append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
				.append(getMisQuery(workOrderAgreement));
		if (workOrderAgreement.getDepartment() != null) {
			query.append(" and wo.executing_department = ? ");
		}
		if (workOrderAgreement.getDepartment() != null) {
		System.out.println("Query :: " + query.toString());
		list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getDepartment());
		} else {
			System.out.println("Query :: " + query.toString());
			list = persistenceService.findAllBy(query.toString());
		}

		if (list.size() != 0) {
			System.out.println(" data present");
			for (final Object[] object : list) {
				agreement = new WorkOrderAgreement();
				agreement.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					agreement.setName_work_order(object[1].toString());
				}
				if (object[2] != null) {
					agreement.setWork_number(object[2].toString());
				}
				if (object[3] != null) {
					agreement.setWork_agreement_number(object[3].toString());
				}
				if (object[4] != null) {
					agreement.setStartDate(object[4].toString());
				}
				if (object[5] != null) {
					agreement.setEndDate(object[5].toString());
				}
				if (object[6] != null) {
					agreement.setWork_amount(object[6].toString());
				}
				String status = null;
				if (object[7] != null) {
					status = object[7].toString();
					agreement.setStatusDescp(map.get(status));
				}
				if (status != null && !status.equalsIgnoreCase("Approved")
						&& !status.equalsIgnoreCase("Project Closed")) {
					agreement.setPendingWith(populatePendingWith(agreement.getId()));
				}
				workList.add(agreement);

			}
		}

		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-work-agreement-form";

	}

	@RequestMapping(value = "/workOrderAgreementSearchExcel")
	public @ResponseBody ResponseEntity<InputStreamResource> boqExportToExcel(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws IOException {

//		LOGGER.info("ExportToExcel");
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement = null;
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description from WorkOrderAgreement wo where 1=1")
				.append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
				.append(getMisQuery(workOrderAgreement));
//		if (workOrderAgreement.getDepartment() != null || !workOrderAgreement.getDepartment().equals("")) {
		if (!workOrderAgreement.getDepartment().equals("") || !workOrderAgreement.getDepartment().isEmpty()) {
			query.append(" and wo.executing_department = ? ");
		}
		if (!workOrderAgreement.getDepartment().equals("") || !workOrderAgreement.getDepartment().isEmpty()) {
			System.out.println("Query :: " + query.toString());
			list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getDepartment());
		} else {
			System.out.println("Query :: " + query.toString());
			list = persistenceService.findAllBy(query.toString());
		}

		if (list.size() != 0) {
			System.out.println(" data present");
			for (final Object[] object : list) {
				agreement = new WorkOrderAgreement();
				agreement.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					agreement.setName_work_order(object[1].toString());
				}
				if (object[2] != null) {
					agreement.setWork_number(object[2].toString());
				}
				if (object[3] != null) {
					agreement.setWork_agreement_number(object[3].toString());
				}
				if (object[4] != null) {
					agreement.setStartDate(object[4].toString());
				}
				if (object[5] != null) {
					agreement.setEndDate(object[5].toString());
				}
				if (object[6] != null) {
					agreement.setWork_amount(object[6].toString());
				}
				String status = null;
				if (object[7] != null) {
					status = object[7].toString();
					agreement.setStatusDescp(map.get(status));
				}
				if (status != null && !status.equalsIgnoreCase("Approved")
						&& !status.equalsIgnoreCase("Project Closed")) {
					agreement.setPendingWith(populatePendingWith(agreement.getId()));
				}
				workList.add(agreement);

			}
		}
		String[] COLUMNS = { "Name of Work", "Estimate Number", "Work Agreement Number", "Work Order/Agreement Start Date", "Work Order/Agreement Intended End Date",
				"Amount", "Status","Pending with"};

		ByteArrayInputStream in = resultToExcel(workList, COLUMNS);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=WorkAgreementReport.xls");
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));

	}

	public static ByteArrayInputStream resultToExcel(List<WorkOrderAgreement> workList, String[] COLUMNS)
			throws IOException {

		HSSFWorkbook workbook = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		CreationHelper createHelper = workbook.getCreationHelper();

		Sheet sheet = workbook.createSheet("Work Agreement Report");

		// Row for Header
		Row headerRow = sheet.createRow(0);
		int sl = 1;
		// Header
		for (int col = 0; col < COLUMNS.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(COLUMNS[col]);
		}

		int rowIdx = 1;
		for (WorkOrderAgreement detail : workList) {
			Row row = sheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(detail.getName_work_order());
			row.createCell(1).setCellValue(detail.getWork_number());
			row.createCell(2).setCellValue(detail.getWork_agreement_number());
			row.createCell(3).setCellValue(detail.getStartDate());
			row.createCell(4).setCellValue(detail.getEndDate());
			String amount = detail.getWork_amount().toString();
			row.createCell(5).setCellValue(amount);
			row.createCell(6).setCellValue(detail.getStatusDescp());
			row.createCell(7).setCellValue(detail.getPendingWith());

		}

		workbook.write(out);
		return new ByteArrayInputStream(out.toByteArray());

	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		String result = "";

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		System.out.println("workOrderAgreement.getNewBoQDetailsList().size() :"
				+ workOrderAgreement.getNewBoQDetailsList().size());
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",
				workOrderAgreement.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkAgreementFile", workOrderAgreement.getId());
		String checkwork = workOrderAgreement.getWorkfrom();
		if (checkwork != null && checkwork.equalsIgnoreCase("EstandDnit")) {
			model.addAttribute("editable", "N");
		}
		workOrderAgreement.setDocumentDetail(documents);
		workOrderAgreement.setRoughCostdocumentDetail(roughCostEstmatedocuments);
		// workOrderAgreement.setSectorlist(getsectorlist());
		// workOrderAgreement.setWardnumber(getwardlist());
		workOrderAgreement.setBoQDetailsList(workOrderAgreement.getNewBoQDetailsList());
		workOrderAgreement.setDepartment(workOrderAgreement.getExecuting_department());
		String dept = workOrderAgreement.getExecuting_department();
		System.out.println("::::::Department::: " + dept);
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(workOrderAgreement.getWorksWing());
		workOrderAgreement.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		System.out.println("::::::::asdads  " + workOrderAgreement.getWorksWing());
		workOrderAgreement.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement.getWorksWing())));
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
		System.out.println(":::::  state::::" + workOrderAgreement.getState().getValue());
		if (workOrderAgreement.getState() != null)
			model.addAttribute("currentState", workOrderAgreement.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(workOrderAgreement.getState(), workOrderAgreement.getStateHistory()));
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed", "Y");
		org.egov.infra.admin.master.entity.User user = null;

		BoQDetails boq = new BoQDetails();
		for (int i = 0; i < workOrderAgreement.getNewBoQDetailsList().size(); i++) {
			boq = workOrderAgreement.getNewBoQDetailsList().get(i);
			boq.setSizeIndex(responseList.size());
			responseList.add(boq);

		}
		workOrderAgreement.setBoQDetailsList(responseList);
		Map<String, List<BoQDetails>> groupByMilesToneMap = responseList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone));
		model.addAttribute("milestoneList", groupByMilesToneMap);

		if ((workOrderAgreement.getProject_closure_comments() == null
				|| workOrderAgreement.getProject_closure_comments().isEmpty())
				&& workOrderAgreement.getStatus().getDescription().equals("Approved")) {
			user = securityUtils.getCurrentUser();
			String desig = getEmployeeDesig(user.getId());
			if (desig.equals("243")) {
				model.addAttribute("currentState", "Pending With SDO");
			}
			result = "modify-work-agreement";
		} else if ((workOrderAgreement.getProject_closure_comments() == null
				|| workOrderAgreement.getProject_closure_comments().isEmpty())
				&& !workOrderAgreement.getStatus().getDescription().equals("Approved")) {
			result = "edit-work-agreement";
		} else if (workOrderAgreement.getProject_closure_comments() != null
				&& !workOrderAgreement.getProject_closure_comments().isEmpty()) {
			result = "view-work-agreement-closure";
		}
		return result;
	}

	@RequestMapping(value = "/edit/work1", params = "save", method = RequestMethod.POST)
	public String editsaveBoqFileData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, @RequestParam("file") MultipartFile file, @RequestParam("file") MultipartFile[] files,
			final HttpServletRequest request) throws Exception {

		Long id = workOrderAgreement.getId();
		System.out.println("::::::ID::: " + id);
		WorkOrderAgreement workOrderAgreement1 = boQDetailsService.viewWorkData(id);
		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		HashSet<String> milesstoneList = new HashSet<>();
		int count = 0;
		String comments = request.getParameter("comments");
		String userName = boQDetailsService.getUserName();
		Boolean error = true;
		String msg = "";
		String result = "";
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		BigDecimal estAmt = new BigDecimal(0);
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
				if (firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
					error = false;
				} else {
					msg = "Please check the uploaded document as there is an issue in AOR detail sheet.";
				}
			}
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println("firstSheet;;" + firstSheet.getSheetName());
				// Sheet firstSheet = workbook.getSheetAt(0);
				if (firstSheet.getSheetName().equalsIgnoreCase("Abst. with AOR")) {
					// error=false;
					Row row = firstSheet.getRow(0);
					System.out.println("00 " + row.getCell(0).getStringCellValue() + " 111  "
							+ row.getCell(1).getStringCellValue() + "22 " + row.getCell(2).getStringCellValue() + " 33 "
							+ row.getCell(3).getStringCellValue() + " 44: " + row.getCell(4).getStringCellValue()
							+ " dd " + row.getCell(5).getStringCellValue());
					if (!row.getCell(0).getStringCellValue().equalsIgnoreCase("Scope/Milestone")
							|| !row.getCell(1).getStringCellValue().equalsIgnoreCase("Item Description")
							|| !row.getCell(2).getStringCellValue().equalsIgnoreCase("Ref DSR/NS")
							|| !row.getCell(3).getStringCellValue().equalsIgnoreCase("Unit")
							|| !row.getCell(4).getStringCellValue().equalsIgnoreCase("Rate")
							|| !row.getCell(5).getStringCellValue().equalsIgnoreCase("Quantity")) {
						error = true;
						msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Sequence of columns does not match.";
						break;
					}

					if (files != null)
						for (int j = 0; j < files.length; j++) {
							DocumentUpload upload = new DocumentUpload();
							if (files[j] == null || files[j].getOriginalFilename().isEmpty())

							{
								continue;

							}
							upload.setInputStream(
									new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
							upload.setFileName(files[j].getOriginalFilename());
							System.out.println(
									"files[i].getOriginalFilename():;;;;;;;;" + files[j].getOriginalFilename());
							upload.setContentType(files[j].getContentType());
							upload.setObjectType("roughWorkAgreementFile");
							upload.setComments(comments);
							upload.setUsername(userName);
							;
							// System.out.println("comments--------"+comments);
							docup.add(upload);

						}
					Iterator<Row> iterator = firstSheet.iterator();
					// List<BoqDetailsPop> array1boqDetailsPop = new ArrayList<BoqDetailsPop>();

					first: while (iterator.hasNext()) {
						Row nextRow = iterator.next();
						Iterator<Cell> cellIterator = nextRow.cellIterator();
						BoQDetails aBoQDetails = new BoQDetails();
						// int rowNum = nextRow.getRowNum();
						while (cellIterator.hasNext()) {
							int erow = nextRow.getRowNum() + 1;
							if (erow > 250) {
								error = true;
								msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 250 items are allowed. ";
								break first;
							}
							Cell cell = (Cell) cellIterator.next();

							if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
								System.out.println("::Cell Type::: " + cell.getCellType() + "::::::Blank:: "
										+ cell.CELL_TYPE_BLANK);
								error = true;
								msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Blank Column in Row "
										+ erow;
								break first;
							}
							if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

								if (cell.getColumnIndex() == 0) {
									aBoQDetails.setMilestone(cell.getStringCellValue());

								}

								else if (cell.getColumnIndex() == 1) {

									aBoQDetails.setItem_description(cell.getStringCellValue());
									int length = cell.getStringCellValue().length();

									if (cell.getStringCellValue().length() > 10000) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Item Description exceeds 10000 character limit.Check Row "
												+ erow;
										break first;
									}
									// boqDetailsPop.setItem_description(cell.getStringCellValue());

								} else if (cell.getColumnIndex() == 2) {

									aBoQDetails.setRef_dsr(cell.getStringCellValue());

								} else if (cell.getColumnIndex() == 3) {

									aBoQDetails.setUnit(cell.getStringCellValue());

								} else if (cell.getColumnIndex() == 5) {
									if (aBoQDetails.getRate() != null) {

										if (aBoQDetails.getQuantity() == null) {
											error = true;
											msg = "Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.Check Row "
													+ erow;
											break first;
										}

									}
								}
							} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
								if (cell.getColumnIndex() == 2) {
									aBoQDetails.setRef_dsr(String.valueOf(cell.getNumericCellValue()));
									// System.out.println(":::::Ref numeric No::: "+cell.getNumericCellValue());
								}
								if (cell.getColumnIndex() == 4) {

									aBoQDetails.setRate(new BigDecimal(String.valueOf(cell.getNumericCellValue())));
									Double d = cell.getNumericCellValue();

									String[] splitter = d.toString().split("\\.");

									if (splitter[1].length() > 4) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 4 Digits are allowed after Decimal.Check row "
												+ erow;
										break first;
									}
								} else if (cell.getColumnIndex() == 5) {

									aBoQDetails.setQuantity(new BigDecimal(String.valueOf(cell.getNumericCellValue())));
									Double d = cell.getNumericCellValue();
									String[] splitter = d.toString().split("\\.");

									// System.out.println("::::Quantity::"+cell.getNumericCellValue()+"::oa:
									// "+aBoQDetails.getQuantity());
									if (splitter[1].length() > 4) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet. Only 4 Digits are allowed after Decimal.Check row "
												+ erow;
										break first;
									}

									if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
										aBoQDetails
												.setAmount(aBoQDetails.getRate().multiply(aBoQDetails.getQuantity()));
										;
										estAmt = estAmt.add(aBoQDetails.getAmount());
									} else {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.Check row "
												+ erow;
										break first;
									}
								}

							}

							if (aBoQDetails.getItem_description() != null && aBoQDetails.getRef_dsr() != null
									&& aBoQDetails.getUnit() != null && aBoQDetails.getRate() != null
									&& aBoQDetails.getQuantity() != null && aBoQDetails.getAmount() != null) {
								count = boQDetailsList.size();
								aBoQDetails.setSlNo(Long.valueOf(count));
								aBoQDetails.setSizeIndex(count);
								boQDetailsList.add(aBoQDetails);

							}

						}

						// workbook.close();
						inputStream.close();
					}
				} else {

				}
			}

		} else {
			// response = "Please choose a file.";
		}
		int nextcount = 1;
		List<BoqUploadDocument> docUpload = new ArrayList<>();

		if (workOrderAgreement.getDocUpload() != null) {
			for (BoqUploadDocument boq : workOrderAgreement.getDocUpload()) {
				System.out.println(":::: " + boq.getId() + ":::::::" + boq.getUsername() + ":::::::::"
						+ boq.getObjectId() + ":::::" + boq.getFilestoreid());
				BoqUploadDocument boqUploadDocument = new BoqUploadDocument();
				if (boq.getObjectId() != null) {
					boqUploadDocument.setId(Long.valueOf(nextcount));
					boqUploadDocument.setObjectId(boq.getObjectId());
					boqUploadDocument.setObjectType(boq.getObjectType());
					boqUploadDocument.setFilestoreid(boq.getFilestoreid());
					boqUploadDocument.setComments(boq.getComments());
					boqUploadDocument.setUsername(boq.getUsername());
					docUpload.add(boqUploadDocument);
					nextcount = nextcount + 1;
				}
			}
		}

		workOrderAgreement.setDocumentDetail(docup);
		if (!error) {
			DocumentUpload savedocebefore = boQDetailsService.savedocebefore(workOrderAgreement);
			boQDetailsService.updateDocuments(id, savedocebefore.getId());
			System.out.println("::::username :: " + savedocebefore.getUsername() + ":::::: "
					+ savedocebefore.getObjectType() + " :::   " + savedocebefore.getFileStore().getId());
			BoqUploadDocument boqUploadDocument2 = new BoqUploadDocument();
			// adding
			boqUploadDocument2.setId(Long.valueOf(nextcount));
			boqUploadDocument2.setObjectId(savedocebefore.getId());
			boqUploadDocument2.setObjectType(savedocebefore.getFileStore().getFileName());
			boqUploadDocument2.setFilestoreid(savedocebefore.getFileStore().getId());
			boqUploadDocument2.setComments(comments);
			boqUploadDocument2.setUsername(savedocebefore.getUsername());
			docUpload.add(boqUploadDocument2);
			Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
					.collect(Collectors.groupingBy(BoQDetails::getMilestone));

			workOrderAgreement1.setWork_amount(String.valueOf(estAmt));
			model.addAttribute("milestoneList", groupByMilesToneMap);
			model.addAttribute("fileuploadAllowed", "Y");
		} else {

			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}
		Map<String, List<BoqUploadDocument>> uploadDocument = docUpload.stream()
				.collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		model.addAttribute("uploadDocument", uploadDocument);

		if (error) {

			BoQDetails boq = new BoQDetails();
			for (int i = 0; i < workOrderAgreement1.getNewBoQDetailsList().size(); i++) {
				boq = workOrderAgreement1.getNewBoQDetailsList().get(i);
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);

			}
			Map<String, List<BoQDetails>> groupByMilesToneMap = responseList.stream()
					.collect(Collectors.groupingBy(BoQDetails::getMilestone));
			model.addAttribute("milestoneList", groupByMilesToneMap);
		}
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Agreement",
				workOrderAgreement1.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkAgreementFile", workOrderAgreement.getId());
		workOrderAgreement1.setRoughCostdocumentDetail(roughCostEstmatedocuments);
		workOrderAgreement1.setDocUpload(docUpload);
		workOrderAgreement1.setDocumentDetail(documents);
		workOrderAgreement.setSectorlist(getsectorlist());
		workOrderAgreement.setWardnumber(getwardlist());
		workOrderAgreement1.setWorksWing(workOrderAgreement1.getWorksWing());
		workOrderAgreement1.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement1.setDepartment(workOrderAgreement1.getDepartment());
		workOrderAgreement1.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(workOrderAgreement1.getWorksWing())));
		workOrderAgreement1.setSubdivision(workOrderAgreement.getSubdivision());
		workOrderAgreement1.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(workOrderAgreement1.getDepartment())));

		workOrderAgreement1.setDepartments(getDepartmentsFromMs());
		workOrderAgreement1.setContractors(getAllActiveContractors());
		workOrderAgreement1.setBoQDetailsList(boQDetailsList);
		workOrderAgreement1.setWork_amount(String.valueOf(estAmt));
		model.addAttribute("fileuploadAllowed", "Y");
		model.addAttribute("workOrderAgreement", workOrderAgreement1);
		model.addAttribute(STATE_TYPE, workOrderAgreement1.getClass().getSimpleName());
		// prepareWorkflow(model, workOrderAgreement1, new WorkflowContainer());
		// model.addAttribute("milestoneList",groupByMilesToneMap);
		// prepareValidActionListByCutOffDate(model);
		/*
		 * if (workOrderAgreement1.getState() != null)
		 * model.addAttribute("currentState",
		 * workOrderAgreement1.getState().getValue());
		 * model.addAttribute("workflowHistory",
		 * getHistory(workOrderAgreement1.getState(),
		 * workOrderAgreement1.getStateHistory()));
		 */

		// System.out.println("::::::::state::::
		// "+workOrderAgreement1.getState().getValue());
		prepareValidActionListByCutOffDate(model);
		org.egov.infra.admin.master.entity.User user = null;
		if ((workOrderAgreement1.getProject_closure_comments() == null
				|| workOrderAgreement1.getProject_closure_comments().isEmpty())
				&& workOrderAgreement1.getStatus().getDescription().equals("Approved")) {
			user = securityUtils.getCurrentUser();
			String desig = getEmployeeDesig(user.getId());
			if (desig.equals("243")) {
				model.addAttribute("currentState", "Pending With SDO");
			}
			result = "modify-work-agreement";
		} else if ((workOrderAgreement1.getProject_closure_comments() == null
				|| workOrderAgreement1.getProject_closure_comments().isEmpty())
				&& !workOrderAgreement1.getStatus().getDescription().equals("Approved")) {
			result = "edit-work-agreement";
		} else if (workOrderAgreement1.getProject_closure_comments() != null
				&& !workOrderAgreement1.getProject_closure_comments().isEmpty()) {
			result = "view-work-agreement-closure";
		}
		return result;

	}

	@RequestMapping(value = "/edit/work1", method = RequestMethod.POST)
	public String saveEditData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request, @RequestParam("file1") MultipartFile[] files)
			throws Exception {
		String workFlowAction = workOrderAgreement.getWorkFlowAction();
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
				if (files[i] == null || files[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		if (workOrderAgreement.getDocUpload() != null) {

			Long ids = workOrderAgreement.getId();
			System.out.println("ids------" + ids);

			boQDetailsService.deleteBoqUploadData(ids);
		}
		workOrderAgreement.setDocumentDetail(list);
		// start of workflow
		Long approvalPosition = 0l;
		String approvalComment = "";
		String approvalDesignation = "";
		if (request.getParameter("approvalComent") != null)
			approvalComment = request.getParameter("approvalComent");
		if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty()) {
			approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
		}

		if (request.getParameter(APPROVAL_DESIGNATION) != null && !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
			approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));
		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,
				approvalPosition, approvalComment, approvalDesignation, workFlowAction);

		return "redirect:/boq/success?approverDetails=" + approvalPosition + "&estId=" + savedWorkOrderAgreement.getId()
				+ "&workflowaction=" + workFlowAction;

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

		// start of workflow

		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveProgress(request, workOrderAgreement);

		return "redirect:/boq/successProgress";

	}

	private void prepareValidActionListByCutOffDate(Model model) {
		model.addAttribute("validActionList", Arrays.asList("Forward/Reassign", "Save As Draft"));
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
				workflowHistory.put("updatedBy",
						stateHistory.getLastModifiedBy() + "::" + getEmployeeName(stateHistory.getLastModifiedBy()));
				workflowHistory.put("status", stateHistory.getValue());
				final Long owner = stateHistory.getOwnerPosition();
				final State _sowner = stateHistory.getState();
				ownerobj = this.microserviceUtils.getEmployee(owner, null, null, null).get(0);
				if (null != ownerobj) {
					workflowHistory.put("user", ownerobj.getUser().getUserName() + "::" + ownerobj.getUser().getName());
					if (ownerobj.getAssignments().get(0).getDepartment() != null) {
						Department department = this.microserviceUtils
								.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
						if (null != department)
							workflowHistory.put("department", department.getName());
					}
				} else if (null != _sowner && null != _sowner.getDeptName()) {
					user = microserviceUtils.getEmployee(owner, null, null, null).get(0).getUser();
					workflowHistory.put("user",
							null != user.getUserName() ? user.getUserName() + "::" + user.getName() : "");
					workflowHistory.put("department", null != _sowner.getDeptName() ? _sowner.getDeptName() : "");
				}
				historyTable.add(workflowHistory);
			}
			map.put("date", state.getDateInfo());
			map.put("comments", state.getComments() != null ? state.getComments() : "");
			map.put("updatedBy", state.getLastModifiedBy() + "::" + getEmployeeName(state.getLastModifiedBy()));
			map.put("status", state.getValue());
			final Long ownerPosition = state.getOwnerPosition();
			ownerobj = this.microserviceUtils.getEmployee(ownerPosition, null, null, null).get(0);
			if (null != ownerobj) {
				map.put("user", ownerobj.getUser().getUserName() + "::" + ownerobj.getUser().getName());
				if (ownerobj.getAssignments().get(0).getDepartment() != null) {
					Department department = this.microserviceUtils
							.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
					if (null != department)
						map.put("department", department.getName());
					// map.put("department", null !=
					// eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
//                        .getDepartmentForUser(user.getId()).getName() : "");
				}
			} else if (null != ownerPosition && null != state.getDeptName()) {
				user = microserviceUtils.getEmployee(ownerPosition, null, null, null).get(0).getUser();
				map.put("user", null != user.getUserName() ? user.getUserName() + "::" + user.getName() : "");
				map.put("department", null != state.getDeptName() ? state.getDeptName() : "");
			}
			historyTable.add(map);
			Collections.sort(historyTable, new Comparator<Map<String, Object>>() {

				public int compare(Map<String, Object> mapObject1, Map<String, Object> mapObject2) {

					return ((java.sql.Timestamp) mapObject1.get("date"))
							.compareTo((java.sql.Timestamp) mapObject2.get("date")); // ascending order
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
	public String searchclosure(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement = null;
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description from WorkOrderAgreement wo where wo.status.description ='Approved' and wo.executing_department = ? ")
				.append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
				.append(getMisQuery(workOrderAgreement));
		System.out.println("Query :: " + query.toString());
		list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getDepartment());

		if (list.size() != 0) {
			System.out.println(" data present");
			for (final Object[] object : list) {
				agreement = new WorkOrderAgreement();
				agreement.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					agreement.setName_work_order(object[1].toString());
				}
				if (object[2] != null) {
					agreement.setWork_number(object[2].toString());
				}
				if (object[3] != null) {
					agreement.setWork_agreement_number(object[3].toString());
				}
				if (object[4] != null) {
					agreement.setStartDate(object[4].toString());
				}
				if (object[5] != null) {
					agreement.setEndDate(object[5].toString());
				}
				if (object[6] != null) {
					agreement.setWork_amount(object[6].toString());
				}
				if (object[7] != null) {
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
	public String searchclosurePage(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement = null;
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description from WorkOrderAgreement wo where wo.status.description ='Approved' and wo.executing_department = ? ")
				.append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
				.append(getMisQuery(workOrderAgreement));
		list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getDepartment());

		if (list.size() != 0) {
			for (final Object[] object : list) {
				agreement = new WorkOrderAgreement();
				agreement.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					agreement.setName_work_order(object[1].toString());
				}
				if (object[2] != null) {
					agreement.setWork_number(object[2].toString());
				}
				if (object[3] != null) {
					agreement.setWork_agreement_number(object[3].toString());
				}
				if (object[4] != null) {
					agreement.setStartDate(object[4].toString());
				}
				if (object[5] != null) {
					agreement.setEndDate(object[5].toString());
				}
				if (object[6] != null) {
					agreement.setWork_amount(object[6].toString());
				}
				if (object[7] != null) {
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
	public String searchboqProgress(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		WorkOrderAgreement agreement = null;
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description,wo.milestonestatus from WorkOrderAgreement wo where wo.status.description ='Approved' and wo.executing_department = ? ")
				.append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()))
				.append(getMisQuery(workOrderAgreement));
		list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getDepartment());

		if (list.size() != 0) {
			for (final Object[] object : list) {
				agreement = new WorkOrderAgreement();
				agreement.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					agreement.setName_work_order(object[1].toString());
				}
				if (object[2] != null) {
					agreement.setWork_number(object[2].toString());
				}
				if (object[3] != null) {
					agreement.setWork_agreement_number(object[3].toString());
				}
				if (object[4] != null) {
					agreement.setStartDate(object[4].toString());
				}
				if (object[5] != null) {
					agreement.setEndDate(object[5].toString());
				}
				if (object[6] != null) {
					agreement.setWork_amount(object[6].toString());
				}
				if (object[7] != null) {
					agreement.setStatusDescp(map.get(object[7].toString()));
				}
				if (object[8] != null) {
					agreement.setMilestonestatus(object[8].toString());
					;
				}
				workList.add(agreement);

			}
		}
		// System.out.println("========++++++++++========");
		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-progress-work-agreement-page-form";

	}

	private WorkOrderAgreement getRoughWorkBillDocuments(final WorkOrderAgreement estDetails) {
		List<DocumentUpload> documentDetailsList = boQDetailsService.findByObjectIdAndObjectType(estDetails.getId(),
				"roughWorkAgreementFile");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}

	@RequestMapping(value = "/downloadRoughWorkBillDoc", method = RequestMethod.GET)
	public void getBillDocRoughWork(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "roughWorkAgreementFile");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		// EstimatePreparationApproval estDetails =
		// estimatePreparationApprovalRepository.findById(Long.parseLong(request.getParameter("estDetailsId")));
		WorkOrderAgreement estDetails = workOrderAgreementRepository
				.findById(Long.parseLong(request.getParameter("workDetailsId")));
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

	@RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "Works_Agreement");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		WorkOrderAgreement estDetails = workOrderAgreementRepository
				.findById(Long.parseLong(request.getParameter("workDetailsId")));
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

	@RequestMapping(value = "/checkref/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String searchRef(@PathVariable("id") final String ref) {

		System.out.println("+++++++++++++++" + ref + "++++++++++++++++++++");
		System.out.println("id :: " + ref);
		if (ref != null && ref != "") {
			List<BoqNewDetails> approvalList = new ArrayList<BoqNewDetails>();

			BoqNewDetails estimate = null;

			final StringBuffer query = new StringBuffer(500);
			List<Object[]> list = null;
			query.append("select bq.id,bq.item_description,bq.ref_dsr,bq.unit,bq.rate from BoqNewDetails bq ");

			query.append("where bq.ref_dsr = ? ");

			System.out.println("Query :: " + query.toString());
			list = persistenceService.findAllBy(query.toString(), ref);

			if (list.size() != 0) {

				for (final Object[] object : list) {
					estimate = new BoqNewDetails();
					estimate.setId(Long.parseLong(object[0].toString()));
					if (object[1] != null) {
						estimate.setItem_description(object[1].toString());
					}
					if (object[2] != null) {
						estimate.setRef_dsr(object[2].toString());
					}
					if (object[3] != null) {
						estimate.setUnit(object[3].toString());
					}
					if (object[4] != null) {
						estimate.setRate(Double.parseDouble(object[4].toString()));
					}

					approvalList.add(estimate);
				}

				return "success";
			}

		}
		return "fail";

	}

	@RequestMapping(value = "/deleteBoq", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String deleteBoq(@RequestParam("id") final String id,
			@RequestParam("slno") final String slno) {

		if (id != null && id != "" && slno != null && slno != "") {

			Long id1 = Long.parseLong(id);
			Long slno1 = Long.parseLong(slno);
			System.out.println("+++++ID+==++++++" + id + "+++++++slno==++++++" + slno + "+++++++++++++++++++");

			final StringBuffer query = new StringBuffer(500);
			query.append(
					"update BoQDetails bq set bq.workOrderAgreement.id=null where bq.slNo=? and bq.workOrderAgreement.id=? ");
			persistenceService.deleteAllBy(query.toString(), slno1, id1);
			return "success";
		}

		return "fail";

	}

	@RequestMapping(value = "/contractorid/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Contractor getById(@PathVariable("id") final Long id) {
		System.out.println("id :: " + id);
		Contractor contractor = null;
		try {
			contractor = contractorService.getById(id);
			System.out.println("con ::" + contractor.getCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return contractor;
	}

	public List<Contractor> getAllActiveContractors() {
		List<Contractor> contractors = contractorService.getAllActiveContractors();
		return contractors;
	}

	private String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and wo.work_start_date>='").append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and wo.work_start_date<='").append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");

			if (null != billDateFrom)
				numDateQuery.append(" and wo.work_intended_date>='").append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and wo.work_intended_date<='").append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}

	public String getMisQuery(WorkOrderAgreement agreement) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != agreement) {
			if (agreement.getName_work_order_search() != null && !agreement.getName_work_order_search().isEmpty()) {
				misQuery.append(" and wo.name_work_order  like '%").append(agreement.getName_work_order_search())
						.append("%'");
			}
			if (agreement.getWork_number_search() != null && !agreement.getWork_number_search().isEmpty()) {
				misQuery.append(" and wo.work_number='").append(agreement.getWork_number_search()).append("'");
			}
			if (agreement.getWork_agreement_number_search() != null
					&& !agreement.getWork_agreement_number_search().isEmpty()) {
				misQuery.append(" and wo.work_agreement_number='").append(agreement.getWork_agreement_number_search())
						.append("'");
			}
			if (agreement.getWorksWing() != null && !agreement.getWorksWing().isEmpty()) {
				misQuery.append(" and wo.worksWing='").append(agreement.getWorksWing()).append("'");
			}
			if (agreement.getSubdivision() != null) {
				misQuery.append(" and wo.subdivision='").append(agreement.getSubdivision()).append("'");
			}

		}
		return misQuery.toString();

	}

	private String populatePendingWith(Long id) {
		String pendingWith = "";
		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		if (workOrderAgreement != null && workOrderAgreement.getState() != null
				&& workOrderAgreement.getState().getOwnerName() != null
				&& !workOrderAgreement.getState().getOwnerName().isEmpty()) {
			try {
				pendingWith = workOrderAgreement.getState().getOwnerName();
			} catch (Exception e) {
				pendingWith = "";
			}

		}
		return pendingWith;
	}

	public List<String> getsectorlist() {
		List<AppConfigValues> sector = appConfigValuesService
				.getConfigValuesByModuleAndKey("EstimatePreparationApproval", "Sector/Locality");

		List<String> sector1 = new ArrayList<>();
		for (AppConfigValues as : sector) {
			// System.out.println("::::sector:: "+as.getValue());
			sector1.add(as.getValue());
		}
		return sector1;
	}

	public List<String> getwardlist() {
		List<AppConfigValues> wardNumber = appConfigValuesService
				.getConfigValuesByModuleAndKey("EstimatePreparationApproval", "WardNumber");
		List<String> wardnumber = new ArrayList<>();
		for (AppConfigValues wa : wardNumber) {
			// System.out.println("::::ward:: "+wa.getValue());
			wardnumber.add(wa.getValue());
		}
		return wardnumber;
	}

	private String populateShortCode(String deptCode, String worksWing, Long subdivision2) {

		String deptPart = appConfigValuesService.getConfigValuesByModuleAndKey("EGF", "works_div_" + deptCode).get(0)
				.getValue();
		String worksWingPart = "";
		if (worksWing.equalsIgnoreCase("1")) {
			worksWingPart = "BR";
		} else if (worksWing.equalsIgnoreCase("2")) {
			worksWingPart = "PH";
		} else {
			worksWingPart = "HE";
		}
		String subPart = "";
		if (subdivision2 == 1) {
			subPart = "S1";
		} else if (subdivision2 == 2) {
			subPart = "S2";
		} else if (subdivision2 == 3) {
			subPart = "S22";
		} else if (subdivision2 == 4) {
			subPart = "S3";
		} else if (subdivision2 == 5) {
			subPart = "S4";
		} else if (subdivision2 == 6) {
			subPart = "S7";
		} else if (subdivision2 == 7) {
			subPart = "S8";
		} else if (subdivision2 == 8) {
			subPart = "S9";
		} else if (subdivision2 == 9) {
			subPart = "S14";
		} else if (subdivision2 == 10) {
			subPart = "S15";
		} else if (subdivision2 == 11) {
			subPart = "S20";
		} else if (subdivision2 == 12) {
			subPart = "S1";
		} else if (subdivision2 == 13) {
			subPart = "S10";
		} else if (subdivision2 == 14) {
			subPart = "S11";
		} else if (subdivision2 == 15) {
			subPart = "S16";
		} else if (subdivision2 == 16) {
			subPart = "S17";
		} else if (subdivision2 == 17) {
			subPart = "S21";
		} else if (subdivision2 == 18) {
			subPart = "S12";
		} else if (subdivision2 == 19) {
			subPart = "S13";
		} else if (subdivision2 == 20) {
			subPart = "S18";
		} else if (subdivision2 == 21) {
			subPart = "S6";
		} else if (subdivision2 == 22) {
			subPart = "S1";
		} else if (subdivision2 == 23) {
			subPart = "S3";
		} else if (subdivision2 == 24) {
			subPart = "S6";
		} else if (subdivision2 == 25) {
			subPart = "S5";
		} else if (subdivision2 == 26) {
			subPart = "S14";
		} else if (subdivision2 == 27) {
			subPart = "S2";
		} else if (subdivision2 == 28) {
			subPart = "S4";
		} else if (subdivision2 == 29) {
			subPart = "S5";
		} else if (subdivision2 == 30) {
			subPart = "S19";
		} else if (subdivision2 == 31) {
			subPart = "S1";
		} else if (subdivision2 == 32) {
			subPart = "S2";
		} else if (subdivision2 == 33) {
			subPart = "S3";
		} else if (subdivision2 == 34) {
			subPart = "S9";
		} else if (subdivision2 == 35) {
			subPart = "S10";
		} else if (subdivision2 == 36) {
			subPart = "S12";
		} else if (subdivision2 == 37) {
			subPart = "S13";
		} else if (subdivision2 == 38) {
			subPart = "S5";
		} else if (subdivision2 == 39) {
			subPart = "S6";
		} else if (subdivision2 == 40) {
			subPart = "S7";
		} else if (subdivision2 == 41) {
			subPart = "S8";
		} else if (subdivision2 == 42) {
			subPart = "S15";
		} else if (subdivision2 == 43) {
			subPart = "S1";
		} else if (subdivision2 == 44) {
			subPart = "S3";
		} else if (subdivision2 == 45) {
			subPart = "S34";
		} else if (subdivision2 == 46) {
			subPart = "S4";
		}

		return worksWingPart + deptPart + subPart;
	}

}
