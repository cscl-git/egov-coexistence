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
package org.egov.council.web.controller;

import static org.egov.council.utils.constants.CouncilConstants.APPROVED;
import static org.egov.council.utils.constants.CouncilConstants.COUNCIL_RESOLUTION;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGRESOLUTIONFILENAME;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGUSEDINRMOM;
import static org.egov.council.utils.constants.CouncilConstants.MEETING_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.MODULE_FULLNAME;
import static org.egov.council.utils.constants.CouncilConstants.MODULE_NAME;
import static org.egov.council.utils.constants.CouncilConstants.MOM_FINALISED;
import static org.egov.council.utils.constants.CouncilConstants.PREAMBLE_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.PREAMBLE_STATUS_ADJOURNED;
import static org.egov.council.utils.constants.CouncilConstants.RESOLUTION_APPROVED_PREAMBLE;
import static org.egov.council.utils.constants.CouncilConstants.RESOLUTION_STATUS_ADJURNED;
import static org.egov.council.utils.constants.CouncilConstants.RESOLUTION_STATUS_APPROVED;
import static org.egov.council.utils.constants.CouncilConstants.RESOLUTION_STATUS_RECORDED;
import static org.egov.council.utils.constants.CouncilConstants.RESOLUTION_STATUS_SANCTIONED;
import static org.egov.council.utils.constants.CouncilConstants.REVENUE_HIERARCHY_TYPE;
import static org.egov.council.utils.constants.CouncilConstants.WARD;
import static org.egov.council.utils.constants.CouncilConstants.getMeetingTimings;
import static org.egov.infra.utils.JsonUtils.toJSON;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.council.autonumber.MOMResolutionNumberGenerator;
import org.egov.council.entity.CommitteeType;
import org.egov.council.entity.CouncilDataResponse;
import org.egov.council.entity.CouncilDataUpdateRequest;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.entity.CouncilMeetingType;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.entity.CouncilPreambleBidderDetails;
import org.egov.council.entity.MeetingMOM;
import org.egov.council.entity.enums.PreambleType;
import org.egov.council.service.BidderService;
import org.egov.council.service.CommitteeTypeService;
import org.egov.council.service.CouncilMeetingService;
import org.egov.council.service.CouncilMeetingTypeService;
import org.egov.council.service.CouncilPreambleService;
import org.egov.council.service.CouncilReportService;
import org.egov.council.service.CouncilSmsAndEmailService;
import org.egov.council.service.CouncilThirdPartyService;
import org.egov.council.service.es.CouncilMeetingIndexService;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.council.web.adaptor.CouncilDepartmentJsonAdaptor;
import org.egov.council.web.adaptor.CouncilMeetingJsonAdaptor;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.utils.ApplicationConstant;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.utils.FileUtils;
import org.egov.infra.utils.StringUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.web.support.json.adapter.BoundaryAdapter;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("/councilmom")
public class CouncilMomController  extends GenericWorkFlowController{
    
    private static final String RESOLUTION_NUMBER_AUTO = "RESOLUTION_NUMBER_AUTO";
    private static final String MESSAGE = "message";
    private static final String COUNCIL_MEETING = "councilMeeting";
    private static final String COUNCIL_MOM_MEETING_SEARCH = "councilmomMeeting-search";
    private static final String COUNCILMOM_NEW = "councilMom-new";
    private static final String COUNCILMEETING_EDIT = "councilmeeting-edit";
    private static final String COUNCILMOM_RESULT = "councilmom-result";
    private static final String COUNCILMOM_SEARCH = "councilmom-search";
    private static final String COUNCILMOM_VIEW = "councilmom-view";
    private static final String COMMONERRORPAGE = "common-error-page";
    private static final String APPLICATION_RTF = "application/rtf";
    
