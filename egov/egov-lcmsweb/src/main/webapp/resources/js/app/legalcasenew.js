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
$(document).ready(function(){	
	var lcNumber=$('#lcNumber').val();	
	var modeval=$('#mode').val();
	
	if(modeval=='edit'){
		if(lcNumber !='')
			//$("#lcNumber").prop("disabled", true);
			$("#finwpYear").hide();
	}	
    $("#petitionDetails tbody tr").each(function( index ) {
    	var $this = $(this);
        $this.find("select, button").prop("disabled", true);
    });
    $("#respondantDetails tbody tr").each(function( index ) {
    	var $this = $(this);
        $this.find("select, button").prop("disabled", true);
    });
    $(".btn-primary").click(function(event){		
		var caseNumber =$('#caseNumber').val();
		var lcnumber=$('#lcNumber').val();
		var mode=$('#mode').val();
		if(mode=='create'){
		if(caseNumber !="" && caseNumber !=null && ($('#wpYear').val() ==null || $('#wpYear').val() =='') )
			{
			bootbox.alert("Select Case Number Year ");
			return false;
			}
		}
		if(mode=='create'){
		  if($('#caseDate').val() != '' && $('#caseReceivingDate').val() != '' ){
				var start = $('#caseDate').val();
				var end = $('#caseReceivingDate').val();
				var stsplit = start.split("/");
				var ensplit = end.split("/");
				
				start = stsplit[1] + "/" + stsplit[0] + "/" + stsplit[2];
				end = ensplit[1] + "/" + ensplit[0] + "/" + ensplit[2];
				if(!validCaseRecievingAndFillingRange(start,end)){					
					return false;
				}
			}
		    if($('#caseDate').val() != '' && $('#noticeDate').val() != '' ){
				var start = $('#caseDate').val();
				var end = $('#noticeDate').val();
				var stsplit = start.split("/");
				var ensplit = end.split("/");				
				start = stsplit[1] + "/" + stsplit[0] + "/" + stsplit[2];
				end = ensplit[1] + "/" + ensplit[0] + "/" + ensplit[2];
				if(!validNoticeDateAndFillingRange(start,end)){
					return false;
				}
			}
		    if($('#caseDate').val() != '' && $('#caDueDate').val() != '' ){
				var start = $('#caseDate').val();
				var end = $('#caDueDate').val();
				var stsplit = start.split("/");
				var ensplit = end.split("/");
				
				start = stsplit[1] + "/" + stsplit[0] + "/" + stsplit[2];
				end = ensplit[1] + "/" + ensplit[0] + "/" + ensplit[2];
				if(!validCaDueDatendFillingRange(start,end)){
					return false;
				}
			}
		}
		  
		$('#newlegalcaseForm :not([type=submit])').prop('disabled',false);
		$(".btn-primary").prop('disabled',false);
		document.forms[0].submit;
		return true;
		event.preventDefault();		
	});    
    
    var assignPosition = new Bloodhound({
		datumTokenizer : function(datum) {
			return Bloodhound.tokenizers
					.whitespace(datum.value);
		},
		queryTokenizer : Bloodhound.tokenizers.whitespace,
		remote : {
			url : '/services/lcms/ajax/getposition', 
			replace : function(url, uriEncodedQuery) {
				return url + '?positionName=' + uriEncodedQuery;

			},
			filter : function(data) {		
				return $.map(data, function(value, key) {					
					return {
						name : value,
						value : key
					};					
				});
			}
		}
	});
	
	assignPosition.initialize();
	var typeaheadobj = $('#positionName').typeahead({
		hint: false,
		highlight: false,
		minLength: 3
	},  {
		displayKey : 'name',
		source : assignPosition.ttAdapter()
	});
	
	typeaheadWithEventsHandling(typeaheadobj, '#positionId');
});

