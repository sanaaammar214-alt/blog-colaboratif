package com.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Vérification de la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("Le fichier dépasse la taille maximale autorisée (5 Mo).");
        }

        // Vérification de l'extension déclarée
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IOException("Nom de fichier invalide.");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IOException("Extension non autorisée : " + ext);
        }

        // Vérification du type MIME réel (magic bytes)
        String detectedMime = detectMimeType(file);
        if (!ALLOWED_MIME_TYPES.contains(detectedMime)) {
            throw new IOException("Type de fichier non autorisé (MIME détecté : " + detectedMime + ").");
        }

        // Chemin absolu basé sur le répertoire de travail courant (user.dir)
        Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Dossier d'upload créé : {}", uploadPath);
        }

        // Nom de fichier sécurisé (UUID + extension validée, sans caractères dangereux)
        String safeFilename = UUID.randomUUID() + "." + ext;
        Path targetPath = uploadPath.resolve(safeFilename);

        // Vérification que le chemin résolu reste dans le dossier d'upload (path traversal)
        if (!targetPath.startsWith(uploadPath)) {
            throw new IOException("Tentative de path traversal détectée.");
        }

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image uploadée : {}", targetPath);

        return safeFilename;
    }

    private String detectMimeType(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            int read = is.read(header);
            if (read < 4) return "application/octet-stream";

            // JPEG : FF D8 FF
            if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
                return "image/jpeg";
            }
            // PNG : 89 50 4E 47 0D 0A 1A 0A
            if ((header[0] & 0xFF) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G') {
                return "image/png";
            }
            // GIF : 47 49 46 38
            if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F' && header[3] == '8') {
                return "image/gif";
            }
            // WebP : 52 49 46 46 ?? ?? ?? ?? 57 45 42 50
            if (read >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                return "image/webp";
            }
            return "application/octet-stream";
        }
    }
}
