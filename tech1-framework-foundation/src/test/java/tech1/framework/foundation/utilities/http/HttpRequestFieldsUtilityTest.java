package tech1.framework.foundation.utilities.http;

import tech1.framework.foundation.domain.base.Email;
import tech1.framework.foundation.domain.constants.DomainConstants;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static tech1.framework.foundation.utilities.http.HttpRequestFieldsUtility.containsCamelCaseLettersAndNumbers;
import static tech1.framework.foundation.utilities.http.HttpRequestFieldsUtility.isEmail;
import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestFieldsUtilityTest {

    private static Stream<Arguments> containsCamelCaseLettersAndNumbersArgs() {
        return Stream.of(
                Arguments.of("only_lowercase", false),
                Arguments.of("only_lowercase", false),
                Arguments.of("ONLY_UPPERCASE", false),
                Arguments.of("ONLY_UPPERCASE", false),
                Arguments.of("BoTh_CaSeS", false),
                Arguments.of("BoTh_CaSeS", false),
                Arguments.of("BoTh_CaSeS123", true)
        );
    }

    private static Stream<Arguments> isEmailTest() {
        return Stream.of(
                Arguments.of("info", false),
                Arguments.of("info@", false),
                Arguments.of("info@tech1", false),
                Arguments.of("info@" + DomainConstants.TECH1, true),
                Arguments.of("petro.petrenko@gmail.com", true),
                Arguments.of("john78@proton.com", true)
        );
    }

    @ParameterizedTest
    @MethodSource("containsCamelCaseLettersAndNumbersArgs")
    void containsCamelCaseLettersAndNumbersTest(String password, boolean expected) {
        // Act
        var actual = containsCamelCaseLettersAndNumbers(password);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isEmailTest")
    void isEmailTest(String email, boolean expected) {
        // Act
        var actual1 = isEmail(Email.of(email));
        var actual2 = isEmail(email);

        // Assert
        assertThat(actual1).isEqualTo(expected);
        assertThat(actual2).isEqualTo(expected);
    }
}
