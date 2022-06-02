package org.egov.egf.expensebill.service;

import java.util.List;

import org.egov.egf.expensebill.repository.RetrachmentRepository;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.EgBillregister;
import org.egov.model.bills.RetrachmentDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RetrachmentService {

	@Autowired
	private RetrachmentRepository retrachmentRepo;

	@Transactional
	public void createRetrachment(final RetrachmentDetails retrachment) {
		retrachmentRepo.save(retrachment);
	}
	public RetrachmentDetails findByAuditId(String auditId) {
		// TODO Auto-generated method stub
		return retrachmentRepo.findByAuditid(auditId);
	}


}
