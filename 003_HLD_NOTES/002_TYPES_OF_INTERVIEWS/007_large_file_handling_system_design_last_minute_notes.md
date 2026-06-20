# Large File Handling in System Design – Last-Minute Notes

> A concise but comprehensive guide to **uploading, downloading, storing, and serving large files** at scale.

---

## Why Large File Handling Matters
Naive file handling breaks quickly for big files.

### Example problem
A user uploads a **2 GB** video:
- upload runs for **45 minutes**
- connection drops at **80%**
- user must restart from **0%**
- server memory spikes
- request times out
- other users slow down

### Core insight
```text
Do not treat a large file as one atomic operation.
Break it into smaller independent pieces.
```

---

## Where This Shows Up
- Google Drive / Dropbox
- YouTube / Netflix
- Slack / Teams
- GitHub
- Backup systems
- Media and document platforms

---

# 1) Why Naive Uploads Fail

## Naive approach
```text
Client -> POST /upload -> server buffers full file -> writes to storage
```

## Problems
- **Memory exhaustion**  
  2 GB file × 100 uploads = 200 GB RAM if buffering
- **Timeouts**  
  Large files exceed HTTP / proxy / LB timeouts
- **No resume support**  
  Failure at 99% means restart from 0%
- **Single point of failure**  
  If app server restarts, upload is lost
- **Resource blocking**  
  Threads, connections, CPU are tied up for long periods

### Example
```text
2 GB on 10 Mbps ~= 27 minutes
```

---

# 2) Pattern 1: Chunked Uploads

## Core idea
Split a file into fixed-size chunks and upload each chunk independently.

```text
2 GB file -> 32 chunks of 64 MB
```

If one chunk fails:
- retry only that chunk
- do not restart whole file

---

## Upload flow

### Step 1: Initialize upload
Client asks server to create an upload session.

```http
POST /uploads/init
{
  "file_name": "video.mp4",
  "file_size": 1073741824,
  "content_type": "video/mp4",
  "checksum": "sha256:abc123"
}
```

Response:
```json
{
  "upload_id": "up_xyz789",
  "chunk_size": 67108864,
  "total_chunks": 16
}
```

---

### Step 2: Upload chunks
```http
PUT /uploads/up_xyz789/chunks/0
Content-Length: 67108864
Content-MD5: <chunk_checksum>
```

Server stores and validates each chunk independently.

---

### Step 3: Complete upload
```http
POST /uploads/up_xyz789/complete
```

Server:
- verifies all chunks exist
- assembles final object
- creates file metadata record

---

## Resumable uploads
If connection drops, ask server which chunks are already uploaded.

```http
GET /uploads/up_xyz789/status
```

Response:
```json
{
  "upload_id": "up_xyz789",
  "total_chunks": 16,
  "received_chunks": [0,1,2,3,4],
  "bytes_received": 335544320
}
```

Then resume from missing chunks only.

### Why this matters
```text
Fail at 90% -> retry 10%
not 100%
```

---

## Idempotent chunk uploads
A retry might happen even if server already received the chunk.

Use:
- per-chunk checksum
- upload_id + chunk_index
- server-side dedup / checksum verification

### Result
Retries become safe:
```text
same chunk uploaded twice -> same final state
```

---

## Choosing chunk size

| Chunk Size | Pros | Cons |
|---|---|---|
| 1–4 MB | fine-grained retry, better for mobile | more HTTP overhead |
| 16–64 MB | best default for most systems | moderate retry cost |
| 100+ MB | fewer requests | expensive retries, more memory |

### Rule of thumb
- **4–8 MB** for unstable/mobile clients
- **16–64 MB** for desktop/server uploads
- use **adaptive chunk size** if possible

---

# 3) Pattern 2: Direct Upload with Pre-Signed URLs

## Problem with chunking alone
If all chunk traffic goes through app servers:
- app servers carry heavy bandwidth
- memory/CPU/network cost increases
- scaling app tier becomes expensive

## Better approach
Let clients upload **directly to blob storage**.

