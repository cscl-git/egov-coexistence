package org.egov.works.estimatesearch.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.repository.WorkOrderAgreementRepository;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchEstimateService {

	@Autowired
	private WorkOrderAgreementRepository workOrderAgreementRepository;

	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;

	@Transactional
	public List<EstimatePreparationApproval> searchEstimateData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub

		List<EstimatePreparationApproval> searchEstimateDetails = estimatePreparationApprovalRepository
				.findByAllParameters(estimatePreparationApproval.getWorkStatus(),
						estimatePreparationApproval.getWorkCategory(),
						estimatePreparationApproval.getExecutingDivision(), estimatePreparationApproval.getWorkName(),
						estimatePreparationApproval.getWorkLocation(), estimatePreparationApproval.getSectorNumber(),
						estimatePreparationApproval.getWardNumber(), estimatePreparationApproval.getFundSource(),
						estimatePreparationApproval.getEstimateAmount(), estimatePreparationApproval.getTenderCost(),
						estimatePreparationApproval.getAgencyWorkOrder(), estimatePreparationApproval.getDate(),
						estimatePreparationApproval.getTimeLimit());

		return searchEstimateDetails;
	}

	@Transactional
	public List<WorkOrderAgreement> searchWorkData(HttpServletRequest request, WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub
		List<WorkOrderAgreement> searchWorkDatas = workOrderAgreementRepository.findByAllParameters(
				workOrderAgreement.getWork_agreement_status(), workOrderAgreement.getCategory(),
				workOrderAgreement.getExecuting_department(), workOrderAgreement.getName_work_order(),
				workOrderAgreement.getWorkType(), workOrderAgreement.getWorkLocation(), workOrderAgreement.getSector(),
				workOrderAgreement.getWardNumber(), workOrderAgreement.getFund(), workOrderAgreement.getEstimatedCost(),
				workOrderAgreement.getTenderCost(), workOrderAgreement.getAgencyWorkOrder(),
				workOrderAgreement.getDate(), workOrderAgreement.getTimeLimit());

		return searchWorkDatas;
	}

}
