package org.egov.apnimandi.web.controller.transactions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.apnimandi.masters.entity.DocumentTypeMaster;
import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.egov.apnimandi.masters.entity.vo.AttachedDocument;
import org.egov.apnimandi.masters.service.DocumentsTypeMasterService;
import org.egov.apnimandi.masters.service.ZoneMasterService;
import org.egov.apnimandi.reports.entity.ApnimandiCollectionSearchResult;
import org.egov.apnimandi.reports.entity.ApnimandiContractorSearchResult;
import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;
import org.egov.apnimandi.transactions.entity.ApnimandiContractor;
import org.egov.apnimandi.transactions.service.ApnimandiCollectionDetailService;
import org.egov.apnimandi.transactions.service.ApnimandiThirdPartyService;
import org.egov.apnimandi.transactions.service.ContractorsService;
import org.egov.apnimandi.utils.ApnimandiUtil;
import org.egov.apnimandi.utils.constants.ApnimandiConstants;
import org.egov.apnimandi.web.adaptor.ContractorJsonAdaptor;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.validation.exception.ValidationError;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
@RequestMapping(value = "/contractor")
public class ApnimandiContractorController extends GenericWorkFlowController{	
	private static final String APNIMANDI_CONTRACTOR_NEW = "contractor-new";
    private static final String APNIMANDI_CONTRACTOR_RESULT = "contractor-result";
    private static final String APNIMANDI_CONTRACTOR_EDIT = "contractor-edit";
    private static final String APNIMANDI_CONTRACTOR_VIEW = "contractor-view";
    private static final String APNIMANDI_CONTRACTOR_WF_VIEW = "contractor-wfview";
    private static final String APNIMANDI_CONTRACTOR_SEARCH = "contractor-search";
    private static final String APNIMANDI_CONTRACTOR_TERMINATE = "contractor-terminate";
    
    private static final String APPLICATION_HISTORY = "applicationHistory";
    private static final String APPROVAL_POSITION = "approvalPosition";
    private static final String WORK_FLOW_ACTION = "workFlowAction";
    private static final String APPROVAL_COMENT = "approvalComent";
    private static final String CURRENT_STATE = "currentState";
    private static final String APPROVAL_NAME = "approverName";
    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    
    private static final String MODE_VIEW = "view";
    private static final String MODE_CREATE = "create";
    private static final String MODE_EDIT = "edit";
    
    public static final String APNIMANDI_CONTRACTOR = "apnimandiContractor";
	public static final String MODE = "mode";
	public static final String EXISTING_APNIMANDI_CONTRACTOR_LIST = "existingContractorList";
	  
	@Autowired 
	private ContractorsService contractorsService;
	
	@Autowired 
	private ApnimandiCollectionDetailService apnimandiCollectionDetailService;
	
	@Autowired
	private DocumentsTypeMasterService documentsTypeMasterService;
	
	@Autowired
	private ZoneMasterService zoneMasterService;
	
	@Autowired
    private ApnimandiUtil apnimandiUtil;
	
	@Autowired
    private MessageSource messageSource;
	
	@Autowired 
	private ApnimandiThirdPartyService apnimandiThirdPartyService;	
	
	private void prepareNewForm(final Model model) {
        model.addAttribute("documentTypeMasters", documentsTypeMasterService.getActiveDocumentTypeList());
        model.addAttribute("zoneMasters", zoneMasterService.getActiveZoneList());
    }
	
