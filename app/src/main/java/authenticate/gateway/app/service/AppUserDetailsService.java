package authenticate.gateway.app.service;

import authenticate.gateway.app.connector.AppUserDetailsConnector;
import authenticate.gateway.app.exception.ClientException;
import authenticate.gateway.app.exception.ServerException;
import authenticate.gateway.app.model.AppUserDetails;
import authenticate.gateway.app.model.AppUserDetailsRequest;
import authenticate.gateway.app.model.AppUserDetailsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import static authenticate.gateway.app.util.CommonUtils.getSystemEnvProperty;
import static authenticate.gateway.app.util.ConstantUtils.APP_SECRET_KEY;

@Slf4j
@Service
public class AppUserDetailsService {

    private final AppUserDetailsConnector appUserDetailsConnector;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    public AppUserDetailsService(AppUserDetailsConnector appUserDetailsConnector) {
        this.appUserDetailsConnector = appUserDetailsConnector;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.objectMapper = new ObjectMapper();
    }

    public AppUserDetailsResponse authenticateLogin(AppUserDetailsRequest appUserDetailsRequest, String sourceIp)
            throws ServerException, ClientException {
        AppUserDetails appUserDetails;

        if (appUserDetailsRequest.getUserDetails() == null) {
            appUserDetails = getAppUserDetails(appUserDetailsRequest.getUsername(), appUserDetailsRequest.getPassword());
        } else {
            appUserDetails = saveAppUserDetails(appUserDetailsRequest.getUserDetails());
        }

        AppUserDetails appUserDetailsToReturn = AppUserDetails.builder().build();
        BeanUtils.copyProperties(appUserDetails, appUserDetailsToReturn, "password");

        String token = createToken(appUserDetails.getUsername(), sourceIp);
        return AppUserDetailsResponse.builder()
                .token(token)
                .userDetails(appUserDetailsToReturn)
                .build();
    }

    private AppUserDetails getAppUserDetails(String username, String passwordInput) throws ServerException, ClientException {
        AppUserDetails appUserDetails = appUserDetailsConnector.getAppUserDetails(username);

        if (appUserDetails == null) {
            log.info("Get App User Details Error Null: [{}]", username);
            throw new ClientException("Get App User Details Error Null");
        }

        if (bCryptPasswordEncoder.matches(passwordInput, appUserDetails.getPassword())) {
            return appUserDetails;
        } else {
            log.error("Get App User Details Wrong Password: [{}] | [{}]", username, appUserDetails.getId());
            throw new ClientException("Get App User Details Wrong Password");
        }
    }

    private AppUserDetails saveAppUserDetails(AppUserDetails appUserDetails) throws ServerException, ClientException {
        AppUserDetails appUserDetailsNew = AppUserDetails.builder()
                .password(bCryptPasswordEncoder.encode(appUserDetails.getPassword()))
                .build();
        BeanUtils.copyProperties(appUserDetails, appUserDetailsNew, "password");
        HttpStatus httpStatus = appUserDetailsConnector.saveAppUserDetails(appUserDetailsNew);

        if (httpStatus.is2xxSuccessful()) {
            return getAppUserDetails(appUserDetails.getUsername(), appUserDetails.getPassword());
        } else {
            log.error("Save App User Details Error: [{}]", appUserDetails);
            throw new ServerException("Save App User Details Error");
        }
    }

    public String createToken(String username, String sourceIp) throws ServerException {
        try {
            AppUserDetailsRequest tokenClaim = AppUserDetailsRequest.builder()
                    .username(username)
                    .sourceIp(sourceIp)
                    .build();

            Map<String, Object> claims = objectMapper.convertValue(tokenClaim, new TypeReference<>() {});

            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS512, getSystemEnvProperty(APP_SECRET_KEY, null))
                    .setExpiration(new Date(System.currentTimeMillis() + 864000000))     // 24 hours, enforce inactive session in UI
                    .compact();
        } catch (Exception ex) {
            log.error("Create Token Exception: [{}] | [{}]", username, sourceIp, ex);
            throw new ServerException("Create Token Exception");
        }
    }
}
