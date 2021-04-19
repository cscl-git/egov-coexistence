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
package org.egov.council.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.council.entity.CommitteeMembers;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.entity.CouncilSmsDetails;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.User;
import org.egov.infra.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.egov.council.utils.constants.CouncilConstants.MODULE_FULLNAME;
import static org.egov.council.utils.constants.CouncilConstants.MOM_FINALISED;
import static org.egov.council.utils.constants.CouncilConstants.SENDEMAILFORCOUNCIL;
import static org.egov.council.utils.constants.CouncilConstants.SENDSMSFORCOUNCIL;

@Service
public class CouncilSmsAndEmailService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String AGENDAATTACHFILENAME = "agendadetails.rtf";
    
    private static final Logger LOGGER = Logger
            .getLogger(CouncilSmsAndEmailService.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    @Qualifier("parentMessageSource")
    private MessageSource councilMessageSource;

    @Autowired
    private AppConfigValueService appConfigValuesService;

    @Autowired
    private CouncilCommitteeMemberService committeeMemberService;

    @Autowired
    private CouncilMeetingService councilMeetingService;

    /**
     * @return this method will send SMS and Email is isSmsEnabled is true
     * @param CouncilMeeting
     * @param workFlowAction
     */
    public void sendSms(CouncilMeeting councilMeeting, String customMessage) {
        String mobileNo;
        Boolean smsEnabled = isSmsEnabled();

        if (smsEnabled) {
        	try {
	            for (CommitteeMembers committeeMembers : committeeMemberService
	                    .findAllByCommitteTypeMemberIsActive(councilMeeting.getCommitteeType())) {
	                mobileNo = committeeMembers.getCouncilMember().getMobileNumber();
	                if (mobileNo != null) {
	                    buildSmsForMeeting(mobileNo, councilMeeting.getCommitteeType().getName(), councilMeeting, customMessage);
	                }
	            }
        	}catch(Exception e) {
            	LOGGER.error("Unable to send SMS to council members of meeting number "+councilMeeting.getMeetingNumber());
            }
            try {
	            List<User> listOfUsers = councilMeetingService.getUserListForMeeting(councilMeeting);
	            for (User user : listOfUsers) {
	                if (user.getMobileNumber() != null) {
	                    buildSmsForMeetingCouncilRoles(user.getUserName(), user.getMobileNumber(), councilMeeting, customMessage);
	                }
	            }
            }catch(Exception e) {
            	LOGGER.error("Unable to send SMS to meeting creators of meeting number "+councilMeeting.getMeetingNumber());
            }
            buildCouncilSmsDetails(customMessage, councilMeeting);
        }
    }
    
    public void sendSmsNotice(CouncilMeeting councilMeeting, String customMessage) {
        String mobileNo;
        Boolean smsEnabled = isSmsEnabled();

        if (smsEnabled) {
        	try {
	            for (CommitteeMembers committeeMembers : committeeMemberService
	                    .findAllByCommitteTypeMemberIsActive(councilMeeting.getCommitteeType())) {
	                mobileNo = committeeMembers.getCouncilMember().getMobileNumber();
	                if (mobileNo != null) {
	                    buildSmsForMeeting(mobileNo, councilMeeting.getCommitteeType().getName(), councilMeeting, customMessage);
	                }
	            }
        	}catch(Exception e) {
            	LOGGER.error("Unable to send SMS to council members of meeting number "+councilMeeting.getMeetingNumber());
            }
            try {
	            List<User> listOfUsers = councilMeetingService.getUserListForNotice(councilMeeting);
	            for (User user : listOfUsers) {
	                if (user.getMobileNumber() != null) {
	                    buildSmsForMeetingCouncilRoles(user.getUserName(), user.getMobileNumber(), councilMeeting, customMessage);
	                }
	            }
            }catch(Exception e) {
            	LOGGER.error("Unable to send SMS to meeting creators of meeting number "+councilMeeting.getMeetingNumber());
            }
            buildCouncilSmsDetails(customMessage, councilMeeting);
        }
    }
    
    public void sendSmsForAgendaInvitation(String customMessage) {
        Boolean smsEnabled = isSmsEnabled();

        if (smsEnabled) {
            try {
	            List<User> listOfUsers = councilMeetingService.getUserListForAgendaInvitation();
	            for (User user : listOfUsers) {
	                if (user.getMobileNumber() != null) {
	                	buildSmsForAgendaInvitation(user.getUserName(), user.getMobileNumber(), customMessage);
	                }
	            }
            }catch(Exception e) {
            	LOGGER.error("Unable to send SMS to agenda invitation");
            }
        }
    }

    public void sendEmail(CouncilMeeting councilMeeting, String customMessage, final byte[] attachment) {
    	sendEmail(councilMeeting, customMessage, attachment, null, null);
    }
    
    public void sendEmail(CouncilMeeting councilMeeting, String customMessage, final byte[] attachment, String fileType, String fileName) {
    	String emailId;
        Boolean emailEnabled = isEmailEnabled();
        if (emailEnabled) {
        	try {
	            for (CommitteeMembers committeeMembers : committeeMemberService
	                    .findAllByCommitteTypeMemberIsActive(councilMeeting.getCommitteeType())) {
	                emailId = committeeMembers.getCouncilMember().getEmailId();
	                if (emailId != null) {
	                    buildEmailForMeeting(emailId, councilMeeting.getCommitteeType().getName(), councilMeeting, customMessage,
	                            attachment, fileType, fileName);
	                }
	            }
	        }catch(Exception e) {
	        	LOGGER.error("Unable to send EMAIL to council members of meeting number "+councilMeeting.getMeetingNumber());
	        }
        	try {
	            List<User> listOfUsers = councilMeetingService.getUserListForMeeting(councilMeeting);
	            for (User user : listOfUsers) {
	                if (user.getEmailId() != null) {
	                    buildEmailForMeetingForCouncilRoles(user.getUserName(), user.getEmailId(), councilMeeting, customMessage,
	                            attachment, fileType, fileName);
	                }
	            }
	        }catch(Exception e) {
	        	LOGGER.error("Unable to send EMAIL to meeting creators of meeting number "+councilMeeting.getMeetingNumber());
	        }
        }
    }
    
    public void sendEmailForAgendaInvitation(String customMessage, final byte[] attachment, String fileType, String fileName) {
        Boolean emailEnabled = isEmailEnabled();
        if (emailEnabled) {
        	try {
	            List<User> listOfUsers = councilMeetingService.getUserListForAgendaInvitation();
	            for (User user : listOfUsers) {
	                if (user.getEmailId() != null) {
	                	buildEmailForAgendaInvitation(user.getUserName(), user.getEmailId(), customMessage,
	                            attachment, fileType, fileName);
	                }
	            }
	        }catch(Exception e) {
	        	LOGGER.error("Unable to send EMAIL to agenda invitation");
	        }
        }
    }

    private CouncilSmsDetails buildCouncilSmsDetails(String message,
            CouncilMeeting councilMeeting) {
        CouncilSmsDetails councilSmsDetails = new CouncilSmsDetails();
        councilSmsDetails.setSmsSentDate(new Date());
        councilSmsDetails.setSmsContent(message);
        councilSmsDetails.setMeeting(councilMeeting);
        councilMeeting.addSmsDetails(councilSmsDetails);
        return councilSmsDetails;
    }

    /**
     * @return SMS AND EMAIL body and subject For Committee Members
     * @param CouncilMeeting
     * @param email
     * @param mobileNumber
     * @param smsMsg
     * @param body
     * @param subject
     */

    public void buildSmsForMeeting(final String mobileNumber, final String name, final CouncilMeeting councilMeeting,
            final String customMessage) {
        String smsMsg;
        if (MOM_FINALISED.equals(councilMeeting.getStatus().getCode())) {
            smsMsg = smsBodyByCodeAndArgsWithType("msg.resolution.sms", name, councilMeeting, customMessage);
        } else {
            smsMsg = smsBodyByCodeAndArgsWithType("msg.meeting.sms", name, councilMeeting, customMessage);
        }
        if (mobileNumber != null && smsMsg != null)
            sendSMSOnSewerageForMeeting(mobileNumber, smsMsg);
    }
    
    public void buildEmailForMeeting(final String email, final String name, final CouncilMeeting councilMeeting,
            final String customMessage, final byte[] attachment) {
    	buildEmailForMeeting(email, name, councilMeeting, customMessage, attachment, null,null);
    }

    public void buildEmailForMeeting(final String email, final String name, final CouncilMeeting councilMeeting,
            final String customMessage, final byte[] attachment, String fileType, String fileName) {
        String body;
        String subject;
        if (MOM_FINALISED.equals(councilMeeting.getStatus().getCode())) {
            body = emailBodyByCodeAndArgsWithType("email.resolution.body", name, councilMeeting, customMessage);
            subject = emailSubjectforEmailByCodeAndArgs("email.resolution.subject", name, councilMeeting);
        } else {
            //body = emailBodyByCodeAndArgsWithType("email.meeting.body", name, councilMeeting, customMessage);
        	final SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        	body="Dear "+councilMeeting.getCommitteeType().getName()+" Members, "+councilMeeting.getMeetingNumber()+ " " +councilMeeting.getCommitteeType().getName() +"Meeting scheduled on "+ sf.format(councilMeeting.getMeetingDate()) +" at "+String.valueOf(councilMeeting.getMeetingTime())+" at "+ String.valueOf(councilMeeting.getMeetingLocation()) +". Please find the agenda of the meeting attached with this email";
            subject = councilMeeting.getCommitteeType().getName()+" Members, "+councilMeeting.getMeetingNumber()+ " " +councilMeeting.getCommitteeType().getName() +"Meeting scheduled on "+ sf.format(councilMeeting.getMeetingDate()) +" at "+String.valueOf(councilMeeting.getMeetingTime())+" at "+ String.valueOf(councilMeeting.getMeetingLocation()) ;
        }
        if (email != null && body != null)
            sendEmailOnSewerageForMeetingWithAttachment(email, body, subject, attachment,fileType,fileName);
    }

    /**
     * @return SMS AND EMAIL body and subject For Council Roles
     * @param CouncilMeeting
     * @param email
     * @param mobileNumber
     * @param smsMsg
     * @param body
     * @param subject
     */

    public void buildSmsForMeetingCouncilRoles(final String userName, final String mobileNumber,
            final CouncilMeeting councilMeeting,
            final String customMessage) {
        String smsMsg;
        if (MOM_FINALISED.equals(councilMeeting.getStatus().getCode())) {
            smsMsg = smsBodyByCodeAndArgsWithType("msg.council.roles.resolution.sms", userName, councilMeeting,
                    customMessage);
        } else {
            smsMsg = smsBodyByCodeAndArgsWithType("msg.council.roles.meeting.sms", userName, councilMeeting,
                    customMessage);
        }
        if (mobileNumber != null && smsMsg != null)
            sendSMSOnSewerageForMeeting(mobileNumber, smsMsg);
    }
    
    public void buildSmsForAgendaInvitation(final String userName, final String mobileNumber,
            final String customMessage) {
    	sendSMSOnSewerageForMeeting(mobileNumber, customMessage);            
    }

    public void buildEmailForMeetingForCouncilRoles(final String userName, final String email,
            final CouncilMeeting councilMeeting,
            final String customMessage, final byte[] attachment) {
    	buildEmailForMeetingForCouncilRoles(userName, email, councilMeeting, customMessage, attachment, null, null);
    }
    
    public void buildEmailForMeetingForCouncilRoles(final String userName, final String email,
            final CouncilMeeting councilMeeting,
            final String customMessage, final byte[] attachment, String fileType, String fileName) {
        String body;
        String subject;
        if (MOM_FINALISED.equals(councilMeeting.getStatus().getCode())) {
            subject = emailSubjectforEmailByCodeAndArgs("email.council.roles.resolution.subject", userName, councilMeeting);
            body = emailBodyByCodeAndArgsWithType("email.council.roles.resolution.body", userName, councilMeeting,
                    customMessage);
        } else {
        	final SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        	body="Dear "+councilMeeting.getCommitteeType().getName()+" Members, "+councilMeeting.getMeetingNumber()+ " " +councilMeeting.getCommitteeType().getName() +"Meeting scheduled on "+ sf.format(councilMeeting.getMeetingDate()) +" at "+String.valueOf(councilMeeting.getMeetingTime())+" at "+ String.valueOf(councilMeeting.getMeetingLocation()) +". Please find the agenda of the meeting attached with this email";
            subject = councilMeeting.getCommitteeType().getName()+" Members, "+councilMeeting.getMeetingNumber()+ " " +councilMeeting.getCommitteeType().getName() +"Meeting scheduled on "+ sf.format(councilMeeting.getMeetingDate()) +" at "+String.valueOf(councilMeeting.getMeetingTime())+" at "+ String.valueOf(councilMeeting.getMeetingLocation()) ;
        }
        if (email != null && body != null)
            sendEmailOnSewerageForMeetingWithAttachment(email, body, subject, attachment,fileType,fileName);
    }
    
    public void buildEmailForAgendaInvitation(final String userName, final String email,
            final String customMessage, final byte[] attachment, String fileType, String fileName) {
        String body = customMessage;
        String subject;
        subject = emailSubjectforEmailByCodeAndArgs("email.council.agenda.invitation.subject");
        if (email != null && body != null)
            sendEmailOnSewerageForMeetingWithAttachment(email, body, subject, attachment,fileType,fileName);
    }

    /**
     * .
     * 
     * @param code
     * @param CouncilMeeting
     * @param applicantName
     * @param type
     * @return EmailBody for All Connection based on Type
     */
    public String emailBodyByCodeAndArgsWithType(final String code, final String name, final CouncilMeeting councilMeeting,
            final String customMessage) {
        final SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        /*return councilMessageSource.getMessage(code,
                new String[] { name,
                        sf.format(councilMeeting.getMeetingDate()),
                        String.valueOf(councilMeeting.getMeetingTime()),
                        String.valueOf(councilMeeting.getMeetingLocation()), customMessage != null ? customMessage : " " },
                LocaleContextHolder.getLocale());*/
        return councilMessageSource.getMessage(code,
                new String[] { councilMeeting.getCommitteeType().getName(),councilMeeting.getMeetingNumber(),councilMeeting.getCommitteeType().getName(),
                        sf.format(councilMeeting.getMeetingDate()),
                        String.valueOf(councilMeeting.getMeetingTime()),
                        String.valueOf(councilMeeting.getMeetingLocation()) },
                LocaleContextHolder.getLocale());
    }

    /**
     * @param code
     * @param CouncilMeeting
     * @param applicantName
     * @param type
     */
    public String smsBodyByCodeAndArgsWithType(final String code, final String name, final CouncilMeeting councilMeeting,
            final String customMessage) {
        final SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        return councilMessageSource.getMessage(code,
                new String[] { name,
                        sf.format(councilMeeting.getMeetingDate()),
                        String.valueOf(councilMeeting.getMeetingTime()),
                        String.valueOf(councilMeeting.getMeetingLocation()), customMessage != null ? customMessage : " " },
                LocaleContextHolder.getLocale());
    }

    public Boolean isSmsEnabled() {

        return getAppConfigValueByPassingModuleAndType(MODULE_FULLNAME, SENDSMSFORCOUNCIL);
    }

    private Boolean getAppConfigValueByPassingModuleAndType(String moduleName, String sendsmsoremail) {
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(moduleName,
                sendsmsoremail);

        return "YES".equalsIgnoreCase(
                appConfigValue != null && !appConfigValue.isEmpty() ? appConfigValue.get(0).getValue() : "NO");
    }

    public Boolean isEmailEnabled() {

        return getAppConfigValueByPassingModuleAndType(MODULE_FULLNAME, SENDEMAILFORCOUNCIL);

    }

    public String emailSubjectforEmailByCodeAndArgs(final String code, final String name, final CouncilMeeting councilMeeting) {
        final SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        return councilMessageSource.getMessage(code,
                new String[] { name,
                        sf.format(councilMeeting.getMeetingDate()),
                        String.valueOf(councilMeeting.getMeetingTime()),
                        String.valueOf(councilMeeting.getMeetingLocation()) },
                LocaleContextHolder.getLocale());
    }
    
    public String emailSubjectforEmailByCodeAndArgs(final String code) {
        final SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        return councilMessageSource.getMessage(code,null,
                LocaleContextHolder.getLocale());
    }

    public void sendSMSOnSewerageForMeeting(final String mobileNumber, final String smsBody) {
        notificationService.sendSMS(mobileNumber, smsBody);
    }

    /*public void sendEmailOnSewerageForMeetingWithAttachment(final String email, final String emailBody,
            final String emailSubject, final byte[] attachment) {
    	notificationService.sendEmailWithAttachment(email, emailSubject, emailBody, "application/rtf", AGENDAATTACHFILENAME,
                attachment);
    }*/
    
    public void sendEmailOnSewerageForMeetingWithAttachment(final String email, final String emailBody,
            final String emailSubject, final byte[] attachment, String fileType, String fileName) {
		/*
		 * if(!StringUtils.isBlank(fileType) && !StringUtils.isBlank(fileName)) {
		 * notificationService.sendEmailWithAttachment(email, emailSubject, emailBody,
		 * fileType, fileName, attachment); }else {
		 * notificationService.sendEmailWithAttachment(email, emailSubject, emailBody,
		 * "application/rtf", AGENDAATTACHFILENAME, attachment); }
		 */
        if(!StringUtils.isBlank(fileType) && !StringUtils.isBlank(fileName)) {
        	try {
				notificationService.sendEmailWithAttachmentNew(email, emailSubject, emailBody, fileType, fileName,
                    attachment);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else {
        	try {
				notificationService.sendEmailWithAttachmentNew(email, emailSubject, emailBody, "application/rtf", AGENDAATTACHFILENAME,
                    attachment);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
        }
    }

}