    private static final String COUNCIL_COMMON_WORKFLOW = "CouncilCommonWorkflow";
    private static final String APPLICATION_HISTORY = "applicationHistory";
    private static final String APPROVAL_POSITION = "approvalPosition";
    private static final String WORK_FLOW_ACTION = "workFlowAction";
    private static final String APPROVAL_COMENT = "approvalComent";
    private static final String CURRENT_STATE = "currentState";
    private static final String ADDITIONALRULE = "additionalRule";

    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CommitteeTypeService committeeTypeService;

    @Autowired
    private CouncilMeetingService councilMeetingService;

    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private AutonumberServiceBeanResolver autonumberServiceBeanResolver;

    @Autowired
    private CouncilPreambleService councilPreambleService;

    @Autowired
    private CouncilReportService councilReportService;

    @Autowired
    private CouncilSmsAndEmailService councilSmsAndEmailService;

    @Autowired
    private CouncilMeetingIndexService councilMeetingIndexService;
    @Qualifier("fileStoreService")
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private CouncilMeetingTypeService councilMeetingTypeService;
    @Autowired
    protected EgovMasterDataCaching masterDataCache;
    @Autowired
    private CouncilThirdPartyService councilThirdPartyService;

    @ModelAttribute("committeeType")
    public List<CommitteeType> getCommitteTypeList() {
        return committeeTypeService.getActiveCommiteeType();
    }

    @ModelAttribute("meetingTimingMap")
    public Map<String, String> getMeetingTimingList() {
        return getMeetingTimings();
    }

    @ModelAttribute("meetingType")
    public List<CouncilMeetingType> getmeetingTypeList() {
        return councilMeetingTypeService.findAllActiveMeetingType();
    }

    @ModelAttribute("resolutionStatus")
    public List<EgwStatus> getResolutionStatusList() {
        return egwStatusHibernateDAO.getStatusByModule(COUNCIL_RESOLUTION);
    }

    @RequestMapping(value = "/new/{id}", method = RequestMethod.GET)
    public String newForm(@PathVariable("id") final Long id, Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);

