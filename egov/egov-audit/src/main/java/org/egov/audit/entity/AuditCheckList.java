package org.egov.audit.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.egov.infra.persistence.entity.AbstractPersistable;

@Entity
@Table(name = "AUDIT_CHECKLIST")
@SequenceGenerator(name = AuditCheckList.SEQ_AUDIT_CHECKLIST, sequenceName = AuditCheckList.SEQ_AUDIT_CHECKLIST, allocationSize = 1)
public class AuditCheckList extends AbstractPersistable<Integer> implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1396632085994354439L;

	public static final String SEQ_AUDIT_CHECKLIST = "SEQ_AUDIT_CHECKLIST";

    @Id
    @GeneratedValue(generator = SEQ_AUDIT_CHECKLIST, strategy = GenerationType.SEQUENCE)
    private Integer id;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;	
	}

	@Override
	protected void setId(Integer id) {
		// TODO Auto-generated method stub
		
	}

}
