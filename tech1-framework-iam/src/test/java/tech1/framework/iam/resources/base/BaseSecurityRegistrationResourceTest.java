package tech1.framework.iam.resources.base;

import tech1.framework.iam.tests.runners.AbstractResourcesRunner1;
import tech1.framework.iam.domain.dto.requests.RequestUserRegistration1;
import tech1.framework.iam.domain.events.EventRegistration1;
import tech1.framework.iam.events.publishers.SecurityJwtIncidentPublisher;
import tech1.framework.iam.events.publishers.SecurityJwtPublisher;
import tech1.framework.iam.services.BaseRegistrationService;
import tech1.framework.iam.validators.BaseRegistrationRequestsValidator;
import tech1.framework.foundation.incidents.domain.registration.IncidentRegistration1;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseSecurityRegistrationResourceTest extends AbstractResourcesRunner1 {

    // Services
    private final BaseRegistrationService baseRegistrationService;
    // Publishers
    private final SecurityJwtPublisher securityJwtPublisher;
    private final SecurityJwtIncidentPublisher securityJwtIncidentPublisher;
    // Validators
    private final BaseRegistrationRequestsValidator baseRegistrationRequestsValidator;

    // Resource
    private final BaseSecurityRegistrationResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.baseRegistrationService,
                this.securityJwtPublisher,
                this.securityJwtIncidentPublisher,
                this.baseRegistrationRequestsValidator
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.baseRegistrationService,
                this.securityJwtPublisher,
                this.securityJwtIncidentPublisher,
                this.baseRegistrationRequestsValidator
        );
    }

    @Test
    void register1() throws Exception {
        // Arrange
        var requestUserRegistration1 = RequestUserRegistration1.testsHardcoded();

        // Act
        this.mvc.perform(
                post("/registration/register1")
                        .content(this.objectMapper.writeValueAsString(requestUserRegistration1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.baseRegistrationRequestsValidator).validateRegistrationRequest1(requestUserRegistration1);
        verify(this.baseRegistrationService).register1(requestUserRegistration1);
        verify(this.securityJwtPublisher).publishRegistration1(new EventRegistration1(requestUserRegistration1));
        verify(this.securityJwtIncidentPublisher).publishRegistration1(new IncidentRegistration1(requestUserRegistration1.username()));
    }
}
