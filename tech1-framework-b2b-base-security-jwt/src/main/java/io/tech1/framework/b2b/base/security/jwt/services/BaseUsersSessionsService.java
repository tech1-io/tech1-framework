package io.tech1.framework.b2b.base.security.jwt.services;

import io.tech1.framework.b2b.base.security.jwt.domain.db.UserSession;
import io.tech1.framework.b2b.base.security.jwt.domain.events.EventSessionUserRequestMetadataAdd;
import io.tech1.framework.b2b.base.security.jwt.domain.events.EventSessionUserRequestMetadataRenew;
import io.tech1.framework.b2b.base.security.jwt.domain.functions.FunctionSessionUserRequestMetadataSave;
import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.UserSessionId;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.RequestAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtUser;
import io.tech1.framework.b2b.base.security.jwt.domain.sessions.SessionsExpiredTable;
import io.tech1.framework.foundation.domain.base.Username;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;

public interface BaseUsersSessionsService {
    void assertAccess(Username username, UserSessionId sessionId);
    void save(JwtUser user, JwtAccessToken accessToken, JwtRefreshToken refreshToken, HttpServletRequest httpServletRequest);
    void refresh(JwtUser user, UserSession oldSession, JwtAccessToken newAccessToken, JwtRefreshToken newRefreshToken, HttpServletRequest httpServletRequest);
    UserSession saveUserRequestMetadata(EventSessionUserRequestMetadataAdd event);
    void saveUserRequestMetadata(EventSessionUserRequestMetadataRenew event);
    UserSession saveUserRequestMetadata(FunctionSessionUserRequestMetadataSave saveFunction);
    SessionsExpiredTable getExpiredRefreshTokensSessions(Set<Username> usernames);
    void enableUserRequestMetadataRenewCron();
    void enableUserRequestMetadataRenewManually(UserSessionId sessionId);
    void renewUserRequestMetadata(UserSession session, HttpServletRequest httpServletRequest);
    void deleteById(UserSessionId sessionId);
    void deleteAllExceptCurrent(Username username, RequestAccessToken requestAccessToken);
    void deleteAllExceptCurrentAsSuperuser(RequestAccessToken requestAccessToken);
}
