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
public class AppUserDetailsRequest implements Serializable {
  private String username;
  @ToString.Exclude private String password;
  private AppUserDetails userDetails;
  private String sourceIp;
}
