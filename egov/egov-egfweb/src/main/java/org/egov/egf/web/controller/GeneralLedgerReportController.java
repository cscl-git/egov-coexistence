package org.egov.egf.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.FundService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.budget.BudgetUploadReport;
import org.egov.services.budget.BudgetDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javaxt.utils.Array;

import org.egov.services.report.GeneralLedgerReportService;
import org.egov.infra.admin.master.service.GeneralLedgerPOJO;
import org.egov.model.report.DedicatedExpenseViewData;
import org.egov.model.report.CapitalRevenueRequestPojo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.ui.ModelMap;
import org.egov.model.report.DivisionReportPOJO;
import com.exilant.eGov.src.reports.TrialBalanceBean;


@Controller
@RequestMapping("/generalledgerreport/")
public class GeneralLedgerReportController {
	private final static String GERENAL_LEDGER_REPORT_SEARCH_HOME = "generalledgerreport-search-home";
	
	private final static String CAPITAL_REVENUE_REPORT_SEARCH_HOME = "capitalrevenue-search-home";
	
	private final static String CAPITAL_DIVISION_REPORT_SEARCH_HOME = "capitaldivision-search-home";
	
	
	 @Autowired
	    private FinancialYearDAO financialYearDAO;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private FunctionDAO functionDAO;
	@Autowired
	@Qualifier("budgetDetailService")
	private BudgetDetailService budgetDetailService;
	@Autowired
	private EgovMasterDataCaching masterDataCache;
	
	
	
	
	@Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;
	
	@Autowired
	@Qualifier("generalLedgerReportService")
	GeneralLedgerReportService generalLedgerReportService;
	
	@Autowired
    private MicroserviceUtils microserviceUtils;
	
	private List<String> glcode;
	
	@Autowired
	FundService fundService;
	
	final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private String removeEntrysWithZeroAmount = "";
	
	private Date startDate = new Date();
	private Date endDate = new Date();
	
