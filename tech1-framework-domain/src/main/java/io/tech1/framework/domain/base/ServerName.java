package io.tech1.framework.domain.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.tech1.framework.domain.utilities.random.RandomUtility.randomString;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasLength;

public record ServerName(@NotNull String value) {

    @JsonCreator
    public static ServerName of(String value) {
        return new ServerName(value);
    }

    public static ServerName random() {
        return of(randomString());
    }

    public static ServerName testsHardcoded() {
        return of("tech1-server");
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    @Target({
            ElementType.FIELD,
            ElementType.METHOD
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ConstraintValidatorOnServerName.class)
    public @interface ValidServerName {
        String message() default "is invalid";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class ConstraintValidatorOnServerName implements ConstraintValidator<ValidServerName, ServerName> {
        @Override
        public boolean isValid(ServerName serverName, ConstraintValidatorContext constraintValidatorContext) {
            return nonNull(serverName) && hasLength(serverName.value);
        }
    }
}
