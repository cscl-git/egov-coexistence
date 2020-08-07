package org.egov.apnimandi.transactions.repository;

import org.egov.apnimandi.transactions.entity.ApnimandiCollectionAmountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApnimandiCollectionAmountDetailsRepository extends JpaRepository<ApnimandiCollectionAmountDetails, Long>{

}
