package com.blog.controller;

import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Article;
import com.blog.model.Commentaire;
import com.blog.model.User;
import com.blog.repository.UserRepository;
import com.blog.service.ArticleService;
import com.blog.service.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/commentaires")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;
    private final ArticleService articleService;
    private final UserRepository userRepository;

    @PostMapping("/{id}/like")
    public String like(@PathVariable Long id, @RequestParam Long articleId, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = getUser(principal);
        Commentaire commentaire = commentaireService.getById(id);
        commentaireService.toggleLike(user, commentaire);
        return "redirect:/articles/" + articleId + "#comment-" + id;
    }

    @PostMapping("/{id}/repondre")
    public String repondre(@PathVariable Long id, 
                           @RequestParam Long articleId,
                           @RequestParam String contenu, 
                           Principal principal) {
        if (principal == null) return "redirect:/login";
        if (contenu == null || contenu.isBlank()) {
            return "redirect:/articles/" + articleId;
        }

        User user = getUser(principal);
        Article article = articleService.getById(articleId);
        Commentaire parent = commentaireService.getById(id);

        Commentaire reponse = new Commentaire();
        reponse.setContenu(contenu);
        reponse.setUser(user);
        reponse.setArticle(article);
        reponse.setParent(parent);
        reponse.setDateCommentaire(LocalDateTime.now());
        
        commentaireService.save(reponse);

        return "redirect:/articles/" + articleId + "#comment-" + reponse.getId();
    }

    private User getUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }
}
