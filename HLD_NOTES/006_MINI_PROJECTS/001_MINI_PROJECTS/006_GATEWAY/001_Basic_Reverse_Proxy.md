# 001_Basic_Reverse_Proxy.md

# MiniGateway Phase 001 — Basic Reverse Proxy

## Clickable Index

- [1. Goal](#1-goal)
- [2. Why This Phase Matters](#2-why-this-phase-matters)
- [3. What We Build](#3-what-we-build)
- [4. Current Architecture](#4-current-architecture)
- [5. Reverse Proxy Mental Model](#5-reverse-proxy-mental-model)
- [6. Folder Structure](#6-folder-structure)
- [7. Step-by-Step Flow](#7-step-by-step-flow)
- [8. Complete Java Code](#8-complete-java-code)
  - [8.1 BackendServer.java](#81-backendserverjava)
  - [8.2 GatewayConfig.java](#82-gatewayconfigjava)
  - [8.3 HttpRequest.java](#83-httprequestjava)
  - [8.4 HttpResponse.java](#84-httpresponsejava)
  - [8.5 SimpleHttpParser.java](#85-simplehttpparserjava)
  - [8.6 BackendHttpClient.java](#86-backendhttpclientjava)
  - [8.7 MiniGatewayReverseProxy.java](#87-minigatewayreverseproxyjava)
  - [8.8 Phase001BasicReverseProxyDriver.java](#88-phase001basicreverseproxydriverjava)
- [9. How To Run](#9-how-to-run)
- [10. Test Using Browser / curl](#10-test-using-browser--curl)
- [11. Dry Run](#11-dry-run)
- [12. DSA / CP Concepts Used](#12-dsa--cp-concepts-used)
- [13. System Design Relevance](#13-system-design-relevance)
- [14. Gateway Connection With This Phase](#14-gateway-connection-with-this-phase)
- [15. Production-Grade Concepts](#15-production-grade-concepts)
- [16. Scalability Discussion](#16-scalability-discussion)
- [17. Interview Notes](#17-interview-notes)
- [18. Common Bugs](#18-common-bugs)
- [19. Current Limitations](#19-current-limitations)
- [20. Next Step](#20-next-step)

---

# 1. Goal

In this phase, we build the first foundation of **MiniGateway**:

```text
Basic Reverse Proxy
```

A reverse proxy receives a client request, forwards it to a backend server, receives the backend response, and returns that response to the client.

This is the core idea behind:

```text
NGINX
Spring Cloud Gateway
Kong
Envoy
Zuul
AWS API Gateway
Kubernetes Ingress
Cloudflare edge proxy
```

In this phase, we build a simple Java gateway that:

- listens on port `8080`
- receives an HTTP request from browser/curl
- forwards the request to backend server on port `9001`
- reads backend response
- returns backend response to the client

This is not a production gateway yet, but it is the first building block.

---

# 2. Why This Phase Matters

Every distributed system needs a front door.

That front door is usually:

```text
API Gateway
Reverse Proxy
Load Balancer
Ingress Controller
Edge Proxy
```

Before we add:

```text
routing
load balancing
JWT authentication
API key authentication
rate limiting
circuit breaker
retry
timeout
metrics
tracing
request validation
response caching
```

we must first understand the simplest gateway behavior:

```text
receive request
forward request
return response
```

That is exactly what a reverse proxy does.

---

# 3. What We Build

We build two components:

```text
1. BackendServer
2. MiniGatewayReverseProxy
```

## Backend Server

Runs on:

```text
localhost:9001
```

Returns:

```text
Hello from backend service
```

## Gateway Server

Runs on:

```text
localhost:8080
```

Client calls:

```text
http://localhost:8080/hello
```

Gateway forwards to:

```text
http://localhost:9001/hello
```

Backend responds, then gateway returns the same response to the client.

---

# 4. Current Architecture

```text
+-------------+          HTTP           +------------------+
|             |  -------------------->  |                  |
|  Browser /  |                         |   MiniGateway    |
|  curl       |  <--------------------  |  Reverse Proxy   |
|             |                         |                  |
+-------------+                         +--------+---------+
                                                  |
                                                  | HTTP forward
                                                  v
                                        +------------------+
                                        | Backend Service  |
                                        | localhost:9001   |
                                        +------------------+
```

## Request Flow

```text
1. Client sends request to gateway on port 8080
2. Gateway reads HTTP request line and headers
3. Gateway opens connection to backend on port 9001
4. Gateway forwards request
5. Backend processes request
6. Backend returns response
7. Gateway sends response back to client
```

---

# 5. Reverse Proxy Mental Model

## Forward Proxy

Used by clients to access internet.

```text
Client -> Forward Proxy -> Internet
```

## Reverse Proxy

Used by servers to receive traffic before backend services.

```text
Client -> Reverse Proxy -> Backend Service
```

For backend engineering, API Gateway is basically a smart reverse proxy.

A basic reverse proxy only forwards.

A production API Gateway adds:

```text
auth
rate limit
routing
load balancing
retry
circuit breaker
metrics
logging
tracing
transformation
```

This phase builds the forwarding foundation.

---

# 6. Folder Structure

Create this structure:

```text
MiniGateway/
└── src/
    └── main/
        └── java/
            └── com/
                └── minigateway/
                    ├── backend/
                    │   └── BackendServer.java
                    ├── config/
                    │   └── GatewayConfig.java
                    ├── http/
                    │   ├── HttpRequest.java
                    │   ├── HttpResponse.java
                    │   └── SimpleHttpParser.java
                    ├── client/
                    │   └── BackendHttpClient.java
                    ├── server/
                    │   └── MiniGatewayReverseProxy.java
                    └── driver/
                        └── Phase001BasicReverseProxyDriver.java
```

---

# 7. Step-by-Step Flow

## Step 1 — Start Backend Server

```text
BackendServer listens on port 9001
```

## Step 2 — Start Gateway Server

```text
MiniGateway listens on port 8080
```

## Step 3 — Client Calls Gateway

```bash
curl http://localhost:8080/hello
```

## Step 4 — Gateway Parses Request

```text
GET /hello HTTP/1.1
Host: localhost:8080
```

Gateway extracts:

```text
method = GET
path = /hello
version = HTTP/1.1
```

## Step 5 — Gateway Forwards Request To Backend

```text
GET /hello HTTP/1.1
Host: localhost:9001
Connection: close
```

## Step 6 — Backend Responds

```text
HTTP/1.1 200 OK
Content-Type: text/plain

Hello from backend service. Path=/hello
```

## Step 7 — Gateway Returns Backend Response

The client sees backend response through the gateway.

---

# 8. Complete Java Code

---

## 8.1 BackendServer.java

### Logic before this class

This class simulates a backend microservice.

In real systems, this could be:

```text
User Service
Order Service
Payment Service
Product Service
Inventory Service
```

For Phase 001, we keep it simple:

```text
listen on port 9001
read HTTP request
return plain text response
```

```java
package com.minigateway.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BackendServer {

    private final int port;

    public BackendServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Backend service started on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket client = serverSocket.accept();
                handle(client);
            }
        } catch (Exception e) {
            System.err.println("Backend server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        try (
                Socket client = socket;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true)
        ) {
            String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            String path = extractPath(requestLine);

            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                // consume headers
            }

            String body = "Hello from backend service. Path=" + path;

            writer.print("HTTP/1.1 200 OK\r\n");
            writer.print("Content-Type: text/plain\r\n");
            writer.print("Content-Length: " + body.getBytes().length + "\r\n");
            writer.print("Connection: close\r\n");
            writer.print("\r\n");
            writer.print(body);
            writer.flush();

        } catch (Exception e) {
            System.err.println("Backend client handling error: " + e.getMessage());
        }
    }

    private String extractPath(String requestLine) {
        String[] parts = requestLine.split("\\s+");

        if (parts.length >= 2) {
            return parts[1];
        }

        return "/";
    }

    public static void main(String[] args) {
        new BackendServer(9001).start();
    }
}
```

---

## 8.2 GatewayConfig.java

### Logic before this class

This class stores gateway configuration.

For this phase, the gateway has only one backend:

```text
backendHost = localhost
backendPort = 9001
gatewayPort = 8080
```

Later this becomes:

```text
route table
path-based routing
service registry
dynamic config
canary routing
blue-green routing
```

```java
package com.minigateway.config;

public class GatewayConfig {

    private final int gatewayPort;
    private final String backendHost;
    private final int backendPort;

    public GatewayConfig(int gatewayPort, String backendHost, int backendPort) {
        this.gatewayPort = gatewayPort;
        this.backendHost = backendHost;
        this.backendPort = backendPort;
    }

    public int getGatewayPort() {
        return gatewayPort;
    }

    public String getBackendHost() {
        return backendHost;
    }

    public int getBackendPort() {
        return backendPort;
    }
}
```

---

## 8.3 HttpRequest.java

### Logic before this class

This class represents a parsed client request.

For now, we store:

```text
method
path
version
```

Later we add:

```text
headers
query params
body
auth headers
idempotency key
correlation id
```

```java
package com.minigateway.http;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;

    public HttpRequest(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
```

---

## 8.4 HttpResponse.java

### Logic before this class

This class represents the response returned by backend.

For Phase 001, we store raw response as a string because the gateway just forwards it.

Later, we parse and modify response for:

```text
compression
caching
CORS
security headers
response transformation
```

```java
package com.minigateway.http;

public class HttpResponse {

    private final String rawResponse;

    public HttpResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public String getRawResponse() {
        return rawResponse;
    }
}
```

---

## 8.5 SimpleHttpParser.java

### Logic before this class

This class parses the first line of an HTTP request.

Example:

```text
GET /hello HTTP/1.1
```

becomes:

```text
method = GET
path = /hello
version = HTTP/1.1
```

```java
package com.minigateway.http;

public class SimpleHttpParser {

    public HttpRequest parseRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line cannot be empty");
        }

        String[] parts = requestLine.split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }

        return new HttpRequest(parts[0], parts[1], parts[2]);
    }
}
```

---

## 8.6 BackendHttpClient.java

### Logic before this class

This class is the gateway's internal HTTP client.

Flow:

```text
Gateway
  -> BackendHttpClient
  -> Backend service
  -> Backend response
```

For Phase 001, it:

```text
opens socket to backend
writes simple HTTP request
reads entire response
returns raw response
```

Later it becomes:

```text
connection pool
timeouts
retry
circuit breaker
load balancing
HTTP/2 client
TLS
```

```java
package com.minigateway.client;

import com.minigateway.http.HttpRequest;
import com.minigateway.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BackendHttpClient {

    private final String backendHost;
    private final int backendPort;

    public BackendHttpClient(String backendHost, int backendPort) {
        this.backendHost = backendHost;
        this.backendPort = backendPort;
    }

    public HttpResponse forward(HttpRequest request) {
        try (
                Socket socket = new Socket(backendHost, backendPort);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                )
        ) {
            writer.print(request.getMethod() + " " + request.getPath() + " " + request.getVersion() + "\r\n");
            writer.print("Host: " + backendHost + ":" + backendPort + "\r\n");
            writer.print("Connection: close\r\n");
            writer.print("\r\n");
            writer.flush();

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line).append("\r\n");
            }

            return new HttpResponse(response.toString());

        } catch (Exception e) {
            String body = "Gateway error while forwarding request: " + e.getMessage();

            String raw = "HTTP/1.1 502 Bad Gateway\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + body.getBytes().length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    body;

            return new HttpResponse(raw);
        }
    }
}
```

---

## 8.7 MiniGatewayReverseProxy.java

### Logic before this class

This is the gateway server.

Responsibilities:

```text
1. Listen on gateway port 8080
2. Accept client connection
3. Read HTTP request line
4. Parse request
5. Forward request to backend
6. Return backend response to client
```

This is the first version of the API Gateway request pipeline.

```java
package com.minigateway.server;

import com.minigateway.client.BackendHttpClient;
import com.minigateway.config.GatewayConfig;
import com.minigateway.http.HttpRequest;
import com.minigateway.http.HttpResponse;
import com.minigateway.http.SimpleHttpParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MiniGatewayReverseProxy {

    private final GatewayConfig config;
    private final SimpleHttpParser parser;
    private final BackendHttpClient backendClient;

    public MiniGatewayReverseProxy(GatewayConfig config) {
        this.config = config;
        this.parser = new SimpleHttpParser();
        this.backendClient = new BackendHttpClient(
                config.getBackendHost(),
                config.getBackendPort()
        );
    }

    public void start() {
        System.out.println("MiniGateway started on port " + config.getGatewayPort());
        System.out.println("Forwarding to " + config.getBackendHost() + ":" + config.getBackendPort());

        try (ServerSocket serverSocket = new ServerSocket(config.getGatewayPort())) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (Exception e) {
            System.err.println("Gateway server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            System.out.println("Gateway received: " + requestLine);

            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                // consume headers for phase 001
            }

            HttpRequest request = parser.parseRequestLine(requestLine);
            HttpResponse backendResponse = backendClient.forward(request);

            writer.print(backendResponse.getRawResponse());
            writer.flush();

        } catch (Exception e) {
            System.err.println("Gateway client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        GatewayConfig config = new GatewayConfig(8080, "localhost", 9001);
        new MiniGatewayReverseProxy(config).start();
    }
}
```

---

## 8.8 Phase001BasicReverseProxyDriver.java

### Logic before this class

This driver starts both:

```text
backend server
gateway server
```

The backend starts in a background thread.

Then the gateway starts in the main thread.

```java
package com.minigateway.driver;

import com.minigateway.backend.BackendServer;
import com.minigateway.config.GatewayConfig;
import com.minigateway.server.MiniGatewayReverseProxy;

public class Phase001BasicReverseProxyDriver {

    public static void main(String[] args) {
        Thread backendThread = new Thread(() -> {
            BackendServer backendServer = new BackendServer(9001);
            backendServer.start();
        });

        backendThread.setDaemon(true);
        backendThread.start();

        sleep(500);

        GatewayConfig config = new GatewayConfig(8080, "localhost", 9001);
        MiniGatewayReverseProxy gateway = new MiniGatewayReverseProxy(config);

        gateway.start();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

# 9. How To Run

## Option A — IntelliJ

1. Create Java project `MiniGateway`
2. Create packages:

```text
com.minigateway.backend
com.minigateway.config
com.minigateway.http
com.minigateway.client
com.minigateway.server
com.minigateway.driver
```

3. Add all classes
4. Run:

```text
Phase001BasicReverseProxyDriver
```

5. Open browser or terminal:

```text
http://localhost:8080/hello
```

---

## Option B — Command Line

From project root:

```bash
javac -d out src/main/java/com/minigateway/backend/BackendServer.java \
             src/main/java/com/minigateway/config/GatewayConfig.java \
             src/main/java/com/minigateway/http/HttpRequest.java \
             src/main/java/com/minigateway/http/HttpResponse.java \
             src/main/java/com/minigateway/http/SimpleHttpParser.java \
             src/main/java/com/minigateway/client/BackendHttpClient.java \
             src/main/java/com/minigateway/server/MiniGatewayReverseProxy.java \
             src/main/java/com/minigateway/driver/Phase001BasicReverseProxyDriver.java
```

Run:

```bash
java -cp out com.minigateway.driver.Phase001BasicReverseProxyDriver
```

---

# 10. Test Using Browser / curl

## Using curl

```bash
curl http://localhost:8080/hello
```

Expected:

```text
Hello from backend service. Path=/hello
```

Try another path:

```bash
curl http://localhost:8080/orders
```

Expected:

```text
Hello from backend service. Path=/orders
```

---

# 11. Dry Run

Input:

```bash
curl http://localhost:8080/hello
```

## Step 1 — Client Sends Request

```text
GET /hello HTTP/1.1
Host: localhost:8080
User-Agent: curl
```

## Step 2 — Gateway Receives Request

Gateway reads:

```text
GET /hello HTTP/1.1
```

Parser converts:

```text
method = GET
path = /hello
version = HTTP/1.1
```

## Step 3 — Gateway Forwards To Backend

Gateway sends to `localhost:9001`:

```text
GET /hello HTTP/1.1
Host: localhost:9001
Connection: close
```

## Step 4 — Backend Responds

```text
HTTP/1.1 200 OK
Content-Type: text/plain
Content-Length: 39
Connection: close

Hello from backend service. Path=/hello
```

## Step 5 — Gateway Sends Response To Client

Visual:

```text
Client                 Gateway                  Backend
  |                       |                         |
  | GET /hello ---------->|                         |
  |                       | GET /hello ------------>|
  |                       |                         |
  |                       |<---- 200 OK ------------|
  |<------ 200 OK --------|                         |
```

---

# 12. DSA / CP Concepts Used

This phase does not use heavy DSA.

But it introduces important systems concepts:

| Concept | Usage |
|---|---|
| String parsing | Parse HTTP request line |
| Loop | Accept requests |
| Socket IO | Gateway/backend communication |
| Object modeling | Request/response classes |
| State separation | Config vs parser vs client vs server |

The real learning here is:

```text
network request forwarding
```

---

# 13. System Design Relevance

This phase maps to real HLD components:

```text
Client -> API Gateway -> Backend Service
```

In interviews, you often draw:

```text
Mobile App
   |
   v
API Gateway
   |
   v
Microservices
```

This phase teaches what the API Gateway actually does internally.

At minimum:

```text
1. receives request
2. forwards request
3. returns response
```

Later:

```text
auth
rate limit
routing
load balancing
retry
circuit breaker
metrics
tracing
```

---

# 14. Gateway Connection With This Phase

Real gateways:

```text
NGINX
Kong
Envoy
Spring Cloud Gateway
AWS API Gateway
Kubernetes Ingress
```

all start with:

```text
reverse proxy
```

Difference between real gateway and current phase:

| Real Gateway | Current Phase |
|---|---|
| Many backends | One backend |
| Path routing | Not yet |
| Load balancing | Not yet |
| Auth filters | Not yet |
| Rate limiting | Not yet |
| TLS | Not yet |
| Metrics | Not yet |
| Tracing | Not yet |
| Connection pooling | Not yet |
| Async event loop | Not yet |

---

# 15. Production-Grade Concepts

Production gateway must handle:

```text
timeouts
connection pooling
slow backend
backend failures
large request body
large response body
TLS termination
HTTP/2
WebSockets
routing table
health checks
observability
security filters
```

Even this simple phase introduces the most important concept:

```text
The gateway is on the hot path of every request.
```

So it must be:

```text
fast
safe
observable
fault-tolerant
scalable
```

---

# 16. Scalability Discussion

## Current Phase

```text
single thread
blocking IO
one backend
no timeout
no connection pool
```

Good for learning.

Bad for production.

## Scaling Path

```text
Phase 001: blocking reverse proxy
Phase 002: request routing
Phase 004: load balancing
Phase 006: health checks
Phase 009: JWT filter
Phase 012: rate limiter
Phase 021: circuit breaker
Phase 023: timeout
Phase 031: metrics
Phase 040: production gateway
```

## Bottlenecks

```text
blocking socket per request
backend connection creation per request
slow backend blocks gateway
large response consumes memory
no thread pool
no backpressure
```

Production gateways solve this with:

```text
event loop
non-blocking IO
connection pools
timeouts
bulkheads
backpressure
circuit breakers
```

---

# 17. Interview Notes

## Q1. What is an API Gateway?

An API Gateway is a reverse proxy that sits between clients and backend services.

It handles cross-cutting concerns like:

```text
routing
authentication
rate limiting
logging
metrics
retry
circuit breaker
request transformation
```

## Q2. What is a reverse proxy?

A reverse proxy receives client requests on behalf of backend servers and forwards them to the correct backend.

```text
Client -> Reverse Proxy -> Backend
```

## Q3. Why not let clients call services directly?

Because:

```text
client must know all services
security logic duplicated
rate limiting duplicated
service URLs exposed
hard to change backend topology
hard to observe all traffic centrally
```

Gateway centralizes these concerns.

## Q4. What are limitations of this basic gateway?

```text
single backend
blocking IO
no timeout
no retry
no load balancing
no auth
no rate limit
no metrics
```

---

# 18. Common Bugs

## Bug 1 — Backend not running

Error:

```text
Connection refused
```

Fix:

```text
Start backend first.
```

## Bug 2 — Port already in use

Error:

```text
Address already in use: bind
```

Fix:

```text
Use another port or stop the process.
```

## Bug 3 — Browser keeps loading

Reason:

```text
missing Content-Length
missing blank line between headers and body
writer not flushed
```

Fix:

```text
headers
blank line
body
flush
```

## Bug 4 — Gateway returns empty response

Reason:

```text
Backend response was not read correctly.
```

Fix:

```text
Read until backend closes connection or parse Content-Length.
```

## Bug 5 — Special HTTP features fail

Examples:

```text
POST body
chunked encoding
keep-alive
gzip
HTTP/2
WebSocket
```

Reason:

```text
Phase 001 supports only simple GET-style requests.
```

---

# 19. Current Limitations

Current gateway supports:

```text
GET request forwarding
one backend
blocking IO
raw response forwarding
```

It does not support yet:

```text
POST body
headers forwarding
query parameter parsing
path routing
load balancing
health checks
JWT auth
rate limiting
retry
timeout
metrics
tracing
TLS
WebSocket
```

This is expected.

We add those one by one.

---

# 20. Next Step

Next file:

```text
002_Request_Routing.md
```

In the next phase, we move from:

```text
all requests -> one backend
```

to:

```text
route table
/orders   -> order-service
/users    -> user-service
/payments -> payment-service
```

This is where MiniGateway starts becoming a real API Gateway.
