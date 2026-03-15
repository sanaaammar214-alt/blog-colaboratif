# Étape 1 : Build (Construction de l'application)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY blog/pom.xml .
COPY blog/src ./src
# Package l'application en sautant les tests pour accélérer le déploiement
RUN mvn clean package -DskipTests

# Étape 2 : Run (Lancement de l'application)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Port exposé par Spring Boot
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
