package authenticate.gateway.app.repository.env;

import authenticate.gateway.app.model.EnvDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvDetailsRepository extends MongoRepository<EnvDetails, String> {}
