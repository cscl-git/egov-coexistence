package org.egov.audit.repository;

import org.egov.audit.entity.AuditChecklistHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditCheckListHistoryRepository extends JpaRepository<AuditChecklistHistory, Long>{

}
