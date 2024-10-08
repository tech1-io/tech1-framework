package tech1.framework.iam.services;

import tech1.framework.iam.domain.db.UserSession;
import tech1.framework.iam.domain.jwt.JwtAccessToken;
import tech1.framework.iam.domain.jwt.JwtRefreshToken;
import tech1.framework.iam.domain.jwt.JwtTokenValidatedClaims;
import tech1.framework.iam.domain.jwt.JwtUser;
import tech1.framework.foundation.domain.exceptions.tokens.*;
import tech1.framework.foundation.domain.tuples.Tuple2;

public interface TokensContextThrowerService {
    JwtTokenValidatedClaims verifyValidityOrThrow(JwtAccessToken accessToken) throws AccessTokenInvalidException;
    JwtTokenValidatedClaims verifyValidityOrThrow(JwtRefreshToken refreshToken) throws RefreshTokenInvalidException;

    void verifyAccessTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws AccessTokenExpiredException;
    void verifyRefreshTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws RefreshTokenExpiredException;

    void verifyDbPresenceOrThrow(JwtAccessToken accessToken, JwtTokenValidatedClaims validatedClaims) throws AccessTokenDbNotFoundException;
    Tuple2<JwtUser, UserSession> verifyDbPresenceOrThrow(JwtRefreshToken refreshToken, JwtTokenValidatedClaims validatedClaims) throws RefreshTokenDbNotFoundException;
}
