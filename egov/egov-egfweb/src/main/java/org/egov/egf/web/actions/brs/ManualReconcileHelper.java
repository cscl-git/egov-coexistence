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

package org.egov.egf.web.actions.brs;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFinancialYear;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.dao.FinancialYearHibernateDAO;
import org.egov.egf.model.ReconcileBean;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.FinancialStatus;
import org.egov.infra.microservice.models.Instrument;
import org.egov.infra.microservice.models.InstrumentSearchContract;
import org.egov.infra.microservice.models.MisRemittanceDetails;
import org.egov.infra.microservice.models.TransactionType;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.services.instrument.InstrumentHeaderService;
import org.egov.services.instrument.InstrumentOtherDetailsService;
import org.egov.services.instrument.ReceiptRemittanceDetailsService;
import org.egov.utils.FinancialConstants;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service
public class ManualReconcileHelper {

	private static  final Logger LOGGER = Logger.getLogger(ManualReconcileHelper.class);

    private static final String INSTRUMENTTYPE_NAME_CHEQUE = "Cheque";

    private static final String INSTRUMENT_NEW_STATUS = "Deposited";

    private static final String INSTRUMENT_RECONCILED_STATUS = "Reconciled";
    
    private static final String INSTRUMENT_ALL_STATUS = "All";

	@Autowired
	private AppConfigValueService appConfigValueService;
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@Autowired
	@Qualifier("instrumentOtherDetailsService")
	private InstrumentOtherDetailsService instrumentOtherDetailsService;
	@Autowired
	private EgwStatusHibernateDAO egwStatusHibernateDAO;
	
	@Autowired
	@Qualifier("instrumentHeaderService")
	private InstrumentHeaderService instrumentHeaderService;
	
	@Autowired
	private MicroserviceUtils microserviceUtils;
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
	@Autowired
        FinancialYearHibernateDAO financialYearDAO;
	
	@Autowired
	private ReceiptRemittanceDetailsService receiptRemittanceDetailsService;

        private int DEFAULT_LIMIT = 100;
        
