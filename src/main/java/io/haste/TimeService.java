package io.haste;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface TimeService {

    /**
     *
     * Provides the current date-time from clock given during the creation
     *
     * @return current date-time
     */
    LocalDateTime now();

    /**
     *
     * Creates and execute action from given runnable that is run with given time delay
     *
     * @param runnable task to execute
     * @param delay time from now to task execution
     * @param timeUnit time unit of delay parameter
     * @return {@link ScheduledFuture} representing scheduled task
     */
    ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit timeUnit);


    /**
     * @return new instance of {@link io.haste.TimeService} with system clock
     */
    static TimeService createNormal(){
        return NormalTimeService.withSystemDefaultZone();
    }

    /**
     * @return new instance of {@link io.haste.TestTimeService} with fixed clock set to current time
     */
    static TestTimeService createTimeServiceForTestsWithCurrentTime(){
        return TestTimeService.withDefaultClock();
    }


    /**
     *
     * @param instance instance to create clock
     * @param zoneId zoneId to create clock
     * @return new instance of {@link io.haste.TestTimeService} with clock from given parameters
     */
    static TestTimeService createTimeServiceForTests(Instant instance, ZoneId zoneId){
        return TestTimeService.withClockOf(instance,zoneId);
    }

}
