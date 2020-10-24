package io.haste;

import java.time.ZonedDateTime;

public interface TimeSource {

    /**
     * Provides the current date-time from clock
     *
     * @return current date-time
     */
    ZonedDateTime now();


    /**
     * Provides the currentTimeMillis() from clock. See {@link System#currentTimeMillis()}
     *
     * @return current time in milliseconds
     */
    long currentTimeMillis();
}
