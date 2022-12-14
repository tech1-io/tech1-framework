package io.tech1.framework.b2b.mongodb.security.jwt.resources;

import io.jsonwebtoken.Claims;
import io.tech1.framework.b2b.mongodb.security.jwt.assistants.core.CurrentSessionAssistant;
import io.tech1.framework.b2b.mongodb.security.jwt.assistants.userdetails.JwtUserDetailsAssistant;
import io.tech1.framework.b2b.mongodb.security.jwt.cookies.CookieProvider;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.requests.RequestUserLogin;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.responses.ResponseUserSession1;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.jwt.*;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.session.Session;
import io.tech1.framework.b2b.mongodb.security.jwt.services.TokenService;
import io.tech1.framework.b2b.mongodb.security.jwt.services.UserSessionService;
import io.tech1.framework.b2b.mongodb.security.jwt.sessions.SessionRegistry;
import io.tech1.framework.b2b.mongodb.security.jwt.tests.runnerts.AbstractResourcesRunner;
import io.tech1.framework.b2b.mongodb.security.jwt.utilities.SecurityJwtTokenUtility;
import io.tech1.framework.b2b.mongodb.security.jwt.validators.AuthenticationRequestsValidator;
import io.tech1.framework.domain.exceptions.cookie.CookieRefreshTokenDbNotFoundException;
import io.tech1.framework.domain.exceptions.cookie.CookieRefreshTokenExpiredException;
import io.tech1.framework.domain.exceptions.cookie.CookieRefreshTokenInvalidException;
import io.tech1.framework.domain.exceptions.cookie.CookieRefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.stream.Stream;

