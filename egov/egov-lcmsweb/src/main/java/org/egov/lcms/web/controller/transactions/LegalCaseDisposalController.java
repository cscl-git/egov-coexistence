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

import org.egov.infra.aadhaar.webservice.contract.AadhaarInfo;
import org.egov.lcms.masters.entity.AdvocateMaster;
import org.egov.lcms.masters.service.AdvocateMasterService;
import org.egov.lcms.transactions.entity.BidefendingCounsilDetails;
import org.egov.lcms.transactions.entity.LegalCase;
import org.egov.lcms.transactions.entity.LegalCaseDisposal;
import org.egov.lcms.transactions.entity.PaymentUpadte;
import org.egov.lcms.transactions.service.LegalCaseDisposalService;
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

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.mchange.v2.cfg.PropertiesConfigSource.Parse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/legalcasedisposal")
public class LegalCaseDisposalController {
    
    private static final String LEGLCASEDISPOSAL = "legalCaseDisposal";


    @Autowired
    private LegalCaseDisposalService legalCaseDisposalService;

    @Autowired
    private LegalCaseService legalCaseService;
	
	@Autowired
    PaymentUpdateService paymentUpdateService;
    
    @Autowired
	AdvocateMasterService advocateMasterService;
    @ModelAttribute
    private LegalCase getLegalCase(@RequestParam("lcNumber") final String lcNumber) {
        return legalCaseService.findByLcNumber(lcNumber);
    }

    @RequestMapping(value = "/new/", method = RequestMethod.GET)
    public String newForm(@ModelAttribute final LegalCaseDisposal legalCaseDisposal, final Model model,
            @RequestParam("lcNumber") final String lcNumber, final HttpServletRequest request) {
        final LegalCase legalCase = getLegalCase(lcNumber);
        model.addAttribute(LcmsConstants.LEGALCASE, legalCase);
        model.addAttribute(LEGLCASEDISPOSAL, legalCaseDisposal);
        model.addAttribute(LcmsConstants.MODE, "create");
        return "legalcaseDisposal-new";
    }

    @RequestMapping(value = "/new/", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final LegalCaseDisposal legalCaseDisposal, final BindingResult errors,
            @RequestParam("lcNumber") final String lcNumber, final RedirectAttributes redirectAttrs, final Model model,
            final HttpServletRequest request) throws ParseException {
        final LegalCase legalCase = getLegalCase(lcNumber);
        if (errors.hasErrors()) {
            model.addAttribute(LcmsConstants.LEGALCASE, legalCase);
            return "legalcaseDisposal-new";
        } else
            legalCaseDisposal.setLegalCase(legalCase);
        legalCaseDisposalService.persist(legalCaseDisposal);
        redirectAttrs.addFlashAttribute(LEGLCASEDISPOSAL, legalCaseDisposal);
        model.addAttribute("message", "Case is closed successfully.");
        model.addAttribute(LcmsConstants.MODE, "create");
        return "legalcaseDisposal-success";
    }
    
    @RequestMapping(value="/updatePayment/new")
    public String updatePayment(@ModelAttribute("updatePayment")PaymentUpadte paymentUpadte, final BindingResult errors,
            @RequestParam("lcNumber") final String lcNumber,final RedirectAttributes redirectAttrs, final Model model,
            final HttpServletRequest request) throws ParseException
    {
    	System.out.println(":::+"+lcNumber);
    	final LegalCase legalCase = getLegalCase(lcNumber);
    	List<PaymentUpadte> listPaymentUpadte = paymentUpdateService.getRecordsByLegalcase(legalCase.getId());
    	for(PaymentUpadte s1:listPaymentUpadte)
    	{
    		System.out.println("name---defending----"+s1.getNameOfDefendingCounsil());
    	}
    	
    	model.addAttribute("listPaymentUpadte",listPaymentUpadte);
    	
    	model.addAttribute("legalCase",legalCase);
    	
    	return "updatePayment-new";
    }
    @RequestMapping(value="/updatePayment/new" ,params = "save", method = RequestMethod.POST)
    public String savePaymentRecords(@ModelAttribute("updatePayment")PaymentUpadte paymentUpadte,Model model)
    {
    	
		String namecounsil = paymentUpadte.getNameOfDefendingCounsil();
		List<BidefendingCounsilDetails> updatePayment = advocateMasterService.getRecordsUpdatePayment(Long.valueOf(namecounsil));
		    	for(BidefendingCounsilDetails name:updatePayment) {
		    		paymentUpadte.setNameOfDefendingCounsil(name.getOppPartyAdvocate());
		    	}
		PaymentUpadte savepay = paymentUpdateService.savePayment(paymentUpadte);
    	
    	
    	List<PaymentUpadte> listPaymentUpadte = paymentUpdateService.getRecordsByLegalcase(paymentUpadte.getLegalcaseid());
    	for(PaymentUpadte s1:listPaymentUpadte)
    	{
    		System.out.println("name---defending----"+s1.getNameOfDefendingCounsil());
    	}
    	
    	model.addAttribute("listPaymentUpadte",listPaymentUpadte);
    	model.addAttribute("updatePayment",savepay);
    	return "updatePayment-new"; 
    }

}
