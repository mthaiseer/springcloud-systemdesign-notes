## This repository contains spring boot with cloud for system designs
---

### 12 factor
- [x] Common code base
- [x] External dependencies
- [x] Store config in environment 
- [x] Backing services : correct config for ext services for Redis, DB etc.
- [x] separate build with pom.xml and run stages
- [x] run app as stateless process
- [x] port binding - port 8080 bind natively to attach with other process like postman, JMeter
- [x] concurrency - scale in and out to adjust user traffic
- [x] Disposability - quick start up and graceful shutdown 
- [x] Dev/prod parity - both dev, stage and production should identical 
- [x] process logs - stream logs to log aggragator like splunk 
- [x] seperate process for ADMIN and priviledged users




