/*$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	document.getElementById("workFlowAction").value = button;
	
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
	alert(estId);
	var url = "/services/works/boq/viewBoq/"+ estId;
	window.open(url,'','width=900, height=700');
	}
function updateBOQ(estId)
{
	alert(estId);
	var url = "/services/works/boq/updateBoq/"+ estId;
	window.open(url,'','width=900, height=700');
}
