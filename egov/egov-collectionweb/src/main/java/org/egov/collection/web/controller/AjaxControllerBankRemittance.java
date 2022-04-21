package org.egov.collection.web.controller;

												  
										
											 
												  
												  
									   
										 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.collection.constants.CollectionConstants;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.RemitancePOJO;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.expenditurePex.ExpenditurePex;
import org.egov.model.recoveries.Recovery;
import org.hibernate.SQLQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping(value = "/remittanceBankdetail/")
public class AjaxControllerBankRemittance {
	
	public static final Integer DEFAULT_PAGE_SIZE = 2;

	private Integer pageSize;

	@Autowired
    protected transient PersistenceService persistenceService;

	@Autowired
	private AppConfigValueService appConfigValuesService;
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd/MMM/yyyy", LOCALE);
	
	@RequestMapping(value = "gldetails")
	  @ResponseBody public List<RemitancePOJO> getTags(@RequestParam("receiptNo")
	  String receiptNo) { 
		 System.out.println(":::::::::Receipt No:::: "+receiptNo);
		 List<RemitancePOJO> details=new ArrayList<>();
		 SQLQuery query =  null;
	    	List<Object[]> rows = null;
	    	 RemitancePOJO r  = null;
	    	
		  try { 
		  query = this.persistenceService.getSession().
				  createSQLQuery("select (select c2.name from chartofaccounts c2 where c2.glcode=gl.glcode),gl.glcode,gl.creditamount from generalledger gl where voucherheaderid =(select vmis.voucherheaderid from vouchermis vmis where vmis.reciept_number =:receipt_no) ) and gl.creditamount >0"); 
		  query.setString("receipt_no", receiptNo); 
		  query =this.persistenceService.getSession().
				  createSQLQuery("select c2.name,gl.glcode,sum(gl.creditamount) from chartofaccounts c2,generalledger gl,vouchermis vmis "
						  +" where c2.glcode = gl.glcode and gl.voucherheaderid =vmis.voucherheaderid and vmis.reciept_number in ('"
						  +receiptNo+"') and gl.creditamount >0 group by c2.name,gl.glcode ");
	    	    
	    	    System.out.println("::::::>>>>>"+query);
	    	    rows = query.list();
	    	    System.out.println("row size "+rows.size());
							  
	  if(rows.size()!=0) 
	  {
	    		   for(Object[] e : rows)
	    	    	{
		  
	    			   r = new RemitancePOJO();
	    			   r.setGlName((null!=e[0]?e[0].toString():null));
	    			   r.setGlcode((null!=e[1]?e[1].toString():null));
	    			   r.setAmount((null!=e[2]?e[2].toString():null));
	    			   details.add(r);
	    	    	}
	    	   }
	    	   return details;  
	  }
	  catch (Exception e) { 
				e.printStackTrace();
			}
		String n="Controller from ajax";
	       return details;
	    }
	
