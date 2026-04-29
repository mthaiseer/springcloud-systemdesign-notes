
# 🐳 Chapter 1 — Docker Fundamentals (Production-Level)

---

## 1.1 What is Docker

Docker is a containerization platform that allows you to package an application along with all its dependencies into a portable unit called a container.

### Key Idea

Application + Dependencies + Runtime = Container

### Why it matters

Without Docker:
- Works on my machine
- Fails in production

With Docker:
- Same behavior across environments

---

## 1.2 Containers vs Virtual Machines

### Virtual Machines
App → OS → Hypervisor → Hardware

### Containers
App → Libraries → Docker Engine → Host OS

### Key Differences

| Feature | Containers | Virtual Machines |
|--------|----------|----------------|
| OS | Shared | Separate |
| Startup | Fast | Slow |
| Size | Small | Large |
| Performance | Near native | Overhead |

---

## 1.3 Why Docker is Important in Production

Problems without Docker:
- Environment inconsistencies
- Dependency conflicts
- Deployment failures

Benefits with Docker:
- Consistency
- Easy deployment
- Isolation
- Scalability

---

## 1.4 Core Concepts

### Docker Image
A read-only blueprint used to create containers.

Example:
docker pull nginx

---

### Docker Container
A running instance of an image.

Image → Run → Container

---

### Docker Layers

Example:
FROM ubuntu
RUN apt install openjdk-21-jdk
COPY app.jar /app.jar

Each instruction creates a layer.

Why layers matter:
- Faster builds
- Smaller updates
- Efficient storage

---

## 1.5 Docker Architecture

Docker Client → Docker Daemon → Containers

Flow:
docker run nginx
→ Client sends request
→ Daemon pulls image
→ Creates container

---

## 1.6 Under the Hood

Docker uses:
- Namespaces (isolation)
- Cgroups (resource limits)

---

## 1.7 Key Commands

docker run nginx
docker ps
docker stop <id>
docker images

---

## 1.8 Common Beginner Mistakes

- Thinking containers = VMs
- Not using image tags
- Running everything as root
- Ignoring logs

---

## 1.9 Mini Exercise

1. Run nginx container
2. Check running containers
3. Stop container

---

## END Chapter 1
