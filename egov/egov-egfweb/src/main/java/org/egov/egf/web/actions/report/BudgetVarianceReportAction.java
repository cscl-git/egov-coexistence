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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.commons.Fundsource;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.Vouchermis;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.service.FundService;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.expensebill.service.ExpenseBillService;
import org.egov.egf.model.BudgetVarianceEntry;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetGroup;
import org.egov.model.payment.Paymentheader;
import org.egov.services.budget.BudgetDetailService;
import org.egov.services.budget.BudgetGroupService;
import org.egov.services.budget.BudgetService;
import org.egov.utils.BudgetAccountType;
import org.egov.utils.BudgetDetailConfig;
import org.egov.utils.Constants;
import org.hibernate.FlushMode;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;


@Component
@Results(value = {
        @Result(name = "results", location = "budgetVarianceReport-results.jsp"),
        @Result(name = "form", location = "budgetVarianceReport-form.jsp"),
        @Result(name = "PDF", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
                "application/pdf", "contentDisposition", "no-cache;filename=BudgetVarianceReport.pdf" }),
        @Result(name = "XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
                "application/xls", "contentDisposition", "no-cache;filename=BudgetVarianceReport.xls" })
})
@ParentPackage("egov")
public class BudgetVarianceReportAction extends BaseFormAction {

    private static final long serialVersionUID = -9048247816556335427L;
    String jasperpath = "budgetVarianceReport";
    List<Paymentheader> paymentHeaderList = new ArrayList<Paymentheader>();
    private List<BudgetVarianceEntry> budgetVarianceEntries = new ArrayList<BudgetVarianceEntry>();
    private Date asOnDate = new Date();
    private InputStream inputStream;
    private EgovCommon egovCommon;
    protected List<String> headerFields = new ArrayList<String>();
    protected List<String> mandatoryFields = new ArrayList<String>();
    private Vouchermis vouchermis = new Vouchermis();

    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
    AppConfigValueService appConfigValuesService;
    @Autowired
    private ReportService reportService;
    private final List<String> accountTypeList = new ArrayList<String>();
    private String accountType = "";
    private BudgetDetail budgetDetail = new BudgetDetail();
    @Autowired
    private BudgetDetailConfig budgetDetailConfig;
    protected List<String> gridFields = new ArrayList<String>();
    @Autowired
    protected BudgetDetailService budgetDetailService;
    @Autowired
    private FinancialYearHibernateDAO financialYearDAO;
    private String type = "Budget";
    @Autowired
    private BudgetService budgetService;
    String budgetType = Constants.BE;
    private final Map<String, String> queryParamMap = new HashMap<String, String>();
    private Department department = new Department();
    private CFunction function = new CFunction();
    private Fund fund = new Fund();
    @Autowired
    @Qualifier("masterDataCache")
    private EgovMasterDataCaching masterDataCache;
    
    @Autowired
	 private  DepartmentService departmentService;
    
    private String dept;
    private String funds;
    private String func;
    private String accCode;
    private String vtype;
    
    @Autowired
    private FundService fundService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;
    @Autowired
    @Qualifier("budgetGroupService")
    private BudgetGroupService budgetGroupService;
    
    private String vhId;
    
    @Autowired
    private ExpenseBillService expenseBillService;
    

    @ValidationErrorPage(value = "form")
    @SkipValidation
    @Override
    public String execute() throws Exception {
        return "form";
    }

    public BudgetVarianceReportAction() {
    }

