package org.egov.works.boq.repository;

import java.util.List;

import org.egov.works.boq.entity.BoqDetailsPop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoqDetailsPopRepository extends JpaRepository<BoqDetailsPop, Long> {

	
	@Query(" from BoqDetailsPop b where b.ref_dsr like %:ref_dsr% ")
	List<BoqDetailsPop> getRecordsByRef(@Param("ref_dsr")String ref_dsr);

	@Query(" from BoqDetailsPop b where b.ref_dsr =:ref_dsr")
	List<BoqDetailsPop> checkRecordsAlreadyExists(@Param("ref_dsr")String ref_dsr);
	
}
