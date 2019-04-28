package io.haste;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

final class MovableTimeSource implements TimeSource {

    private Clock clock;

    MovableTimeSource(Clock clock) {
        this.clock = Clock.fixed(clock.instant(), clock.getZone());
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public void advanceTimeBy(long delayTime, TimeUnit timeUnit) {
        long nanos = timeUnit.toNanos(delayTime);
        clock = Clock.offset(clock, Duration.ofNanos(nanos));
    }

}
