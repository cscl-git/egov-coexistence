package org.egov.audit.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AuditConstants {
	
	public static final String FILESTORE_MODULECODE = "audit";
	public static final String FILESTORE_MODULEOBJECT = "auditDetails";
	public static final String AUDIT_CREATED_STATUS = "Created";
	public static final String AUDIT_APPROVED_STATUS = "Approved";
	public static final String AUDIT_REJECTED_STATUS = "Rejected";
	public static final String WORKFLOWTYPE_PRE_AUDIT_DISPLAYNAME = "Pre-Audit";
	public static final String AUDIT = "Audit";
	public static final String AUDIT_PENDING_WITH_DEPARTMENT ="Pending with Department";
	public static final String AUDIT_PENDING_WITH_AUDITOR ="Pending with Auditor";
	public static final String AUDIT_PENDING_WITH_SECTION_OFFICER ="Pending with Section Officer";
	public static final String AUDIT_PENDING_WITH_EXAMINER ="Pending with Examiner";
	public static final String AUDIT_SECTION_OFFICER = "audit_sec_officer";
	public static final String AUDIT_EXAMINER = "audit_examiner";
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);

}
