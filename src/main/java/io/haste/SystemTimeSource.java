package io.haste;

import java.time.Clock;
import java.time.LocalDateTime;

public final class SystemTimeSource implements TimeSource {

    private Clock clock = Clock.systemDefaultZone();

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

}
