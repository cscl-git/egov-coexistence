package org.egov.apnimandi.transactions.repository;

import org.egov.apnimandi.transactions.entity.ContractorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorDocumentsRepository extends JpaRepository<ContractorDocument, Long>{

}
