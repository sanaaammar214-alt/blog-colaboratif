package com.blog.config;

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

    @ModelAttribute
    public void addCurrentUser(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            userRepository.findByEmail(authentication.getName())
                    .ifPresent(user -> model.addAttribute("currentUserNom", user.getNom()));
        }
    }
}
