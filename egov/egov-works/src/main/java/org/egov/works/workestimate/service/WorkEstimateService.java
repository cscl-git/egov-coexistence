package org.egov.works.workestimate.service;

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
public class WorkEstimateService {

	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;

	@Transactional
	public List<EstimatePreparationApproval> searchWorkEstimateData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub
		List<EstimatePreparationApproval> workEstimateDetails = new ArrayList<EstimatePreparationApproval>();
		if (estimatePreparationApproval.getEstimateNumber() != null
				&& !estimatePreparationApproval.getEstimateNumber().isEmpty()) {
			workEstimateDetails = estimatePreparationApprovalRepository
					.findByEstimateNumber(estimatePreparationApproval.getEstimateNumber());
		}else {
		workEstimateDetails = estimatePreparationApprovalRepository.findByAllParams(
					 estimatePreparationApproval.getEstimateNumber(),
				estimatePreparationApproval.getExecutingDivision(), estimatePreparationApproval.getFromDt(),
				estimatePreparationApproval.getToDt());
		}

		return workEstimateDetails;
	}
	
	@Transactional
	public EstimatePreparationApproval searchEstimateData(Long estimatePreparationId) {
		// TODO Auto-generated method stub
		EstimatePreparationApproval estimatePreparationApproval =estimatePreparationApprovalRepository
				.findById(estimatePreparationId);

		return estimatePreparationApproval;
	}

	@Transactional
	public List<EstimatePreparationApproval> searchWorkEstimate(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub
		List<EstimatePreparationApproval> workEstimateDetails = new ArrayList<EstimatePreparationApproval>();
		if (estimatePreparationApproval.getExecutingDivision() != null
				&& estimatePreparationApproval.getFromDt() == null && estimatePreparationApproval.getToDt() == null) {
			workEstimateDetails = estimatePreparationApprovalRepository
					.findByExecutingDivisionAndStatusId(estimatePreparationApproval.getExecutingDivision());
		} else {
			workEstimateDetails = estimatePreparationApprovalRepository.findByParams(
					estimatePreparationApproval.getExecutingDivision(), estimatePreparationApproval.getFromDt(),
					estimatePreparationApproval.getToDt());
		}
		return workEstimateDetails;
		}

	@Transactional
	public EstimatePreparationApproval searchBoqData(HttpServletRequest request, Long estimatePreparationId) {
		// TODO Auto-generated method stub
		EstimatePreparationApproval boqDetailsList = estimatePreparationApprovalRepository
				.findById(estimatePreparationId);

		return boqDetailsList;
	}

	@Transactional
	public EstimatePreparationApproval saveBoqData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub

		EstimatePreparationApproval boqList = estimatePreparationApprovalRepository
				.findById(estimatePreparationApproval.getId());

		for (BoQDetails boqDb : boqList.getNewBoQDetailsList()) {
			for (BoQDetails boqUI : estimatePreparationApproval.getNewBoQDetailsList()) {
				if (boqDb.getSlNo() == boqUI.getSlNo()) {
					boqDb.setCheckboxChecked(boqUI.isCheckboxChecked());
				}
			}
		}
		EstimatePreparationApproval saveBoqDetails = estimatePreparationApprovalRepository.save(boqList);

		return saveBoqDetails;
	}

}
