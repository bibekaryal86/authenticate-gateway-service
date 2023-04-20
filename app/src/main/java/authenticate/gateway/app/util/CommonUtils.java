package authenticate.gateway.app.util;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {

  public static String getSystemEnvProperty(String keyName, String defaultValue) {
    String envProperty =
        System.getProperty(keyName) != null ? System.getProperty(keyName) : System.getenv(keyName);
    return envProperty == null ? defaultValue : envProperty;
  }

  public static SecretKey getSecretKey() {
    String petsSecretKey = getSystemEnvProperty(ConstantUtils.APP_SECRET_KEY, null);
    return Keys.hmacShaKeyFor(petsSecretKey.getBytes(StandardCharsets.UTF_8));
  }
}
