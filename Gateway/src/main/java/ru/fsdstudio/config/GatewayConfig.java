package ru.fsdstudio.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("person-service", r -> r
//                        .path("/person/**")
//                        .filters(f -> f.rewritePath("/person/(?<segment>.*)", "/${segment}"))
//                        .uri("lb://person-service"))
//                .route("product-service", r -> r
//                        .path("/product/**")
//                        .filters(f -> f.rewritePath("/product/(?<segment>.*)", "/${segment}"))
//                        .uri("lb://product-service"))
//                .build();
//    }
//}

//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("person-service", r -> r
//                        .path("/person/**")
//                        .uri("lb://person-service"))
//                .route("product-service", r -> r
//                        .path("/product/**")
//                        .uri("lb://product-service"))
//                .build();
//    }
//}

@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("person-service", r -> r
                        .path("/person/**")
                        .uri("http://localhost:8081/person"))
                .route("product-service", r -> r
                        .path("/product/**")
                        .uri("http://localhost:8082/product"))
                .build();
    }
}
