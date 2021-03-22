package org.egov.works.boq.repository;

import java.util.Date;
import java.util.List;

import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.entity.WorkOrderAgreementRESTPOJO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderAgreementRepository extends JpaRepository<WorkOrderAgreement, Long> {
	@Query(" from WorkOrderAgreement w where w.work_agreement_status =:work_agreement_status and w.work_number =:work_number and w.work_start_date =:work_start_date and w.work_end_date =:work_end_date and w.executing_department =:executing_department")
	WorkOrderAgreement findByAllParams(@Param("work_agreement_status") String work_agreement_status,
			@Param("work_number") String work_number, @Param("work_start_date") Date work_start_date,
			@Param("work_end_date") Date work_end_date, @Param("executing_department") String executing_department);

	WorkOrderAgreement findById(final Long id);

	@Query(" from WorkOrderAgreement w where w.work_start_date =:work_start_date and w.work_end_date =:work_end_date and w.executing_department =:executing_department and w.work_agreement_status =:work_agreement_status or w.work_number =:work_number")
	List<WorkOrderAgreement> findByParams(@Param("work_start_date") Date work_start_date,
			@Param("work_end_date") Date work_end_date, @Param("executing_department") String executing_department,
			@Param("work_agreement_status") String work_agreement_status, @Param("work_number") String work_number);

	@Query(" from WorkOrderAgreement w where w.work_agreement_status =:work_agreement_status or w.category =:category or w.executing_department =:executing_department or w.name_work_order =:name_work_order or w.workType =:workType or w.workLocation =:workLocation or w.sector =:sector or w.wardNumber =:wardNumber or w.fund =:fund or w.estimatedCost =:estimatedCost or w.tenderCost =:tenderCost or w.agencyWorkOrder =:agencyWorkOrder or w.date =:date or w.timeLimit =:timeLimit")
	List<WorkOrderAgreement> findByAllParameters(@Param("work_agreement_status") String work_agreement_status,
			@Param("category") String category, @Param("executing_department") String executing_department,
			@Param("name_work_order") String name_work_order, @Param("workType") String workType,
			@Param("workLocation") String workLocation, @Param("sector") String sector,
			@Param("wardNumber") String wardNumber, @Param("fund") String fund,
			@Param("estimatedCost") String estimatedCost, @Param("tenderCost") String tenderCost,
			@Param("agencyWorkOrder") String agencyWorkOrder, @Param("date") Date date,
			@Param("timeLimit") String timeLimit);

	@Query(" from WorkOrderAgreement w where w.work_start_date =:work_start_date and w.work_end_date =:work_end_date and w.work_agreement_status =:work_agreement_status")
	List<WorkOrderAgreement> findByParam(@Param("work_start_date") Date work_start_date,
			@Param("work_end_date") Date work_end_date, @Param("work_agreement_status") String work_agreement_status);
	
	
	@Query(nativeQuery = true)
	List<WorkOrderAgreementRESTPOJO> getAllWorkOrderAgreement();
}
