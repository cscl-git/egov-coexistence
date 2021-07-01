package org.egov.works.estimatepreparationapproval.repository;

import java.util.List;

import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.entity.Subdivisionworks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SudivisionRepository extends JpaRepository<Subdivisionworks, Long>{

	List<Subdivisionworks> findByDivisionid(final Long id);
}
