package org.egov.audit.web.controller.preAudit;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.audit.model.ManageAuditor;
import org.egov.audit.service.ManageAuditorService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/manageAuditor")
public class ManageAuditorsController{

	
	private static final Logger LOGGER = Logger.getLogger(ManageAuditorsController.class);
	private static final String MANAGEAUDITOR_FORM = "manageAuditor-form";
	private static final String MANAGEAUDITOR_FORMEDIT = "manageAuditor-formedit";
	 @Autowired
	 MicroserviceUtils microserviceUtils;
	 @Autowired
	  protected EgovMasterDataCaching masterDataCache;
	 
	 @Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	 
	 @Autowired
	 ManageAuditorService manageAuditorService;
	
	@Autowired
	private AppConfigValueService appConfigValuesService;

	@Autowired
    private DepartmentService departmentService;
	
	
	  
	  @RequestMapping(value = "/manageAudform", method = {RequestMethod.GET,RequestMethod.POST})
	    public String manageAudform(Model model)
	    {  
		  List<ManageAuditor> manageAuditorslist=new ArrayList<>();
		  LOGGER.info("New manageAuditors creation request created");
		  
		  ManageAuditor manageAuditors= new ManageAuditor();
	       
	        prepareNewFormDropDown(model);
	        manageAuditorslist=manageAuditorService.getAudiors();
	        
	        for (ManageAuditor manageAuditor : manageAuditorslist) {
	        	Department dept=departmentService.getDepartmentById(Long.valueOf(manageAuditor.getDeptid()));
	        	EmployeeInfo employeeInfo=microserviceUtils.getEmployeeById(Long.valueOf(manageAuditor.getEmployeeid()));
	        	if(dept!=null) 
	        	{
	        		manageAuditor.setDeptName(dept.getName());
	        	}
	        	if(employeeInfo!=null) {
	        		
	        		manageAuditor.setEmployeeName(employeeInfo.getUser().getName());
	        	}
			}
	    
	       
	        model.addAttribute("manageAuditorslist", manageAuditorslist);
	        model.addAttribute("manageAuditors", manageAuditors);
	        return MANAGEAUDITOR_FORM;

	    }
	  
	  @RequestMapping(value="/updateauditor/{id}")    
	    public String edit(@PathVariable Long id, Model model){    
		  
		  LOGGER.info("update manageAuditors creation request created");
		  ManageAuditor manageAuditors=  manageAuditorService.getAudiorById(id);
		  
		  prepareNewFormDropDown(model);
		  
		  List<EmployeeInfo> EmployeeInfoList=getApproversdropDown(manageAuditors.getType());
		  
		  Department dept=departmentService.getDepartmentById(Long.valueOf(manageAuditors.getDeptid()));
		  EmployeeInfo employeeInfo=microserviceUtils.getEmployeeById(Long.valueOf(manageAuditors.getEmployeeid()));
      	if(dept!=null) 
      	{
      		manageAuditors.setDeptName(dept.getName());
      	}
      	if(employeeInfo!=null) {
      		
      		manageAuditors.setEmployeeName(employeeInfo.getUser().getName());
      	}
		  
        model.addAttribute("approverListEdit", EmployeeInfoList);
		  model.addAttribute("manageAuditors", manageAuditors); 
	        return MANAGEAUDITOR_FORMEDIT;    
	    }    
	  
	 
	
