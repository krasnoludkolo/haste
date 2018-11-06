package io.haste;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BlockingScheduledExecutionService extends BlockingExecutorService implements ScheduledExecutorService, TimeSource {

    private static final Logger LOGGER = Logger.getLogger(BlockingScheduledExecutionService.class.getName());
    private PriorityQueue<AbstractRunnableScheduledFuture> scheduledFutures = new PriorityQueue<>();

    private Clock clock;

    public static BlockingScheduledExecutionService withFixedClockFromNow() {
        return new BlockingScheduledExecutionService(Clock.systemDefaultZone());
    }

    public static BlockingScheduledExecutionService withFixedClock(Clock clock) {
        return new BlockingScheduledExecutionService(clock);
    }

    private BlockingScheduledExecutionService(Clock clock) {
        this.clock = Clock.fixed(clock.instant(), clock.getZone());
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
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

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
        PeriodicScheduledFutureWithRunnable scheduledFuture = new PeriodicScheduledFutureWithRunnable(runnable, initialDelay, timeUnit, period);
        scheduledFutures.add(scheduledFuture);
        return scheduledFuture;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public void advanceTimeBy(long delayTime, TimeUnit timeUnit) {
        long remainingOffsetInNano = timeUnit.toNanos(delayTime);

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

        final LocalDateTime scheduledTime;
        final long delayInNanos;
        boolean canceled;
        boolean done;

        private AbstractRunnableScheduledFuture(long delay, TimeUnit timeUnit) {
            scheduledTime = LocalDateTime.now(clock);
            delayInNanos = timeUnit.toNanos(delay);
            this.canceled = false;
            this.done = false;
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            Duration d = Duration.between(LocalDateTime.now(clock), scheduledTime.plusNanos(delayInNanos));
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

        Runnable runnable;

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

        Callable<V> callable;
        V value;

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

    class PeriodicScheduledFutureWithRunnable extends ScheduledFutureWithRunnable {

        long periodic;
        TimeUnit periodicTimeUnit;

        private PeriodicScheduledFutureWithRunnable(Runnable runnable, long delay, TimeUnit timeUnit, long periodic) {
            super(delay, timeUnit, runnable);
            this.periodic = periodic;
            this.periodicTimeUnit = timeUnit;
        }

        @Override
        public void run() {
            scheduleAtFixedRate(this.runnable, periodic, periodic, periodicTimeUnit);
            super.run();
        }

        @Override
        public boolean isPeriodic() {
            return true;
        }
    }

}
