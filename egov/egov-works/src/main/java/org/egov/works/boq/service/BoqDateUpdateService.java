package org.egov.works.boq.service;

import org.egov.works.boq.entity.BoqDateUpdate;
import org.egov.works.boq.repository.BoqDateUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoqDateUpdateService {

	@Autowired
	BoqDateUpdateRepository boqDateUpdateRepository;
	
	public BoqDateUpdate saveUpdateDate(BoqDateUpdate boqDateUpdate)
	
	{
		return boqDateUpdateRepository.save(boqDateUpdate);
	}
	
}
