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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.Bank;
import org.egov.commons.Bankaccount;
import org.egov.commons.Bankbranch;
import org.egov.commons.Bankreconciliation;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.BankHibernateDAO;
import org.egov.egf.model.ReconcileBean;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.DateValidateByUser;
import org.egov.infra.microservice.models.DateValidations;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.microservice.models.UserInfo;
import org.egov.infra.utils.ApplicationConstant;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;

@ParentPackage("egov")
@Results({
	@Result(name = ManualReconciliationAction.NEW, location = "manualReconciliation-" + ManualReconciliationAction.NEW + ".jsp"),
	@Result(name = "search", location = "manualReconciliation-" + "search" + ".jsp"),
	@Result(name = "report", location = "manualReconciliation-" + "report" + ".jsp"),
	@Result(name = "update", location = "manualReconciliation-update.jsp"),
	@Result(name = "balance", location = "manualReconciliation-balance.jsp"),
	@Result(name = "PDF", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
			"application/pdf", "contentDisposition", "no-cache;filename=AutoReconcileReport.pdf" }),
			@Result(name = "XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream", "contentType",
					"application/xls", "contentDisposition", "no-cache;filename=AutoReconcileReport.xls" })
})
public class ManualReconciliationAction extends BaseFormAction {


	private static final long serialVersionUID = -4207341983597707193L;
	private List<Bankbranch> branchList = Collections.EMPTY_LIST;
	private final List<Bankaccount> accountList = Collections.EMPTY_LIST;
	private List<EgwStatus> statusTypeList = new ArrayList<EgwStatus>();
	
	@Autowired
	protected EgovMasterDataCaching masterDataCache;
	/* @Autowired
    private ReconcileService reconcileService;*/

	public List<EgwStatus> getStatusTypeList() {
		return statusTypeList;
	}

	public void setStatusTypeList(List<EgwStatus> statusTypeList) {
		this.statusTypeList = statusTypeList;
	}

	@Autowired
	private ManualReconcileHelper manualReconcileHelper;

	private ReconcileBean reconcileBean;
	private Map<String,String> unReconciledDrCr;
	private List<ReconcileBean> unReconciledCheques;
	List<String> instrumentHeaders;
	List<Date> reconDates;
	List<String> reconComments;
	@Autowired
	private BankHibernateDAO bankHibernateDAO;
        private Integer DEFAULT_LIMIT = 100;

	@Override
	public Object getModel() {
		return new Bankreconciliation();
	}

	@SuppressWarnings("unchecked")
	public void prepareNewForm()
	{

		reconcileBean=new ReconcileBean();
		reconcileBean.setLimit(DEFAULT_LIMIT );

		
		EgwStatus status2=new EgwStatus();
		
		status2.setCode("New");
		status2.setDescription("Unreconciled");
		EgwStatus status3=new EgwStatus();
		
		status3.setCode("Reconciled");
		status3.setDescription("Reconciled");
		statusTypeList.clear();
		
		statusTypeList.add(status2);
		statusTypeList.add(status3);
		
		List<Bank> allBankHavingAccounts = bankHibernateDAO.getAllBankHavingBranchAndAccounts(); 
		dropdownData.put("bankList", allBankHavingAccounts);  
		dropdownData.put("branchList", branchList);
		dropdownData.put("accountList", accountList);
		dropdownData.put("statusTypeList", statusTypeList);
		dropdownData.put("departmentList", masterDataCache.get("egi-department"));
		if (reconcileBean.getBranchId() != null)
		{
			branchList = persistenceService
					.findAllBy(
							"select  bb from Bankbranch bb,Bankaccount ba where bb.bank.id=? and ba.bankbranch=bb and bb.isactive=true",
							reconcileBean.getBankId());
			dropdownData.put("branchList", branchList);

		}
		if (reconcileBean.getAccountId() != null)
		{
			final List<Bankaccount> accountList = getPersistenceService().findAllBy(
					"from Bankaccount ba where ba.bankbranch.id=? and isactive=true order by ba.chartofaccounts.glcode", reconcileBean.getBranchId());
			dropdownData.put("accountList", accountList);
		}

	}


	@Action(value = "/brs/manualReconciliation-newForm")
	public String newForm()
	{

		return NEW;
	}

