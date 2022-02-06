package test.microservices.gateway.configurations;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@LoadBalancerClient(name = "service-a", configuration = GeneralConfiguration.class)
public class GeneralConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment env,
                                                                   LoadBalancerClientFactory lbClientFactory){
        String propertyName = env.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        System.out.println("SERVICE NAME ========= " + propertyName);
        return new RandomLoadBalancer(
                lbClientFactory.getLazyProvider(propertyName, ServiceInstanceListSupplier.class),
                propertyName);

    }
}
