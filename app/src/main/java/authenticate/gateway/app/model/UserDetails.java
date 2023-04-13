package authenticate.gateway.app.model;

import authenticate.gateway.app.util.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = ConstantUtils.MONGO_COLLECTION_USER_DETAILS)
public class UserDetails implements Serializable {
  @MongoId private ObjectId id;
  private String username;
  @ToString.Exclude private String password;
  private String firstName;
  private String lastName;
  @ToString.Exclude private String streetAddress;
  private String city;
  private String state;
  private String zipCode;
  private String email;
  @ToString.Exclude private String phone;
  private String status;
}
