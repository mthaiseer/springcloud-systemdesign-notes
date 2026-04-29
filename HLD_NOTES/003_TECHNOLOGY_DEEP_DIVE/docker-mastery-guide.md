
# Docker Mastery Guide (Beginner → Production)

Author: You + ChatGPT

---

# Chapter 1: Docker Fundamentals
- What is Docker?
- Containers vs VMs
- Images, Containers, Layers
- Docker architecture (Client, Daemon, Registry)
- Under the hood: namespaces & cgroups

---

# Chapter 2: Installation & First Containers
- Install Docker
- docker run, ps, stop
- Run nginx container
- Interactive containers

---

# Chapter 3: Docker Images
- Image layers
- Pulling from Docker Hub
- Tagging/versioning
- Inspecting images

---

# Chapter 4: Dockerfiles
- FROM, RUN, COPY, WORKDIR
- Multi-stage builds
- Best practices

Example:

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

---

# Chapter 5: Volumes
- Persistent storage
- Volumes vs bind mounts
- Backup strategies

---

# Chapter 6: Networking
- Bridge network
- Host network
- DNS resolution
- Custom networks

---

# Chapter 7: Docker Compose
- Multi-container apps
- docker-compose.yml

Example:

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
  db:
    image: postgres
```

---

# Chapter 8: Real App (Spring Boot)
- Dockerize Spring Boot
- Connect PostgreSQL
- Env configs

---

# Chapter 9: Debugging
- docker logs
- docker exec
- inspect
- common issues

---

# Chapter 10: Optimization
- Reduce image size
- Alpine images
- multi-stage builds

---

# Chapter 11: Security
- non-root user
- secrets
- vulnerabilities

---

# Chapter 12: Registries
- push/pull images
- private registries

---

# Chapter 13: CI/CD
- build pipelines
- versioning
- automation

---

# Chapter 14: Docker + Kubernetes
- containers → pods
- scaling basics

---

# Chapter 15: Production Deployment
- reverse proxy
- load balancing
- zero downtime

---

# Chapter 16: Observability
- metrics
- logs
- health checks

---

# Chapter 17: Scaling
- multi-host networking
- service discovery

---

# Chapter 18: Performance
- CPU/memory limits
- tuning

---

# Chapter 19: Patterns
- sidecar
- blue-green
- canary

---

# Chapter 20: Best Practices
- versioning
- backups
- DR strategies

---

# END
