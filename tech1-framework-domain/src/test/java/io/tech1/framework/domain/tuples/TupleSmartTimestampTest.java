package io.tech1.framework.domain.tuples;

import org.junit.jupiter.api.Test;

import static io.tech1.framework.domain.constants.ZoneIdsConstants.UKRAINE;
import static io.tech1.framework.domain.tests.constants.TestsDTFsConstants.DEFAULT_DATE_FORMAT_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

class TupleSmartTimestampTest extends AbstractTupleTest {
    private static final TupleSmartTimestamp TUPLE = TupleSmartTimestamp.of(
            1668419401637L,
            UKRAINE,
            DEFAULT_DATE_FORMAT_PATTERN
    );

    @Override
    protected String getFileName() {
        return "tuple-smart-timestamp.json";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(TUPLE);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    // deserialization ignored deliberately
}
