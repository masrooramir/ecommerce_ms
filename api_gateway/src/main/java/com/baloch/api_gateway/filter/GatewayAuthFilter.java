package com.baloch.api_gateway.filter;

import com.baloch.api_gateway.config.ApplicationConfig;
import com.baloch.api_gateway.dto.User.User;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private final ApplicationConfig applicationConfig;

    public GatewayAuthFilter(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();

        if (path.startsWith("/api/v1/auth")) {
            System.out.println(path);
            return chain.filter(exchange);
        }

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String jwtToken = authorizationHeader.substring(7);
        System.out.println("000000000000000000");
        System.out.println(jwtToken);

        try{
            System.out.println("11111111111111111");
            var call = applicationConfig.webClient().get()
                    .uri("http://localhost:8084/api/v1/auth/validate-token")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+jwtToken)
                    .retrieve()
                    .bodyToMono(User.class)
                    .flatMap(user -> {
                        System.out.println(user);
                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(builder -> builder
                                        .header("X-User-Object", String.valueOf(user)).build()
                                )
                                .build();

                        return chain.filter(mutatedExchange);
                    });
            System.out.println("2222222222222222222");
            return call;


        }catch (Exception e){
            System.out.println("3333333333333333333");


            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
