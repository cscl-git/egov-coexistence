/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.audit.web.controller.preAudit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.audit.entity.AuditCheckList;
import org.egov.audit.entity.AuditChecklistHistory;
import org.egov.audit.entity.AuditDetails;
import org.egov.audit.model.AuditDetail;
import org.egov.audit.service.AuditService;
import org.egov.audit.utils.AuditConstants;
import org.egov.audit.utils.AuditUtils;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillregister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author venki
 */

@Controller
@RequestMapping(value = "/createAudit")
public class CreateAuditController extends GenericWorkFlowController {

	private static final Logger LOGGER = Logger.getLogger(CreateAuditController.class);
	private static final int BUFFER_SIZE = 4096;
	private static final String STATE_TYPE = "stateType";

	@Autowired
	@Qualifier("messageSource")
	private MessageSource messageSource;

	@Autowired
	private FileStoreService fileStoreService;

	@Autowired
	private AuditService auditService;
	
	@Autowired
	private DocumentUploadRepository documentUploadRepository;

	private List<AuditCheckList> checkList = null;

	@Autowired
	private AppConfigValueService appConfigValuesService;
	
	@Autowired
	private AuditUtils auditUtils;

	
	@RequestMapping(value = "/create/{auditId}", method = RequestMethod.GET)
	public String showNewFormGet(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request, @PathVariable final String auditId) {
		LOGGER.info("Test");
		AuditCheckList checklistDetail = null;
		List<AppConfigValues> appConfigValuesList =null;
		checkList=new ArrayList<AuditCheckList>();
		AuditDetails auditDetails = auditService.getById(Long.parseLong(auditId));
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("auditDetails",Long.valueOf(auditId));
		auditDetail.setDocumentDetail(documents);
		auditDetail.setAuditNumber(auditDetails.getAuditno());
		auditDetail.setAuditScheduledDate(auditDetails.getAudit_sch_date());
		auditDetail.setAuditType(auditDetails.getType());
		EgBillregister bill = auditDetails.getEgBillregister();
		auditDetail.setAuditId(Long.parseLong(auditId));
		auditDetail.setBillId(bill.getId());
		auditDetail.setAuditStatus(auditDetails.getStatus().getCode());
		model.addAttribute("billSource", "/services/EGF/expensebill/view/" + bill.getId());
		if(auditDetails.getStatus().getCode().equalsIgnoreCase("Created") || auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Auditor") || auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Section Officer") || auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Examiner"))
		{
				appConfigValuesList = appConfigValuesService.getConfigValuesByModuleAndKey("Audit",
						"checklist_" + bill.getEgBillregistermis().getEgBillSubType().getName());
				for (AppConfigValues value : appConfigValuesList) {
					checklistDetail = new AuditCheckList();
					if(!auditDetails.getStatus().getCode().equalsIgnoreCase("Created"))
					{
						checklistDetail.setCheckListId(getAuditCheckList(auditDetails,value.getValue(),"ID"));
						checklistDetail.setSeverity(getAuditCheckList(auditDetails,value.getValue(),"Sev"));
						checklistDetail.setStatus(getAuditCheckList(auditDetails,value.getValue(),"Stat"));
					}
					else
					{
						checklistDetail.setCheckListId("0");
					}
					checklistDetail.setChecklist_description(value.getValue());
					checkList.add(checklistDetail);
				}
				auditDetail.setCheckList(checkList);
		}
		else if(auditDetails.getStatus().getCode().equalsIgnoreCase("Pending with Department"))
		{
			if(auditDetails.getCheckList() != null && !auditDetails.getCheckList().isEmpty())
			{
				for(AuditCheckList row : auditDetails.getCheckList())
				{
					row.setUser_comments("");
					row.setCheckListId(String.valueOf(row.getId()));
				}
				auditDetail.setCheckList(auditDetails.getCheckList());
			}
		}
		model.addAttribute("workflowHistory",
				auditUtils.getHistory(auditDetails.getState(), auditDetails.getStateHistory()));
		model.addAttribute("auditDetail", auditDetail);
		model.addAttribute(STATE_TYPE, auditDetails.getClass().getSimpleName());
		return "create";
	}


