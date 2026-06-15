package grupoC.mascotas.security;

import org.springframework.stereotype.Component;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignAuthRequestInterceptor implements RequestInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public FeignAuthRequestInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void apply(RequestTemplate template) {
        try {
            String token = jwtTokenProvider.generateServiceToken("motor-service");
            // small debug prefix to confirm interceptor ran
            System.out.println("DEBUG: FeignAuth attaching token prefix: " + (token != null && token.length() > 8 ? token.substring(0, 8) : token));
            template.header("Authorization", "Bearer " + token);
        } catch (Exception ex) {
            // no-op: if token generation fails, let the call proceed without header and let downstream handle auth error
        }
    }

}
