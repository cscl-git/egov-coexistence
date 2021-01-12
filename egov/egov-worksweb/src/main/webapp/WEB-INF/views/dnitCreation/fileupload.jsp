 <%@ include file="/includes/taglibs.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--<%@ taglib uri="/WEB-INF/tags/cdn.tld" prefix="cdn" %> --%>
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
<div class="panel panel-primary" data-collapsed="0" style=" scrollable:true;">
    <div class="panel-heading">
         <div class="panel-title">
                <spring:message code="lbl.upload.document" text="Documents" />
        </div> 
      </div>
   <c:if test="${estimatePreparationApproval.documentDetail != null &&  !estimatePreparationApproval.documentDetail.isEmpty()}">
        <c:forEach items="${estimatePreparationApproval.documentDetail }" var="documentDetials">
        
        <c:if test="${documentDetials.objectType != 'roughWorkFile' }">
            <a href="/services/works/estimatePreparation/downloadBillDoc?estDetailsId=${estimatePreparationApproval.id}&fileStoreId=${documentDetials.fileStore.fileStoreId }">${documentDetials.fileStore.fileName }</a><br />
       </c:if>
        </c:forEach>
    </c:if>
   <br> 
   <c:if test="${mode != 'view' }">
        <div>
            <table width="100%">
                        <tbody>
                        <tr>
                            <td valign="top">
                                <table id="uploadertbl" width="100%"><tbody>
                                <tr id="row1">
                                    <td>
                                        <input type="file" name="file1" id="file1" onchange="isValidFile(this.id)" style="color:#000000;" class="padding-10">
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
       </c:if>
</div>