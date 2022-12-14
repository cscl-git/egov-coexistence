package org.egov.egf.expensebill.repository;

import org.egov.model.bills.RetrachmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetrachmentRepository extends JpaRepository<RetrachmentDetails, Long> {

	RetrachmentDetails findByAuditid(final String auditid);

}
