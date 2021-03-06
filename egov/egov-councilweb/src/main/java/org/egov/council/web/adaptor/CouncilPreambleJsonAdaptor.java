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
package org.egov.council.web.adaptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.entity.MeetingMOM;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.utils.ApplicationConstant;
import org.egov.infra.utils.StringUtils;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

import static org.egov.council.utils.constants.CouncilConstants.PREAMBLEUSEDINAGENDA;

@Service
public class CouncilPreambleJsonAdaptor implements JsonSerializer<CouncilPreamble> {
	
	@Override
	public JsonElement serialize(final CouncilPreamble councilPreamble, final Type type,
			final JsonSerializationContext jsc) {
		String meetingDate = StringUtils.EMPTY;
		String meetingType = StringUtils.EMPTY;
		final JsonObject jsonObject = new JsonObject();
		if (councilPreamble != null) {
			jsonObject.addProperty("ward", councilPreamble.getWards().stream().map(Boundary::getName).collect(Collectors.joining(",")));
			if (councilPreamble.getDepartment() != null) {
				jsonObject.addProperty("department", councilPreamble.getDepartment());
			}
			else
				jsonObject.addProperty("department", StringUtils.EMPTY);
			if (councilPreamble.getType() != null)
				jsonObject.addProperty("preambleType", councilPreamble.getType().toString());
			else
				jsonObject.addProperty("preambleType", StringUtils.EMPTY);
			if (councilPreamble.getPreambleNumber() != null)
				jsonObject.addProperty("preambleNumber", councilPreamble.getPreambleNumber());
			else
				jsonObject.addProperty("preambleNumber", StringUtils.EMPTY);
			if (councilPreamble.getGistOfPreamble() != null){
                                jsonObject.addProperty("gistOfPreamble",
                                        StringEscapeUtils.escapeJava(councilPreamble.getGistOfPreamble().replaceAll("[\n\r]", "")));
			}
			else
			        jsonObject.addProperty("gistOfPreamble", StringUtils.EMPTY);
			if (councilPreamble.getSanctionAmount() != null)
				jsonObject.addProperty("sanctionAmount", councilPreamble.getSanctionAmount());
			else
				jsonObject.addProperty("sanctionAmount", StringUtils.EMPTY);
			if (councilPreamble.getCreatedDate() != null)
				jsonObject.addProperty("createdDate", councilPreamble.getCreatedDate().toString());
			else
				jsonObject.addProperty("createdDate", StringUtils.EMPTY);
			if (councilPreamble.getStatus() != null)
				jsonObject.addProperty("preambleUsedInAgenda",
						PREAMBLEUSEDINAGENDA.equals(councilPreamble.getStatus().getCode()) ? "Yes" : "No");
			else
				jsonObject.addProperty("preambleUsedInAgenda", StringUtils.EMPTY);

			if (!councilPreamble.getMeetingMOMs().isEmpty()) {
				for (MeetingMOM meetingMOM : councilPreamble.getMeetingMOMs()) {
					meetingDate = meetingMOM.getMeeting().getMeetingDate().toString();
					meetingType = meetingMOM.getMeeting().getCommitteeType().getName();
				}
			}

			if (meetingDate != null)
				jsonObject.addProperty("meetingDate", meetingDate);
			else
				jsonObject.addProperty("meetingDate", StringUtils.EMPTY);

			if (meetingType != null)
				jsonObject.addProperty("meetingType", meetingType);
			else
				jsonObject.addProperty("meetingType", StringUtils.EMPTY);
			if (councilPreamble.getImplementationStatus() != null)
				jsonObject.addProperty("implementationStatus", councilPreamble.getImplementationStatus().getCode());
			else
				jsonObject.addProperty("implementationStatus", "N/A");
			if (councilPreamble.getStatus() != null)
				jsonObject.addProperty("status", councilPreamble.getStatus().getDescription());
			else
				jsonObject.addProperty("status", StringUtils.EMPTY);

			jsonObject.addProperty("id", councilPreamble.getId());
		}
		return jsonObject;
	}

}