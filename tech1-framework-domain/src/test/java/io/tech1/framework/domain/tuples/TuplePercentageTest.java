package io.tech1.framework.domain.tuples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static io.tech1.framework.domain.tuples.TuplePercentage.progressTuplePercentage;
import static org.assertj.core.api.Assertions.assertThat;

public class TuplePercentageTest extends AbstractTupleTest {
    private final static TuplePercentage TUPLE = TuplePercentage.of(new BigDecimal(3L), new BigDecimal(11L), 2, 3);

    private static Stream<Arguments> constructorsTest() {
        return Stream.of(
                Arguments.of(new BigDecimal(2L), new BigDecimal(3L), "2.00", "2.000", "66.67", "66.66667"),
                Arguments.of(new BigDecimal(2L), new BigDecimal(4L), "2.00", "2.000", "50.00", "50.00000"),
                Arguments.of(new BigDecimal(2L), new BigDecimal(7L), "2.00", "2.000", "28.57", "28.57143")
        );
    }

    @Override
    protected String getFileName() {
        return "tuple-percentage.json";
    }

    @ParameterizedTest
    @MethodSource("constructorsTest")
    public void constructorsTest(
            BigDecimal value,
            BigDecimal maxValue,
            String expectedValue1,
            String expectedValue2,
            String expectedPercentage1,
            String expectedPercentage2
    ) {
        // Act
        var actual1 = progressTuplePercentage(value, maxValue);
        var actual2 = TuplePercentage.of(value, maxValue, 3, 5);

        // Assert
        assertThat(actual1).isNotNull();
        assertThat(actual1.getValue()).isEqualTo(expectedValue1);
        assertThat(actual1.getPercentage()).isEqualTo(expectedPercentage1);

        assertThat(actual2).isNotNull();
        assertThat(actual2.getValue()).isEqualTo(expectedValue2);
        assertThat(actual2.getPercentage()).isEqualTo(expectedPercentage2);
    }

    @Test
    public void zeroTest() {
        // Act
        var actual = TuplePercentage.zero();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo("0.00");
        assertThat(actual.getPercentage()).isEqualTo("0.00");
    }

    @Test
    public void oneHundredTest() {
        // Act
        var actual = TuplePercentage.oneHundred();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getValue()).isEqualTo("100.00");
        assertThat(actual.getPercentage()).isEqualTo("100.00");
    }

    @Test
    public void serializeTest() {
        // Act
        var json = this.writeValueAsString(TUPLE);

        // Assert
        assertThat(json).isNotNull();
        assertThat(json).isEqualTo(this.readFile());
    }

    // deserialized ignored deliberately
}
