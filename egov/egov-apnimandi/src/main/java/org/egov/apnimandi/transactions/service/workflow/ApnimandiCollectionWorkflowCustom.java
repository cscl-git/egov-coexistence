package org.egov.apnimandi.transactions.service.workflow;

import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;

public interface ApnimandiCollectionWorkflowCustom {
	public void createCommonWorkflowTransition(ApnimandiCollectionDetails apnimandiCollectionDetails, Long approvalPosition, String approvalComent, String workFlowAction);
}