	@RequestMapping(value = "getdata")
	@ResponseBody
	public List<ExpenditurePex> getData(@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate) {
		List details= new ArrayList<>();
		List<ExpenditurePex> detailList=new ArrayList();
		SQLQuery query = null;
		List<Object[]> rows = null;
		List<Object[]> rows1 = null;
		System.out.println("fromDate:::: "+fromDate+" toDate:::: "+toDate);
		ExpenditurePex r = null;
		Map<String,List<ExpenditurePex> > result =new HashMap();
			Map<String,String> map1 =new HashMap();
			Map<String,String> partyMap =new HashMap();
			Map<String,List<String>> dedPexMap=new HashMap();
			try {
		//tds
		List<Object[]> list= null;
	  	SQLQuery querytds =  null;
	   	final StringBuffer query10 = new StringBuffer(500);
	  	query10
	      .append("select ch.glcode,t.id from tds t, chartofaccounts ch where t.glcodeid = ch.id and isactive =true");
	  	querytds=this.persistenceService.getSession().createSQLQuery(query10.toString());
	   	list = querytds.list();
	  	List<String> tds=new ArrayList<String>();
	  	if (list.size() != 0) {
	  		for (final Object[] object : list)
	  		{
	  			tds.add(object[0].toString());
	  		}
	  	}
			  //for partyname
			  	SQLQuery queryparty =  null;
			  	list.clear();
			  	final StringBuffer query2 = new StringBuffer(500);
			  	query2
			      .append("select distinct vdp.id,vdp.detailname from voucher_detail_party vdp ");
			  	queryparty=this.persistenceService.getSession().createSQLQuery(query2.toString());
			   	list = queryparty.list();
			  	if (list.size() != 0) {
			  		for (final Object[] object : list)
			  		{
			  			partyMap.put(object[0].toString(),object[1].toString());
			  		}
			  	}
			  	
			  	//for deduction pex
			/*
			 * SQLQuery querydedpex = null; list.clear(); final StringBuffer querypex = new
			 * StringBuffer(500); querypex
			 * .append("select distinct v1.id,ei.transactionnumber as pex,to_char(ei.transactiondate, 'dd-Mon-yyyy') as pexdate, "
			 * +
			 * " ei.instrumentnumber as cheque,to_char(ei.instrumentdate , 'dd-Mon-yyyy') as chequedate "
			 * +
			 * " from voucherheader v1,generalledger g2,miscbilldetail m,egf_instrumentheader ei,egf_instrumentvoucher ei2 "
			 * +
			 * " where g2.voucherheaderid = v1.id and ei2.voucherheaderid =v1.id and ei2.instrumentheaderid = ei.id "
			 * + " and ei2.voucherheaderid = m.payvhid order by v1.id");
			 * querydedpex=this.persistenceService.getSession().createSQLQuery(querypex.
			 * toString()); list = querydedpex.list(); List dedPex=new ArrayList(); if
			 * (list.size() != 0) { for (final Object[] e : list) { dedPex.add((null != e[1]
			 * ? e[1].toString() : "")); dedPex.add((null != e[2] ? e[2].toString() : ""));
			 * dedPex.add((null != e[3] ? e[3].toString() : "")); dedPex.add((null != e[4] ?
			 * e[4].toString() : "")); dedPexMap.put(e[0].toString(),dedPex); } }
			 */
			
				String voucherid="";
				String glcode="";
				String conString="";
				SQLQuery query1 = null;
				query1 = this.persistenceService.getSession().createSQLQuery(
						"select distinct vouchernumber,g2.glcode,dvm.vh_id from voucherheader v1,generalledger g2,deduc_voucher_mpng dvm where g2.voucherheaderid = v1.id and v1.id = dvm.ph_id");
				rows1 = query1.list();
				if (rows1.size() != 0) {
					for (Object[] e1 : rows1) {
							voucherid=e1[2].toString();
							glcode=e1[1].toString();
							conString=voucherid+"#"+glcode;
							map1.put(conString,e1[0].toString());
					}
				}
				query = this.persistenceService.getSession().createSQLQuery("select vh.vouchernumber as vouchernumber, " + 
				" to_char(voucherdate, 'dd-Mon-yyyy')as voucherdate,vh.description as partyName,(select vouchernumber from voucherheader where id = ei2.voucherheaderid) as bvpno," + 
				" ei.transactionnumber as pex, to_char(ei.transactiondate, 'dd-Mon-yyyy') as pexdate,concat(vh.name, '-', vh.type) as vouchertype," + 
				" f.name as functionName,vh.description as naration,c2.name as glcode,g2.glcodeid as glcodeid ,debitamount as debitamount,g2.creditamount as creditamount,g2.glcode as code,vh.id as id " + 
				" from voucherheader vh,vouchermis v,function f,egf_instrumentheader ei,egf_instrumentvoucher ei2,miscbilldetail m,chartofaccounts c2,generalledger g2 " + 
				" where f.id = v.functionid and c2.id = g2.glcodeid and v.voucherheaderid = m.billvhid and ei2.instrumentheaderid = ei.id " + 
				" and vh.id = m.billvhid and ei2.voucherheaderid = m.payvhid and m.billvhid = g2.voucherheaderid " + 
				" and ei.transactiondate >= to_date('"+fromDate+"','dd/mm/yyyy') and ei.transactiondate <= to_date('"+toDate+"','dd/mm/yyyy') " + 
				" and ei.id_status not in (1) order by ei.transactiondate asc,vh.vouchernumber,debitamount desc,creditamount desc") ;

			System.out.println("::::::>>>>>" + query);
			rows = query.list();

			System.out.println("row size " + rows.size());
			List finalList=new ArrayList();
			String vid="";
			if (rows.size() != 0) {
				for (Object[] e : rows) {
					r = new ExpenditurePex();
					if(result.get(e[4].toString())==null) {
					r.setPex((null != e[4] ? e[4].toString() : null));
					r.setPexDate((null != e[5] ? e[5].toString() : null));
					r.setBvp((null != e[3] ? e[3].toString() : null));
					r.setBvpDate((null != e[1] ? e[1].toString() : null));
					r.setvNo((null != e[0] ? e[0].toString() : null));
					r.setvDate((null != e[1] ? e[1].toString() : null));
					r.setVoucherType((null != e[6] ? e[6].toString() : null));
							if(partyMap.containsKey(e[14].toString()))
							{
								r.setPartyName(partyMap.get(e[14].toString()));
							}
							else
							{
								r.setPartyName("");
							}
							r.setBudgetHead((null != e[7] ? e[7].toString() : ""));
							r.setNarration((null != e[8] ? e[8].toString() : ""));
							r.setParticulars((null != e[9] ? e[9].toString() : ""));
							//vid=e[14].toString();
					r.setDebitamt((null != e[11] ? e[11].toString() : null));
					r.setCreditamt((null != e[12] ? e[12].toString() : null));
							r.setGlcodeId((null != e[13] ? e[13].toString() : null));
							//if(r.getBvp().contains("CJV")||r.getBvp().contains("EJV")||r.getBvp().contains("PJV"))
							//{}
							//else {
					details.add(r);
					result.put(r.getPex(),details);
							//}
						}
						else
						{
							//code form dedpexmap
							if(dedPexMap.containsKey(e[14].toString()))
							{
								String transactionno="";
								String transactiondate="";
								String instrumentno="";
								String instrumentdate="";
								List<String> dedPexList=dedPexMap.get(e[14].toString());
								for(int i=0;i<dedPexList.size();i+=3) {
									transactionno=dedPexList.get(i);
									transactiondate=dedPexList.get(i+1);
									instrumentno=dedPexList.get(i+2);
									instrumentdate=dedPexList.get(i+3);
									if(transactionno=="")
									{
										r.setPex(instrumentno);
										r.setPexDate(instrumentdate);
					}
					else
					{
										r.setPex(transactionno);
										r.setPexDate(transactiondate);
									}
								}
							}
						r.setBvp("");
						r.setBvpDate("");
						r.setvNo("");
						r.setvDate("");
						r.setVoucherType("");
						r.setPartyName("");
						r.setBudgetHead("");
						r.setNarration("");
						r.setParticulars((null != e[9] ? e[9].toString() : null));
							///code from map
							voucherid=e[14].toString();
							glcode=e[13].toString();
							conString=voucherid+"#"+glcode;
							if(map1.containsKey(conString))
							{
								r.setBvp(map1.get(conString));
						}
							////
						r.setDebitamt((null != e[11] ? e[11].toString() : null));
						r.setCreditamt((null != e[12] ? e[12].toString() : null));
							r.setGlcodeId((null != e[13] ? e[13].toString() : null));
							if(r.getBvp().contains("CJV")||r.getBvp().contains("EJV")||r.getBvp().contains("PJV"))
							{}
							else {
						details.add(r);
						result.put(r.getPex(),details);
		   
		   
					}
				}
			}
				}
			for(Map.Entry<String, List<ExpenditurePex>>	a:result.entrySet()) {
				detailList.addAll(a.getValue());
			}
		return details;
		} catch (Exception e) {
			e.printStackTrace();
		}
								  
	   
		return detailList;
	}
 
