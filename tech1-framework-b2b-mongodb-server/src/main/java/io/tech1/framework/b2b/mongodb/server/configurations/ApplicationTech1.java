package io.tech1.framework.b2b.mongodb.server.configurations;

import io.tech1.framework.b2b.mongodb.security.jwt.configurations.AbstractApplicationSecurityJwtConfigurer;
import io.tech1.framework.b2b.mongodb.security.jwt.configurations.ApplicationBaseSecurityJwt;
import io.tech1.framework.b2b.mongodb.server.properties.ApplicationProperties;
import io.tech1.framework.hardware.configurations.ApplicationHardwareMonitoring;
import io.tech1.framework.hardware.monitoring.store.HardwareMonitoringStore;
import io.tech1.framework.hardware.monitoring.subscribers.HardwareMonitoringSubscriber;
import io.tech1.framework.hardware.monitoring.subscribers.impl.BaseHardwareMonitoringSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import javax.annotation.PostConstruct;

import static io.tech1.framework.domain.properties.utilities.PropertiesAsserter.assertProperties;

@Configuration
@ComponentScan({
        // -------------------------------------------------------------------------------------------------------------
        "io.tech1.framework.b2b.mongodb.security.jwt.assistants.core",
        "io.tech1.framework.b2b.mongodb.security.jwt.essence"
        // -------------------------------------------------------------------------------------------------------------
})
@Import({
        ApplicationHardwareMonitoring.class,
        ApplicationBaseSecurityJwt.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationTech1 implements AbstractApplicationSecurityJwtConfigurer {

    // Store
    private final HardwareMonitoringStore hardwareMonitoringStore;
    // Properties
    private final ApplicationProperties applicationProperties;

    @PostConstruct
    public void init() {
        assertProperties(this.applicationProperties.getServerConfigs(), "serverConfigs");
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        // no tech1-server configurations yet
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        var urlRegistry = http.authorizeRequests();
        urlRegistry.antMatchers("/hardware/**").permitAll();
    }

    @Bean
    public HardwareMonitoringSubscriber hardwareMonitoringSubscriber() {
        return new BaseHardwareMonitoringSubscriber(
                this.hardwareMonitoringStore
        );
    }
}
