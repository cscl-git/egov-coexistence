package org.egov.lcms.transactions.repository;

import java.util.List;

import org.egov.lcms.transactions.entity.PaymentUpadte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentUpdateReposistory extends JpaRepository<PaymentUpadte, Long>{

	List<PaymentUpadte> findByLegalcaseid(Long id);

	
}
