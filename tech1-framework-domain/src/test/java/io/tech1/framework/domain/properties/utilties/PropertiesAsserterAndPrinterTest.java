package io.tech1.framework.domain.properties.utilties;

import io.tech1.framework.domain.hardware.monitoring.HardwareName;
import io.tech1.framework.domain.properties.base.ScheduledJob;
import io.tech1.framework.domain.properties.base.SchedulerConfiguration;
import io.tech1.framework.domain.properties.base.SpringLogging;
import io.tech1.framework.domain.properties.base.SpringServer;
import io.tech1.framework.domain.properties.configs.EmailConfigs;
import io.tech1.framework.domain.properties.configs.HardwareMonitoringConfigs;
import io.tech1.framework.domain.properties.configs.MvcConfigs;
import io.tech1.framework.domain.properties.configs.SecurityJwtConfigs;
import io.tech1.framework.domain.properties.configs.security.jwt.IncidentsConfigs;
import io.tech1.framework.domain.tests.classes.NotUsedPropertiesConfigs;
import io.tech1.framework.domain.utilities.collections.CollectorUtility;
import io.tech1.framework.domain.utilities.enums.EnumUtility;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.tech1.framework.domain.properties.base.SecurityJwtIncidentType.*;
import static io.tech1.framework.domain.properties.utilities.PropertiesAsserter.assertProperties;
import static io.tech1.framework.domain.properties.utilities.PropertiesPrinter.printProperties;
import static io.tech1.framework.domain.tests.constants.TestsPropertiesConstants.*;
import static io.tech1.framework.domain.utilities.random.RandomUtility.randomBoolean;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

public class PropertiesAsserterAndPrinterTest {

    @Test
    public void notUsedPropertiesConfigsTest() {
        // Arrange
        var notUsedPropertiesConfigs = NotUsedPropertiesConfigs.of(
                ScheduledJob.of(true, SchedulerConfiguration.of(10, 10, TimeUnit.SECONDS)),
                SpringServer.of(8080),
                SpringLogging.of("logback-test.xml")
        );

        // Act
        assertProperties(notUsedPropertiesConfigs, "notUsedPropertiesConfigs");
        printProperties(notUsedPropertiesConfigs);

        // Assert
        // no asserts
    }

    @Test
    public void serverConfigsTest() {
        // Act
        assertProperties(SERVER_CONFIGS, "serverConfigs");
        printProperties(SERVER_CONFIGS);

        // Assert
        // no asserts
    }

    @Test
    public void asyncConfigsTest() {
        // Act
        assertProperties(ASYNC_CONFIGS, "asyncConfigs");
        printProperties(ASYNC_CONFIGS);

        // Assert
        // no asserts
    }

    @Test
    public void eventsConfigsTest() {
        // Act
        assertProperties(EVENTS_CONFIGS, "eventsConfigs");
        printProperties(EVENTS_CONFIGS);

        // Assert
        // no asserts
    }

    @Test
    public void mvcConfigsDisabledTest() {
        // Arrange
        var mvcConfigs = MvcConfigs.of(
                false,
                null,
                null
        );

        // Act
        assertProperties(mvcConfigs, "mvcConfigs");
        printProperties(mvcConfigs);

        // Assert
        // no asserts
    }

    @Test
    public void mvcConfigsTest() {
        // Act
        assertProperties(MVC_CONFIGS, "mvcConfigs");
        printProperties(MVC_CONFIGS);

        // Assert
        // no asserts
    }

    @Test
    public void emailConfigsDisabledTest() {
        // Arrange
        var emailConfigs = EmailConfigs.of(
                false,
                null,
                0,
                null,
                null,
                null,
                null
        );

        // Act
        assertProperties(emailConfigs, "emailConfigs");
        printProperties(emailConfigs);

        // Assert
        // no asserts
    }

    @Test
    public void emailConfigsTest() {
        // Act
        assertProperties(EMAIL_CONFIGS, "emailConfigs");
        printProperties(EMAIL_CONFIGS);

        // Assert
        // no asserts
    }

    @Test
    public void incidentConfigsTest() {
        // Act
        assertProperties(INCIDENT_CONFIGS, "incidentConfigs");
        printProperties(INCIDENT_CONFIGS);

        // Assert
        // no asserts
    }

    @Test
    public void hardwareMonitoringConfigsDisabledTest() {
        // Arrange
        var hardwareMonitoringConfigs = HardwareMonitoringConfigs.disabled();

        // Act
        assertProperties(hardwareMonitoringConfigs, "hardwareMonitoringConfigs");

        // Assert
        var thresholdsConfigs = hardwareMonitoringConfigs.getThresholdsConfigs();
        assertThat(thresholdsConfigs).hasSize(5);
        assertThat(thresholdsConfigs.keySet()).isEqualTo(EnumUtility.set(HardwareName.class));
        assertThat(thresholdsConfigs.values().stream().distinct().collect(CollectorUtility.toSingleton())).isEqualTo(ZERO);
    }

