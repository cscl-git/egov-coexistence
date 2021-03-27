package org.egov.works.web.controller.searchestimate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.egov.egf.masters.services.ContractorService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.masters.Contractor;
import org.egov.works.boq.entity.BoQDetails;
import org.egov.works.boq.entity.PaymentDistribution;
import org.egov.works.boq.entity.WorkOrderAgreement;
import org.egov.works.boq.service.BoQDetailsService;
import org.egov.works.estimatepreparationapproval.entity.EstimatePreparationApproval;
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
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	 
	@RequestMapping(value = "/estimateApprovalSearch", method = RequestMethod.POST)
	public String showNewFormGet(
			@ModelAttribute("estimateSearchDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, HttpServletRequest request) {

		estimatePreparationApproval.setDepartments(getDepartmentsFromMs());

		return "estimate-detail-search-form";
	}

	@RequestMapping(value = "/workOrderSearch", method = RequestMethod.POST)
	public String progressUpdate(@ModelAttribute("workOrderAgreement") WorkOrderAgreement workOrderAgreement,
			final Model model, HttpServletRequest request) {
		workOrderAgreement.setDepartments(getDepartmentsFromMs());
		workOrderAgreement.setContractors(getAllActiveContractors());
		return "search-mis-work-agreement-page-form";
	}

	public List<Contractor> getAllActiveContractors() {
		List<Contractor> contractors = contractorService.getAllActiveContractors();
		return contractors;
	}

	//Search Estimate
	@RequestMapping(value = "/estimateSearch", params = "estimateSearch", method = RequestMethod.POST)
	public String searchEstimateData(
			@ModelAttribute("estimateSearchDetails") final EstimatePreparationApproval estimatePreparationApproval,
			final Model model, final HttpServletRequest request) throws Exception {

		List<EstimatePreparationApproval> approvalList = new ArrayList<EstimatePreparationApproval>();

		if (estimatePreparationApproval.getDepartment() != null && estimatePreparationApproval.getDepartment() != ""
				&& !estimatePreparationApproval.getDepartment().isEmpty()) {
			long department = Long.parseLong(estimatePreparationApproval.getDepartment());
			estimatePreparationApproval.setExecutingDivision(department);
		}

		List<EstimatePreparationApproval> workEstimateDetails = searchEstimateService.searchEstimateData(request,
				estimatePreparationApproval);

		approvalList.addAll(workEstimateDetails);
		estimatePreparationApproval.setEstimateList(approvalList);

		model.addAttribute("estimateSearchDetails", estimatePreparationApproval);

		return "estimate-detail-search-form";

	}
	
	//Search Work agreement
	@RequestMapping(value = "/workorderSearch1", method = RequestMethod.POST)
	public String searchclosurePage(
			@ModelAttribute("workOrderAgreement") final WorkOrderAgreement workOrderAgreement, final Model model,
			final HttpServletRequest request) throws Exception {
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

		return "search-mis-work-agreement-page-form";

	}

	private String populateCompletion(Long id) {
		
		WorkOrderAgreement workOrderAgreement = boQDetailsService.viewWorkData(id);
		DecimalFormat df = new DecimalFormat("0.00");
		Double sumAmount=0.0;
		Double measuredSumAmount=0.0;
		Double completion=0.0;
		Double payment=0.0;
		Double progress=0.0;
		Double percent=100.0;
		
		if(workOrderAgreement.getMilestonestatus() != null && workOrderAgreement.getMilestonestatus().equalsIgnoreCase("YES"))
		{
			if(workOrderAgreement.getPaymentDistribution() != null && !workOrderAgreement.getPaymentDistribution().isEmpty())
			{
				for(PaymentDistribution row :workOrderAgreement.getPaymentDistribution())
				{
					payment=Double.valueOf(row.getPayment_percent());
					progress=Double.valueOf(row.getCompletion_percent());
					measuredSumAmount=(payment/percent)*progress;
					completion=completion+measuredSumAmount;
				}
			}
		}
		else
		{
			for (BoQDetails boq : workOrderAgreement.getNewBoQDetailsList()) {
				sumAmount=sumAmount +boq.getAmount();
				if (boq.getMeasured_amount() != null) {
					measuredSumAmount=measuredSumAmount+boq.getMeasured_amount();
				}
				
			}
			 completion=(measuredSumAmount/sumAmount)*100;
		}
		
		
		return df.format(completion);
	}

	public List<Department> getDepartmentsFromMs() {
		List<Department> departments = microserviceUtils.getDepartments();
		return departments;
	}
	
	private  String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append(" and wo.work_start_date>='").append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and wo.work_start_date<='").append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
			
			if (null != billDateFrom)
				numDateQuery.append(" and wo.work_intended_date>='").append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and wo.work_intended_date<='").append(DDMMYYYYFORMAT1.format(billDateTo))
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
			
		}
		return misQuery.toString();

	}
}
