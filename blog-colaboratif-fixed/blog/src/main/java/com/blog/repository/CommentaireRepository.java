package com.blog.repository;

import com.blog.model.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    // Commentaires d'un article triés du plus récent au plus ancien
    @Query("SELECT c FROM Commentaire c LEFT JOIN FETCH c.user WHERE c.article.id = :articleId ORDER BY c.dateCommentaire DESC")
    List<Commentaire> findByArticleIdOrderByDateDesc(Long articleId);
}
