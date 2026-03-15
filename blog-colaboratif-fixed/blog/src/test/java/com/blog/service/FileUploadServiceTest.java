package com.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadServiceTest {

    private FileUploadService fileUploadService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileUploadService = new FileUploadService();
        ReflectionTestUtils.setField(fileUploadService, "uploadDir", tempDir.toString());
    }

    @Test
    void uploadImage_nullFile_returnsNull() throws IOException {
        assertNull(fileUploadService.uploadImage(null));
    }

    @Test
    void uploadImage_emptyFile_returnsNull() throws IOException {
        MockMultipartFile empty = new MockMultipartFile("file", new byte[0]);
        assertNull(fileUploadService.uploadImage(empty));
    }

    @Test
    void uploadImage_validJpeg_returnsFilename() throws IOException {
        // Magic bytes JPEG : FF D8 FF E0
        byte[] jpegBytes = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0,
                0,0,0,0,0,0,0,0,0,0,0,0};
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", jpegBytes);

        String result = fileUploadService.uploadImage(file);
        assertNotNull(result);
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    void uploadImage_validPng_returnsFilename() throws IOException {
        // Magic bytes PNG : 89 50 4E 47 0D 0A 1A 0A
        byte[] pngBytes = new byte[]{(byte)0x89,'P','N','G',
                (byte)0x0D,(byte)0x0A,(byte)0x1A,(byte)0x0A,
                0,0,0,0,0,0,0,0};
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", pngBytes);

        String result = fileUploadService.uploadImage(file);
        assertNotNull(result);
        assertTrue(result.endsWith(".png"));
    }

    @Test
    void uploadImage_invalidExtension_throwsException() {
        byte[] fakeBytes = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0,
                0,0,0,0,0,0,0,0,0,0,0,0};
        MockMultipartFile file = new MockMultipartFile(
                "file", "malicious.php", "image/jpeg", fakeBytes);

        assertThrows(IOException.class, () -> fileUploadService.uploadImage(file));
    }

    @Test
    void uploadImage_validExtensionButWrongMime_throwsException() {
        // Extension .jpg mais contenu texte (pas de magic bytes image)
        byte[] textBytes = "<?php echo 'hack'; ?>".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "hack.jpg", "image/jpeg", textBytes);

        assertThrows(IOException.class, () -> fileUploadService.uploadImage(file));
    }

    @Test
    void uploadImage_filenameIsSafeUUID() throws IOException {
        byte[] jpegBytes = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0,
                0,0,0,0,0,0,0,0,0,0,0,0};
        MockMultipartFile file = new MockMultipartFile(
                "file", "../../etc/passwd.jpg", "image/jpeg", jpegBytes);

        // Doit uploader sans erreur et retourner un UUID sans slash
        String result = fileUploadService.uploadImage(file);
        assertNotNull(result);
        assertFalse(result.contains("/"));
        assertFalse(result.contains(".."));
    }
}
