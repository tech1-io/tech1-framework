package tech1.framework.foundation.domain.properties.configs.security.jwt.websockets;

import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.base.AbstractTogglePropertyConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static tech1.framework.foundation.utilities.random.RandomUtility.randomBoolean;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class WebsocketsTemplateConfigs extends AbstractTogglePropertyConfigs {
    @MandatoryProperty
    private final boolean enabled;

    public static WebsocketsTemplateConfigs testsHardcoded() {
        return new WebsocketsTemplateConfigs(true);
    }

    public static WebsocketsTemplateConfigs random() {
        return randomBoolean() ? enabled() : disabled();
    }

    public static WebsocketsTemplateConfigs enabled() {
        return testsHardcoded();
    }

    public static WebsocketsTemplateConfigs disabled() {
        return new WebsocketsTemplateConfigs(false);
    }
}
