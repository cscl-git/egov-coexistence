package org.egov.apnimandi.transactions.repository;

import java.util.List;

import org.egov.apnimandi.masters.entity.DocumentTypeMaster;
import org.egov.apnimandi.transactions.entity.ApnimandiContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorsRepository  extends JpaRepository<ApnimandiContractor, Long>{
	List<ApnimandiContractor> findByActiveTrue();
	
	List<ApnimandiContractor> findByActiveFalse();
}
