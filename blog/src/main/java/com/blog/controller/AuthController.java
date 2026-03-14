package com.blog.controller;

import com.blog.model.Role;
import com.blog.model.User;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "public/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "public/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String nom,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(defaultValue = "LECTEUR") String role,
            Model model) {

        try {
            User user = new User();
            user.setNom(nom);
            user.setEmail(email);
            user.setPassword(password);

            // Sécurité : seulement LECTEUR ou AUTEUR autorisé
            if ("AUTEUR".equals(role)) {
                user.setRole(Role.AUTEUR);
            } else {
                user.setRole(Role.LECTEUR);
            }

            userService.inscrire(user);
            return "redirect:/login?registered";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "public/register";
        }
    }

    @GetMapping("/about")
    public String about() {
        return "public/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "public/contact";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}