	private String header = "The Report is Generated ";
	private boolean data_sucess = false;
	private void prepareNewForm(Model model) {
		List<Department> de = microserviceUtils.getDepartments();
	
		model.addAttribute("departments", microserviceUtils.getDepartments());
		model.addAttribute("funds", fundService.findAll());
		model.addAttribute("functions", functionDAO.findAll());
	
		
	}

	
	@RequestMapping(value = "home",  method = {RequestMethod.GET,RequestMethod.POST})
	public String search(@ModelAttribute("generalLedgerPOJO")GeneralLedgerPOJO generalLedgerPOJO,  Model model) {
	  System.out.println("generalledgerreport--->>>>");
		prepareNewForm(model);
		model.addAttribute("data_success",data_sucess );
		return GERENAL_LEDGER_REPORT_SEARCH_HOME;

	}
	
	
	@RequestMapping(value = "results", method = {RequestMethod.GET,RequestMethod.POST})
	public String result(@ModelAttribute("generalLedgerPOJO")GeneralLedgerPOJO generalLedgerPOJO,Model model) {
		System.out.println("Results ------>>>>>");
		
		 List<String> glnames = new ArrayList();
		StringBuilder queryString  = new StringBuilder(" ");
		   
		 	
		queryString.append(" voucherdate >= TO_DATE('"+ generalLedgerPOJO.getFrom_date()+"','dd/mm/yyyy') and voucherdate <= TO_DATE('"+generalLedgerPOJO.getTo_date()+"','dd/mm/yyyy')" );
		if(generalLedgerPOJO.getDepartment()!=null && !generalLedgerPOJO.getDepartment().isEmpty()) {
			queryString.append(" and department =:department");
		}
		
		List<DedicatedExpenseViewData> d = generalLedgerReportService.getDedicated(queryString,null, null,generalLedgerPOJO.getDepartment());
		 String head = header+" from "+generalLedgerPOJO.getFrom_date()+" to "+generalLedgerPOJO.getTo_date();
		model.addAttribute("glname",generalLedgerReportService.sendGlname());
		model.addAttribute("headertag", head);
		model.addAttribute("data", d);
		model.addAttribute("data_success",true );
		
		prepareNewForm(model);
	
		return GERENAL_LEDGER_REPORT_SEARCH_HOME;

	}
	
	
	@RequestMapping(value = "xls", method = {RequestMethod.GET,RequestMethod.POST})
	public void xls(@ModelAttribute("generalLedgerPOJO")GeneralLedgerPOJO generalLedgerPOJO,Model model,HttpServletResponse response) {
		System.out.println("Results ------>>>>>");
		
		 byte[] fileContent=null;
		
		 System.out.println("Results ------>>>>>");
			
			
			System.out.println(generalLedgerPOJO.getTo_date());
			System.out.println(generalLedgerPOJO.getFrom_date());
			
			System.out.println(generalLedgerPOJO.getDepartment());
			
			
			StringBuilder queryString  = new StringBuilder(" ");
			   
			
			  String head = header+" from "+generalLedgerPOJO.getFrom_date()+" to "+generalLedgerPOJO.getTo_date();
			queryString.append(" voucherdate >= TO_DATE('"+ generalLedgerPOJO.getFrom_date()+"','dd/mm/yyyy') and voucherdate <= TO_DATE('"+generalLedgerPOJO.getTo_date()+"','dd/mm/yyyy')" );
			if(generalLedgerPOJO.getDepartment()!=null && !generalLedgerPOJO.getDepartment().isEmpty()) {
				queryString.append(" and department =:department");
			}
			List<DedicatedExpenseViewData> d = generalLedgerReportService.getDedicated(queryString,null, null,generalLedgerPOJO.getDepartment());
			
			
			Map<String,String>headerData  = new HashMap<>();
			headerData.put("h1", head);
			fileContent = generalLedgerReportService.DedicatedExpense(headerData, d);
		
		 response.setContentType("application/ms-excel");
		 response.setContentLength(fileContent.length); 
		 response.setHeader("Expires:", "0"); // eliminates browser caching 
		 response.setHeader("Content-Disposition","attachment; filename=salaryexpenditure.xls"); 
		 try { 
			 OutputStream outStream =response.getOutputStream(); 
			 outStream.write(fileContent); outStream.flush();
		 		  }
		 		  catch(Exception ex) {
		 		  
		 		  }
	
		//return GERENAL_LEDGER_REPORT_SEARCH_HOME;

	}

	
	
	
	@RequestMapping(value = "capitalrevenuehome",  method = {RequestMethod.GET,RequestMethod.POST})
	public String capitalRevenuesearchhome(@ModelAttribute("capitalRevenueRequestPojo")CapitalRevenueRequestPojo capitalRevenueRequestPojo, 
			Model model,HttpServletRequest request) {
	  System.out.println("generalledgerreport--->>>>");
		prepareNewForm(model);
		model.addAttribute("data_success",data_sucess );
		return CAPITAL_REVENUE_REPORT_SEARCH_HOME;

	}
	
	
	@RequestMapping(value = "capitalrevenuehomeresults",  method = {RequestMethod.GET,RequestMethod.POST})
	public String capitalRevenuesearch(@ModelAttribute("capitalRevenueRequestPojo")CapitalRevenueRequestPojo capitalRevenueRequestPojo,  Model model,HttpServletResponse response,HttpServletRequest request) {
	  System.out.println("capitalrevenuehomeresults--->>>>");
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		prepareNewForm(model);
		
		System.out.println(capitalRevenueRequestPojo.getFrom_date()+"--"+capitalRevenueRequestPojo.getTo_date());
		System.out.println(capitalRevenueRequestPojo.getDepartment()+"---"+capitalRevenueRequestPojo.getExpense_type());
		System.out.println(capitalRevenueRequestPojo.getFund()+"----"+capitalRevenueRequestPojo.getFunction());
		String head = "";
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String sDate = capitalRevenueRequestPojo.getFrom_date();
		String eDate = capitalRevenueRequestPojo.getTo_date();
		Date dt = new Date();
		Date dd = dt;
		CFinancialYear finYearByDate = financialYearDAO.getFinYearByDate(dt);
		String endFormat = formatter.format(dd);
		String endDate1 = formatter.format(finYearByDate.getEndingDate());
		
		try {
			dt = sdf.parse(sDate);
		} catch (ParseException e1) {

		}
		
		
		try {
			dd = sdf.parse(eDate);
		} catch (ParseException e1) {

		}
		

		if(capitalRevenueRequestPojo.getExpense_type().equals("1")) {
			head="Revenue Expenditure Report  From: "+capitalRevenueRequestPojo.getFrom_date()+" To: "+capitalRevenueRequestPojo.getTo_date();
		}
		
		if(capitalRevenueRequestPojo.getExpense_type().equals("2")) {
			head="Capital Expenditure Report  From: "+capitalRevenueRequestPojo.getFrom_date()+" To: "+capitalRevenueRequestPojo.getTo_date();
		}
		if (endFormat.compareTo(endDate1) > 0) {
			System.out.println("Start Date and End Date should be in same financial year");
			model.addAttribute("headertag",head );
			model.addAttribute("data_success", false);
			model.addAttribute("data", null);
			model.addAttribute("msg", "Start Date and End Date should be in same financial year");
			return CAPITAL_REVENUE_REPORT_SEARCH_HOME;
			
	}
		
		data  = generalLedgerReportService.getRevenueCapitalData(capitalRevenueRequestPojo);
		
		if(null!=data) {
			model.addAttribute("data_success",true );
			model.addAttribute("error",false );
			model.addAttribute("headertag",head );
			model.addAttribute("data",data );
			
			
			
			return CAPITAL_REVENUE_REPORT_SEARCH_HOME;
			
		}else {
			
			
			
			model.addAttribute("data_success", true);
			model.addAttribute("headertag",head );
			model.addAttribute("error",true );
			model.addAttribute("data", null);
			model.addAttribute("msg", "No data Found");
			
			
			
		
			return CAPITAL_REVENUE_REPORT_SEARCH_HOME;

		}
		
	}
	
	
	
