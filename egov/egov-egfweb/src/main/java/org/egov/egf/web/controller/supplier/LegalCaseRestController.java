package org.egov.egf.web.controller.supplier;

import java.util.ArrayList;
import java.util.List;

import org.egov.lcms.reports.entity.LegalCaseSearchResult;
import org.egov.lcms.transactions.service.SearchLegalCaseService;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/legalcase/")
public class LegalCaseRestController {


	@Autowired
    private SearchLegalCaseService searchLegalCaseService;
	public static final String SUCCESS = "Success";
	
	private   List<String> allowheaderList= new ArrayList<String>(); 
	private HttpHeaders headers = new HttpHeaders();
	private final String headername="Content-Security-Policy";
	private final String headervalue="default-src 'self' https://egov.chandigarhsmartcity.in https://egov-dev.chandigarhsmartcity.in https://egov-uat.chandigarhsmartcity.in https://mcc.chandigarhsmartcity.in https://chandigarh-dev.chandigarhsmartcity.in https://chandigarh-uat.chandigarhsmartcity.in";
	
	@ResponseBody
	@RequestMapping(value = "getLegalCase", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getLegalCases( @ModelAttribute final LegalCaseSearchResult legalCaseSearchResult){
		System.out.println("Inside Legal Case Rest Controleler");
		 List<LegalCaseSearchResult> legalcaseSearchList =null;
		
		try {
			legalcaseSearchList =searchLegalCaseService.getLegalCaseReportRestData(legalCaseSearchResult);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		List<LegalCaseSearchResult> legal = new ArrayList<LegalCaseSearchResult>();
		for(LegalCaseSearchResult l:legalcaseSearchList) {
			LegalCaseSearchResult lg = new LegalCaseSearchResult();
			lg.setLcNumber(l.getLegalCase().getLcNumber());
			lg.setCaseNumber(l.getLegalCase().getCaseNumber());
			lg.setCaseTitle(l.getLegalCase().getCaseTitle());
			lg.setCourtName(l.getCourtName());
			lg.setPetName(l.getLegalCase().getPetitionersNames());
			lg.setPetitionType(l.getLegalCase().getPetitionTypeMaster().getPetitionType());
			lg.setResName( l.getLegalCase().getRespondantNames());
			lg.setAssignDept(l.getAssignDept());
			lg.setStandingCouncil(l.getLegalCase().getOppPartyAdvocate());
			lg.setCaseStatus(l.getCaseStatus());
			lg.setStatusDesc(l.getLegalCase().getStatus().getDescription());
			lg.setLegalViewAccess( l.getLegalViewAccess());
			lg.setConcernedBranch(l.getConcernedBranch());
			lg.setCaseFromDate(l.getCaseFromDate());
			lg.setCaseToDate(l.getCaseToDate());
			lg.setCaseType(l.getLegalCase().getCaseTypeMaster().getCaseType());
			lg.setCourtId(Integer.parseInt(l.getLegalCase().getCourtMaster().getId().toString()));
			lg.setCourtType(Integer.parseInt(l.getLegalCase().getCourtMaster().getCourtType().getId().toString()));
			lg.setLegalCase(l.getLegalCase());
			lg.setHearingDate(l.getHearingDate());
			legal.add(lg);
			
			
			
		}
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(legal).build(), getHeaders(),HttpStatus.OK);
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
