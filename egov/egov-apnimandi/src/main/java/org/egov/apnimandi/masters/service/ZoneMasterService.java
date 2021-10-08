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

import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.egov.apnimandi.masters.repository.ZoneMasterRepository;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ZoneMasterService extends PersistenceService<ZoneMaster, Long>{
	@Autowired
    private ZoneMasterRepository zoneMasterRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
    
    public ZoneMasterService() {
        super(ZoneMaster.class);
    }
    
	public ZoneMasterService(Class<ZoneMaster> type) {
		super(type);
	}
	
	@Transactional
    public ZoneMaster persist(final ZoneMaster zoneMaster) {
    	applyAuditing(zoneMaster);
        return zoneMasterRepository.save(zoneMaster);
    }	
	
	public List<ZoneMaster> getZoneList() {
        return zoneMasterRepository.findAll();
    }

    public List<ZoneMaster> getActiveZoneList() {
        return zoneMasterRepository.findByActiveTrueOrderByOrdernumberAsc();
    }

    public List<ZoneMaster> findAll() {
        return zoneMasterRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public ZoneMaster findByCode(final String code) {
        return zoneMasterRepository.findByCode(code);
    }

    public ZoneMaster findOne(final Long id) {
        return zoneMasterRepository.findOne(id);
    }
    
    public List<ZoneMaster> search(final ZoneMaster zoneMaster) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ZoneMaster> createQuery = cb.createQuery(ZoneMaster.class);
        final Root<ZoneMaster> zonemasters = createQuery.from(ZoneMaster.class);
        createQuery.select(zonemasters);
        final Metamodel model = entityManager.getMetamodel();
        final javax.persistence.metamodel.EntityType<ZoneMaster> zoneMasters = model.entity(ZoneMaster.class);

        final List<ZoneMaster> resultList;
        final List<Predicate> predicates = new ArrayList<>();
        if (zoneMaster.getName() == null 
        		&& zoneMaster.getCode() == null
                && zoneMaster.getActive() == null)
            resultList = findAll();
        else {
            if (zoneMaster.getCode() != null) {
                final String code = "%" + zoneMaster.getCode().toLowerCase() + "%";
                predicates.add(cb.isNotNull(zonemasters.get("code")));
                predicates.add(cb.like(
                        cb.lower(zonemasters.get(zoneMasters.getDeclaredSingularAttribute("code", String.class))), code));
            }
            if (zoneMaster.getName() != null) {
                final String zone = "%" + zoneMaster.getName().toLowerCase() + "%";
                predicates.add(cb.isNotNull(zonemasters.get("name")));
                predicates.add(cb.like(
                        cb.lower(zonemasters
                                .get(zoneMasters.getDeclaredSingularAttribute("name", String.class))),
                        zone));
            }
            if (zoneMaster.getActive() != null)
                if (zoneMaster.getActive())
                    predicates.add(cb.equal(
                    		zonemasters.get(zoneMasters.getDeclaredSingularAttribute("active", Boolean.class)),
                            true));
                else
                    predicates.add(cb.equal(
                    		zonemasters.get(zoneMasters.getDeclaredSingularAttribute("active", Boolean.class)),
                            false));

            createQuery.where(predicates.toArray(new Predicate[] {}));
            final TypedQuery<ZoneMaster> query = entityManager.createQuery(createQuery);

            resultList = query.getResultList();
        }
        return resultList;
    }
}
