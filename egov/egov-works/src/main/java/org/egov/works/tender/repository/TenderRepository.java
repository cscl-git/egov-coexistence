package org.egov.works.tender.repository;

import org.egov.works.tender.entity.Tender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderRepository extends JpaRepository<Tender, Long> {

}
