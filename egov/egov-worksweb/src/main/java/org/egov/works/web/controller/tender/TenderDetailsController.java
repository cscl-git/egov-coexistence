package org.egov.works.web.controller.tender;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.tender.entity.Tender;
import org.egov.works.tender.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
			@RequestParam("file1") MultipartFile file1, final HttpServletRequest request) throws Exception {
		System.out.println("sneha");
		Tender savedTender = tenderService.saveTenderDetailsData(request, tender);

		return "tender-form";

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.POST)
	public String view(@PathVariable("id") final Long id, Model model) {

		Tender tenderDetails = tenderService.searchTenderData(id);

		model.addAttribute("tender", tenderDetails);

		return "view-tender-form";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String showEstimateNewFormGet(@ModelAttribute("tender") final Tender tender, final Model model,
			HttpServletRequest request) {

		return "search-tender-form";
	}

	@RequestMapping(value = "/tenderSearch", params = "tenderSearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(@ModelAttribute("tender") final Tender tender, final Model model,
			final HttpServletRequest request) throws Exception {
		List<Tender> tenderList = new ArrayList<Tender>();

		List<Tender> tenderDetails = tenderService.searchTenderData(request, tender);
		 tenderList.addAll(tenderDetails);
		 tender.setTenderList(tenderList);

		model.addAttribute("tender", tender);

		return "search-tender-form";

	}

}
