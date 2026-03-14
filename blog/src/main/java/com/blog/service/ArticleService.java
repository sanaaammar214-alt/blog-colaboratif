package com.blog.service;

import com.blog.model.Article;
import com.blog.model.Categorie;
import com.blog.model.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;

    public List<Article> getAll() {
        return articleRepository.findAllWithDetails();
    }

    // ✅ CORRECTION : utilise la vraie requête triée par likes
    public List<Article> getAllOrderByLikes() {
        return articleRepository.findAllOrderByLikesDesc();
    }

    public Article getById(Long id) {
        return articleRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Article introuvable : " + id));
    }

    public List<Article> getByCategorie(Long categorieId) {
        return articleRepository.findByCategorieIdWithDetails(categorieId);
    }

    public List<Article> getByAuteur(Long auteurId) {
        return articleRepository.findByAuteurIdWithDetails(auteurId);
    }

    public List<Article> search(String keyword) {
        return articleRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void save(Article article) {
        articleRepository.save(article);
    }

    @Transactional
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    // Vérifie que l'utilisateur est bien propriétaire ou admin
    public boolean peutModifier(Article article, User user) {
        return article.getAuteur().getId().equals(user.getId())
                || user.getRole().name().equals("ADMIN");
    }

    public Categorie getCategorieById(Long id) {
        return categorieRepository.findById(id).orElse(null);
    }
}
