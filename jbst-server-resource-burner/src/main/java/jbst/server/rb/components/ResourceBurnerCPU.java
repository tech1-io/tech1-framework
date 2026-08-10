package jbst.server.rb.components;

import com.sun.management.OperatingSystemMXBean;
import jakarta.annotation.PreDestroy;
import jbst.server.rb.domain.ResourceBurnerCpuStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static jbst.foundation.domain.tuples.TuplePercentage.progressTuplePercentage;
import static jbst.foundation.domain.numbers.JbstNumbers.scale;

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

    private static final OperatingSystemMXBean OS_MX_BEAN = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger counter = new AtomicInteger();
    private final List<Thread> threads = new CopyOnWriteArrayList<>();
    // captured by each burner thread; clean() flips the current flag and replaces it
    private volatile AtomicBoolean burning = new AtomicBoolean(true);
    private volatile boolean growing = false;
    @SuppressWarnings("unused")
    private volatile double sink; // critical: otherwise JIT eliminates the busy loop

    @Scheduled(fixedRate = 10_000)
    public void tick() {
        this.lock.lock();
        try {
            if (this.growing) {
                this.addBurner();
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
                this.addBurner();
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
            LOGGER.info("Resource Burner CPU — growth frozen, live threads: {}/{} cores ({}%), system CPU load: {}%", status.threads(), status.availableProcessors(), status.threadsPercentage(), status.systemCpuLoadPercentage());
        } finally {
            this.lock.unlock();
        }
    }

    public void clean() {
        this.lock.lock();
        try {
            this.growing = false;
            this.burning.set(false);
            this.burning = new AtomicBoolean(true);
            this.threads.clear();
            LOGGER.info("Resource Burner CPU — cleaned, all burner threads terminating, system CPU load: {}%", systemCpuLoadPercentage());
        } finally {
            this.lock.unlock();
        }
    }

    public ResourceBurnerCpuStatus getStatus() {
        var liveThreads = this.threads.stream().filter(Thread::isAlive).count();
        var availableProcessors = Runtime.getRuntime().availableProcessors();
        return new ResourceBurnerCpuStatus(
                this.growing,
                liveThreads,
                availableProcessors,
                progressTuplePercentage(liveThreads, availableProcessors).percentage(),
                systemCpuLoadPercentage()
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
        var status = this.getStatus();
        LOGGER.info("Resource Burner CPU — burner thread #{} started, live threads: {}/{} cores ({}%), system CPU load: {}%", id, status.threads(), status.availableProcessors(), status.threadsPercentage(), status.systemCpuLoadPercentage());
    }

    // system-wide CPU load: 0.00-100.00; -1 when the JVM cannot measure it (e.g. first call)
    private static BigDecimal systemCpuLoadPercentage() {
        var cpuLoad = OS_MX_BEAN.getCpuLoad();
        return cpuLoad >= 0 ? scale(BigDecimal.valueOf(cpuLoad * 100), 2) : BigDecimal.valueOf(-1);
    }
}
