package tech1.framework.iam.services.abstracts;

import tech1.framework.iam.domain.dto.responses.ResponseInvitationCode;
import tech1.framework.iam.domain.dto.responses.ResponseSuperadminSessionsTable;
import tech1.framework.iam.domain.jwt.JwtUser;
import tech1.framework.iam.domain.jwt.RequestAccessToken;
import tech1.framework.iam.repositories.InvitationCodesRepository;
import tech1.framework.iam.repositories.UsersSessionsRepository;
import tech1.framework.iam.services.BaseSuperadminService;
import tech1.framework.iam.sessions.SessionRegistry;
import tech1.framework.iam.tasks.superadmin.AbstractSuperAdminResetServerTask;
import tech1.framework.foundation.domain.system.reset_server.ResetServerStatus;
import tech1.framework.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import tech1.framework.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import tech1.framework.foundation.incidents.events.publishers.IncidentPublisher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseSuperadminService implements BaseSuperadminService {

    // Incidents
    protected final IncidentPublisher incidentPublisher;
    // Sessions
    protected final SessionRegistry sessionRegistry;
    // Repositories
    protected final InvitationCodesRepository invitationCodesRepository;
    protected final UsersSessionsRepository usersSessionsRepository;
    // Tasks
    protected final AbstractSuperAdminResetServerTask resetServerTask;

    // =================================================================================================================
    // Server
    // =================================================================================================================
    @Override
    public ResetServerStatus getResetServerStatus() {
        return this.resetServerTask.getStatus();
    }

    @Override
    public void resetServerBy(JwtUser user) {
        this.incidentPublisher.publishResetServerStarted(new IncidentSystemResetServerStarted(user.username()));

        this.resetServerTask.reset(user);

        this.incidentPublisher.publishResetServerCompleted(new IncidentSystemResetServerCompleted(user.username()));
    }

    // =================================================================================================================
    // Invitation Codes
    // =================================================================================================================
    @Override
    public List<ResponseInvitationCode> findUnused() {
        return this.invitationCodesRepository.findUnused();
    }

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================
    @Override
    public ResponseSuperadminSessionsTable getSessions(RequestAccessToken requestAccessToken) {
        var activeAccessTokens = this.sessionRegistry.getActiveSessionsAccessTokens();
        return this.usersSessionsRepository.getSessionsTable(activeAccessTokens, requestAccessToken);
    }
}
