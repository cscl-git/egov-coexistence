package org.egov.works.web.controller.searchestimate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.egov.egf.masters.services.ContractorService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.masters.Contractor;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.entity.DNITCreation;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
import org.egov.works.estimatepreparationapproval.entity.Subdivisionworks;
import org.egov.works.estimatepreparationapproval.entity.Workswing;
import org.egov.works.estimatepreparationapproval.service.EstimatePreparationApprovalService;
import org.egov.works.estimatesearch.service.SearchEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/searchEstimate")
public class SearchEstimateController {

	@Autowired
	public MicroserviceUtils microserviceUtils;
	
	  @Autowired 
	  SearchEstimateService searchEstimateService;
	  
	  @Autowired
		BoQDetailsService boQDetailsService;
	  
	@Autowired
	private ContractorService contractorService;
	@Autowired
	EstimatePreparationApprovalService estimatePreparationApprovalService;
	
	

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	 
    public static final SimpleDateFormat DDMMYYYYFORMAT2 = new SimpleDateFormat("yyyy-MMM-dd", LOCALE);

	@RequestMapping(value = "/estimateApprovalSearch", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		
		estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		//estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		return "estimate-detail-search-form";
	}

	@RequestMapping(value = "/workOrderSearch", method = RequestMethod.POST)
	public String progressUpdate(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		//workOrderAgreement.setContractors(getAllActiveContractors());
		return "search-mis-work-agreement-page-form-new";
	}
	@RequestMapping(value = "/workorderSearch1", method = RequestMethod.POST)
	public String searchWorkOrderAgreement(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		List<Department> dep = getDepartmentsFromMs();
		Map<String, String> depname=new HashMap<>();
		Map<String, String> works=new HashMap<>();
		Map<String, String> subd=new HashMap<>();
		for(Department dd:dep) {

			depname.put(dd.getCode(), dd.getName());
		}
		List<Workswing> workswing = searchEstimateService.getworskwing();
		for(Workswing we:workswing)
		{
			works.put(we.getId().toString(), we.getWorkswingname());
			}
		List<Subdivisionworks> subdivision = searchEstimateService.getSubdivision();
		for(Subdivisionworks sd:subdivision) {
			subd.put(sd.getId().toString(), sd.getSubdivision());
		}
		WorkOrderAgreement agreement = null;
		final StringBuffer query = new StringBuffer(500);
		List<Object[]> list = null;
		query.append(
				"select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description,wo.executing_department,wo.worksWing,wo.subdivision,wo.milestonestatus from WorkOrderAgreement wo  ");
		query.append(" where wo.id> ? ");
		 if(workOrderAgreement.getDepartment()!=null)
	        	{
	        	query.append("and wo.executing_department ='").append(workOrderAgreement.getDepartment()).append("'");
	        	}
		 query.append(getDateQuery(workOrderAgreement));
		 query.append(getMisQuery(workOrderAgreement));
		System.out.println("Query :: " + query.toString());
		workOrderAgreement.setId(0l);
		list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getId());
System.out.println("size "+list.size());
		if (list.size() != 0) {
			System.out.println(" data present");
			for (final Object[] object : list) {
				agreement = new WorkOrderAgreement();
				agreement.setId(Long.parseLong(object[0].toString()));
				System.out.println(" percentage completion  "+populateCompletion(agreement.getId()));
				agreement.setPercentCompletion(populateCompletion(agreement.getId()));
				if (object[1] != null) {
					agreement.setName_work_order(object[1].toString());
				}
				if (object[3] != null) {
					agreement.setWork_number(object[3].toString());
				}
				if (object[2] != null) {
					String old = object[2].toString();
     				String[] split = old.split("/");
     				if(split.length>2) {
     				String dn=split[1];
					if(dn.equalsIgnoreCase("DNIT"))
					agreement.setWork_agreement_number(object[2].toString());
     				}
				}
				if (object[4] != null) {
					agreement.setStartDate(object[4].toString());
				}
				if (object[5] != null) {
					agreement.setEndDate(object[5].toString());
				}
				if (object[6] != null) {
					agreement.setWork_amount(object[6].toString());
				}
				if (object[7] != null) {
					agreement.setStatusDescp(object[7].toString());
				}
				if (object[8] != null) {
					agreement.setExecuting_department(depname.get(object[8].toString()));
				}
				if (object[9] != null) {
					agreement.setWorksWing(works.get(object[9].toString()));
				}
				if (object[10] != null) {
					agreement.setSubdiv(subd.get(object[10].toString()));
	}
				if (object[11] != null) {
					agreement.setMilestonestatus(object[11].toString());
				}
				if(workOrderAgreement.getCompletionpercentage()!=null ) {
					//System.out.println(":::: "+agreement.getPercentCompletion()+" ::: "+workOrderAgreement.getCompletionpercentage());
					Double cop = Double.valueOf(workOrderAgreement.getCompletionpercentage());
					Double per = Double.valueOf(agreement.getPercentCompletion());
					DecimalFormat df = new DecimalFormat("#.00");
					String cop1 = df.format(cop);
					String per1=df.format(per);

					System.out.println("after  "+cop1+" ? "+Double.valueOf(cop1)+"kkkkkkk per1 "+per1+" ?? "+Double.valueOf(per1));
					if(Double.valueOf(per1)>=Double.valueOf(cop1)) {
						workList.add(agreement);
					}
				}else {
				workList.add(agreement);
				}
			}
		}
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setWorkswings(estimatePreparationApprovalService.getworskwing());
		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

