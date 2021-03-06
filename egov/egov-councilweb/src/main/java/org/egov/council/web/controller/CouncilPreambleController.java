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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.egov.council.utils.constants.CouncilConstants.AGENDA_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.AGENDA_STATUS_APPROVED;
import static org.egov.council.utils.constants.CouncilConstants.IMPLEMENTATIONSTATUS;
import static org.egov.council.utils.constants.CouncilConstants.IMPLEMENTATION_STATUS_FINISHED;
import static org.egov.council.utils.constants.CouncilConstants.MODULE_FULLNAME;
import static org.egov.council.utils.constants.CouncilConstants.PREAMBLEUSEDINAGENDA;
import static org.egov.council.utils.constants.CouncilConstants.PREAMBLE_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.AGENDA_STATUS_INWORKFLOW;
import static org.egov.infra.utils.JsonUtils.toJSON;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.council.autonumber.PreambleNumberGenerator;
import org.egov.council.entity.CommitteeType;
import org.egov.council.entity.CouncilAgenda;
import org.egov.council.entity.CouncilAgendaDetails;
import org.egov.council.entity.CouncilAgendaInvitation;
import org.egov.council.entity.CouncilAgendaType;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.entity.enums.PreambleType;
import org.egov.council.enums.PreambleTypeEnum;
import org.egov.council.service.BidderService;
import org.egov.council.service.CommitteeTypeService;
import org.egov.council.service.CouncilAgendaInvitationService;
import org.egov.council.service.CouncilAgendaService;
import org.egov.council.service.CouncilAgendaTypeService;
import org.egov.council.service.CouncilPreambleService;
import org.egov.council.service.CouncilSmsAndEmailService;
import org.egov.council.service.CouncilThirdPartyService;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.council.web.adaptor.CouncilPreambleJsonAdaptor;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.Assignment;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Role;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationConstant;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/councilpreamble")
public class CouncilPreambleController extends GenericWorkFlowController {
    private static final String COUNCIL_COMMON_WORKFLOW = "CouncilCommonWorkflow";
    private static final String COUNCIL_ABA_WORKFLOW = "CouncilABAWorkflow";
    private static final String PREAMBLE_NUMBER_AUTO = "PREAMBLE_NUMBER_AUTO";
    private static final String REDIRECT_COUNCILPREAMBLE_RESULT = "redirect:/councilpreamble/result/";
    private static final String MESSAGE2 = "message";
    private static final String APPLICATION_HISTORY = "applicationHistory";
    private static final String APPROVAL_POSITION = "approvalPosition";
    private static final String WORK_FLOW_ACTION = "workFlowAction";
    private static final String APPROVAL_COMENT = "approvalComent";
    private static final String CURRENT_STATE = "currentState";
    private static final String COUNCIL_PREAMBLE = "councilPreamble";
    private static final String COUNCILPREAMBLE_NEW = "councilpreamble-new";
    private static final String COUNCILPREAMBLE_RESULT = "councilpreamble-result";
    private static final String COUNCILPREAMBLE_EDIT = "councilpreamble-edit";
    private static final String COUNCILPREAMBLE_API_EDIT ="councilpreambleapi-edit";
    private static final String COUNCILPREAMBLE_VIEW = "councilpreamble-view";
    private static final String COUNCILPREAMBLE_SEARCH = "councilpreamble-search";
    private static final String COUNCILPREAMBLE_UPDATE_STATUS = "councilpreamble-update-status";
    private static final String COMMONERRORPAGE = "common-error-page";
    private static final String ADDITIONALRULE = "additionalRule";
    private static final String IS_AGENDA_ADMIN = "isAgendaAdmin";
    private static final String RICH_TEXT_EDITOR = "editor";
    private static final String COUNCIL_AGENDA_INVITATION = "councilAgendaInvitation";
    private static final String COUNCIL_AGENDA_INVITATION_NEW = "councilagendainvitation-new";
    private static final String REDIRECT_COUNCIL_AGENDA_INVITATION_RESULT = "redirect:/councilpreamble/agendainvitation/result/";
    private static final String COUNCIL_AGENDA_INVITATION_RESULT = "councilagendainvitation-result";
    
