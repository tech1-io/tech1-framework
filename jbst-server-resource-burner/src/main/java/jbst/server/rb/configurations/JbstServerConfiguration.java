package jbst.server.rb.configurations;

import jbst.foundation.configurations.JbstConfigurationAsync;
import jbst.foundation.configurations.JbstConfigurationEvents;
import jbst.foundation.configurations.JbstConfigurationSpringBootServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@Import({
        JbstConfigurationAsync.class,
        JbstConfigurationEvents.class,
        JbstConfigurationSpringBootServer.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstServerConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/resource-burner/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
        ).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
