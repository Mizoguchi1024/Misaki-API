package org.mizoguchi.misaki.service.common;

import org.mizoguchi.misaki.pojo.vo.common.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {
    UploadResponse uploadFile(MultipartFile file);
    InputStream downloadFile(String fileName);
}
