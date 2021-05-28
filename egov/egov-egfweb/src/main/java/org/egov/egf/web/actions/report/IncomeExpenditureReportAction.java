/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.egf.web.actions.report;


import net.sf.jasperreports.engine.JasperPrint;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.service.CFinancialYearService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.service.FunctionaryService;
import org.egov.commons.service.FundService;
import org.egov.egf.model.IEStatementEntry;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.services.report.IncomeExpenditureScheduleService;
import org.egov.services.report.IncomeExpenditureService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@ParentPackage("egov")
@Results({
    @Result(name = "report", location = "incomeExpenditureReport-report.jsp"),
    @Result(name = "scheduleResults", location = "incomeExpenditureReport-scheduleResults.jsp"),
    @Result(name = "allScheduleResults", location = "incomeExpenditureReport-allScheduleResults.jsp"),
    @Result(name = "results", location = "incomeExpenditureReport-results.jsp"),
    @Result(name = "PDF", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
            Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/pdf", Constants.CONTENT_DISPOSITION,
    "no-cache;filename=IncomeExpenditureStatement.pdf" }),
    @Result(name = "XLS", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
            Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/xls", Constants.CONTENT_DISPOSITION,
    "no-cache;filename=IncomeExpenditureStatement.xls" })
})
public class IncomeExpenditureReportAction extends BaseFormAction {
    /**
     *
     */
    private static final long serialVersionUID = 91711010096900620L;
    private static final String INCOME_EXPENSE_PDF = "PDF";
    private static final String INCOME_EXPENSE_XLS = "XLS";
    private static SimpleDateFormat FORMATDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    InputStream inputStream;
    ReportHelper reportHelper;
    Statement incomeExpenditureStatement = new Statement();
    @Autowired
    IncomeExpenditureService incomeExpenditureService;
    @Autowired
    IncomeExpenditureScheduleService incomeExpenditureScheduleService;
    private String majorCode;
    private String minorCode;
    private String scheduleNo;
    private String financialYearId;
    // private String asOndate;
    private Date todayDate;
    private String fromDate;
    private String toDate;
    private String asOnDateRange;
    private String period;
    private Integer fundId;
    private final StringBuffer heading = new StringBuffer();
    private StringBuffer scheduleheading = new StringBuffer();
    private StringBuffer statementheading = new StringBuffer();
    List<CChartOfAccounts> listChartOfAccounts;
    private boolean detailReport = false;
    //Added By Bikash Dhal For IncomeExpenditureExcelSheet
    private byte[]excelData=null;
   
 @Autowired
 @Qualifier("persistenceService")
 private PersistenceService persistenceService;
 @Autowired
    private EgovMasterDataCaching masterDataCache;
 
     @Autowired
     private CityService cityService;
    
     
     @Autowired
  private   FundService fundService;
     
     @Autowired
  private   CFinancialYearService cFinancialYearService;
     
     @Autowired
   private  FunctionService functionService;
     
     @Autowired
    private  BoundaryService boundaryService;
     @Autowired
     private FunctionaryService functionaryService;
     
     @Autowired
     private  DepartmentService departmentService;
     
    
    public void setIncomeExpenditureService(final IncomeExpenditureService incomeExpenditureService) {
        this.incomeExpenditureService = incomeExpenditureService;
    }

    public void setIncomeExpenditureScheduleService(final IncomeExpenditureScheduleService incomeExpenditureScheduleService) {
        this.incomeExpenditureScheduleService = incomeExpenditureScheduleService;
    }

    public void setReportHelper(final ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Statement getIncomeExpenditureStatement() {
        return incomeExpenditureStatement;
    }

    public IncomeExpenditureReportAction() {
        addRelatedEntity("department", Department.class);
        addRelatedEntity("function", CFunction.class);
        addRelatedEntity("functionary", Functionary.class);
        addRelatedEntity("financialYear", CFinancialYear.class);
        addRelatedEntity("field", Boundary.class);
        addRelatedEntity("fund", Fund.class);
    }

	
	  @Override public void prepare() {
		
        persistenceService.getSession().setDefaultReadOnly(true);
        persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
        super.prepare();
        if (!parameters.containsKey("showDropDown")) {
            addDropdownData("departmentList", masterDataCache.get("egi-department"));
				  addDropdownData("functionList", masterDataCache.get("egi-function")); //
				  addDropdownData("functionaryList", masterDataCache.get("egi-functionary"));
				  addDropdownData("fundDropDownList", masterDataCache.get("egi-fund")); //
				  addDropdownData("fieldList", masterDataCache.get("egi-ward"));
				  addDropdownData("financialYearList", getPersistenceService().findAllBy("from CFinancialYear where isActive=true  order by finYearRange desc " ));
			  
        }
			  
		 
	 
    }


    protected void setRelatedEntitesOn() {
        setTodayDate(new Date());
        if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
                && incomeExpenditureStatement.getFund().getId() != 0) {
            incomeExpenditureStatement.setFund((Fund) getPersistenceService().find("from Fund where id=?",
                    incomeExpenditureStatement.getFund().getId()));
            heading.append(" in " + incomeExpenditureStatement.getFund().getName());
        }
        if (incomeExpenditureStatement.getDepartment() != null && incomeExpenditureStatement.getDepartment().getCode() != null
                && !incomeExpenditureStatement.getDepartment().getCode().isEmpty()) {

            Department dept = microserviceUtils.getDepartmentByCode(incomeExpenditureStatement.getDepartment().getCode());
            incomeExpenditureStatement.setDepartment(dept);
            heading.append(" in " + incomeExpenditureStatement.getDepartment().getName() + " Department");
        } else
            incomeExpenditureStatement.setDepartment(null);
        if (incomeExpenditureStatement.getFinancialYear() != null
                && incomeExpenditureStatement.getFinancialYear().getId() != null
                && incomeExpenditureStatement.getFinancialYear().getId() != 0) {
            incomeExpenditureStatement.setFinancialYear((CFinancialYear) getPersistenceService().find(
                    "from CFinancialYear where id=?", incomeExpenditureStatement.getFinancialYear().getId()));
            heading.append(" for the Financial Year " + incomeExpenditureStatement.getFinancialYear().getFinYearRange());
        }
        if (incomeExpenditureStatement.getFunction() != null && incomeExpenditureStatement.getFunction().getId() != null
                && incomeExpenditureStatement.getFunction().getId() != 0) {
            incomeExpenditureStatement.setFunction((CFunction) getPersistenceService().find("from CFunction where id=?",
                    incomeExpenditureStatement.getFunction().getId()));
            heading.append(" in Function Code " + incomeExpenditureStatement.getFunction().getName());
        }
        if (incomeExpenditureStatement.getField() != null && incomeExpenditureStatement.getField().getId() != null
                && incomeExpenditureStatement.getField().getId() != 0) {
            incomeExpenditureStatement.setField((Boundary) getPersistenceService().find("from Boundary where id=?",
                    incomeExpenditureStatement.getField().getId()));
            heading.append(" in the field value" + incomeExpenditureStatement.getField().getName());
        }

        if (incomeExpenditureStatement.getFunctionary() != null && incomeExpenditureStatement.getFunctionary().getId() != null
                && incomeExpenditureStatement.getFunctionary().getId() != 0) {
            incomeExpenditureStatement.setFunctionary((Functionary) getPersistenceService().find("from Functionary where id=?",
                    incomeExpenditureStatement.getFunctionary().getId()));
            heading.append(" and " + incomeExpenditureStatement.getFunctionary().getName() + " Functionary");
        }

    }

