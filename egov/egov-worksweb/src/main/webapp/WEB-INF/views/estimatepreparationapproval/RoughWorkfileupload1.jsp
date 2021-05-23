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
<script>

$('#save').click(function() {
	debugger;
    //get file object
    var file = document.getElementById('file').files[0];
    if (file) {
    	console.log('name'+file.name());
        // create reader
        var reader = new FileReader();
        reader.readAsText(file);
        reader.onload = function(e) {
            // browser completed reading file - display it
            alert(e.target.result);
        };
    }
});

</script>
<div class="" data-collapsed="0" style=" scrollable:true;">
 <c:if test="${estimatePreparationApproval.docUpload != null &&  !estimatePreparationApproval.docUpload.isEmpty()}">
       
        <table id="table" class="table table-bordered">
						<thead>
							<tr>
								<th>File</th>
								<th>Boq Upload Remarks</th>
								<th>Version</th>
								
								
							</tr>
						</thead>
						<tbody>
        <c:forEach items="${estimatePreparationApproval.docUpload }" var="documentDetials" varStatus="loop">
        
        
       <c:if test="${documentDetials.objectType !=null }">
						<tr>
						<td>
						
						 <%-- <a href="/services/works/estimatePreparation/downloadRoughWorkBillDoc?estDetailsId=${estimatePreparationApproval.id}&fileStoreId=${documentDetials.fileStore.fileStoreId }">${documentDetials.fileStore.fileName }</a> --%> 
						  <p style="color: #000000;">${documentDetials.objectType }</p> 
						</td>
						<td><p style="color: #000000;">${documentDetials.comments }</p></td>
						<td><span style="color: #643d19;"> ${loop.index + 1}</span></td>
						
						</tr>
						</c:if>
        </c:forEach>
						</tbody>
			</table>			
        
           
    </c:if> 
    
    <%-- <c:forEach var="mapboq" items="${uploadDocument}" varStatus="mapstatus">
					<table id="boq${mapstatus.index}tableBoq" class="table table-bordered tableBoq">
						<thead>
							<tr>
							<th><c:out value="${mapboq.key}"/></th>
							</tr>
							<tr>
								
								<th>ID</th>
								<th> ObjectId</th>
								<th>filestoreid</th>
								<th>objectType</th>
								<th>comments</th>
								
							</tr>
						</thead>
						<tbody>
						<c:forEach var="boq" items="${mapboq.value}" varStatus="status">
						<c:if test="${mapboq.key == boq.milestone }">
								<tr id="boq${mapstatus.index}tableBoqrow" class="boq${status.index}repeat-address">
								
											
									<td><form:input type="number" style="height: 100px; "
											path="docUpload[${boq.id}].id"
											id="docUpload[${boq.slNo}].id"
											required="required" readonly="true" class="form-control item_description"
											 title="${boq.id}"></form:input></td>
									<td>
									 	
											<form:input type="number" style="width:75px;"
													path="docUpload[${boq.id}].objectId"
													id="docUpload[${boq.id}].objectId"
													required="required" readonly="true"  class="form-control ref_dsr"
													  title="${boq.objectId}"></form:input>
										
								    </td>
									<td><form:input type="number" style="width:80px;"
											path="docUpload[${boq.id}].filestoreid"
												id="docUpload[${boq.id}].filestoreid"
												required="required" readonly="true" class="form-control unit"
												></form:input></td>
									<td><form:input type="text" style="width:100px;"
											path="docUpload[${boq.id}].objectType" 
												id="docUpload[${boq.id}].objectType"
												required="required" readonly="true"  class="form-control rate"
												></form:input></td>
									<td><form:input type="text" style="width:100px;"
											path="docUpload[${boq.id}].comments" 
											id="docUpload[${boq.id}].comments"
											required="required" readonly="true"  class="form-control quantity"
											></form:input></td>
									
								</tr>
							</c:forEach>
						</tbody>
				</table>
			  </c:forEach> --%>
   <br> 
 <%-- <c:if test="${mode != 'view' }">
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
       </c:if>  --%>
</div>