package tech1.framework.iam.domain.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

import static tech1.framework.foundation.utilities.random.RandomUtility.randomStringsAsList;

public record RequestNewInvitationCodeParams(
        @NotEmpty Set<String> authorities
) {

    public static RequestNewInvitationCodeParams random() {
        return new RequestNewInvitationCodeParams(new HashSet<>(randomStringsAsList(3)));
    }

    public static RequestNewInvitationCodeParams testsHardcoded() {
        return new RequestNewInvitationCodeParams(new HashSet<>(Set.of("invitationCode:read", "invitationCode:write")));
    }
}
