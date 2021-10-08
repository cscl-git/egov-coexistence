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

package org.egov.infra.notification.service;

import org.apache.commons.codec.binary.Base64;
import org.egov.infra.admin.common.service.MessageTemplateService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.notification.entity.NotificationPriority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import javax.jms.Destination;
import javax.jms.MapMessage;

import org.springframework.mail.*;
import org.egov.infra.sendgrid.*;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.egov.infra.notification.NotificationConstants.ATTACHMENT;
import static org.egov.infra.notification.NotificationConstants.EMAIL;
import static org.egov.infra.notification.NotificationConstants.FILENAME;
import static org.egov.infra.notification.NotificationConstants.FILETYPE;
import static org.egov.infra.notification.NotificationConstants.MESSAGE;
import static org.egov.infra.notification.NotificationConstants.MOBILE;
import static org.egov.infra.notification.NotificationConstants.PRIORITY;
import static org.egov.infra.notification.NotificationConstants.SUBJECT;
import static org.egov.infra.notification.entity.NotificationPriority.HIGH;
import static org.egov.infra.notification.entity.NotificationPriority.MEDIUM;

@Service
public class NotificationService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("emailQueue")
    private Destination emailQueue;

    @Autowired
    @Qualifier("smsQueue")
    private Destination smsQueue;

    @Autowired
    @Qualifier("flashQueue")
    private Destination flashQueue;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Value("${mail.enabled}")
    private boolean mailEnabled;

    @Value("${sms.enabled}")
    private boolean smsEnabled;
    
    @Value("${mail.sender.username}")
    private String emailFrom;
    
    @Value("${mail.sender.password}")
    private String apiKey;

    public void sendEmail(User user, String subject, String templateName,
                          Object... messageValues) {
        sendEmail(user.getEmailId(), subject, messageTemplateService.realizeMessage(
                messageTemplateService.getByTemplateName(templateName), messageValues));
    }

    public void sendEmail(String email, String subject, String message) {
        if (mailEnabled && isNoneBlank(email, subject, message))
            jmsTemplate.send(emailQueue, session -> {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString(EMAIL, email);
                mapMessage.setString(MESSAGE, message);
                mapMessage.setString(SUBJECT, subject);
                return mapMessage;
            });
    }

    public void sendEmailWithAttachment(String email, String subject, String message,
                                        String fileType, String fileName, byte[] attachment) {
        if (mailEnabled && isNoneBlank(email, subject, message))
            jmsTemplate.send(emailQueue, session -> {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString(EMAIL, email);
                mapMessage.setString(MESSAGE, message);
                mapMessage.setString(SUBJECT, subject);
                mapMessage.setString(FILETYPE, fileType);
                mapMessage.setString(FILENAME, fileName);
                mapMessage.setBytes(ATTACHMENT, attachment);
                return mapMessage;
            });
    }
    
    public void sendEmailWithAttachmentNew(String email, String subject, String message,
            String fileType, String fileName, byte[] attachment) throws IOException {
			if (mailEnabled && isNoneBlank(email, subject, message))
			{
				Email from= new Email(emailFrom);
				Email to=new Email(email);
				Content content=new Content("text/plain",message);
				Mail mail = new Mail(from, subject, to, content);
				Attachments attachments3 = new Attachments();
				Base64 x = new Base64();
				String imageDataString = x.encodeAsString(attachment);
				attachments3.setContent(imageDataString);
				attachments3.setType(fileType);
				attachments3.setFilename(fileName);
				attachments3.setDisposition("attachment");
				attachments3.setContentId("Banner");
				mail.addAttachments(attachments3);
				SendGrid sg = new SendGrid(apiKey);
			    Request request = new Request();
			    try {
			        request.setMethod(Method.POST);
			        request.setEndpoint("mail/send");
			        request.setBody(mail.build());
			        Response response = sg.api(request);
			        System.out.println(response.getStatusCode());
			        System.out.println(response.getBody());
			        System.out.println(response.getHeaders());
			      } catch (IOException ex) {
			    	  ex.printStackTrace();
			        throw ex;
			      }
    		}
			
		}

    public void sendSMS(String mobileNo, String message,String templateId) {
        sendSMS(mobileNo, message, MEDIUM,templateId);
    }

    public void sendSMS(User user, String templateName, Object... messageValues) {
        sendSMS(user.getMobileNumber(), messageTemplateService.realizeMessage(
                messageTemplateService.getByTemplateName(templateName), messageValues), MEDIUM,null);
    }

    public void sendSMS(String mobileNo, String message, NotificationPriority priority,String templateId) {
    	System.out.println("f");
        if (smsEnabled && isNoneBlank(mobileNo, message))
            jmsTemplate.send(HIGH.equals(priority) ? flashQueue : smsQueue, session -> {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString(MOBILE, mobileNo);
                mapMessage.setString(MESSAGE, message);
                mapMessage.setString(PRIORITY, priority.name());
                mapMessage.setString("template",templateId);
                return mapMessage;
            });
    }
}
