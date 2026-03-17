package com.blog.controller;

import com.blog.model.Categorie;
import com.blog.model.Role;
import com.blog.model.User;
import com.blog.repository.CategorieRepository;
import com.blog.repository.CommentaireRepository;
import com.blog.repository.LikeRepository;
import com.blog.service.ArticleService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ArticleService articleService;
    private final CategorieRepository categorieRepository;
    private final CommentaireRepository commentaireRepository;
    private final LikeRepository likeRepository;

    // ─── DASHBOARD ──────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("nbUsers",        userService.getAll().size());
        model.addAttribute("nbArticles",     articleService.getAll().size());
        model.addAttribute("nbCommentaires", commentaireRepository.count());
        model.addAttribute("nbLikes",        likeRepository.count());
        model.addAttribute("dernierArticles", articleService.getAll().stream().limit(5).toList());
        model.addAttribute("topArticles",    articleService.getAllOrderByLikes().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ─── UTILISATEURS ───────────────────────────────────────────────────────
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changerRole(@PathVariable Long id,
                              @RequestParam Role role,
                              RedirectAttributes redirectAttributes) {
        userService.changerRole(id, role);
        redirectAttributes.addFlashAttribute("successMessage", "Rôle mis à jour avec succès.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String supprimerUser(@PathVariable Long id,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal != null) {
            User current = userService.getAll().stream()
                    .filter(u -> u.getEmail().equals(principal.getName()))
                    .findFirst()
                    .orElse(null);
            if (current != null && current.getId().equals(id)) {
                throw new AccessDeniedException("Un administrateur ne peut pas se supprimer lui-même.");
            }
        }

        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès.");
        return "redirect:/admin/users";
    }

    // ─── CATÉGORIES ─────────────────────────────────────────────────────────
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("nouvelleCategorie", new Categorie());
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategorie(@ModelAttribute Categorie categorie,
                                RedirectAttributes redirectAttributes) {
        categorieRepository.save(categorie);
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie ajoutée avec succès.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategorie(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        categorieRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie supprimée avec succès.");
        return "redirect:/admin/categories";
    }

    // ─── ARTICLES (admin) ───────────────────────────────────────────────────
    @GetMapping("/articles")
    public String articles(Model model) {
        model.addAttribute("articles", articleService.getAll());
        return "admin/articles";
    }

    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Article supprimé avec succès.");
        return "redirect:/admin/articles";
    }
}