function validCaseRecievingAndFillingRange(start, end) {
    var startDate = Date.parse(start);
    var endDate = Date.parse(end);
	
    // Check the date range, 86400000 is the number of milliseconds in one day
    var difference = (endDate - startDate) / (86400000 * 7);
    if (difference < 0) {
    	bootbox.alert("Case Receiving Date should not be less than Case Filling Date");
		$('#end_date').val('');
		return false;
	} else {
		return true;
	}
    return true;
}

function validNoticeDateAndFillingRange(start, end) {
    var startDate = Date.parse(start);
    var endDate = Date.parse(end);
	
    // Check the date range, 86400000 is the number of milliseconds in one day
    var difference = (endDate - startDate) / (86400000 * 7);
    if (difference < 0) {
    	bootbox.alert("Notice Date should not be less than Case Filling Date");
		$('#end_date').val('');
		return false;
	} else {
		return true;
	}
    return true;
}

function validCaDueDatendFillingRange(start, end) {
    var startDate = Date.parse(start);
    var endDate = Date.parse(end);
	
    // Check the date range, 86400000 is the number of milliseconds in one day
    var difference = (endDate - startDate) / (86400000 * 7);
    if (difference < 0) {
    	bootbox.alert("Counter Affidavit Due Date should not be less than Case Filling Date");
		$('#end_date').val('');
		return false;
	} else {
		return true;
	}
    return true;
}
function checkLCType(){
	 if($('#lcNumberType').val() == "MANUAL"){
		 $(".show-ManualLcNumber").show();
	 }else{
		 $(".show-ManualLcNumber").hide(); 
	 }
	 document.getElementById("lcNumber").value="";	
	 document.getElementById("finwpYear").value=""; 
}
	
function addPetRow(){     
	var tableObj=document.getElementById('petitionDetails');
	var tbody=tableObj.tBodies[0];
	var lastRow = tableObj.rows.length;
	var rowObj = tableObj.rows[1].cloneNode(true);
	 var phoneno = /^\d{10}$/;  
	nextIdx=(lastRow-1);
	var currentROwIndex=nextIdx-1;
	jQuery(rowObj).find("input, select").each(
			function() {
			
			jQuery(this).attr({
						'id' : function(_, id) {
							return id.replace('[0]', '['
									+ nextIdx + ']');
						},
						'name' : function(_, name) {
							return name.replace('[0]', '['
									+ nextIdx + ']');
							
						}
			});  
   });
   tbody.appendChild(rowObj);			
   $('#petitionDetails tbody tr:last').find('input').val('');
   generateSno(".petitionDetails");		   
}

function generateSno(tablenameclass)
{
	$(tablenameclass+'.spansno').each(function(idx){
		$(this).html(""+(idx+1));
	});
}

function addResRow()
{     
	var index=document.getElementById('respondantDetails').rows.length-1;
	var tableObj=document.getElementById('respondantDetails');
	var tbody=tableObj.tBodies[0];
	var lastRow = tableObj.rows.length;
	var rowObj = tableObj.rows[1].cloneNode(true);
	
	nextIdx=(lastRow-1);
	var currentROwIndex=nextIdx-1;
	jQuery(rowObj).find("input, select").each(
			function() {
			
			jQuery(this).attr({
						'id' : function(_, id) {
							return id.replace('[0]', '['
									+ nextIdx + ']');
						},
						'name' : function(_, name) {
							return name.replace('[0]', '['
									+ nextIdx + ']');
							
						}
			});  
   });


   tbody.appendChild(rowObj);
   $('#respondantDetails tbody tr:last').find('input').val('');
   generateSno(".respondantDetails");
		
}

function addPetEditRow(){     
	var tableObj=document.getElementById('petitionDetails');
	var tbody=tableObj.tBodies[0];
	var lastRow = tableObj.rows.length;
	var rowObj = tableObj.rows[1].cloneNode(true);
	
	nextIdx=(lastRow-1);
	var currentROwIndex=nextIdx-1;
	jQuery(rowObj).find("input, select").each(
			function() {
			
			jQuery(this).attr({
						'id' : function(_, id) {
							return id.replace('['+ currentROwIndex +']', '['
									+ nextIdx + ']');
						},
						'name' : function(_, name) {
							return name.replace('[' + currentROwIndex + ']', '['
									+ nextIdx + ']');
							
						}
			});  
   });
   tbody.appendChild(rowObj);		   
   generateSno(".petitionDetails");			
}

