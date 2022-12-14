package io.tech1.framework.b2b.mongodb.security.jwt.services;

import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.requests.RequestNewInvitationCodeParams;
import io.tech1.framework.b2b.mongodb.security.jwt.domain.dto.responses.ResponseInvitationCodes;
import io.tech1.framework.domain.base.Username;

public interface InvitationCodeService {
    ResponseInvitationCodes findByOwner(Username owner);
    void save(RequestNewInvitationCodeParams requestNewInvitationCodeParams, Username owner);
    void deleteById(String invitationCodeId);
}
