/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
var tableContainer;
var reportdatatable;
var temp=[];
var unique=[];
jQuery(document).ready(
		function($) {
			$("#downloadexcel").hide();
			loadsubreportstatus();
			$('#statusId').change(function(){
				loadsubreportstatus();
			});
			
			tableContainer = $('#legalCaseResults');
			var judgment = $('#judgment').val();
			document.onkeydown = function(evt) {
				var keyCode = evt ? (evt.which ? evt.which : evt.keyCode)
						: event.keyCode;
				if (keyCode == 13) {
					submitForm();
				}
			}
			$('#legalcaseReportSearch').click(function() {
				submitForm();
			});
			
			$('#legalcaseReportSearchExcel').click(function() {
					submitFormForExcel();
		});
			$('#legalcaseReportSearchPdf').click(function() {
				submitFormForPdf();
	});
	$('#legalcasesavecheckboxval').click(function() {
	   /*alert("onclick");*/
			submitcheckboxes();	
  });
  
  $('#legacasesaveremarks').click(function() {
	   /*alert("onclick");*/
			legalCaseRemarks();	
  });
			
		});
$('#searchapp').keyup(function(){
	tableContainer.fnFilter(this.value);
});

function submitFormForExcel(){
	url : "/services/lcms/search/legalsearchResultExcel?"+$('#searchlegalcaseForm').serialize();
	
		$.ajax({
			 type: "GET",
			  url: "/services/lcms/search/legalsearchResultExcel?"+$('#searchlegalcaseForm').serialize(),
			  cache: false,
			  success: function(){
				  window.location ="/services/lcms/search/legalsearchResultExcel?"+$('#searchlegalcaseForm').serialize();
			  }
			});
}

function submitFormForPdf(){
	url : "/services/lcms/search/legalcaseReportSearchPdf?"+$('#searchlegalcaseForm').serialize();
	
		$.ajax({
			 type: "GET",
			  url: "/services/lcms/search/legalcaseReportSearchPdf?"+$('#searchlegalcaseForm').serialize(),
			  cache: false,
			  success: function(){
				  window.location ="/services/lcms/search/legalcaseReportSearchPdf?"+$('#searchlegalcaseForm').serialize();
			  }
			});
}


function submitcheckboxes(){
	/*alert("save click");*/
	var mytable=$('#legalCaseResults').dataTable();
	var rowcollection=mytable.$(".impcases:checked",{"page": "all"});
	var mycheckboxes=[];
	rowcollection.each(function(index,elem){
		var checkbox_value=$(elem).val();
		//alert("selected checkboxes :"+checkbox_value);
		mycheckboxes.push(checkbox_value);
	});
	
	//alert("onclick temp values:"+temp);
	
	//alert("onclick unique values:"+unique);
	
	$.ajax({
			 type: "POST",
			  url: "/services/lcms/search/legalcasecheckboxupdate",
			  dataType: "json",
			  contentType: "application/json",
			  data:JSON.stringify(mycheckboxes),
			  success: function(data){			
			  }
			});
			
	 $.ajax({
			 type: "POST",
			  url: "/services/lcms/search/legalcaseuncheckboxupdate",
			  dataType: "json",
			  contentType: "application/json",
			  data:JSON.stringify(unique),
			  success: function(data){
				 console.log("done"+data);
				 bootbox.alert(data);
			  }
			});
	
}

