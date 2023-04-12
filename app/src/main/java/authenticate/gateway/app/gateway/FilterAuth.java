package authenticate.gateway.app.gateway;

import static authenticate.gateway.app.util.CommonUtils.getAuthConfig;
import static authenticate.gateway.app.util.CommonUtils.getSystemEnvProperty;
import static authenticate.gateway.app.util.ConstantUtils.APP_SECRET_KEY;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.net.URI;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class FilterAuth implements GatewayFilter {

  private static final List<String> AUTH_EXCLUSIONS =
      List.of("/pets-service/tests/ping", "/pets-database/tests/ping", "/health-data/tests/ping");

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String requestPathString = exchange.getRequest().getPath().toString();
    String authHeaderString = authHeader(requestPathString);

    if (AUTH_EXCLUSIONS.contains(requestPathString)) {
      return chain.filter(exchange);
    } else if (isTokenValid(exchange) && hasText(authHeaderString)) {
      return chain.filter(
          exchange
              .mutate()
              .request(
                  exchange.getRequest().mutate().header("Authorization", authHeaderString).build())
              .build());

    } else {
      logRequestDetails(exchange);
      exchange.getResponse().setStatusCode(UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
  }

  private String authHeader(String requestPathString) {
    String[] strings = requestPathString.split("/");

    if (strings.length > 1) {
      return getAuthConfig(strings[1]);
    }

    return "";
  }

  private boolean isTokenValid(ServerWebExchange exchange) {
    try {
      HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
      List<String> tokens = httpHeaders.get("Authorization");

      if (!isEmpty(tokens)) {
        String oldToken = tokens.get(0);
        oldToken = oldToken.replace("Bearer ", "");

        Claims claims =
            Jwts.parser()
                .setSigningKey(getSystemEnvProperty(APP_SECRET_KEY, null))
                .parseClaimsJws(oldToken)
                .getBody();

        return claims.getExpiration().after(new Date(System.currentTimeMillis()));
      }
    } catch (Exception ex) {
      log.error("Error parsing request token: {}, {}", ex.getClass().getName(), ex.getMessage());
    }

    return false;
  }

  private void logRequestDetails(ServerWebExchange exchange) {
    URI incomingUri = exchange.getRequest().getURI();
    HttpMethod httpMethod = exchange.getRequest().getMethod();

    log.info(
        "Invalid / Expired Auth Token:: Incoming: [ {} ] | Method: [ {} ]",
        incomingUri,
        httpMethod);
  }
}
