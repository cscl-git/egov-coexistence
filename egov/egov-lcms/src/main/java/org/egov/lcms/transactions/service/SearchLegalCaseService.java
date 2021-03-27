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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
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
System.out.println("++++++++++++"+legalCaseSearchResultObj.getIsStatusExcluded()+"+++++++++++++++");
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("select distinct legalObj  as  legalCase ,courtmaster.name  as  courtName ,");
        queryStr.append(" egwStatus.code  as  caseStatus ");

		queryStr.append(" from LegalCase legalObj left join Judgment judgment on legalObj.id=judgment.legalCase ");
		queryStr.append(" left join JudgmentType judtype on judgment.judgmentType=judtype.id ");

		queryStr.append(" left join CourtMaster courtmaster on legalObj.id=courtmaster.id ");
		queryStr.append(" left join CaseTypeMaster casetypemaster on legalObj.id=casetypemaster.id ");
		queryStr.append(" left join PetitionTypeMaster petmaster on legalObj.id=petmaster.id ");
		queryStr.append("left join EgwStatus egwStatus on legalObj.id=egwStatus.id  ");

        if (legalCaseSearchResultObj.getReportStatusId() != null)
			queryStr.append(" left join ReportStatus reportStatus on legalObj.id = reportStatus.id ");

		queryStr.append("where legalObj.id=legalObj.id ");
		System.out.println(queryStr);
        getAppendQuery(legalCaseSearchResultObj, queryStr);
		System.out.println(queryStr);
        Query queryResult = getCurrentSession().createQuery(queryStr.toString());
        queryResult = setParametersToQuery(legalCaseSearchResultObj, queryResult);
        final List<LegalCaseSearchResult> legalcaseSearchList = queryResult.list();
        if (loggedInUserViewAccess)
            for (final LegalCaseSearchResult searchResults : legalcaseSearchList)
                searchResults.setLegalViewAccess(loggedInUserViewAccess);
        return legalcaseSearchList;

    }

    private Query setParametersToQuery(final LegalCaseSearchResult legalCaseSearchResultObj, final Query queryResult) {
		// queryResult.setString("moduleType", LcmsConstants.MODULE_TYPE_LEGALCASE);
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

		if (legalCaseSearchResultObj.getJudgmentType() != null)
			queryResult.setString("judgmentType", legalCaseSearchResultObj.getJudgmentType());

		
		/*if (legalCaseSearchResultObj.getIsStatusExcluded() != null) {
            final List<String> statusCodeList = new ArrayList<>();
			//statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_CLOSED);
			statusCodeList.add("127");
			statusCodeList.add("128");
			//statusCodeList.add(LcmsConstants.LEGALCASE_STATUS_JUDGMENT_IMPLIMENTED);

            queryResult.setParameterList("statusCodeList", statusCodeList);
		}*/
		
        queryResult.setResultTransformer(new AliasToBeanResultTransformer(LegalCaseSearchResult.class));
        return queryResult;
    }

    private void getAppendQuery(final LegalCaseSearchResult legalCaseSearchResultOblj, final StringBuilder queryStr) {
        if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getLcNumber()))
            queryStr.append(" and legalObj.lcNumber =:lcNumber");
        if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getCaseNumber()))
            queryStr.append(" and legalObj.caseNumber like :caseNumber ");
        if (legalCaseSearchResultOblj.getCourtId() != null)
			// queryStr.append(" and courtmaster.id =:court ");
			queryStr.append(" and legalObj.courtMaster =:court ");
        if (legalCaseSearchResultOblj.getCasecategory() != null)
			// queryStr.append(" and casetypemaster.id =:casetype");
			queryStr.append(" and legalObj.caseTypeMaster =:casetype");
        if (legalCaseSearchResultOblj.getCourtType() != null)
			// queryStr.append(" and courtmaster.id =:courttype ");
			queryStr.append(" and legalObj.courtMaster =:courttype ");
        if (StringUtils.isNotBlank(legalCaseSearchResultOblj.getStandingCouncil()))
            queryStr.append(" and legalObj.oppPartyAdvocate like :standingcoouncil ");
        if (legalCaseSearchResultOblj.getStatusId() != null)
			queryStr.append(" and legalObj.status =:status ");
        if (legalCaseSearchResultOblj.getCaseFromDate() != null)
            queryStr.append(" and legalObj.caseDate >=:fromdate ");
        if (legalCaseSearchResultOblj.getCaseToDate() != null)
            queryStr.append(" and legalObj.caseDate <=:toDate ");
        if (legalCaseSearchResultOblj.getPetitionTypeId() != null)
			// queryStr.append(" and petmaster.id =:petiontionType ");
			queryStr.append(" and legalObj.petitionTypeMaster =:petiontionType ");
        if (legalCaseSearchResultOblj.getIsStatusExcluded() != null)
			queryStr.append(" and legalObj.status not in (127,128 ) ");

        if (legalCaseSearchResultOblj.getReportStatusId() != null)
            queryStr.append(" and reportStatus.id =:reportStatus ");

		if (legalCaseSearchResultOblj.getJudgmentType() != null)
			queryStr.append(" and judtype.name =:judgmentType ");
		
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

}
