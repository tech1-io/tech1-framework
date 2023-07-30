package io.tech1.framework.b2b.base.security.jwt.services.abstracts;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestNewInvitationCodeParams;
import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.InvitationCodeId;
import io.tech1.framework.b2b.base.security.jwt.repositories.AnyDbInvitationCodesRepository;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import io.tech1.framework.properties.tests.contexts.ApplicationFrameworkPropertiesContext;
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

import static io.tech1.framework.b2b.base.security.jwt.tests.random.BaseSecurityJwtDbRandomUtility.getInvitationCode;
import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.domain.utilities.random.RandomUtility.randomUsername;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseInvitationCodesServiceTest {

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesContext.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ApplicationFrameworkProperties applicationFrameworkProperties;

        @Bean
        AnyDbInvitationCodesRepository anyDbInvitationCodesRepository() {
            return mock(AnyDbInvitationCodesRepository.class);
        }

        @Bean
        AbstractBaseInvitationCodesService abstractBaseInvitationCodesService() {
            return new AbstractBaseInvitationCodesService(
                    this.anyDbInvitationCodesRepository(),
                    this.applicationFrameworkProperties
            ) {
                @Override
                public void save(RequestNewInvitationCodeParams requestNewInvitationCodeParams, Username owner) {
                    // no actions
                }
            };
        }
    }

    private final AnyDbInvitationCodesRepository anyDbInvitationCodesRepository;
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    private final AbstractBaseInvitationCodesService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.anyDbInvitationCodesRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.anyDbInvitationCodesRepository
        );
    }

    @Test
    void findByOwnerTest() {
        // Arrange
        var owner = randomUsername();

        var invitationCode1 = getInvitationCode(owner, Username.of("user2"));
        var invitationCode2 = getInvitationCode(owner, Username.of("user1"));
        var invitationCode3 = getInvitationCode(owner);
        var invitationCode4 = getInvitationCode(owner);
        var invitationCode5 = getInvitationCode(owner, Username.of("user5"));
        var invitationCode6 = getInvitationCode(owner);

        var invitationCodes = asList(invitationCode1, invitationCode2, invitationCode3, invitationCode4, invitationCode5, invitationCode6);
        when(this.anyDbInvitationCodesRepository.findResponseCodesByOwner(owner)).thenReturn(invitationCodes);

        // Act
        var responseInvitationCodes = this.componentUnderTest.findByOwner(owner);

        // Assert
        verify(this.anyDbInvitationCodesRepository).findResponseCodesByOwner(owner);
        assertThat(responseInvitationCodes.invitationCodes().get(0)).isEqualTo(invitationCode3);
        assertThat(responseInvitationCodes.invitationCodes().get(1)).isEqualTo(invitationCode4);
        assertThat(responseInvitationCodes.invitationCodes().get(2)).isEqualTo(invitationCode6);
        assertThat(responseInvitationCodes.invitationCodes().get(3)).isEqualTo(invitationCode2);
        assertThat(responseInvitationCodes.invitationCodes().get(4)).isEqualTo(invitationCode1);
        assertThat(responseInvitationCodes.invitationCodes().get(5)).isEqualTo(invitationCode5);
        assertThat(responseInvitationCodes.authorities()).isEqualTo(this.applicationFrameworkProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities());
    }

    @Test
    void deleteByIdTest() {
        // Arrange
        var invitationCodeId = entity(InvitationCodeId.class);

        // Act
        this.componentUnderTest.deleteById(invitationCodeId);

        // Assert
        verify(this.anyDbInvitationCodesRepository).delete(invitationCodeId);
    }
}
