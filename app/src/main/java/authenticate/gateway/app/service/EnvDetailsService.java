package authenticate.gateway.app.service;

import static org.springframework.util.StringUtils.hasText;

import authenticate.gateway.app.model.EnvDetails;
import authenticate.gateway.app.repository.EnvDetailsRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EnvDetailsService {

  private final EnvDetailsRepository envDetailsRepository;

  public EnvDetailsService(EnvDetailsRepository envDetailsRepository) {
    this.envDetailsRepository = envDetailsRepository;
  }

  public List<EnvDetails> getEnvDetails(String name) {
    List<EnvDetails> envDetailsList = envDetailsRepository.findAll();

    if (!hasText(name)) {
      return envDetailsList;
    }

    return envDetailsList.stream()
        .filter(envDetails -> name.equals(envDetails.getName()))
        .findFirst()
        .stream()
        .toList();
  }
}
