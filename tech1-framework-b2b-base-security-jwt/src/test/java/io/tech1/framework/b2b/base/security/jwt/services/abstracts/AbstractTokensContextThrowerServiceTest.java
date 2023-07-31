package io.tech1.framework.b2b.base.security.jwt.services.abstracts;

import io.tech1.framework.b2b.base.security.jwt.assistants.userdetails.JwtUserDetailsService;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtTokenValidatedClaims;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtUser;
import io.tech1.framework.b2b.base.security.jwt.repositories.AnyDbUsersSessionsRepository;
import io.tech1.framework.b2b.base.security.jwt.utils.SecurityJwtTokenUtils;
import io.tech1.framework.domain.exceptions.cookie.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtTokenValidatedClaims.valid;
import static io.tech1.framework.b2b.base.security.jwt.tests.random.BaseSecurityJwtRandomUtility.randomValidDefaultClaims;
import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractTokensContextThrowerServiceTest {

    private static Stream<Arguments> verifyAccessTokenExpirationOrThrow() {
        return Stream.of(
                Arguments.of(valid(entity(JwtAccessToken.class), randomValidDefaultClaims()), false, false),
                Arguments.of(valid(entity(JwtRefreshToken.class), randomValidDefaultClaims()), false, false),
                Arguments.of(valid(entity(JwtAccessToken.class), randomValidDefaultClaims()), true, true),
                Arguments.of(valid(entity(JwtRefreshToken.class), randomValidDefaultClaims()), true, false)
        );
    }

    private static Stream<Arguments> verifyRefreshTokenExpirationOrThrowTest() {
        return Stream.of(
                Arguments.of(valid(entity(JwtAccessToken.class), randomValidDefaultClaims()), false, false),
                Arguments.of(valid(entity(JwtRefreshToken.class), randomValidDefaultClaims()), false, false),
                Arguments.of(valid(entity(JwtAccessToken.class), randomValidDefaultClaims()), true, false),
                Arguments.of(valid(entity(JwtRefreshToken.class), randomValidDefaultClaims()), true, true)
        );
    }

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        JwtUserDetailsService jwtUserDetailsService() {
            return mock(JwtUserDetailsService.class);
        }

        @Bean
        AnyDbUsersSessionsRepository usersSessionsRepository() {
            return mock(AnyDbUsersSessionsRepository.class);
        }

        @Bean
        SecurityJwtTokenUtils securityJwtTokenUtils() {
            return mock(SecurityJwtTokenUtils.class);
        }

        @Bean
        AbstractTokensContextThrowerService abstractTokensContextThrowerService() {
            return new AbstractTokensContextThrowerService(
                    this.jwtUserDetailsService(),
                    this.usersSessionsRepository(),
                    this.securityJwtTokenUtils()
            ) {};
        }
    }

    // Assistants
    private final JwtUserDetailsService jwtUserDetailsService;
    // Repositories
    private final AnyDbUsersSessionsRepository usersSessionsRepository;
    // Utilities
    private final SecurityJwtTokenUtils securityJwtTokenUtils;

    private final AbstractTokensContextThrowerService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.securityJwtTokenUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.securityJwtTokenUtils
        );
    }

    @Test
    void verifyValidityAccessTokenTest() throws CookieAccessTokenInvalidException {
        // Arrange
        var jwtAccessToken = entity(JwtAccessToken.class);
        when(this.securityJwtTokenUtils.validate(jwtAccessToken)).thenReturn(valid(jwtAccessToken, randomValidDefaultClaims()));

        // Act
        this.componentUnderTest.verifyValidityOrThrow(jwtAccessToken);

        // Assert
        verify(this.securityJwtTokenUtils).validate(jwtAccessToken);
    }

    @Test
    void verifyValidityAccessTokenThrowTest() {
        // Arrange
        var jwtAccessToken = entity(JwtAccessToken.class);
        when(this.securityJwtTokenUtils.validate(jwtAccessToken)).thenReturn(JwtTokenValidatedClaims.invalid(jwtAccessToken));

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyValidityOrThrow(jwtAccessToken));

        // Assert
        verify(this.securityJwtTokenUtils).validate(jwtAccessToken);
        assertThat(throwable)
                .isInstanceOf(CookieAccessTokenInvalidException.class)
                .hasMessageContaining("JWT access token is invalid");
    }

    @Test
    void verifyValidityRefreshTokenTest() throws CookieRefreshTokenInvalidException {
        // Arrange
        var jwtRefreshToken = entity(JwtRefreshToken.class);
        when(this.securityJwtTokenUtils.validate(jwtRefreshToken)).thenReturn(valid(jwtRefreshToken, randomValidDefaultClaims()));

        // Act
        this.componentUnderTest.verifyValidityOrThrow(jwtRefreshToken);

        // Assert
        verify(this.securityJwtTokenUtils).validate(jwtRefreshToken);
    }

    @Test
    void verifyValidityRefreshTokenThrowTest() {
        // Arrange
        var jwtRefreshToken = entity(JwtRefreshToken.class);
        when(this.securityJwtTokenUtils.validate(jwtRefreshToken)).thenReturn(JwtTokenValidatedClaims.invalid(jwtRefreshToken));

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyValidityOrThrow(jwtRefreshToken));

        // Assert
        verify(this.securityJwtTokenUtils).validate(jwtRefreshToken);
        assertThat(throwable)
                .isInstanceOf(CookieRefreshTokenInvalidException.class)
                .hasMessageContaining("JWT refresh token is invalid");
    }

    @ParameterizedTest
    @MethodSource("verifyAccessTokenExpirationOrThrow")
    void verifyAccessTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims, boolean expiredFlag, boolean throwableFlag) {
        // Arrange
        when(this.securityJwtTokenUtils.isExpired(validatedClaims)).thenReturn(expiredFlag);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyAccessTokenExpirationOrThrow(validatedClaims));

        // Assert
        if (throwableFlag) {
            assertThat(throwable)
                    .isInstanceOf(CookieAccessTokenExpiredException.class)
                    .hasMessageContaining("JWT access token is expired. Username: " + validatedClaims.safeGetUsername());
        } else {
            verify(this.securityJwtTokenUtils).isExpired(validatedClaims);
        }
        reset(
                this.securityJwtTokenUtils
        );
    }

    @ParameterizedTest
    @MethodSource("verifyRefreshTokenExpirationOrThrowTest")
    void verifyRefreshTokenExpirationOrThrowTest(JwtTokenValidatedClaims validatedClaims, boolean expiredFlag, boolean throwableFlag) {
        // Arrange
        when(this.securityJwtTokenUtils.isExpired(validatedClaims)).thenReturn(expiredFlag);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyRefreshTokenExpirationOrThrow(validatedClaims));

        // Assert
        if (throwableFlag) {
            assertThat(throwable)
                    .isInstanceOf(CookieRefreshTokenExpiredException.class)
                    .hasMessageContaining("JWT refresh token is expired. Username: " + validatedClaims.safeGetUsername());
        } else {
            verify(this.securityJwtTokenUtils).isExpired(validatedClaims);
        }
        reset(
                this.securityJwtTokenUtils
        );
    }

    @Test
    void verifyDbPresenceTest() throws CookieRefreshTokenDbNotFoundException {
        // Arrange
        var oldJwtRefreshToken = entity(JwtRefreshToken.class);
        var validatedClaims = valid(oldJwtRefreshToken, randomValidDefaultClaims());
        var jwtUser = entity(JwtUser.class);
        when(this.jwtUserDetailsService.loadUserByUsername(validatedClaims.safeGetUsername().identifier())).thenReturn(jwtUser);
        when(this.usersSessionsRepository.isPresent(oldJwtRefreshToken)).thenReturn(true);

        // Act
        var dbUser = this.componentUnderTest.verifyDbPresenceOrThrow(validatedClaims, oldJwtRefreshToken);

        // Assert
        verify(this.jwtUserDetailsService).loadUserByUsername(validatedClaims.safeGetUsername().identifier());
        verify(this.usersSessionsRepository).isPresent(oldJwtRefreshToken);
        assertThat(dbUser).isEqualTo(jwtUser);
    }

    @Test
    void verifyDbPresenceThrowTest() {
        // Arrange
        var oldJwtRefreshToken = entity(JwtRefreshToken.class);
        var validatedClaims = valid(oldJwtRefreshToken, randomValidDefaultClaims());
        var jwtUser = entity(JwtUser.class);
        when(this.jwtUserDetailsService.loadUserByUsername(validatedClaims.safeGetUsername().identifier())).thenReturn(jwtUser);
        when(this.usersSessionsRepository.isPresent(oldJwtRefreshToken)).thenReturn(false);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyDbPresenceOrThrow(validatedClaims, oldJwtRefreshToken));

        // Assert
        verify(this.jwtUserDetailsService).loadUserByUsername(validatedClaims.safeGetUsername().identifier());
        verify(this.usersSessionsRepository).isPresent(oldJwtRefreshToken);
        assertThat(throwable)
                .isInstanceOf(CookieRefreshTokenDbNotFoundException.class)
                .hasMessageContaining("JWT refresh token is not present in database. Username: " + validatedClaims.safeGetUsername());
    }
}