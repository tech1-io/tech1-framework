package io.tech1.framework.domain.tuples;

import io.tech1.framework.domain.constants.StringConstants;
import lombok.Data;

// Lombok
@Data
public class TupleExceptionDetails {
    private final boolean ok;
    private final String message;

    public static TupleExceptionDetails ok() {
        return new TupleExceptionDetails(
                true,
                StringConstants.EMPTY
        );
    }

    public static TupleExceptionDetails exception(
            String message
    ) {
        return new TupleExceptionDetails(
                false,
                message
        );
    }
}
