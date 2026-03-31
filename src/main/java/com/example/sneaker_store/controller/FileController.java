package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.response.UploadFileResponse;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file/v1")
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage(message = "Upload thành công")
    @Operation(summary = "Upload file", description = "Upload file")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestPart("file") MultipartFile file,
                                                         @RequestParam("folder") String folder) throws URISyntaxException {
        return ResponseEntity.ok().body(this.fileService.uploadFile(file, folder));
    }

    @GetMapping("/files")
    @ApiMessage(message = "Download thành công")
    @Operation(summary = "Download file", description = "Downloadfile")
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "fileName", required = false) String fileName,
                                                 @RequestParam(name = "folder", required = false) String folder) throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(this.fileService.existFile(fileName, folder))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(this.fileService.downloadFile(fileName, folder));
    }
}
