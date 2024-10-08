package tech1.framework.iam.configurations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import tech1.framework.foundation.domain.base.PropertyId;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import tech1.framework.iam.handshakes.CsrfInterceptorHandshake;
import tech1.framework.iam.handshakes.SecurityHandshakeHandler;

/**
 * <a href="https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html">Documentation #1</a>
 * <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket">Documentation #2</a>
 * <p>
 * Spring Boot 3 Migration Issues 24.04.2024:
 * <p>
 * <a href="https://github.com/spring-projects/spring-security/issues/13640">
 * EnableWebSocketSecurity is not 1:1 replacement for AbstractSecurityWebSocketMessageBrokerConfigurer
 * </a>
 * <p>
 * <a href="https://github.com/jhipster/generator-jhipster/issues/20404">
 * Migrate to Spring Security 6's @EnableWebSocketSecurity (it is not possible to disable CSRF currently)
 * </a>
 */
// idea - reconnect flow: https://stackoverflow.com/questions/53244720/spring-websocket-stomp-exception-handling
@Slf4j
@Configuration
@Import({
        ApplicationBaseSecurityJwt.class
})
@ComponentScan({
        "tech1.framework.iam.events.subscribers.websockets",
        "tech1.framework.iam.handshakes",
        "tech1.framework.iam.resources.websockets",
        "tech1.framework.iam.tasks.hardware",
        "tech1.framework.iam.template",
})
@EnableWebSocketMessageBroker
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationBaseSecurityJwtWebsockets extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    // Handshakes
    private final CsrfInterceptorHandshake csrfInterceptorHandshake;
    private final SecurityHandshakeHandler securityHandshakeHandler;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @PostConstruct
    public void init() {
        this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().assertProperties(new PropertyId("securityJwtWebsocketsConfigs"));
    }

    // =================================================================================================================
    // @Overrides
    // =================================================================================================================
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().getStompConfigs().getEndpoint())
                .setAllowedOrigins(this.applicationFrameworkProperties.getMvcConfigs().getCorsConfigs().getAllowedOrigins())
                .setHandshakeHandler(this.securityHandshakeHandler)
                .addInterceptors(this.csrfInterceptorHandshake)
                .withSockJS();
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry registry) {
        registry.anyMessage().authenticated();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        var broker = this.applicationFrameworkProperties.getSecurityJwtWebsocketsConfigs().getBrokerConfigs();
        registry.setApplicationDestinationPrefixes(broker.getApplicationDestinationPrefix());
        registry.enableSimpleBroker(broker.getSimpleDestination());
        registry.setUserDestinationPrefix(broker.getUserDestinationPrefix());
    }

    /**
     * Determines if a CSRF token is required for connecting. This protects against remote
     * sites from connecting to the application and being able to read/write data over the
     * connection. The default is false (the token is required).
     */
    @Override
    protected boolean sameOriginDisabled() {
        return false;
    }

}
