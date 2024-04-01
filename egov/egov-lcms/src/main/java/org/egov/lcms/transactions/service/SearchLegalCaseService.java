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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.egov.infstr.services.PersistenceService;
import org.egov.lcms.reports.entity.LegalCaseSearchResult;
import org.egov.lcms.transactions.entity.ReportStatus;
import org.egov.lcms.transactions.repository.ReportStatusRepository;
import org.egov.lcms.utils.constants.LcmsConstants;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchLegalCaseService {

	@Autowired
	private ReportStatusRepository reportStatusRepository;

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@PersistenceContext
	private EntityManager entityManager;
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public List<LegalCaseSearchResult> getLegalCaseReport(final LegalCaseSearchResult legalCaseSearchResultObj) {
		final Boolean loggedInUserViewAccess = checkLoggedInUser(securityUtils.getCurrentUser());
		final StringBuilder queryStr = new StringBuilder();
		queryStr.append("select distinct legalObj  as  legalCase ,courtmaster.courtType  as  courtName ,");
		queryStr.append(" egwStatus.code  as  caseStatus ,");
		queryStr.append(" bidefcounsil.oppPartyAdvocate  as  standingCouncil ,");
		queryStr.append(" hear.hearingDate  as  hearingDate , hear.hearingOutcome  as  hearingOutcome, ");
		queryStr.append(
				" cb.concernedBranch  as  concernedBranch, jt.replySubmit as replySubmit, jt.argument as argument");
		queryStr.append(" from LegalCase legalObj,CourtTypeMaster courtmaster, Judgment jt,");
		queryStr.append(
				" PetitionTypeMaster petmaster,EgwStatus egwStatus,ReportStatus reportStatus Left JOIN legalObj.concernedBranch cb");
		queryStr.append(
				" LEFT JOIN BidefendingCounsilDetails bidefcounsil ON legalObj.id =bidefcounsil.legalCase and bidefcounsil.defCounsilPrimary ='YES'");
		queryStr.append(
				" LEFT JOIN Hearings hear on legalObj.id=hear.legalCase and hear.hearingDate =(select max(hear2.hearingDate) from Hearings hear2 where legalObj.id=hear2.legalCase) ");
		queryStr.append(" LEFT JOIN   legalObj.judgment jt");
		queryStr.append(" where legalObj.courtTypeMaster.id=courtmaster.id and  ");
		queryStr.append(" legalObj.petitionTypeMaster.id=petmaster.id and ");
		queryStr.append(" legalObj.status.id=egwStatus.id and egwStatus.moduletype =:moduleType ");
		if (legalCaseSearchResultObj.getReportStatusId() != null)
			queryStr.append("  and legalObj.reportStatus.id = reportStatus.id ");

		getAppendQuery(legalCaseSearchResultObj, queryStr);
		Query queryResult = getCurrentSession().createQuery(queryStr.toString());
		queryResult = setParametersToQuery(legalCaseSearchResultObj, queryResult);
		System.out.println("query:: " + queryResult.toString());
		System.out.println("final quer " + queryStr);
		final List<LegalCaseSearchResult> legalcaseSearchList = queryResult.list();

		System.out.println("sizeeee " + legalcaseSearchList.size());

		if (loggedInUserViewAccess)
			for (final LegalCaseSearchResult searchResults : legalcaseSearchList)
				searchResults.setLegalViewAccess(loggedInUserViewAccess);

		return legalcaseSearchList;

	}

	public List<String> getLegalCaseData(final LegalCaseSearchResult legalCaseSearchResultObj) {
		final Boolean loggedInUserViewAccess = checkLoggedInUser(securityUtils.getCurrentUser());
		final StringBuilder queryStr = new StringBuilder();
		queryStr.append("SELECT DISTINCT\r\n"
				+ "    CONCAT_WS('  |  ', ROW_NUMBER() OVER (ORDER BY lc.id),\r\n"
				+ "              CONCAT('Hearing Date :', h.hearingDate),\r\n"
				+ "              CONCAT('Case No :', lc.casenumber),\r\n"
				+ "              CONCAT('Title of Case :', lc.casetitle),\r\n"
				+ "              CONCAT('Court Name :', ct.courttype),\r\n"
				+ "              CONCAT('Defending Counsel :', COALESCE(b.opppartyadvocate, ' - ')),\r\n"
				+ "              CONCAT('Branch :', cb.concernedBranch)) AS hearingdate\r\n"
				+ "FROM\r\n"
				+ "    EGLC_LEGALCASE lc\r\n"
				+ "LEFT OUTER JOIN\r\n"
				+ "    eglc_concerned_branch_master cb ON lc.concernedbranch = cb.id\r\n"
				+ "LEFT OUTER JOIN\r\n"
				+ "    EGLC_HEARINGS h ON lc.id = h.legalcase AND h.hearingDate = (\r\n"
				+ "        SELECT MAX(h2.hearingDate)\r\n"
				+ "        FROM EGLC_HEARINGS h2\r\n"
				+ "        WHERE lc.id = h2.legalcase\r\n"
				+ "    )\r\n"
				+ "LEFT OUTER JOIN\r\n"
				+ "    EGLC_COURTTYPE_MASTER ct ON lc.COURT = ct.id\r\n"
				+ "LEFT OUTER JOIN\r\n"
				+ "    EGLC_BIDEFENDINGCOUNSILDETAILS b ON lc.id = b.legalcase AND b.defCounsilPrimary = 'YES'\r\n"
				+ "LEFT OUTER JOIN\r\n"
				+ "    EGLC_LEGALCASE lc2 ON b.legalcase = lc2.id\r\n"
				+ "WHERE\r\n"
				+ "    lc.STATUS IN (\r\n"
				+ "        SELECT ID\r\n"
				+ "        FROM EGW_STATUS\r\n"
				+ "        WHERE MODULETYPE = 'Legal Case'\r\n"
				+ "    )\r\n"
				+ "AND\r\n"
				+ "    lc.id NOT IN (\r\n"
				+ "        SELECT j.legalcase\r\n"
				+ "        FROM EGLC_JUDGMENT j\r\n"
				+ "        INNER JOIN EGLC_JUDGMENTTYPE_MASTER jt ON j.judgmenttype = jt.id\r\n"
				+ "        WHERE jt.judgmenttype = 'Decided'\r\n"
				+ "    )\r\n"
				+ "AND\r\n"
				+ "    h.hearingDate IS NOT NULL\r\n"
				+ "AND\r\n"
				+ "    h.hearingDate BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 day'\r\n"
				+ "ORDER BY\r\n"
				+ "    hearingdate ASC");
		/*
		 * queryStr.append(" egwStatus.code  as  caseStatus ,");
		 * queryStr.append(" bidefcounsil.oppPartyAdvocate  as  standingCouncil ,");
		 * queryStr.
		 * append(" hear.hearingDate  as  hearingDate , hear.hearingOutcome  as  hearingOutcome, "
		 * ); queryStr.append(
		 * " cb.concernedBranch  as  concernedBranch, jt.replySubmit as replySubmit, jt.argument as argument"
		 * ); queryStr.
		 * append(" from LegalCase legalObj,CourtMaster courtmaster,CaseTypeMaster casetypemaster, Judgment jt,"
		 * ); queryStr.append(
		 * " PetitionTypeMaster petmaster,EgwStatus egwStatus,ReportStatus reportStatus Left JOIN legalObj.concernedBranch cb"
		 * ); queryStr.append(
		 * " LEFT JOIN BidefendingCounsilDetails bidefcounsil ON legalObj.id =bidefcounsil.legalCase and bidefcounsil.defCounsilPrimary ='YES'"
		 * ); queryStr.append(
		 * " LEFT JOIN Hearings hear on legalObj.id=hear.legalCase and hear.hearingDate =(select max(hear2.hearingDate) from Hearings hear2 where legalObj.id=hear2.legalCase) "
		 * ); queryStr.append(" LEFT JOIN   legalObj.judgment jt");
		 * queryStr.append(" where legalObj.courtMaster.id=courtmaster.id and  ");
		 * queryStr.append(
		 * " legalObj.caseTypeMaster.id=casetypemaster.id and legalObj.petitionTypeMaster.id=petmaster.id and "
		 * ); queryStr.
		 * append(" legalObj.status.id=egwStatus.id and egwStatus.moduletype =:moduleType "
		 * );
		 */
		/*
		 * if (legalCaseSearchResultObj.getReportStatusId() != null)
		 * queryStr.append("  and legalObj.reportStatus.id = reportStatus.id ");
		 */

		//getAppendQuery(legalCaseSearchResultObj, queryStr);
		SQLQuery queryResult = persistenceService.getSession().createSQLQuery(queryStr.toString());
		//queryResult.setResultTransformer(Transformers.aliasToBean(LegalCaseSearchResult.class));
		//queryResult = setParametersToQuery(legalCaseSearchResultObj, queryResult);
		System.out.println("query:: " + queryResult.toString());
		System.out.println("final query " + queryStr);
		//final List<LegalCaseSearchResult> legalcaseSearchList = (List<LegalCaseSearchResult>)queryResult.list();
		final List<String> legalcaseSearchList = queryResult.list();

		System.out.println("sizeeee " + legalcaseSearchList.size());

		/*
		 * if (loggedInUserViewAccess) for (final LegalCaseSearchResult searchResults :
		 * legalcaseSearchList)
		 * searchResults.setLegalViewAccess(loggedInUserViewAccess);
		 */

		return legalcaseSearchList;

	}
	
	private Query setParametersToQuery(final LegalCaseSearchResult legalCaseSearchResultObj, final Query queryResult) {
		queryResult.setString("moduleType", LcmsConstants.MODULE_TYPE_LEGALCASE);
		if (StringUtils.isNotBlank(legalCaseSearchResultObj.getLcNumber()))
			queryResult.setString("lcNumber", legalCaseSearchResultObj.getLcNumber());
		if (StringUtils.isNotBlank(legalCaseSearchResultObj.getCaseNumber()))
			queryResult.setString("caseNumber", legalCaseSearchResultObj.getCaseNumber() + "%");
		/*
		 * if (legalCaseSearchResultObj.getCourtId() != null)
		 * queryResult.setInteger("court", legalCaseSearchResultObj.getCourtId()); if
		 * (legalCaseSearchResultObj.getCasecategory() != null)
		 * queryResult.setInteger("casetype",
		 * legalCaseSearchResultObj.getCasecategory());
		 */
		if (legalCaseSearchResultObj.getCourtType() != null)
			queryResult.setInteger("courttype", legalCaseSearchResultObj.getCourtType());
		if (StringUtils.isNotBlank(legalCaseSearchResultObj.getStandingCouncil()))
			queryResult.setString("standingcoouncil",
					legalCaseSearchResultObj.getStandingCouncil().toLowerCase() + "%");
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
		if (legalCaseSearchResultObj.getConcernedBranchId() != null)
			queryResult.setInteger("branchid", legalCaseSearchResultObj.getConcernedBranchId());

		final List<String> statusCodeList = new ArrayList<>();

		if (legalCaseSearchResultObj.getIsStatusExcluded() != null) {
			statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_CLOSED);
			statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_JUDGMENT_IMPLIMENTED);
			queryResult.setParameterList("statusCodeList", statusCodeList);
		}

		if (legalCaseSearchResultObj.getIsJudgementDone() != null) {
			statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_CREATED);
			statusCodeList.add(LcmsConstants.LEGALCASE_HEARING_STATUS);
			statusCodeList.add(LcmsConstants.LEGALCASE_INTERIMSTAY_STATUS);
			queryResult.setParameterList("statusCodeList", statusCodeList);
		}

		if (legalCaseSearchResultObj.getIsJudgementPending() != null) {

			statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_CLOSED);
			statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_JUDGMENT);
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
		/*
		 * if (legalCaseSearchResultOblj.getCourtId() != null)
		 * queryStr.append(" and courtmaster.id =:court "); if
		 * (legalCaseSearchResultOblj.getCasecategory() != null)
		 * queryStr.append(" and casetypemaster.id =:casetype");
		 */
		if (legalCaseSearchResultOblj.getCourtType() != null)
			queryStr.append(" and courtmaster.id =:courttype ");
		/*
		 * if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getStandingCouncil()))
		 * queryStr.append(" and legalObj.oppPartyAdvocate like :standingcoouncil ");
		 */
		if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getStandingCouncil()))
			queryStr.append(" and lower(bidefcounsil.oppPartyAdvocate) like :standingcoouncil ");
		if (legalCaseSearchResultOblj.getStatusId() != null)
			queryStr.append(" and egwStatus.id =:status ");
		if (legalCaseSearchResultOblj.getCaseFromDate() != null)
			queryStr.append(" and legalObj.caseDate >=:fromdate ");
		if (legalCaseSearchResultOblj.getCaseToDate() != null)
			queryStr.append(" and legalObj.caseDate <=:toDate ");
		if (legalCaseSearchResultOblj.getPetitionTypeId() != null)
			queryStr.append(" and petmaster.id =:petiontionType ");
		if ((legalCaseSearchResultOblj.getIsStatusExcluded() != null)
				|| (legalCaseSearchResultOblj.getIsJudgementDone() != null)
				|| (legalCaseSearchResultOblj.getIsJudgementPending() != null))
			queryStr.append(" and egwStatus.code not in (:statusCodeList ) ");
		if (legalCaseSearchResultOblj.getReportStatusId() != null)
			queryStr.append(" and reportStatus.id =:reportStatus ");
		if (legalCaseSearchResultOblj.getJudgmentTypeId() != null) {
			queryStr.append(" and jt.judgmentType.id =:judgmentid ");
		} else {
			queryStr.append(
					" and legalObj.id not in (select ej.legalCase.id from Judgment ej where ej.judgmentType.name='Decided') ");
		}
		
		
		if (legalCaseSearchResultOblj.getConcernedBranchId() != null)
			queryStr.append(" and cb.id =:branchid ");


		if (legalCaseSearchResultOblj.getIscaseImp() != null)
			queryStr.append("and (legalObj.caseImportant='Yes' or legalObj.impcasesflag=true)");

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

	public byte[] getSearchLegalCaseExcelSheet(Map<String, String> headerData,
			List<LegalCaseSearchResult> legalcaseSearchList) {
		byte[] fileContent = null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Legal Case Report");
			sheet.getPrintSetup().setLandscape(true);
			sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
			HSSFCellStyle style = wb.createCellStyle();
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11);
			font.setFontName("Times New Roman");
			// font.setBoldweight((short)10);
			style.setFont(font);
			int i = 0;

			Row row1 = sheet.createRow(i++);
			Cell c1 = row1.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(headerData.get("h1"));

			Cell c2 = row1.createCell(1);
			c2.setCellStyle(style);
			c2.setCellValue(headerData.get("h2"));

			Cell c3 = row1.createCell(2);
			c3.setCellStyle(style);
			c3.setCellValue(headerData.get("h3"));

			Cell c4 = row1.createCell(3);
			c4.setCellStyle(style);
			c4.setCellValue(headerData.get("h4"));

			Cell c5 = row1.createCell(4);
			c5.setCellStyle(style);
			c5.setCellValue(headerData.get("h5"));

			Cell c6 = row1.createCell(5);
			c6.setCellStyle(style);
			c6.setCellValue(headerData.get("h6"));

			Cell c7 = row1.createCell(6);
			c7.setCellStyle(style);
			c7.setCellValue(headerData.get("h7"));

			Cell c8 = row1.createCell(7);
			c8.setCellStyle(style);
			c8.setCellValue(headerData.get("h8"));

			Cell c9 = row1.createCell(8);
			c9.setCellStyle(style);
			c9.setCellValue(headerData.get("h9"));

			Cell c10 = row1.createCell(9);
			c10.setCellStyle(style);
			c10.setCellValue(headerData.get("h10"));

			Cell c11 = row1.createCell(10);
			c11.setCellStyle(style);
			c11.setCellValue(headerData.get("h11"));

			Cell c12 = row1.createCell(11);
			c12.setCellStyle(style);
			c12.setCellValue(headerData.get("h12"));

			Cell c13 = row1.createCell(12);
			c13.setCellStyle(style);
			c13.setCellValue(headerData.get("h13"));

			Cell c14 = row1.createCell(13);
			c14.setCellStyle(style);
			c14.setCellValue(headerData.get("h14"));
			Cell c15 = row1.createCell(14);
			c15.setCellStyle(style);
			c15.setCellValue(headerData.get("h15"));

			Cell c16 = row1.createCell(15);
			c16.setCellStyle(style);
			c16.setCellValue(headerData.get("h16"));

			Cell c17 = row1.createCell(16);
			c17.setCellStyle(style);
			c17.setCellValue(headerData.get("h17"));

			for (LegalCaseSearchResult s : legalcaseSearchList) {
				String casenumber = "";
				String casetitle = "";
				String concernedBranch = "";
				String courtname = "";
				String standingcouncil = "";
				String legalcaseno = "";
				String petitioners = "";
				String respondants = "";
				String statusDesc = "";
				String hearingDate = "";
				String hearingOutcome = "";
				String petetiontype = "";
				String brief = "";
				String nodalofficer = "";
				String councelengage = "";
				String replySubmit = "";
				String argument = "";

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
				Cell cell12 = row.createCell(12);
				Cell cell13 = row.createCell(13);
				Cell cell14 = row.createCell(14);
				Cell cell15 = row.createCell(15);
				Cell cell16 = row.createCell(16);

				if (s.getLegalCase().getLcNumber() != null) {
					legalcaseno = s.getLegalCase().getLcNumber();
				}
				if (s.getLegalCase().getCaseNumber() != null) {
					casenumber = s.getLegalCase().getCaseNumber();
				}
				if (s.getLegalCase().getCaseTitle() != null) {
					casetitle = s.getLegalCase().getCaseTitle();
				}

				if (s.getCourtName() != null) {
					courtname = s.getCourtName();
				}
				// if(s.getLegalCase().getOppPartyAdvocate()!=null) {
				if (s.getStandingCouncil() != null) {
					// standingcouncil=s.getLegalCase().getOppPartyAdvocate();
					standingcouncil = s.getStandingCouncil();
				}
				if (s.getLegalCase().getStatus().getDescription() != null) {
					statusDesc = s.getLegalCase().getStatus().getDescription();
				}
				if (s.getLegalCase().getPetitionersNames() != null) {
					petitioners = s.getLegalCase().getPetitionersNames();
				}

				if (s.getLegalCase().getRespondantNames() != null) {
					respondants = s.getLegalCase().getRespondantNames();
				}
				if (s.getConcernedBranch() != null) {
					concernedBranch = s.getConcernedBranch();
				}
				if (s.getHearingDate() != null) {
					hearingDate = DDMMYYYYFORMAT1.format(s.getHearingDate());
				}
				if (s.getHearingOutcome() != null) {
					hearingOutcome = s.getHearingOutcome();
				}
				if (s.getLegalCase().getPetitionTypeMaster() != null) {
					petetiontype = s.getLegalCase().getPetitionTypeMaster().getPetitionType();
				}
				if (s.getLegalCase().getBrief() != null) {
					brief = s.getLegalCase().getBrief();
				}
				if (s.getLegalCase().getCouncelengage() != null) {
					councelengage = s.getLegalCase().getCouncelengage();
				}
				if (s.getLegalCase().getNodalOfficername() != null) {
					nodalofficer = s.getLegalCase().getNodalOfficername();
				}
				if (s.getReplySubmit() != null) {
					replySubmit = s.getReplySubmit();
				}
				if (s.getArgument() != null) {
					argument = s.getArgument();
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
				cell9.setCellValue(hearingDate);
				cell10.setCellValue(hearingOutcome);
				cell11.setCellValue(petetiontype);
				cell12.setCellValue(brief);
				cell13.setCellValue(nodalofficer);
				cell14.setCellValue(councelengage);
				cell15.setCellValue(replySubmit);
				cell16.setCellValue(argument);

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

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fileContent;
	}

	public List<LegalCaseSearchResult> getLegalCaseReportRestData(
			final LegalCaseSearchResult legalCaseSearchResultObj) {
		final Boolean loggedInUserViewAccess = false;
		final StringBuilder queryStr = new StringBuilder();
		queryStr.append("select distinct legalObj  as  legalCase ,courtmaster.courtType  as  courtName ,");
		queryStr.append(" egwStatus.code  as  caseStatus ,");
		queryStr.append(" cb.concernedBranch  as  concernedBranch, ");
		queryStr.append(" hr.hearingDate as  hearingDate");
		queryStr.append(" from LegalCase legalObj,CourtTypeMaster courtmaster,");
		queryStr.append(
				" PetitionTypeMaster petmaster,EgwStatus egwStatus,ReportStatus reportStatus Left JOIN legalObj.concernedBranch cb");
		queryStr.append(" LEFT JOIN   legalObj.judgment jt");
		queryStr.append(" LEFT JOIN   Hearings hr on legalObj.id=hr.legalCase.id ");
		queryStr.append(" where legalObj.courtTypeMaster.id=courtmaster.id and  ");
		queryStr.append(
				" legalObj.petitionTypeMaster.id=petmaster.id and ");
		queryStr.append(" legalObj.status.id=egwStatus.id and egwStatus.moduletype =:moduleType ");
		if (legalCaseSearchResultObj.getReportStatusId() != null)
			queryStr.append("  and legalObj.reportStatus.id = reportStatus.id ");

		getAppendQuery(legalCaseSearchResultObj, queryStr);
		System.out.println("Query Execute\n------------>>>>>>>" + queryStr.toString());
		Query queryResult = getCurrentSession().createQuery(queryStr.toString());
		queryResult = setParametersToQuery(legalCaseSearchResultObj, queryResult);
		final List<LegalCaseSearchResult> legalcaseSearchList = queryResult.list();
		if (loggedInUserViewAccess)
			for (final LegalCaseSearchResult searchResults : legalcaseSearchList)
				searchResults.setLegalViewAccess(loggedInUserViewAccess);
		return legalcaseSearchList;

	}

}