# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-11 AS build
WORKDIR /app
# Copy pom.xml and download deps (layer caching)
COPY pom.xml .
RUN mvn -q -e dependency:go-offline
# Copy source
COPY src ./src
# Build application (skip tests for faster build)
RUN mvn -q package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:11-jre
WORKDIR /app
# Copy built jar
COPY --from=build /app/target/file-encryptor-web-0.0.1-SNAPSHOT.jar app.jar
# Set memory options (can be overridden by Render env var JAVA_TOOL_OPTIONS)
ENV JAVA_TOOL_OPTIONS="-Xmx512m -Xms256m"
# Expose default port; Spring Boot will use PORT env var on Render
EXPOSE 8080
# Run application
ENTRYPOINT ["java","-jar","/app/app.jar"]
