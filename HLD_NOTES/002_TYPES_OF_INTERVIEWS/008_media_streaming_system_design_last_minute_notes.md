# Media Streaming in System Design – Last-Minute Notes

> A compact but complete guide for interviews on **Netflix / YouTube / Twitch / Zoom / streaming systems**.

---

## Why Streaming Is Hard

Streaming combines 3 hard problems at once:

1. **Video is huge**
2. **Timing is strict**
3. **Networks are unstable**

### 1) Video is massive
Raw 1080p @ 30fps:
- 1920 × 1080 pixels
- 24 bits/pixel
- 30 fps

```text
Raw rate = 1920 × 1080 × 24 × 30 ≈ 1.49 Gbps
≈ 11 GB/minute raw
```

Even compressed:
- good 1080p stream = **5–10 Mbps**

At scale:
```text
1M viewers × 5 Mbps = 5 Pbps
```

So:
- compression is mandatory
- CDN is mandatory

---

### 2) Timing is critical
Streaming is not file download.

At 30 fps:
```text
1 frame every 33.3 ms
```

If frames arrive late:
- stutter
- buffering
- A/V sync issues

Audio is even stricter:
- gaps as small as **10–20 ms** are noticeable
- A/V sync should stay within **~40 ms**

---

### 3) Networks are unreliable
Users may be on:
- fiber
- cable
- 4G / 3G
- congested WiFi
- trains / tunnels / moving networks

Bandwidth and latency change constantly.  
That is why **adaptive bitrate streaming (ABR)** matters.

---

## Core Streaming Pipeline

```text
Streamer/Source
-> Ingest
-> Transcoding
-> Segmenting
-> Storage / Origin
-> CDN
-> Player / Viewer
```

---

# 1) Encoding Fundamentals

## Why compress video?
Compression removes redundancy:
- **Spatial redundancy** -> similar nearby pixels
- **Temporal redundancy** -> many frames are similar

## Frame types
- **I-frame** = full image, large, independent
- **P-frame** = difference from previous frame
- **B-frame** = difference using past + future frames

### Why this matters
- more I-frames -> faster seeking/channel switching
- fewer B-frames -> lower latency
- but lower efficiency

---

## Common codecs

| Codec | Efficiency | CPU Cost | Notes |
|---|---|---|---|
| H.264 | baseline | low | universal, safest default |
| H.265 / HEVC | ~50% better than H.264 | high | growing adoption |
| VP9 | similar to HEVC | medium | popular on YouTube |
| AV1 | ~30% better than HEVC | very high | best compression, expensive |

### Interview default
- **H.264** for compatibility
- **AV1 / HEVC** when bandwidth savings justify CPU cost

---

## Encoding profile trade-off
Example 1080p:
- normal: keyframe every **2 sec**, B-frames enabled, bitrate **6 Mbps**
- low latency: keyframe every **1 sec**, no B-frames, bitrate **8 Mbps**

### Rule
```text
Lower latency -> worse compression efficiency
```

---

# 2) Streaming Protocols

## RTMP
Used mostly for **ingest from streamer to platform**.

### Characteristics
- TCP-based
- ~1–5s latency
- persistent connection
- low viewer-side relevance today

### Best use
```text
Streamer -> ingest server
```

---

## HLS
The default choice for large-scale delivery.

### How it works
- split video into segments (2–10 sec)
- create playlist/manifest (`.m3u8`)
- client downloads manifest + segments over HTTP

### Pros
- works over HTTP
- CDN-friendly
- firewall-friendly
- supports ABR
- huge compatibility

### Cons
- latency floor because of segmenting + buffering

### Typical latency
- standard HLS: **15–30s**
- LL-HLS: **2–5s**

---

## DASH
Very similar to HLS:
- segments + manifest
- manifest is XML (`MPD`)
- good DRM ecosystem

### In practice
Often:
- HLS only
- or both HLS + DASH

### Safe interview answer
```text
Choose HLS for broad compatibility unless DRM / ecosystem pushes DASH.
```

---

## WebRTC
Different category:
- UDP-based
- <1s latency
- built for real-time interaction

