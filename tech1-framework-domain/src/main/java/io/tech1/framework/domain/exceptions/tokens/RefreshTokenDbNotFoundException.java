package io.tech1.framework.domain.exceptions.tokens;

import io.tech1.framework.domain.base.Username;

public class RefreshTokenDbNotFoundException extends Exception {

    public RefreshTokenDbNotFoundException(Username username) {
        super("JWT refresh token is not present in database. Username: " + username);
    }
}
