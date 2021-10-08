package org.egov.lcms.masters.repository;

import java.util.List;

import org.egov.lcms.masters.entity.DocumentTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentTypeMasterRepository extends JpaRepository<DocumentTypeMaster, Long> {

	DocumentTypeMaster findByCode(String code);
    
    List<DocumentTypeMaster> findByActiveTrueOrderByOrdernumberAsc();
}