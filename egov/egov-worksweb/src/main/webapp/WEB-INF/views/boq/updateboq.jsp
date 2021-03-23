<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
	src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

<form:form name="newboqDetails-form" role="form" method="post"
	action="updateBoq" modelAttribute="boqNewDetails" id="saveboqform"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">
				
  
				<div class="form-group" style="padding: 50px 20px 0;">
					<div class="text-center">Update the BOQ Details</div>
					
					 
					 
					<label class="col-sm-3 control-label text-left-audit">Item
						Description<span class="mandatory"></span>:
					</label>
					<div class="col-sm-9 add-margin">
						<form:textarea class="form-control" path="item_description"
							maxlength="2000" style="height: 100px;" required="required" />
					</div>
					<label class="col-sm-3 control-label text-left-audit">Ref
						DSR/NS<span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="ref_dsr"
							required="required"  />
					</div>
					<label class="col-sm-3 control-label text-left-audit">Unit<span
						class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="unit"
							required="required" />
					</div>
					<label class="col-sm-3 control-label text-left-audit">Rate<span
						class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="number" class="form-control" path="rate"
							required="required" />
					</div>
					
					
					<div class="col-sm-3 add-margin">
						<form:hidden type="number" class="form-control" path="id"
							required="required" />
					</div>


				</div>
				<div class="buttonbottom" align="center">
					<input type="submit" id="saveboqform" class="btn btn-primary"
						name="updateBoq" value="Update" />
				</div>
			</div>
		</div>
	</div>





</form:form>