    public void setIncomeExpenditureStatement(final Statement incomeExpenditureStatement) {
        this.incomeExpenditureStatement = incomeExpenditureStatement;
    }

    @Override
    public Object getModel() {
        return incomeExpenditureStatement;
    }

    @Action(value = "/report/incomeExpenditureReport-generateIncomeExpenditureReport")
    public String generateIncomeExpenditureReport() {
        return "report";
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateIncomeExpenditureSubReport")
    public String generateIncomeExpenditureSubReport() {
        setDetailReport(false);
        populateDataSourceForSchedule();
        return "scheduleResults";
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateScheduleReport")
    public String generateScheduleReport() {
        populateDataSourceForAllSchedules();
        return "allScheduleResults";
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateDetailCodeReport")
    public String generateDetailCodeReport() {
        setDetailReport(true);
        populateSchedulewiseDetailCodeReport();
        return "scheduleResults";
    }

    private void populateSchedulewiseDetailCodeReport() {
        setRelatedEntitesOn();
        scheduleheading.append("Income And Expenditure Schedule Statement").append(heading);
        if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
                && incomeExpenditureStatement.getFund().getId() != 0) {
            final List<Fund> fundlist = new ArrayList<Fund>();
            fundlist.add(incomeExpenditureStatement.getFund());
            incomeExpenditureStatement.setFunds(fundlist);
            incomeExpenditureScheduleService.populateDetailcode(incomeExpenditureStatement);

        } else {
            incomeExpenditureStatement.setFunds(incomeExpenditureService.getFunds());
            incomeExpenditureScheduleService.populateDetailcode(incomeExpenditureStatement);
        }
    }


    private void populateDataSourceForSchedule() {
        setDetailReport(false);
        setRelatedEntitesOn();

        scheduleheading.append("Income And Expenditure Schedule Statement").append(heading);
        if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
                && incomeExpenditureStatement.getFund().getId() != 0) {
            final List<Fund> fundlist = new ArrayList<Fund>();
            fundlist.add(incomeExpenditureStatement.getFund());
            incomeExpenditureStatement.setFunds(fundlist);
            incomeExpenditureScheduleService.populateDataForLedgerSchedule(incomeExpenditureStatement,
                    parameters.get("majorCode")[0]);

        } else {
            incomeExpenditureStatement.setFunds(incomeExpenditureService.getFunds());
            incomeExpenditureScheduleService.populateDataForLedgerSchedule(incomeExpenditureStatement,
                    parameters.get("majorCode")[0]);

        }
    }

    private void populateDataSourceForAllSchedules() {
        setRelatedEntitesOn();
        if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
                && incomeExpenditureStatement.getFund().getId() != 0) {
            final List<Fund> fundlist = new ArrayList<Fund>();
            fundlist.add(incomeExpenditureStatement.getFund());
            incomeExpenditureStatement.setFunds(fundlist);
            incomeExpenditureScheduleService.populateDataForAllSchedules(incomeExpenditureStatement);
        } else {
            incomeExpenditureStatement.setFunds(incomeExpenditureService.getFunds());
            incomeExpenditureScheduleService.populateDataForAllSchedules(incomeExpenditureStatement);
        }
    }

    public String printIncomeExpenditureReport() {
        populateDataSource();
        return "report";
    }

