package jbst.server.rb.components;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceBurnerRAMTest {

    private final ResourceBurnerRAM component = new ResourceBurnerRAM();

    @AfterEach
    void afterEach() {
        this.component.clean();
    }

    @Test
    void startRetainsConfiguredChunkSize() {
        // Act
        this.component.start(3600, 2);

        // Assert
        var status = this.component.getStatus();
        assertThat(status.growing()).isTrue();
        assertThat(status.everySeconds()).isEqualTo(3600);
        assertThat(status.chunkMB()).isEqualTo(2);
        assertThat(status.chunks()).isEqualTo(1);
        assertThat(status.retainedMB()).isEqualTo(2);
    }

    @Test
    void startWhileGrowingRetunesWithoutExtraChunk() {
        // Arrange
        this.component.start(3600, 1);

        // Act
        this.component.start(1800, 3);

        // Assert
        var status = this.component.getStatus();
        assertThat(status.everySeconds()).isEqualTo(1800);
        assertThat(status.chunkMB()).isEqualTo(3);
        assertThat(status.chunks()).isEqualTo(1);
        assertThat(status.retainedMB()).isEqualTo(1);
    }

    @Test
    void stopFreezesGrowthKeepingChunks() {
        // Arrange
        this.component.start(3600, 1);

        // Act
        this.component.stop();

        // Assert
        var status = this.component.getStatus();
        assertThat(status.growing()).isFalse();
        assertThat(status.chunks()).isEqualTo(1);
        assertThat(status.retainedMB()).isEqualTo(1);
    }

    @Test
    void cleanReleasesAllChunks() {
        // Arrange
        this.component.start(3600, 1);

        // Act
        this.component.clean();

        // Assert
        var status = this.component.getStatus();
        assertThat(status.growing()).isFalse();
        assertThat(status.chunks()).isZero();
        assertThat(status.retainedMB()).isZero();
    }

    @Test
    void schedulerGrowsAtConfiguredInterval() throws InterruptedException {
        // Act
        this.component.start(1, 1);

        // Assert
        var deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (this.component.getStatus().chunks() < 2 && System.nanoTime() < deadline) {
            Thread.sleep(100);
        }
        assertThat(this.component.getStatus().chunks()).isGreaterThanOrEqualTo(2);
    }
}