function addResEditRow(){     
	var index=document.getElementById('respondantDetails').rows.length-1;
	var tableObj=document.getElementById('respondantDetails');
	var tbody=tableObj.tBodies[0];
	var lastRow = tableObj.rows.length;
	var rowObj = tableObj.rows[1].cloneNode(true);
	
	nextIdx=(lastRow-1);
	var currentROwIndex=nextIdx;
	nextIdx=nextIdx+1;
	jQuery(rowObj).find("input, select").each(
			function() {
			
			jQuery(this).attr({
				'id' : function(_, id) {
					return id.replace('['+ currentROwIndex +']', '[' + nextIdx + ']');
				},
				'name' : function(_, name) {
					return name.replace('[' + currentROwIndex + ']', '[' + nextIdx + ']');							
				}
			});
   });

   validatePhone(contactNumber);
   tbody.appendChild(rowObj);
   generateSno(".respondantDetails");		
}

$(document).on('click',"#pet_delete_row",function (){
	var table = document.getElementById('petitionDetails');
    var rowCount = table.rows.length;
    var counts = rowCount - 1;
    var k = 2;
    var m;
    if(counts==1)
	{
		bootbox.alert("This Row cannot be deleted");
		return false;
	}else{	

		$(this).closest('tr').remove();		
		
		jQuery("#petitionDetails tr:eq(1) td span[alt='AddF']").show();
		//starting index for table fields
		var idx=0;
		
		//regenerate index existing inputs in table row
		jQuery("#petitionDetails tr:not(:first)").each(function() {
			jQuery(this).find("input, select").each(function() {
			   jQuery(this).attr({
			      'id': function(_, id) {  
			    	  return id.replace(/\[.\]/g, '['+ idx +']'); 
			       },
			      'name': function(_, name) {
			    	  return name.replace(/\[.\]/g, '['+ idx +']'); 
			      },
			   });
			  });
			
			idx++;
		});
		
		generateSno(".petitionDetails");
		
		return true;
	}
});



function onChangeofPetitioncheck(obj){
	if ( $(obj).is(':checked')) {
    	console.log('Checkbox checked');
    	$(obj).closest('tr').find("select").removeAttr("disabled");
    }else{
    	console.log('Checkbox not checked');
    	$(obj).closest('tr').find("select").attr("disabled", "disabled");
    }		
}

$(document).on('click',"#res_delete_row",function (){
	var table = document.getElementById('respondantDetails');
    var rowCount = table.rows.length;
    var counts = rowCount - 1;
    var j = 2;
    var i;
    if(counts==1)
	{
		bootbox.alert("This Row cannot be deleted");
		return false;
	}else{	

		$(this).closest('tr').remove();		
		
		jQuery("#respondantDetails tr:eq(1) td span[alt='AddF']").show();
		//starting index for table fields
		var idx=0;
		
		//regenerate index existing inputs in table row
		jQuery("#respondantDetails tr:not(:first)").each(function() {
			jQuery(this).find("input, select").each(function() {
			   jQuery(this).attr({
			      'id': function(_, id) {  
			    	  return id.replace(/\[.\]/g, '['+ idx +']'); 
			       },
			      'name': function(_, name) {
			    	  return name.replace(/\[.\]/g, '['+ idx +']'); 
			      },
			   });
			   
			  
			   
		    });
			idx++;
		});
		
		generateSno(".respondantDetails");
		
		return true;
	}
});

$(document).on('keyup','.validateZero', function(){
	  var valid = /^[1-9],,()OR?$/.test(this.value),
	  val = this.value;
	  
	  if(!valid){
	    console.log("Invalid input!");
	    this.value = val.substring(0, val.length - 1);
	   }
	});

