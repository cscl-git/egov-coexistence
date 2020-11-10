package org.egov.audit.service;

import java.util.List;

import org.egov.audit.model.ManageAuditor;
import org.egov.audit.repository.ManageAuditorsRepository;
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
	 
	 
	 
	 
	 
	 public ManageAuditor saveAuditors(ManageAuditor manageAuditors) {
		 try {
		 manageAuditors= manageAuditorsRepository.save(manageAuditors);
		 }
		 catch(Exception e) 
		 {
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
		 
		 manageAuditors= manageAuditorsRepository.save(manageAuditorsdb);
		 }
		 catch(Exception e) 
		 {
			 e.printStackTrace();
			 }
		 return manageAuditors;
	 }
	 
	 
	 
	 
	 public List<ManageAuditor> getAudiors(){
		 List<ManageAuditor> ManageAuditorslist=null;
		 try {
		 ManageAuditorslist=manageAuditorsRepository.findAll();
		 }
		 catch(Exception e) 
		 {e.printStackTrace();
		 }
		 System.out.println(ManageAuditorslist);
		 return ManageAuditorslist;
		 
	 }
	 
	 
	 public List<ManageAuditor> getAudiorsByType(String type){
		 List<ManageAuditor> ManageAuditorslist=null;
		 try {
		 ManageAuditorslist=manageAuditorsRepository.findByType(type);
		 }
		 catch(Exception e) 
		 {e.printStackTrace();
		 }
		
		 return ManageAuditorslist;
		 
	 }
	 
	 
	 
	 public ManageAuditor getAudiorById(Long id){
		 ManageAuditor manageAuditors=null;
		 try {
			 manageAuditors=manageAuditorsRepository.findById(id);
		 }
		 catch(Exception e) 
		 {e.printStackTrace();
		 }
		 System.out.println(manageAuditors);
		 return manageAuditors;
		 
	 }
	 
	 public void delete(Long id){
		
		// manageAuditorsRepository.delete(id);
		 manageAuditorsRepository.deleteById(id);
		 
	 }
	 
	 
	 
	 public boolean findByEmployeeidExists(ManageAuditor manageAuditor) 
	 {
		 boolean exist=false;
		 List<ManageAuditor> ManageAuditorslist=null;
		 try {
		 ManageAuditorslist=manageAuditorsRepository.findByEmployeeid(manageAuditor.getEmployeeid());
		 
		 for (ManageAuditor manageAuditor2 : ManageAuditorslist) {
			
			 if(manageAuditor2.getDeptid().equals(manageAuditor.getDeptid()))
			 {
				 exist=true;
			 }
		}
		 
		 
		 }
		 catch(Exception e) 
		 {e.printStackTrace();
		 }
		 
		 return exist;
	 }
	 
	 public List<ManageAuditor> getAudiorsDepartmentByType(Integer deptid,String type){
		 List<ManageAuditor> ManageAuditorslist=null;
		 try {
		 ManageAuditorslist=manageAuditorsRepository.findByDeptidAndType(deptid, type);
		 }
		 catch(Exception e) 
		 {e.printStackTrace();
		 }
		
		 return ManageAuditorslist;
		 
	 }
	 
}
