# Multi-stage Dockerfile for building and running the Spring Boot app
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY . .
# Build the application (skip tests for faster dev builds)
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=builder /workspace/target/*.jar /app/app.jar
# Expose app port
EXPOSE 8080
# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]