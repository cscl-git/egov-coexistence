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


