package org.egov.apnimandi.masters.repository;

import java.util.List;

import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneMasterRepository  extends JpaRepository<ZoneMaster, Long>{
	ZoneMaster findByCode(String code);
    
    List<ZoneMaster> findByActiveTrueOrderByOrdernumberAsc();
}
