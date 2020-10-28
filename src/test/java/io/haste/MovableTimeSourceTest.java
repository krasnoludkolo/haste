package io.haste;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovableTimeSourceTest {

    @Test
    void shouldNowReturnTimeWithOffsetWhenMovedByTimeUnit() {
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant, zoneId);
        MovableTimeSource timeSource = Haste.TimeSource.withFixedClock(clock);

        timeSource.advanceTimeBy(1, TimeUnit.HOURS);

        var now = timeSource.now();

        var expected = ZonedDateTime.now(clock).plusHours(1);
        assertEquals(expected, now);
    }

    @Test
    void shouldNowReturnTimeWithOffsetWhenMovedByDuration() {
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant, zoneId);
        MovableTimeSource timeSource = Haste.TimeSource.withFixedClock(clock);

        timeSource.advanceTimeBy(Duration.ofHours(1));

        var now = timeSource.now();

        var expected = ZonedDateTime.now(clock).plusHours(1);
        assertEquals(expected, now);
    }

    @Test
    void shouldNotThrowSOWhenAdvancingTimeManyTimes() {
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant, zoneId);
        MovableTimeSource timeSource = Haste.TimeSource.withFixedClock(clock);

        for (int i = 0; i < 10000; i++) {
            timeSource.advanceTimeBy(1, TimeUnit.MINUTES);
        }
    }

    // this test was failing in version 0.3.0 if system default timeZone is not Europe/Berlin
    @Test
    void shouldPreserveTimeZoneWhileAdvancingTime() {
        Instant instant = Instant.parse("2020-10-24T22:22:03.00Z");
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        Clock clock = Clock.fixed(instant, zoneId);
        MovableTimeSource timeSource = Haste.TimeSource.withFixedClock(clock);

        timeSource.advanceTimeBy(6, TimeUnit.HOURS);

        var actual = timeSource.now().getZone().toString();

        assertEquals("Europe/Berlin", actual);
    }

}
