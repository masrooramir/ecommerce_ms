package com.baloch.auth.filter;

import com.baloch.auth.service.CustomUserDetailsService;
import com.baloch.auth.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authInput = request.getHeader("Authorization");
        String uri = request.getRequestURI();
        String jwtToken = "";
        String username="";

        if (authInput == null ) {
            System.out.println(uri);
            if(uri.equals("/api/v1/auth/register") || uri.equals("/api/v1/auth/login")) {
                System.out.println("11111111111");
                filterChain.doFilter(request,response);
                return;
            }

            System.out.println("2222222222");

            setResponse(response,HttpStatus.UNAUTHORIZED.value(),
                    "Invalid authentication error!","MISSING_TOKEN_ERROR");
            return;
        }

        if (authInput.startsWith("Bearer ")) {
            jwtToken = authInput.substring(7);

            try {
                username = jwtService.extractUsername(jwtToken);

                if (username == null) {
                    System.out.println("333333333333");
                    setResponse(response, HttpStatus.BAD_REQUEST.value(),
                            "Invalid user token error! Re-login to get valid token", "INVALID_TOKEN_ERROR");
                    return;
                }

                System.out.println("44444444");
                username = jwtService.extractUsername(jwtToken);
            }catch (Exception e){
                setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.toString(), "JWT_TOKEN_ERROR");
                return;
            }

        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            System.out.println("555555555");
            if(!jwtService.validateToken(jwtToken,userDetails)) {
                System.out.println("66666666666");
                setResponse(response,HttpStatus.BAD_REQUEST.value(),
                            "Invalid user token error! Re-login to get valid token","INVALID_TOKEN_ERROR");
                return;
            }
            System.out.println("77777777");

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                if(Objects.equals(request.getRequestURI(), "/api/v1/auth/validate-token")){
                    System.out.println("88888888");

                    filterChain.doFilter(request,response);
                    return;
                }

        }

        filterChain.doFilter(request,response);
    }

    private void setResponse(
            HttpServletResponse response, int statusCode,
            String errorMessage, String errorCode){
        response.resetBuffer();
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", errorMessage);
        errorDetails.put("code", errorCode);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response.getOutputStream().print(objectMapper.writeValueAsString(errorDetails));
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}