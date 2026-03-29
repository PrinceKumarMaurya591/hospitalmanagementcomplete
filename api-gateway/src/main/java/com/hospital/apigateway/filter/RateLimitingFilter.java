package com.hospital.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private static final String RATE_LIMIT_SCRIPT = """
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refillRate = tonumber(ARGV[2])
            local refillDuration = tonumber(ARGV[3])
            local now = tonumber(ARGV[4])
            
            local bucket = redis.call('hmget', key, 'tokens', 'lastRefill')
            
            local tokens = capacity
            local lastRefill = now
            
            if bucket[1] and bucket[2] then
                tokens = tonumber(bucket[1])
                lastRefill = tonumber(bucket[2])
                
                local timePassed = now - lastRefill
                local refills = math.floor(timePassed / refillDuration)
                local newTokens = math.min(capacity, tokens + refills * refillRate)
                
                if refills > 0 then
                    tokens = newTokens
                    lastRefill = lastRefill + refills * refillDuration
                end
            end
            
            if tokens >= 1 then
                tokens = tokens - 1
                redis.call('hmset', key, 'tokens', tokens, 'lastRefill', lastRefill)
                redis.call('expire', key, math.ceil(capacity / refillRate) * refillDuration * 2)
                return 1
            else
                return 0
            end
            """;

    public RateLimitingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Get client identifier (IP address or user ID from JWT)
            String clientId = getClientIdentifier(request);
            String rateLimitKey = "rate_limit:" + clientId;
            
            // Rate limit configuration
            int capacity = config.getCapacity();
            int refillRate = config.getRefillRate();
            int refillDuration = config.getRefillDuration();
            
            long now = Instant.now().getEpochSecond();
            
            List<String> keys = Arrays.asList(rateLimitKey);
            List<String> args = Arrays.asList(
                    String.valueOf(capacity),
                    String.valueOf(refillRate),
                    String.valueOf(refillDuration),
                    String.valueOf(now)
            );
            
            RedisScript<Long> script = RedisScript.of(RATE_LIMIT_SCRIPT, Long.class);
            
            return redisTemplate.execute(script, keys, args)
                    .next() // Convert Flux<Long> to Mono<Long> by taking the first element
                    .flatMap(result -> {
                        if (result == 1) {
                            // Request allowed
                            return chain.filter(exchange);
                        } else {
                            // Rate limit exceeded
                            return rateLimitExceeded(exchange);
                        }
                    });
        };
    }
    
    private String getClientIdentifier(ServerHttpRequest request) {
        // Try to get user ID from JWT header first
        String userId = request.getHeaders().getFirst("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return "user:" + userId;
        }
        
        // Fall back to IP address
        String ipAddress = request.getRemoteAddress() != null ? 
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        return "ip:" + ipAddress;
    }
    
    private Mono<Void> rateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("X-RateLimit-Limit", "100");
        response.getHeaders().add("X-RateLimit-Remaining", "0");
        response.getHeaders().add("X-RateLimit-Reset", String.valueOf(Instant.now().plusSeconds(60).getEpochSecond()));
        
        String errorBody = "{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Please try again later.\", \"status\": 429}";
        DataBuffer buffer = response.bufferFactory().wrap(errorBody.getBytes());
        response.getHeaders().add("Content-Type", "application/json");
        
        return response.writeWith(Mono.just(buffer));
    }
    
    public static class Config {
        private int capacity = 100;
        private int refillRate = 10;
        private int refillDuration = 1; // seconds
        
        public int getCapacity() {
            return capacity;
        }
        
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
        
        public int getRefillRate() {
            return refillRate;
        }
        
        public void setRefillRate(int refillRate) {
            this.refillRate = refillRate;
        }
        
        public int getRefillDuration() {
            return refillDuration;
        }
        
        public void setRefillDuration(int refillDuration) {
            this.refillDuration = refillDuration;
        }
    }
}