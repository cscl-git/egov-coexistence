package org.egov.egf.web.controller.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.commons.CFinancialYear;
import org.egov.commons.Fund;
import org.egov.commons.service.CFinancialYearService;
import org.egov.egf.model.BudgetVarianceEntry;
import org.egov.egf.model.BudgetVarianceEntryRestData;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.MinorScheduleRestData;
import org.egov.egf.model.ScheduleReportRestData;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.egf.web.actions.report.BudgetVarianceReportAction;
import org.egov.egf.web.actions.report.IncomeExpenditureReportAction;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.microservice.models.Department;
import org.egov.model.common.ResponseInfo;
import org.egov.model.common.ResponseInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/incomeexpend/")
public class IncomeExpenditureRestController {
	public static final String SUCCESS = "Success";
	@Autowired
	IncomeExpenditureReportAction incomeExpenditureReportAction;
	
	@Autowired
	 private  DepartmentService departmentService;
	
    @Autowired
	BudgetVarianceReportAction budgetVarianceReportAction;
	
    @Autowired
  private   CFinancialYearService cFinancialYearService;
  private   List<String> allowheaderList= new ArrayList<String>(); 
  private HttpHeaders headers = new HttpHeaders();
  
  private final String headername="Content-Security-Policy";
  private final String headervalue="default-src 'self' https://egov.chandigarhsmartcity.in https://egov-dev.chandigarhsmartcity.in https://egov-uat.chandigarhsmartcity.in https://mcc.chandigarhsmartcity.in https://chandigarh-dev.chandigarhsmartcity.in https://chandigarh-uat.chandigarhsmartcity.in";
	
	@ResponseBody
	@RequestMapping(value = "getAllIncomeExpentiureYearly", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllIncomeExpentiure( @RequestParam(name = "org_id") Long o,@RequestParam(name = "fin_id") Long fin,HttpServletRequest req ){
		
			
			Department d = new Department();
			
			CFinancialYear cf = new CFinancialYear();
			Fund f = new Fund();
			cf.setId(fin);
			d.setCode(o.toString());
			f.setId(0);
			
			CFinancialYear cFinancialYear = null;
			cFinancialYear = cFinancialYearService.findOne(cf.getId());
			if(null==cFinancialYear) {
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Financial Year").build(),getHeaders(),  HttpStatus.OK);
			}
			
			org.egov.infra.admin.master.entity.Department dt = departmentService.getDepartmentByCode(d.getCode());
			
			
			if(null==dt) {
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Department ").build(),getHeaders(),  HttpStatus.OK);
			}
			final Statement incomeExpenditureStatement=  new  Statement (cf,d,f);
			incomeExpenditureStatement.setRestData(true);
			
			incomeExpenditureReportAction.populateDataSource2(incomeExpenditureStatement);
			
			
			 List<IEStatementEntry> ieEntries = new ArrayList<IEStatementEntry>();
			
			 ieEntries = incomeExpenditureReportAction.getIncomeExpenditureStatement().getIeEntries();
			 if(null!=ieEntries)
			 ieEntries=removeEmptyEntries(ieEntries);
			
			 if(null!=ieEntries && ieEntries.size()>0) {
				 
				 ModelMap m =new ModelMap();
				 m.addAttribute("Report Type","Income And Expenditure Report");
				// m.addAttribute("departmentname", dt.getName());
				 m.addAttribute("departmentname", incomeExpenditureReportAction.getIncomeExpenditureStatement().getDepartment().getName());
				 m.addAttribute("Year Range",cFinancialYear.getFinYearRange());
				 m.addAttribute("currentyear",incomeExpenditureReportAction.getCurrentYearToDate());
				 m.addAttribute("previousyear",incomeExpenditureReportAction.getPreviousYearToDate());
				 m.addAttribute("incomeExpenditureList", ieEntries);
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(m).build(), headers,HttpStatus.OK);
			 }else {
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("no data").build(),getHeaders(), HttpStatus.OK);
			 }
		
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllIncomeExpentiureHalfYearly", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllIncomeExpentiureHalf( @RequestParam(name = "org_id") Long o,@RequestParam(name = "fin_id") Long fin,HttpServletRequest req ){
	
			Department d = new Department();
			CFinancialYear cf = new CFinancialYear();
			Fund f = new Fund();
			cf.setId(fin);
			d.setCode(o.toString());
			f.setId(0);
			
			
			
