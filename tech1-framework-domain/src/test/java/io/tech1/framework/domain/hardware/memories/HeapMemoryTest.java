package io.tech1.framework.domain.hardware.memories;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.tech1.framework.domain.tests.io.TestsIOUtils.readFile;
import static org.assertj.core.api.Assertions.assertThat;

public class HeapMemoryTest extends AbstractMemoriesTest {

    private static Stream<Arguments> serializeDeserializeTest() {
        return Stream.of(
                Arguments.of(new HeapMemory(
                        1073741824L,
                        573741824L,
                        1073741824L,
                        1073741824L
                ), "heap-memory-1.json"),
                Arguments.of(HeapMemory.zeroUsage(), "heap-memory-2.json")
        );
    }

    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    public void serializeTest(HeapMemory heapMemory, String fileName) {
        // Act
        var json = this.writeValueAsString(heapMemory);

        // Assert
        assertThat(json).isNotNull();
        assertThat(json).isEqualTo(readFile(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    public void deserializeTest(HeapMemory heapMemory, String fileName) {
        // Arrange
        var json = readFile(this.getFolder(), fileName);
        var typeReference = new TypeReference<HeapMemory>() {};

        // Act
        var tuple = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(tuple).isNotNull();
        assertThat(tuple).isEqualTo(heapMemory);
    }
}
