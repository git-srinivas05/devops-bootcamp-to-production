# DevOps Bootcamp to Production 

This repository demonstrates an **end-to-end DevOps implementation** that takes an application from development to a production-ready Kubernetes environment on AWS.

The project simulates how modern DevOps teams build, deploy, monitor, and scale applications using industry tools.

---

# Project Goal

The objective of this project is to implement a **complete DevOps lifecycle**, including:

* Continuous Integration
* Containerization
* Infrastructure as Code
* Kubernetes Deployment
* Continuous Deployment
* Observability and Monitoring
* Production Hardening

---

# Architecture Overview

```
Developer
   ↓
GitHub Repository
   ↓
Jenkins CI Pipeline
   ↓
Docker Image Build
   ↓
AWS ECR (Container Registry)
   ↓
Amazon EKS (Kubernetes Cluster)
   ↓
Prometheus Monitoring
   ↓
Grafana Dashboards
```

---

# Technology Stack

| Category                | Tools                             |
| ----------------------- | --------------------------------- |
| Application             | Java (Spring Boot)                |
| Build Tool              | Maven                             |
| Containerization        | Docker                            |
| CI/CD                   | Jenkins                           |
| Infrastructure as Code  | Terraform                         |
| Container Registry      | AWS ECR                           |
| Container Orchestration | Amazon EKS                        |
| Monitoring              | Prometheus                        |
| Visualization           | Grafana                           |
| Metrics                 | Node Exporter, kube-state-metrics |

---

# Repository Structure

```
devops-bootcamp-to-production
│
├── 00-foundations
│   └── Basic Spring Boot application
│
├── 01-ci-cd-basics
│   └── Jenkins pipeline for CI
│
├── 02-docker
│   └── Dockerfile for containerizing the application
│
├── 03-kubernetes-core
│   └── Kubernetes deployment manifests
│
├── 04-ci-cd-to-k8s
│   └── Jenkins pipeline for automatic deployment
│
├── 05-terraform-aws
│   └── Infrastructure provisioning using Terraform
│
├── 06-observability
│   └── Prometheus + Grafana monitoring setup
│
├── 07-production-hardening
│   └── Autoscaling, resource limits, reliability improvements
│
└── docs
    └── architecture diagram
```

---

# CI/CD Workflow

1. Developer pushes code to GitHub.
2. Jenkins pipeline triggers automatically.
3. Maven builds the application.
4. Docker builds the container image.
5. Image is pushed to AWS ECR.
6. Jenkins updates the Kubernetes deployment.
7. Kubernetes performs a rolling update.
8. Prometheus collects cluster metrics.
9. Grafana visualizes monitoring dashboards.

---

# Monitoring & Observability

The monitoring stack includes:

* **Prometheus** – Collects metrics from Kubernetes
* **Node Exporter** – Exposes node metrics
* **kube-state-metrics** – Provides Kubernetes object metrics
* **Grafana** – Visualizes cluster health

Example monitored metrics:

* Node CPU usage
* Node memory usage
* Pod restart counts
* Deployment status
* Cluster capacity

---

# Key DevOps Concepts Demonstrated

* Infrastructure as Code
* Container orchestration
* Automated deployments
* Rolling updates
* Monitoring and observability
* Autoscaling strategies
* Production reliability practices
---

# Final Outcome
This project simulates a **real production DevOps workflow**:
```
Code → CI/CD → Containerization → Kubernetes → Monitoring → Autoscaling
```
It demonstrates how modern DevOps pipelines are implemented using cloud-native technologies.
and is designed to be explainable and reproducible.
<img width="1680" height="945" alt="image" src="https://github.com/user-attachments/assets/48fd5498-0abc-45aa-bcfd-4d9e6b527916" />
