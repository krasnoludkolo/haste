package io.haste;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class CurrentTimeMillsTest {

    @Test
    void shouldReturnCorrectMillisTime() {
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant, zoneId);
        MovableTimeSource timeSource = Haste.TimeSource.withFixedClock(clock);

        long currentTime = timeSource.currentTimeMillis();

        assertEquals(0, currentTime);
    }


}
