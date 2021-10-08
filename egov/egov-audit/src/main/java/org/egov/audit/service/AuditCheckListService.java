package org.egov.audit.service;

import org.apache.log4j.Logger;
import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.repository.AuditCheckListRepository;
import org.egov.audit.repository.BillTypeCheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditCheckListService {
	
	private static final Logger LOGGER = Logger.getLogger(AuditCheckListService.class);
	
	@Autowired
	private AuditCheckListRepository auditCheckListRepository;

	public AuditCheckList getById(Long id) {
		return auditCheckListRepository.findOne(id);
	}

	public void deleteAuditChecklist(AuditCheckList auditCheckList) {
		auditCheckListRepository.delete(auditCheckList);
	}
	
	

}
