# Stage 1: Build with Maven
FROM eclipse-temurin:23-jdk as build

# Install Maven manually
RUN apt-get update && \
    apt-get install -y curl unzip && \
    curl -fsSL https://dlcdn.apache.org/maven/maven-3/3.9.10/binaries/apache-maven-3.9.10-bin.zip -o maven.zip && \
    unzip maven.zip -d /opt && \
    ln -s /opt/apache-maven-3.9.10 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn

WORKDIR /app
COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests
RUN ls -l /app/target

# Stage 2: Run with Java 23
FROM eclipse-temurin:23-jdk

WORKDIR /app
COPY --from=build /app/target/CamelUp-backend-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]