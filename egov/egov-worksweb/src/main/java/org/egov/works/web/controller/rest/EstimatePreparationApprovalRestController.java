package org.egov.works.web.controller.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApprovalRESTPOJO;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.workestimate.service.WorkDnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/*@EnableWebMvc*/
@RestController
public class EstimatePreparationApprovalRestController {
	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;
	@Autowired
	WorkDnitService workDnitService;
	@Autowired
	BoQDetailsService boQDetailsService;
	
	@ResponseBody
	@RequestMapping(value = "/rest/dashboard/getAllEstimationPreparation", method = RequestMethod.POST)
	public ModelMap getAllEstimationPreparationPOJO(HttpServletRequest req,HttpServletResponse res) {
			System.out.println("TESTING");
		  List<EstimatePreparationApprovalRESTPOJO> fetchedData =estimatePreparationApprovalService.getAllEstimationPreparationNative();
		  ModelMap m = new ModelMap(); 
		  m.put("allestimation", fetchedData);
		 
		return m;
	}
	@ResponseBody
	@RequestMapping(value = "/rest/dashboard/getAllDnit", method = RequestMethod.POST)
	public ModelMap getAllDnit(){
		ModelMap m = new ModelMap();
		m.put("allestimation", workDnitService.getAllDnitList());
		return m;
	}
	
	@ResponseBody
	@RequestMapping(value = "/rest/dashboard/getAllWorkAgreement", method = RequestMethod.POST)
	public ModelMap getAllWorkAgreement(){
		ModelMap m = new ModelMap();
		m.put("allestimation", boQDetailsService.getAllWorkOrderAgreementRest());
		return m;
	}
	
	

}
