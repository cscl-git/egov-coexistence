package org.egov.apnimandi.web.controller.transactions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.apnimandi.masters.entity.ApnimandiCollectionType;
import org.egov.apnimandi.masters.entity.SiteMaster;
import org.egov.apnimandi.masters.entity.ZoneMaster;
import org.egov.apnimandi.masters.entity.vo.AttachedDocument;
import org.egov.apnimandi.masters.service.ApnimaniCollectionTypeService;
import org.egov.apnimandi.masters.service.SiteMasterService;
import org.egov.apnimandi.masters.service.ZoneMasterService;
import org.egov.apnimandi.reports.entity.ApnimandiCollectionMISReport;
import org.egov.apnimandi.reports.entity.ApnimandiCollectionSearchResult;
import org.egov.apnimandi.reports.entity.ApnimandiContractorSearchResult;
import org.egov.apnimandi.reports.entity.EstimatedIncomeReport;
import org.egov.apnimandi.transactions.entity.ApnimandiCollectionAmountDetails;
import org.egov.apnimandi.transactions.entity.ApnimandiCollectionDetails;
import org.egov.apnimandi.transactions.entity.ApnimandiContractor;
import org.egov.apnimandi.transactions.service.ApnimandiCollectionDetailService;
import org.egov.apnimandi.transactions.service.ApnimandiThirdPartyService;
import org.egov.apnimandi.transactions.service.ContractorsService;
import org.egov.apnimandi.utils.ApnimandiUtil;
import org.egov.apnimandi.utils.constants.ApnimandiConstants;
import org.egov.apnimandi.web.adaptor.CollectionJsonAdaptor;
import org.egov.apnimandi.web.adaptor.EstimatedIncomeJsonAdaptor;
import org.egov.apnimandi.web.adaptor.MisCollectionJsonAdaptor;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.TaxHeadMaster;
import org.egov.infra.utils.DateUtils;
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
@RequestMapping(value = "/collection")
public class ApnimandiCollectionController extends GenericWorkFlowController{
	private static final String APNIMANDI_COLLECTION_DETAILS_NEW = "apnimandicollection-new";
    private static final String APNIMANDI_COLLECTION_DETAILS_RESULT = "apnimandicollection-result";
    private static final String APNIMANDI_COLLECTION_DETAILS_EDIT = "apnimandicollection-edit";
    private static final String APNIMANDI_COLLECTION_DETAILS_VIEW = "apnimandicollection-view";
    private static final String APNIMANDI_COLLECTION_DETAILS_WF_VIEW = "apnimandicollection-wfview";
    private static final String APNIMANDI_COLLECTION_DETAILS_SEARCH = "apnimandicollection-search";
    private static final String APNIMANDI_COLLECTION_RECEIPT_VIEW = "apnimandicollection-receiptview";
    
    private static final String DM_COLLECTION_SEARCH = "dm-collection-search";
    private static final String AM_COLLECTION_SEARCH = "am-collection-search"; 
    private static final String DMAM_INCOME_REPORT = "estimated-income-report"; 
    
    private static final String APPLICATION_HISTORY = "applicationHistory";
    private static final String APPROVAL_POSITION = "approvalPosition";
    private static final String WORK_FLOW_ACTION = "workFlowAction";
    private static final String APPROVAL_COMENT = "approvalComent";
    private static final String CURRENT_STATE = "currentState";
    private static final String ADDITIONALRULE = "additionalRule";
    private static final String APPROVAL_NAME = "approverName";
    private static final String APPROVAL_DESIGNATION = "approvalDesignation";
    
    private static final String MODE_VIEW = "view";
    private static final String MODE_CREATE = "create";
    private static final String MODE_EDIT = "edit";
    
    public static final String APNIMANDI_COLLECTION_DETAILS = "apnimandiCollectionDetails";
	public static final String MODE = "mode";
	public static final String EXISTING_APNIMANDI_CONTRACTOR_LIST = "existingApnimandiCollectionList";
	