	private String getAuditCheckList(AuditDetails auditDetails, String value,String type) {
		String res="";
		for(AuditCheckList row:auditDetails.getCheckList())
		{
			if(value.equalsIgnoreCase(row.getChecklist_description()))
			{
				if(type.equals("ID"))
				{
					res=String.valueOf(row.getId());
				}
				else if(type.equals("Sev"))
				{
					res=row.getSeverity();
				}
				else
				{
					res=row.getStatus();
				}
			}
		}
		return res;
	}

	@RequestMapping(value = "/create/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			final BindingResult resultBinder, 
			final HttpServletRequest request,@RequestParam("file") MultipartFile[] files) throws IOException {
		LOGGER.info("Save");
		AuditDetails auditDetails = null;
		String workFlowAction=auditDetail.getWorkFlowAction();
		if(auditDetail.getAuditStatus().equalsIgnoreCase("Created"))
		{
			auditDetails = populateDetails(auditDetail);
		}
		else if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Department"))
		{
			auditDetails = populateDetailsDept(auditDetail);
		}
		else if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Auditor") &&  workFlowAction.equalsIgnoreCase("department"))
		{
			auditDetails = populateDetailsAudit(auditDetail);
		}
		else if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Auditor") &&  workFlowAction.equalsIgnoreCase("sectionOfficer"))
		{
			auditDetails = populateDetailsSO(auditDetail);
		}
		else if(auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Section Officer") || auditDetail.getAuditStatus().equalsIgnoreCase("Pending with Examiner"))
		{
			 auditDetails = auditService.getById(auditDetail.getAuditId());
		}
		 
		
		
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		 
		if (resultBinder.hasErrors()) {

			return "create";
		} else {

			AuditDetails savedAuditDetails=null;
			auditDetails.setDocumentDetail(list);
			try {
				savedAuditDetails = auditService.create(auditDetails,workFlowAction,auditDetail.getApprovalComent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			final String message = getMessageByStatus(workFlowAction,savedAuditDetails);
			model.addAttribute("message", message);

			return "audit-success";

		}

	}

	private AuditDetails populateDetailsSO(AuditDetail auditDetail) {
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		AuditChecklistHistory checkListHistory=null;
		List<AuditChecklistHistory> checkListHistoryList=null;
		for(AuditCheckList checkListDb:auditDetails.getCheckList())
		{
			for(AuditCheckList checkListUI : auditDetail.getCheckList())
			{
				if(checkListDb.getChecklist_description().equalsIgnoreCase(checkListUI.getChecklist_description()))
				{
					checkListDb.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListDb.setUser_comments("N/A");
					checkListDb.setStatus(checkListUI.getStatus());
					checkListDb.setSeverity(checkListUI.getSeverity());
					applyAuditing(checkListDb);
					checkListHistoryList=new ArrayList<AuditChecklistHistory>();
					checkListHistory=new AuditChecklistHistory();
					checkListHistory.setAuditCheckList(checkListDb);
					checkListHistory.setChecklist_description(checkListUI.getChecklist_description());
					checkListHistory.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListHistory.setUser_comments("N/A");
					checkListHistory.setStatus(checkListUI.getStatus());
					checkListHistory.setSeverity(checkListUI.getSeverity());
					checkListHistory.setAuditDetails(auditDetails);
					applyAuditing(checkListHistory);
					checkListHistoryList.add(checkListHistory);
					checkListDb.getCheckList_history().addAll(checkListHistoryList);
				}
			}
		}
		applyAuditing(auditDetails);
		return auditDetails;
	}

	private AuditDetails populateDetailsAudit(AuditDetail auditDetail) {
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		AuditChecklistHistory checkListHistory=null;
		List<AuditChecklistHistory> checkListHistoryList=null;
		for(AuditCheckList checkListDb:auditDetails.getCheckList())
		{
			for(AuditCheckList checkListUI : auditDetail.getCheckList())
			{
				if(checkListDb.getChecklist_description().equalsIgnoreCase(checkListUI.getChecklist_description()))
				{
					checkListDb.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListDb.setStatus(checkListUI.getStatus());
					checkListDb.setSeverity(checkListUI.getSeverity());
					applyAuditing(checkListDb);
					checkListHistoryList=new ArrayList<AuditChecklistHistory>();
					checkListHistory=new AuditChecklistHistory();
					checkListHistory.setAuditCheckList(checkListDb);
					checkListHistory.setChecklist_description(checkListUI.getChecklist_description());
					checkListHistory.setAuditor_comments(checkListUI.getAuditor_comments());
					checkListHistory.setStatus(checkListUI.getStatus());
					checkListHistory.setSeverity(checkListUI.getSeverity());
					checkListHistory.setAuditDetails(auditDetails);
					applyAuditing(checkListHistory);
					checkListHistoryList.add(checkListHistory);
					checkListDb.getCheckList_history().addAll(checkListHistoryList);
				}
			}
		}
		applyAuditing(auditDetails);
		return auditDetails;
	}

	private AuditDetails populateDetailsDept(AuditDetail auditDetail) {
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		for(AuditCheckList checkListDb:auditDetails.getCheckList())
		{
			for(AuditCheckList checkListUI : auditDetail.getCheckList())
			{
				if(checkListDb.getChecklist_description().equalsIgnoreCase(checkListUI.getChecklist_description()))
				{
					checkListDb.setUser_comments(checkListUI.getUser_comments());
					applyAuditing(checkListDb);
					for(AuditChecklistHistory history : checkListDb.getCheckList_history())
					{
						if(history.getUser_comments() == null || history.getUser_comments().isEmpty())
						{
							history.setUser_comments(checkListUI.getUser_comments());
							applyAuditing(history);
						}
					}
				}
			}
		}
		applyAuditing(auditDetails);
		return auditDetails;
	}

	private AuditDetails populateDetails(AuditDetail auditDetail) {
		List<AuditCheckList> checkList=new ArrayList<AuditCheckList>();
		AuditChecklistHistory checkListHistory=null;
		List<AuditChecklistHistory> checkListHistoryList=null;
		AuditDetails auditDetails = auditService.getById(auditDetail.getAuditId());
		auditDetails.setAudit_sch_date(auditDetail.getAuditScheduledDate());
		for(AuditCheckList row : auditDetail.getCheckList())
		{
			row.setAuditDetails(auditDetails);
			checkListHistoryList=new ArrayList<AuditChecklistHistory>();
			checkListHistory=new AuditChecklistHistory();
			checkListHistory.setAuditCheckList(row);
			checkListHistory.setChecklist_description(row.getChecklist_description());
			checkListHistory.setUser_comments(row.getUser_comments());
			checkListHistory.setAuditor_comments(row.getAuditor_comments());
			checkListHistory.setStatus(row.getStatus());
			checkListHistory.setSeverity(row.getSeverity());
			checkListHistory.setAuditDetails(auditDetails);
			applyAuditing(checkListHistory);
			checkListHistoryList.add(checkListHistory);
			row.setCheckList_history(checkListHistoryList);
			applyAuditing(row);
			checkList.add(row);
		}
		auditDetails.setCheckList(checkList);
		applyAuditing(auditDetails);
		return auditDetails;

	}

	@RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, AuditConstants.FILESTORE_MODULECODE);
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		AuditDetails auditDetails = auditService.getById(Long.parseLong(request.getParameter("auditDetailsId")));
		auditDetails = getBillDocuments(auditDetails);

		for (final DocumentUpload doc : auditDetails.getDocumentDetail())
			if (doc.getFileStore().getFileStoreId().equalsIgnoreCase(fileStoreId))
				fileName = doc.getFileStore().getFileName();

		// get MIME type of the file
		String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
		if (mimeType == null)
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";

		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());

		// set headers for the response
		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		final OutputStream outStream = response.getOutputStream();

		final byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1)
			outStream.write(buffer, 0, bytesRead);

