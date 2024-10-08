package tech1.framework.foundation.domain.hardware.monitoring;

import tech1.framework.foundation.domain.hardware.bytes.ByteSize;
import tech1.framework.foundation.domain.hardware.memories.GlobalMemory;
import tech1.framework.foundation.domain.hardware.memories.HeapMemory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static tech1.framework.foundation.domain.asserts.Asserts.assertNonNullOrThrow;
import static tech1.framework.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class HardwareMonitoringMaxValues {
    private final ByteSize server;
    private final ByteSize swap;
    private final ByteSize virtual;
    private final ByteSize heap;

    public HardwareMonitoringMaxValues(
            GlobalMemory global,
            HeapMemory heap
    ) {
        assertNonNullOrThrow(global, invalidAttribute("HardwareMonitoringMaxValues.global"));
        assertNonNullOrThrow(heap, invalidAttribute("HardwareMonitoringMaxValues.heap"));
        this.server = global.getTotal();
        this.swap = global.getSwapTotal();
        this.virtual = global.getVirtualTotal();
        this.heap = heap.getMax();
    }

    public static HardwareMonitoringMaxValues random() {
        return new HardwareMonitoringMaxValues(
                GlobalMemory.random(),
                HeapMemory.random()
        );
    }
}
