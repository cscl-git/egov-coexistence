package org.egov.works.workestimate.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.DNITCreationRESTPOJO;
import org.egov.works.estimatepreparationapproval.repository.DNITCreationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkDnitService {

	
	
	@Autowired
	DNITCreationRepository dNITCreationRepository;

	@Transactional
	public List<DNITCreation> searchWorkEstimateData(HttpServletRequest request,
			DNITCreation dNITCreation) {
		// TODO Auto-generated method stub
		List<DNITCreation> workEstimateDetails = new ArrayList<DNITCreation>();
		if (dNITCreation.getEstimateNumber() != null
				&& !dNITCreation.getEstimateNumber().isEmpty()) {
			workEstimateDetails = dNITCreationRepository
					.findByEstimateNumber(dNITCreation.getEstimateNumber());
		} else {
			workEstimateDetails = dNITCreationRepository.findByAllParams(
					dNITCreation.getEstimateNumber(),
					dNITCreation.getExecutingDivision(), dNITCreation.getFromDt(),
					dNITCreation.getToDt());
		}

		return workEstimateDetails;
	}

	@Transactional
	public DNITCreation searchEstimateData(Long estimatePreparationId) {
		// TODO Auto-generated method stub
		DNITCreation dNITCreation = dNITCreationRepository
				.findById(estimatePreparationId);

		return dNITCreation;
	}

	@Transactional
	public List<DNITCreation> searchWorkEstimate(HttpServletRequest request,
			DNITCreation dNITCreation) {
		// TODO Auto-generated method stub
		List<DNITCreation> workEstimateDetails = new ArrayList<DNITCreation>();
		if (dNITCreation.getExecutingDivision() != null
				&& dNITCreation.getFromDt() == null && dNITCreation.getToDt() == null) {
			workEstimateDetails = dNITCreationRepository
					.findByExecutingDivisionAndStatusId(dNITCreation.getExecutingDivision());
		} else {
			workEstimateDetails = dNITCreationRepository.findByParams(
					dNITCreation.getExecutingDivision(), dNITCreation.getFromDt(),
					dNITCreation.getToDt());
		}
		return workEstimateDetails;
	}

	@Transactional
	public DNITCreation searchBoqData(HttpServletRequest request, Long estimatePreparationId) {
		// TODO Auto-generated method stub
		DNITCreation boqDetailsList = dNITCreationRepository
				.findById(estimatePreparationId);

		return boqDetailsList;
	}

	@Transactional
	public DNITCreation saveBoqData(HttpServletRequest request,
			DNITCreation dNITCreation) {
		// TODO Auto-generated method stub

		DNITCreation boqList = dNITCreationRepository
				.findById(dNITCreation.getId());

		for (BoQDetails boqDb : boqList.getNewBoQDetailsList()) {
			for (BoQDetails boqUI : dNITCreation.getNewBoQDetailsList()) {
				if (boqDb.getSlNo() == boqUI.getSlNo()) {
					boqDb.setCheckboxChecked(boqUI.isCheckboxChecked());
				}
			}
		}
		DNITCreation saveBoqDetails = dNITCreationRepository.save(boqList);

		return saveBoqDetails;
	}
	
	public List<DNITCreationRESTPOJO>getAllDnitList(){
		List<DNITCreationRESTPOJO> dnitCreations =  null;
		
		try{
		dnitCreations = dNITCreationRepository.getAllDNITCreation();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		return dnitCreations;
	}

}
