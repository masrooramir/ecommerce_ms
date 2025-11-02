package com.baloch.api_gateway.filter;

import com.baloch.api_gateway.config.ApplicationConfig;
import com.baloch.api_gateway.dto.User.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
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

        try{
            return applicationConfig.webClient().get()
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
        }catch (Exception e){
            System.out.println("Error is here: "+e +" Error Ends here!");
            System.out.println("Status Code" + response.getStatusCode());
            System.out.println("Headers " + response.getHeaders());

//            DataBuffer buffer = response.bufferFactory().allocateBuffer();

            // Write the DataBuffer to the response
//            return response.writeWith(Flux.just(buffer));

            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
}
