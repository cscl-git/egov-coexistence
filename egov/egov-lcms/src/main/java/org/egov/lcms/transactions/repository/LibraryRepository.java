package org.egov.lcms.transactions.repository;

import java.util.List;

import org.egov.lcms.masters.entity.DocumentTypeMaster;
import org.egov.lcms.transactions.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository  extends JpaRepository<Library, Long>{
	List<Library> findByDocumentType(DocumentTypeMaster documentTypeMaster);
	
	List<Library> findByActiveTrue();
}
