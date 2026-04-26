# 11 — Design a Notification System

> Goal: design a scalable notification system that supports mobile push notifications, SMS, and email.

---

# Step 0 — What is a Notification System?

A notification system alerts users about important events.

```text
Push:  "Your package has shipped"
SMS:   "Your OTP is 123456"
Email: "Your invoice is ready"
```

Supported channels:

```text
iOS Push -> APNs
Android Push -> FCM
SMS -> SMS Provider
Email -> Email Provider
```

---

# Step 1 — Requirements

## Functional Requirements

- Send notifications through push, SMS, and email.
- Notifications can be triggered by backend services or scheduled jobs.
- Users can opt out.
- A user can have multiple devices.
- Support templates.
- Track status and engagement.

## Non-functional Requirements

- Highly available.
- Scalable to millions of notifications/day.
- Soft real-time delivery.
- No notification data loss.
- Retry failed sends.
- Reduce duplicates.
- Rate limit users and services.

## Scale

```text
Push:  10M/day
SMS:    1M/day
Email:  5M/day
Total: 16M/day

Average throughput:
16M / 86400 ≈ 185 notifications/sec
```

Interview line:

> Notifications can be delayed or duplicated occasionally, but they should not be lost.

---

# Step 2 — Channel Basics

## iOS Push

```text
Provider -> APNs -> iOS Device
```

## Android Push

```text
Provider -> FCM -> Android Device
```

## SMS

```text
Provider -> SMS Service -> Phone
```

## Email

```text
Provider -> Email Service -> Inbox
```

Visual:

```text
                  +-------> APNs --------> iOS
                  |
Notification API -+-------> FCM ---------> Android
                  |
                  +-------> SMS Provider -> Phone
                  |
                  +-------> Email Service -> Inbox
```

---

# Step 3 — Contact Info Collection

To send notifications, store:

```text
Push  -> device token
SMS   -> phone number
Email -> email address
```

Flow:

```text
User installs app / signs up
        |
        v
Load Balancer
        |
        v
API Servers
        |
        v
Database
```

Visual:

```text
+------+     +---------------+     +-------------+     +----+
| User | --> | Load Balancer | --> | API Servers | --> | DB |
+------+     +---------------+     +-------------+     +----+
```

---

# Step 4 — Data Model

## User Table

```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255),
    country_code VARCHAR(8),
    phone_number VARCHAR(32),
    created_at TIMESTAMP
);
```

## Device Table

