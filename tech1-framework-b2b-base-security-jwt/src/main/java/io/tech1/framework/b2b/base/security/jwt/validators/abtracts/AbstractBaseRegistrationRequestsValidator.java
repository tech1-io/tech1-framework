package io.tech1.framework.b2b.base.security.jwt.validators.abtracts;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserRegistration1;
import io.tech1.framework.b2b.base.security.jwt.domain.events.EventRegistration1Failure;
import io.tech1.framework.b2b.base.security.jwt.events.publishers.SecurityJwtIncidentPublisher;
import io.tech1.framework.b2b.base.security.jwt.events.publishers.SecurityJwtPublisher;
import io.tech1.framework.b2b.base.security.jwt.repositories.InvitationCodesRepository;
import io.tech1.framework.b2b.base.security.jwt.repositories.UsersRepository;
import io.tech1.framework.b2b.base.security.jwt.validators.BaseRegistrationRequestsValidator;
import io.tech1.framework.domain.exceptions.authentication.RegistrationException;
import io.tech1.framework.incidents.domain.registration.IncidentRegistration1Failure;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static io.tech1.framework.domain.asserts.Asserts.*;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.*;
import static java.util.Objects.nonNull;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseRegistrationRequestsValidator implements BaseRegistrationRequestsValidator {

    // Publishers
    protected final SecurityJwtPublisher securityJwtPublisher;
    protected final SecurityJwtIncidentPublisher securityJwtIncidentPublisher;
    // Repositories
    protected final InvitationCodesRepository invitationCodesRepository;
    protected final UsersRepository mongoUsersRepository;

    @Override
    public void validateRegistrationRequest1(RequestUserRegistration1 request) throws RegistrationException {
        var username = request.username();
        var zoneId = request.zoneId();
        var password = request.password();
        var confirmPassword = request.confirmPassword();
        var invitationCode = request.invitationCode();

        assertNonNullOrThrow(username, invalidAttribute("username"));
        assertNonNullOrThrow(password, invalidAttribute("password"));
        assertNonNullOrThrow(confirmPassword, invalidAttribute("confirmPassword"));
        assertNonNullNotBlankOrThrow(invitationCode, invalidAttribute("invitationCode"));

        assertZoneIdOrThrow(zoneId, invalidAttribute("zoneId"));

        var user = this.mongoUsersRepository.findByUsernameAsJwtUserOrNull(username);
        if (nonNull(user)) {
            var exception = entityAlreadyUsed("Username", username.value());
            this.securityJwtPublisher.publishRegistration1Failure(
                    EventRegistration1Failure.of(
                            username,
                            invitationCode,
                            exception
                    )
            );
            this.securityJwtIncidentPublisher.publishRegistration1Failure(
                    IncidentRegistration1Failure.of(
                            username,
                            invitationCode,
                            exception
                    )
            );
            throw new RegistrationException(exception);
        }

        var dbInvitationCode = this.invitationCodesRepository.findByValueAsAny(invitationCode);
        if (nonNull(dbInvitationCode)) {
            if (nonNull(dbInvitationCode.invited())) {
                var exception = entityAlreadyUsed("InvitationCode", dbInvitationCode.value());
                this.securityJwtPublisher.publishRegistration1Failure(
                        new EventRegistration1Failure(
                                username,
                                invitationCode,
                                dbInvitationCode.owner(),
                                exception
                        )
                );
                this.securityJwtIncidentPublisher.publishRegistration1Failure(
                        new IncidentRegistration1Failure(
                                username,
                                invitationCode,
                                dbInvitationCode.owner(),
                                exception
                        )
                );
                throw new RegistrationException(exception);
            }
        } else {
            var exception = entityNotFound("InvitationCode", invitationCode);
            this.securityJwtPublisher.publishRegistration1Failure(
                    EventRegistration1Failure.of(
                            username,
                            invitationCode,
                            exception
                    )
            );
            this.securityJwtIncidentPublisher.publishRegistration1Failure(
                    IncidentRegistration1Failure.of(
                            username,
                            invitationCode,
                            exception
                    )
            );
            throw new RegistrationException(exception);
        }
    }
}
