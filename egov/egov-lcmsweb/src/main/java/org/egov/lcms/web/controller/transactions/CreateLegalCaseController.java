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

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.lcms.autonumber.LegalCaseNumberGenerator;
import org.egov.lcms.masters.entity.AdvocateMaster;
import org.egov.lcms.masters.entity.vo.AttachedDocument;
import org.egov.lcms.masters.service.AdvocateMasterService;
import org.egov.lcms.masters.service.ConcernedBranchMasterService;
import org.egov.lcms.transactions.entity.LegalCase;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/application")
public class CreateLegalCaseController extends GenericLegalCaseController {

    @Autowired
    private LegalCaseService legalCaseService;
    
    @Autowired
    private ConcernedBranchMasterService concernedBranchMasterService;

    @Autowired
    private LegalCaseNumberGenerator legalCaseNumberGenerator;

    @Autowired
	AdvocateMasterService advocateMasterService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String newForm(@ModelAttribute final LegalCase legalcase, final Model model,
            final HttpServletRequest request) {
//    	model.addAttribute(LcmsConstants.DefCLIST,advocateMasterService.findAll());
    	List<AdvocateMaster> dropdownValue=advocateMasterService.findAll();
    	for(AdvocateMaster as:dropdownValue) {
    		System.out.println("::::"+as.getName());
    		
    	}
    	model.addAttribute("defendingDropdown",dropdownValue);
    	model.addAttribute(LcmsConstants.CONCERNEDBRANCHLIST, concernedBranchMasterService.getActiveConcernedBranchs());
        model.addAttribute(LcmsConstants.LEGALCASE, legalcase);
        model.addAttribute(LcmsConstants.MODE, "create");
        model.addAttribute(LcmsConstants.IS_REAPPEAL_CASE, false);
        return "legalCase-newForm";
    }

    @RequestMapping(value = "/createlegalcase", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final LegalCase legalCase, final BindingResult errors, final Model model,
            final RedirectAttributes redirectAttrs, final HttpServletRequest request)
            throws IOException, ParseException {
    	
    	List<AttachedDocument> attachedDocuments = new ArrayList<AttachedDocument>();
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
    	
        final String caseNumber = legalCase.getCaseNumber() + "/" + legalCase.getWpYear();
        final LegalCase validateCasenumber = legalCaseService.getLegalCaseByCaseNumber(caseNumber);
        if (validateCasenumber != null)
            errors.reject("error.legalcase.casenumber");
        final LegalCase validateFilenumber = legalCaseService.findByLcNumber(legalCase.getLcNumber());
        if (validateFilenumber != null)
            errors.reject("error.legalcase.filenumber");
        if (errors.hasErrors()) {
        	model.addAttribute(LcmsConstants.CONCERNEDBRANCHLIST, concernedBranchMasterService.getActiveConcernedBranchs());
            model.addAttribute(LcmsConstants.MODE, "create");
            model.addAttribute(LcmsConstants.IS_REAPPEAL_CASE, false);
            model.addAttribute("bipartisanRespondentDetailsList", legalCase.getBipartisanRespondentDetailsList());
            model.addAttribute("bipartisanPetitionerDetailsList", legalCase.getBipartisanPetitionerDetailsList());
            model.addAttribute("biDefendingCounsilDetailsList", legalCase.getBiDefendingCounsilDetailsList());
            return "legalCase-newForm";
        }
        //legalCase.setLcNumber(legalCaseNumberGenerator.generateLegalCaseNumber());
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
        legalCaseService.persist(legalCase, attachedDocuments);
        redirectAttrs.addFlashAttribute(LcmsConstants.LEGALCASE, legalCase);
        model.addAttribute("message", "Legal Case created successfully.");
        model.addAttribute(LcmsConstants.MODE, "view");
        return "legalcase-success";
    }

}