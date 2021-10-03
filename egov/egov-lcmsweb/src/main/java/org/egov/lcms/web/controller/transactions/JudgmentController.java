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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.User;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.notification.service.NotificationService;
import org.egov.lcms.masters.entity.AdvocateMaster;
import org.egov.lcms.masters.entity.vo.AttachedDocument;
import org.egov.lcms.masters.service.AdvocateMasterService;
import org.egov.lcms.masters.service.JudgmentTypeService;
import org.egov.lcms.transactions.entity.BidefendingCounsilDetails;
import org.egov.lcms.transactions.entity.Hearings;
import org.egov.lcms.transactions.entity.Judgment;
import org.egov.lcms.transactions.entity.LegalCase;
import org.egov.lcms.transactions.service.JudgmentService;
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
@RequestMapping("/judgment")
public class JudgmentController {
    
    private static final String JUDGMENT = "judgment";
    private static final String MODULE_FULLNAME = "Council Management";
    private static final String SENDSMSFORCOUNCIL = "SENDSMSFORCOUNCILMEMBER";
    private static final String ROLE_MEETING_SENIOR_OFFICER = "AGENDA_MEETING_SENIOR_OFFICER";
    private static final String SENDEMAILFORCOUNCIL = "SENDEMAILFORCOUNCILMEMBER";

    @Autowired
    private JudgmentTypeService judgmentTypeService;

    @Autowired
    private LegalCaseService legalcaseService;

    @Autowired
    private JudgmentService judgmentService;

    @Autowired
    MicroserviceUtils microserviceUtils;
    
    @Autowired
    private AppConfigValueService appConfigValuesService;
    
    @Autowired
    private NotificationService notificationService;
    @Autowired
    AdvocateMasterService advocateMasterService;

    private void prepareNewForm(final Model model) {
        model.addAttribute("judgmentTypes", judgmentTypeService.getActiveJudgementTypes());
    }

    @RequestMapping(value = "/new/", method = RequestMethod.GET)
    public String viewForm(@ModelAttribute("judgment") final Judgment judgment,
            @RequestParam("id") final Long id, final HttpServletRequest request, final Model model) {
        prepareNewForm(model);
        final LegalCase legalCase = getLegalCase(id, request);
        List<AdvocateMaster> dropdownValue=advocateMasterService.findAll();
        model.addAttribute("defendingDropdown",dropdownValue);
        model.addAttribute(LcmsConstants.LEGALCASE, legalCase);
        model.addAttribute(JUDGMENT, judgment);
        model.addAttribute(LcmsConstants.MODE, "create");
        return "judgment-new";
    }

    @ModelAttribute
    private LegalCase getLegalCase(@RequestParam("id") final Long id, final HttpServletRequest request) {
       return legalcaseService.findById(id);
    }

