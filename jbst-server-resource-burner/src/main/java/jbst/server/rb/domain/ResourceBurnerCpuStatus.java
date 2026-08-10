package jbst.server.rb.domain;

import java.math.BigDecimal;

public record ResourceBurnerCpuStatus(
        boolean growing,
        long threads,
        int availableProcessors,
        BigDecimal threadsPercentage,
        BigDecimal systemCpuLoadPercentage
) {
}
