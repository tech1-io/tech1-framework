package io.tech1.framework.domain.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import io.tech1.framework.domain.tests.runners.AbstractObjectMapperRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.tech1.framework.domain.exceptions.ExceptionEntityType.ERROR;
import static io.tech1.framework.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;

class ExceptionEntityTest extends AbstractObjectMapperRunner {

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void serializeDeserializeTest() {
        // Arrange
        var exceptionMessage = randomString();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        var arrangedExceptionEntity = new ExceptionEntity(new NullPointerException(exceptionMessage));
        arrangedExceptionEntity.addAttribute("externalAttribute", randomString());

        // Act
        var json = this.writeValueAsString(arrangedExceptionEntity);
        HashMap<String, Object> exceptionEntity = OBJECT_MAPPER.readValue(json, typeRef);

        // Assert
        assertThat(exceptionEntity)
                .hasSize(3)
                .containsKeys("exceptionEntityType", "attributes", "timestamp")
                .containsEntry("exceptionEntityType", ERROR.toString());
        assertThat(exceptionEntity.get("timestamp")).isNotNull();
        var attributes = (Map<String, Object>) exceptionEntity.get("attributes");
        assertThat(attributes)
                .hasSize(3)
                .containsKey("externalAttribute")
                .containsEntry("shortMessage", exceptionMessage)
                .containsEntry("fullMessage", exceptionMessage);
    }
}
