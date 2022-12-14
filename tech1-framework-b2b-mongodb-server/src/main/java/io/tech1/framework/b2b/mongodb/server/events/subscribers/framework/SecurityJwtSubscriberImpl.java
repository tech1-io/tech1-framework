package io.tech1.framework.b2b.mongodb.server.events.subscribers.framework;

import io.tech1.framework.b2b.mongodb.security.jwt.domain.events.EventAuthenticationLogin;
import io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.SecurityJwtIncidentPublisher;
import io.tech1.framework.b2b.mongodb.security.jwt.events.subscribers.base.BaseSecurityJwtSubscriber;
import io.tech1.framework.b2b.mongodb.security.jwt.services.UserEmailService;
import io.tech1.framework.b2b.mongodb.security.jwt.services.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityJwtSubscriberImpl extends BaseSecurityJwtSubscriber {

    @Autowired
    public SecurityJwtSubscriberImpl(
            SecurityJwtIncidentPublisher securityJwtIncidentPublisher,
            UserEmailService userEmailService,
            UserSessionService userSessionService
    ) {
        super(
                securityJwtIncidentPublisher,
                userEmailService,
                userSessionService
        );
    }

    @Override
    public void onAuthenticationLogin(EventAuthenticationLogin event) {
        super.onAuthenticationLogin(event);
        LOGGER.warn("[Server] SecurityJwtSubscriber.onAuthenticationLogin(). Username: `{}`", event.getUsername());
    }
}