		return "search-mis-work-agreement-page-form-new";

	}
	/*// Search Work agreement
	@RequestMapping(value = "/workorderSearch1", method = RequestMethod.POST)
		public String searchclosurePage(@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement,
				final Model model, final HttpServletRequest request) throws Exception {
		List<WorkOrderAgreement> workList = new ArrayList<WorkOrderAgreement>();
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		WorkOrderAgreement agreement=null;
		workOrderAgreement.setContractors(getAllActiveContractors());
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		query.append(
				"select wo.id,wo.name_work_order,wo.work_number,wo.work_agreement_number,wo.work_start_date,wo.work_intended_date,wo.work_amount,wo.status.description,wo.milestonestatus from WorkOrderAgreement wo where wo.status.description ='Approved' and wo.executing_department = ? ");
		if (workOrderAgreement.getContractor_name() != null) {
			query.append(" and wo.contractor_name=? ");
			query.append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()));
			query.append(getMisQuery(workOrderAgreement));
			list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getDepartment(),
					workOrderAgreement.getContractor_name());
			
			}else
			{
		query.append(getDateQuery(workOrderAgreement.getFromDate(), workOrderAgreement.getToDate()));
		query.append(getMisQuery(workOrderAgreement));
		list = persistenceService.findAllBy(query.toString(), workOrderAgreement.getDepartment());
		
			}
		
        if (list.size() != 0) {
       	 for (final Object[] object : list) {
       		agreement=new WorkOrderAgreement();
       		agreement.setId(Long.parseLong(object[0].toString()));
       		agreement.setPercentCompletion(populateCompletion(agreement.getId()));
				if (object[1] != null) {
       			agreement.setName_work_order(object[1].toString());
       		}
				if (object[2] != null) {
       			agreement.setWork_number(object[2].toString());
       		}
				if (object[3] != null) {
       			agreement.setWork_agreement_number(object[3].toString());
       		}
				if (object[4] != null) {
       			agreement.setStartDate(object[4].toString());
       		}
				if (object[5] != null) {
       			agreement.setEndDate(object[5].toString());
       		}
				if (object[6] != null) {
       			agreement.setWork_amount(object[6].toString());
       		}
				if (object[7] != null) {
       			agreement.setStatusDescp(object[7].toString());
       		}
				if (object[8] != null) {
					agreement.setMilestonestatus(object[8].toString());
				}
       		workList.add(agreement);
       		
       	 }
        }
		
		workOrderAgreement.setWorkOrderList(workList);
		model.addAttribute("workOrderAgreement", workOrderAgreement);

			return "search-mis-work-agreement-page-form-new";

		}
*/
	@RequestMapping(value = "/searchDnitApproval", method = RequestMethod.POST)
	public String showEstimateNewFormGet(
			@ModelAttribute("workdnitDetails") final DNITCreation dnitCreation,
			final Model model, HttpServletRequest request) {
		
		dnitCreation.setWorkswings(estimatePreparationApprovalService.getworskwing());
		dnitCreation.setDepartments(getDepartmentsFromMs());
		model.addAttribute("workdnitDetails", dnitCreation);

		return "search-dnit-form-mis";
	}
	@RequestMapping(value = "/workDnitSearchnew", params = "workDnitSearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(
			@ModelAttribute("workdnitDetails") final DNITCreation estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {
		List<DNITCreation> approvalList = new ArrayList<DNITCreation>();
		List<Department> dep = getDepartmentsFromMs();
		Map<String, String> depname=new HashMap<>();
		Map<String, String> works=new HashMap<>();
		Map<String, String> subd=new HashMap<>();
		for(Department dd:dep) {
			
			depname.put(dd.getCode(), dd.getName());
		}
		List<Workswing> workswing = searchEstimateService.getworskwing();
		for(Workswing we:workswing)
		{
			works.put(we.getId().toString(), we.getWorkswingname());
			}
		List<Subdivisionworks> subdivision = searchEstimateService.getSubdivision();
		for(Subdivisionworks sd:subdivision) {
			subd.put(sd.getId().toString(), sd.getSubdivision());
		}
		// Convert input string into a date

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != "" && !estimatePreparationApproval.getDepartment().isEmpty()) {
		long department = Long.parseLong(estimatePreparationApproval.getDepartment());
		estimatePreparationApproval.setExecutingDivision(department);
		}
		DNITCreation estimate=null;
		
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query.append(
	                "select es.id,es.workName,es.worksWing,es.executingDivision,es.subdivision,es.status.description,es.expHead_est,es.estimateAmount,es.createdDate,es.lastModifiedDate from DNITCreation es  ");
	        query.append(" where es.id> ? ");
			 if(estimatePreparationApproval.getExecutingDivision()!=null)
		        	{
		        	query.append("and es.executingDivision ='").append(estimatePreparationApproval.getExecutingDivision()).append("'");
		        	}
			 query.append(getDateQuerydn(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()));
	        query.append(getMisQuerydn(estimatePreparationApproval));
	        estimatePreparationApproval.setId(0l);
		 System.out.println("Query :: "+query.toString());
         list = persistenceService.findAllBy(query.toString(),
        		 estimatePreparationApproval.getId());
		 
         if (list.size() != 0) {
        	 
        	 for (final Object[] object : list) {
        		 
        		  estimate = new DNITCreation();
        		 estimate.setId(Long.parseLong(object[0].toString()));
        		 if(object[1] != null)
        		 {
        			 estimate.setWorkName(object[1].toString());
        		 }
        		 if(object[2] != null)
        		 {
        			 estimate.setWorksWing(works.get(object[2].toString()));
        		 }if(object[3] != null)
        		 {
        			 estimate.setExecuteDiv(depname.get(object[3].toString()));
        		 }
        		 if(object[4] != null)
        		 {
        			 estimate.setSubdivis(subd.get(object[4].toString()));
        		 }
        		 if(object[5] != null)
        		 {
        			 estimate.setStatussearch(object[5].toString());
        			 if (object[5].toString().equalsIgnoreCase("Approved") && object[9].toString()!=null ) {
        				 String old = object[9].toString();
         				String[] split = old.split(" ");
         				String[] split2 = split[0].split("-");
         				 String  datef=split2[2]+"/"+split2[1]+"/"+split2[0];
         				 estimate.setApproveDt(datef);
					}
        		 }
        		 if(object[6] != null)
        		 {
        			 estimate.setExpHead_est(object[6].toString());
        		 }
        		 if(object[7] != null)
        		 {
        			  
        			 estimate.setEstimateAmount(new BigDecimal(Double.valueOf(object[7].toString())).setScale(2,BigDecimal.ROUND_HALF_UP));
        		 }
        		 if(object[8] != null)
        		 {
        			 String oldstring = object[8].toString();
        				String[] split = oldstring.split(" ");
        				String[] split2 = split[0].split("-");
        				 String  datef=split2[2]+"/"+split2[1]+"/"+split2[0];
        				
        			 estimate.setCreatedDt(datef);
        		 }
        		 
        		 approvalList.add(estimate);
        		 }
        	 
        	 }
         estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
        estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setEstimateList(approvalList);
		

		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "search-dnit-form-mis";

	}

		
	public List<Contractor> getAllActiveContractors() {
		List<Contractor> contractors = contractorService.getAllActiveContractors();
		return contractors;
	}

	// Search Estimate
	@RequestMapping(value = "/estimateSearch", params = "estimateSearch", method = RequestMethod.POST)
	public String searchEstimateData(
			@ModelAttribute("workEstimateDetails") final EstimatePreparationApproval estimatePreparationApproval, final Model model,
			final HttpServletRequest request) throws Exception {
List<Department> dep = getDepartmentsFromMs();
Map<String, String> depname=new HashMap<>();
Map<String, String> works=new HashMap<>();
Map<String, String> subd=new HashMap<>();
for(Department dd:dep) {
		
	depname.put(dd.getCode(), dd.getName());
}
List<Workswing> workswing = searchEstimateService.getworskwing();
for(Workswing we:workswing)
{
	works.put(we.getId().toString(), we.getWorkswingname());
	}
List<Subdivisionworks> subdivision = searchEstimateService.getSubdivision();
for(Subdivisionworks sd:subdivision) {
	subd.put(sd.getId().toString(), sd.getSubdivision());
}
		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			long department = Long.parseLong(estimatePreparationApproval.getDepartment());
			estimatePreparationApproval.setExecutingDivision(department);
		}
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select es.id,es.workName,es.worksWing,es.executingDivision,es.subdivision,es.status.description,es.expHead_est,es.estimateAmount,es.createdDate,es.roughapprovedate,es.adminapprovedate,es.approvedate from EstimatePreparationApproval es ");
		 query.append(" where es.id> ? ");
		 if(estimatePreparationApproval.getExecutingDivision()!=null)
	        	{
	        	query.append("and es.executingDivision ='").append(estimatePreparationApproval.getExecutingDivision()).append("'");
	        	}
	        query.append(getDateQueryEs(estimatePreparationApproval.getFromDt(), estimatePreparationApproval.getToDt()));
	        query.append(getMisQueryEs(estimatePreparationApproval));
	        
	        estimatePreparationApproval.setId(0l);
		 System.out.println("Query :: "+query.toString());
        list = persistenceService.findAllBy(query.toString(),estimatePreparationApproval.getId());
