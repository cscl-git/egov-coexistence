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
package org.egov.lcms.web.controller.transactions;

import org.egov.commons.EgwStatus;
import org.egov.egf.model.BankAdviceReportInfo;
import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.reporting.viewer.ReportViewerUtil;
import org.egov.infra.utils.JsonUtils;
import org.egov.lcms.reports.entity.LegalCasePdfbean;
import org.egov.lcms.reports.entity.LegalCaseSearchResult;
import org.egov.lcms.reports.entity.LegalCaseSearchResultInfo;
import org.egov.lcms.transactions.entity.ReportStatus;
import org.egov.lcms.transactions.service.LegalCaseFileNoService;
import org.egov.lcms.transactions.service.SearchLegalCaseService;
import org.egov.lcms.utils.LegalCaseUtil;
import org.egov.lcms.web.adaptor.LegalCaseSearchJsonAdaptor;
import org.jfree.layout.LCBLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.egov.lcms.masters.entity.ConcernedBranchMaster;
import org.egov.lcms.masters.entity.JudgmentType;
import org.egov.lcms.masters.service.ConcernedBranchMasterService;
import org.egov.lcms.masters.service.JudgmentTypeService;

@Controller
@RequestMapping(value = "/search")
public class LegalCaseSearchController extends GenericLegalCaseController {

    @Autowired
    private SearchLegalCaseService searchLegalCaseService;
    
    @Autowired
	private LegalCaseFileNoService legalCaseileNoService;

    @Autowired
    JudgmentTypeService JudgmentTypeService;
    
    @Autowired
    private ConcernedBranchMasterService concernedBranchMasterService;
    
    @Autowired
    private LegalCaseUtil legalCaseUtil;
    private InputStream inputStream;
    private String contentType;
    private String fileName;
    private String textFileName;
    @Autowired
    private ReportService reportService;

    @ModelAttribute
    public LegalCaseSearchResult searchRequest() {
        return new LegalCaseSearchResult();
    }

    public @ModelAttribute("statusList") List<EgwStatus> getStatusList() {
        return legalCaseUtil.getStatusForModule();
    }
    
    public @ModelAttribute("judgementTypeList") List<JudgmentType> getJudgmentTypeist() {
        return JudgmentTypeService.findAll();
    }

