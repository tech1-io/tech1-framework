package tech1.framework.iam.domain.events;

import tech1.framework.foundation.domain.base.Username;

public record EventAuthenticationLogout(
        Username username
) {
}
