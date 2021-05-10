package org.egov.egf.web.controller.supplier;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.entity.AuditChecklistHistory;
import org.egov.audit.entity.AuditDetails;
import org.egov.audit.model.AuditDetail;
import org.egov.audit.model.AuditRestDataPOJO;
import org.egov.audit.service.AuditService;
import org.egov.audit.utils.AuditUtils;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@RequestMapping("/auditrest/")
public class AuditRestDataController {
	public static final String SUCCESS = "Success";
	private static final Logger LOGGER = Logger.getLogger(AuditRestDataController.class);
	@Autowired
	private AuditUtils auditUtils;
	
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	
	@Autowired
	 private  DepartmentService departmentService;
	@Autowired
	private AuditService auditService;
	
	private   List<String> allowheaderList= new ArrayList<String>(); 
	private HttpHeaders headers = new HttpHeaders();
	private final String headername="Content-Security-Policy";
	private final String headervalue="default-src 'self' https://egov.chandigarhsmartcity.in https://egov-dev.chandigarhsmartcity.in https://egov-uat.chandigarhsmartcity.in https://mcc.chandigarhsmartcity.in https://chandigarh-dev.chandigarhsmartcity.in https://chandigarh-uat.chandigarhsmartcity.in";
	
	
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(value = "getAllAuditByType", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllAuditByType(@ModelAttribute("auditDetail") final AuditDetail auditDetail){
	
		final StringBuffer query = new StringBuffer(500);
	
		 List<AuditDetails> auditDetailsList= new ArrayList<>();
		 List<AuditRestDataPOJO> responseData =  new ArrayList<>();
       query
       .append(
               " From AuditDetails ad where ad.type = ? ")
               .append(auditUtils.getAuditDateQuery(auditDetail.getBillFrom(), auditDetail.getBillTo()))
                               .append(auditUtils.getAuditMisQuery(auditDetail));
       LOGGER.info("Query :: "+query.toString());
       auditDetailsList = (List<AuditDetails> )persistenceService.findAllBy(query.toString(),auditDetail.getAuditType());
       auditDetailsList.stream().forEach(audit->{
    	   AuditRestDataPOJO  a=  new AuditRestDataPOJO(); 
    	   List<AuditCheckList> ac= new ArrayList<AuditCheckList>();
    	   a.setId(audit.getId());
    	   a.setAudit_status(audit.getStatus().getCode());
    	   a.setStatusDescription(audit.getStatusDescription());
    	   a.setAudit_schedule_date(audit.getAudit_sch_date());
    	   a.setAudit_type(audit.getType());
    	   a.setAudit_comp_date(audit.getAudit_comp_date());
    	   a.setState(audit.getState().getValue());
    	   a.setAudit_no(audit.getAuditno());
    	   a.setPassUnderobjection(audit.getPassUnderobjection());
    	   a.setBillid(audit.getEgBillregister().getId().toString());
    	   a.setDepartment( departmentService.getDepartmentById(Long.parseLong(audit.getDepartment())).getName());
    	   if(audit.getCheckList().size()>0) {
    		   audit.getCheckList().stream().forEach(ck->{
    			   AuditCheckList obj = new AuditCheckList(ck);
    			  List<AuditChecklistHistory> AuditChecklistHistoryList = new ArrayList<AuditChecklistHistory>();
    			  if(ck.getCheckList_history().size()>0) {
    				 
    				  ck.getCheckList_history().stream().forEach(hs->{
    					  AuditChecklistHistory ach = new AuditChecklistHistory(hs);
    					  AuditChecklistHistoryList.add(ach);
    				  });
    				  
    			  }
    			  obj.setCheckList_history(AuditChecklistHistoryList);  
    			  ac.add(obj);
    		});
    		   
    	   }
    	   a.setAuditCheckList(ac);
    	   responseData.add(a);
       });
         
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(responseData).build(),getHeaders(), HttpStatus.OK);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "getAllAudit", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllAudit(){
	
	
		 List<AuditDetails> auditDetailsList= new ArrayList<>();
		 List<AuditRestDataPOJO> responseData =  new ArrayList<>();
       auditDetailsList = (List<AuditDetails> )auditService.getAllAudit();
       
       auditDetailsList.stream().forEach(audit->{
    	   AuditRestDataPOJO  a=  new AuditRestDataPOJO(); 
    	   List<AuditCheckList> ac= new ArrayList<AuditCheckList>();
    	   a.setId(audit.getId());
    	   a.setAudit_status(audit.getStatus().getCode());
    	   a.setStatusDescription(audit.getStatusDescription());
    	   a.setAudit_schedule_date(audit.getAudit_sch_date());
    	   a.setAudit_type(audit.getType());
    	   a.setAudit_comp_date(audit.getAudit_comp_date());
    	   a.setState(audit.getState().getValue());
    	   a.setAudit_no(audit.getAuditno());
    	   a.setPassUnderobjection(audit.getPassUnderobjection());
    	   a.setBillid((null!=audit.getEgBillregister())?audit.getEgBillregister().getId().toString():null);
    	   a.setDepartment( departmentService.getDepartmentById(Long.parseLong(audit.getDepartment())).getName());
    	   if(audit.getCheckList().size()>0) {
    		   audit.getCheckList().stream().forEach(ck->{
    			   AuditCheckList obj = new AuditCheckList(ck);
    			  List<AuditChecklistHistory> AuditChecklistHistoryList = new ArrayList<AuditChecklistHistory>();
    			  if(ck.getCheckList_history().size()>0) {
    				 
    				  ck.getCheckList_history().stream().forEach(hs->{
    					  AuditChecklistHistory ach = new AuditChecklistHistory(hs);
    					  AuditChecklistHistoryList.add(ach);
    				  });
    				  
    			  }
    			  obj.setCheckList_history(AuditChecklistHistoryList);  
    			  ac.add(obj);
    		});
    		   
    	   }
    	   a.setAuditCheckList(ac);
    	   responseData.add(a);
       });
         
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(responseData).build(),getHeaders(), HttpStatus.OK);
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
