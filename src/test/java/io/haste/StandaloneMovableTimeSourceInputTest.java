package io.haste;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StandaloneMovableTimeSourceInputTest {

    @Test
    void shouldThrowExceptionWhenPassingNullClock() {
        assertThrows(
                NullPointerException.class,
                () -> new StandaloneMovableTimeSource(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullDurationToAdvanceTimeBy() {
        var timeSource = new StandaloneMovableTimeSource(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> timeSource.advanceTimeBy(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullTimeUnitsToAdvanceTimeBy() {
        var timeSource = new StandaloneMovableTimeSource(Clock.systemDefaultZone());
        assertThrows(
                NullPointerException.class,
                () -> timeSource.advanceTimeBy(1, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNegativeDelayToAdvanceTimeBy() {
        var timeSource = new StandaloneMovableTimeSource(Clock.systemDefaultZone());
        assertThrows(
                IllegalArgumentException.class,
                () -> timeSource.advanceTimeBy(-1, TimeUnit.MINUTES)
        );
    }

}