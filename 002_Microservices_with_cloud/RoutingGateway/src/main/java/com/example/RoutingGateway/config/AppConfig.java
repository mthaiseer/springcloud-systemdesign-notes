package com.example.RoutingGateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder){
        return builder.routes()
                .route(p-> p.path("/api/**", "/api/author").uri("lb://SPRINGBOOT-JPA-CACHING")).build();
    }


}
