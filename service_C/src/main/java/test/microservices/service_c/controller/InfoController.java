package test.microservices.service_c.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import test.microservices.service_c.feign.APIServiceB;
import test.microservices.service_c.mapper.JwtMapper;
import test.microservices.service_c.model.UserClientInfoDTO;
import test.microservices.service_c.model.UserInfo;
import test.microservices.service_c.service.UserService;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/s-c")
public class InfoController {

    private final UserService userService;
    private final JwtMapper jwtToUserDetails;
    private final APIServiceB apiServiceB;

    @RequestMapping(value = "/info/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo getUserInfo(@AuthenticationPrincipal Jwt jwt){
        Map<String, String> userDetails = jwtToUserDetails.retrieveUserDetails(jwt);
        return userService.getUserInfo(userDetails);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserClientInfoDTO getUserClientInfo(@AuthenticationPrincipal Jwt jwt){
        return apiServiceB.getUserClientInfo();
    }
}
