package org.egov.works.web.controller.searchestimate;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatesearch.service.SearchEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/searchEstimate")
public class SearchEstimateController {

	@Autowired
	public MicroserviceUtils microserviceUtils;
	
	  @Autowired 
	  SearchEstimateService searchEstimateService;
	 
	@RequestMapping(value = "/estimateApprovalSearch", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("estimateSearchDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		return "estimate-detail-search-form";
	}

	@RequestMapping(value = "/workOrderSearch", method = RequestMethod.POST)
	public String showNewFormGetwork(
			@ModelAttribute("workOrderSearchDetails") final WorkOrderAgreement workOrderAgreement, final Model model,
			HttpServletRequest request) {

		workOrderAgreement.setDepartments(getDepartmentsFromMs());

		return "work-order-search-form";
	}

	//Search Estimate
	@RequestMapping(value = "/estimateSearch", params = "estimateSearch", method = RequestMethod.POST)
	public String searchEstimateData(
			@ModelAttribute("estimateSearchDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {

		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			long department = Long.parseLong(estimatePreparationApproval.getDepartment());
			estimatePreparationApproval.setExecutingDivision(department);
		}

		List<EstimatePreparationApproval> workEstimateDetails = searchEstimateService.searchEstimateData(request,
				estimatePreparationApproval);

		approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("estimateSearchDetails", estimatePreparationApproval);

		return "estimate-detail-search-form";

	}
	
	//Search Work agreement
	@RequestMapping(value = "/workorderSearch", params = "workorderSearch", method = RequestMethod.POST)
	public String searchWorkData(@ModelAttribute("workOrderSearchDetails") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {

		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		if (workOrderAgreement.getDepartment() != null && workOrderAgreement.getDepartment() != ""
				&& !workOrderAgreement.getDepartment().isEmpty()) {
			workOrderAgreement.setExecuting_department(workOrderAgreement.getDepartment());
		}

		List<WorkOrderAgreement> workDetails = searchEstimateService.searchWorkData(request, workOrderAgreement);
		Double sum = 0d;
		Double estimateCost = 0d;
		for (int i = 0; i < workDetails.size(); i++) {
			if(workDetails.get(i).getNewBoQDetailsList() != null) {
			sum += workDetails.get(i).getNewBoQDetailsList().get(i).getMeasured_amount();
			estimateCost = Double.parseDouble(workDetails.get(i).getEstimatedCost());
			}

		}
		

		workList.addAll(workDetails);
		workOrderAgreement.setWorkOrderList(workList);
		workOrderAgreement.setProgressCompletion(sum / estimateCost * 100);
		model.addAttribute("workOrderSearchDetails", workOrderAgreement);

		return "work-order-search-form";

	}

	public List<Department> getDepartmentsFromMs() {
		List<Department> departments = microserviceUtils.getDepartments();
		return departments;
	}
}
