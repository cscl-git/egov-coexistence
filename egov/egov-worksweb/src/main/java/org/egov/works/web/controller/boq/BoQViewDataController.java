package org.egov.works.web.controller.boq;

import java.util.List;

import org.egov.works.boq.entity.BoqDateUpdate;
import org.egov.works.boq.service.BoQViewDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/timeExt")
public class BoQViewDataController {

	@Autowired
	BoQViewDataService boQViewDataService;
	private static final String BOQ_DATA_UPDATE = "boqDataUpdate";
	
	
	@RequestMapping(value = "/viewdata/{id}")
	public String viewModifyDate(@PathVariable("id")  Long id,Model model,ModelMap modelmap,BoqDateUpdate boqDataUpdate)
	{
		model.addAttribute(BOQ_DATA_UPDATE, boqDataUpdate);
		List<BoqDateUpdate>  boqDateUpdateValue =boQViewDataService.viewData(id);
		System.out.println("id number------"+id);
		modelmap.put("viewdataHistory", boqDateUpdateValue); 
		System.out.println("kundan view data here ............");
		return "view-data";
	}
}
