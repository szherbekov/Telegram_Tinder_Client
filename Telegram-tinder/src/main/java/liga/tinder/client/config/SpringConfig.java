package liga.tinder.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringConfig {

    @Bean
    RestTemplate creatRestTemplate() {
     return new RestTemplate();

    }

}
