package test.microservices.service_b.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String uuid;
    private String name;
    private String surname;
    private String email;
}
