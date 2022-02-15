package org.egov.audit.repository;

import org.egov.audit.entity.PassUnderObjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassUnderObjectionRepository extends JpaRepository<PassUnderObjection, Long>{

}
