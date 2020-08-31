package org.egov.lcms.masters.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.infstr.services.PersistenceService;
import org.egov.lcms.masters.entity.ConcernedBranchMaster;
import org.egov.lcms.masters.repository.ConcernedBranchMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConcernedBranchMasterService  extends PersistenceService<ConcernedBranchMaster, Long>{
	@Autowired
    private ConcernedBranchMasterRepository concernedBranchMasterRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ConcernedBranchMasterService() {
        super(ConcernedBranchMaster.class);
    }
    
    public ConcernedBranchMasterService(final Class<ConcernedBranchMaster> type) {
        super(type);
    }
    
    public List<ConcernedBranchMaster> getConcernedBranchList() {
        return concernedBranchMasterRepository.findAll();
    }
    
    public List<ConcernedBranchMaster> findAll() {
        return concernedBranchMasterRepository.findAll(new Sort(Sort.Direction.ASC, "concernedBranch"));
    }

    public ConcernedBranchMaster findOne(final Long id) {
        return concernedBranchMasterRepository.findOne(id);
    }

    public List<ConcernedBranchMaster> getActiveConcernedBranchs() {
        return concernedBranchMasterRepository.findByActiveTrueOrderByOrderNumberAsc();
    }
}
