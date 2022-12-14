package io.tech1.framework.b2b.mongodb.security.jwt.tests.contexts;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.SecurityJwtIncidentPublisher;
import io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.SecurityJwtPublisher;
import io.tech1.framework.b2b.mongodb.security.jwt.utilities.HttpRequestUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        "io.tech1.framework.b2b.mongodb.security.jwt.handlers"
})
public class TestsApplicationHandlersContext {

    @Bean
    public SecurityJwtPublisher securityJwtPublisher() {
        return mock(SecurityJwtPublisher.class);
    }

    @Bean
    public SecurityJwtIncidentPublisher securityJwtIncidentPublisher() {
        return mock(SecurityJwtIncidentPublisher.class);
    }

    @Bean
    public HttpRequestUtility httpRequestUtility() {
        return mock(HttpRequestUtility.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