    private static final String COUNCILPREAMBLE_API_VIEW = "councilpreamble-viewnew";
    private static final Logger LOGGER = Logger
            .getLogger(CouncilPreambleController.class);
    @Qualifier("fileStoreService")
    @Autowired
    protected FileStoreService fileStoreService;
    @Autowired
    protected FileStoreUtils fileStoreUtils;
    @Autowired
    private CouncilPreambleService councilPreambleService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;
    @Autowired
    private AutonumberServiceBeanResolver autonumberServiceBeanResolver;
    @Autowired
    private CouncilThirdPartyService councilThirdPartyService;
    @Autowired
    private BidderService bidderService;
    @Autowired
    protected EgovMasterDataCaching masterDataCache;
    @Autowired
    private MicroserviceUtils microserviceUtils;
    @Autowired
    protected CommitteeTypeService committeeTypeService;
    @Autowired
    protected CouncilAgendaService councilAgendaService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    protected CouncilAgendaTypeService councilAgendaTypeService;
    @Autowired
    protected CouncilSmsAndEmailService councilSmsAndEmailService;
    @Autowired
    protected CouncilAgendaInvitationService councilAgendaInvitationService;

    @ModelAttribute("departments")
    public List<Department> getDepartmentList() {
        return masterDataCache.get(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_AGENDA);
    }

    @ModelAttribute("wards")
    public List<Boundary> getWardsList() {
        /*return boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(WARD,
                        REVENUE_HIERARCHY_TYPE);*/
    	return null;
    }

    @ModelAttribute("URL")
    public String getAppConfigValues() {
    	/*List<AppConfigValues> appConfigValue = appConfigValueService
                .getConfigValuesByModuleAndKey(MODULE_FULLNAME, CHECK_BUDGET);
        if (appConfigValue != null && !appConfigValue.isEmpty())
            return appConfigValueService
                    .getConfigValuesByModuleAndKey(MODULE_FULLNAME,
                            CHECK_BUDGET)
                    .get(0).getValue();*/
        return "";
    }
    
    @ModelAttribute("committeeType")
    public List<CommitteeType> getCommitteTypeList() {
        return committeeTypeService.getActiveCommiteeType();
    }

    @ModelAttribute("councilAgendaType")
    public List<CouncilAgendaType> getCouncilAgendaTypeList() {
        return councilAgendaTypeService.getActiveCouncilAgendaTypes();
    }
    
    @ModelAttribute("implementationStatus")
    public List<EgwStatus> getImplementationStatusList() {
        return egwStatusHibernateDAO.getStatusByModule(IMPLEMENTATIONSTATUS);
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newForm(final Model model) {
        CouncilPreamble councilPreamble = new CouncilPreamble();
        councilPreamble.setType(PreambleType.GENERAL);
        EmployeeInfo info = getEmployee();
        model.addAttribute(IS_AGENDA_ADMIN, isAgendaAdmin(info));
        
        if(!isAgendaAdmin(info) && null != info 
        		&& !CollectionUtils.isEmpty(info.getAssignments())) {
        	councilPreamble.setDepartment(info.getAssignments().get(0).getDepartment());
        	Map<String, String> deptMap = masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_GENERIC);
        	councilPreamble.setDepartmentName(deptMap.get(councilPreamble.getDepartment()));
        }
        model.addAttribute("autoPreambleNoGenEnabled", isAutoPreambleNoGenEnabled());     
        model.addAttribute(COUNCIL_PREAMBLE, councilPreamble);
        if(isAgendaAdmin(info)) {
        	model.addAttribute(ADDITIONALRULE, COUNCIL_ABA_WORKFLOW);
        }else {
        	model.addAttribute(ADDITIONALRULE, COUNCIL_COMMON_WORKFLOW);
        }
        model.addAttribute(CURRENT_STATE, "NEW");
        
        prepareWorkFlowOnLoad(model, councilPreamble);
        return COUNCILPREAMBLE_NEW;
    }

