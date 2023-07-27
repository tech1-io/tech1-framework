package io.tech1.framework.domain.tests.domain.enums;

import io.tech1.framework.domain.base.AbstractAuthority;

// WARNING: used in PropertiesAsserterAndPrinterTest (reflection scan process)
public enum TestAuthority implements AbstractAuthority {
    USER("user"),
    ADMIN("admin");

    private final String value;

    TestAuthority(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
