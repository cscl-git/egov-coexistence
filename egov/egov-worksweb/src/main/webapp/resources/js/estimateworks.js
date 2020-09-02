$('.btn-wf-primary').click(function(){
	var button = $(this).attr('id');
	if (button != null && (button == 'Forward')) {
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		
	 }
	else if (button != null && (button == 'Reject')) {
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
	 }
	   else if (button != null && (button == 'SaveAsDraft')) {
	      
	       if(!validateWorkFlowApprover(button))
	           {
	           
	            return false;
	           }
	        if(true){
	             
	            deleteHiddenSubledgerRow();
	            //$('approvalDesignation').attr('required', false);
	            //$('approvalDepartment').attr('required', false);
	            //$('approvalPosition').attr('required', false);
	            return true;
	        }else
	            {
	             //alert('button-----2.2------'+button);
	            return false;
	            
	            }
	        
	    
	}else if (button != null && (button == 'Create And Approve')) {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').removeAttr('required');
		if(!validateWorkFlowApprover(button))
			return false;
		if(!$("form").valid())
			return false;
		if(validate()){
			deleteHiddenSubledgerRow();
			return true;
		}else
			return false;
	} else{
		if(!validateWorkFlowApprover(button))
			return false;
		
	}
	return false;
});


function validateWorkFlowApprover(name) {
	document.getElementById("workFlowAction").value = name;
	var button = document.getElementById("workFlowAction").value;
	if (button != null && button == 'Submit') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Reject') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').attr('required', 'required');
	}
	 if (button != null && button == 'SaveAsDraft') {
	        
	        $('#approvalDepartment').removeAttr('required');
	        $('#approvalDesignation').removeAttr('required');
	        $('#approvalPosition').removeAttr('required');
	        $('#approvalComent').removeAttr('required');
	     
	        $('#narration').removeAttr('required');
	          $('#billSubType').removeAttr('required');
	    }
	if (button != null && button == 'Cancel') {
		$('#approvalDepartment').removeAttr('required');
		$('#approvalDesignation').removeAttr('required');
		$('#approvalPosition').removeAttr('required');
		$('#approvalComent').attr('required', 'required');
	}
	if (button != null && button == 'Forward') {
		$('#approvalDepartment').attr('required', 'required');
		$('#approvalDesignation').attr('required', 'required');
		$('#approvalPosition').attr('required', 'required');
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Verify') {
		$('#approvalComent').removeAttr('required');
	}
	if (button != null && button == 'Create And Approve') {
		return validateCutOff();
	}else
		return true;
	
	return true;
}