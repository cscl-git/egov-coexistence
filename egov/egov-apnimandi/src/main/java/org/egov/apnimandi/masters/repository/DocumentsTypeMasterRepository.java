package org.egov.apnimandi.masters.repository;

import java.util.List;

import org.egov.apnimandi.masters.entity.DocumentsTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentsTypeMasterRepository extends JpaRepository<DocumentsTypeMaster, Long> {

	DocumentsTypeMaster findByCode(String code);
    
    List<DocumentsTypeMaster> findByActiveTrueOrderByOrdernumberAsc();
}
