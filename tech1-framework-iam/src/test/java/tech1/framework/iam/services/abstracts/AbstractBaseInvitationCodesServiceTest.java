package tech1.framework.iam.services.abstracts;

import tech1.framework.iam.domain.dto.requests.RequestNewInvitationCodeParams;
import tech1.framework.iam.domain.dto.responses.ResponseInvitationCode;
import tech1.framework.iam.domain.identifiers.InvitationCodeId;
import tech1.framework.iam.repositories.InvitationCodesRepository;
import tech1.framework.foundation.domain.base.Username;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkPropertiesTestsHardcodedContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseInvitationCodesServiceTest {

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesTestsHardcodedContext.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ApplicationFrameworkProperties applicationFrameworkProperties;

        @Bean
        InvitationCodesRepository invitationCodesRepository() {
            return mock(InvitationCodesRepository.class);
        }

        @Bean
        AbstractBaseInvitationCodesService abstractBaseInvitationCodesService() {
            return new AbstractBaseInvitationCodesService(
                    this.invitationCodesRepository(),
                    this.applicationFrameworkProperties
            ) {};
        }
    }

    private final InvitationCodesRepository invitationCodesRepository;
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    private final AbstractBaseInvitationCodesService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationCodesRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationCodesRepository
        );
    }

    @Test
    void findByOwnerTest() {
        // Arrange
        var owner = Username.random();

        var invitationCode1 = ResponseInvitationCode.random(owner, Username.of("user2"));
        var invitationCode2 = ResponseInvitationCode.random(owner, Username.of("user1"));
        var invitationCode3 = ResponseInvitationCode.random(owner);
        var invitationCode4 = ResponseInvitationCode.random(owner);
        var invitationCode5 = ResponseInvitationCode.random(owner, Username.of("user5"));
        var invitationCode6 = ResponseInvitationCode.random(owner);

        var invitationCodes = asList(invitationCode1, invitationCode2, invitationCode3, invitationCode4, invitationCode5, invitationCode6);
        when(this.invitationCodesRepository.findResponseCodesByOwner(owner)).thenReturn(invitationCodes);

        // Act
        var responseInvitationCodes = this.componentUnderTest.findByOwner(owner);

        // Assert
        verify(this.invitationCodesRepository).findResponseCodesByOwner(owner);
        assertThat(responseInvitationCodes.invitationCodes().stream()
                        .limit(3)
                        .map(ResponseInvitationCode::value)
                        .collect(Collectors.toSet())
        ).containsExactlyInAnyOrder(
                invitationCode3.value(),
                invitationCode4.value(),
                invitationCode6.value()
        );
        assertThat(responseInvitationCodes.invitationCodes().stream()
                .skip(3)
                .map(ResponseInvitationCode::value)
                .collect(Collectors.toSet())
        ).containsExactlyInAnyOrder(
                invitationCode1.value(),
                invitationCode2.value(),
                invitationCode5.value()
        );
        assertThat(responseInvitationCodes.authorities()).isEqualTo(this.applicationFrameworkProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities());
    }

    @Test
    void saveTest() {
        // Arrange
        var username = Username.random();
        var request = RequestNewInvitationCodeParams.random();

        // Act
        this.componentUnderTest.save(username, request);

        // Assert
        verify(this.invitationCodesRepository).saveAs(username, request);
    }

    @Test
    void deleteByIdTest() {
        // Arrange
        var invitationCodeId = InvitationCodeId.random();

        // Act
        this.componentUnderTest.deleteById(invitationCodeId);

        // Assert
        verify(this.invitationCodesRepository).delete(invitationCodeId);
    }
}