    @Override
    public void prepare() {
        headerFields = budgetDetailConfig.getHeaderFields();
        gridFields = budgetDetailConfig.getGridFields();
        mandatoryFields = budgetDetailConfig.getMandatoryFields();
        if (isFieldMandatory(Constants.EXECUTING_DEPARTMENT))
            addRelatedEntity("executingDepartment", String.class);
        if (isFieldMandatory(Constants.FUND))
            addRelatedEntity("fund", Fund.class);
        if (isFieldMandatory(Constants.FUNCTION))
            addRelatedEntity("function", CFunction.class);
        if (isFieldMandatory(Constants.SCHEME))
            addRelatedEntity("scheme", Scheme.class);
        if (isFieldMandatory(Constants.SUBSCHEME))
            addRelatedEntity("subscheme", SubScheme.class);
        if (isFieldMandatory(Constants.FUNCTIONARY))
            addRelatedEntity("functionary", Functionary.class);
        if (isFieldMandatory(Constants.FUNDSOURCE))
            addRelatedEntity("fundsource", Fundsource.class);
        if (isFieldMandatory(Constants.BOUNDARY))
            addRelatedEntity("boundary", Boundary.class);
        addRelatedEntity("budgetGroup", BudgetGroup.class);
        super.prepare();
        persistenceService.getSession().setDefaultReadOnly(true);
        persistenceService.getSession().setFlushMode(FlushMode.MANUAL);

        mandatoryFields = budgetDetailConfig.getMandatoryFields();
        if (!parameters.containsKey("skipPrepare")) {
            accountTypeList.add(BudgetAccountType.REVENUE_EXPENDITURE.name());
            accountTypeList.add(BudgetAccountType.REVENUE_RECEIPTS.name());
            accountTypeList.add(BudgetAccountType.CAPITAL_EXPENDITURE.name());
            accountTypeList.add(BudgetAccountType.CAPITAL_RECEIPTS.name());
            addDropdownData("accountTypeList", accountTypeList);

            dropdownData.put("budgetGroupList",
                    persistenceService.findAllBy("from BudgetGroup where isActive=true order by name"));
            if (isFieldMandatory(Constants.EXECUTING_DEPARTMENT))
                addDropdownData("departmentList", masterDataCache.get("egi-department"));
            if (isFieldMandatory(Constants.FUNCTION))
                addDropdownData("functionList",
                        persistenceService.findAllBy("from CFunction where isactive=true and isnotleaf=false  order by name"));
            if (isFieldMandatory(Constants.FUNCTIONARY))
                addDropdownData("functionaryList",
                        persistenceService.findAllBy(" from Functionary where isactive=true order by name"));
            if (isFieldMandatory(Constants.FUND))
                addDropdownData("fundList",
                        persistenceService.findAllBy(" from Fund where isactive=true and isnotleaf=false order by name"));
            if (isFieldMandatory(Constants.FIELD))
                addDropdownData("fieldList",
                        persistenceService.findAllBy(" from Boundary b where lower(b.boundaryType.name)='ward' "));
            if (isFieldMandatory(Constants.SCHEME))
                addDropdownData("schemeList", Collections.EMPTY_LIST);
            if (isFieldMandatory(Constants.SUBSCHEME))
                addDropdownData("subschemeList", Collections.EMPTY_LIST);
        }
    }

    @ValidationErrorPage(value = "form")
    @SkipValidation
    @Action(value = "/report/budgetVarianceReport-loadData")
    public String loadData() {
    	System.out.println(" dept::"+ dept);
    	System.out.println("funds  :::"+funds);
    	System.out.println("func ::::"+func);
    	System.out.println("accCode ::::"+accCode);
    	System.out.println("asOnDate :::"+asOnDate);
    	System.out.println("vtype :::"+vtype);
    	String result="form";
    	if(vtype !=null && vtype.equals("jv"))
        {
        	budgetDetail.setExecutingDepartment(dept);
        	budgetDetail.setFund(fundService.findOne(Integer.parseInt(funds)));
        	budgetDetail.setFunction(functionService.findOne(Long.parseLong(func)));
        	BudgetGroup budgrp=budgetGroupService.getBudgetGroup((chartOfAccountsService.getByGlCode(accCode)).getId());
        	budgetDetail.setBudgetGroup(budgrp);
        	result="results";
        }
    	else if(vtype !=null && vtype.equals("jvw"))
    	{
    		String department=getDepartment(Long.parseLong(vhId));
    		budgetDetail.setExecutingDepartment(department);
    		String function = getFunction(Long.parseLong(vhId));
    		budgetDetail.setFunction(functionService.findOne(Long.parseLong(function)));
    		String fund=getFundDetails(Long.parseLong(vhId));
    		budgetDetail.setFund(fundService.findOne(Integer.parseInt(fund)));
    		String acCode=getAcCode(Long.parseLong(vhId));
    		BudgetGroup budgrp=budgetGroupService.getBudgetGroup((chartOfAccountsService.getByGlCode(acCode)).getId());
        	budgetDetail.setBudgetGroup(budgrp);
        	result="results";
    	}
    	else if(vtype !=null && vtype.equals("bpw"))
    	{
    		vhId=getVoucherId(Long.parseLong(vhId));
    		String department=getDepartment(Long.parseLong(vhId));
    		budgetDetail.setExecutingDepartment(department);
    		String function = getFunction(Long.parseLong(vhId));
    		budgetDetail.setFunction(functionService.findOne(Long.parseLong(function)));
    		String fund=getFundDetails(Long.parseLong(vhId));
    		budgetDetail.setFund(fundService.findOne(Integer.parseInt(fund)));
    		String acCode=getAcCode(Long.parseLong(vhId));
    		BudgetGroup budgrp=budgetGroupService.getBudgetGroup((chartOfAccountsService.getByGlCode(acCode)).getId());
        	budgetDetail.setBudgetGroup(budgrp);
        	result="results";
    	}
    	else if (vtype !=null && vtype.equals("pv"))
    	{
    		EgBillregister eg=expenseBillService.getById(Long.parseLong(vhId));
    		budgetDetail.setExecutingDepartment(eg.getEgBillregistermis().getDepartmentcode());
    		budgetDetail.setFunction(functionService.findOne(eg.getEgBillregistermis().getFunction().getId()));
    		budgetDetail.setFund(fundService.findOne(eg.getEgBillregistermis().getFund().getId()));
    		Long glcodeId=0L;
    		System.out.println("id ; "+eg.getId());
    		for(EgBilldetails row :eg.getEgBilldetailes())
    		{
    			
    			String amt = row.getDebitamount().toString();
    			System.out.println("amt = "+amt);
    			if(amt != null && amt != "" && !(amt.equalsIgnoreCase("0") || amt.equalsIgnoreCase("0.00")) )
    			{
    				glcodeId = (row.getGlcodeid()).longValue();
    				break;
    			}
    		}
    		System.out.println("code :::"+glcodeId);
    		BudgetGroup budgrp=budgetGroupService.getBudgetGroup(glcodeId);
        	budgetDetail.setBudgetGroup(budgrp);
        	result="results";
    	}
    	else if (vtype !=null && vtype.equals("pr"))
    	{
    		budgetDetail.setExecutingDepartment(dept);
        	budgetDetail.setFund(fundService.findOne(Integer.parseInt(funds)));
        	budgetDetail.setFunction(functionService.findOne(Long.parseLong(func)));
        	BudgetGroup budgrp=budgetGroupService.getBudgetGroup(Long.parseLong(accCode));
        	budgetDetail.setBudgetGroup(budgrp);
        	result="results";
    	}
        populateData();
        return result;
    }

