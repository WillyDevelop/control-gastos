# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Start Spring Boot application with production profile and strict 512MB RAM limits
ENTRYPOINT ["java", "-Xmx200m", "-Xms128m", "-XX:MaxMetaspaceSize=128m", "-XX:ReservedCodeCacheSize=32m", "-Xss256k", "-XX:+UseSerialGC", "-XX:TieredStopAtLevel=1", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