	@RequestMapping(value = "capitalrevenuehomeresultsXLS",  method = {RequestMethod.GET,RequestMethod.POST})
	public void capitalRevenuesearchXLS(@ModelAttribute("capitalRevenueRequestPojo")CapitalRevenueRequestPojo capitalRevenueRequestPojo,  Model model,HttpServletResponse response) {
	  System.out.println("capitalrevenuehomeresults--->>>>");
		prepareNewForm(model);
		ModelMap m = new ModelMap();
		ArrayList<ArrayList<String>>  a = new ArrayList<ArrayList<String>>();
		byte[] fileContent=null;
		Set<String> dname = new HashSet<String>();
		System.out.println(capitalRevenueRequestPojo.getFrom_date()+"--"+capitalRevenueRequestPojo.getTo_date());
		System.out.println(capitalRevenueRequestPojo.getDepartment()+"---"+capitalRevenueRequestPojo.getExpense_type());
		System.out.println(capitalRevenueRequestPojo.getFund()+"----"+capitalRevenueRequestPojo.getFunction());
		String head = "";
		if(capitalRevenueRequestPojo.getExpense_type().equals("1")) {
			head="Revenue Expenditure";
		}
		
		if(capitalRevenueRequestPojo.getExpense_type().equals("2")) {
			head="Capital Expenditure";
		}
	
		Map<String,String>headerData  = new HashMap<>();
		headerData.put("h1", "Report for");
		fileContent = generalLedgerReportService.getRevenueCapitalDataExl(capitalRevenueRequestPojo);
		
		 response.setContentType("application/ms-excel");
		 response.setContentLength(fileContent.length); 
		 response.setHeader("Expires:", "0"); // eliminates browser caching 
		 response.setHeader("Content-Disposition","attachment; filename=capitalrevenueexpenditure.xls"); 
		 try { 
			 OutputStream outStream =response.getOutputStream(); 
			 outStream.write(fileContent); outStream.flush();
		 		  }
		 		  catch(Exception ex) {
		 		  
		 		  }
	

	}
	
	

