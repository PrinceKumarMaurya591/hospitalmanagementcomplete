package com.hospital.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hospital.apigateway.filter.JwtAuthenticationFilter;

@Configuration
public class RouteConfiguration {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // Routes are now primarily configured via config-server YAML files
        // This bean is kept for any custom routing that can't be expressed in YAML
        return builder.routes()
                // Custom routes can be added here if needed
                .build();
    }
}
