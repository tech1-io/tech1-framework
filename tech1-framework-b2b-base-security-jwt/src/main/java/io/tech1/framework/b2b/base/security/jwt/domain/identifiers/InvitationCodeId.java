package io.tech1.framework.b2b.base.security.jwt.domain.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

public record InvitationCodeId(@NotNull String value) {

    @JsonCreator
    public static InvitationCodeId of(String value) {
        return new InvitationCodeId(value);
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}