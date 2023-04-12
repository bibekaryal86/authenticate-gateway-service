package authenticate.gateway.app.service;

import authenticate.gateway.app.exception.ClientException;
import authenticate.gateway.app.exception.ServerException;
import authenticate.gateway.app.model.UserDetails;
import authenticate.gateway.app.model.UserDetailsRequest;
import authenticate.gateway.app.model.UserDetailsResponse;
import authenticate.gateway.app.repository.UserDetailsRepository;
import authenticate.gateway.app.util.CommonUtils;
import authenticate.gateway.app.util.ConstantUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService {

  private final UserDetailsRepository userDetailsRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ObjectMapper objectMapper;

  public AppUserDetailsService(UserDetailsRepository userDetailsRepository) {
    this.userDetailsRepository = userDetailsRepository;
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    this.objectMapper = new ObjectMapper();
  }

  public UserDetailsResponse authenticateLogin(
      UserDetailsRequest userDetailsRequest, String sourceIp)
      throws ServerException, ClientException {
    UserDetails userDetails;

    if (userDetailsRequest.getUserDetails() == null) {
      userDetails =
          getUserDetails(userDetailsRequest.getUsername(), userDetailsRequest.getPassword());
    } else {
      userDetails = saveUserDetails(userDetailsRequest.getUserDetails());
    }

    UserDetails userDetailsToReturn = UserDetails.builder().build();
    BeanUtils.copyProperties(userDetails, userDetailsToReturn, "password");

    String token = createToken(userDetails.getUsername(), sourceIp);
    return UserDetailsResponse.builder().token(token).userDetails(userDetailsToReturn).build();
  }

  private UserDetails getUserDetails(String username, String passwordInput)
      throws ServerException, ClientException {
    try {
      UserDetails userDetails = userDetailsRepository.findUserDetailsByUsername(username);

      if (userDetails == null) {
        throw new ClientException(String.format("Get User Details User is Null: [ %s ]", username));
      }

      if (!bCryptPasswordEncoder.matches(passwordInput, userDetails.getPassword())) {
        throw new ClientException(
            String.format("Get User Details User Wrong Password: [ %s ]", username));
      }

      return userDetails;
    } catch (Exception ex) {
      throw new ServerException("Get User Details Error", ex.getCause());
    }
  }

  private UserDetails saveUserDetails(UserDetails userDetails) throws ServerException {
    UserDetails userDetailsNew =
        UserDetails.builder()
            .password(bCryptPasswordEncoder.encode(userDetails.getPassword()))
            .build();
    BeanUtils.copyProperties(userDetails, userDetailsNew, "password");
    try {
      return userDetailsRepository.save(userDetailsNew);
    } catch (Exception ex) {
      throw new ServerException("Save User Details Error", ex);
    }
  }

  public String createToken(String username, String sourceIp) throws ServerException {
    try {
      UserDetailsRequest tokenClaim =
          UserDetailsRequest.builder().username(username).sourceIp(sourceIp).build();

      Map<String, Object> claims = objectMapper.convertValue(tokenClaim, new TypeReference<>() {});

      return Jwts.builder()
          .setClaims(claims)
          .signWith(
              SignatureAlgorithm.HS512,
              CommonUtils.getSystemEnvProperty(ConstantUtils.APP_SECRET_KEY, null))
          .setExpiration(
              new Date(
                  System.currentTimeMillis()
                      + 864000000)) // 24 hours, enforce inactive session in UI
          .compact();
    } catch (Exception ex) {
      throw new ServerException(
          String.format("Create Token Exception: [ %s ] [ %s ]", username, sourceIp), ex);
    }
  }
}
