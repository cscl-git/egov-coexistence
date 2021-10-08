package org.egov.lcms.masters.repository;

import java.util.List;

import org.egov.lcms.masters.entity.ConcernedBranchMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcernedBranchMasterRepository  extends JpaRepository<ConcernedBranchMaster, java.lang.Long>{
	List<ConcernedBranchMaster> findByActiveTrueOrderByOrderNumberAsc();
}