			CFinancialYear cFinancialYear = null;
			cFinancialYear = cFinancialYearService.findOne(cf.getId());
			if(null==cFinancialYear) {
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Financial Year").build(),getHeaders(),  HttpStatus.OK);
			}
			final Statement incomeExpenditureStatement=  new  Statement (cf,d,f);
			incomeExpenditureStatement.setRestData(true);
			incomeExpenditureStatement.setPeriod("Half Yearly");
			incomeExpenditureStatement.setRestData(true);
			incomeExpenditureReportAction.populateDataSource2(incomeExpenditureStatement);
			
			 List<IEStatementEntry> ieEntries = new ArrayList<IEStatementEntry>();
			 ieEntries = incomeExpenditureReportAction.getIncomeExpenditureStatement().getIeEntries();
			 if(null!=ieEntries)
			 ieEntries= removeEmptyEntries(ieEntries);
			 if(null!=ieEntries && ieEntries.size()>0) {
				 
				 ModelMap m =new ModelMap();
				 m.addAttribute("Report Type","Income And Expenditure Report");
				 m.addAttribute("departmentname", incomeExpenditureReportAction.getIncomeExpenditureStatement().getDepartment().getName());
				 m.addAttribute("Year Range",cFinancialYear.getFinYearRange());
				 m.addAttribute("currentyear",incomeExpenditureReportAction.getCurrentYearToDate());
				 m.addAttribute("previousyear",incomeExpenditureReportAction.getPreviousYearToDate());
				 m.addAttribute("incomeExpenditureList", ieEntries);
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(ieEntries).build(), HttpStatus.OK);
			 }else {
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("no data").build(),getHeaders(),  HttpStatus.OK);
			 }	
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllIncomeExpentiureMinorSchedules", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllIncomeExpentiureMinorSchedules(@RequestParam(name = "fin_id") Long fin,HttpServletRequest req ){
	
			Department d = null;
			CFinancialYear cf = new CFinancialYear();
			Fund f = new Fund();
			cf.setId(fin);
			//d.setCode("0");
			f.setId(0);
			
			Statement incomeExpenditureStatement=  new  Statement (cf,d,f);
			incomeExpenditureStatement.setRestData(true);
			incomeExpenditureStatement.setRestData(true);
			CFinancialYear cFinancialYear = null;
			cFinancialYear = cFinancialYearService.findOne(incomeExpenditureStatement.getFinancialYear().getId());
			if(null==cFinancialYear) {
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Financial Year").build(),getHeaders(),  HttpStatus.OK);
			}
			incomeExpenditureStatement= incomeExpenditureReportAction.populateDataSourceForAllSchedules2(incomeExpenditureStatement);
	
			 List<StatementEntry> smentry =  new ArrayList<StatementEntry>();
			 smentry=null;
			 smentry = incomeExpenditureStatement.getEntries(); 
			 if(null!=smentry&&smentry.size()>0) {
				
				 List<MinorScheduleRestData> finallist = new ArrayList<MinorScheduleRestData>();
				 finallist= segregateIncomeExpendStatementlist(smentry);
				 
				 if(null==finallist) {
					 return new ResponseEntity<>(ResponseInfoWrapper.builder()
								.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
								.responseBody("No Data").build(),getHeaders(),  HttpStatus.OK);
				 }
				 ModelMap m =new ModelMap();
				 m.addAttribute("Report Type","Minor Schedules");
				 m.addAttribute("Year Range",cFinancialYear.getFinYearRange());
				 m.addAttribute("currentyear",incomeExpenditureReportAction.getCurrentYearToDate());
				 m.addAttribute("previousyear",incomeExpenditureReportAction.getPreviousYearToDate());
				 m.addAttribute("minorschdulelist", finallist);
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(m).build(),getHeaders(),  HttpStatus.OK);
			 }else {
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("No Data").build(),getHeaders(),  HttpStatus.OK);
			 }
			
					
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllIncomeExpentiureSchedules", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllIncomeExpentiureSchedules(@RequestParam(name = "fin_id") Long fin,HttpServletRequest req ){
	
			
			
		
			Department d = null;
			CFinancialYear cf = new CFinancialYear();
			Fund f = new Fund();
			cf.setId(fin);
			//d.setCode("0");
			f.setId(0);
			
			Statement incomeExpenditureStatement=  new  Statement (cf,d,f);
			incomeExpenditureStatement.setRestData(true);
			incomeExpenditureStatement.setRestData(true);
			CFinancialYear cFinancialYear = null;
			cFinancialYear = cFinancialYearService.findOne(incomeExpenditureStatement.getFinancialYear().getId());
			if(null==cFinancialYear) {
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Financial Year").build(),getHeaders(),  HttpStatus.OK);
			}
			incomeExpenditureStatement= incomeExpenditureReportAction.populateSchedulewiseDetailCodeReport2(incomeExpenditureStatement);
			
				
		
			 List<IEStatementEntry> ieEntries = new ArrayList<IEStatementEntry>();
			
			
			 List<StatementEntry> smentry =  new ArrayList<StatementEntry>();
			 smentry=null;
			 ieEntries = null;
			 ieEntries = incomeExpenditureStatement.getIeEntries();
		
			 ieEntries = removeEmptyEntries(ieEntries);
			 
			 //System.out.println(ieEntries.size());
			 
			 smentry = incomeExpenditureStatement.getEntries(); 
			 if(null!=ieEntries&&ieEntries.size()>0) {
				
				 List<ScheduleReportRestData> finallist = new ArrayList<ScheduleReportRestData>();
				
				 finallist= segregateIncomeExpendIElist(ieEntries);
				 if(null==finallist) {
					 return new ResponseEntity<>(ResponseInfoWrapper.builder()
								.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
								.responseBody("No Data").build(),getHeaders(),  HttpStatus.OK);
				 }
				 
				 ModelMap m =new ModelMap();
				 m.addAttribute("Report Type","All Schedules");
				 m.addAttribute("Year Range",cFinancialYear.getFinYearRange());
				 m.addAttribute("currentyear",incomeExpenditureReportAction.getCurrentYearToDate());
				 m.addAttribute("previousyear",incomeExpenditureReportAction.getPreviousYearToDate());
				 m.addAttribute("Allschedulelist", finallist);
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(m).build(),getHeaders(),  HttpStatus.OK);
			 }else {
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("No Data").build(),getHeaders(), HttpStatus.OK);
			 }
			
					
	}
	
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllBudgetVarianceReportRest", method = RequestMethod.GET)
	@CrossOrigin(origins = {"http://localhost:3010","https://egov.chandigarhsmartcity.in","https://egov-uat.chandigarhsmartcity.in","https://egov-dev.chandigarhsmartcity.in"}, allowedHeaders = "*")
	public ResponseEntity<ResponseInfoWrapper>  getAllBudgetVarianceReportRest(HttpServletRequest req ){
	
			Department d = null;
			CFinancialYear cf = new CFinancialYear();
			Fund f = new Fund();
			List<BudgetVarianceEntry> budgetVarianceEntry = new ArrayList<BudgetVarianceEntry>();
			List<BudgetVarianceEntryRestData>finallist= new ArrayList<BudgetVarianceEntryRestData>();
			budgetVarianceEntry=null;
			budgetVarianceEntry = budgetVarianceReportAction.populateRestData();
			
			if(null!=budgetVarianceEntry) {
				
				budgetVarianceEntry.stream().forEach(bv->{
					BudgetVarianceEntryRestData o = new BudgetVarianceEntryRestData();
					o.setAe(bv.getActual());
					o.setBe(bv.getTotal());
					o.setAdditionalAppropriation(bv.getAdditionalAppropriation());
					o.setBudgetCode(bv.getBudgetCode());
					o.setBudgetHead(bv.getBudgetHead());
					o.setDepartmentCode(bv.getDepartmentCode());
					o.setDepartmentName(bv.getDepartmentName());
					o.setDetailId(bv.getDetailId());
					o.setEstimate(bv.getEstimate());
					o.setFunctionCode(bv.getFunctionCode());
					o.setFundCode(bv.getFundCode());
					o.setVariance(bv.getVariance());
					finallist.add(o);
					
				});
				
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(finallist).build(),getHeaders(), HttpStatus.OK);
			}
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("No Data").build(),getHeaders(), HttpStatus.OK);
			 
			
					
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllIncomeExpentiureByDeptAndDate", method = RequestMethod.GET)
	public ResponseEntity<ResponseInfoWrapper>  getAllIncomeExpentiureByDate( @RequestParam(name = "org_id") Long o,
			@RequestParam(name="fromDate") Date fromDate,@RequestParam(name="toDate") Date toDate,
			HttpServletRequest req ){
			Department d = new Department();	
			Fund f = new Fund();
			d.setCode(o.toString());
			f.setId(0);	
			if(null==fromDate|| null==toDate || null==o ) {
				
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Request, Required Parameters not Passed ").build(),getHeaders(),  HttpStatus.OK);
			}
			
			org.egov.infra.admin.master.entity.Department dt = departmentService.getDepartmentByCode(d.getCode());
			
			
			if(null==dt) {
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Department ").build(),getHeaders(),  HttpStatus.OK);
			}
			final Statement incomeExpenditureStatement=  new  Statement ();
			incomeExpenditureStatement.setRestData(true);
			
			incomeExpenditureStatement.setDepartment(d);;
			incomeExpenditureStatement.setFromDate(fromDate);
			incomeExpenditureStatement.setToDate(toDate);
			incomeExpenditureStatement.setPeriod("Date");
			incomeExpenditureReportAction.populateDataSource2(incomeExpenditureStatement);
			
			
			 List<IEStatementEntry> ieEntries = new ArrayList<IEStatementEntry>();
			
			 ieEntries = incomeExpenditureReportAction.getIncomeExpenditureStatement().getIeEntries();
			 if(null!=ieEntries)
			 ieEntries=removeEmptyEntries(ieEntries);
			
			 if(null!=ieEntries && ieEntries.size()>0) {
				 
				 ModelMap m =new ModelMap();
				 m.addAttribute("Report Type","Income And Expenditure Report");
				 m.addAttribute("departmentname", dt.getName());
				 m.addAttribute("currentyear",incomeExpenditureReportAction.getCurrentYearToDate());
				 m.addAttribute("previousyear",incomeExpenditureReportAction.getPreviousYearToDate());
				 m.addAttribute("incomeExpenditureList", ieEntries);
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(m).build(), getHeaders(),HttpStatus.OK);
			 }else {
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("no data").build(),getHeaders(), HttpStatus.OK);
			 }
		
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "getAllIncomeExpentiureByFromToDate", method = RequestMethod.GET)
	public ResponseEntity<ResponseInfoWrapper>  getAllIncomeExpentiureByFromToDate( 
			@RequestParam(name="fromDate") Date fromDate,@RequestParam(name="toDate") Date toDate,
			HttpServletRequest req ){
		
		
			Department d = new Department();
			Fund f = new Fund();
			d.setCode(null);
			f.setId(0);
			
			
			if(null==fromDate|| null==toDate ) {
				
				return new ResponseEntity<>(ResponseInfoWrapper.builder()
						.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
						.responseBody("Invalid Request, Required Parameters not Passed ").build(),getHeaders(),  HttpStatus.OK);
			}
			
			
			final Statement incomeExpenditureStatement=  new  Statement ();
			incomeExpenditureStatement.setRestData(true);
			
			incomeExpenditureStatement.setDepartment(d);
			incomeExpenditureStatement.setFromDate(fromDate);
			incomeExpenditureStatement.setToDate(toDate);
			incomeExpenditureStatement.setPeriod("Date");
			incomeExpenditureReportAction.populateDataSource2(incomeExpenditureStatement);
			 List<IEStatementEntry> ieEntries = new ArrayList<IEStatementEntry>();
			
			 ieEntries = incomeExpenditureReportAction.getIncomeExpenditureStatement().getIeEntries();
			 if(null!=ieEntries)
			 ieEntries=removeEmptyEntries(ieEntries);
			
			 if(null!=ieEntries && ieEntries.size()>0) {
				 
				 ModelMap m =new ModelMap();
				 m.addAttribute("Report Type","Income And Expenditure Report");
				// m.addAttribute("departmentname", dt.getName());
				 m.addAttribute("currentyear",incomeExpenditureReportAction.getCurrentYearToDate());
				 m.addAttribute("previousyear",incomeExpenditureReportAction.getPreviousYearToDate());
				 m.addAttribute("incomeExpenditureList", ieEntries);
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody(m).build(), getHeaders(),HttpStatus.OK);
			 }else {
				 return new ResponseEntity<>(ResponseInfoWrapper.builder()
							.responseInfo(ResponseInfo.builder().status(SUCCESS).build())
							.responseBody("no data").build(),getHeaders(), HttpStatus.OK);
			 }
		
	}
	
	
	
	
	private List<MinorScheduleRestData>segregateIncomeExpendStatementlist(List<StatementEntry> smentry){
		
		List<MinorScheduleRestData> income=new ArrayList<MinorScheduleRestData>();
		List<MinorScheduleRestData> expense=new ArrayList<MinorScheduleRestData>();
		List<MinorScheduleRestData> finallist=new ArrayList<MinorScheduleRestData>();
		smentry.stream().forEach(s->{
			MinorScheduleRestData m = new MinorScheduleRestData();
			if(null!=s.getGlCode()&&s.getGlCode().startsWith("1")) {
				m.setAccountName(s.getAccountName());
				m.setCurrentYearTotal(s.getCurrentYearTotal());
				m.setFundWiseAmount(s.getFundWiseAmount());
				m.setFundCode(s.getFundCode());
				m.setGlCode(s.getGlCode());
				m.setPreviousYearTotal(s.getPreviousYearTotal());
				m.setScheduleNo(s.getScheduleNo());
				m.setType("Income");
				income.add(m);
				
			}
			if(null!=s.getGlCode()&&s.getGlCode().startsWith("2")) {
				m.setAccountName(s.getAccountName());
				m.setCurrentYearTotal(s.getCurrentYearTotal());
				m.setFundWiseAmount(s.getFundWiseAmount());
				m.setFundCode(s.getFundCode());
				m.setGlCode(s.getGlCode());
				m.setPreviousYearTotal(s.getPreviousYearTotal());
				m.setScheduleNo(s.getScheduleNo());
				m.setType("Expense");
				income.add(m);
				
			}
		});
		
		finallist.addAll(income);
		finallist.addAll(expense);
		return finallist;
	}
	
	
	
