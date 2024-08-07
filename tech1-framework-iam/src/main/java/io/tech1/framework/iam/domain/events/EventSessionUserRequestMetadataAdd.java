package io.tech1.framework.iam.domain.events;

import io.tech1.framework.iam.domain.functions.FunctionSessionUserRequestMetadataSave;
import io.tech1.framework.iam.domain.db.UserSession;
import io.tech1.framework.foundation.domain.base.Email;
import io.tech1.framework.foundation.domain.base.Username;
import io.tech1.framework.foundation.domain.http.requests.IPAddress;
import io.tech1.framework.foundation.domain.http.requests.UserAgentHeader;
import io.tech1.framework.foundation.domain.tuples.TupleToggle;
import org.jetbrains.annotations.NotNull;

public record EventSessionUserRequestMetadataAdd(
        @NotNull Username username,
        Email email,
        @NotNull UserSession session,
        @NotNull IPAddress clientIpAddr,
        UserAgentHeader userAgentHeader,
        boolean isAuthenticationLoginEndpoint,
        boolean isAuthenticationRefreshTokenEndpoint
) {
    public FunctionSessionUserRequestMetadataSave getSaveFunction() {
        return new FunctionSessionUserRequestMetadataSave(
                this.username,
                this.session,
                this.clientIpAddr,
                this.userAgentHeader,
                TupleToggle.disabled(),
                TupleToggle.disabled()
        );
    }
}
