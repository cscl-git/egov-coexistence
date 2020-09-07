package org.egov.works.boq.repository;

import org.egov.works.boq.entity.WorkOrderAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkOrderAgreementRepository extends JpaRepository<WorkOrderAgreement, Long> {
	@Query(" from WorkOrderAgreement w where w.work_agreement_status =:work_agreement_status and w.work_number =:work_number and w.work_start_date =:work_start_date and w.work_end_date =:work_end_date and w.executing_department =:executing_department")
	WorkOrderAgreement findByAllParams(@Param("work_agreement_status") String work_agreement_status,
			@Param("work_number") String work_number, @Param("work_start_date") String work_start_date,
			@Param("work_end_date") String work_end_date, @Param("executing_department") String executing_department);

}
