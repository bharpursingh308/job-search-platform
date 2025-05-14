# STEP 1: Build the application using Gradle
FROM gradle:8.5-jdk17 AS builder

#Working directory inside Docker container
WORKDIR /user-service

# Copy Gradle files (you're using Groovy DSL)
COPY build.gradle settings.gradle ./

# Pre-download dependencies
RUN gradle dependencies

# Now copy the entire source code
COPY . .

# Build the Spring Boot fat jar
RUN gradle bootJar

# STEP 2: Create the actual lightweight image
FROM openjdk:17-jdk-slim

#Work Directory
WORKDIR /user-service

# Copy the built jar from the builder image
COPY --from=builder /user-service/build/libs/*.jar app.jar

# Expose port if needed (Spring default is 8080)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]