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
         * @return {@link io.haste.ScheduledExecutorServiceWithMovableTime} instance with fixed clock with current time
         */
        public static ScheduledExecutorServiceWithMovableTime withFixedClockFromNow() {
            return new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        }

        /**
         * @param clock source of 'now'.
         * @return {@link io.haste.BlockingScheduledExecutionService} instance with fixed clock from given clock
         */
        public static ScheduledExecutorServiceWithMovableTime withFixedClock(Clock clock) {
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
        public static io.haste.TimeSource systemTimeSource() {
            return new SystemTimeSource();
        }


        /**
         * @return Instance with fixed clock with current time
         */
        public static MovableTimeSource withFixedClockFromNow() {
            return new StandaloneMovableTimeSource(Clock.systemDefaultZone());
        }

        /**
         * @param clock source of 'now'.
         * @return Instance with fixed clock from given clock
         */
        public static MovableTimeSource withFixedClock(Clock clock) {
            return new StandaloneMovableTimeSource(clock);
        }

    }

}
