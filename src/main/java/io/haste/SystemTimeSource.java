package io.haste;

import java.time.Clock;
import java.time.ZonedDateTime;

final class SystemTimeSource implements TimeSource {

    private final Clock clock = Clock.systemDefaultZone();

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
