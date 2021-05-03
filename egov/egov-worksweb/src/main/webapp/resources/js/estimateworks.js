	
var data;
function makeFieldAsAutocomplete(field){
	console.log('--makeFieldAsAutocomplete--');
	var tr = $(field).closest('tr');
	tr.find('div[id^="worklist_"]').empty();
	$.ajax({
		 url:'/services/works/popup/getboq/'+$(field).val(),
		 contentType:"application/json",		
		 dataType:"json",
		 success:function(r)
		 {
			 data=r;
			for (var i=0; i<data.length; i++) {
				 console.log(data[i].id+ '------KK----' + data[i].ref_dsr +'--'+ data[i].item_description + '--'+data[i].unit+'--'+data[i].rate);
				 tr.find('div[id^="worklist_"]').append("<option value='"+data[i].id+"'   data-description='"+data[i].item_description+"'  data-rate1='"+data[i].rate+"' data-unit='"+data[i].unit+"'        >"+data[i].ref_dsr+"</option>");
			}
		 }
	 })
	
}



$(document).ready(function(){
    
    console.log('--------------------start------------------------');
    $('#mainContainerDiv').find('table[id^="boq"] > tbody').each(function(){
    	console.log(this);
    	$(this).on('keyup', 'input[type="text"][id$=".ref_dsr"]', function(){
    		console.log('keyup');	
    		makeFieldAsAutocomplete(this);	
    	});
    	$(this).on('click', 'div[id^="worklist_"] > option', function(){
    		console.log('keyup worklist_  option click' );
    		var row = $(this).closest('tr');
    		
    		row.find('input[type="text"][id$="ref_dsr"]').val($(this).text());
    		row.find('input[type="text"][id$="item_description"]').val($(this).data('description'));
    		row.find('input[type="number"][id$="rate"]').val($(this).data('rate1'));
    		row.find('input[type="text"][id$="unit"]').val($(this).data('unit'));
//    		row.find('input[type="text"][id$="milestone"]').val('FOR THE WORK OF "STRENGTHENING OF SWD SYSTEM AT PHIRNI ROAD AND VARIOUS LOCATIONS IN VILLAGE BURAIL UT, CHANDIGARH ');
    		
//    		var rate = parseFloat($("[id*=rate]").val());
//          var quantity = parseFloat($("[id*=quantity]").val());
//          var total = parseFloat(rate * quantity);
//            $("[id*=lblPrice]").val(total);
//    		row.find('input[type="number"][id$="amount"]').val(total);
    		var optionDiv = $(this).closest('div[id^="worklist_"]');
    		optionDiv.css('display','none');
    		
    		
    	});
    });
    console.log('--------------------end------------------------');
    
   
 }); //Ready end






$( function() {
    $( "#work_intended_date" ).datepicker();
  } );


/*$(document).ready(function(){
	console.log('work_intended_date');
	$("work_intended_date").datetimepicker({
	    format: 'DD/MM/YYYY',
	}).on('changeDate', function() {
	    $('.datepicker').hide();
	});
});*/

