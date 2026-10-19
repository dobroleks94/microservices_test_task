package test.microservices.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "GOOGLE_CLIENT_ID=test-client-id",
        "GOOGLE_CLIENT_SECRET=test-client-secret",
        "spring.cloud.kubernetes.discovery.enabled=false"})
class GatewayApplicationTests {

    @Test
    void contextLoads() {
    }

}
