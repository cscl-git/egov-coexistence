<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form role="form" action="create" modelAttribute="councilPreamble"
	id="councilPreambleform"
	cssClass="form-horizontal form-groups-bordered"
	enctype="multipart/form-data">

	<jsp:include page="councilpreamble-form.jsp" />

	<jsp:include page="../workflow/commonWorkflowMatrix.jsp" />
	<div class="buttonbottom" align="center">
		<jsp:include page="../workflow/commonWorkflowMatrix-button.jsp" />
	</div>
	<%-- <div class="form-group">
		<div class="text-center">
			<button type='submit' class='btn btn-primary' id="buttonSubmit">
				<spring:message code='lbl.submit' />
			</button>
			<button type='submit' class='btn btn-primary' id="buttonClose">
				<spring:message code='lbl.close' />
			</button>
		</div>
	</div> --%>
</form:form>

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
<!--<script type="text/javascript"
	src="<cdn:url value='/resources/app/js/ckeditorload.js'/>"></script>  -->

<script>
	$('#buttonSubmit').click(function(e) {
		if ($('form').valid()) {
		} else {
			e.preventDefault();
		}
	});
	$('.workflow-submit').click(function(e) {
		var editorData = editor.getData();
		console.log("editorData:"+editorData);
		if(editorData.trim() === ''){
			alert("Agenda details can not be blank.");
			e.preventDefault();
		}
	});
	
</script>
<script>ClassicEditor
			.create( document.querySelector( '#editor' ), {
				toolbar: {
					items: [
						'heading',
						'|',
						'fontFamily',
						'fontSize',
						'bold',
						'italic',
						'underline',
						'fontColor',
						'fontBackgroundColor',						
						'highlight',	
						'|',						
						'bulletedList',						
						'numberedList',
						'insertTable',						
						'indent',
						'outdent',
						'alignment',
						'pageBreak',
						'|',
						'link',
						'blockQuote',							
						'horizontalLine',						
						'strikethrough',						
						'superscript',						
						'subscript',
						'specialCharacters',
						'|',
						'undo',
						'redo',
						'removeFormat'
						
					]
				},
				language: {
					ui: 'en',
					content: 'en'
				},
				fontSize: {
					options: [
						9,
						11,
						13,
						'default',
						17,
						19,
						21
					]
				},
				alignment: {
					options: [ 'left', 'right','center','justify' ]
				},
				image: {
					toolbar: [
						'imageTextAlternative',
						'imageStyle:full',
						'imageStyle:side'
					]
				},
				table: {
					contentToolbar: [
						'tableColumn',
						'tableRow',
						'mergeTableCells',
						'tableCellProperties',
						'tableProperties'
					]
				},
				typing: {
					transformations: {
						remove: [
							// Do not use the transformations from the
							// 'symbols' and 'quotes' groups.
							'symbols',
							'quotes',

							// As well as the following transformations.
							'arrowLeft',
							'arrowRight'
						],

						extra: [
							// Add some custom transformations – e.g. for emojis.
							{ from: ':)', to: '🙂' },
							{ from: ':+1:', to: '👍' },
							{ from: ':tada:', to: '🎉' },

							// You can also define patterns using regular expressions.
							// Note: The pattern must end with `$` and all its fragments must be wrapped
							// with capturing groups.
							// The following rule replaces ` "foo"` with ` «foo»`.
							{
								from: /(^|\s)(")([^"]*)(")$/,
								to: [ null, '«', null, '»' ]
							},

							// Finally, you can define `to` as a callback.
							// This (naive) rule will auto-capitalize the first word after a period.
							{
								from: /(\. )([a-z])$/,
								to: matches => [ null, matches[ 1 ].toUpperCase() ]
							}
						],
					}
				},
				licenseKey: '',
				
			} )
			.then( editor => {
				window.editor = editor;				
			} )
			.catch( error => {
				console.error( 'Oops, something gone wrong!' );
				console.error( error );
			} );
	</script>