function setWorkflow(action)
{
	
	document.getElementById("workFlowAction").value = action;
	if(action == null)
		{
			return false;
		}
	else 
		{
			if(action == 'Forward/Reassign')
				{
					if(document.getElementById('approvalPosition') != null && document.getElementById('approvalPosition').value == '')
						{
							bootbox.alert("Please select Approver");
							return false;
						}
					if(document.getElementById('approvalComent') != null && document.getElementById('approvalComent').value == '')
					{
						bootbox.alert("Please select Approval Comment");
						return false;
					}
					
					if(document.getElementById('wardCheck') != null && document.getElementById('wardCheck').value != '')
					{
					var wardcheck=document.getElementById('wardCheck').value;
					console.log("wardcheck ::: "+wardcheck);
					var pos=document.getElementById('approvalDesignation').value;
					console.log("pos ::: "+pos);
						if(document.getElementById('approvalDesignation').value == '214' && (document.getElementById('wardCheck').value =='Deposit Estimate works' || document.getElementById('wardCheck').value =='Ward Development Funds'))
							{
							bootbox.alert("Cannot for Forward to Commissioner as this is "+document.getElementById('wardCheck').value);
							return false;
							}
					}
					if(document.getElementById('stateType') != null && document.getElementById('stateType').value !='' && document.getElementById('stateType').value == 'DNITCreation')
						{
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With EXECUTIVE ENGINEER')
								{
									if(document.getElementById('approvalDesignation').value == '251')
										{
											if(parseInt(document.getElementById('estimateAmount').value) <= 1000000 )
												{
												bootbox.alert("Cannot Forward as the Amount is less than 10 Lakhs");
												return false;
												}
										}
								}
							
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With SUPERINTENDENT')
							{
								if(document.getElementById('approvalDesignation').value == '217')
								{
									if(parseInt(document.getElementById('estimateAmount').value) <= 5000000 )
										{
										bootbox.alert("Cannot Forward as the Amount is less than 50 Lakhs");
										return false;
										}
								}
							}
							
						}
				}
			else if (action == 'Approve')
				{
					if(document.getElementById('approvalComent') != null && document.getElementById('approvalComent').value == '')
					{
						bootbox.alert("Please select Approval Comment");
						return false;
					}
					if(document.getElementById('wardCheck') != null && document.getElementById('wardCheck').value != '')
						{
						var wardcheck=document.getElementById('wardCheck').value;
						console.log("wardcheck ::: "+wardcheck);
						var pos=document.getElementById('approvalDesignation').value;
						console.log("pos ::: "+pos);
							if(document.getElementById('approvalDesignation').value == '214' && (document.getElementById('wardCheck').value =='Deposit Estimate works' || document.getElementById('wardCheck').value =='Ward Development Funds'))
								{
								bootbox.alert("Cannot for Forward to Commissioner as this is "+document.getElementById('wardCheck').value);
								return false;
								}
						}
				}
		return true;
		}
	
}

function openEstimate(estId)
{
	var url = "/services/works/estimatePreparation/view/"+ estId;
	window.open(url,'','width=900, height=700');
}

function editEstimate(estId)
{
	var url = "/services/works/estimatePreparation/edit/"+ estId;
	window.open(url,'','width=900, height=700');
}

function openTender(tenderId)
{
	var url = "/services/works/tenderProcurement/view/"+ tenderId;
	window.open(url,'','width=900, height=700');
}

function openBG(bgId)
{
	var url = "/services/works/bgSecurity/view/"+ bgId;
	window.open(url,'','width=900, height=700');
}

function openWork(woId)
{
	var url = "/services/works/boq/edit/"+ woId;
	window.open(url,'','width=900, height=700');
}

function openWorkView(woId)
{
	var url = "/services/works/boq/view/"+ woId;
	window.open(url,'','width=900, height=700');
}

function openClosure(woId)
{
	var url = "/services/works/boq/closureDetails/"+ woId;
	window.open(url,'','width=900, height=700');
}
function openProgress(woId)
{
	var url = "/services/works/boq/progress/"+ woId;
	window.open(url,'','width=900, height=700');
}
function openDNIT(estId)
{
	var url = "/services/works/dnit/view/"+ estId;
	window.open(url,'','width=900, height=700');
}
function editDNIT(estId)
{
	var url = "/services/works/dnit/editdnit/"+ estId;
	window.open(url,'','width=900, height=700');
}
function deletedata(empid,slno)
{
	var url = "/services/works/dnit/deletednit/"+ empid+"/"+slno;
	
	window.location.href = url;
	}
function openBOQ(estId)
{
	
	var url = "/services/works/boq/viewBoq/"+ estId;
	window.open(url,'','width=900, height=700');
	}
function updateBOQ(estId)
{
	
	var url = "/services/works/boq/updateBoq/"+ estId;
	window.open(url,'','width=900, height=700');
}
function filecheck(){
	
	var file=document.getElementById('file');
	if(file.files.length >0){
		
		return true;
	}else{
		bootbox.alert("Please Select File.")
		return false;
	}
}
