<%@ include file="/includes/taglibs.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form:form name="tender-form" role="form" method="post"
	action="tenderSave" modelAttribute="tender" id="tender"
	enctype="multipart/form-data"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">


	<div class="tab-content">

		<jsp:include page="fileupload.jsp" />

	</div>
</form:form>
