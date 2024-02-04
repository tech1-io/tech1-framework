package io.tech1.framework.b2b.base.security.jwt.assistants.current;

import io.tech1.framework.b2b.base.security.jwt.domain.db.UserSession;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseUserSessionsTable;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.CookieAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtUser;
import io.tech1.framework.b2b.base.security.jwt.domain.security.CurrentClientUser;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.domain.exceptions.tokens.CookieAccessTokenNotFoundException;

import javax.servlet.http.HttpServletRequest;

public interface CurrentSessionAssistant {
    Username getCurrentUsername();
    JwtUser getCurrentJwtUser();
    CurrentClientUser getCurrentClientUser();
    UserSession getCurrentUserSession(HttpServletRequest httpServletRequest) throws CookieAccessTokenNotFoundException;
    ResponseUserSessionsTable getCurrentUserDbSessionsTable(CookieAccessToken cookie);
}
