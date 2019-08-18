package io.haste;

import java.time.LocalDateTime;

public interface TimeSource {

    /**
     * Provides the current date-time from clock
     *
     * @return current date-time
     */
    LocalDateTime now();


    /**
     * Provides the currentTimeMillis() from clock. See {@link System#currentTimeMillis()}
     *
     * @return current time in milliseconds
     */
    long currentTimeMillis();
}
