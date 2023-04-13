package authenticate.gateway.app.gateway;

import authenticate.gateway.app.model.EnvDetails;
import authenticate.gateway.app.service.EnvDetailsService;
import authenticate.gateway.app.util.ConstantUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class Routes {

  private List<String> routePaths = new ArrayList<>();
  private Map<String, String> baseUrls = new HashMap<>();

  private final String springProfilesActive;
  private final EnvDetailsService envDetailsService;

  public Routes(
      @Value("${spring.profiles.active}") String springProfilesActive,
      EnvDetailsService envDetailsService) {
    this.springProfilesActive = springProfilesActive;
    this.envDetailsService = envDetailsService;
  }

  @Bean
  public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(
            r ->
                r.path("/pets-database/**")
                    .filters(f -> f.filter(new FilterAuth(envDetailsService)))
                    .uri("https://pets-database.appspot.com"))
        .route(
            r ->
                r.path("/pets-service/**")
                    .filters(f -> f.filter(new FilterAuth(envDetailsService)))
                    .uri("https://pets-service.appspot.com"))
        .route(
            r ->
                r.path("/pets-authenticate/**")
                    .filters(f -> f.filter(new FilterAuth(envDetailsService)))
                    .uri("https://pets-authenticate.appspot.com"))
        .route(
            r ->
                r.path("/health-data/**")
                    .filters(f -> f.filter(new FilterAuth(envDetailsService)))
                    .uri("https://healthdatajava.appspot.com"))
        .build();
  }

  @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 6)
  private void setPaths() {
    log.info("Setting Paths...");
    EnvDetails envDetails =
        envDetailsService.getEnvDetails(ConstantUtils.ENV_DETAILS_ROUTE_PATHS).get(0);
    routePaths = Collections.unmodifiableList(envDetails.getListValue());
  }

  @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 6)
  private void setBaseUrls() {
    log.info("Setting Base Urls...");
    EnvDetails envDetails =
        envDetailsService
            .getEnvDetails(
                String.format(ConstantUtils.ENV_DETAILS_BASE_URLS, this.springProfilesActive))
            .get(0);
    baseUrls = Collections.unmodifiableMap(envDetails.getMapValue());
  }
}
