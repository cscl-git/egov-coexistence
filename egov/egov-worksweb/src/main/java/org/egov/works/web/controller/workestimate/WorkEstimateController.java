package org.egov.works.web.controller.workestimate;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
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
public class WorkEstimateController {

	@Autowired
	WorkEstimateService workEstimateService;
	
	@Autowired
	public MicroserviceUtils microserviceUtils;

	@Autowired
	BoQDetailsService boQDetailsService;

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {
		
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		return "search-work-estimate-form";
	}

	@RequestMapping(value = "/workEstimateSearch", params = "workEstimateSearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(
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

		return "search-work-estimate-form";

	}

	@RequestMapping(value = "/workEstimateSearch", params = "save", method = RequestMethod.POST)
	public String saveWorkEstimateData(
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
						respList.add(boq);
					}
				}

				estimatePreparationApproval.setId(workEstimateDetails.getId());
				// estimatePreparationApproval.setBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				// estimatePreparationApproval.setNewBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				estimatePreparationApproval.setNewBoQDetailsList(respList);

				estimatePreparationApproval.setDepartment(workEstimateDetails.getExecutingDivision().toString());
				
				model.addAttribute("workEstimateDetails", estimatePreparationApproval);

			}
		}

		return "search-work-estimate-form";

	}
	
	private List<Department> getDepartmentsFromMs() {
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
				responseList.add(boq);
				amount += estimatePreparationApproval.getNewBoQDetailsList().get(i).getAmount();
			}
		}
		EstimatePreparationApproval saveBoqDetails = workEstimateService.searchBoqData(request, id);
		WorkOrderAgreement workOrderAgreement = new WorkOrderAgreement();
		workOrderAgreement.setWork_number(saveBoqDetails.getEstimateNumber());
		workOrderAgreement.setAgreement_details(saveBoqDetails.getAgencyWorkOrder());
		// workOrderAgreement.setBoQDetailsList(saveBoqDetails.getNewBoQDetailsList());
		workOrderAgreement.setBoQDetailsList(responseList);
		workOrderAgreement.setWork_amount(saveBoqDetails.getEstimateAmount().toString());
		workOrderAgreement.setFund(saveBoqDetails.getFundSource());
		workOrderAgreement.setExecuting_department(saveBoqDetails.getExecutingDivision().toString());
		workOrderAgreement.setWork_status(saveBoqDetails.getWorkStatus().toString());
		workOrderAgreement.setTenderCost(saveBoqDetails.getTenderCost());
		workOrderAgreement.setTimeLimit(saveBoqDetails.getTimeLimit());
		workOrderAgreement.setEstimatedCost(amount.toString());
		workOrderAgreement.setDepartments(getDepartmentsFromMs());

		model.addAttribute("workOrderAgreement", workOrderAgreement);
		return "edit-work-agreement";

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

		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement);

		return "edit-work-agreement";

	}
	
	@RequestMapping(value = "/work1", method = RequestMethod.POST)
	public String saveEditData(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {

		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}

		WorkOrderAgreement savedWorkOrderAgreement = boQDetailsService.saveBoQDetailsData(request, workOrderAgreement);

		return "boqDetails";

	}


}
