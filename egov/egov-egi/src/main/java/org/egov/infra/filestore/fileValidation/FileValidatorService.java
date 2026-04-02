package org.egov.infra.filestore.fileValidation;

import java.io.File;
import java.io.InputStream;

import org.apache.struts2.dispatcher.multipart.UploadedFile;

public interface FileValidatorService {

    void validateFile(UploadedFile file, String fileName, String contentType);

    void validateFile(File file, String fileName, String contentType);

    void validateFile(InputStream inputStream, long size, String fileName, String contentType);

    void validateFile(byte[] bytes, String fileName, String contentType);

}
