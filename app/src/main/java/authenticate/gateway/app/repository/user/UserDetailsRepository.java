package authenticate.gateway.app.repository.user;

import authenticate.gateway.app.model.UserDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends MongoRepository<UserDetails, String> {
  UserDetails findUserDetailsByUsername(String username);
}
