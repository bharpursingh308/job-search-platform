# STEP 1: Build the application using Gradle
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy only build.gradle and settings.gradle first to leverage Docker layer caching
COPY user-service/build.gradle.kts user-service/settings.gradle.kts ./user-service/

# Copy Gradle wrapper files
COPY user-service/build.gradle user-service/settings.gradle ./user-service/

# Pre-download dependencies
WORKDIR /app/user-service
RUN ./gradlew dependencies

# Now copy the entire source code
COPY user-service /app/user-service

# Build the Spring Boot fat jar
RUN ./gradlew bootJar

# STEP 2: Create the actual lightweight image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from the builder image
COPY --from=builder /app/user-service/build/libs/*.jar app.jar

# Expose port if needed (Spring default is 8080)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]
