package org.egov.audit.autonumber;

import org.springframework.stereotype.Service;

@Service
public interface AuditNumberGenerator {
	
	public String getNextPreAuditNumber(String deptCode);
	
	public String getNextPostAuditNumber(String deptCode);

}
