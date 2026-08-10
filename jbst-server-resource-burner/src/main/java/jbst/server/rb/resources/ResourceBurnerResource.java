package jbst.server.rb.resources;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.server.rb.components.ResourceBurnerCPU;
import jbst.server.rb.components.ResourceBurnerRAM;
import jbst.server.rb.domain.ResourceBurnerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Swagger
@Tag(name = "[jbst] Resource Burner API")
// Spring
@RestController
@RequestMapping("/resource-burner")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceBurnerResource {

    // Components
    private final ResourceBurnerCPU resourceBurnerCPU;
    private final ResourceBurnerRAM resourceBurnerRAM;

    @GetMapping("/status")
    public ResourceBurnerStatus status() {
        return this.getStatus();
    }

    // =================================================================================================================
    // CPU
    // =================================================================================================================

    @PostMapping("/cpu/start")
    public ResourceBurnerStatus cpuStart(
            @Parameter(description = "Growth step interval, seconds (1-3600)")
            @RequestParam(defaultValue = "" + ResourceBurnerCPU.DEFAULT_EVERY_SECONDS) int everySeconds,
            @Parameter(description = "Burner threads added per step (1-256)")
            @RequestParam(defaultValue = "" + ResourceBurnerCPU.DEFAULT_THREADS_PER_STEP) int threads
    ) {
        this.startCpu(everySeconds, threads);
        return this.getStatus();
    }

    @PostMapping("/cpu/stop")
    public ResourceBurnerStatus cpuStop() {
        this.resourceBurnerCPU.stop();
        return this.getStatus();
    }

    @PostMapping("/cpu/clean")
    public ResourceBurnerStatus cpuClean() {
        this.resourceBurnerCPU.clean();
        return this.getStatus();
    }

    // =================================================================================================================
    // RAM
    // =================================================================================================================

    @PostMapping("/ram/start")
    public ResourceBurnerStatus ramStart(
            @Parameter(description = "Growth step interval, seconds (1-3600)")
            @RequestParam(defaultValue = "" + ResourceBurnerRAM.DEFAULT_EVERY_SECONDS) int everySeconds,
            @Parameter(description = "Heap chunk retained per step, MB (1-1024)")
            @RequestParam(defaultValue = "" + ResourceBurnerRAM.DEFAULT_CHUNK_MB) int chunkMB
    ) {
        this.startRam(everySeconds, chunkMB);
        return this.getStatus();
    }

    @PostMapping("/ram/stop")
    public ResourceBurnerStatus ramStop() {
        this.resourceBurnerRAM.stop();
        return this.getStatus();
    }

    @PostMapping("/ram/clean")
    public ResourceBurnerStatus ramClean() {
        this.resourceBurnerRAM.clean();
        return this.getStatus();
    }

    // =================================================================================================================
    // CPU + RAM
    // =================================================================================================================

    @PostMapping("/start")
    public ResourceBurnerStatus start(
            @Parameter(description = "Growth step interval, seconds (1-3600)")
            @RequestParam(defaultValue = "" + ResourceBurnerCPU.DEFAULT_EVERY_SECONDS) int everySeconds,
            @Parameter(description = "Burner threads added per step (1-256)")
            @RequestParam(defaultValue = "" + ResourceBurnerCPU.DEFAULT_THREADS_PER_STEP) int threads,
            @Parameter(description = "Heap chunk retained per step, MB (1-1024)")
            @RequestParam(defaultValue = "" + ResourceBurnerRAM.DEFAULT_CHUNK_MB) int chunkMB
    ) {
        this.startCpu(everySeconds, threads);
        this.startRam(everySeconds, chunkMB);
        return this.getStatus();
    }

    @PostMapping("/stop")
    public ResourceBurnerStatus stop() {
        this.resourceBurnerCPU.stop();
        this.resourceBurnerRAM.stop();
        return this.getStatus();
    }

    @PostMapping("/clean")
    public ResourceBurnerStatus clean() {
        this.resourceBurnerCPU.clean();
        this.resourceBurnerRAM.clean();
        return this.getStatus();
    }

    private void startCpu(int everySeconds, int threads) {
        requireRange("everySeconds", everySeconds, ResourceBurnerCPU.MIN_EVERY_SECONDS, ResourceBurnerCPU.MAX_EVERY_SECONDS);
        requireRange("threads", threads, ResourceBurnerCPU.MIN_THREADS_PER_STEP, ResourceBurnerCPU.MAX_THREADS_PER_STEP);
        this.resourceBurnerCPU.start(everySeconds, threads);
    }

    private void startRam(int everySeconds, int chunkMB) {
        requireRange("everySeconds", everySeconds, ResourceBurnerRAM.MIN_EVERY_SECONDS, ResourceBurnerRAM.MAX_EVERY_SECONDS);
        requireRange("chunkMB", chunkMB, ResourceBurnerRAM.MIN_CHUNK_MB, ResourceBurnerRAM.MAX_CHUNK_MB);
        this.resourceBurnerRAM.start(everySeconds, chunkMB);
    }

    private static void requireRange(String name, int value, int min, int max) {
        if (value < min || value > max) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "%s must be in range [%d, %d], got: %d".formatted(name, min, max, value));
        }
    }

    private ResourceBurnerStatus getStatus() {
        return new ResourceBurnerStatus(
                this.resourceBurnerCPU.getStatus(),
                this.resourceBurnerRAM.getStatus()
        );
    }
}
