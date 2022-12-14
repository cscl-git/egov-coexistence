package org.egov.audit.repository;

import org.egov.audit.entity.AuditChecklistHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditCheckListHistoryRepository extends JpaRepository<AuditChecklistHistory, Long>{

	@Modifying
	@Query(value = "delete from AuditChecklistHistory where id=?1")
	void deleteById(Long id);

}
