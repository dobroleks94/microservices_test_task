package test.microservices.service_c.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtMapper {

    @Value("${user.details.id}")
    private String userId;
    @Value("${user.details.name}")
    private String userName;
    @Value("${user.details.surname}")
    private String userSurname;
    @Value("${user.details.email}")
    private String userEmail;

    public Map<String, String> retrieveUserDetails(Jwt jwt) {
        return new HashMap<>(){{
            put(userId, jwt.getClaimAsString(userId));
            put(userName, jwt.getClaimAsString(userName));
            put(userSurname, jwt.getClaimAsString(userSurname));
            put(userEmail, jwt.getClaimAsString(userEmail));
        }};
    }
}
