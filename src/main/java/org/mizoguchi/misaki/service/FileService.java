package org.mizoguchi.misaki.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {
    String uploadFile(MultipartFile file);
    InputStream downloadFile(String fileName);
}
