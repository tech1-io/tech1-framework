
package io.tech1.framework.domain.properties.base;

import io.tech1.framework.domain.base.Password;
import io.tech1.framework.domain.base.Username;
import io.tech1.framework.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConstructorBinding;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class RemoteServer extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final String baseURL;
    // TODO [YYL] String -> Username
    @MandatoryProperty
    private final String username;
    // TODO [YYL] String -> Password
    @MandatoryProperty
    private final String password;

    public static RemoteServer testsHardcoded() {
        return new RemoteServer("localhost", Username.testsHardcoded().identifier(), Password.testsHardcoded().value());
    }

    public Username getUsername() {
        return new Username(this.username);
    }

    public Password getPassword() {
        return new Password(this.password);
    }
}
