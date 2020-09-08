package org.egov.works.tender.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.tender.entity.Tender;
import org.egov.works.tender.repository.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TenderService {

	@Autowired
	private TenderRepository tenderRepository;

	@Transactional
	public Tender saveTenderDetailsData(HttpServletRequest request, Tender tender) {
		// TODO Auto-generated method stub
		Tender savedTender = tenderRepository.save(tender);
		return savedTender;

	}

	@Transactional
	public Tender searchTenderData(Long id) {
		// TODO Auto-generated method stub
		Tender tender = new Tender();
		Tender detailsList = tenderRepository.findById(id);

		return detailsList;

	}

	@Transactional
	public List<Tender> searchTenderData(HttpServletRequest request, Tender tender) {
		// TODO Auto-generated method stub
		List<Tender> tenderDetails = new ArrayList<Tender>();
		if (tender.getLoaNumber() != null) {
			tenderDetails = tenderRepository.findByLoaNumber(tender.getLoaNumber());
		} else {
			tenderDetails = tenderRepository.findByAllParams(tender.getLoaNumber(), tender.getFromDt(), tender.getToDt());
		}

		return tenderDetails;
	}

}
