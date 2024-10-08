package tech1.framework.iam.events.subscribers;

import tech1.framework.foundation.incidents.domain.authetication.*;
import tech1.framework.foundation.incidents.domain.registration.IncidentRegistration1;
import tech1.framework.foundation.incidents.domain.registration.IncidentRegistration1Failure;
import tech1.framework.foundation.incidents.domain.session.IncidentSessionExpired;
import tech1.framework.foundation.incidents.domain.session.IncidentSessionRefreshed;
import org.springframework.context.event.EventListener;

public interface SecurityJwtIncidentSubscriber {
    @EventListener
    void onEvent(IncidentAuthenticationLogin incident);
    @EventListener
    void onEvent(IncidentAuthenticationLoginFailureUsernamePassword incident);
    @EventListener
    void onEvent(IncidentAuthenticationLoginFailureUsernameMaskedPassword incident);
    @EventListener
    void onEvent(IncidentAuthenticationLogoutMin incident);
    @EventListener
    void onEvent(IncidentAuthenticationLogoutFull incident);
    @EventListener
    void onEvent(IncidentRegistration1 incident);
    @EventListener
    void onEvent(IncidentRegistration1Failure incident);
    @EventListener
    void onEvent(IncidentSessionRefreshed incident);
    @EventListener
    void onEvent(IncidentSessionExpired incident);
}
