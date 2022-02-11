package test.microservices.service_c.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientInfo {
    private String address;
    private int port;
    private String userAgent;
}
