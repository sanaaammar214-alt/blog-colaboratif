package com.blog.config;

import com.blog.repository.CategorieRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;

    @ModelAttribute
    public void addGlobalAttributes(Authentication authentication, Model model) {
        // Ajouter les catégories sur toutes les pages pour le header
        model.addAttribute("categories", categorieRepository.findAll());

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            userRepository.findByEmail(authentication.getName())
                    .ifPresent(user -> {
                        model.addAttribute("currentUser", user);
                        model.addAttribute("currentUserNom", user.getNom());
                    });
        }
    }
}
