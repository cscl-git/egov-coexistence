package org.egov.services.report;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.dao.FunctionDAO;
import org.egov.commons.service.ChartOfAccountsService;
import org.egov.commons.service.FunctionService;
import org.egov.commons.service.FundService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.report.CapitalRevenueDataPOJO;
import org.egov.model.report.CapitalRevenueRequestPojo;
import org.egov.model.report.DedicatedExpense;
import org.egov.model.report.DedicatedExpenseViewData;
import org.egov.model.report.DivisionReportPOJO;
import org.egov.model.report.ReceiptReportPOJO;
import org.egov.model.report.ReportBean;
import org.egov.services.budget.BudgetDetailService;
import org.egov.utils.FinancialConstants;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.exilant.eGov.src.reports.TrialBalanceBean;




@Service
public class GeneralLedgerReportService   {
	
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
	FundService fundService;
	@Autowired
    @Qualifier("chartOfAccountsService")
    private ChartOfAccountsService chartOfAccountsService;

	@Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
	
	@Autowired
	private DepartmentService departmentservice;
	
	@Autowired
	private FunctionService functionService;
	
	public List<String> glnames= new ArrayList<String>();
	
	 @PersistenceContext
	    private EntityManager entityManager;
	    @Autowired
	    private FinancialYearDAO financialYearDAO;
	    
	    
	    @Autowired
		private AppConfigValueService appConfigValuesService;
	    private static final Logger LOGGER = Logger.getLogger(GeneralLedgerReportService.class);
	    private String schemeId;
	    List<String> glcode = new ArrayList<String>();
	    private BigDecimal totalClosingBalance = BigDecimal.ZERO;
		private BigDecimal totalOpeningBalance = BigDecimal.ZERO;
		private BigDecimal totalDebitAmount = BigDecimal.ZERO;
		private BigDecimal totalCreditAmount = BigDecimal.ZERO;
		private BigDecimal totalAmount = BigDecimal.ZERO;
		private final SimpleDateFormat mmddyyyyformatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	    //List<TrialBalanceBean> al = new ArrayList<TrialBalanceBean>();
		List<TrialBalanceBean> nonZeroItemsList = new ArrayList<TrialBalanceBean>();
		List<TrialBalanceBean> finalList = new ArrayList<TrialBalanceBean>();
		//private ReportBean rb = new ReportBean();
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		private String removeEntrysWithZeroAmount = "";
		
		private Date startDate = new Date();
		private Date endDate = new Date();
		
		 public Session getCurrentSession() {
		        return entityManager.unwrap(Session.class);
		    }
		public class COAcomparator implements Comparator<TrialBalanceBean> {
			@Override
			public int compare(final TrialBalanceBean o1, final TrialBalanceBean o2) {
				return o1.getAccCode().compareTo(o2.getAccCode());
			}

		}
		public void prepare() {
			glcode.clear();
			glcode.add("3117001");
			glcode.add("3501150");
			glcode.add("3117002");
			glcode.add("3502009");
			glcode.add("3502002");
			glcode.add("4601002");
			glcode.add("3503003");
			glcode.add("3504101");
			glcode.add("3502068");
			glcode.add("3502064");
			
			glnames.clear();
			
			
			  glnames.add("GPF");
			  glnames.add(" DEFIN  PEN. Contt.");
			  glnames.add("PENS.Contt.10%");
			  glnames.add("INCOME TAX");
			  glnames.add("L.I. C.");
			  glnames.add("HBA");
			  glnames.add("COURT  Recove");
			  glnames.add("All L/Fee");
			  glnames.add("GIS LIC");
			  glnames.add("B/Loan");
			  glnames.add("TOTAL");
			  
		}
	
	public List<DedicatedExpenseViewData> getDedicated(StringBuilder qs, Date fromdate, Date todate,String dept_code) {
		
		
		//System.out.println("Inside dedicated");
		StringBuilder query = new StringBuilder(" ");
		String department = "";
		query.append("select id as id,vouchernumber as vouchernumber ,voucherdate as voucherdate ,department as department,glcode as glcode ,creditamount as creditamount,debitamount as debitamount from exp_ded_view ");
		query.append(" where ");
		query.append(qs);
		query.append(" order by glcode ASC ");
		
		Query q =entityManager.createNativeQuery(query.toString());
		//q.setParameter("fromdate", fromdate);
		//q.setParameter("todate", todate);
		if(dept_code!=null) {
			try {
				
				department  = departmentService.getDepartmentByCode(dept_code).getName();
				q.setParameter("department", department);
			}catch(Exception ex) {
				ex.printStackTrace();
				
			}
			
		}
		List<DedicatedExpense> data = new ArrayList<DedicatedExpense>();
		//System.out.println("Query Printing---"+query.toString());
		try {
			List<Object[]> list = q.getResultList();
			System.out.println(list.size());
			
			
			if(list.size()>0) {
				for (final Object[] o : list)
	    		{
					DedicatedExpense a = new DedicatedExpense();
					
					a.setId(o[0].toString());
					a.setVouchernumber(o[1].toString());
					a.setVoucherdate(o[2].toString());
					a.setDepartment(o[3].toString());
					a.setGlcode(o[4].toString());
					a.setCreditamount((null!=o[5])? new BigDecimal(o[5].toString()):new BigDecimal(0));
					a.setDebitamount((null!=o[6])? new BigDecimal(o[6].toString()):new BigDecimal(0));
					
					//System.out.println(o[0].toString()+" "+o[1].toString()+"  "+o[2].toString()+" "+o[3].toString()+" "+o[4].toString()+" "+o[5].toString());
					data.add(a);
	    		}
			}
			
			
			System.out.println(list.size());
		}catch(Exception ex) {
			ex.printStackTrace();
			
		}
		List<DedicatedExpenseViewData> d = new ArrayList<DedicatedExpenseViewData>();
		Department deparrtm = departmentservice.getDepartmentByCode(dept_code);
				if(dept_code!=null) {
			
			 d = calculateSummation(data,deparrtm);
		}else {
			 d = calculateSummation(data,null);
		}
		
		return d;
	}
	
	

