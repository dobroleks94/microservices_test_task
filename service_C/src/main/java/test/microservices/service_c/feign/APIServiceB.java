package test.microservices.service_c.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import test.microservices.service_c.model.UserClientInfoDTO;

@FeignClient(name = "service-b", url = "http://b.service:8030")
public interface APIServiceB {
    @RequestMapping(value = "/s-b/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    UserClientInfoDTO getUserClientInfo();
}
