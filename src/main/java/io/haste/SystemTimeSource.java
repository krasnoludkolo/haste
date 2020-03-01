package io.haste;

import java.time.Clock;
import java.time.LocalDateTime;

final class SystemTimeSource implements TimeSource {

    private final Clock clock = Clock.systemDefaultZone();

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