    @Action(value = "/report/incomeExpenditureReport-ajaxPrintIncomeExpenditureReport")
    public String ajaxPrintIncomeExpenditureReport() {
    	try {
        populateDataSource();
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        return "results";
    }

    public void populateDataSource() {
    	
        setRelatedEntitesOn();
       // System.out.println(incomeExpenditureStatement.getFund().getId());
       // System.out.println(incomeExpenditureStatement.getFinancialYear().getId());
       // System.out.println(incomeExpenditureStatement.getDepartment().getCode());
        statementheading.append("Income And Expenditure Statement").append(heading);
        if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
                && incomeExpenditureStatement.getFund().getId() != 0) {
            final List<Fund> fundlist = new ArrayList<Fund>();
            fundlist.add(incomeExpenditureStatement.getFund());
            incomeExpenditureStatement.setFunds(fundlist);
            incomeExpenditureService.populateIEStatement(incomeExpenditureStatement);
            System.out.println("Inside If");
        } else {
        	// System.out.println("Inside Else");
        	// System.out.println(incomeExpenditureService.getFunds().size());
            incomeExpenditureStatement.setFunds(incomeExpenditureService.getFunds());
           // System.out.println("FUND SIZE:::::::-----"+incomeExpenditureService.getFunds().size());
            incomeExpenditureService.populateIEStatement(incomeExpenditureStatement);
        }
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateIncomeExpenditurePdf")
    public String generateIncomeExpenditurePdf() throws Exception {
        populateDataSource();
        final String heading = ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) + "\\n" + statementheading.toString();
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate());
        final JasperPrint jasper = reportHelper.generateIncomeExpenditureReportJasperPrint(incomeExpenditureStatement, heading,
                getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return INCOME_EXPENSE_PDF;
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateDetailCodePdf")
    public String generateDetailCodePdf() throws Exception {
        populateSchedulewiseDetailCodeReport();
        final String heading = ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) + "\\n" + statementheading.toString();
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate());
        final JasperPrint jasper = reportHelper.generateIncomeExpenditureReportJasperPrint(incomeExpenditureStatement, heading,
                getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return INCOME_EXPENSE_PDF;
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateDetailCodeXls")
    public String generateDetailCodeXls() throws Exception {
        populateSchedulewiseDetailCodeReport();
		/*
		 * final String heading = ReportUtil.getCityName()
		 * +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) +
		 * "\\n" + statementheading.toString(); final String subtitle =
		 * "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()) +
		 * "                                               "; final JasperPrint jasper =
		 * reportHelper.generateIncomeExpenditureReportJasperPrint(
		 * incomeExpenditureStatement, heading, getPreviousYearToDate(),
		 * getCurrentYearToDate(), subtitle, true); inputStream =
		 * reportHelper.exportXls(inputStream, jasper);
		 */
        
        final String heading = ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) + " " + statementheading.toString();
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
                + "                                               ";
		 
        
        System.out.println("Schedule Action");
        Map<String, String> headerData = new HashMap<>();
        headerData.put("h1", heading);
        headerData.put("h2", subtitle);
        headerData.put("h3", "Amount in "+incomeExpenditureStatement.getCurrency());
        headerData.put("h4", "Account Code");
        headerData.put("h5","Head Of Account");
        byte[]excelData = getScheduleResultsExcelSheet(headerData,incomeExpenditureStatement);
        inputStream = new ByteArrayInputStream(excelData);
        
        return INCOME_EXPENSE_XLS;
    }

   /* public String getUlbName() {
        final Query query = persistenceService.getSession().createSQLQuery(
                "select name from companydetail");
        final List<String> result = query.list();
        if (result != null)
            return result.get(0);
        return "";
    }*/


    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateIncomeExpenditureXls")
    public String generateIncomeExpenditureXls() throws Exception {
       
    	try {
        populateDataSource();
        System.out.println(incomeExpenditureStatement.getFunds().get(0));
        final String heading = ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) + " " + statementheading.toString();
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
                + "                                               ";
		System.out.println(incomeExpenditureStatement.getIeEntries().size());
		
        
        	Map<String, String> headerData = new HashMap<>();
            headerData.put("h1", heading);
            headerData.put("h2", subtitle);
            headerData.put("h3", "Amount in "+incomeExpenditureStatement.getCurrency());
            headerData.put("h4", "Account Code");
            headerData.put("h5","Head Of Account");
            headerData.put("h6","Schedule No");
           
            headerData.put("h8",getPreviousYearToDate());
            headerData.put("h9", getCurrentYearToDate());
            
            System.out.println("TEST 2"); 
			 
           
			  
			  //inputStream = new ByteArrayInputStream(excelData);
            
           excelData = getIncomeExpenditureExcelSheet(headerData,incomeExpenditureStatement);
            	System.out.println(excelData.length);
            	if(excelData.length>0) {
           inputStream = new ByteArrayInputStream(excelData);
            	}
            		
            	else {
            excelData = noDataExcel(headerData);
		}
    	}	
            	
            catch(Exception ex) {
            	return "report";
            }
            	
        
		
        return INCOME_EXPENSE_XLS;
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateSchedulePdf")
    public String generateSchedulePdf() throws Exception {
        populateDataSourceForAllSchedules();
        final JasperPrint jasper = reportHelper.generateFinancialStatementReportJasperPrint(incomeExpenditureStatement,
                getText("report.ie.heading"), heading.toString(),
                getPreviousYearToDate(), getCurrentYearToDate(), false);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return INCOME_EXPENSE_PDF;
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateScheduleXls")
    public String generateScheduleXls() throws Exception {
        populateDataSourceForAllSchedules();
		/*
		 * final JasperPrint jasper =
		 * reportHelper.generateFinancialStatementReportJasperPrint(
		 * incomeExpenditureStatement, getText("report.ie.heading"), heading.toString(),
		 * getPreviousYearToDate(), getCurrentYearToDate(), false); inputStream =
		 * reportHelper.exportXls(inputStream, jasper);
		 */
        
        
        final String heading = ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) + " " + scheduleheading.toString();
        // Blank space for space didvidion between left and right corner
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()) + "					  						 ";
		
