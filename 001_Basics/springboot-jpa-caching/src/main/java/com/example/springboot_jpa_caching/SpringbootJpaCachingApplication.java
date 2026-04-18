package com.example.springboot_jpa_caching;

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
