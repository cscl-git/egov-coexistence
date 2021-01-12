/*$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	document.getElementById("workFlowAction").value = button;
	
});*/


function setWorkflow(action)
{
	
	document.getElementById("workFlowAction").value = action;
	if(document.getElementById('boQDetailsList[0].amount') == null)
		{
			bootbox.alert("Please upload BOQ details");
			return false;
		}
	else 
		{
			if(action == 'Forward')
				{
					if(document.getElementById('approvalPosition') != null && document.getElementById('approvalPosition').value == '')
						{
							bootbox.alert("Please select Approver");
							return false;
						}
				}
			else if (action == 'Approve')
				{
					if(document.getElementById('approvalPosition') != null && document.getElementById('approvalPosition').value == '')
						{
						bootbox.alert("Please select Approver");
						return false;
						}
				}
		return true;
		}
	
}

function openEstimate(estId)
{
	var url = "/services/works/dnit/view/"+ estId;
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


