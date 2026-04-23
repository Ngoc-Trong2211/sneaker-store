package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.response.UploadFileResponse;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file/v1")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage(message = "Upload file thành công")
    @Operation(summary = "Upload file", description = "Upload file lên cloud")
    public ResponseEntity<UploadFileResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folder") String folder
    ) {
        return ResponseEntity.ok(this.fileService.uploadFile(file, folder));
    }

    @DeleteMapping
    @ApiMessage(message = "Xóa file thành công")
    @Operation(summary = "Delete file", description = "Xóa file trên cloud")
    public ResponseEntity<Void> deleteFile(@RequestParam String publicId) {
        this.fileService.deleteFile(publicId);
        return ResponseEntity.ok().build();
    }
}