function submitForm() {

	var caseNumber = $("#caseNumber").val();
	var lcNumber = $("#lcNumber").val();
	


	$('.report-section').removeClass('display-hide');
	$('#report-footer').show();
	var isCancelled = jQuery('#isStatusExcluded').is(":checked");
	reportdatatable = tableContainer
			.dataTable({
				ajax : {
					url : "/services/lcms/search/legalsearchResult?"+$('#searchlegalcaseForm').serialize(),
					
				},
				destroy : true,
				"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-3 col-xs-12'i><'col-md-3 col-xs-6 col-right'l><'col-xs-12 col-md-3 col-right'<'export-data'T>><'col-md-3 col-xs-6 text-right'p>>",
				"aLengthMenu" : [[10,25,50,-1 ],[10,25,50,"All" ] ],
				"autoWidth" : false,
				"oTableTools" : {
					"sSwfPath" : "../../../../../../services/egi/resources/global/swf/copy_csv_xls_pdf.swf",
					"aButtons" : []
				},
				columns : [
						{
						"data" : "id",
						"sTitle" : "Id",
						"visible": false,
		                "searchable": false,
						"className" : "text-right"
					},
					{       
						   "data" : {legalcaseno : "legalcaseno", id : "id", impcasesflag: "impcasesflag"},
							"sTitle" : "Important Cases",
							"className" : "text-right",
							"render" : function(data, type, full) {
								if(data.impcasesflag == false){								
								return '<input type="checkbox" name="importantcases"  class="impcases" id="impcases'+data.id+'" value="'+data.id+'" style= "height: 71px;width: 20px;" onclick="impCases(\''+ data.id +'\',\''+data.legalcaseno+'\',\''+data.impcasesflag+'\')">';
						}else{
						return '<input type="checkbox" name="importantcases"  class="impcases" id="impcases'+data.id+'" value="'+data.id+'" style= "height: 71px;width: 20px;accent-color: red;" onclick="impCases(\''+ data.id +'\',\''+data.legalcaseno+'\',\''+data.impcasesflag+'\')" checked="checked">';	
						}		
						}
					},
						/*{
							"data" : {legalcaseno : "legalcaseno", id : "id"},
							"sTitle" : "File Number",
							"className" : "text-right",
							"render" : function(data, type,row, full, meta) {
								return '<a href="javascript:void(0);" onclick="openLegalCase(\''+ data.id +'\',\''+data.legalcaseno+'\')">' + data.legalcaseno + '</a>';
								
							}
						},*/
						
						{
							"data" : "casenumber",
							"sTitle" : "Case Number",
							"className" : "text-right"
						},

						{
							"data" : "casetitle",
							"sTitle" : "Title of Case",
							"className" : "text-right"
						},
						{
							"data" : "courtname",
							"sTitle" : "Court Name",
							"className" : "text-right"
						},
						{
							"data" : "standingcouncil",
							"sTitle" : "Defending Counsel",
							"className" : "text-right"
						},
						{
							"data" : "statusDesc",
							"sTitle" : "Case Status",
							"className" : "text-right"
						},
						/*{
							"data" : "petitioners",
							"sTitle" : "Petitioners",
							"className" : "text-left"
						},
						{
							"data" : "respondants",
							"sTitle" : "Respondents",
							"className" : "text-left"
						},*/
						{
							"data" : "concernedBranch",
							"sTitle" : "Name of the Branch to which cases relates",
							"className" : "text-left"
						},
						{
							"data" : "hearingDate",
							"sTitle" : "Next date of hearing",
							"className" : "text-left"
						},
						{
							"data" : "hearingOutcome",
							"sTitle" : "Hearing Outcome",
							"className" : "text-left"
						},
						/*{
							"data" : "petetiontype",
							"sTitle" : "Petetion Type",
							"className" : "text-left"
						},
						{
							"data" : "brief",
							"sTitle" : "Brief",
							"className" : "text-left"
						},
						{
							"data" : "nodalofficer",
							"sTitle" : "Nodal Officer",
							"className" : "text-left"
						},
						{
							"data" : "councelengage",
							"sTitle" : "Councel Engage",
							"className" : "text-left"
						},
						
						{
							"data" : "replySubmit",
							"sTitle" : "Reply Submit",
							"className" : "text-left"
						},
						{
							"data" : "argument",
							"sTitle" : "Argument",
							"className" : "text-left"
						},
						{
							title : 'Actions',
							"className" : "text-right",
							render : function(data, type, full) {
								if(full.legalViewAccess)
									return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="4">View legalCase</option></select>');

								if (full.casestatus == 'LCCREATED'
										|| full.casestatus == 'HEARING' || full.casestatus == 'INTERIM_STAY' && !full.legalViewAccess) {
									//return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="1">Judgment</option><option value="2">Add/Edit Defending Counsel</option><option value="10">Edit para wise remarks/Counter filing date</option><option value="3">Edit legalCase</option><option value="4">View legalCase</option><option value="6">Hearings</option><option value="7">Interim Order</option><option value="8">Close Case</option></select>');
									return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="1">Create Judgement</option><option value="3">Edit legalCase</option><option value="4">View legalCase</option><option value="6">Hearings</option><option value="15">Update Payment</option><option value="8">Close Case</option></select>');
								} else if (full.casestatus == 'JUDGMENT' && !full.legalViewAccess) {
									//return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="4">View legalCase</option><option value="5">Edit Judgment</option><option value="8">Close Case</option><option value="11">Judgment Implementation</option></select>');
									return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="4">View legalCase</option><option value="5">Edit Judgment</option><option value="8">Close Case</option></select>');
								} else if (full.casestatus == 'CLOSED' && !full.legalViewAccess) {
									return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="9">Edit Close Case</option></select>');
								} else if (full.casestatus == 'JUDGEMENT_IMPL' && !full.legalViewAccess) {
									//return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="12">Edit Judgment Implementation</option><option value="8">Close Case</option></select>');
									return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="8">Close Case</option></select>');
								}else if (full.casestatus == 'UPDATE_PAYMENT' && !full.legalViewAccess) {
									//return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="12">Edit Judgment Implementation</option><option value="8">Close Case</option></select>');
									return ('<select class="dropchange" id="additionconn" ><option>Select from Below</option><option value="15">Update Payment</option></select>');
								}
							}
						},*/
						{       
						   "data" : {legalcaseno : "legalcaseno", id : "id", legalcaseremarks:"legalcaseremarks"},
							"sTitle" : "Remarks",
							"className" : "text-right",
							"render" : function(data, type, full) {
								if(data.legalcaseremarks=="" || data.legalcaseremarks==null){							
								return '<input type="text" name="legalcaseremarks"  class="remarks" id="'+data.id+'"  style= "border:1px solid;height:35px;">';	
						}else{
						        return '<input type="text" name="legalcaseremarks"  class="remarks" id="'+data.id+'" value="'+data.legalcaseremarks+'"  style= "border:1px solid;height:35px;">';	
						}
						}
					}  ],
				"footerCallback" : function(row, data, start, end, display) {
					var api = this.api(), data;
					if (data.length == 0) {
						$('#report-footer').hide();
						$("#downloadexcel").hide();
					} else {
						$('#report-footer').show();
						$("#downloadexcel").show();
					}

				},

			});
	$('.loader-class').modal('hide');
}

