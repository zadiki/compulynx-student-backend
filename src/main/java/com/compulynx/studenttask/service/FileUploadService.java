package com.compulynx.studenttask.service;


import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "static/images/";

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public String imageUpload(MultipartFile file) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds the maximum limit of 5MB.");
        }
        String fileType = file.getContentType();
        if (fileType == null || !(fileType.equals("image/png") || fileType.equals("image/jpeg"))) {
            throw new IOException("Invalid file type. Only PNG and JPEG are allowed.");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            var crested = uploadDir.mkdirs();
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String filenameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));


        String newFilename = filenameWithoutExtension + "_" + System.currentTimeMillis() + fileExtension;
        Path destinationPath = Paths.get(UPLOAD_DIR + newFilename);

        // Save the file to the static/images directory
//        file.transferTo(destinationPath.toFile());
        FileOutputStream fout = new FileOutputStream(destinationPath.toString());
        fout.write(file.getBytes());

        // Closing the connection
        fout.close();
        return "http://localhost:8080/images/"+newFilename;

    }
}
