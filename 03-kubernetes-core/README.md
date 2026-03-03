# 03 – Kubernetes Core Deployment (Amazon EKS)

## Objective

Deploy the Spring Boot application to Amazon EKS using production-grade Kubernetes practices:

* Namespace isolation
* Deployment with readiness & liveness probes
* Resource requests & limits
* LoadBalancer exposure
* Structured debugging & verification

---

## Architecture Flow

```
Jenkins → ECR → EKS → LoadBalancer → Public Access
```

CI builds the Docker image and pushes it to ECR.
EKS nodes pull the image using their IAM role and run it inside the cluster.

---

## Namespace Isolation

kubectl apply -f namespace.yaml

All resources are deployed inside:
bootcamp

This avoids polluting the `default` namespace and allows better RBAC, quotas, and isolation.

---

## Deployment Design

Key configurations:

* 2 replicas
* Resource requests & limits
* Readiness probe
* Liveness probe
* Explicit imagePullPolicy

Example:

yaml
resources:
  requests:
    cpu: "200m"
    memory: "256Mi"
  limits:
    cpu: "500m"
    memory: "512Mi"
Why?

* Prevents resource starvation
* Ensures scheduler placement
* Avoids noisy-neighbor issues

---

## Readiness vs Liveness

### Readiness Probe
Determines if pod can receive traffic.

If it fails:
* Pod removed from Service endpoints
* Container NOT restarted

Used for:
* Startup delay
* Dependency validation (DB, external APIs)

---

### Liveness Probe
Determines if container must be restarted.

If it fails:
* Kubernetes restarts container

Used for:
* Deadlocks
* Stuck threads
* Frozen application

---

## Service Exposure

yaml
type: LodBalancer

EKS provisions AWS ELB automatically.
Verify:
kubectl get svc -n bootcamp

---

# Issues Faced & Production Debugging

## 1.ImagePullBackOff – Wrong IAM Role

Symptom:
ImagePullBackOff

Root Cause:
Node IAM role missing:
AmazonEC2ContainerRegistryReadOnly

Fix:
Attach required policy to node group IAM role.

Lesson:
EKS nodes authenticate to ECR using IAM — not Docker credentials.

---

## 2.ImagePullBackOff – Architecture Mismatch
Error:
no match for platform in manifest

Root Cause:
Image built on Apple Silicon (arm64),
EKS nodes running amd64.

Fix:
Updated build command:

docker build --platform linux/amd64

Lesson:
Always align image architecture with cluster architecture.

---

## 3.CrashLoopBackOff
Symptom:
CrashLoopBackOff

Meaning:
Container starts → crashes → restarts repeatedly.

Debug Steps:

kubectl logs <pod-name> -n bootcamp
kubectl describe pod <pod-name> -n bootcamp

Common Causes:
* Wrong application port
* Unhandled exception
* Missing environment variables
* Dependency connection failure

Lesson:
CrashLoopBackOff indicates application-level failure, not image pull issue.

---

## 4.OOMKilled (Out of Memory)

Symptom:
Reason: OOMKilled

Cause:
Container exceeded memory limit.

Fix:
* Increase memory limit
* Optimize application memory usage

Example:
yaml
limits:
  memory: "512Mi"

Lesson:
Always define resource limits to prevent node instability.

---

## 5.Whitelabel Error Page (404)

Symptom:
Application accessible but returns 404.

Root Cause:
No mapping for `/`.

Infrastructure was healthy; issue was application routing.

Lesson:
Differentiate infrastructure failure from application logic issues.

# Verification Commands

kubectl get pods -n bootcamp
kubectl describe pod <pod>
kubectl logs <pod>
kubectl get svc -n bootcamp
kubectl get endpoints -n bootcamp
kubectl get nodes

Healthy Indicators:
* Pods in `Running`
* READY 1/1
* No restart loops
* LoadBalancer accessible
* Endpoints populated

---

# Outcome

Successfully deployed containerized application to EKS with:

* Proper IAM integration
* Architecture compatibility validation
* Health probes
* Resource governance
* Structured production debugging

This stage demonstrates strong understanding of Kubernetes runtime behavior, failure analysis, and cloud-native troubleshooting.