### Best for
- video calls
- auctions
- gaming
- interactive live streams

### Trade-offs
- expensive to scale
- poor CDN fit
- needs STUN / TURN / ICE
- better for thousands than millions

---

## Protocol summary

| Protocol | Latency | Scale | Best For |
|---|---:|---|---|
| RTMP | 1–5s | low | ingest |
| HLS / DASH | 15–30s | very high | VOD / large broadcasts |
| LL-HLS / LL-DASH | 2–5s | high | low-latency live |
| WebRTC | <1s | medium | interactive / video calls |

---

# 3) Architecture of a Streaming System

## 1. Ingest layer
Responsibilities:
- accept incoming RTMP/WebRTC streams
- authenticate streamers
- validate stream health
- route to transcoding pipeline

### Design notes
- deploy ingest in multiple regions
- keep streamer close to ingest
- support redundancy for premium streams
- rate limit to prevent abuse

---

## 2. Transcoding layer
Convert source stream into multiple qualities.

### Example ladder

| Quality | Resolution | Bitrate | Target |
|---|---|---:|---|
| 1080p60 | 1920×1080 | 6 Mbps | desktop / strong connection |
| 1080p30 | 1920×1080 | 4.5 Mbps | desktop / average |
| 720p | 1280×720 | 3 Mbps | tablet / mobile |
| 480p | 854×480 | 1.5 Mbps | weak network |
| 360p | 640×360 | 0.8 Mbps | very poor network |
| Audio only | - | 128 kbps | audio fallback |

### Scaling notes
- GPU encoding (NVENC / QuickSync) is much cheaper than CPU
- often 1 transcoder per stream
- queue jobs and auto-scale workers
- cloud transcoders reduce complexity but cost more

---

## 3. Storage layer

### Live streaming
Segments are short-lived:
- hot for minutes
- keep for DVR window (e.g. 2 hours / 4 hours)
- then delete

### VOD
Store:
- original source
- transcoded renditions
- segment files / packaged outputs

### Tiering
- **Hot** = active live segments
- **Warm** = recent / popular VOD
- **Cold** = archives

---

## 4. Origin layer
Origin is the authoritative source.

Responsibilities:
- serve manifests
- serve segments on cache miss
- access control / token checks
- origin shielding

### Origin shielding
Without shielding:
```text
100 edge misses -> 100 origin requests
```

With shielding:
```text
100 edge misses -> few shield/origin requests
```

---

## 5. CDN layer
The CDN is the real scaling engine.

### Why CDN is essential
- low latency via edge servers
- huge bandwidth distribution
- origin protection
- lower cost than self-serving traffic

### Caching rules
- **segments** are immutable -> cache aggressively
- **live manifests** change often -> short TTL

Example:
```http
# segments
Cache-Control: public, max-age=31536000

# live manifests
Cache-Control: no-cache, max-age=2
```

---

## 6. Player layer
Player responsibilities:
- fetch manifest
- choose quality (ABR)
- manage buffer
- decode and render
- recover from errors

Popular players:
- Video.js
- hls.js
- Shaka Player
- ExoPlayer
- AVPlayer

---

# 4) Live vs VOD

| Aspect | Live | VOD |
|---|---|---|
| Processing | real-time | batch |
| Latency | important | not important |
| Encoding | single-pass | can use multi-pass |
| CDN cache | short-lived | long-lived |
| Viewers | synchronized | each viewer independent |
| Failure impact | immediate | can retry/re-encode |

### Live challenges
- real-time transcoding must keep up
- sudden viewer spikes
- short manifest/segment propagation windows
- low-latency requirement

### VOD challenges
- storage cost
- long-tail content rarely watched
- startup latency for cold content
- multi-quality archive management

---

# 5) Adaptive Bitrate Streaming (ABR)

## Goal
Maximize quality while avoiding buffering.

### Player loop
1. download segment
2. measure throughput
3. check buffer level
4. choose next quality
5. repeat

---

## ABR approaches

### A) Throughput-based
Choose highest rendition that fits measured bandwidth.

### B) Buffer-based
Choose quality based on how healthy the player buffer is.

