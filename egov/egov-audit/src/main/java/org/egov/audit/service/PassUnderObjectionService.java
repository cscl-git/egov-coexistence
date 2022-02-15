package org.egov.audit.service;

import org.apache.log4j.Logger;
import org.egov.audit.entity.PassUnderObjection;
import org.egov.audit.model.ManageAuditor;
import org.egov.audit.repository.PassUnderObjectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)

public class PassUnderObjectionService {

	private static final Logger LOGGER = Logger.getLogger(PassUnderObjectionService.class);

	@Autowired
	private PassUnderObjectionRepository passUnderObjectionRepository;

	public PassUnderObjection savePassUnderObjection(PassUnderObjection passUnderObjection) {
		try {
			passUnderObjection = passUnderObjectionRepository.save(passUnderObjection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return passUnderObjection;
	}
	public PassUnderObjection getById(Long id) {
		return passUnderObjectionRepository.findOne(id);
	}

	public void deletePassUnderObjection(PassUnderObjection passUnderObjection) {
		passUnderObjectionRepository.delete(passUnderObjection);
	}

	
	
}
