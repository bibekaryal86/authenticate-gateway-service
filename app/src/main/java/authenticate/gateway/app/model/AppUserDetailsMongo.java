package authenticate.gateway.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUserDetailsMongo implements Serializable {
  @ToString.Exclude private String dataSource;
  @ToString.Exclude private String database;
  @ToString.Exclude private String collection;
  private AppUserDetails document;
  private AppUserDetailsMongoFilter filter;
  private String insertedId;
}
