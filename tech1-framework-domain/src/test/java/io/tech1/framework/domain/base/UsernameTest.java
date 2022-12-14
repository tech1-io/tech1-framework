package io.tech1.framework.domain.base;

import com.fasterxml.jackson.core.type.TypeReference;
import io.tech1.framework.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UsernameTest extends AbstractSerializationDeserializationRunner {
    private static final Username USERNAME = Username.of("tech1");

    @Override
    protected String getFileName() {
        return "username-1.json";
    }

    @Override
    protected String getFolder() {
        return "base";
    }

    @Test
    public void serializeTest() {
        // Act
        var json = this.writeValueAsString(USERNAME);

        // Assert
        assertThat(json).isNotNull();
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    public void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<Username>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(USERNAME);
        assertThat(actual.getIdentifier()).isEqualTo(USERNAME.getIdentifier());
        assertThat(actual.toString()).isEqualTo(USERNAME.getIdentifier());
    }
}
