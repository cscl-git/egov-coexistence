package org.egov.works.estimatepreparationapproval.repository;

import java.util.Date;
import java.util.List;

import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimatePreparationApprovalRepository extends JpaRepository<EstimatePreparationApproval, Long> {

	@Query(" from EstimatePreparationApproval e where e.estimateNumber =:estimateNumber and e.executingDivision =:executingDivision and e.estimateDate BETWEEN :fromDt AND :toDt")
	List<EstimatePreparationApproval> findByAllParams(
			@Param("estimateNumber") String estimateNumber, @Param("executingDivision") Long executingDivision,
			@Param("fromDt") Date fromDt, @Param("toDt") Date toDt);
	
	EstimatePreparationApproval findById(final Long id);

	List<EstimatePreparationApproval> findByEstimateNumber(final String estimateNumber);

	@Query(" from EstimatePreparationApproval tep where tep.executingDivision =:executingDivision and tep.status = (select es.id from EgwStatus es where es.code ='Approved' and es.moduletype ='EstimatePreparationApproval')")
	List<EstimatePreparationApproval> findByExecutingDivisionAndStatusId(
			@Param("executingDivision") Long executingDivision);
	
	@Query(" from EstimatePreparationApproval tep where tep.executingDivision =:executingDivision and tep.estimateDate BETWEEN :fromDt AND :toDt and tep.status = (select es.id from EgwStatus es where es.code ='Approved' and es.moduletype ='EstimatePreparationApproval')")
	List<EstimatePreparationApproval> findByParams(@Param("executingDivision") Long executingDivision,
			@Param("fromDt") Date fromDt, @Param("toDt") Date toDt);

	@Query(" from EstimatePreparationApproval tep where tep.workStatus =:workStatus or tep.workCategory =:workCategory or tep.executingDivision =:executingDivision or tep.workName =:workName or tep.workLocation =:workLocation or tep.sectorNumber =:sectorNumber or tep.wardNumber =:wardNumber or tep.fundSource =:fundSource or tep.estimateAmount =:estimateAmount or tep.tenderCost =:tenderCost or tep.agencyWorkOrder =:agencyWorkOrder or tep.date =:date or tep.timeLimit =:timeLimit")
	List<EstimatePreparationApproval> findByAllParameters(@Param("workStatus") Long workStatus,
			@Param("workCategory") String workCategory, @Param("executingDivision") Long executingDivision,
			@Param("workName") String workName, @Param("workLocation") String workLocation,
			@Param("sectorNumber") String sectorNumber, @Param("wardNumber") String wardNumber,
			@Param("fundSource") String fundSource, @Param("estimateAmount") Double estimateAmount,
			@Param("tenderCost") String tenderCost, @Param("agencyWorkOrder") String agencyWorkOrder,
			@Param("date") Date date, @Param("timeLimit") String timeLimit);

}
