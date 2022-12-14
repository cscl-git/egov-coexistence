package org.egov.audit.repository;

import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.entity.BillTypeCheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditCheckListRepository extends JpaRepository<AuditCheckList, Long>{

	@Modifying
	@Query(value = "delete from AuditCheckList where id=?1")
	void deleteById(Long id);

	
}
