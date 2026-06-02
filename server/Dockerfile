# ── Fase 1: compilar ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# ── Fase 2: imagen final ligera ───────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Carpetas de almacenamiento
RUN mkdir -p storage/audios storage/playlist-covers storage/profile-pictures

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
