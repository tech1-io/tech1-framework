package io.tech1.framework.iam.template.impl;

import io.tech1.framework.foundation.domain.base.Username;
import io.tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import io.tech1.framework.foundation.incidents.events.publishers.IncidentPublisher;
import io.tech1.framework.iam.domain.events.WebsocketEvent;
import io.tech1.framework.iam.template.WssMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WssMessagingTemplateImpl implements WssMessagingTemplate {

    // WebSocket - Stomp
    private final SimpMessagingTemplate messagingTemplate;
    // Incidents
    private final IncidentPublisher incidentPublisher;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Override
    public void sendEventToUser(Username username, String destination, WebsocketEvent event) {
        this.sendObjectToUser(
                username,
                destination,
                event
        );
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void sendObjectToUser(Username username, String destination, Object data) {
        if (!this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().getTemplateConfigs().isEnabled()) {
            return;
        }
        var brokerConfigs = this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().getBrokerConfigs();
        try {
            this.messagingTemplate.convertAndSendToUser(
                    username.value(),
                    brokerConfigs.getSimpleDestination() + destination,
                    data
            );
        } catch (MessagingException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }
}
