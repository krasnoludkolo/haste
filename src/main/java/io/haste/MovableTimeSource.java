package io.haste;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface MovableTimeSource extends TimeSource {

    /**
     * Move internal clock by given amount of time
     *
     * @param delayTime amount of time to move
     * @param timeUnit  time unit of delay parameter
     */
    void advanceTimeBy(long delayTime, TimeUnit timeUnit);

    /**
     * Move internal clock by given duration
     *
     * @param duration amount of time to move
     */
    void advanceTimeBy(Duration duration);

}