	public Map<String,String> getUnReconciledDrCr(Long bankAccId,Date fromDate,Date toDate)  
	{
		Map<String,String> unreconMap=new LinkedHashMap<String,String>();
		//String  ="decode(iv.voucherHeaderId,null,0,ih.instrumentAmount)";
		String instrumentsForBrsEntryTotal="case when br.voucherHeaderId is null then ih.instrumentAmount else 0 end";
		//String instrumentsForOtherTotal="decode(br.voucherHeaderId,null,ih.instrumentAmount,0)";
		String voucherExcludeStatuses=getExcludeStatuses();
		
		String totalQuery="SELECT (sum(CASE WHEN ih.ispaycheque='1' then ih.instrumentAmount else 0 end ))  AS \"brs_creditTotal\", "
			+" (sum(CASE WHEN ih.ispaycheque = '0' then  ih.instrumentAmount else 0 end)) AS \"brs_debitTotal\" "
			+" FROM egf_instrumentheader ih 	WHERE   ih.bankAccountId =:bankAccountId "
			+" AND IH.INSTRUMENTDATE >= :fromDate" 
			+" AND IH.INSTRUMENTDATE <= :toDate"
			+" AND  ( (ih.ispaycheque='0' and  ih.id_status=(select id from egw_status where moduletype='Instrument' "
			+ " and description='Deposited'))or (ih.ispaycheque='1' and  ih.id_status=(select id from egw_status where "
			+ " moduletype='Instrument'  and description='New'))) "
			+" and ih.instrumentnumber is not null";
	//see u might need to exclude brs entries here 
		
		String otherTotalQuery=" SELECT (sum(case when ih.ispaycheque='1' then ih.instrumentAmount else 0 end))  AS \"brs_creditTotalOthers\", "
			+" (sum(case when ih.ispaycheque= '0' then ih.instrumentAmount else 0 end))  AS \"brs_debitTotalOthers\" "
			+" FROM  egf_instrumentheader ih	WHERE   ih.bankAccountId =:bankAccountId"
			+" AND IH.transactiondate >= :fromDate"
			+" AND IH.transactiondate <= :toDate  "
			+" AND IH.instrumenttype NOT IN (SELECT id from egf_instrumenttype where type='pex')"
			+" AND ( (ih.ispaycheque='0' and ih.id_status=(select id from egw_status where moduletype='Instrument'"
			+ "  and description='Deposited'))or (ih.ispaycheque='1' and  ih.id_status=(select id from egw_status where"
			+ " moduletype='Instrument'  and description='New'))) "
			+" AND ih.transactionnumber is not null";
		
		String otherTotalQueryPEX=" SELECT (sum(case when ih.ispaycheque='1' then ih.instrumentAmount else 0 end))  AS \"brs_creditTotalOthers\", "
				+" (sum(case when ih.ispaycheque= '0' then ih.instrumentAmount else 0 end))  AS \"brs_debitTotalOthers\" "
				+" FROM  egf_instrumentheader ih	WHERE   ih.bankAccountId =:bankAccountId"
				+" AND IH.transactiondate >= :fromDate"
				+" AND IH.transactiondate <= :toDate  "
				+" AND IH.instrumenttype IN (SELECT id from egf_instrumenttype where type='pex')"
				+" AND ( (ih.ispaycheque='0' and ih.id_status=(select id from egw_status where moduletype='Instrument'"
				+ "  and description='Deposited'))or (ih.ispaycheque='1' and  ih.id_status=(select id from egw_status where"
				+ " moduletype='Instrument'  and description='New'))) "
				+" AND ih.transactionnumber is not null";
		
		String brsEntryQuery=" SELECT (sum(case when ih.ispaycheque= '1' then "+instrumentsForBrsEntryTotal+" else 0 end ))  AS \"brs_creditTotalBrsEntry\", "
		+" (sum(case when ih.ispaycheque= '0' then "+instrumentsForBrsEntryTotal+" else 0 end))  AS \"brs_debitTotalBrsEntry\" "
		+" FROM egf_instrumentheader ih, bankentries br	WHERE   ih.bankAccountId = :bankAccountId"
		+" AND IH.transactiondate >= :fromDate  "
		+" AND IH.transactiondate <= :toDate "
		+" AND ( (ih.ispaycheque='0' and ih.id_status=(select id from egw_status where moduletype='Instrument' "
		+ " and description='Deposited')) or (ih.ispaycheque='1' and  ih.id_status=(select id from egw_status where moduletype='Instrument'  and description='New'))) "
		+" AND br.instrumentHeaderid=ih.id and ih.transactionnumber is not null"	;
	
		if(LOGGER.isInfoEnabled())     LOGGER.info("  query  for  total : "+totalQuery);
		
		
		
		String unReconciledDrCr="";
		
		
		String creditTotal=null;
		String creditOthertotal=null;
		String debitTotal=null;
		String debitOtherTotal=null;
		String creditTotalBrsEntry=null;
		String debitTotalBrsEntry=null;
		String creditOthertotalPex=null;
		try
		{
			SQLQuery totalSQLQuery = persistenceService.getSession().createSQLQuery(totalQuery);
			totalSQLQuery.setLong("bankAccountId",bankAccId);
			totalSQLQuery.setDate("fromDate",fromDate);
			totalSQLQuery.setDate("toDate",toDate);
			
			List list = totalSQLQuery.list();
			if (list.size()>0)
			{
				if(LOGGER.isDebugEnabled())     LOGGER.debug(list.get(0));
				Object [] my = (Object[])list.get(0);
				creditTotal=my[0]!=null?my[0].toString():null;
				debitTotal=my[1]!=null?my[1].toString():null;
			}

			if(LOGGER.isInfoEnabled())     LOGGER.info("  query  for other than cheque/DD: "+otherTotalQuery);
			totalSQLQuery = persistenceService.getSession().createSQLQuery(otherTotalQuery);
			totalSQLQuery.setLong("bankAccountId",bankAccId);
			totalSQLQuery.setDate("fromDate",fromDate);
			totalSQLQuery.setDate("toDate",toDate);
			list = totalSQLQuery.list();
			if (list.size()>0)
			{
				if(LOGGER.isDebugEnabled())     LOGGER.debug(list.get(0));
				Object [] my = (Object[])list.get(0);
				creditOthertotal=my[0]!=null?my[0].toString():null;
				debitOtherTotal=my[1]!=null?my[1].toString():null;
			}
			totalSQLQuery = persistenceService.getSession().createSQLQuery(otherTotalQueryPEX);
			totalSQLQuery.setLong("bankAccountId",bankAccId);
			totalSQLQuery.setDate("fromDate",fromDate);
			totalSQLQuery.setDate("toDate",toDate);
			list = totalSQLQuery.list();
			if (list.size()>0)
			{
				if(LOGGER.isDebugEnabled())     LOGGER.debug(list.get(0));
				Object [] my = (Object[])list.get(0);
				creditOthertotalPex=my[0]!=null?my[0].toString():null;
				
			}
			if(LOGGER.isInfoEnabled())     LOGGER.info("  query  for bankEntries: "+brsEntryQuery);

			totalSQLQuery = persistenceService.getSession().createSQLQuery(brsEntryQuery);
			totalSQLQuery.setLong("bankAccountId",bankAccId);
			totalSQLQuery.setDate("fromDate",fromDate);
			totalSQLQuery.setDate("toDate",toDate);
			list = totalSQLQuery.list();
			if (list.size()>0)
			{
				if(LOGGER.isDebugEnabled())     LOGGER.debug(list.get(0));
				Object [] my = (Object[])list.get(0);
				creditTotalBrsEntry=my[0]!=null?my[0].toString():null;
				debitTotalBrsEntry=my[1]!=null?my[1].toString():null;
			}

			
      /* ReconcileBean reconBean=new ReconcileBean();
       reconBean.setCreditAmount(BigDecimal.valueOf(creditTotal));
       reconBean.setDebitAmount(debitTotal);
       */
			creditTotal=creditTotal==null?"0":creditTotal;
			debitTotal=debitTotal==null?"0":debitTotal;
			creditOthertotal=creditOthertotal==null?"0":creditOthertotal;
			debitOtherTotal=debitOtherTotal==null?"0":debitOtherTotal;
			debitTotalBrsEntry=debitTotalBrsEntry==null?"0":debitTotalBrsEntry;
			creditOthertotalPex=creditOthertotalPex==null?"0":creditOthertotalPex;
			
			unreconMap.put("Cheque/DD/Cash Payments",creditTotal);
			unreconMap.put("Cheque/DD/Cash Receipts",debitTotal);
			unreconMap.put("RTGS Payments",creditOthertotal);
			unreconMap.put("Other Receipts",debitOtherTotal);
			unreconMap.put("BRS Entry",debitTotalBrsEntry);
			unreconMap.put("PEX Payments",creditOthertotalPex);
			
		/*//unReconciledDrCr="Cheque/DD/Cash Payments:"+(creditTotal != null ? creditTotal : "0" )+",RTGS Payments:"+(creditOthertotal!= null ? creditOthertotal : "0")
		+",Cheque/DD/Cash Receipts:"+(debitTotal!= null ? debitTotal : "0") +",Other Receipts:"+( debitOtherTotal!= null ? debitOtherTotal : "0")+""+
		"/"+(creditTotalBrsEntry!= null ? creditTotalBrsEntry : "0") +",Net:"+( debitTotalBrsEntry!= null ? debitTotalBrsEntry : "0")+"";*/
		}
		catch(Exception e)
		{
			LOGGER.error("Exp in getUnReconciledDrCr"+e.getMessage());
			
		}
		return unreconMap;
	}
	
