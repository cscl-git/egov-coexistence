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
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.filestore.service.FileStoreService;
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

/**
 * @author venki
 */

@Controller
@RequestMapping(value = "/createAudit")
public class CreateAuditController extends GenericWorkFlowController {

	private static final Logger LOGGER = Logger.getLogger(CreateAuditController.class);
	private static final int BUFFER_SIZE = 4096;
	private static final String DESIGNATION = "designation";
	private static final String EXPENSEBILL_FORM = "expensebill-form";

	private static final String STATE_TYPE = "stateType";

	private static final String APPROVAL_POSITION = "approvalPosition";

	private static final String APPROVAL_DESIGNATION = "approvalDesignation";
	private Long billId = 0L;

	@Autowired
	@Qualifier("messageSource")
	private MessageSource messageSource;

	@Autowired
	private FileStoreService fileStoreService;

	@Autowired
	private AuditService auditService;

	private List<AuditCheckList> checkList = new ArrayList<AuditCheckList>();

	@Autowired
	private AppConfigValueService appConfigValuesService;
	
	@Autowired
	private AuditUtils auditUtils;

	@RequestMapping(value = "/create/{auditId}", method = RequestMethod.POST)
	public String showNewForm(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			HttpServletRequest request, @PathVariable final String auditId) {
		LOGGER.info("Test");
		AuditCheckList checklistDetail = null;
		AuditDetails auditDetails = auditService.getById(Long.parseLong(auditId));
		
		EgBillregister bill = auditDetails.getEgBillregister();
		model.addAttribute("billSource", "/services/EGF/expensebill/view/" + bill.getId());
		List<AppConfigValues> appConfigValuesList = appConfigValuesService.getConfigValuesByModuleAndKey("Audit",
				"checklist_" + bill.getEgBillregistermis().getEgBillSubType().getName());
		for (AppConfigValues value : appConfigValuesList) {
			checklistDetail = new AuditCheckList();
			checklistDetail.setChecklist_description(value.getValue());
			checkList.add(checklistDetail);
		}
		auditDetail.setCheckList(checkList);
		model.addAttribute("auditDetail", auditDetail);
		model.addAttribute(STATE_TYPE, auditDetails.getClass().getSimpleName());
		prepareWorkflow(model, auditDetails, new WorkflowContainer());
		return "create";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("auditDetail") final AuditDetail auditDetail, final Model model,
			final BindingResult resultBinder, @RequestParam final String workFlowAction,
			final HttpServletRequest request) throws IOException {
		System.out.println("Save");
		AuditDetails auditDetails = new AuditDetails();
		populateDetails(auditDetail,auditDetails);
		String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
		List<DocumentUpload> list = new ArrayList<>();
		UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
		String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
		if (uploadedFiles != null)
			for (int i = 0; i < uploadedFiles.length; i++) {

				Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
				byte[] fileBytes = Files.readAllBytes(path);
				ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
				DocumentUpload upload = new DocumentUpload();
				upload.setInputStream(bios);
				upload.setFileName(fileName[i]);
				upload.setContentType(contentType[i]);
				list.add(upload);
			}
		if (resultBinder.hasErrors()) {

			return "create";
		} else {
			Long approvalPosition = 0l;
			String approvalComment = "";
			String approvalDesignation = "";
			if (request.getParameter("approvalComent") != null)
				approvalComment = request.getParameter("approvalComent");
			if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
				approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
			if (request.getParameter(APPROVAL_DESIGNATION) != null
					&& !request.getParameter(APPROVAL_DESIGNATION).isEmpty())
				approvalDesignation = String.valueOf(request.getParameter(APPROVAL_DESIGNATION));

			AuditDetails savedAuditDetails=null;
			auditDetails.setDocumentDetail(list);
			try {
				savedAuditDetails = auditService.create(auditDetails, approvalPosition, approvalComment, null,
						workFlowAction, approvalDesignation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			final String approverName = String.valueOf(request.getParameter("approverName"));

			final String approverDetails = auditUtils.getApproverDetails(workFlowAction,
					savedAuditDetails.getState(), savedAuditDetails.getId(), approvalPosition, approverName);

			return "redirect:/createAudit/success?approverDetails=" + approverDetails + "&auditNumber="
					+ savedAuditDetails.getAudit_no();

		}

	}

	private void populateDetails(AuditDetail auditDetail,AuditDetails auditDetails) {
		List<AuditCheckList> checkList=new ArrayList<AuditCheckList>();
		AuditChecklistHistory checkListHistory=null;
		EgBillregister bill = auditService.getBillDetails(auditDetail.getBillId());
		auditDetails.setEgBillregister(bill);
		auditDetails.setAudit_no(auditDetail.getAuditNumber());
		auditDetails.setAudit_sch_date(auditDetail.getAuditScheduledDate());
		auditDetails.setType(auditDetail.getAuditType());
		auditDetails.setCreatedBy(ApplicationThreadLocals.getUserId());
		for(AuditCheckList row : auditDetail.getCheckList())
		{
			row.setAuditDetails(auditDetails);
			checkListHistory=new AuditChecklistHistory();
			checkListHistory.setAuditCheckList(row);
			checkListHistory.setChecklist_description(row.getChecklist_description());
			checkListHistory.setUser_comments(row.getUser_comments());
			checkListHistory.setAuditor_comments(row.getAuditor_comments());
			checkListHistory.setStatus(row.getStatus());
			checkListHistory.setSeverity(row.getSeverity());
			checkListHistory.setAuditDetails(auditDetails);
			row.setCheckList_history(checkListHistory);
			checkList.add(row);
		}
		auditDetails.setCheckList(checkList);

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

	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String showSuccessPage(@RequestParam("auditNumber") final String auditNumber, final Model model,
			final HttpServletRequest request) {
		final String[] keyNameArray = request.getParameter("approverDetails").split(",");
		Long id = 0L;
		String approverName = "";
		String nextDesign = "";
		if (keyNameArray.length != 0 && keyNameArray.length > 0)
			if (keyNameArray.length == 1)
				id = Long.parseLong(keyNameArray[0].trim());
			else if (keyNameArray.length == 3) {
				id = Long.parseLong(keyNameArray[0].trim());
				approverName = keyNameArray[1];
			} else {
				id = Long.parseLong(keyNameArray[0].trim());
				approverName = keyNameArray[1];
			}
		if (id != null)
			model.addAttribute("approverName", approverName);

		final AuditDetails auditDetails = auditService.getById(Long.parseLong(auditNumber));

		final String message = getMessageByStatus(auditDetails, approverName, nextDesign);

		model.addAttribute("message", message);

		return "audit-success";
	}

	private String getMessageByStatus(final AuditDetails auditDetails, final String approverName,
			final String nextDesign) {
		String message = "";

		if (AuditConstants.AUDIT_CREATED_STATUS.equals(auditDetails.getStatus().getCode())) {
			message = messageSource.getMessage("msg.expense.audit.create.success",
					new String[] { auditDetails.getAudit_no(), approverName, nextDesign }, null);
		} else if (AuditConstants.AUDIT_APPROVED_STATUS.equals(auditDetails.getStatus().getCode()))
			message = messageSource.getMessage("msg.expense.audit.approved.success",
					new String[] { auditDetails.getAudit_no() }, null);
		else if (AuditConstants.WORKFLOW_STATE_REJECTED.equals(auditDetails.getState().getValue()))
			message = messageSource.getMessage("msg.expense.audit.reject",
					new String[] { auditDetails.getAudit_no(), approverName, nextDesign }, null);
		else if (AuditConstants.WORKFLOW_STATE_CANCELLED.equals(auditDetails.getState().getValue()))
			message = messageSource.getMessage("msg.expense.audit.cancel", new String[] { auditDetails.getAudit_no() },
					null);

		return message;
	}

}