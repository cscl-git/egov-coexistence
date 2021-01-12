package org.egov.works.web.controller.bgsecurity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.works.bgsecurity.entity.BGSecurityDetails;
import org.egov.works.bgsecurity.repository.BGSecurityDetailsRepository;
import org.egov.works.bgsecurity.service.BGSecurityDetailsService;
import org.egov.works.tender.entity.Tender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/bgSecurity")
public class BGSecurityController {

	@Autowired
	BGSecurityDetailsService bgSecurityDetailsService;
	@Autowired
	private BGSecurityDetailsRepository bgSecurityDetailsRepository;
	public static final Locale LOCALE = new Locale("en", "IN");
    public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
    @Autowired
	private FileStoreService fileStoreService;
    
    @Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
    
    @Autowired
	private DocumentUploadRepository documentUploadRepository;
    private static final int BUFFER_SIZE = 4096;

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("bgSecurityDetails") final BGSecurityDetails bgSecurityDetails,
			final Model model, HttpServletRequest request) {

		return "bg-security-form";
	}

	@RequestMapping(value = "/bgSecuritySave", params = "bgSecuritySave", method = RequestMethod.POST)
	public String saveSecurityDetailsData(
			@ModelAttribute("bgSecurityDetails")  BGSecurityDetails bgSecurityDetails, final Model model,
			@RequestParam("file1") MultipartFile[] files,final HttpServletRequest request,final RedirectAttributes redirectAttrs) throws Exception {
		
		List<DocumentUpload> list = new ArrayList<>();
		if (files != null)
			for (int i = 0; i < files.length; i++) {
				DocumentUpload upload = new DocumentUpload();
				if(files[i] == null || files[i].getOriginalFilename().isEmpty())
				{
					continue;
				}
				upload.setInputStream(new ByteArrayInputStream(IOUtils.toByteArray(files[i].getInputStream())));
				upload.setFileName(files[i].getOriginalFilename());
				upload.setContentType(files[i].getContentType());
				list.add(upload);
			}
		bgSecurityDetails.setDocumentDetail(list);
		BGSecurityDetails savedBGSecurityDetails = bgSecurityDetailsService.saveSecurityDetailsData(request,
				bgSecurityDetails);
		bgSecurityDetails=new BGSecurityDetails();
		model.addAttribute("bgSecurityDetails", bgSecurityDetails);
		model.addAttribute("message", "BG/Security Details saved successfully");
		model.addAttribute("mode", "initial");
		return "bg-security-form";

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {
		// TODO Auto-generated method stub
		BGSecurityDetails bgSecurityDetails = bgSecurityDetailsService.getBGSecurityDetails(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_BG",bgSecurityDetails.getId());
		bgSecurityDetails.setDocumentDetail(documents);
		model.addAttribute("mode","view");
		model.addAttribute("bgSecurityDetails", bgSecurityDetails);
		return "view-bg-security-form";

	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String showEstimateNewFormGet(@ModelAttribute("bgSecurityDetails") final BGSecurityDetails bgSecurityDetails,
			final Model model, HttpServletRequest request) {

		return "search-bgsecurity-form";
	}

	@RequestMapping(value = "/bgsecuritySearch", params = "bgsecuritySearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(@ModelAttribute("bgSecurityDetails") final BGSecurityDetails bgSecurityDetails,
			final Model model, final HttpServletRequest request) throws Exception {
		List<BGSecurityDetails> securityList = new ArrayList<BGSecurityDetails>();
		BGSecurityDetails bg=null;
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select bg.id,bg.security_validity,bg.security_amount,bg.security_start_date,bg.security_end_date,bg.loaNumber,bg.security_number,bg.project_name,bg.security_tender_number,bg.narration  from BGSecurityDetails bg where ")
	        .append(getDateQuery(bgSecurityDetails.getFromDt(), bgSecurityDetails.getToDt()))
	        .append(getMisQuery(bgSecurityDetails));
		 System.out.println("Query :: "+query.toString());
        list = persistenceService.findAllBy(query.toString());
		
        if(list.size() != 0)
        {
        	for (final Object[] object : list) {
        		bg = new BGSecurityDetails();
        		bg.setId(Long.parseLong(object[0].toString()));
        		if(object[1] != null)
        		{
        			bg.setSecurity_validity(object[1].toString());
        		}
        		if(object[2] != null)
        		{
        			bg.setSecurity_amount(Double.parseDouble(object[2].toString()));
        		}
        		if(object[3] != null)
        		{
        			bg.setBgStartDate(object[3].toString());
        		}
        		if(object[4] != null)
        		{
        			bg.setBgEndDate(object[4].toString());
        		}
        		if(object[5] != null)
        		{
        			bg.setLoaNumber(object[5].toString());
        		}
        		if(object[6] != null)
        		{
        			bg.setSecurity_number(object[6].toString());
        		}
        		if(object[7] != null)
        		{
        			bg.setProject_name(object[7].toString());
        		}
        		if(object[8] != null)
        		{
        			bg.setSecurity_tender_number(object[8].toString());
        		}
        		if(object[9] != null)
        		{
        			bg.setNarration(object[9].toString());
        		}
        		securityList.add(bg);
        	}
        }
		
		bgSecurityDetails.setBgSecurityDetailsList(securityList);

		model.addAttribute("bgSecurityDetails", bgSecurityDetails);

		return "search-bgsecurity-form";

	}
	
	private  String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append("  bg.security_start_date>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and bg.security_start_date<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
			if (null != billDateFrom)
				numDateQuery.append(" and bg.security_end_date>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and bg.security_end_date<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
	
	public String getMisQuery( BGSecurityDetails bg) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != bg) {
			if ( bg.getLoaNumber() != null && !bg.getLoaNumber().isEmpty())
			{
				misQuery.append(" and bg.security_number='")
						.append(bg.getSecurity_number()).append("'");
			}
			
			
		}
		return misQuery.toString();
	}
	
	@RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "Works_BG");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		BGSecurityDetails tenderDetails = bgSecurityDetailsRepository.findById(Long.parseLong(request.getParameter("bgDetailsId")));
		tenderDetails = getBillDocuments(tenderDetails);
		System.out.println("xxx");
		
		for (final org.egov.model.bills.DocumentUpload doc : tenderDetails.getDocumentDetail())
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
	
	private BGSecurityDetails getBillDocuments(final BGSecurityDetails estDetails) {
		List<org.egov.model.bills.DocumentUpload> documentDetailsList = bgSecurityDetailsService.findByObjectIdAndObjectType(estDetails.getId(),
				"Works_BG");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}

}
