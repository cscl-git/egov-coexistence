package org.egov.apnimandi.masters.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

import org.egov.apnimandi.masters.entity.DocumentsTypeMaster;
import org.egov.apnimandi.masters.repository.DocumentsTypeMasterRepository;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DocumentsTypeMasterService extends PersistenceService<DocumentsTypeMaster, Long>{
	
	@Autowired
    private DocumentsTypeMasterRepository documentsTypeMasterRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
    
    public DocumentsTypeMasterService() {
        super(DocumentsTypeMaster.class);
    }
    
	public DocumentsTypeMasterService(Class<DocumentsTypeMaster> type) {
		super(type);
	}
	
	@Transactional
    public DocumentsTypeMaster persist(final DocumentsTypeMaster documentsTypeMaster) {
    	applyAuditing(documentsTypeMaster);
        return documentsTypeMasterRepository.save(documentsTypeMaster);
    }	
	
	public List<DocumentsTypeMaster> getDocumentTypeList() {
        return documentsTypeMasterRepository.findAll();
    }

    public List<DocumentsTypeMaster> getActiveDocumentTypeList() {
        return documentsTypeMasterRepository.findByActiveTrueOrderByOrdernumberAsc();
    }

    public List<DocumentsTypeMaster> findAll() {
        return documentsTypeMasterRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public DocumentsTypeMaster findByCode(final String code) {
        return documentsTypeMasterRepository.findByCode(code);
    }

    public DocumentsTypeMaster findOne(final Long id) {
        return documentsTypeMasterRepository.findOne(id);
    }

    public List<DocumentsTypeMaster> search(final DocumentsTypeMaster documentsTypeMaster) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<DocumentsTypeMaster> createQuery = cb.createQuery(DocumentsTypeMaster.class);
        final Root<DocumentsTypeMaster> documentstypemasters = createQuery.from(DocumentsTypeMaster.class);
        createQuery.select(documentstypemasters);
        final Metamodel model = entityManager.getMetamodel();
        final javax.persistence.metamodel.EntityType<DocumentsTypeMaster> documentstypeMasters = model.entity(DocumentsTypeMaster.class);

        final List<DocumentsTypeMaster> resultList;
        final List<Predicate> predicates = new ArrayList<>();
        if (documentsTypeMaster.getDocumentType() == null 
        		&& documentsTypeMaster.getCode() == null
                && documentsTypeMaster.getActive() == null)
            resultList = findAll();
        else {
            if (documentsTypeMaster.getCode() != null) {
                final String code = "%" + documentsTypeMaster.getCode().toLowerCase() + "%";
                predicates.add(cb.isNotNull(documentstypemasters.get("code")));
                predicates.add(cb.like(
                        cb.lower(documentstypemasters.get(documentstypeMasters.getDeclaredSingularAttribute("code", String.class))), code));
            }
            if (documentsTypeMaster.getDocumentType() != null) {
                final String documentType = "%" + documentsTypeMaster.getDocumentType().toLowerCase() + "%";
                predicates.add(cb.isNotNull(documentstypemasters.get("documentType")));
                predicates.add(cb.like(
                        cb.lower(documentstypemasters
                                .get(documentstypeMasters.getDeclaredSingularAttribute("documentType", String.class))),
                        documentType));
            }
            if (documentsTypeMaster.getActive() != null)
                if (documentsTypeMaster.getActive())
                    predicates.add(cb.equal(
                    		documentstypemasters.get(documentstypeMasters.getDeclaredSingularAttribute("active", Boolean.class)),
                            true));
                else
                    predicates.add(cb.equal(
                    		documentstypemasters.get(documentstypeMasters.getDeclaredSingularAttribute("active", Boolean.class)),
                            false));

            createQuery.where(predicates.toArray(new Predicate[] {}));
            final TypedQuery<DocumentsTypeMaster> query = entityManager.createQuery(createQuery);

            resultList = query.getResultList();
        }
        return resultList;
    }
}