    private void prepareWorkFlowOnLoad(final Model model,
                                       CouncilPreamble councilPreamble) {
        WorkflowContainer workFlowContainer = new WorkflowContainer();
        prepareWorkflow(model, councilPreamble, workFlowContainer, true);
        model.addAttribute("stateType", councilPreamble.getClass()
                .getSimpleName());
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final CouncilPreamble councilPreamble,
                         final BindingResult errors,
                         @RequestParam final MultipartFile attachments, final Model model,
                         final HttpServletRequest request,
                         final RedirectAttributes redirectAttrs,
                         @RequestParam String workFlowAction) {
    	
    	String editor="";
        if (request.getParameter(RICH_TEXT_EDITOR) != null)
        	editor = request.getParameter(RICH_TEXT_EDITOR);
    	//LOGGER.info("editor:"+request.getParameter("editor"));
        councilPreamble.setGistOfPreamble(editor);
    	
        validatePreamble(councilPreamble, errors);
        EmployeeInfo info = getEmployee();
        if (errors.hasErrors()) {
             model.addAttribute(IS_AGENDA_ADMIN, isAgendaAdmin(info));
             
             if(!isAgendaAdmin(info) && null != info 
             		&& !CollectionUtils.isEmpty(info.getAssignments())) {
             	councilPreamble.setDepartment(info.getAssignments().get(0).getDepartment());
             	Map<String, String> deptMap = masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_GENERIC);
             	councilPreamble.setDepartmentName(deptMap.get(councilPreamble.getDepartment()));
             }
             model.addAttribute("autoPreambleNoGenEnabled", isAutoPreambleNoGenEnabled());   
             councilPreamble.setApprovalDepartment("");
             councilPreamble.setApprovalDesignation("");
             councilPreamble.setApprovalPosition(0l);
             model.addAttribute(COUNCIL_PREAMBLE, councilPreamble);
             if(isAgendaAdmin(info)) {
             	model.addAttribute(ADDITIONALRULE, COUNCIL_ABA_WORKFLOW);
             }else {
            	 model.addAttribute(ADDITIONALRULE, COUNCIL_COMMON_WORKFLOW);
             }
             model.addAttribute(CURRENT_STATE, "NEW");
            prepareWorkFlowOnLoad(model, councilPreamble);
            return COUNCILPREAMBLE_NEW;
        }
        
        if (attachments != null && attachments.getSize() > 0) {
            try {
                councilPreamble.setFilestoreid(fileStoreService.store(
                        attachments.getInputStream(),
                        attachments.getOriginalFilename(),
                        attachments.getContentType(),
                        CouncilConstants.MODULE_NAME));
            } catch (IOException e) {
                LOGGER.error("Error in loading documents" + e.getMessage(), e);
            }
        }
        if (isAutoPreambleNoGenEnabled()){
	        PreambleNumberGenerator preamblenumbergenerator = autonumberServiceBeanResolver
	                .getAutoNumberServiceFor(PreambleNumberGenerator.class);
	        councilPreamble.setPreambleNumber(preamblenumbergenerator
	                .getNextNumber(councilPreamble));
        }
        
        if(isAgendaAdmin(info)) {
        	councilPreamble.setStatus(egwStatusHibernateDAO
                    .getStatusByModuleAndCode(CouncilConstants.PREAMBLE_MODULENAME,
                            CouncilConstants.PREAMBLE_STATUS_ABA_APPROVED));
         }else {
        	 councilPreamble.setStatus(egwStatusHibernateDAO
                     .getStatusByModuleAndCode(CouncilConstants.PREAMBLE_MODULENAME,
                             CouncilConstants.PREAMBLE_STATUS_CREATED));
         }
        
        councilPreamble.setType(PreambleType.GENERAL);

        Long approvalPosition = 0l;
        String approvalComment = "";
        String approverName = "";
        String nextDesignation = "";
        
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

        councilPreambleService.create(councilPreamble, approvalPosition,
                approvalComment, workFlowAction);
        
        //Create agenda
        CouncilAgenda councilAgenda = new CouncilAgenda();
        buildCouncilAgendaDetails(councilAgenda, councilPreamble);
        councilAgendaService.create(councilAgenda);

