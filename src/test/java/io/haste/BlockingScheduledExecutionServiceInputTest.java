package io.haste;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BlockingScheduledExecutionServiceInputTest {

    private final static Runnable EMPTY_RUNNABLE = () -> {
    };

    private final static Callable<Integer> EMPTY_CALLABLE = () -> 1;

    @Test
    void shouldThrowExceptionWhenPassingNullClock() {
        assertThrows(
                NullPointerException.class,
                () -> new BlockingScheduledExecutionService(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullTimeUnitToScheduleWithRunnable() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.schedule(EMPTY_RUNNABLE, 1, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativeDelayToScheduleWithRunnable() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> service.schedule(EMPTY_RUNNABLE, -1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullTimeUnitToScheduleWithCallable() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.schedule(EMPTY_CALLABLE, 1, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativeDelayToScheduleWithCallable() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> service.schedule(EMPTY_CALLABLE, -1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullRunnableToScheduleWithFixRate() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.scheduleAtFixedRate(null, 1, 1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativeInitialDelayToScheduleWithFixRate() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> service.scheduleAtFixedRate(EMPTY_RUNNABLE, -1, 1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativePeriodToScheduleWithFixRate() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> service.scheduleAtFixedRate(EMPTY_RUNNABLE, 1, -1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullTimeUnitsToScheduleWithFixRate() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.scheduleAtFixedRate(EMPTY_RUNNABLE, 1, 1, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullRunnableToScheduleWithFixedDelay() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.scheduleWithFixedDelay(null, 1, 1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativeInitialDelayToScheduleWithFixedDelay() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> service.scheduleWithFixedDelay(EMPTY_RUNNABLE, -1, 1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativePeriodToScheduleWithFixedDelay() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> service.scheduleWithFixedDelay(EMPTY_RUNNABLE, 1, -1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullTimeUnitsToScheduleWithFixedDelay() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.scheduleWithFixedDelay(EMPTY_RUNNABLE, 1, 1, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullTimeUnitsToAdvanceTimeBy() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.advanceTimeBy(1, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullDurationToAdvanceTimeBy() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> service.advanceTimeBy(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativeDelayToAdvanceTimeBy() {
        var service = new BlockingScheduledExecutionService(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> service.advanceTimeBy(-1, TimeUnit.MINUTES)
        );
    }

}
