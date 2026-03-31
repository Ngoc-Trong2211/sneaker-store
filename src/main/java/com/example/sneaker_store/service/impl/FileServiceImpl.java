package com.example.sneaker_store.service.impl;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.example.sneaker_store.model.response.UploadFileResponse;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j(topic = "FILE-SERVICE")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${sneaker.upload-file.base-uri}")
    private String baseUri;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        if (!file.isDirectory()){
            try {
                Files.createDirectory(file.toPath());
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
    }

    public String saveFile(MultipartFile file, String folder) throws URISyntaxException {
        String fileSignature = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        URI uri = new URI(baseUri + folder + "/" + fileSignature);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return fileSignature;
    }

    @Override
    public UploadFileResponse uploadFile(MultipartFile file, String folder) throws URISyntaxException {
        if (file.isEmpty()){
            throw new IdInvalidException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        List<String> checkFile = Arrays.asList("jpg", "jepg", "png");
        boolean checkValid = checkFile.stream().anyMatch(
                item -> fileName != null && fileName.toLowerCase().endsWith(item));

        if (!checkValid){
            throw new IdInvalidException("Kieu file phu hop la" + checkFile.toString());
        }
        this.createDirectory(baseUri + folder);
        String finalName = this.saveFile(file, folder);

        return new UploadFileResponse(finalName, Instant.now());
    }

    @Override
    public long existFile(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(baseUri + folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        if (file.isDirectory() || !file.exists()) return 0;
        return file.length();
    }

    public Resource getFileDownload(String fileName, String folder) throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(baseUri + folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }

    @Override
    public Resource downloadFile(String fileName, String folder) throws URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new IdInvalidException("Phai co du fileName va folder");
        }

        long fileLength = this.existFile(fileName, folder);
        if (fileLength == 0) {
            throw new IdInvalidException("Khong ton tai ten file");
        }

        return this.getFileDownload(fileName, folder);
    }
}
