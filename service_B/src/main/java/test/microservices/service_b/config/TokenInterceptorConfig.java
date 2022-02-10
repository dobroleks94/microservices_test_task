package test.microservices.service_b.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class TokenInterceptorConfig {

    @Value("${interceptor.header}")
    private String authHeader;
    @Value("${interceptor.tokenType}")
    private String tokenType;

    @Bean
    public RequestInterceptor tokenInterceptor(){
        return requestTemplate -> {
            JwtAuthenticationToken jwt =
                    (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            requestTemplate.header(authHeader, String.format("%s %s", tokenType, jwt.getToken().getTokenValue()));
        };
    }
}
