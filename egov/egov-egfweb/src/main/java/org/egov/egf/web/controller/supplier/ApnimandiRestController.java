package org.egov.egf.web.controller.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.egov.apnimandi.reports.entity.ApnimandiCollectionMISReport;
import org.egov.apnimandi.reports.entity.ApnimandiContractorSearchResult;
import org.egov.apnimandi.transactions.entity.ApnimandiContractor;
import org.egov.apnimandi.transactions.service.ApnimandiCollectionDetailService;
import org.egov.apnimandi.transactions.service.ContractorsService;
import org.egov.commons.CFinancialYear;
import org.egov.commons.Fund;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.Statement;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.utils.DateUtils;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/apnimandiapi/")
public class ApnimandiRestController {
	public static final String SUCCESS = "Success";
	
	@Autowired 
	private ContractorsService contractorsService;
	
	@Autowired 
	private ApnimandiCollectionDetailService apnimandiCollectionDetailService;
	
	private HttpHeaders headers = new HttpHeaders();
	private   List<String> allowheaderList= new ArrayList<String>();
	private final String headername="Content-Security-Policy";
	private final String headervalue="default-src 'self' https://egov.chandigarhsmartcity.in https://egov-dev.chandigarhsmartcity.in https://egov-uat.chandigarhsmartcity.in https://mcc.chandigarhsmartcity.in https://chandigarh-dev.chandigarhsmartcity.in https://chandigarh-uat.chandigarhsmartcity.in";
	
	
	@ResponseBody
	@RequestMapping(value = "getAllApniMandiContractorList", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllContractorList(HttpServletRequest req ){
		
			 ApnimandiContractor apnimandiContractor=new ApnimandiContractor();
			 apnimandiContractor.setZone(null);
			 apnimandiContractor.setValidFromDate(null);
			 apnimandiContractor.setValidToDate(null);
			/// apnimandiContractor.setName();
		 final List<ApnimandiContractorSearchResult> searchResultList = contractorsService.dmContractorReport(apnimandiContractor);
			 if(null!=searchResultList) {
				 
				 List<ApniMandiContractorApiData> finalData = new ArrayList<ApniMandiContractorApiData>();
				 ModelMap m = new ModelMap();
				 m.addAttribute("list_type", "Apni Mandi Contrator List");
				 
				 searchResultList.stream().forEach(a->{
					 ApniMandiContractorApiData ap = new ApniMandiContractorApiData();
					 ap.setAddress(a.getApnimandiContractor().getAddress());
					 ap.setContractSignedOn(a.getApnimandiContractor().getContractSignedOn().toString());
					 ap.setValidFromDate(a.getApnimandiContractor().getValidFromDate().toString());
					 ap.setValidToDate(a.getApnimandiContractor().getValidToDate().toString());
					 ap.setName(a.getApnimandiContractor().getSalutation()+" "+a.getApnimandiContractor().getName());
					 ap.setStatus(a.getStatusName());
					 ap.setZonename(a.getZoneName());
					 finalData.add(ap);
					 
				 });
				 m.addAttribute("data", finalData);
				 
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(m).build(),getHeaders(),  HttpStatus.OK);
			 }
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("no data").build(),getHeaders(),  HttpStatus.OK);
			 
	}
	
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllApniMandiDayMarketCollection", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllApniMandiDayMarketCollection(@RequestParam final String fromDate,
			   @RequestParam final String toDate,HttpServletRequest req ){
		
		Date newFromDate=null;
		Date newToDate=null;	
		if(StringUtils.isNotEmpty(fromDate)) {
			newFromDate = DateUtils.getDate(fromDate, "dd/MM/yyyy");
		}		
		if(StringUtils.isNotEmpty(toDate)) {
			newToDate = DateUtils.getDate(toDate, "dd/MM/yyyy");
		}else {
			newToDate = DateUtils.today();
		}	
		if(null==newFromDate && null==newToDate) {
			
			 return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Parameters").build(),getHeaders(),  HttpStatus.OK);
	       
		}
		List<ApnimandiCollectionMISReport> searchResultList=null;
		searchResultList= apnimandiCollectionDetailService.dmCollectionReport(newFromDate, newToDate);
		if(null!=searchResultList) {
			ModelMap m = new ModelMap();
			m.addAttribute("list_type", "Apni Mandi Day Market Collection");
			m.addAttribute("data",searchResultList );
			
			 return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody(m).build(),getHeaders(),  HttpStatus.OK);
		}
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("no data").build(),getHeaders(),  HttpStatus.OK);
			 
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllApniMandiCollection", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllApniMandiCollection(@RequestParam final String fromDate,
			   @RequestParam final String toDate,HttpServletRequest req ){
		
		Date newFromDate=null;
		Date newToDate=null;	
		if(StringUtils.isNotEmpty(fromDate)) {
			newFromDate = DateUtils.getDate(fromDate, "dd/MM/yyyy");
		}		
		if(StringUtils.isNotEmpty(toDate)) {
			newToDate = DateUtils.getDate(toDate, "dd/MM/yyyy");
		}else {
			newToDate = DateUtils.today();
		}	
		if(null==newFromDate && null==newToDate) {
			
			 return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Parameters").build(),getHeaders(),  HttpStatus.OK);
	       
		}
		List<ApnimandiCollectionMISReport> searchResultList=null;
		searchResultList= apnimandiCollectionDetailService.amCollectionReport(newFromDate, newToDate);
		if(null!=searchResultList) {
			ModelMap m = new ModelMap();
			m.addAttribute("list_type", "Apni Mandi Collection");
			m.addAttribute("data",searchResultList );
			
			 return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody(m).build(),getHeaders(),  HttpStatus.OK);
		}
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("no data").build(),getHeaders(),  HttpStatus.OK);
			 
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
		allowheaderList.add("http://localhost:3010");
		headers.set(headername, headervalue);
		headers.setAccessControlAllowHeaders(allowheaderList);
		return headers;
		
	}

}
