package jbst.server.rb.domain;

public record ResourceBurnerStatus(
        ResourceBurnerCpuStatus cpu,
        ResourceBurnerRamStatus ram
) {
}
