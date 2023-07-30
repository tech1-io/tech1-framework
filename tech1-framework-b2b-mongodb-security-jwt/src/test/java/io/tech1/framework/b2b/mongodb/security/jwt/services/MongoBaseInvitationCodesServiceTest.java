package io.tech1.framework.b2b.mongodb.security.jwt.services;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestNewInvitationCodeParams;
import io.tech1.framework.b2b.base.security.jwt.services.BaseInvitationCodesService;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.MongoDbInvitationCode;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.db.MongoDbUser;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.MongoInvitationCodesRepository;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import io.tech1.framework.properties.tests.contexts.ApplicationFrameworkPropertiesContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashSet;
import java.util.stream.Collectors;

import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.domain.utilities.random.RandomUtility.randomStringsAsList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MongoBaseInvitationCodesServiceTest {

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesContext.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ApplicationFrameworkProperties applicationFrameworkProperties;

        @Bean
        MongoInvitationCodesRepository invitationCodeRepository() {
            return mock(MongoInvitationCodesRepository.class);
        }

        @Bean
        BaseInvitationCodesService invitationCodeService() {
            return new MongoBaseInvitationCodesService(
                    this.invitationCodeRepository(),
                    this.applicationFrameworkProperties
            );
        }
    }

    private final MongoInvitationCodesRepository invitationCodesRepository;

    private final BaseInvitationCodesService componentUnderTest;

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
    void saveTest() {
        // Arrange
        var dbUser = entity(MongoDbUser.class);
        var requestNewInvitationCodeParams = new RequestNewInvitationCodeParams(new HashSet<>(randomStringsAsList(3)));
        var dbInvitationCodeAC = ArgumentCaptor.forClass(MongoDbInvitationCode.class);

        // Act
        this.componentUnderTest.save(requestNewInvitationCodeParams, dbUser.getUsername());

        // Assert
        verify(this.invitationCodesRepository).save(dbInvitationCodeAC.capture());
        assertThat(dbInvitationCodeAC.getValue().getAuthorities()).isEqualTo(requestNewInvitationCodeParams.authorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        assertThat(dbInvitationCodeAC.getValue().getOwner()).isEqualTo(dbUser.getUsername());
        assertThat(dbInvitationCodeAC.getValue().getValue()).hasSize(40);
    }
}
