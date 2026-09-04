# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy backend configuration and source
COPY backend/pom.xml ./
COPY backend/src ./src

# Build production jar
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Start Spring Boot application with production profile and strict 512MB RAM limits
ENTRYPOINT ["java", "-Xmx200m", "-Xms128m", "-XX:MaxMetaspaceSize=128m", "-XX:ReservedCodeCacheSize=32m", "-Xss256k", "-XX:+UseSerialGC", "-XX:TieredStopAtLevel=1", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
