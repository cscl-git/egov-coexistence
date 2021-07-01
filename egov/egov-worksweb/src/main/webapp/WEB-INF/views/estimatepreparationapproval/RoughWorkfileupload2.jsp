 <%@ include file="/includes/taglibs.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script
	src="<cdn:url value='/resources/js/estimatepreparationapproval/fileupload.js'/>"></script>
<style>
    .file-ellipsis {
        width : auto !Important;
    }
    .padding-10
    {
        padding:10px;
    }
</style>
<div class="" data-collapsed="0" style=" scrollable:true;">
   <c:if test="${estimatePreparationApproval.roughCostdocumentDetail != null &&  !estimatePreparationApproval.roughCostdocumentDetail.isEmpty()}">
       
        <table id="table" class="table table-bordered">
						<thead>
							<tr>
								<th>File</th>
								<th>Boq Upload Remarks</th>
								<th>Version</th>
								<th>File Owner</th>
								
							</tr>
						</thead>
						<tbody>
        <c:forEach items="${estimatePreparationApproval.roughCostdocumentDetail }" var="documentDetials" varStatus="loop">
        
        
       
						<tr>
						<td>  <a href="/services/works/estimatePreparation/downloadRoughWorkBillDoc?estDetailsId=${estimatePreparationApproval.id}&fileStoreId=${documentDetials.fileStore.fileStoreId }">${documentDetials.fileStore.fileName }</a><br />
						</td>
						<td><span style="color: #643d19;"> ${documentDetials.comments }</span></td>
						<td><span style="color: #643d19;"> ${loop.index + 1}</span></td>
						<td><p style="color: #643d19;">${documentDetials.username }</p></td>
						
						</tr>
        </c:forEach>
						</tbody>
			</table>			
        
           
    </c:if>
   <br> 
  <%--  <c:if test="${mode != 'view' }">
        <div>
            <table width="100%">
                        <tbody>
                        <tr>
                            <td valign="top">
                                <table id="uploadertbl" width="100%"><tbody>
                                <tr id="row1">
                                    <td>
                                        <input type="file" name="fileRoughCost" id="file1" onchange="isValidFile(this.id)" style="color:#000000;" class="padding-10">
                                    <form:hidden path="objectType" id="objectType" class="checklist_description" value="roughWorkFile"></form:hidden>
                                    
                                    </td>
                                </tr>
                                </tbody></table>
                            </td>
                        </tr>
                        <tr>
                            <td align="center">
                                <button id="attachNewFileBtn" type="button" class="btn btn-primary" onclick="addFileInputFieldUpload()"><spring:message code="lbl.addfile" text="Add File"/></button>
                            </td>
                        </tr>
                        </tbody>
            </table>
        </div>
       </c:if> --%>
</div>