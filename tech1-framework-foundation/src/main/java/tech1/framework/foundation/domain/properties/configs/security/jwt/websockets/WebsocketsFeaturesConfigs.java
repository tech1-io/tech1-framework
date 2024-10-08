package tech1.framework.foundation.domain.properties.configs.security.jwt.websockets;

import tech1.framework.foundation.domain.properties.annotations.NonMandatoryProperty;
import tech1.framework.foundation.domain.properties.configs.AbstractPropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class WebsocketsFeaturesConfigs extends AbstractPropertiesConfigs {
    @NonMandatoryProperty
    private WebsocketsFeatureHardwareConfigs hardwareConfigs;
    @NonMandatoryProperty
    private WebsocketsFeatureHardwareConfigs resetServerConfigs;

    public static WebsocketsFeaturesConfigs testsHardcoded() {
        return new WebsocketsFeaturesConfigs(
                WebsocketsFeatureHardwareConfigs.testsHardcoded(),
                WebsocketsFeatureHardwareConfigs.testsHardcoded()
        );
    }

    public static WebsocketsFeaturesConfigs random() {
        return new WebsocketsFeaturesConfigs(
                WebsocketsFeatureHardwareConfigs.random(),
                WebsocketsFeatureHardwareConfigs.random()
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return false;
    }
}
