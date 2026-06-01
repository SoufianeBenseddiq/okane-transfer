# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run WAR directly with Jetty runner
FROM eclipse-temurin:17-jre
WORKDIR /app

# Download Jetty runner
RUN apt-get update && apt-get install -y wget && \
    wget -q https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/11.0.20/jetty-runner-11.0.20.jar \
    -O jetty-runner.jar && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy WAR from builder
COPY --from=builder /app/target/okane-transfer-1.0-SNAPSHOT.war app.war

EXPOSE 8080

CMD ["java", "-jar", "jetty-runner.jar", "--path", "/okane_transfer_war", "app.war"]