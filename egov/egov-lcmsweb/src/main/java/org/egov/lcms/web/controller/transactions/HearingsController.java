/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.lcms.web.controller.transactions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.notification.service.NotificationService;
import org.egov.lcms.transactions.entity.Hearings;
import org.egov.lcms.transactions.entity.LegalCase;
import org.egov.lcms.transactions.service.HearingsService;
import org.egov.lcms.transactions.service.LegalCaseService;
import org.egov.lcms.utils.constants.LcmsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/hearing")
public class HearingsController {
    
    private static final String HEARINGS = "hearings";
    private static final String ROLE_MEETING_SENIOR_OFFICER = "LEGAL_NODAL_OFFICER";
    @Autowired
    private HearingsService hearingsService;
    
    @Autowired
    MicroserviceUtils microserviceUtils;

    
    @Autowired
    private LegalCaseService legalCaseService;
    
    @Autowired
    private AppConfigValueService appConfigValuesService;
    
    @Autowired
    private NotificationService notificationService;
    
    
    
    @RequestMapping(value = "/new/", method = RequestMethod.GET)
    public String newForm(@ModelAttribute("hearings") final Hearings hearings, final Model model,
            @RequestParam("lcNumber") final String lcNumber, final HttpServletRequest request) {
        final LegalCase legalCase = getLegalCase(lcNumber, request);
        model.addAttribute(LcmsConstants.LEGALCASE, legalCase);
        model.addAttribute("positionTemplList", hearings.getPositionTemplList());
        model.addAttribute(HEARINGS, hearings);
        model.addAttribute(LcmsConstants.MODE, "create");
        return "hearings-new";
    }

    @ModelAttribute
    private LegalCase getLegalCase(@RequestParam("lcNumber") final String lcNumber, final HttpServletRequest request) {
        return legalCaseService.findByLcNumber(lcNumber);
    }

    @RequestMapping(value = "/new/", method = RequestMethod.POST)
    public String create(@ModelAttribute final Hearings hearings, final BindingResult errors,
            @RequestParam("lcNumber") final String lcNumber, final RedirectAttributes redirectAttrs, final Model model,
            final HttpServletRequest request) throws ParseException {
        final LegalCase legalCase = getLegalCase(lcNumber, request);
        hearingsService.validateDate(hearings, legalCase, errors);
        if (errors.hasErrors()) {
            model.addAttribute(LcmsConstants.LEGALCASE, legalCase);
            return "hearings-new";
        }
        hearings.setLegalCase(legalCase);
        hearingsService.persistHearings(hearings);
        
        Boolean hearingsmsStatus=sendSmsAndEmailDetailsForAgendaInvitation(hearings, lcNumber);
        if(hearingsmsStatus) {
        	System.out.println("+++++++++++++++++++++++++++Sms Sent Successfully..++++++++++++++++++++++++++++++++");
        }
        redirectAttrs.addFlashAttribute(HEARINGS, hearings);
        model.addAttribute("message", "Hearing created successfully.");
        model.addAttribute(LcmsConstants.MODE, "create");
        return "hearings-success";
    }

    @RequestMapping(value = "/list/", method = RequestMethod.GET)
    public String getHearingsList(final Model model, @RequestParam("lcNumber") final String lcNumber,
            @Valid @ModelAttribute final Hearings hearings, final HttpServletRequest request) {
        final LegalCase legalCase = getLegalCase(lcNumber, request);
        final List<Hearings> hearingsList = hearingsService.findByLCNumber(lcNumber);
        
        model.addAttribute(LcmsConstants.LEGALCASE, legalCase);
        model.addAttribute("lcNumber", legalCase.getLcNumber());
        model.addAttribute("hearingsId", legalCase.getHearings());
        model.addAttribute("positionTemplList", hearings.getPositionTemplList());
        model.addAttribute(HEARINGS, hearings);
        model.addAttribute("hearingsList", hearingsList);
        return "hearings-list";
    }
    
    
    //SMS and Email Service
    public Boolean sendSmsAndEmailDetailsForAgendaInvitation(final Hearings hearings, final String lcNumber) {
    	final LegalCase legalCase =legalCaseService.findByLcNumber(lcNumber);
          boolean successStatus=false;
    final String msg="Dear Member,\r\n" + 
    			"This is to inform you that hearing for Court Type-" +legalCase.getCourtMaster().getName()+" has been scheduled on Date-"+legalCase.getNextDate() +"\r\n" + 
    			"Thanks & Regards\r\n" + 
    			"Legal Branch";
            	
            
        try {
        	//System.out.println("+++++++++++"+msg+"+++++++++++++++++");
        	Boolean status=sendSmsForAgendaInvitation(msg,legalCase);
        	if(status) {
        		successStatus=true;
        	}
        }catch(Exception e) {
        	//LOGGER.error("Unable to send SMS to for agenda invitation ");
        	System.out.println("+++++++++++++Unable to send SMS to for agenda invitation+++++++++");
        }
            
        
		/*
		 * try {
		 * 
		 * boolean emailStatus=sendEmailForAgendaInvitation(msg,legalCase);
		 * if(emailStatus) { //System.out.
		 * println("++++++++++++++++++++++++++email Sent Successfully+++++++++++++++++++"
		 * ); } } catch (Exception e) {
		 * //LOGGER.error("Error in sending email for agenda invitation",e);
		 * 
		 * }
		 */
	        
       
        
        
        
        return successStatus;
    }
    //------------------------------------------------EMAIL-------------------------------------------------------------------
    
