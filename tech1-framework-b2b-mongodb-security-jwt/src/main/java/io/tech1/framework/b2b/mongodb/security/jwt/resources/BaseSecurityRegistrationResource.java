package io.tech1.framework.b2b.mongodb.security.jwt.resources;

import io.tech1.framework.b2b.mongodb.security.jwt.annotations.AbstractFrameworkBaseSecurityResource;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.requests.RequestUserRegistration1;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.events.EventRegistration1;
import io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.SecurityJwtIncidentPublisher;
import io.tech1.framework.b2b.mongodb.security.jwt.events.publishers.SecurityJwtPublisher;
import io.tech1.framework.b2b.mongodb.security.jwt.services.RegistrationService;
import io.tech1.framework.b2b.mongodb.security.jwt.validators.RegistrationRequestsValidator;
import io.tech1.framework.domain.exceptions.authentication.RegistrationException;
import io.tech1.framework.incidents.domain.registration.IncidentRegistration1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AbstractFrameworkBaseSecurityResource
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityRegistrationResource {

    // Services
    private final RegistrationService registrationService;
    // Publishers
    private final SecurityJwtPublisher securityJwtPublisher;
    private final SecurityJwtIncidentPublisher securityJwtIncidentPublisher;
    // Validators
    private final RegistrationRequestsValidator registrationRequestsValidator;

    @PostMapping("/register1")
    @ResponseStatus(HttpStatus.OK)
    public void register1(@RequestBody RequestUserRegistration1 requestUserRegistration1) throws RegistrationException {
        this.registrationRequestsValidator.validateRegistrationRequest1(requestUserRegistration1);
        this.registrationService.register1(requestUserRegistration1);
        this.securityJwtPublisher.publishRegistration1(EventRegistration1.of(requestUserRegistration1));
        this.securityJwtIncidentPublisher.publishRegistration1(IncidentRegistration1.of(requestUserRegistration1.getUsername()));
    }
}
