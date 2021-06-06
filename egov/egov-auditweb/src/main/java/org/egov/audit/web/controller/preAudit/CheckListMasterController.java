package org.egov.audit.web.controller.preAudit;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.egov.audit.entity.BillTypeCheckList;
import org.egov.audit.model.BillTypeCheckListData;
import org.egov.audit.service.BillTypeCheckListService;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.model.bills.EgBillSubType;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Sonu
 */

@Controller
@RequestMapping(value = "/checkListMaster")
public class CheckListMasterController extends GenericWorkFlowController {
	
	private static final Logger LOGGER = Logger.getLogger(CheckListMasterController.class);
	
	 @Autowired
	 private EgBillSubTypeService egBillSubTypeService;
	 @Autowired
	 private BillTypeCheckListService billTypeCheckListService;
	
	 public List<EgBillSubType> getBillSubTypes() {
	        return egBillSubTypeService.getByExpenditureType(FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT);
	    }
    
    @RequestMapping(value = "/checklist", method = {RequestMethod.GET,RequestMethod.POST})
    public String showcheckList(@ModelAttribute("billTypeCheckListdata") final BillTypeCheckListData billTypeCheckListdata,
    		final Model model,HttpServletRequest requests) {
        LOGGER.info("New Bill Type List request created");
        EgBillSubType sub=new EgBillSubType();
        sub.setName("Post Audit");
        getBillSubTypes().add(sub);
        model.addAttribute("billSubTypes", getBillSubTypes());
        return "check-list-master";
    }
    
	/*
	 * @RequestMapping(value = "/checklist", method = RequestMethod.GET) public
	 * String showcheckListGET(@ModelAttribute("billTypeCheckListdata") final
	 * BillTypeCheckListData billTypeCheckListdata, final Model
	 * model,HttpServletRequest requests) {
	 * LOGGER.info("New Bill Type List request created"); EgBillSubType sub=new
	 * EgBillSubType(); sub.setName("Post Audit"); getBillSubTypes().add(sub);
	 * model.addAttribute("billSubTypes", getBillSubTypes());
	 * System.out.println(billTypeCheckListdata.getMessege());
	 * model.addAttribute("billTypeCheckListdata", billTypeCheckListdata); return
	 * "check-list-master"; }
	 */
    
    
    @RequestMapping(value = "/showBillCheckList", method = RequestMethod.POST)
    public String showcheckListGet(@ModelAttribute("billTypeCheckListdata") final BillTypeCheckListData billTypeCheckListdata,
    		final Model model,HttpServletRequest requests) {
        LOGGER.info("New Bill Type List request created");
        EgBillSubType sub=new EgBillSubType();
        sub.setName("Post Audit");
        getBillSubTypes().add(sub);
        model.addAttribute("billSubTypes", getBillSubTypes());
        List<BillTypeCheckList> CheckListbill;
        String str=billTypeCheckListdata.getBillType();
        int len=0;
       LOGGER.info(str);
       if(!billTypeCheckListdata.isStringNull(str)) {
       	CheckListbill=billTypeCheckListService.findByBillType(billTypeCheckListdata.getBillType());
       	len=CheckListbill.size();
       	billTypeCheckListdata.setBillTypeCheckLists(CheckListbill);
       	billTypeCheckListdata.setBillType(str);
       	model.addAttribute("len",len);
       	model.addAttribute("billTypeCheckListdata",billTypeCheckListdata);
       }else {
       	billTypeCheckListdata.setBillTypeCheckLists(null);
       	billTypeCheckListdata.setBillType(str);
       	model.addAttribute("len",len);
       	model.addAttribute("billTypeCheckListdata",billTypeCheckListdata);
       }
        return "check-list-master";
    }
    
    @RequestMapping(value = "/showBillDescriptionList", params="billtypedescripSave", method = RequestMethod.POST)
    public String showbillcheckList(@ModelAttribute("billTypeCheckListdata") final BillTypeCheckListData billTypeCheckListdata
    		,final Model model,HttpServletRequest requests,final RedirectAttributes redir) {
        LOGGER.info("New Bill Type List request created");
        EgBillSubType sub=new EgBillSubType();
        sub.setName("Post Audit");
        getBillSubTypes().add(sub);
        model.addAttribute("billSubTypes", getBillSubTypes());
        String billdiscrip=billTypeCheckListdata.getBillType();
        List<BillTypeCheckList> billtypechecklist=new ArrayList<BillTypeCheckList>();
        List<BillTypeCheckList> CheckListbill=billTypeCheckListdata.getBillTypeCheckLists();
        System.out.println(billdiscrip);
        for(BillTypeCheckList btck:CheckListbill) {
        	btck.setBillType(billdiscrip);
        	billtypechecklist.add(btck);
        }
        billTypeCheckListService.addBillTypeDescription(billtypechecklist);
        BillTypeCheckListData billCheckSaveStatus=new BillTypeCheckListData();
        billCheckSaveStatus.setMessege("Add Bill Check List Successfully");
        redir.addFlashAttribute("billTypeCheckListdata", billCheckSaveStatus);
        return "redirect:/checkListMaster/checklist";
    }


    
    @RequestMapping(value = "/deleteBillDescription/{Id}", method = RequestMethod.GET)
	public String deleteBillTypeDescrip(@ModelAttribute("billTypeCheckListdata") final BillTypeCheckListData billTypeCheckListdata,
    		final Model model,HttpServletRequest request, @PathVariable final String Id,final RedirectAttributes redir) {
    	
    	billTypeCheckListService.deleteBillTypeDescripById(Long.parseLong(Id));
    	billTypeCheckListdata.setMessege("Delete Record Successfully");
    	redir.addFlashAttribute("billTypeCheckListdata", billTypeCheckListdata);
		return "redirect:/checkListMaster/checklist";
    	
    }
    
}
