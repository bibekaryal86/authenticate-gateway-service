package authenticate.gateway.app.gateway;

import authenticate.gateway.app.service.EnvDetailsService;
import authenticate.gateway.app.util.ConstantUtils;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Routes {

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
    RouteLocatorBuilder.Builder routes = builder.routes();

    log.info("Getting Route Paths...");
    List<String> routePaths =
        envDetailsService
            .getEnvDetails(ConstantUtils.ENV_DETAILS_ROUTE_PATHS)
            .get(0)
            .getListValue();
    log.info("Getting Base Urls...");
    Map<String, String> baseUrlsMap =
        envDetailsService
            .getEnvDetails(
                String.format(ConstantUtils.ENV_DETAILS_BASE_URLS, this.springProfilesActive))
            .get(0)
            .getMapValue();

    for (String routePath : routePaths) {
      String uri = baseUrlsMap.get(routePath);
      routes.route(
          r ->
              r.path(routePath).filters(f -> f.filter(new FilterAuth(envDetailsService))).uri(uri));
    }

    return routes.build();
  }
}
