package test.microservices.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import test.microservices.gateway.configurations.SwitchLoadBalancerAlgo;

@SpringBootApplication
@LoadBalancerClient(name = "service-a", configuration = SwitchLoadBalancerAlgo.class)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