private List<ScheduleReportRestData>segregateIncomeExpendIElist(List<IEStatementEntry> smentry){
		
		List<ScheduleReportRestData> income=new ArrayList<ScheduleReportRestData>();
		List<ScheduleReportRestData> expense=new ArrayList<ScheduleReportRestData>();
		List<ScheduleReportRestData> finallist=new ArrayList<ScheduleReportRestData>();
		smentry.stream().forEach(s->{
			ScheduleReportRestData m = new ScheduleReportRestData();
			if(null!=s.getGlCode()&&s.getGlCode().startsWith("1")) {
				m.setAccountName(s.getAccountName());
				m.setBudgetAmount(s.getBudgetAmount());
				m.setMajorCode(s.getMajorCode());
				m.setNetAmount(s.getNetAmount());
				m.setPreviousYearAmount(s.getPreviousYearAmount());
				m.setGlCode(s.getGlCode());
				m.setScheduleNo(s.getScheduleNo());
				m.setType("Income");
				income.add(m);
				
			}
			if(null!=s.getGlCode()&&s.getGlCode().startsWith("2")) {
				m.setAccountName(s.getAccountName());
				m.setAccountName(s.getAccountName());
				m.setBudgetAmount(s.getBudgetAmount());
				m.setMajorCode(s.getMajorCode());
				m.setNetAmount(s.getNetAmount());
				m.setPreviousYearAmount(s.getPreviousYearAmount());
				m.setGlCode(s.getGlCode());
				m.setScheduleNo(s.getScheduleNo());
				m.setType("Expense");
				income.add(m);
				
			}
		});
		
		finallist.addAll(income);
		finallist.addAll(expense);
		return finallist;
	}
	
	
	
	
	
	
	
	public  List<IEStatementEntry> removeEmptyEntries( List<IEStatementEntry>  eiEntries){
		int i=0;
		List<IEStatementEntry> res= new ArrayList<IEStatementEntry>();
		if(eiEntries.size()>0)
		for(IEStatementEntry e: eiEntries) {
			
			if(null==e.getAccountName()&&null==e.getBudgetAmount()&&null==e.getGlCode()&&null==e.getMajorCode()&&e.getNetAmount().isEmpty() && e.getPreviousYearAmount().isEmpty()&&null==e.getScheduleNo()&& null==e.getBudgetAmount() ) {
				//eiEntries.remove(i);
				//System.out.println("Empty");
			}else {
				//System.out.println("Added"+i);
				res.add(e);
			}
			i++;		
		}
		return res;
	}



	public HttpHeaders getHeaders() {
		
		return headers = updateHeaders(headers);
	}



	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}
	
	public HttpHeaders updateHeaders(HttpHeaders headers) {
		allowheaderList.clear();
		allowheaderList.add("https://egov.chandigarhsmartcity.in");
		allowheaderList.add("https://egov-dev.chandigarhsmartcity.in");
		allowheaderList.add("https://egov-uat.chandigarhsmartcity.in");
		allowheaderList.add("https://mcc.chandigarhsmartcity.in");
		allowheaderList.add("https://chandigarh-dev.chandigarhsmartcity.in");
		allowheaderList.add("https://chandigarh-uat.chandigarhsmartcity.in");
		allowheaderList.add("http://localhost:3010");
		headers.set(headername, headervalue);
		headers.setAccessControlAllowHeaders(allowheaderList);
		return headers;
		
	}
	
}
