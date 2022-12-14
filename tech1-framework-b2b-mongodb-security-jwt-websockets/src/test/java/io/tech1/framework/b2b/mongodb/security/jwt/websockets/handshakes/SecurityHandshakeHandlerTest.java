package io.tech1.framework.b2b.mongodb.security.jwt.websockets.handshakes;

import io.tech1.framework.b2b.mongodb.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.jwt.JwtUser;
import io.tech1.framework.b2b.mongodb.security.jwt.services.TokenService;
import io.tech1.framework.domain.exceptions.cookie.*;
import io.tech1.framework.domain.tuples.Tuple2;
import io.tech1.framework.properties.tests.contexts.ApplicationFrameworkPropertiesContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.domain.utilities.random.RandomUtility.randomUsername;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityHandshakeHandlerTest {

    private static Stream<Arguments> determineUserExceptionTest() {
        return Stream.of(
                Arguments.of(new CookieAccessTokenNotFoundException()),
                Arguments.of(new CookieRefreshTokenNotFoundException()),
                Arguments.of(new CookieAccessTokenInvalidException()),
                Arguments.of(new CookieRefreshTokenInvalidException()),
                Arguments.of(new CookieAccessTokenExpiredException(randomUsername()))
        );
    }

    @Configuration
    @Import({
            ApplicationFrameworkPropertiesContext.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        TokenService tokenService() {
            return mock(TokenService.class);
        }

        @Bean
        SecurityHandshakeHandler securityHandshakeHandler() {
            return new SecurityHandshakeHandler(
                    this.tokenService()
            );
        }
    }

    // Services
    private final TokenService tokenService;

    private final SecurityHandshakeHandler componentUnderTest;

    @BeforeEach
    public void beforeEach() {
        reset(
                this.tokenService
        );
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(
                this.tokenService
        );
    }

    @ParameterizedTest
    @MethodSource("determineUserExceptionTest")
    public void determineUserExceptionTest(Exception exception) throws CookieAccessTokenInvalidException, CookieRefreshTokenInvalidException, CookieAccessTokenNotFoundException, CookieAccessTokenExpiredException, CookieRefreshTokenNotFoundException {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var serverHttpRequest = mock(ServletServerHttpRequest.class);
        var wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();
        when(serverHttpRequest.getServletRequest()).thenReturn(request);
        when(this.tokenService.getJwtUserByAccessTokenOrThrow(eq(request))).thenThrow(exception);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.determineUser(serverHttpRequest, wsHandler, attributes));

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).startsWith("WebSocket user not determined");
        verify(this.tokenService).getJwtUserByAccessTokenOrThrow(eq(request));
    }

    @Test
    public void determineUserTest() throws CookieAccessTokenInvalidException, CookieRefreshTokenInvalidException, CookieAccessTokenNotFoundException, CookieAccessTokenExpiredException, CookieRefreshTokenNotFoundException {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var serverHttpRequest = mock(ServletServerHttpRequest.class);
        var wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();
        var jwtUser = entity(JwtUser.class);
        var jwtRefreshToken = entity(JwtRefreshToken.class);
        when(serverHttpRequest.getServletRequest()).thenReturn(request);
        when(this.tokenService.getJwtUserByAccessTokenOrThrow(eq(request))).thenReturn(Tuple2.of(jwtUser, jwtRefreshToken));

        // Act
        var actual = this.componentUnderTest.determineUser(serverHttpRequest, wsHandler, attributes);

        // Assert
        verify(this.tokenService).getJwtUserByAccessTokenOrThrow(eq(request));
        assertThat(actual.getName()).isEqualTo(jwtUser.getUsername());
    }
}
