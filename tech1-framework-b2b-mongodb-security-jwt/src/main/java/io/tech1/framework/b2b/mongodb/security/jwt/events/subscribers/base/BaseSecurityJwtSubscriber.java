package io.tech1.framework.b2b.mongodb.security.jwt.events.subscribers.base;

import io.tech1.framework.b2b.mongodb.security.jwt.domain.events.*;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.functions.FunctionAuthenticationLoginEmail;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.functions.FunctionSessionRefreshedEmail;
import io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.SecurityJwtIncidentPublisher;
import io.tech1.framework.b2b.mongodb.security.jwt.events.subscribers.SecurityJwtSubscriber;
import io.tech1.framework.b2b.mongodb.security.jwt.services.UserEmailService;
import io.tech1.framework.b2b.mongodb.security.jwt.services.UserSessionService;
import io.tech1.framework.domain.pubsub.AbstractEventSubscriber;
import io.tech1.framework.incidents.domain.authetication.IncidentAuthenticationLogin;
import io.tech1.framework.incidents.domain.session.IncidentSessionRefreshed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static io.tech1.framework.domain.constants.FrameworkLogsConstants.*;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityJwtSubscriber extends AbstractEventSubscriber implements SecurityJwtSubscriber {

    // Publishers
    private final SecurityJwtIncidentPublisher securityJwtIncidentPublisher;
    // Services
    private final UserEmailService userEmailService;
    private final UserSessionService userSessionService;

    @Override
    public void onAuthenticationLogin(EventAuthenticationLogin event) {
        LOGGER.debug(SECURITY_JWT_AUTHENTICATION_LOGIN, this.getType(), event.getUsername());
    }

    @Override
    public void onAuthenticationLoginFailure(EventAuthenticationLoginFailure event) {
        LOGGER.debug(SECURITY_JWT_AUTHENTICATION_LOGIN_FAILURE, this.getType(), event.getUsername());
    }

    @Override
    public void onAuthenticationLogout(EventAuthenticationLogout event) {
        LOGGER.debug(SECURITY_JWT_AUTHENTICATION_LOGOUT, this.getType(), event.getSession().getUsername());
    }

    @Override
    public void onRegistration1(EventRegistration1 event) {
        LOGGER.debug(SECURITY_JWT_REGISTER1, this.getType(), event.getRequestUserRegistration1().getUsername());
    }

    @Override
    public void onRegistration1Failure(EventRegistration1Failure event) {
        LOGGER.debug(SECURITY_JWT_REGISTER1_FAILURE, this.getType(), event.getUsername());
    }

    @Override
    public void onSessionRefreshed(EventSessionRefreshed event) {
        LOGGER.debug(SECURITY_JWT_SESSION_REFRESHED, this.getType(), event.getSession().getUsername());
    }

    @Override
    public void onSessionExpired(EventSessionExpired event) {
        LOGGER.debug(SECURITY_JWT_SESSION_EXPIRED, this.getType(), event.getSession().getUsername());
    }

    @Override
    public void onSessionAddUserRequestMetadata(EventSessionAddUserRequestMetadata event) {
        LOGGER.debug(SECURITY_JWT_SESSION_ADD_USER_REQUEST_METADATA, this.getType(), event.getUsername());
        var userSession = this.userSessionService.saveUserRequestMetadata(event);
        if (event.isAuthenticationLoginEndpoint()) {
            this.userEmailService.executeAuthenticationLogin(
                    FunctionAuthenticationLoginEmail.of(
                            event.getUsername(),
                            event.getEmail(),
                            userSession.getRequestMetadata()
                    )
            );
            this.securityJwtIncidentPublisher.publishAuthenticationLogin(
                    IncidentAuthenticationLogin.of(
                            event.getUsername(),
                            userSession.getRequestMetadata()
                    )
            );
        }
        if (event.isAuthenticationRefreshTokenEndpoint()) {
            this.userEmailService.executeSessionRefreshed(
                    FunctionSessionRefreshedEmail.of(
                            event.getUsername(),
                            event.getEmail(),
                            userSession.getRequestMetadata()
                    )
            );
            this.securityJwtIncidentPublisher.publishSessionRefreshed(
                    IncidentSessionRefreshed.of(
                            event.getUsername(),
                            userSession.getRequestMetadata()
                    )
            );
        }
    }
}