import static io.tech1.framework.domain.utilities.random.EntityUtility.entity;
import static io.tech1.framework.domain.utilities.random.RandomUtility.randomString;
import static io.tech1.framework.domain.utilities.random.RandomUtility.randomUsername;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityAuthenticationResourceTest extends AbstractResourcesRunner {

    private static Stream<Arguments> refreshTokenThrowCookieUnauthorizedExceptionsTest() {
        return Stream.of(
                Arguments.of(new CookieRefreshTokenNotFoundException()),
                Arguments.of(new CookieRefreshTokenInvalidException()),
                Arguments.of( new CookieRefreshTokenExpiredException(randomUsername())),
                Arguments.of(new CookieRefreshTokenDbNotFoundException(randomUsername()))
        );
    }

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Session
    private final SessionRegistry sessionRegistry;
    // Services
    private final UserSessionService userSessionService;
    private final TokenService tokenService;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    private final JwtUserDetailsAssistant jwtUserDetailsAssistant;
    // Cookies
    private final CookieProvider cookieProvider;
    // Validators
    private final AuthenticationRequestsValidator authenticationRequestsValidator;
    // Utilities
    private final SecurityJwtTokenUtility securityJwtTokenUtility;

    // Resource
    private final BaseSecurityAuthenticationResource componentUnderTest;

    @BeforeEach
    public void beforeEach() throws Exception {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.authenticationManager,
                this.sessionRegistry,
                this.userSessionService,
                this.tokenService,
                this.currentSessionAssistant,
                this.jwtUserDetailsAssistant,
                this.cookieProvider,
                this.authenticationRequestsValidator,
                this.securityJwtTokenUtility
        );
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(
                this.authenticationManager,
                this.sessionRegistry,
                this.userSessionService,
                this.tokenService,
                this.currentSessionAssistant,
                this.jwtUserDetailsAssistant,
                this.cookieProvider,
                this.authenticationRequestsValidator,
                this.securityJwtTokenUtility
        );
    }

    @Test
    public void loginTest() throws Exception {
        // Arrange
        var requestUserLogin = entity(RequestUserLogin.class);
        var username = requestUserLogin.getUsername();
        var password = requestUserLogin.getPassword();
        var jwtUser = entity(JwtUser.class);
        when(this.jwtUserDetailsAssistant.loadUserByUsername(eq(username.getIdentifier()))).thenReturn(jwtUser);
        var jwtAccessToken = entity(JwtAccessToken.class);
        var jwtRefreshToken = entity(JwtRefreshToken.class);
        when(this.securityJwtTokenUtility.createJwtAccessToken(eq(jwtUser.getDbUser()))).thenReturn(jwtAccessToken);
        when(this.securityJwtTokenUtility.createJwtRefreshToken(eq(jwtUser.getDbUser()))).thenReturn(jwtRefreshToken);
        var currentClientUser = randomCurrentClientUser();
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(currentClientUser);

        // Act
        this.mvc.perform(
                        post("/authentication/login")
                                .content(this.objectMapper.writeValueAsString(requestUserLogin))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo(currentClientUser.getUsername().getIdentifier())))
                .andExpect(jsonPath("$.email", equalTo(currentClientUser.getEmail().getValue())));

        // Assert
        verify(this.authenticationRequestsValidator).validateLoginRequest(eq(requestUserLogin));
        verify(this.authenticationManager).authenticate(eq(new UsernamePasswordAuthenticationToken(username.getIdentifier(), password.getValue())));
        verify(this.jwtUserDetailsAssistant).loadUserByUsername(eq(username.getIdentifier()));
        verify(this.securityJwtTokenUtility).createJwtAccessToken(eq(jwtUser.getDbUser()));
        verify(this.securityJwtTokenUtility).createJwtRefreshToken(eq(jwtUser.getDbUser()));
        verify(this.userSessionService).save(eq(jwtUser.getDbUser()), eq(jwtRefreshToken), any(HttpServletRequest.class));
        verify(this.cookieProvider).createJwtAccessCookie(eq(jwtAccessToken), any(HttpServletResponse.class));
        verify(this.cookieProvider).createJwtRefreshCookie(eq(jwtRefreshToken), any(HttpServletResponse.class));
        // WARNING: no verifications on static SecurityContextHolder
        verify(this.sessionRegistry).register(eq(Session.of(username, jwtRefreshToken)));
        verify(this.currentSessionAssistant).getCurrentClientUser();
    }

    @Test
    public void logoutNoJwtRefreshTokenTest() throws Exception {
        // Arrange
        var cookieRefreshToken = new CookieRefreshToken(null);
        when(this.cookieProvider.readJwtRefreshToken(any(HttpServletRequest.class))).thenReturn(cookieRefreshToken);

        // Act
        this.mvc.perform(
                        post("/authentication/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.cookieProvider).readJwtRefreshToken(any(HttpServletRequest.class));
    }

    @Test
    public void logoutInvalidJwtRefreshTokenTest() throws Exception {
        // Arrange
        var cookieRefreshToken = new CookieRefreshToken(randomString());
        var jwtRefreshToken = cookieRefreshToken.getJwtRefreshToken();
        when(this.cookieProvider.readJwtRefreshToken(any(HttpServletRequest.class))).thenReturn(cookieRefreshToken);
        when(this.securityJwtTokenUtility.validate(eq(jwtRefreshToken))).thenReturn(JwtTokenValidatedClaims.invalid(jwtRefreshToken));

        // Act
        this.mvc.perform(
                        post("/authentication/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.cookieProvider).readJwtRefreshToken(any(HttpServletRequest.class));
        verify(this.securityJwtTokenUtility).validate(eq(jwtRefreshToken));
    }

    @Test
    public void logoutTest() throws Exception {
        // Arrange
        var httpSession = mock(HttpSession.class);
        var username = randomUsername();
        var cookieRefreshToken = new CookieRefreshToken(randomString());
        var jwtRefreshToken = cookieRefreshToken.getJwtRefreshToken();
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(username.getIdentifier());
        when(this.cookieProvider.readJwtRefreshToken(any(HttpServletRequest.class))).thenReturn(cookieRefreshToken);
        when(this.securityJwtTokenUtility.validate(eq(jwtRefreshToken))).thenReturn(JwtTokenValidatedClaims.valid(jwtRefreshToken, claims));

        // Act
        this.mvc.perform(
                post("/authentication/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setSession(httpSession);
                            return request;
                        })
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.cookieProvider).readJwtRefreshToken(any(HttpServletRequest.class));
        verify(this.securityJwtTokenUtility).validate(eq(jwtRefreshToken));
        verify(this.sessionRegistry).logout(eq(Session.of(username, jwtRefreshToken)));
        verify(this.cookieProvider).clearCookies(any(HttpServletResponse.class));
        verify(httpSession).invalidate();
        // WARNING: no verifications on static SecurityContextHolder
    }

    @Test
    public void logoutNullSessionTest() throws Exception {
        // Arrange
        var username = randomUsername();
        var cookieRefreshToken = new CookieRefreshToken(randomString());
        var jwtRefreshToken = cookieRefreshToken.getJwtRefreshToken();
        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(username.getIdentifier());
        when(this.cookieProvider.readJwtRefreshToken(any(HttpServletRequest.class))).thenReturn(cookieRefreshToken);
        when(this.securityJwtTokenUtility.validate(eq(jwtRefreshToken))).thenReturn(JwtTokenValidatedClaims.valid(jwtRefreshToken, claims));

        // Act
        this.mvc.perform(
                post("/authentication/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.cookieProvider).readJwtRefreshToken(any(HttpServletRequest.class));
        verify(this.securityJwtTokenUtility).validate(eq(jwtRefreshToken));
        verify(this.sessionRegistry).logout(eq(Session.of(username, jwtRefreshToken)));
        verify(this.cookieProvider).clearCookies(any(HttpServletResponse.class));
        // WARNING: no verifications on static SecurityContextHolder
    }

    @ParameterizedTest
    @MethodSource("refreshTokenThrowCookieUnauthorizedExceptionsTest")
    public void refreshTokenThrowCookieUnauthorizedExceptionsTest(Exception exception) throws Exception {
        // Arrange
        when(this.tokenService.refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenThrow(exception);

        // Act
        this.mvc.perform(
                        post("/authentication/refreshToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.exceptionEntityType", equalTo("ERROR")))
                .andExpect(jsonPath("$.attributes.shortMessage", equalTo(exception.getMessage())))
                .andExpect(jsonPath("$.attributes.fullMessage", equalTo(exception.getMessage())));

        // Assert
        verify(this.tokenService).refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(this.cookieProvider).clearCookies(any());
        reset(
                this.tokenService,
                this.cookieProvider
        );
    }

    @Test
    public void refreshTokenValidTest() throws Exception {
        // Arrange
        var userSession1 = entity(ResponseUserSession1.class);
        when(this.tokenService.refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class))).thenReturn(userSession1);

        // Act
        this.mvc.perform(
                        post("/authentication/refreshToken")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken", equalTo(userSession1.getRefreshToken())));

        // Assert
        verify(this.tokenService).refreshSessionOrThrow(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
