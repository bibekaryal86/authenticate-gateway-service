package authenticate.gateway.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Base64;

import static authenticate.gateway.app.util.ConstantUtils.*;
import static org.springframework.util.StringUtils.hasText;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {

    public static String getSystemEnvProperty(String keyName, String defaultValue) {
        String envProperty = System.getProperty(keyName) != null ?
                System.getProperty(keyName) :
                System.getenv(keyName);
        return envProperty == null ?
                defaultValue :
                envProperty;
    }

    public static String getAuthConfig(String serviceName) {
        String auth = "";
        String username;
        String password;

        switch (serviceName) {
            case "pets-database":
                username = getSystemEnvProperty(PETS_DATABASE_USR, null);
                password = getSystemEnvProperty(PETS_DATABASE_PWD, null);
                break;
            case "pets-service":
                username = getSystemEnvProperty(PETS_SERVICE_USR, null);
                password = getSystemEnvProperty(PETS_SERVICE_PWD, null);
                break;
            case "health-data":
                username = getSystemEnvProperty(HDT_SERVICE_USR, null);
                password = getSystemEnvProperty(HDT_SERVICE_PWD, null);
                break;
            default:
                username = "";
                password = "";
                break;
        }

        if (hasText(username) && hasText(password)) {
            auth = "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes());
        }

        return auth;
    }
}
