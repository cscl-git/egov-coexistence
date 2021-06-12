package org.egov.lcms.web.controller.transactions;

import java.util.List;

import org.egov.lcms.masters.entity.AdvocateMaster;
import org.egov.lcms.masters.service.AdvocateMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/popup")
public class AjaxDefendingCounsilController {
	
	@Autowired
	AdvocateMasterService advocateMasterService;
	
	@RequestMapping(value = "/getboq/{oppPartyAdvocate}")
	 @ResponseBody 
	 public  List<AdvocateMaster> getTags(@PathVariable("oppPartyAdvocate") String name) {
		 List<AdvocateMaster> defendingCounsil = advocateMasterService.getRecords(name);
		       System.out.println("ajax calling --------------kundan------- "+name);
		       System.out.println("defendingCounsil----list data"+defendingCounsil);
	      
	       return defendingCounsil;
	    }
}