        String message = messageSource.getMessage("msg.councilPreamble.create",
                new String[]{
                        approverName.concat("~").concat(nextDesignation),
                        councilPreamble.getPreambleNumber()},
                null);
        redirectAttrs.addFlashAttribute(MESSAGE2, message);
        return REDIRECT_COUNCILPREAMBLE_RESULT + councilPreamble.getId();
    }
    
    private void buildCouncilAgendaDetails(CouncilAgenda councilAgenda, CouncilPreamble councilPreamble) {
    	councilAgenda.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(
				AGENDA_MODULENAME, AGENDA_STATUS_INWORKFLOW));
    	councilAgenda.setCommitteeType(councilPreamble.getCommitteeType());
    	councilAgenda.setCouncilAgendaType(councilPreamble.getCouncilAgendaType());
    	councilAgenda.setAgendaNumber(councilPreamble.getPreambleNumber());
    	Long itemNumber = Long.valueOf(1);
    	List<CouncilAgendaDetails> councilAgendaDetailsList = new ArrayList<CouncilAgendaDetails>();
    	CouncilAgendaDetails councilAgendaDetails = new CouncilAgendaDetails();
    	
    	councilAgendaDetails.setPreamble(councilPreamble);
        councilAgendaDetails.setAgenda(councilAgenda);
        councilAgendaDetails.setItemNumber(itemNumber.toString());
        councilAgendaDetails.setOrder(itemNumber);
        councilAgendaDetailsList.add(councilAgendaDetails);
        councilAgenda.setAgendaDetails(councilAgendaDetailsList);
    }

    @RequestMapping(value = "/downloadfile/{fileStoreId}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> download(@PathVariable final String fileStoreId) {
        return fileStoreUtils.fileAsResponseEntity(fileStoreId,
                CouncilConstants.MODULE_NAME, false);
    }

    @RequestMapping(value = "/result/{id}", method = RequestMethod.GET)
    public String result(@PathVariable("id") final Long id, Model model) {
        CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
        updateDepartment(councilPreamble);
        try {
        	CouncilAgenda agenda = councilAgendaService.findByPreambleId(councilPreamble.getId()).getAgenda();
        	if(null != agenda) {
	        	councilPreamble.setCommitteeType(agenda.getCommitteeType());
	        	councilPreamble.setCouncilAgendaType(agenda.getCouncilAgendaType());
        	}
        }catch(Exception e) {
        	Log.error("No agenda found with preambleid "+councilPreamble.getId());
        }
        
        model.addAttribute(COUNCIL_PREAMBLE, councilPreamble);
        model.addAttribute(APPLICATION_HISTORY,
                councilThirdPartyService.getHistory(councilPreamble));
        prepareWorkFlowOnLoad(model, councilPreamble);
        return COUNCILPREAMBLE_RESULT;
    }

    public void validatePreamble(final CouncilPreamble councilPreamble, final BindingResult errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "department", "notempty.preamble.department");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gistOfPreamble", "notempty.preamble.gistOfPreamble");
        if (councilPreamble.getAttachments().getSize() == 0 && councilPreamble.getFilestoreid() == null)
            errors.rejectValue("attachments", "notempty.preamble.attachments");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final CouncilPreamble councilPreamble,
                         final Model model, @RequestParam final MultipartFile attachments,
                         final BindingResult errors, final HttpServletRequest request,
                         final RedirectAttributes redirectAttrs,
                         @RequestParam String workFlowAction) {
        validatePreamble(councilPreamble, errors);
        if (errors.hasErrors()) {
            prepareWorkFlowOnLoad(model, councilPreamble);
            model.addAttribute(CURRENT_STATE, councilPreamble
                    .getCurrentState().getValue());
            return COUNCILPREAMBLE_EDIT;
        }
        /*List<Boundary> wardIdsList = new ArrayList<>();

        String selectedWardIds=request.getParameter("wardsHiddenIds");

        if (StringUtils.isNotEmpty(selectedWardIds)) {
            String[] wardIds = selectedWardIds.split(",");
            
            for (String wrdId : wardIds) {
                if (StringUtils.isNotEmpty(wrdId))
                    wardIdsList.add(boundaryService.getBoundaryById(Long.valueOf(wrdId)));
            }
        }
        councilPreamble.setWards(wardIdsList);*/

        if (attachments != null && attachments.getSize() > 0) {
            try {
                councilPreamble.setFilestoreid(fileStoreService.store(
                        attachments.getInputStream(),
                        attachments.getOriginalFilename(),
                        attachments.getContentType(),
                        CouncilConstants.MODULE_NAME));
            } catch (IOException e) {
                LOGGER.error(
                        "Error in loading agenda document" + e.getMessage(), e);
            }
        }
        
        Long approvalPosition = 0l;
        String approvalComment = StringUtils.EMPTY;
        String message = StringUtils.EMPTY;
        String nextDesignation = "";
        String approverName = "";

        if (request.getParameter(APPROVAL_COMENT) != null)
            approvalComment = request.getParameter(APPROVAL_COMENT);
        if (request.getParameter(WORK_FLOW_ACTION) != null)
            workFlowAction = request.getParameter(WORK_FLOW_ACTION);
        if (request.getParameter(APPROVAL_POSITION) != null
                && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request
                    .getParameter(APPROVAL_POSITION));
        if (request.getParameter("approverName") != null)
            approverName = request.getParameter("approverName");
        if( request.getParameter("nextDesignation") == null)
            nextDesignation=StringUtils.EMPTY;
        else
            nextDesignation = request.getParameter("nextDesignation");

        councilPreambleService.update(councilPreamble, approvalPosition,
                approvalComment, workFlowAction);
        
        //We have merged preamble with agenda & created agenda functionality. 
        //So when agenda gets approved then we need to update the status in preamble & agenda table.
        if (CouncilConstants.WF_APPROVE_BUTTON
                .equalsIgnoreCase(workFlowAction)) {
        	List<CouncilAgenda> councilAgendaList = councilAgendaService.findByAgendaNo(councilPreamble.getPreambleNumber());
        	if(!CollectionUtils.isEmpty(councilAgendaList)) {
        		CouncilAgenda councilAgenda = councilAgendaList.get(0);
        		councilAgenda.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(
        										AGENDA_MODULENAME, AGENDA_STATUS_APPROVED));
        		((CouncilAgendaDetails)councilAgenda.getAgendaDetails().get(0)).getPreamble().setStatus(
	                    egwStatusHibernateDAO.getStatusByModuleAndCode(
	                            PREAMBLE_MODULENAME, PREAMBLEUSEDINAGENDA));
        		councilAgendaService.update(councilAgenda);
        	}
        }
        
        if (null != workFlowAction) {
            if (CouncilConstants.WF_STATE_REJECT
                    .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.councilPreamble.reject",nextDesignation,approverName,
                        councilPreamble);
            } else if (CouncilConstants.WF_APPROVE_BUTTON
                    .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.councilPreamble.success",nextDesignation,approverName,
                        councilPreamble);
            } else if (CouncilConstants.WF_FORWARD_BUTTON
                    .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.councilPreamble.forward",nextDesignation,approverName,
                        councilPreamble);
            } else if (CouncilConstants.WF_PROVIDE_INFO_BUTTON
                    .equalsIgnoreCase(workFlowAction)) {
                message = getMessage("msg.councilPreamble.moreInfo",nextDesignation,approverName,
                        councilPreamble);
            }
            redirectAttrs.addFlashAttribute(MESSAGE2, message);
        }
        return REDIRECT_COUNCILPREAMBLE_RESULT + councilPreamble.getId();
    }

    @RequestMapping(value = "/updateimplimentaionstatus/{id}", method = RequestMethod.GET)
    public String updateStatus(@PathVariable("id") final Long id, final Model model,
                               final HttpServletResponse response) {
        CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
        if (null != councilPreamble.getImplementationStatus()
                && IMPLEMENTATION_STATUS_FINISHED.equals(councilPreamble.getImplementationStatus().getCode())) {
            model.addAttribute(MESSAGE2, "msg.councilPreamble.alreadyfinished");
            return COMMONERRORPAGE;
        }
        model.addAttribute(COUNCIL_PREAMBLE, councilPreamble);
        model.addAttribute(APPLICATION_HISTORY,
                councilThirdPartyService.getHistory(councilPreamble));
        return COUNCILPREAMBLE_UPDATE_STATUS;
    }

    @RequestMapping(value = "/updateimplimentaionstatus", method = RequestMethod.POST)
    public String updateImplementationStatus(
            @Valid @ModelAttribute final CouncilPreamble councilPreamble,
            final Model model, final BindingResult errors,
            final HttpServletRequest request,
            final RedirectAttributes redirectAttrs) {

        if (councilPreamble.getImplementationStatus().getCode() != null) {
            councilPreambleService.updateImplementationStatus(councilPreamble);
        }
        redirectAttrs.addFlashAttribute(MESSAGE2, messageSource.getMessage("msg.councilPreamble.update", null, null));
        return REDIRECT_COUNCILPREAMBLE_RESULT + councilPreamble.getId();
    }

    private String getMessage(String messageLabel,String designation,String approver,
                              final CouncilPreamble councilPreamble) {
        String message;
        message = messageSource.getMessage(messageLabel,
                new String[] { councilPreamble.getPreambleNumber() ,approver.concat("~").concat(designation) }, null);
        return message;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") final Long id, final Model model,
            final HttpServletResponse response) {
        CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
        WorkflowContainer workFlowContainer = new WorkflowContainer();
        
        EmployeeInfo info = getEmployee();
        
        //Setting pending action based on owner
		
        List<String> assignedDesignations = null;
        Long approvalPosition = councilPreamble.getState().getOwnerPosition();
        if (null != approvalPosition && approvalPosition != -1 && !approvalPosition.equals(Long.valueOf(0))) {
        	EmployeeInfo approverInfo = microserviceUtils.getEmployeeById(approvalPosition);
        	if(null != approverInfo && !CollectionUtils.isEmpty(approverInfo.getAssignments())) {
        		assignedDesignations = approverInfo.getAssignments().stream().map(Assignment::getDesignation).collect(Collectors.toList());
        	}
        }
        
		if (!CollectionUtils.isEmpty(assignedDesignations)
				&& assignedDesignations.contains(CouncilConstants.DESIGNATION_SECRETARY)
				&& CouncilConstants.SECRETARY_APPROVALPENDING.equalsIgnoreCase(councilPreamble.getState().getNextAction())) {
			workFlowContainer.setPendingActions(councilPreamble.getState().getNextAction()); 
		}else if (!CollectionUtils.isEmpty(assignedDesignations)
				&& assignedDesignations.contains(CouncilConstants.DESIGNATION_COMMISSIONER) 
				&& CouncilConstants.COMMISSIONER_APPROVALPENDING.equalsIgnoreCase(councilPreamble.getState().getNextAction())) {
			workFlowContainer.setPendingActions(councilPreamble.getState().getNextAction()); 
		}else if (!CollectionUtils.isEmpty(assignedDesignations)
				&& assignedDesignations.contains(CouncilConstants.DESIGNATION_MAYOR) 
				&& CouncilConstants.MAYOR_APPROVALPENDING.equalsIgnoreCase(councilPreamble.getState().getNextAction())) {
			workFlowContainer.setPendingActions(councilPreamble.getState().getNextAction()); 
		}
		 
        if(CouncilConstants.REJECTED.equalsIgnoreCase(councilPreamble.getStatus().getCode())){
        	if(isAgendaAdmin(info)) {
            	model.addAttribute(ADDITIONALRULE, COUNCIL_ABA_WORKFLOW);
            }else {
            	model.addAttribute(ADDITIONALRULE, COUNCIL_COMMON_WORKFLOW);
            }
        }
        
        try {
        	CouncilAgenda agenda = councilAgendaService.findByPreambleId(councilPreamble.getId()).getAgenda();
        	if(null != agenda) {
	        	councilPreamble.setCommitteeType(agenda.getCommitteeType());
	        	councilPreamble.setCouncilAgendaType(agenda.getCouncilAgendaType());
        	}
        }catch(Exception e) {
        	Log.error("No agenda found with preambleid "+councilPreamble.getId());
        }
        Map<String, String> deptMap = masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_GENERIC);
    	councilPreamble.setDepartmentName(deptMap.get(councilPreamble.getDepartment()));
        
        model.addAttribute("stateType", councilPreamble.getClass()
                .getSimpleName());
        model.addAttribute(CURRENT_STATE, councilPreamble.getCurrentState()
                .getValue());
        model.addAttribute(COUNCIL_PREAMBLE, councilPreamble);
        prepareWorkflow(model, councilPreamble, workFlowContainer, true);
        
        model.addAttribute(APPLICATION_HISTORY,
                councilThirdPartyService.getHistory(councilPreamble));
        model.addAttribute("wfNextAction", councilPreamble.getState().getNextAction());
        
        if ("PREAMBLEAPPROVEDFORMOM".equals(councilPreamble.getStatus().getCode())
                && !PreambleTypeEnum.WORKS.equals(councilPreamble.getTypeOfPreamble())) {
            return COUNCILPREAMBLE_VIEW;
        }
        if (PreambleTypeEnum.WORKS.equals(councilPreamble.getTypeOfPreamble())) {
            model.addAttribute("bidders", bidderService.getBidderDetails(councilPreamble.getId()));
            if ("PREAMBLEAPPROVEDFORMOM".equals(councilPreamble.getStatus().getCode()))
                return COUNCILPREAMBLE_API_VIEW;
            return COUNCILPREAMBLE_API_EDIT;
        }
        return COUNCILPREAMBLE_EDIT;
                 
    }
   

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, Model model) {
        CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
        updateDepartment(councilPreamble);
        try {
        	CouncilAgenda agenda = councilAgendaService.findByPreambleId(councilPreamble.getId()).getAgenda();
        	if(null != agenda) {
	        	councilPreamble.setCommitteeType(agenda.getCommitteeType());
	        	councilPreamble.setCouncilAgendaType(agenda.getCouncilAgendaType());
        	}
        }catch(Exception e) {
        	Log.error("No agenda found with preambleid "+councilPreamble.getId());
        }
        model.addAttribute(COUNCIL_PREAMBLE, councilPreamble);
        model.addAttribute(APPLICATION_HISTORY,
                councilThirdPartyService.getHistory(councilPreamble));
        if (PreambleTypeEnum.WORKS.equals(councilPreamble.getTypeOfPreamble())) {
            model.addAttribute("bidders", bidderService.getBidderDetails(councilPreamble.getId()));
            return COUNCILPREAMBLE_API_VIEW;
        } else
            return COUNCILPREAMBLE_VIEW;
    }

    @RequestMapping(value = "/search/{mode}", method = RequestMethod.POST)
    public String search(@PathVariable("mode") final String mode, Model model) {
        model.addAttribute(COUNCIL_PREAMBLE, new CouncilPreamble());
        return COUNCILPREAMBLE_SEARCH;

    }

    @RequestMapping(value = "/ajaxsearch/{mode}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String ajaxsearch(@PathVariable("mode") final String mode, Model model,
                             @ModelAttribute final CouncilPreamble councilPreamble) {
        List<CouncilPreamble> searchResultList;

        if ("edit".equalsIgnoreCase(mode)) {
            searchResultList = councilPreambleService
                    .searchFinalizedPreamble(councilPreamble);
        } else {
            searchResultList = councilPreambleService.search(councilPreamble);
        }
		
        List<CouncilPreamble> finalResultList = new ArrayList<CouncilPreamble>(searchResultList);
        updateDepartment(finalResultList);
        
        return new StringBuilder("{\"data\":")
                .append(toJSON(finalResultList, CouncilPreamble.class,
                        CouncilPreambleJsonAdaptor.class))
                .append("}").toString();
    }
    
    @RequestMapping(value = "/agendainvitationnew", method = RequestMethod.POST)
    public String agendaInvitationNewForm(final Model model) {
        CouncilAgendaInvitation councilAgendaInvitation = new CouncilAgendaInvitation();
        model.addAttribute(COUNCIL_AGENDA_INVITATION, councilAgendaInvitation);
        return COUNCIL_AGENDA_INVITATION_NEW;
    }
    
    @RequestMapping(value = "/sendsmsemailforagendainvitation", method = RequestMethod.POST)
    public String sendSmsAndEmailDetailsForAgendaInvitation(@Valid @ModelAttribute final CouncilAgendaInvitation councilAgendaInvitation, 
    		final BindingResult errors,@RequestParam final MultipartFile attachments,
    		final Model model, final RedirectAttributes redirectAttrs) {
        
    	validateCouncilAgendaInvitation(councilAgendaInvitation, errors);
        if (errors.hasErrors()) {
            model.addAttribute(COUNCIL_AGENDA_INVITATION, councilAgendaInvitation);
            return COUNCIL_AGENDA_INVITATION_NEW;
        }
        
        if (attachments != null && attachments.getSize() > 0) {
            try {
            	councilAgendaInvitation.setFilestoreid(fileStoreService.store(
                        attachments.getInputStream(),
                        attachments.getOriginalFilename(),
                        attachments.getContentType(),
                        CouncilConstants.MODULE_NAME));
            } catch (IOException e) {
                LOGGER.error("Error in upload documents" + e.getMessage(), e);
            }
        }
    	
        councilAgendaInvitationService.create(councilAgendaInvitation);
        
        try {
        	councilSmsAndEmailService.sendSmsForAgendaInvitation(councilAgendaInvitation.getMessage());
        }catch(Exception e) {
        	LOGGER.error("Unable to send SMS to for agenda invitation "+councilAgendaInvitation.getMeetingNumber());
        }
            
        try {
	        //Sent mail with of uploaded agenda invitation document
	        if(null != councilAgendaInvitation.getFilestoreid()) {
	        	Path file = fileStoreService.fetchAsPath(councilAgendaInvitation.getFilestoreid().getFileStoreId(), CouncilConstants.MODULE_NAME);
	    		try {
					byte[] data = Files.readAllBytes(file);
					String fileType = isBlank(councilAgendaInvitation.getFilestoreid().getContentType()) ? Files.probeContentType(file)
	                        : councilAgendaInvitation.getFilestoreid().getContentType();
	        		String fileName = councilAgendaInvitation.getFilestoreid().getFileName();
	        		councilSmsAndEmailService.sendEmailForAgendaInvitation(councilAgendaInvitation.getMessage(), data,fileType, fileName);
				} catch (IOException e) {
					LOGGER.error("Error in sending email for agenda invitation",e);
				}
	        }
        }catch(Exception e) {
        	LOGGER.error("Unable to send EMAIL of agenda invitation "+councilAgendaInvitation.getMeetingNumber());
        }
        
        redirectAttrs.addFlashAttribute("message", messageSource.getMessage("msg.councilagenda.invitation.success", null, null));
        return REDIRECT_COUNCIL_AGENDA_INVITATION_RESULT + councilAgendaInvitation.getId();
    }
    
    @RequestMapping(value = "/agendainvitation/result/{id}", method = RequestMethod.GET)
    public String agendaInvitationResult(@PathVariable("id") final Long id, Model model) {
    	CouncilAgendaInvitation councilAgendaInvitation = councilAgendaInvitationService.findOne(id);
        
    	model.addAttribute(COUNCIL_AGENDA_INVITATION, councilAgendaInvitation);
        return COUNCIL_AGENDA_INVITATION_RESULT;
    }
    
    private void validateCouncilAgendaInvitation(final CouncilAgendaInvitation councilAgendaInvitation, BindingResult errors) {
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingNumber", "notempty.meeting.meetingNumber");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingDate", "notempty.meeting.meetingDate");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingTime", "notempty.meeting.meetingTime");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingLocation", "notempty.meeting.committeeType");
        if (councilAgendaInvitation.getAttachments().getSize() == 0 && councilAgendaInvitation.getFilestoreid() == null)
            errors.rejectValue("attachments", "notempty.preamble.attachments");
    }
    
    public Boolean isAutoPreambleNoGenEnabled() {
        return councilPreambleService.autoGenerationModeEnabled(
                MODULE_FULLNAME, PREAMBLE_NUMBER_AUTO);
    }
    
    private void updateDepartment(CouncilPreamble councilPreamble) {
    	Map<String, String> deptMap = masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_GENERIC);
    	if(deptMap.containsKey(councilPreamble.getDepartment())) {
    		councilPreamble.setDepartmentName(deptMap.get(councilPreamble.getDepartment()));
		}else {
			councilPreamble.setDepartmentName(councilPreamble.getDepartment());
		}
    	if(CouncilConstants.PREAMBLEUSEDINAGENDA.equalsIgnoreCase(councilPreamble.getStatus().getCode())) {
    		List<CouncilAgenda> councilAgendaList = councilAgendaService.findByAgendaNo(councilPreamble.getPreambleNumber());
        	if(!CollectionUtils.isEmpty(councilAgendaList)) {
        		councilPreamble.setDisplayStatus(councilAgendaList.get(0).getStatus());
        	}
    	}
    }
    
    private void updateDepartment(List<CouncilPreamble> finalResultList) {
    	Map<String, String> deptMap = masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_GENERIC);
    	finalResultList.stream().forEach(council->{
	    	if(deptMap.containsKey(council.getDepartment())) {
	    		council.setDepartment(deptMap.get(council.getDepartment()));
			}
	    	if(CouncilConstants.PREAMBLEUSEDINAGENDA.equalsIgnoreCase(council.getStatus().getCode())) {
	    		List<CouncilAgenda> councilAgendaList = councilAgendaService.findByAgendaNo(council.getPreambleNumber());
	        	if(!CollectionUtils.isEmpty(councilAgendaList)) {
	        		council.setStatus(councilAgendaList.get(0).getStatus());
	        	}
	    	}
    	});
    }
    
    private boolean isAgendaAdmin(EmployeeInfo info){
    	boolean isAgendaAdmin= false;
    	if(null != info) {
			Optional<Role> adminRole = info.getUser().getRoles().stream()
                    .filter(role -> CouncilConstants.ROLE_AGENDA_BRANCH_ADMIN.equals(role.getCode()))
                    .findFirst();

			if(adminRole.isPresent()) {
				isAgendaAdmin = true;
			}
		}
    	return isAgendaAdmin;
    }
    
    private EmployeeInfo getEmployee(){
    	final User user = securityUtils.getCurrentUser();
    	if(null != user) {
    		EmployeeInfo info = microserviceUtils.getEmployeeById(user.getId());
	    	return info;
    	}
    	return null;
    }

}
