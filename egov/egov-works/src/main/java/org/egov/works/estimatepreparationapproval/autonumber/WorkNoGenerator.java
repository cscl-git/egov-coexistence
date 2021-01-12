package org.egov.works.estimatepreparationapproval.autonumber;

import org.springframework.stereotype.Service;

@Service
public interface WorkNoGenerator {
	
	public String getWorkNumber(String deptCode);

}