    private String getVoucherId(Long vhId2) {
    	
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String voucherId="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select p.type,p.voucherheaderid from paymentheader p where p.id =:phId ");
    	    query.setLong("phId", vhId2);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		voucherId= element[1].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return voucherId;
	}

	private String getAcCode(Long vhId) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String acCode="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select gl.glcode,gl.glcodeid from generalledger gl where gl.debitamount not in (0) and gl.voucherheaderid =:voucherHeaderId");
    	    query.setLong("voucherHeaderId", vhId);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		acCode= element[0].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return acCode;
	}


	private String getFundDetails(Long vhId) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String fund="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select vh.name,vh.fundid from voucherheader vh where vh.id=:voucherHeaderId");
    	    query.setLong("voucherHeaderId", vhId);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		fund= element[1].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return fund;
	}

	private String getDepartment(Long vhId) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String dept="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select vmis.departmentcode,vmis.functionid from vouchermis vmis where vmis.voucherheaderid=:voucherHeaderId");
    	    query.setLong("voucherHeaderId", vhId);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		dept= element[0].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return dept;
	}
    private String getFunction(Long vhId) {
    	SQLQuery query =  null;
    	List<Object[]> rows = null;
    	String func="";
    	try
    	{
    		 query = this.persistenceService.getSession().createSQLQuery("select vmis.departmentcode,vmis.functionid from vouchermis vmis where vmis.voucherheaderid=:voucherHeaderId");
    	    query.setLong("voucherHeaderId", vhId);
    	    rows = query.list();
    	    if(rows != null && !rows.isEmpty())
    	    {
    	    	for(Object[] element : rows)
    	    	{
    	    		func= element[1].toString();
    	    	}
    	    }
    	}catch (Exception e) {
			e.printStackTrace();
		}
	    return func;
	}


	public boolean shouldShowHeaderField(final String fieldName) {
        return (headerFields.contains(fieldName) || gridFields.contains(fieldName)) && mandatoryFields.contains(fieldName);
    }

    Date parseDate(final String stringDate) {
        if (parameters.containsKey(stringDate) && parameters.get(stringDate)[0] != null)
            try {
                return Constants.DDMMYYYYFORMAT2.parse(parameters.get(stringDate)[0]);
            } catch (final ParseException e) {
                throw new ValidationException("Invalid date", "Invalid date");
            }
        return new Date();
    }

    private StringBuffer formMiscQuery(final String mis, final String gl, final String detail) {
        StringBuffer miscQuery = new StringBuffer();
        if (shouldShowHeaderField(Constants.FUND) && queryParamMap.containsKey("fundId")) {
            miscQuery = miscQuery.append(" and " + detail + ".fundId=bd.fund ");
            miscQuery = miscQuery.append(" and bd.fund= " + Integer.parseInt(queryParamMap.get("fundId")));
        }
        if (shouldShowHeaderField(Constants.SCHEME) && queryParamMap.containsKey("schemeId")) {
            miscQuery = miscQuery.append(" and " + mis + ".schemeid=bd.scheme ");
            miscQuery = miscQuery.append(" and bd.scheme= " + Integer.parseInt(queryParamMap.get("schemeId")));
        }
        if (shouldShowHeaderField(Constants.SUB_SCHEME) && queryParamMap.containsKey("subSchemeId")) {
            miscQuery = miscQuery.append(" and " + mis + ".subschemeid=bd.subscheme ");
            miscQuery = miscQuery.append(" and bd.subscheme= " + Integer.parseInt(queryParamMap.get("subSchemeId")));
        }
        if (shouldShowHeaderField(Constants.FUNCTIONARY) && queryParamMap.containsKey("functionaryId")) {
            miscQuery = miscQuery.append(" and " + mis + ".functionaryid=bd.functionary ");
            miscQuery = miscQuery.append(" and bd.functionary= " + Integer.parseInt(queryParamMap.get("functionaryId")));
        }
        if (shouldShowHeaderField(Constants.FUNCTION) && queryParamMap.containsKey("functionId")) {
            miscQuery = miscQuery.append(" and " + gl + ".functionId=bd.function ");
            miscQuery = miscQuery.append(" and bd.function= " + Long.parseLong(queryParamMap.get("functionId")));
        }
        if (shouldShowHeaderField(Constants.EXECUTING_DEPARTMENT) && queryParamMap.containsKey("deptId")) {
            miscQuery = miscQuery.append(" and " + mis + ".departmentcode=bd.executing_department ");
            miscQuery = miscQuery.append(" and bd.executing_department= '" + queryParamMap.get("deptId")+"' ");
        }
        return miscQuery;
    }

    public List<Paymentheader> getPaymentHeaderList() {
        return paymentHeaderList;
    }

    public void setAsOnDate(final Date startDate) {
        asOnDate = startDate;
    }

    public Date getAsOnDate() {
        return asOnDate;
    }

    public String getFormattedDate(final Date date) {
        return Constants.DDMMYYYYFORMAT2.format(date);
    }

    @SkipValidation
    @Action(value = "/report/budgetVarianceReport-exportPdf")
    public String exportPdf() throws JRException, IOException {
        generateReport();
        return "PDF";
    }

    private void generateReport() {
        populateData();
        final ReportRequest reportInput = new ReportRequest(jasperpath, budgetVarianceEntries, getParamMap());
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
    }

    Map<String, Object> getParamMap() {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("departmentName", getDepartmentName());
        String estimateHeading = "";
        if (Constants.BE.equalsIgnoreCase(budgetType))
            estimateHeading = "Budget Estimate";
        else
            estimateHeading = "Revised Estimate";
        paramMap.put("estimateHeading", estimateHeading);
        paramMap.put("asOnDate", Constants.DDMMYYYYFORMAT2.format(asOnDate));
        return paramMap;
    }

    @ReadOnly
    private void populateData() {
    	System.out.println("1");
        final CFinancialYear financialYear = financialYearDAO.getFinancialYearByDate(asOnDate);
        final boolean hasApprovedReForYear = budgetService.hasApprovedReForYear(financialYear.getId());
        if (hasApprovedReForYear) {
            type = "Revised";
            budgetType = Constants.RE;
        }
        final List<BudgetDetail> result = persistenceService.findAllBy("from BudgetDetail where budget.isbere='" + budgetType
                + "' and " +
                "budget.isActiveBudget=true and budget.status.code='Approved' and budget.financialYear.id="
                + financialYear.getId()
                + getMiscQuery() + " order by budget.name,budgetGroup.name");
        System.out.println("Populate data Query");
        System.out.println("from BudgetDetail where budget.isbere=" + budgetType +"and budget.isActiveBudget=true and budget.status.code='Approved' "
        		+ "and budget.financialYear.id="+ financialYear.getId()+" "+ getMiscQuery() + "order by budget.name,budgetGroup.name");
        System.out.println("2");
        if (budgetVarianceEntries == null)
            budgetVarianceEntries = new ArrayList<BudgetVarianceEntry>();
        for (final BudgetDetail budgetDetail : result) {
            final BudgetVarianceEntry budgetVarianceEntry = new BudgetVarianceEntry();
            budgetVarianceEntry.setBudgetHead(budgetDetail.getBudgetGroup().getName());
            if (budgetDetail.getExecutingDepartment() != null) {
                budgetVarianceEntry.setDepartmentCode(budgetDetail.getExecutingDepartment());
                budgetVarianceEntry.setDepartmentName(microserviceUtils.getDepartmentByCode(budgetDetail.getExecutingDepartment()).getName());
            }
            if (budgetDetail.getFund() != null)
                budgetVarianceEntry.setFundCode(budgetDetail.getFund().getName());
            if (budgetDetail.getFunction() != null)
                budgetVarianceEntry.setFunctionCode(budgetDetail.getFunction().getName());
            budgetVarianceEntry.setDetailId(budgetDetail.getId());
            budgetVarianceEntry.setBudgetCode(budgetDetail.getBudget().getName());
            
            if ("RE".equalsIgnoreCase(budgetType) && !getConsiderReAppropriationAsSeperate()) {
                budgetVarianceEntry.setAdditionalAppropriation(BigDecimal.ZERO);
                final BigDecimal estimateAmount = (budgetDetail.getApprovedAmount() == null ? BigDecimal.ZERO : budgetDetail
                        .getApprovedAmount()).add(budgetDetail.getApprovedReAppropriationsTotal() == null ? BigDecimal.ZERO
                        : budgetDetail.getApprovedReAppropriationsTotal());
                budgetVarianceEntry.setEstimate(estimateAmount);
            } else {
                budgetVarianceEntry.setEstimate(budgetDetail.getApprovedAmount() == null ? BigDecimal.ZERO : budgetDetail
                        .getApprovedAmount());
                budgetVarianceEntry
                        .setAdditionalAppropriation(budgetDetail.getApprovedReAppropriationsTotal() == null ? BigDecimal.ZERO
                                : budgetDetail.getApprovedReAppropriationsTotal());
            }
            budgetVarianceEntry.setTotal(budgetVarianceEntry.getEstimate().add(budgetVarianceEntry.getAdditionalAppropriation()));
            budgetVarianceEntries.add(budgetVarianceEntry);
        }
        populateActualData(financialYear);
    }

    
    
    
    
    public List<BudgetVarianceEntry> populateRestData() {
    	System.out.println("1");
		/*
		 * final CFinancialYear financialYear =
		 * financialYearDAO.getFinancialYearByDate(asOnDate); final boolean
		 * hasApprovedReForYear =
		 * budgetService.hasApprovedReForYear(financialYear.getId()); if
		 * (hasApprovedReForYear) { type = "Revised"; budgetType = Constants.RE; }
		 */
        final List<BudgetDetail> result = persistenceService.findAllBy("from BudgetDetail where budget.isbere='" + budgetType
                + "' and " +
                "budget.isActiveBudget=true and budget.status.code='Approved' "+ getMiscQuery() + " order by budget.name,budgetGroup.name");
       // System.out.println("Populate data Query");
       // System.out.println("from BudgetDetail where budget.isbere=" + budgetType +"and budget.isActiveBudget=true and budget.status.code='Approved' "
        //		+ ""+" "+ getMiscQuery() + "order by budget.name,budgetGroup.name");
        System.out.println("2");
        if (budgetVarianceEntries == null)
            budgetVarianceEntries = new ArrayList<BudgetVarianceEntry>();
        for (final BudgetDetail budgetDetail : result) {
            final BudgetVarianceEntry budgetVarianceEntry = new BudgetVarianceEntry();
            budgetVarianceEntry.setBudgetHead(budgetDetail.getBudgetGroup().getName());
            if (budgetDetail.getExecutingDepartment() != null) {
                budgetVarianceEntry.setDepartmentCode(budgetDetail.getExecutingDepartment());
                
                budgetVarianceEntry.setDepartmentName(departmentService.getDepartmentByCode(budgetDetail.getExecutingDepartment()).getName());
            }
            if (budgetDetail.getFund() != null)
                budgetVarianceEntry.setFundCode(budgetDetail.getFund().getName());
            if (budgetDetail.getFunction() != null)
                budgetVarianceEntry.setFunctionCode(budgetDetail.getFunction().getName());
            budgetVarianceEntry.setDetailId(budgetDetail.getId());
            budgetVarianceEntry.setBudgetCode(budgetDetail.getBudget().getName());
            if ("RE".equalsIgnoreCase(budgetType) && !getConsiderReAppropriationAsSeperate()) {
                budgetVarianceEntry.setAdditionalAppropriation(BigDecimal.ZERO);
                final BigDecimal estimateAmount = (budgetDetail.getApprovedAmount() == null ? BigDecimal.ZERO : budgetDetail
                        .getApprovedAmount()).add(budgetDetail.getApprovedReAppropriationsTotal() == null ? BigDecimal.ZERO
                        : budgetDetail.getApprovedReAppropriationsTotal());
                budgetVarianceEntry.setEstimate(estimateAmount);
            } else {
                budgetVarianceEntry.setEstimate(budgetDetail.getApprovedAmount() == null ? BigDecimal.ZERO : budgetDetail
                        .getApprovedAmount());
                budgetVarianceEntry
                        .setAdditionalAppropriation(budgetDetail.getApprovedReAppropriationsTotal() == null ? BigDecimal.ZERO
                                : budgetDetail.getApprovedReAppropriationsTotal());
            }
            budgetVarianceEntry.setTotal(budgetVarianceEntry.getEstimate().add(budgetVarianceEntry.getAdditionalAppropriation()));
            
            budgetVarianceEntries.add(budgetVarianceEntry);
        }
       
        populateActualDataRest();
        
        return budgetVarianceEntries;
    }

    private String getMiscQuery() {
        final StringBuilder query = new StringBuilder();
        if (budgetDetail.getExecutingDepartment() != null && !"".equals(budgetDetail.getExecutingDepartment()))
        {
        	System.out.println("department : "+budgetDetail.getExecutingDepartment());
        	query.append(" and executingDepartment='").append(budgetDetail.getExecutingDepartment()).append("' ");
        }
        if (budgetDetail.getBudgetGroup() != null && budgetDetail.getBudgetGroup().getId() != null
                && budgetDetail.getBudgetGroup().getId() != -1)
        {
        	System.out.println("budgetDetail.getBudgetGroup().getId() :: "+budgetDetail.getBudgetGroup().getId());
        	query.append(" and budgetGroup.id=").append(budgetDetail.getBudgetGroup().getId());
        }
        if (budgetDetail.getFunction() != null && budgetDetail.getFunction().getId() != null
                && budgetDetail.getFunction().getId() != -1)
        {
        	System.out.println("budgetDetail.getFunction().getId() :: "+budgetDetail.getFunction().getId());
        	query.append(" and function.id=").append(budgetDetail.getFunction().getId());
        }
        if (budgetDetail.getFund() != null && budgetDetail.getFund().getId() != null && budgetDetail.getFund().getId() != -1)
        {
        	System.out.println("budgetDetail.getFund().getId() ::"+budgetDetail.getFund().getId());
        	query.append(" and fund.id=").append(budgetDetail.getFund().getId());
        }
        if (budgetDetail.getFunctionary() != null && budgetDetail.getFunctionary().getId() != null
                && budgetDetail.getFunctionary().getId() != -1)
            query.append(" and functionary.id=").append(budgetDetail.getFunctionary().getId());
        if (budgetDetail.getScheme() != null && budgetDetail.getScheme().getId() != null
                && budgetDetail.getScheme().getId() != -1)
            query.append(" and scheme.id=").append(budgetDetail.getScheme().getId());
        if (budgetDetail.getSubScheme() != null && budgetDetail.getSubScheme().getId() != null
                && budgetDetail.getSubScheme().getId() != -1)
            query.append(" and subScheme.id=").append(budgetDetail.getSubScheme().getId());
        if (budgetDetail.getBoundary() != null && budgetDetail.getBoundary().getId() != null
                && budgetDetail.getBoundary().getId() != -1)
            query.append(" and boundary.id=").append(budgetDetail.getBoundary().getId());
        if (!"".equalsIgnoreCase(accountType) && !"-1".equalsIgnoreCase(accountType))
            query.append(" and budgetGroup.accountType='").append(accountType).append("'");
        return query.toString();
    }

    private void setQueryParams() {
    	System.out.println("7");
        if (shouldShowHeaderField(Constants.EXECUTING_DEPARTMENT) && budgetDetail.getExecutingDepartment() != null
                && budgetDetail.getExecutingDepartment() != "")
            queryParamMap.put("deptId", budgetDetail.getExecutingDepartment());
        if (shouldShowHeaderField(Constants.FUNCTION) && budgetDetail.getFunction() != null
                && budgetDetail.getFunction().getId() != null && budgetDetail.getFunction().getId() != -1
                && budgetDetail.getFunction().getId() != 0)
            queryParamMap.put("functionId", budgetDetail.getFunction().getId().toString());
        if (shouldShowHeaderField(Constants.FUND) && budgetDetail.getFund() != null && budgetDetail.getFund().getId() != null
                && budgetDetail.getFund().getId() != -1 && budgetDetail.getFund().getId() != 0)
            queryParamMap.put("fundId", budgetDetail.getFund().getId().toString());
        if (shouldShowHeaderField(Constants.SCHEME) && budgetDetail.getScheme() != null
                && budgetDetail.getScheme().getId() != null && budgetDetail.getScheme().getId() != -1
                && budgetDetail.getScheme().getId() != 0)
            queryParamMap.put("schemeId", budgetDetail.getScheme().getId().toString());
        if (shouldShowHeaderField(Constants.SUBSCHEME) && budgetDetail.getSubScheme() != null
                && budgetDetail.getSubScheme().getId() != null && budgetDetail.getSubScheme().getId() != -1
                && budgetDetail.getSubScheme().getId() != 0)
            queryParamMap.put("subSchemeId", budgetDetail.getSubScheme().getId().toString());
        if (shouldShowHeaderField(Constants.FUNCTIONARY) && budgetDetail.getFunctionary() != null
                && budgetDetail.getFunctionary().getId() != null && budgetDetail.getFunctionary().getId() != -1
                && budgetDetail.getFunctionary().getId() != 0)
            queryParamMap.put("functionaryId", budgetDetail.getFunctionary().getId().toString());
    }

    private void populateActualData(final CFinancialYear financialYear) {
        final String fromDate = Constants.DDMMYYYYFORMAT2.format(financialYear.getStartingDate());
        //System.out.println("4");
        if (budgetVarianceEntries != null && budgetVarianceEntries.size() != 0) {
        	System.out.println("5");
            setQueryParams();
            final List<Object[]> resultForVoucher = budgetDetailService.fetchActualsForFYWithParams(fromDate, "'"
                    + Constants.DDMMYYYYFORMAT2.format(asOnDate) + "'", formMiscQuery("vmis", "gl", "vh"));
            extractData(resultForVoucher);
            //system.out.println("");
            final List<Object[]> resultForBill = budgetDetailService.fetchActualsForBillWithVouchersParams(fromDate, "'"
                    + Constants.DDMMYYYYFORMAT2.format(asOnDate) + "'", formMiscQuery("bmis", "bdetail", "bmis"));
            extractData(resultForBill);
        } else {
            addActionError("no data found");
        }
    }

    
    
    private void populateActualDataRest() {
       // final String fromDate = Constants.DDMMYYYYFORMAT2.format(financialYear.getStartingDate());
        System.out.println("4");
        if (budgetVarianceEntries != null && budgetVarianceEntries.size() != 0) {
        	System.out.println("5");
            setQueryParams();
           // System.out.println("calling budgetDetailService.fetchActualsForFYWithParams");
            final List<Object[]> resultForVoucher = budgetDetailService.fetchActualsForFYWithParamsRest( formMiscQuery("vmis", "gl", "vh"));
          //  System.out.println("calling "+resultForVoucher.get(0)[0]);
            extractData(resultForVoucher);
           // System.out.println("budgetDetailService.fetchActualsForBillWithVouchersParams");
            final List<Object[]> resultForBill = budgetDetailService.fetchActualsForBillWithVouchersParamsRest( formMiscQuery("bmis", "bdetail", "bmis"));
            extractData(resultForBill);
        } else {
            addActionError("no data found");
        }
    }

    private void extractData(final List<Object[]> result) {
        final Map<String, String> budgetDetailIdsAndAmount = new HashMap<String, String>();
        
        if (result == null)
            return;
        for (final Object[] row : result)
            if (row[0] != null && row[1] != null) {
            	if(budgetDetailIdsAndAmount.containsKey(row[0].toString())) {
            		System.out.println(":::::::::1::::"+new BigDecimal(budgetDetailIdsAndAmount.get(row[0].toString())));
            		System.out.println(":::::::::2:::"+new BigDecimal(row[1].toString()));
            		
            		budgetDetailIdsAndAmount.put(row[0].toString(), (new BigDecimal(budgetDetailIdsAndAmount.get(row[0].toString())).add(new BigDecimal(row[1].toString()))).toString());
            	}else {
                budgetDetailIdsAndAmount.put(row[0].toString(), row[1].toString());
            	}
            }
        
        for (final BudgetVarianceEntry row : budgetVarianceEntries) {
            final BigDecimal actual = row.getActual();
            
            if (budgetDetailIdsAndAmount.get(row.getDetailId().toString()) != null) {
                if (actual == null || BigDecimal.ZERO.compareTo(actual) == 0)
                    row.setActual(new BigDecimal(budgetDetailIdsAndAmount.get(row.getDetailId().toString())));
                else
                    row.setActual(
                            row.getActual().add(new BigDecimal(budgetDetailIdsAndAmount.get(row.getDetailId().toString()))));
            } else if (actual == null)
                row.setActual(BigDecimal.ZERO);
            row.setVariance(row.getEstimate().add(
                    row.getAdditionalAppropriation().subtract(row.getActual() == null ? BigDecimal.ZERO : row.getActual())));
        
        }
    }

    @SkipValidation
    @Action(value = "/report/budgetVarianceReport-exportXls")
    public String exportXls() throws JRException, IOException {
        populateData();
        final ReportRequest reportInput = new ReportRequest(jasperpath, budgetVarianceEntries, getParamMap());
        reportInput.setReportFormat(ReportFormat.XLS);
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
        return "XLS";
    }

    protected void checkMandatoryField(final String objectName, final String fieldName, final Object value,
            final String errorKey) {
        if (mandatoryFields.contains(fieldName) && (value == null || value.equals(-1) || value.equals(0)))
            addFieldError(objectName, getText(errorKey));
    }

    @Override
    public void validate() {
        checkMandatoryField("fund", Constants.FUND, budgetDetail.getFund() == null ? Integer.parseInt("0") : budgetDetail
                .getFund().getId(), "voucher.fund.mandatory");
        checkMandatoryField("executingDepartment", Constants.EXECUTING_DEPARTMENT,
                budgetDetail.getExecutingDepartment() == null ? "" : budgetDetail.getExecutingDepartment(),
                "voucher.department.mandatory");
        checkMandatoryField("scheme", Constants.SCHEME, budgetDetail.getScheme() == null ? Integer.parseInt("0") : budgetDetail
                .getScheme().getId(), "voucher.scheme.mandatory");
        checkMandatoryField("subScheme", Constants.SUBSCHEME, budgetDetail.getSubScheme() == null ? Integer.parseInt("0")
                : budgetDetail.getSubScheme().getId(), "voucher.subscheme.mandatory");
        checkMandatoryField("function", Constants.FUNCTION, budgetDetail.getFunction() == null ? Integer.parseInt("0")
                : budgetDetail.getFunction().getId(), "budget.function.mandatory");
        checkMandatoryField("functionary", Constants.FUNCTIONARY, budgetDetail.getFunctionary() == null ? Integer.parseInt("0")
                : budgetDetail.getFunctionary().getId(), "voucher.functionary.mandatory");
    }

    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setEgovCommon(final EgovCommon egovCommon) {
        this.egovCommon = egovCommon;
    }

    public EgovCommon getEgovCommon() {
        return egovCommon;
    }

    public void setBudgetVarianceEntries(final List<BudgetVarianceEntry> bankBookViewEntries) {
        budgetVarianceEntries = bankBookViewEntries;
    }

    public List<BudgetVarianceEntry> getBudgetVarianceEntries() {
        return budgetVarianceEntries;
    }

    public Vouchermis getVouchermis() {
        return vouchermis;
    }

    @Override
    public Object getModel() {
        return budgetDetail;
    }

    public void setVouchermis(final Vouchermis vouchermis) {
        this.vouchermis = vouchermis;
    }

    public List<String> getAccountTypeList() {
        return accountTypeList;
    }

    public void setBudgetDetail(final BudgetDetail budgetDetail) {
        this.budgetDetail = budgetDetail;
    }

    public BudgetDetail getBudgetDetail() {
        return budgetDetail;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setReportService(final ReportService reportService) {
        this.reportService = reportService;
    }

    public void setBudgetDetailService(final BudgetDetailService budgetDetailService) {
        this.budgetDetailService = budgetDetailService;
    }

    public void setFinancialYearDAO(final FinancialYearHibernateDAO financialYearDAO) {
        this.financialYearDAO = financialYearDAO;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setBudgetService(final BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public boolean isFieldMandatory(final String field) {
        return mandatoryFields.contains(field);
    }

    public String getDepartmentName() {
        if (budgetDetail.getExecutingDepartment() != null && budgetDetail.getExecutingDepartment() != "") {
            final Department department = microserviceUtils.getDepartmentByCode(budgetDetail.getExecutingDepartment());
            return department.getName();
        }
        return "";
    }

    private boolean getConsiderReAppropriationAsSeperate() {
        final List<AppConfigValues> appList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
                "CONSIDER_RE_REAPPROPRIATION_AS_SEPARATE");
        String appValue = "-1";
        appValue = appList.get(0).getValue();
        return "Y".equalsIgnoreCase(appValue);
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public CFunction getFunction() {
        return function;
    }

    public void setFunction(CFunction function) {
        this.function = function;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getFunds() {
		return funds;
	}

	public void setFunds(String funds) {
		this.funds = funds;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public String getAccCode() {
		return accCode;
	}

	public void setAccCode(String accCode) {
		this.accCode = accCode;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getVhId() {
		return vhId;
	}

	public void setVhId(String vhId) {
		this.vhId = vhId;
	}
    	
	

}