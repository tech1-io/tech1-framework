package jbst.server.rb.domain;

public record ResourceBurnerCpuStatus(
        boolean growing,
        long threads,
        int availableProcessors
) {
}