$('#btnclose').click(function(){
	bootbox.confirm({
	    message: 'Information entered in this screen will be lost if you close this page ? Please confirm if you want to close. ',
	    buttons: {
	        'cancel': {
	            label: 'No',
	            className: 'btn-default pull-right'
	        },
	        'confirm': {
	            label: 'Yes',
	            className: 'btn-danger pull-right'
	        }
	    },
	    callback: function(result) {
	        if (result) {
	             window.close();
	        }
	    }
	});
	
});

$('#reappealOfCase').click(function() {
	$('#appealNum').val('');
	if ($(this).is(':checked')) {
		$('.reappealcase').show();
		$('#appealNum').attr('required', 'required');
		$('#prevCaseYear').attr('required', 'required');
		$('#prevCourtType').attr('required', 'required');
		$('#prevPetitionType').attr('required', 'required');
	}else{
		$('.reappealcase').hide();
		$('#appealNum').removeAttr('required');
		$('#prevCaseYear').removeAttr('required');
		$('#prevCourtType').removeAttr('required');
		$('#prevPetitionType').removeAttr('required');
	}
});


/*function showToggle() {
	debugger;
		  var x = document.getElementById("myDIV");
		  if (x.style.display === "none") {
		    x.style.display = "block";
		  } else {
		    x.style.display = "none";
		  }
		}*/

function showToggle(){
	  document.getElementById('myDIV').style.display = 'block';
	}

function hideToggle(){
	debugger;
	  document.getElementById('myDIV').style.display ='none';
	}



function importantGuide()
{
	bootbox.alert('Guide line !!');
}




// added by kundan here

function addDefendingCounsilRow()
{   
	
	var index=document.getElementById('defendingCounsilDetails').rows.length-1;
	var tableObj=document.getElementById('defendingCounsilDetails');
	var tbody=tableObj.tBodies[0];
	var lastRow = tableObj.rows.length;
	var rowObj = tableObj.rows[1].cloneNode(true);
	console.log($(rowObj).html());
	debugger;
	
	nextIdx=(lastRow-1);
	var currentROwIndex=nextIdx-1;
	jQuery(rowObj).find("input, select").each(
			function() {
			
			jQuery(this).attr({
						'id' : function(_, id) {
							return id.replace('[0]', '['
									+ nextIdx + ']');
						},
						'name' : function(_, name) {
							return name.replace('[0]', '['
									+ nextIdx + ']');
							
						}
			});  
   });


   tbody.appendChild(rowObj);
   $('#defendingCounsilDetails tbody tr:last').find('input[type=text]').val('');
   $('#defendingCounsilDetails tbody tr:last').find('option').attr("selected",null);
   $('#defendingCounsilDetails tbody tr:last').find('input[type=checkbox]').prop('checked', false);
//   $(':checkbox').prop('checked', false).removeAttr('checked');
//   var mainTable = $('#mainContainerDiv');
//   var tr = mainTable.find('tbody tr');
//   tr.find('div[id^="worklist_"]').empty();
//   $('#defendingCounsilDetails tbody tr:last').find('div[id^="worklist_"]').css('display','block');
   generateSno(".defendingCounsilDetails");
		
}

