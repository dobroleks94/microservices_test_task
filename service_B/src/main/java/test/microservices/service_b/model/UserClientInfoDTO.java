package test.microservices.service_b.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserClientInfoDTO {
    private UserInfo user;
    private ClientInfo client;
}
