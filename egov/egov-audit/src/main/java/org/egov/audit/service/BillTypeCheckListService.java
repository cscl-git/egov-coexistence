package org.egov.audit.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.egov.audit.entity.BillTypeCheckList;
import org.egov.audit.repository.BillTypeCheckListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BillTypeCheckListService {
	
	private static final Logger LOGGER = Logger.getLogger(BillTypeCheckListService.class);
	
	@Autowired
	private BillTypeCheckListRepository billTypeCheckListRepository;

	public List<BillTypeCheckList> findByBillType(String billType) {
		return billTypeCheckListRepository.findByBillType(billType);
	}

	public void addBillTypeDescription( final List<BillTypeCheckList> billtypechecklist) {
		System.out.println("in Service Class");
		for(BillTypeCheckList btck:billtypechecklist) {
			billTypeCheckListRepository.save(btck);
		}
	}

	public void deleteBillTypeDescripById(final Long Id) {
		BillTypeCheckList billTypeCheckList=billTypeCheckListRepository.findOne(Id);
		billTypeCheckListRepository.delete(billTypeCheckList);
	}
	

}
