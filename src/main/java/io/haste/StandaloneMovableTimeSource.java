package io.haste;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class StandaloneMovableTimeSource implements MovableTimeSource {

    private Clock clock;

    StandaloneMovableTimeSource(Clock clock) {
        Objects.requireNonNull(clock);
        this.clock = Clock.fixed(clock.instant(), clock.getZone());
    }

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }

    public void advanceTimeBy(long delayTime, TimeUnit timeUnit) {
        if (delayTime < 0) throw new IllegalArgumentException();
        Objects.requireNonNull(timeUnit);

        Duration duration = Duration.ofNanos(timeUnit.toNanos(delayTime));
        advanceTimeBy(duration);
    }

    public void advanceTimeBy(Duration duration) {
        clock = Clock.fixed(Clock.offset(clock, duration).instant(), clock.getZone());
    }

    @Override
    public long currentTimeMillis() {
        return clock.millis();
    }

}
