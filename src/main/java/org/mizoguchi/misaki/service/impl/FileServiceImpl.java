package org.mizoguchi.misaki.service.impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InternalServerErrorException;
import org.mizoguchi.misaki.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final MinioClient minioClient;

//    @Value("${minio.endpoint}")
//    private String endpoint;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try{
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }catch (Exception e){
            throw new InternalServerErrorException(FailMessageConstant.INTERNAL_SERVER_ERROR);
        }

        return "/" + bucket + "/" + fileName;
    }

    @Override
    public InputStream downloadFile(String fileName) {
        try{
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new InternalServerErrorException(FailMessageConstant.INTERNAL_SERVER_ERROR);
        }
    }
}
