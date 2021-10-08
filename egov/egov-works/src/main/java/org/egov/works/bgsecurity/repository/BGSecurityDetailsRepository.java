package org.egov.works.bgsecurity.repository;

import java.util.Date;
import java.util.List;

import org.egov.works.bgsecurity.entity.BGSecurityDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BGSecurityDetailsRepository extends JpaRepository<BGSecurityDetails, Long> {

	BGSecurityDetails findById(final Long id);

	List<BGSecurityDetails> findByLoaNumber(final String loaNumber);

	@Query(" from BGSecurityDetails e where e.loaNumber =:loaNumber and e.security_start_date  =:security_start_date and e.security_end_date  =:security_end_date ")
	List<BGSecurityDetails> findByAllParams(@Param("loaNumber") String loaNumber,
			@Param("security_start_date") Date security_start_date, @Param("security_end_date") Date security_end_date);

}
