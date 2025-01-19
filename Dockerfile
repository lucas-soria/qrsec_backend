# Stage 1: Build
FROM alpine:3.19.1 AS builder

# Copy all files of the current folder into /app
COPY . /app

# Set working directory to /app
WORKDIR /app

# Gradle needs openjdk21 in order to build the .jar file
RUN apk add --no-cache openjdk21-jre-headless

# Build the project
RUN sh ./gradlew clean build --exclude-task test --no-daemon

# Stage 2: Run
FROM openjdk:21-jdk as run

# Copy the .jar file from the builder stage into /app/qrsec_backend.jar
COPY --from=builder /app/build/libs/*.jar /app/qrsec_backend.jar

# Set working directory to /app
WORKDIR /app

# Start QRSec backend application
CMD ["java", "-jar",  "qrsec_backend.jar"]
