function setWorkflow(action)
{
	var a=document.getElementById('boQDetailsList[0].milestone');
	var b=document.getElementById('boQDetailsList[0].item_description');
	var c=document.getElementById('boQDetailsList[0].ref_dsr');
	var d=document.getElementById('boQDetailsList[0].unit');
	var e=document.getElementById('boQDetailsList[0].rate');
	var f=document.getElementById('boQDetailsList[0].quantity');
	
	var file=document.getElementById('file');
	
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
					if(document.getElementById('estimateDate') != null && document.getElementById('estimateDate').value == '')
					{
						bootbox.alert("Please select  Estimate Date");
						document.getElementById('estimateDate').focus();
						return false;
					}
					if(document.getElementById('workName') != null && document.getElementById('workName').value == '')
					{
						bootbox.alert("Please Fill Name of Work");
						document.getElementById('workName').focus();
						return false;
					}
					if(document.getElementById('necessity') != null && document.getElementById('necessity').value == '')
					{
						bootbox.alert("Please Fill Necessity");
						document.getElementById('necessity').focus();
						return false;
					}
					if(document.getElementById('workScope') != null && document.getElementById('workScope').value == '')
					{
						bootbox.alert("Please Fill Provision/Scope of work");
						document.getElementById('workScope').focus();
						return false;
					}
					if(document.getElementById('wardCheck') != null && document.getElementById('wardCheck').value == '')
					{
						bootbox.alert("Please Select Expenditure Head.");
						document.getElementById('wardCheck').focus();
						return false;
					}
					if(a != null && a!="" && b != null && b!="" && c != null && c!="" && d != null && d!="" && e != null && e!="" && f != null && f!=""){
						
						return true;
					}else{
						bootbox.alert("Please Upload Boq File.")
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
					//ts
					if(document.getElementById('stateType') != null && document.getElementById('stateType').value !='' && document.getElementById('stateType').value == 'EstimatePreparationApproval' && document.getElementById('estStatus') != null && document.getElementById('estStatus').value !='' && document.getElementById('estStatus').value == 'TS Pending for Approval')
						{
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With EXECUTIVE ENGINEER')
								{
									if(document.getElementById('approvalDesignation').value == '251')
									{
										if(parseInt(document.getElementById('estimatedCost').value) <= 1000000 )
											{
											bootbox.alert("Cannot Forward as the Amount is less than 10 Lakhs");
											return false;
											}
									}
								}
							
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With SUPERINTENDENT ENGINEER')
							{
								if(document.getElementById('approvalDesignation').value == '217')
								{
									if(parseInt(document.getElementById('estimatedCost').value) <= 5000000 )
										{
										bootbox.alert("Cannot Forward as the Amount is less than 50 Lakhs");
										return false;
										}
								}
							}
								
								
						}
					//dnit
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
					if(document.getElementById('stateType') != null && document.getElementById('stateType').value !='' && document.getElementById('stateType').value == 'EstimatePreparationApproval' && document.getElementById('estStatus') != null && document.getElementById('estStatus').value !='' && document.getElementById('estStatus').value == 'TS Pending for Approval')
						{
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With EXECUTIVE ENGINEER')
								{
									if(parseInt(document.getElementById('estimatedCost').value) > 1000000 )
									{
										bootbox.alert("Cannot Approve as the Amount is more than 10 Lakhs");
										return false;
									}
								}
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With SUPERINTENDENT ENGINEER')
							{
								if(parseInt(document.getElementById('estimatedCost').value) > 5000000 )
								{
									bootbox.alert("Cannot Approve as the Amount is more than 50 Lakhs");
									return false;
								}
								if(document.getElementById('prevState') != null && document.getElementById('prevState').value != null && document.getElementById('prevState').value != '' && document.getElementById('prevState').value != 'Pending With CIRCLE HEAD DRAFTSMAN')
									{
										bootbox.alert("Cannot Approve as estimate is not sent to Circle Head Draftsman");
										return false;
									}
							}
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With CHIEF ENGINEER')
							{
								if(document.getElementById('prevState') != null && document.getElementById('prevState').value != null && document.getElementById('prevState').value != '' && document.getElementById('prevState').value != 'Pending With HEAD DRAFTSMAN')
									{
										bootbox.alert("Cannot Approve as estimate is not sent to Head Draftsman");
										return false;
									}
							}
						}
					
					//dnit
					if(document.getElementById('stateType') != null && document.getElementById('stateType').value !='' && document.getElementById('stateType').value == 'DNITCreation')
						{
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With EXECUTIVE ENGINEER')
							{
								if(parseInt(document.getElementById('estimateAmount').value) > 1000000 )
									{
									bootbox.alert("Cannot Approve DNIT as the Amount is more than 10 Lakhs");
									return false;
									}
								if(document.getElementById('prevState') != null && document.getElementById('prevState').value != null && document.getElementById('prevState').value != '' && document.getElementById('prevState').value != 'Pending With SUPERINTENDENT')
								{
								bootbox.alert("Cannot Approve as DNIT as the Amount is not sent to Executive Engineer");
								return false;
								}
							}
							if(document.getElementById('currentState') != null && document.getElementById('currentState').value !='' && document.getElementById('currentState').value == 'Pending With SUPERINTENDENT')
							{
								if(document.getElementById('prevState') != null && document.getElementById('prevState').value != null && document.getElementById('prevState').value != '' && document.getElementById('prevState').value != 'Pending With EXECUTIVE ENGINEER')
								{
								bootbox.alert("Cannot Approve as DNIT as the Amount is not sent to Executive Engineer");
								return false;
								}
							}
						}
				}
		return true;
		}
	
}
function deleteestimate(estId,pendingwith){
	var curr=document.getElementById('createdbyuser1');
	
	if(curr!=null && curr!=='' && pendingwith !=null && pendingwith!=''){
		var curr1=document.getElementById('createdbyuser1').value;
		if(curr1===pendingwith){
	$.ajax({
		url: "/services/works/estimatePreparation/deleteestimate",     
		type: "GET",
		data: {
			id : estId
			
		},
		dataType: "json",
		success: function (response) {
			console.log("success  "+response);
			if(response==='success'){
				window.location.reload();
			}
			
		}, 
		error: function (response) {
			//bootbox.alert('json fail');
			console.log("failed");
		}
	});
			return true;
		}else{
			bootbox.alert("Can be deleted by owner Only.")
			return false;
		}
	}else{
		return false;
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
$(document).ready(function(){
	/*$(window).unload(function(){
		parent.window.opener.inboxloadmethod();
	});*/
	$(window).on("unload", function(e) {
	   console.log("Unloading Function");
	    parent.window.opener.inboxloadmethod();
	});
});
