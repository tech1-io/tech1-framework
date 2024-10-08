package tech1.framework.foundation.domain.properties.configs.security.jwt;

import tech1.framework.foundation.domain.base.PropertyId;
import tech1.framework.foundation.domain.properties.annotations.MandatoryMapProperty;
import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.base.AbstractPropertyConfigs;
import tech1.framework.foundation.domain.properties.base.SecurityJwtIncidentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.EnumMap;
import java.util.Map;

import static tech1.framework.foundation.domain.properties.base.SecurityJwtIncidentType.*;
import static tech1.framework.foundation.utilities.random.RandomUtility.getEnumMapMappedRandomBoolean;
import static java.lang.Boolean.TRUE;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class IncidentsConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    @MandatoryMapProperty(propertyName = "typesConfigs", keySetClass = SecurityJwtIncidentType.class)
    private final Map<SecurityJwtIncidentType, Boolean> typesConfigs;

    public static IncidentsConfigs testsHardcoded() {
        return new IncidentsConfigs(
                new EnumMap<>(
                        Map.of(
                                AUTHENTICATION_LOGIN, true,
                                AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, false,
                                AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true,
                                AUTHENTICATION_LOGOUT, false,
                                AUTHENTICATION_LOGOUT_MIN, false,
                                SESSION_REFRESHED, true,
                                SESSION_EXPIRED, false,
                                REGISTER1, true,
                                REGISTER1_FAILURE, true
                        )
                )
        );
    }

    public static IncidentsConfigs random() {
        return new IncidentsConfigs(getEnumMapMappedRandomBoolean(SecurityJwtIncidentType.values()));
    }

    @Override
    public void assertProperties(PropertyId propertyId) {
        super.assertProperties(propertyId);

        var loginFailureUsernamePassword = this.typesConfigs.get(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD);
        var loginFailureUsernameMaskedPassword = this.typesConfigs.get(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD);

        if (TRUE.equals(loginFailureUsernamePassword) && TRUE.equals(loginFailureUsernameMaskedPassword)) {
            throw new IllegalArgumentException("Please configure login failure incident feature. Only one feature type could be enabled");
        }
    }

    public boolean isEnabled(SecurityJwtIncidentType type) {
        return TRUE.equals(this.typesConfigs.get(type));
    }
}
