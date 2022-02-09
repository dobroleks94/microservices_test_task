package test.microservices.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FetchJwtFromAccessTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {


    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->
                exchange.getPrincipal()
                        .filter(token -> token instanceof OAuth2AuthenticationToken)
                        .cast(OAuth2AuthenticationToken.class)
                        .map(OAuth2AuthenticationToken::getPrincipal)
                        .filter(principal -> principal instanceof OidcUser)
                        .cast(OidcUser.class)
                        .map(OidcUser::getIdToken)
                        .map(token -> withBearerAuth(exchange, token.getTokenValue()))
                        .defaultIfEmpty(exchange)
                        .flatMap(chain::filter);
    }

    private ServerWebExchange withBearerAuth(ServerWebExchange exchange, String accessToken) {
        return exchange.mutate().request(r -> r.headers(headers -> headers.setBearerAuth(accessToken))).build();
    }
}
