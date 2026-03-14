package com.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Utiliser un chemin absolu pour éviter les problèmes de répertoire de travail
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                log.info("Création du dossier d'upload : {} -> {}", directory.getAbsolutePath(), created);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(directory.getAbsolutePath()).resolve(filename);
            
            log.info("Tentative d'écriture de l'image : {}", path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            
            return filename;
        } catch (IOException e) {
            log.error("Erreur lors de l'upload de l'image : {}", e.getMessage());
            throw e;
        }
    }
}
