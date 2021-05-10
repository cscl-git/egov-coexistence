package org.egov.egf.web.controller.supplier;

import java.util.ArrayList;
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
import org.springframework.http.HttpHeaders;
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
	private   List<String> allowheaderList= new ArrayList<String>(); 
	private HttpHeaders headers = new HttpHeaders();
	
	private final String headername="Content-Security-Policy";
	private final String headervalue="default-src 'self' https://egov.chandigarhsmartcity.in https://egov-dev.chandigarhsmartcity.in https://egov-uat.chandigarhsmartcity.in https://mcc.chandigarhsmartcity.in https://chandigarh-dev.chandigarhsmartcity.in https://chandigarh-uat.chandigarhsmartcity.in";
	
	
	public static final String SUCCESS = "Success";
	@GetMapping(value = "/_get")
	public ModelMap getData() {
		ModelMap m = new ModelMap();
		m.addAttribute("Test", "Hello");
		return  m;
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllEstimationPreparation", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper> getAllEstimationPreparationPOJO(HttpServletRequest req,HttpServletResponse res) {
			System.out.println("TESTING");
		  List<EstimatePreparationApprovalRESTPOJO> fetchedData =estimatePreparationApprovalService.getAllEstimationPreparationNative();
		  
		  return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
					.responseBody(fetchedData).build(),getHeaders(), HttpStatus.OK);
	}
	@ResponseBody
	@RequestMapping(value = "getAllDnit", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllDnit(){
		ModelMap m = new ModelMap();
		m.put("allestimation", workDnitService.getAllDnitList());
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(workDnitService.getAllDnitList()).build(),getHeaders(), HttpStatus.OK);
	}
	
	/*
	 * @ResponseBody
	 * 
	 * @RequestMapping(value = "getAllWorkAgreement", method = RequestMethod.GET)
	 * public ResponseEntity<ResponseInfoWrapper> getAllWorkAgreement(){ ModelMap m
	 * = new ModelMap();
	 * 
	 * return new ResponseEntity<>(ResponseInfoWrapper.builder()
	 * .responseInfo(ResponseInfo.builder().status(SUCCESS).build())
	 * .responseBody(boQDetailsService.getAllWorkOrderAgreementRest()).build(),
	 * HttpStatus.OK); }
	 */
	
	@ResponseBody
	@RequestMapping(value = "getAllWorkAgreementByMilestone", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllWorkOrderAgreementRestByMileStone(){
		ModelMap m = new ModelMap();
		
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(boQDetailsService.getAllWorkOrderAgreementRestByMileStone()).build(),getHeaders(), HttpStatus.OK);
	}
	public HttpHeaders getHeaders() {
		
		return headers = updateHeaders(headers);
	}



	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}
	
	public HttpHeaders updateHeaders(HttpHeaders headers) {
		
		allowheaderList.clear();
		allowheaderList.add("https://egov.chandigarhsmartcity.in");
		allowheaderList.add("https://egov-dev.chandigarhsmartcity.in");
		allowheaderList.add("https://egov-uat.chandigarhsmartcity.in");
		allowheaderList.add("https://mcc.chandigarhsmartcity.in");
		allowheaderList.add("https://chandigarh-dev.chandigarhsmartcity.in");
		allowheaderList.add("https://chandigarh-uat.chandigarhsmartcity.in");
		headers.set(headername, headervalue);
		headers.setAccessControlAllowHeaders(allowheaderList);
		return headers;
		
	}
	
	
}