	  @RequestMapping(value = "/saveAuditor", method = RequestMethod.POST)
	    public String saveAuditor(@ModelAttribute("manageAuditors") final ManageAuditor manageAuditors, final BindingResult errors,
	            final RedirectAttributes redirectAttrs, @RequestParam final String workAction,Model model) {
	        LOGGER.info("New manageAuditors creation request created");
	        boolean exists=false;
	        
	        exists=  manageAuditorService. findByEmployeeidExists(manageAuditors);
	        
	        if(exists) {
	        	redirectAttrs.addFlashAttribute("message", "msg.ManageAuditorAlreadyExist.success");
	        }
	        else {
	        ManageAuditor manageAuditorsSave=	manageAuditorService.saveAuditors(manageAuditors);
	        
	        	prepareNewFormDropDown(model);
		        model.addAttribute("manageAuditors", manageAuditors);
	        
		        if(manageAuditorsSave!=null) {
		        	redirectAttrs.addFlashAttribute("message", "msg.ManageAuditor.success");
		        }
	        }    
				  return "redirect:/manageAuditor/manageAudform";
	    }
	  
	  
	  @RequestMapping(value = "/updateAuditor", method = RequestMethod.POST)
	    public String updateAuditor(@ModelAttribute("manageAuditors") final ManageAuditor manageAuditors, final BindingResult errors,
	            final RedirectAttributes redirectAttrs, @RequestParam final String workAction,Model model) {
	        LOGGER.info("New manageAuditors creation request created");
	        
	        Long id=Long.valueOf(workAction);
	        manageAuditors.setId(id);
	        boolean exists=false;
	        
	        exists=  manageAuditorService. findByEmployeeidExists(manageAuditors);
	        
	        if(exists) {
	        	//redirectAttrs.addFlashAttribute("message", "msg.ManageAuditorAlreadyExist.success");
	        	 model.addAttribute("message", "Relation Already Exists");
	        }
	        else {
	        ManageAuditor manageAuditorsSave=manageAuditorService.saveAuditors(manageAuditors);
	        
	        	prepareNewFormDropDown(model);
		        model.addAttribute("manageAuditors", manageAuditors);
	        
		        if(manageAuditorsSave!=null) {
		        	//redirectAttrs.addFlashAttribute("message", "msg.ManageAuditorAlreadyupdate.success");
		        	 model.addAttribute("message", "Updated Successfully");
		        }
	        }    
	       

			 return "audit-success"; 
	    }
	 
	  private void prepareNewFormDropDown(final Model model) {
	      List<String> Auditortype=new ArrayList<>();
	      Auditortype.add("RSA");
	      Auditortype.add("Auditor");
	      List<EmployeeInfo> approverslist=new ArrayList<>();
	    
	      List<AppConfigValues> appConfigValuesEmpList =null;
		
			appConfigValuesEmpList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					"audit_designation");
	      
	      for (AppConfigValues appConfigValues : appConfigValuesEmpList) {
			
	    	  
	    	  List<EmployeeInfo> approvers = microserviceUtils.getApprovers("", appConfigValues.getValue());
	    	  approverslist.addAll(approvers);
		}
	      
	     
	    
	      model.addAttribute("approverList", approverslist);
	        model.addAttribute("departments", microserviceUtils.getDepartments());
	        model.addAttribute("auditorType", Auditortype);
	    }
	  
	  
	  public List<EmployeeInfo> getApproversdropDown( String type) {

		  String appconfig="";
		  if(type.equalsIgnoreCase("RSA")) {
			  appconfig="audit_designation";
		  }
		  else if(type.equalsIgnoreCase("Auditor"))
		  {
			  appconfig="junior_auditor_designation";
		  }
		  List<EmployeeInfo> approverslist=new ArrayList<>();
		    
	      List<AppConfigValues> appConfigValuesEmpList =null;
		
			appConfigValuesEmpList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					appconfig);
	      
	      for (AppConfigValues appConfigValues : appConfigValuesEmpList) {
			
	    	  
	    	  List<EmployeeInfo> approvers = microserviceUtils.getApprovers("", appConfigValues.getValue());
	    	  approverslist.addAll(approvers);
		}
	      
	     
	        return approverslist;
	    }	
	  
	  
	  @RequestMapping(value = "/employee/{type}", method = RequestMethod.GET)
	    @ResponseBody
	    public List<EmployeeInfo> getApprovers(@PathVariable(name = "type") String type) {

		  String appconfig="";
		  if(type.equalsIgnoreCase("RSA")) {
			  appconfig="audit_designation";
		  }
		  else if(type.equalsIgnoreCase("Auditor"))
		  {
			  appconfig="junior_auditor_designation";
		  }
		  List<EmployeeInfo> approverslist=new ArrayList<>();
		    
	      List<AppConfigValues> appConfigValuesEmpList =null;
		
			appConfigValuesEmpList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
					appconfig);
	      
	      for (AppConfigValues appConfigValues : appConfigValuesEmpList) {
			
	    	  
	    	  List<EmployeeInfo> approvers = microserviceUtils.getApprovers("", appConfigValues.getValue());
	    	  approverslist.addAll(approvers);
		}
	      
	     
	        return approverslist;
	    }	  
	  
	  
	  
	  @RequestMapping(value="/deleteAuditor/{id}")    
	    public String deleteAuditor(@PathVariable Long id, Model model){    
		  LOGGER.info("delete manageAuditors creation request created");
		  
		  persistenceService
	      .getSession()
	      .createSQLQuery(
	              "delete from eg_manageauditor where id =:Id").setLong("Id", id).executeUpdate();
		persistenceService.getSession().flush();
		  
		  model.addAttribute("message", "Updated Successfully");
		  
			return "audit-success"; 
	    }   
	  
	  
}
