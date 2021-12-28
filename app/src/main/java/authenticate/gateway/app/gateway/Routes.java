package authenticate.gateway.app.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    // Add routes here, and also update AUTH_EXCLUSIONS in FilterAuth.java if needed

    @Value("${routes_base_url.pets_service}")
    private String petsServiceBaseUrl;

    @Value("${routes_base_url.pets_database}")
    private String petsDatabaseBaseUrl;

    @Value("${routes_base_url.pets_authenticate}")
    private String petsAuthenticateBaseUrl;

    @Value("${routes_base_url.hdt_service}")
    private String hdtServiceBaseUrl;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r
                        .path("/pets-database/**")
                        .filters(f -> f.filter(new FilterAuth()))
                        .uri(this.petsDatabaseBaseUrl))
                .route(r -> r
                        .path("/pets-service/**")
                        .filters(f -> f.filter(new FilterAuth()))
                        .uri(this.petsServiceBaseUrl))
                .route(r -> r
                        .path("/pets-authenticate/**")
                        .uri(this.petsAuthenticateBaseUrl))
                .route(r -> r
                        .path("/health-data/**")
                        .filters(f -> f.filter(new FilterAuth()))
                        .uri(this.hdtServiceBaseUrl))
                .build();
    }
}