```text
Client -> API server (metadata only)
Client -> S3/GCS/Azure Blob (actual bytes)
```

---

## How pre-signed URLs work
Server creates a short-lived signed URL that allows:
- one operation
- one object/path
- one method (PUT / GET)
- limited expiry

### Example concept
```text
https://storage.example.com/object?signature=...&expires=...
```

Client uses the URL directly, without having raw storage credentials.

---

## Why pre-signed URLs matter
- removes heavy file traffic from app servers
- lowers infra cost
- improves upload speed
- storage scales independently
- safer than giving storage credentials directly

---

## Security considerations
- short expiration (e.g. 15 min–2 hrs)
- content-length / content-type constraints
- optional IP restrictions
- logging + monitoring for generated URLs

### Rule
```text
Pre-signed URLs are like password-reset links:
short-lived, scoped, and limited.
```

---

# 4) Pattern 3: Multipart Upload Protocol

If using S3/GCS/Azure Blob, prefer their built-in multipart upload flow.

## Basic multipart flow
1. **Initiate** upload  
2. **Upload parts** independently  
3. **Complete** upload  
4. **Abort** if abandoned

### Why use it
- built-in chunking
- resumable by design
- battle-tested
- supports parallel uploads
- optimized for huge files (TB scale)

---

## Parallel part uploads
Multiple threads can upload different parts simultaneously.

```text
Thread 1 -> parts 1,5,9
Thread 2 -> parts 2,6,10
Thread 3 -> parts 3,7,11
Thread 4 -> parts 4,8,12
```

### Benefit
Can significantly improve throughput for large uploads.

### Typical concurrency
```text
4–8 parallel streams
```

More is rarely helpful beyond bandwidth limits.

---

## Handling incomplete uploads
Incomplete multipart uploads still consume storage.

### Cleanup strategies
- **Lifecycle rule** (best default)  
  auto-abort uploads older than X days
- **DB tracking + cleanup job**  
  for stricter control

### Best default
```text
Abort incomplete multipart uploads after 7 days
```

---

# 5) Pattern 4: Streaming Uploads

## Use case
Sometimes file size is unknown in advance:
- live video/audio
- generated/compressed data on the fly
- log streams

## Technique
Use HTTP streaming / chunked transfer encoding.

### Example concept
```http
Transfer-Encoding: chunked
```

Server processes incoming chunks as they arrive.

### Server-side idea
```python
async for chunk in request.stream():
    await store_chunk(upload_id, chunk_index, chunk)
```

---

## When streaming is good
- live streams
- log pipelines
- dynamic content generation

## Limits
- no clean resumability
- harder integrity validation
- some proxies buffer it badly

### Rule
```text
If file size is known -> prefer explicit chunking
If stream is truly open-ended -> use streaming
```

---

# 6) Download Optimizations

Uploads get attention, but large downloads matter too.

---

## Range requests
Clients can request only part of a file.

```http
GET /files/video.mp4
Range: bytes=0-1048575
```

Response:
```http
206 Partial Content
Content-Range: bytes 0-1048575/104857600
```

### Enables
- resumable downloads
- parallel downloads
- video seeking
- partial fetches

---

## Parallel downloads
Download different ranges in parallel.

```text
Thread 1 -> bytes 0-25M
Thread 2 -> bytes 25-50M
Thread 3 -> bytes 50-75M
Thread 4 -> bytes 75-100M
```

---

## CDN distribution
Popular large files should be served via CDN.

### Why
- lower latency
- lower origin load
- better throughput
- global distribution
- edge caching

### Important headers
```http
Cache-Control: public, max-age=31536000
ETag: "abc123"
Accept-Ranges: bytes
```

`Accept-Ranges: bytes` is critical for resume + seeking.

---

# 7) Storage Architecture

## Best storage types

| Storage Type | Best For | Avoid When |
|---|---|---|
| Blob storage (S3/GCS) | large files, media, backups | low-latency random byte-level edits |
| Distributed FS | analytics/shared compute | normal web serving |
| DB BLOBs | very small files (<1 MB) | large media/files |

