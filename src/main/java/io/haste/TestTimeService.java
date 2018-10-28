package io.haste;

import java.time.*;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestTimeService implements TimeService {

    private static final Logger LOGGER = Logger.getLogger(TestTimeService.class.getName());


    private PriorityQueue<AbstractRunnableScheduledFuture> scheduledFutures = new PriorityQueue<>();
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

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit timeUnit) {
        ScheduledFutureWithCallable<V> scheduledFuture = new ScheduledFutureWithCallable<>(delay, timeUnit, callable);
        scheduledFutures.add(scheduledFuture);
        return scheduledFuture;
    }

    public void hackIntoFuture(long offset, TimeUnit timeUnit) {
        long remainingOffsetInNano = timeUnit.toNanos(offset);

        while (!scheduledFutures.isEmpty() && nextTaskIsInRange(remainingOffsetInNano)) {
            RunnableScheduledFuture task = scheduledFutures.poll();
            long delay = task.getDelay(TimeUnit.NANOSECONDS);
            updateClock(delay);
            remainingOffsetInNano -= delay;
            runTask(task);
        }

        updateClock(remainingOffsetInNano);
    }

    private boolean nextTaskIsInRange(long remainingOffsetInNano) {
        return scheduledFutures.peek().getDelay(TimeUnit.NANOSECONDS) <= remainingOffsetInNano;
    }

    private void runTask(RunnableScheduledFuture task) {
        if (!task.isCancelled()) {
            task.run();
        }
    }

    private void updateClock(long delay) {
        clock = Clock.offset(clock, Duration.ofNanos(delay));
    }


    private abstract class AbstractRunnableScheduledFuture<V> implements RunnableScheduledFuture<V> {

        final LocalDateTime taskTime;
        boolean canceled;
        boolean done;

        private AbstractRunnableScheduledFuture(long delay, TimeUnit timeUnit) {
            taskTime = LocalDateTime.now(clock).plusNanos(timeUnit.toNanos(delay));
            this.canceled = false;
            this.done = false;
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            Duration d = Duration.between(LocalDateTime.now(clock), taskTime);
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
        public boolean isPeriodic() {
            return false;
        }
    }

    private class ScheduledFutureWithRunnable extends AbstractRunnableScheduledFuture<Object> {

        private Runnable runnable;

        private ScheduledFutureWithRunnable(long delay, TimeUnit timeUnit, Runnable runnable) {
            super(delay, timeUnit);
            this.runnable = runnable;
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
    }

    private class ScheduledFutureWithCallable<V> extends AbstractRunnableScheduledFuture<V> {

        private Callable<V> callable;
        private V value;

        private ScheduledFutureWithCallable(long delay, TimeUnit timeUnit, Callable<V> callable) {
            super(delay, timeUnit);
            this.callable = callable;
        }

        @Override
        public V get() {
            return value;
        }

        @Override
        public V get(long offset, TimeUnit timeUnit) {
            return value;
        }

        @Override
        public void run() {
            try {
                value = callable.call();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e::getMessage);
            }
            done = true;
        }
    }

}

