package tech1.framework.iam.domain.events;

import tech1.framework.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableRow;
import tech1.framework.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableView;
import tech1.framework.foundation.domain.system.reset_server.ResetServerStatus;
import tech1.framework.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static tech1.framework.foundation.domain.tests.io.TestsIOUtils.readFile;
import static org.assertj.core.api.Assertions.assertThat;

class WebsocketEventTest extends AbstractFolderSerializationRunner {

    @Override
    protected String getFolder() {
        return "domain/events";
    }

    @Test
    void serialize1Test() {
        // Arrange
        var websocketEvent = new WebsocketEvent();

        // Act
        var json = this.writeValueAsString(websocketEvent);

        // Assert
        assertThat(json).isEqualTo(readFile(this.getFolder(), "websocket-event-1.json"));
    }

    @Test
    void serialize2Test() {
        // Arrange
        var websocketEvent = new WebsocketEvent(
                Map.of(
                        "key2", 2L,
                        "key1", "value1",
                        "key3", true
                )
        );

        // Act
        var json = this.writeValueAsString(websocketEvent);

        // Assert
        assertThat(json).isEqualTo(readFile(this.getFolder(), "websocket-event-2.json"));
    }

    @Test
    void hardwareMonitoringTest() {
        var websocketEvent = WebsocketEvent.hardwareMonitoring(
                new HardwareMonitoringDatapointTableView(
                        List.of(
                                HardwareMonitoringDatapointTableRow.random(),
                                HardwareMonitoringDatapointTableRow.random()
                        )
                )
        );
        assertThat(websocketEvent.getAttributes())
                .hasSize(2)
                .containsKey("datapoint")
                .containsEntry("eventType", "HARDWARE_MONITORING");
        assertThat(websocketEvent.getAttributes().get("datapoint").getClass()).isEqualTo(HardwareMonitoringDatapointTableView.class);

        websocketEvent.add("key1", "value1");
        websocketEvent.add("key2", "value2");
        websocketEvent.add("key1", "value4");
        websocketEvent.add("key3", "value3");
        assertThat(websocketEvent.getAttributes()).hasSize(5);
    }

    @Test
    void resetServerProgressTest() {
        var websocketEvent = WebsocketEvent.resetServerProgress(
                new ResetServerStatus(15)
        );
        assertThat(websocketEvent.getAttributes())
                .hasSize(2)
                .containsKey("status")
                .containsEntry("eventType", "RESET_SERVER_PROGRESS");
        assertThat(websocketEvent.getAttributes().get("status").getClass()).isEqualTo(ResetServerStatus.class);

        websocketEvent.add("key1", "value1");
        websocketEvent.add("key2", "value2");
        websocketEvent.add("key1", "value4");
        websocketEvent.add("key3", "value3");
        assertThat(websocketEvent.getAttributes()).hasSize(5);
    }
}
