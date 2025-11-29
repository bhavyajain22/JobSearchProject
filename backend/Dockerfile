# backend/Dockerfile
# Stage 1: build with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY /backend/pom.xml .
COPY /backend/src ./src
# Avoid downloading optional plugins when not needed
RUN mvn -B -DskipTests package

# Stage 2: runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
# copy jar produced by the build stage (matches Spring Boot default)
COPY --from=build /workspace/target/*.jar app.jar
# If your app listens on 8080, expose it (informational)
EXPOSE 8080
# Use a non-root user for safety
USER 1000
ENTRYPOINT ["java","-jar","/app/app.jar"]