### Default recommendation
```text
For web apps, use blob storage for file bytes.
```

---

## Separate metadata from file data

### Store metadata in DB
- file name
- owner
- size
- content type
- created_at
- permissions
- blob path / hash

### Store bytes in blob storage
- S3 / GCS / Azure Blob

### Why separate
- independent scaling
- better DB queries
- better storage performance
- cleaner retention policies

---

## Content-addressable storage
Use a hash of content as storage key.

### Example
```text
SHA-256(file) -> 3b4c5d...
path -> /blobs/3b/4c/3b4c5d...
```

### Benefits
- automatic deduplication
- immutable content
- easy integrity verification
- safe concurrent uploads

### Pseudocode idea
```python
content_hash = sha256(file_data).hexdigest()
if not blob_exists(content_hash):
    blob_storage.put(content_hash, file_data)
```

---

# 8) Compression and Deduplication

## Compression
Compress when it actually helps.

### Good candidates
- text
- logs
- CSV / JSON
- raw audio/image formats

### Bad candidates
Do **not** recompress already compressed formats:
- JPEG
- MP4
- ZIP
- MP3

---

## Deduplication levels

### File-level dedup
If two users upload exactly same file:
- same hash
- store once
- multiple metadata records point to same blob

### Block-level dedup
If files are mostly similar:
- split into blocks
- store only unique changed blocks

Useful for:
- backups
- Dropbox-like sync
- versioned files

---

## Content-defined chunking (CDC)
Fixed-size chunking breaks badly when bytes are inserted near start of file.

CDC uses content patterns to define chunk boundaries.

### Why CDC matters
It preserves chunk alignment better when files shift slightly.

### Best for
- incremental backups
- sync engines
- dedup-heavy systems

### Not needed for
- simple upload/download systems
- generic media sharing platforms

---

# 9) Complete Production Upload Flow

## Recommended end-to-end flow
1. Client selects file
2. Client asks API to initialize upload
3. API creates upload session
4. API returns chunk size + pre-signed part URLs
5. Client uploads chunks directly to storage
6. Client tracks progress locally
7. If interrupted, client asks upload status
8. Resume missing chunks only
9. Client calls complete
10. Server verifies parts and creates metadata record
11. Server computes/stores content hash for dedup if needed

### Mental model
```text
Init -> Chunk -> Parallelize -> Resume -> Complete -> Register Metadata
```

---

# 10) Reliability Checklist

## Upload path
- chunked uploads
- configurable chunk size
- resumable sessions
- per-chunk checksum
- idempotent retries
- pre-signed URLs
- parallel upload support
- upload timeout / cleanup

## Download path
- range request support
- CDN integration
- cache headers
- ETag
- resume support

## Storage
- blob storage for bytes
- metadata DB for lookup/query
- content hash support
- cleanup of orphaned blobs/uploads

## Resilience
- exponential backoff on retry
- timeout handling
- integrity verification
- cleanup abandoned uploads

---

# 11) When to Use Which Pattern

| Pattern | When to Use | Key Benefit |
|---|---|---|
| Chunked upload | files > 10 MB | resume + retry + parallelism |
| Pre-signed URLs | direct-to-storage uploads | bypass app servers |
| Multipart upload | cloud blob storage | built-in scalable chunking |
| Streaming upload | unknown size/live streams | no upfront size needed |
| Range requests | large downloads | resume + seek + parallel download |
| Content-addressable storage | duplicate content common | deduplication |
| CDN | popular downloads | lower latency + lower origin load |

---

# 12) Interview Answer Template

```text
For large file handling, I would avoid proxying the entire file through application servers.

For uploads, I’d use chunked or cloud multipart uploads, usually with pre-signed URLs so clients upload directly to blob storage. That gives us resumability, parallel uploads, lower server load, and better scalability.

I’d track upload sessions in a metadata database, support resume by storing uploaded part state, and verify chunks with checksums for idempotent retries.

For downloads, I’d support HTTP range requests and put files behind a CDN. That enables resumable downloads, seeking for media, and much lower origin load.

For storage, I’d keep file metadata in a database and store bytes in blob storage. If deduplication matters, I’d use content-addressable storage and optionally block-level dedup for backup/sync use cases.
```