    @RequestMapping(value = "/new/", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("judgment") final Judgment judgment, final BindingResult errors,
            final RedirectAttributes redirectAttrs, @RequestParam("id") final Long id,
            final HttpServletRequest request, final Model model)
            throws IOException, ParseException {
    	List<AttachedDocument> attachedDocuments = new ArrayList<AttachedDocument>();
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
        
        final LegalCase legalcase = getLegalCase(id, request);
        if (errors.hasErrors()) {
            prepareNewForm(model);
            model.addAttribute(LcmsConstants.LEGALCASE, legalcase);
            return "judgment-new";
        } else
            judgment.setLegalCase(legalcase);
        
        if(uploadedFiles!=null) {
            for (int i = 0; i < uploadedFiles.length; i++) {
                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(path);
                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
                AttachedDocument attachedDocument = new AttachedDocument();
                attachedDocument.setFileStream(bios);
                attachedDocument.setFileName(fileName[i]);
                attachedDocument.setMimeType(contentType[i]);
                attachedDocuments.add(attachedDocument);
            }
        }
        judgmentService.persist(judgment, attachedDocuments);
        //sms and email 
        if(judgment.getJudgmentType().getName().equalsIgnoreCase("Disposed Off"))
        {
        	sendSmsAndEmailDetailsForAgendaInvitation(judgment, id,request);
        }
        model.addAttribute(LcmsConstants.MODE, "view");
        redirectAttrs.addFlashAttribute(JUDGMENT, judgment);
        model.addAttribute("message", "Judgment Created successfully.");
        return "judgment-success";

    }
    //SMS and Email Service
    public Boolean sendSmsAndEmailDetailsForAgendaInvitation(final Judgment judgment, final Long id,final HttpServletRequest request) {
    	final LegalCase legalCase = getLegalCase(id, request);
          //mSG
          Boolean smsEnabled = true;
          Boolean smsStatus=false;
          String customMessage="";
          final List<AppConfigValues> appList = appConfigValuesService
                  .getConfigValuesByModuleAndKey("EGF",
                          "LEGAL_JUDGEMENT_TEMPLATE_ID");
          final String templateId = appList.get(0).getValue();
          String name="";
          String phone="";
          for(BidefendingCounsilDetails row:legalCase.getBidefendingCounsilDetails())
          {
         	 if(row.getDefCounsilPrimary() != null && row.getDefCounsilPrimary().equalsIgnoreCase("YES"))
         	 {
         		 name=row.getOppPartyAdvocate();
         		 phone=row.getCounselphoneNo();
         	 }
          }
          
         if (smsEnabled) {
             try {
             	if(legalCase.getNodalOfficername()!=null && !legalCase.getNodalOfficername().isEmpty() && legalCase.getNodalOfficernumber() != null && !legalCase.getNodalOfficernumber().isEmpty() && !legalCase.getNodalOfficernumber().equalsIgnoreCase("0")) {
 	            	customMessage="Dear "+legalCase.getNodalOfficername()+", This is to inform you that the court type: "+legalCase.getCourtMaster().getCourtType().getCourtType()+", Case No: "+legalCase.getCaseNumber() +"has been disposed off and given time for Resolution is before "+judgment.getResolutiondate()+". Rgds, Chandigarh Smart City Ltd.";
             		buildSmsForAgendaInvitation(legalCase.getNodalOfficername(), legalCase.getNodalOfficernumber(), customMessage,templateId);
             	}
 	            if(name!=null && !name.isEmpty() && phone != null && !phone.isEmpty() && !phone.equalsIgnoreCase("0")) {
 	            	customMessage="Dear "+name+", This is to inform you that the court type: "+legalCase.getCourtMaster().getCourtType().getCourtType()+", Case No: "+legalCase.getCaseNumber() +"has been disposed off and given time for Resolution is before "+judgment.getResolutiondate()+". Rgds, Chandigarh Smart City Ltd.";
             		buildSmsForAgendaInvitation(name, phone, customMessage,templateId);
             	}
             }catch(Exception e) {
             	e.printStackTrace();
             	System.out.println("Unable to send Legal");
             }
         }
         return smsStatus;
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
    private Boolean getAppConfigValueByPassingModuleAndType(String moduleName, String sendsmsoremail) {
        final List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(moduleName,
                sendsmsoremail);

        return "YES".equalsIgnoreCase(
                appConfigValue != null && !appConfigValue.isEmpty() ? appConfigValue.get(0).getValue() : "NO");
    }
    
    public void buildSmsForAgendaInvitation(final String userName, final String mobileNumber,
            final String customMessage,String templateId) {
    	sendSMSOnSewerageForMeeting(mobileNumber, customMessage,templateId);            
    }
    public void sendSMSOnSewerageForMeeting(final String mobileNumber, final String smsBody,String templateId) {
        notificationService.sendSMS(mobileNumber, smsBody,templateId);
    }
    public Boolean isSmsEnabled() {

        return getAppConfigValueByPassingModuleAndType(MODULE_FULLNAME, SENDSMSFORCOUNCIL);
    }
}
