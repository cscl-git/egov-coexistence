package org.egov.apnimandi.masters.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.apnimandi.masters.entity.ApnimandiCollectionType;
import org.egov.apnimandi.masters.repository.ApnimandiCollectionTypeRepository;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ApnimaniCollectionTypeService extends PersistenceService<ApnimandiCollectionType, Long>{		
	@Autowired
    private ApnimandiCollectionTypeRepository apnimandiCollectionTypeRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
    
    public ApnimaniCollectionTypeService() {
        super(ApnimandiCollectionType.class);
    }
    
	public ApnimaniCollectionTypeService(Class<ApnimandiCollectionType> type) {
		super(type);
	}
	
	@Transactional
    public ApnimandiCollectionType persist(final ApnimandiCollectionType apnimandiCollectionType) {
    	applyAuditing(apnimandiCollectionType);
        return apnimandiCollectionTypeRepository.save(apnimandiCollectionType);
    }	
	
	public List<ApnimandiCollectionType> getApnimandiCollectionTypeList() {
        return apnimandiCollectionTypeRepository.findAll();
    }

    public List<ApnimandiCollectionType> getActiveApnimandiCollectionTypeList() {
        return apnimandiCollectionTypeRepository.findByActiveTrueOrderByOrdernumberAsc();
    }

    public List<ApnimandiCollectionType> findAll() {
        return apnimandiCollectionTypeRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public ApnimandiCollectionType findByCode(final String code) {
        return apnimandiCollectionTypeRepository.findByCode(code);
    }

    public ApnimandiCollectionType findOne(final Long id) {
        return apnimandiCollectionTypeRepository.findOne(id);
    }
}