	private void prepareWorkFlowOnLoad(final Model model, ApnimandiContractor apnimandiContractor) {
		WorkflowContainer workFlowContainer = new WorkflowContainer();
		prepareWorkflow(model, apnimandiContractor, workFlowContainer, true);
		model.addAttribute("stateType", apnimandiContractor.getClass().getSimpleName());
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newForm(@ModelAttribute final ApnimandiContractor apnimandiContractor, final Model model,
            final HttpServletRequest request) {
		prepareNewForm(model);
        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute(MODE, MODE_CREATE);
        model.addAttribute(CURRENT_STATE, "NEW");
        prepareWorkFlowOnLoad(model, apnimandiContractor);
        return APNIMANDI_CONTRACTOR_NEW;
    }
	
	@RequestMapping(value = "/new/{zoneid}", method = RequestMethod.GET)
    public String addNewForm(@PathVariable("zoneid") final Long zoneid, @ModelAttribute final ApnimandiContractor apnimandiContractor, final Model model,
            final HttpServletRequest request) {
		prepareNewForm(model);
		ZoneMaster zone = zoneMasterService.findOne(zoneid);
		apnimandiContractor.setZone(zone);
        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute(MODE, MODE_CREATE);
        model.addAttribute(CURRENT_STATE, "NEW");
        prepareWorkFlowOnLoad(model, apnimandiContractor);
        return APNIMANDI_CONTRACTOR_NEW;
    }
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final ApnimandiContractor apnimandiContractor, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
		List<ValidationError> validationErrors = apnimandiContractor.validate();
		if(null!=validationErrors) {
			for(ValidationError validationError:validationErrors) {
				errors.reject(validationError.getMessage());
			}
		}
		List<ApnimandiContractorSearchResult> existingContractorList = contractorsService.getAllExistedContractor(apnimandiContractor);
		if(null!=existingContractorList) {
			if(existingContractorList.size()>0) {
				errors.reject("apnimandiContractor.already.exist");
			}
		}else {
			existingContractorList = new ArrayList<ApnimandiContractorSearchResult>();
		}
		if (errors.hasErrors()) {
	        prepareNewForm(model);
	        model.addAttribute(EXISTING_APNIMANDI_CONTRACTOR_LIST, existingContractorList);	        
	        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
	        model.addAttribute(MODE, MODE_CREATE);
	        model.addAttribute(CURRENT_STATE, "NEW");
	        prepareWorkFlowOnLoad(model, apnimandiContractor);
	        return APNIMANDI_CONTRACTOR_NEW;
	    }
		List<DocumentTypeMaster> documentTypeList = documentsTypeMasterService.getActiveDocumentTypeList();
		List<AttachedDocument> attachedDocuments = new ArrayList<AttachedDocument>();
		for(DocumentTypeMaster documentType:documentTypeList) {
			String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file_" + documentType.getCode());
	        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file_" + documentType.getCode());
	        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file_" + documentType.getCode());  
	        if(null!=uploadedFiles) {
	            for (int i = 0; i < uploadedFiles.length; i++) {
	                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
	                byte[] fileBytes = Files.readAllBytes(path);
	                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);  
	                AttachedDocument attachedDocument = new AttachedDocument();
	                attachedDocument.setFileStream(bios);
	                attachedDocument.setFileName(fileName[i]);
	                attachedDocument.setMimeType(contentType[i]);
	                attachedDocument.setDocumentCode(documentType.getCode());
	                attachedDocuments.add(attachedDocument);
	            }
	        }
		}
		apnimandiContractor.setActive(false);
		apnimandiContractor.setStatus(apnimandiUtil.getStatusForModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_CONTRACTOR,ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_CREATED));
		Long approvalPosition = 0l;
        String approvalComment = "";
        String approverName = "";
        String approvalDesignation = "";
        String workFlowAction = "";
        if (request.getParameter(APPROVAL_COMENT) != null)
            approvalComment = request.getParameter(APPROVAL_COMENT);
        if (request.getParameter(WORK_FLOW_ACTION) != null)
            workFlowAction = request.getParameter(WORK_FLOW_ACTION);
        if (request.getParameter(APPROVAL_NAME) != null)
            approverName = request.getParameter(APPROVAL_NAME);
        if (request.getParameter(APPROVAL_DESIGNATION) != null)
        	approvalDesignation = request.getParameter(APPROVAL_DESIGNATION);
        if (request.getParameter(APPROVAL_POSITION) != null
                && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request
                    .getParameter(APPROVAL_POSITION));
		
		contractorsService.persist(apnimandiContractor, attachedDocuments, approvalPosition, approvalComment, workFlowAction);
		redirectAttrs.addFlashAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute("message", getMessage("msg.contractor.create",approvalDesignation,approverName));
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute("documentTypeMasters", documentsTypeMasterService.getActiveDocumentTypeList());
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_RESULT;
    }
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final ApnimandiContractor apnimandiContractor, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
		List<ValidationError> validationErrors = apnimandiContractor.validate();
		if(null!=validationErrors) {
			for(ValidationError validationError:validationErrors) {
				errors.reject(validationError.getMessage());
			}
		}
		List<ApnimandiContractorSearchResult> existingContractorList = contractorsService.getAllExistedContractor(apnimandiContractor);
		if(null!=existingContractorList) {
			if(existingContractorList.size()>0) {
				errors.reject("apnimandiContractor.already.exist");
			}
		}else {
			existingContractorList = new ArrayList<ApnimandiContractorSearchResult>();
		}
		if (errors.hasErrors()) {
	        prepareNewForm(model);
	        model.addAttribute(EXISTING_APNIMANDI_CONTRACTOR_LIST, existingContractorList);	        
	        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
	        model.addAttribute(MODE, MODE_EDIT);
	        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
	        return APNIMANDI_CONTRACTOR_EDIT;
	    }
		List<DocumentTypeMaster> documentTypeList = documentsTypeMasterService.getActiveDocumentTypeList();
		List<AttachedDocument> attachedDocuments = new ArrayList<AttachedDocument>();
		for(DocumentTypeMaster documentType:documentTypeList) {
			String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file_" + documentType.getCode());
	        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file_" + documentType.getCode());
	        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file_" + documentType.getCode());  
	        if(null!=uploadedFiles) {
	            for (int i = 0; i < uploadedFiles.length; i++) {
	                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
	                byte[] fileBytes = Files.readAllBytes(path);
	                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);  
	                AttachedDocument attachedDocument = new AttachedDocument();
	                attachedDocument.setFileStream(bios);
	                attachedDocument.setFileName(fileName[i]);
	                attachedDocument.setMimeType(contentType[i]);
	                attachedDocument.setDocumentCode(documentType.getCode());
	                attachedDocuments.add(attachedDocument);
	            }
	        }
		}		
		contractorsService.persist(apnimandiContractor, attachedDocuments, null, null, StringUtils.EMPTY);
		redirectAttrs.addFlashAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute("message", "Contractor Updated successfully.");
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute("documentTypeMasters", documentsTypeMasterService.getActiveDocumentTypeList());
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_RESULT;
	}
	
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, final Model model) {
		final ApnimandiContractor apnimandiContractor = contractorsService.findOne(id);
        prepareNewForm(model);
        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_VIEW;
    }
	
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") final Long id, final Model model) {
        final ApnimandiContractor apnimandiContractor = contractorsService.findOne(id);
        prepareNewForm(model);
        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute(MODE, MODE_EDIT);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_EDIT;
    }
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(final Model model) {
		final ApnimandiContractor apnimandiContractor = new ApnimandiContractor();
        prepareNewForm(model);
        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        return APNIMANDI_CONTRACTOR_SEARCH;

    }
	
	@RequestMapping(value = "/workflow/view/{id}", method = RequestMethod.GET)
    public String workflowView(@PathVariable("id") final Long id, final Model model) {
		final ApnimandiContractor apnimandiContractor = contractorsService.findOne(id);
        prepareNewForm(model);
        model.addAttribute(CURRENT_STATE, apnimandiContractor.getState().getValue());
        prepareWorkFlowOnLoad(model, apnimandiContractor);
        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_WF_VIEW;
    }
	
	@RequestMapping(value = "/workflow/update", method = RequestMethod.POST)
    public String workflowUpdate(@Valid @ModelAttribute final ApnimandiContractor apnimandiContractor, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
		if (errors.hasErrors()) {
			prepareNewForm(model);
	        model.addAttribute(CURRENT_STATE, apnimandiContractor.getState().getValue());
	        prepareWorkFlowOnLoad(model, apnimandiContractor);
	        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
	        model.addAttribute(MODE, MODE_VIEW);
	        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
	        return APNIMANDI_CONTRACTOR_WF_VIEW;
	    }	
		Long approvalPosition = 0l;
		String workFlowAction = StringUtils.EMPTY;
        String approvalComment = StringUtils.EMPTY;
        String message = StringUtils.EMPTY;
        String approverDesignation = StringUtils.EMPTY;
        String approverName = StringUtils.EMPTY;

        if (request.getParameter(APPROVAL_COMENT) != null)
            approvalComment = request.getParameter(APPROVAL_COMENT);
        if (request.getParameter(WORK_FLOW_ACTION) != null)
            workFlowAction = request.getParameter(WORK_FLOW_ACTION);
        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
        if (request.getParameter(APPROVAL_NAME) != null)
            approverName = request.getParameter(APPROVAL_NAME);
        if( request.getParameter(APPROVAL_DESIGNATION) == null)
        	approverDesignation=StringUtils.EMPTY;
        else
        	approverDesignation = request.getParameter(APPROVAL_DESIGNATION);
        
        if (null != workFlowAction) {
        	if (ApnimandiConstants.APPROVE.equalsIgnoreCase(workFlowAction)) {
        		apnimandiContractor.setActive(true);
            }
        }
        contractorsService.persist(apnimandiContractor, null, approvalPosition, approvalComment, workFlowAction);
        if (null != workFlowAction) {
        	if (ApnimandiConstants.DELETE.equalsIgnoreCase(workFlowAction)) {
        		apnimandiContractor.setActive(false);
        		apnimandiContractor.setStatus(apnimandiUtil.getStatusForModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_CONTRACTOR,ApnimandiConstants.APNIMANDI_STATUS_CONTRACTOR_DELETED));
        		contractorsService.persist(apnimandiContractor, null, null, null, StringUtils.EMPTY);
        	}
        }
        if (null != workFlowAction) {
            if (ApnimandiConstants.REJECT .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.contractor.reject",approverDesignation,approverName);
            } else if (ApnimandiConstants.APPROVE.equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.contractor.success");
            } else if (ApnimandiConstants.FORWARD.equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.contractor.forward",approverDesignation,approverName);
            } else if (ApnimandiConstants.DELETE.equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.contractor.delete");
            }
            model.addAttribute("message", message);
        }        
        redirectAttrs.addFlashAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute("documentTypeMasters", documentsTypeMasterService.getActiveDocumentTypeList());
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_RESULT;
	}
	
	@RequestMapping(value = "/terminate/edit/{id}", method = RequestMethod.GET)
    public String terminateEdit(@PathVariable("id") final Long id, final Model model) {
        final ApnimandiContractor apnimandiContractor = contractorsService.findOne(id);
        prepareNewForm(model);
        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_TERMINATE;
    }
	
	@RequestMapping(value = "/terminate/contract", method = RequestMethod.POST)
    public String terminateContract(@Valid @ModelAttribute final ApnimandiContractor apnimandiContractor, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
		boolean isTerminateDateGEtoDate=false;
		if (!DateUtils.compareDates(apnimandiContractor.getTerminateOn(), apnimandiContractor.getValidFromDate())) {
			errors.reject("terminatedate.less.fromdate");
		}else {
			isTerminateDateGEtoDate=ApnimandiUtil.isFirstDateGreaterThanEqualToSecondDate(apnimandiContractor.getTerminateOn(), apnimandiContractor.getValidToDate());
			if(!isTerminateDateGEtoDate) {
				DateTime datetime = new DateTime(apnimandiContractor.getTerminateOn());
				int month = Integer.parseInt(datetime.toString("MM"));
				int year = Integer.parseInt(DateUtils.toYearFormat(apnimandiContractor.getTerminateOn()));
				ApnimandiCollectionDetails apnimandiCollectionDetails = new ApnimandiCollectionDetails();
				apnimandiCollectionDetails.setContractor(apnimandiContractor);
				apnimandiCollectionDetails.setCollectionForMonth(month);
				apnimandiCollectionDetails.setCollectionForYear(year);
				List<ApnimandiCollectionSearchResult> existingCollectionList = apnimandiCollectionDetailService.getCollectionsByContractor(apnimandiCollectionDetails);
				if(null!=existingCollectionList) {
					if(existingCollectionList.size()>0) {
						errors.reject("terminate.collection.already.exist");
					}
				}
			}
		}
		if (errors.hasErrors()) {
	        prepareNewForm(model);
	        model.addAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
	        model.addAttribute(MODE, MODE_VIEW);
	        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
	        return APNIMANDI_CONTRACTOR_TERMINATE;
	    }
		if(!isTerminateDateGEtoDate) {
			apnimandiContractor.setValidToDate(apnimandiContractor.getTerminateOn());
		}
		apnimandiContractor.setActive(false);
		apnimandiContractor.setStatus(apnimandiUtil.getStatusForModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_CONTRACTOR,ApnimandiConstants.APNIMANDI_STATUS_CONTRACT_TERMINATED));
		contractorsService.persist(apnimandiContractor, null, null, null, StringUtils.EMPTY);
		redirectAttrs.addFlashAttribute(APNIMANDI_CONTRACTOR, apnimandiContractor);
        model.addAttribute("message", "Contractor Terminated successfully.");
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute("documentTypeMasters", documentsTypeMasterService.getActiveDocumentTypeList());
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiContractor));
        return APNIMANDI_CONTRACTOR_RESULT;
	}
	
	@RequestMapping(value = "/ajax/getContractorByZone", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)	
    public @ResponseBody void getContractorByZone(@RequestParam final long zoneid, 
    												@RequestParam final int month, @RequestParam final int year,final HttpServletResponse response) throws IOException {
        final List<ApnimandiContractorSearchResult> searchResultList = contractorsService.getContractorByZoneMonthAndYear(zoneid, month, year);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(buildContractor(searchResultList), response.getWriter());
    }
	
	private String buildContractor(List<ApnimandiContractorSearchResult> contractors) {
        final JsonArray jsonArray = new JsonArray();
        for (final ApnimandiContractorSearchResult contractor : contractors) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(ApnimandiConstants.VALUE_KEY, contractor.getApnimandiContractor().getId());
            jsonObject.addProperty(ApnimandiConstants.DISPLAY_KEY, contractor.getApnimandiContractor().getName());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }
	
	@RequestMapping(value = "/ajaxsearch", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String ajaxsearch(final Model model, @ModelAttribute final ApnimandiContractor apnimandiContractor) {
        final List<ApnimandiContractorSearchResult> searchResultList = contractorsService.search(apnimandiContractor);
        return new StringBuilder("{ \"data\":").append(toSearchResultJson(searchResultList)).append("}")
                .toString();
    }
	
	public Object toSearchResultJson(final Object object) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.registerTypeAdapter(ApnimandiContractorSearchResult.class, new ContractorJsonAdaptor()).create();
        return gson.toJson(object);
    }
	
	private String getMessage(String messageLabel,String designation,String approver) {
		return messageSource.getMessage(messageLabel,new String[] {approver.concat("~").concat(designation)}, null);
	}
	
	private String getMessage(String messageLabel) {
		return messageSource.getMessage(messageLabel, null, null);
	}
}
