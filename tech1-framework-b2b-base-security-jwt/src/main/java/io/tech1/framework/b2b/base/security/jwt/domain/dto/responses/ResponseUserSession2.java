package io.tech1.framework.b2b.base.security.jwt.domain.dto.responses;

import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.UserSessionId;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.RequestAccessToken;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtAccessToken;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.domain.http.requests.UserRequestMetadata;
import io.tech1.framework.domain.time.TimeAgo;
import io.tech1.framework.domain.tuples.TupleExceptionDetails;

public record ResponseUserSession2(
        UserSessionId id,
        Username who,
        boolean current,
        String activity,
        TimeAgo when,
        TupleExceptionDetails exception,
        String ipAddr,
        String countryFlag,
        String where,
        String browser,
        String what
) {

    public static ResponseUserSession2 of(
            UserSessionId id,
            long updatedAt,
            Username username,
            RequestAccessToken requestAccessToken,
            JwtAccessToken accessToken,
            UserRequestMetadata metadata
    ) {
        var current = requestAccessToken.value().equals(accessToken.value());
        var activity = current ? "Current session" : "—";

        var whereTuple3 = metadata.getWhereTuple3();
        var whatTuple2 = metadata.getWhatTuple2();

        return new ResponseUserSession2(
                id,
                username,
                current,
                activity,
                new TimeAgo(updatedAt),
                metadata.getException(),
                whereTuple3.a(),
                whereTuple3.b(),
                whereTuple3.c(),
                whatTuple2.a(),
                whatTuple2.b()
        );
    }
}
