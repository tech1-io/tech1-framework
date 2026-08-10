package jbst.server.rb.components;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceBurnerCPUTest {

    private final ResourceBurnerCPU component = new ResourceBurnerCPU();

    @AfterEach
    void afterEach() {
        this.component.clean();
    }

    @Test
    void startSpawnsConfiguredThreadsPerStep() {
        // Act
        this.component.start(3600, 3);

        // Assert
        var status = this.component.getStatus();
        assertThat(status.growing()).isTrue();
        assertThat(status.everySeconds()).isEqualTo(3600);
        assertThat(status.threadsPerStep()).isEqualTo(3);
        assertThat(status.threads()).isEqualTo(3);
    }

    @Test
    void startWhileGrowingRetunesWithoutExtraStep() {
        // Arrange
        this.component.start(3600, 2);

        // Act
        this.component.start(1800, 5);

        // Assert
        var status = this.component.getStatus();
        assertThat(status.everySeconds()).isEqualTo(1800);
        assertThat(status.threadsPerStep()).isEqualTo(5);
        assertThat(status.threads()).isEqualTo(2);
    }

    @Test
    void stopFreezesGrowthKeepingThreads() {
        // Arrange
        this.component.start(3600, 2);

        // Act
        this.component.stop();

        // Assert
        var status = this.component.getStatus();
        assertThat(status.growing()).isFalse();
        assertThat(status.threads()).isEqualTo(2);
    }

    @Test
    void cleanTerminatesAllThreads() {
        // Arrange
        this.component.start(3600, 2);

        // Act
        this.component.clean();

        // Assert
        var status = this.component.getStatus();
        assertThat(status.growing()).isFalse();
        assertThat(status.threads()).isZero();
    }

    @Test
    void schedulerGrowsAtConfiguredInterval() throws InterruptedException {
        // Act
        this.component.start(1, 1);

        // Assert
        var deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (this.component.getStatus().threads() < 2 && System.nanoTime() < deadline) {
            Thread.sleep(100);
        }
        assertThat(this.component.getStatus().threads()).isGreaterThanOrEqualTo(2);
    }
}
