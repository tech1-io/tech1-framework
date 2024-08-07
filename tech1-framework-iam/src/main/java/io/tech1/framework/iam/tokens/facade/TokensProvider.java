package io.tech1.framework.iam.tokens.facade;

import io.tech1.framework.iam.domain.jwt.RequestAccessToken;
import io.tech1.framework.iam.domain.jwt.RequestRefreshToken;
import io.tech1.framework.iam.domain.jwt.JwtAccessToken;
import io.tech1.framework.iam.domain.jwt.JwtRefreshToken;
import io.tech1.framework.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import io.tech1.framework.foundation.domain.exceptions.tokens.CsrfTokenNotFoundException;
import io.tech1.framework.foundation.domain.exceptions.tokens.RefreshTokenNotFoundException;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokensProvider {
    void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response);
    void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response);
    DefaultCsrfToken readCsrfToken(HttpServletRequest httpRequest) throws CsrfTokenNotFoundException;
    RequestAccessToken readRequestAccessToken(HttpServletRequest httpRequest) throws AccessTokenNotFoundException;
    RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest httpRequest) throws AccessTokenNotFoundException;
    RequestRefreshToken readRequestRefreshToken(HttpServletRequest httpRequest) throws RefreshTokenNotFoundException;
    RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest httpRequest) throws RefreshTokenNotFoundException;
    void clearTokens(HttpServletResponse httpResponse);
}
