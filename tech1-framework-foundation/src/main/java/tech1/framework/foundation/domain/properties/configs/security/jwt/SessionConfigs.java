package tech1.framework.foundation.domain.properties.configs.security.jwt;

import tech1.framework.foundation.domain.constants.ZoneIdsConstants;
import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.base.Cron;
import tech1.framework.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final Cron cleanSessionsByExpiredRefreshTokensCron;
    @MandatoryProperty
    private final Cron enableSessionsMetadataRenewCron;

    public static SessionConfigs testsHardcoded() {
        return new SessionConfigs(
                Cron.enabled("*/30 * * * * *", ZoneIdsConstants.UKRAINE.getId()),
                Cron.enabled("*/15 * * * * *", ZoneIdsConstants.UKRAINE.getId())
        );
    }

    public static SessionConfigs random() {
        return new SessionConfigs(
                Cron.random(),
                Cron.random()
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return false;
    }
}
