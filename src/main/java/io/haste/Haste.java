package io.haste;

import java.time.Clock;

/**
 * Allows to create <b>Haste</b> classes
 */
public final class Haste {

    private Haste() {
    }

    /**
     * Allows to create {@link io.haste.BlockingScheduledExecutionService} instances
     */
    public static class ScheduledExecutionService {

        private ScheduledExecutionService() {
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

    /**
     * Allows to create {@link io.haste.TimeSource} instances
     */
    public static class TimeSource {

        private TimeSource() {
        }

        /**
         * @return instance of TimeSource based on system clock
         */
        static io.haste.TimeSource systemTimeSource() {
            return new SystemTimeSource();
        }


        /**
         * @return {@link io.haste.MovableTimeSource} instance with fixed clock with current time
         */
        public static MovableTimeSource withFixedClockFromNow() {
            return new MovableTimeSource(Clock.systemDefaultZone());
        }

        /**
         * @param clock source of 'now'.
         * @return {@link io.haste.MovableTimeSource} instance with fixed clock from given clock
         */
        public static MovableTimeSource withFixedClock(Clock clock) {
            return new MovableTimeSource(clock);
        }

    }

}
