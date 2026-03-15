package com.blog.service;

import com.blog.model.Commentaire;
import com.blog.model.User;
import com.blog.repository.CommentaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;

    @Transactional
    public void save(Commentaire commentaire) {
        commentaireRepository.save(commentaire);
    }

    public Commentaire getById(Long id) {
        return commentaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire introuvable : " + id));
    }

    public List<Commentaire> getByArticle(Long articleId) {
        return commentaireRepository.findByArticleIdOrderByDateDesc(articleId);
    }

    // ✅ Vérification propriétaire ou admin avant suppression
    @Transactional
    public void delete(Long id, User currentUser) {
        Commentaire c = getById(id);
        boolean isOwner = c.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        if (!isOwner && !isAdmin)
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce commentaire.");
        commentaireRepository.deleteById(id);
    }
}
