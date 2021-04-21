package org.egov.egf.web.controller.supplier;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApprovalRESTPOJO;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.workestimate.service.WorkDnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard/")
public class EstimateApprovalRestTestController {
	
	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;
	@Autowired
	WorkDnitService workDnitService;
	@Autowired
	BoQDetailsService boQDetailsService;
	
	public static final String SUCCESS = "Success";
	@GetMapping(value = "/_get")
	public ModelMap getData() {
		ModelMap m = new ModelMap();
		m.addAttribute("Test", "Hello");
		return  m;
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllEstimationPreparation", method = RequestMethod.GET)
	@CrossOrigin(origins = "https://egov-uat.chandigarhsmartcity.in")
	public ResponseEntity<ResponseInfoWrapper> getAllEstimationPreparationPOJO(HttpServletRequest req,HttpServletResponse res) {
			System.out.println("TESTING");
		  List<EstimatePreparationApprovalRESTPOJO> fetchedData =estimatePreparationApprovalService.getAllEstimationPreparationNative();
		  
		  return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
					.responseBody(fetchedData).build(), HttpStatus.OK);
	}
	@ResponseBody
	@RequestMapping(value = "getAllDnit", method = RequestMethod.GET)
	@CrossOrigin(origins = "https://egov-uat.chandigarhsmartcity.in")
	public ResponseEntity<ResponseInfoWrapper>  getAllDnit(){
		ModelMap m = new ModelMap();
		m.put("allestimation", workDnitService.getAllDnitList());
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(workDnitService.getAllDnitList()).build(), HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "getAllWorkAgreement", method = RequestMethod.GET)
	@CrossOrigin(origins = "https://egov-uat.chandigarhsmartcity.in")
	public ResponseEntity<ResponseInfoWrapper>  getAllWorkAgreement(){
		ModelMap m = new ModelMap();
		
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(boQDetailsService.getAllWorkOrderAgreementRestByMileStone()).build(), HttpStatus.OK);
	}
	
}
