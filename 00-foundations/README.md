## What this stage demonstrates
Running a minimal application locally without any DevOps tooling.

## Application overview
A simple REST API that exposes health and business endpoints.

## How to run locally
mvn clean package
mvn spring-boot:run

## What can break here
Port conflicts, Java/Maven version mismatch, missing dependencies.

## Why DevOps is needed next
Manual builds, no automation, environment inconsistency, no deployment safety.

