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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.lcms.masters.entity.AdvocateMaster;
import org.egov.lcms.masters.entity.vo.AttachedDocument;
import org.egov.lcms.masters.service.AdvocateMasterService;
import org.egov.lcms.masters.service.ConcernedBranchMasterService;
import org.egov.lcms.masters.service.CourtMasterService;
import org.egov.lcms.masters.service.PetitionTypeMasterService;
import org.egov.lcms.transactions.entity.BidefendingCounsilDetails;
import org.egov.lcms.transactions.entity.LegalCase;
import org.egov.lcms.transactions.entity.LegalCaseUploadDocuments;
import org.egov.lcms.transactions.service.LegalCaseService;
import org.egov.lcms.utils.LegalCaseUtil;
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
@RequestMapping(value = "/application/")
public class ViewAndEditLegalCaseController extends GenericLegalCaseController {

    @Autowired
    private LegalCaseService legalCaseService;

    @Autowired
    private PetitionTypeMasterService petitiontypeMasterService;

    @Autowired
    private CourtMasterService courtMasterService;

    @Autowired
    private LegalCaseUtil legalCaseUtil;
    
    @Autowired
    private ConcernedBranchMasterService concernedBranchMasterService;

    @Autowired
    AdvocateMasterService advocateMasterService;

//    @ModelAttribute
//    private LegalCase getLegalCase(@RequestParam("lcNumber") final String lcNumber) {
//        return legalCaseService.findByLcNumber(lcNumber);
//    }
    
    @ModelAttribute
    private LegalCase getLegalCase(@RequestParam("lcNumber") final String lcNumber) {
        return legalCaseService.findByLcNumber(lcNumber);
    }

    @RequestMapping(value = "/view/", method = RequestMethod.GET)
    public String view(@RequestParam("lcNumber") final String lcNumber, final Model model) {
        final LegalCase legalCase = legalCaseService.findByLcNumber(lcNumber);
        final LegalCase newlegalCase = getLegalCaseDocuments(legalCase);
        model.addAttribute(LcmsConstants.LEGALCASE, newlegalCase);
        model.addAttribute(LcmsConstants.MODE, "view");
        return "legalcasedetails-view";
    }

    @RequestMapping(value = "/edit/", method = RequestMethod.GET)
    public String edit(@RequestParam("lcNumber") final String lcNumber, final Model model) {
        final LegalCase legalCase = legalCaseService.findByLcNumber(lcNumber);
        final LegalCase newlegalCase = getLegalCaseDocuments(legalCase);
        model.addAttribute(LcmsConstants.LEGALCASE, newlegalCase);
        setDropDownValues(model);
        setDropDefending(model);
        final String[] casenumberyear = legalCase.getCaseNumber().split("/");
        legalCase.setCaseNumber(casenumberyear[0]);
        if (casenumberyear.length > 1)
            legalCase.setWpYear(casenumberyear[1]);
        legalCase.getBipartisanPetitionerDetailsList().addAll(legalCase.getPetitioners());
        legalCase.getBipartisanRespondentDetailsList().addAll(legalCase.getRespondents());
        legalCase.setFileNumber(legalCase.getLcNumber());
        model.addAttribute(LcmsConstants.MODE, "edit");
        model.addAttribute(LcmsConstants.IS_REAPPEAL_CASE, legalCase.getIsReappealOfCase());
        model.addAttribute(LcmsConstants.CONCERNEDBRANCHLIST, concernedBranchMasterService.getActiveConcernedBranchs());
        List<AdvocateMaster> dropdownValue=advocateMasterService.findAll();
        System.out.println("legalCase.getDefendingCounsilNames==("+legalCase.getDefendingCounsilNames());
    	for(AdvocateMaster as:dropdownValue) {
    		System.out.println("::::"+as.getName());
    		
    	}
    	model.addAttribute("defendingDropdown",dropdownValue);
        return "legalcase-edit";
    }