---

# 13) Spring Boot Example – Step by Step

Below is a practical **Spring Boot + S3 multipart / pre-signed URL** example structure.

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
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
    </dependency>

    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3-presigner</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

---

## Step 2: Define upload session model

```java
public class UploadSession {
    private String uploadId;
    private String fileName;
    private long fileSize;
    private int chunkSize;
    private int totalChunks;
    private String objectKey;

    // getters/setters
}
```

---

## Step 3: Init request / response DTOs

```java
public record InitUploadRequest(
        String fileName,
        long fileSize,
        String contentType
) {}
```

```java
import java.util.List;

public record PartUploadUrl(
        int partNumber,
        String url
) {}
```

```java
import java.util.List;

public record InitUploadResponse(
        String uploadId,
        String objectKey,
        int chunkSize,
        int totalChunks,
        List<PartUploadUrl> urls
) {}
```

---

## Step 4: Configure S3 client + presigner

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}
```

---

## Step 5: Service to initialize multipart upload and generate pre-signed URLs

```java
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LargeFileUploadService {

    private final S3Client s3Client;
    private final S3Presigner presigner;

    private static final String BUCKET = "my-upload-bucket";
    private static final int CHUNK_SIZE = 64 * 1024 * 1024; // 64 MB

    public LargeFileUploadService(S3Client s3Client, S3Presigner presigner) {
        this.s3Client = s3Client;
        this.presigner = presigner;
    }

    public InitUploadResponse initialize(InitUploadRequest request) {
        String objectKey = "uploads/" + UUID.randomUUID() + "/" + request.fileName();

        CreateMultipartUploadResponse multipart = s3Client.createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                        .bucket(BUCKET)
                        .key(objectKey)
                        .contentType(request.contentType())
                        .build()
        );

        String uploadId = multipart.uploadId();
        int totalChunks = (int) Math.ceil((double) request.fileSize() / CHUNK_SIZE);

        List<PartUploadUrl> urls = new ArrayList<>();

        for (int partNumber = 1; partNumber <= totalChunks; partNumber++) {
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(BUCKET)
                    .key(objectKey)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();

            PresignedUploadPartRequest presigned = presigner.presignUploadPart(
                    UploadPartPresignRequest.builder()
                            .signatureDuration(java.time.Duration.ofHours(1))
                            .uploadPartRequest(uploadPartRequest)
                            .build()
            );

            urls.add(new PartUploadUrl(partNumber, presigned.url().toString()));
        }

        return new InitUploadResponse(uploadId, objectKey, CHUNK_SIZE, totalChunks, urls);
    }
}
```

---

## Step 6: Controller to initialize upload

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/uploads")
public class LargeFileUploadController {

    private final LargeFileUploadService uploadService;

    public LargeFileUploadController(LargeFileUploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/init")
    public InitUploadResponse initUpload(@RequestBody InitUploadRequest request) {
        return uploadService.initialize(request);
    }
}
```

---

## Step 7: Client uploads parts directly to S3
Your frontend/mobile client:
1. calls `/api/uploads/init`
2. receives `uploadId`, `objectKey`, and list of pre-signed part URLs
3. splits file into chunks
4. uploads each chunk directly to returned URLs
5. records returned `ETag` for each uploaded part

### Important
For S3 multipart completion, client must keep:
- `partNumber`
- `ETag`

---

## Step 8: Complete multipart upload from Spring Boot

### DTOs
```java
import java.util.List;

public record CompletePartRequest(
        int partNumber,
        String eTag
) {}
```

```java
import java.util.List;

public record CompleteUploadRequest(
        String uploadId,
        String objectKey,
        List<CompletePartRequest> parts
) {}
```

---

