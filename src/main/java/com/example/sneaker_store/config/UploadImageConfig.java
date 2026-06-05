package com.example.sneaker_store.config;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadImageConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:file:///D:/DoAnTN/projectSneaker/images/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(normalizeUploadDir());
    }

    private String normalizeUploadDir() {
        return uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
    }
}
