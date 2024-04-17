package io.tech1.framework.utilities.spring.actuator.info;

import com.fasterxml.jackson.core.type.TypeReference;
import io.tech1.framework.domain.base.Version;
import io.tech1.framework.domain.tests.runners.AbstractFolderSerializationRunner;
import io.tech1.framework.utilities.feigns.domain.spring.actuator.info.SpringBootActuatorInfo;
import io.tech1.framework.utilities.feigns.domain.spring.actuator.info.git.SpringBootActuatorInfoGit;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static io.tech1.framework.domain.tests.io.TestsIOUtils.readFile;
import static org.assertj.core.api.Assertions.assertThat;

class SpringBootActuatorInfoTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> deserializeTest() {
        return Stream.of(
                Arguments.of(
                        SpringBootActuatorInfo.dash(),
                        "—",
                        true,
                        Version.dash(),
                        "info-1.json"
                ),
                Arguments.of(
                        new SpringBootActuatorInfo(
                                SpringBootActuatorInfoGit.dash(),
                                new ArrayList<>(),
                                null,
                                null
                        ),
                        "—",
                        true,
                        Version.dash(),
                        "info-2.json"
                ),
                Arguments.of(
                        new SpringBootActuatorInfo(
                                SpringBootActuatorInfoGit.dash(),
                                new ArrayList<>(List.of("dev", "prod")),
                                null,
                                null
                        ),
                        "dev",
                        false,
                        Version.dash(),
                        "info-3.json"
                ),
                Arguments.of(
                        new SpringBootActuatorInfo(
                                SpringBootActuatorInfoGit.dash(),
                                null,
                                "stage",
                                null
                        ),
                        "stage",
                        false,
                        Version.dash(),
                        "info-4.json"
                ),
                Arguments.of(
                        new SpringBootActuatorInfo(
                                SpringBootActuatorInfoGit.dash(),
                                null,
                                null,
                                null
                        ),
                        "—",
                        true,
                        Version.dash(),
                        "info-5.json"
                ),
                Arguments.of(
                        SpringBootActuatorInfo.testsHardcoded(),
                        "dev",
                        false,
                        Version.testsHardcoded(),
                        "info-6.json"
                )
        );
    }

    @Override
    protected String getFolder() {
        return "spring/actuator/info";
    }

    // serialization ignored deliberately

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("deserializeTest")
    void deserializeTest(SpringBootActuatorInfo springBootActuatorInfo, String profile, boolean isDash, Version version, String fileName) {
        // Arrange
        var json = readFile(this.getFolder(), fileName);
        var typeReference = new TypeReference<SpringBootActuatorInfo>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        assertThat(actual).isEqualTo(springBootActuatorInfo);
        assertThat(actual.getProfileOrDash()).isEqualTo(profile);
        assertThat(actual.isProfileDash()).isEqualTo(isDash);
        assertThat(actual.getMavenVersionOrDash()).isEqualTo(version);
    }
}
