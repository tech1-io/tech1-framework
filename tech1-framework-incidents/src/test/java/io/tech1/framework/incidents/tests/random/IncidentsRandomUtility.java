package io.tech1.framework.incidents.tests.random;

import io.tech1.framework.incidents.domain.Incident;
import io.tech1.framework.incidents.domain.throwable.ThrowableIncident;
import lombok.experimental.UtilityClass;

import static io.tech1.framework.domain.utilities.random.RandomUtility.randomString;

@UtilityClass
public class IncidentsRandomUtility {

    public static Incident randomIncident() {
        var incident = new Incident();
        incident.add(randomString(), randomString());
        incident.add(randomString(), randomString());
        incident.add(randomString(), randomString());
        return incident;
    }

    public static ThrowableIncident randomThrowableIncident() {
        return ThrowableIncident.of(
                new Throwable(randomString())
        );
    }
}
