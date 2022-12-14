package io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.impl;

import io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.SecurityJwtIncidentPublisher;
import io.tech1.framework.domain.properties.base.SecurityJwtIncidentType;
import io.tech1.framework.domain.properties.configs.SecurityJwtConfigs;
import io.tech1.framework.domain.properties.configs.security.jwt.IncidentsConfigs;
import io.tech1.framework.incidents.domain.authetication.*;
import io.tech1.framework.incidents.domain.registration.IncidentRegistration1;
import io.tech1.framework.incidents.domain.registration.IncidentRegistration1Failure;
import io.tech1.framework.incidents.domain.session.IncidentSessionExpired;
import io.tech1.framework.incidents.domain.session.IncidentSessionRefreshed;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.tech1.framework.domain.properties.base.SecurityJwtIncidentType.*;
import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityJwtIncidentPublisherImplTest {

    @Configuration
    static class ContextConfiguration {
        @Primary
        @Bean
        ApplicationEventPublisher applicationEventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }

        @Bean
        ApplicationFrameworkProperties applicationFrameworkProperties() {
            return mock(ApplicationFrameworkProperties.class);
        }

        @Bean
        SecurityJwtIncidentPublisher securityJwtIncidentPublisher() {
            return new SecurityJwtIncidentPublisherImpl(
                    this.applicationEventPublisher(),
                    this.applicationFrameworkProperties()
            );
        }
    }

    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    private final SecurityJwtIncidentPublisher componentUnderTest;

    @BeforeEach
    public void beforeEach() {
        reset(
                this.applicationEventPublisher,
                this.applicationFrameworkProperties
        );
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(
                this.applicationEventPublisher,
                this.applicationFrameworkProperties
        );
    }

    @Test
    public void publishAuthenticationLoginDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogin(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishAuthenticationLoginEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogin(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishAuthenticationLoginFailureUsernamePasswordDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernamePassword(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishAuthenticationLoginFailureUsernamePasswordEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernamePassword(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishAuthenticationLoginFailureUsernameMaskedPasswordDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernameMaskedPassword(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishAuthenticationLoginFailureUsernameMaskedPasswordEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernameMaskedPassword(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishAuthenticationLogoutMinDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT_MIN, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutMin(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishAuthenticationLogoutMinEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT_MIN, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutMin(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishAuthenticationLogoutFullDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutFull(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishAuthenticationLogoutFullEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutFull(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishSessionRefreshedDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_REFRESHED, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.publishSessionRefreshed(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishSessionRefreshedEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_REFRESHED, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.publishSessionRefreshed(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishSessionExpiredDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_EXPIRED, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionExpired.class);

        // Act
        this.componentUnderTest.publishSessionExpired(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishSessionExpiredEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_EXPIRED, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionExpired.class);

        // Act
        this.componentUnderTest.publishSessionExpired(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishRegistration1DisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1.class);

        // Act
        this.componentUnderTest.publishRegistration1(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishRegistration1EnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1.class);

        // Act
        this.componentUnderTest.publishRegistration1(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    @Test
    public void publishRegistration1FailureDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1_FAILURE, false);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.publishRegistration1Failure(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
    }

    @Test
    public void publishRegistration1FailureEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1_FAILURE, true);
        when(this.applicationFrameworkProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.publishRegistration1Failure(incident);

        // Assert
        verify(this.applicationFrameworkProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(eq(incident));
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static SecurityJwtConfigs randomSecurityJwtConfigs(SecurityJwtIncidentType type, boolean enabled) {
        var typesConfigs = Stream.of(SecurityJwtIncidentType.values())
                .collect(Collectors.toMap(
                        entry -> entry,
                        entry -> type.equals(entry) && enabled
                ));
        var securityJwtConfigs = new SecurityJwtConfigs();
        securityJwtConfigs.setIncidentsConfigs(
                IncidentsConfigs.of(
                        typesConfigs
                )
        );
        return securityJwtConfigs;
    }
}
