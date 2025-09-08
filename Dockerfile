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

# Install Node.js 22 (still keeping it as per original request, though not used by Spring app)
RUN mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg && \
    echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_22.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list && \
    apt-get update && apt-get install -y nodejs && rm -rf /var/lib/apt/lists/*

# Install Python 3.12 (still keeping it as per original request, though not used by Spring app)
RUN apt-get update && apt-get install -y python3.12 python3.12-venv python3-pip && rm -rf /var/lib/apt/lists/*

# Set Python 3.12 as default python
RUN update-alternatives --install /usr/bin/python python /usr/bin/python3.12 1

# Install Python libraries (still keeping it as per original request)
RUN pip install --no-cache-dir \
    google-cloud-aiplatform \
    pandas \
    numpy \
    httpx \
    Agno \
    tensorflow \
    torch

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