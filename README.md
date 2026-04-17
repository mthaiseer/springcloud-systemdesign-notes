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

### Build tools
- Maven 
- Gradle 

#### Structure of Maven
- ***parent*** : this tag mention current pom version and all dependecies resolved matching to that version 
- ***properties*** : this contains property needed to this project
```xml
<properties>
    <java.version>1.8</java.version>
</properties>
 ```
- ***dependencies and dependency*** : declare dependencies needed for project 
- ***build and plugin*** : plugin needed for this project

#### Structure of Gradle 

create ***build.gradle***

```yaml
plugins{
}
group = ''
version =''
sourceCompatibility = ''

repositories{
  mavenCentral()
}

dependencies{
  implementation ''
  developmentOnly ''
  testImplementation ''
}
```

run ***gradle build***

### Anatomy of Spring boot application 

important annotations 

| Annotation             | description                                                                                      |
|------------------------|--------------------------------------------------------------------------------------------------|
| @SpringbootApplication | This combination <br/> @SpringBootConfiguration<br/> @EnableAutoConfiguration<br/>@componentScan |
 
### Bootstrap application context

```java
ConfigurableApplicationContext ctx = SpringApplication.run(MrFirstApp.class, args)
```
This ctx we can use to access application name, profiles, beans etc...

### Spring boot starters

| Starter                      | Description                                       |
|------------------------------|---------------------------------------------------|
| spring-boot-strater-parent   | decide version and compilation                    |
| spring-boot-starter-web      | manage to setup tomact and web                    |
| spring-boot-starter-jpa      | used for DB access                                |
| spring-boot-starter-test     | support for unit and integration test             |
| spring-boot-starter-security | support for authentication and authorization      |
| spring-boot-starter-actuator | support for monitor under /actuator /info /health |
| spring-boot-starter-logging  | support for logback                               |
| spring-boot-starter-cache    | support for cache                                 |
| spring-boot-starter-aop      | support for AOP                                   |

once we set up these starters @EnableAutoConfiguration will setup during app loading

### Spring boot annonation 


| Annotation                        | Description                                                                    |
|-----------------------------------|--------------------------------------------------------------------------------|
| @Bean                             | declare bean                                                                   |
| @ComponentScan(value = "package") | custom configuration package                                                   |
| @Configuration                    | has contains @Bean used in project                                             |
| @ConfigurationProperties          | advance way to use properies from yml, see example                             |
| @TestPropertySource(locations ="")  | used to configure properties in junit from different class uses @Configuration |
| spring-boot-starter-actuator      | support for monitor under /actuator /info /health                              |
| spring-boot-starter-logging       | support for logback                                                            |
| spring-boot-starter-cache         | support for cache                                                              |
| spring-boot-starter-aop           | support for AOP                                                                |
