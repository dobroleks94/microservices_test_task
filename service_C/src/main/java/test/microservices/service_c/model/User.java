package test.microservices.service_c.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String uuid;
    private String name;
    private String surname;
    private String email;
}
