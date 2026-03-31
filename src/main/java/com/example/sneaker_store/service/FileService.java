package com.example.sneaker_store.service;

import com.example.sneaker_store.model.response.UploadFileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public interface FileService {
    UploadFileResponse uploadFile(MultipartFile file, String folder) throws URISyntaxException;
    Resource downloadFile(String fileName, String folder) throws URISyntaxException, FileNotFoundException;
    long existFile(String fileName, String folder) throws URISyntaxException;
}
