package io.tech1.framework.b2b.base.security.jwt.validators.abtracts;

import io.tech1.framework.b2b.base.security.jwt.domain.identifiers.UserSessionId;
import io.tech1.framework.b2b.base.security.jwt.repositories.UsersSessionsRepository;
import io.tech1.framework.b2b.base.security.jwt.validators.BaseUsersSessionsRequestsValidator;
import io.tech1.framework.domain.base.Username;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.entityAccessDenied;
import static io.tech1.framework.domain.utilities.exceptions.ExceptionsMessagesUtility.entityNotFound;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseUsersSessionsRequestsValidator implements BaseUsersSessionsRequestsValidator {

    // Repositories
    protected final UsersSessionsRepository usersSessionsRepository;

    @Override
    public void validateAccess(Username username, UserSessionId sessionId) {
        var tuplePresence = this.usersSessionsRepository.isPresent(sessionId);

        if (!tuplePresence.present()) {
            throw new IllegalArgumentException(entityNotFound("Session", sessionId.value()));
        }

        if (!username.equals(tuplePresence.value().username())) {
            throw new IllegalArgumentException(entityAccessDenied("Session", sessionId.value()));
        }
    }
}