        if (null != councilMeeting && null != councilMeeting.getStatus()) {
            if (APPROVED.equals(councilMeeting.getStatus().getCode())) {
                model.addAttribute(MESSAGE, "msg.attendance.not.finalizd");
                return COMMONERRORPAGE;
            }
            if (MOM_FINALISED.equals(councilMeeting.getStatus().getCode())) {
                model.addAttribute(MESSAGE, "msg.mom.alreadyfinalized");
                return COMMONERRORPAGE;
            }

        }
        if (councilMeeting != null) {
            sortMeetingMomByItemNumber(councilMeeting);
            model.addAttribute("autoResolutionNoGenEnabled", isAutoResolutionNoGenEnabled());
            model.addAttribute(COUNCIL_MEETING, councilMeeting);
            if(null == councilMeeting.getState()) {
	            model.addAttribute(ADDITIONALRULE, COUNCIL_COMMON_WORKFLOW);
	            model.addAttribute(CURRENT_STATE, "NEW");
            }else {
            	model.addAttribute(CURRENT_STATE, councilMeeting.getCurrentState()
                        .getValue());
            	if(CouncilConstants.REJECTED.equalsIgnoreCase(councilMeeting.getStatus().getCode())){
                    model.addAttribute(ADDITIONALRULE, COUNCIL_COMMON_WORKFLOW);
                }
            	
            	model.addAttribute(APPLICATION_HISTORY,
                        councilThirdPartyService.getHistory(councilMeeting));
            	model.addAttribute("wfNextAction", councilMeeting.getState().getNextAction());
            }
            prepareWorkFlowOnLoad(model, councilMeeting);
        }
        return COUNCILMOM_NEW;
    }
    
    private void prepareWorkFlowOnLoad(final Model model,
            CouncilMeeting councilMeeting) {
		model.addAttribute("stateType", councilMeeting.getClass().getSimpleName());
		WorkflowContainer workFlowContainer = new WorkflowContainer();
		prepareWorkflow(model, councilMeeting, workFlowContainer, true);
	}

    private void sortMeetingMomByItemNumber(CouncilMeeting councilMeeting) {
        councilMeeting.getMeetingMOMs().sort(
                (MeetingMOM f1, MeetingMOM f2) -> Long.valueOf(f1.getItemNumber()).compareTo(Long.valueOf(f2.getItemNumber())));

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(
            @Valid @ModelAttribute final CouncilMeeting councilMeeting,
            final BindingResult errors, final Model model,
            final RedirectAttributes redirectAttrs,final HttpServletRequest request,
            @RequestParam String workFlowAction) {
        if (errors.hasErrors()) {
        	prepareWorkFlowOnLoad(model, councilMeeting);
        	if(null == councilMeeting.getCurrentState()) {
        		model.addAttribute(CURRENT_STATE, "NEW");
        	}else {
        		model.addAttribute(CURRENT_STATE, councilMeeting
                    .getCurrentState().getValue());
        	}
            return COUNCILMEETING_EDIT;
        }
        /*String biddersId = request.getParameter("councilBidderHdn");
        if (StringUtils.isNotEmpty(biddersId)) {
	        String[] bidderId = biddersId.split(",");
	
	        ArrayList<CouncilPreambleBidderDetails> bidderlist = new ArrayList<>();
	        ArrayList<CouncilPreambleBidderDetails> existingBidderlist = new ArrayList<>();
	        for (String bidder : bidderId) {
	            if (bidder != null && !bidder.isEmpty())
	                bidderlist.add(bidderService.getBidderDetailsbyId(Long.valueOf(bidder)));
	        }
	        for (MeetingMOM mom : councilMeeting.getMeetingMOMs()) {
	            for (CouncilPreambleBidderDetails bidders : mom.getPreamble().getBidderDetails()) {
	                existingBidderlist.add(bidders);
	            }
	        }
	        existingBidderlist.removeAll(bidderlist);
	        for (CouncilPreambleBidderDetails selectedBidder : bidderlist) {
	            selectedBidder.setIsAwarded(true);
	        }
	        for (CouncilPreambleBidderDetails rejectedBidder : existingBidderlist) {
	            rejectedBidder.setIsAwarded(false);
	        }
        }*/
        
        EgwStatus preambleResolutionApprovedStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(PREAMBLE_MODULENAME,
                RESOLUTION_APPROVED_PREAMBLE);
        Long itemNumber = Long.valueOf(0);
        for (final MeetingMOM meetingMOM : councilMeeting.getMeetingMOMs())
            if (meetingMOM.getId() != null)
                itemNumber++;

        for (MeetingMOM meetingMOM : councilMeeting.getMeetingMOMs()) {
            if (meetingMOM.getPreamble().getId() == null) {
                meetingMOM
                        .setPreamble(councilPreambleService
                                .buildSumotoPreamble(meetingMOM,
                                        preambleResolutionApprovedStatus));
                meetingMOM.setMeeting(councilMeeting);
                itemNumber++;
                meetingMOM.setItemNumber(itemNumber.toString());

            }
        }
        
        Long approvalPosition = 0l;
        String approvalComment = "";
        String approverName = "";
        String nextDesignation = "";
        String message = StringUtils.EMPTY;
        
        if (request.getParameter(APPROVAL_COMENT) != null)
            approvalComment = request.getParameter(APPROVAL_COMENT);
        if (request.getParameter(WORK_FLOW_ACTION) != null)
            workFlowAction = request.getParameter(WORK_FLOW_ACTION);
        if (request.getParameter("approverName") != null)
            approverName = request.getParameter("approverName");
        if (request.getParameter("nextDesignation") != null)
            nextDesignation = request.getParameter("nextDesignation");
        if (request.getParameter(APPROVAL_POSITION) != null
                && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request
                    .getParameter(APPROVAL_POSITION));
        
        if (councilMeeting.getFiles() != null && councilMeeting.getFiles().length > 0) {
            councilMeeting.setSupportDocs(councilMeetingService.addToFileStore(councilMeeting.getFiles()));
        }
        councilMeetingService.update(councilMeeting, approvalPosition,
                approvalComment, workFlowAction);
        
        if (null != workFlowAction 
        		&& CouncilConstants.WF_APPROVE_BUTTON.equalsIgnoreCase(workFlowAction)) {
        	councilMeeting
                .setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(
                        MEETING_MODULENAME, MEETINGUSEDINRMOM));
        	councilMeetingService.update(councilMeeting);
        }
        
        if (null != workFlowAction) {
            if (CouncilConstants.WF_STATE_REJECT
                    .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.meetingmom.reject",nextDesignation,approverName,
                		councilMeeting);
            } else if (CouncilConstants.WF_APPROVE_BUTTON
                    .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.meetingmom.success",nextDesignation,approverName,
                		councilMeeting);
            } else if (CouncilConstants.WF_FORWARD_BUTTON
                    .equalsIgnoreCase(workFlowAction)) {
            	if(null != councilMeeting.getStatus() && CouncilConstants.CREATED.equalsIgnoreCase(councilMeeting.getStatus().getCode())) {
                    message = getMessage("msg.meetingmom.create",nextDesignation,approverName,
                            		councilMeeting);      
            	}else {
            		message = getMessage("msg.meetingmom.forward",nextDesignation,approverName,
                		councilMeeting);
            	}
            }
            redirectAttrs.addFlashAttribute(MESSAGE, message);
        }
        
        return "redirect:/councilmom/result/" + councilMeeting.getId();
    }

    @RequestMapping(value = "/result/{id}", method = RequestMethod.GET)
    public String result(@PathVariable("id") final Long id, Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        sortMeetingMomByItemNumber(councilMeeting);
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        return COUNCILMOM_RESULT;
    }

    @RequestMapping(value = "/meetingsearch/{mode}", method = RequestMethod.POST)
    public String searchMeeting(@PathVariable("mode") final String mode,
            Model model) {
        model.addAttribute(COUNCIL_MEETING, new CouncilMeeting());
        return COUNCIL_MOM_MEETING_SEARCH;

    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        if (!councilMeeting.getMeetingMOMs().get(0).isLegacy()) {

            sortMeetingMomByItemNumber(councilMeeting);
        }
        model.addAttribute(COUNCIL_MEETING, councilMeeting);

        return COUNCILMOM_VIEW;
    }

    @RequestMapping(value = "/search/{mode}", method = RequestMethod.POST)
    public String search(@PathVariable("mode") final String mode, Model model) {
        CouncilMeeting councilMeeting = new CouncilMeeting();
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        return COUNCILMOM_SEARCH;

    }

    @RequestMapping(value = "/searchcreated-mom/{mode}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String searchCreatedMOM(@PathVariable("mode") final String mode, @ModelAttribute final CouncilMeeting councilMeeting) {

        if (null != mode && !"".equals(mode)) {
            List<CouncilMeeting> searchResultList;

            if ("edit".equalsIgnoreCase(mode)) {
                searchResultList = councilMeetingService.searchMeetingWithMomCreatedStatus(councilMeeting);
            } else {
                searchResultList = councilMeetingService.searchMeeting(councilMeeting);
            }
            return new StringBuilder("{ \"data\":")
                    .append(toJSON(searchResultList, CouncilMeeting.class,
                            CouncilMeetingJsonAdaptor.class))
                    .append("}")
                    .toString();
        }

        return null;
    }

    @RequestMapping(value = "/departmentlist", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String ajaxsearch(@ModelAttribute final CouncilMeeting councilMeeting) {
        //List<Department> departmentList = departmentService.getAllDepartments();
    	List<Department> departmentList = masterDataCache.get(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_AGENDA);
        return new StringBuilder("{ \"departmentLists\":")
                .append(toJSON(departmentList, Department.class,
                        CouncilDepartmentJsonAdaptor.class))
                .append("}")
                .toString();
    }

    @RequestMapping(value = "/resolutionlist", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String ajaxsearchResolutionlist(@ModelAttribute final CouncilMeeting councilMeeting) {
        List<EgwStatus> resolutionList = egwStatusHibernateDAO
                .getStatusByModule(COUNCIL_RESOLUTION);
        Gson gson = new Gson();
        Type type = new TypeToken<List<EgwStatus>>() {
        }.getType();
        String json = gson.toJson(resolutionList, type);
        return new StringBuilder("{ \"resolutionLists\":")
                .append(json).append("}").toString();
    }

    @RequestMapping(value = "/wardlist", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String ajaxsearchWardlist(@ModelAttribute final CouncilMeeting councilMeeting) {
        List<Boundary> wardList = boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(WARD,
                        REVENUE_HIERARCHY_TYPE);
        return new StringBuilder("{ \"wardLists\":")
                .append(toJSON(wardList, Boundary.class, BoundaryAdapter.class))
                .append("}").toString();
    }

    @RequestMapping(value = "/generateresolution", method = RequestMethod.POST)
    public String generateResolutionnumber( final HttpServletRequest request,
            @Valid @ModelAttribute final CouncilMeeting councilMeeting) throws ParseException {
        byte[] reportOutput;
        final RestTemplate restTemplate = new RestTemplate();
        String response = null;
        EgwStatus resoulutionApprovedStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(COUNCIL_RESOLUTION,
                RESOLUTION_STATUS_APPROVED);
        EgwStatus resoulutionAdjurnedStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(COUNCIL_RESOLUTION,
                RESOLUTION_STATUS_ADJURNED);
        EgwStatus resoulutionSanctionedStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(COUNCIL_RESOLUTION,
                RESOLUTION_STATUS_SANCTIONED);
        EgwStatus resoulutionRecordedStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(COUNCIL_RESOLUTION,
                RESOLUTION_STATUS_RECORDED);
        EgwStatus preambleAdjurnedStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(PREAMBLE_MODULENAME,
                PREAMBLE_STATUS_ADJOURNED);
        EgwStatus resolutionApprovedStatus = egwStatusHibernateDAO.getStatusByModuleAndCode(PREAMBLE_MODULENAME,
                RESOLUTION_APPROVED_PREAMBLE);

        Long itemNumber = Long.valueOf(0);
        for (final MeetingMOM meetingMOM : councilMeeting.getMeetingMOMs())
            if (meetingMOM.getId() != null)
                itemNumber++;

        for (MeetingMOM meetingMOM : councilMeeting.getMeetingMOMs()) {
            if (meetingMOM.getPreamble().getId() == null) {
                itemNumber++;
                meetingMOM.setItemNumber(itemNumber.toString());
                meetingMOM.setPreamble(councilPreambleService
                        .buildSumotoPreamble(meetingMOM, resolutionApprovedStatus));
                meetingMOM.setMeeting(councilMeeting);
            }
        }

        for (MeetingMOM meetingMOM : councilMeeting.getMeetingMOMs()) {

            // if mom status is approved, generate resolution number
            if (meetingMOM.getResolutionStatus().getCode().equals(resoulutionApprovedStatus.getCode())
                    && isAutoResolutionNoGenEnabled()) {
                MOMResolutionNumberGenerator momResolutionNumberGenerator = autonumberServiceBeanResolver
                        .getAutoNumberServiceFor(MOMResolutionNumberGenerator.class);
                meetingMOM.setResolutionNumber(
                        meetingMOM.getResolutionNumber() != null ? meetingMOM.getResolutionNumber() : momResolutionNumberGenerator
                                .getNextNumber(meetingMOM));
                meetingMOM.getPreamble().setStatus(resolutionApprovedStatus);
                // if mom status adjourned, update preamble status to adjurned. These record will be used in next meeting.
            } else if (meetingMOM.getResolutionStatus().getCode().equals(resoulutionAdjurnedStatus.getCode())) {
                meetingMOM.getPreamble()
                        .setStatus(preambleAdjurnedStatus);
            } else if (meetingMOM.getResolutionStatus().getCode().equals(resoulutionRecordedStatus.getCode())) {
                meetingMOM.getPreamble()
                        .setStatus(resoulutionRecordedStatus);
            } else if (meetingMOM.getResolutionStatus().getCode().equals(resoulutionSanctionedStatus.getCode())) {
                meetingMOM.getPreamble()
                        .setStatus(resoulutionSanctionedStatus);
            }
        }
        councilMeeting.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(MEETING_MODULENAME, MOM_FINALISED));
        for (MeetingMOM meetingMOM : councilMeeting.getMeetingMOMs()) {
            if (meetingMOM.getPreamble().getId() != null && meetingMOM.getPreamble().getReferenceNumber() != null
                    && null == meetingMOM.getPreamble().getStatusMessage() &&  meetingMOM.getPreamble().getTypeOfPreamble().ordinal()==0) {
                CouncilDataUpdateRequest councilDataUpdateRequest = new CouncilDataUpdateRequest();
                councilDataUpdateRequest.setReferenceNo(meetingMOM.getPreamble().getReferenceNumber());
                councilDataUpdateRequest.setPreambleNo(meetingMOM.getPreamble().getPreambleNumber());
                councilDataUpdateRequest.setResolutionDate(DateUtils.currentDateToGivenFormat("yyyy/MM/dd"));
                councilDataUpdateRequest.setSanctionAmount(meetingMOM.getPreamble().getSanctionAmount().doubleValue());
                councilDataUpdateRequest.setResolutionNo(meetingMOM.getResolutionNumber());
                councilDataUpdateRequest.setResolution(meetingMOM.getResolutionDetail());
                councilDataUpdateRequest.setResolutionStatus(meetingMOM.getResolutionStatus().getDescription());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<CouncilDataUpdateRequest> requestObj = new HttpEntity<>(councilDataUpdateRequest, headers);

                response = restTemplate.postForObject(messageSource.getMessage("appms.endpoint.url", null, null), requestObj,
                        String.class);
                if (response != null) {
                    Gson gson = new Gson();
                    CouncilDataResponse res = gson.fromJson(response, CouncilDataResponse.class);
                    if (null == res) {
                        meetingMOM.getPreamble().setStatusMessage(StringUtils.EMPTY);
                    } else if (res.getStatus().equalsIgnoreCase("0")) {
                        meetingMOM.getPreamble().setStatusMessage("Failed");
                    }

                    else if (res.getStatus().equalsIgnoreCase("1")) {
                        meetingMOM.getPreamble().setStatusMessage("Success");
                    }
                }

            }

        }
        reportOutput = generateMomPdfByPassingMeeting(councilMeeting);
        if (reportOutput != null) {
            councilMeeting.setFilestore(fileStoreService.store(
                    FileUtils.byteArrayToFile(reportOutput, MEETINGRESOLUTIONFILENAME, "rtf").toFile(), MEETINGRESOLUTIONFILENAME,
                    APPLICATION_RTF, MODULE_NAME));
        }
        councilMeetingService.update(councilMeeting);
        councilMeetingIndexService.createCouncilMeetingIndex(councilMeeting);
        councilSmsAndEmailService.sendSms(councilMeeting, null);
        councilSmsAndEmailService.sendEmail(councilMeeting, null, reportOutput);
        return "forward:/councilmeeting/generateresolution/" + councilMeeting.getId();
    }

    private byte[] generateMomPdfByPassingMeeting(final CouncilMeeting councilMeeting) {
        return councilReportService.generatePDFForMom(councilMeeting);
    }

    public Boolean isAutoResolutionNoGenEnabled() {
        return councilPreambleService.autoGenerationModeEnabled(
                MODULE_FULLNAME, RESOLUTION_NUMBER_AUTO);
    }
    
    private String getMessage(String messageLabel,String designation,String approver,
            final CouncilMeeting councilMeeting) {
		String message;
		message = messageSource.getMessage(messageLabel,
									new String[] { councilMeeting.getMeetingNumber() ,approver.concat("~").concat(designation) }, null);
		return message;
	}
}