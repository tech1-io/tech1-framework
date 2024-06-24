package io.tech1.framework.domain.time;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.tech1.framework.domain.tuples.Tuple2;
import io.tech1.framework.foundation.utilities.time.TimestampUtility;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

import static io.tech1.framework.foundation.utilities.strings.StringUtility.isNullOrBlank;
import static io.tech1.framework.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;
import static java.util.concurrent.TimeUnit.*;

// Lombok
@Getter
@EqualsAndHashCode
public class TimeAgo {
    public static final List<Tuple2<Long, String>> UNITS = List.of(
            new Tuple2<>(DAYS.toMillis(365), "year"),
            new Tuple2<>(DAYS.toMillis(30), "month"),
            new Tuple2<>(DAYS.toMillis(1), "day"),
            new Tuple2<>(HOURS.toMillis(1), "hour"),
            new Tuple2<>(MINUTES.toMillis(1), "minute"),
            new Tuple2<>(SECONDS.toMillis(1), "second")
    );

    @JsonValue
    private final String value;

    @JsonCreator
    public TimeAgo(long timestamp) {
        var duration = getCurrentTimestamp() - timestamp;
        var agoSb = new StringBuilder();
        for (Tuple2<Long, String> unit : UNITS) {
            var current = unit.a();
            var temp = duration / current;
            if (temp > 0) {
                agoSb.append(temp);
                agoSb.append(" ");
                agoSb.append(unit.b());
                agoSb.append(temp != 1 ? "s" : "");
                agoSb.append(" ");
                agoSb.append("ago");
                break;
            }
        }
        var ago = agoSb.toString();
        if(isNullOrBlank(ago)) {
            this.value = "just now";
        } else {
            this.value = ago;
        }
    }

    public static TimeAgo of(long timestamp) {
        return new TimeAgo(timestamp);
    }

    public static TimeAgo justNow() {
        return new TimeAgo(TimestampUtility.getCurrentTimestamp());
    }

    @Override
    public String toString() {
        return this.value;
    }
}
