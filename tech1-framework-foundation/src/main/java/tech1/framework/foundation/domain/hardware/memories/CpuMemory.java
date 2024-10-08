package tech1.framework.foundation.domain.hardware.memories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

import static tech1.framework.foundation.utilities.numbers.RoundingUtility.scale;
import static tech1.framework.foundation.utilities.random.RandomUtility.randomBigDecimalByBounds;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class CpuMemory {
    @JsonValue
    private final BigDecimal value;

    @JsonCreator
    public CpuMemory(
            BigDecimal value
    ) {
        this.value = scale(value, 2);
    }

    public static CpuMemory zeroUsage() {
        return new CpuMemory(
                BigDecimal.ZERO
        );
    }

    public static CpuMemory random() {
        return new CpuMemory(
                randomBigDecimalByBounds(1, 50)
        );
    }

    public static CpuMemory testsHardcoded() {
        return new CpuMemory(
                new BigDecimal("1.234")
        );
    }
}
