
$(document).ready(function(){
	
});

function getLocale(paramName){
	return getCookie(paramName) ? getCookie(paramName) : navigator.language;
}

function getCookie(name){
	let cookies = document.cookie;
	if(cookies.search(name) != -1){
		var keyValue = cookies.match('(^|;) ?' + name + '=([^;]*)(;|$)');
	    return keyValue ? keyValue[2] : null;
	}
}

function openHistory(auditId,checkListId){
	var sourcepath="/services/audit/createAudit/history/"+auditId+"/"+checkListId;
			window.open(sourcepath,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');  
	}

function setWorkFLowAction(name)
{
	console.log(name);
	document.getElementById('workFlowAction').value=name;
	
}

function openSource(sourcepath){
	window.open(sourcepath,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');  
}

function openBill(url){
	window.open(url,'Source','resizable=yes,scrollbars=yes,left=300,top=40, width=900, height=700');  
}
