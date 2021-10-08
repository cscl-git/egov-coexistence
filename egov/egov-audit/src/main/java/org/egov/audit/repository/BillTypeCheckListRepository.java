package org.egov.audit.repository;


import java.util.List;

import org.egov.audit.entity.BillTypeCheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillTypeCheckListRepository extends JpaRepository<BillTypeCheckList, Long>{

	List<BillTypeCheckList> findByBillType(String billType);

	

}
