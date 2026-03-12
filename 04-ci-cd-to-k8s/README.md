# 04 – CI/CD Pipeline to Kubernetes (Jenkins → ECR → EKS)

This stage implements an automated **Continuous Integration and Continuous Deployment pipeline** that builds the application, containerizes it, pushes the image to AWS ECR, and deploys the updated version to the Kubernetes cluster running on Amazon EKS.

---

# Objective

Automate the deployment process so that every code change can be built and deployed with minimal manual intervention.

The pipeline performs the following actions:

* Fetch source code from GitHub
* Build the application using Maven
* Build the Docker image
* Push the image to AWS ECR
* Update the Kubernetes deployment
* Perform a rolling update

---

# CI/CD Architecture

```text
Developer
   ↓
GitHub (Code Push)
   ↓
Jenkins Pipeline
   ↓
Maven Build
   ↓
Docker Image Build
   ↓
Push to AWS ECR
   ↓
Update Deployment in EKS
   ↓
Kubernetes Rolling Update
```

---

# Jenkins Pipeline Stages

The Jenkins pipeline is defined using a `Jenkinsfile`.

Key stages include:

### 1. Checkout Source Code

Jenkins pulls the latest code from the repository.

```bash
checkout scm
```

---

### 2. Build Application

The application is compiled using Maven.

```bash
mvn clean package -DskipTests
```

This produces a runnable **Spring Boot JAR file**.

---

### 3. Build Docker Image

A Docker image is created using the project Dockerfile.

```bash
docker build \
  --platform linux/amd64 \
  -t foundations-app:${BUILD_NUMBER} \
  -f 02-docker/Dockerfile .
```

The `BUILD_NUMBER` is used as the image tag to ensure versioned deployments.

---

### 4. Authenticate to AWS ECR

Jenkins authenticates with Amazon Elastic Container Registry.

```bash
aws ecr get-login-password --region us-east-1 | \
docker login --username AWS \
--password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
```

---

### 5. Push Image to ECR

The Docker image is tagged and pushed to the ECR repository.

```bash
docker tag foundations-app:${BUILD_NUMBER} \
<account-id>.dkr.ecr.us-east-1.amazonaws.com/bootcamp-app:${BUILD_NUMBER}

docker push \
<account-id>.dkr.ecr.us-east-1.amazonaws.com/bootcamp-app:${BUILD_NUMBER}
```

---

### 6. Update Kubernetes Deployment

Jenkins updates the running deployment using the new image.

```bash
kubectl set image deployment/bootcamp-app \
bootcamp-container=<account-id>.dkr.ecr.us-east-1.amazonaws.com/bootcamp-app:${BUILD_NUMBER} \
-n bootcamp
```

---

### 7. Monitor Deployment Rollout

Kubernetes performs a rolling update to replace the old pods with the new version.

```bash
kubectl rollout status deployment/bootcamp-app -n bootcamp
```

This ensures zero downtime during deployments.

---

# Rolling Deployment Process

When a new image is deployed:

1. Kubernetes creates a new pod with the updated image.
2. Readiness probes verify the new pod is healthy.
3. Traffic is gradually shifted to the new pod.
4. Old pods are terminated once the new version is ready.

This process prevents service interruption.

---

# Troubleshooting Issues Faced

### Docker Client Version Mismatch

Error:

```
client version 1.41 is too old
```

Fix:

Updated Docker client inside the Jenkins container to a newer version.

---

### ARM64 vs AMD64 Image Issue

Error:

```
no match for platform in manifest
```

Cause:

The Docker image was built on Apple Silicon (ARM64), while the Kubernetes nodes required AMD64 images.

Fix:

```bash
docker build --platform linux/amd64
```

---

### ECR Authentication Issues

Error:

```
Unable to locate credentials
```

Fix:

Configured AWS credentials in Jenkins using the **AWS Credentials Plugin**.

---

# Deployment Verification

Verify the deployment after the pipeline runs:

```bash
kubectl get pods -n bootcamp
kubectl get deployment -n bootcamp
kubectl describe deployment bootcamp-app -n bootcamp
```

Expected output:

```
bootcamp-app-xxx   Running
bootcamp-app-yyyyy   Running
```

---

# Outcome

At the end of this stage:

* Jenkins automatically builds the application
* Docker images are stored in AWS ECR
* Kubernetes deployments update automatically
* Rolling updates ensure zero downtime

This completes the **CI/CD automation for Kubernetes deployments**.
