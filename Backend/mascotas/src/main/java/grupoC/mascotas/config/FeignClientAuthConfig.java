package grupoC.mascotas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;
import grupoC.mascotas.security.JwtTokenProvider;

@Configuration
public class FeignClientAuthConfig {

    @Bean
    public RequestInterceptor authRequestInterceptor(JwtTokenProvider jwtTokenProvider) {
        return template -> {
            try {
                String token = jwtTokenProvider.generateServiceToken("motor-service");
                template.header("Authorization", "Bearer " + token);
            } catch (Exception ex) {
                // ignore
            }
        };
    }

}
