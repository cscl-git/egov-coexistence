package org.egov.works.bgsecurity.service;

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

}
