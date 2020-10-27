package org.egov.works.bgsecurity.service;

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
import org.egov.works.bgsecurity.entity.BGSecurityDetails;
import org.egov.works.bgsecurity.repository.BGSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BGSecurityDetailsService {

	@Autowired
	private BGSecurityDetailsRepository bgSecurityDetailsRepository;
	@Autowired
	private DocumentUploadRepository documentUploadRepository;
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
    private FileStoreService fileStoreService;

	@Transactional
	public BGSecurityDetails saveSecurityDetailsData(HttpServletRequest request, BGSecurityDetails bgSecurityDetails) {
		// TODO Auto-generated method stub
		BGSecurityDetails savedBGSecurityDetails = bgSecurityDetailsRepository.save(bgSecurityDetails);
		List<DocumentUpload> files = bgSecurityDetails.getDocumentDetail() == null ? null
				: bgSecurityDetails.getDocumentDetail();
		final List<DocumentUpload> documentDetails;
		
		documentDetails = getDocumentDetails(files, bgSecurityDetails,
				"Works_BG");
		if (!documentDetails.isEmpty()) {
			savedBGSecurityDetails.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}
		BGSecurityDetails savedBGSecurityDetails1 = bgSecurityDetailsRepository.save(savedBGSecurityDetails);
		return savedBGSecurityDetails1;

	}

	@Transactional
	public BGSecurityDetails getBGSecurityDetails(Long id) {
		// TODO Auto-generated method stub
		BGSecurityDetails bgSecurityDetails = bgSecurityDetailsRepository.findById(id);
		return bgSecurityDetails;
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
                    doc.getContentType(), "Works_BG"));
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