function onchnageofDate() {
	var date;
	var d = new Date();
	var curr_month = d.getMonth();
	var curr_date = d.getDate();
	if (curr_date <= 9) {
		curr_date = ("0" + curr_date);
	}
	curr_month++;
	if (!(curr_month > 9)) {
		curr_month = ("0" + curr_month);
	}
	var curr_year = d.getFullYear();
	date = curr_date + "/" + (curr_month) + "/" + curr_year;
	$("#caseToDate").val(date);

}
$("#legalCaseResults").on('change', 'tbody tr td .dropchange', function() {
	var id = tableContainer.fnGetData($(this).parent().parent(), 0);
	if (this.value == 1) {
		var url = '/services/lcms/judgment/new/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;	
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 2) {
		var url = '/services/lcms/standingCouncil/create/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 10) {
		var url = '/services/lcms/counterAffidavit/create/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 5) {
		var url = '/services/lcms/judgment/edit/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 4) {
		var url = '/services/lcms/application/view/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 3) {
		var url = '/services/lcms/application/edit/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 6) {
		var url = '/services/lcms/hearing/list/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 7) {
		var url = '/services/lcms/lcinterimorder/list/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 8) {
		var url = '/services/lcms/legalcasedisposal/new/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 9) {
		var url = '/services/lcms/legalcasedisposal/edit/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 11) {
		var url = '/services/lcms/judgmentimpl/new/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
	if (this.value == 12) {
		var url = '/services/lcms/judgmentimpl/new/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}if (this.value == 15) {
		var url = '/services/lcms/legalcasedisposal/updatePayment/new/?id=' + id;
		$('#searchlegalcaseForm1').attr('method', 'get');
		$('#searchlegalcaseForm1').attr('action', url);
//		window.location = url;
		window.open(url,"","height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");//Added By Kundan For new Pop Window
	}
});

function loadsubreportstatus(){
	if ($('#statusId :selected').text().localeCompare("Created") == 0 ) { 
		$("#reportstatus").show();
	}else
		$("#reportstatus").hide();
}
	
function openLegalCase(data,id) {
	window.open("/services/lcms/application/view/?id="+ data , "", "height=650,width=980,scrollbars=yes,left=0,top=0,status=yes");
}

function impCases(data,id,impcasesflag) {
	//alert("onclick" + data);
	if ($("#impcases"+data+"").is(':checked')) {
		/*alert("checked if");*/	
	$("#impcases"+data+"").prop("checked",true);	
	 $("input[type=checkbox]:checked").css('accent-color', 'red');	
	}
	else{
	var unchecked=($("#impcases"+data+"").val());
	temp.push(unchecked);	
	temp.forEach(element =>{
		if(!unique.includes(element)){
			unique.push(element);
		}
	});

	//alert("temp values:"+temp);
	
	//alert("unique checked values:"+unique);
	$("#impcases"+data+"").removeAttr("checked");			
	$("#impcases"+data+"").prop("checked",false);
	}
}

function legalCaseRemarks() {
    //alert("onclick");
    
    var mytable=$('#legalCaseResults').dataTable();
	var rowcollection=mytable.$("input:text",{"page": "all"});
	var mytextboxes=[];
	rowcollection.each(function(index,elem){
		var textbox_value=$(elem).val();
		var textbox_id=$(elem).attr('id');
		if(textbox_value != ""){
		//alert("selected id :"+textbox_id);	
		//alert("selected textboxes :"+textbox_value);
		var remarks=textbox_id+'-'+textbox_value;
		//alert(remarks +"remarks");
		mytextboxes.push(remarks);
		}	
	});
	
		$.ajax({
			 type: "POST",
			  url: "/services/lcms/search/legalcaseremarks",
			  dataType: "json",
			  contentType: "application/json",
			  data:JSON.stringify(mytextboxes),
			  success: function(data){
				 console.log("done"+data);
				 bootbox.alert(data);		
			  }
			});
			
}
