package org.egov.council.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.egov.council.entity.CouncilAgendaUpload;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilAgendaUploadRepository extends JpaRepository<CouncilAgendaUpload, Long> {

	CouncilAgendaUpload findById(Long id);
	
//	@Query("select  cn from CouncilAgendaUpload as cn where cn.createdDate >= :fromDate AND createdDate<=:toDate")
//	List<CouncilAgendaUpload> getAllsearch(@Param("fromDate")Date fromDate ,@Param("toDate")Date toDate);

}
