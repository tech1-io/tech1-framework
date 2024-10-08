package tech1.framework.foundation.domain.properties.configs;

import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.annotations.MandatoryToggleProperty;
import tech1.framework.foundation.domain.properties.configs.mvc.CorsConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static tech1.framework.foundation.utilities.random.RandomUtility.randomBoolean;
import static tech1.framework.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class MvcConfigs extends AbstractTogglePropertiesConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    private String frameworkBasePathPrefix;
    @MandatoryToggleProperty
    private CorsConfigs corsConfigs;

    public static MvcConfigs testsHardcoded() {
        return new MvcConfigs(
                true,
                "/framework/security",
                CorsConfigs.testsHardcoded()
        );
    }

    public static MvcConfigs random() {
        return new MvcConfigs(
                randomBoolean(),
                randomString(),
                CorsConfigs.random()
        );
    }

    public static MvcConfigs enabled() {
        return testsHardcoded();
    }

    public static MvcConfigs disabled() {
        return new MvcConfigs(
                false,
                randomString(),
                CorsConfigs.random()
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }
}
