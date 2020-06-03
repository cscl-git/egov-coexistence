<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="buttonbottom" align="center">
	<table>
		<tr>
			<td id="actionButtons">
				<c:if test="${mode != 'readOnly' }">
					<c:forEach items="${validActionList}" var="validButtons">
						<input type="submit" id="${validButtons}" class="btn btn-primary btn-wf-primary"  value="${validButtons}"/>
					</c:forEach>
				</c:if>
				<input type="button" name="button2" id="button2" value='<spring:message code="lbl.close" text="Close"/>' class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();" />
			</td>
		</tr>
	</table>
</div>