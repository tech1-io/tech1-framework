package jbst.server.rb.domain;

public record ResourceBurnerRamStatus(
        boolean growing,
        int chunks,
        long retainedMB,
        long heapUsedMB,
        long heapMaxMB
) {
}
