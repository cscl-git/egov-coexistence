package org.egov.lcms.web.controller.transactions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.egov.lcms.masters.entity.vo.AttachedDocument;
import org.egov.lcms.masters.service.DocumentTypeMasterService;
import org.egov.lcms.transactions.entity.Library;
import org.egov.lcms.transactions.service.LibraryService;
import org.egov.lcms.utils.constants.LcmsConstants;
import org.egov.lcms.web.adaptor.CourtMasterJsonAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("/library")
public class LibraryController {
	private static final String LIBRARY_NEW = "library-new";
    private static final String LIBRARY_RESULT = "library-result";
    private static final String LIBRARY_EDIT = "library-edit";
    private static final String LIBRARY_VIEW = "library-view";
    private static final String LIBRARY_SEARCH = "library-search";
	
	@Autowired
	private DocumentTypeMasterService documentTypeMasterService;
	
	@Autowired
	private LibraryService libraryService;
	
	private void prepareNewForm(final Model model) {
        model.addAttribute("documentTypeMasters", documentTypeMasterService.getActiveDocumentTypeList());
    }
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newForm(final Model model) {
        prepareNewForm(model);
        model.addAttribute(LcmsConstants.LIBRARY, new Library());
        model.addAttribute(LcmsConstants.MODE, "new");
        return LIBRARY_NEW;
    }
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final Library library, final BindingResult errors,
        final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException {
		AttachedDocument attachedDocument = null;
		String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");        
	    if (errors.hasErrors()) {
	        prepareNewForm(model);
	        model.addAttribute(LcmsConstants.MODE, "new");
	        return LIBRARY_NEW;
	    }
	    if(uploadedFiles!=null) {
            for (int i = 0; i < uploadedFiles.length; i++) {
                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(path);
                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);  
                attachedDocument = new AttachedDocument();
                attachedDocument.setFileStream(bios);
                attachedDocument.setFileName(fileName[i]);
                attachedDocument.setMimeType(contentType[i]);
            }
        }
	    libraryService.persist(library, attachedDocument);
	    redirectAttrs.addFlashAttribute(LcmsConstants.LIBRARY, library);
        model.addAttribute("message", "Document added to library successfully.");
        model.addAttribute(LcmsConstants.MODE, "view");
        return LIBRARY_RESULT;
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") final Long id, final Model model) {
        final Library library = libraryService.findOne(id);
        prepareNewForm(model);
        model.addAttribute(LcmsConstants.LIBRARY, library);
        model.addAttribute(LcmsConstants.MODE, "edit");
        return LIBRARY_EDIT;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final Library library, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) throws IOException {
    	AttachedDocument attachedDocument = null;
		String[] contentType = ((MultiPartRequestWrapper) request).getContentTypes("file");
        UploadedFile[] uploadedFiles = ((MultiPartRequestWrapper) request).getFiles("file");
        String[] fileName = ((MultiPartRequestWrapper) request).getFileNames("file");
        if (errors.hasErrors()) {
            prepareNewForm(model);
            model.addAttribute(LcmsConstants.MODE, "edit");
            return LIBRARY_EDIT;
        }
        if(uploadedFiles!=null) {
            for (int i = 0; i < uploadedFiles.length; i++) {
                Path path = Paths.get(uploadedFiles[i].getAbsolutePath());
                byte[] fileBytes = Files.readAllBytes(path);
                ByteArrayInputStream bios = new ByteArrayInputStream(fileBytes);  
                attachedDocument = new AttachedDocument();
                attachedDocument.setFileStream(bios);
                attachedDocument.setFileName(fileName[i]);
                attachedDocument.setMimeType(contentType[i]);
            }
        }
        libraryService.persist(library, attachedDocument);
        redirectAttrs.addFlashAttribute(LcmsConstants.LIBRARY, library);
        model.addAttribute("message", "Document updated to library successfully.");
        model.addAttribute(LcmsConstants.MODE, "view");
        return LIBRARY_RESULT;
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, final Model model) {
        final Library library = libraryService.findOne(id);
        prepareNewForm(model);
        model.addAttribute(LcmsConstants.LIBRARY, library);
        return LIBRARY_VIEW;
    }

    @RequestMapping(value = "/search/{mode}", method = RequestMethod.POST)
    public String search(@PathVariable("mode") final String mode, final Model model) {
        final Library library = new Library();
        prepareNewForm(model);
        model.addAttribute(LcmsConstants.LIBRARY, library);
        return LIBRARY_SEARCH;

    }

    @RequestMapping(value = "/ajaxsearch/{mode}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String ajaxsearch(@PathVariable("mode") final String mode, final Model model,
            @ModelAttribute final Library library) {
        final List<Library> searchResultList = libraryService.search(library);
        return new StringBuilder("{ \"data\":").append(toSearchResultJson(searchResultList)).append("}")
                .toString();
    }

    public Object toSearchResultJson(final Object object) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.registerTypeAdapter(Library.class, new CourtMasterJsonAdaptor()).create();
        return gson.toJson(object);
    }
}
