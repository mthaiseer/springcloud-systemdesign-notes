# 007_Copy_On_Write.md

# Copy On Write – Understanding First Edition With ASCII Diagrams

## Goal Of This Chapter

After this chapter you will understand:

- Why 100 containers do not consume 100 copies of the same files
- How Docker containers share image layers safely
- Why container startup is fast
- How Copy-On-Write (COW) saves storage
- Why modifying one container does not affect others
- How Docker, UnionFS, and Copy-On-Write work together
- Common production issues related to writable layers

The goal is understanding, not filesystem implementation details.

---

# Mental Model

Imagine a library.

There is one shared book.

```text
Library

+------------------+
| Java Book        |
+------------------+
```

100 people read it.

```text
Reader A
Reader B
Reader C
...
Reader 100
```

Nobody copies the book.

Storage cost:

```text
One Book
```

Now Reader A wants to write notes.

Instead of changing the original:

```text
Original Book
      +
Private Notes
```

This is Copy-On-Write.

Docker works the same way.

---

# Why This Problem Exists

Imagine:

```text
Ubuntu Layer = 100 MB
Java Layer   = 300 MB
App Layer    = 50 MB
```

One container:

```text
450 MB
```

100 containers:

Without Copy-On-Write:

```text
450 MB x 100
= 45 GB
```

Huge waste.

Most files are identical.

Containers should share them.

But sharing creates a problem.

---

# The Sharing Problem

Suppose:

```text
Container A
Container B
```

both use:

```text
Java Layer
```

Shared layer:

```text
+--------------------+
| Java Runtime       |
+--------------------+
```

Now Container A modifies a file.

Question:

```text
Should Container B see it?
```

Answer:

```text
No
```

Containers must remain isolated.

This is the problem Copy-On-Write solves.

---

# Real World Analogy

Apartment Building

```text
Building
 |
 +-- Shared Foundation
 +-- Shared Elevator
 +-- Shared Roof
```

Shared resources are reused.

Now Apartment A paints a wall.

```text
Apartment A Wall
```

Only Apartment A changes.

Apartment B stays unchanged.

This is Copy-On-Write thinking.

---

# Core Concept

Read Operations

When container reads a file:

```text
Container
     |
Read File
     |
Shared Layer
```

No copy needed.

Visual:

```text
Container A
      |
      +------> Shared Layer

Container B
      |
      +------> Shared Layer
```

Storage usage:

```text
One Shared Copy
```

---

# Write Operations

When container modifies a file:

```text
Container
     |
Modify File
```

Docker creates:

```text
Private Copy
```

Visual:

```text
Before

Container A
       \
        \
Shared Layer
        /
       /
Container B
```

After Write:

```text
Container A
     |
Private Copy

Container B
     |
Shared Layer
```

This is:

```text
Copy On Write
```

Copy only when modification occurs.

---

# Why This Is Powerful

Without Copy-On-Write:

```text
Every Container
Gets Full Copy
```

With Copy-On-Write:

```text
Shared Until Modified
```

Result:

```text
Less Storage
Less Disk Usage
Faster Startup
```

---

# UnionFS + Copy-On-Write

These concepts work together.

UnionFS:

```text
Multiple Layers
      |
Merged View
```

Copy-On-Write:

```text
Shared Layer
      |
Modified
      |
Private Copy
```

Visual:

```text
Container

+--------------------+
| Writable Layer     |
+--------------------+
| App Layer          |
+--------------------+
| Java Layer         |
+--------------------+
| Ubuntu Layer       |
+--------------------+
```

Writes go to:

```text
Writable Layer
```

Shared layers stay unchanged.

---

# Docker Startup Story

Without Copy-On-Write:

```text
Start Container
      |
Copy 450 MB
      |
Container Starts
```

Slow.

With Copy-On-Write:

```text
Start Container
      |
Reuse Existing Layers
      |
Create Small Writable Layer
      |
Container Starts
```

Fast.

---

# Why Containers Start Quickly

Visual:

```text
Shared Image

Ubuntu
Java
App
```

Container A:

```text
Writable Layer Only
```

Container B:

```text
Writable Layer Only
```

Container C:

```text
Writable Layer Only
```

Docker does not duplicate everything.

---

# Spring Boot Example

Spring Boot image:

```text
Ubuntu Layer
Java Layer
Application Layer
```

Container starts.

User uploads:

```text
report.pdf
```

Where is it stored?

```text
Writable Layer
```

Not:

```text
Ubuntu Layer
Java Layer
Application Layer
```

Those remain unchanged.

---

