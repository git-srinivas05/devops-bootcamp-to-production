# 05 – Infrastructure Provisioning with Terraform (AWS + EKS)

## Objective

Provision production-grade infrastructure for Kubernetes deployment using Terraform.

Resources created:

* VPC
* Public & private subnets
* Internet Gateway
* NAT Gateway
* Security Groups
* EKS Cluster
* Managed Node Group
* IAM roles and policies

---

## Architecture Overview

Terraform → AWS API → VPC → EKS → Node Group

Terraform manages infrastructure declaratively via `.tf` configuration files.

---

## Terraform Workflow

### Initialize Providers

terraform init

Downloads provider plugins (not committed to Git).

---

### Validate Configuration

terraform validate

Ensures configuration syntax correctness.

---

### Plan Infrastructure

terraform plan

Displays execution plan without making changes.

---

### Apply Infrastructure

terraform apply

Creates AWS infrastructure resources.

---

### Destroy Infrastructure

terraform destroy

Removes provisioned resources.

---

## Issues Faced & Resolution

### 1.AWS Account Misconfiguration

Error:

InvalidClientTokenId

Root Cause:
AWS CLI configured with old account credentials.

Fix:
Reconfigured AWS CLI with correct profile.

Lesson:
Always verify active AWS account before provisioning.

---

### ⃣2.erraform State Committed to Git

Accidentally committed:

* `.terraform/`
* `terraform.tfstate`
* Provider binaries (~680MB)

Result:
GitHub rejected push (>100MB limit).

Fix:

* Removed files from Git history
* Added proper `.gitignore`
* Reinitialized repository cleanly

Lesson:
Never commit Terraform state or provider binaries.
Use remote backend in production (S3 + DynamoDB locking).

---

## Node IAM Role & ECR Access

Node group role includes:

AmazonEC2ContainerRegistryReadOnly

Required for pulling images from ECR.

Without it:
Pods fail with `ImagePullBackOff`.

---

## Infrastructure Verification

aws eks list-clusters
kubectl get nodes
kubectl get pods -A

Healthy indicators:

* Nodes in Ready state
* Cluster accessible via kubeconfig
* Security groups properly attached

---
## Outcome

Provisioned fully functional EKS cluster using Infrastructure as Code principles.

This stage demonstrates:

* Declarative infrastructure management
* Cloud networking fundamentals
* IAM integration
* Production troubleshooting

