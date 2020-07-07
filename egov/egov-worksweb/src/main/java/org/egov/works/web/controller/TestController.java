package org.egov.works.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/test")
public class TestController {
	
	
	@RequestMapping(value = "/create}", method = RequestMethod.POST)
	public String showNewFormGet(final Model model,
			HttpServletRequest request) {
		
		System.out.println("Test");
		
		return "test";
	}
		
	

}
