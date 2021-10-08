package org.egov.apnimandi.transactions.repository;

import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApnimandiCollectionDocumentRepository extends JpaRepository<ApnimandiCollectionDocument, Long>{

}
