package test.microservices.service_b.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import test.microservices.service_b.feign.APIServiceC;
import test.microservices.service_b.model.UserClientInfoDTO;
import test.microservices.service_b.model.UserInfo;

@Service
@AllArgsConstructor
public class InfoService {

    private APIServiceC apiServiceC;

    public UserClientInfoDTO getUserClientInfo(){
        UserInfo user = apiServiceC.getUserInfo();
        return UserClientInfoDTO.builder()
                .user(user)
                .client("Client Info")
                .build();
    }

}
