package authenticate.gateway.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantUtils {

  // provided at runtime
  public static final String SERVER_PORT = "PORT";
  public static final String MONGO_USER = "MONGO_USER";
  public static final String MONGO_PWD = "MONGO_PWD";
  public static final String APP_SECRET_KEY = "SECRET_KEY";

  public static final String PETS_DATABASE_USR = "PD_USR";
  public static final String PETS_DATABASE_PWD = "PD_PWD";
  public static final String PETS_SERVICE_USR = "PS_USR";
  public static final String PETS_SERVICE_PWD = "PS_PWD";
  public static final String HDT_SERVICE_USR = "HDT_USR";
  public static final String HDT_SERVICE_PWD = "HDT_PWD";

  // others
  public static final String MONGO_DATABASE_ENV_DETAILS = "env_details";
  public static final String MONGO_DATABASE_USER_DETAILS = "user_details";
  public static final String MONGO_COLLECTION_ENV_DETAILS = "app_authgateway";
  public static final String MONGO_COLLECTION_USER_DETAILS = "userdetails";

  public static final String MONGO_TEMPLATE_ENV_DETAILS = "mongoTemplateEnvDetails";
  public static final String MONGO_TEMPLATE_USER_DETAILS = "mongoTemplateUserDetails";
  public static final String MONGO_CLIENT_ENV_DETAILS = "mongoClientEnvDetails";
  public static final String MONGO_CLIENT_USER_DETAILS = "mongoClientUserDetails";
  public static final String MONGO_DB_FACTORY_ENV_DETAILS = "mongoDbFactoryEnvDetails";
  public static final String MONGO_DB_FACTORY_USER_DETAILS = "mongoDbFactoryUserDetails";

  public static final String MONGO_CONNECTION_STRING = "mongodb+srv://%s:%s@appdetails.bulegrc.mongodb.net/?retryWrites=true&w=majority";
}
