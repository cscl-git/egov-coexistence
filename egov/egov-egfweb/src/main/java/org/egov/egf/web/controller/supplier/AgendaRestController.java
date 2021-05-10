package org.egov.egf.web.controller.supplier;

import static org.egov.council.utils.constants.CouncilConstants.ATTENDANCEFINALIZED;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGUSEDINRMOM;
import static org.egov.council.utils.constants.CouncilConstants.MOM_FINALISED;
import static org.egov.council.utils.constants.CouncilConstants.PREAMBLEUSEDINAGENDA;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.egov.council.entity.AgendaRestDataView;
import org.egov.council.entity.CommitteeMembers;
import org.egov.council.entity.CouncilAgenda;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.entity.CouncilMemberStatus;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.entity.MeetingAttendence;
import org.egov.council.entity.MeetingMOM;
import org.egov.council.entity.MeetingMoMRestDataView;
import org.egov.council.entity.MeetingRestDataView;
import org.egov.council.service.CouncilAgendaService;
import org.egov.council.service.CouncilMeetingService;
import org.egov.council.service.CouncilPreambleService;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.utils.StringUtils;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agenda/")
public class AgendaRestController {
	public static final String SUCCESS = "Success";
	
	
	 @Autowired
	 private CouncilPreambleService councilPreambleService;
	 
	 @Autowired
	 protected CouncilAgendaService councilAgendaService;
	 
	 @Autowired
	 private  DepartmentService departmentService;
	 @Autowired
	 private CouncilMeetingService councilMeetingService;
	