    private void setDropDefending(final Model model) {
        model.addAttribute("defendingList", advocateMasterService.findAll());
//        model.addAttribute("petitiontypeList", petitiontypeMasterService.getPetitiontypeList());
    }
    
    @RequestMapping(value = "/edit/", method = RequestMethod.POST)
    public String update(@ModelAttribute final LegalCase legalCase, @RequestParam("lcNumber") final String lcNumber,
            final BindingResult errors, final Model model,
            final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
    	
    	final String caseNumber = legalCase.getCaseNumber() + "/" + legalCase.getWpYear();
        final LegalCase validateCasenumber = legalCaseService.getLegalCaseByCaseNumber(caseNumber);
        if (validateCasenumber != null) {
        	if(legalCase.getId()!=validateCasenumber.getId()) {
        		errors.reject("error.legalcase.casenumber");
        	}
        }
            
        final LegalCase validateFilenumber = legalCaseService.findByLcNumber(legalCase.getFileNumber());
        if (validateFilenumber != null) {
        	if(legalCase.getId()!=validateFilenumber.getId()) {
        		errors.reject("error.legalcase.filenumber");
        	}
        }            
    	
        if (errors.hasErrors()) {
        	model.addAttribute(LcmsConstants.MODE, "edit");
            model.addAttribute(LcmsConstants.IS_REAPPEAL_CASE, legalCase.getIsReappealOfCase());
            model.addAttribute(LcmsConstants.CONCERNEDBRANCHLIST, concernedBranchMasterService.getActiveConcernedBranchs());
            return "legalcase-edit";
        }
        
        List<AttachedDocument> attachedDocuments = new ArrayList<AttachedDocument>();
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
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
        legalCase.setLcNumber(legalCase.getFileNumber());
        String checkedValue=request.getParameter("defCounsilPrimary");
        String ids=request.getParameter("id");
        long id=Long.parseLong(ids);
        System.out.println("id-------"+id);
//        for (final BidefendingCounsilDetails respondent : legalCase.getBiDefendingCounsilDetailsList()) {
//        	if(respondent.getDefCounsilPrimary()!=null)
//        	{
//        	System.out.println("respondent.getDefCounsilPrimary()"+respondent.getDefCounsilPrimary());
//        if(respondent.getDefCounsilPrimary().equals("NO") || respondent.getDefCounsilPrimary().equals("YES"))
//        {
//        	if(respondent.getDefCounsilPrimary().equals("NO"))
//        	{
//        		String novalue="NO";
//            	String yesValue="YES";
//            	legalCaseService.updateDefenidngCounsil(id,novalue,yesValue);
//            	System.out.println("respondent.getDefCounsilPrimary()----------"+respondent.getDefCounsilPrimary());
//                legalCaseService.persist(legalCase, attachedDocuments);
//        	}else {
//        		legalCaseService.persist(legalCase, attachedDocuments);
//        	}
//        	
//            
//        }
//        else {
//        	legalCaseService.persist(legalCase, attachedDocuments);
//        }
//        	}
//        }
        legalCaseService.persist(legalCase, attachedDocuments);
        setDropDownValues(model);
        final LegalCase newlegalCase = getLegalCaseDocuments(legalCase);
        model.addAttribute(LcmsConstants.LEGALCASE, newlegalCase);
        redirectAttrs.addFlashAttribute(LcmsConstants.LEGALCASE, newlegalCase);
        model.addAttribute(LcmsConstants.MODE, "view");
        model.addAttribute("message", "LegalCase updated successfully.");
        return "legalcase-success";
    }

    private void setDropDownValues(final Model model) {
        model.addAttribute("courtsList", courtMasterService.findAll());
        model.addAttribute("petitiontypeList", petitiontypeMasterService.getPetitiontypeList());
    }

    private LegalCase getLegalCaseDocuments(final LegalCase legalCase) {
        final List<LegalCaseUploadDocuments> documentDetailsList = legalCaseUtil.getLegalCaseDocumentList(legalCase);
        legalCase.setLegalCaseUploadDocuments(documentDetailsList);
        return legalCase;
    }

}
