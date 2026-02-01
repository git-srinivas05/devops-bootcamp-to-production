# Stage 0 – Foundations

## What this stage demonstrates
A minimal Spring Boot application running locally without any DevOps tooling.
This stage establishes a clear application baseline and exposes common
manual build and execution pitfalls before introducing automation.

## Application overview
A simple REST API exposing:
- `/health` – health check endpoint
- `/api/message` – sample business endpoint

This application is intentionally minimal and serves as the foundation
for all subsequent DevOps stages in this repository.

## How to run locally
From the application directory:
```bash
cd 00-foundations/app
mvn clean package
mvn spring-boot:run

## What can break here
Java or Maven version mismatch
Port 8080 already in use
Incorrect package or directory structure
Malformed pom.xml
Running build commands from an incorrect directory

## Errors faced and lessons learned

Maven build failures due to malformed XML highlighted how fragile
manual configuration can be without validation.
Spring Boot startup failures (“Unable to find main class”) reinforced
the importance of correct package structure and execution context.
Running Maven from the repository root instead of the application
directory demonstrated how easily builds can fail without enforced standards.
Accidentally creating a nested Git repository emphasized the importance
of strict repository hygiene and working from a single repo root.

## Why DevOps is needed next
Manual builds and local execution are inconsistent and error-prone.
There is no automated validation, no early feedback on failures,
and no guarantee that the application will behave consistently across environments.
Introducing CI/CD is necessary to automate builds, enforce standards,
and catch failures early in the development lifecycle.

