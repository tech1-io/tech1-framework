package io.tech1.framework.iam.server.configurations;

import io.tech1.framework.foundation.domain.base.PropertyId;
import io.tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import io.tech1.framework.iam.configurations.AbstractApplicationSecurityJwtConfigurer;
import io.tech1.framework.iam.configurations.ApplicationBaseSecurityJwtWebsockets;
import io.tech1.framework.iam.server.base.properties.ApplicationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@ComponentScan({
        // -------------------------------------------------------------------------------------------------------------
        "io.tech1.framework.iam.filters.jwt_extension",
        "io.tech1.framework.iam.assistants.current.base"
        // -------------------------------------------------------------------------------------------------------------
})
@Import({
        ApplicationBaseSecurityJwtWebsockets.class
})
@EnableConfigurationProperties({
        ApplicationProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationIamServer implements AbstractApplicationSecurityJwtConfigurer {

    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;
    private final ApplicationProperties applicationProperties;

    @PostConstruct
    public void init() {
        this.applicationProperties.getServerConfigs().assertProperties(new PropertyId("serverConfigs"));
    }

    @Override
    public void configure(WebSecurity web) {
        var endpoint = this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().getStompConfigs().getEndpoint();
        web.ignoring().requestMatchers(endpoint + "/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/**"))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(GET, "/system/csrf").authenticated()
                                .requestMatchers("/hardware/**").permitAll()
                );
    }

}
