package authenticate.gateway.app.gateway;

import static authenticate.gateway.app.util.CommonUtils.getSecretKey;

import authenticate.gateway.app.model.EnvDetails;
import authenticate.gateway.app.service.EnvDetailsService;
import authenticate.gateway.app.util.ConstantUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class FilterAuth implements GatewayFilter {

  private final EnvDetailsService envDetailsService;

  private static List<String> authExclusions = new ArrayList<>();
  private static Map<String, String> authConfigs = new HashMap<>();

  public FilterAuth(EnvDetailsService envDetailsService) {
    this.envDetailsService = envDetailsService;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String requestPathString = exchange.getRequest().getPath().toString();
    String authHeaderString = authHeader(requestPathString);

    if (authExclusions.isEmpty()) {
      setAuthExclusions();
    }

    if (authExclusions.contains(requestPathString)) {
      return chain.filter(exchange);
    } else if (isTokenValid(exchange) && StringUtils.hasText(authHeaderString)) {
      return chain.filter(
          exchange
              .mutate()
              .request(
                  exchange.getRequest().mutate().header("Authorization", authHeaderString).build())
              .build());

    } else {
      logRequestDetails(exchange);
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
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

  private String getAuthConfig(String serviceName) {
    if (authConfigs.isEmpty()) {
      setAuthConfigs();
    }

    String auth = "";
    String serviceUsernamePropertyName = serviceName + ConstantUtils.SERVICE_AUTH_USR;
    String servicePasswordPropertyName = serviceName + ConstantUtils.SERVICE_AUTH_PWD;
    String username = authConfigs.get(serviceUsernamePropertyName);
    String password = authConfigs.get(servicePasswordPropertyName);

    if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
      auth =
          "Basic "
              + Base64.getEncoder()
                  .encodeToString(String.format("%s:%s", username, password).getBytes());
    }

    return auth;
  }

  private boolean isTokenValid(ServerWebExchange exchange) {
    try {
      HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
      List<String> tokens = httpHeaders.get("Authorization");

      if (!CollectionUtils.isEmpty(tokens)) {
        String oldToken = tokens.get(0);
        oldToken = oldToken.replace("Bearer ", "");

        Claims claims =
            Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
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

  @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 6)
  private void setAuthExclusions() {
    log.info("Setting Auth Exclusions...");
    EnvDetails envDetails =
        envDetailsService.getEnvDetails(ConstantUtils.ENV_DETAILS_AUTH_EXCLUSIONS).get(0);
    authExclusions = Collections.unmodifiableList(envDetails.getListValue());
  }

  @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 6)
  private void setAuthConfigs() {
    log.info("Setting Auth Configs...");
    EnvDetails envDetails =
        envDetailsService.getEnvDetails(ConstantUtils.ENV_DETAILS_AUTH_CONFIGS).get(0);
    authConfigs = Collections.unmodifiableMap(envDetails.getMapValue());
  }
}
