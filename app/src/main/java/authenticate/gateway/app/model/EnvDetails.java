package authenticate.gateway.app.model;

import authenticate.gateway.app.util.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = ConstantUtils.MONGO_COLLECTION_ENV_DETAILS)
public class EnvDetails implements Serializable {
  @MongoId private ObjectId id;
  private String name;
  private String stringValue;
  private List<String> listValue;
  private Map<String, String> mapValue;
}