	@RequestMapping(value = "capitaldivisionhome",  method = {RequestMethod.GET,RequestMethod.POST})
	public String capitaldivisionhome(@ModelAttribute("capitalRevenueRequestPojo")CapitalRevenueRequestPojo capitalRevenueRequestPojo, 
			Model model,HttpServletRequest request) {
	
		prepareNewForm(model);
		model.addAttribute("data_success",data_sucess );
		return CAPITAL_DIVISION_REPORT_SEARCH_HOME;

	}
	
	
	@RequestMapping(value = "capitaldivisionresults",  method = {RequestMethod.GET,RequestMethod.POST})
	public String capitaDivisionReportResults(@ModelAttribute("capitalRevenueRequestPojo")CapitalRevenueRequestPojo capitalRevenueRequestPojo,  Model model,HttpServletResponse response) {
	  System.out.println("capitaldivisionresultsXLS--->>>>");
		prepareNewForm(model);
	
		String sDate = capitalRevenueRequestPojo.getFrom_date();
		String eDate = capitalRevenueRequestPojo.getTo_date();
		Date dt = new Date();
		Date dd = dt;

		try {
			dt = sdf.parse(sDate);
		} catch (ParseException e1) {

		}
		CFinancialYear finYearByDate = financialYearDAO.getFinYearByDate(dt);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dd = sdf.parse(eDate);
		} catch (ParseException e1) {

		}
		
		String endFormat = formatter.format(dd);
		String endDate1 = formatter.format(finYearByDate.getEndingDate());
		
		
		if (endFormat.compareTo(endDate1) > 0) {
			model.addAttribute("error", true);
			model.addAttribute("data_success",false);
			model.addAttribute("data",null);
			model.addAttribute("msg","Dates must be in same financial year");
			return CAPITAL_DIVISION_REPORT_SEARCH_HOME;
		}
		
		List<DivisionReportPOJO> data = new ArrayList<DivisionReportPOJO>();
		List<TrialBalanceBean> tb = new ArrayList<TrialBalanceBean>();
		tb= null;
		
		tb = generalLedgerReportService.getdivisionDataCheck(capitalRevenueRequestPojo);
		if(null==tb) {
			model.addAttribute("error", true);
			model.addAttribute("data_success",false);
			model.addAttribute("data",null);
			model.addAttribute("msg","No Data Found");
			return CAPITAL_DIVISION_REPORT_SEARCH_HOME;
		}
		
		model.addAttribute("error", false);
		model.addAttribute("data_success",true);
		model.addAttribute("data",tb);
		model.addAttribute("msg","Data Found");
		return CAPITAL_DIVISION_REPORT_SEARCH_HOME;

	}
	
	
	@RequestMapping(value = "capitaldivisionresultsXLS",  method = {RequestMethod.GET,RequestMethod.POST})
	public void capitaDivisionReportXLS(@ModelAttribute("capitalRevenueRequestPojo")CapitalRevenueRequestPojo capitalRevenueRequestPojo,  Model model,HttpServletResponse response) {
	  System.out.println("capitaldivisionresultsXLS--->>>>");
		prepareNewForm(model);
		ModelMap m = new ModelMap();
		
		byte[] fileContent=null;
		
		String head = "Report Generated for ";
		head = head+"From - "+capitalRevenueRequestPojo.getFrom_date()+" To - "+capitalRevenueRequestPojo.getTo_date();
		Map<String,String>headerData  = new HashMap<>();
		headerData.put("h1", head);
		
		String sDate = capitalRevenueRequestPojo.getFrom_date();
		String eDate = capitalRevenueRequestPojo.getTo_date();
		Date dt = new Date();
		Date dd = dt;

		try {
			dt = sdf.parse(sDate);
		} catch (ParseException e1) {

		}
		CFinancialYear finYearByDate = financialYearDAO.getFinYearByDate(dt);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dd = sdf.parse(eDate);
		} catch (ParseException e1) {

		}
		
		String endFormat = formatter.format(dd);
		String endDate1 = formatter.format(finYearByDate.getEndingDate());

		if (endFormat.compareTo(endDate1) > 0) {
			System.out.println("Start Date and End Date should be in same financial year");
			//return null;
		}
		
		List<DivisionReportPOJO> data = new ArrayList<DivisionReportPOJO>();
		data = generalLedgerReportService.getdivisionData(capitalRevenueRequestPojo);
		fileContent = generalLedgerReportService.getDivisionExl(headerData, data);
		
		 response.setContentType("application/ms-excel");
		 response.setContentLength(fileContent.length); 
		 response.setHeader("Expires:", "0"); // eliminates browser caching 
		 response.setHeader("Content-Disposition","attachment; filename=ReceiptReport.xls"); 
		 try { 
			 OutputStream outStream =response.getOutputStream(); 
			 outStream.write(fileContent); outStream.flush();
		 		  }
		 		  catch(Exception ex) {
		 		  
		 		  }
	

	}
	
	
	
	
	
}
