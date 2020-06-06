package org.egov.apnimandi.transactions.service.workflow;

import org.egov.apnimandi.transactions.entity.ApnimandiContractor;

public interface ApnimandiContractorWorkflowCustom {
	public void createCommonWorkflowTransition(ApnimandiContractor apnimandiContractor, Long approvalPosition, String approvalComent, String workFlowAction);
}
