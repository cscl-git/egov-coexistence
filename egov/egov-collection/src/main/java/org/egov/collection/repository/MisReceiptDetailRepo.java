package org.egov.collection.repository;

import org.egov.collection.entity.MisReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MisReceiptDetailRepo extends JpaRepository<MisReceiptDetail, Long>{
	
	@Query("SELECT u FROM MisReceiptDetail u WHERE u.receipt_number = :receipt_number")
	MisReceiptDetail findUserByReceipt_number(@Param("receipt_number") String receipt_number); 
}