```sql
CREATE TABLE devices (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    platform VARCHAR(32),        -- IOS / ANDROID
    device_token VARCHAR(512),
    last_logged_in_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

## Notification Settings

```sql
CREATE TABLE notification_settings (
    user_id BIGINT,
    channel VARCHAR(32),          -- PUSH / SMS / EMAIL
    notification_type VARCHAR(64),
    opt_in BOOLEAN,
    updated_at TIMESTAMP,
    PRIMARY KEY(user_id, channel, notification_type)
);
```

## Notification Log

```sql
CREATE TABLE notification_log (
    notification_id VARCHAR(128) PRIMARY KEY,
    user_id BIGINT,
    channel VARCHAR(32),
    status VARCHAR(32),           -- PENDING / SENT / DELIVERED / FAILED
    template_id VARCHAR(64),
    payload TEXT,
    retry_count INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

Relationship:

```text
users 1 -------- * devices
users 1 -------- * notification_settings
users 1 -------- * notification_log
```

---

# Step 5 — Naive Design

```text
Service 1
Service 2
Service N
    |
    v
+---------------------+
| Notification Server |
+----------+----------+
           |
   +-------+--------+--------+---------+
   |                |        |         |
   v                v        v         v
 APNs              FCM      SMS       Email
```

Problems:

- Single point of failure.
- Hard to scale.
- Third-party latency blocks the server.
- SMS outage can affect push/email.
- No buffering.
- No retry.

---

# Step 6 — Improved Design with Queues

```text
Service 1 / Service 2 / Cron Jobs
              |
              v
       +----------------------+
       | Notification Servers |
       | auth + validation    |
       +----------+-----------+
                  |
                  v
       +-----------------------+
       | Cache / DB Lookup     |
       | users, devices, prefs |
       +----------+------------+
                  |
       +----------+-----------+-----------+
       |                      |           |
       v                      v           v
+-------------+        +-------------+ +-------------+
| Push Queue  |        | SMS Queue   | | Email Queue |
+------+------+        +------+------+ +------+------+
       |                      |               |
       v                      v               v
+-------------+        +-------------+ +-------------+
| Push Worker |        | SMS Worker  | | EmailWorker |
+------+------+        +------+------+ +------+------+
       |                      |               |
       v                      v               v
   APNs/FCM              SMS Provider    Email Provider
```

Key idea:

> Notification servers accept requests quickly. Queues buffer work. Workers send notifications asynchronously.

---

# Step 7 — End-to-End Flow

```text
1. Service calls notification API.
2. Server authenticates caller.
3. Server validates request.
4. Server checks user settings.
5. Server fetches device token/email/phone.
6. Server renders template.
7. Server writes notification log as PENDING.
8. Server publishes event to channel queue.
9. Worker consumes event.
10. Worker sends to provider.
11. Worker updates status.
12. Tracking events update analytics.
```

Visual:

```text
Service
  |
  v
Notification API
  |
  v
Validate + Settings + Template
  |
  v
Notification Log: PENDING
  |
  v
Message Queue
  |
  v
Worker
  |
  v
Third-party Provider
  |
  v
User
```

---

# Step 8 — Notification API

```http
POST /v1/notifications/send
Authorization: Bearer internal-service-token
Content-Type: application/json
```

```json
{
  "notification_id": "evt-12345",
  "user_ids": [123, 456],
  "channel": "PUSH",
  "notification_type": "ORDER_UPDATE",
  "template_id": "order_shipped_v1",
  "template_data": {
    "userName": "Alex",
    "orderId": "A1001"
  }
}
```

Response:

```json
{
  "status": "accepted",
  "notification_id": "evt-12345"
}
```

Important:

```text
Return accepted quickly.
Actual delivery happens asynchronously.
```

---

# Step 9 — Templates

Template:

```text
Title:
Your order has shipped

Body:
Hi {{userName}}, your order {{orderId}} is on the way.
```

Flow:

```text
Template + Data
      |
      v
Rendered Notification
```

Benefits:

- consistent format,
- easy localization,
- fewer mistakes,
- reusable content.

---

# Step 10 — User Settings

Before sending:

```text
Check if user opted in for channel + notification type.
```

Flow:

```text
Notification Request
        |
        v
Check settings
        |
   +----+----+
   |         |
 opt-in    opt-out
   |         |
   v         v
send      skip
```

---

# Step 11 — Rate Limiting

Rate limit:

```text
Per user
Per channel
Per notification type
Per sending service
Per provider quota
```

Examples:

```text
Max 5 marketing pushes/user/day
Max 3 OTP SMS/user/minute
Max 100K emails/provider/minute
```

Visual:

```text
Notification Request
        |
        v
Rate Limiter
        |
   +----+----+
   |         |
allowed    blocked
   |         |
   v         v
queue      reject/skip
```

---

# Step 12 — Reliability

Requirement:

```text
Do not lose notifications.
```

Strategy:

```text
1. Write notification log before enqueue.
2. Use durable message queue.
3. Worker updates status after provider call.
4. Retry failures.
5. Move permanently failed events to DLQ.
```

Visual:

```text
API -> Log(PENDING) -> Durable Queue -> Worker -> Provider -> Log(SENT/FAILED)
```

---

# Step 13 — Retry and DLQ

```text
Worker sends notification
        |
        v
Provider error?
        |
   +----+----+
   |         |
  no        yes
   |         |
   v         v
mark SENT  retry with backoff
             |
             v
       retry count exceeded?
             |
        +----+----+
        |         |
       no        yes
        |         |
        v         v
    requeue    DLQ
```

Retry policy:

```text
Retry 1: 10 sec
Retry 2: 1 min
Retry 3: 5 min
Retry 4: 30 min
Then DLQ
```

---

# Step 14 — Deduplication

Exactly-once delivery is hard.

Use:

```text
notification_id as idempotency key
```

Logic:

```text
if notification_id already processed:
    drop duplicate
else:
    process
```

Visual:

```text
Event
  |
  v
Dedupe Store
  |
+---+---+
|       |
seen    new
|       |
drop    process
```

---

# Step 15 — Channel Isolation

Each channel has its own queue.

Why?

```text
If SMS provider is down:
    SMS queue backs up.
    Push and email still work.
```

Visual:

```text
Push Queue  -> Push Workers  -> APNs/FCM
SMS Queue   -> SMS Workers   -> SMS Provider
Email Queue -> Email Workers -> Email Provider
```

---

# Step 16 — Provider Failover

Example for SMS:

```text
Primary: Twilio
Backup:  Nexmo
```

Flow:

```text
Send SMS
   |
   v
Try primary
   |
   +-- success -> done
   |
   +-- fail -> try backup
```

---

# Step 17 — Security

Use:

```text
Internal auth token
mTLS
appKey/appSecret
RBAC
service allowlist
payload validation
```

For push:

```text
APNs/FCM credentials must be encrypted and rotated.
```

---

# Step 18 — Monitoring

Metrics:

```text
Queue depth
Queue age
Send success rate
Provider error rate
Retry count
DLQ count
Delivery latency
Open rate
Click rate
Unsubscribe rate
Worker CPU/memory
```

Alert:

```text
If queue age grows, workers are not keeping up or provider is failing.
```

---

# Step 19 — Event Tracking

Track lifecycle:

```text
START -> PENDING -> SENT -> DELIVERED -> OPENED -> CLICKED
                         |
                         v
                       FAILED
```

Visual:

```text
start
  |
  v
pending
  |
  v
sent
  |
  +--> delivered --> opened --> clicked
  |
  +--> error
```

---

# Step 20 — Final Architecture

```text
                           +----------------+
                           | Service 1..N   |
                           | Cron Jobs      |
                           +--------+-------+
                                    |
                                    v
                           +----------------+
                           | Notification   |
                           | Servers        |
                           | auth + validate|
                           | rate limit     |
                           +--------+-------+
                                    |
                  +-----------------+-----------------+
                  |                                   |
                  v                                   v
            +------------+                     +-------------+
            | Cache      |                     | Database    |
            | user/device|                     | user/device |
            | templates  |                     | settings    |
            +------------+                     | logs        |
                                               +-------------+
                                    |
                                    v
                       +-------------------------+
                       | Render Template         |
                       | Check Opt-in            |
                       | Write Notification Log  |
                       +------------+------------+
                                    |
       +----------------------------+----------------------------+
       |                            |                            |
       v                            v                            v
+-------------+              +-------------+              +-------------+
| Push Queue  |              | SMS Queue   |              | Email Queue |
+------+------+              +------+------+              +------+------+
       |                            |                            |
       v                            v                            v
+-------------+              +-------------+              +-------------+
| Push Workers|              | SMS Workers |              | Email Worker|
+------+------+              +------+------+              +------+------+
       |                            |                            |
 +-----+-----+                      v                            v
 |           |               +-------------+              +--------------+
 v           v               | SMS Provider|              | Email Service|
APNs        FCM              +------+------+              +------+-------+
 |           |                      |                            |
 v           v                      v                            v
iOS       Android                 Phone                        Inbox

Tracking:
Workers / Clients -> Analytics Service -> Analytics DB

Failures:
Workers -> Retry Queue -> DLQ
```

---

# Step 21 — Java Code: Models

```java
import java.util.List;
import java.util.Map;

enum Channel {
    PUSH, SMS, EMAIL
}

public record NotificationRequest(
        String notificationId,
        List<Long> userIds,
        Channel channel,
        String notificationType,
        String templateId,
        Map<String, String> templateData
) {}

public record NotificationEvent(
        String notificationId,
        long userId,
        Channel channel,
        String destination,
        String title,
        String body,
        int retryCount,
        Map<String, String> metadata
) {}
```

---

# Step 22 — Java Code: Template Renderer

```java
import java.util.Map;

public class TemplateRenderer {
    public String render(String template, Map<String, String> data) {
        String result = template;

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue());
        }

        return result;
    }

    public static void main(String[] args) {
        TemplateRenderer renderer = new TemplateRenderer();

        String template = "Hi {{userName}}, your order {{orderId}} has shipped.";
        Map<String, String> data = Map.of(
                "userName", "Alex",
                "orderId", "A1001"
        );

        System.out.println(renderer.render(template, data));
    }
}
```

---

# Step 23 — Java Code: Settings Service

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationSettingsService {
    private final Map<String, Boolean> settings = new ConcurrentHashMap<>();

    public void setOptIn(long userId, Channel channel, String type, boolean optIn) {
        settings.put(key(userId, channel, type), optIn);
    }

    public boolean isOptedIn(long userId, Channel channel, String type) {
        return settings.getOrDefault(key(userId, channel, type), true);
    }

    private String key(long userId, Channel channel, String type) {
        return userId + ":" + channel + ":" + type;
    }
}
```

---

# Step 24 — Java Code: Fixed Window Rate Limiter

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter {
    private static class Counter {
        long windowStartMillis;
        int count;
    }

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final int limit;
    private final long windowMillis;

    public FixedWindowRateLimiter(int limit, long windowMillis) {
        this.limit = limit;
        this.windowMillis = windowMillis;
    }

    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();

        Counter counter = counters.computeIfAbsent(key, k -> {
            Counter c = new Counter();
            c.windowStartMillis = now;
            return c;
        });

        if (now - counter.windowStartMillis >= windowMillis) {
            counter.windowStartMillis = now;
            counter.count = 0;
        }

        if (counter.count >= limit) {
            return false;
        }

        counter.count++;
        return true;
    }
}
```

---

# Step 25 — Java Code: Dedupe Store

```java
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationDedupeStore {
    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();

    public boolean markIfNew(String notificationId) {
        return processedIds.add(notificationId);
    }
}
```

---

# Step 26 — Java Code: Worker with Retry

```java
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

interface NotificationSender {
    void send(NotificationEvent event);
}

public class NotificationWorker {
    private final Queue<NotificationEvent> retryQueue = new ConcurrentLinkedQueue<>();
    private final Queue<NotificationEvent> deadLetterQueue = new ConcurrentLinkedQueue<>();
    private final NotificationSender sender;
    private final int maxRetries = 3;

    public NotificationWorker(NotificationSender sender) {
        this.sender = sender;
    }

    public void process(NotificationEvent event) {
        try {
            sender.send(event);
            System.out.println("SENT: " + event.notificationId());
        } catch (Exception e) {
            if (event.retryCount() < maxRetries) {
                NotificationEvent retryEvent = new NotificationEvent(
                        event.notificationId(),
                        event.userId(),
                        event.channel(),
                        event.destination(),
                        event.title(),
                        event.body(),
                        event.retryCount() + 1,
                        event.metadata()
                );

                retryQueue.offer(retryEvent);
                System.out.println("RETRY: " + event.notificationId());
            } else {
                deadLetterQueue.offer(event);
                System.out.println("DLQ: " + event.notificationId());
            }
        }
    }
}
```

---

# Step 27 — Java Code: Channel Senders

```java
public class PushSender implements NotificationSender {
    @Override
    public void send(NotificationEvent event) {
        // Production: call APNs or FCM.
        System.out.println("Push sent to token: " + event.destination());
    }
}

public class SmsSender implements NotificationSender {
    @Override
    public void send(NotificationEvent event) {
        // Production: call Twilio/Nexmo/etc.
        System.out.println("SMS sent to phone: " + event.destination());
    }
}

public class EmailSender implements NotificationSender {
    @Override
    public void send(NotificationEvent event) {
        // Production: call SendGrid/SES/Mailchimp/etc.
        System.out.println("Email sent to: " + event.destination());
    }
}
```

---

# Step 28 — Java Code: Mini Notification Service

```java
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MiniNotificationService {
    private final NotificationSettingsService settingsService = new NotificationSettingsService();
    private final FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter(5, 60_000);
    private final NotificationDedupeStore dedupeStore = new NotificationDedupeStore();
    private final TemplateRenderer renderer = new TemplateRenderer();

    private final Queue<NotificationEvent> pushQueue = new ConcurrentLinkedQueue<>();
    private final Queue<NotificationEvent> smsQueue = new ConcurrentLinkedQueue<>();
    private final Queue<NotificationEvent> emailQueue = new ConcurrentLinkedQueue<>();

    public void send(NotificationRequest request) {
        if (!dedupeStore.markIfNew(request.notificationId())) {
            System.out.println("Duplicate notification ignored: " + request.notificationId());
            return;
        }

        String template = "Hello {{userName}}, your order {{orderId}} has shipped.";
        String body = renderer.render(template, request.templateData());

        for (long userId : request.userIds()) {
            if (!settingsService.isOptedIn(userId, request.channel(), request.notificationType())) {
                System.out.println("User opted out: " + userId);
                continue;
            }

            String rateLimitKey = userId + ":" + request.channel();

            if (!rateLimiter.allow(rateLimitKey)) {
                System.out.println("Rate limited user: " + userId);
                continue;
            }

            NotificationEvent event = new NotificationEvent(
                    request.notificationId() + "-" + userId,
                    userId,
                    request.channel(),
                    resolveDestination(userId, request.channel()),
                    "Order Update",
                    body,
                    0,
                    Map.of("type", request.notificationType())
            );

            enqueue(event);
        }
    }

    private void enqueue(NotificationEvent event) {
        switch (event.channel()) {
            case PUSH -> pushQueue.offer(event);
            case SMS -> smsQueue.offer(event);
            case EMAIL -> emailQueue.offer(event);
        }
    }

    private String resolveDestination(long userId, Channel channel) {
        return switch (channel) {
            case PUSH -> "device-token-for-user-" + userId;
            case SMS -> "+100000000" + userId;
            case EMAIL -> "user" + userId + "@example.com";
        };
    }

    public Queue<NotificationEvent> pushQueue() {
        return pushQueue;
    }
}
```

---

# Step 29 — Scaling Strategy

## Notification Servers

```text
Stateless.
Horizontally scalable behind load balancer.
```

## Queues

```text
Partition by channel, region, and priority.
```

Examples:

```text
push-ios-us
push-android-eu
sms-us
email-global
```

## Workers

```text
Autoscale based on queue depth and queue age.
```

## Database

```text
Shard notification logs by user_id or notification_id.
Replicate user/device/settings data.
```

---

# Step 30 — Failure Scenarios

## Provider Down

```text
Provider fails -> retry -> backup provider -> DLQ
```

## Queue Backlog

```text
Queue grows -> autoscale workers -> alert if queue age high
```

## Duplicate Event

```text
Same notification_id -> dedupe rejects duplicate
```

## User Opts Out

```text
Settings check fails -> skip notification
```

---

# Step 31 — FAANG Talking Points

1. Support push, SMS, and email.
2. Use queues to decouple notification creation from delivery.
3. Use separate queues per channel.
4. Persist notification logs before enqueue.
5. Retry with exponential backoff.
6. Use DLQ for permanently failed messages.
7. Use notification ID for dedupe/idempotency.
8. Check user opt-in settings before sending.
9. Rate limit per user, channel, service, and provider.
10. Use templates for reusable content.
11. Cache user/device/template data.
12. Monitor queue depth, queue age, provider errors, and latency.
13. Track sent, delivered, opened, clicked, and unsubscribed events.
14. Keep notification servers stateless.
15. Use provider failover where possible.

---

# Step 32 — One-Minute Interview Summary

> I would design the notification system with stateless notification servers behind a load balancer. Services call internal APIs to request notifications. The server authenticates the caller, validates the payload, checks user settings and rate limits, renders templates, writes a notification log, and publishes the event to a channel-specific durable queue. Separate worker pools consume push, SMS, and email queues and send messages through APNs, FCM, SMS providers, or email providers. Failed sends are retried with exponential backoff and eventually moved to a dead-letter queue. The system uses notification IDs for dedupe, caches user/device/template data, tracks lifecycle events, and monitors queue size and provider errors.

---

# Quick Revision

```text
Core flow:
Service -> Notification API -> Validate -> Settings -> Template -> Log -> Queue -> Worker -> Provider -> User

Channels:
iOS Push -> APNs
Android Push -> FCM
SMS -> SMS Provider
Email -> Email Provider

Reliability:
Durable queue + notification log + retry + DLQ + dedupe

User safety:
Opt-out + rate limiting + authentication

Monitoring:
Queue depth, retry count, DLQ count, provider errors, delivery latency

Best phrase:
Notifications can be delayed or duplicated occasionally, but should not be lost.
```
