package tech1.framework.foundation.domain.properties.configs;

import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import tech1.framework.foundation.domain.properties.base.Mongodb;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class MongodbSecurityJwtConfigs extends AbstractPropertiesConfigs {
    @MandatoryProperty
    private final Mongodb mongodb;

    public static MongodbSecurityJwtConfigs testsHardcoded() {
        return new MongodbSecurityJwtConfigs(
                Mongodb.testsHardcoded()
        );
    }

    public static MongodbSecurityJwtConfigs random() {
        return new MongodbSecurityJwtConfigs(
                Mongodb.random()
        );
    }

    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }
}