# Why Containers Lose Uploaded Files

Very common beginner issue.

Container:

```text
User Uploads

invoice.pdf
report.pdf
```

Stored in:

```text
Writable Layer
```

Container deleted.

```text
Writable Layer Deleted
```

Files disappear.

Visual:

```text
Container

+----------------------+
| Writable Layer       |
| invoice.pdf          |
| report.pdf           |
+----------------------+
```

Delete container:

```text
Layer Gone
```

Lesson:

```text
Container Storage
!=
Persistent Storage
```

Use volumes.

---

# Docker Connection

Docker depends heavily on Copy-On-Write.

Without it:

```text
Containers Expensive
```

With it:

```text
Containers Cheap
```

Diagram:

```text
Shared Layers

Ubuntu
Java
Application

     |
     +---- Container A
     |
     +---- Container B
     |
     +---- Container C
```

Only writable layers differ.

---

# Kubernetes Connection

Imagine:

```text
100 Pods
```

All using:

```text
Java Runtime
```

Without Copy-On-Write:

Huge duplication.

With Copy-On-Write:

```text
Shared Image Layers
      +
Small Writable Layers
```

Benefits:

```text
Fast Scaling
Less Storage
Fast Node Startup
```

---

# Production Failure Story #1

Problem:

```text
Node Disk Full
```

Investigation:

```text
Containers Writing Logs
```

Logs stored in:

```text
Writable Layer
```

Layers grow.

Visual:

```text
Writable Layer

100 MB
500 MB
1 GB
5 GB
```

Fix:

```text
External Logging
Log Rotation
```

---

# Production Failure Story #2

Problem:

```text
Uploads Disappear
```

Investigation:

```text
Files Stored Inside Container
```

Container Restarted.

Files Lost.

Fix:

```text
Volumes
Object Storage
```

---

# Production Failure Story #3

Problem:

```text
Container Startup Slow
```

Investigation:

```text
Huge Image
```

Large layers.

Fix:

```text
Smaller Images
Multi-stage Builds
```

---

# Debugging Mindset

Ask:

```text
Is file in image layer?
```

or

```text
Writable layer?
```

Ask:

```text
Will restart delete this?
```

Think:

```text
Shared Layer
or
Private Layer
```

---

# Performance Tradeoffs

Benefits:

```text
Storage Savings
Fast Startup
Fast Scaling
Layer Reuse
```

Costs:

```text
Writable Layer Growth
Storage Management
```

Table:

| Area | No COW | COW |
|--------|--------|------|
| Storage | High | Low |
| Startup | Slow | Fast |
| Reuse | Poor | Excellent |

---

# Common Mistakes

Wrong:

```text
Container has its own full OS copy
```

Correct:

```text
Container shares layers
```

Wrong:

```text
Container storage is permanent
```

Correct:

```text
Writable layer is temporary
```

Wrong:

```text
Every write changes image
```

Correct:

```text
Writes go to writable layer
```

Additional mistakes:

1. Storing uploads in container
2. Large logs in container
3. Ignoring layer growth
4. Confusing image and container
5. Ignoring volumes
6. Huge images
7. Excessive writes

---

# System Design Connection

```text
Microservice
      |
Docker Image
      |
UnionFS
      |
Copy-On-Write
      |
Container
      |
Kubernetes
```

Copy-On-Write enables:

```text
Fast Scaling
Efficient Storage
Cloud Native Density
```

Without it:

Containers would be much more expensive.

---

# Strong Interview Answers

Q: What is Copy-On-Write?

Expected Answer:

Copy-On-Write allows containers to share image layers until a modification occurs. When a file is modified, a private copy is created instead of modifying the shared layer.

Wrong Answer:

Copy-On-Write means copying everything before starting a container.

---

Q: Why is Copy-On-Write useful?

Expected Answer:

It reduces storage usage, speeds up container startup, and enables safe sharing of image layers.

---

Q: Why do files disappear after container restart?

Expected Answer:

Files written inside the writable container layer are lost when the container is deleted unless external persistence is used.

---

# One Picture To Remember

```text
Container

+--------------------------------+
| Writable Layer                 |
| Logs                           |
| Uploaded Files                 |
| Cache                          |
+--------------------------------+
| Spring Boot Layer              |
+--------------------------------+
| Java Runtime Layer             |
+--------------------------------+
| Ubuntu Layer                   |
+--------------------------------+
              |
              v
      Copy-On-Write

Read:
Shared Layers

Write:
Private Copy

Benefits:

Storage Savings
Fast Startup
Fast Scaling
```
