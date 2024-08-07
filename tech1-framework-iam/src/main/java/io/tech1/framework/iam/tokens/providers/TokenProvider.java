package io.tech1.framework.iam.tokens.providers;

import io.tech1.framework.iam.domain.jwt.JwtAccessToken;
import io.tech1.framework.iam.domain.jwt.JwtRefreshToken;
import io.tech1.framework.iam.domain.jwt.RequestAccessToken;
import io.tech1.framework.iam.domain.jwt.RequestRefreshToken;
import io.tech1.framework.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import io.tech1.framework.foundation.domain.exceptions.tokens.CsrfTokenNotFoundException;
import io.tech1.framework.foundation.domain.exceptions.tokens.RefreshTokenNotFoundException;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenProvider {
    void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response);
    void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response);
    DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws CsrfTokenNotFoundException;
    RequestAccessToken readRequestAccessToken(HttpServletRequest request) throws AccessTokenNotFoundException;
    RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws AccessTokenNotFoundException;
    RequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws RefreshTokenNotFoundException;
    RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws RefreshTokenNotFoundException;
    void clearTokens(HttpServletResponse response);
}
