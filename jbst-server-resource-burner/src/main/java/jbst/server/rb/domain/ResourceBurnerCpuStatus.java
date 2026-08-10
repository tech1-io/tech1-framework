package jbst.server.rb.domain;

import java.math.BigDecimal;

public record ResourceBurnerCpuStatus(
        boolean growing,
        int everySeconds,
        int threadsPerStep,
        long threads,
        int availableProcessors,
        BigDecimal threadsPercentage,
        BigDecimal systemCpuLoadPercentage
) {
}
