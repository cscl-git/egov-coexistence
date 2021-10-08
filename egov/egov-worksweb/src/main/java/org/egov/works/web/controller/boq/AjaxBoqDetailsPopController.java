package org.egov.works.web.controller.boq;

import static org.egov.infra.web.support.json.adapter.HibernateProxyTypeAdapter.FACTORY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.egov.commons.CChartOfAccounts;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.works.boq.entity.BoqDetailsPop;
import org.egov.works.boq.service.BoqDetailsPopService;
import org.python.core.exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

@Controller
@RequestMapping(value = "/popup")
public class AjaxBoqDetailsPopController {

	
	@Autowired
	BoqDetailsPopService BoqDetailsPop;
	
	 @RequestMapping(value = "/getboq/{ref_dsr}")
	 @ResponseBody 
	 public  List<BoqDetailsPop> getTags(@PathVariable("ref_dsr") String ref_dsr) {
		 List<BoqDetailsPop> boqdata = BoqDetailsPop.getRecords(ref_dsr);
		       System.out.println("ajax calling --------------kundan------- "+ref_dsr);
		       System.out.println("boqdata----list data"+boqdata);
	      
	       return boqdata;
	    }
	 

}