### C) Hybrid
Modern systems combine:
- throughput
- buffer level
- recent history
- switching stability

---

## ABR rules of thumb
- **drop quality fast**
- **increase slowly**
- add **hysteresis** to avoid annoying quality oscillation
- stable 720p is often better UX than flapping 1080p/480p

### Important
```text
Frequent quality switching is bad UX.
Buffering is worse.
```

---

# 6) Low-Latency Streaming

## Where normal latency comes from
```text
Capture + Encode + Ingest + Transcode + Segment + CDN + Player Buffer + Decode
```

Typical standard HLS:
- segment duration: 6s
- player buffer: 3 segments
- total: ~15–30s

---

## How to reduce latency

### 1. Shorter segments
- from 6s -> 2s
- less segment wait
- more HTTP overhead

### 2. CMAF / chunked transfer
Send partial chunks before full segment is complete.

### 3. LL-HLS / LL-DASH
Use:
- partial segments
- blocking playlist reload
- preload hints

### 4. Smaller player buffer
- lower latency
- less tolerance to network hiccups

### 5. WebRTC
Fastest option:
- ~200–500ms
- but expensive and harder to scale

---

## Latency vs scale trade-off

| Approach | Latency | Scale | Cost |
|---|---:|---|---|
| WebRTC | <1s | ~10K | high |
| LL-HLS | 2–5s | ~500K | medium |
| Standard HLS | 15–30s | millions | low |

### Rule
```text
Lower latency -> higher cost -> lower scale
```

Choose the latency your use case actually needs.

---

# 7) Scaling Strategies

## 1. CDN edge caching
This is the main strategy.

Without CDN:
```text
1M viewers -> 1M origin connections
```

With CDN:
```text
1M viewers -> edges
origin handles content scale, not viewer scale
```

---

## 2. Multi-CDN
Do not depend on one CDN.

### Benefits
- redundancy
- regional optimization
- better negotiation leverage
- traffic shifting during outages

---

## 3. Regional origins
For global traffic:
- US origin
- EU origin
- APAC origin

Helps reduce origin-to-edge latency.

---

## 4. Distributed transcoding
Instead of one giant transcoder:
- split work across workers
- auto-scale off queue depth
- use GPUs
- possibly transcode regionally

---

## 5. Predictive scaling
For known events:
- sports finals
- product launches
- major streamers
- halftime / kickoff spikes

Pre-scale:
- CDN capacity
- transcoders
- manifests/caches
- ops staffing

---

# 8) Twitch-like Live Streaming Architecture

## Requirements
- streamers upload via RTMP
- viewers watch via HLS
- multiple qualities
- standard latency < 10s
- low-latency mode < 3s
- DVR up to 2 hours
- 10M concurrent viewers

## Architecture
```text
Streamers
-> Ingest Load Balancer
-> Ingest Servers
-> Transcode Queue
-> Transcoding Workers
-> Segment Store (S3)
-> Metadata Store
-> Origin
-> CDN
-> Viewers
```

### Components
- **Ingest LB** -> route streamers to healthy region/server
- **Ingest servers** -> auth + validate + handoff
- **Transcode queue** -> decouple ingest from processing
- **Transcoders** -> create ladder outputs
- **Segment store** -> S3/GCS for segments/manifests
- **Metadata DB** -> active qualities, recent segments, DVR
- **Origin** -> serve CDN on cache miss
- **CDN** -> serve viewers at scale

---

# 9) Common Mistakes to Avoid

## 1. Ignoring client diversity
Bad:
- only 1080p
- assume fast networks

Good:
- 1080p / 720p / 480p / 360p / audio-only

---

## 2. Underestimating transcoding cost
Transcoding is often the most expensive part.

### Optimizations
- prefer GPU encoders
- only transcode full ladder for popular streams
- let premium/partner streamers upload better source profiles
- tier transcoding depth by audience size

---

## 3. Not testing network conditions
Test:
- 3G / 4G / weak WiFi
- packet loss
- jitter
- high latency
- train/tunnel transitions

---

