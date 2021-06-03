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
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.egf.model.Statement;
import org.egov.egf.model.StatementEntry;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.services.report.BalanceSheetScheduleService;
import org.egov.services.report.BalanceSheetService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.exilant.eGov.src.common.SubDivision;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ParentPackage("egov")
@Results({
    @Result(name = "allScheduleDetailedResults", location = "balanceSheetReport-allScheduleDetailedResults.jsp"),
    @Result(name = "report", location = "balanceSheetReport-report.jsp"),
    @Result(name = "scheduleResults", location = "balanceSheetReport-scheduleResults.jsp"),
    @Result(name = "allScheduleResults", location = "balanceSheetReport-allScheduleResults.jsp"),
    @Result(name = "balanceSheet-PDF", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
            Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/pdf", Constants.CONTENT_DISPOSITION,
    "no-cache;filename=BalanceSheet.pdf" }),
    @Result(name = "balanceSheet-XLS", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
            Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/xls", Constants.CONTENT_DISPOSITION,
    "no-cache;filename=BalanceSheet.xls" }),
    @Result(name = "balanceSheet-HTML", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
            Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "text/html" })
})
public class BalanceSheetReportAction extends BaseFormAction {
    /**
     *
     */
    private static final long serialVersionUID = 7914013458428148999L;
    private static final String BALANCE_SHEET_PDF = "balanceSheet-PDF";
    private static final String BALANCE_SHEET_XLS = "balanceSheet-XLS";
    InputStream inputStream;
    ReportHelper reportHelper;
    Statement balanceSheet = new Statement();
    private Date todayDate;
    FinancialYearDAO financialYearDAO;
    CFinancialYear financialYear=new CFinancialYear();
    
    private static SimpleDateFormat FORMATDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private byte[]excelData=null;
   
 @Autowired
 @Qualifier("persistenceService")
 private PersistenceService persistenceService;
 @Autowired
    private EgovMasterDataCaching masterDataCache;
 @Autowired
 protected AppConfigValueService appConfigValuesService;

    private Date asOnDate;
    
    public FinancialYearDAO getFinancialYearDAO() {
		return financialYearDAO;
	}

	public void setFinancialYearDAO(FinancialYearDAO financialYearDAO) {
		this.financialYearDAO = financialYearDAO;
	}

	public Date getFromDate() {
        return balanceSheetService.getFromDate(balanceSheet);
    }

    public Date getToDate() {
        return balanceSheetService.getToDate(balanceSheet);
    }

    public void setFromDate(final Date fromDate) {
    }

    public Date getCurrentYearfromDate() {
        return getFromDate();
    }

    public void setCurrentYearfromDate(final Date currentYearfromDate) {
    }

    public Date getCurrentYeartoDate() {
        return getToDate();
    }

    public void setCurrentYeartoDate(final Date currentYeartoDate) {
    }

    public void setToDate(final Date toDate) {
    }

    public Date getPreviousYearfromDate() {
        return balanceSheetService.getPreviousYearFor(getFromDate());
    }

    public Date getPreviousYeartoDate() {
        return balanceSheetService.getPreviousYearFor(getToDate());
    }

    public void setPreviousYearfromDate(final Date previousYearfromDate) {
    }

    public void setPreviousYeartoDate(final Date previousYeartoDate) {
    }

    private StringBuffer header = new StringBuffer();
    // private String heading;
    BalanceSheetService balanceSheetService;
    BalanceSheetScheduleService balanceSheetScheduleService;
    public static final Locale LOCALE = new Locale("en", "IN");
    public static final SimpleDateFormat DDMMYYYYFORMATS = new SimpleDateFormat("dd/MM/yyyy", LOCALE);

    public void setBalanceSheetService(final BalanceSheetService balanceSheetService) {
        this.balanceSheetService = balanceSheetService;
    }

    public void setBalanceSheetScheduleService(final BalanceSheetScheduleService balanceSheetScheduleService) {
        this.balanceSheetScheduleService = balanceSheetScheduleService;
    }

