package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.response.UploadFileResponse;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file/v1")
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage(message = "Upload file thành công")
    @Operation(summary = "Upload file", description = "Upload file")
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestPart("file") MultipartFile file,
                                                         @RequestParam("folder") String folder) throws URISyntaxException {
        return ResponseEntity.ok().body(this.fileService.uploadFile(file, folder));
    }

    @GetMapping("/files")
    @ApiMessage(message = "Download file thành công")
    @Operation(summary = "Download file", description = "Download file")
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "fileName", required = false) String fileName,
                                                 @RequestParam(name = "folder", required = false) String folder) throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(this.fileService.existFile(fileName, folder))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(this.fileService.downloadFile(fileName, folder));
    }

    @GetMapping("/files/{fileName}")
    @ApiMessage(message = "Get file thành công")
    @Operation(summary = "Get file", description = "Get file")
    public ResponseEntity<Resource> showCV(@PathVariable ("fileName") String fileName) throws IOException {
        Resource resource = this.fileService.getImage(fileName);
        if (resource==null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
