package org.egov.audit.repository;

import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.entity.BillTypeCheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditCheckListRepository extends JpaRepository<AuditCheckList, Long>{

	
}
