package io.tech1.framework.domain.exceptions.cookie;

import org.junit.jupiter.api.Test;

import static io.tech1.framework.domain.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;

public class CookieNotFoundExceptionTest {

    @Test
    public void testException() {
        // Arrange
        var message = randomString();

        // Act
        var actual = new CookieNotFoundException(message);

        // Assert
        assertThat(actual.getMessage()).isEqualTo(message);
    }
}
