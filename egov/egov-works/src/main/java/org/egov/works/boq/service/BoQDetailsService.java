package org.egov.works.boq.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.repository.WorkOrderAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BoQDetailsService {

	@Autowired
	private WorkOrderAgreementRepository workOrderAgreementRepository;

	@Transactional
	public WorkOrderAgreement saveBoQDetailsData(HttpServletRequest request, WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub
		List<BoQDetails> list = new ArrayList<BoQDetails>();
		for (BoQDetails boq : workOrderAgreement.getBoQDetailsList()) {
			boq.setWorkOrderAgreement(workOrderAgreement);
			list.add(boq);
		}
		workOrderAgreement.setNewBoQDetailsList(list);

		WorkOrderAgreement savedWorkOrderAgreement = workOrderAgreementRepository.save(workOrderAgreement);
		return savedWorkOrderAgreement;

	}

	@Transactional
	public WorkOrderAgreement viewWorkData(Long id) {
		// TODO Auto-generated method stub
		WorkOrderAgreement viewWorkDataResponse = workOrderAgreementRepository.findById(id);

		return viewWorkDataResponse;
	}

	@Transactional
	public List<WorkOrderAgreement> searchWorkOrderAgreement(HttpServletRequest request,
			WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub

		List<WorkOrderAgreement> workDetails = workOrderAgreementRepository.findByParams(
				workOrderAgreement.getWork_start_date(), workOrderAgreement.getWork_end_date(),
				workOrderAgreement.getExecuting_department(), workOrderAgreement.getWork_agreement_status(),
				workOrderAgreement.getWork_number());
		return workDetails;
	}

	@Transactional
	public WorkOrderAgreement saveBoqData(HttpServletRequest request, WorkOrderAgreement workOrderAgreement) {
		// TODO Auto-generated method stub

		WorkOrderAgreement boqList = workOrderAgreementRepository.findById(workOrderAgreement.getId());

		for (BoQDetails boqDb : boqList.getNewBoQDetailsList()) {
			for (BoQDetails boqUI : workOrderAgreement.getNewBoQDetailsList()) {
				if (boqDb.getSlNo() == boqUI.getSlNo()) {
					boqDb.setAmount(boqUI.getAmount());
					boqDb.setItem_description(boqUI.getItem_description());
					boqDb.setQuantity(boqUI.getQuantity());
					boqDb.setRate(boqUI.getRate());
					boqDb.setRef_dsr(boqUI.getRef_dsr());
					boqDb.setUnit(boqUI.getUnit());
				}
			}
		}
		WorkOrderAgreement saveBoqDetails = workOrderAgreementRepository.save(boqList);

		return saveBoqDetails;
	}

}
