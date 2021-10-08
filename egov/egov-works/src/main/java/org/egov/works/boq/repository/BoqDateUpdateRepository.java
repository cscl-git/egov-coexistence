package org.egov.works.boq.repository;

import org.egov.works.boq.entity.BoqDateUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoqDateUpdateRepository extends JpaRepository<BoqDateUpdate, Long>{

}
