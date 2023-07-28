package io.tech1.framework.b2b.mongodb.security.jwt.validators.impl;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestNewInvitationCodeParams;
import io.tech1.framework.b2b.mongodb.security.jwt.repositories.InvitationCodeRepository;
import io.tech1.framework.b2b.mongodb.security.jwt.validators.InvitationCodeRequestsValidator;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.properties.ApplicationFrameworkProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.tech1.framework.domain.asserts.Asserts.*;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.accessDenied;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvitationCodeRequestsValidatorImpl implements InvitationCodeRequestsValidator {

    // Repositories
    private final InvitationCodeRepository invitationCodeRepository;
    // Properties
    private final ApplicationFrameworkProperties applicationFrameworkProperties;

    @Override
    public void validateCreateNewInvitationCode(RequestNewInvitationCodeParams requestNewInvitationCodeParams) {
        var authorities = requestNewInvitationCodeParams.authorities();
        var availableAuthorities = this.applicationFrameworkProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities();

        assertNonNullNotEmptyOrThrow(authorities, invalidAttribute("authorities"));
        assertTrueOrThrow(availableAuthorities.containsAll(authorities), "Invitation code request params contains unsupported authority");
    }

    @Override
    public void validateDeleteById(Username username, String invitationCodeId) {
        assertNonNullNotBlankOrThrow(invitationCodeId, invalidAttribute("invitationCodeId"));
        assertNonNullOrThrow(username, invalidAttribute("owner"));

        var invitationCode = this.invitationCodeRepository.requirePresence(invitationCodeId);
        if (!username.equals(invitationCode.getOwner())) {
            throw new IllegalArgumentException(accessDenied(username, "InvitationCode", invitationCodeId));
        }
    }
}
