package com.RentXDemo.apigateway.routing;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutingConfiguration {

    @Bean
    RouteLocator routing(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route("AUTH-SERVICE", r -> r.path("/auth-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://AUTH-SERVICE/"))
                .build();
    }
}