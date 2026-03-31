package org.egov.infra.filestore.fileValidation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileValidator implements FileValidatorService {

    private static final long DEFAULT_MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final List<String> allowedExtensions;
    private final long maxFileSize;

    public FileValidator(@Value("${filestore.allowed.extensions:pdf,jpg,jpeg,png}") final String allowedExts,
                         @Value("${filestore.max.size:5242880}") final long maxSize) {
        this.allowedExtensions = Arrays.asList(allowedExts.split(","));
        this.maxFileSize = maxSize > 0 ? maxSize : DEFAULT_MAX_FILE_SIZE;
    }

    @Override
    public void validateFile(final UploadedFile file, final String fileName, final String contentType) {
        if (file == null || file.getAbsolutePath() == null || file.getAbsolutePath().isEmpty())
            throw new IllegalArgumentException("Invalid file");

        final File actualFile = new File(file.getAbsolutePath());
        validateFile(actualFile, fileName, contentType);
    }

    @Override
    public void validateFile(final File file, final String fileName, final String contentType) {
        if (file == null || !file.exists())
            throw new IllegalArgumentException("Invalid file");

        if (file.length() > maxFileSize)
            throw new IllegalArgumentException("File size exceeds limit");

        validateNameAndType(fileName, contentType);
    }

    @Override
    public void validateFile(final InputStream inputStream, final long size, final String fileName, final String contentType) {
        if (inputStream == null)
            throw new IllegalArgumentException("Invalid file stream");

        if (size > maxFileSize)
            throw new IllegalArgumentException("File size exceeds limit");

        validateNameAndType(fileName, contentType);
    }

    @Override
    public void validateFile(final byte[] bytes, final String fileName, final String contentType) {
        if (bytes == null)
            throw new IllegalArgumentException("Invalid file");

        if (bytes.length > maxFileSize)
            throw new IllegalArgumentException("File size exceeds limit");

        validateNameAndType(fileName, contentType);
    }

    private void validateNameAndType(final String fileName, final String contentType) {
        if (fileName == null || fileName.trim().isEmpty())
            throw new IllegalArgumentException("Invalid file name");

        if (fileName.contains("..") || fileName.contains(";"))
            throw new IllegalArgumentException("Invalid file name");

        final int idx = fileName.lastIndexOf('.') ;
        if (idx < 0)
            throw new IllegalArgumentException("File type not allowed");

        final String extension = fileName.substring(idx + 1).toLowerCase();
        if (!allowedExtensions.contains(extension))
            throw new IllegalArgumentException("File type not allowed");

        if (contentType != null && !contentType.isEmpty()) {
            if (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))
                throw new IllegalArgumentException("Invalid content type");
        }
    }

}
