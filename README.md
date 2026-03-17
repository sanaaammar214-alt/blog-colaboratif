# CollabInk — Blog Collaboratif

Application web de blog collaboratif développée avec Spring Boot 3, Spring Security, Thymeleaf et MySQL.

## Stack technique
- Java 21 + Spring Boot 3
- Spring Security (authentification par formulaire, rôles LECTEUR / AUTEUR / ADMIN)
- Spring Data JPA + MySQL 8
- Thymeleaf (templates HTML)
- Bootstrap 5 + Bootstrap Icons

## Prérequis
- Java 21+
- MySQL 8+
- Maven 3.8+

## Installation

1. Créer la base de données :
```sql
CREATE DATABASE blog_collaboratif CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Ou utiliser le fichier fourni : `blog/init.sql`

2. Configurer les variables d'environnement (ou modifier `application.properties`) :
```bash
DB_URL=jdbc:mysql://localhost:3306/blog_collaboratif?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=votre_mot_de_passe
UPLOAD_DIR=uploads/images
```

3. Lancer l'application :
```bash
cd blog
mvn spring-boot:run
```

4. Accéder à : http://localhost:8080

## Comptes de test (créés automatiquement au 1er démarrage)
| Rôle   | Email              | Mot de passe |
|--------|--------------------|--------------|
| ADMIN  | admin@collabink.ma | admin123     |
| AUTEUR | sara@collabink.ma  | auteur123    |

## Fonctionnalités
- ✅ Inscription / Connexion / Déconnexion
- ✅ Création, modification, suppression d'articles (AUTEUR/ADMIN)
- ✅ Upload d'image de couverture (JPEG, PNG, GIF, WebP — max 5 Mo)
- ✅ Catégories
- ✅ Commentaires (authentifiés)
- ✅ Système de likes (toggle)
- ✅ Recherche d'articles
- ✅ Pagination
- ✅ Panel d'administration (gestion users, articles, catégories)
- ✅ Gestion des rôles par l'admin

## Structure des uploads
Les images uploadées sont stockées dans `{user.dir}/uploads/images/` et servies via l'URL `/uploads/images/{filename}`.

compte  
Admin: admin@collabink.ma / admin123
Auteur1: sara@collabink.ma  / auteur123
Auteur2: sanaa@collabink.ma/ sanaa123
Auteur3: meriem@collabink.ma/ meriem123
Auteur4: salma@collabink.ma/ salma123
Auteur5: oussama@collabink.ma/oussama123
lecteur1:ahmed@collabink.ma/ahmed123
lecteur2:ali@collabink.ma/ali123
lecteur3:ilham@collabink.ma/ilham123
lecteur4:badr@collabink.ma/badr123
lecteur5:taha@collabink.ma/taha123


