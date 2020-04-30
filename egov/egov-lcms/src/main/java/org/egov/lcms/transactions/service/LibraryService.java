package org.egov.lcms.transactions.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infstr.services.PersistenceService;
import org.egov.lcms.masters.entity.DocumentTypeMaster;
import org.egov.lcms.masters.entity.vo.AttachedDocument;
import org.egov.lcms.transactions.entity.Library;
import org.egov.lcms.transactions.repository.LibraryRepository;
import org.egov.lcms.utils.constants.LcmsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LibraryService  extends PersistenceService<Library, Long>{
	@Autowired
    private LibraryRepository libraryRepository;
	
	@Autowired
    private FileStoreService fileStoreService;
	
	public LibraryService() {
        super(Library.class);
    }
    
    public LibraryService(final Class<Library> type) {
        super(type);
    }
    
    @Transactional
    public Library persist(final Library library, AttachedDocument file) throws IOException {    	
    	if(null != file) {
    		library.setFilestoreid(getFileStoreObj(file));
    		library.setReffileid(library.getFilestoreid().getFileStoreId());
    	}
    	applyAuditing(library);
        return libraryRepository.save(library);
    }
    
    public FileStoreMapper getFileStoreObj(final AttachedDocument file) throws IOException {
        if (null != file) {
        	return fileStoreService.store(file.getFileStream(), file.getFileName(),
        			file.getMimeType(), LcmsConstants.MODULE_NAME);
        }
        return null;
    }

    public List<Library> getLibraryList() {
        return libraryRepository.findAll();
    }

    public List<Library> getActiveLibraryList() {
        return libraryRepository.findByActiveTrue();
    }

    public List<Library> findAll() {
        return libraryRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public List<Library> findByDocumentType(final DocumentTypeMaster documentTypeMaster) {
        return libraryRepository.findByDocumentType(documentTypeMaster);
    }

    public Library findOne(final Long id) {
        return libraryRepository.findOne(id);
    }
    
    public List<Library> search(final Library library) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Library> createQuery = cb.createQuery(Library.class);
        final Root<Library> libraries = createQuery.from(Library.class);
        createQuery.select(libraries);
        final Metamodel model = entityManager.getMetamodel();
        final javax.persistence.metamodel.EntityType<Library> libraryModule = model.entity(Library.class);

        final List<Library> resultList;
        final List<Predicate> predicates = new ArrayList<>();
        if (library.getTitle() == null 
        		&& library.getDocumentType() == null
                && library.getActive() == null)
            resultList = findAll();
        else {
            if (library.getTitle() != null) {
                final String code = "%" + library.getTitle().toLowerCase() + "%";
                predicates.add(cb.isNotNull(libraries.get("title")));
                predicates.add(cb.like(
                        cb.lower(
                        		libraries.get(libraryModule.getDeclaredSingularAttribute("title", String.class))),
                        code));
            }
            if (library.getDocumentType() != null) {
            	predicates.add(cb.equal(libraries.get("documentType"),library.getDocumentType()));
            }
            if (library.getActive() != null) {
                if (library.getActive())
                    predicates.add(cb.equal(
                    		libraries.get(libraryModule.getDeclaredSingularAttribute("active", Boolean.class)),
                            true));
                else
                    predicates.add(cb.equal(
                    		libraries.get(libraryModule.getDeclaredSingularAttribute("active", Boolean.class)),
                            false));
            }

            createQuery.where(predicates.toArray(new Predicate[] {}));
            final TypedQuery<Library> query = entityManager.createQuery(createQuery);

            resultList = query.getResultList();
        }
        return resultList;
    }
}
