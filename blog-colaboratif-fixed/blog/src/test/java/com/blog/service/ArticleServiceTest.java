package com.blog.service;

import com.blog.model.Article;
import com.blog.model.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategorieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

        assertThrows(RuntimeException.class, () -> articleService.getById(1L));
        verify(articleRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    void testPeutModifier_Author() {
        User author = new User();
        author.setId(1L);
        author.setRole(com.blog.model.Role.AUTEUR);

        Article article = new Article();
        article.setAuteur(author);

        assertTrue(articleService.peutModifier(article, author));
    }

    @Test
    void testPeutModifier_Admin() {
        User author = new User();
        author.setId(1L);
        author.setRole(com.blog.model.Role.AUTEUR);

        User admin = new User();
        admin.setId(2L);
        admin.setRole(com.blog.model.Role.ADMIN);

        Article article = new Article();
        article.setAuteur(author);

        assertTrue(articleService.peutModifier(article, admin));
    }

    @Test
    void testPeutModifier_Forbidden() {
        User author = new User();
        author.setId(1L);
        author.setRole(com.blog.model.Role.AUTEUR);

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setRole(com.blog.model.Role.LECTEUR);

        Article article = new Article();
        article.setAuteur(author);

        assertFalse(articleService.peutModifier(article, otherUser));
    }
}
