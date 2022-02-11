package test.microservices.service_b.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import test.microservices.service_b.feign.APIServiceC;
import test.microservices.service_b.model.ClientInfo;
import test.microservices.service_b.model.UserClientInfoDTO;
import test.microservices.service_b.model.UserInfo;

import javax.servlet.http.HttpServletRequest;

@Service
@AllArgsConstructor
public class InfoService {

    private APIServiceC serviceC;
    private ClientInfoRetriever headerProcessor;

    public ClientInfo getClientInfo(HttpServletRequest httpReq){
        return headerProcessor.getClientInfoFromHttpRequest(httpReq);
    }
    public UserInfo getUserInfo(){
        return serviceC.getUserInfo();
    }

    public UserClientInfoDTO getUserClientInfo(UserInfo user, ClientInfo client){
        return UserClientInfoDTO.builder()
                .user(user)
                .client(client)
                .build();
    }

}
