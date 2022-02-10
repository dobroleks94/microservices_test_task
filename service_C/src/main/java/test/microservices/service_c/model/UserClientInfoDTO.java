package test.microservices.service_c.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserClientInfoDTO {
    private UserInfo user;
//    private ClientInfo client;
//    private String user;
    private String client;
}
