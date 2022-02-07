package test.microservices.gateway.configurations;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class SwitchLoadBalancerAlgo {
    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment env,
                                                                   LoadBalancerClientFactory lbClientFactory){
        String propertyName = env.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(
                lbClientFactory.getLazyProvider(propertyName, ServiceInstanceListSupplier.class),
                propertyName);

    }
}