    public void setReportHelper(final ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Statement getBalanceSheet() {
        return balanceSheet;
    }

    public BalanceSheetReportAction() {
        addRelatedEntity("department", Department.class);
        addRelatedEntity("function", CFunction.class);
        addRelatedEntity("fund", Fund.class);
        addRelatedEntity("functionary", Functionary.class);
        addRelatedEntity("financialYear", CFinancialYear.class);
        addRelatedEntity("field", Boundary.class);
        addRelatedEntity("subdivision", SubDivision.class);
    }

    @Override
    public void prepare() {
        persistenceService.getSession().setDefaultReadOnly(true);
        persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
        super.prepare();
        if (!parameters.containsKey("showDropDown")) {
            addDropdownData("departmentList", masterDataCache.get("egi-department"));
            addDropdownData("fundList", masterDataCache.get("egi-fund"));
            addDropdownData("functionList", masterDataCache.get("egi-function"));
        //    addDropdownData("functionaryList", masterCache.get("egi-functionary"));
          //  addDropdownData("fieldList", masterCache.get("egi-ward"));
            // addDropdownData("financialYearList",
            // getPersistenceService().findAllBy("from CFinancialYear where isActive=true and isActiveForPosting=true order by finYearRange desc "));
            addDropdownData("financialYearList", persistenceService.findAllBy("from CFinancialYear order by finYearRange desc "));
            List<AppConfigValues> appConfigValuesList =appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
    				"receipt_sub_divison");
            List<SubDivision> subdivisionList=new ArrayList<SubDivision>();
            SubDivision subdivision=null;
            for(AppConfigValues value:appConfigValuesList)
            {
            	subdivision = new SubDivision();
            	subdivision.setSubdivisionCode(value.getValue());
            	subdivision.setSubdivisionName(value.getValue());
            	subdivisionList.add(subdivision);
            }
            addDropdownData("subdivisionList", subdivisionList);
        }
    }

    protected void setRelatedEntitesOn() {
        setTodayDate(new Date());
        if (balanceSheet.getFinancialYear() != null && balanceSheet.getFinancialYear().getId() != null)
            balanceSheet.setFinancialYear((CFinancialYear) getPersistenceService().find("from CFinancialYear where id=?",
                    balanceSheet.getFinancialYear().getId()));
        if (balanceSheet.getDepartment() != null && balanceSheet.getDepartment().getCode() != null
                && !balanceSheet.getDepartment().getCode().isEmpty()) {
            Department dept= microserviceUtils.getDepartmentByCode(balanceSheet.getDepartment().getCode());
            balanceSheet.setDepartment(dept);
            header.append(" in " + balanceSheet.getDepartment().getName());
        } else
            balanceSheet.setDepartment(null);
/*        if (balanceSheet.getField() != null && balanceSheet.getField().getId() != null && balanceSheet.getField().getId() != 0) {
            balanceSheet.setField((Boundary) getPersistenceService().find("from Boundary where id=?",
                    balanceSheet.getField().getId()));
            header.append(" in " + balanceSheet.getField().getName());
        }*/
        if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null && balanceSheet.getFund().getId() != 0) {
            balanceSheet.setFund((Fund) getPersistenceService().find("from Fund where id=?", balanceSheet.getFund().getId()));
            header.append(" for " + balanceSheet.getFund().getName());
        }
        if (balanceSheet.getFunction() != null && balanceSheet.getFunction().getId() != null
                && balanceSheet.getFunction().getId() != 0) {
            balanceSheet.setFunction((CFunction) getPersistenceService().find("from CFunction where id=?",
                    balanceSheet.getFunction().getId()));
            header.append(" for " + balanceSheet.getFunction().getName());
        }
 /*       if (balanceSheet.getFunctionary() != null && balanceSheet.getFunctionary().getId() != null
                && balanceSheet.getFunctionary().getId() != 0) {
            balanceSheet.setFunctionary((Functionary) getPersistenceService().find("from Functionary where id=?",
                    balanceSheet.getFunctionary().getId()));
            header.append(" in " + balanceSheet.getFunctionary().getName());
        }*/
        
        if (balanceSheet.getSubdivision() != null && balanceSheet.getSubdivision().getSubdivisionName() != null
                && !"null".equalsIgnoreCase(balanceSheet.getSubdivision().getSubdivisionName())) {
            SubDivision subd = new SubDivision();
            subd.setSubdivisionCode(balanceSheet.getSubdivision().getSubdivisionName());
            subd.setSubdivisionName(balanceSheet.getSubdivision().getSubdivisionName());
            balanceSheet.setSubdivision(subd);
            header.append(" in " + balanceSheet.getSubdivision().getSubdivisionName());
        } else {
        	balanceSheet.setSubdivision(null);
        }
        
