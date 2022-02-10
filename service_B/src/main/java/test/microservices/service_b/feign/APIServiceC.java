package test.microservices.service_b.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import test.microservices.service_b.model.UserInfo;

@FeignClient(name = "service-c")
public interface APIServiceC {
    @RequestMapping(value = "/s-c/info/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    UserInfo getUserInfo();
}
