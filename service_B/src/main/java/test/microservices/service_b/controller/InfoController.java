package test.microservices.service_b.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import test.microservices.service_b.model.UserClientInfoDTO;
import test.microservices.service_b.model.UserInfo;
import test.microservices.service_b.service.InfoService;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@RequestMapping("/s-b")
public class InfoController {

    private InfoService infoService;

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserClientInfoDTO getClientAndUserInfo(HttpServletRequest httpReq){
        return infoService.getUserClientInfo(
                infoService.getUserInfo(),
                infoService.getClientInfo(httpReq)
        );
    }
}