        if (balanceSheet.getAsOndate() != null)
            header.append(" as on " + DDMMYYYYFORMATS.format(balanceSheet.getAsOndate()));
        header.toString();
    }

    @Override
    public Object getModel() {
        return balanceSheet;
    }

    @SkipValidation
    @Action(value = "/report/balanceSheetReport-generateBalanceSheetReport")
    public String generateBalanceSheetReport() {
        return "report";
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateBalanceSheetSubReport")
    public String generateBalanceSheetSubReport() {
        populateDataSourceForSchedule();
        asOnDate=balanceSheet.getAsOndate();
        return "scheduleResults";
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateScheduleReport")
    public String generateScheduleReport() {
        populateDataSourceForAllSchedules();
        return "allScheduleResults";
    }

    /* for Detailed */
    @ReadOnly
    @SkipValidation
    @Action(value = "/report/balanceSheetReport-generateScheduleReportDetailed")
    public String generateScheduleReportDetailed() {
        populateDataSourceForAllSchedulesDetailed();
        return "allScheduleDetailedResults";
    }

    private void populateDataSourceForSchedule() {
        setRelatedEntitesOn();
        if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null) {
            final List<Fund> selFund = new ArrayList<Fund>();
            selFund.add(balanceSheet.getFund());
            balanceSheet.setFunds(selFund);
        } else
            balanceSheet.setFunds(balanceSheetService.getFunds());
        balanceSheetScheduleService.populateDataForSchedule(balanceSheet, parameters.get("majorCode")[0]);
    }

    private void populateDataSourceForAllSchedules() {
        setRelatedEntitesOn();
        if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null && balanceSheet.getFund().getId() != 0) {
            final List<Fund> selFund = new ArrayList<Fund>();
            selFund.add(balanceSheet.getFund());
            balanceSheet.setFunds(selFund);
        } else
            balanceSheet.setFunds(balanceSheetService.getFunds());
        balanceSheetScheduleService.populateDataForAllSchedules(balanceSheet);
    }

    /* for detailed */
    private void populateDataSourceForAllSchedulesDetailed() {
        setRelatedEntitesOn();
        if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null && balanceSheet.getFund().getId() != 0) {
            final List<Fund> selFund = new ArrayList<Fund>();
            selFund.add(balanceSheet.getFund());
            balanceSheet.setFunds(selFund);
        } else
            balanceSheet.setFunds(balanceSheetService.getFunds());
        balanceSheetScheduleService.populateDataForAllSchedulesDetailed(balanceSheet);
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-printBalanceSheetReport")
    public String printBalanceSheetReport() {
        populateDataSource();
        return "report";
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateBalanceSheetPdf")
    public String generateBalanceSheetPdf() throws Exception {
        populateDataSource();
        final JasperPrint jasper = reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
                getText("report.heading"),
                header.toString(),
                getCurrentYearToDate(), getPreviousYearToDate(), true);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return BALANCE_SHEET_PDF;
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateBalanceSheetXls")
    public String generateBalanceSheetXls() throws Exception {
        populateDataSource();
		/*
		 * JasperPrint jasper = null; if
		 * (!balanceSheet.getPeriod().equalsIgnoreCase("Yearly")) jasper =
		 * reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
		 * getText("report.heading"), header.toString(), getCurrentYearToDate(),
		 * getPreviousYearToDate(), true); else jasper =
		 * reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
		 * getText("report.heading"), header.toString(), getCurrentYearToDate(),
		 * getPreviousYearToDate(), true); inputStream =
		 * reportHelper.exportXls(inputStream, jasper); return BALANCE_SHEET_XLS;
		 */
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
        + "                                               ";
        String report_header="Balance Sheet Report  ";
        Map<String, String> headerData = new HashMap<>();
        headerData.put("h1", report_header+header.toString());
        headerData.put("h2", subtitle);
        headerData.put("h3", "Amount in "+balanceSheet.getCurrency());
        headerData.put("h4", "Account Code");
        headerData.put("h5","Head Of Account");
        headerData.put("h6","Schedule No");
       
        headerData.put("h8",getPreviousYearToDate());
        headerData.put("h9", getCurrentYearToDate());
        
      try {
    	  excelData = getBalanceSheetResultsExcelSheet(headerData,balanceSheet);
      } catch (Exception e) {
		e.printStackTrace();
	}
       inputStream = new ByteArrayInputStream(excelData);
        return BALANCE_SHEET_XLS;
        
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateSchedulePdf")
    public String generateSchedulePdf() throws Exception {
        populateDataSourceForAllSchedules();
        final JasperPrint jasper = reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
                getText("report.heading"),
                header.toString(),
                getCurrentYearToDate(), getPreviousYearToDate(), false);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return BALANCE_SHEET_PDF;
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateScheduleXls")
    public String generateScheduleXls() throws Exception {
        populateDataSourceForAllSchedules();
		/*
		 * final JasperPrint jasper =
		 * reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
		 * getText("report.heading"), header.toString(), getCurrentYearToDate(),
		 * getPreviousYearToDate(), false); inputStream =
		 * reportHelper.exportXls(inputStream, jasper); return BALANCE_SHEET_XLS;
		 */
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
        + "                                               ";
        
        String report_header="Balance Sheet Report  ";
        Map<String, String> headerData = new HashMap<>();
        headerData.put("h1", report_header+header.toString());
        headerData.put("h2", subtitle);
        headerData.put("h3", "Amount in "+balanceSheet.getCurrency());
        headerData.put("h4", "Account Code");
        headerData.put("h5","Head Of Account");
        headerData.put("h6","Schedule No");
       
        
       excelData = getBalanceSheetMinorAllScheduleResultsExcelSheet(headerData,balanceSheet);
       inputStream = new ByteArrayInputStream(excelData);
        return BALANCE_SHEET_XLS;
    }

    /* for detailed */
    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateDetailedSchedulePdf")
    public String generateDetailedSchedulePdf() throws Exception {
        populateDataSourceForAllSchedulesDetailed();
        final JasperPrint jasper = reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
                getText("report.heading"),
                header.toString(),
                getCurrentYearToDate(), getPreviousYearToDate(), false);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return BALANCE_SHEET_PDF;
    }

    /* for detailed */
    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateDetailedScheduleXls")
    public String generateDetailedScheduleXls() throws Exception {
        populateDataSourceForAllSchedulesDetailed();
		/*
		 * final JasperPrint jasper =
		 * reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
		 * getText("report.heading"), header.toString(), getCurrentYearToDate(),
		 * getPreviousYearToDate(), false); inputStream =
		 * reportHelper.exportXls(inputStream, jasper); return BALANCE_SHEET_XLS;
		 */
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
        + "                                               ";
        
        String report_header="Balance Sheet Report  ";
        Map<String, String> headerData = new HashMap<>();
        headerData.put("h1", report_header+header.toString());
        headerData.put("h2", subtitle);
        headerData.put("h3", "Amount in "+balanceSheet.getCurrency());
        headerData.put("h4", "Account Code");
        headerData.put("h5","Head Of Account");
        headerData.put("h6","Schedule No");
       
        
       excelData = getBalanceSheetMinorAllScheduleResultsExcelSheet(headerData,balanceSheet);
       inputStream = new ByteArrayInputStream(excelData);
        
        return BALANCE_SHEET_XLS;
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateBalanceSheetSchedulePdf")
    public String generateBalanceSheetSchedulePdf() throws Exception {
        populateDataSourceForSchedule();
        final JasperPrint jasper = reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
                getText("report.sub.schedule.heading"), header.toString(),
                getCurrentYearToDate(), getPreviousYearToDate(), false);
        inputStream = reportHelper.exportPdf(inputStream, jasper);
        return BALANCE_SHEET_PDF;
    }

    @ReadOnly
    @Action(value = "/report/balanceSheetReport-generateBalanceSheetScheduleXls")
    public String generateBalanceSheetScheduleXls() throws Exception {
        populateDataSourceForSchedule();
		/*
		 * final JasperPrint jasper =
		 * reportHelper.generateFinancialStatementReportJasperPrint(balanceSheet,
		 * getText("report.sub.schedule.heading"), header.toString(),
		 * getCurrentYearToDate(), getPreviousYearToDate(), false); inputStream =
		 * reportHelper.exportXls(inputStream, jasper); return BALANCE_SHEET_XLS;
		 */
        
        final String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
        + "                                               ";
        
        String report_header="Balance Sheet Report  ";
        Map<String, String> headerData = new HashMap<>();
        headerData.put("h1", report_header+header.toString());
        headerData.put("h2", subtitle);
        headerData.put("h3", "Amount in "+balanceSheet.getCurrency());
        headerData.put("h4", "Account Code");
        headerData.put("h5","Head Of Account");
        excelData = getBalanceSheetScheduleResultsExcelSheet(headerData,balanceSheet);
        inputStream = new ByteArrayInputStream(excelData);
        return BALANCE_SHEET_XLS;
    }

    protected void populateDataSource() {
    	
    	setRelatedEntitesOn();
        
        if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null) {
            final List<Fund> selFund = new ArrayList<Fund>();
            selFund.add(balanceSheet.getFund());
            balanceSheet.setFunds(selFund);
        } else
            balanceSheet.setFunds(balanceSheetService.getFunds());
        balanceSheetService.populateBalanceSheet(balanceSheet);
    }

    //TODO- This table is not used. Check reference and remove
  
    public String getCurrentYearToDate() {
    	if ("Date".equalsIgnoreCase(balanceSheet.getPeriod()))
 	   {
         return balanceSheetService.getFormattedDate(balanceSheet.getFromDate())  +" To "+balanceSheetService.getFormattedDate(balanceSheet.getToDate());
 	   }else {
        return balanceSheetService.getFormattedDate(balanceSheetService.getToDate(balanceSheet));
    }
    }

    public String getPreviousYearToDate() {
    	if ("Date".equalsIgnoreCase(balanceSheet.getPeriod()))
  	   {
        return balanceSheetService.getFormattedDate(balanceSheetService.getPreviousYearFor(balanceSheet.getFromDate())) +" To "+ balanceSheetService.getFormattedDate(balanceSheetService.getPreviousYearFor(balanceSheet.getToDate()));
  	   }else {
        return balanceSheetService.getFormattedDate(balanceSheetService.getPreviousYearFor(balanceSheetService
                .getToDate(balanceSheet)));
  	   }
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(final Date todayDate) {
        this.todayDate = todayDate;
    }

    public StringBuffer getHeader() {
        return header;
    }

    public void setHeader(final StringBuffer header) {
        this.header = header;
    }

    public Date getAsOnDate() {
        return asOnDate;
    }

    public void setAsOnDate(Date asOnDate) {
        this.asOnDate = asOnDate;
    }
    
    
  //Added By Bikash For Income Expenditure EXcel Sheet 22022021
  		private byte[] getBalanceSheetResultsExcelSheet(Map<String,String>headerData,Statement balanceSheet) {
  			System.out.println("Inside Schedule Action");
  			byte[]fileContent=null;
  			try {
  				HSSFWorkbook wb = new HSSFWorkbook();
  				Sheet sheet = wb.createSheet("Balance Sheet Report");
  				//sheet.getPrintSetup().setLandscape(true);
  				//sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A5_PAPERSIZE); 
  				HSSFCellStyle style = wb.createCellStyle();  
			/*
			 * HSSFFont font = wb.createFont(); font.setFontHeightInPoints((short)11);
			 * font.setFontName("Times New Roman"); font.setBoldweight((short)10);
			 */
  		      //  style.setFont(font);  
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

  				Cell c11=  row3.createCell(2);
  				c11.setCellStyle(style);
  				c11.setCellValue(headerData.get("h6"));

  				
  				int j =3;
  				int fund_size=balanceSheet.getFunds().size();
  				if(fund_size>1) {
  					
  					for(Fund f:balanceSheet.getFunds()) {
  						
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
  				
  				for(StatementEntry s : balanceSheet.getEntries()) {
  					//System.out.println("GLCODE"+s.getGlCode());
  					
  					//System.out.println("Account"+s.getAccountName());
  					String schedule_num="";
  					String glcode="";
  					String account_name="";
  					String fundAmount="";
  					String currentYearTotal="";
  					String previousYearTotal="";
  					
  					 Row row = sheet.createRow(i++);
  					 	Cell cell0 = row.createCell(0);
  						Cell cell1 = row.createCell(1);
  						Cell cell7=row.createCell(2);
  						Cell cell4;
  						Cell cell5;
  						int x=3;
  						if(fund_size>1) {

  						 for(Fund f:balanceSheet.getFunds()) {
  							  
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
  						if(null!=s.getScheduleNo()) {
  						schedule_num = s.getScheduleNo().toString();
  						}
  						
  				/*
  				 * if(currentYearTotal.equals("0")) { currentYearTotal="0.0"; }
  				 * if(previousYearTotal.equals("0")) { previousYearTotal="0.0"; }
  				 */
  						cell0.setCellValue(glcode);
  						cell1.setCellValue(account_name);
  						cell4.setCellValue(currentYearTotal);
  						cell5.setCellValue(previousYearTotal);
  						cell7.setCellValue(schedule_num);
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
    
  		private byte[] getBalanceSheetScheduleResultsExcelSheet(Map<String,String>headerData,Statement balanceSheet) {
			System.out.println("Inside Schedule Action");
			byte[]fileContent=null;
			try {
				HSSFWorkbook wb = new HSSFWorkbook();
				Sheet sheet = wb.createSheet("Balance Sheet Report");
			/*
			 * sheet.getPrintSetup().setLandscape(true);
			 * sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
			 */
				HSSFCellStyle style = wb.createCellStyle();  
			/*
			 * HSSFFont font = wb.createFont(); font.setFontHeightInPoints((short)11);
			 * font.setFontName("Times New Roman"); font.setBoldweight((short)10);
			 */
		        //style.setFont(font);  
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
				int fund_size=balanceSheet.getFunds().size();
				System.out.println(fund_size);
				if(fund_size>1) {
					
					for(Fund f:balanceSheet.getFunds()) {
						
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
				
				for(StatementEntry s : balanceSheet.getEntries()) {
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
						if(fund_size>1) {
											
						 for(Fund f:balanceSheet.getFunds()) {
							  
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
						
						System.out.println("currentYearTotal"+currentYearTotal);
						System.out.println("previousYearTotal"+previousYearTotal);
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
  		
  		
  		private byte[] getBalanceSheetMinorAllScheduleResultsExcelSheet(Map<String,String>headerData,Statement balanceSheet) {
			System.out.println("Inside ALl Schedule Action");
			byte[]fileContent=null;
			try {
				HSSFWorkbook wb = new HSSFWorkbook();
				Sheet sheet = wb.createSheet("Balance Sheet Report");
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
				int fund_size=balanceSheet.getFunds().size();
				System.out.println(fund_size);
				if(fund_size>1) {
					
					for(Fund f:balanceSheet.getFunds()) {
						Cell c6=  row3.createCell(j++);
						c6.setCellStyle(style);
						c6.setCellValue(f.getName().toString());
					}
				
					Cell c7=  row3.createCell(j);
					c7.setCellStyle(style);
					c7.setCellValue(getCurrentYearToDate());
					
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
	
				for(StatementEntry s : balanceSheet.getEntries()) {
					
					String glcode="";
					String account_name="";
					String fundAmount="";
					String currentYearTotal="";
					String previousYearTotal="";
					
					 Row row = sheet.createRow(i++);
					 	Cell cell0 = row.createCell(0);
						Cell cell1 = row.createCell(1);
						Cell cell4;
						Cell cell5;
						int x=2;
						if(fund_size>1) {
 
						 for(Fund f:balanceSheet.getFunds()) {
							  
							  Cell cell3 = row.createCell(x++); 
							  if(null!=s.getFundWiseAmount().get(f.getName()))
							  { 
								  fundAmount = s.getFundWiseAmount().get(f.getName()).toString();
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
						
		/*
		 * if(currentYearTotal.equals("0")) { currentYearTotal="0.0"; }
		 * if(previousYearTotal.equals("0")) { previousYearTotal="0.0"; }
		 */
						System.out.println("currentYearTotal"+currentYearTotal);
						System.out.println("previousYearTotal"+previousYearTotal);
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