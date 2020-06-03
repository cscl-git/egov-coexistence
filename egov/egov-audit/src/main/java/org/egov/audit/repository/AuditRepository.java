package org.egov.audit.repository;

import org.egov.audit.entity.AuditDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditDetails, Long>{

}
