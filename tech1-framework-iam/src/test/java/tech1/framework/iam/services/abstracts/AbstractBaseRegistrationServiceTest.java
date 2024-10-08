package tech1.framework.iam.services.abstracts;

import tech1.framework.iam.domain.db.InvitationCode;
import tech1.framework.iam.domain.dto.requests.RequestUserRegistration1;
import tech1.framework.iam.repositories.InvitationCodesRepository;
import tech1.framework.iam.repositories.UsersRepository;
import tech1.framework.iam.services.BaseRegistrationService;
import tech1.framework.foundation.domain.base.Password;
import tech1.framework.foundation.domain.base.Username;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static tech1.framework.foundation.utilities.random.EntityUtility.entity;
import static tech1.framework.foundation.utilities.random.RandomUtility.randomString;
import static tech1.framework.foundation.utilities.random.RandomUtility.randomZoneId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseRegistrationServiceTest {
    @Configuration
    static class ContextConfiguration {
        @Bean
        InvitationCodesRepository invitationCodeRepository() {
            return mock(InvitationCodesRepository.class);
        }

        @Bean
        UsersRepository userRepository() {
            return mock(UsersRepository.class);
        }

        @Bean
        BCryptPasswordEncoder bCryptPasswordEncoder() {
            return mock(BCryptPasswordEncoder.class);
        }

        @Bean
        AbstractBaseRegistrationService registrationService() {
            return new AbstractBaseRegistrationService(
                    this.invitationCodeRepository(),
                    this.userRepository(),
                    this.bCryptPasswordEncoder()
            ) {};
        }
    }

    private final InvitationCodesRepository invitationCodesRepository;
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final BaseRegistrationService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationCodesRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationCodesRepository,
                this.usersRepository,
                this.bCryptPasswordEncoder
        );
    }

    @Test
    void register1Test() {
        // Arrange
        var requestUserRegistration1 = new RequestUserRegistration1(
                Username.random(),
                Password.random(),
                Password.random(),
                randomZoneId(),
                randomString()
        );
        var invitationCode = entity(InvitationCode.class);
        when(this.invitationCodesRepository.findByValueAsAny(requestUserRegistration1.invitationCode())).thenReturn(invitationCode);
        var hashPassword = randomString();
        when(this.bCryptPasswordEncoder.encode(requestUserRegistration1.password().value())).thenReturn(hashPassword);
        var invitationCodeAC1 = ArgumentCaptor.forClass(InvitationCode.class);
        var invitationCodeAC2 = ArgumentCaptor.forClass(InvitationCode.class);

        // Act
        this.componentUnderTest.register1(requestUserRegistration1);

        // Assert
        verify(this.invitationCodesRepository).findByValueAsAny(requestUserRegistration1.invitationCode());
        verify(this.bCryptPasswordEncoder).encode(requestUserRegistration1.password().value());
        verify(this.usersRepository).saveAs(eq(requestUserRegistration1), eq(Password.of(hashPassword)), invitationCodeAC1.capture());
        assertThat(invitationCodeAC1.getValue().invited()).isEqualTo(requestUserRegistration1.username());
        verify(this.invitationCodesRepository).saveAs(invitationCodeAC2.capture());
        assertThat(invitationCodeAC2.getValue().invited()).isEqualTo(requestUserRegistration1.username());
    }
}
