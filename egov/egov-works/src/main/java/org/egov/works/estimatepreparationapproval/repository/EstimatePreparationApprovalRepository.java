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

	@Query(" from EstimatePreparationApproval e where e.workCategory =:workCategory and e.estimateNumber =:estimateNumber and e.executingDivision =:executingDivision and e.estimateDate BETWEEN :fromDt AND :toDt")
	List<EstimatePreparationApproval> findByAllParams(@Param("workCategory") Long workCategory,
			@Param("estimateNumber") String estimateNumber, @Param("executingDivision") Long executingDivision,
			@Param("fromDt") Date fromDt, @Param("toDt") Date toDt);
	
	EstimatePreparationApproval findById(final Long id);

	List<EstimatePreparationApproval> findByEstimateNumber(final String estimateNumber);

	@Query(" from EstimatePreparationApproval tep where tep.executingDivision =:executingDivision and tep.status = (select es.id from EgwStatus es where es.code ='Approved' and es.moduletype ='EstimatePreparationApproval')")
	List<EstimatePreparationApproval> findByExecutingDivisionAndStatusId(@Param("executingDivision") Long executingDivision);
	
	@Query(" from EstimatePreparationApproval tep where tep.executingDivision =:executingDivision and tep.estimateDate BETWEEN :fromDt AND :toDt and tep.status = (select es.id from EgwStatus es where es.code ='Approved' and es.moduletype ='EstimatePreparationApproval')")
	List<EstimatePreparationApproval> findByParams(@Param("executingDivision") Long executingDivision,
			@Param("fromDt") Date fromDt, @Param("toDt") Date toDt);



	

	/*@Query(" from EstimatePreparationApproval tep where tep.id = (select es.estimatePreparationApproval from BoQDetails es where es.workOrderAgreement =:'null' and es.estimatePreparationApproval =:id)")
	EstimatePreparationApproval findByIdAndWorId(Long id, int workId);

	*/
	/*@Query(" from EstimatePreparationApproval tep where tep.id = (select es.estimatePreparationApproval from BoQDetails es where es.checkboxChecked =:'true' and es.estimatePreparationApproval =:estimatePreparationId)")
	EstimatePreparationApproval findByParam(Long estimatePreparationId, boolean isCheckboxChecked);*/
}
