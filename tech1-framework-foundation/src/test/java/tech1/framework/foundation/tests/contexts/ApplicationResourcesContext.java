package tech1.framework.foundation.tests.contexts;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tech1.framework.foundation.services.hardware.publishers.HardwareMonitoringPublisher;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        // -------------------------------------------------------------------------------------------------------------
        "tech1.framework.foundation.services.hardware.resources"
        // -------------------------------------------------------------------------------------------------------------
})
@EnableWebMvc
public class ApplicationResourcesContext {
    @Bean
    HardwareMonitoringPublisher hardwareMonitoringPublisher() {
        return mock(HardwareMonitoringPublisher.class);
    }
}
