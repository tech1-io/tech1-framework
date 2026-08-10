package jbst.server.rb.components;

import jakarta.annotation.PreDestroy;
import jbst.server.rb.domain.ResourceBurnerCpuStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Burns CPU by spinning daemon platform threads in a busy math loop.
 * <p>
 * Lifecycle: {@code start()} — begin growing (one extra burner thread every 10 seconds),
 * {@code stop()} — freeze growth but keep the current load (plateau),
 * {@code clean()} — stop growth and terminate all burner threads.
 */
@Slf4j
@Component
public class ResourceBurnerCPU {

    private final AtomicInteger counter = new AtomicInteger();
    private final List<Thread> threads = new CopyOnWriteArrayList<>();
    // captured by each burner thread; clean() flips the current flag and replaces it
    private volatile AtomicBoolean burning = new AtomicBoolean(true);
    private volatile boolean growing = false;
    @SuppressWarnings("unused")
    private volatile double sink; // critical: otherwise JIT eliminates the busy loop

    @Scheduled(fixedRate = 10_000)
    public void tick() {
        if (this.growing) {
            this.addBurner();
        }
    }

    public synchronized void start() {
        if (!this.growing) {
            this.growing = true;
            this.addBurner();
        }
    }

    public synchronized void stop() {
        this.growing = false;
    }

    public synchronized void clean() {
        this.growing = false;
        this.burning.set(false);
        this.burning = new AtomicBoolean(true);
        this.threads.clear();
        LOGGER.info("Resource Burner CPU — cleaned, all burner threads terminating");
    }

    public ResourceBurnerCpuStatus getStatus() {
        return new ResourceBurnerCpuStatus(
                this.growing,
                this.threads.stream().filter(Thread::isAlive).count(),
                Runtime.getRuntime().availableProcessors()
        );
    }

    @PreDestroy
    void destroy() {
        this.clean();
    }

    private void addBurner() {
        var flag = this.burning;
        var id = this.counter.incrementAndGet();
        var thread = Thread.ofPlatform()
                .daemon()
                .name("resource-burner-cpu-" + id)
                .start(() -> {
                    double x = 0;
                    while (flag.get()) {
                        x += Math.sqrt(ThreadLocalRandom.current().nextDouble());
                    }
                    this.sink = x;
                });
        this.threads.add(thread);
        LOGGER.info("Resource Burner CPU — burner thread #{} started, live threads: {}", id, this.getStatus().threads());
    }
}
