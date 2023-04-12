package authenticate.gateway.app.config;

import authenticate.gateway.app.model.UserDetails;
import authenticate.gateway.app.util.CommonUtils;
import authenticate.gateway.app.util.ConstantUtils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = UserDetails.class, mongoTemplateRef = ConstantUtils.MONGO_TEMPLATE_USER_DETAILS)
public class MongoConfigUserDetails {

    @Bean(name = ConstantUtils.MONGO_CLIENT_USER_DETAILS)
    public MongoClient mongoClient() {
        String username = CommonUtils.getSystemEnvProperty(ConstantUtils.MONGO_USER, null);
        String password = CommonUtils.getSystemEnvProperty(ConstantUtils.MONGO_PWD, null);
        String connectionString = String.format(ConstantUtils.MONGO_CONNECTION_STRING, username, password);
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build());
    }

    @Bean(name = ConstantUtils.MONGO_DB_FACTORY_USER_DETAILS)
    public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier(ConstantUtils.MONGO_CLIENT_USER_DETAILS) MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, ConstantUtils.MONGO_DATABASE_USER_DETAILS);
    }

    @Bean(name = ConstantUtils.MONGO_TEMPLATE_USER_DETAILS)
    public MongoTemplate mongoTemplate(@Qualifier(ConstantUtils.MONGO_DB_FACTORY_USER_DETAILS) MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
