package com.hospital.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hospital.apigateway.filter.JwtAuthenticationFilter;

/**
 * Route Configuration
 * 
 * Configuration class for defining API Gateway routing rules in the hospital management system.
 * This class configures how incoming HTTP requests are routed to various microservices
 * based on URL patterns and other criteria.
 * 
 * Key Responsibilities:
 * - Defines routing rules for microservices
 * - Integrates JWT authentication filter for secured routes
 * - Provides fallback for custom routing not expressible in YAML configuration
 * 
 * Note: Most routing configuration is managed through the config-server YAML files
 * for better externalization and dynamic updates. This Java configuration serves
 * as a fallback for complex routing logic that cannot be expressed in YAML.
 * 
 * @author Hospital Management System
 * @version 1.0
 */
@Configuration
public class RouteConfiguration {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Creates a custom route locator for the API Gateway.
     * 
     * This method defines routing rules for directing incoming requests to appropriate
     * microservices. While most routing is configured via external YAML files through
     * the config-server, this method provides a programmatic alternative for complex
     * routing scenarios that cannot be expressed in YAML.
     * 
     * Current Implementation:
     * - Returns an empty route builder as primary routing is handled by config-server
     * - Can be extended to add custom routes for special cases
     * - Integrates with JwtAuthenticationFilter for authentication
     * 
     * Example custom route that could be added:
     * ```java
     * .route("user-service", r -> r
     *     .path("/api/users/**")
     *     .filters(f -> f.filter(jwtAuthenticationFilter))
     *     .uri("lb://USER-SERVICE"))
     * ```
     * 
     * @param builder RouteLocatorBuilder for constructing route definitions
     * @return RouteLocator with configured routing rules
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // Routes are now primarily configured via config-server YAML files
        // This bean is kept for any custom routing that can't be expressed in YAML
        return builder.routes()
                // Custom routes can be added here if needed
                // Example: Special routing for legacy endpoints or complex path matching
                .build();
    }
}
