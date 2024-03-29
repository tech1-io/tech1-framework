package io.tech1.framework.b2b.base.security.jwt.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AccountAccessMethod {
    USERNAME_PASSWORD("username/password"),
    SECURITY_TOKEN("security token");

    @Getter
    private final String value;
}
