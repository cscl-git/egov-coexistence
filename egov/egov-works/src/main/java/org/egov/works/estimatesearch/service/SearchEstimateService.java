package org.egov.works.estimatesearch.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.repository.WorkOrderAgreementRepository;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchEstimateService {

	@Autowired
	private WorkOrderAgreementRepository workOrderAgreementRepository;

	@Transactional
	public List<EstimatePreparationApproval> searchEstimateData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	public List<WorkOrderAgreement> searchWorkData(HttpServletRequest request, WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub
		return null;
	}

	
	

}
