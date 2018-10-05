package io.haste;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface TimeService {

    LocalDateTime now();
    ScheduledFuture schedule(Runnable runnable, long offset, TimeUnit timeUnit);

    static TimeService createNormal(){
        return NormalTimeService.withSystemDefaultZone();
    }

    static TimeService createNormalWithClock(Clock clock){
        return NormalTimeService.withClock(clock);
    }

    static TestTimeService createTimeServiceForTestsWithCurrentTime(){
        return TestTimeService.withDefaultClock();
    }

    static TestTimeService createTimeServiceForTests(Instant instance, ZoneId zoneId){
        return TestTimeService.withClockOf(instance,zoneId);
    }

}
