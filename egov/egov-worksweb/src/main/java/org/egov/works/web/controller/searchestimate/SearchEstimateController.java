package org.egov.works.web.controller.searchestimate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
	  SearchEstimateService searchEstimateService;
	 

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("estimateSearchDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		return "estimate-detail-search-form";
	}

	@RequestMapping(value = "/newformone", method = RequestMethod.POST)
	public String showNewFormGetwork(
			@ModelAttribute("workOrderSearchDetails") final WorkOrderAgreement workOrderAgreement, final Model model,
			HttpServletRequest request) {

		return "work-order-search-form";
	}

	
	@RequestMapping(value = "/estimateSearch", params = "estimateSearch", method = RequestMethod.POST)
	public String searchEstimateData(
			@ModelAttribute("estimateSearchDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		/*List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		// Convert input string into a date
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fromdate = inputFormat.parse(estimatePreparationApproval.getFromDate());
		estimatePreparationApproval.setFromDt(fromdate);

		Date todate = inputFormat.parse(estimatePreparationApproval.getToDate());
		estimatePreparationApproval.setToDt(todate);*/

		List<EstimatePreparationApproval> workEstimateDetails = searchEstimateService.searchEstimateData(request,
				estimatePreparationApproval);
		/*approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);*/

		return "estimate-detail-search-form";

	}
	
	@RequestMapping(value = "/workEstimateSearch", params = "workEstimateSearch", method = RequestMethod.POST)
	public String searchWorkData(
			@ModelAttribute("workEstimateDetails") final WorkOrderAgreement workOrderAgreement,
			final Model model, final HttpServletRequest request) throws Exception {
		/*List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		// Convert input string into a date
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fromdate = inputFormat.parse(estimatePreparationApproval.getFromDate());
		estimatePreparationApproval.setFromDt(fromdate);

		Date todate = inputFormat.parse(estimatePreparationApproval.getToDate());
		estimatePreparationApproval.setToDt(todate);*/

		List<WorkOrderAgreement> workEstimateDetails = searchEstimateService.searchWorkData(request,
				workOrderAgreement);
		/*approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);*/

		return "estimate-detail-search-form";

	}
}
