package authenticate.gateway.app.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static authenticate.gateway.app.util.CommonUtils.getAuthConfig;
import static org.springframework.util.StringUtils.hasText;

@Component
public class FilterAuth implements GatewayFilter {

    private static final List<String> AUTH_EXCLUSIONS = List.of(
            "/pets-service/tests/ping",
            "/pets-database/tests/ping",
            "health-data/tests/ping"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPathString = exchange.getRequest().getPath().toString();
        String authHeaderString = authHeader(requestPathString);

        if (!AUTH_EXCLUSIONS.contains(requestPathString) && hasText(authHeaderString)) {
            return chain.filter(
                    exchange.mutate()
                            .request(exchange.getRequest()
                                    .mutate()
                                    .header("Authorization", authHeaderString)
                                    .build())
                            .build());
        }

        return chain.filter(exchange);
    }

    private String authHeader(String requestPathString) {
        String[] strings = requestPathString.split("/");

        if (strings.length > 1) {
            return getAuthConfig(strings[1]);
        }

        return "";
    }
}
