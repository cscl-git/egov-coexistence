package org.egov.works.estimatepreparationapproval.repository;

import java.util.List;

import org.egov.works.estimatepreparationapproval.entity.Workswing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkswingRepository extends JpaRepository<Workswing,Long> {
	
	

}
