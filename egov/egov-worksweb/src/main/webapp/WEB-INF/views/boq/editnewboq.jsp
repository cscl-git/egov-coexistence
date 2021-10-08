<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglibs/cdn.tld" prefix="cdn"%>

<script
	src="<cdn:url value='/resources/js/estimateworks.js?rnd=${app_release_no}' context='/services/works'/>"></script>

<form:form name="newboqDetails-form" role="form" method="post"
	action="editboqnew" modelAttribute="boqNewDetails" id="searchboqform"
	class="form-horizontal form-groups-bordered" style="margin-top:-20px;">

	<div class="tab-content">
		<div class="tab-pane fade in active">
			<div class="panel panel-primary" data-collapsed="0">


				<div class="form-group" style="padding: 50px 20px 0;">
					<div class="text-center">Search BOQ Details</div>
					
					<label class="col-sm-3 control-label text-left-audit">Ref
						DSR/NS<span class="mandatory"></span>:
					</label>
					<div class="col-sm-3 add-margin">
						<form:input type="text" class="form-control" path="ref_dsr"
							 />
					</div>
				
				</div>
				<div class="buttonbottom" align="center">
					<input type="submit" id="searchboqnew" class="btn btn-primary"
						name="editboqnew" value="Search" />
				</div>
			</div>
	</div>
	<br> <br> <br>
		<div class="tab-pane fade in active" id="resultheader">
			<h3>Search Result</h3>
			<div class="panel panel-primary" data-collapsed="0">
				<div style="padding: 0 15px;">
					<table class="table table-bordered" id="table">
						<thead>
							<tr>
								<th>Item Description</th>
								<th>Ref DSR/NS</th>
								<th>Unit</th>
								<th>Rate</th>
								

							</tr>
						</thead>
						`
						<c:if
							test="${boqNewDetails.estimateList != null &&  !boqNewDetails.estimateList.isEmpty()}">
							<tbody>
								<c:forEach items="${boqNewDetails.estimateList}"
									var="result" varStatus="status">
									<tr>
										<td><form:hidden
												path="estimateList[${status.index}].item_description"
												id="estimateList[${status.index}].item_description" />
											${result.item_description }</td>
										<td><form:hidden
												path="estimateList[${status.index}].ref_dsr"
												id="estimateList[${status.index}].ref_dsr" /><a href="#" onclick="updateBOQ('${result.id}')">${result.ref_dsr }</a>
											</td>
										<td><form:hidden
												path="estimateList[${status.index}].unit"
												id="estimateList[${status.index}].unit" />
											
												${result.unit }</td>
										<td><form:hidden
												path="estimateList[${status.index}].rate"
												id="estimateList[${status.index}].rate" />
											${result.rate }</td>
										
									</tr>
								</c:forEach>
							<tbody>
						</c:if>
						<c:if
							test="${boqNewDetails.estimateList == null ||  boqNewDetails.estimateList.isEmpty()}">
					No records found
					</c:if>
					</table>
				</div>

			</div>
		</div>
	</div>
	
	





</form:form>