## 4. Hardcoding URLs
Avoid:
- fixed CDN URLs
- single CDN assumptions

Prefer:
- manifest-driven URLs
- switchable CDN strategy

---

## 5. Ignoring the last mile
Your system may be perfect to the edge, but home WiFi still breaks.

That is why:
- ABR matters
- buffer tuning matters
- fallback qualities matter

---

# 10) Interview Answer Template

```text
For a streaming system, I’d break the design into ingest, transcoding, storage, origin, CDN, and player.

For ingest, streamers would send RTMP to geographically distributed ingest servers.
The stream would then go through a transcoding pipeline that creates a bitrate ladder, for example 1080p, 720p, 480p, and audio-only.

The transcoded output would be segmented into HLS or LL-HLS, stored in object storage, and served through an origin plus CDN.
The CDN is the main scale lever, because origin should scale with content, not viewers.

On the player side, I’d use adaptive bitrate streaming so the client can adjust quality based on measured throughput and buffer health.
For standard large-scale delivery, I’d choose HLS.
If latency requirements are stricter, I’d move to LL-HLS, and only use WebRTC when sub-second latency is truly required.
```

---

# 11) Spring Boot Example – Step by Step

Below is a **simple Spring Boot backend** for a streaming platform:
- stream metadata API
- manifest proxy/generator
- signed playback URL generation
- basic live channel model

This is not a full transcoder, but it shows how the backend fits.

---

## Step 1: Add dependencies

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
</dependencies>
```

---

## Step 2: Create stream entity

```java
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class StreamChannel {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String streamerId;
    private boolean live;
    private String ingestKey;
    private String playbackPath;   // e.g. /live/{channelId}/master.m3u8
    private Instant startedAt;

    // getters and setters
}
```

---

## Step 3: Create repository

```java
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StreamChannelRepository extends JpaRepository<StreamChannel, UUID> {
    Optional<StreamChannel> findByIngestKey(String ingestKey);
}
```

---

## Step 4: DTOs

```java
import java.util.UUID;

public record CreateStreamRequest(
        String title,
        String streamerId
) {}
```

```java
import java.util.UUID;

public record StreamResponse(
        UUID id,
        String title,
        boolean live,
        String ingestKey,
        String playbackUrl
) {}
```

---

## Step 5: Create service

```java
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class StreamService {

    private final StreamChannelRepository repository;

    public StreamService(StreamChannelRepository repository) {
        this.repository = repository;
    }

    public StreamResponse create(CreateStreamRequest request) {
        StreamChannel channel = new StreamChannel();
        channel.setTitle(request.title());
        channel.setStreamerId(request.streamerId());
        channel.setLive(false);
        channel.setIngestKey(UUID.randomUUID().toString());
        channel.setPlaybackPath("/live/" + UUID.randomUUID() + "/master.m3u8");
        repository.save(channel);

        return toResponse(channel);
    }

    public StreamResponse markLive(String ingestKey) {
        StreamChannel channel = repository.findByIngestKey(ingestKey)
                .orElseThrow();

        channel.setLive(true);
        channel.setStartedAt(Instant.now());
        repository.save(channel);

        return toResponse(channel);
    }

    public StreamResponse get(UUID id) {
        return toResponse(repository.findById(id).orElseThrow());
    }

    private StreamResponse toResponse(StreamChannel channel) {
        return new StreamResponse(
                channel.getId(),
                channel.getTitle(),
                channel.isLive(),
                channel.getIngestKey(),
                "https://cdn.example.com" + channel.getPlaybackPath()
        );
    }
}
```

---

## Step 6: Create controller

```java
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/streams")
public class StreamController {

    private final StreamService streamService;

    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @PostMapping
    public StreamResponse create(@RequestBody CreateStreamRequest request) {
        return streamService.create(request);
    }

    @PostMapping("/ingest/{ingestKey}/live")
    public StreamResponse goLive(@PathVariable String ingestKey) {
        return streamService.markLive(ingestKey);
    }

