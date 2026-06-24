# 055_GitHub_Actions_CI_CD.md
# MiniCloud / MiniDevOps — GitHub Actions CI/CD

> Core mental model: **GitHub Actions CI/CD is an automated factory line for your code. Every commit enters the factory, gets checked, tested, packaged, scanned, and safely delivered to an environment.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. CI vs CD](#4-ci-vs-cd)
- [5. GitHub Actions Building Blocks](#5-github-actions-building-blocks)
- [6. Workflow Mental Model](#6-workflow-mental-model)
- [7. Spring Boot CI Pipeline](#7-spring-boot-ci-pipeline)
- [8. Docker Image Build Pipeline](#8-docker-image-build-pipeline)
- [9. Docker Registry Push](#9-docker-registry-push)
- [10. Deployment Strategy Overview](#10-deployment-strategy-overview)
- [11. EKS Deployment Pipeline](#11-eks-deployment-pipeline)
- [12. Environment Separation](#12-environment-separation)
- [13. Secrets And Variables](#13-secrets-and-variables)
- [14. Branch Strategy](#14-branch-strategy)
- [15. Pull Request Checks](#15-pull-request-checks)
- [16. Release Tag Pipeline](#16-release-tag-pipeline)
- [17. Rollback Mental Model](#17-rollback-mental-model)
- [18. Caching And Speed](#18-caching-and-speed)
- [19. Security Scanning](#19-security-scanning)
- [20. Observability In CI/CD](#20-observability-in-cicd)
- [21. Full GitHub Actions YAML](#21-full-github-actions-yaml)
- [22. Step-by-Step Dry Runs](#22-step-by-step-dry-runs)
- [23. Internal Execution Walkthrough](#23-internal-execution-walkthrough)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. Interview-Ready Explanation](#27-interview-ready-explanation)
- [28. Senior Engineer Checklist](#28-senior-engineer-checklist)
- [29. One-Page Cheat Sheet](#29-one-page-cheat-sheet)
- [30. One Picture To Remember](#30-one-picture-to-remember)

---

## 1. Why This Exists

In a production backend project, writing code is not enough.

You also need a safe path from:

```text
Developer laptop
      |
      v
Git commit
      |
      v
Build
      |
      v
Test
      |
      v
Docker image
      |
      v
Registry
      |
      v
Kubernetes / EKS
      |
      v
Running production service
```

Without CI/CD, deployment becomes manual and risky.

Manual deployment usually means:

```text
1. Developer builds locally.
2. Developer runs tests manually.
3. Developer builds Docker image manually.
4. Developer pushes image manually.
5. Developer connects to server manually.
6. Developer runs kubectl manually.
7. Something fails.
8. Nobody knows exactly what changed.
```

This creates production problems:

```text
Works on my machine.
Wrong image deployed.
Tests skipped.
Secrets leaked.
No rollback plan.
No audit trail.
Deployment depends on one person.
Production differs from staging.
```

GitHub Actions solves this by turning delivery into a repeatable pipeline.

For your Spring Boot + Docker + Kubernetes + EKS projects, GitHub Actions becomes the bridge between code and cloud.

Senior engineer memory:

```text
CI/CD is not only automation.
CI/CD is risk control.
```

---

## 2. The One Core Mental Model

GitHub Actions is an:

```text
AUTOMATED DELIVERY FACTORY
```

Your code enters as raw material.

The workflow checks it step by step.

ASCII:

```text
                 CODE DELIVERY FACTORY

Developer
  |
  | git push
  v
+-------------------+
| GitHub Repository |
+-------------------+
  |
  | event: push / pull_request / tag
  v
+-------------------+
| GitHub Actions    |
| Workflow starts   |
+-------------------+
  |
  v
+-------------------+
| Checkout Code     |
+-------------------+
  |
  v
+-------------------+
| Setup Java        |
+-------------------+
  |
  v
+-------------------+
| Build + Test      |
+-------------------+
  |
  v
+-------------------+
| Build Docker      |
+-------------------+
  |
  v
+-------------------+
| Push Image        |
+-------------------+
  |
  v
+-------------------+
| Deploy to EKS     |
+-------------------+
  |
  v
Running Application
```

One-line memory:

```text
GitHub Actions turns every code change into a controlled delivery path.
```

The most important idea:

```text
Humans decide intent.
Pipeline performs repeatable execution.
```

---

## 3. Problem Statement

Build a production-shaped CI/CD pipeline for a Spring Boot backend.

It should support:

```text
1. Pull request validation.
2. Maven build.
3. Unit tests.
4. Integration tests if available.
5. Docker image build.
6. Image push to registry.
7. Kubernetes deployment.
8. Separate staging and production environments.
9. Safe secrets management.
10. Rollback-friendly image tags.
```

For MiniURLShortener / MiniCloud project, the target flow is:

```text
Pull Request:
    compile
    test
    validate Docker build

Merge to main:
    compile
    test
    build Docker image
    push image
    deploy to staging

Release tag:
    build image
    push versioned image
    deploy to production
```

Out of scope for this chapter:

```text
1. Full ArgoCD deep dive.
2. Full Terraform setup.
3. Multi-account AWS design.
4. Blue-green deployment implementation.
5. Canary release controller.
```

But you will understand the foundation needed for those.

---

## 4. CI vs CD

CI means:

```text
Continuous Integration
```

It checks whether new code safely integrates with the existing codebase.

CI answers:

```text
Does it compile?
Do tests pass?
Does formatting/static analysis pass?
Does the Docker image build?
```

CD means:

```text
Continuous Delivery / Continuous Deployment
```

It moves verified code toward an environment.

CD answers:

```text
Can this artifact be released?
Can this image be deployed?
Can Kubernetes run the new version?
Can we rollback if it breaks?
```

ASCII:

```text
CI
Code ---> Build ---> Test ---> Package

CD
Package ---> Registry ---> Deploy ---> Verify
```

Simple distinction:

```text
CI protects code quality.
CD protects delivery quality.
```

For interviews:

```text
CI is before artifact confidence.
CD is after artifact confidence.
```

---

## 5. GitHub Actions Building Blocks

GitHub Actions has a few core objects.

```text
Workflow
Event
Job
Runner
Step
Action
Secret
Environment
Artifact
Cache
```

ASCII:

```text
.github/workflows/ci-cd.yml
        |
        v
+-------------------+
| Workflow          |
+-------------------+
        |
        +-- triggered by event
        |
        v
+-------------------+
| Jobs              |
| build, test, deploy
+-------------------+
        |
        v
+-------------------+
| Steps             |
| checkout, mvn test|
+-------------------+
        |
        v
+-------------------+
| Runner            |
| temporary machine |
+-------------------+
```

Meaning:

```text
Workflow: YAML file that defines automation.
Event: Something that starts workflow.
Job: Group of steps running on a runner.
Runner: Machine executing the job.
Step: One command or one reusable action.
Action: Reusable packaged step.
Secret: Encrypted sensitive value.
Environment: Target like staging/prod with approvals.
Artifact: File produced by workflow.
Cache: Reused dependencies for speed.
```

Example event:

```yaml
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
```

Example job:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: mvn test
```

---

## 6. Workflow Mental Model

A workflow is a directed execution graph.

Simple graph:

```text
build
  |
  v
test
  |
  v
docker
  |
  v
deploy
```

In GitHub Actions, jobs run in parallel by default unless you connect them using `needs`.

Wrong mental model:

```text
Jobs always run one after another.
```

Correct mental model:

```text
Jobs are independent unless dependencies are declared.
```

Example:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest

  deploy:
    needs: build
    runs-on: ubuntu-latest
```

ASCII:

```text
Without needs:

build  ---- runs

deploy ---- runs at same time

With needs:

build ---- success ----> deploy
```

Production rule:

```text
Deploy must depend on successful build and tests.
```

---

## 7. Spring Boot CI Pipeline

For Spring Boot, the basic CI path is:

```text
checkout code
setup JDK
cache Maven dependencies
run mvn clean verify
upload test reports if needed
```

ASCII:

```text
Pull Request
   |
   v
+----------------+
| Checkout Code  |
+----------------+
   |
   v
+----------------+
| Setup Java 21  |
+----------------+
   |
   v
+----------------+
| Maven Cache    |
+----------------+
   |
   v
+----------------+
| mvn verify     |
+----------------+
   |
   +-- pass --> PR can merge
   |
   +-- fail --> PR blocked
```

Example:

```yaml
name: CI

on:
  pull_request:
    branches: [ main ]

jobs:
  build-test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven

      - name: Build and test
        run: mvn clean verify
```

Why `verify` instead of only `test`?

```text
mvn test runs unit tests.
mvn verify runs more lifecycle checks, including integration tests if configured.
```

Good default:

```text
Use mvn clean verify for serious CI.
```

---

## 8. Docker Image Build Pipeline

After tests pass, build a Docker image.

Docker image is the deployable artifact.

ASCII:

```text
Source Code
   |
   v
Maven Build
   |
   v
JAR file
   |
   v
Docker Build
   |
   v
Container Image
   |
   v
Registry
```

Example Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Better production Dockerfile can use multi-stage build:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

CI note:

```text
If GitHub Actions already runs mvn verify, Dockerfile can copy target/*.jar.
If Dockerfile builds inside image, image build is slower but more self-contained.
```

---

## 9. Docker Registry Push

A Docker registry stores images.

Common registries:

```text
GitHub Container Registry: ghcr.io
Docker Hub
AWS Elastic Container Registry: ECR
```

For EKS, AWS ECR is common.

ASCII:

```text
GitHub Actions Runner
        |
        | docker build
        v
Local image on runner
        |
        | docker push
        v
AWS ECR / GHCR
        |
        v
Kubernetes pulls image
```

Image tag strategy matters.

Bad:

```text
latest
```

Why bad?

```text
latest does not tell which commit is running.
Rollback is confusing.
Kubernetes may not pull what you expect.
```

Better:

```text
shortener-api:sha-8f3a91c
shortener-api:v1.2.0
shortener-api:2026-06-24-1430
```

Best practical approach:

```text
Use commit SHA for every build.
Use release tag for production release.
```

---

## 10. Deployment Strategy Overview

Deployment means updating the running environment to use the new artifact.

Kubernetes deployment update:

```text
new image tag
   |
   v
kubectl set image / kubectl apply
   |
   v
Deployment creates new ReplicaSet
   |
   v
new Pods start
   |
   v
readiness probes pass
   |
   v
old Pods terminate gradually
```

ASCII:

```text
Before:

Service ---> Pod v1
        ---> Pod v1
        ---> Pod v1

Deploy v2:

Service ---> Pod v1
        ---> Pod v1
        ---> Pod v2 starting
        ---> Pod v2 ready
        ---> Pod v1 terminating

After:

Service ---> Pod v2
        ---> Pod v2
        ---> Pod v2
```

CI/CD should not only apply YAML.

It should also verify rollout:

```bash
kubectl rollout status deployment/shortener-api -n miniurl
```

Without rollout verification, pipeline may say success while pods are crashing.

---

## 11. EKS Deployment Pipeline

For EKS, the deployment job needs:

```text
AWS credentials or OIDC role
EKS cluster name
AWS region
kubectl
kubeconfig update
Kubernetes manifests
image tag
```

ASCII:

```text
GitHub Actions
   |
   v
Assume AWS Role
   |
   v
Login to ECR
   |
   v
Push Docker Image
   |
   v
Update kubeconfig for EKS
   |
   v
kubectl apply / set image
   |
   v
rollout status
```

Deployment command example:

```bash
aws eks update-kubeconfig \
  --region eu-central-1 \
  --name miniurl-eks

kubectl set image deployment/shortener-api \
  shortener-api=$ECR_REGISTRY/shortener-api:$IMAGE_TAG \
  -n miniurl

kubectl rollout status deployment/shortener-api -n miniurl
```

Production note:

```text
For serious teams, prefer GitOps with ArgoCD/Flux.
For learning and early production, direct GitHub Actions deploy is acceptable.
```

---

## 12. Environment Separation

Do not treat all environments the same.

Common environments:

```text
local
ci
dev
staging
production
```

ASCII:

```text
Pull Request ----> CI only

main merge ------> staging deploy

tag v1.2.0 -----> production deploy
```

Environment table:

```text
+-------------+----------------------+----------------------+
| Environment | Trigger              | Purpose              |
+-------------+----------------------+----------------------+
| CI          | pull_request         | verify code          |
| Staging     | push to main         | test deployed system |
| Production  | release tag/manual   | serve real users     |
+-------------+----------------------+----------------------+
```

GitHub environments can protect production.

Example:

```yaml
environment: production
```

You can configure production environment approvals in GitHub UI.

Mental model:

```text
Staging should be automatic.
Production should be deliberate.
```

---

## 13. Secrets And Variables

CI/CD needs sensitive values:

```text
AWS role ARN
ECR repository
cluster name
namespace
registry token
```

Never hardcode secrets in YAML.

Wrong:

```yaml
AWS_SECRET_ACCESS_KEY: abc123
```

Correct:

```yaml
env:
  AWS_REGION: ${{ vars.AWS_REGION }}
```

Use:

```text
GitHub Secrets for sensitive data.
GitHub Variables for non-sensitive config.
```

ASCII:

```text
GitHub Repository Settings
      |
      +-- Secrets: encrypted sensitive values
      |
      +-- Variables: normal config values
      |
      v
Workflow reads them at runtime
```

Better AWS security:

```text
Use GitHub OIDC to assume AWS IAM role.
Avoid long-lived AWS access keys.
```

Why?

```text
Long-lived keys can leak.
OIDC creates short-lived credentials for the workflow.
```

---

## 14. Branch Strategy

A simple branch strategy:

```text
feature/* -> pull request -> main -> staging
release tag -> production
```

ASCII:

```text
feature/login
     |
     | pull request
     v
main
     |
     | auto deploy
     v
staging
     |
     | tag v1.0.0
     v
production
```

For your projects, this is enough.

Avoid overcomplicating early with:

```text
develop branch
release branch
hotfix branch
long-lived environment branches
```

Simple rule:

```text
Use trunk-based development unless team complexity forces otherwise.
```

Production safety comes from:

```text
PR checks
small changes
feature flags
staging
rollback
monitoring
```

Not from complicated branching alone.

---

## 15. Pull Request Checks

Pull request checks protect main branch.

For backend project, PR should run:

```text
compile
tests
static checks if available
Docker build smoke check
```

ASCII:

```text
Developer opens PR
       |
       v
CI checks run
       |
       +-- fail --> cannot merge
       |
       +-- pass --> review + merge
```

Useful branch protection rules:

```text
Require status checks to pass.
Require pull request review.
Require branch up to date before merge.
Disallow direct push to main.
```

Senior mindset:

```text
Main branch should always be deployable.
```

---

## 16. Release Tag Pipeline

Production deployment should use a stable version.

Example tag:

```text
v1.0.0
v1.1.0
v2.0.0
```

Trigger:

```yaml
on:
  push:
    tags:
      - 'v*.*.*'
```

ASCII:

```text
main commit
   |
   | git tag v1.2.0
   | git push origin v1.2.0
   v
Production workflow starts
   |
   v
build image shortener-api:v1.2.0
   |
   v
deploy production
```

Why tags are useful:

```text
Clear release identity.
Easy rollback target.
Good audit trail.
Matches changelog/release notes.
```

Rollback example:

```bash
kubectl set image deployment/shortener-api \
  shortener-api=repo/shortener-api:v1.1.0 \
  -n miniurl
```

---

## 17. Rollback Mental Model

Rollback means returning to a known good version.

Do not think rollback starts after failure.

Rollback starts when you design the pipeline.

You need:

```text
immutable image tags
previous version known
Kubernetes rollout history
database migration safety
monitoring alerts
```

ASCII:

```text
Deploy v2
   |
   v
Health checks fail
   |
   v
Rollback to v1
   |
   v
Service stable again
```

Kubernetes rollback:

```bash
kubectl rollout undo deployment/shortener-api -n miniurl
```

But be careful:

```text
Application rollback is easy.
Database rollback is hard.
```

Migration rule:

```text
Prefer backward-compatible database migrations.
```

Example safe migration:

```text
1. Add nullable column.
2. Deploy app that writes both old and new.
3. Backfill data.
4. Deploy app reading new.
5. Later remove old column.
```

---

## 18. Caching And Speed

CI should be fast enough that developers do not ignore it.

Common caches:

```text
Maven dependencies
Docker build layers
Node modules if frontend exists
```

Maven cache:

```yaml
- name: Set up Java
  uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: '21'
    cache: maven
```

Docker Buildx cache:

```yaml
cache-from: type=gha
cache-to: type=gha,mode=max
```

ASCII:

```text
First CI run:
    download dependencies -> slow

Next CI run:
    restore cache -> faster
```

Warning:

```text
Do not sacrifice correctness for speed.
```

Bad optimization:

```text
skip tests always
```

Good optimization:

```text
cache dependencies
parallelize independent jobs
run heavy tests only when needed
```

---

## 19. Security Scanning

CI/CD is a security boundary.

Add checks for:

```text
secret leaks
dependency vulnerabilities
container image vulnerabilities
IaC/Kubernetes manifest issues
```

Common tools:

```text
GitHub secret scanning
Dependabot
Trivy
CodeQL
Checkov
```

Example Trivy image scan:

```yaml
- name: Run Trivy vulnerability scanner
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: ${{ env.IMAGE_URI }}
    format: table
    exit-code: '1'
    severity: CRITICAL,HIGH
```

ASCII:

```text
Docker Image
    |
    v
Security Scan
    |
    +-- critical vuln --> block deploy
    |
    +-- clean enough ---> deploy
```

Senior mindset:

```text
CI/CD should prevent known bad artifacts from reaching production.
```

---

## 20. Observability In CI/CD

A pipeline should answer:

```text
What commit deployed?
Who triggered it?
Which image tag is running?
Did rollout finish?
How long did deployment take?
What failed?
```

Useful outputs:

```text
commit SHA
image URI
environment
namespace
deployment status
kubectl rollout status
```

Deployment annotation idea:

```bash
kubectl annotate deployment shortener-api \
  deployment.kubernetes.io/revision-commit=$GITHUB_SHA \
  -n miniurl --overwrite
```

ASCII:

```text
Pipeline logs
   |
   v
Deployment metadata
   |
   v
Kubernetes state
   |
   v
Production debugging
```

Rule:

```text
A deployment without traceability is a future debugging problem.
```

---

## 21. Full GitHub Actions YAML

Example production-shaped pipeline:

```yaml
name: CI-CD

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]
    tags:
      - 'v*.*.*'

permissions:
  id-token: write
  contents: read
  packages: write

env:
  AWS_REGION: eu-central-1
  ECR_REPOSITORY: shortener-api
  EKS_CLUSTER_NAME: miniurl-eks
  K8S_NAMESPACE: miniurl
  APP_NAME: shortener-api

jobs:
  build-test:
    name: Build and Test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven

      - name: Build and test
        run: mvn clean verify

  docker-build:
    name: Docker Build
    runs-on: ubuntu-latest
    needs: build-test
    if: github.event_name == 'push'

    outputs:
      image_tag: ${{ steps.meta.outputs.image_tag }}

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set image tag
        id: meta
        run: |
          if [[ "${GITHUB_REF}" == refs/tags/* ]]; then
            echo "image_tag=${GITHUB_REF_NAME}" >> $GITHUB_OUTPUT
          else
            echo "image_tag=sha-${GITHUB_SHA::7}" >> $GITHUB_OUTPUT
          fi

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_GITHUB_ACTIONS_ROLE_ARN }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build and push Docker image
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ steps.meta.outputs.image_tag }}
        run: |
          IMAGE_URI=$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
          docker build -t $IMAGE_URI .
          docker push $IMAGE_URI

  deploy-staging:
    name: Deploy Staging
    runs-on: ubuntu-latest
    needs: docker-build
    if: github.ref == 'refs/heads/main'
    environment: staging

    steps:
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_GITHUB_ACTIONS_ROLE_ARN }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Update kubeconfig
        run: |
          aws eks update-kubeconfig \
            --region $AWS_REGION \
            --name $EKS_CLUSTER_NAME

      - name: Deploy image to staging
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ needs.docker-build.outputs.image_tag }}
        run: |
          IMAGE_URI=$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
          kubectl set image deployment/$APP_NAME \
            $APP_NAME=$IMAGE_URI \
            -n $K8S_NAMESPACE
          kubectl rollout status deployment/$APP_NAME -n $K8S_NAMESPACE

  deploy-production:
    name: Deploy Production
    runs-on: ubuntu-latest
    needs: docker-build
    if: startsWith(github.ref, 'refs/tags/v')
    environment: production

    steps:
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: ${{ secrets.AWS_GITHUB_ACTIONS_ROLE_ARN }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Update kubeconfig
        run: |
          aws eks update-kubeconfig \
            --region $AWS_REGION \
            --name $EKS_CLUSTER_NAME

      - name: Deploy image to production
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ needs.docker-build.outputs.image_tag }}
        run: |
          IMAGE_URI=$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
          kubectl set image deployment/$APP_NAME \
            $APP_NAME=$IMAGE_URI \
            -n $K8S_NAMESPACE
          kubectl rollout status deployment/$APP_NAME -n $K8S_NAMESPACE
```

Key things this YAML does:

```text
1. Pull requests run build and test only.
2. Push to main builds and pushes image.
3. Main deploys to staging.
4. Version tags deploy to production.
5. AWS uses OIDC role instead of static keys.
6. Kubernetes rollout is verified.
7. Image tags are immutable SHA/tag based.
```

---

## 22. Step-by-Step Dry Runs

### Dry Run 1: Pull Request

Event:

```text
Developer opens PR to main.
```

Flow:

```text
1. GitHub detects pull_request event.
2. Workflow starts.
3. build-test job runs.
4. Runner checks out code.
5. Java 21 is installed.
6. Maven dependencies restored from cache.
7. mvn clean verify runs.
8. If tests pass, PR check is green.
9. docker-build is skipped because event is not push.
10. No deployment happens.
```

ASCII:

```text
PR ---> build-test ---> pass/fail
              |
              v
        no deployment
```

Why good?

```text
Unmerged code should not deploy automatically.
```

---

### Dry Run 2: Merge to Main

Event:

```text
PR merged into main.
```

Flow:

```text
1. GitHub detects push to main.
2. build-test runs.
3. docker-build runs after build-test.
4. Image tag becomes sha-xxxxxxx.
5. GitHub assumes AWS role.
6. Docker image is pushed to ECR.
7. deploy-staging runs.
8. kubeconfig is updated.
9. Kubernetes Deployment image is changed.
10. kubectl rollout status waits for healthy pods.
```

ASCII:

```text
main push
   |
   v
build-test
   |
   v
docker-build + push ECR
   |
   v
deploy staging
   |
   v
rollout status
```

---

### Dry Run 3: Production Release Tag

Event:

```text
git tag v1.2.0
git push origin v1.2.0
```

Flow:

```text
1. GitHub detects tag push.
2. build-test runs.
3. docker-build creates image tag v1.2.0.
4. Image pushed to ECR.
5. deploy-production runs.
6. GitHub environment protection may require approval.
7. Pipeline updates Kubernetes deployment.
8. Rollout status confirms success.
```

ASCII:

```text
tag v1.2.0
    |
    v
build-test
    |
    v
image:v1.2.0
    |
    v
production approval
    |
    v
deploy production
```

---

### Dry Run 4: Test Failure

Problem:

```text
A unit test fails.
```

Flow:

```text
1. build-test runs mvn clean verify.
2. Maven returns non-zero exit code.
3. GitHub marks job failed.
4. docker-build does not run because needs build-test.
5. No image is pushed.
6. No deployment happens.
```

Memory:

```text
CI failure should stop delivery early.
```

---

### Dry Run 5: Kubernetes Rollout Failure

Problem:

```text
New pod crashes because DB password is wrong.
```

Flow:

```text
1. Image is pushed successfully.
2. Deployment image is updated.
3. New ReplicaSet creates pod.
4. Pod enters CrashLoopBackOff.
5. Readiness never passes.
6. kubectl rollout status eventually fails.
7. Pipeline marks deployment failed.
8. Engineer checks kubectl describe/logs.
9. Rollback to previous version if needed.
```

Important:

```text
Docker push success does not mean deployment success.
```

---

## 23. Internal Execution Walkthrough

What happens inside GitHub Actions?

```text
1. GitHub receives event.
2. It matches event against workflow YAML.
3. It creates workflow run.
4. It schedules jobs on runners.
5. Runner downloads repository.
6. Runner executes steps one by one.
7. Each shell command has an exit code.
8. Non-zero exit code fails the step.
9. Failed step fails the job unless configured otherwise.
10. Dependent jobs are skipped if needed job fails.
11. Logs and artifacts are stored.
```

ASCII:

```text
GitHub Event
    |
    v
Workflow Matcher
    |
    v
Job Scheduler
    |
    v
Runner Machine
    |
    v
Step 1 -> Step 2 -> Step 3
    |       |        |
    |       |        +-- exit code
    |       +----------- exit code
    +------------------- exit code
```

Exit code rule:

```text
0 means success.
Non-zero means failure.
```

This is why:

```bash
mvn clean verify
```

can block the whole pipeline.

---

## 24. Production Failure Stories

### Failure Story 1: latest tag deployed wrong version

Team used:

```text
image: shortener-api:latest
```

Production showed unexpected behavior.

Nobody knew which commit was running.

Root cause:

```text
Mutable latest tag.
```

Fix:

```text
Use immutable commit SHA and release tags.
```

Lesson:

```text
Traceable image tags are mandatory.
```

---

### Failure Story 2: Tests passed locally but failed in CI

Developer used Java 21 locally.

CI used Java 17.

Build failed.

Root cause:

```text
CI runtime did not match project runtime.
```

Fix:

```yaml
java-version: '21'
```

Lesson:

```text
CI environment must match application requirements.
```

---

### Failure Story 3: Pipeline succeeded but pods crashed

Pipeline only ran:

```bash
kubectl apply -f deployment.yaml
```

GitHub showed success.

Pods were in CrashLoopBackOff.

Root cause:

```text
No rollout status verification.
```

Fix:

```bash
kubectl rollout status deployment/shortener-api -n miniurl
```

Lesson:

```text
Deployment command success is not application success.
```

---

### Failure Story 4: AWS keys leaked

Long-lived AWS keys were stored in a developer laptop and copied into YAML accidentally.

Root cause:

```text
Static credentials and poor secret hygiene.
```

Fix:

```text
Use GitHub secrets and AWS OIDC role assumption.
```

Lesson:

```text
CI/CD credentials should be short-lived and least privilege.
```

---

### Failure Story 5: Database migration broke rollback

New application version required a renamed database column.

Deployment failed.

Rollback to old app also failed because old column was gone.

Root cause:

```text
Non-backward-compatible migration.
```

Fix:

```text
Use expand-contract migration pattern.
```

Lesson:

```text
Application rollback is useless if database rollback is impossible.
```

---

## 25. Debugging Mindset

When pipeline fails, ask:

```text
Which event triggered it?
Which job failed?
Which step failed?
What was the exit code?
Was it build, test, Docker, registry, AWS, kubectl, or rollout?
Did credentials load correctly?
Was the image tag correct?
Was the Kubernetes namespace correct?
Are pods running?
Are readiness probes passing?
```

Debug map:

```text
mvn fails:
    code or test problem

Docker build fails:
    Dockerfile/context/JAR problem

Docker push fails:
    registry/auth/repository permission

aws eks update-kubeconfig fails:
    AWS role/region/cluster name problem

kubectl set image fails:
    kubeconfig/RBAC/deployment name problem

rollout status fails:
    pod crash/readiness/resource/config problem
```

Useful commands:

```bash
kubectl get pods -n miniurl
kubectl describe pod <pod-name> -n miniurl
kubectl logs <pod-name> -n miniurl
kubectl rollout history deployment/shortener-api -n miniurl
kubectl rollout undo deployment/shortener-api -n miniurl
```

Golden rule:

```text
Find the failing boundary first.
Do not randomly change YAML.
```

---

## 26. Common Mistakes

### Mistake 1: Deploying on pull request

Wrong:

```text
Every PR deploys to production.
```

Correct:

```text
PR validates only. Main/tag deploys.
```

### Mistake 2: No `needs`

Wrong:

```text
Deploy job runs even if test job is unrelated.
```

Correct:

```yaml
deploy:
  needs: build-test
```

### Mistake 3: Using latest tag

Wrong:

```text
shortener-api:latest
```

Correct:

```text
shortener-api:sha-abc1234
shortener-api:v1.2.0
```

### Mistake 4: Hardcoding secrets

Wrong:

```yaml
password: my-secret
```

Correct:

```yaml
${{ secrets.MY_SECRET }}
```

### Mistake 5: No rollout check

Wrong:

```bash
kubectl apply -f k8s/
```

Correct:

```bash
kubectl rollout status deployment/shortener-api -n miniurl
```

### Mistake 6: Production without approval

Wrong:

```text
Any tag push instantly deploys production without guard.
```

Correct:

```text
Use GitHub production environment approvals.
```

### Mistake 7: CI too slow

Wrong:

```text
Every PR takes 45 minutes for simple backend.
```

Correct:

```text
Cache dependencies and separate fast checks from heavy checks.
```

### Mistake 8: No rollback plan

Wrong:

```text
We will think after failure.
```

Correct:

```text
Rollback command and previous image tag are known before release.
```

---

## 27. Interview-Ready Explanation

If interviewer asks:

```text
How would you design CI/CD for a Spring Boot service deployed to EKS?
```

Strong answer:

```text
I would split the pipeline into CI and CD stages. For pull requests, GitHub Actions runs checkout, Java setup, Maven cache, and mvn clean verify to ensure the service compiles and tests pass before merge. On push to main, after successful tests, the pipeline builds a Docker image, tags it with the commit SHA, pushes it to ECR, updates the EKS deployment image, and waits for kubectl rollout status so the pipeline fails if pods do not become ready. For production, I prefer a release tag such as v1.2.0 and a protected GitHub environment with manual approval. Secrets should be stored in GitHub Secrets, and AWS access should use OIDC role assumption instead of long-lived keys. I would avoid latest tags, keep image tags immutable, and maintain rollback ability using Kubernetes rollout undo or redeploying the previous image tag. For serious production, I would consider GitOps with ArgoCD, but GitHub Actions direct deployment is a good first production-shaped setup.
```

Why this is strong:

```text
1. Separates PR, staging, and production.
2. Mentions build/test quality gate.
3. Mentions Docker artifact.
4. Uses immutable tags.
5. Uses ECR and EKS correctly.
6. Verifies rollout.
7. Handles secrets safely.
8. Mentions rollback.
9. Shows production maturity.
```

Senior one-liner:

```text
A good CI/CD pipeline does not just deploy code; it proves the artifact, controls risk, and gives a safe rollback path.
```

---

## 28. Senior Engineer Checklist

Before calling your GitHub Actions CI/CD production-shaped, confirm:

```text
[ ] PR runs build and tests
[ ] main branch is protected
[ ] direct push to main is blocked
[ ] Maven dependencies are cached
[ ] Java version matches production
[ ] Docker image builds in CI
[ ] Image uses immutable tag
[ ] Image is pushed to registry
[ ] No secrets are hardcoded
[ ] AWS uses OIDC or secured secrets
[ ] Staging deploy is automatic
[ ] Production deploy uses tag/manual approval
[ ] kubectl rollout status is checked
[ ] Rollback command is documented
[ ] Kubernetes namespace is explicit
[ ] Environment variables are separated by env
[ ] Test failures block deployment
[ ] Docker push failures block deployment
[ ] Deployment failures block pipeline
[ ] Logs show image tag and commit SHA
[ ] Security scanning is planned or enabled
```

---

## 29. One-Page Cheat Sheet

```text
Core mental model:
GitHub Actions is an automated delivery factory.

CI:
compile
test
verify code quality

CD:
build image
push registry
deploy environment
verify rollout

Main GitHub Actions blocks:
workflow
event
job
runner
step
action
secret
environment
artifact
cache

PR pipeline:
checkout
setup Java
mvn clean verify
no deploy

Main pipeline:
build/test
Docker build
push image
staging deploy
rollout status

Production pipeline:
tag vX.Y.Z
build image with tag
environment approval
deploy production
verify rollout

Image tagging:
Bad: latest
Good: sha-abc1234
Good: v1.2.0

Secrets:
Never hardcode.
Use GitHub Secrets.
Prefer AWS OIDC.

Rollback:
kubectl rollout undo deployment/app -n namespace
or redeploy previous image tag

Debug boundaries:
Maven fail -> code/test
Docker fail -> Dockerfile/build context
Push fail -> registry/auth
AWS fail -> IAM/region/cluster
kubectl fail -> kubeconfig/RBAC/name
rollout fail -> pods/readiness/config/resources
```

---

## 30. One Picture To Remember

```text
                    GITHUB ACTIONS CI/CD MENTAL MODEL

                         "Code enters a factory"

Developer
   |
   | git push / PR / tag
   v
+-----------------------------+
| GitHub Repository           |
+-----------------------------+
   |
   v
+-----------------------------+
| Workflow Event              |
| pull_request / push / tag   |
+-----------------------------+
   |
   v
+-----------------------------+
| CI Quality Gate             |
| checkout -> Java -> tests   |
+-----------------------------+
   |
   +-- fail --> stop delivery
   |
   v
+-----------------------------+
| Build Artifact              |
| JAR + Docker image          |
+-----------------------------+
   |
   v
+-----------------------------+
| Push Registry               |
| ECR / GHCR                  |
+-----------------------------+
   |
   v
+-----------------------------+
| Deploy Environment          |
| staging / production        |
+-----------------------------+
   |
   v
+-----------------------------+
| Kubernetes Rollout Check    |
| pods become ready?          |
+-----------------------------+
   |
   +-- fail --> rollback/debug
   |
   v
Running Service

FINAL MEMORY:

CI proves the code.
Docker image freezes the artifact.
Registry stores the artifact.
CD moves the artifact safely.
Rollout status proves deployment health.
Rollback protects production.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. GitHub Actions turns code changes into repeatable build, test, package, and deploy steps.
2. Pull requests should validate code but should not deploy production.
3. Docker images should be tagged immutably using commit SHA or release tags, not only latest.
4. EKS deployment should verify rollout status, not only run kubectl apply.
5. A production-shaped pipeline includes secrets safety, environment separation, traceability, and rollback.
```

Next chapter:

```text
056_Infrastructure_As_Code_Terraform.md
```
