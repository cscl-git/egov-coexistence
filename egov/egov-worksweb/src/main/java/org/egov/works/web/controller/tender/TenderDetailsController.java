package org.egov.works.web.controller.tender;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.tender.entity.Tender;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.egov.works.tender.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/tenderProcurement")
public class TenderDetailsController {

	@Autowired
	TenderService tenderService;

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("tender") final Tender tender, final Model model,
			HttpServletRequest request) {

		return "tender-form";
	}

	@RequestMapping(value = "/tenderSave", params = "tenderSave", method = RequestMethod.POST)
	public String saveTenderDetailsData(@ModelAttribute("tender") final Tender tender, final Model model,
			final HttpServletRequest request) throws Exception {
		//Tender savedTender = null;
		// Convert input string into a date
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = inputFormat.parse(tender.getProcurement_dt());
		tender.setProcurement_date(date);
		Tender savedTender = tenderService.saveTenderDetailsData(request, tender);

		return "tender-form";

	}

}