System.out.println("list size "+list.size());
if (list.size() != 0) {
	 
	 for (final Object[] object : list) {
		 
		 EstimatePreparationApproval estimate = new EstimatePreparationApproval();
		 estimate.setId(Long.parseLong(object[0].toString()));
		 if(object[1] != null)
		{
			 estimate.setWorkName(object[1].toString());
		 }
		 if(object[2] != null)
			{
			 estimate.setWorksWing(works.get(object[2].toString()));
		 }if(object[3] != null)
				{
			 estimate.setExecuteDiv(depname.get(object[3].toString()));
				}
		 if(object[4] != null)
		 {
			 estimate.setSubdivis(subd.get(object[4].toString()));
			}
		 if(object[5] != null)
		 {
			 estimate.setStatussearch(object[5].toString());
		 }
		 if(object[6] != null)
		 {
			 estimate.setExpHead_est(object[6].toString());
		 }
		 if(object[7] != null)
		 {
			  
			 estimate.setEstimateAmount(new BigDecimal(Double.valueOf(object[7].toString())).setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		 if(object[8] != null)
		{
			 String oldstring = object[8].toString();
				String[] split = oldstring.split(" ");
				String[] split2 = split[0].split("-");
				 String  datef=split2[2]+"/"+split2[1]+"/"+split2[0];
				
			 estimate.setCreatedDt(datef);
		 }
		 if(object[9] != null)
		 {
			 String oldstring = object[9].toString();
				String[] split = oldstring.split(" ");
				String[] split2 = split[0].split("-");
				 String  datef=split2[2]+"/"+split2[1]+"/"+split2[0];
				
			 estimate.setRoughapproveDt(datef);
		 }
		 if(object[10] != null)
		 {
			 String oldstring = object[10].toString();
				String[] split = oldstring.split(" ");
				String[] split2 = split[0].split("-");
				 String  datef=split2[2]+"/"+split2[1]+"/"+split2[0];
				
			 estimate.setAdminapproveDt(datef);
		 }
		 if(object[11] != null)
		 {
			 String oldstring = object[11].toString();
				String[] split = oldstring.split(" ");
				String[] split2 = split[0].split("-");
				 String  datef=split2[2]+"/"+split2[1]+"/"+split2[0];
				
			 estimate.setApproveDt(datef);
		 }
		 
		 approvalList.add(estimate);
		 }
	 
	 }

		//List<EstimatePreparationApproval> workEstimateDetails = searchEstimateService.searchEstimateData(request,estimatePreparationApproval);

		//approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);
		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());
		estimatePreparationApproval.setWorkswings(estimatePreparationApprovalService.getworskwing());
		model.addAttribute("workEstimateDetails", estimatePreparationApproval);

		return "estimate-detail-search-form";

	}

	
	private String populateCompletion(Long id) {

		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		DecimalFormat df = new DecimalFormat("0.00");
		Double sumAmount = 0.0;
		Double measuredSumAmount = 0.0;
		Double completion=0.0;
			for (BoQDetails boq : workOrderAgreement.getNewBoQDetailsList()) {
			if(boq.getAmount()!=null) {
				sumAmount=sumAmount +boq.getAmount();
			}
				if (boq.getMeasured_amount() != null) {
					measuredSumAmount=measuredSumAmount+boq.getMeasured_amount();
				}
				
			}
		if(sumAmount !=0.0 && measuredSumAmount !=0.0) {
			 completion=(measuredSumAmount/sumAmount)*100;
		}
		return df.format(completion);
	}

	public List<Department> getDepartmentsFromMs() {
		List<Department> departments = microserviceUtils.getDepartments();
		return departments;
	}
	
	private String getDateQuery(WorkOrderAgreement works) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != works.getFromDate())
				numDateQuery.append(" and wo.work_start_date>='").append(DDMMYYYYFORMAT1.format(works.getFromDate()))
						.append("'");
			if (null != works.getToDate())
				numDateQuery.append(" and wo.work_start_date<='").append(DDMMYYYYFORMAT1.format(works.getToDate()))
						.append("'");
			
			if (null != works.getFromDate())
				numDateQuery.append(" and wo.work_intended_date>='").append(DDMMYYYYFORMAT1.format(works.getFromDate()))
						.append("'");
			if (null != works.getToDate())
				numDateQuery.append(" and wo.work_intended_date<='").append(DDMMYYYYFORMAT1.format(works.getToDate()))
						.append("'");
			if (null != works.getCreatedfrom())
				numDateQuery.append(" and wo.createdDate>='").append(DDMMYYYYFORMAT1.format(works.getCreatedfrom()))
						.append("'");
			if (null != works.getCreatedto())
				numDateQuery.append(" and wo.createdDate<='").append(DDMMYYYYFORMAT1.format(works.getCreatedto()))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
	
	public String getMisQuery( WorkOrderAgreement agreement) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != agreement) {
			if (agreement.getName_work_order_search() != null && !agreement.getName_work_order_search().isEmpty()) {
				misQuery.append(" and wo.name_work_order  like '%").append(agreement.getName_work_order_search())
						.append("%'");
			}
			if (agreement.getWork_number_search() != null && !agreement.getWork_number_search().isEmpty()) {
				misQuery.append(" and wo.work_number='").append(agreement.getWork_number_search()).append("'");
			}
			if (agreement.getWork_agreement_number_search() != null
					&& !agreement.getWork_agreement_number_search().isEmpty()) {
				misQuery.append(" and wo.work_agreement_number='").append(agreement.getWork_agreement_number_search())
						.append("'");
			}
			if (agreement.getWorksWing() != null && !agreement.getWorksWing().isEmpty()) {
				misQuery.append(" and wo.worksWing='").append(agreement.getWorksWing()).append("'");
			}
			if (agreement.getSubdivision() != null) {
				misQuery.append(" and wo.subdivision='").append(agreement.getSubdivision()).append("'");
			}
			if ( agreement.getWorkStatusSearch() != null && !agreement.getWorkStatusSearch().isEmpty())
			{
				misQuery.append(" and wo.status.description='")
						.append(agreement.getWorkStatusSearch()).append("'");
			}
			if ( agreement.getMilestonestatus() != null && !agreement.getMilestonestatus().isEmpty())
			{
				misQuery.append(" and milestonestatus='")
						.append(agreement.getMilestonestatus()).append("'");
			}
			

		}
		return misQuery.toString();

	}

	private  String getDateQueryEs(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and es.estimateDate>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and es.estimateDate<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
	
	public String getMisQueryEs( EstimatePreparationApproval estimate) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != estimate) {
			
			if ( estimate.getWorkStatusSearch() != null && !estimate.getWorkStatusSearch().isEmpty())
			{
				misQuery.append(" and es.status.description='")
						.append(estimate.getWorkStatusSearch()).append("'");
			}
			if ( estimate.getWorkName() != null && !estimate.getWorkName().isEmpty())
			{
				misQuery.append(" and es.workName like '%")
						.append(estimate.getWorkName()).append("%'");
			}
			if ( estimate.getFundSource() != null && !estimate.getFundSource().isEmpty())
			{
				misQuery.append(" and es.fundSource='")
						.append(estimate.getFundSource()).append("'");
			}
			
			
			if ( estimate.getEstimateNumber() != null && !estimate.getEstimateNumber().isEmpty())
			{
				misQuery.append(" and es.estimateNumber='")
						.append(estimate.getEstimateNumber()).append("'");
			}
			if(estimate.getWorksWing() != null)
			{
				misQuery.append(" and es.worksWing='")
				.append(estimate.getWorksWing()).append("'");
			}
			if(estimate.getWorkLocation() != null && !estimate.getWorkLocation().isEmpty())
			{
				misQuery.append(" and es.workLocation='")
				.append(estimate.getWorkLocation()).append("'");
			}
			if(estimate.getSectorNumber() != null)
			{
				misQuery.append(" and es.sectorNumber='")
				.append(estimate.getSectorNumber()).append("'");
			}
			if(estimate.getWardNumber() != null)
			{
				misQuery.append(" and es.wardNumber='")
				.append(estimate.getWardNumber()).append("'");
			}
			if(estimate.getWorkCategory() != null)
			{
				misQuery.append(" and es.workCategory='")
				.append(estimate.getWorkCategory()).append("'");
			}
			
			if(estimate.getExpHead_est() != null)
			{
				misQuery.append(" and es.expHead_est='")
				.append(estimate.getExpHead_est()).append("'");
			}
			if(estimate.getExecutingDivision() != null)
			{
				misQuery.append(" and es.executingDivision='")
				.append(estimate.getExecutingDivision()).append("'");
			}
			
			
			if(estimate.getEstimateAmount() != null)
			{
				misQuery.append(" and es.estimateAmount=")
				.append(estimate.getEstimateAmount());
			}
			if ( estimate.getCreatedbyuser() != null && !estimate.getCreatedbyuser().isEmpty())
			{
				misQuery.append(" and lower(es.createdbyuser) like lower('%")
						.append(estimate.getCreatedbyuser()).append("%')");
			}
			if(estimate.getSubdivision() != null)
			{
				misQuery.append(" and es.subdivision=")
				.append(estimate.getSubdivision());
			}
			
			
		}
		return misQuery.toString();

	}
	private  String getDateQuerydn(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and es.estimateDate>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and es.estimateDate<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
	
	public String getMisQuerydn( DNITCreation estimate) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != estimate) {
			
			if ( estimate.getWorkStatusSearch() != null && !estimate.getWorkStatusSearch().isEmpty())
			{
				misQuery.append(" and es.status.description='")
						.append(estimate.getWorkStatusSearch()).append("'");
			}
			if ( estimate.getWorkName() != null && !estimate.getWorkName().isEmpty())
			{
				misQuery.append(" and es.workName like '%")
						.append(estimate.getWorkName()).append("%'");
			}
			if ( estimate.getFundSource() != null && !estimate.getFundSource().isEmpty())
			{
				misQuery.append(" and es.fundSource='")
						.append(estimate.getFundSource()).append("'");
			}
			
			
			if ( estimate.getEstimateNumber() != null && !estimate.getEstimateNumber().isEmpty())
			{
				misQuery.append(" and es.estimateNumber='")
						.append(estimate.getEstimateNumber()).append("'");
			}
			if(estimate.getWorksWing() != null)
			{
				misQuery.append(" and es.worksWing='")
				.append(estimate.getWorksWing()).append("'");
			}
			if(estimate.getWorkLocation() != null && !estimate.getWorkLocation().isEmpty())
			{
				misQuery.append(" and es.workLocation='")
				.append(estimate.getWorkLocation()).append("'");
			}
			if(estimate.getSectorNumber() != null)
			{
				misQuery.append(" and es.sectorNumber='")
				.append(estimate.getSectorNumber()).append("'");
			}
			if(estimate.getWardNumber() != null)
			{
				misQuery.append(" and es.wardNumber='")
				.append(estimate.getWardNumber()).append("'");
			}
			if(estimate.getWorkCategory() != null)
			{
				misQuery.append(" and es.workCategory=")
				.append(estimate.getWorkCategory());
			}
			if(estimate.getEstimateAmount() != null)
			{
				misQuery.append(" and es.estimateAmount=")
				.append(estimate.getEstimateAmount());
			}
			if(estimate.getSubdivision() != null)
			{
				misQuery.append(" and es.subdivision=")
				.append(estimate.getSubdivision());
			}
			if ( estimate.getCreatedbyuser() != null && !estimate.getCreatedbyuser().isEmpty())
			{
				misQuery.append(" and lower(es.createdbyuser) like lower('%")
						.append(estimate.getCreatedbyuser()).append("%')");
			}
			if(estimate.getExpHead_est() != null)
			{
				misQuery.append(" and es.expHead_est='")
				.append(estimate.getExpHead_est()).append("'");
			}
		}
		return misQuery.toString();

	}
	
}