$(document).on('click',"#counsil_delete_row",function (){
	var table = document.getElementById('defendingCounsilDetails');
    var rowCount = table.rows.length;
    var counts = rowCount - 1;
    var j = 2;
    var i;
    if(counts==1)
	{
		bootbox.alert("This Row cannot be deleted");
		return false;
	}else{	

		$(this).closest('tr').remove();		
		
		jQuery("#defendingCounsilDetails tr:eq(1) td span[alt='AddF']").show();
		//starting index for table fields
		var idx=0;
		
		//regenerate index existing inputs in table row
		jQuery("#defendingCounsilDetails tr:not(:first)").each(function() {
			jQuery(this).find("input, select").each(function() {
			   jQuery(this).attr({
			      'id': function(_, id) {  
			    	  return id.replace(/\[.\]/g, '['+ idx +']'); 
			       },
			      'name': function(_, name) {
			    	  return name.replace(/\[.\]/g, '['+ idx +']'); 
			      },
			   });
			   
			  
			   
		    });
			idx++;
		});
		
		generateSno(".defendingCounsilDetails");
		
		return true;
	}
});
//var email;
//var mobile;
//function fillvalue(name,id){
//	debugger;
//	var columnid=id;
//	alert("::: "+columnid);
//	var ids=name;
//	var vale = document.getElementById(ids).value;
//	console.log("name--"+name);
//	console.log("name--"+vale);
//	
//	$.ajax({
//		 url:'/services/lcms/popup/getboq/'+vale,
//		 contentType:"application/json",		
//		 dataType:"json",
//		 success:function(r)
//		 {
//			 debugger;
//			 data=r;
//			 console.log("data-name"+data.mobileNumber);
//			 console.log("data-emial"+data.email);
//			for (var i=0; i<data.length; i++) {
//					
//					console.log(data[i].id+ '------KK----' + data[i].name +'--'+ data[i].mobileNumber + '--'+data[i].email);
////					 tr.find('div[id^="worklist_"]').append("<option value='"+data[i].id+"'   data-name='"+data[i].name+"' data-email='"+data[i].email+"'  data-mobile='"+data[i].mobileNumber+"'   >" + data[i].name+"</option>");
//					 var mainTable = $('#mainContainerDiv');
//					 var tr = mainTable.find('tbody tr');
////					  var row = $(this).closest('tbody');
//					 tr.find('input[type="text"][id$="counselEmail"]').val(data[i].email);
//					 tr.find('input[type="text"][id$="counselphoneNo"]').val(data[i].mobileNumber);
//						
//			
//			}
//		 }
//	
//	 })
//}

$(document).ready(function(){
$('#mainContainerDiv').find('table[id^="defendingCounsilDetails"] > tbody').each(function(){

$(this).on('change', 'select', function() {   
	debugger;
	var name=this.value;
	console.log("vale"+name);
	var row = $(this).closest('tr');
	$.ajax({
		 url:'/services/lcms/popup/getboq/'+name,
		 contentType:"application/json",
		 dataType:"json",
		 success:function(r)
		 {
			 debugger;
			 data=r;
			for (var i=0; i<data.length; i++) {
					
					console.log(data[i].id+ '------KK----' + data[i].name +'--'+ data[i].mobileNumber + '--'+data[i].email);
					row.find('input[type="text"][id$="counselEmail"]').val(data[i].email);
					row.find('input[type="text"][id$="counselphoneNo"]').val(data[i].mobileNumber);
						
			}
		 }
	
	 })
    
  });

});

});







var data;
var oppPartyAdvocate;
function makeFieldAsAutocomplete(field){
	console.log('--makeFieldAsAutocomplete--');
	
	var mainTable = $('#mainContainerDiv');
	var tr = mainTable.find('tbody tr');
	tr.each(function() {
		oppPartyAdvocate = $(this).closest('tr').find('[id$="oppPartyAdvocate"]').val();
	console.log("Name--"+oppPartyAdvocate);
	});
	var tr = $(field).closest('tr');
	tr.find('div[id^="worklist_"]').empty();
	$.ajax({
		 url:'/services/lcms/popup/getboq/'+$(field).val(),
		 contentType:"application/json",		
		 dataType:"json",
		 success:function(r)
		 {
			 debugger;
			 data=r;
			for (var i=0; i<data.length; i++) {
				if(data[i].name!=oppPartyAdvocate)
					{
					
					console.log(data[i].id+ '------KK----' + data[i].name +'--'+ data[i].mobileNumber + '--'+data[i].email);
					 tr.find('div[id^="worklist_"]').append("<option value='"+data[i].id+"'   data-name='"+data[i].name+"' data-email='"+data[i].email+"'  data-mobile='"+data[i].mobileNumber+"'   >" + data[i].name+"</option>");
					 
					}
				 
			
			}
		 }
	 })
	
	
}



