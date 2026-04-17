## Spring boot with JPA and Caching 

### create new spring boot project 
create new spring boot application with the following dependencies
- jpa
- H2
- Web
- DevTools

### Enable cache

1. add cache depedency 

```yaml
    implementation 'org.springframework.boot:spring-boot-starter-cache'
```

2. add Cachable annotation 

```java
    @Cacheable("authors")
    public List<Author> findAllAuthors() throws InterruptedException {
        Thread.sleep(3000);
        List<Author> authors = new ArrayList<>();
        authorRepository.findAll().forEach(authors::add);
        return authors;
    }
```

3. Add @EnableCaching to main class 

```java

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringbootJpaCachingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootJpaCachingApplication.class, args);
	}

}
```