        System.out.println(getText("model.financialYear.finYearRange"));
        System.out.println(getText("report.amount.in.rupees"));
       
        
        System.out.println("Minor Schedule Action");
        Map<String, String> headerData = new HashMap<>();
        headerData.put("h1", heading);
        headerData.put("h2", subtitle);
        headerData.put("h3", "Amount in "+incomeExpenditureStatement.getCurrency());
        headerData.put("h4", "Account Code");
        headerData.put("h5","Head Of Account");
        byte[]excelData = getMinorScheduleResultsExcelSheet(headerData,incomeExpenditureStatement);
        inputStream = new ByteArrayInputStream(excelData);
        return INCOME_EXPENSE_XLS;
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateIncomeExpenditureSchedulePdf")
    public String generateIncomeExpenditureSchedulePdf() throws Exception {
        populateDataSourceForSchedule();
        final String heading = ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) + "\\n" + scheduleheading.toString();
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
                + "                                             ";
        final JasperPrint jasper = reportHelper.generateIncomeExpenditureReportJasperPrint(incomeExpenditureStatement, heading,
                getPreviousYearToDate(), getCurrentYearToDate(), subtitle, false);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return INCOME_EXPENSE_PDF;
    }

    @ReadOnly
    @Action(value = "/report/incomeExpenditureReport-generateIncomeExpenditureScheduleXls")
    public String generateIncomeExpenditureScheduleXls() throws Exception {
        populateDataSourceForSchedule();
		/*
		 * final String heading = ReportUtil.getCityName()
		 * +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) +
		 * "\\n" + scheduleheading.toString(); // Blank space for space didvidion
		 * between left and right corner final String subtitle = "Report Run Date-" +
		 * FORMATDDMMYYYY.format(getTodayDate()) +
		 * "					  						 "; final JasperPrint jasper =
		 * reportHelper.generateIncomeExpenditureReportJasperPrint(
		 * incomeExpenditureStatement, heading, getPreviousYearToDate(),
		 * getCurrentYearToDate(), subtitle, false); inputStream =
		 * reportHelper.exportXls(inputStream, jasper);
		 */
        
        
        final String heading = ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) + " " + scheduleheading.toString();
        // Blank space for space didvidion between left and right corner
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()) + "					  						 ";
		
        System.out.println("Schedule Action");
        Map<String, String> headerData = new HashMap<>();
        headerData.put("h1", heading);
        headerData.put("h2", subtitle);
        headerData.put("h3", "Amount in "+incomeExpenditureStatement.getCurrency());
        headerData.put("h4", "Account Code");
        headerData.put("h5","Head Of Account");
        byte[]excelData = getScheduleResultsExcelSheet(headerData,incomeExpenditureStatement);
        inputStream = new ByteArrayInputStream(excelData);
        return INCOME_EXPENSE_XLS;
    }

   /* public String getCurrentYearToDate() {
    	
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getToDate(incomeExpenditureStatement));
    }

    public String getPreviousYearToDate() {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getPreviousYearFor(incomeExpenditureService
                .getToDate(incomeExpenditureStatement)));
    }

    public String getCurrentYearFromDate() {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getFromDate(incomeExpenditureStatement));
    }

    public String getPreviousYearFromDate() {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getPreviousYearFor(incomeExpenditureService
                .getFromDate(incomeExpenditureStatement)));
    }*/
    public String getCurrentYearToDate() {
	   if ("Date".equalsIgnoreCase(incomeExpenditureStatement.getPeriod()))
	   {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureStatement.getFromDate())  +" To "+incomeExpenditureService.getFormattedDate(incomeExpenditureStatement.getToDate());
	   }else {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getToDate(incomeExpenditureStatement));
    }
    }

    public String getPreviousYearToDate() {
    	if ("Date".equalsIgnoreCase(incomeExpenditureStatement.getPeriod()))
 	   {
    		  return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getPreviousYearFor(incomeExpenditureStatement.getFromDate())) + " To " +
    				  incomeExpenditureService.getFormattedDate(incomeExpenditureService.getPreviousYearFor(incomeExpenditureStatement.getToDate()));
 	   }else {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getPreviousYearFor(incomeExpenditureService
                .getToDate(incomeExpenditureStatement)));
    }
    }

    public String getCurrentYearFromDate() {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getFromDate(incomeExpenditureStatement));
    }

    public String getPreviousYearFromDate() {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getPreviousYearFor(incomeExpenditureService
                .getFromDate(incomeExpenditureStatement)));
    }
    public String getCurrentYearToDateStr() {
      
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getToDate(incomeExpenditureStatement));
    }

    public String getPreviousYearToDateStr() {
        return incomeExpenditureService.getFormattedDate(incomeExpenditureService.getPreviousYearFor(incomeExpenditureService
                .getToDate(incomeExpenditureStatement)));
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(final Date todayDate) {
        this.todayDate = todayDate;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(final String majorCode) {
        this.majorCode = majorCode;
    }

    public String getMinorCode() {
        return minorCode;
    }

    public void setMinorCode(final String minorCode) {
        this.minorCode = minorCode;
    }

    public String getScheduleNo() {
        return scheduleNo;
    }

    public void setScheduleNo(final String scheduleNo) {
        this.scheduleNo = scheduleNo;
    }

    public List<CChartOfAccounts> getListChartOfAccounts() {
        return listChartOfAccounts;
    }

    public void setListChartOfAccounts(final List<CChartOfAccounts> listChartOfAccounts) {
        this.listChartOfAccounts = listChartOfAccounts;
    }

    public String getFinancialYearId() {
        return financialYearId;
    }

    public void setFinancialYearId(final String financialYearId) {
        this.financialYearId = financialYearId;
    }

    public Integer getFundId() {
        return fundId;
    }

    public void setFundId(final Integer fundId) {
        this.fundId = fundId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(final String period) {
        this.period = period;
    }

    public String getAsOnDateRange() {
        return asOnDateRange;
    }

    public void setAsOnDateRange(final String asOnDateRange) {
        this.asOnDateRange = asOnDateRange;
    }

    public StringBuffer getScheduleheading() {
        return scheduleheading;
    }

    public void setScheduleheading(final StringBuffer scheduleheading) {
        this.scheduleheading = scheduleheading;
    }

    public StringBuffer getStatementheading() {
        return statementheading;
    }

    public void setStatementheading(final StringBuffer statementheading) {
        this.statementheading = statementheading;
    }

    public boolean isDetailReport() {
        return detailReport;
    }

    public void setDetailReport(final boolean detailReport) {
        this.detailReport = detailReport;
    }

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

public void populateDataSource2(Statement s) {
    		try {
    			incomeExpenditureStatement= setRelatedEntitesOn2(s);
    		}catch(Exception ex) {
    	
    		}
    		
    	            incomeExpenditureStatement.setFunds(fundService.findAll());
    	            System.out.println(incomeExpenditureStatement.getFunds().size());
    	            System.out.println(incomeExpenditureStatement.getFunds().get(0).getName());
    	            
    	            try {
    	            	 incomeExpenditureService.populateIEStatement2(incomeExpenditureStatement);
    	            	 System.out.println(null==incomeExpenditureService);
    	            }catch(Exception ex) {
    	            	System.out.println("Exception");
    	            	ex.printStackTrace();
    	            }
    	               
    }
    	
       
public void populateDataSourceForApi(Statement s) {
	try {
		incomeExpenditureStatement= setRelatedEntitesOn2(s);
	}catch(Exception ex) {
		
	}
        
            incomeExpenditureStatement.setFunds(fundService.findAll());
            System.out.println(incomeExpenditureStatement.getFunds().size());
            System.out.println(incomeExpenditureStatement.getFunds().get(0).getName());
            
            try {
            	 incomeExpenditureService.populateIEStatementForApi(incomeExpenditureStatement);
            	 System.out.println(null==incomeExpenditureService);
            }catch(Exception ex) {
            	System.out.println("Exception");
            	ex.printStackTrace();
            }
               
}



        

protected Statement setRelatedEntitesOn2(Statement incomeExpenditureStatement) {
    setTodayDate(new Date());
    if (incomeExpenditureStatement.getFund() != null && incomeExpenditureStatement.getFund().getId() != null
            && incomeExpenditureStatement.getFund().getId() != 0) {
        incomeExpenditureStatement.setFund((Fund) fundService.findOne(incomeExpenditureStatement.getFund().getId()));
        heading.append(" in " + incomeExpenditureStatement.getFund().getName());
    }
    if (incomeExpenditureStatement.getDepartment() != null && incomeExpenditureStatement.getDepartment().getCode() != null
            && !incomeExpenditureStatement.getDepartment().getCode().isEmpty()) {
    	System.out.println(incomeExpenditureStatement.getDepartment().getCode());
    	org.egov.infra.admin.master.entity.Department dep = departmentService.getDepartmentByCode(incomeExpenditureStatement.getDepartment().getCode());
    	
    	
        Department dept =new Department(dep.getId(),dep.getName(),dep.getCode());
        
        incomeExpenditureStatement.setDepartment(dept);
        heading.append(" in " + incomeExpenditureStatement.getDepartment().getName() + " Department");
    } else
        incomeExpenditureStatement.setDepartment(null);
    if (incomeExpenditureStatement.getFinancialYear() != null
            && incomeExpenditureStatement.getFinancialYear().getId() != null
            && incomeExpenditureStatement.getFinancialYear().getId() != 0) {
        incomeExpenditureStatement.setFinancialYear((CFinancialYear)cFinancialYearService.findOne(incomeExpenditureStatement.getFinancialYear().getId()) );
        heading.append(" for the Financial Year " + incomeExpenditureStatement.getFinancialYear().getFinYearRange());
    }
    if (incomeExpenditureStatement.getFunction() != null && incomeExpenditureStatement.getFunction().getId() != null
            && incomeExpenditureStatement.getFunction().getId() != 0) {
        incomeExpenditureStatement.setFunction((CFunction)functionService.findOne(incomeExpenditureStatement.getFunction().getId()));
        heading.append(" in Function Code " + incomeExpenditureStatement.getFunction().getName());
	}
    if (incomeExpenditureStatement.getField() != null && incomeExpenditureStatement.getField().getId() != null
            && incomeExpenditureStatement.getField().getId() != 0) {
        incomeExpenditureStatement.setField((Boundary) boundaryService.getBoundaryById(incomeExpenditureStatement.getField().getId()));
        heading.append(" in the field value" + incomeExpenditureStatement.getField().getName());
    }

    
    if (incomeExpenditureStatement.getFunctionary() != null && incomeExpenditureStatement.getFunctionary().getId() != null
            && incomeExpenditureStatement.getFunctionary().getId() != 0) {
        incomeExpenditureStatement.setFunctionary((Functionary)functionaryService.findOne(
        		(Long.parseLong(incomeExpenditureStatement.getFunctionary().getId().toString()))));
        heading.append(" and " + incomeExpenditureStatement.getFunctionary().getName() + " Functionary");
    }
   // System.out.println(heading);
    return incomeExpenditureStatement;
	}
	


	public Statement populateDataSourceForAllSchedules2(Statement s) {
	
	

		incomeExpenditureStatement= setRelatedEntitesOn2(s);
	
        incomeExpenditureStatement.setFunds(fundService.findAll());
        System.out.println(incomeExpenditureStatement.getFunds().size());
        System.out.println(incomeExpenditureStatement.getFunds().get(0).getName());
        
        incomeExpenditureStatement= incomeExpenditureScheduleService.populateDataForAllSchedulesForRestData(incomeExpenditureStatement);
        System.out.println("populateDataSourceForAllSchedules2::::"+ incomeExpenditureStatement.getEntries().size());
        return s;
	
	}
	
	
	
	
	public  Statement populateSchedulewiseDetailCodeReport2(Statement s) {
		try {
			incomeExpenditureStatement= setRelatedEntitesOn2(s);
		}catch(Exception ex) {
			
		}
	   
	        incomeExpenditureStatement.setFunds(incomeExpenditureService.getFunds());
	        incomeExpenditureStatement =   incomeExpenditureScheduleService.populateDetailcodeRestData(incomeExpenditureStatement);
	       
	        return incomeExpenditureStatement;
	    
	}

	
	public  Statement populateSchedulewiseDetailCodeReportApi(Statement s) {
		try {
			incomeExpenditureStatement= setRelatedEntitesOn2(s);
		}catch(Exception ex) {
			
		}
	   
	        incomeExpenditureStatement.setFunds(incomeExpenditureService.getFunds());
	        incomeExpenditureStatement =   incomeExpenditureScheduleService.populateDetailcodeRestDataApi(incomeExpenditureStatement);
	       
	        return incomeExpenditureStatement;
	    
	}

	//Added By Bikash For Income Expenditure EXcel Sheet 1722021
	private byte[] getIncomeExpenditureExcelSheet(Map<String,String>headerData,Statement incomExpenditureStatement) {
		
		byte[]fileContent=null;
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Income And Expenditure Report");
			HSSFCellStyle style = wb.createCellStyle();  
			int i =0;
			Row row1 = sheet.createRow(i++);	  
			Cell c1=  row1.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(headerData.get("h1"));
			
			Row row2 = sheet.createRow(i++);	  
			Cell c2=  row2.createCell(0);
			c2.setCellStyle(style);
			c2.setCellValue(headerData.get("h2"));
			Cell c3=  row2.createCell(3);
			c3.setCellStyle(style);
			c3.setCellValue(headerData.get("h3"));
			
			
			Row row3 = sheet.createRow(i++);	  
			Cell c4=  row3.createCell(0);
			c4.setCellStyle(style);
			c4.setCellValue(headerData.get("h4"));
			
			Cell c5=  row3.createCell(1);
			c5.setCellStyle(style);
			c5.setCellValue(headerData.get("h5"));
			
			Cell c6=  row3.createCell(2);
			c6.setCellStyle(style);
			c6.setCellValue(headerData.get("h6"));
			int j =3;
			for(Fund f:incomeExpenditureStatement.getFunds()) {
				j=j+2;
				Cell c7=  row3.createCell(j);
				c7.setCellStyle(style);
				c7.setCellValue(f.getName());
			}
			
			j=5;
			Row row4 = sheet.createRow(i++);
			
			
			for(Fund f:incomeExpenditureStatement.getFunds()) {

						Cell c8=  row4.createCell(j++);
						c8.setCellStyle(style);
						c8.setCellValue(getCurrentYearToDate());
						
						Cell c9=  row4.createCell(j++);
						c9.setCellStyle(style);
						c9.setCellValue(getPreviousYearToDate());
			}
			
			 for(IEStatementEntry s : incomeExpenditureStatement.getIeEntries()) {
				 
				 Row row = sheet.createRow(i++);
				 Cell cell0 = row.createCell(0);
					Cell cell1 = row.createCell(1);
					Cell cell2 = row.createCell(2);
					
					
					String schedule_num="";
					String account_name="";
					String glcode="";
					
					String net_amount="";
					String prev_amount="";
					
				
					if(null!=s.getScheduleNo()) {
						schedule_num = s.getScheduleNo().toString();
					}
					
					if(null!=s.getAccountName()) {
						account_name =s.getAccountName().toString();
					}
					
					
					if(null!=s.getGlCode()) {
						glcode =s.getGlCode().toString();
					}
					
					j=5;
					for(Fund f:incomeExpenditureStatement.getFunds()) {
						
						Cell cell3 = row.createCell(j++);
						Cell cell4 = row.createCell(j++);
						
						if(null!=s.getNetAmount().get(f.getName())) {
							net_amount =s.getNetAmount().get(f.getName()).toString();
							
						}
						
						if(null!=s.getPreviousYearAmount().get(f.getName())) {
							prev_amount =s.getPreviousYearAmount().get(f.getName()).toString();
						}
						cell3.setCellValue(net_amount);
						cell4.setCellValue(prev_amount);
					}
					cell0.setCellValue(glcode);
					cell1.setCellValue(account_name);
					cell2.setCellValue(schedule_num);
					
		        } 
			
			 int numberOfSheets = wb.getNumberOfSheets();
			 for (int x = 0; x < numberOfSheets; x++) {
			        Sheet sheet1 = wb.getSheetAt(x);
			        int total_row=6;
			        if (sheet1.getPhysicalNumberOfRows() > 0) {
			        	
			        		Row row = sheet1.getRow(total_row);
				            Iterator<Cell> cellIterator = row.cellIterator();
				            while (cellIterator.hasNext()) {
				                Cell cell = cellIterator.next();
				                int columnIndex = cell.getColumnIndex();
				               // System.out.println(columnIndex);
				                sheet1.autoSizeColumn(columnIndex);
				            }
			        	
			            
			        }
			    }
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			wb.write(os);
			 
			
			   fileContent = os.toByteArray();
			
			
		  }catch(Exception ex) {  }
		 
		
		 return fileContent;
	}
	
	private byte[]noDataExcel(Map<String,String> headerData){
		byte[]fileContent=null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Income And Expenditure Report");
			HSSFCellStyle style = wb.createCellStyle(); // Creating Style  
			/*
			 * HSSFFont font = wb.createFont(); font.setFontHeightInPoints((short)11);
			 * font.setFontName("Times New Roman"); font.setBoldweight((short)10);
			 * style.setFont(font);
			 */ 
			int i =0;
			Row row1 = sheet.createRow(i++);	  
			Cell c1=  row1.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(headerData.get("h1"));
			
			Row row2 = sheet.createRow(i++);	  
			Cell c2=  row2.createCell(0);
			c2.setCellStyle(style);
			c2.setCellValue(headerData.get("h2"));
			Cell c3=  row2.createCell(3);
			c3.setCellStyle(style);
			c3.setCellValue(headerData.get("h3"));
			
			
			Row row3 = sheet.createRow(i++);	  
			Cell c4=  row3.createCell(0);
			c4.setCellStyle(style);
			c4.setCellValue("No Data");
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			wb.write(os);
			fileContent = os.toByteArray();
			
		}catch(Exception ex) {
			
		}
		return fileContent;
	}
	
	
	
	//Added By Bikash For Income Expenditure EXcel Sheet 1722021
		private byte[] getScheduleResultsExcelSheet(Map<String,String>headerData,Statement incomExpenditureStatement) {
			System.out.println("Inside Schedule Action");
			byte[]fileContent=null;
			try {
				HSSFWorkbook wb = new HSSFWorkbook();
				Sheet sheet = wb.createSheet("Income And Expenditure Report");
			/*
			 * sheet.getPrintSetup().setLandscape(true);
			 * sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
			 */
				HSSFCellStyle style = wb.createCellStyle();  
			/*
			 * HSSFFont font = wb.createFont(); font.setFontHeightInPoints((short)11);
			 * font.setFontName("Times New Roman"); font.setBoldweight((short)10);
			 * style.setFont(font);
			 */
				int i =0;
				Row row1 = sheet.createRow(i++);	  
				Cell c1=  row1.createCell(0);
				c1.setCellStyle(style);
				c1.setCellValue(headerData.get("h1"));
				
				Row row2 = sheet.createRow(i++);	  
				Cell c2=  row2.createCell(0);
				c2.setCellStyle(style);
				c2.setCellValue(headerData.get("h2"));
				Cell c3=  row2.createCell(3);
				c3.setCellStyle(style);
				c3.setCellValue(headerData.get("h3"));
				
				
				Row row3 = sheet.createRow(i++);	  
				Cell c4=  row3.createCell(0);
				c4.setCellStyle(style);
				c4.setCellValue(headerData.get("h4"));
				
				Cell c5=  row3.createCell(1);
				c5.setCellStyle(style);
				c5.setCellValue(headerData.get("h5"));
				
				
				int j =3;
				for(Fund f:incomeExpenditureStatement.getFunds()) {
					j=j+2;
					Cell c7=  row3.createCell(j);
					c7.setCellStyle(style);
					c7.setCellValue(f.getName());
				}
				
				j=5;
				Row row4 = sheet.createRow(i++);
				
				
				for(Fund f:incomeExpenditureStatement.getFunds()) {

							Cell c8=  row4.createCell(j++);
							c8.setCellStyle(style);
							c8.setCellValue(getCurrentYearToDate());
							
							Cell c9=  row4.createCell(j++);
							c9.setCellStyle(style);
							c9.setCellValue(getPreviousYearToDate());
							
					/*
					 * Cell c10= row4.createCell(j++); c10.setCellStyle(style);
					 * c10.setCellValue("");
					 */
		
				}
				
				 for(IEStatementEntry s : incomeExpenditureStatement.getIeEntries()) {
					 
					 Row row = sheet.createRow(i++);
					 	Cell cell0 = row.createCell(0);
						Cell cell1 = row.createCell(1);
						Cell cell2 = row.createCell(2);
						
						String glcode="";
						String schedule_num="";
						String account_name="";
						
						
						String net_amount="";
						String prev_amount="";
						
						
						if(null!=s.getScheduleNo()) {
							schedule_num = s.getScheduleNo().toString();
						}
						
						if(null!=s.getGlCode()) {
							glcode =s.getGlCode().toString();
						}
						
						if(null!=s.getAccountName()) {
							account_name =s.getAccountName().toString();
						}
						
						
						j=5;
						for(Fund f:incomeExpenditureStatement.getFunds()) {
							
							Cell cell3 = row.createCell(j++);
							Cell cell4 = row.createCell(j++);
							
							if(null!=s.getNetAmount().get(f.getName())) {
								net_amount =s.getNetAmount().get(f.getName()).toString();
								
							}
							
							if(null!=s.getPreviousYearAmount().get(f.getName())) {
								prev_amount =s.getPreviousYearAmount().get(f.getName()).toString();
							}
							cell3.setCellValue(net_amount);
							cell4.setCellValue(prev_amount);
						}
						cell0.setCellValue(glcode);
						cell1.setCellValue(account_name);
						cell2.setCellValue(schedule_num);
						
			        } 
					 int numberOfSheets = wb.getNumberOfSheets();
					 for (int x = 0; x < numberOfSheets; x++) {
					        Sheet sheet1 = wb.getSheetAt(x);
					        int total_row=6;
					        if (sheet1.getPhysicalNumberOfRows() > 0) {
				
					        		Row row = sheet1.getRow(total_row);
						            Iterator<Cell> cellIterator = row.cellIterator();
						            while (cellIterator.hasNext()) {
						                Cell cell = cellIterator.next();
						                int columnIndex = cell.getColumnIndex();
						               // System.out.println(columnIndex);
						                sheet1.autoSizeColumn(columnIndex);
						            }
					        	
					            
					        }
					    }
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				wb.write(os);
				 
				
				   fileContent = os.toByteArray();
				
				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			 return fileContent;
		}
		
		
		//Added By Bikash For Income Expenditure EXcel Sheet 1722021
		private byte[] getMinorScheduleResultsExcelSheet(Map<String,String>headerData,Statement incomExpenditureStatement) {
			System.out.println("Inside Schedule Action");
			byte[]fileContent=null;
			try {
				HSSFWorkbook wb = new HSSFWorkbook();
				Sheet sheet = wb.createSheet("Income And Expenditure Report");
			/*
			 * sheet.getPrintSetup().setLandscape(true);
			 * sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
			 */
				HSSFCellStyle style = wb.createCellStyle();  
			/*
			 * HSSFFont font = wb.createFont(); font.setFontHeightInPoints((short)11);
			 * font.setFontName("Times New Roman"); font.setBoldweight((short)10);
			 * style.setFont(font);
			 */
				int i =0;
				Row row1 = sheet.createRow(i++);	  
				Cell c1=  row1.createCell(0);
				c1.setCellStyle(style);
				c1.setCellValue(headerData.get("h1"));
				
				Row row2 = sheet.createRow(i++);	  
				Cell c2=  row2.createCell(0);
				c2.setCellStyle(style);
				c2.setCellValue(headerData.get("h2"));
				Cell c3=  row2.createCell(3);
				c3.setCellStyle(style);
				c3.setCellValue(headerData.get("h3"));
				
				
				Row row3 = sheet.createRow(i++);	  
				Cell c4=  row3.createCell(0);
				c4.setCellStyle(style);
				c4.setCellValue(headerData.get("h4"));
				
				Cell c5=  row3.createCell(1);
				c5.setCellStyle(style);
				c5.setCellValue(headerData.get("h5"));
				
				int j =2;
				int fund_size=incomeExpenditureStatement.getFunds().size();
					System.out.println(fund_size);
				if(fund_size>0) {
					
					for(Fund f:incomeExpenditureStatement.getFunds()) {
						
						//System.out.println("Fund loop "+j);
						Cell c6=  row3.createCell(j++);
			
						c6.setCellStyle(style);
						c6.setCellValue(f.getName().toString());
					}
					
					//System.out.println("After Fund loop "+j);
					Cell c7=  row3.createCell(j);
					c7.setCellStyle(style);
					c7.setCellValue(getCurrentYearToDate());
					//System.out.println("Fund loop 2 "+j);
					Cell c8=  row3.createCell(j+1);
					c8.setCellStyle(style);
					c8.setCellValue(getPreviousYearToDate());
					
				}else {
					Cell c7=  row3.createCell(j++);
					c7.setCellStyle(style);
					c7.setCellValue(getCurrentYearToDate());
					
					Cell c8=  row3.createCell(j++);
					c8.setCellStyle(style);
					c8.setCellValue(getPreviousYearToDate());
					
				}
	
				//Row row4 = sheet.createRow(i++);
				
				for(StatementEntry s : incomeExpenditureStatement.getEntries()) {
					//System.out.println("GLCODE"+s.getGlCode());
					
					//System.out.println("Account"+s.getAccountName());
					String glcode="";
					String account_name="";
					String fundAmount="";
					String currentYearTotal="0.0";
					String previousYearTotal="0.0";
					
					 Row row = sheet.createRow(i++);
					 	Cell cell0 = row.createCell(0);
						Cell cell1 = row.createCell(1);
						Cell cell4;
						Cell cell5;
						int x=2;
						if(fund_size>0) {
 
						 for(Fund f:incomeExpenditureStatement.getFunds()) {
							  
							  Cell cell3 = row.createCell(x++); 
							  if(null!=s.getFundWiseAmount().get(f.getName()))
							  { 
								  fundAmount =s.getFundWiseAmount().get(f.getName()).toString();
							  }
							  		cell3.setCellValue(fundAmount);
							 
							  
							  }
						
						 cell4=row.createCell(x);
							
						 cell5=row.createCell(x+1);
						}else {
							 cell4=row.createCell(x++);
							 cell5=row.createCell(x++);
						}
						
						
						
						if(null!=s.getGlCode()) {
							glcode =s.getGlCode().toString();
						}
						
						if(null!=s.getAccountName()) {
							account_name =s.getAccountName().toString();
						}
						
						if(null!=s.getCurrentYearTotal()) {
							currentYearTotal=s.getCurrentYearTotal().toString();
						}
						if(null!=s.getCurrentYearTotal()) {
							previousYearTotal=s.getPreviousYearTotal().toString();
						}
						
						cell0.setCellValue(glcode);
						cell1.setCellValue(account_name);
						cell4.setCellValue(currentYearTotal);
						cell5.setCellValue(previousYearTotal);	
			        } 
					int numberOfSheets = wb.getNumberOfSheets();
					 for (int x = 0; x < numberOfSheets; x++) {
					        Sheet sheet1 = wb.getSheetAt(x);
					        int total_row=6;
					        if (sheet1.getPhysicalNumberOfRows() > 0) {
				
					        		Row row = sheet1.getRow(total_row);
						            Iterator<Cell> cellIterator = row.cellIterator();
						            while (cellIterator.hasNext()) {
						                Cell cell = cellIterator.next();
						                int columnIndex = cell.getColumnIndex();
						               // System.out.println(columnIndex);
						                sheet1.autoSizeColumn(columnIndex);
						            }
					        	
					            
					        }
					    }
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				wb.write(os);
				fileContent = os.toByteArray();
				
				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			 return fileContent;
		}

}