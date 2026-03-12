# Troubleshooting Guide

This document lists real-world issues encountered during the implementation of this project and the steps taken to resolve them.

These troubleshooting scenarios reflect common challenges faced in real DevOps environments.

---

# 1. Docker Client Version Mismatch in Jenkins

## Problem

The Jenkins pipeline failed during the Docker build stage.

Error:

```
client version 1.41 is too old. Minimum supported API version is 1.44
```

## Cause

The Docker CLI installed inside the Jenkins container was outdated and incompatible with the Docker daemon version.

## Solution

Updated the Docker installation inside the Jenkins container by rebuilding the Jenkins custom image with an updated Docker client.

---

# 2. ARM64 vs AMD64 Image Compatibility

## Problem

Kubernetes pods failed to start with the following error:

```
no match for platform in manifest
```

## Cause

The Docker image was built on an Apple Silicon machine using the ARM64 architecture, while the Kubernetes worker nodes required AMD64 images.

## Solution

Force Docker to build the image for the AMD64 platform:

```
docker build --platform linux/amd64 -t foundations-app:ci .
```

---

# 3. AWS ECR Authentication Failure

## Problem

The Jenkins pipeline failed to push images to AWS ECR.

Error:

```
Unable to locate credentials
```

## Cause

AWS credentials were not configured correctly in the Jenkins pipeline environment.

## Solution

Configured AWS credentials in Jenkins using the **AWS Credentials Plugin** and referenced them in the pipeline using:

```
withCredentials([[
  $class: 'AmazonWebServicesCredentialsBinding',
  credentialsId: 'aws-bootcamp'
]])
```

---

# 4. Terraform State Accidentally Committed to Git

## Problem

GitHub rejected the repository push.

Error:

```
remote: File exceeds GitHub's file size limit of 100 MB
```

## Cause

Terraform provider binaries and state files were committed to the repository.

Examples:

```
terraform.tfstate
.terraform/
provider binaries
```

## Solution

Removed large files from Git history and added the following entries to `.gitignore`:

```
.terraform/
terraform.tfstate
terraform.tfstate.backup
target/
```

---

# 5. Node Exporter Port Conflict

## Problem

The Prometheus Node Exporter pod remained in **Pending** state.

Error:

```
0/1 nodes are available: node(s) didn't have free ports for the requested pod ports
```

## Cause

Two node-exporter DaemonSets were running simultaneously:

```
kube-system/node-exporter
monitoring/prometheus-node-exporter
```

Both attempted to bind to port **9100**, causing a scheduling conflict.

## Solution

Removed the duplicate DaemonSet:

```
kubectl delete daemonset node-exporter -n kube-system
```

After removal, the monitoring node-exporter pod scheduled successfully.

---

# 6. Grafana Showing No Metrics

## Problem

Grafana dashboards displayed the following values:

```
Cluster CPU Usage → N/A
Cluster Memory Usage → N/A
Cluster Pod Capacity → No data
```

## Cause

Prometheus was not scraping node metrics due to the node-exporter scheduling failure.

## Solution

After resolving the node-exporter conflict, Prometheus targets became healthy and Grafana dashboards began displaying metrics correctly.

---

# 7. Jenkins Container Unable to Access Docker

## Problem

Docker commands inside Jenkins failed.

Error:

```
failed to connect to docker API at unix:///var/run/docker.sock
```

## Cause

The Docker socket was not mounted inside the Jenkins container.

## Solution

Restart Jenkins with Docker socket mounted:

```
docker run -d \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins-custom
```

This allowed Jenkins to run Docker commands.

---

# Lessons Learned

During the development of this project several important DevOps lessons were reinforced:

* Always ensure container images match the architecture of the deployment environment.
* Avoid committing large build artifacts or infrastructure state files to Git.
* Monitoring components must not conflict with existing services.
* CI/CD pipelines require careful credential management.
* Observability tools are essential for diagnosing production issues.

---

# Summary

This troubleshooting experience reflects real operational challenges encountered when building production-grade DevOps systems.

Understanding how to diagnose and resolve these issues is a key skill for DevOps engineers.

