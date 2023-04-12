package authenticate.gateway.app.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StringUtils.hasText;

import authenticate.gateway.app.exception.ClientException;
import authenticate.gateway.app.exception.ServerException;
import authenticate.gateway.app.model.AppUserDetailsRequest;
import authenticate.gateway.app.model.AppUserDetailsResponse;
import authenticate.gateway.app.service.AppUserDetailsService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class AppUserDetailsController {

  private final AppUserDetailsService appUserDetailsService;

  public AppUserDetailsController(AppUserDetailsService appUserDetailsService) {
    this.appUserDetailsService = appUserDetailsService;
  }

  @CrossOrigin
  @PostMapping(value = "/authenticate-gateway/{username}/login", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<AppUserDetailsResponse> userAuthenticateLogin(
      @PathVariable("username") String username,
      @RequestBody AppUserDetailsRequest appUserDetailsRequest,
      ServerHttpRequest request) {
    if (validateRequest(username, appUserDetailsRequest)) {
      try {
        return ResponseEntity.ok(
            appUserDetailsService.authenticateLogin(appUserDetailsRequest, getSourceIp(request)));
      } catch (ClientException exc) {
        return new ResponseEntity<>(
            AppUserDetailsResponse.builder().build(), HttpStatus.UNAUTHORIZED);
      } catch (ServerException exs) {
        return new ResponseEntity<>(
            AppUserDetailsResponse.builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    return new ResponseEntity<>(AppUserDetailsResponse.builder().build(), HttpStatus.BAD_REQUEST);
  }

  // ServerHttpRequest for webflux-starter, HttpServletRequest for web-starter
  private String getSourceIp(ServerHttpRequest request) {
    try {
      return Objects.requireNonNull(request.getHeaders().get("X-Forwarded-For")).get(0);
    } catch (NullPointerException ignored) {
      /* ignored */
    }

    try {
      return Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
    } catch (NullPointerException ignored) {
      /* ignored */
    }

    log.error("Returning Default Source IP: [0:0:0:0:0:0:0:1]");
    return "0:0:0:0:0:0:0:1";
  }

  private boolean validateRequest(String username, AppUserDetailsRequest appUserDetailsRequest) {
    log.info(
        "Incoming Request User Authenticate Login: [{}] | [{}] | [{}] | [{}]",
        username,
        appUserDetailsRequest,
        appUserDetailsRequest != null && hasText(appUserDetailsRequest.getPassword()),
        appUserDetailsRequest != null
            && appUserDetailsRequest.getUserDetails() != null
            && hasText(appUserDetailsRequest.getUserDetails().getPassword()));

    if (hasText(username) && appUserDetailsRequest != null) {
      boolean isSaveNewUser = appUserDetailsRequest.getUserDetails() != null;

      if (isSaveNewUser) {
        return hasText(appUserDetailsRequest.getUserDetails().getUsername())
            && hasText(appUserDetailsRequest.getUserDetails().getPassword())
            && hasText(appUserDetailsRequest.getUserDetails().getFirstName())
            && hasText(appUserDetailsRequest.getUserDetails().getLastName())
            && hasText(appUserDetailsRequest.getUserDetails().getEmail())
            && username.equals(appUserDetailsRequest.getUserDetails().getUsername());
      } else {
        return hasText(appUserDetailsRequest.getUsername())
            && hasText(appUserDetailsRequest.getPassword())
            && username.equals(appUserDetailsRequest.getUsername());
      }
    }

    return false;
  }
}
