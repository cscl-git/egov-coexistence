
<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ include file="/includes/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/challan.js?rnd=${app_release_no}"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/autocomplete-debug.js?rnd=${app_release_no}"></script>
<style type="text/css">
	#codescontainer {position:absolute;left:11em;width:9%;text-align: left;}
	#codescontainer .yui-ac-content {position:absolute;width:350px;border:1px solid #404040;background:#fff;overflow:hidden;z-index:9050;}
	#codescontainer .yui-ac-shadow {position:absolute;margin:.3em;width:300px;background:#a0a0a0;z-index:9049;}
	#codescontainer ul {padding:5px 0;width:100%;}
	#codescontainer li {padding:0 5px;cursor:default;white-space:nowrap;}
	#codescontainer li.yui-ac-highlight {background:#ff0;}
	#codescontainer li.yui-ac-prehighlight {background:#FFFFCC;}
#subledgercodescontainer {position:absolute;left:11em;width:9%;text-align: left;}
	#subledgercodescontainer .yui-ac-content {position:absolute;width:350px;border:1px solid #404040;background:#fff;overflow:hidden;z-index:9050;}
	#subledgercodescontainer .yui-ac-shadow {position:absolute;margin:.3em;width:300px;background:#a0a0a0;z-index:9049;}
	#subledgercodescontainer ul {padding:5px 0;width:100%;}
	#subledgercodescontainer li {padding:0 5px;cursor:default;white-space:nowrap;}
	#subledgercodescontainer li.yui-ac-highlight {background:#ff0;}
	#subledgercodescontainer li.yui-ac-prehighlight {background:#FFFFCC;}
	th.yui-dt-hidden,
tr.yui-dt-odd .yui-dt-hidden,
tr.yui-dt-even .yui-dt-hidden
 {
display:none;
}
</style>
<title><s:text name="challan.pagetitle"/>
</title>
</head>

<body onload="onBodyLoad();window.setTimeout('populatepositionuseronload()', 2000);" ><br>
<div class="errorstyle" id="challan_error_area" style="display:none;"></div>
<div class="formmainbox">
<s:if test="%{hasErrors()}">
    <div id="actionErrors" class="errorstyle">
      <s:actionerror/>
      <s:fielderror/>
    </div>
</s:if>
<s:if test="%{hasActionMessages()}">
    <div id="actionMessages" class="messagestyle" align="center">
    	<s:actionmessage theme="simple"/>
    </div>
    <div class="blankspace">&nbsp;</div>
</s:if>
Hi
<s:form theme="simple" name="challan">
<s:token/>
</s:form>
</div>
<script src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
</body>

