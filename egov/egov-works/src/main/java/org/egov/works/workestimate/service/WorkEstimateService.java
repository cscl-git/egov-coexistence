package org.egov.works.workestimate.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkEstimateService {

	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;

	@Transactional
	public List<EstimatePreparationApproval> searchWorkEstimateData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub
		List<EstimatePreparationApproval> workEstimateDetails = estimatePreparationApprovalRepository.findByAllParams(
				estimatePreparationApproval.getWorkCategory(), estimatePreparationApproval.getEstimateNumber(),
				estimatePreparationApproval.getExecutingDivision(), estimatePreparationApproval.getFromDt(),
				estimatePreparationApproval.getToDt());

		return workEstimateDetails;
	}
/*
	@Transactional
	public List<EstimatePreparationApproval> searchBoqData(HttpServletRequest request, boolean checked,
			Long estimatePreparationId) {
		List<EstimatePreparationApproval> boqDetailsList = estimatePreparationApprovalRepository
				.findByIsCheckedAndEstimatePreparationId(checked, estimatePreparationId);

		//return boqDetailsList;
		return null;
	}*/

	@Transactional
	public List<EstimatePreparationApproval> searchBoqData(HttpServletRequest request, Long estimatePreparationId) {
		// TODO Auto-generated method stub
		List<EstimatePreparationApproval> boqDetailsList = estimatePreparationApprovalRepository
				.findByEstimatePreparationId(estimatePreparationId);

		//return boqDetailsList;
		return boqDetailsList;
		
	}
}
