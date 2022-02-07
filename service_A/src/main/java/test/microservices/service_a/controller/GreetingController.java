package test.microservices.service_a.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import test.microservices.service_a.model.Greeting;
import test.microservices.service_a.service.GreetingService;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/s-a")
@AllArgsConstructor
public class GreetingController {

    private final GreetingService greetingService;

    @RequestMapping(value = {"", "/greeting"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Greeting sayHello() throws UnknownHostException {
        return greetingService.resolveGreeting( InetAddress.getLocalHost().getHostName() );
    }
}
