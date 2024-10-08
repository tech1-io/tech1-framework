package tech1.framework.foundation.domain.tests.classes;

import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.base.ScheduledJob;
import tech1.framework.foundation.domain.properties.base.SpringLogging;
import tech1.framework.foundation.domain.properties.base.SpringServer;
import tech1.framework.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class NotUsedPropertiesConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final ScheduledJob scheduledJob;
    @MandatoryProperty
    private final SpringServer springServer;
    @MandatoryProperty
    private final SpringLogging springLogging;

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }
}
