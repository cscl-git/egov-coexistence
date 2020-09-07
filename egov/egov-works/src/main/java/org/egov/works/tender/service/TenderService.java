package org.egov.works.tender.service;

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

}
