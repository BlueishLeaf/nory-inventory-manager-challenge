package ai.nory.api.identity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(identityInterceptor());
    }

    @Bean
    public IdentityInterceptor identityInterceptor() {
        return new IdentityInterceptor(identityHeaders());
    }

    // The identity headers are scoped at the request level as each request can have different identity headers
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public IdentityHeaders identityHeaders() {
        return new IdentityHeaders();
    }
}
