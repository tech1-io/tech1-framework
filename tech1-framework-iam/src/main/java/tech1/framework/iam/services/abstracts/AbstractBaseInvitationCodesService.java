package tech1.framework.iam.services.abstracts;

import tech1.framework.iam.domain.dto.requests.RequestNewInvitationCodeParams;
import tech1.framework.iam.domain.dto.responses.ResponseInvitationCode;
import tech1.framework.iam.domain.dto.responses.ResponseInvitationCodes;
import tech1.framework.iam.domain.identifiers.InvitationCodeId;
import tech1.framework.iam.repositories.InvitationCodesRepository;
import tech1.framework.iam.services.BaseInvitationCodesService;
import tech1.framework.foundation.domain.base.Username;
import tech1.framework.foundation.domain.properties.ApplicationFrameworkProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseInvitationCodesService implements BaseInvitationCodesService {

    // Repositories
    protected final InvitationCodesRepository invitationCodesRepository;
    // Properties
    protected final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Override
    public ResponseInvitationCodes findByOwner(Username owner) {
        var invitationCodes = this.invitationCodesRepository.findResponseCodesByOwner(owner);
        invitationCodes.sort(ResponseInvitationCode.INVITATION_CODE);
        return new ResponseInvitationCodes(
                this.applicationFrameworkProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities(),
                invitationCodes
        );
    }

    @Override
    public void save(Username owner, RequestNewInvitationCodeParams request) {
        this.invitationCodesRepository.saveAs(owner, request);
    }

    @Override
    public void deleteById(InvitationCodeId invitationCodeId) {
        this.invitationCodesRepository.delete(invitationCodeId);
    }
}
