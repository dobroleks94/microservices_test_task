package test.microservices.service_b.service;

import org.springframework.stereotype.Service;
import test.microservices.service_b.model.ClientInfo;
import test.microservices.service_b.model.enums.ClientInfoReqHeader;

import javax.servlet.http.HttpServletRequest;

@Service
public class ClientInfoRetriever {

    public ClientInfo getClientInfoFromHttpRequest(HttpServletRequest httpRequest){

        return ClientInfo.builder()
                .address(httpRequest.getRemoteAddr())
                .port(httpRequest.getRemotePort())
                .userAgent(httpRequest.getHeader(ClientInfoReqHeader.USER_AGENT.getValue()))
                .build();
    }
}
