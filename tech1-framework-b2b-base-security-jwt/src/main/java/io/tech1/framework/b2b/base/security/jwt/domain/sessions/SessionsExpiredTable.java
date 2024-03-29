package io.tech1.framework.b2b.base.security.jwt.domain.sessions;

import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.UserSessionId;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtRefreshToken;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.domain.http.requests.UserRequestMetadata;
import io.tech1.framework.domain.tuples.Tuple3;

import java.util.List;
import java.util.Set;

public record SessionsExpiredTable(
        List<Tuple3<Username, JwtRefreshToken, UserRequestMetadata>> expiredSessions,
        Set<UserSessionId> expiredOrInvalidSessionIds
) {
}