	@Autowired 
	private ApnimaniCollectionTypeService apnimaniCollectionTypeService;
	@Autowired
	private ZoneMasterService zoneMasterService;
	@Autowired 
	private ApnimandiCollectionDetailService apnimandiCollectionDetailService;	
	@Autowired
	private ApnimandiUtil apnimandiUtil;
	@Autowired 
	private ContractorsService contractorsService;	
	@Autowired
    private MessageSource messageSource;	
	@Autowired 
	private ApnimandiThirdPartyService apnimandiThirdPartyService;	
	@Autowired
	private SiteMasterService siteMasterService;
	
	private void prepareNewForm(final Model model) {
        model.addAttribute("apnimandiCollectionTypes", apnimaniCollectionTypeService.getActiveApnimandiCollectionTypeList());
        model.addAttribute("zoneMasters", zoneMasterService.getActiveZoneList());
        model.addAttribute("months", DateUtils.getAllMonthsWithFullNames());
        model.addAttribute("years", ApnimandiUtil.getYears());
    }
	
	private void prepareWorkFlowOnLoad(final Model model, ApnimandiCollectionDetails apnimandiCollectionDetails) {
		WorkflowContainer workFlowContainer = new WorkflowContainer();
		prepareWorkflow(model, apnimandiCollectionDetails, workFlowContainer, true);
		model.addAttribute("stateType", apnimandiCollectionDetails.getClass().getSimpleName());
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newForm(@ModelAttribute final ApnimandiCollectionDetails apnimandiCollectionDetails, final Model model,
            final HttpServletRequest request) {
		prepareNewForm(model);
		apnimandiCollectionDetails.setPayeeName(ApnimandiConstants.KISSAN_MANDI);
		apnimandiCollectionDetails.setCollectedBy(apnimandiUtil.getLoggedInUserId());
		apnimandiCollectionDetails.setCollectionDate(new Date());
        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        model.addAttribute(MODE, MODE_CREATE);
        model.addAttribute(CURRENT_STATE, "NEW");
        model.addAttribute(ADDITIONALRULE, ApnimandiConstants.RD1);
        prepareWorkFlowOnLoad(model, apnimandiCollectionDetails);
        return APNIMANDI_COLLECTION_DETAILS_NEW;
    }
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final ApnimandiCollectionDetails apnimandiCollectionDetails, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
		
		List<AttachedDocument> attachedDocuments = new ArrayList<AttachedDocument>();
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
        
        int receiptHeadCount = Integer.valueOf(request.getParameter("receiptHeadCount"));
        if(receiptHeadCount==0) {
        	errors.reject("account.head.empty");
        }
        if(ApnimandiConstants.DAY_MARKET.equalsIgnoreCase(apnimandiCollectionDetails.getCollectiontype().getCode())) {
			List<ApnimandiCollectionSearchResult> existingCollectionList = apnimandiCollectionDetailService.getAllExistedCollections(apnimandiCollectionDetails);
			if(null!=existingCollectionList) {
				if(existingCollectionList.size()>0) {
					errors.reject("collection.already.exist");
				}
			}
        }
		if (errors.hasErrors()) {
	        prepareNewForm(model);
	        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
	        model.addAttribute(MODE, MODE_CREATE);
	        model.addAttribute(CURRENT_STATE, "NEW");
	        model.addAttribute(ADDITIONALRULE, apnimandiCollectionDetails.getZone().getRoadDivision());
	        prepareWorkFlowOnLoad(model, apnimandiCollectionDetails);
	        return APNIMANDI_COLLECTION_DETAILS_NEW;
	    }
		if(uploadedFiles!=null) {
            for (int i = 0; i < uploadedFiles.length; i++) {
                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(path);
                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
                AttachedDocument attachedDocument = new AttachedDocument();
                attachedDocument.setFileStream(bios);
                attachedDocument.setFileName(fileName[i]);
                attachedDocument.setMimeType(contentType[i]);
                attachedDocuments.add(attachedDocument);
            }
        }
		List<ApnimandiCollectionAmountDetails> collectionAmountDetails = new ArrayList<ApnimandiCollectionAmountDetails>();
		for(int i=0;i<receiptHeadCount;i++) {
			ApnimandiCollectionAmountDetails collectionAmountDetail = new ApnimandiCollectionAmountDetails();
			collectionAmountDetail.setAccountHead(request.getParameter("apnimandiCollectionAmountDetails["+i+"].accountHead"));
			collectionAmountDetail.setAmountType(request.getParameter("apnimandiCollectionAmountDetails["+i+"].amountType"));
			collectionAmountDetail.setGlCodeIdDetail(request.getParameter("apnimandiCollectionAmountDetails["+i+"].glCodeIdDetail"));
			collectionAmountDetail.setCreditAmountDetail(Double.valueOf(request.getParameter("apnimandiCollectionAmountDetails["+i+"].creditAmountDetail")));
			collectionAmountDetails.add(collectionAmountDetail);
		}
		if(ApnimandiConstants.DAY_MARKET.equalsIgnoreCase(apnimandiCollectionDetails.getCollectiontype().getCode())) {
			apnimandiCollectionDetails.setPayeeName(apnimandiCollectionDetails.getContractor().getSalutation() + " " + apnimandiCollectionDetails.getContractor().getName());
		}
		apnimandiCollectionDetails.setCollectionMonthName(ApnimandiUtil.getMonthFullName(apnimandiCollectionDetails.getCollectionForMonth()));
		apnimandiCollectionDetails.setActive(false);
		apnimandiCollectionDetails.setStatus(apnimandiUtil.getStatusForModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_COLLECTION,ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_CREATED));
		
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
		
