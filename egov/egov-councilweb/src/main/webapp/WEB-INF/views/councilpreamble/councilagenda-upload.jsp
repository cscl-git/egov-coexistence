<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
	href="<cdn:url value='/resources/app/css/council-style.css?rnd=${app_release_no}'/>" />
<script
	src="<cdn:url value='/resources/app/js/councilPreambleHelper.js?rnd=${app_release_no}'/>"></script>
<script
	src="<cdn:url value='/resources/global/js/egov/inbox.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
<script
	src="<cdn:url value='/resources/app/js/documentsupload.js?rnd=${app_release_no}'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/common-util-helper.js?rnd=${app_release_no}'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/ckeditor.js'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/ckeditorload.js'/>"></script>
<meta charset="UTF-8">
<script>
	$('#buttonSubmit').click(function(e) {
		if ($('form').valid()) {
		} else {
			e.preventDefault();
		}
	});
	});
		$('#buttonSubmit').on('click',function(){
		     /*  $('#department').val(""); */
			$("form").trigger("reset");
		});
</script>

</head>
<body>

		
<form:form role="form" action="create" modelAttribute="councilAgendaUpload"
	id="councilAgendaUploadform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data"> 
	<c:choose>
	<c:when test="${message != null}">
<div class="alert alert-success" role="alert">
				<strong>${message}</strong>
			</div>
		</c:when>
<c:otherwise>
  </c:otherwise>
</c:choose>	
<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary" data-collapsed="0">
					<div class="panel-heading">
						<div class="panel-title">
							<spring:message code="title.agendaUpload.new"/>
						</div>
					</div>
					<div class="panel-body">
					<div class="form-group">
				<label class="col-sm-2 control-label text-right">
					<spring:message code="lbl.department" /> <span class="mandatory"></span> 
				</label>
				<div class="col-sm-3 add-margin">
							<form:select path="department" id="department" name="department"
								cssClass="form-control" cssErrorClass="form-control error"
								required="required">
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${departments}" itemValue="code"
									itemLabel="name" />
							</form:select>
							<form:errors path="department" cssClass="error-msg" />
				</div>
	</div>
	<div class="form-group">
				<label class="col-sm-2 control-label text-right"><spring:message
						code="lbl.upload" /><span class="mandatory"></span></label>
				<div class="col-sm-8 add-margin">
					<c:choose>
						<c:when test="${createUploadAgenda.filestoreid != null}">

							<form:input path="attachments" type="file" id="attachments"
								name="attachments" data-id="1"
								class="filechange inline btn upload-file" />
							<form:errors path="attachments" cssClass="error-msg" />

							<form:hidden path="filestoreid.id"
								value="${createUploadAgenda.filestoreid.id}" />
							<form:hidden path="filestoreid.fileStoreId"
								value="${createUploadAgenda.filestoreid.fileStoreId}" />

							<a
								href="/services/council/councilmember/downloadfile/${createUploadAgenda.filestoreid.fileStoreId}"
								data-gallery style="display:block"> ${createUploadAgenda.filestoreid.fileName}</a>
							<small class="error-msg"><spring:message
									code="lbl.mesg.document" /></small>
						</c:when>
						<c:otherwise>
							<form:input path="attachments" type="file" id="attachments"
								name="attachments" required="true" data-id="1"
								class="filechange inline btn upload-file" />
							<small class="error-msg"><spring:message
									code="lbl.mesg.document" /></small>
							<form:errors path="attachments" cssClass="error-msg" />
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			</div>
			</div>
			</div>
		</div>	
		<%-- <form:button type="submit" id="save" name="save" class="btn btn-primary"  value="Save"/> --%>
		<button type='submit' class='btn btn-primary' id="buttonSubmit" value="Submit" style="
    margin-left: 435px;"/>
				<spring:message code='lbl.save' />
			</button>
			
				
</form:form> 

</body>
</html>

