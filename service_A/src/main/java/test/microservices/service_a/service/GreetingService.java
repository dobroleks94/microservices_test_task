package test.microservices.service_a.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test.microservices.service_a.model.Greeting;

@Service
public class GreetingService {

    @Value("${greeting.message}")
    private String greetingMessage;
    @Value("${spring.application.name}")
    private String service;

    public Greeting resolveGreeting(String hostName){
        return Greeting.builder()
                .service(service)
                .host(hostName)
                .message(greetingMessage)
                .build();
    }
}
