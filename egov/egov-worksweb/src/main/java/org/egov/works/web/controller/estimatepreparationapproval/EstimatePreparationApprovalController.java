package org.egov.works.web.controller.estimatepreparationapproval;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.works.boq.entity.BoQDetails;
//import org.egov.works.estimatepreparationapproval.autonumber.AuditNumberGenerator;
import org.egov.works.estimatepreparationapproval.autonumber.EstimateNoGenerator;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.workestimate.service.WorkEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
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
			final Model model, @RequestParam("file1") MultipartFile file1, final HttpServletRequest request)
			throws Exception {

		String workFlowAction=estimatePreparationApproval.getWorkFlowAction();
		/*DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (estimatePreparationApproval.getEstimateDt() != null && estimatePreparationApproval.getEstimateDt() != "") {
			estimatePreparationApproval.setEstimateDate(inputFormat.parse(estimatePreparationApproval.getEstimateDt()));
		}

		if (estimatePreparationApproval.getDt() != null && estimatePreparationApproval.getDt() != "") {
			estimatePreparationApproval.setDate(inputFormat.parse(estimatePreparationApproval.getDt()));
		}*/
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
			msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+"is Saved as draft";
		}
		else if((workflowaction.equalsIgnoreCase("Forward") || workflowaction.equalsIgnoreCase("Approve")) && savedEstimatePreparationApproval.getStatus().getCode().equalsIgnoreCase("Approved"))
		{
			msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+"is approved";
		}
		else 
		{
			approverName=getEmployeeName(Long.parseLong(approverDetails));
			msg="Estimate Number "+savedEstimatePreparationApproval.getEstimateNumber()+"has been forwarded to "+approverName;
		}
		return msg;
	}

	@RequestMapping(value = "/estimate", params = "Save As Draft", method = RequestMethod.POST)
	public String saveBoQDetailsDataDraft(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, @RequestParam("file1") MultipartFile file1, final HttpServletRequest request)
			throws Exception {
		
		String workFlowAction=estimatePreparationApproval.getWorkFlowAction();
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
						}

					} else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {

						if (cell.getColumnIndex() == 2) {
							aBoQDetails.setUnit(cell.getNumericCellValue());
						} else if (cell.getColumnIndex() == 3) {
							aBoQDetails.setRate(cell.getNumericCellValue());
						} else if (cell.getColumnIndex() == 4) {
							aBoQDetails.setQuantity(cell.getNumericCellValue());
							aBoQDetails.setAmount(aBoQDetails.getRate() * aBoQDetails.getQuantity());
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
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		model.addAttribute(STATE_TYPE, estimatePreparationApproval.getClass().getSimpleName());
        prepareWorkflow(model, estimatePreparationApproval, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		model.addAttribute("estimatePreparationApproval", estimatePreparationApproval);

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
		/*DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (estimatePreparationApproval.getFromDate() != null && estimatePreparationApproval.getFromDate() != ""
				&& !estimatePreparationApproval.getFromDate().isEmpty()) {
		Date fromdate = inputFormat.parse(estimatePreparationApproval.getFromDate());
		estimatePreparationApproval.setFromDt(fromdate);
		}

		if (estimatePreparationApproval.getToDate() != null && estimatePreparationApproval.getToDate() != ""
				&& !estimatePreparationApproval.getToDate().isEmpty()) {
		Date todate = inputFormat.parse(estimatePreparationApproval.getToDate());
		estimatePreparationApproval.setToDt(todate);
		}
		
*/
		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
		long department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}

		List<EstimatePreparationApproval> workEstimateDetails = workEstimateService.searchWorkEstimateData(request,
				estimatePreparationApproval);
		approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-estimate-form";

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);

		for (int j = 0; j < estimateDetails.getNewBoQDetailsList().size(); j++) {
			responseList = estimateDetails.getNewBoQDetailsList();
		}
		estimateDetails.setBoQDetailsList(responseList);
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		model.addAttribute("estimatePreparationApproval", estimateDetails);

		return "view-estimate-form";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") final Long id, Model model) {

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();

		EstimatePreparationApproval estimateDetails = workEstimateService.searchEstimateData(id);

		for (int j = 0; j < estimateDetails.getNewBoQDetailsList().size(); j++) {
			responseList = estimateDetails.getNewBoQDetailsList();
		}

		estimateDetails.setBoQDetailsList(responseList);
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);

		estimateDetails.setDepartments(getDepartmentsFromMs());

		/*String dt = estimateDetails.getDate().toString();
		estimateDetails.setDt(dt);

		String estimateDt = estimateDetails.getEstimateDate().toString();
		estimateDetails.setEstimateDt(estimateDt);*/

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
			@ModelAttribute("estimatePreparationApproval")  EstimatePreparationApproval estimatePreparationApproval,
			final HttpServletRequest request) throws Exception {

		String workFlowAction=estimatePreparationApproval.getWorkFlowAction();

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
                        + stateHistory.getLastModifiedBy());
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
            map.put("updatedBy", state.getLastModifiedBy() + "::" + state.getLastModifiedBy());
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

}
