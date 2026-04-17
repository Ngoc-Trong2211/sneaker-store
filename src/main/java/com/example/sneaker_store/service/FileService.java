package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.response.UploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    UploadFileResponse uploadFile(MultipartFile file, String folder);
    void deleteFile(String publicId);
}
