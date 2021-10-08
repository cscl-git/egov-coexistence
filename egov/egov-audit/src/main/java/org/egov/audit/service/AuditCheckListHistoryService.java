package org.egov.audit.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.egov.audit.entity.AuditChecklistHistory;
import org.egov.audit.repository.AuditCheckListHistoryRepository;
import org.egov.audit.repository.AuditCheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditCheckListHistoryService {
private static final Logger LOGGER = Logger.getLogger(AuditCheckListHistoryService.class);
	
	@Autowired
	private AuditCheckListHistoryRepository auditCheckListHistoryRepository;

	public void deleteAuditChecklistHistory(List<AuditChecklistHistory> auditChecklisthistory) {
		for(AuditChecklistHistory auditcheckhistory:auditChecklisthistory) {
			auditCheckListHistoryRepository.delete(auditcheckhistory);
		}
		
	}

}