	 private   List<String> allowheaderList= new ArrayList<String>(); 
	  private HttpHeaders headers = new HttpHeaders();
	  private final String headername="Content-Security-Policy";
	  private final String headervalue="default-src 'self' https://egov.chandigarhsmartcity.in https://egov-dev.chandigarhsmartcity.in https://egov-uat.chandigarhsmartcity.in https://mcc.chandigarhsmartcity.in https://chandigarh-dev.chandigarhsmartcity.in https://chandigarh-uat.chandigarhsmartcity.in";
		
	 
	@RequestMapping("getAllAgenda")
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllAgenda( @ModelAttribute final CouncilPreamble councilPreamble){
		 List<CouncilPreamble> searchResultList = new ArrayList<CouncilPreamble>();
		 List<AgendaRestDataView> agendaRestDataViewsList=new ArrayList<AgendaRestDataView>();
		 try {
		 searchResultList = councilPreambleService.search(councilPreamble);
		

		 searchResultList.stream().forEach(council->{
			 AgendaRestDataView a= new AgendaRestDataView();
		    	String meetingDate = StringUtils.EMPTY;
				String meetingType = StringUtils.EMPTY;
				String committeeType = StringUtils.EMPTY;
				String agendaType =  StringUtils.EMPTY;
				List<CouncilAgenda> councilAgendaList=new ArrayList<CouncilAgenda>();
				
		    	if(null!=council.getDepartment()) {
		    		council.setDepartment(departmentService.getDepartmentById(Long.parseLong(council.getDepartment())).getName());
				}
		    	if(CouncilConstants.PREAMBLEUSEDINAGENDA.equalsIgnoreCase(council.getStatus().getCode())) {
		    		 councilAgendaList = councilAgendaService.findByAgendaNo(council.getPreambleNumber());
		        	if(!CollectionUtils.isEmpty(councilAgendaList)) {
		        		council.setStatus(councilAgendaList.get(0).getStatus());
		        	}
		    	}
		    	a.setWard(council.getWards().stream().map(Boundary::getName).collect(Collectors.joining(",")));
		    	a.setId(Integer.parseInt(council.getId().toString()));
		    	a.setCreatedDate(council.getCreatedDate().toString());
		    	a.setDepartment(council.getDepartment());
		    	a.setGistOfPreamble(StringEscapeUtils.escapeJava(council.getGistOfPreamble().replaceAll("[\n\r]", "")));
		    	
		    	for (MeetingMOM meetingMOM : council.getMeetingMOMs()) {
					meetingDate = meetingMOM.getMeeting().getMeetingDate().toString();
					meetingType = meetingMOM.getMeeting().getCommitteeType().getName();
				}
		    	a.setMeetingDate(meetingDate);
		    	a.setMeetingType(meetingType);
		    	a.setPreambleNumber(council.getPreambleNumber());
		    	a.setPreambleType(council.getType().toString());
		    	a.setPreambleUsedInAgenda(PREAMBLEUSEDINAGENDA.equals(council.getStatus().getCode()) ? "Yes" : "No");
		    	a.setSanctionAmount((null!=council.getSanctionAmount())? council.getSanctionAmount().toString():null);
		    	a.setStatus(council.getStatus().getCode());
		    	a.setStatusDesc(council.getStatus().getDescription());
		    	
		    	
		    	CouncilAgenda agenda = councilAgendaService.findByPreambleId(council.getId()).getAgenda();
	        	if(null != agenda) {
		        	council.setCommitteeType(agenda.getCommitteeType());
		        	council.setCouncilAgendaType(agenda.getCouncilAgendaType());
	        	}
		    
		    		committeeType = (null!=council.getCommitteeType())?council.getCommitteeType().getName():"N/A";
		    		agendaType = (null!=council.getCouncilAgendaType())?council.getCouncilAgendaType().getName():"N/A";

	       
		    	
		    	
		    	a.setCommitteeType(committeeType);
		    	a.setAgendatype(agendaType);
		    	a.setDocument_name(council.getFilestoreid().getFileName());
		    
		    	agendaRestDataViewsList.add(a);
	    	});
		 
		 
		 }catch(Exception ex) {
			 
		 }
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(agendaRestDataViewsList).build(),getHeaders(), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(path= {"getAllMeeting"})
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllMeetings( @ModelAttribute final CouncilMeeting councilMeetingobj){
		List<CouncilMeeting> searchResultList = new ArrayList<CouncilMeeting>();
		List<MeetingRestDataView>meetingRestDataViewList=new ArrayList<MeetingRestDataView>();
		 try {
		 searchResultList = councilMeetingService.searchMeeting(councilMeetingobj);
		

		 searchResultList.stream().forEach(councilMeeting->{
			 
			 MeetingRestDataView m = new MeetingRestDataView();
			 int noOfMembersPresent = 0;
	         int noOfMembersAbsent = 0;
	         int totCommitteMemCount = 0;
			 
	         List<Long> committeeMembersId=new ArrayList<>();
	         if (ATTENDANCEFINALIZED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                 || MOM_FINALISED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                 || MEETINGUSEDINRMOM.equalsIgnoreCase(councilMeeting.getStatus().getCode())) {
	             totCommitteMemCount = councilMeeting.getMeetingAttendence().size();
	           
	         } else if (councilMeeting.getMeetingAttendence() != null) {
	             for (CommitteeMembers committeeMembers : councilMeeting.getCommitteeType().getCommiteemembers()) {
	                 if (CouncilMemberStatus.ACTIVE.equals(committeeMembers.getCouncilMember().getStatus())) {
	                     totCommitteMemCount++;
	                     committeeMembersId.add(committeeMembers.getCouncilMember().getId());
	                 }
	             }
	            
	         }

	         if (councilMeeting.getMeetingAttendence() != null) {
	             for (MeetingAttendence attendence : councilMeeting.getMeetingAttendence()) {
	                 if (attendence.getAttendedMeeting()) {
	                     if (ATTENDANCEFINALIZED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                             || MOM_FINALISED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                             || MEETINGUSEDINRMOM.equalsIgnoreCase(councilMeeting.getStatus().getCode())) {
	                         noOfMembersPresent++;
	                     }else if(committeeMembersId.indexOf(attendence.getCouncilMember().getId())>-1){
	                         noOfMembersPresent++;
	                     }
	                 }
	             }
	         }
	         noOfMembersAbsent = totCommitteMemCount - noOfMembersPresent;
	         m.setId(councilMeeting.getId());
	         m.setMeetingNumber(councilMeeting.getMeetingNumber());
	         m.setMeetingCreatedDate(councilMeeting.getCreatedDate().toString());
	         m.setMeetingDate(councilMeeting.getMeetingDate().toString());
	         m.setMeetingLocation(councilMeeting.getMeetingLocation());
	         m.setMeetingTime(councilMeeting.getMeetingTime().toString());
	         m.setMeetingStatus(councilMeeting.getStatus().getCode());
	         m.setNoOfMembersAbsent(noOfMembersAbsent);
	         m.setNoOfMembersPresent(noOfMembersPresent);
	         m.setTotCommitteMemCount(totCommitteMemCount);
	         m.setMeetingType(councilMeeting.getMeetingType().getName());
	         meetingRestDataViewList.add(m);
	         
		 });
		
		 }catch(Exception ex) {
			 
		 }
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(meetingRestDataViewList).build(),getHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(path= {"getAllMom"})
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllMoM( @ModelAttribute final CouncilMeeting councilMeetingobj){
		List<CouncilMeeting> searchResultList = new ArrayList<CouncilMeeting>();
		List<MeetingRestDataView>meetingRestDataViewList=new ArrayList<MeetingRestDataView>();
		 try {
		 searchResultList = councilMeetingService.searchMeeting(councilMeetingobj);
		

		 searchResultList.stream().forEach(councilMeeting->{
			 
			 MeetingRestDataView m = new MeetingRestDataView();
			 int noOfMembersPresent = 0;
	         int noOfMembersAbsent = 0;
	         int totCommitteMemCount = 0;
			 
	         List<Long> committeeMembersId=new ArrayList<>();
	         if (ATTENDANCEFINALIZED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                 || MOM_FINALISED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                 || MEETINGUSEDINRMOM.equalsIgnoreCase(councilMeeting.getStatus().getCode())) {
	             totCommitteMemCount = councilMeeting.getMeetingAttendence().size();
	           
	         } else if (councilMeeting.getMeetingAttendence() != null) {
	             for (CommitteeMembers committeeMembers : councilMeeting.getCommitteeType().getCommiteemembers()) {
	                 if (CouncilMemberStatus.ACTIVE.equals(committeeMembers.getCouncilMember().getStatus())) {
	                     totCommitteMemCount++;
	                     committeeMembersId.add(committeeMembers.getCouncilMember().getId());
	                 }
	             }
	            
	         }

	         if (councilMeeting.getMeetingAttendence() != null) {
	             for (MeetingAttendence attendence : councilMeeting.getMeetingAttendence()) {
	                 if (attendence.getAttendedMeeting()) {
	                     if (ATTENDANCEFINALIZED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                             || MOM_FINALISED.equalsIgnoreCase(councilMeeting.getStatus().getCode())
	                             || MEETINGUSEDINRMOM.equalsIgnoreCase(councilMeeting.getStatus().getCode())) {
	                         noOfMembersPresent++;
	                     }else if(committeeMembersId.indexOf(attendence.getCouncilMember().getId())>-1){
	                         noOfMembersPresent++;
	                     }
	                 }
	             }
	         }
	         noOfMembersAbsent = totCommitteMemCount - noOfMembersPresent;
	         m.setId(councilMeeting.getId());
	         m.setMeetingNumber(councilMeeting.getMeetingNumber());
	         m.setMeetingCreatedDate(councilMeeting.getCreatedDate().toString());
	         m.setMeetingDate(councilMeeting.getMeetingDate().toString());
	         m.setMeetingLocation(councilMeeting.getMeetingLocation());
	         m.setMeetingTime(councilMeeting.getMeetingTime().toString());
	         m.setMeetingStatus(councilMeeting.getStatus().getCode());
	         m.setNoOfMembersAbsent(noOfMembersAbsent);
	         m.setNoOfMembersPresent(noOfMembersPresent);
	         m.setTotCommitteMemCount(totCommitteMemCount);
	         m.setMeetingType(councilMeeting.getMeetingType().getName());
	         meetingRestDataViewList.add(m);
	         
		 });
		
		 }catch(Exception ex) {
			 
		 }
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(meetingRestDataViewList).build(),getHeaders(), HttpStatus.OK);
	}
	
	
	@RequestMapping(path= {"getAllMom2"})
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllMoM2( @ModelAttribute final CouncilMeeting councilMeetingobj){
		List<MeetingMOM> searchResultList = new ArrayList<MeetingMOM>();
		List<MeetingMoMRestDataView>meetingRestDataViewList=new ArrayList<MeetingMoMRestDataView>();
		
		 try {
		 searchResultList = councilMeetingService.getAllMoM();
		System.out.println(searchResultList.size());

		 searchResultList.stream().forEach(mom->{
			 MeetingMoMRestDataView m =new MeetingMoMRestDataView();
			 m.setId(mom.getId());
			 m.setCouncilAgenda((null!=mom.getAgenda())?mom.getAgenda().getAgendaNumber():null);
			 m.setCouncilMeeting((null!=mom.getMeeting())?mom.getMeeting().getMeetingNumber():null);
			 m.setCouncilPreamble((null!=mom.getPreamble())?mom.getPreamble().getGistOfPreamble():null);
			 m.setResolutionStatus((null!=mom.getResolutionStatus())?mom.getResolutionStatus().getCode():null);
			 m.setResolutionNumber((null!=mom.getResolutionNumber())?mom.getResolutionNumber():null);
			 m.setResolutionDetail((null!=mom.getResolutionDetail())?mom.getResolutionDetail():null);
			 m.setLegacy((mom.isLegacy())?mom.isLegacy():false);
			 meetingRestDataViewList.add(m);
			 
	         
		 });
		
		 }catch(Exception ex) {
			 
		 }
		return new ResponseEntity<>(ResponseInfoWrapper.builder()
				.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
				.responseBody(meetingRestDataViewList).build(),getHeaders(), HttpStatus.OK);
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
		allowheaderList.add("https://egov-dev.chandigarhsmartcity.in ");
		allowheaderList.add("https://egov-uat.chandigarhsmartcity.in");
		allowheaderList.add("https://mcc.chandigarhsmartcity.in");
		allowheaderList.add("https://chandigarh-dev.chandigarhsmartcity.in");
		allowheaderList.add("https://chandigarh-uat.chandigarhsmartcity.in");
		headers.set(headername, headervalue);
		headers.setAccessControlAllowHeaders(allowheaderList);
		return headers;
		
	}
}
