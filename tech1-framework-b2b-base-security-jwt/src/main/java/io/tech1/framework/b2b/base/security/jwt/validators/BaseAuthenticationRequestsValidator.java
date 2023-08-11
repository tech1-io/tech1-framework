package io.tech1.framework.b2b.base.security.jwt.validators;

import io.tech1.framework.b2b.base.security.jwt.domain.dto.requests.RequestUserLogin;

public interface BaseAuthenticationRequestsValidator {
    void validateLoginRequest(RequestUserLogin requestUserLogin);}
