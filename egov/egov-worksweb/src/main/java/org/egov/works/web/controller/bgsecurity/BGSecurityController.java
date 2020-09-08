package org.egov.works.web.controller.bgsecurity;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.bgsecurity.entity.BGSecurityDetails;
import org.egov.works.bgsecurity.service.BGSecurityDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

		BGSecurityDetails savedBGSecurityDetails = bgSecurityDetailsService.saveSecurityDetailsData(request,
				bgSecurityDetails);

		return "bg-security-form";

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {
		// TODO Auto-generated method stub
		BGSecurityDetails bgSecurityDetails = bgSecurityDetailsService.getBGSecurityDetails(id);

		model.addAttribute("bgSecurityDetails", bgSecurityDetails);
		return "view-bg-security-form";

	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String showEstimateNewFormGet(@ModelAttribute("bgSecurityDetails") final BGSecurityDetails bgSecurityDetails,
			final Model model, HttpServletRequest request) {

		return "search-bgsecurity-form";
	}

	@RequestMapping(value = "/bgsecuritySearch", params = "bgsecuritySearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(@ModelAttribute("bgSecurityDetails") final BGSecurityDetails bgSecurityDetails,
			final Model model, final HttpServletRequest request) throws Exception {
		List<BGSecurityDetails> securityList = new ArrayList<BGSecurityDetails>();

		List<BGSecurityDetails> bgsecurityDetails = bgSecurityDetailsService.searchBgSecurityData(request,
				bgSecurityDetails);
		securityList.addAll(bgsecurityDetails);
		bgSecurityDetails.setBgSecurityDetailsList(securityList);

		model.addAttribute("bgSecurityDetails", bgSecurityDetails);

		return "search-bgsecurity-form";

	}

}
