<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary" data-collapsed="0">
					<div class="panel-heading">
						<div class="panel-title">
							<spring:message code="title.agendaUpload.search"/>
						</div>
					</div>
					<div class="panel-body">
						<div class="form-group">
							 <label class="col-sm-2 control-label text-right"><spring:message
									code="lbl.fromdate" /> <span class="mandatory"></span></label>
							<div class="col-sm-3 add-margin">
									<form:input path="fromDate"
										class="form-control text-left patternvalidation datepicker"
										 data-date-end-date="0d"  required="true"/>
									<form:errors path="fromDate" cssClass="error-msg" />
							</div> 
							
							<label class="col-sm-2 control-label text-right"><spring:message
							code="lbl.todate" /> <span class="mandatory"></span></label>
							<div class="col-sm-3 add-margin">
								<form:input type="text" cssClass="form-control datepicker"
									path="toDate"  required="true"/>
								<form:errors path="toDate" cssClass="error-msg" />
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label text-right"><spring:message
									code="lbl.department" /> </label>
							<div class="col-sm-3 add-margin">
								<form:select path="department" id="department"
								cssClass="form-control" cssErrorClass="form-control error"
								>
								<form:option value="">
									<spring:message code="lbl.select" />
								</form:option>
								<form:options items="${departments}" itemValue="code"
									itemLabel="name" />
							</form:select>
							<form:errors path="department" cssClass="error-msg" />
							</div>
							
						</div>
					
							
						<input type="hidden" id="mode" name="mode" value="${mode}" />
				</div>
			</div>
		</div>
	</div>
	<div class="form-group">
		<div class="text-center">
			<button type="submit" class='btn btn-primary' id="btnsearch">
				<spring:message code='lbl.search' />
			</button>
			<button type="reset" class="btn btn-danger"><spring:message code="lbl.reset"/></button>
			<!-- <a href='javascript:void(0)' class='btn btn-default'
				onclick='self.close()'><spring:message code='lbl.close' /></a> -->
		</div>
    </div>