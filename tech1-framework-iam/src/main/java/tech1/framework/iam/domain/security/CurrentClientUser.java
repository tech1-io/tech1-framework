package tech1.framework.iam.domain.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tech1.framework.foundation.domain.base.AbstractAuthority;
import tech1.framework.foundation.domain.base.Email;
import tech1.framework.foundation.domain.base.Username;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static tech1.framework.foundation.utilities.random.RandomUtility.*;

// Lombok
@Data
public class CurrentClientUser {
    private final Username username;
    private final Email email;
    private final String name;
    @Schema(type = "string")
    private final ZoneId zoneId;
    private final boolean passwordChangeRequired;
    private final Set<String> authorities;
    private Map<String, Object> attributes;

    public static CurrentClientUser random() {
        return new CurrentClientUser(
                Username.random(),
                Email.random(),
                randomString(),
                randomZoneId(),
                randomBoolean(),
                new HashSet<>(),
                new HashMap<>()
        );
    }

    public CurrentClientUser(
            Username username,
            Email email,
            String name,
            ZoneId zoneId,
            boolean passwordChangeRequired,
            Set<SimpleGrantedAuthority> authorities,
            Map<String, Object> attributes
    ) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.zoneId = zoneId;
        this.passwordChangeRequired = passwordChangeRequired;
        this.authorities = authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toSet());
        this.attributes = attributes;
    }

    @JsonIgnore
    public Object getAttributeByKey(String key) {
        if (nonNull(this.attributes)) {
            return this.attributes.get(key);
        } else {
            return null;
        }
    }

    public boolean hasAuthority(AbstractAuthority authority) {
        return this.authorities.contains(authority.getValue());
    }

    public boolean hasAuthority(String authority) {
        return this.authorities.contains(authority);
    }
}
