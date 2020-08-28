package org.egov.works.web.controller.bgsecurity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.bgsecurity.entity.BGSecurityDetails;
import org.egov.works.bgsecurity.service.BGSecurityDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/bgSecurity")
public class BGSecurityController {

	@Autowired
	BGSecurityDetailsService bgSecurityDetailsService;

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("bgSecurityDetails") final BGSecurityDetails bgSecurityDetails,
			final Model model, HttpServletRequest request) {

		return "bg-security-form";
	}

	@RequestMapping(value = "/bgSecuritySave", params = "bgSecuritySave", method = RequestMethod.POST)
	public String saveSecurityDetailsData(
			@ModelAttribute("bgSecurityDetails") final BGSecurityDetails bgSecurityDetails, final Model model,
			final HttpServletRequest request) throws Exception {
		//BGSecurityDetails savedBGSecurityDetails = null;
		// Convert input string into a date
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = inputFormat.parse(bgSecurityDetails.getStart_dt());
		bgSecurityDetails.setSecurity_start_date(startDate);
		Date endDate = inputFormat.parse(bgSecurityDetails.getEnd_dt());
		bgSecurityDetails.setSecurity_end_date(endDate);
		BGSecurityDetails savedBGSecurityDetails = bgSecurityDetailsService.saveSecurityDetailsData(request, bgSecurityDetails);

		return "bg-security-form";

	}

}
