> Production-grade Kubernetes + Spring Boot handbook. Covers
> fundamentals to expert practices with real YAML, commands, and
> reasoning.

# Chapter 1: Foundations: Kubernetes Mental Model

## Overview

This chapter explains **Foundations: Kubernetes Mental Model** in a
production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo1 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-1
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-1
  template:
    metadata:
      labels:
        app: app-1
    spec:
      containers:
        - name: app
          image: app:1
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-1
spec:
  selector:
    app: app-1
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-1
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 2: Cluster Architecture

## Overview

This chapter explains **Cluster Architecture** in a production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo2 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-2
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-2
  template:
    metadata:
      labels:
        app: app-2
    spec:
      containers:
        - name: app
          image: app:2
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-2
spec:
  selector:
    app: app-2
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-2
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 3: Pods & Lifecycle

## Overview

This chapter explains **Pods & Lifecycle** in a production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo3 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-3
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-3
  template:
    metadata:
      labels:
        app: app-3
    spec:
      containers:
        - name: app
          image: app:3
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-3
spec:
  selector:
    app: app-3
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-3
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 4: Deployments & Rollouts

## Overview

This chapter explains **Deployments & Rollouts** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo4 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-4
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-4
  template:
    metadata:
      labels:
        app: app-4
    spec:
      containers:
        - name: app
          image: app:4
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-4
spec:
  selector:
    app: app-4
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-4
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 5: Services & Networking Basics

## Overview

This chapter explains **Services & Networking Basics** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo5 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-5
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-5
  template:
    metadata:
      labels:
        app: app-5
    spec:
      containers:
        - name: app
          image: app:5
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-5
spec:
  selector:
    app: app-5
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-5
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 6: ConfigMaps & Secrets

## Overview

This chapter explains **ConfigMaps & Secrets** in a production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo6 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-6
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-6
  template:
    metadata:
      labels:
        app: app-6
    spec:
      containers:
        - name: app
          image: app:6
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-6
spec:
  selector:
    app: app-6
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-6
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 7: Storage & Stateful Workloads

## Overview

This chapter explains **Storage & Stateful Workloads** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo7 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-7
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-7
  template:
    metadata:
      labels:
        app: app-7
    spec:
      containers:
        - name: app
          image: app:7
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-7
spec:
  selector:
    app: app-7
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-7
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 8: Ingress & Traffic Management

## Overview

This chapter explains **Ingress & Traffic Management** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo8 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-8
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-8
  template:
    metadata:
      labels:
        app: app-8
    spec:
      containers:
        - name: app
          image: app:8
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-8
spec:
  selector:
    app: app-8
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-8
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 9: Health Checks & Reliability

## Overview

This chapter explains **Health Checks & Reliability** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo9 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-9
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-9
  template:
    metadata:
      labels:
        app: app-9
    spec:
      containers:
        - name: app
          image: app:9
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-9
spec:
  selector:
    app: app-9
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-9
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 10: Scaling & Autoscaling

## Overview

This chapter explains **Scaling & Autoscaling** in a production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo10 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-10
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-10
  template:
    metadata:
      labels:
        app: app-10
    spec:
      containers:
        - name: app
          image: app:10
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-10
spec:
  selector:
    app: app-10
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-10
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 11: Scheduling & Placement

## Overview

This chapter explains **Scheduling & Placement** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo11 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-11
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-11
  template:
    metadata:
      labels:
        app: app-11
    spec:
      containers:
        - name: app
          image: app:11
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-11
spec:
  selector:
    app: app-11
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-11
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 12: Security (RBAC, Secrets)

## Overview

This chapter explains **Security (RBAC, Secrets)** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo12 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-12
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-12
  template:
    metadata:
      labels:
        app: app-12
    spec:
      containers:
        - name: app
          image: app:12
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-12
spec:
  selector:
    app: app-12
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-12
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 13: Observability

## Overview

This chapter explains **Observability** in a production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo13 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-13
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-13
  template:
    metadata:
      labels:
        app: app-13
    spec:
      containers:
        - name: app
          image: app:13
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-13
spec:
  selector:
    app: app-13
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-13
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 14: Troubleshooting

## Overview

This chapter explains **Troubleshooting** in a production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo14 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-14
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-14
  template:
    metadata:
      labels:
        app: app-14
    spec:
      containers:
        - name: app
          image: app:14
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-14
spec:
  selector:
    app: app-14
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-14
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 15: CI/CD & GitOps

## Overview

This chapter explains **CI/CD & GitOps** in a production context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo15 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-15
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-15
  template:
    metadata:
      labels:
        app: app-15
    spec:
      containers:
        - name: app
          image: app:15
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-15
spec:
  selector:
    app: app-15
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-15
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs

# Chapter 16: Production Architecture

## Overview

This chapter explains **Production Architecture** in a production
context.

## Architecture

``` mermaid
flowchart LR
A[Client] --> B[Ingress]
B --> C[Service]
C --> D[Pods]
D --> E[(Database)]
```

## Spring Boot Example

``` java
@RestController
public class Demo16 {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

## Dockerfile

``` dockerfile
FROM eclipse-temurin:21-jre
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Deployment YAML

``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-16
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-16
  template:
    metadata:
      labels:
        app: app-16
    spec:
      containers:
        - name: app
          image: app:16
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

## Service YAML

``` yaml
apiVersion: v1
kind: Service
metadata:
  name: app-svc-16
spec:
  selector:
    app: app-16
  ports:
    - port: 80
      targetPort: 8080
```

## Commands

``` bash
kubectl apply -f deployment.yaml
kubectl get pods -o wide
kubectl describe pod <pod>
kubectl logs <pod>
kubectl rollout status deployment/app-16
```

## Production Notes

-   Always define resource requests/limits
-   Use readiness/liveness probes
-   Avoid hardcoding configs
-   Monitor metrics and logs
