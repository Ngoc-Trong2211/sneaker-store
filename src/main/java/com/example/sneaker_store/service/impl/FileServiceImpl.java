package com.example.sneaker_store.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.sneaker_store.dto.response.UploadFileResponse;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j(topic = "FILE-SERVICE")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final Cloudinary cloudinary;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    private String getTransformation(String folder) {
        if ("brand".equalsIgnoreCase(folder)) {
            return "c_fill,w_300,h_300,g_auto,q_auto,f_auto";
        } else if ("product".equalsIgnoreCase(folder)) {
            return "c_fit,w_300,h_300,q_auto,f_auto";
        }
        return "c_fit,w_300,h_300,q_auto,f_auto";
    }

    @Override
    @PreAuthorize("hasAuthority('FILE_CREATE') or hasAuthority('ADMIN')")
    public UploadFileResponse uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IdInvalidException("File is empty");
        }
        String fileName = file.getOriginalFilename();

        boolean isValid = ALLOWED_EXTENSIONS.stream().anyMatch(
                ext -> fileName != null && fileName.toLowerCase().endsWith(ext)
        );
        if (!isValid) {
            throw new IdInvalidException("Chỉ chấp nhận file: " + ALLOWED_EXTENSIONS);
        }
        String transformation = getTransformation(folder);
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "unique_filename", true
                    )
            );

            String imageUrl = uploadResult.get("url").toString();
            String publicId = uploadResult.get("public_id").toString();

            return new UploadFileResponse(imageUrl, publicId, Instant.now());
        } catch (IOException e) {
            log.error("Upload failed", e);
            throw new RuntimeException("Upload failed");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('FILE_DELETE') or hasAuthority('ADMIN')")
    public void deleteFile(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            throw new IdInvalidException("PublicId không được để trống");
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("Delete failed", e);
            throw new RuntimeException("Không thể xóa ảnh");
        }
    }
}