 	@SuppressWarnings("deprecation")
	@RequestMapping(value = "exportXLS")
    public @ResponseBody ResponseEntity<InputStreamResource> exportXlsPexEXp(@RequestParam("fromDate") String fromDate,@RequestParam("toDate") String toDate){
    	System.out.println("XLS 1");
    	
    	String[] COLUMNS = { "SlNo", " Pex Date", "Pex/Cheque no.", "Bpv No",
					"Bpv Date", "Voucher NO.", "Voucher Date", "Voucher Type","Party Name","Budget Head","Narration","GlCode","Account Number","Particulars"," Debit Amount(Rs.)"," Credit Amount(Rs.)"};
		
		List details= new ArrayList<>();
		List<ExpenditurePex> detailList=new ArrayList();
		List<ExpenditurePex> detailExp=new ArrayList();
		ByteArrayInputStream in = null;
		SQLQuery query = null;
		List<Object[]> rows = null;
		List<Object[]> rows1 = null;
		System.out.println("fromDate:::: "+fromDate+" toDate:::: "+toDate);
		ExpenditurePex r = null;
		Map<String,List<ExpenditurePex> > result =new HashMap();
		Map<String,String > map1 =new HashMap();
		Map<String,String> partyMap=new HashMap();
		Map<String,String> accMap =new HashMap();
		Map<String,List<String>> dedPexMap=new HashMap();
		try {
			//tds
			List<Object[]> list= null;
		  	SQLQuery querytds =  null;
		   	final StringBuffer query10 = new StringBuffer(500);
		  	query10
		      .append("select ch.glcode,t.id from tds t, chartofaccounts ch where t.glcodeid = ch.id and isactive =true");
		  	querytds=this.persistenceService.getSession().createSQLQuery(query10.toString());
		   	list = querytds.list();
		  	List<String> tds=new ArrayList<String>();
		  	if (list.size() != 0) {
		  		for (final Object[] object : list)
		  		{
		  			tds.add(object[0].toString());
		  		}
		  	}
			  	//for partyname
			  	SQLQuery queryparty =  null;
			  	list.clear();
			  	final StringBuffer query2 = new StringBuffer(500);
			  	query2
			      .append("select distinct vdp.id,vdp.detailname from voucher_detail_party vdp ");
			  	queryparty=this.persistenceService.getSession().createSQLQuery(query2.toString());
			   	list = queryparty.list();
			  	if (list.size() != 0) {
			  		for (final Object[] object : list)
			  		{
			  			partyMap.put(object[0].toString(),object[1].toString());
			  		}
			  	}
 //for account number
	SQLQuery queryAcc =  null;
	list.clear();
	final StringBuffer query3 = new StringBuffer(500);
	query3
	    .append("select distinct ei2.voucherheaderid ,v1.vouchernumber ,b2.accountnumber from egf_instrumentheader ei,egf_instrumentvoucher ei2,bankaccount b2,voucherheader v1 where b2.id = ei.bankaccountid and ei2.voucherheaderid = v1.id and ei2.instrumentheaderid = ei.id ");
	queryparty=this.persistenceService.getSession().createSQLQuery(query3.toString());
	list = queryparty.list();
	if (list.size() != 0) {
		for (final Object[] object : list)
		{
			accMap.put(object[1].toString(),object[2].toString());
		}
	}
			  	//for deduction pex
				  SQLQuery querydedpex = null; 
				  list.clear(); 
				  final StringBuffer querypex = new StringBuffer(500); 
				  querypex
				  .append("select distinct v1.id,ei.transactionnumber as pex,to_char(ei.transactiondate, 'dd-Mon-yyyy') as pexdate, "
			  +" ei.instrumentnumber as cheque,to_char(ei.instrumentdate , 'dd-Mon-yyyy') as chequedate "
			  +" from voucherheader v1,generalledger g2,miscbilldetail m,egf_instrumentheader ei,egf_instrumentvoucher ei2 "
			  +" where g2.voucherheaderid = v1.id and ei2.voucherheaderid =v1.id and ei2.instrumentheaderid = ei.id "
				  + " and ei2.voucherheaderid = m.payvhid order by v1.id");
				  querydedpex=this.persistenceService.getSession().createSQLQuery(querypex.toString()); 
				  list = querydedpex.list(); List dedPex=new ArrayList(); 
				  if(list.size() != 0) 
				  { 
					  for (final Object[] e : list) 
					  { 
						  dedPex.add((null != e[1] ? e[1].toString() : "")); 
						  dedPex.add((null != e[2] ? e[2].toString() : ""));
						  dedPex.add((null != e[3] ? e[3].toString() : "")); 
						  dedPex.add((null != e[4] ? e[4].toString() : "")); 
						  dedPexMap.put(e[0].toString(),dedPex); 
					  } 
				  }
				String voucherid="";
				String glcode="";
				String conString="";
				SQLQuery query1 = null;
				query1 = this.persistenceService.getSession().createSQLQuery(
						"select distinct v1.vouchernumber,g2.glcode,dvm.vh_id from voucherheader v1,generalledger g2,deduc_voucher_mpng dvm where g2.voucherheaderid = v1.id and v1.id = dvm.ph_id");
				rows1 = query1.list();
				if (rows1.size() != 0) {
					for (Object[] e1 : rows1) {
							voucherid=e1[2].toString();
							glcode=e1[1].toString();
							conString=voucherid+"#"+glcode;
							map1.put(conString,e1[0].toString());
					}
				}
				query = this.persistenceService.getSession().createSQLQuery("select vh.vouchernumber as vouchernumber, " + 
				" to_char(voucherdate, 'dd-Mon-yyyy')as voucherdate,vh.description as partyName,(select vouchernumber from voucherheader where id = ei2.voucherheaderid) as bvpno," + 
				" ei.transactionnumber as pex, to_char(ei.transactiondate, 'dd-Mon-yyyy') as pexdate,concat(vh.name, '-', vh.type) as vouchertype," + 
						" f.name as functionName,vh.description as naration,c2.name as glcode,g2.glcodeid as glcodeid ,debitamount as debitamount,g2.creditamount as creditamount,g2.glcode as code,vh.id as id,b2.accountnumber " + 
						" from voucherheader vh,vouchermis v,function f,egf_instrumentheader ei,egf_instrumentvoucher ei2,miscbilldetail m,chartofaccounts c2,generalledger g2,bankaccount b2 " + 
						" where f.id = v.functionid and b2.id = ei.bankaccountid and c2.id = g2.glcodeid and v.voucherheaderid = m.billvhid and ei2.instrumentheaderid = ei.id " + 
				" and vh.id = m.billvhid and ei2.voucherheaderid = m.payvhid and m.billvhid = g2.voucherheaderid " + 
				" and ei.transactiondate >= to_date('"+fromDate+"','dd/mm/yyyy') and ei.transactiondate <= to_date('"+toDate+"','dd/mm/yyyy') " + 
						" and ei.id_status not in (1) order by ei.transactionnumber asc,ei.transactiondate asc,vh.vouchernumber,debitamount desc,creditamount desc") ;
													
			System.out.println("::::::>>>>>" + query);
			rows = query.list();

			System.out.println("row size " + rows.size());
			List finalList=new ArrayList();
			String vid="";
String bvpNew="",vNew="", budgetHeadNew="", accNumNew="", narrationNew="";
			if (rows.size() != 0) {
				for (Object[] e : rows) {
					r = new ExpenditurePex();
					if(result.get(e[4].toString())==null) {
					r.setPex((null != e[4] ? e[4].toString() : null));
					r.setPexDate((null != e[5] ? e[5].toString() : null));
					bvpNew="";
					r.setBvp((null != e[3] ? e[3].toString() : null));
					bvpNew=r.getBvp();
					r.setBvpDate((null != e[1] ? e[1].toString() : null));
					vNew="";
					r.setvNo((null != e[0] ? e[0].toString() : null));
					vNew=r.getvNo();
					r.setvDate((null != e[1] ? e[1].toString() : null));
					r.setVoucherType((null != e[6] ? e[6].toString() : null));
								
					if(partyMap.containsKey(e[14].toString()))
					{
						r.setPartyName(partyMap.get(e[14].toString()));
					}
					else
					{
						r.setPartyName("");
					}
					budgetHeadNew="";
					r.setBudgetHead((null != e[7] ? e[7].toString() : ""));
					budgetHeadNew=r.getBudgetHead();
					narrationNew="";
					r.setNarration((null != e[8] ? e[8].toString() : ""));
					narrationNew=r.getNarration();
					r.setParticulars((null != e[9] ? e[9].toString() : ""));
					//vid=e[14].toString();
					r.setDebitamt((null != e[11] ? e[11].toString() : null));
					r.setCreditamt((null != e[12] ? e[12].toString() : null));
					r.setGlcodeId((null != e[13] ? e[13].toString() : null));
					r.setAccNum((null != e[15] ? e[15].toString() : null));
					//if(r.getBvp().contains("CJV")||r.getBvp().contains("EJV")||r.getBvp().contains("PJV"))
					//{}
					//else {
					details.add(r);
					detailExp.add(r);
					result.put(r.getPex(),details);
					//}
				}
				else
				{
					//code form dedpexmap
					if(dedPexMap.containsKey(e[14].toString()))
					{
						String transactionno="";
						String transactiondate="";
						String instrumentno="";
						String instrumentdate="";
						List<String> dedPexList=dedPexMap.get(e[14].toString());
						for(int i=0;i<dedPexList.size();i+=3) {
							transactionno=dedPexList.get(i);
							transactiondate=dedPexList.get(i+1);
							instrumentno=dedPexList.get(i+2);
							instrumentdate=dedPexList.get(i+3);
							if(transactionno=="")
							{
								r.setPex(instrumentno);
								r.setPexDate(instrumentdate);
							}
							else
							{
								r.setPex(transactionno);
								r.setPexDate(transactiondate);
							}
						}
					}
					if(null != e[3]) {
						if(bvpNew.equalsIgnoreCase(e[3].toString())) {
							r.setBvp("");
							r.setBvpDate("");
							r.setPartyName("");
							r.setVoucherType("");
						}
						else {
							r.setBvp((null != e[3] ? e[3].toString() : null));
							bvpNew=r.getBvp();
							r.setBvpDate((null != e[1] ? e[1].toString() : null));
							r.setAccNum((null != e[15] ? e[15].toString() : null));
							if(partyMap.containsKey(e[14].toString()))
							{
								r.setPartyName(partyMap.get(e[14].toString()));
							}
							r.setVoucherType((null != e[6] ? e[6].toString() : null));
						}
					}
					if(null != e[0]) {
						if(vNew.equalsIgnoreCase(e[0].toString())) {
							r.setvNo("");
							r.setvDate("");
						}
						else {
							r.setvNo((null != e[0] ? e[0].toString() : null));
							vNew=r.getvNo();
							r.setvDate((null != e[1] ? e[1].toString() : null));
						}
					}
								
					if(null != e[7]) {
						if(budgetHeadNew.equalsIgnoreCase(e[7].toString())) {
							r.setBudgetHead("");
						}
						else {
							r.setBudgetHead((null != e[7] ? e[7].toString() : ""));
							budgetHeadNew=r.getBudgetHead();
						}
					}
								
					if(null != e[8]) {
						if(narrationNew.equalsIgnoreCase(e[8].toString())) {
							r.setNarration("");
						}
						else {
							r.setNarration((null != e[8] ? e[8].toString() : ""));
							narrationNew=r.getNarration();
						}
					}
					r.setParticulars((null != e[9] ? e[9].toString() : null));
					///code from map
					voucherid=e[14].toString();
					glcode=e[13].toString();
					conString=voucherid+"#"+glcode;
					if(map1.containsKey(conString))
					{
						r.setBvp(map1.get(conString));
						if(accMap.containsKey(r.getBvp()))
						{
							r.setAccNum(accMap.get(r.getBvp()));
						}
					}
					////
					r.setDebitamt((null != e[11] ? e[11].toString() : null));
					r.setCreditamt((null != e[12] ? e[12].toString() : null));
					r.setGlcodeId((null != e[13] ? e[13].toString() : null));
					if(r.getBvp().contains("CJV")||r.getBvp().contains("EJV")||r.getBvp().contains("PJV"))
					{}
					else {
						details.add(r);
						detailExp.add(r);
						result.put(r.getPex(),details);
					}
				}
			}
		}
				
		in = resultToExcel(detailExp, COLUMNS);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=PexExp.xls");
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));

