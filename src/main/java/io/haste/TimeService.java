package io.haste;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface TimeService {

    LocalDateTime now();
    ScheduledFuture schedule(Runnable runnable, long l, TimeUnit timeUnit);

    default TimeService createNormal(){
        return NormalTimeService.withSystemDefaultZone();
    }
}
