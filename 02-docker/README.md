Stage 2 – Docker (Application Packaging)
Purpose of This Stage

This stage focuses on packaging a CI-validated Spring Boot application into a Docker image.
Docker is used only for packaging and runtime consistency, not for deployment or orchestration.
This stage is intentionally placed after CI to ensure that only correct and tested code is containerized.

What This Stage Demonstrates

* Docker fundamentals applied to a real application
* Multi-stage Docker builds
* Separation of build-time and runtime responsibilities
* Reproducible application packaging
* Understanding and debugging Docker build/runtime failures
* Clear boundaries between CI and Docker responsibilities

Why Docker Comes After CI

CI validates code correctness (build + tests)
Docker ensures runtime consistency
Packaging broken code only hides problems and delays feedback.
Correct sequence 
Application → CI (Build + Test) → Docker → Deployment

Docker Strategy Used

Multi-Stage Docker Build
Two stages are used:
1. Builder Stage
Contains Maven and full JDK
Compiles and packages the application

2. Runtime Stage
Contains only a slim Java runtime
Runs the compiled JAR

Benefits:
* Smaller final image
* Reduced attack surface
* Clear responsibility separation
* Faster startup and predictable runtime behavior

Dockerfile:
# -------- Build stage --------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY 00-foundations/app/pom.xml .
COPY 00-foundations/app/src ./src
RUN mvn clean package -DskipTests

# -------- Runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/foundations-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

Dockerfile Explanation: 
Builder Stage: 

Uses a full JDK and Maven to compile the application
WORKDIR /build ensures Maven output is predictable
Split COPY instructions improve Docker cache efficiency
mvn clean package generates the JAR inside the container

Runtime Stage: 

Uses a lightweight Java runtime (no build tools)
Copies only the JAR from the builder stage
EXPOSE 8080 documents the application port
ENTRYPOINT defines container startup behavior

Build the Docker Image
From repository root: 
docker build -t foundations-app:stage2 -f 02-docker/Dockerfile .


What happens internally:
Maven runs inside the builder image
JAR is created under /build/target
Runtime image contains only Java + JAR


Run the Container:
docker run -d --name foundations-app -p 8080:8080 foundations-app:stage2

Verify the Application
curl http://localhost:8080/health
curl http://localhost:8080/api/message


Expected output:
UP
Hello from DevOps Foundations.
This confirms the container behaves exactly like the CI-validated application.





Intentional Failures and Debugging (Learning by Breaking):
To understand Docker behavior, several failures were intentionally introduced and fixed.
1. Artifact Not Found (Wrong JAR Path)

Error
COPY failed: file not found

Cause
WORKDIR was removed from the builder stage
Maven generated the JAR in a different filesystem path

Fix
Restored WORKDIR /build in the builder stage

Lesson
COPY --from=builder depends entirely on the builder stage filesystem
Runtime WORKDIR does NOT affect artifact copying
Explicit paths prevent subtle Docker failures

2. Port Mapping Errors
Error
Connection refused

Cause
Container port exposed but not mapped correctly

Fix
docker run -p 8080:8080 ...

Lesson
Docker does not expose ports automatically
Port mapping must be explicit

3. Removing EXPOSE Instruction
Observation
Application still ran successfully

Lesson
EXPOSE is documentation only
It does not publish or bind ports

4. Java Version Mismatch
Error
Unsupported class file major version

Cause
Application built with Java 17
Runtime image used Java 11

Fix
Aligned Java 17 across:
Local development
CI (Jenkins)
Docker build image
Docker runtime image

Lesson
Java version mismatches are a common production failure



Outcome of This Stage: 
Reproducible Docker image
Minimal runtime footprint
Clear understanding of Docker failure modes
Application ready for CI-driven Docker builds
