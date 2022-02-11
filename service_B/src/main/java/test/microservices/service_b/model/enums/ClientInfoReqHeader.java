package test.microservices.service_b.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientInfoReqHeader {
    USER_AGENT("User-Agent");

    private final String value;
}
