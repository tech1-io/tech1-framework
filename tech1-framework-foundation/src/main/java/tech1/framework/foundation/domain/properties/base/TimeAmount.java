package tech1.framework.foundation.domain.properties.base;

import tech1.framework.foundation.domain.properties.annotations.MandatoryProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.temporal.ChronoUnit;

import static tech1.framework.foundation.utilities.random.RandomUtility.randomChronoUnit;
import static tech1.framework.foundation.utilities.random.RandomUtility.randomIntegerGreaterThanZeroByBounds;
import static java.time.temporal.ChronoUnit.HOURS;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class TimeAmount extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final long amount;
    @MandatoryProperty
    private final ChronoUnit unit;

    public static TimeAmount testsHardcoded() {
        return new TimeAmount(12L, HOURS);
    }

    public static TimeAmount random() {
        return new TimeAmount(randomIntegerGreaterThanZeroByBounds(1, 10), randomChronoUnit());
    }

    public tech1.framework.foundation.domain.time.TimeAmount getTimeAmount() {
        return new tech1.framework.foundation.domain.time.TimeAmount(
                this.amount,
                this.unit
        );
    }

    @Override
    public String toString() {
        return this.amount + " " + this.unit;
    }
}