		apnimandiCollectionDetailService.persist(apnimandiCollectionDetails, attachedDocuments, collectionAmountDetails, approvalPosition, approvalComment, workFlowAction);
		redirectAttrs.addFlashAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        model.addAttribute(MODE, MODE_VIEW);        
        model.addAttribute("message", getMessage("msg.collection.create",approvalDesignation,approverName));
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));     
        return APNIMANDI_COLLECTION_DETAILS_RESULT;
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final ApnimandiCollectionDetails apnimandiCollectionDetails, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
		
		List<AttachedDocument> attachedDocuments = new ArrayList<AttachedDocument>();
        String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
        
        int receiptHeadCount = Integer.valueOf(request.getParameter("receiptHeadCount"));
        if(receiptHeadCount==0) {
        	errors.reject("account.head.empty");
        }
        if(ApnimandiConstants.DAY_MARKET.equalsIgnoreCase(apnimandiCollectionDetails.getCollectiontype().getCode())) {
			List<ApnimandiCollectionSearchResult> existingCollectionList = apnimandiCollectionDetailService.getAllExistedCollections(apnimandiCollectionDetails);
			if(null!=existingCollectionList) {
				if(existingCollectionList.size()>0) {
					errors.reject("collection.already.exist");
				}
			}
        }
		if (errors.hasErrors()) {
	        prepareNewForm(model);
	        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
	        model.addAttribute(MODE, MODE_EDIT);
	        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));
	        return APNIMANDI_COLLECTION_DETAILS_EDIT;
	    }
		if(uploadedFiles!=null) {
            for (int i = 0; i < uploadedFiles.length; i++) {
                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(path);
                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);
                AttachedDocument attachedDocument = new AttachedDocument();
                attachedDocument.setFileStream(bios);
                attachedDocument.setFileName(fileName[i]);
                attachedDocument.setMimeType(contentType[i]);
                attachedDocuments.add(attachedDocument);
            }
        }
		List<ApnimandiCollectionAmountDetails> collectionAmountDetails = apnimandiCollectionDetails.getApnimandiCollectionAmountDetails();
		for(int i=0;i<receiptHeadCount;i++) {
			double amount = Double.valueOf(request.getParameter("apnimandiCollectionAmountDetails["+i+"].creditAmountDetail"));
			String accountHead = request.getParameter("apnimandiCollectionAmountDetails["+i+"].accountHead");
			for (final ApnimandiCollectionAmountDetails collectionAmountDetail : collectionAmountDetails) {
				if(collectionAmountDetail.getAccountHead().equals(accountHead)) {
					collectionAmountDetail.setCreditAmountDetail(amount);
				}
			}
		}
		if(ApnimandiConstants.DAY_MARKET.equalsIgnoreCase(apnimandiCollectionDetails.getCollectiontype().getCode())) {
			apnimandiCollectionDetails.setPayeeName(apnimandiCollectionDetails.getContractor().getSalutation() + " " + apnimandiCollectionDetails.getContractor().getName());
		}
		apnimandiCollectionDetails.setCollectionMonthName(ApnimandiUtil.getMonthFullName(apnimandiCollectionDetails.getCollectionForMonth()));
		apnimandiCollectionDetailService.persist(apnimandiCollectionDetails, attachedDocuments, collectionAmountDetails, null, null, StringUtils.EMPTY);
		redirectAttrs.addFlashAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        model.addAttribute("message", "Collection Details Updated successfully.");
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));
        return APNIMANDI_COLLECTION_DETAILS_RESULT;
	}
	
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, final Model model) {
		final ApnimandiCollectionDetails apnimandiCollectionDetails = apnimandiCollectionDetailService.findOne(id);
		apnimandiCollectionDetails.setCollectionMonthName(ApnimandiUtil.getMonthFullName(apnimandiCollectionDetails.getCollectionForMonth()));
        prepareNewForm(model);
        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));
        return APNIMANDI_COLLECTION_DETAILS_VIEW;
    }
	
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") final Long id, final Model model) {
		final ApnimandiCollectionDetails apnimandiCollectionDetails = apnimandiCollectionDetailService.findOne(id);
        prepareNewForm(model);        
        int receiptHeadCount = 0; 
        double totalcramount = 0;
        String collectionTypeCode = apnimandiCollectionDetails.getCollectiontype().getCode();
        long zoneId = apnimandiCollectionDetails.getZone().getId();
        if(!apnimandiCollectionDetails.getApnimandiCollectionAmountDetails().isEmpty()) {
        	receiptHeadCount = apnimandiCollectionDetails.getApnimandiCollectionAmountDetails().size();
        	totalcramount = apnimandiCollectionDetails.getApnimandiCollectionAmountDetails()
        					.stream()
        					.mapToDouble(x -> x.getCreditAmountDetail()).sum();
        }
        List<ApnimandiContractorSearchResult> searchResultList = contractorsService.getContractorByZoneMonthAndYear(apnimandiCollectionDetails.getZone().getId(), apnimandiCollectionDetails.getCollectionForMonth(), apnimandiCollectionDetails.getCollectionForYear());
        List<ApnimandiContractor> contractorList = new ArrayList<ApnimandiContractor>();
        if(null!=searchResultList) {
        	for (final ApnimandiContractorSearchResult contractor : searchResultList) {
        		contractorList.add(contractor.getApnimandiContractor());
        	}
        }
        model.addAttribute("zoneId", zoneId);
        model.addAttribute("collectionTypeCode", collectionTypeCode);
        model.addAttribute("receiptHeadCount", receiptHeadCount);
        model.addAttribute("totalcramount", totalcramount);
        model.addAttribute("apnimandiContractorList", contractorList);
        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        model.addAttribute(MODE, MODE_EDIT);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));
        return APNIMANDI_COLLECTION_DETAILS_EDIT;
    }
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(final Model model) {
		final ApnimandiCollectionDetails apnimandiCollectionDetails = new ApnimandiCollectionDetails();
        prepareNewForm(model);
        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        return APNIMANDI_COLLECTION_DETAILS_SEARCH;
    }
	
	@RequestMapping(value = "/workflow/view/{id}", method = RequestMethod.GET)
    public String workflowView(@PathVariable("id") final Long id, final Model model) {
		final ApnimandiCollectionDetails apnimandiCollectionDetails = apnimandiCollectionDetailService.findOne(id);
		apnimandiCollectionDetails.setCollectionMonthName(ApnimandiUtil.getMonthFullName(apnimandiCollectionDetails.getCollectionForMonth()));
        prepareNewForm(model);
        model.addAttribute(CURRENT_STATE, apnimandiCollectionDetails.getState().getValue());
        model.addAttribute(ADDITIONALRULE, apnimandiCollectionDetails.getZone().getRoadDivision());
        prepareWorkFlowOnLoad(model, apnimandiCollectionDetails);
        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));
        return APNIMANDI_COLLECTION_DETAILS_WF_VIEW;
    }
	
	@RequestMapping(value = "/workflow/update", method = RequestMethod.POST)
    public String workflowUpdate(@Valid @ModelAttribute final ApnimandiCollectionDetails apnimandiCollectionDetails, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException, ParseException {
		if (errors.hasErrors()) {
			prepareNewForm(model);
	        model.addAttribute(CURRENT_STATE, apnimandiCollectionDetails.getState().getValue());
	        model.addAttribute(ADDITIONALRULE, apnimandiCollectionDetails.getZone().getRoadDivision());
	        prepareWorkFlowOnLoad(model, apnimandiCollectionDetails);
	        model.addAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
	        model.addAttribute(MODE, MODE_VIEW);
	        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));
	        return APNIMANDI_COLLECTION_DETAILS_WF_VIEW;
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
        		apnimandiCollectionDetails.setActive(true);
            }
        }
        apnimandiCollectionDetails.setCollectionMonthName(ApnimandiUtil.getMonthFullName(apnimandiCollectionDetails.getCollectionForMonth()));
        apnimandiCollectionDetailService.persist(apnimandiCollectionDetails, null, null, approvalPosition, approvalComment, workFlowAction);
        if (null != workFlowAction) {
        	if (ApnimandiConstants.DELETE.equalsIgnoreCase(workFlowAction)) {
        		apnimandiCollectionDetails.setActive(false);
        		apnimandiCollectionDetails.setStatus(apnimandiUtil.getStatusForModuleAndCode(ApnimandiConstants.MODULE_TYPE_APNIMANDI_COLLECTION,ApnimandiConstants.APNIMANDI_STATUS_COLLECTION_DELETED));
        		apnimandiCollectionDetailService.persist(apnimandiCollectionDetails, null, null, null, null, StringUtils.EMPTY);
        	}
        }
        if (null != workFlowAction) {
            if (ApnimandiConstants.REJECT .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.collection.reject",approverDesignation,approverName);
            } else if (ApnimandiConstants.APPROVE.equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.collection.success");
            } else if (ApnimandiConstants.FORWARD.equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.collection.forward",approverDesignation,approverName);
            } else if (ApnimandiConstants.DELETE.equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.collection.delete");
            }
            model.addAttribute("message", message);
        } 
        redirectAttrs.addFlashAttribute(APNIMANDI_COLLECTION_DETAILS, apnimandiCollectionDetails);
        model.addAttribute(MODE, MODE_VIEW);
        model.addAttribute(APPLICATION_HISTORY, apnimandiThirdPartyService.getHistory(apnimandiCollectionDetails));
        return APNIMANDI_COLLECTION_DETAILS_RESULT;
	}
	
	@RequestMapping(value = "/ajax/isCollectionForDayMarket", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void getContractorByZone(@RequestParam final long collectionTypeId, final HttpServletResponse response) throws IOException {
		final ApnimandiCollectionType apnimandiCollectionType = apnimaniCollectionTypeService.findOne(collectionTypeId);
		boolean isDaymarket=false;
		if(null!=apnimandiCollectionType) {
			if(apnimandiCollectionType.getCode().equalsIgnoreCase(ApnimandiConstants.DAY_MARKET)) {
				isDaymarket=true;
			}
		}
		JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("isCollectionForDayMarket", isDaymarket);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(jsonObject.toString(), response.getWriter());
    }
	
	@RequestMapping(value = "/ajax/getPaymentType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void getPaymentType(@RequestParam final long collectionTypeId, final HttpServletResponse response) throws IOException {
		final ApnimandiCollectionType apnimandiCollectionType = apnimaniCollectionTypeService.findOne(collectionTypeId);
		JsonObject jsonObject = new JsonObject();
		JsonArray serviceTypes = new JsonArray();		
		if(null!=apnimandiCollectionType) {
			if(apnimandiCollectionType.getCode().equalsIgnoreCase(ApnimandiConstants.DAY_MARKET)) {
				jsonObject.addProperty("serviceCategory", ApnimandiConstants.SERVICE_DAY_MARKET);
				JsonObject serviceType = new JsonObject();
				serviceType.addProperty(ApnimandiConstants.VALUE_KEY, ApnimandiConstants.SERVICE_TYPE_CONTRACTOR_SECURITY_FEE);
				serviceType.addProperty(ApnimandiConstants.DISPLAY_KEY, ApnimandiConstants.SERVICE_NAME_CONTRACTOR_SECURITY_FEE);
				serviceTypes.add(serviceType);
				serviceType = new JsonObject();
				serviceType.addProperty(ApnimandiConstants.VALUE_KEY, ApnimandiConstants.SERVICE_TYPE_RENT_AMOUNT);
				serviceType.addProperty(ApnimandiConstants.DISPLAY_KEY, ApnimandiConstants.SERVICE_NAME_RENT_AMOUNT);
				serviceTypes.add(serviceType);				
			}else {
				jsonObject.addProperty("serviceCategory", ApnimandiConstants.SERVICE_APNI_MANDI);
				JsonObject serviceType = new JsonObject();
				serviceType.addProperty(ApnimandiConstants.VALUE_KEY, ApnimandiConstants.SERVICE_TYPE_COLLECTION_AMOUNT);
				serviceType.addProperty(ApnimandiConstants.DISPLAY_KEY, ApnimandiConstants.SERVICE_NAME_COLLECTION_AMOUNT);
				serviceTypes.add(serviceType);
			}
		}else {
			jsonObject.addProperty("serviceCategory", "");
		}
		jsonObject.add("serviceTypes", serviceTypes);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(jsonObject.toString(), response.getWriter());
    }
	
	@RequestMapping(value = "/ajax/getAccountHeads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void getAccountHeads(@RequestParam final String serviceCategory,
    										  @RequestParam final String servicetype,
    										  @RequestParam final long zoneId,
    										  @RequestParam final long collectionTypeId,
    										  final HttpServletResponse response) throws IOException {
		JsonObject jsonObject = new JsonObject();
		JsonArray accountHeads = new JsonArray();
		ZoneMaster zoneMaster = zoneMasterService.findOne(zoneId);
		ApnimandiCollectionType apnimandiCollectionType = apnimaniCollectionTypeService.findOne(collectionTypeId);
		
		String serviceId = ApnimandiConstants.SERVICE_PREFIX + "_" + zoneMaster.getRoadDivision() + "." + serviceCategory;		
		List<TaxHeadMaster> accountHeadMasters = apnimandiCollectionDetailService.getAccountHeadMasterByService(serviceId);
		if(null!=accountHeadMasters) {
			boolean isFound=false;
			for(TaxHeadMaster accountHead :accountHeadMasters) {
				isFound=false;
				if(ApnimandiConstants.APNI_MANDI.equalsIgnoreCase(apnimandiCollectionType.getCode())) {
					if(ApnimandiConstants.SERVICE_TYPE_COLLECTION_AMOUNT.equalsIgnoreCase(servicetype)) {
						if((ApnimandiConstants.GROUND_RENT_APNI_MANDI + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())
							|| (ApnimandiConstants.PENALITIES_FINES_APNI_MANDI + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())
								|| (ApnimandiConstants.CGST_UTGST_APNI_MANDI + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())
									|| (ApnimandiConstants.IGST_APNI_MANDI + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())) {
							isFound=true;
						}
					}
				}else if(ApnimandiConstants.DAY_MARKET.equalsIgnoreCase(apnimandiCollectionType.getCode())) {
					if(ApnimandiConstants.SERVICE_TYPE_CONTRACTOR_SECURITY_FEE.equalsIgnoreCase(servicetype)) {					
						if((ApnimandiConstants.SECURITY_DAY_MARKET + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())) {
							isFound=true;
						}
					}else if(ApnimandiConstants.SERVICE_TYPE_RENT_AMOUNT.equalsIgnoreCase(servicetype)) {
						if((ApnimandiConstants.GROUND_RENT_DAY_MARKET + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())
							|| (ApnimandiConstants.PENALITIES_FINES_DAY_MARKET + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())
								|| (ApnimandiConstants.CGST_UTGST_DAY_MARKET + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())
									|| (ApnimandiConstants.IGST_DAY_MARKET + "_" + zoneMaster.getRoadDivision()).equalsIgnoreCase(accountHead.getCode())) {
							isFound=true;
						}
					}
				}
				if(isFound) {
					JsonObject accountHeadJson = new JsonObject();
					accountHeadJson.addProperty("code", accountHead.getCode());
					accountHeadJson.addProperty("name", accountHead.getName());
					accountHeadJson.addProperty("isDebit", accountHead.getIsDebit());
					accountHeads.add(accountHeadJson);
				}							
			}
		}		
		jsonObject.add("accountHeads", accountHeads);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(jsonObject.toString(), response.getWriter());
	}
	
	@RequestMapping(value = "/ajaxsearch", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String ajaxsearch(final Model model, @ModelAttribute final ApnimandiCollectionDetails apnimandiCollectionDetails) {
        final List<ApnimandiCollectionSearchResult> searchResultList = apnimandiCollectionDetailService.search(apnimandiCollectionDetails);
        return new StringBuilder("{ \"data\":").append(toSearchResultJson(searchResultList)).append("}")
                .toString();
    }
	
	@RequestMapping(value = "/view-receipt/{receiptid}", method = RequestMethod.GET)
    public String viewReceipt(@PathVariable("receiptid") final String receiptId, final Model model) {
		String reportId = apnimandiCollectionDetailService.getReceipt(receiptId);
		model.addAttribute("reportId", reportId);
        return APNIMANDI_COLLECTION_RECEIPT_VIEW;
    }
	
	public Object toSearchResultJson(final Object object) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.registerTypeAdapter(ApnimandiCollectionSearchResult.class, new CollectionJsonAdaptor()).create();
        String json = gson.toJson(object);
        return json;
    }
	
	private String getMessage(String messageLabel,String designation,String approver) {
		return messageSource.getMessage(messageLabel,new String[] {approver.concat("~").concat(designation)}, null);
	}
	
	private String getMessage(String messageLabel) {
		return messageSource.getMessage(messageLabel, null, null);
	}
	
	@RequestMapping(value = "/ajax/getDepertmentsByZone", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void getDepertmentsByZone(@ModelAttribute("departments") @RequestParam final String currentState,
            						 @RequestParam final String objectType, 
            						 @RequestParam final Long zoneid,
       								 final HttpServletResponse response) throws IOException {
		JsonObject jsonObject = new JsonObject();
		JsonArray departments = new JsonArray();
		if(null != zoneid) {
			ZoneMaster zone = zoneMasterService.findOne(zoneid);
			jsonObject.addProperty("additionalRule", zone.getRoadDivision());
			List<Department> depts =  apnimandiUtil.getDepartmentsByZone(currentState, objectType, zone.getRoadDivision());
			depts.forEach(dept -> {
				JsonObject department = new JsonObject();
				department.addProperty("code", dept.getCode());
				department.addProperty("name", dept.getName());
				departments.add(department);
			});
		}
		jsonObject.add("departments", departments);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(jsonObject.toString(), response.getWriter());        
    }
	
	@RequestMapping(value = "/ajax/getSitesByZone", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void getSitesByZone(@ModelAttribute("siteMaster") @RequestParam final Long zoneid,
       								 final HttpServletResponse response) throws IOException {
		JsonObject jsonObject = new JsonObject();
		JsonArray sites = new JsonArray();
		if(null != zoneid) {
			ZoneMaster zone = zoneMasterService.findOne(zoneid);
			jsonObject.addProperty("additionalRule", zone.getRoadDivision());
			List<SiteMaster> siteMasters =  siteMasterService.findByZone(zone);
			siteMasters.forEach(siteMaster -> {
				JsonObject site = new JsonObject();
				site.addProperty("id", siteMaster.getId());
				site.addProperty("name", siteMaster.getName());
				sites.add(site);
			});
		}
		jsonObject.add("sites", sites);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(jsonObject.toString(), response.getWriter());        
    }
	
	@RequestMapping(value = "/day-market-collection", method = RequestMethod.POST)
    public String dmCollection(final Model model) {
        return DM_COLLECTION_SEARCH;
    }
	
	@RequestMapping(value = "/apni-mandi-collection", method = RequestMethod.POST)
    public String amCollection(final Model model) {
        return AM_COLLECTION_SEARCH;
    }
	
	@RequestMapping(value = "/ajax/day-market-collection", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String dmCollectionsearch(@RequestParam final String fromDate,
			  									   @RequestParam final String toDate) {
		Date newFromDate=null;
		Date newToDate=null;	
		if(StringUtils.isNotEmpty(fromDate)) {
			newFromDate = DateUtils.getDate(fromDate, "dd/MM/yyyy");
		}		
		if(StringUtils.isNotEmpty(toDate)) {
			newToDate = DateUtils.getDate(toDate, "dd/MM/yyyy");
		}else {
			newToDate = DateUtils.today();
		}	
		if(null!=newFromDate && null!=newToDate) {
			final List<ApnimandiCollectionMISReport> searchResultList = apnimandiCollectionDetailService.dmCollectionReport(newFromDate, newToDate);
	        return new StringBuilder("{ \"data\":").append(toMISReportJson(searchResultList)).append("}")
	                .toString();
		}else {
			return new StringBuilder("{ \"data\":").append("[]").append("}").toString();
		}
    }
	
	public Object toMISReportJson(final Object object) {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		final Gson gson = gsonBuilder.registerTypeAdapter(ApnimandiCollectionMISReport.class, new MisCollectionJsonAdaptor()).create();
		String json = gson.toJson(object);
		return json;
    }
	
	@RequestMapping(value = "/ajax/apni-mandi-collection", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String amCollectionsearch(@RequestParam final String fromDate,
			  									   @RequestParam final String toDate) {
		Date newFromDate=null;
		Date newToDate=null;	
		if(StringUtils.isNotEmpty(fromDate)) {
			newFromDate = DateUtils.getDate(fromDate, "dd/MM/yyyy");
		}		
		if(StringUtils.isNotEmpty(toDate)) {
			newToDate = DateUtils.getDate(toDate, "dd/MM/yyyy");
		}else {
			newToDate = DateUtils.today();
		}	
		if(null!=newFromDate && null!=newToDate) {
			final List<ApnimandiCollectionMISReport> searchResultList = apnimandiCollectionDetailService.amCollectionReport(newFromDate, newToDate);
	        return new StringBuilder("{ \"data\":").append(toMISReportJson(searchResultList)).append("}")
	                .toString();
		}else {
			return new StringBuilder("{ \"data\":").append("[]").append("}").toString();
		}
    }
	
	@RequestMapping(value = "/estimated-income-report", method = RequestMethod.POST)
    public String estimatedIncomeSearch(final Model model) {
		model.addAttribute("apnimandiCollectionTypes", apnimaniCollectionTypeService.getActiveApnimandiCollectionTypeList());
        return DMAM_INCOME_REPORT;
    }	
	
	@RequestMapping(value = "/ajax/estimated-income-report", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String amCollectionsearch(@RequestParam final String collTypeCode,
    											   @RequestParam final String fromDate,
			  									   @RequestParam final String toDate) {
		Date newFromDate=null;
		Date newToDate=null;	
		if(StringUtils.isNotEmpty(fromDate)) {
			newFromDate = DateUtils.getDate(fromDate, "dd/MM/yyyy");
		}		
		if(StringUtils.isNotEmpty(toDate)) {
			newToDate = DateUtils.getDate(toDate, "dd/MM/yyyy");
		}else {
			newToDate = DateUtils.today();
		}	
		if(null!=newFromDate && null!=newToDate) {
			final List<EstimatedIncomeReport> searchResultList = apnimandiCollectionDetailService.estimatedIncomeReport(collTypeCode, newFromDate, newToDate);
	        return new StringBuilder("{ \"data\":").append(toEIReportJson(searchResultList)).append("}")
	                .toString();
		}else {
			return new StringBuilder("{ \"data\":").append("[]").append("}").toString();
		}
    }
	
	public Object toEIReportJson(final Object object) {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		final Gson gson = gsonBuilder.registerTypeAdapter(EstimatedIncomeReport.class, new EstimatedIncomeJsonAdaptor()).create();
		String json = gson.toJson(object);
		return json;
    }
}
