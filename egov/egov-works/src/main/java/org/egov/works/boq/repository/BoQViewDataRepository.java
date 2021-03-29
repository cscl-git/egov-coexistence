package org.egov.works.boq.repository;

import java.util.List;

import org.egov.works.boq.entity.BoqDateUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoQViewDataRepository extends JpaRepository<BoqDateUpdate, Long> {

//	List<BoqDateUpdate>  findBySl_no(Long id);
	@Query("from BoqDateUpdate b where b.sl_no=:sl_no")
	List<BoqDateUpdate>  fildSerialNoId(@Param("sl_no")Long id);
}
