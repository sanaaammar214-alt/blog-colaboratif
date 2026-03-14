package com.blog.repository;

import com.blog.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndArticleId(Long userId, Long articleId);
    long countByArticleId(Long articleId);
    boolean existsByUserIdAndArticleId(Long userId, Long articleId);
}
