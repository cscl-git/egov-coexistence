package org.egov.works.tender.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.bills.DocumentUpload;
import org.egov.works.tender.entity.Tender;
import org.egov.works.tender.repository.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TenderService {

	@Autowired
	private TenderRepository tenderRepository;
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
    private FileStoreService fileStoreService;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;

	@Transactional
	public Tender saveTenderDetailsData(HttpServletRequest request, Tender tender) {
		
		Tender savedTender = tenderRepository.save(tender);
		List<DocumentUpload> files = tender.getDocumentDetail() == null ? null
				: tender.getDocumentDetail();
		final List<DocumentUpload> documentDetails;
		
		documentDetails = getDocumentDetails(files, tender,
				"Works_Tender");
		if (!documentDetails.isEmpty()) {
			savedTender.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		
		Tender savedTender1=tenderRepository.save(savedTender);
		return savedTender1;

	}

	@Transactional
	public Tender searchTenderData(Long id) {
		// TODO Auto-generated method stub

		Tender detailsList = tenderRepository.findById(id);

		return detailsList;

	}

	@Transactional
	public List<Tender> searchTenderData(HttpServletRequest request, Tender tender) {
		// TODO Auto-generated method stub
		List<Tender> tenderDetails = new ArrayList<Tender>();
		/*if (tender.getLoaNumber() != null) {
			tenderDetails = tenderRepository.findByLoaNumber(tender.getLoaNumber());
		} else {
			tenderDetails = tenderRepository.findByAllParams(tender.getLoaNumber(), tender.getFromDt(),
					tender.getToDt());
		}*/

		return tenderDetails;
	}
	
	public List<DocumentUpload> getDocumentDetails(final List<DocumentUpload> files, final Object object,
            final String objectType) {
        final List<DocumentUpload> documentDetailsList = new ArrayList<>();

        Long id;
        Method method;
        try {
            method = object.getClass().getMethod("getId", null);
            id = (Long) method.invoke(object, null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new ApplicationRuntimeException("error.expense.bill.document.error", e);
        }

        for (DocumentUpload doc : files) {
            final DocumentUpload documentDetails = new DocumentUpload();
            documentDetails.setObjectId(id);
            documentDetails.setObjectType(objectType);
            documentDetails.setFileStore(fileStoreService.store(doc.getInputStream(), doc.getFileName(),
                    doc.getContentType(), "Works_Tender"));
            documentDetailsList.add(documentDetails);

        }
        return documentDetailsList;
    }
	
	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}
	
	public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
		return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	}

}
