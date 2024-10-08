package tech1.framework.iam.domain.jwt;

import static tech1.framework.foundation.domain.constants.StringConstants.UNKNOWN;
import static tech1.framework.foundation.utilities.random.RandomUtility.randomString;

public record RequestRefreshToken(String value) {
    public static RequestRefreshToken random() {
        return new RequestRefreshToken(randomString());
    }

    public static RequestRefreshToken unknown() {
        return new RequestRefreshToken(UNKNOWN);
    }

    public static RequestRefreshToken testsHardcoded() {
        return new RequestRefreshToken("AE3C542E4368A21EA007");
    }

    public JwtRefreshToken getJwtRefreshToken() {
        return new JwtRefreshToken(this.value);
    }
}
