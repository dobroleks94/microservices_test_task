package test.microservices.service_a.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Greeting {
    private String message;
    private String service;
    private String host;
}
