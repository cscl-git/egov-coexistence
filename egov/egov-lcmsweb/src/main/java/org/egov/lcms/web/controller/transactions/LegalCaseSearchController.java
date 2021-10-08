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
import org.egov.lcms.transactions.entity.ReportStatus;
import org.egov.lcms.transactions.service.SearchLegalCaseService;
import org.egov.lcms.utils.LegalCaseUtil;
import org.egov.lcms.web.adaptor.LegalCaseSearchJsonAdaptor;
import org.jfree.layout.LCBLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.lcms.masters.entity.JudgmentType;
import org.egov.lcms.masters.service.JudgmentTypeService;
@Controller
@RequestMapping(value = "/search")
public class LegalCaseSearchController extends GenericLegalCaseController {

    @Autowired
    private SearchLegalCaseService searchLegalCaseService;

    @Autowired
    JudgmentTypeService JudgmentTypeService;
    
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

    @RequestMapping(method = RequestMethod.POST, value = "/searchForm")
    public String searchForm(final Model model) {
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
    
    
    @RequestMapping(value = "/legalsearchResultExcel")
    @ResponseBody
    public void getLegalCaseSearchResultExcel(final Model model,
            @ModelAttribute final LegalCaseSearchResult legalCaseSearchResult, final HttpServletRequest request,HttpServletResponse response) {
        final List<LegalCaseSearchResult> legalcaseSearchList = searchLegalCaseService.getLegalCaseReport(legalCaseSearchResult);
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
			 
			 byte[] fileContent =searchLegalCaseService.getSearchLegalCaseExcelSheet(headerData,legalcaseSearchList);
			 response.setContentType("application/ms-excel");
			  response.setContentLength(fileContent.length); 
			  response.setHeader("Expires:","0"); 
			  response.setHeader("Content-Disposition","attachment; filename=Legal Case Search.xls");
			  try { 
				  OutputStream outStream = response.getOutputStream();
				  outStream.write(fileContent); outStream.flush();
			  }catch(Exception ex) {
				  ex.printStackTrace();
				  
			  }
			  
        }
			
        
    }
    //controller for Pdf download not working currently
    /*@RequestMapping(value = "/legalsearchResultpdf")
    @ResponseBody
    public void getLegalCaseSearchResultpdf(final Model model,
            @ModelAttribute final LegalCaseSearchResult legalCaseSearchResult, final HttpServletRequest request,HttpServletResponse response) {
        final List<LegalCaseSearchResult> legalcaseSearchList = searchLegalCaseService.getLegalCaseReport(legalCaseSearchResult);
        System.out.println("Pdf Controller..");
        System.out.println("Size of list   "+legalcaseSearchList.size());
        final Map<String, Object> reportParams = new HashMap<String, Object>();
        List<LegalCasePdfbean> pdfbewn= new ArrayList<LegalCasePdfbean>();
        for(LegalCaseSearchResult list:legalcaseSearchList) {
        	LegalCasePdfbean lcb=new LegalCasePdfbean();
        	lcb.setCaseNumber(list.getLegalCase().getLcNumber());
        	lcb.setCaseTitle(list.getLegalCase().getCaseTitle());
        	lcb.setConcernedBranch(list.getConcernedBranch());
        	lcb.setCourtName(list.getCourtName());
        	lcb.setDefendingCounsel(list.getStandingCouncil());
        	lcb.setFileNumber(list.getLegalCase().getCaseNumber());
        	lcb.setHearingOutcome(list.getHearingOutcome());
        	lcb.setNextHearingDate(list.getHearingDate().toString());
        	lcb.setRespondents(list.getLegalCase().getRespondantNames());
        	lcb.setPetitioners(list.getLegalCase().getPetitionersNames());
        	pdfbewn.add(lcb);
        	
        }
        JRBeanCollectionDataSource items = new JRBeanCollectionDataSource(pdfbewn);
        reportParams.put("legalCaseSearchDataSource", items);
        reportParams.put("fileNumber", pdfbewn.get(0).getFileNumber());
       reportParams.put("caseNumber", pdfbewn.get(0).getCaseNumber());
        reportParams.put("caseTitle", pdfbewn.get(0).getCaseTitle());
        reportParams.put("courtName", pdfbewn.get(0).getCourtName());
        reportParams.put("defendingCounsel", pdfbewn.get(0).getDefendingCounsel());
        reportParams.put("petitioners", pdfbewn.get(0).getPetitioners());
        reportParams.put("respondents", pdfbewn.get(0).getRespondents());
        
        reportParams.put("concernedBranch", pdfbewn.get(0).getConcernedBranch());
        reportParams.put("nextHearingDate", pdfbewn.get(0).getNextHearingDate());
        reportParams.put("hearingOutcome", pdfbewn.get(0).getHearingOutcome());
       final ReportRequest reportInput = new ReportRequest("legalCaseSearch",pdfbewn,reportParams);
        reportInput.setReportFormat(ReportFormat.PDF);
        contentType = ReportViewerUtil.getContentType(ReportFormat.PDF);
        fileName = "LegalCaseSearch." + ReportFormat.PDF.toString().toLowerCase();
        final ReportOutput reportOutput = reportService.createReport(reportInput);
        if (reportOutput != null && reportOutput.getReportOutputData() != null) {
            inputStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
          
            byte[] fileContent=reportOutput.getReportOutputData();
            response.setContentType("application/pdf");
			  response.setContentLength(fileContent.length); 
			  response.setHeader("Expires:","0"); 
			  response.setHeader("Content-Disposition","attachment; filename=Legal Case Search.pdf");
			  try { 
				  OutputStream outStream = response.getOutputStream();
				  outStream.write(fileContent); outStream.flush();
			  }catch(Exception ex) {
				  ex.printStackTrace();
				  
			  }
        }

       
    	
        
    }*/

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
    
}
