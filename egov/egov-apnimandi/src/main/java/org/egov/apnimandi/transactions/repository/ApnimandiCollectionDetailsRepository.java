package org.egov.apnimandi.transactions.repository;

import java.util.List;

import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApnimandiCollectionDetailsRepository extends JpaRepository<ApnimandiCollectionDetails, Long>{
	List<ApnimandiCollectionDetails> findByActiveTrue();
	
	List<ApnimandiCollectionDetails> findByActiveFalse();
}
