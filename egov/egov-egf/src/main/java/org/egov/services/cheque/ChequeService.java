/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency, 
 *    accountability and the service delivery of the government  organizations.
 * 
 *     Copyright (C) <2015>  eGovernments Foundation
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
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
/**
 * 
 */
package org.egov.services.cheque;

import java.util.Arrays;
import java.util.List;

import org.egov.commons.Bankaccount;
import org.egov.infstr.services.PersistenceService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.model.cheque.AccountCheques;

/**
 * @author mani
 *
 */
public class ChequeService extends PersistenceService<AccountCheques, Long> {
	/**
	 * 
	 */
	private static final String	REQUIRED_NUMBER_OF_CHEQUES_ARE_NOT_AVAILABLE	= "Required number of cheques are not available";
	@SuppressWarnings("unchecked")
	private  PersistenceService persistenceService;
	/**
	 * @param persistenceService the persistenceService to set
	 */
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
	public ChequeService()
	{
		setType(AccountCheques.class);
	}
	/**
	 * 
	 * @param accId
	 * @param noChqs
	 * @param allotId
	 * @return String containing cheque numbers of requested count seperated by comma
	 * 
	 */
	public String nextChequeNumber(String accId,int noChqs,int allotId)
	{  
		String nextChequeNumber="";
		int count = 1;
		int i=0;
		Bankaccount bankaccount = getBankaccount(accId);
		Department department = getDepartment(allotId);
		
		  String chqQuery="select ac from AccountCheques ac, ChequeDeptMapping cd where ac.id=cd.accountCheque.id and ac.bankAccountId=? and cd.allotedTo=?  and (ac.isExhausted is null or ac.isExhausted=0)  order by ac.id";
		  List<AccountCheques> chqList = findAllBy(chqQuery,bankaccount,department);
		  if(chqList==null || chqList.size()==0)
		  {
			  throw new ValidationException(Arrays.asList(new ValidationError("No cheques available","No cheques available")));
		  }
		  AccountCheques chq=(AccountCheques)chqList.get(i);
		  while (count<=noChqs)
		  { 
			 
			  if(i<chqList.size())
			  { 
				 
				  if(chq.getNextChqNo()==null)                       // this book not yet used
				  { 
					  // chq.setNextChqNo(chq.getFromChequeNumber());
					  nextChequeNumber+=addComma(nextChequeNumber)+chq.getFromChequeNumber();
					  chq.setNextChqNo(increment(chq.getFromChequeNumber()));
				  }
				  else if(chq.getNextChqNo().equals(chq.getToChequeNumber())) //this is last leaf in the cheque
				  {
					  nextChequeNumber+=addComma(nextChequeNumber)+chq.getToChequeNumber();
					  chq.setIsExhausted(true);
					  i++;
					  if(count==noChqs)
					  {
						  count++;
						  continue; //means with last leaf we got enough cheques so no need fetch next 
						  
					  }
					  if(i<chqList.size())
					  {
					  chq=(AccountCheques)chqList.get(i);
					  }else
					  {  
						  throw new ApplicationRuntimeException(REQUIRED_NUMBER_OF_CHEQUES_ARE_NOT_AVAILABLE);  
					  }
					  
				  }
				  else
				  {
					  nextChequeNumber+=addComma(nextChequeNumber)+chq.getNextChqNo();
					  chq.setNextChqNo(increment(chq.getNextChqNo()));
				  }
				  count++;
			  }
			  else if(i<noChqs)
			  {
				  throw new ApplicationRuntimeException(REQUIRED_NUMBER_OF_CHEQUES_ARE_NOT_AVAILABLE);
			  }
				  
			  
		  }
		
		return nextChequeNumber;
		
	}
	
	
	/**
	 * 
	 * @param nextChequeNumber
	 * @return "," OR "" String
	 */
	private String addComma(String nextChequeNumber) {
		if(nextChequeNumber.equals(""))
		{
			return "";
		}
		else
		{
			return ",";
		}
	}
	private String increment(String fromChequeNumber) {
		
		StringBuffer temp=new StringBuffer(fromChequeNumber);
	    StringBuffer numBuff=new StringBuffer();
		Long num=Long.valueOf(fromChequeNumber);
		num+=1;
		numBuff=new StringBuffer(num.toString());
		while(numBuff.length()<temp.length())
		{
			numBuff.insert(0, "0");
		}
		return numBuff.toString();
	}
	private Bankaccount getBankaccount(String accId) {
		Bankaccount account=(Bankaccount)persistenceService.find("from Bankaccount where id=? ",Integer.valueOf(accId));
		if(account==null)
		{
			throw new IllegalArgumentException("Bankaccount doesnot exist in the system for id:"+accId);
		}
		else
		{
			return account;
		}
	}
	private Department getDepartment(int allotId) {
		Department dept=(Department)persistenceService.find("from Department where id=? ",allotId);
		if(dept==null)
		{
			throw new IllegalArgumentException("Department doesnot exist in the system for id:"+allotId);
		}
		else
		{
			return dept;
		}
	}

}
