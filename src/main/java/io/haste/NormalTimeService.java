package io.haste;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.*;

class NormalTimeService implements TimeService {

    private Clock clock;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    static TimeService withSystemDefaultZone() {
        return new NormalTimeService(Clock.systemDefaultZone());
    }

    private NormalTimeService(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    @Override
    public ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        return executor.schedule(runnable, delay, timeUnit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit timeUnit) {
        return executor.schedule(callable, delay, timeUnit);
    }

}
