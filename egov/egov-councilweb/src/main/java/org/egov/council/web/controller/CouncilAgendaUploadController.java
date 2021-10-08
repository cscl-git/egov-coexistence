package org.egov.council.web.controller;

import static org.egov.infra.utils.JsonUtils.toJSON;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.egov.council.entity.CommitteeType;
import org.egov.council.entity.CouncilAgendaUpload;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.repository.CouncilAgendaUploadRepository;
import org.egov.council.service.CommitteeTypeService;
import org.egov.council.service.CouncilAgendaUploadService;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.Role;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationConstant;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.egov.infra.utils.JsonUtils.toJSON;

@Controller
@RequestMapping("/agendaUpload")
public class CouncilAgendaUploadController extends GenericWorkFlowController {

	private static final String COUNCILAGENDA_UPLOAD = "councilagenda-upload";
	private static final String COUNCIL_AGENDA_UPLOAD = "councilAgendaUpload";
	private static final String IS_AGENDA_ADMIN = "isAgendaAdmin";
	private static final String CURRENT_STATE = "currentState";
	private static final String COUNCILAGENDAUPLOAD_SEARCH = "councilagendaUpload-search";
	private static final Logger LOGGER = Logger.getLogger(CouncilAgendaUploadController.class);
	@Autowired
	protected EgovMasterDataCaching masterDataCache;

	@ModelAttribute("departments")
	public List<Department> getDepartmentList() {
		return masterDataCache.get(ApplicationConstant.DEPARTMENT_CACHE_NAME, ApplicationConstant.MODULE_AGENDA);
	}

	@ModelAttribute("committeeType")
	public List<CommitteeType> getCommitteTypeList() {
		return committeeTypeService.getActiveCommiteeType();
	}

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private MicroserviceUtils microserviceUtils;
	@Autowired
	protected FileStoreUtils fileStoreUtils;
	@Autowired
	CouncilAgendaUploadService councilAgendaUploadService;
	@Qualifier("fileStoreService")
	@Autowired
	protected FileStoreService fileStoreService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	protected CommitteeTypeService committeeTypeService;
//	@Autowired
//	CouncilAgendaUploadRepository councilAgendaUploadRepository;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String agendaUpload(final Model model) {
		CouncilAgendaUpload councilAgendaUpload = new CouncilAgendaUpload();
		EmployeeInfo info = getEmployee();
		model.addAttribute(IS_AGENDA_ADMIN, isAgendaAdmin(info));

//		if (!isAgendaAdmin(info) && null != info && !CollectionUtils.isEmpty(info.getAssignments())) {
//			councilAgendaUpload.setDepartment(info.getAssignments().get(0).getDepartment());
//			Map<String, String> deptMap = masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME,
//					ApplicationConstant.MODULE_GENERIC);
////			councilAgendaUpload.setDepartmentName(deptMap.get(councilAgendaUpload.getDepartment()));
//		}
		model.addAttribute(CURRENT_STATE, "NEW");
		model.addAttribute(COUNCIL_AGENDA_UPLOAD, councilAgendaUpload);
		
