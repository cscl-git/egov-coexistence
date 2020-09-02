package org.egov.works.estimatepreparationapproval.repository;

import java.util.Date;
import java.util.List;

import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstimatePreparationApprovalRepository extends JpaRepository<EstimatePreparationApproval, Long> {

	@Query(" from EstimatePreparationApproval e where e.workCategory =:workCategory and e.estimateNumber =:estimateNumber and e.executingDivision =:executingDivision and e.estimateDate BETWEEN :fromDt AND :toDt")

	List<EstimatePreparationApproval> findByAllParams(@Param("workCategory") Long workCategory,
			@Param("estimateNumber") String estimateNumber, @Param("executingDivision") Long executingDivision,
			@Param("fromDt") Date fromDt, @Param("toDt") Date toDt);
/*
	List<EstimatePreparationApproval> findByIsCheckedAndEstimatePreparationId(boolean checked,
			Long estimatePreparationId);*/

	List<EstimatePreparationApproval> findById(final Long id);
}