$(document).ready(function(){
    
    console.log('--------------------start------------------------');
    $('#mainContainerDiv').find('table[id^="defendingCounsilDetails"] > tbody').each(function(){
    	console.log(this);
    	$(this).on('keyup', 'input[type="text"][id$=".oppPartyAdvocate"]', function(){
    		console.log('keyup');	
    		makeFieldAsAutocomplete(this);	
    		var inputVal1 = $(this).val();
    		console.log("inputVal1--"+inputVal1);
    	});
    	$(this).on('click', 'div[id^="worklist_"] > option', function(){
    		console.log('keyup worklist_  option click' );
    		var row = $(this).closest('tr');
    		row.find('input[type="text"][id$="oppPartyAdvocate"]').val($(this).text());
    		row.find('input[type="text"][id$="counselEmail"]').val($(this).data('email'));
    		row.find('input[type="text"][id$="counselphoneNo"]').val($(this).data('mobile'));
    		var optionDiv = $(this).closest('div[id^="worklist_"]');
    		optionDiv.css('display','none');
    	});
    });
    console.log('--------------------end------------------------');
    
   
 }); //Ready end



$(document).on('click', 'input[type="checkbox"]', function() {      
  if ($(':checkbox').is(':checked')){
	  	var row = $(this).closest('tr');
		row.find('input[type="text"][id$="oppPartyAdvocate"]').prop('checked', true).attr('checked', 'checked');
		$('input[type="checkbox"]').not(this).prop('checked', false); 
  	}
  else {
    $(':checkbox').prop('checked', false).removeAttr('checked');
}     
});


$("#buttonid").on("click",function(){
    if (($("input[name*='defCounsilPrimary']:checked").length)<=0) {
        bootbox.alert("You must check at least one checkbox");
        return false;
    }
    
});

//$("#buttonSubmit").on("click",function(){
//    if (($("input[name*='defCounsilPrimary']:checked").length)<=0) {
//        bootbox.alert("You must check at least one checkbox");
//        return false;
//    }
//    
//});

//$("#petitionTypeMaster").blur(function(){
//	$(':checkbox').prop('checked', false).removeAttr('checked');
//	});

$('#mainContainerDiv').find('table[id^="defendingCounsilDetails"] > tbody').each(function(){
	console.log(this);
	$(this).on('blur', 'input[type="text"][id$=".oppPartyAdvocate"]', function(){
		var row = $(this).closest('tr');
		row.find('input[type="text"][id$="oppPartyAdvocate"]').val("");
		var optionDiv = $(this).closest('tr').find('div[id^="worklist_"]');
		optionDiv.css('display','block');
	});
});


//$('#mainContainerDiv').find('table[id^="defendingCounsilDetails"] > tbody').each(function(){
//$(this).on('click', 'input[type="checkbox"]', function() { 
//	debugger;
//	var row = $(this).closest('tr');
//    if(this.checked)
//    	
//    //	 var citylat 
//        row.find('input[type="text"][id$="defCounsilPrimary"]').val("YES");
////        $("#primaryCounsin").val("YES");
//    
//   else
//
//        row.find('input[type="text"][id$="defCounsilPrimary"]').val("");
//});
//});





$(window).on('load', function () {
	$('#mainContainerDiv').find('table[id^="defendingCounsilDetails"] > tbody').each(function(){
	debugger;
	var bla = $('#defCounsilPrimary').val();
	console.log('checkbox checked--'+bla)
	if(bla=='YES')
		{
		$( '#input[type="checkbox"][id$="defCounsilPrimary"]' ).prop( "checked", true );
		}else
			{
			$( '#input[type="checkbox"][id$="defCounsilPrimary"]'  ).prop( "checked", false );
			}
	});
});
