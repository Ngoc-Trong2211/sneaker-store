package com.example.sneaker_store.service;

import com.example.sneaker_store.model.response.UploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;

public interface FileService {
    UploadFileResponse uploadFile(MultipartFile file, String folder) throws URISyntaxException;
}
