package io.tech1.framework.b2b.mongodb.security.jwt.events.subscribers.impl;

import io.tech1.framework.b2b.mongodb.security.jwt.events.subscribers.SecurityJwtIncidentSubscriber;
import io.tech1.framework.b2b.mongodb.security.jwt.incidents.converters.SecurityJwtIncidentConverter;
import io.tech1.framework.incidents.converters.IncidentConverter;
import io.tech1.framework.incidents.domain.authetication.*;
import io.tech1.framework.incidents.domain.registration.IncidentRegistration1;
import io.tech1.framework.incidents.domain.registration.IncidentRegistration1Failure;
import io.tech1.framework.incidents.domain.session.IncidentSessionExpired;
import io.tech1.framework.incidents.domain.session.IncidentSessionRefreshed;
import io.tech1.framework.incidents.feigns.clients.IncidentClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.incidents.tests.random.IncidentsRandomUtility.randomIncident;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityJwtIncidentSubscriberImplTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        IncidentClient incidentClient() {
            return mock(IncidentClient.class);
        }

        @Bean
        SecurityJwtIncidentConverter securityJwtIncidentConverter() {
            return mock(SecurityJwtIncidentConverter.class);
        }

        @Bean
        IncidentConverter incidentConverter() {
            return mock(IncidentConverter.class);
        }

        @Bean
        SecurityJwtIncidentSubscriber securityJwtIncidentSubscriber() {
            return new SecurityJwtIncidentSubscriberImpl(
                    this.incidentClient(),
                    this.securityJwtIncidentConverter(),
                    this.incidentConverter()
            );
        }
    }

    // Clients
    private final IncidentClient incidentClient;
    // Converters
    private final SecurityJwtIncidentConverter securityJwtIncidentConverter;
    private final IncidentConverter incidentConverter;

    private final SecurityJwtIncidentSubscriber componentUnderTest;

    @BeforeEach
    public void beforeEach() {
        reset(
                this.incidentClient,
                this.securityJwtIncidentConverter,
                this.incidentConverter
        );
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(
                this.incidentClient,
                this.securityJwtIncidentConverter,
                this.incidentConverter
        );
    }


    @Test
    public void onEventAuthenticationLoginIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var authenticationLoginIncident = entity(IncidentAuthenticationLogin.class);
        when(this.securityJwtIncidentConverter.convert(eq(authenticationLoginIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(authenticationLoginIncident);

        // Assert
        verify(this.securityJwtIncidentConverter).convert(eq(authenticationLoginIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventAuthenticationLoginFailureUsernamePasswordIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var authenticationLoginFailureUsernamePasswordIncident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);
        when(this.incidentConverter.convert(eq(authenticationLoginFailureUsernamePasswordIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(authenticationLoginFailureUsernamePasswordIncident);

        // Assert
        verify(this.incidentConverter).convert(eq(authenticationLoginFailureUsernamePasswordIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventAuthenticationLoginFailureUsernameMaskedPasswordIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var authenticationLoginFailureUsernameMaskedPasswordIncident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);
        when(this.incidentConverter.convert(eq(authenticationLoginFailureUsernameMaskedPasswordIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(authenticationLoginFailureUsernameMaskedPasswordIncident);

        // Assert
        verify(this.incidentConverter).convert(eq(authenticationLoginFailureUsernameMaskedPasswordIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventAuthenticationLogoutMinIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var authenticationLogoutMinIncident = entity(IncidentAuthenticationLogoutMin.class);
        when(this.incidentConverter.convert(eq(authenticationLogoutMinIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(authenticationLogoutMinIncident);

        // Assert
        verify(this.incidentConverter).convert(eq(authenticationLogoutMinIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventAuthenticationLogoutFullIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var authenticationLogoutFullIncident = entity(IncidentAuthenticationLogoutFull.class);
        when(this.securityJwtIncidentConverter.convert(eq(authenticationLogoutFullIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(authenticationLogoutFullIncident);

        // Assert
        verify(this.securityJwtIncidentConverter).convert(eq(authenticationLogoutFullIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventSessionRefreshedIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var sessionRefreshedIncident = entity(IncidentSessionRefreshed.class);
        when(this.securityJwtIncidentConverter.convert(eq(sessionRefreshedIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(sessionRefreshedIncident);

        // Assert
        verify(this.securityJwtIncidentConverter).convert(eq(sessionRefreshedIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventSessionExpiredIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var sessionExpiredIncident = entity(IncidentSessionExpired.class);
        when(this.securityJwtIncidentConverter.convert(eq(sessionExpiredIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(sessionExpiredIncident);

        // Assert
        verify(this.securityJwtIncidentConverter).convert(eq(sessionExpiredIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventRegister1IncidentTest() {
        // Arrange
        var incident = randomIncident();
        var register1Incident = entity(IncidentRegistration1.class);
        when(this.securityJwtIncidentConverter.convert(eq(register1Incident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(register1Incident);

        // Assert
        verify(this.securityJwtIncidentConverter).convert(eq(register1Incident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }

    @Test
    public void onEventRegister1FailureIncidentTest() {
        // Arrange
        var incident = randomIncident();
        var register1FailureIncident = entity(IncidentRegistration1Failure.class);
        when(this.securityJwtIncidentConverter.convert(eq(register1FailureIncident))).thenReturn(incident);

        // Act
        this.componentUnderTest.onEvent(register1FailureIncident);

        // Assert
        verify(this.securityJwtIncidentConverter).convert(eq(register1FailureIncident));
        verify(this.incidentClient).registerIncident(eq(incident));
    }
}
