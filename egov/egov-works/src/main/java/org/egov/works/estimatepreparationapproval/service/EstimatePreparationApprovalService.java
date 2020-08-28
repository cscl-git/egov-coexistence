package org.egov.works.estimatepreparationapproval.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EstimatePreparationApprovalService {

	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;

	@Transactional
	public EstimatePreparationApproval saveEstimatePreparationData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub

		List<BoQDetails> list = new ArrayList<BoQDetails>();
		for (BoQDetails boq : estimatePreparationApproval.getBoQDetailsList()) {
			boq.setEstimatePreparationApproval(estimatePreparationApproval);
			list.add(boq);
		}
		estimatePreparationApproval.setNewBoQDetailsList(list);

		EstimatePreparationApproval savedEstimatePreparationApproval = estimatePreparationApprovalRepository
				.save(estimatePreparationApproval);
		return savedEstimatePreparationApproval;
	}

}
