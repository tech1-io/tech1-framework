package io.tech1.framework.domain.geo;

import io.tech1.framework.domain.tests.runners.AbstractFolderSerializationRunner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.tech1.framework.domain.geo.GeoLocation.*;
import static io.tech1.framework.domain.tests.io.TestsIOUtils.readFile;
import static io.tech1.framework.domain.utilities.random.RandomUtility.localhost;
import static org.assertj.core.api.Assertions.assertThat;

public class GeoLocationTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(processed(localhost(), "Ukraine", "UA", "πΊπ¦", "Lviv"), "geo-location-1.json"),
                Arguments.of(unknown(localhost(), "exception details"), "geo-location-2.json"),
                Arguments.of(processing(localhost()), "geo-location-3.json"),
                Arguments.of(processed(localhost(), null, null, null, "Lviv"), "geo-location-4.json"),
                Arguments.of(unknown(null, "exception details"), "geo-location-5.json")
        );
    }

    private static Stream<Arguments> getWhereTest() {
        return Stream.of(
                Arguments.of(processed(localhost(), "Ukraine", "UA", "πΊπ¦", "Lviv"), "Ukraine, Lviv"),
                Arguments.of(processed(localhost(), "Ukraine", "UA", "πΊπ¦", ""), "Ukraine"),
                Arguments.of(processed(localhost(), "Ukraine", "UA", "πΊπ¦", " "), "Ukraine"),
                Arguments.of(processed(localhost(), "Ukraine", "UA", "πΊπ¦", "    "), "Ukraine"),
                Arguments.of(processed(localhost(), "Ukraine", "UA", "πΊπ¦", null), "Ukraine"),
                Arguments.of(processed(localhost(), null, "UA", "πΊπ¦", "Lviv"), "Unknown"),
                Arguments.of(processed(localhost(), "", "UA", "πΊπ¦", "Lviv"), "Unknown"),
                Arguments.of(processed(localhost(), "", "UA", "πΊπ¦", "Lviv"), "Unknown"),
                Arguments.of(processed(localhost(), "  ", "UA", "πΊπ¦", "Lviv"), "Unknown"),
                Arguments.of(processed(localhost(), "     ", "UA", "πΊπ¦", "Lviv"), "Unknown")
        );
    }

    @Override
    protected String getFolder() {
        return "geo";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    public void serializeTest(GeoLocation geoLocation, String fileName) {
        // Act
        var json = this.writeValueAsString(geoLocation);

        // Assert
        assertThat(json).isNotNull();
        assertThat(json).isEqualTo(readFile(this.getFolder(), fileName));
    }

    @ParameterizedTest
    @MethodSource("getWhereTest")
    public void getWhereTest(GeoLocation geoLocation, String expected) {
        // Act
        var actual = geoLocation.getWhere();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
