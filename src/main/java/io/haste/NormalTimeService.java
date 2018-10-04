package io.haste;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class NormalTimeService implements TimeService {

    private Clock clock;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    static TimeService withSystemDefaultZone(){
        return new NormalTimeService(Clock.systemDefaultZone());
    }

    static TimeService withClock(Clock clock){
        return new NormalTimeService(clock);
    }

    private NormalTimeService(Clock clock) {
        this.clock = clock;
    }


    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    @Override
    public ScheduledFuture schedule(Runnable runnable, long offset, TimeUnit timeUnit) {
        return executor.schedule(runnable,offset,timeUnit);
    }
}
