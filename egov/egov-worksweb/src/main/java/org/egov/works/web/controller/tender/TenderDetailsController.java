package org.egov.works.web.controller.tender;

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
import org.egov.works.tender.entity.Tender;
import org.egov.works.tender.repository.TenderRepository;
import org.egov.works.tender.service.TenderService;
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
@RequestMapping(value = "/tenderProcurement")
public class TenderDetailsController {

	@Autowired
	TenderService tenderService;
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
    
    @Autowired
	private TenderRepository tenderRepository;
    

	@RequestMapping(value = "/newform", method = RequestMethod.POST)
	public String showNewFormGet(@ModelAttribute("tender") final Tender tender, final Model model,
			HttpServletRequest request) {

			
		return "tender-form";
	}

	@RequestMapping(value = "/tenderSave", params = "tenderSave", method = RequestMethod.POST)
	public String saveTenderDetailsData(@ModelAttribute("tender")  Tender tender, final Model model,
			@RequestParam("file1") MultipartFile[] files, final HttpServletRequest request,final RedirectAttributes redirectAttrs) throws Exception {
		
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
		tender.setDocumentDetail(list);
		 tenderService.saveTenderDetailsData(request, tender);
		 tender=new Tender();
		 model.addAttribute("tender", tender);
		 model.addAttribute("mode", "initial");
		 model.addAttribute("message", "Tender Details saved successfully");
		return "tender-form";

	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {

		
		Tender tender = tenderService.searchTenderData(id);
		final List<DocumentUpload> documents = documentUploadRepository.findByobjectTypeAndObjectId("Works_Tender",tender.getId());
		tender.setDocumentDetail(documents);
		model.addAttribute("mode","view");
		model.addAttribute("tender", tender);

		return "view-tender-form";
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String showEstimateNewFormGet(@ModelAttribute("tender") final Tender tender, final Model model,
			HttpServletRequest request) {

		return "search-tender-form";
	}

	@RequestMapping(value = "/tenderSearch", params = "tenderSearch", method = RequestMethod.POST)
	public String searchWorkEstimateData(@ModelAttribute("tender") final Tender tender, final Model model,
			final HttpServletRequest request) throws Exception {
		List<Tender> tenderList = new ArrayList<Tender>();
		Tender tenderDetails=null;
		final StringBuffer query = new StringBuffer(500);
		 List<Object[]> list =null;
		 query
	        .append(
	                "select td.id,td.procurementAmount,td.procurementDate,td.contractorDetails,td.loaNumber,td.tenderProNumber,td.project_name from Tender td where ")
	        .append(getDateQuery(tender.getFromDt(), tender.getToDt()))
	        .append(getMisQuery(tender));
		 System.out.println("Query :: "+query.toString());
        list = persistenceService.findAllBy(query.toString());
		
        if(list.size() != 0)
        {
        	for (final Object[] object : list) {
        		tenderDetails = new Tender();
        		tenderDetails.setId(Long.parseLong(object[0].toString()));
        		if(object[1] != null)
        		{
        			tenderDetails.setProcurementAmount(Double.parseDouble(object[1].toString()));
        		}
        		if(object[2] != null)
        		{
        			tenderDetails.setTenderDate(object[2].toString());
        		}
        		if(object[3] != null)
        		{
        			tenderDetails.setContractorDetails(object[3].toString());
        		}
        		if(object[4] != null)
        		{
        			tenderDetails.setLoaNumber(object[4].toString());
        		}
        		if(object[5] != null)
        		{
        			tenderDetails.setTenderProNumber(object[5].toString());
        		}
        		if(object[6] != null)
        		{
        			tenderDetails.setProject_name(object[6].toString());
        		}
        		tenderList.add(tenderDetails);
        	}
        }
		
		 tender.setTenderList(tenderList);

		model.addAttribute("tender", tender);

		return "search-tender-form";

	}
	
	private  String getDateQuery(final Date billDateFrom, final Date billDateTo) {
		final StringBuffer numDateQuery = new StringBuffer();
		try {

			if (null != billDateFrom)
				numDateQuery.append("  td.procurementDate>='")
						.append(DDMMYYYYFORMAT1.format(billDateFrom))
						.append("'");
			if (null != billDateTo)
				numDateQuery.append(" and td.procurementDate<='")
						.append(DDMMYYYYFORMAT1.format(billDateTo))
						.append("'");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return numDateQuery.toString();
	}
	
	public String getMisQuery( Tender tender) {

		final StringBuffer misQuery = new StringBuffer(300);
		if (null != tender) {
			if ( tender.getLoaNumber() != null && !tender.getLoaNumber().isEmpty())
			{
				misQuery.append(" and td.loaNumber='")
						.append(tender.getLoaNumber()).append("'");
			}
			if ( tender.getProject_name_search() != null && !tender.getProject_name_search().isEmpty())
			{
				misQuery.append(" and td.project_name like '%")
						.append(tender.getProject_name_search()).append("%'");
			}
			
			
		}
		return misQuery.toString();
	}
	
	@RequestMapping(value = "/downloadBillDoc", method = RequestMethod.GET)
	public void getBillDoc(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final ServletContext context = request.getServletContext();
		final String fileStoreId = request.getParameter("fileStoreId");
		String fileName = "";
		final File downloadFile = fileStoreService.fetch(fileStoreId, "Works_Tender");
		final FileInputStream inputStream = new FileInputStream(downloadFile);
		Tender tenderDetails = tenderRepository.findById(Long.parseLong(request.getParameter("tenderDetailsId")));
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
	
	private Tender getBillDocuments(final Tender estDetails) {
		List<org.egov.model.bills.DocumentUpload> documentDetailsList = tenderService.findByObjectIdAndObjectType(estDetails.getId(),
				"Works_Tender");
		estDetails.setDocumentDetail(documentDetailsList);
		return estDetails;
	}

}