	private String getExcludeStatuses() {
		
		List<AppConfigValues> configValuesByModuleAndKey = appConfigValueService.getConfigValuesByModuleAndKey("EGF","statusexcludeReport");
		final String statusExclude = configValuesByModuleAndKey.get(0).getValue();
		return statusExclude;
		
	}
	
	public List<ReconcileBean> getUnReconciledCheques(ReconcileBean reconBean,String accountno) 
	{
		List<ReconcileBean> list=new ArrayList<ReconcileBean>();
		String instrumentCondition="";
		if("2".equalsIgnoreCase(reconBean.getPaymenttype())) {	
			StringBuffer query = null;
			try{	
			if(reconBean.getStatusType().equalsIgnoreCase("New")) {	
			     query=new StringBuffer().append(" select    string_agg(distinct v.vouchernumber, ',') as \"voucherNumber\" ,mr.id as \"ihId\", vm.voucherheaderid  as \"vhId\",\r\n"
			     		+ "			     		 v.\"type\" as \"type\",\r\n"
			     		+ "			     		'Receipt'  as instrumentType ,\r\n"
			     		+ "			     		mr.amount as chequeAmount,\r\n"
			     		+ "			     		to_char(v.voucherdate,'dd/mm/yyyy') as reconciledOn,\r\n"
			     		+ "			     		mr.reconciledcomment as \"reconciledComment\"\r\n"
			     		+ "			     		FROM \r\n"
			     		+ "			     		VOUCHERHEADER v ,\r\n"
			     		+ "			     		VOUCHERMIS vm,\r\n"
			     		+ "			     		mis_receipts_details mis,\r\n"
			     		+ "			     		MIS_REMITTANCE_DETAILS mr\r\n"
			     		+ "			     		 WHERE \r\n"
			     		+ "			     		v.voucherdate  >= '"+reconBean.getFromDate()+"' AND \r\n"
			     		+ "			     		v.voucherdate  <= '"+reconBean.getReconciliationDate()+"' AND \r\n"
			     		+ "			     		v.ID= vm.voucherheaderid  and\r\n"
			     		+ "			     		v.STATUS not in (4,5) and\r\n"
			     		+ "			     		v.vouchernumber =mr.voucher_number and \r\n"
			     		+ "			     		trim(split_part(mr.receiptnumbers,',',1),' \"\" ') =mis.receipt_number\r\n"
			     		+ "			     		and mis.payment_status= 'DEPOSITED'\r\n"
			     		+ "			     		and mr.bankaccount ='"+accountno+"' \r\n"
			     		+ "			     		and (mr.status is null or mr.status = '') \r\n"
			     		+ "			     		and vm.departmentcode = coalesce(nullif('"+reconBean.getDepartment()+"',''),mr.department) \r\n"
			     		+ "			     		group by\r\n"
			     		+ "			     		v.vouchernumber,\r\n"
			     		+ "			     		mr.id ,\r\n"
			     		+ "			     		vm.voucherheaderid ,\r\n"
			     		+ "			     		v.\"type\", \r\n"
			     		+ "			     		mr.amount,\r\n"
			     		+ "			     		v.voucherdate,\r\n"
			     		+ "			     		mr.reconciledcomment ");
			}
			if(reconBean.getStatusType().equalsIgnoreCase("Reconciled")) {				
				 query=new StringBuffer().append(" select distinct string_agg(distinct v.vouchernumber, ',') as \"voucherNumber\" ,mr.id as \"ihId\", vm.voucherheaderid  as \"vhId\", \r\n"
				 		+ "				 		v.\"type\" as \"type\",\r\n"
				 		+ "				 		'Receipt'  as instrumentType ,\r\n"
				 		+ "				 		mr.amount as chequeAmount,\r\n"
				 		+ "				 		to_char(mr.reconciledon,'dd/mm/yyyy') as reconciledOn,\r\n"
				 		+ "				 		mr.reconciledcomment as \"reconciledComment\"\r\n"
				 		+ "				 		FROM \r\n"
				 		+ "				 		VOUCHERHEADER v ,\r\n"
				 		+ "				 		VOUCHERMIS vm,\r\n"
				 		+ "				 		mis_receipts_details mis,\r\n"
				 		+ "				 		MIS_REMITTANCE_DETAILS mr\r\n"
				 		+ "				 		WHERE \r\n"
				 		+ "				 		v.voucherdate  >= '"+reconBean.getFromDate()+"' AND \r\n"
				 		+ "				 		v.voucherdate  <= '"+reconBean.getReconciliationDate()+"'  AND \r\n"
				 		+ "				 		v.ID= vm.voucherheaderid  and\r\n"
				 		+ "				 		v.STATUS not in (4,5) and\r\n"
				 		+ "				 		v.vouchernumber =mr.voucher_number and\r\n"
				 		+ "				 		trim(split_part(mr.receiptnumbers,',',1),' \"\" ') =mis.receipt_number\r\n"
				 		+ "				 		and mr.bankaccount ='"+accountno+"' \r\n"
				 		+ "				 		and mr.status = 'Reconciled' \r\n"
				 		+ "				 		and vm.departmentcode = coalesce(nullif('"+reconBean.getDepartment()+"',''),mr.department)  \r\n"
				 		+ "				 		group by\r\n"
				 		+ "				 		v.vouchernumber,\r\n"
				 		+ "				 		mr.id ,\r\n"
				 		+ "				 		vm.voucherheaderid ,\r\n"
				 		+ "				 		v.\"type\", \r\n"
				 		+ "				 		mr.id  ,\r\n"
				 		+ "				 		mr.amount,\r\n"
				 		+ "				 		v.voucherdate,\r\n"
				 		+ "				 		mr.reconciledcomment ");				
			}	
			
			System.out.println("###query1:::"+query);
			
			LOGGER.info("from date : "+reconBean.getFromDate());
	        LOGGER.info("Reconciliation or to Date : "+reconBean.getReconciliationDate());
	        
			SQLQuery createSQLQuery = persistenceService.getSession().createSQLQuery(query.toString());
			//createSQLQuery.setLong("bankAccId", reconBean.getAccountId());
			//createSQLQuery.setDate("fromDate", reconBean.getFromDate()); // added by Abhishek on 24/02/2021
			//createSQLQuery.setDate("toDate", reconBean.getReconciliationDate());
			createSQLQuery.addScalar("voucherNumber",StringType.INSTANCE);
			createSQLQuery.addScalar("ihId",StringType.INSTANCE);
			createSQLQuery.addScalar("vhId",StringType.INSTANCE);
			//createSQLQuery.addScalar("vhId",StringType.INSTANCE);
			//createSQLQuery.addScalar("chequeDate",StringType.INSTANCE);
			//createSQLQuery.addScalar("chequeNumber",StringType.INSTANCE);
			createSQLQuery.addScalar("chequeAmount",BigDecimalType.INSTANCE);
			//createSQLQuery.addScalar("txnType",StringType.INSTANCE);
			createSQLQuery.addScalar("type",StringType.INSTANCE);
			createSQLQuery.addScalar("instrumentType",StringType.INSTANCE);
			createSQLQuery.addScalar("reconciledOn",StringType.INSTANCE);
			createSQLQuery.addScalar("reconciledComment",StringType.INSTANCE);
			createSQLQuery.setResultTransformer(Transformers.aliasToBean(ReconcileBean.class));
			
		    list = (List<ReconcileBean>)createSQLQuery.list();
		        try {
		            this.getUnreconsiledReceiptInstruments(reconBean,list);
	                } catch (Exception e) {
	                    LOGGER.error("ERROR occurred while fetching the unrconciled receipt instruments : "+e.getMessage());
	                }
			}
			catch(Exception e)
			{
				LOGGER.error("Exp in getUnReconciledCheques:"+e.getMessage());
				throw new ApplicationRuntimeException(e.getMessage());
			}
			
		}
		else {
		if(reconBean.getInstrumentNo()!=null && !reconBean.getInstrumentNo().isEmpty())
		{
			instrumentCondition="and (ih.instrumentNumber='"+reconBean.getInstrumentNo()+"' or ih.transactionnumber='"+reconBean.getInstrumentNo()+"' )";
		}
		try{
		String voucherExcludeStatuses=getExcludeStatuses();
       
		StringBuffer query=new StringBuffer().append(" select string_agg(distinct v.vouchernumber, ',') as \"voucherNumber\" ,ih.id as \"ihId\",iv.voucherheaderid as \"vhId\", case when ih.instrumentNumber is null then 'Direct' else ih.instrumentNumber end as \"chequeNumber\", " + 
				 " to_char(ih.instrumentdate,'dd/mm/yyyy') as \"chequeDate\" ,ih.instrumentAmount as \"chequeAmount\",rec.transactiontype as \"txnType\" , " 
				 + " case when rec.transactionType='Cr' then 'Payment' else 'Receipt' end as \"type\" , insType.type as instrumentType , io.reconciledcomment as \"reconciledComment\", to_char(io.reconciledon,'dd/mm/yyyy') as \"reconciledOn\" FROM BANKRECONCILIATION rec, BANKACCOUNT BANK," 
				 +" VOUCHERHEADER v ,egf_instrumentheader ih, egf_instrumentotherdetails io, egf_instrumentVoucher iv, egf_instrumenttype insType WHERE " 
				 + " ih.bankAccountId = BANK.ID AND bank.id =:bankAccId AND IH.INSTRUMENTDATE >= '"+reconBean.getFromDate()+"' AND IH.INSTRUMENTDATE <= '"+reconBean.getReconciliationDate()+"' " 
				 +" AND v.ID= iv.voucherheaderid and v.STATUS not in ("+voucherExcludeStatuses+") " +instrumentCondition  );
				 //" AND ((ih.id_status=(select id from egw_status where moduletype='Instrument' and description='Deposited') and ih.ispaycheque='0') or (ih.ispaycheque='1' and ih.id_status=(select id from egw_status where moduletype='Instrument' and description='New'))) " 
		if(reconBean.getStatusType().equalsIgnoreCase("New")) {
				query.append(" AND ((ih.id_status=(select id from egw_status where moduletype='Instrument' and description='Deposited') and ih.ispaycheque='0') or (ih.ispaycheque='1' and ih.id_status=(select id from egw_status where moduletype='Instrument' and description='New'))) ");
		}
		if(reconBean.getStatusType().equalsIgnoreCase("All")) {
			query.append(" AND ih.id_status in('3','4') ");
		}
		if(reconBean.getStatusType().equalsIgnoreCase("Reconciled")) {
			query.append(" AND ih.id_status in('4') ");
		}
				 query.append(" AND rec.instrumentHeaderId=cast(ih.id as varchar(100)) and iv.instrumentHeaderid=ih.id and io.instrumentheaderid=ih.id and insType.id=ih.instrumenttype and ih.instrumentNumber is not null" 
				 + " group by ih.id,rec.transactiontype,insType.type,iv.voucherheaderid ,io.reconciledcomment,io.reconciledon" 
				  
				 + " union " 
				  
				 +" select string_agg(distinct v.vouchernumber, ',') as \"voucherNumber\" , ih.id as \"ihId\",iv.voucherheaderid as \"vhId\", case when ih.transactionnumber is null then 'Direct' else ih.transactionnumber end as \"chequeNumber\", " + 
				 " to_char(ih.transactiondate,'dd/mm/yyyy') as \"chequedate\" ,ih.instrumentAmount as \"chequeamount\",rec.transactiontype as \"txnType\", case when rec.transactionType= 'Cr' then 'Payment' else 'Receipt' end as \"type\" , insType.type as instrumentType ,io.reconciledcomment as \"reconciledComment\", to_char(io.reconciledon,'dd/mm/yyyy') as \"reconciledOn\" FROM BANKRECONCILIATION rec, BANKACCOUNT BANK," 
				 +" VOUCHERHEADER v ,egf_instrumentheader ih, egf_instrumentotherdetails io, egf_instrumentVoucher iv, egf_instrumenttype insType WHERE ih.bankAccountId = BANK.ID AND bank.id = :bankAccId " 
				 +" AND IH.transactiondate >= '"+reconBean.getFromDate()+"' AND IH.transactiondate <= '"+reconBean.getReconciliationDate()+"' " +instrumentCondition  
				 +" AND v.ID= iv.voucherheaderid and v.STATUS not in ("+voucherExcludeStatuses+") ");
				 if(reconBean.getStatusType().equalsIgnoreCase("New")) {
						query.append(" AND ((ih.id_status=(select id from egw_status where moduletype='Instrument' and description='Deposited') and ih.ispaycheque='0') or (ih.ispaycheque='1' and ih.id_status=(select id from egw_status where moduletype='Instrument' and description='New'))) ");
				}
				if(reconBean.getStatusType().equalsIgnoreCase("All")) {
					query.append(" AND ih.id_status in('2','3','4') ");
				}
				if(reconBean.getStatusType().equalsIgnoreCase("Reconciled")) {
					query.append(" AND ih.id_status in('4') ");
				}

				//"AND ((ih.id_status=(select id from egw_status where moduletype='Instrument' and description='Deposited') and ih.ispaycheque='0')or (ih.ispaycheque='1' and ih.id_status=(select id from egw_status where moduletype='Instrument' and description='New'))) " 
				 query.append(" AND rec.instrumentHeaderId=cast(ih.id as varchar(100)) and iv.instrumentHeaderid=ih.id and io.instrumentheaderid=ih.id and insType.id=ih.instrumenttype and ih.transactionnumber is not null" 
				 +" group by ih.id,rec.transactiontype,insType.type,iv.voucherheaderid,io.reconciledcomment,io.reconciledon " ); 
        		
        
			  System.out.println("###query:::"+query);
        
			/*
			 * if(reconBean.getLimit() != null && reconBean.getLimit() != 0){
			 * query.append(" limit "+reconBean.getLimit()); }else{
			 * query.append(" limit "+DEFAULT_LIMIT ); reconBean.setLimit(DEFAULT_LIMIT); }
			 */
		
       // if(LOGGER.isInfoEnabled())    
        LOGGER.info("  query  for getUnReconciledCheques: "+query);
		/*String query=" SELECT decode(rec.chequeNumber, null, 'Direct', rec.chequeNumber) as \"chequeNumber\",rec.chequedate as \"chequedate\" ,amount as \"chequeamount\",transactiontype as \"txnType\" ,rec.type as \"type\" from bankreconciliation rec, bankAccount bank, voucherheader vh "
			+" where  rec.bankAccountId = bank.id AND bank.id ="+bankAccId+" and  rec.isReversed = 0 AND (rec.reconciliationDate > to_date('"+recDate+"'  || ' 23:59:59','DD-MON-YYYY HH24:MI:SS') "
			+" OR (rec.isReconciled = 0)) AND vh.VOUCHERDATE <= to_date('"+recDate+"'  || ' 23:59:59','DD-MON-YYYY HH24:MI:SS') and vh.id=rec.VOUCHERHEADERID and vh.STATUS<>4"
			+" union "
			+" select refno as \"chequeNumber\", txndate as \"chequedate\", txnamount as \"chequeamount\", decode(type,'R','Dr','Cr') as \"txnType\", "
			+" type as \"type\" from bankentries be,bankAccount bank where  be.bankAccountId = bank.id and bank.id ="+bankAccId+"  "
			+" and txndate<= to_date('"+recDate+"'  || ' 23:59:59','DD-MON-YYYY HH24:MI:SS') and voucherheaderid is null ";
*/
        LOGGER.info("from date : "+reconBean.getFromDate());
        LOGGER.info("Reconciliation or to Date : "+reconBean.getReconciliationDate());
        
		SQLQuery createSQLQuery = persistenceService.getSession().createSQLQuery(query.toString());
		createSQLQuery.setLong("bankAccId", reconBean.getAccountId());
		//createSQLQuery.setDate("fromDate", reconBean.getFromDate()); // added by Abhishek on 24/02/2021
		//createSQLQuery.setDate("toDate", reconBean.getReconciliationDate());
		createSQLQuery.addScalar("voucherNumber",StringType.INSTANCE);
		createSQLQuery.addScalar("ihId",StringType.INSTANCE);
		createSQLQuery.addScalar("vhId",StringType.INSTANCE);
		createSQLQuery.addScalar("chequeDate",StringType.INSTANCE);
		createSQLQuery.addScalar("chequeNumber",StringType.INSTANCE);
		createSQLQuery.addScalar("chequeAmount",BigDecimalType.INSTANCE);
		createSQLQuery.addScalar("txnType",StringType.INSTANCE);
		createSQLQuery.addScalar("type",StringType.INSTANCE);
		createSQLQuery.addScalar("instrumentType",StringType.INSTANCE);
		createSQLQuery.addScalar("reconciledOn",StringType.INSTANCE);
		createSQLQuery.addScalar("reconciledComment",StringType.INSTANCE);
		createSQLQuery.setResultTransformer(Transformers.aliasToBean(ReconcileBean.class));
	        list = (List<ReconcileBean>)createSQLQuery.list();
	        try {
	            this.getUnreconsiledReceiptInstruments(reconBean,list);
                } catch (Exception e) {
                    LOGGER.error("ERROR occurred while fetching the unrconciled receipt instruments : "+e.getMessage());
                }
		}
		catch(Exception e)
		{
			LOGGER.error("Exp in getUnReconciledCheques:"+e.getMessage());
			throw new ApplicationRuntimeException(e.getMessage());
		}
		
		}
		return list;
	}