			//return details;
	} catch (Exception e) {
		e.printStackTrace();
	}
		//return detailList;

	HttpHeaders headers = new HttpHeaders();
	headers.add("Content-Disposition", "attachment; filename=PexExp.xls");
	return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));

   }
    
    public static ByteArrayInputStream resultToExcel(List<ExpenditurePex> RetrachmentReport, String[] COLUMNS)
			throws IOException {

		HSSFWorkbook workbook = new HSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CreationHelper createHelper = workbook.getCreationHelper();

		Sheet sheet = workbook.createSheet("Pex Exp Report");

		// Row for Header
		Row headerRow = sheet.createRow(0);
		int sl = 1;
		// Header
		for (int col = 0; col < COLUMNS.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(COLUMNS[col]);
		}

		int rowIdx = 1;
		for (ExpenditurePex retrachment : RetrachmentReport) {
			Row row = sheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(String.valueOf(sl++));

				//CellStyle cellStyle = workbook.createCellStyle();
				//cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy h:mm"));
			if (retrachment.getPexDate() != null) {
				Cell cell = row.createCell(1);
				cell.setCellValue(retrachment.getPexDate());
					//cell.setCellStyle(cellStyle);
			}

			if (retrachment.getPex()!= null)
				row.createCell(2).setCellValue(retrachment.getPex());

			if (retrachment.getBvp()!= null)
				row.createCell(3).setCellValue(retrachment.getBvp());

			if (retrachment.getBvpDate() != null) {
				Cell cell = row.createCell(4);
				cell.setCellValue(retrachment.getBvpDate());
					//cell.setCellStyle(cellStyle);
			}

			if (retrachment.getvNo()!= null)
				row.createCell(5).setCellValue(retrachment.getvNo());
			
			if (retrachment.getvDate() != null) {
				Cell cell = row.createCell(6);
				cell.setCellValue(retrachment.getvDate());
					//cell.setCellStyle(cellStyle);
			}

			if (retrachment.getVoucherType()!= null)
				row.createCell(7).setCellValue(retrachment.getVoucherType());
			
			if (retrachment.getPartyName()!= null)
				row.createCell(8).setCellValue(retrachment.getPartyName());
			
			if (retrachment.getBudgetHead() != null) {
				row.createCell(9).setCellValue(retrachment.getBudgetHead());
			}
			
			if (retrachment.getNarration()!= null)
				row.createCell(10).setCellValue(retrachment.getNarration());

			if (retrachment.getGlcodeId()!= null)
				row.createCell(11).setCellValue(retrachment.getGlcodeId());

			if (retrachment.getAccNum()!= null)
				row.createCell(12).setCellValue(retrachment.getAccNum());
			
			if (retrachment.getParticulars()!= null)
				row.createCell(13).setCellValue(retrachment.getParticulars());

			if (retrachment.getDebitamt()!= null)
				row.createCell(14).setCellValue(new Double(retrachment.getDebitamt()));
			
			if (retrachment.getCreditamt()!= null)
				row.createCell(15).setCellValue(new Double(retrachment.getCreditamt()));
 


		}

		workbook.write(out);
		return new ByteArrayInputStream(out.toByteArray());

	}


}