		System.out.println("kundan here ");
		return COUNCILAGENDA_UPLOAD;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String saveAgendaUpload(@ModelAttribute final CouncilAgendaUpload councilAgendaUpload,
			final BindingResult errors, @RequestParam final MultipartFile attachments, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectAttrs) {

		
		System.out.println("kundan department is ;-------"+request.getParameter("name"));
		 EmployeeInfo info = getEmployee(); 
//		 if (errors.hasErrors()) {
//		  model.addAttribute(IS_AGENDA_ADMIN, isAgendaAdmin(info));
//		  
//		  if (!isAgendaAdmin(info) && null != info &&
//		  !CollectionUtils.isEmpty(info.getAssignments())) {
//		  councilAgendaUpload.setDepartment(info.getAssignments().get(0).getDepartment(
//		  )); Map<String, String> deptMap = masterDataCache.getDepartmentMapMS(
//		  ApplicationConstant.DEPARTMENT_CACHE_NAME,
//		  ApplicationConstant.MODULE_GENERIC);
//		  councilAgendaUpload.setDepartmentName(deptMap.get(councilAgendaUpload.
//		  getDepartment())); } model.addAttribute(COUNCIL_AGENDA_UPLOAD,
//		  councilAgendaUpload); model.addAttribute(CURRENT_STATE, "NEW");
//		  
//		   }
		 
		if (attachments != null && attachments.getSize() > 0) {
			try {
				councilAgendaUpload.setFilestoreid(fileStoreService.store(attachments.getInputStream(),
						attachments.getOriginalFilename(), attachments.getContentType(), CouncilConstants.MODULE_NAME));
			} catch (IOException e) {
				LOGGER.error("Error in loading documents" + e.getMessage(), e);
			}
		}
		
		  model.addAttribute(IS_AGENDA_ADMIN, isAgendaAdmin(info));
		  
//		  if (!isAgendaAdmin(info) && null != info &&
//		  !CollectionUtils.isEmpty(info.getAssignments())) {
//		  councilAgendaUpload.setDepartment(info.getAssignments().get(0).getDepartment(
//		  )); 
		  Map<String, String> deptMap =masterDataCache.getDepartmentMapMS(ApplicationConstant.DEPARTMENT_CACHE_NAME,
		  ApplicationConstant.MODULE_GENERIC);
		  councilAgendaUpload.setDepartmentName(deptMap.get(councilAgendaUpload.getDepartment())); 
		  
//		  }
//		  
//		  SimpleDateFormat formaDate = new SimpleDateFormat("dd/MM/yyyy");
		  Date currentDate = new Date();
          councilAgendaUpload.setCreatedDate(currentDate);
		 
		model.addAttribute(COUNCIL_AGENDA_UPLOAD, councilAgendaUpload);
//		model.addAttribute(CURRENT_STATE, "NEW");
		councilAgendaUploadService.saveCouncilAgendaUpload(councilAgendaUpload);
		councilAgendaUpload.setDepartment("");
		 
		model.addAttribute(COUNCIL_AGENDA_UPLOAD, councilAgendaUpload);
		System.out.println("submit pages...........");
		model.addAttribute("message", "Agenda Uploaded Successfully..");
//		redirectAttrs.addFlashAttribute("message", messageSource.getMessage("msg.agenda.upload.success", null, null));
		return COUNCILAGENDA_UPLOAD;

	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(Model model, CouncilAgendaUpload councilAgendaUpload) {
		model.addAttribute(COUNCIL_AGENDA_UPLOAD, councilAgendaUpload);
		System.out.println("search executed ..................");
		return COUNCILAGENDAUPLOAD_SEARCH;

	}

	@RequestMapping(value = "/searchagendaupload", method = RequestMethod.POST)
	public String searchAgendaUplaod(Model model,ModelMap modelmap, CouncilAgendaUpload councilAgendaUpload ,HttpServletRequest request) {
		String fromDate =request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		try {
			Date fromDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
			Date toDate1 = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);
			System.out.println("Dtae from date:-----"+fromDate1);
			System.out.println("Date toDate:---"+toDate1);
			
//			Flux<UserDto> userDto = RegistryDBService.getDbWebClient(dbServiceUrl).get()
//		            .uri("/user/getAllInactiveUsers?date={dateTime}", dateTime).retrieve()
//		            .bodyToFlux(User.class).map(UserDto::of);
			
//			List<CouncilAgendaUpload> cn=councilAgendaUploadService.findRecordsSearch(fromDate1,toDate1);
//			modelmap.put("agendaUploadR", cn);
			
		}catch(Exception e)
		{
			e.getMessage();
		}
		
		modelmap.put("agendaUploadR", councilAgendaUploadService.findRecords()); 
//		modelmap.put("agendaUploadR", councilAgendaUploadRepository.getAllsearch(fromDate1,toDate1));
		System.out.println("search records using click button ---");
		model.addAttribute(COUNCIL_AGENDA_UPLOAD, councilAgendaUpload);
		return COUNCILAGENDAUPLOAD_SEARCH;
	}


	 
	@RequestMapping(value = "/downloadfile/{fileStoreId}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> download(@PathVariable final String fileStoreId) {
		return fileStoreUtils.fileAsResponseEntity(fileStoreId, CouncilConstants.MODULE_NAME, false);
	}

	private boolean isAgendaAdmin(EmployeeInfo info) {
		boolean isAgendaAdmin = false;
		if (null != info) {
			Optional<Role> adminRole = info.getUser().getRoles().stream()
					.filter(role -> CouncilConstants.ROLE_AGENDA_BRANCH_ADMIN.equals(role.getCode())).findFirst();

			if (adminRole.isPresent()) {
				isAgendaAdmin = true;
			}
		}
		return isAgendaAdmin;
	}

	private EmployeeInfo getEmployee() {
		final User user = securityUtils.getCurrentUser();
		if (null != user) {
			EmployeeInfo info = microserviceUtils.getEmployeeById(user.getId());
			return info;
		}
		return null;
	}

}
