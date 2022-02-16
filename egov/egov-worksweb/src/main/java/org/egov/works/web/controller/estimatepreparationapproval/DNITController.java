package org.egov.works.web.controller.estimatepreparationapproval;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
import org.egov.works.estimatepreparationapproval.entity.WorksAbstractliabilities;
import org.egov.works.estimatepreparationapproval.entity.Workswing;
import org.egov.works.estimatepreparationapproval.repository.DNITCreationRepository;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.egov.works.estimatepreparationapproval.service.DNITCreationService;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.utils.ExcelGenerator;
import org.egov.works.workestimate.service.WorkDnitService;
import org.egov.works.workestimate.service.WorkEstimateService;
import org.python.icu.text.RuleBasedBreakIterator;
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
	private DNITCreationRepository dNITCreationRepository;
	private static final int BUFFER_SIZE = 4096;
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	public static final SimpleDateFormat DDMMYYYYFORMAT2 = new SimpleDateFormat("yyyy/MM/dd", LOCALE);

	@Autowired
	private FileStoreService fileStoreService;

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private DocumentUploadRepository documentUploadRepository;

	private static Map<String, String> map;

	// Instantiating the static map
	static {
		map = new HashMap<>();
		map.put("Created", "Under DNIT approval process");
		map.put("Pending for Approval", "Under DNIT approval process");
		map.put("Approved", "DNIT Approved");
	}

	@RequestMapping(value = "/createDnit", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("dnitCreation") final DNITCreation dnitCreation, final Model model,
			HttpServletRequest request) {
		dnitCreation.setCreatedbyuser(estimatePreparationApprovalService.getUserName());
		List<Workswing> worskwing = estimatePreparationApprovalService.getworskwing();
		dnitCreation.setDesignatationlist(getdesignationlist());
		dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
		prepareWorkflow(model, dnitCreation, new WorkflowContainer());
		prepareValidActionListByCutOffDate(model);

		return "dnitCreation-form";
	}

	private void prepareValidActionListByCutOffDate(Model model) {
		model.addAttribute("validActionList", Arrays.asList("Forward/Reassign", "Save As Draft"));
	}

	@RequestMapping(value = "/dnitCreation", params = "Forward/Reassign", method = RequestMethod.POST)
	public String saveBoQDetailsData(@ModelAttribute("dnitCreation") final DNITCreation dnitCreation, final Model model,
			@RequestParam("file1") MultipartFile[] files, @RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,
			final HttpServletRequest request) throws Exception {
		String workFlowAction = dnitCreation.getWorkFlowAction();

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
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}

		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}

		String deptCode = "";
		String estimateNumber = "";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = dnitCreation.getDepartment();
		// String
		// deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
		// "works_div_"+deptCode).get(0).getValue();
		if (dnitCreation.getEstimateNumber() == null
				|| (dnitCreation.getEstimateNumber() != null && dnitCreation.getEstimateNumber().isEmpty())) {
			String deptShortCode = populateShortCode(deptCode, dnitCreation.getWorksWing(),
					dnitCreation.getSubdivision());
			estimateNumber = v.getDNITNumber(deptShortCode);
			dnitCreation.setEstimateNumber(estimateNumber);
		}

		dnitCreation.setDepartment(dnitCreation.getDepartment());
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
			DNITCreation savedEstimatePreparationApproval = dnitCreationService.saveEstimatePreparationData(request,
					dnitCreation, approvalPosition, approvalComment, approvalDesignation, workFlowAction);

			Long id = savedEstimatePreparationApproval.getId();
			if (dnitCreation.getDocUpload() != null) {
				for (BoqUploadDocument boq : dnitCreation.getDocUpload()) {
					System.out.println(":::: " + boq.getId() + ":::::::" + boq.getComments() + ":::::::::"
							+ boq.getObjectId() + ":::::" + boq.getFilestoreid());
					if (boq.getObjectId() != null) {
						Long update = boq.getObjectId();
						dnitCreationService.updateDocuments(id, update);
					}
				}
			}
			return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
					+ savedEstimatePreparationApproval.getId() + "&workflowaction=" + workFlowAction;
		} catch (Exception e) {
			e.printStackTrace();

		}
		String msg = "Dnit Not Forwarded .";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		dnitCreation.setDesignatationlist(getdesignationlist());
		// dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartment(dnitCreation.getDepartment());
		dnitCreation.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		dnitCreation.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
		prepareWorkflow(model, dnitCreation, new WorkflowContainer());
		prepareValidActionListByCutOffDate(model);

		return "dnitCreation-form";
	}

	/* DNIT creation by EstimateApproval */
	@RequestMapping(value = "/dnitCreationEstApproval", params = "Forward/Reassign", method = RequestMethod.POST)
	public String dnitCreationEstApproval(@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {
		String workFlowAction = dnitCreation.getWorkFlowAction();
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
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}

		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}

		String deptCode = "";
		String estimateNumber = "";
		EstimateNoGenerator v = beanResolver.getAutoNumberServiceFor(EstimateNoGenerator.class);
		deptCode = dnitCreation.getDepartment();
		// String
		// deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
		// "works_div_"+deptCode).get(0).getValue();
		if (dnitCreation.getEstimateNumber() == null
				|| (dnitCreation.getEstimateNumber() != null && dnitCreation.getEstimateNumber().isEmpty())) {
			String deptShortCode = populateShortCode(deptCode, dnitCreation.getWorksWing(),
					dnitCreation.getSubdivision());
			estimateNumber = v.getDNITNumber(deptShortCode);
			dnitCreation.setEstimateNumber(estimateNumber);
		}
		dnitCreation.setDepartment(dnitCreation.getDepartment());
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

		DNITCreation savedEstimatePreparationApproval = dnitCreationService.saveDnitByEstimatePreparationData(request,
				dnitCreation, approvalPosition, approvalComment, approvalDesignation, workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
				+ savedEstimatePreparationApproval.getId() + "&workflowaction=" + workFlowAction;

	}

	// save as draft from estimate dnit by anshuman
	@RequestMapping(value = "/dnitCreationEstApproval", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsfromestimateDataDraft(@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {

		String workFlowAction = dnitCreation.getWorkFlowAction();
		System.out.println(":::::Works Wing:  " + dnitCreation.getWorksWing());
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
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
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
		// String
		// deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
		// "works_div_"+deptCode).get(0).getValue();
		String deptShortCode = populateShortCode(deptCode, dnitCreation.getWorksWing(), dnitCreation.getSubdivision());
		String estimateNumber = v.getDNITNumber(deptShortCode);
		dnitCreation.setEstimateNumber(estimateNumber);
		dnitCreation.setDepartment(dnitCreation.getDepartment());
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
			DNITCreation saveddnitCreation = dnitCreationService.saveDnitByEstimatePreparationData(request,
					dnitCreation, approvalPosition, approvalComment, approvalDesignation, workFlowAction);

			Long id = saveddnitCreation.getId();
			if (dnitCreation.getDocUpload() != null) {
				for (BoqUploadDocument boq : dnitCreation.getDocUpload()) {
					System.out.println(":::: " + boq.getId() + ":::::::" + boq.getComments() + ":::::::::"
							+ boq.getObjectId() + ":::::" + boq.getFilestoreid());
					if (boq.getObjectId() != null) {
						Long update = boq.getObjectId();
						dnitCreationService.updateDocuments(id, update);
					}
				}
			}

			return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId=" + saveddnitCreation.getId()
					+ "&workflowaction=" + workFlowAction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String msg = "Dnit Not Saved As Draft .";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		dnitCreation.setDesignatationlist(getdesignationlist());
		// dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartment(dnitCreation.getDepartment());
		dnitCreation.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		dnitCreation.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		dnitCreation.setDesignations(getDesignationsFromMs());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
		prepareWorkflow(model, dnitCreation, new WorkflowContainer());
		prepareValidActionListByCutOffDate(model);
		model.addAttribute("estimatePreparationApproval", dnitCreation);

		return "dnitboqDetails";
	}

	@RequestMapping(value = "/dnitCreationEstApproval", params = "Approve", method = RequestMethod.POST)
	public String dnitCreationEstApprovalAprover(@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {
		String workFlowAction = dnitCreation.getWorkFlowAction();
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
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}

		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
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

		DNITCreation savedEstimatePreparationApproval = dnitCreationService.saveDnitByEstimatePreparationData(request,
				dnitCreation, approvalPosition, approvalComment, approvalDesignation, workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId="
				+ savedEstimatePreparationApproval.getId() + "&workflowaction=" + workFlowAction;

	}

	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String showSuccessPage(@RequestParam("approverDetails") final String approverDetails,
			@RequestParam("workflowaction") final String workflowaction, final Model model,
			final HttpServletRequest request, @RequestParam("estId") final String estId) {

		DNITCreation dnitCreation = dNITCreationRepository.getOne(Long.parseLong(estId));
		final String message = getMessageByStatus(dnitCreation, approverDetails, workflowaction);

		model.addAttribute("message", message);

		return "works-success";
	}

	private String getMessageByStatus(DNITCreation dnitCreation, String approverDetails, String workflowaction) {
		String approverName = "";
		String msg = "";

		if (workflowaction.equalsIgnoreCase("Save As Draft")) {
			msg = "DNIT Number " + dnitCreation.getEstimateNumber() + " is Saved as draft";
		} else if (workflowaction.equalsIgnoreCase("Approve")
				&& dnitCreation.getStatus().getCode().equalsIgnoreCase("Approved")) {
			msg = "DNIT Number " + dnitCreation.getEstimateNumber() + "  is approved";
		} else {
			approverName = getEmployeeName(Long.parseLong(approverDetails));

			msg = "DNIT Number " + dnitCreation.getEstimateNumber() + " has been forwarded to " + approverName;

		}
		return msg;
	}

	@RequestMapping(value = "/dnitCreation", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsDataDraft(@ModelAttribute("dnitCreation") final DNITCreation dnitCreation,
			final Model model, @RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {

		String workFlowAction = dnitCreation.getWorkFlowAction();
		System.out.println(":::::Works Wing:  " + dnitCreation.getWorksWing());
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
		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
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
		// String
		// deptShortCode=appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
		// "works_div_"+deptCode).get(0).getValue();
		String deptShortCode = populateShortCode(deptCode, dnitCreation.getWorksWing(), dnitCreation.getSubdivision());
		String estimateNumber = v.getDNITNumber(deptShortCode);
		dnitCreation.setEstimateNumber(estimateNumber);
		dnitCreation.setDepartment(dnitCreation.getDepartment());
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
			DNITCreation saveddnitCreation = dnitCreationService.saveEstimatePreparationData(request, dnitCreation,
					approvalPosition, approvalComment, approvalDesignation, workFlowAction);

			Long id = saveddnitCreation.getId();
			if (dnitCreation.getDocUpload() != null) {
				for (BoqUploadDocument boq : dnitCreation.getDocUpload()) {
					System.out.println(":::: " + boq.getId() + ":::::::" + boq.getComments() + ":::::::::"
							+ boq.getObjectId() + ":::::" + boq.getFilestoreid());
					if (boq.getObjectId() != null) {
						Long update = boq.getObjectId();
						dnitCreationService.updateDocuments(id, update);
					}
				}
			}

			return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId=" + saveddnitCreation.getId()
					+ "&workflowaction=" + workFlowAction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String msg = "Dnit Not Saved As Draft .";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);
		dnitCreation.setDesignatationlist(getdesignationlist());
		// dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartment(dnitCreation.getDepartment());
		dnitCreation.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		dnitCreation.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		dnitCreation.setDesignations(getDesignationsFromMs());
		model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
		prepareWorkflow(model, dnitCreation, new WorkflowContainer());
		prepareValidActionListByCutOffDate(model);

		return "dnitCreation-form";
	}

	@RequestMapping(value = "/dnitCreation", params = "save", method = RequestMethod.POST)
	public String uploadBoqFileData(@ModelAttribute("dnitCreation") final DNITCreation dnitCreation, final Model model,
			@RequestParam("file") MultipartFile file, @RequestParam("file") MultipartFile[] files,
			final HttpServletRequest request) throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		List<BoQDetails> boQDetailsList2 = new ArrayList();
		List<DocumentUpload> docup = new ArrayList<>();
		String comments = request.getParameter("comments");
		HashSet<String> milesstoneList = new HashSet<>();
		String userName = estimatePreparationApprovalService.getUserName();
		Long userId = estimatePreparationApprovalService.getUserId();
		System.out.println(":::::User Name:::: " + userName + "::::UserID:::: " + userId);
		String refNo = null;
		int count = 0;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		Boolean error = true;
		String msg = "";
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		BigDecimal estAmt = new BigDecimal(0);

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
			 * Path doc = Paths.get(documentPath); if (!Files.exists(doc)) {
			 * Files.createDirectories(doc); }
			 * 
			 * Files.write(Path, bytes);
			 */
		}

		// File xlsFile = new File(fileToUpload.toString());
		// if (xlsFile.exists()) {

		// FileInputStream inputStream = new FileInputStream(new File(filePath));
		Workbook workbook = WorkbookFactory.create(file.getInputStream());
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
						+ row.getCell(3).getStringCellValue() + " 44: " + row.getCell(4).getStringCellValue() + " dd "
						+ row.getCell(5).getStringCellValue());
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
						upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
						upload.setFileName(files[j].getOriginalFilename());
						System.out.println("files[i].getOriginalFilename():;;;;;;;;" + files[j].getOriginalFilename());
						upload.setContentType(files[j].getContentType());
						upload.setObjectType("roughWorkFileDnit");
						upload.setComments(comments);
						upload.setUsername(userName);
						// System.out.println("comments--------"+comments);
						docup.add(upload);

					}

				Iterator<Row> iterator = firstSheet.iterator();

				first: while (iterator.hasNext()) {
					Row nextRow = iterator.next();
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					BoQDetails aBoQDetails = new BoQDetails();
					// BoqDetailsPop boqDetailsPop =new BoqDetailsPop();
					while (cellIterator.hasNext()) {
						int erow = nextRow.getRowNum() + 1;
						if (erow > 250) {
							error = true;
							msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 250 items are allowed. ";
							break first;
						}
						Cell cell = (Cell) cellIterator.next();
						if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
							System.out.println(
									"::Cell Type::: " + cell.getCellType() + "::::::Blank:: " + cell.CELL_TYPE_BLANK);
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
								// boqDetailsPop.setRef_dsr(cell.getStringCellValue());
								refNo = cell.getStringCellValue();
							} else if (cell.getColumnIndex() == 3) {

								aBoQDetails.setUnit(cell.getStringCellValue());
								// boqDetailsPop.setUnit(cell.getStringCellValue());

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
								// boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
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
									aBoQDetails.setAmount(aBoQDetails.getRate().multiply(aBoQDetails.getQuantity()));
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
							boQDetailsList.add(aBoQDetails);
							// arrayboqDetailsPop.add(boqDetailsPop);

						}
					}

				}

				// workbook.close();
				// inputStream.close();
			} else {
				// msg="Uploaded document must contain Sheet with name Abst. with AOR";
				// error=true;
			}
		}

		// } else {
		// response = "Please choose a file.";
		// }
		int nextcount = 1;
		List<BoqUploadDocument> docUpload = new ArrayList<>();

		if (dnitCreation.getDocUpload() != null) {
			for (BoqUploadDocument boq : dnitCreation.getDocUpload()) {
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
		dnitCreation.setDocumentDetail(docup);
		dnitCreation.setRoughCostdocumentDetail(docup);
		if (!error) {
			DocumentUpload savedocebefore = dnitCreationService.savedocebefore(dnitCreation);

			System.out.println("::OBJECT ID:: " + savedocebefore.getId() + " ::ObjectType:::: "
					+ savedocebefore.getUsername() + " :::::FileStore():::: " + savedocebefore.getFileStore().getId());

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
					.collect(Collectors.groupingBy(BoQDetails::getMilestone, LinkedHashMap::new, Collectors.toList()));

			List<DocumentUpload> uploadDoc = new ArrayList<DocumentUpload>();

			DocumentUpload doc1 = new DocumentUpload();
			Long fileStoreId = savedocebefore.getFileStore().getId();
			doc1.setId(savedocebefore.getId());
			doc1.setFileStore(savedocebefore.getFileStore());
			uploadDoc.add(doc1);
			model.addAttribute("milestoneList", groupByMilesToneMap);
			model.addAttribute("uploadDocID", uploadDoc);
			model.addAttribute("fileuploadAllowed", "Y");

		}
		dnitCreation.setDesignatationlist(getdesignationlist());
		// dnitCreation.setDepartments(getDepartmentsFromMs());
		dnitCreation.setBoQDetailsList(boQDetailsList);
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setEstimateAmount(estAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
		dnitCreation.setDocUpload(docUpload);
		Map<String, List<BoqUploadDocument>> uploadDocument = docUpload.stream()
				.collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));
		model.addAttribute("uploadDocument", uploadDocument);
		dnitCreation.setBoQDetailsList(boQDetailsList);
		dnitCreation.setDesignations(getDesignationsFromMs());
		dnitCreation.setWorksWing(dnitCreation.getWorksWing());
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartment(dnitCreation.getDepartment());
		dnitCreation.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(dnitCreation.getWorksWing())));
		dnitCreation.setSubdivision(dnitCreation.getSubdivision());
		dnitCreation.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(dnitCreation.getDepartment())));
		if (!error) {
			BigDecimal bgestAmt = estAmt;

			dnitCreation.setEstimateAmount(bgestAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
		} else {

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

	public BigDecimal percentage(BigDecimal base, BigDecimal pct) {
		BigDecimal bg100 = new BigDecimal(100);
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
	public String showEstimateNewFormGet(@ModelAttribute("workdnitDetails") final DNITCreation dnitCreation,
			final Model model, HttpServletRequest request) {
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workdnitDetails", dnitCreation);

		return "search-dnit-form";
	}

	// edited
	@RequestMapping(value = "/workEditDnit", method = RequestMethod.POST)
	public String showEditEstimateNewFormGet(@ModelAttribute("workdnitDetails") final DNITCreation dnitCreation,
			final Model model, HttpServletRequest request) {

		dnitCreation.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workdnitDetails", dnitCreation);

		return "search-edit-dnit-form";
	}

	@RequestMapping(value = "/searchEditDnit", params = "searchEditDnit", method = RequestMethod.POST)
	public String searchWorkEditEstimateData(
			@ModelAttribute("workdnitDetails") final DNITCreation estimatePreparationApproval, final Model model,
			final HttpServletRequest request) throws Exception {
		List<DNITCreation> approvalList = new ArrayList<DNITCreation>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			long department = Long.parseLong(estimatePreparationApproval.getDepartment());
			estimatePreparationApproval.setExecutingDivision(department);
		}
		DNITCreation estimate = null;

		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from DNITCreation es where es.executingDivision = ? and es.status='686' or es.status='625'")
				.append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
				.append(getMisQuery(estimatePreparationApproval));
		System.out.println("Query :: " + query.toString());
		list = persistenceService.findAllBy(query.toString(), estimatePreparationApproval.getExecutingDivision());

		if (list.size() != 0) {

			for (final Object[] object : list) {
				estimate = new DNITCreation();
				estimate.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					estimate.setWorkName(object[1].toString());
				}
				if (object[2] != null) {
					estimate.setWorkCategry(object[2].toString());
				}
				if (object[3] != null) {
					estimate.setEstimateNumber(object[3].toString());
				}
				if (object[4] != null) {
					estimate.setEstimateDt(object[4].toString());
				}
				if (object[5] != null) {
					estimate.setEstimateAmount(BigDecimal.valueOf(Double.parseDouble(object[5].toString())).setScale(2,
							BigDecimal.ROUND_HALF_UP));
				}
				if (object[6] != null) {
					estimate.setStatusDescription(object[6].toString());
				}
				if (estimate.getStatusDescription() != null
						&& !estimate.getStatusDescription().equalsIgnoreCase("Approved")) {
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
			@ModelAttribute("workdnitDetails") final DNITCreation estimatePreparationApproval, final Model model,
			final HttpServletRequest request) throws Exception {
		List<DNITCreation> approvalList = new ArrayList<DNITCreation>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			long department = Long.parseLong(estimatePreparationApproval.getDepartment());
			estimatePreparationApproval.setExecutingDivision(department);
		}
		DNITCreation estimate = null;

		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from DNITCreation es where 1=1");

		query.append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
				.append(getMisQuery(estimatePreparationApproval));
		if (estimatePreparationApproval.getExecutingDivision() != null) {
			query.append(" and es.executingDivision = ? ");
		}
		System.out.println("Query :: " + query.toString());
		if (estimatePreparationApproval.getExecutingDivision() == null) {
			list = persistenceService.findAllBy(query.toString());
		} else {
		list = persistenceService.findAllBy(query.toString(), estimatePreparationApproval.getExecutingDivision());
		}
		if (list.size() != 0) {

			for (final Object[] object : list) {
				estimate = new DNITCreation();
				estimate.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					estimate.setWorkName(object[1].toString());
				}
				if (object[2] != null) {
					estimate.setWorkCategry(object[2].toString());
				}
				if (object[3] != null) {
					estimate.setEstimateNumber(object[3].toString());
				}
				if (object[4] != null) {
					estimate.setEstimateDt(object[4].toString());
				}
				if (object[5] != null) {
					estimate.setEstimateAmount(BigDecimal.valueOf(Double.parseDouble(object[5].toString())).setScale(2,
							BigDecimal.ROUND_HALF_UP));
				}
				String status = null;
				if (object[6] != null) {
					status = object[6].toString();
					estimate.setStatusDescription(map.get(status));
				}
				if (status != null && !status.equalsIgnoreCase("Approved")) {
					estimate.setPendingWith(populatePendingWith(estimate.getId()));
				}
				approvalList.add(estimate);
			}

		}
		estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-dnit-form";

	}

	/*
	 * @RequestMapping(value = "/workDnitSearch", params = "exportToExcel")
	 * public @ResponseBody ResponseEntity<InputStreamResource> auditExportToExcel(
	 * 
	 * @ModelAttribute("workdnitDetails") final DNITCreation estimatePreparationApproval, final Model model,
	 *  final HttpServletRequest
	 * request) throws IOException
	 */
	@RequestMapping(value = "/workDnitSearchExcel")
	public @ResponseBody ResponseEntity<InputStreamResource> dnitExportToExcel(
			@ModelAttribute("workdnitDetails") final DNITCreation estimatePreparationApproval, final Model model,
			final HttpServletRequest request) throws IOException {

//		LOGGER.info("ExportToExcel");
		List<DNITCreation> approvalList = new ArrayList<DNITCreation>();

		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			long department = Long.parseLong(estimatePreparationApproval.getDepartment());
			estimatePreparationApproval.setExecutingDivision(department);
		}
		DNITCreation estimate = null;

		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select es.id,es.workName,es.workCategory,es.estimateNumber,es.estimateDate,es.estimateAmount,es.status.description from DNITCreation es where 1=1");

		query.append(getDateQuery(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()))
				.append(getMisQuery(estimatePreparationApproval));
		if (estimatePreparationApproval.getExecutingDivision() != null) {
			query.append(" and es.executingDivision = ? ");
		}
		System.out.println("Query :: " + query.toString());
		if (estimatePreparationApproval.getExecutingDivision() == null) {
			list = persistenceService.findAllBy(query.toString());
		} else {
			list = persistenceService.findAllBy(query.toString(), estimatePreparationApproval.getExecutingDivision());
		}
		if (list.size() != 0) {

			for (final Object[] object : list) {
				estimate = new DNITCreation();
				estimate.setId(Long.parseLong(object[0].toString()));
				if (object[1] != null) {
					estimate.setWorkName(object[1].toString());
				}
				if (object[2] != null) {
					estimate.setWorkCategry(object[2].toString());
				}
				if (object[3] != null) {
					estimate.setEstimateNumber(object[3].toString());
				}
				if (object[4] != null) {
					estimate.setEstimateDt(object[4].toString());
				}
				if (object[5] != null) {
					estimate.setEstimateAmount(BigDecimal.valueOf(Double.parseDouble(object[5].toString())).setScale(2,
							BigDecimal.ROUND_HALF_UP));
				}
				String status = null;
				if (object[6] != null) {
					status = object[6].toString();
					estimate.setStatusDescription(map.get(status));
				}
				if (status != null && !status.equalsIgnoreCase("Approved")) {
					estimate.setPendingWith(populatePendingWith(estimate.getId()));
				}
				approvalList.add(estimate);
			}
		}
		String[] COLUMNS = { "Name of Work", "DNIT Number", "DNIT Date", "DNIT Amount", "Work Status",
				"Pending with", };

		ByteArrayInputStream in = resultToExcel(approvalList, COLUMNS);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=DNITDetailReport.xls");
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));

	}

	public static ByteArrayInputStream resultToExcel(List<DNITCreation> approvalList, String[] COLUMNS)
			throws IOException {

		HSSFWorkbook workbook = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		CreationHelper createHelper = workbook.getCreationHelper();

		Sheet sheet = workbook.createSheet("DNIT Detail Report");

		// Row for Header
		Row headerRow = sheet.createRow(0);
		int sl = 1;
		// Header
		for (int col = 0; col < COLUMNS.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(COLUMNS[col]);
		}

		int rowIdx = 1;
		for (DNITCreation detail : approvalList) {
			Row row = sheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(detail.getWorkName());
			row.createCell(1).setCellValue(detail.getEstimateNumber());
			row.createCell(2).setCellValue(detail.getEstimateDt());
			String ss = detail.getEstimateAmount().toString();
			row.createCell(3).setCellValue(ss);
			row.createCell(4).setCellValue(detail.getStatusDescription());
			row.createCell(5).setCellValue(detail.getPendingWith());

		}

		workbook.write(out);
		return new ByteArrayInputStream(out.toByteArray());

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",
				estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkFileDnit", estimateDetails.getId());

		estimateDetails.setDesignatationlist(getdesignationlist());
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		// estimateDetails.setDepartment(dept);
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setDepartment(dept);
		estimateDetails.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
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

		Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone, LinkedHashMap::new, Collectors.toList()));

		model.addAttribute("milestoneList", groupByMilesToneMap);

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
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",
				estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkFileDnit", estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment() + "++++++" + estimateDetails.getSectorNumber() + "++++++++");

		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);
		estimateDetails.setDesignatationlist(getdesignationlist());
		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		// estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		estimateDetails.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		// System.out.println(estimateDetails.getStatus().getCode()+"+++++++++++++++++++++++++++++++++++++++++++");

		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {

			boq = estimateDetails.getBoQDetailsList().get(j);
			boq.setSizeIndex(boQDetailsList.size());
			boQDetailsList.add(boq);
		}
		Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone, LinkedHashMap::new, Collectors.toList()));

		// System.out.println(groupByMilesToneMap+"+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList", groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
			model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "edit-dnit-form";
	}

	@RequestMapping(value = "/deleteBoqdata", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String deleteBoqdata(@RequestParam("id") final String id,
			@RequestParam("slno") final String slno) {

		if (id != null && id != "" && slno != null && slno != "") {

			Long id1 = Long.parseLong(id);
			Long slno1 = Long.parseLong(slno);
			System.out.println("+++++ID+==++++++" + id + "+++++++slno==++++++" + slno + "+++++++++++++++++++");

			final StringBuffer query = new StringBuffer(500);
			query.append("update BoQDetails bq set bq.dnitCreation.id=null where bq.slNo=? and bq.dnitCreation.id=? ");
			persistenceService.deleteAllBy(query.toString(), slno1, id1);
			return "success";
		}

		return "fail";

	}

	@RequestMapping(value = "/deletednit/{id}/{slno}", method = RequestMethod.GET)
	public String deleteDnit(@PathVariable("id") final Long id, @PathVariable("slno") final Long slno, Model model) {

		if (id != null && id != 0 && slno != null && slno != 0) {

			final StringBuffer query = new StringBuffer(500);
			query.append("update BoQDetails bq set bq.dnitCreation.id=null where bq.slNo=? and bq.dnitCreation.id=? ");
			persistenceService.deleteAllBy(query.toString(), slno, id);
		}

		List<BoQDetails> boQDetailsList = new ArrayList();

		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",
				estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkFileDnit", estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment() + "++++++" + estimateDetails.getSectorNumber() + "++++++++");

		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());

		System.out.println(estimateDetails.getStatus().getCode() + "+++++++++++++++++++++++++++++++++++++++++++");

		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {

			boq = estimateDetails.getBoQDetailsList().get(j);
			boq.setSizeIndex(boQDetailsList.size());
			boQDetailsList.add(boq);
		}

		Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone, LinkedHashMap::new, Collectors.toList()));

		System.out.println(groupByMilesToneMap + "+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList", groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
			model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "edit-dnit-form";
	}

	@RequestMapping(value = "/editdnit/updateDnit", method = RequestMethod.POST)
	public String editDnitEstimateData(@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,
			@RequestParam("file1") MultipartFile[] files, @RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,
			final HttpServletRequest request) throws Exception {

		String workFlowAction = "Forward/Reassign";
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

		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}
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

		DNITCreation savedDNITCreation = dnitCreationService.saveEstimatePreparationData(request, dnitCreation,
				approvalPosition, approvalComment, approvalDesignation, workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId=" + savedDNITCreation.getId()
				+ "&workflowaction=" + workFlowAction;

	}

	@RequestMapping(value = "/deletednit/{id}/updateDnit", method = RequestMethod.POST)
	public String updateDnitEstimateData(@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,
			@PathVariable("id") final Long id, @RequestParam("file1") MultipartFile[] files,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, final HttpServletRequest request)
			throws Exception {

		String workFlowAction = "Forward/Reassign";
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

		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}
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

		DNITCreation savedDNITCreation = dnitCreationService.saveEstimatePreparationData(request, dnitCreation,
				approvalPosition, approvalComment, approvalDesignation, workFlowAction);

		return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId=" + savedDNITCreation.getId()
				+ "&workflowaction=" + workFlowAction;

	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();

		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",
				estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkFileDnit", estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment() + "++++++" + estimateDetails.getSectorNumber() + "++++++++");
		String checkdnit = estimateDetails.getDnitfromestimate();
		// System.out.println(":::::::::::::::::::DNIT ORIGIN::::: "+checkdnit);
		if (checkdnit != null && checkdnit.equalsIgnoreCase("fromEstimate")) {
			model.addAttribute("editable", "N");
		}
		estimateDetails.setDesignatationlist(getdesignationlist());
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);
		estimateDetails.setEstimateAmount(estimateDetails.getEstimateAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		// estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		System.out.println("::::::::asdads  " + estimateDetails.getWorksWing());
		estimateDetails.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));

		System.out.println(estimateDetails.getStatus().getCode() + "+++++++++++++++++++++++++++++++++++++++++++");

		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {

			boq = estimateDetails.getBoQDetailsList().get(j);
			boq.setSizeIndex(boQDetailsList.size());
			boQDetailsList.add(boq);
		}

		Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone, LinkedHashMap::new, Collectors.toList()));

		System.out.println(groupByMilesToneMap + "+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList", groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
			model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		model.addAttribute("prevState", populate(estimateDetails.getStateHistory()));
		return "create-dnit-form";
	}

	private String populate(List<StateHistory> stateHistory) {
		String result = null;
		int l = 0;
		if (stateHistory != null && !stateHistory.isEmpty()) {
			l = stateHistory.size();
			result = stateHistory.get(l - 1).getValue();
		}
		return result;
	}

	@RequestMapping(value = "/edits/{id}", method = RequestMethod.GET)
	public String editerr(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> boQDetailsList = new ArrayList();

		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",
				estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkFileDnit", estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment() + "++++++" + estimateDetails.getSectorNumber() + "++++++++");
		String checkdnit = estimateDetails.getDnitfromestimate();
		// System.out.println(":::::::::::::::::::DNIT ORIGIN::::: "+checkdnit);
		if (checkdnit != null && checkdnit.equalsIgnoreCase("fromEstimate")) {
			model.addAttribute("editable", "N");
		}
		estimateDetails.setDesignatationlist(getdesignationlist());
		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);
		estimateDetails.setEstimateAmount(estimateDetails.getEstimateAmount());
		// estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
		estimateDetails.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));

		System.out.println(estimateDetails.getStatus().getCode() + "+++++++++++++++++++++++++++++++++++++++++++");

		BoQDetails boq = new BoQDetails();
		for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {

			boq = estimateDetails.getBoQDetailsList().get(j);
			boq.setSizeIndex(boQDetailsList.size());
			boQDetailsList.add(boq);
		}
		String msg = "Unable to Update Something Went Wrong !! please try Again.";
		model.addAttribute("error", "Y");
		model.addAttribute("message", msg);

		Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
				.collect(Collectors.groupingBy(BoQDetails::getMilestone));
		System.out.println(groupByMilesToneMap + "+++++++++++++++++++++++++++++++++++++++++");
		model.addAttribute("milestoneList", groupByMilesToneMap);
		model.addAttribute(STATE_TYPE, estimateDetails.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, estimateDetails, new WorkflowContainer());
		if (estimateDetails.getState() != null)
			model.addAttribute("currentState", estimateDetails.getState().getValue());
		model.addAttribute("workflowHistory",
				getHistory(estimateDetails.getState(), estimateDetails.getStateHistory()));
		return "create-dnit-form";
	}

	@RequestMapping(value = "/edit/updateDnit", params = "save", method = RequestMethod.POST)
	public String editEstimateData1(@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,
			@RequestParam("file1") MultipartFile[] file1, Model model,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, @RequestParam("file") MultipartFile file,
			@RequestParam("file") MultipartFile[] files, final HttpServletRequest request) throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		Long id = dnitCreation.getId();
		System.out.println(":::::ID:::::: " + id);
		String userName = estimatePreparationApprovalService.getUserName();
		Long userId = estimatePreparationApprovalService.getUserId();
