package jbst.server.rb.components;

import jbst.server.rb.domain.ResourceBurnerRamStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static jbst.foundation.domain.tuples.TuplePercentage.progressTuplePercentage;

/**
 * Burns RAM by retaining heap chunks.
 * <p>
 * Lifecycle: {@code start(everySeconds, chunkMB)} — begin growing (one extra
 * {@code chunkMB}-sized chunk every {@code everySeconds} seconds; calling it again
 * while growing retunes the speed without restarting),
 * {@code stop()} — freeze growth but keep the retained chunks (plateau),
 * {@code clean()} — stop growth and release all retained chunks.
 * <p>
 * Growth is bounded by the JVM max heap ({@code -Xmx}); on heap exhaustion the
 * component stops growing on its own and keeps whatever it managed to retain.
 */
@Slf4j
@Component
public class ResourceBurnerRAM {

    public static final int DEFAULT_EVERY_SECONDS = 10;
    public static final int DEFAULT_CHUNK_MB = 50;
    public static final int MIN_EVERY_SECONDS = 1;
    public static final int MAX_EVERY_SECONDS = 3600;
    public static final int MIN_CHUNK_MB = 1;
    public static final int MAX_CHUNK_MB = 1024;

    private final ReentrantLock lock = new ReentrantLock();
    private final List<byte[]> retained = new ArrayList<>();
    private volatile boolean growing = false;
    private int everySeconds = DEFAULT_EVERY_SECONDS;
    private int chunkMB = DEFAULT_CHUNK_MB;
    private long retainedBytes = 0;
    private long lastStepNanos = 0;

    @Scheduled(fixedRate = 1_000)
    public void tick() {
        this.lock.lock();
        try {
            if (this.growing && this.stepIntervalElapsed()) {
                this.grow();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void start(int everySeconds, int chunkMB) {
        this.lock.lock();
        try {
            this.everySeconds = everySeconds;
            this.chunkMB = chunkMB;
            if (!this.growing) {
                this.growing = true;
                this.grow();
            } else {
                LOGGER.info("Resource Burner RAM — retuned, +{}MB every {}s", chunkMB, everySeconds);
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void stop() {
        this.lock.lock();
        try {
            this.growing = false;
            var status = this.getStatus();
            LOGGER.info("Resource Burner RAM — growth frozen, retained: {}MB ({}% of max heap), heap used: {}MB/{}MB ({}%)", status.retainedMB(), status.retainedPercentage(), status.heapUsedMB(), status.heapMaxMB(), status.heapUsedPercentage());
        } finally {
            this.lock.unlock();
        }
    }

    public void clean() {
        this.lock.lock();
        try {
            this.growing = false;
            this.retained.clear();
            this.retainedBytes = 0;
            var status = this.getStatus();
            LOGGER.info("Resource Burner RAM — cleaned, all retained chunks released, heap used: {}MB/{}MB ({}%)", status.heapUsedMB(), status.heapMaxMB(), status.heapUsedPercentage());
        } finally {
            this.lock.unlock();
        }
    }

    public ResourceBurnerRamStatus getStatus() {
        this.lock.lock();
        try {
            var runtime = Runtime.getRuntime();
            var retainedMB = this.retainedBytes / (1024 * 1024);
            var heapUsedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            var heapMaxMB = runtime.maxMemory() / (1024 * 1024);
            return new ResourceBurnerRamStatus(
                    this.growing,
                    this.everySeconds,
                    this.chunkMB,
                    this.retained.size(),
                    retainedMB,
                    progressTuplePercentage(retainedMB, heapMaxMB).percentage(),
                    heapUsedMB,
                    heapMaxMB,
                    progressTuplePercentage(heapUsedMB, heapMaxMB).percentage()
            );
        } finally {
            this.lock.unlock();
        }
    }

    private boolean stepIntervalElapsed() {
        return System.nanoTime() - this.lastStepNanos >= TimeUnit.SECONDS.toNanos(this.everySeconds);
    }

    private void grow() {
        this.lastStepNanos = System.nanoTime();
        try {
            var chunk = new byte[this.chunkMB * 1024 * 1024];
            ThreadLocalRandom.current().nextBytes(chunk); // touch every page so RSS actually grows
            this.retained.add(chunk);
            this.retainedBytes += chunk.length;
            var status = this.getStatus();
            LOGGER.info("Resource Burner RAM — chunk #{} retained, total: {}MB ({}% of max heap), heap used: {}MB/{}MB ({}%)", status.chunks(), status.retainedMB(), status.retainedPercentage(), status.heapUsedMB(), status.heapMaxMB(), status.heapUsedPercentage());
        } catch (OutOfMemoryError error) {
            this.growing = false;
            var status = this.getStatus();
            LOGGER.warn("Resource Burner RAM — heap exhausted, growth stopped, retained: {}MB ({}% of max heap), heap used: {}MB/{}MB ({}%)", status.retainedMB(), status.retainedPercentage(), status.heapUsedMB(), status.heapMaxMB(), status.heapUsedPercentage());
        }
    }
}
