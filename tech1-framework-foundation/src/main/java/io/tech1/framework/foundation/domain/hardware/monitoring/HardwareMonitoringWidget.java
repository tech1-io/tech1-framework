package io.tech1.framework.foundation.domain.hardware.monitoring;

import io.tech1.framework.foundation.domain.base.Version;
import io.tech1.framework.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;
import io.tech1.framework.foundation.domain.properties.configs.HardwareMonitoringConfigs;

public record HardwareMonitoringWidget(
        Version version,
        HardwareMonitoringDatapointTableView datapoint
) {

    public static HardwareMonitoringWidget of(EventLastHardwareMonitoringDatapoint event, HardwareMonitoringConfigs configs) {
        return new HardwareMonitoringWidget(
                event.version(),
                event.last().tableView(
                        new HardwareMonitoringThresholds(
                               configs.getThresholdsConfigs()
                        )
                )
        );
    }
}
