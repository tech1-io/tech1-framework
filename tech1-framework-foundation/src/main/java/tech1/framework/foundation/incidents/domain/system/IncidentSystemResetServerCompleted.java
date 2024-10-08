package tech1.framework.foundation.incidents.domain.system;

import tech1.framework.foundation.domain.base.Username;
import tech1.framework.foundation.incidents.domain.AbstractIncident;
import tech1.framework.foundation.incidents.domain.Incident;

public record IncidentSystemResetServerCompleted(
        Username username
) implements AbstractIncident {

    public static IncidentSystemResetServerCompleted testsHardcoded() {
        return new IncidentSystemResetServerCompleted(
                Username.testsHardcoded()
        );
    }

    @Override
    public Incident getPlainIncident() {
        var incident = new Incident("Reset Server Completed");
        incident.addUsername(this.username);
        return incident;
    }
}
