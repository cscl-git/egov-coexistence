package org.egov.audit.repository;

import org.egov.audit.entity.AuditDetails;
import org.egov.model.bills.EgBillregister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditDetails, Long>{
	
	AuditDetails findByAuditno(final String auditNumber);
	AuditDetails findById(Long id);

}
