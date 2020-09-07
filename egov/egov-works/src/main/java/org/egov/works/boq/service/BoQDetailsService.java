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

}
