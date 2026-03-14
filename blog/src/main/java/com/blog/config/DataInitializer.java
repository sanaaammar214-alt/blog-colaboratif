package com.blog.config;

import com.blog.model.Article;
import com.blog.model.Categorie;
import com.blog.model.Role;
import com.blog.model.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategorieRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Initialise les données de base au premier démarrage.
 * Crée un admin, des catégories et quelques articles de test.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;
    private final ArticleRepository articleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 1. Créer l'admin si inexistant
        User admin = userRepository.findByEmail("admin@collabink.ma").orElseGet(() -> {
            User u = new User();
            u.setNom("Administrateur");
            u.setEmail("admin@collabink.ma");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setRole(Role.ADMIN);
            userRepository.save(u);
            log.info("✅ Admin créé — email: admin@collabink.ma / password: admin123");
            return u;
        });

        // 2. Créer un auteur de test si inexistant
        User auteur = userRepository.findByEmail("sara@collabink.ma").orElseGet(() -> {
            User u = new User();
            u.setNom("Sara Ammar");
            u.setEmail("sara@collabink.ma");
            u.setPassword(passwordEncoder.encode("auteur123"));
            u.setRole(Role.AUTEUR);
            userRepository.save(u);
            log.info("✅ Auteur créé — email: sara@collabink.ma / password: auteur123");
            return u;
        });

        // 3. Créer catégories par défaut si aucune
        if (categorieRepository.count() == 0) {
            List.of("Technologie", "Culture", "Science", "Société", "Art & Design").forEach(nom -> {
                Categorie c = new Categorie();
                c.setNom(nom);
                categorieRepository.save(c);
            });
            log.info("✅ Catégories créées");
        }

        // 4. Créer des articles de test si aucun
        if (articleRepository.count() == 0) {
            Categorie tech = categorieRepository.findAll().stream()
                    .filter(c -> c.getNom().equals("Technologie")).findFirst().orElse(null);
            Categorie science = categorieRepository.findAll().stream()
                    .filter(c -> c.getNom().equals("Science")).findFirst().orElse(null);

            // Article 1
            Article a1 = new Article();
            a1.setTitre("L'Intelligence Artificielle en 2026");
            a1.setContenu("L'IA continue de transformer nos vies de manière inédite. " +
                    "De la santé à l'éducation, les modèles génératifs deviennent des assistants indispensables...");
            a1.setAuteur(auteur);
            a1.setCategorie(tech);
            a1.setDatePublication(LocalDateTime.now().minusDays(2));
            a1.setImage("tech.jpg"); // Image fictive (doit exister dans C:/Users/HP/uploads/img/)
            articleRepository.save(a1);

            // Article 2
            Article a2 = new Article();
            a2.setTitre("Exploration de Mars : Nouveaux Horizons");
            a2.setContenu("La mission habitée vers Mars franchit une étape cruciale ce mois-ci avec l'atterrissage du module Hope...");
            a2.setAuteur(admin);
            a2.setCategorie(science);
            a2.setDatePublication(LocalDateTime.now().minusHours(5));
            a2.setImage("mars.jpg"); // Image fictive
            articleRepository.save(a2);

            // Article 3 : Test Upload Interne
            Article a3 = new Article();
            a3.setTitre("Test de l'Upload Interne");
            a3.setContenu("Cet article vérifie que les images sont bien lues depuis le dossier static/assets/img/uploads du projet.");
            a3.setAuteur(admin);
            a3.setCategorie(tech);
            a3.setDatePublication(LocalDateTime.now().minusMinutes(30));
            a3.setImage("test-upload.jpg"); 
            articleRepository.save(a3);

            log.info("✅ Articles de test créés avec succès");
        }
    }
}
