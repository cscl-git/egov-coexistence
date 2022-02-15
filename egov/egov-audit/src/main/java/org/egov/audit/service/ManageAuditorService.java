package org.egov.audit.service;

import java.util.HashMap;
import java.util.List;

import org.egov.audit.model.ManageAuditor;
import org.egov.audit.repository.ManageAuditorsRepository;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ManageAuditorService {
	 private static final Logger LOG = LoggerFactory.getLogger(ManageAuditorService.class);
	 @Autowired
	 ManageAuditorsRepository manageAuditorsRepository;
	private Object persistenceService;
	 
	 public ManageAuditor saveAuditors(ManageAuditor manageAuditors) {
		 try {
		 manageAuditors= manageAuditorsRepository.save(manageAuditors);
		} catch (Exception e) {
			 e.printStackTrace();
			 }
		 return manageAuditors;
	 }
	 
	 public ManageAuditor updateAuditors(ManageAuditor manageAuditors) {
		 try {
			 
			 ManageAuditor manageAuditorsdb= manageAuditorsRepository.findById(manageAuditors.getId());
			 manageAuditorsdb.setDeptid(manageAuditors.getDeptid());
			 manageAuditorsdb.setEmployeeid(manageAuditors.getEmployeeid());
			 manageAuditorsdb.setType(manageAuditors.getType());
		 
			manageAuditorsdb.setBilltype(manageAuditors.getBilltype());
			manageAuditorsdb.setSubdivision(manageAuditors.getSubdivision());

		 manageAuditors= manageAuditorsRepository.save(manageAuditorsdb);
		} catch (Exception e) {
			 e.printStackTrace();
			 }
		 return manageAuditors;
	 }
	 
	 public List<ManageAuditor> getAudiors(){
		 List<ManageAuditor> ManageAuditorslist=null;
		 try {
		 ManageAuditorslist=manageAuditorsRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		 }
		 System.out.println(ManageAuditorslist);
		 return ManageAuditorslist;
		 
	 }
	 
	 public List<ManageAuditor> getAudiorsByType(String type){
		 List<ManageAuditor> ManageAuditorslist=null;
		 try {
		 ManageAuditorslist=manageAuditorsRepository.findByType(type);
		} catch (Exception e) {
			e.printStackTrace();
		 }
		
		 return ManageAuditorslist;
		 
	 }
	 
	 public ManageAuditor getAudiorById(Long id){
		 ManageAuditor manageAuditors=null;
		 try {
			 manageAuditors=manageAuditorsRepository.findById(id);
		} catch (Exception e) {
			e.printStackTrace();
		 }
		 System.out.println(manageAuditors);
		 return manageAuditors;
		 
	 }
	 
	 public void delete(Long id){
		
		// manageAuditorsRepository.delete(id);
		 manageAuditorsRepository.deleteById(id);
		 
	 }
	 
	public boolean findByEmployeeidExists(ManageAuditor manageAuditor) {
		 boolean exist=false;
		// List<ManageAuditor> ManageAuditorslist = null;
		List<ManageAuditor> ManageAuditorslist1 = null;
		 
		// int deptid = manageAuditor.getDeptid();
		String billtype = manageAuditor.getBilltype();
		String subDivision = manageAuditor.getSubdivision();
		String type = manageAuditor.getType();
		String auditType = manageAuditor.getAudittype();

		try {
			// ManageAuditorslist =
			// manageAuditorsRepository.findByEmployeeid(manageAuditor.getEmployeeid());
			ManageAuditorslist1 = manageAuditorsRepository.findByDeptid(manageAuditor.getDeptid());

			for (ManageAuditor manageAuditor2 : ManageAuditorslist1) {

				// int deptid1 = manageAuditor2.getDeptid();
				String billtype1 = manageAuditor2.getBilltype();
				String subDivision1 = manageAuditor2.getSubdivision();
				String type1 = manageAuditor2.getType();
				String auditType1 = manageAuditor2.getAudittype();

				/*
				 * if (manageAuditor2.getDeptid().equals(manageAuditor.getDeptid())) { exist =
				 * true; }
				 */
				if (subDivision == null && billtype == null) {
					if (subDivision == subDivision1 && billtype == billtype1) {
						if (type.equalsIgnoreCase(type1) && auditType.equalsIgnoreCase(auditType1))
							return true;

					}
				} else {
					if (subDivision.equalsIgnoreCase(subDivision1) && billtype.equalsIgnoreCase(billtype1)) {
						if (type.equalsIgnoreCase(type1) && auditType.equalsIgnoreCase(auditType1))
							return true;
					}
			 }
		}
		 
			/*
			 * for (ManageAuditor manageAuditor2 : ManageAuditorslist) {
			 * 
			 * int deptid1 = manageAuditor2.getDeptid(); String billtype1 =
			 * manageAuditor2.getBilltype(); String subDivision1 =
			 * manageAuditor2.getSubdivision();
			 * 
			 * if (manageAuditor2.getDeptid().equals(manageAuditor.getDeptid())) { exist =
			 * true; } if(deptid == deptid1 && subDivision == subDivision1 && billtype ==
			 * billtype1) { return true; } }
			 */
		 
		} catch (Exception e) {
			e.printStackTrace();
		 }
		 
		 return exist;
	 }
	 
	 public List<ManageAuditor> getAudiorsDepartmentByType(Integer deptid,String type){
		 List<ManageAuditor> ManageAuditorslist=null;
		 try {
		 ManageAuditorslist=manageAuditorsRepository.findByDeptidAndType(deptid, type);
		} catch (Exception e) {
			e.printStackTrace();
		 }
		
		 return ManageAuditorslist;
		 
	 }
	 
}
