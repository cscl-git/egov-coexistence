package org.egov.works.bgsecurity.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.works.bgsecurity.entity.BGSecurityDetails;
import org.egov.works.bgsecurity.repository.BGSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BGSecurityDetailsService {

	@Autowired
	private BGSecurityDetailsRepository bgSecurityDetailsRepository;

	@Transactional
	public BGSecurityDetails saveSecurityDetailsData(HttpServletRequest request, BGSecurityDetails bgSecurityDetails) {
		// TODO Auto-generated method stub
		BGSecurityDetails savedBGSecurityDetails = bgSecurityDetailsRepository.save(bgSecurityDetails);
		return savedBGSecurityDetails;

	}

	@Transactional
	public BGSecurityDetails getBGSecurityDetails(Long id) {
		// TODO Auto-generated method stub
		BGSecurityDetails bgSecurityDetails = bgSecurityDetailsRepository.findById(id);
		return bgSecurityDetails;
	}

	@Transactional
	public List<BGSecurityDetails> searchBgSecurityData(HttpServletRequest request,
			BGSecurityDetails bgSecurityDetails) {
		List<BGSecurityDetails> securityDetails = new ArrayList<BGSecurityDetails>();
		if (bgSecurityDetails.getLoaNumber() != null) {
			securityDetails = bgSecurityDetailsRepository.findByLoaNumber(bgSecurityDetails.getLoaNumber());
		} else {
			securityDetails = bgSecurityDetailsRepository.findByAllParams(bgSecurityDetails.getLoaNumber(),
					bgSecurityDetails.getSecurity_start_date(), bgSecurityDetails.getSecurity_end_date());
		}

		return securityDetails;
	}

}
