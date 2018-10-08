package io.haste;

import java.time.*;
import java.util.PriorityQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TestTimeService implements TimeService {

    private PriorityQueue<RunnableScheduledFuture> scheduledFutures = new PriorityQueue<>();
    private Clock clock;

    static TestTimeService withClockOf(Instant instant, ZoneId zoneId) {
        return new TestTimeService(instant, zoneId);
    }

    static TestTimeService withDefaultClock() {
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();
        return new TestTimeService(instant, zoneId);
    }

    private TestTimeService(Instant instant, ZoneId zoneId) {
        clock = Clock.fixed(instant, zoneId);
    }


    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    @Override
    public ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        ScheduledFutureWithRunnable scheduledFuture = new ScheduledFutureWithRunnable(delay, timeUnit, runnable);
        scheduledFutures.add(scheduledFuture);
        return scheduledFuture;
    }

    public void hackIntoFuture(long offset, TimeUnit timeUnit) {
        long offsetInNano = timeUnit.toNanos(offset);
        clock = Clock.offset(clock, Duration.ofNanos(offsetInNano));

        while (!scheduledFutures.isEmpty() && scheduledFutures.peek().getDelay(TimeUnit.NANOSECONDS) <= 0) {
            RunnableScheduledFuture scheduledFuture = scheduledFutures.poll();
            if (!scheduledFuture.isCancelled()) {
                scheduledFuture.run();
            }
        }
    }


    private final class ScheduledFutureWithRunnable implements RunnableScheduledFuture<Object> {

        private final LocalDateTime jobTime;
        private Runnable runnable;
        private boolean canceled;
        private boolean done;

        private ScheduledFutureWithRunnable(long delay, TimeUnit timeUnit, Runnable runnable) {
            jobTime = LocalDateTime.now(clock).plusNanos(timeUnit.toNanos(delay));
            this.canceled = false;
            this.done = false;
            this.runnable = runnable;
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            Duration d = Duration.between(LocalDateTime.now(clock), jobTime);
            return TimeUnit.NANOSECONDS.convert(d.toNanos(), timeUnit);
        }

        @Override
        public int compareTo(Delayed delayed) {
            return Long.compare(getDelay(TimeUnit.NANOSECONDS), delayed.getDelay(TimeUnit.NANOSECONDS));
        }

        @Override
        public boolean cancel(boolean b) {
            if (done) {
                return false;
            }
            canceled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return canceled;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public Object get() {
            return new Object();
        }

        @Override
        public Object get(long offset, TimeUnit timeUnit) {
            return new Object();
        }

        @Override
        public void run() {
            runnable.run();
            done = true;
        }

        @Override
        public boolean isPeriodic() {
            return false;
        }
    }
}

