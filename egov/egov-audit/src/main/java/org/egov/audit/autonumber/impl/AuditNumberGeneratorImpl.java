package org.egov.audit.autonumber.impl;

import java.io.Serializable;
import java.util.Date;

import org.egov.audit.autonumber.AuditNumberGenerator;
import org.egov.commons.CFiscalPeriod;
import org.egov.commons.dao.FiscalPeriodHibernateDAO;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.persistence.utils.GenericSequenceNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditNumberGeneratorImpl implements AuditNumberGenerator{
	
	@Autowired
    private FiscalPeriodHibernateDAO fiscalPeriodHibernateDAO;
    @Autowired
    private GenericSequenceNumberGenerator genericSequenceNumberGenerator;
	@Override
	public String getNextPreAuditNumber(String deptCode) {
		String preAuditNumber;

        String sequenceName;
        Date currDate=new Date();

        final CFiscalPeriod fiscalPeriod = fiscalPeriodHibernateDAO.getFiscalPeriodByDate(currDate);
        if (fiscalPeriod == null)
            throw new ApplicationRuntimeException("Fiscal period is not defined for the audit date");
        sequenceName = "sq_pre_" +  fiscalPeriod.getName();
        final Serializable nextSequence = genericSequenceNumberGenerator.getNextSequence(sequenceName);
        preAuditNumber = String.format("%s/%s/%02d/%s/%06d", "Pre", fiscalPeriod.getcFinancialYear().getFinYearRange(),currDate.getMonth() + 1,
        		deptCode,nextSequence);

        return preAuditNumber;
	}
	@Override
	public String getNextPostAuditNumber(String deptCode) {
		String postAuditNumber;

        String sequenceName;
        Date currDate=new Date();

        final CFiscalPeriod fiscalPeriod = fiscalPeriodHibernateDAO.getFiscalPeriodByDate(currDate);
        if (fiscalPeriod == null)
            throw new ApplicationRuntimeException("Fiscal period is not defined for the audit date");
        sequenceName = "sq_post_" +  fiscalPeriod.getName();
        final Serializable nextSequence = genericSequenceNumberGenerator.getNextSequence(sequenceName);
        postAuditNumber = String.format("%s/%s/%02d/%s/%06d", "Post", fiscalPeriod.getcFinancialYear().getFinYearRange(),currDate.getMonth() + 1,
        		deptCode,nextSequence);

        return postAuditNumber;
	}

}
