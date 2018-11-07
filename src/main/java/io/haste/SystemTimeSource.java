package io.haste;

import java.time.Clock;
import java.time.LocalDateTime;

final class SystemTimeSource implements TimeSource {

    private Clock clock = Clock.systemDefaultZone();

    SystemTimeSource() {
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

}
