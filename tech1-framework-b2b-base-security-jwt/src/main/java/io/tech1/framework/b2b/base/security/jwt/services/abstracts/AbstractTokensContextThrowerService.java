package io.tech1.framework.b2b.base.security.jwt.services.abstracts;

import io.tech1.framework.b2b.base.security.jwt.assistants.userdetails.JwtUserDetailsService;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtTokenValidatedClaims;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtUser;
import io.tech1.framework.b2b.base.security.jwt.repositories.AnyDbUsersSessionsRepository;
import io.tech1.framework.b2b.base.security.jwt.services.TokensContextThrowerService;
import io.tech1.framework.b2b.base.security.jwt.utils.SecurityJwtTokenUtils;
import io.tech1.framework.domain.exceptions.cookie.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTokensContextThrowerService implements TokensContextThrowerService {

    // Assistants
    protected final JwtUserDetailsService jwtUserDetailsService;
    // Repositories
    protected final AnyDbUsersSessionsRepository usersSessionsRepository;
    // Utilities
    protected final SecurityJwtTokenUtils securityJwtTokenUtils;

    @Override
    public JwtTokenValidatedClaims verifyValidityOrThrow(JwtAccessToken jwtAccessToken) throws CookieAccessTokenInvalidException {
        var validatedClaims = this.securityJwtTokenUtils.validate(jwtAccessToken);
        if (validatedClaims.isInvalid()) {
            SecurityContextHolder.clearContext();
            throw new CookieAccessTokenInvalidException();
        }
        return validatedClaims;
    }

    @Override
    public JwtTokenValidatedClaims verifyValidityOrThrow(JwtRefreshToken jwtRefreshToken) throws CookieRefreshTokenInvalidException {
        var validatedClaims = this.securityJwtTokenUtils.validate(jwtRefreshToken);
        if (validatedClaims.isInvalid()) {
            SecurityContextHolder.clearContext();
            throw new CookieRefreshTokenInvalidException();
        }
        return validatedClaims;
    }

    @Override
    public void verifyAccessTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws CookieAccessTokenExpiredException {
        if (this.securityJwtTokenUtils.isExpired(validatedClaims) && validatedClaims.isAccess()) {
            SecurityContextHolder.clearContext();
            throw new CookieAccessTokenExpiredException(validatedClaims.safeGetUsername());
        }
    }

    @Override
    public void verifyRefreshTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws CookieRefreshTokenExpiredException {
        if (this.securityJwtTokenUtils.isExpired(validatedClaims) && validatedClaims.isRefresh()) {
            SecurityContextHolder.clearContext();
            throw new CookieRefreshTokenExpiredException(validatedClaims.safeGetUsername());
        }
    }

    @Override
    public JwtUser verifyDbPresenceOrThrow(JwtTokenValidatedClaims validatedClaims, JwtRefreshToken oldJwtRefreshToken) throws CookieRefreshTokenDbNotFoundException {
        var username = validatedClaims.safeGetUsername();
        var user = this.jwtUserDetailsService.loadUserByUsername(username.identifier());
        var databasePresence = this.usersSessionsRepository.isPresent(oldJwtRefreshToken);
        if (!databasePresence) {
            SecurityContextHolder.clearContext();
            throw new CookieRefreshTokenDbNotFoundException(username);
        }
        return user;
    }
}