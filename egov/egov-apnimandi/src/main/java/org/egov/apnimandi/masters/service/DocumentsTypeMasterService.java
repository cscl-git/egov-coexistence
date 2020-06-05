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

import org.egov.apnimandi.masters.entity.DocumentTypeMaster;
import org.egov.apnimandi.masters.repository.DocumentsTypeMasterRepository;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DocumentsTypeMasterService extends PersistenceService<DocumentTypeMaster, Long>{
	
	@Autowired
    private DocumentsTypeMasterRepository documentsTypeMasterRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
    
    public DocumentsTypeMasterService() {
        super(DocumentTypeMaster.class);
    }
    
	public DocumentsTypeMasterService(Class<DocumentTypeMaster> type) {
		super(type);
	}
	
	@Transactional
    public DocumentTypeMaster persist(final DocumentTypeMaster documentTypeMaster) {
    	applyAuditing(documentTypeMaster);
        return documentsTypeMasterRepository.save(documentTypeMaster);
    }	
	
	public List<DocumentTypeMaster> getDocumentTypeList() {
        return documentsTypeMasterRepository.findAll();
    }

    public List<DocumentTypeMaster> getActiveDocumentTypeList() {
        return documentsTypeMasterRepository.findByActiveTrueOrderByOrdernumberAsc();
    }

    public List<DocumentTypeMaster> findAll() {
        return documentsTypeMasterRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public DocumentTypeMaster findByCode(final String code) {
        return documentsTypeMasterRepository.findByCode(code);
    }

    public DocumentTypeMaster findOne(final Long id) {
        return documentsTypeMasterRepository.findOne(id);
    }

    public List<DocumentTypeMaster> search(final DocumentTypeMaster documentTypeMaster) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<DocumentTypeMaster> createQuery = cb.createQuery(DocumentTypeMaster.class);
        final Root<DocumentTypeMaster> documenttypemasters = createQuery.from(DocumentTypeMaster.class);
        createQuery.select(documenttypemasters);
        final Metamodel model = entityManager.getMetamodel();
        final javax.persistence.metamodel.EntityType<DocumentTypeMaster> documenttypeMasters = model.entity(DocumentTypeMaster.class);

        final List<DocumentTypeMaster> resultList;
        final List<Predicate> predicates = new ArrayList<>();
        if (documentTypeMaster.getDocumentType() == null 
        		&& documentTypeMaster.getCode() == null
                && documentTypeMaster.getActive() == null)
            resultList = findAll();
        else {
            if (documentTypeMaster.getCode() != null) {
                final String code = "%" + documentTypeMaster.getCode().toLowerCase() + "%";
                predicates.add(cb.isNotNull(documenttypemasters.get("code")));
                predicates.add(cb.like(
                        cb.lower(documenttypemasters.get(documenttypeMasters.getDeclaredSingularAttribute("code", String.class))), code));
            }
            if (documentTypeMaster.getDocumentType() != null) {
                final String documentType = "%" + documentTypeMaster.getDocumentType().toLowerCase() + "%";
                predicates.add(cb.isNotNull(documenttypemasters.get("documentType")));
                predicates.add(cb.like(
                        cb.lower(documenttypemasters
                                .get(documenttypeMasters.getDeclaredSingularAttribute("documentType", String.class))),
                        documentType));
            }
            if (documentTypeMaster.getActive() != null)
                if (documentTypeMaster.getActive())
                    predicates.add(cb.equal(
                            documenttypemasters.get(documenttypeMasters.getDeclaredSingularAttribute("active", Boolean.class)),
                            true));
                else
                    predicates.add(cb.equal(
                            documenttypemasters.get(documenttypeMasters.getDeclaredSingularAttribute("active", Boolean.class)),
                            false));

            createQuery.where(predicates.toArray(new Predicate[] {}));
            final TypedQuery<DocumentTypeMaster> query = entityManager.createQuery(createQuery);

            resultList = query.getResultList();
        }
        return resultList;
    }
}

