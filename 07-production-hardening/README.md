# 07 – Production Hardening

This stage focuses on improving the reliability, scalability, and stability of the Kubernetes deployment.

In real production environments, applications must be able to **handle traffic spikes, recover from failures, and use resources efficiently**.

Production hardening introduces mechanisms that make the system resilient and scalable.

---

# Objectives

The goals of this stage include:

* automatic application scaling
* resource control
* deployment stability
* system reliability

---

# Horizontal Pod Autoscaler (HPA)

The Horizontal Pod Autoscaler automatically scales application pods based on resource utilization.

When CPU usage increases, Kubernetes creates additional pods to handle the load.

When traffic decreases, extra pods are removed.

Example configuration:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: bootcamp-app-hpa
  namespace: bootcamp
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: bootcamp-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 50
```

This configuration maintains **2–10 replicas depending on CPU load**.

---

# Metrics Server

The HPA requires metrics to determine scaling decisions.

Kubernetes obtains CPU usage metrics from the **metrics-server** component.

Verify metrics availability:

```bash
kubectl top nodes
kubectl top pods -n bootcamp
```

Example output:

```
bootcamp-app-xxxxx   20m CPU   90Mi Memory
```

---

# Resource Requests and Limits

Resource requests and limits prevent containers from consuming excessive cluster resources.

Example configuration:

```yaml
resources:
  requests:
    cpu: "100m"
    memory: "128Mi"
  limits:
    cpu: "500m"
    memory: "512Mi"
```

Benefits:

* prevents resource starvation
* improves cluster stability
* ensures fair scheduling

---

# Pod Disruption Budget

A PodDisruptionBudget ensures a minimum number of pods remain available during node maintenance or voluntary disruptions.

Example configuration:

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: bootcamp-pdb
  namespace: bootcamp
spec:
  minAvailable: 1
  selector:
    matchLabels:
      app: bootcamp-app
```

This guarantees at least **one pod remains available** during cluster updates.

---

# Deployment Reliability

Kubernetes provides built-in mechanisms to maintain application stability:

* rolling updates
* automatic pod restarts
* replica management
* health probes

Verify deployment status:

```bash
kubectl get deployment -n bootcamp
kubectl get pods -n bootcamp
kubectl get hpa -n bootcamp
```

---

# Load Testing (Optional)

To test autoscaling behavior, load generation tools can simulate traffic.

Examples:

* k6
* hey
* Apache Bench (ab)

Example test:

```bash
hey -z 30s http://<load-balancer-url>
```

During load testing:

* CPU usage increases
* HPA creates additional pods
* cluster handles traffic spikes

---

# Observability Integration

Autoscaling behavior can be observed using:

* Prometheus metrics
* Grafana dashboards

Useful metrics:

```
kube_deployment_status_replicas
container_cpu_usage_seconds_total
kube_pod_container_status_restarts_total
```

These metrics help track scaling behavior and resource usage.

---

# Final Architecture

After implementing production hardening, the system architecture becomes:

```
Developer
   ↓
GitHub
   ↓
Jenkins CI/CD
   ↓
Docker Build
   ↓
AWS ECR
   ↓
Amazon EKS
   ↓
Kubernetes Deployment
   ↓
Horizontal Pod Autoscaler
   ↓
Prometheus Monitoring
   ↓
Grafana Dashboards
```

---

# Outcome

After completing this stage, the platform supports:

* automated deployments
* real-time monitoring
* automatic scaling
* high availability
* production-grade reliability

This completes the **end-to-end DevOps workflow for a cloud-native application**.
