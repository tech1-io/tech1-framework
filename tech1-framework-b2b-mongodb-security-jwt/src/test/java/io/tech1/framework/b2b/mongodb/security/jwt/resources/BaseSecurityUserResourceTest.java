package io.tech1.framework.b2b.mongodb.security.jwt.resources;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserChangePassword1;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserUpdate1;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserUpdate2;
import io.tech1.framework.b2b.mongodb.security.jwt.assistants.core.CurrentSessionAssistant;
import io.tech1.framework.b2b.mongodb.security.jwt.services.BaseUserService;
import io.tech1.framework.b2b.mongodb.security.jwt.tests.runnerts.AbstractResourcesRunner;
import io.tech1.framework.b2b.mongodb.security.jwt.validators.BaseUserValidator;
import io.tech1.framework.domain.base.Username;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseSecurityUserResourceTest extends AbstractResourcesRunner {

    // Services
    private final BaseUserService baseUserService;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Validators
    private final BaseUserValidator baseUserValidator;

    // Resource
    private final BaseSecurityUserResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.baseUserService,
                this.currentSessionAssistant,
                this.baseUserValidator
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.baseUserService,
                this.currentSessionAssistant,
                this.baseUserValidator
        );
    }

    @Test
    void update1Test() throws Exception {
        // Arrange
        var requestUserUpdate1 = entity(RequestUserUpdate1.class);
        var username = entity(Username.class);
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(username);

        // Act
        this.mvc.perform(
                post("/user/update1")
                        .content(this.objectMapper.writeValueAsString(requestUserUpdate1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.baseUserValidator).validateUserUpdateRequest1(username, requestUserUpdate1);
        verify(this.baseUserService).updateUser1(requestUserUpdate1);
    }

    @Test
    void update2Test() throws Exception {
        // Arrange
        var requestUserUpdate2 = entity(RequestUserUpdate2.class);

        // Act
        this.mvc.perform(
                        post("/user/update2")
                                .content(this.objectMapper.writeValueAsString(requestUserUpdate2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.baseUserValidator).validateUserUpdateRequest2(requestUserUpdate2);
        verify(this.baseUserService).updateUser2(requestUserUpdate2);
    }

    @Test
    void changePassword1Test() throws Exception {
        // Arrange
        var requestUserChangePassword1 = entity(RequestUserChangePassword1.class);

        // Act
        this.mvc.perform(
                post("/user/changePassword1")
                        .content(this.objectMapper.writeValueAsString(requestUserChangePassword1))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.baseUserValidator).validateUserChangePasswordRequest1(requestUserChangePassword1);
        verify(this.baseUserService).changePassword1(requestUserChangePassword1);
    }
}
