package io.haste;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class BlockingScheduledExecutionServiceTest {


    private static final Runnable EMPTY_RUNNABLE = () -> {
    };

    private static final Callable<Integer> RETURN_ONE_CALLABLE = () -> 1;

    @Test
    void shouldExecuteAllScheduledJobs() throws ExecutionException, InterruptedException {
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClockFromNow();

        ScheduledFuture<Integer> schedule1 = executorService.schedule(RETURN_ONE_CALLABLE, 1, TimeUnit.SECONDS);
        ScheduledFuture schedule2 = executorService.schedule(EMPTY_RUNNABLE, 2, TimeUnit.SECONDS);
        ScheduledFuture schedule3 = executorService.schedule(EMPTY_RUNNABLE, 3, TimeUnit.SECONDS);
        ScheduledFuture schedule4 = executorService.schedule(EMPTY_RUNNABLE, 5, TimeUnit.SECONDS);

        executorService.advanceTimeBy(4, TimeUnit.SECONDS);

        assertEquals(Integer.valueOf(1), schedule1.get());
        assertTrue(schedule1.isDone());
        assertTrue(schedule2.isDone());
        assertTrue(schedule3.isDone());
        assertFalse(schedule4.isDone());
    }

    @Test
    void shouldNowReturnTimeWithOffset() {
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant, zoneId);
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClock(clock);

        executorService.advanceTimeBy(1, TimeUnit.HOURS);

        LocalDateTime now = executorService.now();

        LocalDateTime expected = LocalDateTime.now(clock).plusHours(1);
        assertEquals(expected, now);
    }

    @Test
    void shouldNotRunCanceledJobs() throws ExecutionException, InterruptedException {
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClockFromNow();

        ScheduledFuture schedule1 = executorService.schedule(RETURN_ONE_CALLABLE, 1, TimeUnit.HOURS);
        schedule1.cancel(true);
        executorService.advanceTimeBy(4, TimeUnit.HOURS);

        assertEquals(null, schedule1.get());
    }

    @Test
    void shouldRunOnceScheduledJob() {
        ObjectWithInteger objectWithInteger = new ObjectWithInteger();
        AddRunnable runnable = new AddRunnable(objectWithInteger);
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClockFromNow();

        executorService.schedule(runnable, 1, TimeUnit.SECONDS);
        executorService.advanceTimeBy(4, TimeUnit.SECONDS);
        executorService.advanceTimeBy(4, TimeUnit.SECONDS);

        assertEquals(1, objectWithInteger.getA());
    }

    @Test
    void shouldChangeInternalClockToEveryScheduledJobTime() {
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClockFromNow();
        ObjectWithData object = new ObjectWithData(executorService.now().plusHours(3));
        IsAfterRunnable runnable = new IsAfterRunnable(executorService, object);

        executorService.schedule(EMPTY_RUNNABLE, 1, TimeUnit.HOURS);
        executorService.schedule(runnable, 2, TimeUnit.HOURS);

        executorService.advanceTimeBy(3, TimeUnit.HOURS);

        assertTrue(object.isAfter);
    }

    @Test
    void shouldGetValueFromScheduledCallable() throws ExecutionException, InterruptedException {
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClockFromNow();
        ScheduledFuture<Integer> schedule = executorService.schedule(RETURN_ONE_CALLABLE, 1, TimeUnit.HOURS);

        executorService.advanceTimeBy(2, TimeUnit.HOURS);

        assertEquals(1, schedule.get().intValue());
    }

    @Test
    void shouldScheduledFutureBeDoneAfterExecuteCallable() {
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClockFromNow();
        ScheduledFuture<Integer> schedule = executorService.schedule(RETURN_ONE_CALLABLE, 1, TimeUnit.HOURS);

        executorService.advanceTimeBy(2, TimeUnit.HOURS);

        assertTrue(schedule.isDone());
    }

    @Test
    void shouldRunPeriodicTaskWithDelay() {
        ObjectWithInteger objectWithInteger = new ObjectWithInteger();
        AddRunnable runnable = new AddRunnable(objectWithInteger);
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClockFromNow();

        executorService.scheduleAtFixedRate(runnable, 1, 3, TimeUnit.SECONDS);
        executorService.advanceTimeBy(2, TimeUnit.SECONDS);

        assertEquals(1, objectWithInteger.getA());
    }

    @Test
    void shouldPeriodicallyRunPeriodicTaskWithDelay() {
        ObjectWithInteger objectWithInteger = new ObjectWithInteger();
        AddRunnable runnable = new AddRunnable(objectWithInteger);
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClock(Clock.fixed(Instant.EPOCH, ZoneId.systemDefault()));

        executorService.scheduleAtFixedRate(runnable, 1, 3, TimeUnit.NANOSECONDS);
        executorService.advanceTimeBy(11, TimeUnit.NANOSECONDS);

        assertEquals(4, objectWithInteger.getA());
    }

    @Test
    void shouldPeriodicallyRunPeriodicTaskWithFixedDelay() {
        ObjectWithInteger objectWithInteger = new ObjectWithInteger();
        AddRunnable runnable = new AddRunnable(objectWithInteger);
        BlockingScheduledExecutionService executorService = BlockingScheduledExecutionService.withFixedClock(Clock.fixed(Instant.EPOCH, ZoneId.systemDefault()));

        executorService.scheduleWithFixedDelay(runnable, 1, 3, TimeUnit.NANOSECONDS);
        executorService.advanceTimeBy(11, TimeUnit.NANOSECONDS);

        assertEquals(4, objectWithInteger.getA());
    }

    class ObjectWithInteger {
        private int a = 0;

        void add() {
            a++;
        }

        int getA() {
            return a;
        }
    }

    class AddRunnable implements Runnable {

        private BlockingScheduledExecutionServiceTest.ObjectWithInteger objectWithInteger;

        AddRunnable(BlockingScheduledExecutionServiceTest.ObjectWithInteger objectWithInteger) {
            this.objectWithInteger = objectWithInteger;
        }

        @Override
        public void run() {
            objectWithInteger.add();
        }
    }

    class ObjectWithData {
        private LocalDateTime localDateTime;
        private boolean isAfter;

        ObjectWithData(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }
    }

    class IsAfterRunnable implements Runnable {

        private BlockingScheduledExecutionService service;
        private ObjectWithData object;

        IsAfterRunnable(BlockingScheduledExecutionService service, ObjectWithData object) {
            this.service = service;
            this.object = object;
        }

        @Override
        public void run() {
            if (object.localDateTime.isAfter(service.now())) {
                object.isAfter = true;
            }
        }
    }


}