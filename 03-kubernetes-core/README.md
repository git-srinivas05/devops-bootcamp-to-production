# 03 – Kubernetes Core Deployment

This stage focuses on deploying the containerized application into a Kubernetes cluster running on Amazon EKS.

It demonstrates how core Kubernetes resources are used to run, expose, and manage applications in a production environment.

---

# Objective

Deploy the Dockerized application into Kubernetes and implement core features such as:

* Namespace isolation
* Deployment configuration
* Readiness and liveness probes
* Resource management
* Service exposure

---

# Kubernetes Resources Used

## Namespace

A dedicated namespace was created to isolate the application resources.

```id="ns01"
apiVersion: v1
kind: Namespace
metadata:
  name: bootcamp
```

Namespaces help organize resources and prevent conflicts between applications.

---

# Deployment

The application is deployed using a **Kubernetes Deployment**.

Key responsibilities of the deployment:

* Maintain desired replica count
* Perform rolling updates
* Automatically restart failed pods

Example configuration:

```id="dep01"
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bootcamp-app
  namespace: bootcamp
spec:
  replicas: 2
```

This ensures **high availability** by running multiple pod replicas.

---

# Readiness Probe

Readiness probes ensure that a container only receives traffic when it is ready.

```id="probe01"
readinessProbe:
  httpGet:
    path: /health
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

If the readiness probe fails, Kubernetes removes the pod from the service endpoints.

This prevents users from hitting an application that has not fully started.

---

# Liveness Probe

Liveness probes detect when an application becomes unresponsive.

```id="probe02"
livenessProbe:
  httpGet:
    path: /health
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 10
```

If the probe fails repeatedly, Kubernetes automatically **restarts the container**.

---

# Resource Requests and Limits

Resource limits ensure pods do not consume excessive node resources.

```id="resources01"
resources:
  requests:
    cpu: "100m"
    memory: "128Mi"
  limits:
    cpu: "500m"
    memory: "512Mi"
```

This helps maintain cluster stability.

---

# Service

A Kubernetes Service exposes the application to external traffic.

```id="svc01"
apiVersion: v1
kind: Service
metadata:
  name: bootcamp-service
spec:
  type: LoadBalancer
```

Using **LoadBalancer** creates an AWS Elastic Load Balancer that routes traffic to the application pods.

---

# Deployment Verification

Verify resources using:

```id="cmd01"
kubectl get pods -n bootcamp
kubectl get deployment -n bootcamp
kubectl get svc -n bootcamp
```

Example output:

```id="cmd02"
bootcamp-app-xxxxx   Running
bootcamp-app-yyyyy   Running
```

---

# Common Errors Encountered

## ImagePullBackOff

Cause:

* Kubernetes cannot pull the container image from ECR.

Possible reasons:

* Incorrect repository name
* Authentication failure
* Missing IAM permissions

Fix:

```id="cmd03"
aws eks update-kubeconfig
```

Ensure worker nodes have **ECR access permissions**.

---

## CrashLoopBackOff

Cause:

* Application crashes repeatedly.

Debug using:

```id="cmd04"
kubectl logs <pod-name>
```

Common reasons include:

* incorrect environment variables
* application runtime errors
* port conflicts

---

## OOMKilled

Cause:

* Container exceeded memory limit.

Solution:

Increase memory limits:

```id="cmd05"
resources:
  limits:
    memory: "1Gi"
```

---

# Rolling Updates

When a new container image is deployed, Kubernetes performs a **rolling update**.

This ensures zero downtime by gradually replacing old pods with new ones.

Check rollout status:

```id="cmd06"
kubectl rollout status deployment bootcamp-app -n bootcamp
```

Rollback if needed:

```id="cmd07"
kubectl rollout undo deployment bootcamp-app -n bootcamp
```

---

# Outcome

At the end of this stage, the application runs successfully inside Kubernetes with:

* multiple replicas
* health checks
* resource limits
* external access through a LoadBalancer

This forms the foundation for implementing automated CI/CD deployments.

