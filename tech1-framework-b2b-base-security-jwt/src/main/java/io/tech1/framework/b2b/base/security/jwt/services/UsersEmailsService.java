package io.tech1.framework.b2b.base.security.jwt.services;

import io.tech1.framework.b2b.base.security.jwt.domain.functions.FunctionAuthenticationLoginEmail;
import io.tech1.framework.b2b.base.security.jwt.domain.functions.FunctionSessionRefreshedEmail;
import org.springframework.scheduling.annotation.Async;

public interface UsersEmailsService {
    @Async
    void executeAuthenticationLogin(FunctionAuthenticationLoginEmail function);
    @Async
    void executeSessionRefreshed(FunctionSessionRefreshedEmail function);
}
