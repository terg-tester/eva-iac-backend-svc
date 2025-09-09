# Use a base image that supports multiple runtimes
FROM ubuntu:22.04

# Set environment variables
ENV DEBIAN_FRONTEND=noninteractive

# Install common tools and dependencies
RUN apt-get update && apt-get install -y \
    software-properties-common \
    curl \
    gnupg \
    build-essential \
    git \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Install Java 21
RUN apt-get update && apt-get install -y openjdk-21-jdk && rm -rf /var/lib/apt/lists/*

# Set a working directory
WORKDIR /app

# Copy the Spring Boot application files
COPY . /app

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Expose the port Spring Boot runs on
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