    public Boolean sendEmailForAgendaInvitation(String customMessage,LegalCase legalCase) {
        Boolean emailEnabled = true;
        Boolean emailStatus=false;
        if (emailEnabled) {
        	try {
	            List<User> listOfUsers = getUserListForAgendaInvitation();
	            for (User user : listOfUsers) {
	                if (user.getEmailId() != null) {
	                	buildEmailForAgendaInvitation(user.getUserName(), user.getEmailId(), customMessage);
	                	if(legalCase.getCounselEmailNo()!=null && !legalCase.getCounselEmailNo().equalsIgnoreCase("0") && !legalCase.getCounselEmailNo().isEmpty()) {
	                		buildEmailForAgendaInvitation(legalCase.getOppPartyAdvocate(), legalCase.getCounselEmailNo(), customMessage);
	                		
	                	}
	                }
	                emailStatus=true;
	            }
	        }catch(Exception e) {
	        	//LOGGER.error("Unable to send EMAIL to agenda invitation");
	        }
        }
        return emailStatus;
    }
    
    
    public void buildEmailForAgendaInvitation(final String userName, final String email,
            final String customMessage) {
        String body = customMessage;
        String subject;
        subject = "Updated Hearring date";
        body = customMessage;
        //System.out.println(customMessage);
        if (email != null && body != null)
            sendEmailOnSewerageForMeetingWithAttachment(email, body, subject);
    }
    
    public void sendEmailOnSewerageForMeetingWithAttachment(final String email, final String emailBody, final String emailSubject) {
        if(!StringUtils.isBlank(email) && !StringUtils.isBlank(emailBody)) {
        	notificationService.sendEmail(email, emailSubject, emailBody);
        	
        }else {
        	System.out.println("++++++++Email not Sent+++++++");
        }
    }

    
    //-----------------------------------------------SMS----------------------------------------------------------------
    public Boolean sendSmsForAgendaInvitation(String customMessage,LegalCase legalCase) {
        Boolean smsEnabled = true;
         Boolean smsStatus=false;
        if (smsEnabled) {
            try {
	            List<User> listOfUsers = getUserListForAgendaInvitation();
	            for (User user : listOfUsers) {
	                if (user.getMobileNumber() != null) {
	                	
	                	buildSmsForAgendaInvitation(user.getUserName(), user.getMobileNumber(), customMessage);
	                	if(legalCase.getCounselPhoneNo()!=null && legalCase.getCounselPhoneNo()!="0") {
	                		buildSmsForAgendaInvitation(legalCase.getOppPartyAdvocate(), legalCase.getCounselPhoneNo(), customMessage);
	                	}
	                }
	                smsStatus=true;
	            }
            }catch(Exception e) {
            	System.out.println("Unable to send Legal");
            }
        }
        return smsStatus;
    }
    

    private Boolean getAppConfigValueByPassingModuleAndType(String moduleName, String sendsmsoremail) {
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(moduleName,
                sendsmsoremail);

        return "YES".equalsIgnoreCase(
                appConfigValue != null && !appConfigValue.isEmpty() ? appConfigValue.get(0).getValue() : "NO");
    }
    
    public void buildSmsForAgendaInvitation(final String userName, final String mobileNumber,
            final String customMessage) {
    	sendSMSOnSewerageForMeeting(mobileNumber, customMessage);            
    }
    public void sendSMSOnSewerageForMeeting(final String mobileNumber, final String smsBody) {
        notificationService.sendSMS(mobileNumber, smsBody);
    }
    public List<User> getUserListForAgendaInvitation() {
        Set<User> usersListResult = new HashSet<>();
        List<String> roles = new ArrayList<String>();
        roles.add(ROLE_MEETING_SENIOR_OFFICER);
        List<EmployeeInfo> employees = microserviceUtils.getEmployeesByRoles(roles);
    	if(!CollectionUtils.isEmpty(employees)) {
    		for(EmployeeInfo info : employees) {
    			usersListResult.add(info.getUser());
    		}
    	}
        return new ArrayList<>(usersListResult);
    }
    
}
