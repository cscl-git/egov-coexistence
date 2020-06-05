package org.egov.apnimandi.masters.repository;

import java.util.List;

import org.egov.apnimandi.masters.entity.ApnimandiCollectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApnimandiCollectionTypeRepository extends JpaRepository<ApnimandiCollectionType, Long> {

	ApnimandiCollectionType findByCode(String code);
    
    List<ApnimandiCollectionType> findByActiveTrueOrderByOrdernumberAsc();
}