    @Test
    public void hardwareMonitoringConfigsExceptionTest() {
        // Arrange
        var hardwareMonitoringConfigs = HardwareMonitoringConfigs.of(
                true,
                Map.of(
                        HardwareName.CPU, new BigDecimal("80"),
                        HardwareName.HEAP, new BigDecimal("85")
                )
        );

        // Act
        var throwable = catchThrowable(() -> assertProperties(hardwareMonitoringConfigs, "hardwareMonitoringConfigs"));

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Attribute `hardwareMonitoringConfigs.thresholdsConfigs` requirements: `[CPU, HEAP, SERVER, SWAP, VIRTUAL]`, disjunction: `[SERVER, SWAP, VIRTUAL]`");
    }

    @Test
    public void hardwareMonitoringConfigsTest() {
        // Act
        assertProperties(HARDWARE_MONITORING_CONFIGS, "hardwareMonitoringConfigs");
        printProperties(HARDWARE_MONITORING_CONFIGS);

        // Assert
        var thresholdsConfigs = HARDWARE_MONITORING_CONFIGS.getThresholdsConfigs();
        assertThat(thresholdsConfigs).hasSize(5);
        assertThat(thresholdsConfigs.keySet()).isEqualTo(EnumUtility.set(HardwareName.class));
        assertThat(new HashSet<>(thresholdsConfigs.values())).hasSize(5);
    }

    @Test
    public void hardwareServerConfigsTest() {
        // Act
        assertProperties(HARDWARE_SERVER_CONFIGS, "hardwareServerConfigs");
        printProperties(HARDWARE_SERVER_CONFIGS);

        // Assert
        // no asserts
    }

