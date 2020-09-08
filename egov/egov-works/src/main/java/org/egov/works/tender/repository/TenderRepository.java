package org.egov.works.tender.repository;

import org.egov.works.tender.entity.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {

	Tender findById(final Long id);

}
