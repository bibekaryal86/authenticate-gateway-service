package authenticate.gateway.app.util;

import static authenticate.gateway.app.util.ConstantUtils.*;
import static org.springframework.util.StringUtils.hasText;

import java.util.Base64;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {

  public static String getSystemEnvProperty(String keyName, String defaultValue) {
    String envProperty =
        System.getProperty(keyName) != null ? System.getProperty(keyName) : System.getenv(keyName);
    return envProperty == null ? defaultValue : envProperty;
  }

  public static String getAuthConfig(String serviceName) {
    String auth = "";
    String username;
    String password;
    String systemEnvPropertyUser = serviceName + SERVICE_AUTH_USR;
    String systemEnvPropertyPassword = serviceName + SERVICE_AUTH_PWD;
    username = getSystemEnvProperty(systemEnvPropertyUser, null);
    password = getSystemEnvProperty(systemEnvPropertyPassword, null);

    if (hasText(username) && hasText(password)) {
      auth =
          "Basic "
              + Base64.getEncoder()
                  .encodeToString(String.format("%s:%s", username, password).getBytes());
    }

    return auth;
  }
}
