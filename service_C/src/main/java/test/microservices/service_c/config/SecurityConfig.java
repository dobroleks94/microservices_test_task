package test.microservices.service_c.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${interceptor.header}")
    private String authHeader;
    @Value("${interceptor.tokenType}")
    private String tokenType;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    @Bean
    public RequestInterceptor bearerTokenInterceptor(){
        return requestTemplate -> {
            JwtAuthenticationToken jwt =
                    (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            requestTemplate.header(authHeader, String.format("%s %s", tokenType, jwt.getToken().getTokenValue()));
        };
    }
}
