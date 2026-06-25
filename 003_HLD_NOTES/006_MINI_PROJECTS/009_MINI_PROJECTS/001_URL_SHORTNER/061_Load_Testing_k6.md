# 061_Load_Testing_k6.md
# MiniURLShortener — Load Testing with k6

> Core mental model: **Load testing is a controlled fire drill for your backend. k6 creates realistic traffic, your application shows where it bends, and metrics tell you which layer breaks first.**

---

## Clickable Index

- [1. Why This Exists](#1-why-this-exists)
- [2. The One Core Mental Model](#2-the-one-core-mental-model)
- [3. Problem Statement](#3-problem-statement)
- [4. Load Testing vs Performance Testing vs Stress Testing](#4-load-testing-vs-performance-testing-vs-stress-testing)
- [5. k6 Mental Model](#5-k6-mental-model)
- [6. URL Shortener Load Testing Targets](#6-url-shortener-load-testing-targets)
- [7. What We Should Measure](#7-what-we-should-measure)
- [8. Test Environment Architecture](#8-test-environment-architecture)
- [9. Installing k6](#9-installing-k6)
- [10. First Smoke Test Script](#10-first-smoke-test-script)
- [11. Create Short URL Load Test](#11-create-short-url-load-test)
- [12. Redirect Load Test](#12-redirect-load-test)
- [13. Mixed Traffic Test](#13-mixed-traffic-test)
- [14. Stages, VUs, RPS, and Arrival Rate](#14-stages-vus-rps-and-arrival-rate)
- [15. Thresholds: Turning Performance Into Pass or Fail](#15-thresholds-turning-performance-into-pass-or-fail)
- [16. Reading k6 Output](#16-reading-k6-output)
- [17. Spring Boot Metrics To Watch During k6](#17-spring-boot-metrics-to-watch-during-k6)
- [18. Database Metrics To Watch](#18-database-metrics-to-watch)
- [19. Redis Metrics To Watch](#19-redis-metrics-to-watch)
- [20. Kafka Metrics To Watch](#20-kafka-metrics-to-watch)
- [21. Step-by-Step Dry Runs](#21-step-by-step-dry-runs)
- [22. Internal Execution Walkthrough](#22-internal-execution-walkthrough)
- [23. Bottleneck Discovery Playbook](#23-bottleneck-discovery-playbook)
- [24. Production Failure Stories](#24-production-failure-stories)
- [25. Debugging Mindset](#25-debugging-mindset)
- [26. Common Mistakes](#26-common-mistakes)
- [27. CI/CD Load Test Gate](#27-cicd-load-test-gate)
- [28. Interview-Ready Explanation](#28-interview-ready-explanation)
- [29. Senior Engineer Checklist](#29-senior-engineer-checklist)
- [30. One-Page Cheat Sheet](#30-one-page-cheat-sheet)
- [31. One Picture To Remember](#31-one-picture-to-remember)

---

## 1. Why This Exists

Your MiniURLShortener now has production-style building blocks:

```text
Create API
Redirect API
Redis cache
Postgres
Kafka click analytics
Workers
Observability
SLO / SLA / error budget
```

But one question remains:

```text
Can it survive real traffic?
```

Normal functional tests answer:

```text
Does the API work once?
```

Load tests answer:

```text
Does the API still work when 1000, 5000, 20000, or 50000 requests per second hit it?
```

Without load testing, you may believe your system is production-ready because all unit tests pass.

Reality:

```text
Unit tests pass.
Integration tests pass.
Docker compose starts.
One manual curl works.
Then real users arrive.
CPU spikes.
DB pool exhausts.
Redis times out.
Kafka lag grows.
p99 becomes 5 seconds.
Error rate explodes.
```

Load testing prevents this surprise.

Production mental model:

```text
A backend is not production-ready until it has been attacked by realistic traffic and measured under pressure.
```

For URL shortener, traffic is usually asymmetric:

```text
Redirect API  : very high read traffic
Create API    : lower write traffic
Analytics     : async Kafka traffic
Admin APIs    : low traffic
```

So your k6 tests should not blindly hit one endpoint.

They should model real behavior:

```text
95% redirects
4% creates
1% health/admin/other
```

---

## 2. The One Core Mental Model

Load testing is a controlled pressure experiment.

ASCII:

```text
                 CONTROLLED TRAFFIC
                       |
                       v
+------------------------------------------------+
| k6                                             |
| - creates virtual users                       |
| - sends HTTP requests                         |
| - measures latency, errors, throughput         |
+------------------------------------------------+
                       |
                       v
+------------------------------------------------+
| Application                                    |
| Spring Boot + Redis + Postgres + Kafka         |
+------------------------------------------------+
                       |
                       v
+------------------------------------------------+
| Metrics                                        |
| p50, p95, p99, error rate, RPS, CPU, DB pool   |
+------------------------------------------------+
                       |
                       v
+------------------------------------------------+
| Decision                                       |
| pass, fail, bottleneck, tune, retest           |
+------------------------------------------------+
```

One-line memory:

```text
k6 applies pressure; metrics reveal the weakest layer.
```

The goal is not only to get a big RPS number.

The real goal is to understand:

```text
At what load does latency rise?
At what load do errors start?
Which dependency saturates first?
Which change improves or hurts performance?
Can the system meet its SLO under realistic traffic?
```

---

## 3. Problem Statement

Build a production-shaped k6 load testing setup for MiniURLShortener.

It must support:

```text
1. Smoke test for quick sanity.
2. Create URL load test.
3. Redirect load test.
4. Mixed traffic test.
5. Fixed VU tests.
6. Constant arrival rate tests.
7. Threshold-based pass/fail.
8. p95/p99 latency checks.
9. Error-rate checks.
10. Debugging bottlenecks across Spring Boot, Postgres, Redis, and Kafka.
```

It should answer:

```text
Can redirect API sustain 1000 RPS?
Can create API sustain 100 RPS?
What is p99 latency under load?
Does DB connection pool saturate?
Does Redis help or hurt?
Does Kafka click logging create backpressure?
Does error budget burn too fast?
```

Out of scope:

```text
1. Full cloud distributed load generation.
2. Global multi-region testing.
3. Chaos testing.
4. Browser UI testing.
5. Paid k6 cloud dashboards.
```

This chapter focuses on local and CI-friendly k6 scripts.

---

## 4. Load Testing vs Performance Testing vs Stress Testing

These terms are often mixed.

### Performance Testing

General category.

It asks:

```text
How fast is the system?
```

Example:

```text
What is p95 latency for GET /abc123?
```

### Load Testing

Tests expected or target traffic.

It asks:

```text
Can the system handle planned production load?
```

Example:

```text
Can redirect API handle 1000 RPS with p95 < 100 ms?
```

### Stress Testing

Pushes beyond expected load until something breaks.

It asks:

```text
Where is the breaking point?
```

Example:

```text
At what RPS does error rate exceed 1%?
```

### Spike Testing

Sudden traffic jump.

It asks:

```text
Can the system absorb a sudden viral link?
```

Example:

```text
100 RPS -> 10000 RPS in 30 seconds.
```

### Soak Testing

Long duration load.

It asks:

```text
Does the system degrade over time?
```

Example:

```text
1000 RPS for 2 hours.
```

ASCII:

```text
Normal Load Test:

RPS
 ^
 |             +----------------+
 |             | target traffic |
 |             |                |
 |_____________+                +__________> time

Stress Test:

RPS
 ^
 |                         break
 |                           X
 |                       /
 |                   /
 |               /
 |___________/___________________________> time

Spike Test:

RPS
 ^
 |              +-----------+
 |              | spike     |
 |______________|           |____________> time
```

---

## 5. k6 Mental Model

k6 is a programmable load generator.

You write JavaScript.
k6 runs virtual users.
Virtual users call your API.
k6 records metrics.
Thresholds decide pass/fail.

ASCII:

```text
k6 Script
   |
   v
+-----------------------------+
| options                     |
| - stages                    |
| - thresholds                |
| - scenarios                 |
+-----------------------------+
   |
   v
+-----------------------------+
| default function            |
| one virtual user iteration  |
+-----------------------------+
   |
   v
+-----------------------------+
| HTTP requests               |
| checks                      |
| sleep                       |
+-----------------------------+
   |
   v
+-----------------------------+
| metrics output              |
| http_req_duration           |
| http_req_failed             |
| iterations                  |
+-----------------------------+
```

A basic k6 script has three parts:

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
};

export default function () {
  const res = http.get('http://localhost:8080/actuator/health');

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
```

Important terms:

```text
VU       = Virtual User
iteration = one execution of default function by one VU
check    = assertion, but does not stop test by default
threshold = pass/fail rule for metrics
scenario = traffic model
```

Mental model:

```text
VU is not exactly RPS.
VU is simulated user concurrency.
RPS depends on latency and sleep.
```

Example:

```text
10 VUs with 100 ms response and no sleep can produce high RPS.
10 VUs with 1 second sleep produces much lower RPS.
```

---

## 6. URL Shortener Load Testing Targets

A URL shortener is read-heavy.

Typical target shape:

```text
Redirects  : 90% - 99%
Creates    : 1% - 10%
Analytics  : async side effect
```

For your staged learning path:

```text
Level 1: 100 RPS
Level 2: 1000 RPS
Level 3: 5000 RPS
Level 4: 10000 RPS
Level 5: 20000+ RPS
```

Example target SLO:

```text
Redirect API:
    availability >= 99.9%
    p95 latency <= 100 ms
    p99 latency <= 250 ms

Create API:
    availability >= 99.5%
    p95 latency <= 300 ms
    p99 latency <= 800 ms
```

Why redirect is stricter:

```text
Redirect is user-facing and extremely frequent.
Create is less frequent and writes to DB.
```

ASCII:

```text
Traffic Shape

Requests
 ^
 |
 | Redirect API  ################################################## 95%
 | Create API    ##                                                  4%
 | Other         #                                                   1%
 |
 +---------------------------------------------------------------> endpoint
```

Load testing goal:

```text
Find the highest RPS where SLO still passes.
```

Not:

```text
Find the largest number that appears once in terminal.
```

---

## 7. What We Should Measure

k6 gives client-side metrics.
Your app and infrastructure give server-side metrics.

Both are needed.

### k6 Client-Side Metrics

```text
http_req_duration     total request latency
http_req_waiting      time waiting for server response
http_req_failed       failed request rate
http_reqs             total HTTP requests
checks                check success rate
vus                   active virtual users
iterations            completed user iterations
```

Latency percentiles:

```text
p50 = median user experience
p95 = slowest 5% user experience
p99 = worst 1% user experience
```

Why p99 matters:

```text
Average can look good while real users suffer.
```

Example:

```text
100 requests:
99 requests = 50 ms
1 request   = 5000 ms

Average may hide pain.
p99 exposes pain.
```

ASCII:

```text
Latency Distribution

fast                                                     slow
 |----------------------------------------------------------|
 50ms  55ms  60ms  70ms  80ms  100ms  200ms  900ms  5000ms
      ^                         ^                   ^
     p50                       p95                 p99/max
```

### Server-Side Metrics

Watch:

```text
Spring Boot:
    CPU
    memory
    GC pauses
    Tomcat threads
    Hikari active connections
    request latency

Postgres:
    active connections
    slow queries
    locks
    CPU / IO
    index usage

Redis:
    hit ratio
    command latency
    evictions
    memory

Kafka:
    producer latency
    consumer lag
    broker throughput
```

Rule:

```text
k6 tells you users are slow.
Server metrics tell you why.
```

---

## 8. Test Environment Architecture

Do not run load generator on the same tiny machine and blindly trust results.

If k6 and app fight for CPU, k6 may become the bottleneck.

Local learning architecture:

```text
+---------------------+        HTTP        +---------------------+
| k6                  | ----------------> | Spring Boot App     |
| load generator      |                   | MiniURLShortener    |
+---------------------+                   +----------+----------+
                                                   |
                         +-------------------------+-------------------------+
                         |                         |                         |
                         v                         v                         v
                 +--------------+          +--------------+          +--------------+
                 | Postgres     |          | Redis        |          | Kafka        |
                 +--------------+          +--------------+          +--------------+
```

Better production-like architecture:

```text
+---------------------+        HTTP        +---------------------+
| k6 machine          | ----------------> | Load Balancer       |
| only generates load |                   +----------+----------+
+---------------------+                              |
                                                     v
                                         +---------------------+
                                         | App replicas        |
                                         | Spring Boot pods    |
                                         +----------+----------+
                                                    |
                    +-------------------------------+-------------------------------+
                    |                               |                               |
                    v                               v                               v
             +-------------+                 +-------------+                 +-------------+
             | Postgres    |                 | Redis       |                 | Kafka       |
             +-------------+                 +-------------+                 +-------------+
```

For learning, Docker Compose is enough.

But remember:

```text
Local laptop results are useful for bottleneck discovery.
They are not exact production capacity numbers.
```

---

## 9. Installing k6

### macOS

```bash
brew install k6
```

### Ubuntu / Debian

```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg \
  --keyserver hkp://keyserver.ubuntu.com:80 \
  --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69

echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" \
  | sudo tee /etc/apt/sources.list.d/k6.list

sudo apt update
sudo apt install k6
```

### Windows

```powershell
winget install k6 --source winget
```

### Docker

```bash
docker run --rm -i grafana/k6 run - <script.js
```

Recommended project structure:

```text
MiniURLShortener/
├── src/
├── docker-compose.yml
├── k6/
│   ├── 001_smoke.js
│   ├── 002_create_url.js
│   ├── 003_redirect.js
│   ├── 004_mixed_traffic.js
│   └── data/
│       └── short_codes.json
└── README.md
```

---

## 10. First Smoke Test Script

File:

```text
k6/001_smoke.js
```

Code:

```javascript
import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1,
  iterations: 1,
  thresholds: {
    http_req_failed: ['rate==0'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  const res = http.get('http://localhost:8080/actuator/health');

  check(res, {
    'health status is 200': (r) => r.status === 200,
    'body contains UP': (r) => r.body.includes('UP'),
  });
}
```

Run:

```bash
k6 run k6/001_smoke.js
```

Purpose:

```text
Before load testing, verify app is reachable.
```

Smoke test is not load test.

It only asks:

```text
Is the system alive enough to test?
```

ASCII:

```text
Smoke Test

k6 ---- one request ----> /actuator/health
              |
              v
          200 OK ?
```

---

## 11. Create Short URL Load Test

Create API is write-heavy.

It touches:

```text
Controller
validation
service
ID generation
Postgres insert
optional Redis write
optional Kafka event
```

File:

```text
k6/002_create_url.js
```

Code:

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m', target: 20 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<300', 'p(99)<800'],
  },
};

export default function () {
  const uniquePath = randomString(12);

  const payload = JSON.stringify({
    longUrl: `https://example.com/product/${uniquePath}`,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(`${BASE_URL}/api/v1/urls`, payload, params);

  check(res, {
    'create status is 201 or 200': (r) => r.status === 201 || r.status === 200,
    'response has shortCode': (r) => {
      try {
        return JSON.parse(r.body).shortCode !== undefined;
      } catch (e) {
        return false;
      }
    },
  });

  sleep(1);
}
```

Run:

```bash
BASE_URL=http://localhost:8080 k6 run k6/002_create_url.js
```

What this test reveals:

```text
DB insert throughput
validation cost
ID generation collision behavior
Hikari pool pressure
Postgres write latency
error handling under duplicate/invalid cases if added
```

ASCII:

```text
k6 VUs
  |
  v
POST /api/v1/urls
  |
  v
+------------+     +----------+     +----------+
| Spring App | --> | Postgres | --> | response |
+------------+     +----------+     +----------+
       |
       +--> optional Kafka click/create event
```

Important:

```text
Use unique longUrl or alias during create tests.
Otherwise duplicate conflicts pollute results.
```

---

## 12. Redirect Load Test

Redirect API is the most important endpoint.

It should be fast.

Possible paths:

```text
cache hit
cache miss + DB lookup + cache fill
not found
expired
blocked
```

First, prepare some short codes.

You can create a seed script or manually create 1000 short URLs and save codes.

Example data file:

```text
k6/data/short_codes.json
```

Example:

```json
[
  "abc123",
  "xYz789",
  "sale01"
]
```

File:

```text
k6/003_redirect.js
```

Code:

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const codes = JSON.parse(open('./data/short_codes.json'));

export const options = {
  stages: [
    { duration: '30s', target: 100 },
    { duration: '2m', target: 100 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.001'],
    http_req_duration: ['p(95)<100', 'p(99)<250'],
  },
};

export default function () {
  const code = codes[Math.floor(Math.random() * codes.length)];

  const res = http.get(`${BASE_URL}/${code}`, {
    redirects: 0,
  });

  check(res, {
    'redirect status is 302 or 301': (r) => r.status === 302 || r.status === 301,
    'location header exists': (r) => r.headers.Location !== undefined,
  });

  sleep(0.1);
}
```

Run:

```bash
BASE_URL=http://localhost:8080 k6 run k6/003_redirect.js
```

Why `redirects: 0`?

Because you want to test your redirect service, not the target external website.

Wrong:

```text
k6 follows redirect to example.com
latency includes external internet
result becomes noisy
```

Correct:

```text
k6 stops at 302
measures your backend only
```

ASCII:

```text
Correct Redirect Test

k6 ---> GET /abc123 ---> MiniURLShortener ---> 302 Location
 ^                                                 |
 |_________________________________________________|
             measure only this system

Wrong Redirect Test

k6 ---> GET /abc123 ---> MiniURLShortener ---> example.com
                                           external network noise
```

---

## 13. Mixed Traffic Test

Real traffic is mixed.

For URL shortener:

```text
95% redirect
5% create
```

File:

```text
k6/004_mixed_traffic.js
```

Code:

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const codes = JSON.parse(open('./data/short_codes.json'));

export const options = {
  scenarios: {
    mixed_traffic: {
      executor: 'constant-arrival-rate',
      rate: 1000,
      timeUnit: '1s',
      duration: '2m',
      preAllocatedVUs: 200,
      maxVUs: 1000,
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.005'],
    http_req_duration: ['p(95)<150', 'p(99)<400'],
    'http_req_duration{endpoint:redirect}': ['p(95)<100', 'p(99)<250'],
    'http_req_duration{endpoint:create}': ['p(95)<300', 'p(99)<800'],
  },
};

function redirectFlow() {
  const code = codes[Math.floor(Math.random() * codes.length)];

  const res = http.get(`${BASE_URL}/${code}`, {
    redirects: 0,
    tags: { endpoint: 'redirect' },
  });

  check(res, {
    'redirect is 302 or 301': (r) => r.status === 302 || r.status === 301,
  });
}

function createFlow() {
  const uniquePath = randomString(12);

  const payload = JSON.stringify({
    longUrl: `https://example.com/page/${uniquePath}`,
  });

  const res = http.post(`${BASE_URL}/api/v1/urls`, payload, {
    headers: { 'Content-Type': 'application/json' },
    tags: { endpoint: 'create' },
  });

  check(res, {
    'create is 201 or 200': (r) => r.status === 201 || r.status === 200,
  });
}

export default function () {
  const n = Math.random();

  if (n < 0.95) {
    redirectFlow();
  } else {
    createFlow();
  }

  sleep(0.01);
}
```

Run:

```bash
BASE_URL=http://localhost:8080 k6 run k6/004_mixed_traffic.js
```

ASCII:

```text
Mixed Traffic

1000 requests/sec
      |
      +-------------------- 950 redirect/sec
      |
      +--------------------  50 create/sec

             +----------------------+
             | MiniURLShortener     |
             +----------+-----------+
                        |
         +--------------+--------------+
         |                             |
         v                             v
      Redis                         Postgres
```

This test is much more realistic than only testing create or only testing redirect.

---

## 14. Stages, VUs, RPS, and Arrival Rate

This is the most important k6 concept.

### VU-Based Testing

Example:

```javascript
export const options = {
  vus: 100,
  duration: '1m',
};
```

Meaning:

```text
Run 100 virtual users for 1 minute.
```

RPS is not fixed.

If app is fast:

```text
100 VUs produce more RPS.
```

If app is slow:

```text
same 100 VUs produce less RPS.
```

ASCII:

```text
VU-Based Test

100 users keep trying
      |
      v
RPS depends on response time
```

### Arrival-Rate Testing

Example:

```javascript
scenarios: {
  redirect_1000_rps: {
    executor: 'constant-arrival-rate',
    rate: 1000,
    timeUnit: '1s',
    duration: '2m',
    preAllocatedVUs: 200,
    maxVUs: 1000,
  }
}
```

Meaning:

```text
Try to start 1000 iterations every second.
```

This is closer to RPS target testing.

ASCII:

```text
Arrival Rate Test

Time second 1: start 1000 iterations
Time second 2: start 1000 iterations
Time second 3: start 1000 iterations
```

### Rule of Thumb

Use VU-based tests for:

```text
simple learning
user journey simulation
soak tests
```

Use arrival-rate tests for:

```text
explicit RPS targets
SLO validation
CI performance gates
```

### Simple capacity formula

```text
Required concurrency ≈ RPS × average latency in seconds
```

Example:

```text
Target RPS = 1000
Average latency = 100 ms = 0.1 sec
Required concurrency ≈ 1000 × 0.1 = 100 active requests
```

If p99 becomes 1 second:

```text
Required concurrency ≈ 1000 × 1 = 1000 active requests
```

This is why latency explosion causes thread and connection exhaustion.

---

## 15. Thresholds: Turning Performance Into Pass or Fail

A load test without thresholds is just noise.

Thresholds convert metrics into a decision.

Example:

```javascript
thresholds: {
  http_req_failed: ['rate<0.01'],
  http_req_duration: ['p(95)<300', 'p(99)<800'],
}
```

Meaning:

```text
Fail test if error rate >= 1%.
Fail test if p95 latency >= 300 ms.
Fail test if p99 latency >= 800 ms.
```

Endpoint-specific thresholds:

```javascript
thresholds: {
  'http_req_duration{endpoint:redirect}': ['p(95)<100'],
  'http_req_duration{endpoint:create}': ['p(95)<300'],
}
```

Why endpoint-specific thresholds matter:

```text
Redirect and create APIs have different performance expectations.
```

ASCII:

```text
Test Result

Metrics
  |
  v
+-----------------------------+
| p95 redirect < 100 ms ?     |
| p99 redirect < 250 ms ?     |
| error rate < 0.1% ?         |
+-----------------------------+
      |
      +-- yes --> PASS
      |
      +-- no  --> FAIL and investigate
```

Good threshold examples for MiniURLShortener:

```text
Smoke:
    failure rate = 0
    p95 < 500 ms

Redirect:
    failure rate < 0.1%
    p95 < 100 ms
    p99 < 250 ms

Create:
    failure rate < 1%
    p95 < 300 ms
    p99 < 800 ms

Mixed:
    failure rate < 0.5%
    p95 < 150 ms
    p99 < 400 ms
```

---

## 16. Reading k6 Output

Example output:

```text
http_req_duration..............: avg=42.3ms min=4.1ms med=18.2ms max=900ms p(90)=80ms p(95)=120ms
http_req_failed................: 0.20%  ✓ 20 ✗ 9980
http_reqs......................: 10000  999.5/s
checks.........................: 99.8%  ✓ 9980 ✗ 20
```

How to read:

```text
avg      = average latency
med      = p50
p(95)    = 95% of requests were faster than this
max      = worst request
http_reqs/s = achieved throughput
failed   = HTTP failures according to k6
checks   = your custom assertions
```

Important difference:

```text
http_req_failed can be false even if business check fails.
checks can fail even when HTTP status is technically 200.
```

Example:

```text
HTTP 200 but response body missing shortCode.
```

That should fail a check.

Debug interpretation table:

```text
+-------------------------------+------------------------------------+
| Observation                   | Meaning                            |
+-------------------------------+------------------------------------+
| p95 high, errors low          | system slow but still responding   |
| errors high, latency low      | fast failures, maybe validation    |
| p99 huge, p95 okay            | tail latency problem               |
| RPS below target              | k6 lacks VUs or app too slow       |
| checks failing, HTTP okay     | business response incorrect        |
| http_req_blocked high         | client/network connection issue    |
+-------------------------------+------------------------------------+
```

Golden rule:

```text
Do not celebrate RPS if p99 and error rate are bad.
```

---

## 17. Spring Boot Metrics To Watch During k6

During load test, watch Spring Boot Actuator and Micrometer metrics.

Important app metrics:

```text
http.server.requests
jvm.memory.used
jvm.gc.pause
system.cpu.usage
process.cpu.usage
tomcat.threads.current
tomcat.threads.busy
hikaricp.connections.active
hikaricp.connections.pending
hikaricp.connections.timeout
```

ASCII:

```text
Request Load
    |
    v
+-------------------+
| Tomcat Threads    |
+-------------------+
    |
    v
+-------------------+
| Service Logic     |
+-------------------+
    |
    v
+-------------------+
| Hikari Pool       |
+-------------------+
    |
    v
+-------------------+
| Postgres          |
+-------------------+
```

If Tomcat busy threads max out:

```text
Requests queue inside app.
Latency rises.
Eventually timeouts happen.
```

If Hikari pending connections rise:

```text
Threads are waiting for DB connections.
DB or pool is bottleneck.
```

If GC pauses rise:

```text
Too much allocation or heap pressure.
Tail latency increases.
```

Typical symptoms:

```text
High CPU + low DB usage:
    app CPU bottleneck

Low CPU + high Hikari pending:
    DB connection bottleneck

High GC pause + p99 spikes:
    memory allocation bottleneck

High Tomcat busy + high latency:
    request thread saturation
```

---

## 18. Database Metrics To Watch

Postgres can become the first bottleneck for create API and cache misses.

Watch:

```text
active connections
idle connections
slow queries
locks
CPU
IO wait
index hit ratio
buffer cache hit ratio
deadlocks
rows inserted/sec
```

Useful SQL:

```sql
SELECT count(*)
FROM pg_stat_activity;
```

```sql
SELECT state, count(*)
FROM pg_stat_activity
GROUP BY state;
```

```sql
SELECT query, calls, mean_exec_time, max_exec_time
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

```sql
SELECT relname, idx_scan, seq_scan
FROM pg_stat_user_tables
ORDER BY seq_scan DESC;
```

ASCII:

```text
Create API pressure
      |
      v
Application threads
      |
      v
Hikari pool
      |
      v
Postgres connections
      |
      v
Indexes / WAL / disk
```

Common DB bottlenecks:

```text
No index on short_code
Too many DB connections
Slow inserts due to disk/WAL
Lock contention
Connection pool too small
Connection pool too large and DB overloaded
```

Important:

```text
Increasing Hikari max pool is not always a fix.
```

If DB can efficiently handle 50 active queries, setting pool to 300 may make everything worse.

Mental model:

```text
Connection pool is a valve, not a magic performance multiplier.
```

---

## 19. Redis Metrics To Watch

Redirect API should use Redis cache for hot short codes.

Watch:

```text
cache hit ratio
cache miss ratio
command latency
used memory
evicted keys
connected clients
ops/sec
network bandwidth
```

ASCII:

```text
Redirect request
      |
      v
+----------------------+
| Redis GET shortCode  |
+----------+-----------+
           |
      hit  | miss
           |
           +-----------------> Postgres lookup
```

High cache hit ratio means:

```text
Most redirects avoid DB.
```

Low cache hit ratio means:

```text
DB may become bottleneck under redirect traffic.
```

Bad sign:

```text
Redis evictions increasing during load test.
```

It means:

```text
Redis memory is insufficient or TTL/key policy is wrong.
```

Redis failure symptoms:

```text
Redis latency high -> redirect p99 high
Redis unavailable -> fallback to DB -> DB load spike
Redis memory full -> evictions -> more DB misses
```

Production rule:

```text
Cache improves read latency only if hit ratio is high and Redis itself is healthy.
```

---

## 20. Kafka Metrics To Watch

If redirect sends click analytics to Kafka, load tests must observe Kafka.

Watch:

```text
producer send latency
producer errors
record send rate
batch size
buffer available bytes
consumer lag
consumer processing rate
DLQ count
retry topic count
```

ASCII:

```text
Redirect API
    |
    v
Return 302 quickly
    |
    +-- async click event --> Kafka topic --> analytics worker
```

Important design rule:

```text
Redirect should not wait too long for analytics.
```

If Kafka is synchronous in request path:

```text
Kafka slowness becomes user-facing redirect slowness.
```

Better:

```text
Keep redirect path fast.
Use async event publishing carefully.
Use timeout/fallback/drop strategy depending on business need.
```

Kafka bottleneck symptoms:

```text
p99 redirect increases when Kafka latency increases
consumer lag grows continuously
producer buffer fills
retry/DLQ topics grow
```

Question to ask:

```text
Is click analytics critical enough to slow down redirect users?
```

Usually answer:

```text
No. Redirect is primary. Analytics is secondary.
```

---

## 21. Step-by-Step Dry Runs

### Dry Run 1: Smoke test passes

Command:

```bash
k6 run k6/001_smoke.js
```

Flow:

```text
1. k6 starts one VU.
2. VU sends GET /actuator/health.
3. Spring Boot returns 200.
4. check validates status.
5. threshold validates latency.
6. test passes.
```

Conclusion:

```text
System is reachable.
Now real load test can start.
```

---

### Dry Run 2: Redirect load with cache hit

Request:

```http
GET /abc123
```

Flow:

```text
1. k6 selects abc123 from short_codes.json.
2. k6 sends GET /abc123 with redirects disabled.
3. Spring Boot receives request.
4. Service checks Redis.
5. Redis returns longUrl.
6. App returns 302 Location.
7. k6 records latency 15 ms.
8. check passes.
```

ASCII:

```text
k6 -> App -> Redis hit -> App -> 302
```

Expected:

```text
low latency
low DB usage
high Redis hit ratio
```

---

### Dry Run 3: Redirect load with cache miss

Flow:

```text
1. k6 sends GET /cold999.
2. Redis lookup misses.
3. App queries Postgres by short_code.
4. DB returns row.
5. App writes Redis cache.
6. App returns 302.
7. k6 records latency 80 ms.
```

ASCII:

```text
k6 -> App -> Redis miss -> Postgres -> Redis SET -> 302
```

Expected:

```text
higher latency than cache hit
DB query count increases
Redis SET rate increases
```

---

### Dry Run 4: Create API under load

Flow:

```text
1. k6 VU creates unique longUrl.
2. POST /api/v1/urls.
3. DTO validation passes.
4. Service generates shortCode.
5. Postgres insert happens.
6. Response returns shortCode.
7. k6 check verifies response.
```

Possible bottlenecks:

```text
DB insert latency
unique constraint conflict
Hikari pool waiting
WAL/disk pressure
```

---

### Dry Run 5: Threshold fails

Output:

```text
http_req_duration p(95)=420ms
threshold p(95)<300 failed
```

Meaning:

```text
API may still respond, but SLO is not met.
```

Correct reaction:

```text
Do not say test passed because there were few errors.
Investigate why latency is too high.
```

---

## 22. Internal Execution Walkthrough

A full mixed load test looks like this:

```text
1. k6 starts constant-arrival-rate scenario.
2. It tries to start 1000 iterations per second.
3. Each iteration randomly chooses redirect or create.
4. Redirect path mostly reads Redis.
5. Cache misses read Postgres.
6. Create path writes Postgres.
7. Redirect may publish Kafka click event.
8. k6 records client-side latency and status.
9. Spring Boot records server-side metrics.
10. Postgres, Redis, Kafka expose dependency metrics.
11. Thresholds decide pass/fail.
12. Engineer compares k6 symptoms with server metrics.
```

ASCII:

```text
                            +------------------+
                            | k6               |
                            | 1000 iterations/s|
                            +--------+---------+
                                     |
                  +------------------+------------------+
                  |                                     |
                  v                                     v
          GET /{shortCode}                       POST /api/v1/urls
                  |                                     |
                  v                                     v
        +-------------------+              +-------------------+
        | Spring Boot App   |              | Spring Boot App   |
        +---------+---------+              +---------+---------+
                  |                                  |
        +---------+----------+                       |
        |                    |                       |
        v                    v                       v
     Redis                Postgres                Postgres
        |                    |                       |
        +---------+----------+-----------------------+
                  |
                  v
                Kafka
                  |
                  v
          Analytics Worker
```

The most important skill:

```text
Connect client symptoms to backend causes.
```

Example:

```text
k6 p99 high
    + Hikari pending high
        -> DB pool bottleneck

k6 p99 high
    + Redis latency high
        -> Redis bottleneck

k6 error rate high
    + app logs timeout
        -> dependency timeout
```

---

## 23. Bottleneck Discovery Playbook

Use a controlled sequence.

### Step 1: Smoke test

```bash
k6 run k6/001_smoke.js
```

Question:

```text
Is app reachable?
```

### Step 2: Single endpoint baseline

Test redirect only.

```bash
k6 run k6/003_redirect.js
```

Question:

```text
What is redirect latency without create traffic?
```

### Step 3: Create endpoint baseline

```bash
k6 run k6/002_create_url.js
```

Question:

```text
What is write latency and DB pressure?
```

### Step 4: Mixed realistic test

```bash
k6 run k6/004_mixed_traffic.js
```

Question:

```text
Does create traffic hurt redirect latency?
```

### Step 5: Increase gradually

```text
100 RPS
500 RPS
1000 RPS
2000 RPS
5000 RPS
```

Do not jump directly to 50000 RPS.

ASCII:

```text
Load Ladder

100 RPS  -> stable?
500 RPS  -> stable?
1k RPS   -> stable?
2k RPS   -> stable?
5k RPS   -> stable?
10k RPS  -> stable?
```

At each step record:

```text
RPS
p95
p99
error rate
CPU
memory
Tomcat busy threads
Hikari active/pending
Postgres CPU/IO
Redis hit ratio
Kafka lag
```

### Step 6: Change one thing at a time

Bad experiment:

```text
increase Hikari pool
increase heap
add Redis
change indexes
change thread pool
rerun
```

You will not know what helped.

Good experiment:

```text
change only one variable
rerun same k6 test
compare results
```

Rule:

```text
One bottleneck experiment = one change.
```

---

## 24. Production Failure Stories

### Failure Story 1: Average latency looked fine

Dashboard showed:

```text
average latency = 80 ms
```

Users complained.

k6 showed:

```text
p99 latency = 2500 ms
```

Root cause:

```text
Most requests were cache hits.
Some cache misses went to slow DB query.
Average hid tail latency.
```

Fix:

```text
Add index on short_code.
Tune cache warmup.
Track p95 and p99, not only average.
```

Lesson:

```text
Average latency is not enough for production systems.
```

---

### Failure Story 2: k6 followed redirects

Redirect test looked slow:

```text
p95 = 900 ms
```

Root cause:

```text
k6 followed the 302 to external websites.
Latency included internet and third-party sites.
```

Fix:

```javascript
http.get(url, { redirects: 0 });
```

Lesson:

```text
Measure your system, not someone else's website.
```

---

### Failure Story 3: Hikari pool exhausted

Symptoms:

```text
p99 increased
errors started
app logs showed connection timeout
Hikari pending connections increased
```

Root cause:

```text
Create endpoint and cache misses competed for DB connections.
```

Fix options:

```text
Improve Redis hit ratio.
Add index.
Reduce DB query time.
Tune Hikari pool carefully.
Separate read/write paths.
Add read replica later.
```

Lesson:

```text
DB connection pool is often the first real bottleneck in Spring Boot apps.
```

---

### Failure Story 4: Kafka analytics slowed redirects

Symptoms:

```text
Redirect p99 increased when Kafka broker was slow.
```

Root cause:

```text
Request thread waited for Kafka send acknowledgment.
```

Fix:

```text
Make analytics publishing async with timeout/fallback.
Do not block redirect path unnecessarily.
Monitor producer latency and failures.
```

Lesson:

```text
Async side effects must not secretly become synchronous user-facing bottlenecks.
```

---

### Failure Story 5: Load generator was bottleneck

k6 could not reach target RPS.

App CPU was low.
DB was low.
Redis was low.

Root cause:

```text
k6 machine CPU/network was saturated.
```

Fix:

```text
Run k6 from stronger machine.
Use distributed load generation for very high RPS.
Check k6 host CPU.
```

Lesson:

```text
Always verify the load generator is not the limiting factor.
```

---

## 25. Debugging Mindset

When a k6 test fails, ask in order:

```text
1. Did k6 reach target RPS?
2. Did error rate fail?
3. Did latency threshold fail?
4. Which endpoint failed?
5. Is failure client-side or server-side?
6. What changed at the exact time latency rose?
7. Which backend metric saturated first?
```

Debug map:

```text
p95 high, CPU high:
    app CPU bottleneck

p95 high, Hikari pending high:
    DB connection bottleneck

p99 spikes, GC pause high:
    JVM memory/GC bottleneck

redirect slow, Redis latency high:
    Redis bottleneck

create slow, Postgres slow queries high:
    DB query/index/write bottleneck

Kafka lag growing:
    analytics workers cannot keep up

RPS below target, app idle:
    k6/load generator bottleneck or insufficient VUs
```

ASCII decision tree:

```text
k6 test failed
    |
    +-- error rate high?
    |       |
    |       +-- yes -> check app logs, status codes, dependency timeouts
    |
    +-- latency high?
    |       |
    |       +-- yes -> check CPU, GC, Tomcat, Hikari, DB, Redis, Kafka
    |
    +-- target RPS not reached?
            |
            +-- check maxVUs, k6 CPU, network, app slowness
```

Golden rule:

```text
Never tune blindly. First identify the saturated resource.
```

---

## 26. Common Mistakes

### Mistake 1: Testing only happy path

Wrong:

```text
Only test one valid short code forever.
```

Correct:

```text
Test hot codes, cold codes, missing codes, expired codes, and mixed traffic.
```

### Mistake 2: Following redirects

Wrong:

```javascript
http.get(`${BASE_URL}/${code}`);
```

Correct:

```javascript
http.get(`${BASE_URL}/${code}`, { redirects: 0 });
```

### Mistake 3: Confusing VUs with RPS

Wrong:

```text
100 VUs means 100 RPS.
```

Correct:

```text
RPS depends on latency, sleep, and scenario executor.
```

### Mistake 4: No thresholds

Wrong:

```text
Run test, manually look at output, guess pass/fail.
```

Correct:

```text
Define p95, p99, and error-rate thresholds.
```

### Mistake 5: Testing from same overloaded laptop

Wrong:

```text
App, DB, Redis, Kafka, and k6 all fight on one weak machine.
```

Correct:

```text
For learning okay, but for capacity claims separate load generator from system.
```

### Mistake 6: Using tiny data set

Wrong:

```text
Only 3 short codes, all hot in cache.
```

Correct:

```text
Use enough codes to model hot and cold distribution.
```

### Mistake 7: Ignoring server metrics

Wrong:

```text
Only read k6 output.
```

Correct:

```text
Correlate k6 with Spring Boot, Postgres, Redis, Kafka metrics.
```

### Mistake 8: Changing many variables at once

Wrong:

```text
Tune JVM, DB, Redis, code, and pool together.
```

Correct:

```text
One change, one retest, one conclusion.
```

---

## 27. CI/CD Load Test Gate

Load tests can run in CI, but keep them small.

Do not run huge 30-minute stress tests on every commit.

Recommended levels:

```text
Pull request:
    smoke test
    tiny 30-second baseline

Nightly:
    5-minute load test
    mixed traffic test

Before release:
    longer stress and soak tests
```

Example GitHub Actions step:

```yaml
name: k6-smoke

on:
  pull_request:

jobs:
  k6:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Start stack
        run: docker compose up -d --build

      - name: Wait for app
        run: |
          for i in {1..30}; do
            curl -f http://localhost:8080/actuator/health && exit 0
            sleep 2
          done
          exit 1

      - name: Run k6 smoke test
        uses: grafana/k6-action@v0.3.1
        with:
          filename: k6/001_smoke.js

      - name: Stop stack
        if: always()
        run: docker compose down -v
```

CI rule:

```text
Use short tests to prevent obvious regressions.
Use longer tests before release or nightly.
```

Performance regression example:

```text
Yesterday p95 redirect = 45 ms
Today p95 redirect = 160 ms
```

Even if functional tests pass, CI should highlight this.

---

## 28. Interview-Ready Explanation

If interviewer asks:

```text
How would you load test your URL shortener?
```

Strong answer:

```text
I would use k6 to model realistic read-heavy traffic. First I would run a smoke test
against health endpoints to verify the environment. Then I would separately baseline
redirect and create APIs, because redirect is read-heavy and latency-sensitive while
create is write-heavy and DB-bound. For redirect tests, I would disable following
redirects so I measure only my service's 302 response, not external websites. Then I
would run a mixed traffic scenario such as 95% redirects and 5% creates using constant
arrival rate to target explicit RPS levels. I would define thresholds for error rate,
p95, and p99 latency, with stricter thresholds for redirect. During the test I would
correlate k6 metrics with Spring Boot, Hikari, Postgres, Redis, and Kafka metrics to
identify whether the bottleneck is app threads, DB pool, slow queries, Redis latency,
or Kafka analytics backpressure. I would increase load gradually and change one tuning
variable at a time so conclusions are reliable.
```

Why this is strong:

```text
1. Models real traffic shape.
2. Separates read and write paths.
3. Knows redirect testing nuance.
4. Uses p95/p99 and error rate.
5. Uses constant arrival rate for RPS target.
6. Correlates client and server metrics.
7. Thinks in bottlenecks, not random tuning.
8. Shows production maturity.
```

Senior one-liner:

```text
I do not use load testing only to get an RPS number; I use it to validate SLOs and discover the first saturated layer under realistic traffic.
```

---

## 29. Senior Engineer Checklist

Before saying your load test is useful, confirm:

```text
[ ] Smoke test exists
[ ] Create API test exists
[ ] Redirect API test exists
[ ] Mixed traffic test exists
[ ] Redirect test disables external redirect following
[ ] Tests use realistic traffic percentages
[ ] Tests include p95 and p99 thresholds
[ ] Tests include error-rate thresholds
[ ] k6 target RPS is clear
[ ] VU vs RPS distinction is understood
[ ] Test data set is large enough
[ ] Hot and cold short codes are tested
[ ] Server metrics are watched during test
[ ] Hikari active/pending connections are watched
[ ] Postgres slow queries are watched
[ ] Redis hit ratio and latency are watched
[ ] Kafka producer latency and consumer lag are watched
[ ] k6 machine is not the bottleneck
[ ] Results are recorded per test run
[ ] Only one tuning variable changes per experiment
[ ] CI smoke/performance gate exists
```

If these are checked, your load testing setup is production-shaped.

---

## 30. One-Page Cheat Sheet

```text
Core mental model:
k6 applies controlled pressure; metrics reveal weakest layer.

Main test types:
Smoke  = is app alive?
Load   = can app handle expected traffic?
Stress = where does app break?
Spike  = can app survive sudden jump?
Soak   = does app degrade over time?

URL shortener traffic:
Redirect 90-99%
Create   1-10%

Important k6 metrics:
http_req_duration
http_req_failed
http_reqs
checks
vus
iterations

Important percentiles:
p50 = median
p95 = slowest 5%
p99 = worst 1%

Redirect test rule:
Use redirects: 0
Measure only your service.

Threshold examples:
Redirect p95 < 100 ms
Redirect p99 < 250 ms
Create p95 < 300 ms
Create p99 < 800 ms
Error rate < 0.1% or 1% depending endpoint

Use VU-based tests for:
learning and user journeys

Use arrival-rate tests for:
explicit RPS targets

Watch during test:
Spring CPU, GC, Tomcat threads
Hikari active/pending/timeouts
Postgres slow queries/connections/locks
Redis hit ratio/latency/evictions
Kafka producer latency/consumer lag

Debug rules:
p99 high + GC pause high -> JVM issue
p99 high + Hikari pending -> DB pool issue
redirect slow + Redis slow -> cache issue
create slow + DB slow -> write/index issue
RPS low + app idle -> k6 bottleneck

Golden rule:
Do not tune blindly. Find saturated resource first.
```

---

## 31. One Picture To Remember

```text
                    LOAD TESTING WITH k6 MENTAL MODEL

                              "Controlled Fire Drill"

+-------------------+       traffic        +---------------------------+
| k6                | -------------------> | Spring Boot App           |
| virtual users     |                      | MiniURLShortener          |
| scenarios         |                      +-------------+-------------+
| thresholds        |                                    |
+---------+---------+                                    |
          |                                              |
          | client metrics                               | server work
          v                                              v
+-------------------+                    +---------------+----------------+
| k6 Output         |                    | Dependencies                    |
| p50 / p95 / p99   |                    | Redis / Postgres / Kafka        |
| error rate        |                    +---------------+----------------+
| achieved RPS      |                                    |
+---------+---------+                                    |
          |                                              |
          +----------------------+-----------------------+
                                 |
                                 v
                    +-----------------------------+
                    | Bottleneck Decision         |
                    | CPU? GC? Hikari? DB? Redis? |
                    | Kafka? k6 machine?          |
                    +-------------+---------------+
                                  |
                                  v
                    +-----------------------------+
                    | Tune one thing, retest      |
                    +-----------------------------+

FINAL MEMORY:

Load testing is not about showing a big RPS number.
It is about proving your SLO under realistic traffic
and finding the first layer that breaks.
```

---

## Final Retention Summary

Remember these five sentences:

```text
1. k6 generates controlled traffic so you can observe system behavior under pressure.
2. Redirect tests must disable following redirects, otherwise you measure external websites.
3. VUs are concurrency, not RPS; use constant-arrival-rate when you need explicit RPS.
4. p95, p99, and error rate matter more than average latency.
5. k6 tells you what users feel; Spring Boot, Postgres, Redis, and Kafka metrics tell you why.
```

After this chapter, MiniURLShortener observability and performance validation has a practical load testing foundation:

```text
057 Prometheus Metrics
058 Grafana Dashboards
059 Distributed Tracing
060 SLO / SLA / Error Budget
061 Load Testing with k6
```
