package org.egov.apnimandi.masters.repository;

import java.util.List;

import org.egov.apnimandi.masters.entity.DocumentTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentsTypeMasterRepository extends JpaRepository<DocumentTypeMaster, Long> {

	DocumentTypeMaster findByCode(String code);
    
    List<DocumentTypeMaster> findByActiveTrueOrderByOrdernumberAsc();
}
