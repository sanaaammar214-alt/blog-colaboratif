package com.blog.repository;

import com.blog.model.LikeCommentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeCommentaireRepository extends JpaRepository<LikeCommentaire, Long> {
    Optional<LikeCommentaire> findByUserIdAndCommentaireId(Long userId, Long commentId);
    boolean existsByUserIdAndCommentaireId(Long userId, Long commentId);
}
