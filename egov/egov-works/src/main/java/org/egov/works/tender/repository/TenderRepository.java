package org.egov.works.tender.repository;

import java.util.Date;
import java.util.List;

import org.egov.works.tender.entity.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {

	Tender findById(final Long id);

	List<Tender> findByLoaNumber(final Double loaNumber);

	@Query(" from Tender e where e.loaNumber =:loaNumber and e.procurementDate BETWEEN :fromDt AND :toDt")
	List<Tender> findByAllParams(@Param("loaNumber") Double loaNumber, @Param("fromDt") Date fromDt,
			@Param("toDt") Date toDt);

}
