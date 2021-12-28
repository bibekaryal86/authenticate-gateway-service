package authenticate.gateway.app.connector;

import authenticate.gateway.app.exception.ServerException;
import authenticate.gateway.app.model.AppUserDetails;
import authenticate.gateway.app.model.AppUserDetailsMongo;
import authenticate.gateway.app.model.AppUserDetailsMongoFilter;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

import static authenticate.gateway.app.util.CommonUtils.getSystemEnvProperty;
import static authenticate.gateway.app.util.ConstantUtils.*;

@Slf4j
@Component
public class AppUserDetailsConnector {

    private final RestTemplate restTemplate;
    public AppUserDetailsConnector() {
        this.restTemplate = new RestTemplate();
    }

    private URI getUri(String endpoint) {
        return UriComponentsBuilder
                .fromHttpUrl(String.format("https://data.mongodb-api.com/app/%s/endpoint/data/beta/action/%s",
                        getSystemEnvProperty(MONGO_API_APP_ID, null), endpoint))
                .build()
                .toUri();
    }

    private HttpEntity<?> getHttpEntity(String username, AppUserDetails appUserDetails) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("api-key", getSystemEnvProperty(MONGO_API_APP_KEY, null));
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString());
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "*");

        AppUserDetailsMongo appUserDetailsMongo = AppUserDetailsMongo.builder()
                .dataSource(getSystemEnvProperty(MONGO_API_DATASOURCE, "").toUpperCase())
                .database(getSystemEnvProperty(MONGO_API_DATASOURCE, null))
                .collection(getSystemEnvProperty(MONGO_API_DATASOURCE, null))
                .document(appUserDetails)
                .filter(username == null ? null : AppUserDetailsMongoFilter.builder()
                        .username(username)
                        .build())
                .build();

        return new HttpEntity<>(appUserDetailsMongo, httpHeaders);
    }

    public AppUserDetails getAppUserDetails(String username) throws ServerException {
        log.info("Get App User Details Request: {}", username);

        try {
            ResponseEntity<AppUserDetailsMongo> responseEntity = restTemplate.exchange(getUri("findOne"),
                    HttpMethod.POST, getHttpEntity(username, null), AppUserDetailsMongo.class);

            log.info("Get App User Details Response: [{}] | [{}]", responseEntity.getStatusCode(), responseEntity.getBody());
            return Objects.requireNonNull(responseEntity.getBody()).getDocument();
        } catch (Exception ex) {
            log.error("Get App User Details Exception: [{}] | [{}]", username, ex.getMessage());
            throw new ServerException("Get App User Details Exception");
        }
    }

    public HttpStatus saveAppUserDetails(AppUserDetails appUserDetails) throws ServerException {
        log.info("Save App User Details Request: {}", appUserDetails);

        try {
            ResponseEntity<AppUserDetailsMongo> responseEntity = restTemplate.exchange(getUri("insertOne"),
                    HttpMethod.POST, getHttpEntity(null, appUserDetails), AppUserDetailsMongo.class);

            log.info("Save App User Details Response: [{}] | [{}]", responseEntity.getStatusCode(), responseEntity.getBody());
            return responseEntity.getStatusCode();
        } catch (Exception ex) {
            log.error("Save App User Details Exception: [{}] | [{}]", appUserDetails, ex.getMessage());
            throw new ServerException("Save App User Details Exception");
        }
    }
}
