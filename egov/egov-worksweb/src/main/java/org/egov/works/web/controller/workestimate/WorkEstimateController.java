package org.egov.works.web.controller.workestimate;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.workestimate.service.WorkEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/workEstimate")
public class WorkEstimateController {

	@Autowired
	WorkEstimateService workEstimateService;
	
	@Autowired
	public MicroserviceUtils microserviceUtils;

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

		/*// Convert input string into a date
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fromdate = inputFormat.parse(estimatePreparationApproval.getFromDate());
		estimatePreparationApproval.setFromDt(fromdate);

		Date todate = inputFormat.parse(estimatePreparationApproval.getToDate());
		estimatePreparationApproval.setToDt(todate);
*/
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
				
				/*List<EstimatePreparationApproval> workEstimateDetails = workEstimateService.searchBoqData(request,
						estimatePreparationApproval.getEstimateList().get(i).getId());
				
				for(int j = 0; j<workEstimateDetails.size();j++ ) {
					responseList= workEstimateDetails.get(j).getNewBoQDetailsList();
				}*/	
				estimatePreparationApproval.setBoQDetailsList(workEstimateDetails.getNewBoQDetailsList());
				model.addAttribute("workEstimateDetails", estimatePreparationApproval);

			}
		}

		return "search-work-estimate-form";

	}
	
	private List<Department> getDepartmentsFromMs() {
        List<Department> departments = microserviceUtils.getDepartments();
        return departments;
    }

}
