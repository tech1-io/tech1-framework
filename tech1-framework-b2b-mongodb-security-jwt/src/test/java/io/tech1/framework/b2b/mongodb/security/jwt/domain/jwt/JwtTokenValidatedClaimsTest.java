package io.tech1.framework.b2b.mongodb.security.jwt.domain.jwt;

import io.jsonwebtoken.Jwts;
import io.tech1.framework.b2b.base.security.jwt.domain.jwt.JwtAccessToken;
import org.junit.jupiter.api.Test;

import static io.tech1.framework.domain.utilities.random.RandomUtility.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JwtTokenValidatedClaimsTest {

    @Test
    void safeGetUsernameExceptionTest() {
        // Arrange
        var validatedClaims = JwtTokenValidatedClaims.invalid(new JwtAccessToken(randomString()));

        // Act
        var throwable = catchThrowable(validatedClaims::safeGetUsername);

        // Assert
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Attribute `JwtTokenValidatedClaims` is invalid");
    }

    @Test
    void safeGetUsernameTest() {
        // Arrange
        var username = randomUsername();
        var claims = Jwts.claims().setSubject(username.identifier());
        var validatedClaims = JwtTokenValidatedClaims.valid(new JwtAccessToken(randomString()), claims);

        // Act
        var actual = validatedClaims.safeGetUsername();

        // Assert
        assertThat(actual).isEqualTo(username);
    }

    @Test
    void safeGetExpirationTimestampExceptionTest() {
        // Arrange
        var validatedClaims = JwtTokenValidatedClaims.invalid(new JwtAccessToken(randomString()));

        // Act
        var throwable = catchThrowable(validatedClaims::safeGetExpirationTimestamp);

        // Assert
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Attribute `JwtTokenValidatedClaims` is invalid");
    }

    @Test
    void safeGetExpirationTimestampTest() {
        // Arrange
        var date = randomDate();
        var claims = Jwts.claims().setExpiration(date);
        var validatedClaims = JwtTokenValidatedClaims.valid(new JwtAccessToken(randomString()), claims);

        // Act
        var actual = validatedClaims.safeGetExpirationTimestamp();

        // Assert
        assertThat(actual).isEqualTo(date.getTime());
    }
}
