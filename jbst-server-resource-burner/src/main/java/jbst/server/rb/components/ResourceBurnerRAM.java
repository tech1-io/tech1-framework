package jbst.server.rb.components;

import jbst.server.rb.domain.ResourceBurnerRamStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import static jbst.foundation.domain.tuples.TuplePercentage.progressTuplePercentage;

/**
 * Burns RAM by retaining heap chunks (50MB every 10 seconds while growing).
 * <p>
 * Lifecycle: {@code start()} — begin growing (one extra chunk every 10 seconds),
 * {@code stop()} — freeze growth but keep the retained chunks (plateau),
 * {@code clean()} — stop growth and release all retained chunks.
 * <p>
 * Growth is bounded by the JVM max heap ({@code -Xmx}); on heap exhaustion the
 * component stops growing on its own and keeps whatever it managed to retain.
 */
@Slf4j
@Component
public class ResourceBurnerRAM {

    public static final int CHUNK_SIZE_MB = 50;

    private final ReentrantLock lock = new ReentrantLock();
    private final List<byte[]> retained = new ArrayList<>();
    private volatile boolean growing = false;

    @Scheduled(fixedRate = 10_000)
    public void tick() {
        this.lock.lock();
        try {
            if (this.growing) {
                this.grow();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void start() {
        this.lock.lock();
        try {
            if (!this.growing) {
                this.growing = true;
                this.grow();
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
            var retainedMB = (long) this.retained.size() * CHUNK_SIZE_MB;
            var heapUsedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            var heapMaxMB = runtime.maxMemory() / (1024 * 1024);
            return new ResourceBurnerRamStatus(
                    this.growing,
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

    private void grow() {
        try {
            var chunk = new byte[CHUNK_SIZE_MB * 1024 * 1024];
            ThreadLocalRandom.current().nextBytes(chunk); // touch every page so RSS actually grows
            this.retained.add(chunk);
            var status = this.getStatus();
            LOGGER.info("Resource Burner RAM — chunk #{} retained, total: {}MB ({}% of max heap), heap used: {}MB/{}MB ({}%)", status.chunks(), status.retainedMB(), status.retainedPercentage(), status.heapUsedMB(), status.heapMaxMB(), status.heapUsedPercentage());
        } catch (OutOfMemoryError error) {
            this.growing = false;
            var status = this.getStatus();
            LOGGER.warn("Resource Burner RAM — heap exhausted, growth stopped, retained: {}MB ({}% of max heap), heap used: {}MB/{}MB ({}%)", status.retainedMB(), status.retainedPercentage(), status.heapUsedMB(), status.heapMaxMB(), status.heapUsedPercentage());
        }
    }
}
