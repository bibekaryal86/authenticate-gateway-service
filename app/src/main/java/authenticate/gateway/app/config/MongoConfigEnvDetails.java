package authenticate.gateway.app.config;

import authenticate.gateway.app.repository.env.EnvDetailsRepository;
import authenticate.gateway.app.util.CommonUtils;
import authenticate.gateway.app.util.ConstantUtils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackageClasses = EnvDetailsRepository.class,
    mongoTemplateRef = ConstantUtils.MONGO_TEMPLATE_ENV_DETAILS)
public class MongoConfigEnvDetails {

  @Bean(name = ConstantUtils.MONGO_CLIENT_ENV_DETAILS)
  public MongoClient mongoClient() {
    String username = CommonUtils.getSystemEnvProperty(ConstantUtils.MONGO_USER, null);
    String password = CommonUtils.getSystemEnvProperty(ConstantUtils.MONGO_PWD, null);
    String connectionString =
        String.format(ConstantUtils.MONGO_CONNECTION_STRING, username, password);
    return MongoClients.create(
        MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .build());
  }

  @Primary
  @Bean(name = ConstantUtils.MONGO_DB_FACTORY_ENV_DETAILS)
  public MongoDatabaseFactory mongoDatabaseFactory(
      @Qualifier(ConstantUtils.MONGO_CLIENT_ENV_DETAILS) MongoClient mongoClient) {
    return new SimpleMongoClientDatabaseFactory(
        mongoClient, ConstantUtils.MONGO_DATABASE_ENV_DETAILS);
  }

  @Primary
  @Bean(name = ConstantUtils.MONGO_TEMPLATE_ENV_DETAILS)
  public MongoTemplate mongoTemplate(
      @Qualifier(ConstantUtils.MONGO_DB_FACTORY_ENV_DETAILS)
          MongoDatabaseFactory mongoDatabaseFactory) {
    return new MongoTemplate(mongoDatabaseFactory);
  }
}
