package org.egov.works.bgsecurity.repository;

import org.egov.works.bgsecurity.entity.BGSecurityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BGSecurityDetailsRepository extends JpaRepository<BGSecurityDetails, Long> {

	BGSecurityDetails findById(final Long id);

}
