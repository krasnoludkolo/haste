package io.haste;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface ScheduledExecutorServiceWithMovableTime extends MovableTimeSource, ScheduledExecutorService {


    /**
     * Move internal clock by given amount of time and trigger scheduled actions
     *
     * @param delayTime amount of time to move
     * @param timeUnit  time unit of delay parameter
     */
    @Override
    void advanceTimeBy(long delayTime, TimeUnit timeUnit);

    /**
     * Move internal clock by given duration and trigger scheduled actions
     *
     * @param duration amount of time to move
     */
    @Override
    void advanceTimeBy(Duration duration);
}
