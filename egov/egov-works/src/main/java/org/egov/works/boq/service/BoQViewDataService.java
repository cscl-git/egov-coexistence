package org.egov.works.boq.service;

import java.util.List;

import org.egov.works.boq.entity.BoqDateUpdate;
import org.egov.works.boq.repository.BoQViewDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoQViewDataService {

	
	@Autowired 
	BoQViewDataRepository boQViewDataRepository;
	
	public List<BoqDateUpdate>  viewData(Long id)
	{
		List<BoqDateUpdate> boqDateUpdate=boQViewDataRepository.fildSerialNoId(id);
//		List<BoqDateUpdate> boqDateUpdate=boQViewDataRepository.findBySl_no(Slno);
//		List<BoqDateUpdate> boqDateUpdate=boQViewDataRepository.findAll();
		return boqDateUpdate;
	}
}
