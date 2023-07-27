package io.tech1.framework.b2b.base.security.jwt.domain.dto.requests;

import io.tech1.framework.domain.base.Password;
import io.tech1.framework.domain.base.Username;

public record RequestUserRegistration1(
        Username username,
        Password password,
        Password confirmPassword,
        String zoneId,
        String invitationCode
) {
}