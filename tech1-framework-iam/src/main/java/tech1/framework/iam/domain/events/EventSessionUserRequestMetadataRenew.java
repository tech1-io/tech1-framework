package tech1.framework.iam.domain.events;

import tech1.framework.iam.domain.functions.FunctionSessionUserRequestMetadataSave;
import tech1.framework.iam.domain.db.UserSession;
import tech1.framework.foundation.domain.base.Username;
import tech1.framework.foundation.domain.http.requests.IPAddress;
import tech1.framework.foundation.domain.http.requests.UserAgentHeader;
import tech1.framework.foundation.domain.tuples.TupleToggle;
import org.jetbrains.annotations.NotNull;

public record EventSessionUserRequestMetadataRenew(
        @NotNull Username username,
        @NotNull UserSession session,
        @NotNull IPAddress clientIpAddr,
        @NotNull UserAgentHeader userAgentHeader,
        @NotNull TupleToggle<Boolean> metadataRenewCron,
        @NotNull TupleToggle<Boolean> metadataRenewManually
) {
    public FunctionSessionUserRequestMetadataSave getSaveFunction() {
        return new FunctionSessionUserRequestMetadataSave(
                this.username,
                this.session,
                this.clientIpAddr,
                this.userAgentHeader,
                this.metadataRenewCron,
                this.metadataRenewManually
        );
    }
}