### Service method
```java
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;

import java.util.Comparator;
import java.util.stream.Collectors;

public String completeUpload(CompleteUploadRequest request) {
    List<CompletedPart> completedParts = request.parts().stream()
            .sorted(Comparator.comparingInt(CompletePartRequest::partNumber))
            .map(p -> CompletedPart.builder()
                    .partNumber(p.partNumber())
                    .eTag(p.eTag())
                    .build())
            .collect(Collectors.toList());

    CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
            .parts(completedParts)
            .build();

    s3Client.completeMultipartUpload(
            CompleteMultipartUploadRequest.builder()
                    .bucket(BUCKET)
                    .key(request.objectKey())
                    .uploadId(request.uploadId())
                    .multipartUpload(multipartUpload)
                    .build()
    );

    return request.objectKey();
}
```

---

### Controller endpoint
```java
@PostMapping("/complete")
public String completeUpload(@RequestBody CompleteUploadRequest request) {
    return uploadService.completeUpload(request);
}
```

---

## Step 9: Abort incomplete uploads
Expose an abort endpoint and also configure S3 lifecycle cleanup.

### Service
```java
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;

public void abortUpload(String uploadId, String objectKey) {
    s3Client.abortMultipartUpload(
            AbortMultipartUploadRequest.builder()
                    .bucket(BUCKET)
                    .key(objectKey)
                    .uploadId(uploadId)
                    .build()
    );
}
```

---

## Step 10: Save metadata in database
After successful completion, store:
- file_id
- owner_id
- object_key
- size
- content_type
- checksum/content hash
- created_at

### Example entity sketch
```java
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class FileMetadata {
    @Id
    @GeneratedValue
    private UUID id;

    private String ownerId;
    private String fileName;
    private String objectKey;
    private long size;
    private String contentType;
    private String checksum;
    private Instant createdAt = Instant.now();
}
```

---

## Step 11: Add resumability support
Track uploaded parts in DB or ask storage for uploaded parts.

For S3, you can query multipart state:
- `ListParts`
- or keep status in your own DB/session table

### Minimal resumable approach
- save `uploadId`
- save `objectKey`
- save uploaded `partNumbers`
- on reconnect, return missing part numbers

---

## Step 12: Add download support
For downloads:
- return CDN URL if file is public/cached
- or generate pre-signed **GET** URL
- support range requests through S3/CDN

### Example pre-signed download URL
```java
// Similar idea using presigner for GET
```

---

## Practical Spring Boot Architecture
```text
Frontend / Mobile
-> Spring Boot API (init / complete / metadata)
-> S3 direct multipart upload
-> PostgreSQL metadata DB
-> CDN for downloads
```

### Why this is good
- app servers do not proxy large files
- uploads are resumable and parallel
- storage scales independently
- metadata is queryable in SQL
- downloads are fast through CDN

---

# 14) Polished Key Takeaways

- **Naive large-file uploads do not scale**  
  They waste memory, time out easily, and force users to restart from zero.

- **Chunking is the foundation**  
  Breaking files into parts enables retries, resumability, and parallel transfer.

- **Direct-to-storage uploads are the default production pattern**  
  Pre-signed URLs remove the application server from the heavy data path.

- **Use cloud multipart upload when available**  
  It is already optimized for huge files, parallelism, and resume support.

- **Support resumability explicitly**  
  Upload sessions, chunk status tracking, and idempotent retries are what make the UX reliable.

- **Downloads need optimization too**  
  Range requests and CDNs are essential for resume, seeking, and high-scale delivery.

- **Separate metadata from file bytes**  
  Keep searchable/queryable metadata in a DB and large binary data in blob storage.

- **Deduplication is optional but powerful**  
  Content-addressable storage gives file-level dedup cheaply; block-level dedup is for backup/sync-style systems.

- **Choose complexity based on product needs**  
  For most systems: chunked upload + pre-signed URLs + blob storage + CDN is enough.

---

## Final 1-Line Shortcut
```text
Chunk -> Upload Direct -> Parallelize -> Resume -> Store in Blob -> Serve via CDN
```
