package org.egov.apnimandi.masters.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.apnimandi.masters.entity.SiteMaster;
import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.egov.apnimandi.masters.repository.SiteMasterRepository;
import org.egov.infstr.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SiteMasterService extends PersistenceService<SiteMaster, Long>{
	@Autowired
    private SiteMasterRepository siteMasterRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	public SiteMasterService(Class<SiteMaster> type) {
		super(type);
	}
	
	public SiteMasterService() {
        super(SiteMaster.class);
    }
	
	@Transactional
    public SiteMaster persist(final SiteMaster siteMaster) {
    	applyAuditing(siteMaster);
        return siteMasterRepository.save(siteMaster);
    }	
	
	public List<SiteMaster> getSiteList() {
        return siteMasterRepository.findAll();
    }

    public List<SiteMaster> getActiveSiteList() {
        return siteMasterRepository.findByActiveTrueOrderByOrdernumberAsc();
    }

    public List<SiteMaster> findAll() {
        return siteMasterRepository.findAll(new Sort(Sort.Direction.DESC, "id"));
    }

    public SiteMaster findByCode(final String code) {
        return siteMasterRepository.findByCode(code);
    }

    public SiteMaster findOne(final Long id) {
        return siteMasterRepository.findOne(id);
    }
    
    public List<SiteMaster> findByZone(final ZoneMaster zone) {
        return siteMasterRepository.findByZoneAndActiveTrue(zone);
    }
}
