package co.edu.icesi.student360.core.infrastructure.config;

import co.edu.icesi.student360.core.domain.policy.StaffAccessPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {

  @Bean
  public StaffAccessPolicy staffAccessPolicy() {
    return new StaffAccessPolicy();
  }
}
