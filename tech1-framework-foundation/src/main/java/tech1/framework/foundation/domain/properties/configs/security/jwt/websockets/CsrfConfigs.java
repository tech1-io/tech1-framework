package tech1.framework.foundation.domain.properties.configs.security.jwt.websockets;

import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.base.AbstractPropertyConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import static tech1.framework.foundation.utilities.random.RandomUtility.randomString;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class CsrfConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final String headerName;
    @MandatoryProperty
    private final String parameterName;
    @MandatoryProperty
    private final String tokenKey;

    public static CsrfConfigs testsHardcoded() {
        return new CsrfConfigs("csrf-header", "_csrf", "csrf-token-key");
    }

    public static CsrfConfigs random() {
        return new CsrfConfigs(randomString(), randomString(), randomString());
    }
}