	@Action(value = "/brs/manualReconciliation-ajaxSearch")
	public String search()
	{
		
		RequestInfo requestInfo = new RequestInfo();
        requestInfo.setAuthToken(microserviceUtils.getUserToken());
        requestInfo.setUserInfo(microserviceUtils.getUserInfo());
        requestInfo.getUserInfo().setId(ApplicationThreadLocals.getUserId());
        
        UserInfo userInfo = requestInfo.getUserInfo();
        String loginuserName = userInfo.getUserName();
        
     		
		List<DateValidations> billregisterDateValidate = microserviceUtils.financeDateValidate();
		
		for (DateValidations dateValidations : billregisterDateValidate) {
			String code = dateValidations.getCode();
			if(ApplicationConstant.MANUAL_BANK_RECONCILATION_DATE_VALIDATION.equals(code)) {					
			List<DateValidateByUser> dateValidateByUser = dateValidations.getDateValidateByUser();
			for (DateValidateByUser dateValidations2 : dateValidateByUser) {
				String username = dateValidations2.getUsername();
				if(loginuserName.equals(username)){
				Integer validDay = dateValidations2.getValidDay();
				
				SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
		        
		        // Convert date to string
		        String fromdatstr = dateformatter.format(reconcileBean.getFromDate());
		        String todatstr = dateformatter.format(reconcileBean.getToDate());
		        String fromDateStr = fromdatstr;  // Starting date
		        String toDateStr = todatstr;    // Ending date

		         long thresholdDays = validDay;

		         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		         LocalDate fromDate = LocalDate.parse(fromDateStr, formatter);
		         LocalDate toDate = LocalDate.parse(toDateStr, formatter);

		         long daysBetween = ChronoUnit.DAYS.between(fromDate, toDate)+ 1;

		         System.out.println("Days between " + fromDate + " and " + toDate + ": " + daysBetween);

		         if (daysBetween > thresholdDays) {
		        	 System.out.println("Dates exceeds " + thresholdDays + " days.");
		             addActionError("Dates exceeds " + thresholdDays + " days.");  
		             return "search";
		         } else {
		        	 System.out.println("Dates is within " + thresholdDays + " days.");
		         }
				
			}
			}
			break;
			}		
		}
		
        String account2= "";
		
		if("2".equalsIgnoreCase(reconcileBean.getPaymenttype())) {
		List<String> getbankaccounts = manualReconcileHelper.getbankaccounts(reconcileBean);
		
		String getaccount = manualReconcileHelper.getaccount(reconcileBean);
		
		for (String account : getbankaccounts) {
			String[] split = account.split("-");
			//String string0= split[0];
			//String string1= split[1];
			String string2= split[2];
			
			if(string2.equals(getaccount)) {
				account2 = account;
			}
		}
		}
		
		unReconciledCheques = manualReconcileHelper.getUnReconciledCheques(reconcileBean,account2);
		if("1".equalsIgnoreCase(reconcileBean.getPaymenttype())) {
		Collections.sort(unReconciledCheques, new Comparator<ReconcileBean>() {
		    public int compare(ReconcileBean m1, ReconcileBean m2) {
		        return (getDate(m1.getChequeDate())).compareTo(getDate(m2.getChequeDate()));
		    }
		});
		}
		List<ReconcileBean> unReconciledChequesCopy=new ArrayList<ReconcileBean>();
		unReconciledChequesCopy.addAll(unReconciledCheques);
		if(unReconciledChequesCopy != null && !unReconciledChequesCopy.isEmpty())
		{
			int i=1;
			int limit=reconcileBean.getLimit();
			System.out.println("Size ::"+unReconciledChequesCopy.size());
			System.out.println("limit :::"+limit);
			unReconciledCheques.clear();
			for(ReconcileBean bean:unReconciledChequesCopy)
			{
				if(i > limit)
				{
					break;
				}
				else
				{
					unReconciledCheques.add(bean);
					i++;
				}
			}
			
		}
		return "search";
	}

	@Action(value = "/brs/manualReconciliation-ajaxBalance")
	public String balance()
	{
		
		String account2= "";
			
		List<String> getbankaccounts = manualReconcileHelper.getbankaccountsForBalance(reconcileBean);
			
		String getaccount = manualReconcileHelper.getaccountForBalance(reconcileBean);
		
		for (String account : getbankaccounts) {
			String[] split = account.split("-");
			String string2= split[2];				
			if(string2.equals(getaccount)) {
				account2 = account;
			}
		}
		
		unReconciledDrCr = manualReconcileHelper.getUnReconciledDrCr(reconcileBean.getAccountId(), reconcileBean.getFromDate(), reconcileBean.getToDate(),account2,reconcileBean.getDepartment());

		return "balance";
	}
	
	@Action(value = "/brs/manualReconciliation-update")
	@ValidationErrorPage("search")
	public String update()
	{
		//Log.info("Recon Comments..:"+reconComments.toString());
		manualReconcileHelper.update(reconDates, reconComments, instrumentHeaders);
		return "update";
	}
	
	@Action(value = "/brs/manualReconciliationreceipt-update")
	@ValidationErrorPage("search")
	public String updateReconciliationReceipt()
	{
		//Log.info("Recon Comments..:"+reconComments.toString());
		manualReconcileHelper.updateReceiptReconciledDetails(reconDates, reconComments, instrumentHeaders);
		return "update";
	}

	@Action(value = "/brs/manualReconciliation-generateReport")
	@SuppressWarnings({ "unchecked", "deprecation" })
	public String generateReport() {

		return "report";

	}

	public List<Bankbranch> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<Bankbranch> branchList) {
		this.branchList = branchList;
	}

	public ReconcileBean getReconcileBean() {
		return reconcileBean;
	}

	public void setReconcileBean(ReconcileBean reconcileBean) {
		this.reconcileBean = reconcileBean;
	}

	public List<Bankaccount> getAccountList() {
		return accountList;
	}

	

	public List<ReconcileBean> getUnReconciledCheques() {
		return unReconciledCheques;
	}

	public void setUnReconciledCheques(List<ReconcileBean> unReconciledCheques) {
		this.unReconciledCheques = unReconciledCheques;
	}

	public List<String> getInstrumentHeaders() {
		return instrumentHeaders;
	}

	public void setInstrumentHeaders(List<String> instrumentHeaders) {
		this.instrumentHeaders = instrumentHeaders;
	}

	public List<Date> getReconDates() {
		return reconDates;
	}

	public void setReconDates(List<Date> reconDates) {
		this.reconDates = reconDates;
	}

	public Map<String, String> getUnReconciledDrCr() {
		return unReconciledDrCr;
	}

	public void setUnReconciledDrCr(Map<String, String> unReconciledDrCr) {
		this.unReconciledDrCr = unReconciledDrCr;
	}

	private  Date getDate(String date)  {  
		Date result=null;
	    try {
	    	result= new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return result;
	}

	public List<String> getReconComments() {
		return reconComments;
	}

	public void setReconComments(List<String> reconComments) {
		this.reconComments = reconComments;
	} 

}