    public @ModelAttribute("reportStatusList") List<ReportStatus> getReportStatusList() {
        return searchLegalCaseService.getReportStatus();
    }
    
    
    public @ModelAttribute("branchList") List<ConcernedBranchMaster> getConcernedBranchList() {
        return concernedBranchMasterService.getActiveConcernedBranchs();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/searchForm")
    public String searchForm(final Model model,@ModelAttribute final LegalCaseSearchResult legalCaseSearchResult,final HttpServletRequest request) {
    	
    	final List<String> legalcaseSearchList = searchLegalCaseService
                .getLegalCaseData(legalCaseSearchResult);
    	String fileno= "";
    	Date hearingdate=null;
    	String branch="";
    	String nexthearingdate="";
    	DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		/*
		 * for (Object[] object : legalcaseSearchList) { hearingdate=(Date)object[0];
		 * nexthearingdate=dateFormat.format(hearingdate); fileno = (String)object[1];
		 * branch=(String)object[2]; System.out.println("hearingdate:"+hearingdate);
		 * System.out.println("nexthearingdate:"+nexthearingdate);
		 * System.out.println("fileno:"+fileno); System.out.println("branch:"+branch);
		 * break; }
		 */
    	int size = legalcaseSearchList.size();
    	System.out.println("list size:"+size);	   	
    	HttpSession session=request.getSession();
    	
    	StringBuilder strbul=new StringBuilder();
    	for (String str : legalcaseSearchList) {
    		strbul.append(str);
    		strbul.append("<br />");
		}
    
    	
    	String legaldata=strbul.toString();
      	
    	if(size==0) {
            session.setAttribute("listofhearingdates", "No Hearing Dates for Next 7 Days");	
        	}else {
        		session.setAttribute("listofhearingdates", legaldata);	
        	}
        	
    	    	
        model.addAttribute("currDate", new Date());
        
        return "searchlegalcase-form";
    }

    @RequestMapping(value = "/legalsearchResult", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getLegalCaseSearchResult(final Model model,
            @ModelAttribute final LegalCaseSearchResult legalCaseSearchResult, final HttpServletRequest request) {
        final List<LegalCaseSearchResult> legalcaseSearchList = searchLegalCaseService
                .getLegalCaseReport(legalCaseSearchResult);
       
        return new StringBuilder("{ \"data\":").append(
                JsonUtils.toJSON(legalcaseSearchList, LegalCaseSearchResult.class, LegalCaseSearchJsonAdaptor.class))
                .append("}").toString();
    }
    
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTextFileName() {
		return textFileName;
	}

	public void setTextFileName(String textFileName) {
		this.textFileName = textFileName;
	}
    
    @RequestMapping(value = "/legalsearchResultExcel")
    @ResponseBody
    public void getLegalCaseSearchResultExcel(final Model model,
			@ModelAttribute final LegalCaseSearchResult legalCaseSearchResult, final HttpServletRequest request,
			HttpServletResponse response) {
		final List<LegalCaseSearchResult> legalcaseSearchList = searchLegalCaseService
				.getLegalCaseReport(legalCaseSearchResult);
        if(null!=legalcaseSearchList) {
        	
        	Map<String, String> headerData = new HashMap<>();
			 headerData.put("h1","File Number");
			 headerData.put("h2","Case Number");
			 headerData.put("h3","Case Title");
			 headerData.put("h4","Court Name");
			 headerData.put("h5","Defending Counsel");
			 headerData.put("h6","Case Status");
			 headerData.put("h7","Petitioners");
			 headerData.put("h8","Respondents");
			 headerData.put("h9","Concerned Branch");
			 headerData.put("h10","Next Hearing Date");
			 headerData.put("h11","Hearing Outcome");
			 headerData.put("h12","Petetion Type");
			 headerData.put("h13","Brief");
			 headerData.put("h14","Nodal Officer");
			 headerData.put("h15","Councel Engage");
			headerData.put("h16", "Reply Submit");
			headerData.put("h17", "Argument");
			 
			 byte[] fileContent =searchLegalCaseService.getSearchLegalCaseExcelSheet(headerData,legalcaseSearchList);
			 response.setContentType("application/ms-excel");
			  response.setContentLength(fileContent.length); 
			  response.setHeader("Expires:","0"); 
			  response.setHeader("Content-Disposition","attachment; filename=Legal Case Search.xls");
			  try { 
				  OutputStream outStream = response.getOutputStream();
				outStream.write(fileContent);
				outStream.flush();
			  }catch(Exception ex) {
				  ex.printStackTrace();
			  }
        }
    }
          
	@RequestMapping(value = "/legalcaseReportSearchPdf")
	public @ResponseBody void getLegalCaseSearchResultPdf(
			@ModelAttribute final LegalCaseSearchResult legalCaseSearchResult, final Model model,
			final HttpServletRequest request) throws IOException, JRException {
			  try { 
			final List<LegalCaseSearchResult> legalcaseSearchList = searchLegalCaseService
					.getLegalCaseReport(legalCaseSearchResult);
			List<LegalCaseSearchResultInfo> listInfo = new ArrayList<LegalCaseSearchResultInfo>();
			int count = 1;
			for (LegalCaseSearchResult s : legalcaseSearchList) {

				LegalCaseSearchResultInfo data = new LegalCaseSearchResultInfo();
				if (s.getLegalCase().getLcNumber() != null) {
					data.setLegalCaseNo(s.getLegalCase().getLcNumber());
				} else {
					data.setLegalCaseNo("");
				}
				if (s.getLegalCase().getCaseNumber() != null) {
					data.setCaseNumber(s.getLegalCase().getCaseNumber());
				} else {
					data.setCaseNumber("");
				}
				if (s.getLegalCase().getCaseTitle() != null) {
					data.setCaseTitle(s.getLegalCase().getCaseTitle());
				} else {
					data.setCaseTitle("");
				}

				if (s.getCourtName() != null) {
					data.setCourtName(s.getCourtName());
				} else {
					data.setCourtName("");
				}
				if (s.getLegalCase().getOppPartyAdvocate() != null) {
					data.setStandingCouncil(s.getLegalCase().getOppPartyAdvocate());
				} else {
					data.setStandingCouncil("");
				}
				if (s.getLegalCase().getStatus().getDescription() != null) {
					data.setStatusDesc(s.getLegalCase().getStatus().getDescription());
				} else {
					data.setStatusDesc("");
				}
				if (s.getLegalCase().getPetitionersNames() != null) {
					data.setPetitioners(s.getLegalCase().getPetitionersNames());
				} else {
					data.setPetitioners("");
				}

				if (s.getLegalCase().getRespondantNames() != null) {
					data.setRespondants(s.getLegalCase().getRespondantNames());
				} else {
					data.setRespondants("");
				}
				if (s.getConcernedBranch() != null) {
					data.setConcernedBranch(s.getConcernedBranch());
				} else {
					data.setConcernedBranch("");
				}

				if (s.getHearingDate() != null) {
					data.setHearingDate(s.getHearingDate().toString());
				} else {
					data.setHearingDate("");
				}
				if (s.getHearingOutcome() != null) {
					data.setHearingOutcome(s.getHearingOutcome());
				} else {
					data.setHearingOutcome("");
				}
				if (s.getLegalCase().getPetitionTypeMaster() != null) {
					data.setPetetionType(s.getLegalCase().getPetitionTypeMaster().getPetitionType());
				} else {
					data.setPetetionType("");
				}
				if (s.getLegalCase().getBrief() != null) {
					data.setBrief(s.getLegalCase().getBrief());
				} else {
					data.setBrief("");
				}
				if (s.getLegalCase().getCouncelengage() != null) {
					data.setCouncelEngage(s.getLegalCase().getCouncelengage());
				} else {
					data.setCouncelEngage("");
				}
				if (s.getLegalCase().getNodalOfficername() != null) {
					data.setNodalOfficer(s.getLegalCase().getNodalOfficername());
				} else {
					data.setNodalOfficer("");
				}

				if (s.getReplySubmit() != null) {
					data.setReplySubmit(s.getReplySubmit());
				} else {
					data.setReplySubmit("");
				}
				if (s.getArgument() != null) {
					data.setArgument(s.getArgument());
				} else {
					data.setArgument("");
				}
				listInfo.add(data);
			}

			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
			response.setHeader("Content-Disposition", "attachment; filename=LegalSearchCase.pdf");
			ServletOutputStream out = response.getOutputStream();
			JasperPrint pp = createPdfReport(listInfo);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(pp, baos);
			out.write(baos.toByteArray());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static JRBeanCollectionDataSource getDataSource(List<LegalCaseSearchResultInfo> listInfo) {
		return new JRBeanCollectionDataSource(listInfo);
	}

	private JasperPrint createPdfReport(final List<LegalCaseSearchResultInfo> listInfo)
			throws JRException, IOException {
		final InputStream stream = this.getClass().getResourceAsStream("/reports/templates/LegalCaseSearch.jrxml");
		final JasperReport report = JasperCompileManager.compileReport(stream);
		final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(listInfo);
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("legalCaseDataSource", getDataSource(listInfo));
		final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);
		return print;
	}
	
	@RequestMapping(value = "/legalcasecheckboxupdate",method = RequestMethod.POST)
	public @ResponseBody void getLegalCasecheckbox(@RequestBody Long[] mycheckboxes) throws IOException, JRException {
			  
		  try { 
				  for (Long data : mycheckboxes) {
					  
				 legalCaseileNoService.updateCheckboxes(data);					
				}
							 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/legalcaseuncheckboxupdate",method = RequestMethod.POST)
	public @ResponseBody String getLegalCaseunchecked(@RequestBody Long[] myuncheckedcheckboxes) throws IOException, JRException {
			  
		  try { 				
				  for (Long data : myuncheckedcheckboxes) {
				  				  
				  legalCaseileNoService.updateunCheckboxes(data);
				  }
				 
		} catch (Exception e) {
			e.printStackTrace();
		}
       return "Important Cases Saved Successfully";
	}
	
	
    @RequestMapping(value = "/legalcaseremarks", method = RequestMethod.POST)
    public @ResponseBody String legalCaseRemarksSubmit(@RequestBody String[] mytextboxes) {
		
    	
    	 try { 				
			  for (String data : mytextboxes) {
				  
				  String[] legalcaseremarks = data.split("-");
				  String id = legalcaseremarks[0];
				  String remarks=legalcaseremarks[1];
			  			  
			  legalCaseileNoService.updateLegalRemarks(id,remarks);
			  }
			 
	} catch (Exception e) {
		e.printStackTrace();
	}
    	 
  return "Remarks Updated Successfully";

    }
}
