package org.egov.apnimandi.masters.repository;

import java.util.List;

import org.egov.apnimandi.masters.entity.SiteMaster;
import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteMasterRepository extends JpaRepository<SiteMaster, Long>{
	SiteMaster findByCode(String code);
    
    List<SiteMaster> findByActiveTrueOrderByOrdernumberAsc();
    
    List<SiteMaster> findByZoneAndActiveTrue(ZoneMaster zone);
}
