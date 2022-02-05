package test.microservices.service_a.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@RestController
@RequestMapping("/s-a")
public class GreetingController {

    @Value("${spring.application.name}")
    private String applicationName;

    @RequestMapping(value = {"", "/greeting"}, method = RequestMethod.GET)
    public String sayHello(){
        return String.format("Hello from '%s' (address: %s)",
                applicationName,
                InetAddress.getLoopbackAddress().getHostAddress());
    }
}
