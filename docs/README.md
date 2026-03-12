# Project Documentation

This directory contains architecture diagrams, screenshots, and supporting documentation for the **DevOps Bootcamp to Production** project.

The purpose of this folder is to separate **visual documentation and system design explanations** from the main source code.

---

# Architecture Overview

The system implements a complete DevOps pipeline for deploying and operating a cloud-native application on AWS.

![System Architecture](architecture.png)

---

# CI/CD Pipeline Flow

The application follows a CI/CD workflow that automatically builds and deploys the application after every change.

![CI/CD Pipeline](cicd-pipeline.png)

---

# Monitoring Architecture

The platform includes an observability stack using Prometheus and Grafana.

```text
Kubernetes Node
     ↓
Node Exporter
     ↓
Prometheus
     ↓
Grafana Dashboards
```

---

# Monitoring Screenshots

Example monitoring dashboards are available below.

### Grafana Cluster Dashboard

![Grafana Dashboard](grafana-dashboard.png)

---

# Troubleshooting Notes

During development several real-world issues were encountered and resolved:

* Docker client version mismatch in Jenkins
* ARM64 vs AMD64 container image compatibility
* AWS ECR authentication failures
* Terraform state accidentally committed to Git
* Node exporter port conflicts
* Grafana dashboards showing "No data"

Detailed troubleshooting steps are documented in:

```
docs/troubleshooting.md
```

---

# Purpose of This Documentation

The documentation in this folder helps explain:

* system architecture
* CI/CD pipeline design
* monitoring setup
* operational challenges encountered during development

This structure mirrors how real DevOps teams document production platforms.

