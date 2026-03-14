package com.blog.controller;

import com.blog.repository.CategorieRepository;
import com.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ArticleService articleService;
    private final CategorieRepository categorieRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("articles", articleService.getAll().stream().limit(6).toList());
        model.addAttribute("topArticles", articleService.getAllOrderByLikes().stream().limit(3).toList());
        model.addAttribute("categories", categorieRepository.findAll());
        return "public/index";
    }
}
