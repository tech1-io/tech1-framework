package io.tech1.framework.domain.asserts;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static io.tech1.framework.domain.asserts.Asserts.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

public class AssertsTests {

    private static Stream<Arguments> assertNonNullOrThrowTest() {
        return Stream.of(
                Arguments.of(new Object(), null),
                Arguments.of(null, "errorMessage")
        );
    }

    private static Stream<Arguments> assertNonBlankOrThrowTest() {
        return Stream.of(
                Arguments.of("not_empty", null),
                Arguments.of("", "errorMessage"),
                Arguments.of(" ", "errorMessage")
        );
    }

    private static Stream<Arguments> assertNonEmptyOrThrowTest() {
        return Stream.of(
                Arguments.of(List.of("not", "empty"), null),
                Arguments.of(emptyList(), "errorMessage"),
                Arguments.of(emptySet(), "errorMessage")
        );
    }

    private static Stream<Arguments> assertTrueOrThrowTest() {
        return Stream.of(
                Arguments.of(true, null),
                Arguments.of(false, "errorMessage1"),
                Arguments.of(false, "errorMessage2")
        );
    }

    private static Stream<Arguments> assertFalseOrThrowTest() {
        return Stream.of(
                Arguments.of(false, null),
                Arguments.of(true, "errorMessage1"),
                Arguments.of(true, "errorMessage2")
        );
    }

    @ParameterizedTest
    @MethodSource("assertNonNullOrThrowTest")
    public void assertNonNullOrThrowTest(Object object, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> assertNonNullOrThrow(object, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
            assertThat(throwable).hasMessageContaining(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertNonBlankOrThrowTest")
    public void assertNonBlankOrThrowTest(String object, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> assertNonBlankOrThrow(object, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
            assertThat(throwable).hasMessageContaining(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertNonEmptyOrThrowTest")
    public void assertNonEmptyOrThrowTest(Collection<?> collection, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> assertNonEmptyOrThrow(collection, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
            assertThat(throwable).hasMessageContaining(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertTrueOrThrowTest")
    public void assertTrueOrThrowTest(boolean flag, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> assertTrueOrThrow(flag, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
            assertThat(throwable.getMessage()).isEqualTo(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertFalseOrThrowTest")
    public void assertFalseOrThrowTest(boolean flag, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> assertFalseOrThrow(flag, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
            assertThat(throwable.getMessage()).isEqualTo(expectedErrorMessage);
        }
    }
}
