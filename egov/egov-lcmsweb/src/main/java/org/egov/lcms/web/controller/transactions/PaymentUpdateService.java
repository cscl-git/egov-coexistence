package org.egov.lcms.web.controller.transactions;

import java.util.List;

import org.egov.lcms.transactions.entity.PaymentUpadte;
import org.egov.lcms.transactions.repository.PaymentUpdateReposistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentUpdateService {

	
	@Autowired
	PaymentUpdateReposistory paymentUpdateReposistory;
	
	public PaymentUpadte savePayment(PaymentUpadte paymentUpadte)
	{
		return paymentUpdateReposistory.save(paymentUpadte);
	}

	public List<PaymentUpadte> getRecordsByLegalcase(Long id) {
		// TODO Auto-generated method stub
		return paymentUpdateReposistory.findByLegalcaseid(id);
	}
}