		inputStream.close();
		outStream.close();
	}

	private AuditDetails getBillDocuments(final AuditDetails auditDetails) {
		List<DocumentUpload> documentDetailsList = auditService.findByObjectIdAndObjectType(auditDetails.getId(),
				AuditConstants.FILESTORE_MODULEOBJECT);
		auditDetails.setDocumentDetail(documentDetailsList);
		return auditDetails;
	}

	public List<AuditCheckList> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<AuditCheckList> checkList) {
		this.checkList = checkList;
	}

	/*
	 * @RequestMapping(value = "/success", method = RequestMethod.GET) public String
	 * showSuccessPage(@RequestParam("auditNumber") final String auditNumber, final
	 * Model model, final HttpServletRequest request) { final String[] keyNameArray
	 * = request.getParameter("approverDetails").split(","); Long id = 0L; String
	 * approverName = ""; String nextDesign = ""; if (keyNameArray.length != 0 &&
	 * keyNameArray.length > 0) if (keyNameArray.length == 1) id =
	 * Long.parseLong(keyNameArray[0].trim()); else if (keyNameArray.length == 3) {
	 * id = Long.parseLong(keyNameArray[0].trim()); approverName = keyNameArray[1];
	 * } else { id = Long.parseLong(keyNameArray[0].trim()); approverName =
	 * keyNameArray[1]; } if (id != null) model.addAttribute("approverName",
	 * approverName);
	 * 
	 * final AuditDetails auditDetails = auditService.getByAudit_no(auditNumber);
	 * 
	 * final String message = getMessageByStatus(auditDetails, approverName,
	 * nextDesign);
	 * 
	 * model.addAttribute("message", message);
	 * 
	 * return "audit-success"; }
	 */

	private String getMessageByStatus(String workflowAction,AuditDetails audit) {
		String message = "";
		if(workflowAction.equalsIgnoreCase("department"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to Department for clarification";
		}
		else if(workflowAction.equalsIgnoreCase("sectionOfficer"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to Section Officer for Verification";
		}
		else if(workflowAction.equalsIgnoreCase("auditor"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to Auditor for Verification";
		}
		else if(workflowAction.equalsIgnoreCase("examiner"))
		{
			message="Audit No : "+audit.getAuditno()+" is sent to Examiner for Approval";
		}
		else if(workflowAction.equalsIgnoreCase("approve"))
		{
			message="Audit No : "+audit.getAuditno()+" is approved";
		}
		

		return message;
	}
	public void applyAuditing(AbstractAuditable auditable) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(ApplicationThreadLocals.getUserId());
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(ApplicationThreadLocals.getUserId());
		auditable.setLastModifiedDate(currentDate);
	}
	
	@RequestMapping(value = "/history/{auditId}/{checkListId}", method = RequestMethod.GET)
	public String history(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request, @PathVariable final String auditId,@PathVariable final String checkListId) {
		
		AuditDetails auditDetails = auditService.getById(Long.parseLong(auditId));
		String checkListDesc="";
		List<AuditChecklistHistory> history =null;
		for(AuditCheckList row : auditDetails.getCheckList())
		{
			if(row.getId() == Long.parseLong(checkListId))
			{
				history=row.getCheckList_history();
				checkListDesc=row.getChecklist_description();
			}
			
		}
		model.addAttribute("history", history);
		model.addAttribute("checkListDesc", checkListDesc);
		
			return "history";
	}

}