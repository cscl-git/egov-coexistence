package org.egov.works.estimatepreparationapproval.autonumber;

import org.springframework.stereotype.Service;

@Service
public interface EstimateNoGenerator {
	
	public String getEstimateNumber(String deptCode);
	
	public String getDNITNumber(String deptCode);
	
	public String getAANumber(String deptCode);

}
