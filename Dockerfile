# --- Stage 1: Build the app with Maven ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Run it with a lightweight Java runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render (and most hosts) inject a PORT environment variable at runtime and
# expect the app to listen on it. This passes it to Spring Boot as a
# command-line argument, which overrides whatever's in application.properties -
# no need to touch that file or hardcode a port.
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
