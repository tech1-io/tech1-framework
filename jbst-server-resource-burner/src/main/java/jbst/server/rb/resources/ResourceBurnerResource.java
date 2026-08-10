package jbst.server.rb.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.server.rb.components.ResourceBurnerCPU;
import jbst.server.rb.components.ResourceBurnerRAM;
import jbst.server.rb.domain.ResourceBurnerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResourceBurnerStatus cpuStart() {
        this.resourceBurnerCPU.start();
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
    public ResourceBurnerStatus ramStart() {
        this.resourceBurnerRAM.start();
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
    public ResourceBurnerStatus start() {
        this.resourceBurnerCPU.start();
        this.resourceBurnerRAM.start();
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

    private ResourceBurnerStatus getStatus() {
        return new ResourceBurnerStatus(
                this.resourceBurnerCPU.getStatus(),
                this.resourceBurnerRAM.getStatus()
        );
    }
}
