<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<form:form role="form"
	action="/services/audit/createAudit/passUnderObjedit" method="post"
	modelAttribute="passUnderObjection" id="approvebudgetsearchform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">
	<div class="main-content">
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary" data-collapsed="0">
					<c:if test="${not empty message}">
						<div id="message" class="success"
							style="color: green; margin-top: 15px;">
							<spring:message code="${message}" />
						</div>
					</c:if>
					<div class="panel-heading">
						<div class="panel-title">Add Resolution comment</div>
					</div>
					<div class="panel-body">
						<div class="form-group">
							<label class="col-sm-3 control-label text-left-audit"><spring:message
									text="Resolution Comment" /> </label>
							<div class="col-sm-3 add-margin">
								<form:input class="form-control"
									data-pattern="alphanumericwithspecialcharacters"
									id="resolutioncomment" path="resolutioncomment" maxlength="500" />
							</div>

							<form:hidden id="temp_id" path="temp_id"
								value="${passUnderObjection.temp_id}" />
							<br> <br> <br> <br> <br>
								<div class="panel-heading slide-history-menu">

									<div class="panel-title">
										<spring:message text="Resolution History" />
									</div>
									<div class="history-icon">
										<i class="fa fa-angle-up fa-2x" id="toggle-his-icon"></i>
									</div>

									<div class="panel-body history-slide">
										<div
											class="row add-margin hidden-xs visible-sm visible-md visible-lg header-color">
											<div class="col-sm-2 col-xs-6 add-margin-audit">
												<spring:message text="Resolution Date" />
											</div>
											<div class="col-sm-2 col-xs-6 add-margin-audit">
												<spring:message text="Resolution comment" />
											</div>
										</div>
										<c:choose>
											<c:when
												test="${passUnderObjection.passUnderObjSearchList != null && !passUnderObjection.passUnderObjSearchList.isEmpty()}">
												<c:forEach
													items="${passUnderObjection.passUnderObjSearchList}"
													var="result">
													<div class="row add-margin">
														<div class="col-sm-2 col-xs-6 add-margin-audit">
															<c:out value="${result.resolutiondate}" />
														</div>
														<div class="col-sm-2 col-xs-6 add-margin-audit">
															<c:out value="${result.resolutioncomment}" />
														</div>
													</div>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<div class="col-md-3 col-xs-6 add-margin">
													<spring:message text="No History Details" />
												</div>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</div>
						</div>


						<div class="form-group">
							<div class="text-center">
								<button type='submit' class='btn btn-primary' id="btnsearch">
									Save</button>

								<a href='javascript:void(0)' class='btn btn-default'
									onclick='self.close()'><spring:message code='lbl.close' /></a>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
</form:form>
