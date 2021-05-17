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
package org.egov.lcms.transactions.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.lcms.reports.entity.LegalCaseSearchResult;
import org.egov.lcms.transactions.entity.ReportStatus;
import org.egov.lcms.transactions.repository.ReportStatusRepository;
import org.egov.lcms.utils.constants.LcmsConstants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchLegalCaseService {

    @Autowired
    private ReportStatusRepository reportStatusRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @PersistenceContext
    private EntityManager entityManager;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public List<LegalCaseSearchResult> getLegalCaseReport(final LegalCaseSearchResult legalCaseSearchResultObj) {
        final Boolean loggedInUserViewAccess = checkLoggedInUser(securityUtils.getCurrentUser());
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct legalObj  as  legalCase ,courtmaster.name  as  courtName ,");
        queryStr.append(" egwStatus.code  as  caseStatus ,");
        queryStr.append(" cb.concernedBranch  as  concernedBranch");
        queryStr.append(" from LegalCase legalObj,CourtMaster courtmaster,CaseTypeMaster casetypemaster,");
        queryStr.append(" PetitionTypeMaster petmaster,EgwStatus egwStatus,ReportStatus reportStatus Left JOIN legalObj.concernedBranch cb");
        queryStr.append(" LEFT JOIN   legalObj.judgment jt");
        queryStr.append(" where legalObj.courtMaster.id=courtmaster.id and  ");
        queryStr.append(
                " legalObj.caseTypeMaster.id=casetypemaster.id and legalObj.petitionTypeMaster.id=petmaster.id and ");
        queryStr.append(" legalObj.status.id=egwStatus.id and egwStatus.moduletype =:moduleType ");
        if (legalCaseSearchResultObj.getReportStatusId() != null)
            queryStr.append("  and legalObj.reportStatus.id = reportStatus.id ");

        getAppendQuery(legalCaseSearchResultObj, queryStr);
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult = setParametersToQuery(legalCaseSearchResultObj, queryResult);
        final List<LegalCaseSearchResult> legalcaseSearchList = queryResult.list();
        if (loggedInUserViewAccess)
            for (final LegalCaseSearchResult searchResults : legalcaseSearchList)
                searchResults.setLegalViewAccess(loggedInUserViewAccess);
        return legalcaseSearchList;

    }

    private Query setParametersToQuery(final LegalCaseSearchResult legalCaseSearchResultObj, final Query queryResult) {
        queryResult.setString("moduleType", LcmsConstants.MODULE_TYPE_LEGALCASE);
        if (StringUtils.isNotBlank(legalCaseSearchResultObj.getLcNumber()))
            queryResult.setString("lcNumber", legalCaseSearchResultObj.getLcNumber());
        if (StringUtils.isNotBlank(legalCaseSearchResultObj.getCaseNumber()))
            queryResult.setString("caseNumber", legalCaseSearchResultObj.getCaseNumber() + "%");
        if (legalCaseSearchResultObj.getCourtId() != null)
            queryResult.setInteger("court", legalCaseSearchResultObj.getCourtId());
        if (legalCaseSearchResultObj.getCasecategory() != null)
            queryResult.setInteger("casetype", legalCaseSearchResultObj.getCasecategory());
        if (legalCaseSearchResultObj.getCourtType() != null)
            queryResult.setInteger("courttype", legalCaseSearchResultObj.getCourtType());
        if (StringUtils.isNotBlank(legalCaseSearchResultObj.getStandingCouncil()))
            queryResult.setString("standingcoouncil", legalCaseSearchResultObj.getStandingCouncil() + "%");
        if (legalCaseSearchResultObj.getStatusId() != null)
            queryResult.setInteger("status", legalCaseSearchResultObj.getStatusId());

        if (legalCaseSearchResultObj.getCaseFromDate() != null)
            queryResult.setDate("fromdate", legalCaseSearchResultObj.getCaseFromDate());
        if (legalCaseSearchResultObj.getCaseToDate() != null)
            queryResult.setDate("toDate", legalCaseSearchResultObj.getCaseToDate());
        if (legalCaseSearchResultObj.getPetitionTypeId() != null)
            queryResult.setInteger("petiontionType", legalCaseSearchResultObj.getPetitionTypeId());
        if (legalCaseSearchResultObj.getReportStatusId() != null)
            queryResult.setInteger("reportStatus", legalCaseSearchResultObj.getReportStatusId());
        
        if (legalCaseSearchResultObj.getJudgmentTypeId() != null)
            queryResult.setInteger("judgmentid", legalCaseSearchResultObj.getJudgmentTypeId());
        
        if (legalCaseSearchResultObj.getIsStatusExcluded() != null) {
            final List<String> statusCodeList = new ArrayList<>();
            statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_CLOSED);
            statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_JUDGMENT_IMPLIMENTED);
            queryResult.setParameterList("statusCodeList", statusCodeList);
        }
        queryResult.setResultTransformer(new AliasToBeanResultTransformer(LegalCaseSearchResult.class));
        return queryResult;
    }

    private void getAppendQuery(final LegalCaseSearchResult legalCaseSearchResultOblj, final StringBuilder queryStr) {
        if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getLcNumber()))
            queryStr.append(" and legalObj.lcNumber =:lcNumber");
        if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getCaseNumber()))
            queryStr.append(" and legalObj.caseNumber like :caseNumber ");
        if (legalCaseSearchResultOblj.getCourtId() != null)
            queryStr.append(" and courtmaster.id =:court ");
        if (legalCaseSearchResultOblj.getCasecategory() != null)
            queryStr.append(" and casetypemaster.id =:casetype");
        if (legalCaseSearchResultOblj.getCourtType() != null)
            queryStr.append(" and courtmaster.id =:courttype ");
        if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getStandingCouncil()))
            queryStr.append(" and legalObj.oppPartyAdvocate like :standingcoouncil ");
        if (legalCaseSearchResultOblj.getStatusId() != null)
            queryStr.append(" and egwStatus.id =:status ");
        if (legalCaseSearchResultOblj.getCaseFromDate() != null)
            queryStr.append(" and legalObj.caseDate >=:fromdate ");
        if (legalCaseSearchResultOblj.getCaseToDate() != null)
            queryStr.append(" and legalObj.caseDate <=:toDate ");
        if (legalCaseSearchResultOblj.getPetitionTypeId() != null)
            queryStr.append(" and petmaster.id =:petiontionType ");
        if (legalCaseSearchResultOblj.getIsStatusExcluded() != null)
            queryStr.append(" and egwStatus.code not in (:statusCodeList ) ");
        if (legalCaseSearchResultOblj.getReportStatusId() != null)
            queryStr.append(" and reportStatus.id =:reportStatus ");
        if (legalCaseSearchResultOblj.getJudgmentTypeId() != null)
        {
            queryStr.append(" and jt.judgmentType.id =:judgmentid ");
        }
        else
        {
        	queryStr.append(" and legalObj.id not in (select ej.legalCase.id from Judgment ej where ej.judgmentType.name='Decided') ");
        }
		
		if(legalCaseSearchResultOblj.getIscaseImp()!=null)
			queryStr.append("and legalObj.caseImportant='Yes'");
    }

    public List<ReportStatus> getReportStatus() {
        return reportStatusRepository.findAll();
    }

    public Boolean checkLoggedInUser(final User user) {
        for (final Role role : user.getRoles())
            if (role != null && role.getName().equalsIgnoreCase(LcmsConstants.LCMS_VIEW_ACCESS_ROLE))
                return true;
        return false;
    }

    public byte[] getSearchLegalCaseExcelSheet(Map<String,String>headerData,	List<LegalCaseSearchResult> legalcaseSearchList) {		
		byte[]fileContent=null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Legal Case Report");
			sheet.getPrintSetup().setLandscape(true);
			sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A5_PAPERSIZE); 
			HSSFCellStyle style = wb.createCellStyle();  
			HSSFFont font = wb.createFont();
			 font.setFontHeightInPoints((short)11);  
	         font.setFontName("Times New Roman");  
	         //font.setBoldweight((short)10);
	        style.setFont(font);  
			int i =0;

			Row row1 = sheet.createRow(i++);	  
			Cell c1=  row1.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(headerData.get("h1"));
			
			
			Cell c2=  row1.createCell(1);
			c2.setCellStyle(style);
			c2.setCellValue(headerData.get("h2"));

			
			Cell c3=  row1.createCell(2);
			c3.setCellStyle(style);
			c3.setCellValue(headerData.get("h3"));

			Cell c4=  row1.createCell(3);
			c4.setCellStyle(style);
			c4.setCellValue(headerData.get("h4"));
			
			Cell c5=  row1.createCell(4);
			c5.setCellStyle(style);
			c5.setCellValue(headerData.get("h5"));
			
			Cell c6=  row1.createCell(5);
			c6.setCellStyle(style);
			c6.setCellValue(headerData.get("h6"));
			
			Cell c7=  row1.createCell(6);
			c7.setCellStyle(style);
			c7.setCellValue(headerData.get("h7"));

			Cell c8=  row1.createCell(7);
			c8.setCellStyle(style);
			c8.setCellValue(headerData.get("h8"));

			Cell c9=  row1.createCell(8);
			c9.setCellStyle(style);
			c9.setCellValue(headerData.get("h9"));
					
			for(LegalCaseSearchResult s : legalcaseSearchList) {
				String casenumber="";
				String casetitle="";
				String concernedBranch="";
				String courtname="";
				String standingcouncil="";
				String legalcaseno="";
				String petitioners="";
				String respondants="";
				String statusDesc="";
				
				 Row row = sheet.createRow(i++);
				 	Cell cell0 = row.createCell(0);
					Cell cell1 = row.createCell(1);
					Cell cell2= row.createCell(2);
					Cell cell3= row.createCell(3);
					Cell cell4= row.createCell(4);
					Cell cell5= row.createCell(5);
					Cell cell6= row.createCell(6);
					Cell cell7= row.createCell(7);
					Cell cell8= row.createCell(8);
						
					if(s.getLegalCase().getLcNumber()!=null) {
						legalcaseno = s.getLegalCase().getLcNumber();
					}
					if(s.getLegalCase().getCaseNumber()!=null) {
						casenumber= s.getLegalCase().getCaseNumber();
					}
					if(s.getLegalCase().getCaseTitle()!=null) {
						casetitle=s.getLegalCase().getCaseTitle();
					}
					
					if(s.getCourtName()!=null) {
						courtname=s.getCourtName();
					}
					if(s.getLegalCase().getOppPartyAdvocate()!=null) {
						standingcouncil=s.getLegalCase().getOppPartyAdvocate();
					}
					if(s.getLegalCase().getStatus().getDescription()!=null) {
						statusDesc=s.getLegalCase().getStatus().getDescription();
					}
					if(s.getLegalCase().getPetitionersNames()!=null) {
						petitioners=s.getLegalCase().getPetitionersNames();
					}
					
					if(s.getLegalCase().getRespondantNames()!=null) {
						respondants=s.getLegalCase().getRespondantNames();
					}
					if(s.getConcernedBranch()!=null) {
						concernedBranch=s.getConcernedBranch();
					}
					
					cell0.setCellValue(legalcaseno);
					cell1.setCellValue(casenumber);
					cell2.setCellValue(casetitle);
					cell3.setCellValue(courtname);
					cell4.setCellValue(standingcouncil);
					cell5.setCellValue(statusDesc);
					cell6.setCellValue(petitioners);
					cell7.setCellValue(respondants);
					cell8.setCellValue(concernedBranch);
		        } 
			int numberOfSheets = wb.getNumberOfSheets();
		    for (int x = 0; x < numberOfSheets; x++) {
		        Sheet sheet1 = wb.getSheetAt(x);
		        if (sheet1.getPhysicalNumberOfRows() > 0) {
		            Row row = sheet1.getRow(sheet1.getFirstRowNum());
		            Iterator<Cell> cellIterator = row.cellIterator();
		            while (cellIterator.hasNext()) {
		                Cell cell = cellIterator.next();
		                int columnIndex = cell.getColumnIndex();
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


	public List<LegalCaseSearchResult> getLegalCaseReportRestData(final LegalCaseSearchResult legalCaseSearchResultObj) {
        final Boolean loggedInUserViewAccess = false;
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct legalObj  as  legalCase ,courtmaster.name  as  courtName ,");
        queryStr.append(" egwStatus.code  as  caseStatus ,");
        queryStr.append(" cb.concernedBranch  as  concernedBranch");
        queryStr.append(" from LegalCase legalObj,CourtMaster courtmaster,CaseTypeMaster casetypemaster,");
        queryStr.append(" PetitionTypeMaster petmaster,EgwStatus egwStatus,ReportStatus reportStatus Left JOIN legalObj.concernedBranch cb");
        queryStr.append(" LEFT JOIN   legalObj.judgment jt");
        queryStr.append(" where legalObj.courtMaster.id=courtmaster.id and  ");
        queryStr.append(
                " legalObj.caseTypeMaster.id=casetypemaster.id and legalObj.petitionTypeMaster.id=petmaster.id and ");
        queryStr.append(" legalObj.status.id=egwStatus.id and egwStatus.moduletype =:moduleType ");
        if (legalCaseSearchResultObj.getReportStatusId() != null)
            queryStr.append("  and legalObj.reportStatus.id = reportStatus.id ");

        getAppendQuery(legalCaseSearchResultObj, queryStr);
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult = setParametersToQuery(legalCaseSearchResultObj, queryResult);
        final List<LegalCaseSearchResult> legalcaseSearchList = queryResult.list();
        if (loggedInUserViewAccess)
            for (final LegalCaseSearchResult searchResults : legalcaseSearchList)
                searchResults.setLegalViewAccess(loggedInUserViewAccess);
        return legalcaseSearchList;

    }
	
	
	
}
