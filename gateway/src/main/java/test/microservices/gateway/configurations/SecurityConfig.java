package test.microservices.gateway.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange()
                .pathMatchers("/actuator/health", "/s-a/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}
