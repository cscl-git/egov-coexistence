package org.egov.works.boq.service;

import java.util.List;

import org.egov.works.boq.entity.BoqDetailsPop;
import org.egov.works.boq.repository.BoqDetailsPopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoqDetailsPopService {
  
	
	@Autowired
	BoqDetailsPopRepository boqDetailsPopRepository;
	
	public List<BoqDetailsPop> getRecords(String ref_dsr)
	{
		return boqDetailsPopRepository.getRecordsByRef(ref_dsr);
	}
	
	public void saveExcelDate(List<BoqDetailsPop> boqDetailsPop)
	{
		try {
			boqDetailsPopRepository.save(boqDetailsPop);
		}catch(Exception e)
		{
			e.getMessage();
		}
		
		
	}
	
	public List<BoqDetailsPop> checkRecordsExists(String ref_dsr)
	{
		return boqDetailsPopRepository.checkRecordsAlreadyExists(ref_dsr);
	}
}