	private void getUnreconsiledReceiptInstruments(ReconcileBean reconBean,List<ReconcileBean> list) {
	    if(list.size() < reconBean.getLimit()){
	        InstrumentSearchContract contract = new InstrumentSearchContract();
	        if(reconBean.getAccountId() != null){
	            StringBuilder query = new StringBuilder("from Bankaccount ba where ba.id=:bankAccountId and isactive=true");
	            Query createSQLQuery = persistenceService.getSession().createQuery(query.toString());
	            List<Bankaccount> bankAccount = createSQLQuery.setLong("bankAccountId", reconBean.getAccountId()).list();
	            contract.setBankAccountNumber(bankAccount.get(0).getAccountnumber());
	        }
	        if(StringUtils.isNotBlank(reconBean.getInstrumentNo())){
	            contract.setTransactionNumber(reconBean.getInstrumentNo());
	        }
	        if(StringUtils.isNotBlank(reconBean.getLimit().toString())){
	            contract.setPageSize(reconBean.getLimit() - list.size());
	        }
	        contract.setInstrumentTypes(INSTRUMENTTYPE_NAME_CHEQUE);
	        contract.setTransactionType(TransactionType.Debit);
	        if(reconBean.getStatusType().equalsIgnoreCase("All")) {
				
	        	contract.setFinancialStatuses(INSTRUMENT_ALL_STATUS);
			}
			if(reconBean.getStatusType().equalsIgnoreCase("Reconciled")) {
				
				contract.setFinancialStatuses(INSTRUMENT_RECONCILED_STATUS);
			}
			if(reconBean.getStatusType().equalsIgnoreCase("New")) {
				
				contract.setFinancialStatuses(INSTRUMENT_NEW_STATUS);
			}
	        
	        CFinancialYear finYearByDate = financialYearDAO.getFinYearByDate(reconBean.getReconciliationDate());
	        contract.setTransactionFromDate(finYearByDate.getStartingDate());
	        contract.setTransactionToDate(reconBean.getReconciliationDate());
	        List<Instrument> instruments = microserviceUtils.getInstrumentsBySearchCriteriaForReconciliation(contract);
	        for(Instrument ins : instruments){
	        	
	            if(ins.getInstrumentVouchers() != null && !ins.getInstrumentVouchers().isEmpty()&& !ins.getFinancialStatus().getDescription().equalsIgnoreCase("Cancelled")){
	                ReconcileBean reconcileBean = new ReconcileBean();
	                String txnType = ins.getTransactionType().name();
	                String type = TransactionType.Credit.equals(txnType) ? "Payment" : "Receipt";
	                String pattern = "dd/MM/yyyy";
	                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	                String date = simpleDateFormat.format(ins.getTransactionDate());
	                String reconciliationDate = simpleDateFormat.format(ins.getReconciledOn());
	                reconcileBean.setVoucherNumber(ins.getInstrumentVouchers().get(0).getVoucherHeaderId());
	                reconcileBean.setIhId("rm_rec~"+ins.getId());
	                reconcileBean.setChequeDate(date);
	                reconcileBean.setChequeNumber(ins.getTransactionNumber());
	                reconcileBean.setChequeAmount(ins.getAmount());
	                reconcileBean.setTxnType(txnType);
	                reconcileBean.setType(type);
	                reconcileBean.setReconciledOn(reconciliationDate);
	                
	                reconBean.setInstrumentType(ins.getInstrumentType().getName());
	                
	                list.add(reconcileBean);
	            }
	        	
	        }
	        
	    }
    }

