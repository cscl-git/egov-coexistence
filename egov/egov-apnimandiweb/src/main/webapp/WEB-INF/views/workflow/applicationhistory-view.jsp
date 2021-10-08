<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading slide-history-menu">
		<div class="panel-title">
			<spring:message  code="lbl.wfhistory"/>
		</div>
		<div class="history-icon">
			<i class="fa fa-angle-up fa-2x" id="toggle-his-icon"></i>
		</div>
	</div>
	<div class="panel-body history-slide display-hide">			
		<table class="table table-bordered" id="momdetails">
			<thead>
				<tr>
					<th><spring:message code="lbl.date" /></th>
					<th><spring:message code="lbl.updatedby" /></th>
					<th><spring:message code="lbl.status" /></th>
					<th><spring:message code="lbl.currentowner" /></th>
					<th><spring:message code="lbl.department" /></th>
					<th><spring:message code="lbl.comments" /></th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${!applicationHistory.isEmpty()}">
						<c:forEach items="${applicationHistory}" var="history">
							<tr>
								<td>
									<fmt:formatDate value="${history.date}" var="historyDate"
										pattern="dd-MM-yyyy HH:mm a E" />
									<c:out value="${historyDate}" />
								</td>
								<td><c:out value="${history.updatedBy}" /></td>
								<td><c:out value="${history.status}" /></td>
								<td><c:out value="${history.user}" /></td>
								<td><c:out value="${history.department}" /></td>
								<td><c:out value="${history.comments}" /></td>
							</tr>
						</c:forEach>
					</c:when>
				</c:choose>
			</tbody>
		</table>
	</div>
</div>
<script>
	$('.slide-history-menu').click(function(){ 
			$('.history-slide').slideToggle();
			if($('#toggle-his-icon').hasClass('fa fa-angle-down'))
			{
				$('#toggle-his-icon').removeClass('fa fa-angle-down').addClass('fa fa-angle-up');
				//$('#see-more-link').hide();
				}else{
				$('#toggle-his-icon').removeClass('fa fa-angle-up').addClass('fa fa-angle-down');
				//$('#see-more-link').show();
			}
	});
</script>