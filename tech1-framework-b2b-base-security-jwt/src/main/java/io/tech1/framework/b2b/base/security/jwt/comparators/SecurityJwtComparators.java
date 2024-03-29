package io.tech1.framework.b2b.base.security.jwt.comparators;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseInvitationCode;
import io.tech1.framework.b2b.base.security.jwt.domain.dto.responses.ResponseUserSession2;
import lombok.experimental.UtilityClass;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;

@UtilityClass
public class SecurityJwtComparators {
    public static final Comparator<ResponseInvitationCode> INVITATION_CODE_2 = (o1, o2) -> {
        if (isNull(o1.invited()) && isNull(o2.invited())) {
            return 0;
        } else if (isNull(o1.invited())) {
            return -1;
        } else if (isNull(o2.invited())) {
            return 1;
        } else {
            return comparing((ResponseInvitationCode code) -> code.invited().value()).compare(o1, o2);
        }
    };

    public static final Comparator<ResponseUserSession2> USERS_SESSIONS = comparing(ResponseUserSession2::current).reversed()
            .thenComparing(ResponseUserSession2::where);

    public static final Comparator<ResponseUserSession2> ACTIVE_SESSIONS_AS_SUPERADMIN = comparing(ResponseUserSession2::current).reversed()
            .thenComparing((ResponseUserSession2 session) -> session.who().value())
            .thenComparing(ResponseUserSession2::where);

    public static final Comparator<ResponseUserSession2> INACTIVE_SESSIONS_AS_SUPERADMIN = comparing((ResponseUserSession2 session) -> session.who().value())
            .thenComparing(ResponseUserSession2::where);
}
