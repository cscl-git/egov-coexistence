package org.egov.works.workestimate.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.repository.DepartmentRepository;
import org.egov.infstr.services.PersistenceService;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.entity.ExpenditureHeadEntity;
import org.egov.works.estimatepreparationapproval.entity.Subdivisionworks;
import org.egov.works.estimatepreparationapproval.entity.Workswing;
import org.egov.works.estimatepreparationapproval.repository.DNITCreationRepository;
import org.egov.works.estimatepreparationapproval.repository.EstimatePreparationApprovalRepository;
import org.egov.works.estimatepreparationapproval.repository.SudivisionRepository;
import org.egov.works.estimatepreparationapproval.repository.WorkswingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class WorkEstimateService {

	@Autowired
	private EstimatePreparationApprovalRepository estimatePreparationApprovalRepository;
	@Autowired
	DNITCreationRepository dNITCreationRepository;
	@Autowired
	private WorkswingRepository workswingrepository;
	@Autowired
	private SudivisionRepository sudivisionrepo;
	@Autowired
	private DepartmentRepository departmentrepository;
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	@Transactional
	public List<EstimatePreparationApproval> searchWorkEstimateData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub
		List<EstimatePreparationApproval> workEstimateDetails = new ArrayList<EstimatePreparationApproval>();
		if (estimatePreparationApproval.getEstimateNumber() != null
				&& !estimatePreparationApproval.getEstimateNumber().isEmpty()) {
			workEstimateDetails = estimatePreparationApprovalRepository
					.findByEstimateNumber(estimatePreparationApproval.getEstimateNumber());
		} else {
			workEstimateDetails = estimatePreparationApprovalRepository.findByAllParams(
					 estimatePreparationApproval.getEstimateNumber(),
					estimatePreparationApproval.getExecutingDivision(), estimatePreparationApproval.getFromDt(),
					estimatePreparationApproval.getToDt());
		}

		return workEstimateDetails;
	}

	@Transactional
	public EstimatePreparationApproval searchEstimateData(Long estimatePreparationId) {
		// TODO Auto-generated method stub
		EstimatePreparationApproval estimatePreparationApproval = estimatePreparationApprovalRepository
				.findById(estimatePreparationId);

		return estimatePreparationApproval;
	}

	@Transactional
	public List<EstimatePreparationApproval> searchWorkEstimate(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub
		List<EstimatePreparationApproval> workEstimateDetails = new ArrayList<EstimatePreparationApproval>();
		if (estimatePreparationApproval.getExecutingDivision() != null
				&& estimatePreparationApproval.getFromDt() == null && estimatePreparationApproval.getToDt() == null) {
			workEstimateDetails = estimatePreparationApprovalRepository
					.findByExecutingDivisionAndStatusId(estimatePreparationApproval.getExecutingDivision());
		}
		else if (estimatePreparationApproval.getFromDt() != null && estimatePreparationApproval.getToDt() != null) {
			workEstimateDetails = estimatePreparationApprovalRepository
					.findByEstimateDates(estimatePreparationApproval.getFromDt(),
							estimatePreparationApproval.getToDt());
		}
		else {
			workEstimateDetails = estimatePreparationApprovalRepository.findByParams(
					estimatePreparationApproval.getExecutingDivision(), estimatePreparationApproval.getFromDt(),
					estimatePreparationApproval.getToDt());
		}
		return workEstimateDetails;
	}

	
	@Transactional(readOnly=true)
	public List<DNITCreation> searchWorkDnit(HttpServletRequest request,
			DNITCreation dNITCreation) {
		// TODO Auto-generated method stub
		List<DNITCreation> workEstimateDetails = new ArrayList<DNITCreation>();
		if (dNITCreation.getExecutingDivision() != null
				&& dNITCreation.getFromDt() == null && dNITCreation.getToDt() == null) {
			workEstimateDetails = dNITCreationRepository
					.findByExecutingDivisionAndStatusId(dNITCreation.getExecutingDivision());
		} else {
			workEstimateDetails = dNITCreationRepository.findByParams(
					dNITCreation.getExecutingDivision(), dNITCreation.getFromDt(),
					dNITCreation.getToDt());
		}
		return workEstimateDetails;
	}
	
	
	

	@Transactional
	public EstimatePreparationApproval searchBoqData(HttpServletRequest request, Long estimatePreparationId) {
		// TODO Auto-generated method stub
		EstimatePreparationApproval boqDetailsList = estimatePreparationApprovalRepository
				.findById(estimatePreparationId);

		return boqDetailsList;
	}

	@Transactional
	public DNITCreation searchDnitBoqData(HttpServletRequest request, Long estimatePreparationId) {
		// TODO Auto-generated method stub
		DNITCreation boqDetailsList = dNITCreationRepository
				.findById(estimatePreparationId);

		return boqDetailsList;
	}
	
	@Transactional
	public EstimatePreparationApproval saveBoqData(HttpServletRequest request,
			EstimatePreparationApproval estimatePreparationApproval) {
		// TODO Auto-generated method stub

		EstimatePreparationApproval boqList = estimatePreparationApprovalRepository
				.findById(estimatePreparationApproval.getId());

		for (BoQDetails boqDb : boqList.getNewBoQDetailsList()) {
			for (BoQDetails boqUI : estimatePreparationApproval.getNewBoQDetailsList()) {
				if (boqDb.getSlNo() == boqUI.getSlNo()) {
					boqDb.setCheckboxChecked(boqUI.isCheckboxChecked());
				}
			}
		}
		EstimatePreparationApproval saveBoqDetails = estimatePreparationApprovalRepository.save(boqList);

		return saveBoqDetails;
	}
	@Transactional
	public void deleteEstimateData(Long estimatePreparationId) {
		// TODO Auto-generated method stub
		try {
		estimatePreparationApprovalRepository.deleteboqData(estimatePreparationId);
		estimatePreparationApprovalRepository.deleteestimateById(estimatePreparationId);
		
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public List<Subdivisionworks> getsubdivision(Long id) {
		List<Subdivisionworks> subdivision = sudivisionrepo.findByDivisionid(id);
	return subdivision;
	}
	public List<Department> getdepartment(Long id) {
		List<Department> departments = departmentrepository.findByWorkswingid(id);
	return departments;
	}
	public List<Workswing> getworskwing() {
		List<Workswing> workwing = workswingrepository.findAll();
	return workwing;
	}

	public List<ExpenditureHeadEntity> fetchExpHeadList() {
		List<ExpenditureHeadEntity> headLst = new ArrayList<ExpenditureHeadEntity>();
		List<Object[]> list =null;
		try {
			String query = "select bq.headId, bq.expenditureHead from ExpenditureHeadEntity bq ";
		
			list = persistenceService.findAllBy(query);
			if(list!= null && list.size()>0) {
				for(Object[] object : list) {
					ExpenditureHeadEntity expHeadObj = new ExpenditureHeadEntity();
					expHeadObj.setHeadId(Long.parseLong(object[0].toString()));
					expHeadObj.setExpenditureHead(object[1].toString());
					headLst.add(expHeadObj);
				}
				
			}
			
			System.out.println("###### head list size::"+headLst.size());
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return headLst;
	}

	public long fetchHeadIdFromHead(String expHead) {
		long headId = 0L;
		List<Object[]> list = null;
		try {
			String query = "SELECT bq.headId from ExpenditureHeadEntity bq ";
			list = persistenceService.findAllBy(query);
			if(list != null && !list.isEmpty()) {
				for(Object[] object : list) {
					headId = Long.parseLong(object[0].toString());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return headId;
	}

	public long fetchSubHeadIdFromSubHead(String expSubCategory) {
		long subheadId = 0L;
		List<Object[]> list = null;
		try {
			String query = "SELECT bq.subHeadId from ExpenditureSubHead bq ";
			list = persistenceService.findAllBy(query);
			if(list != null && !list.isEmpty()) {
				for(Object[] object : list) {
					subheadId = Long.parseLong(object[0].toString());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return subheadId;
	}

}