package test.microservices.service_c.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import test.microservices.service_c.model.User;

@RestController
@RequestMapping("/s-c")
public class InfoController {

    @RequestMapping(value = "/info/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUserInfo(@AuthenticationPrincipal Jwt jwt){
        return User.builder()
                .uuid(jwt.getSubject())
                .name(jwt.getClaimAsString("given_name"))
                .surname(jwt.getClaimAsString("family_name"))
                .email(jwt.getClaimAsString("email"))
                .build();
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Jwt getUserClientInfo(@AuthenticationPrincipal Jwt jwt){
        return jwt;
    }
}
