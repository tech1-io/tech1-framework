package jbst.server.rb.domain;

import java.math.BigDecimal;

public record ResourceBurnerRamStatus(
        boolean growing,
        int chunks,
        long retainedMB,
        BigDecimal retainedPercentage,
        long heapUsedMB,
        long heapMaxMB,
        BigDecimal heapUsedPercentage
) {
}
