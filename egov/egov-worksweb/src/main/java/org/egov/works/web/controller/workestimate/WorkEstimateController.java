package org.egov.works.web.controller.workestimate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.masters.services.ContractorService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.model.masters.Contractor;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.workestimate.service.WorkEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/workEstimate")
public class WorkEstimateController extends GenericWorkFlowController{

	@Autowired
	WorkEstimateService workEstimateService;
	
	@Autowired
	public MicroserviceUtils microserviceUtils;

	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;

	@Autowired
	BoQDetailsService boQDetailsService;
	
	private static final String STATE_TYPE = "stateType";
	 @Autowired
		private DocumentUploadRepository documentUploadRepository;
	@Autowired
	private ContractorService contractorService;

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {
		
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		return "search-work-estimate-form";
	}
	
	@RequestMapping(value = "/newformEstimate", method = RequestMethod.POST)
	public String showNewFormEstimateGet(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {
		
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		return "search-work-estimate-form-new";
	}
	
	
	/*forDnit creation from estimate*/
	@RequestMapping(value = "/dnitsearchEstimate", method = RequestMethod.POST)
	public String showNewDnitCreation(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {
		
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		return "search-work-estimate-dnitCreationform";
	}
	

	@RequestMapping(value = "/workEstimateSearch", params = "workEstimateSearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(
			@ModelAttribute("workEstimateDetails") final DNITCreation estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<DNITCreation> approvalList = new ArrayList<DNITCreation>();
	

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			estimatePreparationApproval
					.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		}
		
		List<DNITCreation> workEstimateDetails = workEstimateService.searchWorkDnit(request,
				estimatePreparationApproval);
		approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-work-estimate-form";

	}
	
	
	@RequestMapping(value = "/workEstimateSearchNew", params = "workEstimateSearchNew", method = RequestMethod.POST)
	public String searchWorkEstimateDataNew(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();
	

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			estimatePreparationApproval
					.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		}
		
		List<EstimatePreparationApproval> workEstimateDetails = workEstimateService.searchWorkEstimate(request,
				estimatePreparationApproval);
		approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);
		//added as department not showing
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-work-estimate-form-new";

	}


	
	/*forDnit creation from estimate*/
	@RequestMapping(value = "/workEstimateSearchDnit", params = "workEstimateSearch", method = RequestMethod.POST)
	public String searchWorkEstimateDataForDnit(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			estimatePreparationApproval
					.setExecutingDivision(Long.parseLong(estimatePreparationApproval.getDepartment()));
		}
		
		List<EstimatePreparationApproval> workEstimateDetails = workEstimateService.searchWorkEstimate(request,
				estimatePreparationApproval);
		approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-work-estimate-dnitCreationform";

	}	
	
	//edited workEstimateSearchNew in place of workEstimateSearchNew

	@RequestMapping(value = "/workEstimateSearch", params = "save", method = RequestMethod.POST)
	public String saveWorkEstimateData(
			@ModelAttribute("workEstimateDetails") final DNITCreation estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		
		//System.out.println(estimatePreparationApproval.getEstimateList().size()+"+++++++++++++++++++++++");
		
		for (int i = 0; i < estimatePreparationApproval.getEstimateList().size(); i++) {
			//System.out.println(estimatePreparationApproval.getEstimateList().get(i).isChecked()+"+++++++++++++++++++++++");
			if (estimatePreparationApproval.getEstimateList().get(i).isChecked() == true) {
			//	System.out.println(estimatePreparationApproval.getEstimateList().get(i).getId()+"+++++++++++++++++++++++++++");
				DNITCreation	workEstimateDetails = workEstimateService.searchDnitBoqData(request,
						estimatePreparationApproval.getEstimateList().get(i).getId());
				
				List<BoQDetails> respList = new ArrayList<BoQDetails>();
				BoQDetails boq = new BoQDetails();
				for (int j = 0; j < workEstimateDetails.getNewBoQDetailsList().size(); j++) {
					if (workEstimateDetails.getNewBoQDetailsList().get(j).getWorkOrderAgreement() == null) {
						boq = workEstimateDetails.getNewBoQDetailsList().get(j);
						boq.setSizeIndex(respList.size());
						respList.add(boq);
					}
				}
				estimatePreparationApproval.setId(workEstimateDetails.getId());
				estimatePreparationApproval.setBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				// estimatePreparationApproval.setNewBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				estimatePreparationApproval.setNewBoQDetailsList(respList);
				estimatePreparationApproval.setDepartment(workEstimateDetails.getExecutingDivision().toString());
				Map<String, List<BoQDetails>> groupByMilesToneMap = 
						respList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));

				model.addAttribute("milestoneList",groupByMilesToneMap);
				model.addAttribute("workEstimateDetails", estimatePreparationApproval);
			}
		}

		return "search-work-estimate-form";

	}
	//After search 
	
	@RequestMapping(value = "/workEstimateSearchNew", params = "save1", method = RequestMethod.POST)
	public String saveWorkEstimateDataNew(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		for (int i = 0; i < estimatePreparationApproval.getEstimateList().size(); i++) {
			if (estimatePreparationApproval.getEstimateList().get(i).isChecked() == true) {
				
				EstimatePreparationApproval	workEstimateDetails = workEstimateService.searchBoqData(request,
						estimatePreparationApproval.getEstimateList().get(i).getId());
				
				List<BoQDetails> respList = new ArrayList<BoQDetails>();
				BoQDetails boq = new BoQDetails();
				for (int j = 0; j < workEstimateDetails.getNewBoQDetailsList().size(); j++) {
					if (workEstimateDetails.getNewBoQDetailsList().get(j).getWorkOrderAgreement() == null) {
						boq = workEstimateDetails.getNewBoQDetailsList().get(j);
						boq.setSizeIndex(respList.size());
						respList.add(boq);
					}
				}
				estimatePreparationApproval.setId(workEstimateDetails.getId());
				estimatePreparationApproval.setBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				// estimatePreparationApproval.setNewBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				estimatePreparationApproval.setNewBoQDetailsList(respList);
				estimatePreparationApproval.setDepartment(workEstimateDetails.getExecutingDivision().toString());
				Map<String, List<BoQDetails>> groupByMilesToneMap = 
						respList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));

				model.addAttribute("milestoneList",groupByMilesToneMap);
				model.addAttribute("workEstimateDetails", estimatePreparationApproval);
			}
		}

		return "search-work-estimate-form-new";

	}
				
	/*forDnit creation from estimate*/
				
	@RequestMapping(value = "/workEstimateSearchDnit", params = "searchBOQ", method = RequestMethod.POST)
	public String searchBoq(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		for (int i = 0; i < estimatePreparationApproval.getEstimateList().size(); i++) {
			if (estimatePreparationApproval.getEstimateList().get(i).isChecked() == true) {
				
				EstimatePreparationApproval	workEstimateDetails = workEstimateService.searchBoqData(request,
						estimatePreparationApproval.getEstimateList().get(i).getId());
				
				List<BoQDetails> respList = new ArrayList<BoQDetails>();
				BoQDetails boq = new BoQDetails();
				for (int j = 0; j < workEstimateDetails.getNewBoQDetailsList().size(); j++) {
					if (workEstimateDetails.getNewBoQDetailsList().get(j).getWorkOrderAgreement() == null && workEstimateDetails.getNewBoQDetailsList().get(j).getDnitCreation() == null) {
						boq = workEstimateDetails.getNewBoQDetailsList().get(j);
						boq.setSizeIndex(respList.size());
						respList.add(boq);
					}
				}
				estimatePreparationApproval.setId(workEstimateDetails.getId());
				estimatePreparationApproval.setBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				// estimatePreparationApproval.setNewBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				estimatePreparationApproval.setNewBoQDetailsList(respList);
				estimatePreparationApproval.setDepartment(workEstimateDetails.getExecutingDivision().toString());
				Map<String, List<BoQDetails>> groupByMilesToneMap = 
						respList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
				 
				model.addAttribute("milestoneList",groupByMilesToneMap);
				model.addAttribute("workEstimateDetails", estimatePreparationApproval);
			}
		}

		return "search-work-estimate-dnitCreationform";

	}
	
	
	public List<Department> getDepartmentsFromMs() {
        List<Department> departments = microserviceUtils.getDepartments();
        return departments;
    }

	@RequestMapping(value = "/workEstimateSearch", params = "saveboq", method = RequestMethod.POST)
	public String saveBoqData(
			@ModelAttribute("workOrderAgreement") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request, final RedirectAttributes redirectAttributes)
			throws Exception {

		Long id = 0l;
		for (int i = 0; i < estimatePreparationApproval.getEstimateList().size(); i++) {
			if (estimatePreparationApproval.getEstimateList().get(i).isChecked() == true) {
				// estimatePreparationApproval.setId(estimatePreparationApproval.getEstimateList().get(i).getId());
				id = estimatePreparationApproval.getEstimateList().get(i).getId();
			}
		}

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		BoQDetails boq = new BoQDetails();
		Double amount = 0d;

		for (int i = 0; i < estimatePreparationApproval.getNewBoQDetailsList().size(); i++) {
			if (estimatePreparationApproval.getNewBoQDetailsList().get(i).isCheckboxChecked()) {
				boq = estimatePreparationApproval.getNewBoQDetailsList().get(i);
				
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);
				amount += estimatePreparationApproval.getNewBoQDetailsList().get(i).getAmount();
			}
		}
		DNITCreation saveBoqDetails = workEstimateService.searchDnitBoqData(request, id);
		WorkOrderAgreement workOrderAgreement = new WorkOrderAgreement();
		workOrderAgreement.setBoQDetailsList(responseList);
		workOrderAgreement.setName_work_order(saveBoqDetails.getWorkName());
		workOrderAgreement.setWork_number(saveBoqDetails.getEstimateNumber());
		workOrderAgreement.setDepartment(String.valueOf(saveBoqDetails.getExecutingDivision()));
		workOrderAgreement.setWork_amount(String.valueOf(amount));
		workOrderAgreement.setCategory(saveBoqDetails.getWorkCategory());
		workOrderAgreement.setWardNumber(saveBoqDetails.getWardNumber());
		workOrderAgreement.setFund(saveBoqDetails.getFundSource());
		workOrderAgreement.setSector(saveBoqDetails.getSectorNumber());
		workOrderAgreement.setWorkLocation(saveBoqDetails.getWorkLocation());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		workOrderAgreement.setWorksWing(saveBoqDetails.getWorksWing());
		workOrderAgreement.setSubdivision(saveBoqDetails.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(saveBoqDetails.getExecutingDivision()));
		workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(saveBoqDetails.getWorksWing())));
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
        prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        
        Map<String, List<BoQDetails>> groupByMilesToneMap = 
        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed","Y");
		return "boqDetails";

	}
	
	
	@RequestMapping(value = "/workEstimateSearchNew", params = "saveboq1", method = RequestMethod.POST)
	public String saveBoqData1(
			@ModelAttribute("workOrderAgreement") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request, final RedirectAttributes redirectAttributes)
			throws Exception {

		Long id = 0l;
		for (int i = 0; i < estimatePreparationApproval.getEstimateList().size(); i++) {
			if (estimatePreparationApproval.getEstimateList().get(i).isChecked() == true) {
				// estimatePreparationApproval.setId(estimatePreparationApproval.getEstimateList().get(i).getId());
				id = estimatePreparationApproval.getEstimateList().get(i).getId();
			}
		}

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		BoQDetails boq = new BoQDetails();
		Double amount = 0d;

		for (int i = 0; i < estimatePreparationApproval.getNewBoQDetailsList().size(); i++) {
			if (estimatePreparationApproval.getNewBoQDetailsList().get(i).isCheckboxChecked()) {
				boq = estimatePreparationApproval.getNewBoQDetailsList().get(i);
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);
				amount += estimatePreparationApproval.getNewBoQDetailsList().get(i).getAmount();
			}
		}
		EstimatePreparationApproval saveBoqDetails = workEstimateService.searchBoqData(request, id);
		WorkOrderAgreement workOrderAgreement = new WorkOrderAgreement();
		workOrderAgreement.setBoQDetailsList(responseList);
		workOrderAgreement.setName_work_order(saveBoqDetails.getWorkName());
		workOrderAgreement.setWork_number(saveBoqDetails.getEstimateNumber());
		workOrderAgreement.setDepartment(String.valueOf(saveBoqDetails.getExecutingDivision()));
		workOrderAgreement.setWork_amount(String.valueOf(amount));
		workOrderAgreement.setCategory(saveBoqDetails.getWorkCategory());
		workOrderAgreement.setWardNumber(saveBoqDetails.getWardNumber());
		workOrderAgreement.setFund(saveBoqDetails.getFundSource());
		workOrderAgreement.setSector(saveBoqDetails.getSectorNumber());
		workOrderAgreement.setWorkLocation(saveBoqDetails.getWorkLocation());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		
		workOrderAgreement.setWorksWing(saveBoqDetails.getWorksWing());
		workOrderAgreement.setSubdivision(saveBoqDetails.getSubdivision());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setSubdivisions(estimatePreparationApprovalService.getsubdivision(saveBoqDetails.getExecutingDivision()));
		workOrderAgreement.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(saveBoqDetails.getWorksWing())));
		model.addAttribute(STATE_TYPE, workOrderAgreement.getClass().getSimpleName());
        prepareWorkflow(model, workOrderAgreement, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
        
        Map<String, List<BoQDetails>> groupByMilesToneMap = 
        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute("workOrderAgreement", workOrderAgreement);
		model.addAttribute("fileuploadAllowed","Y");
		return "boqDetails";

	}

	/*forDnit creation from estimate*/
	
	@RequestMapping(value = "/workEstimateSearchDnit", params = "sendboqForDnit", method = RequestMethod.POST)
	public String sendBoqDataForDnit(
			@ModelAttribute("estimatePreparationApproval") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request, final RedirectAttributes redirectAttributes)
			throws Exception {

		Long id = 0l;
		for (int i = 0; i < estimatePreparationApproval.getEstimateList().size(); i++) {
			if (estimatePreparationApproval.getEstimateList().get(i).isChecked() == true) {
				// estimatePreparationApproval.setId(estimatePreparationApproval.getEstimateList().get(i).getId());
				id = estimatePreparationApproval.getEstimateList().get(i).getId();
			}
		}

		List<BoQDetails> responseList = new ArrayList<BoQDetails>();
		BoQDetails boq = new BoQDetails();
		Double amount = 0d;
		DNITCreation dnitCreation=new DNITCreation();

		for (int i = 0; i < estimatePreparationApproval.getNewBoQDetailsList().size(); i++) {
			if (estimatePreparationApproval.getNewBoQDetailsList().get(i).isCheckboxChecked()) {
				boq = estimatePreparationApproval.getNewBoQDetailsList().get(i);
				boq.setSizeIndex(responseList.size());
				responseList.add(boq);
				amount += estimatePreparationApproval.getNewBoQDetailsList().get(i).getAmount();
			}
		}
		EstimatePreparationApproval estimateDetails = workEstimateService.searchBoqData(request, id);

		estimateDetails.setBoQDetailsList(responseList);
		estimateDetails.setDesignations(getDesignationsFromMs());
		String dept = estimateDetails.getExecutingDivision().toString();
		estimateDetails.setDepartment(dept);
		estimateDetails.setWorksWing(estimateDetails.getWorksWing());
		estimateDetails.setSubdivision(estimateDetails.getSubdivision());
		estimateDetails.setWorkswings(estimatePreparationApprovalService.getworskwing());
        estimateDetails.setSubdivisions(estimatePreparationApprovalService.getsubdivision(Long.valueOf(dept)));
        estimateDetails.setNewdepartments(estimatePreparationApprovalService.getdepartment(Long.valueOf(estimateDetails.getWorksWing())));
		//estimateDetails.setDepartments(getDepartmentsFromMs());
		estimateDetails.setEstimateAmount(amount);
        
        Map<String, List<BoQDetails>> groupByMilesToneMap = 
        		responseList.stream().collect(Collectors.groupingBy(BoQDetails::getMilestone));
		 
        
    	model.addAttribute(STATE_TYPE, dnitCreation.getClass().getSimpleName());
		model.addAttribute("estimatePreparationApproval", estimateDetails);
		prepareWorkflow(model, dnitCreation, new WorkflowContainer());
        prepareValidActionListByCutOffDate(model);
		model.addAttribute("dnitCreation",estimateDetails);
		model.addAttribute("milestoneList",groupByMilesToneMap);
		model.addAttribute("fileuploadAllowed","Y");
		return "dnitboqDetails";

	}

	@RequestMapping(value = "/boq", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		return "edit-work-agreement";
	}

	@RequestMapping(value = "/work", params = "work", method = RequestMethod.POST)
	public String saveBoQDetailsData(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {

		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}

		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,null,null,null,null);

		return "edit-work-agreement";

	}
	
	@RequestMapping(value = "/work1", method = RequestMethod.POST)
	public String saveEditData(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {

		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}

		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement,null,null,null,null);

		return "boqDetails";

	}
	
	public List<Contractor> getAllActiveContractors() {
		List<Contractor> contractors = contractorService.getAllActiveContractors();
		return contractors;
	}
	
	private void prepareValidActionListByCutOffDate(Model model) {
        model.addAttribute("validActionList",
                Arrays.asList("Forward/Reassign","Save As Draft"));
	}
	private void prepareValidActionListByCutOffDatednit(Model model) {
        model.addAttribute("validActionList",
                Arrays.asList("Forward/Reassign","Approve"));
	}
	
	public List<Designation> getDesignationsFromMs() {
		List<Designation> designations = microserviceUtils.getDesignations();
        return designations;
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
	public String getEmployeeName(Long empId){
        
	       return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	    }
	
}