	public List<DedicatedExpenseViewData>  calculateSummation(List<DedicatedExpense> dedicatedExpenses,Department depget) {
			
		List<DedicatedExpenseViewData> dvlist= new ArrayList<DedicatedExpenseViewData>();
		List<Department> departmentList= new ArrayList<Department>();
		if(depget==null) {
			departmentList= departmentservice.getAllDepartments();
		}else {
			departmentList.add(depget);
		}
		 
		prepare();
		
		Map<String,List<DedicatedExpense>> dedicatedListMap=new HashMap<String,List<DedicatedExpense>>();
		List<DedicatedExpense> dedicatedList=null;
		for(DedicatedExpense row : dedicatedExpenses)
		{
			if(dedicatedListMap.get(row.getDepartment())==null)
			{
				dedicatedList=new ArrayList<DedicatedExpense>();
				dedicatedList.add(row);
				dedicatedListMap.put(row.getDepartment(),dedicatedList);
			}
			else
			{
				dedicatedListMap.get(row.getDepartment()).add(row);
			}
		}
		
		DedicatedExpenseViewData  dview = null;
		//System.out.println(dedicatedListMap);
		for(Department dep:departmentList)
		{
			 BigDecimal glsum1=new BigDecimal(0);
			 BigDecimal glsum2=new BigDecimal(0);
			 BigDecimal glsum3=new BigDecimal(0);
			 BigDecimal glsum4=new BigDecimal(0);
			 BigDecimal glsum5=new BigDecimal(0);
			 BigDecimal glsum6=new BigDecimal(0);
			 BigDecimal glsum7=new BigDecimal(0);
			 BigDecimal glsum8=new BigDecimal(0);
			 BigDecimal glsum9=new BigDecimal(0);
			 BigDecimal glsum10=new BigDecimal(0);
			 BigDecimal sum=new BigDecimal(0);
			if(dedicatedListMap.get(dep.getName()) != null)
			{	
			  dview = new DedicatedExpenseViewData();
				dview.setName(dep.getName());		
				for(DedicatedExpense row:dedicatedListMap.get(dep.getName()))
				{	if(row.getGlcode().equalsIgnoreCase("3117001"))
				{
					glsum1=glsum1.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3501150"))
				{
					glsum2=glsum2.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3117002"))
				{
					glsum3=glsum3.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3502009"))
				{
					glsum4=glsum4.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3502002"))
				{
					glsum5=glsum5.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("4601002"))
				{
					glsum6=glsum6.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3503003"))
				{
					glsum7=glsum7.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3504101"))
				{
					glsum8=glsum8.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3502068"))
				{
					glsum9=glsum9.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				if(row.getGlcode().equalsIgnoreCase("3502064"))
				{
					glsum10=glsum10.add(row.getCreditamount().subtract(row.getDebitamount()));
				}
				}
				dview.setGlsum1(glsum1);
				dview.setGlsum2(glsum2);
				
				dview.setGlsum3(glsum3);
				dview.setGlsum4(glsum4);
				dview.setGlsum5(glsum5);
				dview.setGlsum6(glsum6);
				dview.setGlsum7(glsum7);
				dview.setGlsum8(glsum8);
				dview.setGlsum9(glsum9);
				dview.setGlsum10(glsum10);
				//dview set indidival values
				//i++;
				//}
				dvlist.add(dview);//for each department
				
			}
			else
			{
				
				dview= new DedicatedExpenseViewData();
				dview.setName(dep.getName());
				
				dview.setGlsum1(new BigDecimal(0.0));
				dview.setGlsum2(new BigDecimal(0.0));
				dview.setGlsum3(new BigDecimal(0.0));
				dview.setGlsum4(new BigDecimal(0.0));
				dview.setGlsum5(new BigDecimal(0.0));
				dview.setGlsum6(new BigDecimal(0.0));
				dview.setGlsum7(new BigDecimal(0.0));
				dview.setGlsum8(new BigDecimal(0.0));
				dview.setGlsum9(new BigDecimal(0.0));
				dview.setGlsum10(new BigDecimal(0.0));
				dview.setGlsum10(new BigDecimal(0.0));
				dvlist.add(dview);
				
			}
			
			
			
		}
		
		BigDecimal rowtotal =new BigDecimal(0.0);
		BigDecimal coltotal1=new BigDecimal(0.0);
		List<DedicatedExpenseViewData> dviewNew = new ArrayList<DedicatedExpenseViewData>();
		List<DedicatedExpenseViewData> dviewNew1 = new ArrayList<DedicatedExpenseViewData>();
		DedicatedExpenseViewData lastdata = new DedicatedExpenseViewData();
		
		lastdata.setGlsum1(new BigDecimal(0.0));
		lastdata.setGlsum2(new BigDecimal(0.0));
		lastdata.setGlsum3(new BigDecimal(0.0));
		lastdata.setGlsum4(new BigDecimal(0.0));
		lastdata.setGlsum5(new BigDecimal(0.0));
		lastdata.setGlsum6(new BigDecimal(0.0));
		lastdata.setGlsum7(new BigDecimal(0.0));
		lastdata.setGlsum8(new BigDecimal(0.0));
		lastdata.setGlsum9(new BigDecimal(0.0));
		lastdata.setGlsum10(new BigDecimal(0.0));
		lastdata.setSum(new BigDecimal(0.0));
		for(DedicatedExpenseViewData dv:dvlist) {
			
			if(dv.getGlsum1()==null){
				dv.setGlsum1(new BigDecimal(0.0));
			}
			if(dv.getGlsum2()==null){
				dv.setGlsum2(new BigDecimal(0.0));
			}
			if(dv.getGlsum3()==null){
				dv.setGlsum3(new BigDecimal(0.0));
			}
			if(dv.getGlsum4()==null){
				dv.setGlsum4(new BigDecimal(0.0));
			}
			if(dv.getGlsum5()==null){
				dv.setGlsum5(new BigDecimal(0.0));
			}
			if(dv.getGlsum6()==null){
				dv.setGlsum6(new BigDecimal(0.0));
			}
			if(dv.getGlsum7()==null){
				dv.setGlsum7(new BigDecimal(0.0));
			}
			if(dv.getGlsum8()==null){
				dv.setGlsum8(new BigDecimal(0.0));
			}
			if(dv.getGlsum9()==null){
				dv.setGlsum9(new BigDecimal(0.0));
			}
			if(dv.getGlsum10()==null){
				dv.setGlsum10(new BigDecimal(0.0));
			}
			
			if(dv.getSum()==null){
				dv.setSum(new BigDecimal(0.0));
			}
	
			
		rowtotal = dv.getGlsum1().add(dv.getGlsum2()).add(dv.getGlsum3()).add(dv.getGlsum4()).add(dv.getGlsum5())
				.add(dv.getGlsum6()).add(dv.getGlsum7()).add(dv.getGlsum8()).add(dv.getGlsum9()).add(dv.getGlsum10());
		dv.setSum(rowtotal);
		
		lastdata.setGlsum1(lastdata.getGlsum1().add(dv.getGlsum1()));
		lastdata.setGlsum2(lastdata.getGlsum2().add(dv.getGlsum2()));
		lastdata.setGlsum3(lastdata.getGlsum3().add(dv.getGlsum3()));
		lastdata.setGlsum4(lastdata.getGlsum4().add(dv.getGlsum4()));
		lastdata.setGlsum5(lastdata.getGlsum5().add(dv.getGlsum5()));
		lastdata.setGlsum6(lastdata.getGlsum6().add(dv.getGlsum6()));
		lastdata.setGlsum7(lastdata.getGlsum7().add(dv.getGlsum7()));
		lastdata.setGlsum8(lastdata.getGlsum8().add(dv.getGlsum8()));
		lastdata.setGlsum9(lastdata.getGlsum9().add(dv.getGlsum9()));
		lastdata.setGlsum10(lastdata.getGlsum10().add(dv.getGlsum10()));
		
		lastdata.setSum(lastdata.getSum().add(dv.getSum()));
		
		dviewNew.add(dv);
	
			
			
		}
		
		lastdata.setName("Total");
		dviewNew.add(lastdata);
		
		
		return dviewNew;
	}
	
	
	
	

	
	public List<String> sendGlname(){
		prepare();
		return glnames;
	}
	
	public byte[] DedicatedExpense(Map<String,String>headerData,List<DedicatedExpenseViewData>  exp) {
		System.out.println("Dedicated");
		
		prepare();
		byte[]fileContent=null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Balance Sheet Report");
		
			HSSFCellStyle style = wb.createCellStyle();  
		
			int i =0;
			Row row1 = sheet.createRow(i++);	  
			Cell c1=  row1.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(headerData.get("h1"));
			
			Row row2 = sheet.createRow(i++);	  
			Cell c2=  row2.createCell(0);
			c2.setCellStyle(style);
			c2.setCellValue("Department/GLCODE");
			

            int j =1;
           
            for(String f:glnames) {
					Cell c6=  row2.createCell(j++);
					c6.setCellStyle(style);
					c6.setCellValue(f);
				}

				

			for(DedicatedExpenseViewData s : exp) {
				
				 	Row row = sheet.createRow(i++);
				 	Cell cell0 = row.createCell(0);
					Cell cell1 = row.createCell(1);
					Cell cell2 = row.createCell(2);
					Cell cell3 = row.createCell(3);
					Cell cell4 = row.createCell(4);
					Cell cell5 = row.createCell(5);
					Cell cell6 = row.createCell(6);
					Cell cell7 = row.createCell(7);
					Cell cell8 = row.createCell(8);
					Cell cell9 = row.createCell(9);
					Cell cell10 = row.createCell(10);
					Cell cell11 = row.createCell(11);
					
					
					cell0.setCellValue(s.getName().toString());
					cell1.setCellValue(s.getGlsum1().toString());
					cell2.setCellValue(s.getGlsum2().toString());
					cell3.setCellValue(s.getGlsum3().toString());
					cell4.setCellValue(s.getGlsum4().toString());
					cell5.setCellValue(s.getGlsum5().toString());
					cell6.setCellValue(s.getGlsum6().toString());
					cell7.setCellValue(s.getGlsum7().toString());
					cell8.setCellValue(s.getGlsum8().toString());
					cell9.setCellValue(s.getGlsum9().toString());
					cell10.setCellValue(s.getGlsum10().toString());
					cell11.setCellValue(s.getSum().toString());
					
					
					 
						
		        } 
			
			int numberOfSheets = wb.getNumberOfSheets();
			 for (int x = 0; x < numberOfSheets; x++) {
			        Sheet sheet1 = wb.getSheetAt(x);
			        int total_row=sheet1.getLastRowNum();
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
	
	
	
	
	
	public byte[] CapitalRevenueExl(Map<String,String>headerData,List<ArrayList<String>>  exp,Set<String>depname) {
		System.out.println("Dedicated");
		
		prepare();
		byte[]fileContent=null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Capital- Revenue Expenditure Report");
			HSSFCellStyle style = wb.createCellStyle();  
			int i =0;
			Row row1 = sheet.createRow(i++);	  
			Cell c1=  row1.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(headerData.get("h1"));
			
			
			Row row2 = sheet.createRow(i++);
			int j=1;
			
			for(ArrayList<String> s : exp) {
				 	Row row = sheet.createRow(i++);
				 	for(int z =0;z<s.size();z++) {
				 		Cell cell0 = row.createCell(z);
						cell0.setCellValue((null!=s.get(z))?s.get(z).toString():"0");
				 	} 
						
		        } 
			
			int numberOfSheets = wb.getNumberOfSheets();
			 for (int x = 0; x < numberOfSheets; x++) {
			        Sheet sheet1 = wb.getSheetAt(x);
			        int total_row=sheet1.getLastRowNum();
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
	
	
	
	public byte[] getRevenueCapitalDataExl(CapitalRevenueRequestPojo capital) {
		ModelMap  m = new ModelMap();
		finalList = null;
		String startwithForglocode="";
		String startwithForaccCode="";
		String reportgenerated="The Report is generated for ";
		
		
		Set<String>deptname=new HashSet<String>();
		Set<String>functionname=new HashSet<String>();
		Map<String,String>headerData  = new HashMap<>();
		
		reportgenerated = reportgenerated+"From: "+capital.getFrom_date()+" to "+capital.getTo_date();
		headerData.put("h1",reportgenerated );

			if(capital.getExpense_type().equalsIgnoreCase("1")) {
				startwithForglocode="2";
			}if(capital.getExpense_type().equalsIgnoreCase("2")) {
				startwithForglocode="4";
			}
			
			startwithForaccCode="000";
			StringBuilder queryString= new StringBuilder("");
			queryString.append(" voucherdate >= TO_DATE('"+ capital.getFrom_date()+"','dd/mm/yyyy') and voucherdate <= TO_DATE('"+capital.getTo_date()+"','dd/mm/yyyy')" );
			queryString.append(" and fundid = "+capital.getFund());
			 if(capital.getDepartment()!=null) {
				 queryString.append(" and deptcode = '"+capital.getDepartment()+"'");
			 }
			 List<CapitalRevenueDataPOJO> fetchlist = capitalRevenuDataFromView(queryString);
			 List<CapitalRevenueDataPOJO> cleanList = new ArrayList<CapitalRevenueDataPOJO>();
			
			 for(CapitalRevenueDataPOJO a:fetchlist) {
					CapitalRevenueDataPOJO tb = new CapitalRevenueDataPOJO();
					if(a.getGlcode().startsWith(startwithForglocode) && null!=a.getFcode() && !a.getFcode().startsWith(startwithForaccCode)) {
						if(capital.getExpense_type().equalsIgnoreCase("1")) {
							int lbt =  Integer.parseInt(a.getFcode());
							//>50 for reve
							if(lbt>50) {
							//System.out.println("fncname---->>>   "+a.getFname()+"   debit--->>  "+a.getDebitamount()+"  deptnmae---->>>  "+a.getDepartment()+"  fnccode--->>  "+a.getFcode());
							tb=a;
							cleanList.add(tb);
							}
						}
						if(capital.getExpense_type().equalsIgnoreCase("2")) {
							
							int lbt =  Integer.parseInt(a.getFcode());
							if(lbt<=50) {
								//<=50 for capital
							//System.out.println("fncname---->>>   "+a.getFname()+"   debit--->>  "+a.getDebitamount()+"  deptnmae---->>>  "+a.getDepartment()+"  fnccode--->>  "+a.getFcode());
							tb=a;
							cleanList.add(tb);
							}
						}
					}
					functionname.add(a.getFname());
					deptname.add(a.getDepartment());
				}
			
			deptname.removeIf(x -> x == null);
			
			List<CFunction> fn= functionDAO.getAllActiveFunctions();
			functionname.clear();
			
				if(capital.getExpense_type().equalsIgnoreCase("1")) {	
								for(CFunction f:fn) {	
									if(!f.getCode().startsWith(startwithForaccCode)) {
										int lbt =  Integer.parseInt(f.getCode());
										//>50 for reve
										if(lbt>50) {
											functionname.add(f.getName());
										}
										
									}	
								}
							}
							
					if(capital.getExpense_type().equalsIgnoreCase("2")) {
							for(CFunction f:fn) {
									
									if(!f.getCode().startsWith(startwithForaccCode)) {
										
										int lbt =  Integer.parseInt(f.getCode());
										//<=50 for cap
										if(lbt<=50) {
											//System.out.println("Function Code"+f.getCode());
											functionname.add(f.getName());
										}
									}
									
								}
							}

			functionname.removeIf(x -> x==null);
			fetchlist = new ArrayList<CapitalRevenueDataPOJO>();
			CapitalRevenueDataPOJO t =null;
			for(String a :functionname ) {
				
				for(String d:deptname) {
					
					BigDecimal sum = new BigDecimal(0.0); 
					t =  new CapitalRevenueDataPOJO();
					for(CapitalRevenueDataPOJO tb:cleanList) {
						
						if(tb.getFname().equalsIgnoreCase(a)&&tb.getDepartment().equalsIgnoreCase(d)) {
							t = tb;
							sum =sum.add(tb.getDebitamount());
							t.setTotalSum(sum);
						}
					}
					fetchlist.add(t);
				}
				
			}
			cleanList =new ArrayList<CapitalRevenueDataPOJO>();
			  for(CapitalRevenueDataPOJO tb:fetchlist) { 
				  if( null!=tb.getFcode() && null!=tb.getDepartment()) {
					  cleanList.add(tb);
				  } 
			  }
			ArrayList< ArrayList<String>> send = new ArrayList<ArrayList<String>>();
			send = getcaptitalRevenueFinalData(cleanList,functionname,deptname);
			return CapitalRevenueExl(headerData,send,deptname);
		
		
	}
	
	public ArrayList<ArrayList<String>> getRevenueCapitalData(CapitalRevenueRequestPojo capital) {
		finalList = null;
		String startwithForglocode="";
		String startwithForaccCode="";
		ReportBean rb = new ReportBean();
		Set<String>deptname=new HashSet<String>();
		Set<String>functionname=new HashSet<String>();
		  //1-50 capital
		//>50 revenue
		
		//1=revenue
		//2=capital
			if(capital.getExpense_type().equalsIgnoreCase("1")) {
				startwithForglocode="2";
			}else {
				startwithForglocode="4";
			}
			startwithForaccCode="000";
			StringBuilder queryString= new StringBuilder("");
			queryString.append(" voucherdate >= TO_DATE('"+ capital.getFrom_date()+"','dd/mm/yyyy') and voucherdate <= TO_DATE('"+capital.getTo_date()+"','dd/mm/yyyy')" );
			queryString.append(" and fundid = "+capital.getFund());
			 if(capital.getDepartment()!=null) {
				 queryString.append(" and deptcode = '"+capital.getDepartment()+"'");
			 }
		 List<CapitalRevenueDataPOJO> fetchlist = capitalRevenuDataFromView(queryString);
		 List<CapitalRevenueDataPOJO> cleanList = new ArrayList<CapitalRevenueDataPOJO>();
			for(CapitalRevenueDataPOJO a:fetchlist) {
				CapitalRevenueDataPOJO tb = new CapitalRevenueDataPOJO();
				if(a.getGlcode().startsWith(startwithForglocode) && null!=a.getFcode() && !a.getFcode().startsWith(startwithForaccCode)) {
						//>50 for reve
					if(capital.getExpense_type().equalsIgnoreCase("1")) {
						int lbt =  Integer.parseInt(a.getFcode());
						if(lbt>50) {
							//System.out.println("fncname---->>>   "+a.getFname()+"   debit--->>  "+a.getDebitamount()+"  deptnmae---->>>  "+a.getDepartment()+"  fnccode--->>  "+a.getFcode());
						tb=a;
						cleanList.add(tb);
						}
					}
					if(capital.getExpense_type().equalsIgnoreCase("2")) {
						//<=50 for capital
						int lbt =  Integer.parseInt(a.getFcode());
						if(lbt<=50) {
							//System.out.println("fncname---->>>   "+a.getFname()+"   debit--->>  "+a.getDebitamount()+"  deptnmae---->>>  "+a.getDepartment()+"  fnccode--->>  "+a.getFcode());
						tb=a;
						cleanList.add(tb);
						}
					}
				
					
				}
				functionname.add(a.getFname());
				deptname.add(a.getDepartment());
			}
			
			//rev <=50
			//cap >50 fundcode
			deptname.removeIf(x -> x == null);
			List<CFunction> fn= functionDAO.getAllActiveFunctions();
			functionname.clear();	
				if(capital.getExpense_type().equalsIgnoreCase("1")) {		
								for(CFunction f:fn) {
									//>50 for reve
									if(!f.getCode().startsWith(startwithForaccCode)) {
										int lbt =  Integer.parseInt(f.getCode());
										if(lbt>50) {
											functionname.add(f.getName());
										}
										
									}
									
								}
							}
							
					if(capital.getExpense_type().equalsIgnoreCase("2")) {
							for(CFunction f:fn) {
									
									if(!f.getCode().startsWith(startwithForaccCode)) {
										//<=50 for capital
										int lbt =  Integer.parseInt(f.getCode());
										if(lbt<=50) {
											functionname.add(f.getName());
										}
									}	
								}
							}

			functionname.removeIf(x -> x==null);
			fetchlist = new ArrayList<CapitalRevenueDataPOJO>();
			CapitalRevenueDataPOJO t =null;
			for(String a :functionname ) {
				for(String d:deptname) {
					BigDecimal sum = new BigDecimal(0); 
					t =  new CapitalRevenueDataPOJO();
					for(CapitalRevenueDataPOJO tb:cleanList) {
						
						if(tb.getFname().equalsIgnoreCase(a)&&tb.getDepartment().equalsIgnoreCase(d)) {
							t = tb;
							sum =sum.add(tb.getDebitamount());
							t.setTotalSum(sum);		
						}
					}	
					fetchlist.add(t);
				}		
			}
			
		  cleanList =new ArrayList<CapitalRevenueDataPOJO>();
		  for(CapitalRevenueDataPOJO tb:fetchlist) { 
			  if( null!=tb.getFcode() && null!=tb.getDepartment()) {
				  cleanList.add(tb);
			  } 
		  }
		  return getcaptitalRevenueFinalData(cleanList,functionname,deptname);	
	}
	
	public ArrayList<ArrayList<String>>getcaptitalRevenueFinalData(List <CapitalRevenueDataPOJO>cleanList,Set<String> fncname,Set<String>depname){
		ArrayList<String> toprow = new ArrayList<String>();
		ArrayList<ArrayList<String>> finallist = new ArrayList<ArrayList<String>>();
		Set<String>depnamenew = new HashSet<String>();
		depnamenew.add("Budget Heads");
		ArrayList<String> dlist = new ArrayList<String>();
		
		dlist.add(0,"Budget Heads");
		dlist.addAll(depname);
		dlist.add("Total");	
		for (String dep : dlist) {
			toprow.add(dep);
		}
		finallist.add(toprow);
		finallist.removeIf(x -> x == null);
		ArrayList<String> bottomrow = new ArrayList<String>(toprow.size()-1);
		bottomrow.add(0,"Total");
		//System.out.println(toprow);
		for(int i=0;i<toprow.size()-1;i++) {
		  	bottomrow.add("");
		  }
		Set <String> check = new HashSet<String>();
		ArrayList<String> c = null;
		int toprowsize=toprow.size();
		if(toprow.size()>2) {
			 toprowsize =toprow.size()-3;
		}
		
		
		for(String fs:fncname) {
			c=new ArrayList<String>(toprowsize);
			c.add(0,fs);
			  for(int i=0;i<=toprowsize;i++) 
			  { 
				  c.add("0.0");
			  }
			 	for(CapitalRevenueDataPOJO tb: cleanList) {		
				if (fs.equalsIgnoreCase(tb.getFname()) ) {
						int getIndex = toprow.indexOf(tb.getDepartment().toString());
						c.remove(getIndex);
						c.add(getIndex,tb.getTotalSum().toString());
						finallist.add(c);		
				}
			 }  	
		}
		
		ArrayList<String> com = new ArrayList<String>();
		ArrayList<ArrayList<String>> ultraFinal = new ArrayList<ArrayList<String>>(); 
		for(ArrayList<String> a : finallist) {
			String fin = a.get(0);
			if(!com.contains(fin)) {
				ultraFinal.add(a);
			}
			com.add(fin);
		}
		//Side Summation Code
		int z=0;
		for(ArrayList<String> a : ultraFinal) {
			if(z>0) {
				//System.out.println(a.size());
				BigDecimal sum = new BigDecimal(0.0);
				for(int i=1;i<a.size();i++) {
					if(a.get(i)!=null && a.get(i)!="" && !a.get(i).isEmpty()  && !check.contains(a.get(0))) {
						sum = sum.add(new BigDecimal(Double.parseDouble((a.get(i)))));
					}
				}
				a.add(ultraFinal.get(0).indexOf("Total"),sum.toString());
			}
			z++;
		}
		
		//Bottom Total
		Integer size = finallist.get(0).size();
		for(int j=1;j<size;j++)
		 {
			 BigDecimal sum = new BigDecimal(0.0);
			 int r = 0;
			 check=new HashSet<String>();
			 for(ArrayList<String> bf : finallist) 
			 {
				 if(r>0) {
					 	if(bf.get(j)!=null && bf.get(j)!="" && !bf.get(j).isEmpty() && !check.contains(bf.get(0)) ) {
							sum = sum.add(new BigDecimal( Double.parseDouble((bf.get(j)))));
						} 
				 }
				 check.add(bf.get(0));
				r++;
			 }
			 bottomrow.remove(j);
			 bottomrow.add(j,sum.toString());
		 }
		ultraFinal.add(bottomrow);
		return ultraFinal;
	}
	
	
	
	

	public List<TrialBalanceBean> Search(CapitalRevenueRequestPojo capital, ReportBean rb ) {
		List<TrialBalanceBean> tb = new ArrayList<TrialBalanceBean>();
		if(capital.getDepartment()!=null) {
			rb.setDepartmentCode(capital.getDepartment());
		}
		if(capital.getFunction()!=null) {
			rb.setFunctionId(Integer.parseInt(capital.getFunction()));
		}
		rb.setReportType("daterange");
		
		if(capital.getFund()==null || capital.getFund().isEmpty()) {
			capital.setFund("1");
		}
		rb.setFundId(Integer.parseInt(capital.getFund()));	
		if (rb.getReportType().equalsIgnoreCase("daterange")) {
			String sDate = capital.getFrom_date();
			String eDate = capital.getTo_date();
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
			rb.setFromDate(dt);
			rb.setToDate(dd);
			if (endFormat.compareTo(endDate1) > 0) {
				System.out.println("Start Date and End Date should be in same financial year");
				return finalList;
				
			}
		}
		try {
			final List<AppConfigValues> configValues = appConfigValuesService.getConfigValuesByModuleAndKey(
					FinancialConstants.MODULE_NAME_APPCONFIG,
					FinancialConstants.REMOVE_ENTRIES_WITH_ZERO_AMOUNT_IN_REPORT);

			for (final AppConfigValues appConfigVal : configValues)
				removeEntrysWithZeroAmount = appConfigVal.getValue();
		} catch (final Exception e) {
			throw new ApplicationRuntimeException(
					"Appconfig value for remove entries with zero amount in report is not defined in the system");
		}
		
			tb = getReportForDateRange(rb);
			List<TrialBalanceBean> retdata = new ArrayList<TrialBalanceBean>();
			retdata = formatTBReport(tb);
			
			//System.out.println(retdata.size());
			//for(TrialBalanceBean a:retdata) {
			//	if(a.getDepartmentcode().equals("429")) {
			//		System.out.println("glcode-->"+a.getAccCode()+"  credit -->"+a.getCreditAmount()+"  debit --> "+a.getDebit()+"depcode -->"+a.getDepartmentcode());
			//	}
		//	}
		
		return retdata;

	
	}
	private List<TrialBalanceBean> removeEntrysWithZeroAmount(final List<TrialBalanceBean> taBean) {
		List<TrialBalanceBean>tb = new ArrayList<TrialBalanceBean>();
		for (final TrialBalanceBean trailBalance : taBean)
			if (!(trailBalance.getOpeningBal().equalsIgnoreCase("0.00")
					&& trailBalance.getCredit().equalsIgnoreCase("0.00")
					&& trailBalance.getDebit().equalsIgnoreCase("0.00")
					&& trailBalance.getClosingBal().equalsIgnoreCase("0.00")))
				tb.add(trailBalance);
		return tb;
	}
	
	private List<TrialBalanceBean> getReportForDateRange( ReportBean rb) {
		List<TrialBalanceBean> bl = new ArrayList<TrialBalanceBean>();

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Starting getTBReport | Getting result for Date Range");
		String voucherMisTable = "";
		String misClause = "";
		String misDeptCond = "";
		String tsDeptCond = "";
		String functionaryCond = "";
		String tsfunctionaryCond = "";
		String functionIdCond = "";
		String tsFunctionIdCond = "";
		String tsdivisionIdCond = "";
		String misdivisionIdCond = "";
		String misSchemeCond="";
		String missubDCond="";
		String deptquerytable = ", eg_department ed";
		String functiontable=", function f";
		/*if ((null != rb.getDepartmentCode() && !rb.getDepartmentCode().isEmpty()) || null != rb.getFunctionaryId() || null != rb.getDivisionId() || null != rb.getSubdivision()
				|| null != rb.getDivisionId() || (schemeId != null && !schemeId.isEmpty())) {*/
			voucherMisTable = ",vouchermis mis ";
			misClause = " and mis.voucherheaderid=vh.id ";
			String deptqureyclause = "and mis.departmentcode=ed.code ";
			String functionquery=" and gl.functionid=f.id ";
	//	}

		if (null != rb.getDepartmentCode() && !rb.getDepartmentCode().isEmpty()) {
			misDeptCond = " and mis.DepartmentCode= :departmentCode";
			tsDeptCond = " and ts.DepartmentCode= :departmentCode";
		}
		if(schemeId != null && !schemeId.isEmpty())
		{
			misSchemeCond=" and mis.schemeid= :scheme ";
		}
		if (null != rb.getSubdivision() && !rb.getSubdivision().isEmpty()) {
			missubDCond = " and mis.subdivision= :subdivision";
		}
		if (null != rb.getFunctionaryId()) {
			functionaryCond = " and mis.FunctionaryId= :functionaryId";
			tsfunctionaryCond = " and ts.FunctionaryId= :functionaryId";
		}
		if (null != rb.getFunctionId()) {
			functionIdCond = " and gl.functionid =:functionId";
			tsFunctionIdCond = " and ts.FUNCTIONID= :functionId";
		}
		if (null != rb.getDivisionId()) {
			misdivisionIdCond = " and mis.divisionId= :divisionId";
			tsdivisionIdCond = " and ts.divisionId= :divisionId";
		}
		String defaultStatusExclude = null;
		final List<AppConfigValues> listAppConfVal = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
				"statusexcludeReport");
		if (null != listAppConfVal)
			defaultStatusExclude = listAppConfVal.get(0).getValue();
		else
			throw new ApplicationRuntimeException("Exlcude statusses not  are not defined for Reports");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("get Opening balance for all account codes");
		// get Opening balance for all account codes
		final String openingBalanceStr = "SELECT coa.glcode AS accCode ,coa.name  AS accName, ed.name as departmentname, ed.code as departmentcode,f.code as functioncode,f.name as functionname, "
				+ " SUM(ts.openingcreditbalance) as creditOPB,"
				+ "sum(ts.openingdebitbalance) as debitOPB"
				+ " FROM transactionsummary ts,chartofaccounts coa,financialyear fy, eg_department ed, function f "
				+ " WHERE ts.glcodeid=coa.id  AND ts.financialyearid=fy.id and ts.departmentcode = ed.code and ts.functionid =f.id  and ts.FundId=:fundId " + tsDeptCond
				+ tsfunctionaryCond + tsFunctionIdCond + tsdivisionIdCond
				+ " AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate "
				+ " GROUP BY ts.glcodeid,coa.glcode,coa.name,ed.name,ed.code,f.code,f.name ORDER BY coa.glcode ASC";
		//if (LOGGER.isDebugEnabled())
			LOGGER.debug("First Query Str" + openingBalanceStr);
		final org.hibernate.Query openingBalanceQry = persistenceService.getSession().createSQLQuery(openingBalanceStr)
				.addScalar("accCode").addScalar("accName").addScalar("departmentname").addScalar("departmentcode").addScalar("functioncode").addScalar("functionname").addScalar("creditOPB", BigDecimalType.INSTANCE)
				.addScalar("debitOPB", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(TrialBalanceBean.class));

		openingBalanceQry.setInteger("fundId", rb.getFundId());

		if (null != rb.getDepartmentCode() && !rb.getDepartmentCode().isEmpty())
			openingBalanceQry.setString("departmentCode", rb.getDepartmentCode());
		if (null != rb.getFunctionaryId())
			openingBalanceQry.setInteger("functionaryId", rb.getFunctionaryId());
		if (null != rb.getFunctionId())
			openingBalanceQry.setInteger("functionId", rb.getFunctionId());
		if (null != rb.getDivisionId())
			openingBalanceQry.setInteger("divisionId", rb.getDivisionId());
		openingBalanceQry.setDate("fromDate", rb.getFromDate());
		openingBalanceQry.setDate("toDate", rb.getToDate());
		final List<TrialBalanceBean> openingBalanceList = openingBalanceQry.list();
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Opening balance query ---->" + openingBalanceQry);

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("get Opening balance for all account codes reulted in " + openingBalanceList.size());

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("get till date balance for all account codes");
		// get till date balance for all account codes
		final String tillDateOPBStr = "SELECT coa.glcode AS accCode ,coa.name  AS accName, ed.name as departmentname, ed.code as departmentcode,f.code as functioncode,f.name as functionname,"
				+ "SUM(gl.creditAmount) as tillDateCreditOPB,sum(gl.debitAmount) as tillDateDebitOPB"
				+ " FROM generalledger  gl,chartofaccounts coa,financialyear fy,Voucherheader vh " + voucherMisTable+deptquerytable+functiontable
				+ " WHERE gl.glcodeid=coa.id and vh.id=gl.voucherheaderid  and vh.fundid=:fundId " + misClause+deptqureyclause+functionquery
				+ misDeptCond + functionaryCond + functionIdCond + misdivisionIdCond +misSchemeCond
				+ " AND vh.voucherdate>=fy.startingdate AND vh.voucherdate<=:fromDateMinus1 "
				+ " AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate" + " AND vh.status not in ("
				+ defaultStatusExclude + ")" + " GROUP BY gl.glcodeid,coa.glcode,coa.name,ed.name,ed.code,f.code,f.name ORDER BY coa.glcode ASC";
				System.out.println("SecondQuery-->"+tillDateOPBStr);
		final org.hibernate.Query tillDateOPBQry = persistenceService.getSession().createSQLQuery(tillDateOPBStr).addScalar("accCode")
				.addScalar("accName").addScalar("departmentname").addScalar("departmentcode").addScalar("functioncode").addScalar("functionname").addScalar("tillDateCreditOPB", BigDecimalType.INSTANCE)
				.addScalar("tillDateDebitOPB", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(TrialBalanceBean.class));
		tillDateOPBQry.setInteger("fundId", rb.getFundId());

		if (null != rb.getDepartmentCode() && !rb.getDepartmentCode().isEmpty())
			tillDateOPBQry.setString("departmentCode", rb.getDepartmentCode());
		if (null != rb.getFunctionaryId())
			tillDateOPBQry.setInteger("functionaryId", rb.getFunctionaryId());
		if (null != rb.getFunctionId())
			tillDateOPBQry.setInteger("functionId", rb.getFunctionId());
		if (null != rb.getDivisionId())
			tillDateOPBQry.setInteger("divisionId", rb.getDivisionId());
		if(schemeId != null && !schemeId.isEmpty())
		{
			tillDateOPBQry.setInteger("scheme", Integer.parseInt(schemeId));
		}
		tillDateOPBQry.setDate("fromDate", rb.getFromDate());
		// tillDateOPBQry.setDate("fromDate",rb.getFromDate());
		tillDateOPBQry.setDate("toDate", rb.getToDate());
		final Calendar cal = Calendar.getInstance();
		cal.setTime(rb.getFromDate());
		cal.add(Calendar.DATE, -1);
		tillDateOPBQry.setDate("fromDateMinus1", cal.getTime());
		final List<TrialBalanceBean> tillDateOPBList = tillDateOPBQry.list();
		
		//Till Date Changes
		
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("get till date balance for all account codes reulted in " + tillDateOPBList.size());
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("get current debit and credit sum for all account codes  ");
		// get current debit and credit sum for all account codes
		final String currentDebitCreditStr = "SELECT coa.glcode AS accCode ,coa.name  AS accName,ed.name as departmentname, ed.code as departmentcode,f.code as functioncode,f.name as functionname, "
				+ "SUM(gl.creditAmount) as creditAmount,sum(gl.debitAmount) as debitAmount"
				+ " FROM generalledger gl,chartofaccounts coa,financialyear fy,Voucherheader vh " + voucherMisTable+deptquerytable+functiontable
				+ " WHERE gl.glcodeid=coa.id and vh.id= gl.voucherheaderid AND  vh.fundid=:fundId " + misClause+deptqureyclause+functionquery
				+ misDeptCond + missubDCond + functionaryCond + functionIdCond + misdivisionIdCond + misSchemeCond
				+ " AND vh.voucherdate>=:fromDate AND vh.voucherdate<=:toDate "
				+ " AND fy.startingdate<=:fromDate AND fy.endingdate>=:toDate" + " AND vh.status not in ("
				+ defaultStatusExclude + ") " + " GROUP BY gl.glcodeid,coa.glcode,coa.name,ed.name,ed.code,f.code,f.name ORDER BY coa.glcode ASC";
		System.out.println("finaquery-->"+currentDebitCreditStr);
		
		final org.hibernate.Query currentDebitCreditQry = persistenceService.getSession().createSQLQuery(currentDebitCreditStr)
				.addScalar("accCode").addScalar("accName").addScalar("departmentname").addScalar("departmentcode").addScalar("functioncode").addScalar("functionname").addScalar("creditAmount", BigDecimalType.INSTANCE)
				.addScalar("debitAmount", BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(TrialBalanceBean.class));
		currentDebitCreditQry.setInteger("fundId", rb.getFundId());
		if (null != rb.getDepartmentCode() && !rb.getDepartmentCode().isEmpty())
			currentDebitCreditQry.setString("departmentCode", rb.getDepartmentCode());
		if (null != rb.getSubdivision() && !rb.getSubdivision().isEmpty())
			currentDebitCreditQry.setString("subdivision", rb.getSubdivision());
		if (null != rb.getFunctionaryId())
			currentDebitCreditQry.setInteger("functionaryId", rb.getFunctionaryId());
		if (null != rb.getFunctionId())
			currentDebitCreditQry.setInteger("functionId", rb.getFunctionId());
		if (null != rb.getDivisionId())
			currentDebitCreditQry.setInteger("divisionId", rb.getDivisionId());
		currentDebitCreditQry.setDate("fromDate", rb.getFromDate());
		currentDebitCreditQry.setDate("toDate", rb.getToDate());
		if(schemeId != null && !schemeId.isEmpty())
		{
			currentDebitCreditQry.setInteger("scheme", Integer.parseInt(schemeId));
		}
		final List<TrialBalanceBean> currentDebitCreditList = currentDebitCreditQry.list();
		if (LOGGER.isInfoEnabled())
			LOGGER.info("closing balance query ---->" + currentDebitCreditQry);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("get current debit and credit sum for all account codes resulted in   "
					+ currentDebitCreditList.size());
		final Map<String, TrialBalanceBean> tbMap = new LinkedHashMap<String, TrialBalanceBean>();
		totalClosingBalance = BigDecimal.ZERO;
		totalOpeningBalance = BigDecimal.ZERO;

		/**
		 * out of 3 list put one(openingBalanceList) into Linked hash map with
		 * accountcode as key So that if other two lists has entry for an
		 * account code it will be merged else new entry will added to map
		 * finally return the contents of the map as list
		 */
		if (!openingBalanceList.isEmpty())
			for (final TrialBalanceBean tb : openingBalanceList) {
				tb.setOpeningBalance(tb.getDebitOPB().subtract(tb.getCreditOPB()));
				tb.setClosingBalance(tb.getOpeningBalance());
				tbMap.put(tb.getAccCode(), tb);

			}
		for (final TrialBalanceBean tillDateTB : tillDateOPBList)
			if (null != tbMap.get(tillDateTB.getAccCode())) {
				final BigDecimal opb = tbMap.get(tillDateTB.getAccCode()).getOpeningBalance()
						.add(tillDateTB.getTillDateDebitOPB().subtract(tillDateTB.getTillDateCreditOPB()));
				tbMap.get(tillDateTB.getAccCode()).setOpeningBalance(opb);
				tbMap.get(tillDateTB.getAccCode()).setClosingBalance(opb);

			} else {
				tillDateTB.setOpeningBalance(
						tillDateTB.getTillDateDebitOPB().subtract(tillDateTB.getTillDateCreditOPB()));
				tillDateTB.setClosingBalance(tillDateTB.getOpeningBalance());
				tbMap.put(tillDateTB.getAccCode(), tillDateTB);
			}
		BigDecimal cb = BigDecimal.ZERO;
		for (final TrialBalanceBean currentAmounts : currentDebitCreditList)
			if (null != tbMap.get(currentAmounts.getAccCode())) {

				tbMap.get(currentAmounts.getAccCode()).setDebitAmount(currentAmounts.getDebitAmount());
				tbMap.get(currentAmounts.getAccCode()).setCreditAmount(currentAmounts.getCreditAmount());
				cb = tbMap.get(currentAmounts.getAccCode()).getOpeningBalance().add(currentAmounts.getDebitAmount())
						.subtract(currentAmounts.getCreditAmount());
				tbMap.get(currentAmounts.getAccCode()).setClosingBalance(cb);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("old amounts" + totalOpeningBalance + "    " + totalClosingBalance);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Current amounts" + tbMap.get(currentAmounts.getAccCode()).getOpeningBalance() + "    "
							+ cb);
				totalOpeningBalance = totalOpeningBalance
						.add(tbMap.get(currentAmounts.getAccCode()).getOpeningBalance());
				totalClosingBalance = totalClosingBalance.add(cb);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("After Amounts" + totalOpeningBalance + "    " + totalClosingBalance);
			} else {
				currentAmounts.setOpeningBalance(BigDecimal.ZERO);
				cb = currentAmounts.getOpeningBalance().add(currentAmounts.getDebitAmount())
						.subtract(currentAmounts.getCreditAmount());
				currentAmounts.setClosingBalance(cb);
				currentAmounts.setOpeningBalance(BigDecimal.ZERO);
				tbMap.put(currentAmounts.getAccCode(), currentAmounts);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("old getTBReport" + totalOpeningBalance + "    " + totalClosingBalance);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Current amounts" + tbMap.get(currentAmounts.getAccCode()).getOpeningBalance() + "    "
							+ cb);
				totalClosingBalance = totalClosingBalance.add(cb);
				totalOpeningBalance = totalOpeningBalance.add(currentAmounts.getOpeningBalance());
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("After getTBReport" + totalOpeningBalance + "    " + totalClosingBalance);

			}
		bl.addAll(tbMap.values());
		/*
		 * for(TrialBalanceBean c:al) { if(LOGGER.isInfoEnabled()) LOGGER.info(
		 * "Items Before Sorting"+c); }
		 */
		Collections.sort(bl, new COAcomparator());

		/*
		 * for(TrialBalanceBean c:al) { if(LOGGER.isInfoEnabled()) LOGGER.info(
		 * "Items After Sorting"+c); }
		 */
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Exiting getTBReport" + totalOpeningBalance + "    " + totalClosingBalance);
		return bl;
	}
	
	private List<TrialBalanceBean>  formatTBReport(List<TrialBalanceBean> al) {

		for (final TrialBalanceBean tb : al) {
			if (null == tb.getOpeningBalance()) {
				tb.setOpeningBal("0.00");
				tb.setOpeningBalance(BigDecimal.ZERO);
			} else if (tb.getOpeningBalance().compareTo(BigDecimal.ZERO) > 0)
				tb.setOpeningBal(numberToString(tb.getOpeningBalance().toString()).toString() + " Dr");
			else if (tb.getOpeningBalance().compareTo(BigDecimal.ZERO) < 0)
				tb.setOpeningBal(
						numberToString(tb.getOpeningBalance().multiply(new BigDecimal(-1)).toString()).toString()
								+ " Cr");
			else
				tb.setOpeningBal(numberToString(tb.getOpeningBalance().toString()).toString());
			if (null == tb.getClosingBalance()) {
				tb.setClosingBal("0.00");
				tb.setClosingBalance(BigDecimal.ZERO);
			}

			else if (tb.getClosingBalance().compareTo(BigDecimal.ZERO) > 0)
				tb.setClosingBal(numberToString(tb.getClosingBalance().toString()).toString() + " Dr");
			else if (tb.getClosingBalance().compareTo(BigDecimal.ZERO) < 0)
				tb.setClosingBal(
						numberToString(tb.getClosingBalance().multiply(new BigDecimal(-1)).toString()).toString()
								+ " Cr");
			else
				tb.setClosingBal(tb.getClosingBalance().setScale(2).toString());
			if (tb.getDebitAmount() != null)
				tb.setDebit(numberToString(tb.getDebitAmount().toString()).toString());
			else {
				tb.setDebit("0.00");
				tb.setDebitAmount(BigDecimal.ZERO);
			}
			if (tb.getCreditAmount() != null)
				tb.setCredit(numberToString(tb.getCreditAmount().toString()).toString());
			else {
				tb.setCredit("0.00");
				tb.setCreditAmount(BigDecimal.ZERO);
			}
			totalDebitAmount = totalDebitAmount.add(tb.getDebitAmount());
			totalCreditAmount = totalCreditAmount.add(tb.getCreditAmount());

		}

		final TrialBalanceBean tb = new TrialBalanceBean();

		tb.setAccCode("   Total  ");
		tb.setAccName("");
		if (totalOpeningBalance.compareTo(BigDecimal.ZERO) > 0)
			tb.setOpeningBal(numberToString(totalOpeningBalance.toString()).toString() + " Dr");
		else if (totalOpeningBalance.compareTo(BigDecimal.ZERO) < 0) {
			totalOpeningBalance = totalOpeningBalance.abs();
			tb.setOpeningBal(numberToString(totalOpeningBalance.toString()).toString() + " Cr");
		} else
			tb.setOpeningBal("0.00");
		if (totalClosingBalance.compareTo(BigDecimal.ZERO) > 0)
			tb.setClosingBal(numberToString(totalClosingBalance.toString()).toString() + " Dr");
		else if (totalClosingBalance.compareTo(BigDecimal.ZERO) < 0) {
			totalClosingBalance = totalClosingBalance.abs();
			tb.setClosingBal(numberToString(totalClosingBalance.abs().toString()).toString() + " Cr");
		} else
			tb.setClosingBal("0.00");
		tb.setDebit(numberToString(totalDebitAmount.toString()).toString());
		tb.setCredit(numberToString(totalCreditAmount.toString()).toString());
		al.add(tb);
		
		//System.out.println("Size ----->>>"+al.size());
		if (removeEntrysWithZeroAmount.equalsIgnoreCase("Yes"))
			return removeEntrysWithZeroAmount(al);
		else
			return al;
	}
	
	
	public static StringBuffer numberToString(final String strNumberToConvert) {
		String strNumber = "", signBit = "";
		if (strNumberToConvert.startsWith("-")) {
			strNumber = "" + strNumberToConvert.substring(1, strNumberToConvert.length());
			signBit = "-";
		} else
			strNumber = "" + strNumberToConvert;
		final DecimalFormat dft = new DecimalFormat("##############0.00");
		final String strtemp = "" + dft.format(Double.parseDouble(strNumber));
		StringBuffer strbNumber = new StringBuffer(strtemp);
		final int intLen = strbNumber.length();

		for (int i = intLen - 6; i > 0; i = i - 2)
			strbNumber.insert(i, ',');
		if (signBit.equals("-"))
			strbNumber = strbNumber.insert(0, "-");
		return strbNumber;
	}


public List<TrialBalanceBean> getdivisionDataCheck(CapitalRevenueRequestPojo capital) {
		
		DivisionReportPOJO d1 = new DivisionReportPOJO();
		List<DivisionReportPOJO> datalist  = d1.getDataList();
		String startwithForglocode="1";
		ReportBean rb = new ReportBean();
		String sDate = capital.getFrom_date();
		String eDate = capital.getTo_date();
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
		rb.setFromDate(dt);
		rb.setToDate(dd);
			//startwithForaccCode="000";
			List<TrialBalanceBean> fetchlist= Search(capital,rb);
			List <TrialBalanceBean>cleanList = new ArrayList<TrialBalanceBean>();
			for(TrialBalanceBean a:fetchlist) {
				TrialBalanceBean tb = new TrialBalanceBean();
				if(a.getAccCode().startsWith(startwithForglocode)) {
					tb=a;
					cleanList.add(tb);
				}		
			}
			
		return cleanList;
		
	}
	
	
public List<ReceiptReportPOJO> getReceiptReportList(StringBuilder qs) {
	
	
	//System.out.println("Inside dedicated");
	StringBuilder query = new StringBuilder(" ");
	String department = "";
	query.append("select id as id,vouchernumber as vouchernumber ,voucherdate as voucherdate ,department as department,"
			+ "glcode as glcode ,creditamount as creditamount,debitamount as debitamount,"
			+ " deptcode as deptcode, fcode as fcode, fname as fname from exp_ded_view3 ");
	query.append(" where ");
	query.append(qs);
	query.append(" order by glcode ASC ");
	
	
	Query q =entityManager.createNativeQuery(query.toString());
	List<ReceiptReportPOJO> data = new ArrayList<ReceiptReportPOJO>();
	System.out.println("Query Printing---"+query.toString());
	try {
		List<Object[]> list = q.getResultList();
		System.out.println(list.size());
		
		
		if(list.size()>0) {
			for (final Object[] o : list)
    		{
				ReceiptReportPOJO a = new ReceiptReportPOJO();
				
				a.setId(o[0].toString());
				a.setVouchernumber(o[1].toString());
				a.setVoucherdate(o[2].toString());
				a.setDepartment(o[3].toString());
				a.setGlcode(o[4].toString());
				a.setCreditamount((null!=o[5])? new BigDecimal(o[5].toString()):new BigDecimal(0));
				a.setDebitamount((null!=o[6])? new BigDecimal(o[6].toString()):new BigDecimal(0));
				a.setDeptcode((null!=o[7])?o[7].toString():null);
				a.setFcode((null!=o[8])?o[8].toString():null);
				a.setFname((null!=o[9])?o[9].toString():null);
				
				//System.out.println(o[0].toString()+" "+o[1].toString()+"  "+o[2].toString()+" "+o[3].toString()+" "+o[4].toString()+" "+o[5].toString());
				data.add(a);
    		}
		}
		
		
	System.out.println(list.size());
	}catch(Exception ex) {
		ex.printStackTrace();
		
	}
	
	return data;
}

	public List<DivisionReportPOJO> getdivisionData(CapitalRevenueRequestPojo capital) {
		DivisionReportPOJO d1 = new DivisionReportPOJO();
		DivisionReportPOJO d2DivisionReportPOJO = new DivisionReportPOJO("Division", false, false, null, null,null);
		List<DivisionReportPOJO> datalist= new ArrayList<DivisionReportPOJO>();
		List<DivisionReportPOJO> datalistFinal= new ArrayList<DivisionReportPOJO>();
		//datalistFinal.add(d2DivisionReportPOJO);
		datalist.addAll(d1.getDataList());
		StringBuilder queryString= new StringBuilder("");
		queryString.append(" voucherdate >= TO_DATE('"+ capital.getFrom_date()+"','dd/mm/yyyy') and voucherdate <= TO_DATE('"+capital.getTo_date()+"','dd/mm/yyyy')" );
		String startwithForglocode="1";
		List<ReceiptReportPOJO> fetchlist =  getReceiptReportList(queryString);
		List <ReceiptReportPOJO>data = new ArrayList<ReceiptReportPOJO>();
		for(ReceiptReportPOJO a:fetchlist) {
			
			ReceiptReportPOJO tb = new ReceiptReportPOJO();
			if(a.getGlcode().startsWith(startwithForglocode)) {
				tb=a;
				data.add(tb);
			}		
		}
		
			Set<String> excludingglcodefordep  = d1.getExlcudingGlcodeForDepartments();
			BigDecimal finalSummation = new BigDecimal(0);
			
			for(DivisionReportPOJO d:datalist) {
				;
				BigDecimal currentsum = new BigDecimal(0);
				BigDecimal total_credit =new BigDecimal(0);
				BigDecimal total_debit =new BigDecimal(0);
				if(!d.isIsglcode()&& d.isIsdep()) {
					
					for(ReceiptReportPOJO a : data) {
						
						if(a.getDeptcode().equals(d.getDepcode()) && !excludingglcodefordep.contains(a.getGlcode())) {
							
							total_credit = total_credit.add(a.getCreditamount());
							total_debit = total_debit.add(a.getDebitamount());
						}
						
					}
					
				}
				
				if(d.isIsglcode()&&! d.isIsdep()) {
					for(String s: d.getGlcodeused())
					for(ReceiptReportPOJO a : data) {
						if(s.equalsIgnoreCase(a.getGlcode())) {
							total_credit = total_credit.add(a.getCreditamount());
							total_debit = total_debit.add(a.getDebitamount());
							
							
						}
					}
					
				}
				currentsum = total_credit.subtract(total_debit);
				finalSummation = finalSummation.add(currentsum);
				d.setAmount(currentsum);
				
			}
			datalistFinal.addAll(datalist);
			DivisionReportPOJO d3DivisionReportPOJO = new DivisionReportPOJO("Total", false, false, null, finalSummation,null);
			
			datalistFinal.add(d3DivisionReportPOJO);
			
			return datalistFinal;
		
	}
	
	
	public List<CapitalRevenueDataPOJO> capitalRevenuDataFromView(StringBuilder qs) {
		
		
		//System.out.println("Inside dedicated");
		StringBuilder query = new StringBuilder(" ");
		query.append("select id as id,vouchernumber as vouchernumber ,voucherdate as voucherdate ,department as department,"
				+ "glcode as glcode ,creditamount as creditamount,debitamount as debitamount,"
				+ " deptcode as deptcode, fcode as fcode, fname as fname, fundid as fundid from capitalrevenue ");
		query.append(" where ");
		query.append(qs);
		query.append(" order by glcode ASC ");
		
		
		Query q =entityManager.createNativeQuery(query.toString());
		List<CapitalRevenueDataPOJO> data = new ArrayList<CapitalRevenueDataPOJO>();
		System.out.println("Query Printing---"+query.toString());
		try {
			List<Object[]> list = q.getResultList();
			System.out.println(list.size());
			
			
			if(list.size()>0) {
				for (final Object[] o : list)
	    		{
					CapitalRevenueDataPOJO a = new CapitalRevenueDataPOJO();
					
					a.setId(o[0].toString());
					a.setVouchernumber(o[1].toString());
					a.setVoucherdate(o[2].toString());
					a.setDepartment(o[3].toString());
					a.setGlcode(o[4].toString());
					a.setCreditamount((null!=o[5])? new BigDecimal(o[5].toString()):new BigDecimal(0));
					a.setDebitamount((null!=o[6])? new BigDecimal(o[6].toString()):new BigDecimal(0));
					a.setDeptcode((null!=o[7])?o[7].toString():null);
					a.setFcode((null!=o[8])?o[8].toString():null);
					a.setFname((null!=o[9])?o[9].toString():null);
					a.setFundid((null!=o[10])?Long.parseLong(o[10].toString()):null);	
					//System.out.println(o[0].toString()+" "+o[1].toString()+"  "+o[2].toString()+" "+o[3].toString()+" "+o[4].toString()+" "+o[5].toString());
					data.add(a);
	    		}
			}
			
			
		System.out.println(list.size());
		}catch(Exception ex) {
			ex.printStackTrace();
			
		}
		
		return data;
	}
	
	public byte[] getDivisionExl(Map<String,String>headerData,List<DivisionReportPOJO> exp) {

		System.out.println("Dedicated");
		
		prepare();
		byte[]fileContent=null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Reciept Report");
			HSSFCellStyle style = wb.createCellStyle();  
			int i =0;
			Row row1 = sheet.createRow(i++);	  
			Cell c1=  row1.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(headerData.get("h1"));
			
			Row row2 = sheet.createRow(i++);	  
			Cell c2=  row2.createCell(0);
			c2.setCellStyle(style);
			c2.setCellValue("Division");
			
			Cell c3=  row2.createCell(1);
			c3.setCellStyle(style);
			c3.setCellValue("Amount");
			
			for(DivisionReportPOJO s : exp) {
				
				 	Row row = sheet.createRow(i++);
				 	Cell cell0 = row.createCell(0);
					Cell cell1 = row.createCell(1);
					
					
					cell0.setCellValue(s.getName().toString());
					cell1.setCellValue((null!=s.getAmount())?s.getAmount().toString():"");
							
		        } 
			
			int numberOfSheets = wb.getNumberOfSheets();
			 for (int x = 0; x < numberOfSheets; x++) {
			        Sheet sheet1 = wb.getSheetAt(x);
			        int total_row=sheet1.getLastRowNum();
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
	

	public void setRemoveEntrysWithZeroAmount(final String removeEntrysWithZeroAmount) {
		this.removeEntrysWithZeroAmount = removeEntrysWithZeroAmount;
	}

	public AppConfigValueService getAppConfigValuesService() {
		return appConfigValuesService;
	}

	public void setAppConfigValuesService(AppConfigValueService appConfigValuesService) {
		this.appConfigValuesService = appConfigValuesService;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setFinancialYearDAO(final FinancialYearDAO financialYearDAO) {
		this.financialYearDAO = financialYearDAO;
	}
	
}
