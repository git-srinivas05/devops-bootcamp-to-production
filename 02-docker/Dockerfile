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