    @GetMapping("/{id}")
    public StreamResponse get(@PathVariable UUID id) {
        return streamService.get(id);
    }
}
```

---

## Step 7: Manifest service
In a real platform, manifests are produced by the transcoding pipeline and stored in S3/object storage.

Your Spring Boot API can:
- return the **master manifest URL**
- proxy manifest requests
- add authorization/token logic

### Example manifest endpoint
```java
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playback")
public class PlaybackController {

    @GetMapping(value = "/{channelId}/master.m3u8", produces = "application/vnd.apple.mpegurl")
    public String masterManifest(@PathVariable String channelId) {
        return "#EXTM3U\n" +
               "#EXT-X-STREAM-INF:BANDWIDTH=6000000,RESOLUTION=1920x1080\n" +
               "1080p/playlist.m3u8\n" +
               "#EXT-X-STREAM-INF:BANDWIDTH=3000000,RESOLUTION=1280x720\n" +
               "720p/playlist.m3u8\n" +
               "#EXT-X-STREAM-INF:BANDWIDTH=1500000,RESOLUTION=854x480\n" +
               "480p/playlist.m3u8\n";
    }
}
```

---

## Step 8: Secure playback with signed URLs / tokens

For premium/private streams, do not expose raw storage URLs.

### Example token generator idea
```java
import java.time.Instant;
import java.util.Base64;

public class PlaybackTokenService {

    public String createToken(String channelId, String userId, long expiresEpochSeconds) {
        String payload = channelId + ":" + userId + ":" + expiresEpochSeconds;
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }

    public boolean isValid(String token) {
        // replace with HMAC/JWT validation in real systems
        return token != null && !token.isBlank();
    }
}
```

Use this token:
- in manifest URLs
- at CDN edge auth
- at origin auth checks

---

## Step 9: Store manifests/segments in object storage
Typical layout:
```text
s3://media/live/{channelId}/master.m3u8
s3://media/live/{channelId}/720p/playlist.m3u8
s3://media/live/{channelId}/720p/00001.ts
```

Spring Boot does not need to serve the bytes directly.
It should mostly:
- manage metadata
- authenticate requests
- issue signed playback URLs
- orchestrate ingest/transcoding state

---

## Step 10: Add live DVR metadata
To support 2-hour rewind:
- keep recent segment metadata
- keep rolling manifest window
- lifecycle old segments out

### Minimal metadata model
```java
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class StreamSegment {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID channelId;
    private String quality;
    private long segmentIndex;
    private double durationSec;
    private String objectKey;
    private Instant createdAt;

    // getters/setters
}
```

---

## Step 11: Practical production architecture

```text
OBS / Streamer
-> RTMP Ingest
-> Transcode Queue
-> FFmpeg / GPU Workers
-> Object Storage (HLS segments/manifests)
-> CDN
-> Player
```

Spring Boot is responsible for:
- channel creation
- ingest key authentication
- metadata
- signed playback
- live state
- feature APIs

---

# 12) Polished Key Takeaways

- **Video is huge**  
  Compression and CDN caching are what make streaming economically possible.

- **Streaming is about timing, not just bytes**  
  Late frames and low buffer health are what create bad UX.

- **Protocols are chosen by latency needs**  
  RTMP for ingest, HLS for scale, LL-HLS for lower latency, WebRTC for interactive real-time.

- **Transcoding is expensive and central**  
  It creates the bitrate ladder that makes ABR work, but it is often the cost bottleneck.

- **CDN is the main scale lever**  
  Origins should scale with content, not with number of viewers.

- **ABR is essential for real-world users**  
  Networks vary constantly, so the player must adapt quality in real time.

- **Lower latency always costs more**  
  You usually trade off efficiency and scale to reduce latency.

- **Live and VOD are different systems**  
  Live cares about latency and real-time processing; VOD cares more about storage efficiency and long-tail caching.

- **Plan for failures and bad networks**  
  Multi-CDN, player fallbacks, and tested low-bandwidth behavior matter.

- **For interviews, keep the architecture layered**  
  Ingest -> Transcode -> Storage -> Origin -> CDN -> Player is the clean mental model.

---

## Final 1-Line Shortcut
```text
Ingest -> Transcode -> Segment -> Store -> CDN -> ABR Player
```
