package test.microservices.service_c.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test.microservices.service_c.model.UserInfo;

import java.util.Map;

@Service
public class UserService {

    @Value("${user.details.id}")
    private String userId;
    @Value("${user.details.name}")
    private String userName;
    @Value("${user.details.surname}")
    private String userSurname;
    @Value("${user.details.email}")
    private String userEmail;

    public UserInfo getUserInfo(Map<String, String> userDetails) {
        return UserInfo.builder()
                .uuid(userDetails.get(userId))
                .name(userDetails.get(userName))
                .surname(userDetails.get(userSurname))
                .email(userDetails.get(userEmail))
                .build();
    }
}
