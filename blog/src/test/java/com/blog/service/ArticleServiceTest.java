package com.blog.service;

import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Article;
import com.blog.model.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategorieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById_Found() {
        Article article = new Article();
        article.setId(1L);
        article.setTitre("Test Title");

        when(articleRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(article));

        Article result = articleService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitre());
        verify(articleRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    void testGetById_NotFound() {
        when(articleRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.getById(1L));
        verify(articleRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    void testPeutEditer_Author() {
        User author = new User();
        author.setId(1L);
        author.setRole(com.blog.model.Role.AUTEUR);

        Article article = new Article();
        article.setAuteur(author);

        assertTrue(articleService.peutEditer(article, author));
    }

    @Test
    void testPeutEditer_Admin() {
        User author = new User();
        author.setId(1L);
        author.setRole(com.blog.model.Role.AUTEUR);

        User admin = new User();
        admin.setId(2L);
        admin.setRole(com.blog.model.Role.ADMIN);

        Article article = new Article();
        article.setAuteur(author);

        assertTrue(articleService.peutEditer(article, admin));
    }

    @Test
    void testPeutEditer_Forbidden() {
        User author = new User();
        author.setId(1L);
        author.setRole(com.blog.model.Role.AUTEUR);

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setRole(com.blog.model.Role.LECTEUR);

        Article article = new Article();
        article.setAuteur(author);

        assertFalse(articleService.peutEditer(article, otherUser));
    }

    @Test
    void testSave_generatesSlugWhenMissing() {
        Article article = new Article();
        article.setTitre("Mon super titre");
        article.setContenu("x".repeat(60));

        articleService.save(article);

        assertNotNull(article.getSlug());
        assertFalse(article.getSlug().isBlank());
        verify(articleRepository, times(1)).save(article);
    }

    @Test
    void testDelete_deletesImageFileWhenPresent() throws Exception {
        Path tmp = Files.createTempDirectory("collabink-test");
        String previousUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tmp.toString());
        try {
            Path uploadDir = tmp.resolve("uploads").resolve("images");
            Files.createDirectories(uploadDir);
            Path img = uploadDir.resolve("test.jpg");
            Files.writeString(img, "x");

            Article article = new Article();
            article.setId(1L);
            article.setImage("test.jpg");

            when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

            articleService.delete(1L);

            assertFalse(Files.exists(img));
            verify(articleRepository, times(1)).deleteById(1L);
        } finally {
            if (previousUserDir != null) {
                System.setProperty("user.dir", previousUserDir);
            }
        }
    }

    @Test
    void testDelete_notFound_throwsResourceNotFound() {
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> articleService.delete(1L));
    }

    @Test
    void testPeutSupprimer_matchesPeutEditer() {
        User author = new User();
        author.setId(1L);
        author.setRole(com.blog.model.Role.AUTEUR);

        Article article = new Article();
        article.setAuteur(author);

        assertTrue(articleService.peutSupprimer(article, author));
    }
}
