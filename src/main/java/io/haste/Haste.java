package io.haste;

import java.time.Clock;

public final class Haste {

    private Haste() {
    }

    /**
     * @return {@link io.haste.BlockingScheduledExecutionService} instance with fixed clock with current time
     */
    public static BlockingScheduledExecutionService withFixedClockFromNow() {
        return new BlockingScheduledExecutionService(Clock.systemDefaultZone());
    }

    /**
     * @param clock source of 'now'.
     * @return {@link io.haste.BlockingScheduledExecutionService} instance with fixed clock from given clock
     */
    public static BlockingScheduledExecutionService withFixedClock(Clock clock) {
        return new BlockingScheduledExecutionService(clock);
    }

}
