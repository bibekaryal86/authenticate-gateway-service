package authenticate.gateway.app.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StringUtils.hasText;

import authenticate.gateway.app.exception.ClientException;
import authenticate.gateway.app.exception.ServerException;
import authenticate.gateway.app.model.UserDetailsRequest;
import authenticate.gateway.app.model.UserDetailsResponse;
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
  public ResponseEntity<UserDetailsResponse> userAuthenticateLogin(
      @PathVariable("username") String username,
      @RequestBody UserDetailsRequest userDetailsRequest,
      ServerHttpRequest request) {
    if (validateRequest(username, userDetailsRequest)) {
      try {
        return ResponseEntity.ok(
            appUserDetailsService.authenticateLogin(userDetailsRequest, getSourceIp(request)));
      } catch (ClientException exc) {
        return new ResponseEntity<>(UserDetailsResponse.builder().build(), HttpStatus.UNAUTHORIZED);
      } catch (ServerException exs) {
        return new ResponseEntity<>(
            UserDetailsResponse.builder().build(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    return new ResponseEntity<>(UserDetailsResponse.builder().build(), HttpStatus.BAD_REQUEST);
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

  private boolean validateRequest(String username, UserDetailsRequest userDetailsRequest) {
    log.info(
        "Incoming Request User Authenticate Login: [{}] | [{}] | [{}] | [{}]",
        username,
        userDetailsRequest,
        userDetailsRequest != null && hasText(userDetailsRequest.getPassword()),
        userDetailsRequest != null
            && userDetailsRequest.getUserDetails() != null
            && hasText(userDetailsRequest.getUserDetails().getPassword()));

    if (hasText(username) && userDetailsRequest != null) {
      boolean isSaveNewUser = userDetailsRequest.getUserDetails() != null;

      if (isSaveNewUser) {
        return hasText(userDetailsRequest.getUserDetails().getUsername())
            && hasText(userDetailsRequest.getUserDetails().getPassword())
            && hasText(userDetailsRequest.getUserDetails().getFirstName())
            && hasText(userDetailsRequest.getUserDetails().getLastName())
            && hasText(userDetailsRequest.getUserDetails().getEmail())
            && username.equals(userDetailsRequest.getUserDetails().getUsername());
      } else {
        return hasText(userDetailsRequest.getUsername())
            && hasText(userDetailsRequest.getPassword())
            && username.equals(userDetailsRequest.getUsername());
      }
    }

    return false;
  }
}
