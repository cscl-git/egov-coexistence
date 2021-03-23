package org.egov.council.service;

import static org.egov.council.utils.constants.CouncilConstants.IMPLEMENTATION_STATUS_FINISHED;
import static org.egov.council.utils.constants.CouncilConstants.REJECTED;
import static org.egov.council.utils.constants.CouncilConstants.RESOLUTION_APPROVED_PREAMBLE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.council.entity.CouncilAgendaUpload;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.repository.CouncilAgendaUploadRepository;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouncilAgendaUploadService {
	 private static final String STATUS_CODE = "status.code";
	@Autowired
	CouncilAgendaUploadRepository councilAgendaUploadRepository;
	 @PersistenceContext
	 private EntityManager entityManager;
	 
	public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }
	public CouncilAgendaUpload saveCouncilAgendaUpload(CouncilAgendaUpload councilAgendaUpload)
	{
		councilAgendaUploadRepository.save(councilAgendaUpload);
		return councilAgendaUpload;
	}
	
	public List<CouncilAgendaUpload> findRecords()
	{
		return councilAgendaUploadRepository.findAll();
	}
	 public CouncilAgendaUpload findOne(Long id) {
	        return councilAgendaUploadRepository.findById(id);
	    }
	
//	 public List<CouncilAgendaUpload> findRecordsSearch(Date fromDate, Date toDate)
//	 {
//		 
//		 List<?> rsult = councilAgendaUploadRepository.getAllsearch(fromDate,toDate);
//		 System.err.println("Size"+rsult);
//		 //return councilAgendaUploadRepository.getAllsearch(fromDate,toDate);
//		return  new ArrayList();
//	 }
	
	
	
	  
	/*
	 * @SuppressWarnings("unchecked") public List<CouncilAgendaUpload>
	 * search(CouncilAgendaUpload councilAgendaUpload) { final Criteria criteria =
	 * buildSearchCriteria(councilAgendaUpload);
	 * criteria.add(Restrictions.ne(STATUS_CODE, REJECTED)); return criteria.list();
	 * }
	 */
}
