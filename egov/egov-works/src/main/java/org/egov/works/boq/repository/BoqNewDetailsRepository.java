package org.egov.works.boq.repository;

import org.egov.works.boq.entity.BoqNewDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoqNewDetailsRepository extends JpaRepository<BoqNewDetails, Long> {

	@Query("from BoqNewDetails bq where bq.id =:id")
	BoqNewDetails findById(@Param("id") final Long id);

	@Modifying(clearAutomatically = true)
	@Query("update BoqNewDetails bq set item_description =:item_description,ref_dsr =:ref_dsr,unit =:unit,rate=:rate where id =:id")
	public void updateById(@Param("id") final Long id,@Param("item_description") final String item_description,@Param("ref_dsr") final String ref_dsr,@Param("unit") final String unit, @Param("rate") final Double rate);
}