    @Transactional
	public void update(List<Date> reconDates, List<String> reconComments, List<String> instrumentHeaders) {
		//int i=0;
		EgwStatus reconciledStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.STATUS_MODULE_INSTRUMENT, FinancialConstants.INSTRUMENT_RECONCILED_STATUS);
		Map<String,Date> instrumentIdAndDateMap = new HashMap<>();
		Map<String,String> instrumentIdAndCommentMap = new HashMap<>();
		int len = 0;
		/*try {
		for(Date reconcileOn:reconDates)
		{
			if(reconcileOn!=null)
			{
				String ihId = instrumentHeaders.get(i);
				if(!ihId.contains("rm_rec~")){
				    InstrumentHeader ih = instrumentHeaderService.reconcile(reconcileOn, Long.parseLong(ihId),reconciledStatus ); 
				    instrumentOtherDetailsService.reconcile(reconcileOn,  Long.parseLong(ihId),ih.getInstrumentAmount());
				}
				else{
				    instrumentIdAndDateMap.put(ihId.split("rm_rec~")[1],reconcileOn);
				}
			}
			i++;
		}
		}catch(Exception e) {
			e.printStackTrace();
		}*/
		//For Recon Comments
		try {
			len = reconDates.size();
			if(len > 0) {
				for(int j=0;j<len;j++) {
					Date reconcileOn =  reconDates.get(j);
					System.out.println("reconcileOn :"+ reconcileOn);
					if(reconcileOn!=null)
					{
					String reconcileComment =  reconComments.get(j);
					String ihId = instrumentHeaders.get(j);
					
					if(!ihId.contains("rm_rec~")){
						InstrumentHeader ih = instrumentHeaderService.reconcile(Long.parseLong(ihId),reconciledStatus ); 
					    instrumentOtherDetailsService.reconcileNew(reconcileOn, reconcileComment,  Long.parseLong(ihId),ih.getInstrumentAmount());
					}
					else{
						instrumentIdAndDateMap.put(ihId.split("rm_rec~")[1],reconcileOn);
						instrumentIdAndCommentMap.put(ihId.split("rm_rec~")[1],reconcileComment);
					}
					}
				}
			}else {
				Log.info("Recon Dates List Empty");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(!instrumentIdAndDateMap.isEmpty()){
		    List<Instrument> instruments = microserviceUtils.getInstruments(StringUtils.join(instrumentIdAndDateMap.keySet(),","));
		    FinancialStatus finStatus = new FinancialStatus();
		    finStatus.setCode("Reconciled");
		    finStatus.setName("Reconciled");
		    instruments.stream().forEach(ins-> {
		        ins.setReconciledOn(instrumentIdAndDateMap.get(ins.getId()));
		        ins.setReconciledComment(instrumentIdAndCommentMap.get(ins.getId()));
		    });
		    microserviceUtils.updateInstruments(instruments, null, finStatus);
		}
	}
    
    
    @Transactional
   	public void updateReceiptReconciledDetails(List<Date> reconDates, List<String> reconComments, List<String> instrumentHeaders) {
   		//int i=0;
   		EgwStatus reconciledStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(FinancialConstants.STATUS_MODULE_REMITTANCE, FinancialConstants.REMITTANCE_RECONCILED_STATUS);
   		Map<String,Date> instrumentIdAndDateMap = new HashMap<>();
   		Map<String,String> instrumentIdAndCommentMap = new HashMap<>();
   		int len = 0;
   		/*try {
   		for(Date reconcileOn:reconDates)
   		{
   			if(reconcileOn!=null)
   			{
   				String ihId = instrumentHeaders.get(i);
   				if(!ihId.contains("rm_rec~")){
   				    InstrumentHeader ih = instrumentHeaderService.reconcile(reconcileOn, Long.parseLong(ihId),reconciledStatus ); 
   				    instrumentOtherDetailsService.reconcile(reconcileOn,  Long.parseLong(ihId),ih.getInstrumentAmount());
   				}
   				else{
   				    instrumentIdAndDateMap.put(ihId.split("rm_rec~")[1],reconcileOn);
   				}
   			}
   			i++;
   		}
   		}catch(Exception e) {
   			e.printStackTrace();
   		}*/
   		//For Recon Comments
   		try {
   			len = reconDates.size();
   			if(len > 0) {
   				for(int j=0;j<len;j++) {
   					Date reconcileOn =  reconDates.get(j);
   					System.out.println("reconcileOn receipt :"+ reconcileOn);
					if(reconcileOn!=null)
					{
   					String reconcileComment =  reconComments.get(j);
   					String ihId = instrumentHeaders.get(j);
   					
   					if(!ihId.contains("rm_rec~")){
   						//InstrumentHeader ih = instrumentHeaderService.reconcile(Long.parseLong(ihId),reconciledStatus );
   						MisRemittanceDetails remittanceDetails = egwStatusHibernateDAO.getRemittanceDetails(Long.parseLong(ihId));
   						BigDecimal amount = remittanceDetails.getAmount();
   						receiptRemittanceDetailsService.receiptReconcileNew(reconcileOn, reconcileComment, Long.parseLong(ihId), amount);
   					}
   					else{
   						instrumentIdAndDateMap.put(ihId.split("rm_rec~")[1],reconcileOn);
   						instrumentIdAndCommentMap.put(ihId.split("rm_rec~")[1],reconcileComment);
   					}
					}
   				}
   			}else {
   				Log.info("Recon Dates List Empty");
   			}
   		}catch(Exception e) {
   			e.printStackTrace();
   		}
   		
		/*
		 * if(!instrumentIdAndDateMap.isEmpty()){ List<Instrument> instruments =
		 * microserviceUtils.getInstruments(StringUtils.join(instrumentIdAndDateMap.
		 * keySet(),",")); FinancialStatus finStatus = new FinancialStatus();
		 * finStatus.setCode("Reconciled"); finStatus.setName("Reconciled");
		 * instruments.stream().forEach(ins-> {
		 * ins.setReconciledOn(instrumentIdAndDateMap.get(ins.getId()));
		 * ins.setReconciledComment(instrumentIdAndCommentMap.get(ins.getId())); });
		 * microserviceUtils.updateInstruments(instruments, null, finStatus); }
		 */
   	}
    
    
    public List<String> getbankaccounts(ReconcileBean reconBean) 
	{
		List<String> list=new ArrayList<String>();
		String instrumentCondition="";		
		if("2".equalsIgnoreCase(reconBean.getPaymenttype())) {	
			StringBuffer query = null;
			try{	
			if(reconBean.getStatusType().equalsIgnoreCase("New")) {	
			     query=new StringBuffer().append("  select mr.bankaccount as bankaccount \r\n"
			     		+ "   FROM \r\n"
			     		+ "   VOUCHERHEADER v ,\r\n"
			     		+ "   VOUCHERMIS vm,\r\n"
			     		+ "   mis_receipts_details mis,\r\n"
			     		+ "   MIS_REMITTANCE_DETAILS mr  \r\n"
			     		+ "    WHERE \r\n"
			     		+ "    v.voucherdate  >= '"+reconBean.getFromDate()+"' AND \r\n"
			     		+ "    v.voucherdate  <= '"+reconBean.getReconciliationDate()+"' AND \r\n"
			     		+ "    v.ID= vm.voucherheaderid  and\r\n"
			     		+ "    v.STATUS not in (4,5) and\r\n"
			     		+ "    v.vouchernumber =mr.voucher_number\r\n"
			     		+ "   and mis.payment_status= 'DEPOSITED'\r\n"
			     		+ "   and (mr.status is null or mr.status = '') \r\n"
			     		+ "   group by\r\n"
			     		+ "   mr.bankaccount ");
			}
			if(reconBean.getStatusType().equalsIgnoreCase("Reconciled")) {				
				 query=new StringBuffer().append(" select mr.bankaccount as \"bankaccount\" \r\n"
				 		+ "   FROM \r\n"
				 		+ "   VOUCHERHEADER v ,\r\n"
				 		+ "   VOUCHERMIS vm,\r\n"
				 		+ "   mis_receipts_details mis,\r\n"
				 		+ "   MIS_REMITTANCE_DETAILS mr  \r\n"
				 		+ "    WHERE \r\n"
				 		+ "    v.voucherdate  >= '"+reconBean.getFromDate()+"' AND \r\n"
				 		+ "    v.voucherdate  <= '"+reconBean.getReconciliationDate()+"' AND \r\n"
				 		+ "    v.ID= vm.voucherheaderid  and\r\n"
				 		+ "    v.STATUS not in (4,5) and\r\n"
				 		+ "    v.vouchernumber =mr.voucher_number\r\n"
				 		+ "    and mr.status = 'Reconciled' \r\n"
				 		+ "   group by\r\n"
				 		+ "   mr.bankaccount ");				
			}	
			
			System.out.println("###query1:::"+query);
			
			LOGGER.info("from date : "+reconBean.getFromDate());
	        LOGGER.info("Reconciliation or to Date : "+reconBean.getReconciliationDate());
	        
			SQLQuery createSQLQuery = persistenceService.getSession().createSQLQuery(query.toString());
			createSQLQuery.addScalar("bankaccount",StringType.INSTANCE);
			//createSQLQuery.setResultTransformer(Transformers.aliasToBean(ReconcileBean.class));
			
		    list = (List<String>)createSQLQuery.list();
		        try {
		            //this.getUnreconsiledReceiptInstruments(reconBean,list);
	                } catch (Exception e) {
	                    LOGGER.error("ERROR occurred while fetching the unrconciled receipt instruments : "+e.getMessage());
	                }
			}
			catch(Exception e)
			{
				LOGGER.error("Exp in getUnReconciledCheques:"+e.getMessage());
				throw new ApplicationRuntimeException(e.getMessage());
			}
			
		}

		
		return list;
	}
    
    public String getaccount(ReconcileBean reconBean) 
	{
		String acc= "";	
		if("2".equalsIgnoreCase(reconBean.getPaymenttype())) {	
			StringBuffer query = null;
			try{	
			     query=new StringBuffer().append(" select accountnumber as accno from BANKACCOUNT where id=:bankAccId ");	
			
			System.out.println("###query1:::"+query);
			
			LOGGER.info("from date : "+reconBean.getFromDate());
	        LOGGER.info("Reconciliation or to Date : "+reconBean.getReconciliationDate());
	        
			SQLQuery createSQLQuery = persistenceService.getSession().createSQLQuery(query.toString());
			createSQLQuery.setLong("bankAccId", reconBean.getAccountId());
			createSQLQuery.addScalar("accno",StringType.INSTANCE);
			//createSQLQuery.setResultTransformer(Transformers.aliasToBean(ReconcileBean.class));
			
		    acc = (String)createSQLQuery.uniqueResult();
		        try {
		            //this.getUnreconsiledReceiptInstruments(reconBean,list);
	                } catch (Exception e) {
	                    LOGGER.error("ERROR occurred while fetching the unrconciled receipt instruments : "+e.getMessage());
	                }
			}
			catch(Exception e)
			{
				LOGGER.error("Exp in getUnReconciledCheques:"+e.getMessage());
				throw new ApplicationRuntimeException(e.getMessage());
			}
			
		}

		
		return acc;
	}
    
	
}