    @RepeatedTest(5)
    public void securityJwtConfigsDisabledUsersEmailsConfigsTest() {
        // Act
        var securityJwtConfigs = SecurityJwtConfigs.disabledUsersEmailsConfigs();

        // Act
        var throwable = catchThrowable(() -> assertProperties(securityJwtConfigs, "securityJwtConfigs"));

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).startsWith("Attribute `securityJwtConfigs.");
        assertThat(throwable.getMessage()).endsWith("` is invalid");
    }

    @Test
    public void securityJwtConfigsTest() {
        // Act
        assertProperties(SECURITY_JWT_CONFIGS, "securityJwtConfigs");
        printProperties(SECURITY_JWT_CONFIGS);

        // Assert
        // no asserts
    }

    @Test
    public void securityJwtConfigsIncidentsCorrectTest() {
        var loginFailureUsernamePassword = randomBoolean();
        var loginFailureUsernameMaskedPassword = !loginFailureUsernamePassword;
        var incidentConfigs = IncidentsConfigs.of(
                Map.of(
                        AUTHENTICATION_LOGIN, randomBoolean(),
                        AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, loginFailureUsernamePassword,
                        AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, loginFailureUsernameMaskedPassword,
                        AUTHENTICATION_LOGOUT, randomBoolean(),
                        AUTHENTICATION_LOGOUT_MIN, randomBoolean(),
                        SESSION_REFRESHED, randomBoolean(),
                        SESSION_EXPIRED, randomBoolean(),
                        REGISTER1, randomBoolean(),
                        REGISTER1_FAILURE, randomBoolean()
                )
        );
        var securityJwtConfigs = new SecurityJwtConfigs();
        securityJwtConfigs.setAuthoritiesConfigs(SECURITY_JWT_CONFIGS.getAuthoritiesConfigs());
        securityJwtConfigs.setCookiesConfigs(SECURITY_JWT_CONFIGS.getCookiesConfigs());
        securityJwtConfigs.setEssenceConfigs(SECURITY_JWT_CONFIGS.getEssenceConfigs());
        securityJwtConfigs.setIncidentsConfigs(incidentConfigs);
        securityJwtConfigs.setJwtTokensConfigs(SECURITY_JWT_CONFIGS.getJwtTokensConfigs());
        securityJwtConfigs.setLoggingConfigs(SECURITY_JWT_CONFIGS.getLoggingConfigs());
        securityJwtConfigs.setMongodb(SECURITY_JWT_CONFIGS.getMongodb());
        securityJwtConfigs.setSessionConfigs(SECURITY_JWT_CONFIGS.getSessionConfigs());
        securityJwtConfigs.setUsersEmailsConfigs(SECURITY_JWT_CONFIGS.getUsersEmailsConfigs());

        // Act
        assertProperties(securityJwtConfigs, "securityJwtConfigs");
        printProperties(securityJwtConfigs);

        // Assert
        // no asserts
    }

    @Test
    public void securityJwtConfigsIncidentsNoSessionRefreshedFailureTest() {
        var incidentConfigs = IncidentsConfigs.of(
                Map.of(
                        AUTHENTICATION_LOGIN, randomBoolean(),
                        AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, false,
                        AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true,
                        AUTHENTICATION_LOGOUT, randomBoolean(),
                        AUTHENTICATION_LOGOUT_MIN, randomBoolean(),
                        SESSION_EXPIRED, randomBoolean(),
                        REGISTER1, randomBoolean(),
                        REGISTER1_FAILURE, randomBoolean()
                )
        );
        var securityJwtConfigs = new SecurityJwtConfigs();
        securityJwtConfigs.setAuthoritiesConfigs(SECURITY_JWT_CONFIGS.getAuthoritiesConfigs());
        securityJwtConfigs.setCookiesConfigs(SECURITY_JWT_CONFIGS.getCookiesConfigs());
        securityJwtConfigs.setEssenceConfigs(SECURITY_JWT_CONFIGS.getEssenceConfigs());
        securityJwtConfigs.setIncidentsConfigs(incidentConfigs);
        securityJwtConfigs.setJwtTokensConfigs(SECURITY_JWT_CONFIGS.getJwtTokensConfigs());
        securityJwtConfigs.setLoggingConfigs(SECURITY_JWT_CONFIGS.getLoggingConfigs());
        securityJwtConfigs.setMongodb(SECURITY_JWT_CONFIGS.getMongodb());
        securityJwtConfigs.setSessionConfigs(SECURITY_JWT_CONFIGS.getSessionConfigs());
        securityJwtConfigs.setUsersEmailsConfigs(SECURITY_JWT_CONFIGS.getUsersEmailsConfigs());

        // Act
        var throwable = catchThrowable(() -> assertProperties(securityJwtConfigs, "securityJwtConfigs"));

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Attribute `incidentsConfigs.typesConfigs` requirements: `[Authentication Login, Authentication Login Failure Username/Masked Password, Authentication Login Failure Username/Password, Authentication Logout, Authentication Logout Min, Register1, Register1 Failure, Session Expired, Session Refreshed]`, disjunction: `[Session Refreshed]`");
    }

    @Test
    public void securityJwtConfigsIncidentsOnlyOneLoginFailureTest() {
        var incidentConfigs = IncidentsConfigs.of(
                Map.of(
                        AUTHENTICATION_LOGIN, randomBoolean(),
                        AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, true,
                        AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true,
                        AUTHENTICATION_LOGOUT, randomBoolean(),
                        AUTHENTICATION_LOGOUT_MIN, randomBoolean(),
                        SESSION_REFRESHED, randomBoolean(),
                        SESSION_EXPIRED, randomBoolean(),
                        REGISTER1, randomBoolean(),
                        REGISTER1_FAILURE, randomBoolean()
                )
        );
        var securityJwtConfigs = new SecurityJwtConfigs();
        securityJwtConfigs.setAuthoritiesConfigs(SECURITY_JWT_CONFIGS.getAuthoritiesConfigs());
        securityJwtConfigs.setCookiesConfigs(SECURITY_JWT_CONFIGS.getCookiesConfigs());
        securityJwtConfigs.setEssenceConfigs(SECURITY_JWT_CONFIGS.getEssenceConfigs());
        securityJwtConfigs.setIncidentsConfigs(incidentConfigs);
        securityJwtConfigs.setJwtTokensConfigs(SECURITY_JWT_CONFIGS.getJwtTokensConfigs());
        securityJwtConfigs.setLoggingConfigs(SECURITY_JWT_CONFIGS.getLoggingConfigs());
        securityJwtConfigs.setMongodb(SECURITY_JWT_CONFIGS.getMongodb());
        securityJwtConfigs.setSessionConfigs(SECURITY_JWT_CONFIGS.getSessionConfigs());
        securityJwtConfigs.setUsersEmailsConfigs(SECURITY_JWT_CONFIGS.getUsersEmailsConfigs());

        // Act
        var throwable = catchThrowable(() -> assertProperties(securityJwtConfigs, "securityJwtConfigs"));

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Please configure login failure incident feature. Only one feature type could be enabled");
    }

    @Test
    public void securityJwtWebsocketsConfigsTest() {
        // Act
        assertProperties(SECURITY_JWT_WEBSOCKETS_CONFIGS, "securityJwtWebsocketsConfigs");
        printProperties(SECURITY_JWT_WEBSOCKETS_CONFIGS);

        // Assert
        // no asserts
    }
}
