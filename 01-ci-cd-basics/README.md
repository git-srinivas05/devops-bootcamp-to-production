Stage 1 – CI/CD Basics (Jenkins)
Purpose of This Stage
This stage introduces Continuous Integration (CI) using Jenkins. The goal is to ensure that every code change is automatically built and tested before moving to packaging (Docker) or deployment.
This stage intentionally does NOT include Docker, Kubernetes, or Terraform to keep the focus on CI fundamentals.

What This Stage Demonstrates
 * Jenkins Pipeline as Code using a Jenkinsfile
 * Jenkins controller–agent architecture
 * Automated build and test execution
 * Fail-fast behavior on errors
 * Correct handling of repository root vs subdirectory
 * Debugging real Jenkins issues faced in practice

High-Level CI Flow
Developer Pushes Code
        ↓
Jenkins Pipeline Triggered
        ↓
Checkout Source Code
        ↓
Run Maven Build & Tests
        ↓
Success → Ready for Packaging
Failure → Pipeline Stops

How Jenkins Works Internally (Simplified)
  * Jenkins Controller Orchestrates pipelines, schedules jobs, manages credentials and plugins.
  * Jenkins Agent Executes pipeline steps (git clone, mvn build, tests).
  * Jenkinsfile Defines pipeline logic as version-controlled code.
    
Jenkins orchestrates builds. Build tools (Maven, Docker) must be explicitly configured.
  -v jenkins_home:/var/jenkins_home \
  
Jenkins Installation (Local – Docker Based)
 Step 1: Verify Docker is running
  docker ps
    Why Confirms Docker daemon is running. If this fails, Jenkins cannot start.

 Step 2: Start Jenkins container
docker run -d --name jenkins -p 8081:8080 -p 50000:50000 jenkins/jenkins:lts
Command breakdown
Commands                               	 Explanation
-d	                                     Run container in background
--name jenkins	                         Fixed container name
-p 8081:8080	                           Jenkins UI access
-p 50000:50000	                         Agent communication
-v jenkins_home:/var/jenkins_home	       Persistent Jenkins data
jenkins/jenkins:lts	                     Stable Jenkins version
Step 3: Unlock Jenkins
  docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   Why Jenkins generates a one-time password on first startup.
Access UI: http://localhost:8081

Common Docker Jenkins Errors & Fixes
   1.Docker daemon not running
      Cannot connect to the Docker daemon
       → Start Docker Desktop.
   2.Container name conflict
        Container name "/jenkins" is already in use
        docker start jenkins or docker rm -f jenkins 
   3.No initialAdminPassword
        Jenkins volume already exists
        docker rm -f jenkins
        docker volume rm jenkins_home
        
Jenkins Installation on EC2 (Interview-Ready)
   Step 1: Launch EC2
           * AMI: Amazon Linux 2
           * Instance type: t2.micro
           * Security Group:
           * SSH (22)
           * Jenkins (8080)
  Step 2: SSH into EC2
           * ssh -i mykey.pem ec2-user@<EC2_PUBLIC_IP>
Step 3: Update system
           * sudo yum update -y
Step 4: Install Java (Jenkins dependency)
           * sudo yum install -y java-17-amazon-corretto
                  java -version
Step 5: Add Jenkins repository
           * sudo wget -O /etc/yum.repos.d/jenkins.repo \
           * https://pkg.jenkins.io/redhat-stable/jenkins.repo
Step 6: Import Jenkins GPG key
           * sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
Step 7: Install Jenkins
           * sudo yum install -y jenkins
Step 8: Start Jenkins
           * sudo systemctl start jenkins
           * sudo systemctl enable jenkins
           * sudo systemctl status jenkins
Step 9: Unlock Jenkins
           * sudo cat /var/lib/jenkins/secrets/initialAdminPassword
              Open: http://<EC2_PUBLIC_IP>:8080
              

Jenkins Pipeline Configuration
Jenkinsfile Location
01-ci-cd-basics/Jenkinsfile

Jenkinsfile (CI Pipeline)
pipeline {
    agent any

    tools {
        maven 'maven-3'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                dir('00-foundations/app') {
                    sh 'mvn clean package'
                }
            }
        }
    }

    post {
        success {
            echo 'Build successful'
        }
        failure {
            echo 'Build failed'
        }
    }
}
Why this works
* Jenkins clones repo root
* Jenkinsfile is resolved via script path
* Maven is installed automatically
* Tests run as part of build


How the Pipeline Is Triggered
   The pipeline is triggered manually during development.In real environments, this would typically be triggered via GitHub webhooks on every       push or pull request.


Issues Faced and Lessons Learned
1. Maven Not Found
     mvn: not found
     Cause Jenkins agent does not include build tools.
     Fix Configured Maven in Jenkins tools.
     Lesson Jenkins orchestrates tools; it does not bundle them.

2. Wrong Repository URL
     repository not found
     Cause Subdirectory used as repo URL.
     Fix Set repo URL to root and Jenkinsfile path separately.
     Lesson Jenkins always clones the repository root.

3. Wrong Execution Context
     Cause Maven executed from wrong directory.
     Fix Explicit dir('00-foundations/app').
     Lesson Most CI failures are context issues, not code issues.

Why CI Comes Before Docker
  * CI validates code correctness
  * Docker ensures runtime consistency
  * Mixing them early hides failures
  * 
Correct order: App → CI → Docker → Deployment

Outcome of This Stage
    * Automated build on every change
    * Tests executed consistently
    * Fail-fast CI pipeline
    * Codebase ready for containerization

Design Summary
    This CI setup enforces early validation of application correctness.By separating build and test automation from packaging and deployment,         failures are detected quickly and debugging remains simple.This foundation allows Docker and deployment stages to be added without               weakening feedback loops.
