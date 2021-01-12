package org.egov.works.estimatepreparationapproval.autonumber.impl;

import java.io.Serializable;
import java.util.Date;

import org.egov.commons.CFiscalPeriod;
import org.egov.commons.dao.FiscalPeriodHibernateDAO;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.persistence.utils.GenericSequenceNumberGenerator;
import org.egov.works.estimatepreparationapproval.autonumber.EstimateNoGenerator;
import org.egov.works.estimatepreparationapproval.autonumber.WorkNoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkNoGeneratorImpl implements WorkNoGenerator {

	@Autowired
	private FiscalPeriodHibernateDAO fiscalPeriodHibernateDAO;
	@Autowired
	private GenericSequenceNumberGenerator genericSequenceNumberGenerator;

	@Override
	public String getWorkNumber(String deptCode) {
		// TODO Auto-generated method stub
		String estimateNumber;

		String sequenceName;
		Date currDate = new Date();

		final CFiscalPeriod fiscalPeriod = fiscalPeriodHibernateDAO.getFiscalPeriodByDate(currDate);
		if (fiscalPeriod == null)
			throw new ApplicationRuntimeException("Fiscal period is not defined for the audit date");
		sequenceName = "sq_work_agreement_" + fiscalPeriod.getName();
		final Serializable nextSequence = genericSequenceNumberGenerator.getNextSequence(sequenceName);

		estimateNumber = String.format("%s/%s/%s/%06d", deptCode, "WO",
				fiscalPeriod.getcFinancialYear().getFinYearRange(), nextSequence);

		return estimateNumber;

	}

}
