package authenticate.gateway.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUserDetails implements Serializable {
    @JsonProperty("_id")
    private String id;
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