//System.out.println(":::::User Name:::: "+userName+"::::UserID:::: "+userId);
		// New File UPload....
		List<DocumentUpload> docup = new ArrayList<>();
		String refNo = null;
		int count = 0;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		Boolean error = true;
		String msg = "";
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		BigDecimal estAmt = new BigDecimal(0);
		String comments = request.getParameter("comments");
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
			 * Path doc = Paths.get(documentPath); if (!Files.exists(doc)) {
			 * Files.createDirectories(doc); }
			 * 
			 * Files.write(Path, bytes);
			 */
		}

		// File xlsFile = new File(fileToUpload.toString());
		// if (xlsFile.exists()) {

		// FileInputStream inputStream = new FileInputStream(new File(filePath));
		Workbook workbook = WorkbookFactory.create(file.getInputStream());
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
						+ row.getCell(3).getStringCellValue() + " 44: " + row.getCell(4).getStringCellValue() + " dd "
						+ row.getCell(5).getStringCellValue());
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
						upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
						upload.setFileName(files[j].getOriginalFilename());
						System.out.println("files[i].getOriginalFilename():;;;;;;;;" + files[j].getOriginalFilename());
						upload.setContentType(files[j].getContentType());
						upload.setObjectType("roughWorkFileDnit");
						upload.setComments(comments);
						upload.setUsername(userName);
						// System.out.println("comments--------"+comments);
						docup.add(upload);

					}

				Iterator<Row> iterator = firstSheet.iterator();

				first: while (iterator.hasNext()) {
					Row nextRow = iterator.next();
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					BoQDetails aBoQDetails = new BoQDetails();
					// BoqDetailsPop boqDetailsPop =new BoqDetailsPop();
					while (cellIterator.hasNext()) {
						int erow = nextRow.getRowNum() + 1;
						if (erow > 250) {
							error = true;
							msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 250 items are allowed. ";
							break first;
						}
						Cell cell = (Cell) cellIterator.next();
						if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
							System.out.println(
									"::Cell Type::: " + cell.getCellType() + "::::::Blank:: " + cell.CELL_TYPE_BLANK);
							error = true;
							msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Blank Column in row number "
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
								// boqDetailsPop.setRef_dsr(cell.getStringCellValue());
								refNo = cell.getStringCellValue();
							} else if (cell.getColumnIndex() == 3) {

								aBoQDetails.setUnit(cell.getStringCellValue());
								// boqDetailsPop.setUnit(cell.getStringCellValue());

							} else if (cell.getColumnIndex() == 5) {
								if (aBoQDetails.getRate() != null) {

									if (aBoQDetails.getQuantity() == null) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.Check row number "
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
								// boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
								Double d = cell.getNumericCellValue();

								String[] splitter = d.toString().split("\\.");

								if (splitter[1].length() > 4) {
									error = true;
									msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 4 decimal places are allowed in Rate and Quantity check row number "
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
									msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 4 decimal places are allowed in Rate and Quantity check row number "
											+ erow;
									break first;
								}
								if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
									aBoQDetails.setAmount(aBoQDetails.getRate().multiply(aBoQDetails.getQuantity()));
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
							aBoQDetails.setSizeIndex(count);
							boQDetailsList.add(aBoQDetails);
							// arrayboqDetailsPop.add(boqDetailsPop);

						}
					}

				}

				// workbook.close();
				// inputStream.close();
			} else {
				// error=true;
				// msg="Uploaded document must contain Sheet with name Abst. with AOR";
			}
		}

		// } else {
		// response = "Please choose a file.";
		// }
		int nextcount = 1;

		List<BoqUploadDocument> docUpload = new ArrayList<>();
		if (dnitCreation.getDocUpload() != null) {
			for (BoqUploadDocument boq : dnitCreation.getDocUpload()) {
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
		dnitCreation.setDocumentDetail(docup);
		dnitCreation.setRoughCostdocumentDetail(docup);
		if (!error) {
			DocumentUpload savedocebefore = dnitCreationService.savedocebefore(dnitCreation);
			dnitCreationService.updateDocuments(id, savedocebefore.getId());

			System.out.println("::OBJECT ID:: " + savedocebefore.getId() + " ::ObjectType:::: "
					+ savedocebefore.getUsername() + " :::::FileStore():::: " + savedocebefore.getFileStore().getId());

			BoqUploadDocument boqUploadDocument2 = new BoqUploadDocument();
			// adding
			boqUploadDocument2.setId(Long.valueOf(nextcount));
			boqUploadDocument2.setObjectId(savedocebefore.getId());
			boqUploadDocument2.setObjectType(savedocebefore.getFileStore().getFileName());
			boqUploadDocument2.setFilestoreid(savedocebefore.getFileStore().getId());
			boqUploadDocument2.setComments(comments);
			boqUploadDocument2.setUsername(savedocebefore.getUsername());
			docUpload.add(boqUploadDocument2);
			// End of document Upload
			List<DocumentUpload> uploadDoc = new ArrayList<DocumentUpload>();

			DocumentUpload doc1 = new DocumentUpload();
			// Long fileStoreId = savedocebefore.getFileStore().getId();
			doc1.setId(savedocebefore.getId());
			doc1.setFileStore(savedocebefore.getFileStore());
			uploadDoc.add(doc1);
			Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
					.collect(Collectors.groupingBy(BoQDetails::getMilestone, LinkedHashMap::new, Collectors.toList()));
			model.addAttribute("milestoneList", groupByMilesToneMap);
			model.addAttribute("uploadDocID", uploadDoc);
		}
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		Map<String, List<BoqUploadDocument>> uploadDocument = docUpload.stream()
				.collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));

		estimateDetails.setDocUpload(docUpload);
		if (!error) {
			estimateDetails.setBoQDetailsList(boQDetailsList);
			BigDecimal bgestAmt = estAmt;
			/*
			 * if(estAmt >= 10000000){ BigDecimal pct = new BigDecimal(3); BigDecimal
			 * ContingentAmt=percentage(bgestAmt,pct);
			 * estimateDetails.setContingentPercentage(3.0);
			 * estimateDetails.setContingentAmount(ContingentAmt.setScale(2,
			 * BigDecimal.ROUND_HALF_UP));
			 * 
			 * BigDecimal estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt); Double
			 * dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			 * estimateDetails.setEstimateAmount(BigDecimal.valueOf(
			 * dobestAmtPlusContingentAmt).setScale(2, BigDecimal.ROUND_HALF_UP)); } else {
			 * BigDecimal pct = new BigDecimal(5); BigDecimal
			 * ContingentAmt=percentage(bgestAmt,pct);
			 * estimateDetails.setContingentPercentage(5.0);
			 * estimateDetails.setContingentAmount(ContingentAmt.setScale(2,
			 * BigDecimal.ROUND_HALF_UP));
			 * 
			 * BigDecimal estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt); Double
			 * dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			 * estimateDetails.setEstimateAmount(BigDecimal.valueOf(
			 * dobestAmtPlusContingentAmt).setScale(2, BigDecimal.ROUND_HALF_UP));
			 * 
			 * 
			 * }
			 */
			estimateDetails.setEstimateAmount(bgestAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
		} else {
			estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}

		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",
				estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkFileDnit", estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment() + "++++++" + estimateDetails.getSectorNumber() + "++++++++");

		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);

		estimateDetails.setDesignatationlist(getdesignationlist());
		estimateDetails.setPreparationDesignationNew(estimateDetails.getPreparationDesignationNew());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());

		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		// estimateDetails.setDepartment(estimateDetails.getDepartment());
		estimateDetails.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(estimateDetails.getDepartment())));
		List<BoQDetails> boQDetailsList1 = new ArrayList();
		BoQDetails boq = new BoQDetails();
		if (error) {

			for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {

				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList1.size());
				System.out.println("::: " + boq.getSlNo() + "::: " + boq.getRef_dsr());
				boQDetailsList1.add(boq);
			}
			Map<String, List<BoQDetails>> groupByMilesToneMap1 = boQDetailsList1.stream()
					.collect(Collectors.groupingBy(BoQDetails::getMilestone));
			model.addAttribute("milestoneList", groupByMilesToneMap1);
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

	@RequestMapping(value = "/edits/updateDnit", params = "save", method = RequestMethod.POST)
	public String editEstimateData1err(@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,
			@RequestParam("file1") MultipartFile[] file1, Model model,
			@RequestParam("fileRoughCost") MultipartFile[] fileRoughCost, @RequestParam("file") MultipartFile file,
			@RequestParam("file") MultipartFile[] files, final HttpServletRequest request) throws Exception {

		List<BoQDetails> boQDetailsList = new ArrayList();
		Long id = dnitCreation.getId();
		System.out.println(":::::ID:::::: " + id);
		String userName = estimatePreparationApprovalService.getUserName();
		Long userId = estimatePreparationApprovalService.getUserId();
		System.out.println(":::::User Name:::: " + userName + "::::UserID:::: " + userId);
		// New File UPload....
		List<DocumentUpload> docup = new ArrayList<>();
		String refNo = null;
		int count = 0;
		String fileName = null;
		String extension = null;
		String filePath = null;
		File fileToUpload = null;
		Boolean error = true;
		String msg = "";
		String FILE_PATH_PROPERTIES = "F:\\Upload\\";
		String FILE_PATH_SEPERATOR = "\\";
		file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
		BigDecimal estAmt = new BigDecimal(0);
		String comments = request.getParameter("comments");
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
			 * Path doc = Paths.get(documentPath); if (!Files.exists(doc)) {
			 * Files.createDirectories(doc); }
			 * 
			 * Files.write(Path, bytes);
			 */
		}

		// File xlsFile = new File(fileToUpload.toString());
		// if (xlsFile.exists()) {

		// FileInputStream inputStream = new FileInputStream(new File(filePath));
		Workbook workbook = WorkbookFactory.create(file.getInputStream());
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
						+ row.getCell(3).getStringCellValue() + " 44: " + row.getCell(4).getStringCellValue() + " dd "
						+ row.getCell(5).getStringCellValue());
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
						upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[j].getInputStream())));
						upload.setFileName(files[j].getOriginalFilename());
						System.out.println("files[i].getOriginalFilename():;;;;;;;;" + files[j].getOriginalFilename());
						upload.setContentType(files[j].getContentType());
						upload.setObjectType("roughWorkFileDnit");
						upload.setComments(comments);
						upload.setUsername(userName);
						// System.out.println("comments--------"+comments);
						docup.add(upload);

					}

				Iterator<Row> iterator = firstSheet.iterator();

				first: while (iterator.hasNext()) {
					Row nextRow = iterator.next();
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					BoQDetails aBoQDetails = new BoQDetails();
					// BoqDetailsPop boqDetailsPop =new BoqDetailsPop();
					while (cellIterator.hasNext()) {
						int erow = nextRow.getRowNum() + 1;
						if (erow > 250) {
							error = true;
							msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 250 items are allowed. ";
							break first;
						}
						Cell cell = (Cell) cellIterator.next();
						if (cell.getCellType() == cell.CELL_TYPE_BLANK) {
							System.out.println(
									"::Cell Type::: " + cell.getCellType() + "::::::Blank:: " + cell.CELL_TYPE_BLANK);
							error = true;
							msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Blank Column in row number "
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
								// boqDetailsPop.setRef_dsr(cell.getStringCellValue());
								refNo = cell.getStringCellValue();
							} else if (cell.getColumnIndex() == 3) {

								aBoQDetails.setUnit(cell.getStringCellValue());
								// boqDetailsPop.setUnit(cell.getStringCellValue());

							} else if (cell.getColumnIndex() == 5) {
								if (aBoQDetails.getRate() != null) {

									if (aBoQDetails.getQuantity() == null) {
										error = true;
										msg = "Please check the uploaded document as there is an issue in AOR detail sheet. Rate and Quantity should be numeric.Check row number "
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
								// boqDetailsPop.setRate((int) nextRow.getCell(4).getNumericCellValue());
								Double d = cell.getNumericCellValue();

								String[] splitter = d.toString().split("\\.");

								if (splitter[1].length() > 4) {
									error = true;
									msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 4 decimal places are allowed in Rate and Quantity check row number "
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
									msg = "Please check the uploaded document as there is an issue in AOR detail sheet.Only 4 decimal places are allowed in Rate and Quantity check row number "
											+ erow;
									break first;
								}
								if (aBoQDetails.getRate() != null && aBoQDetails.getQuantity() != null) {
									aBoQDetails.setAmount(aBoQDetails.getRate().multiply(aBoQDetails.getQuantity()));
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
							aBoQDetails.setSizeIndex(count);
							boQDetailsList.add(aBoQDetails);
							// arrayboqDetailsPop.add(boqDetailsPop);

						}
					}

				}

				// workbook.close();
				// inputStream.close();
			} else {
				// error=true;
				// msg="Uploaded document must contain Sheet with name Abst. with AOR";
			}
		}

		// } else {
		// response = "Please choose a file.";
		// }
		int nextcount = 1;

		List<BoqUploadDocument> docUpload = new ArrayList<>();
		if (dnitCreation.getDocUpload() != null) {
			for (BoqUploadDocument boq : dnitCreation.getDocUpload()) {
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
		dnitCreation.setDocumentDetail(docup);
		dnitCreation.setRoughCostdocumentDetail(docup);
		if (!error) {
			DocumentUpload savedocebefore = dnitCreationService.savedocebefore(dnitCreation);
			dnitCreationService.updateDocuments(id, savedocebefore.getId());

			System.out.println("::OBJECT ID:: " + savedocebefore.getId() + " ::ObjectType:::: "
					+ savedocebefore.getUsername() + " :::::FileStore():::: " + savedocebefore.getFileStore().getId());

			BoqUploadDocument boqUploadDocument2 = new BoqUploadDocument();
			// adding
			boqUploadDocument2.setId(Long.valueOf(nextcount));
			boqUploadDocument2.setObjectId(savedocebefore.getId());
			boqUploadDocument2.setObjectType(savedocebefore.getFileStore().getFileName());
			boqUploadDocument2.setFilestoreid(savedocebefore.getFileStore().getId());
			boqUploadDocument2.setComments(comments);
			boqUploadDocument2.setUsername(savedocebefore.getUsername());
			docUpload.add(boqUploadDocument2);
			// End of document Upload
			List<DocumentUpload> uploadDoc = new ArrayList<DocumentUpload>();

			DocumentUpload doc1 = new DocumentUpload();
			// Long fileStoreId = savedocebefore.getFileStore().getId();
			doc1.setId(savedocebefore.getId());
			doc1.setFileStore(savedocebefore.getFileStore());
			uploadDoc.add(doc1);
			Map<String, List<BoQDetails>> groupByMilesToneMap = boQDetailsList.stream()
					.collect(Collectors.groupingBy(BoQDetails::getMilestone));
			model.addAttribute("milestoneList", groupByMilesToneMap);
			model.addAttribute("uploadDocID", uploadDoc);
		}
		DNITCreation estimateDetails = workDnitService.searchEstimateData(id);
		Map<String, List<BoqUploadDocument>> uploadDocument = docUpload.stream()
				.collect(Collectors.groupingBy(BoqUploadDocument::getObjectType));

		estimateDetails.setDocUpload(docUpload);
		if (!error) {
			BigDecimal bgestAmt = estAmt;
			/*
			 * if(estAmt >= 10000000){ BigDecimal pct = new BigDecimal(3); BigDecimal
			 * ContingentAmt=percentage(bgestAmt,pct);
			 * estimateDetails.setContingentPercentage(3.0);
			 * estimateDetails.setContingentAmount(ContingentAmt.setScale(2,
			 * BigDecimal.ROUND_HALF_UP));
			 * 
			 * BigDecimal estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt); Double
			 * dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			 * estimateDetails.setEstimateAmount(BigDecimal.valueOf(
			 * dobestAmtPlusContingentAmt).setScale(2, BigDecimal.ROUND_HALF_UP)); } else {
			 * BigDecimal pct = new BigDecimal(5); BigDecimal
			 * ContingentAmt=percentage(bgestAmt,pct);
			 * estimateDetails.setContingentPercentage(5.0);
			 * estimateDetails.setContingentAmount(ContingentAmt.setScale(2,
			 * BigDecimal.ROUND_HALF_UP));
			 * 
			 * BigDecimal estAmtPlusContingentAmt=ContingentAmt.add(bgestAmt); Double
			 * dobestAmtPlusContingentAmt=estAmtPlusContingentAmt.doubleValue();
			 * estimateDetails.setEstimateAmount(BigDecimal.valueOf(
			 * dobestAmtPlusContingentAmt).setScale(2, BigDecimal.ROUND_HALF_UP));
			 * 
			 * 
			 * }
			 */
			estimateDetails.setEstimateAmount(bgestAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
			estimateDetails.setBoQDetailsList(boQDetailsList);
		} else {

			model.addAttribute("error", "Y");
			model.addAttribute("message", msg);
		}

		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Dnit",
				estimateDetails.getId());
		final List<DocumentUpload> roughCostEstmatedocuments = documentUploadRepository
				.findByobjectTypeAndObjectId("roughWorkFileDnit", estimateDetails.getId());
		System.out.println(estimateDetails.getDepartment() + "++++++" + estimateDetails.getSectorNumber() + "++++++++");

		estimateDetails.setDocumentDetail(documents);
		estimateDetails.setRoughCostdocumentDetail(roughCostEstmatedocuments);
		estimateDetails.setDesignatationlist(getdesignationlist());
		estimateDetails.setPreparationDesignationNew(estimateDetails.getPreparationDesignationNew());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);
		estimateDetails.setBoQDetailsList(estimateDetails.getNewBoQDetailsList());
		estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setDesignations(getDesignationsFromMs());
		estimateDetails.setTenderCost(String.valueOf(estimateDetails.getEstimateAmount()));
		estimateDetails.setEstimateNumber(estimateDetails.getEstimateNumber());
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
		// estimateDetails.setDepartment(estimateDetails.getDepartment());
		estimateDetails.setNewdepartments(
				estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setSubdivisions(
				estimatePreparationApprovalService.getsubdivision(Long.valueOf(estimateDetails.getDepartment())));
		List<BoQDetails> boQDetailsList1 = new ArrayList();
		BoQDetails boq = new BoQDetails();
		if (error) {

			for (int j = 0; j < estimateDetails.getBoQDetailsList().size(); j++) {

				boq = estimateDetails.getBoQDetailsList().get(j);
				boq.setSizeIndex(boQDetailsList1.size());
				System.out.println("::: " + boq.getSlNo() + "::: " + boq.getRef_dsr());
				boQDetailsList1.add(boq);
			}
			Map<String, List<BoQDetails>> groupByMilesToneMap1 = boQDetailsList1.stream()
					.collect(Collectors.groupingBy(BoQDetails::getMilestone));
			model.addAttribute("milestoneList", groupByMilesToneMap1);
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

	@RequestMapping(value = "/edits/updateDnit", method = RequestMethod.POST)
	public String editEstimateDataerr(@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,
			@RequestParam("file1") MultipartFile[] files, @RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,
			final HttpServletRequest request) throws Exception {

		String workFlowAction = dnitCreation.getWorkFlowAction();
		List<DocumentUpload> list = new ArrayList<>();

		Long id = dnitCreation.getId();
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		BoQDetails boq = new BoQDetails();

		if (dnitCreation.getDocUpload() != null) {

			Long ids = dnitCreation.getId();
			System.out.println("ids------" + ids);

			dnitCreationService.deleteBoqUploadData(ids);
		}
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

		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}
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
			DNITCreation savedDNITCreation = dnitCreationService.saveEstimatePreparationData(request, dnitCreation,
					approvalPosition, approvalComment, approvalDesignation, workFlowAction);

			return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId=" + savedDNITCreation.getId()
					+ "&workflowaction=" + workFlowAction;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/dnit/edits/" + id;
	}

	@RequestMapping(value = "/edit/updateDnit", method = RequestMethod.POST)
	public String editEstimateData(@ModelAttribute("estimatePreparationApproval") final DNITCreation dnitCreation,
			@RequestParam("file1") MultipartFile[] files, @RequestParam("fileRoughCost") MultipartFile[] fileRoughCost,
			final HttpServletRequest request) throws Exception {

		String workFlowAction = dnitCreation.getWorkFlowAction();
		List<DocumentUpload> list = new ArrayList<>();

		Long id = dnitCreation.getId();
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		BoQDetails boq = new BoQDetails();

		if (dnitCreation.getDocUpload() != null) {

			Long ids = dnitCreation.getId();
			System.out.println("ids------" + ids);

			dnitCreationService.deleteBoqUploadData(ids);
		}
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

		if (fileRoughCost != null)
			for (int i = 0; i < fileRoughCost.length; i++) {
				DocumentUpload upload2 = new DocumentUpload();
				if (fileRoughCost[i] == null || fileRoughCost[i].getOriginalFilename().isEmpty()) {
					continue;
				}
				upload2.setInputStream(
						new ByteArrayInputStream(IOUtils.toByteArray(fileRoughCost[i].getInputStream())));
				upload2.setFileName(fileRoughCost[i].getOriginalFilename());
				upload2.setContentType(fileRoughCost[i].getContentType());
				upload2.setObjectType(dnitCreation.getObjectType());
				list.add(upload2);
			}
		dnitCreation.setDocumentDetail(list);
		if (dnitCreation.getDepartment() != null && dnitCreation.getDepartment() != ""
				&& !dnitCreation.getDepartment().isEmpty()) {
			dnitCreation.setExecutingDivision(Long.parseLong(dnitCreation.getDepartment()));
		}
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
			DNITCreation savedDNITCreation = dnitCreationService.saveEstimatePreparationData(request, dnitCreation,
					approvalPosition, approvalComment, approvalDesignation, workFlowAction);

			return "redirect:/dnit/success?approverDetails=" + approvalPosition + "&estId=" + savedDNITCreation.getId()
					+ "&workflowaction=" + workFlowAction;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/dnit/edits/" + id;
	}

	public String getEmployeeName(Long empId) {

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
				workflowHistory.put("updatedBy",
						stateHistory.getLastModifiedBy() + "::" + getEmployeeName(stateHistory.getLastModifiedBy()));
				workflowHistory.put("status", stateHistory.getValue());
				final Long owner = stateHistory.getOwnerPosition();
				final State _sowner = stateHistory.getState();
				ownerobj = this.microserviceUtils.getEmployee(owner, null, null, null).get(0);
				if (null != ownerobj) {
					workflowHistory.put("user", ownerobj.getUser().getUserName() + "::" + ownerobj.getUser().getName());

					// edited....
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
				// edited...
				if (ownerobj.getAssignments().get(0).getDepartment() != null) {
					Department department = this.microserviceUtils
							.getDepartmentByCode(ownerobj.getAssignments().get(0).getDepartment());
					// Department department=
					// this.microserviceUtils.getDepartmentByCode(state.getDeptCode());
					if (null != department)
						map.put("department", department.getName());
					//
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
	public void getBillDocRoughWork(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "roughWorkFileDnit");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		// EstimatePreparationApproval estDetails =
		// estimatePreparationApprovalRepository.findById(Long.parseLong(request.getParameter("estDetailsId")));
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

	private String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and es.estimateDate>='").append(DDMMYYYYFORMAT1.format(billDateFrom)).append("'");
			if (null != billDateTo)
				numDateQuery.append(" and es.estimateDate<='").append(DDMMYYYYFORMAT1.format(billDateTo)).append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}

	public String getMisQuery(DNITCreation estimate) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != estimate) {

			if (estimate.getWorkStatusSearch() != null && !estimate.getWorkStatusSearch().isEmpty()) {
				misQuery.append(" and es.status.description='").append(estimate.getWorkStatusSearch()).append("'");
			}
			if (estimate.getWorkName() != null && !estimate.getWorkName().isEmpty()) {
				misQuery.append(" and es.workName like '%").append(estimate.getWorkName()).append("%'");
			}
			if (estimate.getFundSource() != null && !estimate.getFundSource().isEmpty()) {
				misQuery.append(" and es.fundSource='").append(estimate.getFundSource()).append("'");
			}

			if (estimate.getEstimateNumber() != null && !estimate.getEstimateNumber().isEmpty()) {
				misQuery.append(" and es.estimateNumber='").append(estimate.getEstimateNumber()).append("'");
			}
			if (estimate.getWorksWing() != null) {
				misQuery.append(" and es.worksWing='").append(estimate.getWorksWing()).append("'");
			}
			if (estimate.getWorkLocation() != null && !estimate.getWorkLocation().isEmpty()) {
				misQuery.append(" and es.workLocation='").append(estimate.getWorkLocation()).append("'");
			}
			if (estimate.getSectorNumber() != null) {
				misQuery.append(" and es.sectorNumber='").append(estimate.getSectorNumber()).append("'");
			}
			if (estimate.getWardNumber() != null) {
				misQuery.append(" and es.wardNumber='").append(estimate.getWardNumber()).append("'");
			}
			if (estimate.getWorkCategory() != null) {
				misQuery.append(" and es.workCategory=").append(estimate.getWorkCategory());
			}
			if (estimate.getEstimateAmount() != null) {
				misQuery.append(" and es.subdivision=").append(estimate.getEstimateAmount());
			}
			if (estimate.getSubdivision() != null) {
				misQuery.append(" and es.subdivision=").append(estimate.getSubdivision());
			}
			if (estimate.getCreatedbyuser() != null && !estimate.getCreatedbyuser().isEmpty()) {
				misQuery.append(" and lower(es.createdbyuser) like lower('%").append(estimate.getCreatedbyuser())
						.append("%')");
			}
		}
		return misQuery.toString();

	}

	private String populatePendingWith(Long id) {
		String pendingWith = "";
		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);
		if (estimateDetails != null && estimateDetails.getState() != null
				&& estimateDetails.getState().getOwnerPosition() != null
				&& estimateDetails.getState().getOwnerPosition() != 0L) {
			try {
				pendingWith = getEmployeeName(estimateDetails.getState().getOwnerPosition());
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

	public List<String> getdesignationlist() {
		List<AppConfigValues> sector = appConfigValuesService
				.getConfigValuesByModuleAndKey("EstimatePreparationApproval", "Designation");

		List<String> designation = new ArrayList<>();
		for (AppConfigValues as : sector) {
			System.out.println("::::designation:: " + as.getValue());
			designation.add(as.getValue());
		}
		return designation;
	}

	@RequestMapping(value = "/abstractliabilities", method = RequestMethod.POST)
	public String excelreport(
			@ModelAttribute("worksabstractliabilities") final WorksAbstractliabilities worksabstractliabilities,
			final Model model, HttpServletRequest request) {

		return "worksabstractliabilities-excel";
	}

	@RequestMapping(value = "/abstractliabilitiesexcel", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ResponseEntity<InputStreamResource> excelreportdownload(
			@RequestParam("fromdate") Date fromdate, @RequestParam("todate") Date todate, final Model model,
			HttpServletRequest request) throws Exception {
		Map<String, WorksAbstractliabilities> worksdata = new LinkedHashMap<>();

		final StringBuffer query = new StringBuffer(500);
		final StringBuffer querydnit = new StringBuffer(500);
		final StringBuffer queryworks = new StringBuffer(500);
		List<Object[]> listest = null;
		List<Object[]> listdnit = null;
		List<Object[]> listworks = null;
		System.out.println("::from :: " + fromdate + " ::: todate:: " + todate);
		query.append(
				"select tdc.estimate_number ,tdc.expenditure_head_est ,tdc.works_wing ,(select es.code from egw_status es where es.id =tdc.statusid ),tdc.estimate_amount from txn_estimate_preparation tdc ");
		query.append(
				"where tdc.statusid in (select es.id from egw_status es where es.moduletype='EstimatePreparationApproval' and (es.code='Approved' or es.code='AA Initiated' or es.code='TS Initiated')) and tdc.works_wing notnull");
		query.append(getDateQuerynew(fromdate, todate));

		querydnit.append(
				"select tdc.estimate_amount ,tdc.expenditure_head_est ,tdc.works_wing,(select es.code from egw_status es where es.id = tdc.statusid ) from txn_dnit_creation tdc where tdc.statusid in (select es.id from egw_status es where es.moduletype = 'DNITCreation'and (es.code = 'Approved')) and tdc.works_wing notnull ");
		querydnit.append(getDateQuerynew(fromdate, todate));

		queryworks.append(
				"select tdc.work_amount ,tdc.expenditure_head_est ,tdc.works_wing,(select es.code from egw_status es where es.id = tdc.statusid ) from txn_work_agreement tdc where tdc.works_wing notnull");
		queryworks.append(getDateQuerynew(fromdate, todate));

		System.out.println("Query est:: " + query);
		System.out.println("Query Dnit:: " + querydnit);
		System.out.println("Query works:: " + queryworks);
		try {
			Query q = entityManager.createNativeQuery(query.toString());
			listest = q.getResultList();
			Query qdnit = entityManager.createNativeQuery(querydnit.toString());
			listdnit = qdnit.getResultList();
			Query qworks = entityManager.createNativeQuery(queryworks.toString());
			listworks = qworks.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("List of est " + listest.size() + "list of dnit " + listdnit.size() + " list of works "
				+ listworks.size());

		if (listest.size() != 0) {

			for (final Object[] ob : listest) {
				WorksAbstractliabilities filterdata = new WorksAbstractliabilities();

				if (ob[1] != null) {// Matching expenditure head and if already exists
					System.out.println("::expenditure::: " + ob[1].toString());
					String[] split = ob[1].toString().split(",");
					String expen = split[0];
					if (worksdata.containsKey(expen)) {
						if (ob[3] != null) { // Matching status and checking if already not exist in map
							if (ob[3].toString().equalsIgnoreCase("AA Initiated")) {
								if (ob[2] != null) {
									if (ob[2].toString().equalsIgnoreCase("Public Health")
											|| ob[2].toString().equalsIgnoreCase("1")) {
										filterdata.setRoughPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setRoughHE(new BigDecimal(0));
										filterdata.setRoughBR(new BigDecimal(0));
										filterdata.setGtotalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
											|| ob[2].toString().equalsIgnoreCase("2")) {
										filterdata.setRoughHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setRoughPH(new BigDecimal(0));
										filterdata.setRoughBR(new BigDecimal(0));
										filterdata.setGtotalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));
									} else if (ob[2].toString().equalsIgnoreCase("B&R")
											|| ob[2].toString().equalsIgnoreCase("3")
											|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
										filterdata.setRoughBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setRoughHE(new BigDecimal(0));
										filterdata.setRoughPH(new BigDecimal(0));
										filterdata.setGtotalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
									}
									filterdata.setRoughtotal(filterdata.getRoughPH().add(filterdata.getRoughHE())
											.add(filterdata.getRoughBR()));
									filterdata.setGtotaltotal(filterdata.getRoughtotal());
									filterdata.setGrandtotalrough(filterdata.getRoughtotal());

								}

							} else if (ob[3].toString().equalsIgnoreCase("TS Initiated")) {
								if (ob[2] != null) {
									if (ob[2].toString().equalsIgnoreCase("Public Health")
											|| ob[2].toString().equalsIgnoreCase("1")) {
										filterdata.setEstimatePH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setEstimateHE(new BigDecimal(0));
										filterdata.setEstimateBR(new BigDecimal(0));
										filterdata.setGtotalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
											|| ob[2].toString().equalsIgnoreCase("2")) {
										filterdata.setEstimateHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setEstimatePH(new BigDecimal(0));
										filterdata.setEstimateBR(new BigDecimal(0));
										filterdata.setGtotalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("B&R")
											|| ob[2].toString().equalsIgnoreCase("3")
											|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
										filterdata.setEstimateBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setEstimateHE(new BigDecimal(0));
										filterdata.setEstimatePH(new BigDecimal(0));
										filterdata.setGtotalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
									}
									filterdata.setEstimatehtotal(filterdata.getEstimatePH()
											.add(filterdata.getEstimateHE()).add(filterdata.getEstimateBR()));
									filterdata.setGtotaltotal(filterdata.getEstimatehtotal());
									filterdata.setGrandtotalestimate(filterdata.getEstimatehtotal());
								}

							} else if (ob[3].toString().equalsIgnoreCase("Approved")) {
								if (ob[2] != null) {
									if (ob[2].toString().equalsIgnoreCase("Public Health")
											|| ob[2].toString().equalsIgnoreCase("1")) {
										filterdata.setTechnicalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setTechnicalHE(new BigDecimal(0));
										filterdata.setTechnicalBR(new BigDecimal(0));
										filterdata.setGtotalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
											|| ob[2].toString().equalsIgnoreCase("2")) {
										filterdata.setTechnicalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setTechnicalPH(new BigDecimal(0));
										filterdata.setTechnicalBR(new BigDecimal(0));
										filterdata.setGtotalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("B&R")
											|| ob[2].toString().equalsIgnoreCase("3")
											|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
										filterdata.setTechnicalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setTechnicalHE(new BigDecimal(0));
										filterdata.setTechnicalPH(new BigDecimal(0));
										filterdata.setGtotalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
									}
									filterdata.setTechnicaltotal(filterdata.getTechnicalPH()
											.add(filterdata.getTechnicalHE()).add(filterdata.getTechnicalBR()));
									filterdata.setGtotaltotal(filterdata.getTechnicaltotal());
									filterdata.setGrandtotaltechnical(filterdata.getTechnicaltotal());
								}
							}
						}
						WorksAbstractliabilities valuedata = worksdata.get(ob[1].toString());
						if (filterdata.getRoughPH() != null)
							valuedata.setRoughPH(valuedata.getRoughPH().add(filterdata.getRoughPH()));
						if (filterdata.getRoughBR() != null)
							valuedata.setRoughBR(valuedata.getRoughBR().add(filterdata.getRoughBR()));
						if (filterdata.getRoughHE() != null)
							valuedata.setRoughHE(valuedata.getRoughHE().add(filterdata.getRoughHE()));
						if (filterdata.getRoughtotal() != null)
							valuedata.setRoughtotal(valuedata.getRoughtotal().add(filterdata.getRoughtotal()));
						if (filterdata.getEstimatePH() != null)
							valuedata.setEstimatePH(valuedata.getEstimatePH().add(filterdata.getEstimatePH()));
						if (filterdata.getEstimateBR() != null)
							valuedata.setEstimateBR(valuedata.getEstimateBR().add(filterdata.getEstimateBR()));
						if (filterdata.getEstimateHE() != null)
							valuedata.setEstimateHE(valuedata.getEstimateHE().add(filterdata.getEstimateHE()));
						if (filterdata.getEstimatehtotal() != null)
							valuedata.setEstimatehtotal(
									valuedata.getEstimatehtotal().add(filterdata.getEstimatehtotal()));
						if (filterdata.getTechnicalPH() != null)
							valuedata.setTechnicalPH(valuedata.getTechnicalPH().add(filterdata.getTechnicalPH()));
						if (filterdata.getTechnicalBR() != null)
							valuedata.setTechnicalBR(valuedata.getTechnicalBR().add(filterdata.getTechnicalBR()));
						if (filterdata.getTechnicalHE() != null)
							valuedata.setTechnicalHE(valuedata.getTechnicalHE().add(filterdata.getTechnicalHE()));
						if (filterdata.getTechnicaltotal() != null)
							valuedata.setTechnicaltotal(
									valuedata.getTechnicaltotal().add(filterdata.getTechnicaltotal()));
						if (filterdata.getGtotalPH() != null)
							valuedata.setGtotalPH(valuedata.getGtotalPH().add(filterdata.getGtotalPH()));
						if (filterdata.getGtotalBR() != null)
							valuedata.setGtotalBR(valuedata.getGtotalBR().add(filterdata.getGtotalBR()));
						if (filterdata.getGtotalHE() != null)
							valuedata.setGtotalHE(valuedata.getGtotalHE().add(filterdata.getGtotalHE()));
						if (filterdata.getGtotaltotal() != null)
							valuedata.setGtotaltotal(valuedata.getGtotaltotal().add(filterdata.getGtotaltotal()));
						if (filterdata.getGrandtotalrough() != null)
							valuedata.setGrandtotalrough(
									valuedata.getGrandtotalrough().add(filterdata.getGrandtotalrough()));
						if (filterdata.getGrandtotalestimate() != null)
							valuedata.setGrandtotalestimate(
									valuedata.getGrandtotalestimate().add(filterdata.getGrandtotalestimate()));
						if (filterdata.getGrandtotaltechnical() != null)
							valuedata.setGrandtotaltechnical(
									valuedata.getGrandtotaltechnical().add(filterdata.getGrandtotaltechnical()));

						worksdata.put(expen, valuedata);

					} else {
						if (ob[3] != null) { // Matching status and checking if already not exist in map
							if (ob[3].toString().equalsIgnoreCase("AA Initiated")) {
								if (ob[2] != null) {
									if (ob[2].toString().equalsIgnoreCase("Public Health")
											|| ob[2].toString().equalsIgnoreCase("1")) {
										filterdata.setRoughPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setRoughHE(new BigDecimal(0));
										filterdata.setRoughBR(new BigDecimal(0));
										filterdata.setGtotalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
											|| ob[2].toString().equalsIgnoreCase("2")) {
										filterdata.setRoughHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setRoughPH(new BigDecimal(0));
										filterdata.setRoughBR(new BigDecimal(0));
										filterdata.setTechnicalBR(new BigDecimal(0));
										filterdata.setGtotalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("B&R")
											|| ob[2].toString().equalsIgnoreCase("3")
											|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
										filterdata.setRoughBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setRoughHE(new BigDecimal(0));
										filterdata.setRoughPH(new BigDecimal(0));
										filterdata.setGtotalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
									}
									filterdata.setRoughtotal(filterdata.getRoughPH().add(filterdata.getRoughHE())
											.add(filterdata.getRoughBR()));
									filterdata.setGtotaltotal(filterdata.getRoughtotal());
									filterdata.setGrandtotalrough(filterdata.getRoughtotal());
									filterdata.setGrandtotalestimate(new BigDecimal(0));
									filterdata.setGrandtotaltechnical(new BigDecimal(0));
									filterdata.setEstimatePH(new BigDecimal(0));
									filterdata.setEstimateBR(new BigDecimal(0));
									filterdata.setEstimateHE(new BigDecimal(0));
									filterdata.setEstimatehtotal(new BigDecimal(0));
									filterdata.setTechnicalPH(new BigDecimal(0));
									filterdata.setTechnicalBR(new BigDecimal(0));
									filterdata.setTechnicalHE(new BigDecimal(0));
									filterdata.setTechnicaltotal(new BigDecimal(0));
									filterdata.setDnitPH(new BigDecimal(0));
									filterdata.setDnitHE(new BigDecimal(0));
									filterdata.setDnitBR(new BigDecimal(0));
									filterdata.setDnittotal(new BigDecimal(0));
									filterdata.setWorksPH(new BigDecimal(0));
									filterdata.setWorksHE(new BigDecimal(0));
									filterdata.setWorksBR(new BigDecimal(0));
									filterdata.setWorkstotal(new BigDecimal(0));
									filterdata.setOngoingworksPH(new BigDecimal(0));
									filterdata.setOngoingworksHE(new BigDecimal(0));
									filterdata.setOngoingworksBR(new BigDecimal(0));
									filterdata.setOngoingworkstotal(new BigDecimal(0));
									filterdata.setGrandtotaldnit(new BigDecimal(0));
									filterdata.setGrandtotalworks(new BigDecimal(0));
									filterdata.setGrandtotalongoing(new BigDecimal(0));
								}

							} else if (ob[3].toString().equalsIgnoreCase("TS Initiated")) {
								if (ob[2] != null) {
									if (ob[2].toString().equalsIgnoreCase("Public Health")
											|| ob[2].toString().equalsIgnoreCase("1")) {
										filterdata.setEstimatePH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setEstimateHE(new BigDecimal(0));
										filterdata.setEstimateBR(new BigDecimal(0));
										filterdata.setGtotalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
											|| ob[2].toString().equalsIgnoreCase("2")) {
										filterdata.setEstimateHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setEstimatePH(new BigDecimal(0));
										filterdata.setEstimateBR(new BigDecimal(0));
										filterdata.setGtotalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("B&R")
											|| ob[2].toString().equalsIgnoreCase("3")
											|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
										filterdata.setEstimateBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setEstimateHE(new BigDecimal(0));
										filterdata.setEstimatePH(new BigDecimal(0));
										filterdata.setGtotalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
									}
									System.out.println("::" + filterdata.getEstimatePH() + ":::"
											+ filterdata.getEstimateHE() + "::" + filterdata.getEstimateBR());
									filterdata.setEstimatehtotal(filterdata.getEstimatePH()
											.add(filterdata.getEstimateHE()).add(filterdata.getEstimateBR()));
									filterdata.setGtotaltotal(filterdata.getEstimatehtotal());
									filterdata.setGrandtotalestimate(filterdata.getEstimatehtotal());
									filterdata.setGrandtotalrough(new BigDecimal(0));
									filterdata.setGrandtotaltechnical(new BigDecimal(0));
									filterdata.setRoughPH(new BigDecimal(0));
									filterdata.setRoughBR(new BigDecimal(0));
									filterdata.setRoughHE(new BigDecimal(0));
									filterdata.setRoughtotal(new BigDecimal(0));
									filterdata.setTechnicalPH(new BigDecimal(0));
									filterdata.setTechnicalBR(new BigDecimal(0));
									filterdata.setTechnicalHE(new BigDecimal(0));
									filterdata.setTechnicaltotal(new BigDecimal(0));
									filterdata.setDnitPH(new BigDecimal(0));
									filterdata.setDnitHE(new BigDecimal(0));
									filterdata.setDnitBR(new BigDecimal(0));
									filterdata.setDnittotal(new BigDecimal(0));
									filterdata.setWorksPH(new BigDecimal(0));
									filterdata.setWorksHE(new BigDecimal(0));
									filterdata.setWorksBR(new BigDecimal(0));
									filterdata.setWorkstotal(new BigDecimal(0));
									filterdata.setOngoingworksPH(new BigDecimal(0));
									filterdata.setOngoingworksHE(new BigDecimal(0));
									filterdata.setOngoingworksBR(new BigDecimal(0));
									filterdata.setOngoingworkstotal(new BigDecimal(0));
									filterdata.setGrandtotaldnit(new BigDecimal(0));
									filterdata.setGrandtotalworks(new BigDecimal(0));
									filterdata.setGrandtotalongoing(new BigDecimal(0));

								}

							} else if (ob[3].toString().equalsIgnoreCase("Approved")) {
								if (ob[2] != null) {
									if (ob[2].toString().equalsIgnoreCase("Public Health")
											|| ob[2].toString().equalsIgnoreCase("1")) {
										filterdata.setTechnicalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setTechnicalHE(new BigDecimal(0));
										filterdata.setTechnicalBR(new BigDecimal(0));
										filterdata.setGtotalPH(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
											|| ob[2].toString().equalsIgnoreCase("2")) {
										filterdata.setTechnicalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setTechnicalPH(new BigDecimal(0));
										filterdata.setTechnicalBR(new BigDecimal(0));
										filterdata.setGtotalHE(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
										filterdata.setGtotalBR(new BigDecimal(0));

									} else if (ob[2].toString().equalsIgnoreCase("B&R")
											|| ob[2].toString().equalsIgnoreCase("3")
											|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
										filterdata.setTechnicalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setTechnicalHE(new BigDecimal(0));
										filterdata.setTechnicalPH(new BigDecimal(0));
										filterdata.setGtotalBR(
												ob[4] != null ? new BigDecimal(ob[4].toString()) : new BigDecimal(0));
										filterdata.setGtotalHE(new BigDecimal(0));
										filterdata.setGtotalPH(new BigDecimal(0));
									}
									filterdata.setTechnicaltotal(filterdata.getTechnicalPH()
											.add(filterdata.getTechnicalHE()).add(filterdata.getTechnicalBR()));
									filterdata.setGtotaltotal(filterdata.getTechnicaltotal());
									filterdata.setGrandtotaltechnical(filterdata.getTechnicaltotal());
									filterdata.setGrandtotalestimate(new BigDecimal(0));
									filterdata.setGrandtotalrough(new BigDecimal(0));
									filterdata.setGrandtotaltechnical(filterdata.getTechnicalPH()
											.add(filterdata.getTechnicalHE()).add(filterdata.getTechnicalBR()));
									filterdata.setRoughPH(new BigDecimal(0));
									filterdata.setRoughBR(new BigDecimal(0));
									filterdata.setRoughHE(new BigDecimal(0));
									filterdata.setRoughtotal(new BigDecimal(0));
									filterdata.setEstimatePH(new BigDecimal(0));
									filterdata.setEstimateBR(new BigDecimal(0));
									filterdata.setEstimateHE(new BigDecimal(0));
									filterdata.setEstimatehtotal(new BigDecimal(0));
									filterdata.setDnitPH(new BigDecimal(0));
									filterdata.setDnitHE(new BigDecimal(0));
									filterdata.setDnitBR(new BigDecimal(0));
									filterdata.setDnittotal(new BigDecimal(0));
									filterdata.setWorksPH(new BigDecimal(0));
									filterdata.setWorksHE(new BigDecimal(0));
									filterdata.setWorksBR(new BigDecimal(0));
									filterdata.setWorkstotal(new BigDecimal(0));
									filterdata.setOngoingworksPH(new BigDecimal(0));
									filterdata.setOngoingworksHE(new BigDecimal(0));
									filterdata.setOngoingworksBR(new BigDecimal(0));
									filterdata.setOngoingworkstotal(new BigDecimal(0));
									filterdata.setGrandtotaldnit(new BigDecimal(0));
									filterdata.setGrandtotalworks(new BigDecimal(0));
									filterdata.setGrandtotalongoing(new BigDecimal(0));
								}
							}
						}

						worksdata.put(expen, filterdata);
					}

				}

			}

		} // forloop
		if (listdnit.size() != 0) {
			for (final Object[] ob : listdnit) {
				WorksAbstractliabilities filterdnit = new WorksAbstractliabilities();
				if (ob[1] != null && ob[2] != null) {
					if (worksdata.containsKey(ob[1].toString())) {
						if (ob[3].toString().equalsIgnoreCase("Approved")) {
							if (ob[2].toString().equalsIgnoreCase("Public Health")
									|| ob[2].toString().equalsIgnoreCase("1")) {
								filterdnit.setDnitPH(
										(ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0)));
								filterdnit.setDnitHE(new BigDecimal(0));
								filterdnit.setDnitBR(new BigDecimal(0));
								filterdnit.setGtotalPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalBR(new BigDecimal(0));
								filterdnit.setGtotalHE(new BigDecimal(0));
							} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
									|| ob[2].toString().equalsIgnoreCase("2")) {
								filterdnit.setDnitPH(new BigDecimal(0));
								filterdnit.setDnitHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setDnitBR(new BigDecimal(0));
								filterdnit.setGtotalHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalPH(new BigDecimal(0));
								filterdnit.setGtotalBR(new BigDecimal(0));

							} else if (ob[2].toString().equalsIgnoreCase("B&R")
									|| ob[2].toString().equalsIgnoreCase("3")
									|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
								filterdnit.setDnitPH(new BigDecimal(0));
								filterdnit.setDnitHE(new BigDecimal(0));
								filterdnit.setDnitBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalPH(new BigDecimal(0));
								filterdnit.setGtotalBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalHE(new BigDecimal(0));
							}
							filterdnit.setDnittotal(
									filterdnit.getDnitBR().add(filterdnit.getDnitHE()).add(filterdnit.getDnitPH()));
							filterdnit.setGtotaltotal(filterdnit.getDnittotal());
							filterdnit.setGrandtotaldnit(filterdnit.getDnittotal());
							WorksAbstractliabilities valuednit = worksdata.get(ob[1].toString());
							if (filterdnit.getDnitPH() != null)
								valuednit.setDnitPH(valuednit.getDnitPH().add(filterdnit.getDnitPH()));
							if (filterdnit.getDnitBR() != null)
								valuednit.setDnitBR(valuednit.getDnitBR().add(filterdnit.getDnitBR()));
							if (filterdnit.getDnitHE() != null)
								valuednit.setDnitHE(valuednit.getDnitHE().add(filterdnit.getDnitHE()));
							if (filterdnit.getDnittotal() != null)
								valuednit.setDnittotal(valuednit.getDnittotal().add(filterdnit.getDnittotal()));
							if (filterdnit.getGtotalPH() != null)
								valuednit.setGtotalPH(valuednit.getGtotalPH().add(filterdnit.getGtotalPH()));
							if (filterdnit.getGtotalBR() != null)
								valuednit.setGtotalBR(valuednit.getGtotalBR().add(filterdnit.getGtotalBR()));
							if (filterdnit.getGtotalHE() != null)
								valuednit.setGtotalHE(valuednit.getGtotalHE().add(filterdnit.getGtotalHE()));
							if (filterdnit.getGtotaltotal() != null)
								valuednit.setGtotaltotal(valuednit.getGtotaltotal().add(filterdnit.getGtotaltotal()));
							if (filterdnit.getGrandtotaldnit() != null)
								valuednit.setGrandtotaldnit(
										valuednit.getGrandtotaldnit().add(filterdnit.getGrandtotaldnit()));
							worksdata.put(ob[1].toString(), valuednit);
						}
					} else {
						if (ob[3].toString().equalsIgnoreCase("Approved")) {
							if (ob[2].toString().equalsIgnoreCase("Public Health")
									|| ob[2].toString().equalsIgnoreCase("1")) {
								filterdnit.setDnitPH(
										(ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0)));
								filterdnit.setDnitHE(new BigDecimal(0));
								filterdnit.setDnitBR(new BigDecimal(0));
								filterdnit.setGtotalPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalBR(new BigDecimal(0));
								filterdnit.setGtotalHE(new BigDecimal(0));
							} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
									|| ob[2].toString().equalsIgnoreCase("2")) {
								filterdnit.setDnitPH(new BigDecimal(0));
								filterdnit.setDnitHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setDnitBR(new BigDecimal(0));
								filterdnit.setGtotalHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalPH(new BigDecimal(0));
								filterdnit.setGtotalBR(new BigDecimal(0));

							} else if (ob[2].toString().equalsIgnoreCase("B&R")
									|| ob[2].toString().equalsIgnoreCase("3")
									|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
								filterdnit.setDnitPH(new BigDecimal(0));
								filterdnit.setDnitHE(new BigDecimal(0));
								filterdnit.setDnitBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalPH(new BigDecimal(0));
								filterdnit.setGtotalBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterdnit.setGtotalHE(new BigDecimal(0));
							}
							filterdnit.setDnittotal(
									filterdnit.getDnitBR().add(filterdnit.getDnitHE()).add(filterdnit.getDnitPH()));
							filterdnit.setGtotaltotal(filterdnit.getDnittotal());
							filterdnit.setGrandtotaldnit(filterdnit.getDnittotal());
							filterdnit.setWorksPH(new BigDecimal(0));
							filterdnit.setWorksHE(new BigDecimal(0));
							filterdnit.setWorksBR(new BigDecimal(0));
							filterdnit.setWorkstotal(new BigDecimal(0));
							filterdnit.setOngoingworksPH(new BigDecimal(0));
							filterdnit.setOngoingworksHE(new BigDecimal(0));
							filterdnit.setOngoingworksBR(new BigDecimal(0));
							filterdnit.setOngoingworkstotal(new BigDecimal(0));
							filterdnit.setGrandtotalworks(new BigDecimal(0));
							filterdnit.setGrandtotalongoing(new BigDecimal(0));
							worksdata.put(ob[1].toString(), filterdnit);
						}

					}
				}
			}
		}
		if (listworks.size() != 0) {
			for (final Object[] ob : listworks) {
				if (ob[1] != null && ob[2] != null) {
					WorksAbstractliabilities filterworks = new WorksAbstractliabilities();
					if (worksdata.containsKey(ob[1].toString())) {
						if (ob[3] != null && ob[3].toString().equalsIgnoreCase("Approved")) {
							if (ob[2].toString().equalsIgnoreCase("Public Health")
									|| ob[2].toString().equalsIgnoreCase("1")) {
								filterworks.setOngoingworksPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setOngoingworksHE(new BigDecimal(0));
								filterworks.setOngoingworksBR(new BigDecimal(0));
								filterworks.setGtotalPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
							} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
									|| ob[2].toString().equalsIgnoreCase("2")) {
								filterworks.setOngoingworksPH(new BigDecimal(0));
								filterworks.setOngoingworksBR(new BigDecimal(0));
								filterworks.setOngoingworksHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));

							} else if (ob[2].toString().equalsIgnoreCase("B&R")
									|| ob[2].toString().equalsIgnoreCase("3")
									|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
								filterworks.setOngoingworksPH(new BigDecimal(0));
								filterworks.setOngoingworksHE(new BigDecimal(0));
								filterworks.setOngoingworksBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
							}
							filterworks.setOngoingworkstotal(filterworks.getOngoingworksPH()
									.add(filterworks.getOngoingworksHE()).add(filterworks.getOngoingworksBR()));
							filterworks.setGtotaltotal(filterworks.getOngoingworkstotal());
							filterworks.setGrandtotalongoing(filterworks.getOngoingworkstotal());
						} else {
							if (ob[2].toString().equalsIgnoreCase("Public Health")
									|| ob[2].toString().equalsIgnoreCase("1")) {
								filterworks.setWorksPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setWorksHE(new BigDecimal(0));
								filterworks.setWorksBR(new BigDecimal(0));
								filterworks.setGtotalPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
							} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
									|| ob[2].toString().equalsIgnoreCase("2")) {
								filterworks.setWorksPH(new BigDecimal(0));
								filterworks.setWorksBR(new BigDecimal(0));
								filterworks.setWorksHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));

							} else if (ob[2].toString().equalsIgnoreCase("B&R")
									|| ob[2].toString().equalsIgnoreCase("3")
									|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
								filterworks.setWorksPH(new BigDecimal(0));
								filterworks.setWorksHE(new BigDecimal(0));
								filterworks.setWorksBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
							}
							filterworks.setWorkstotal(filterworks.getWorksPH().add(filterworks.getWorksHE())
									.add(filterworks.getWorksBR()));
							filterworks.setGtotaltotal(filterworks.getWorkstotal());
							filterworks.setGrandtotalworks(filterworks.getWorkstotal());
						}
						WorksAbstractliabilities valuednit = worksdata.get(ob[1].toString());
						if (filterworks.getWorksPH() != null)
							valuednit.setWorksPH(valuednit.getWorksPH().add(filterworks.getWorksPH()));
						if (filterworks.getWorksBR() != null)
							valuednit.setWorksBR(valuednit.getWorksBR().add(filterworks.getWorksBR()));
						if (filterworks.getWorksHE() != null)
							valuednit.setWorksHE(valuednit.getWorksHE().add(filterworks.getWorksHE()));
						if (filterworks.getWorkstotal() != null)
							valuednit.setWorkstotal(valuednit.getWorkstotal().add(filterworks.getWorkstotal()));
						if (filterworks.getOngoingworksPH() != null)
							valuednit.setOngoingworksPH(
									valuednit.getOngoingworksPH().add(filterworks.getOngoingworksPH()));
						if (filterworks.getOngoingworksBR() != null)
							valuednit.setOngoingworksBR(
									valuednit.getOngoingworksBR().add(filterworks.getOngoingworksBR()));
						if (filterworks.getOngoingworksHE() != null)
							valuednit.setOngoingworksHE(
									valuednit.getOngoingworksHE().add(filterworks.getOngoingworksHE()));
						if (filterworks.getOngoingworkstotal() != null)
							valuednit.setOngoingworkstotal(
									valuednit.getOngoingworkstotal().add(filterworks.getOngoingworkstotal()));
						if (filterworks.getGtotalPH() != null)
							valuednit.setGtotalPH(valuednit.getGtotalPH().add(filterworks.getGtotalPH()));
						if (filterworks.getGtotalBR() != null)
							valuednit.setGtotalBR(valuednit.getGtotalBR().add(filterworks.getGtotalBR()));
						if (filterworks.getGtotalHE() != null)
							valuednit.setGtotalHE(valuednit.getGtotalHE().add(filterworks.getGtotalHE()));
						if (filterworks.getGtotaltotal() != null)
							valuednit.setGtotaltotal(valuednit.getGtotaltotal().add(filterworks.getGtotaltotal()));
						if (filterworks.getGrandtotalongoing() != null)
							valuednit.setGrandtotalongoing(
									valuednit.getGrandtotalongoing().add(filterworks.getGrandtotalongoing()));
						if (filterworks.getGrandtotalworks() != null)
							valuednit.setGrandtotalworks(
									valuednit.getGrandtotalworks().add(filterworks.getGrandtotalworks()));
						worksdata.put(ob[1].toString(), valuednit);
					} else {
						// if not found in map

						if (ob[3] != null && ob[3].toString().equalsIgnoreCase("Approved")) {
							if (ob[2].toString().equalsIgnoreCase("Public Health")
									|| ob[2].toString().equalsIgnoreCase("1")) {
								filterworks.setOngoingworksPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setOngoingworksHE(new BigDecimal(0));
								filterworks.setOngoingworksBR(new BigDecimal(0));
								filterworks.setGtotalPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
							} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
									|| ob[2].toString().equalsIgnoreCase("2")) {
								filterworks.setOngoingworksPH(new BigDecimal(0));
								filterworks.setOngoingworksBR(new BigDecimal(0));
								filterworks.setOngoingworksHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));

							} else if (ob[2].toString().equalsIgnoreCase("B&R")
									|| ob[2].toString().equalsIgnoreCase("3")
									|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
								filterworks.setOngoingworksPH(new BigDecimal(0));
								filterworks.setOngoingworksHE(new BigDecimal(0));
								filterworks.setOngoingworksBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
							}
							filterworks.setOngoingworkstotal(filterworks.getOngoingworksPH()
									.add(filterworks.getOngoingworksHE()).add(filterworks.getOngoingworksBR()));
							filterworks.setGtotaltotal(filterworks.getOngoingworkstotal());
							filterworks.setGrandtotalongoing(filterworks.getOngoingworkstotal());
							filterworks.setGrandtotalworks(new BigDecimal(0));
							filterworks.setWorksPH(new BigDecimal(0));
							filterworks.setWorksHE(new BigDecimal(0));
							filterworks.setWorksBR(new BigDecimal(0));
							filterworks.setWorkstotal(new BigDecimal(0));
						} else {
							if (ob[2].toString().equalsIgnoreCase("Public Health")
									|| ob[2].toString().equalsIgnoreCase("1")) {
								filterworks.setWorksPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setWorksHE(new BigDecimal(0));
								filterworks.setWorksBR(new BigDecimal(0));
								filterworks.setGtotalPH(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
							} else if (ob[2].toString().equalsIgnoreCase("Horticulture & Electrical")
									|| ob[2].toString().equalsIgnoreCase("2")) {
								filterworks.setWorksPH(new BigDecimal(0));
								filterworks.setWorksBR(new BigDecimal(0));
								filterworks.setGtotalHE(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(new BigDecimal(0));

							} else if (ob[2].toString().equalsIgnoreCase("B&R")
									|| ob[2].toString().equalsIgnoreCase("3")
									|| ob[2].toString().equalsIgnoreCase("Building & Roads")) {
								filterworks.setWorksPH(new BigDecimal(0));
								filterworks.setWorksHE(new BigDecimal(0));
								filterworks.setWorksBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
								filterworks.setGtotalHE(new BigDecimal(0));
								filterworks.setGtotalPH(new BigDecimal(0));
								filterworks.setGtotalBR(
										ob[0] != null ? new BigDecimal(ob[0].toString()) : new BigDecimal(0));
							}
							filterworks.setWorkstotal(filterworks.getWorksPH()
									.add(filterworks.getWorksHE().add(filterworks.getWorksBR())));
							filterworks.setGtotaltotal(filterworks.getWorkstotal());
							filterworks.setGrandtotalworks(filterworks.getWorkstotal());
							filterworks.setGrandtotalongoing(new BigDecimal(0));
							filterworks.setOngoingworksPH(new BigDecimal(0));
							filterworks.setOngoingworksHE(new BigDecimal(0));
							filterworks.setOngoingworksBR(new BigDecimal(0));
						}
						worksdata.put(ob[1].toString(), filterworks);
					}
				}

			}
		}
		WorksAbstractliabilities granddata = new WorksAbstractliabilities();
		granddata.setGrandtotalrough(new BigDecimal(0));
		granddata.setGrandtotalestimate(new BigDecimal(0));
		granddata.setGrandtotaltechnical(new BigDecimal(0));
		granddata.setGrandtotalg(new BigDecimal(0));
		granddata.setGrandtotaldnit(new BigDecimal(0));
		granddata.setGrandtotalongoing(new BigDecimal(0));
		granddata.setGrandtotalworks(new BigDecimal(0));
		for (Entry<String, WorksAbstractliabilities> test : worksdata.entrySet()) {
			System.out.println("Expenditure:: " + test.getKey() + "::value:: " + test.getValue().getRoughPH() + ":"
					+ test.getValue().getRoughBR() + ":" + test.getValue().getRoughtotal() + ": "
					+ test.getValue().getEstimatehtotal() + " : " + test.getValue().getTechnicaltotal());
			granddata.setGrandtotalrough(granddata.getGrandtotalrough()
					.add(test.getValue().getGrandtotalrough() != null ? test.getValue().getGrandtotalrough()
							: new BigDecimal(0)));
			granddata.setGrandtotalestimate(granddata.getGrandtotalestimate()
					.add(test.getValue().getGrandtotalestimate() != null ? test.getValue().getGrandtotalestimate()
							: new BigDecimal(0)));
			granddata.setGrandtotaltechnical(granddata.getGrandtotaltechnical()
					.add(test.getValue().getGrandtotaltechnical() != null ? test.getValue().getGrandtotaltechnical()
							: new BigDecimal(0)));
			granddata.setGrandtotaldnit(granddata.getGrandtotaldnit()
					.add(test.getValue().getGrandtotaldnit() != null ? test.getValue().getGrandtotaldnit()
							: new BigDecimal(0)));
			granddata.setGrandtotalongoing(granddata.getGrandtotalongoing()
					.add(test.getValue().getGrandtotalongoing() != null ? test.getValue().getGrandtotalongoing()
							: new BigDecimal(0)));
			granddata.setGrandtotalworks(granddata.getGrandtotalworks()
					.add(test.getValue().getGrandtotalworks() != null ? test.getValue().getGrandtotalworks()
							: new BigDecimal(0)));
			granddata.setGrandtotalg(granddata.getGrandtotalg().add(
					test.getValue().getGtotaltotal() != null ? test.getValue().getGtotaltotal() : new BigDecimal(0)));
		}
		worksdata.put("Grand Total", granddata);
		System.out.println(":::::List size:::: " + listest.size() + ":::Size of map:: " + worksdata.size());
		String[] COLUMNS = { "DESCRIPT ON OF WORK", " ", "Rough Cost Estimate Approved",
				"Estimate Administrative approval awarded", "Technical Estimate Approval awarded", "DNIT awarded",
				"Work agreement awarded but work yet to start", "Onging Work", "TOTAL" };
		String startdate = DDMMYYYYFORMAT1.format(fromdate);
		String enddate = DDMMYYYYFORMAT1.format(todate);
		System.out.println("from date " + startdate + "end date " + enddate);
		String heading = "ABSTRACT/LIABILITIES OF WORK  (Date - " + startdate + " to " + enddate + ")";
		ByteArrayInputStream in = ExcelGenerator.estimateworksToExcel(worksdata, COLUMNS, heading);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=ABSTRACTWorks.xlsx");
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));

	}

	private String getDateQuerynew(final Date fromdate, final Date todate) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != fromdate) {
				numDateQuery.append(" and to_char(tdc.createddate,'yyyy/mm/dd')>='")
						.append(DDMMYYYYFORMAT2.format(fromdate)).append("'");
			}
			if (null != todate) {
				numDateQuery.append(" and  to_char(tdc.createddate,'yyyy/mm/dd')<='");
				numDateQuery.append(DDMMYYYYFORMAT2.format(todate));
				numDateQuery.append("'");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
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
