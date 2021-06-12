package org.egov.lcms.transactions.repository;

import org.egov.lcms.transactions.entity.BidefendingCounsilDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BidefendingCounsilDetailsRepository extends JpaRepository<BidefendingCounsilDetails, Long>{

	@Modifying(clearAutomatically = true)
    @Transactional
	@Query("update BidefendingCounsilDetails b set b.defCounsilPrimary=:novalue where b.id=:id and b.defCounsilPrimary=:yesValue")
	public	void updateDefending(@Param("id")Long id,@Param("novalue")String novalue,@Param("yesValue")String yesValue);

	@Modifying(clearAutomatically = true)
    @Transactional
	@Query("update BidefendingCounsilDetails b set b.defCounsilPrimary=:novalue where b.id=:id ")
	public void updateRecords(@Param("novalue")String yesValue,@Param("id")Long id);

}
