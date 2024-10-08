package tech1.framework.iam.tests.contexts;

import tech1.framework.iam.events.publishers.SecurityJwtIncidentPublisher;
import tech1.framework.iam.events.publishers.SecurityJwtPublisher;
import tech1.framework.iam.repositories.InvitationCodesRepository;
import tech1.framework.iam.repositories.UsersRepository;
import tech1.framework.iam.repositories.UsersSessionsRepository;
import tech1.framework.foundation.incidents.events.publishers.IncidentPublisher;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkPropertiesTestsHardcodedContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import({
        ApplicationFrameworkPropertiesTestsHardcodedContext.class
})
public class TestsApplicationValidatorsContext {

    // =================================================================================================================
    // Publishers
    // =================================================================================================================
    @Bean
    SecurityJwtPublisher securityJwtPublisher() {
        return mock(SecurityJwtPublisher.class);
    }

    @Bean
    SecurityJwtIncidentPublisher securityJwtIncidentPublisher() {
        return mock(SecurityJwtIncidentPublisher.class);
    }

    @Bean
    IncidentPublisher incidentPublisher() {
        return mock(IncidentPublisher.class);
    }

    // =================================================================================================================
    // Repositories
    // =================================================================================================================
    @Bean
    InvitationCodesRepository invitationCodeRepository() {
        return mock(InvitationCodesRepository.class);
    }

    @Bean
    UsersRepository userRepository() {
        return mock(UsersRepository.class);
    }

    @Bean
    UsersSessionsRepository userSessionRepository() {
        return mock(UsersSessionsRepository.class);
    }
}
