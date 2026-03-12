# 06 – Observability (Prometheus + Grafana Monitoring)

This stage implements a complete monitoring stack for the Kubernetes cluster using **Prometheus and Grafana**.

Observability allows engineers to monitor system health, detect failures, and analyze resource usage in real time.

---

# Objective

Implement a monitoring solution capable of collecting and visualizing metrics from:

* Kubernetes nodes
* Kubernetes pods
* cluster components
* application workloads

---

# Monitoring Architecture

```text
Kubernetes Nodes
       ↓
Node Exporter
       ↓
Prometheus (Metrics Collection)
       ↓
Grafana (Visualization)
```

Additional components:

```text
kube-state-metrics → Kubernetes object metrics
Alertmanager       → Alert handling
```

---

# Components Used

## Prometheus

Prometheus is responsible for **collecting metrics** from various Kubernetes components.

Metrics sources include:

* node-exporter
* kube-state-metrics
* kubelet
* Kubernetes API server

Prometheus periodically scrapes metrics endpoints and stores them in its time-series database.

---

## Node Exporter

Node Exporter exposes **system-level metrics** from Kubernetes worker nodes.

Examples of metrics:

* CPU usage
* memory usage
* disk I/O
* network traffic

Example query:

```promql
node_cpu_seconds_total
```

---

## kube-state-metrics

This component exposes **Kubernetes object metrics**.

Examples:

* pod status
* deployment replicas
* node information
* container restart counts

Example query:

```promql
kube_pod_status_phase
```

---

## Grafana

Grafana is used to visualize metrics stored in Prometheus.

Dashboards provide insights into:

* node health
* cluster capacity
* pod resource usage
* deployment status

Grafana is exposed using a **LoadBalancer service**.

---

# Grafana Dashboards

Common dashboards used:

| Dashboard                     | ID   |
| ----------------------------- | ---- |
| Node Exporter Full            | 1860 |
| Kubernetes Cluster Monitoring | 315  |
| Kubernetes Pod Monitoring     | 6417 |

These dashboards display:

* CPU usage
* memory usage
* pod restart counts
* cluster capacity
* network traffic

---

# Example Metrics Queries

Node CPU usage:

```promql
sum(rate(node_cpu_seconds_total{mode!="idle"}[5m]))
```

Node memory availability:

```promql
node_memory_MemAvailable_bytes
```

Pod restart count:

```promql
kube_pod_container_status_restarts_total
```

---

# Troubleshooting Issues Faced

## Duplicate Node Exporter

Issue:

Two node-exporter DaemonSets were running.

```
kube-system/node-exporter
monitoring/prometheus-node-exporter
```

Result:

```
node(s) didn't have free ports for the requested pod ports
```

The second node-exporter pod remained **Pending**.

Fix:

Remove the duplicate exporter.

```bash
kubectl delete daemonset node-exporter -n kube-system
```

---

## Grafana Showing No Data

Symptoms:

Grafana dashboards displayed:

```
Cluster CPU Usage → N/A
Cluster Memory Usage → N/A
Cluster Pod Capacity → No data
```

Root cause:

Prometheus was not scraping node metrics due to the node-exporter conflict.

After fixing the DaemonSet issue, metrics appeared correctly.

---

# Prometheus Verification

Check Prometheus targets:

```
http://<prometheus-url>/targets
```

Healthy targets should display:

```
node-exporter → UP
kube-state-metrics → UP
kubelet → UP
```

---

# Useful Debug Commands

Check monitoring pods:

```bash
kubectl get pods -n monitoring
```

Check Prometheus targets:

```bash
kubectl port-forward svc/monitoring-kube-prometheus-prometheus 9090 -n monitoring
```

Verify metrics:

```bash
node_cpu_seconds_total
```

---

# Outcome

After completing this stage:

* Prometheus collects metrics from Kubernetes nodes and pods
* Grafana provides real-time dashboards
* cluster health can be monitored visually
* troubleshooting production issues becomes easier

Observability is a critical component of modern DevOps systems.

It enables engineers to detect problems early and maintain system reliability.
