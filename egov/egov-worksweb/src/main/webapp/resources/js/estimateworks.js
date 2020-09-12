/*$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	document.getElementById("workFlowAction").value = button;
	
});*/


function setWorkflow(action)
{
	alert('action :::'+action);
	document.getElementById("workFlowAction").value = action;
}


