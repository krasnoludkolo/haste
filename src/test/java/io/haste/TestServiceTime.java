package io.haste;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TestServiceTime {

    @Test
    public void shouldExecuteAllScheduledJobs(){
        ObjectWithInteger objectWithInteger = new ObjectWithInteger(); // object with int value
        AddRunnable runnable = new AddRunnable(objectWithInteger); // add one to object value
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        TestTimeService timeService = TimeService.createTimeServiceForTests(instant,zoneId);

        ScheduledFuture schedule1 = timeService.schedule(runnable, 1, TimeUnit.SECONDS);
        ScheduledFuture schedule2 = timeService.schedule(runnable, 2, TimeUnit.SECONDS);
        ScheduledFuture schedule3 = timeService.schedule(runnable, 3, TimeUnit.SECONDS);
        ScheduledFuture schedule4 = timeService.schedule(runnable, 5, TimeUnit.SECONDS);

        timeService.hackIntoFuture(4,TimeUnit.SECONDS);

        assertEquals(3, objectWithInteger.getA());
        assertTrue(schedule1.isDone());
        assertTrue(schedule2.isDone());
        assertTrue(schedule3.isDone());
        assertFalse(schedule4.isDone());
    }

    @Test
    public void shouldCreateInstanceWithGivenClock(){
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant,zoneId);
        TestTimeService timeService = TimeService.createTimeServiceForTests(instant,zoneId);

        LocalDateTime now = timeService.now();
        
        assertEquals(LocalDateTime.now(clock),now);
    }
    
    @Test
    public void shouldNowReturnTimeWithOffset(){
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant,zoneId);
        TestTimeService timeService = TimeService.createTimeServiceForTests(instant,zoneId);

        timeService.hackIntoFuture(1,TimeUnit.HOURS);

        LocalDateTime now = timeService.now();

        LocalDateTime expected = LocalDateTime.now(clock).plusHours(1);
        assertEquals(expected,now);
    }

    @Test
    public void shouldNotRunCanceledJobs(){
        ObjectWithInteger objectWithInteger = new ObjectWithInteger();
        AddRunnable runnable = new AddRunnable(objectWithInteger);
        TestTimeService timeService = TestTimeService.withDefaultClock();

        ScheduledFuture schedule1 = timeService.schedule(runnable, 1, TimeUnit.HOURS);
        schedule1.cancel(true);
        timeService.hackIntoFuture(4,TimeUnit.HOURS);

        assertEquals(0, objectWithInteger.getA());
    }

    @Test
    public void shouldRunOnceScheduledJob(){
        ObjectWithInteger objectWithInteger = new ObjectWithInteger();
        AddRunnable runnable = new AddRunnable(objectWithInteger);
        TestTimeService timeService = TimeService.createTimeServiceForTestsWithCurrentTime();

        timeService.schedule(runnable, 1, TimeUnit.SECONDS);
        timeService.hackIntoFuture(4,TimeUnit.SECONDS);
        timeService.hackIntoFuture(4,TimeUnit.SECONDS);

        assertEquals(1, objectWithInteger.getA());
    }

    @Test
    public void shouldChangeInternalClockToEveryScheduledJobTime() {
        TestTimeService timeService = TimeService.createTimeServiceForTestsWithCurrentTime();
        ObjectWithData object = new ObjectWithData(timeService.now().plusHours(3));
        IsAfterRunnable runnable = new IsAfterRunnable(timeService, object);

        timeService.schedule(() -> {
        }, 1, TimeUnit.HOURS);
        timeService.schedule(runnable, 2, TimeUnit.HOURS);

        timeService.hackIntoFuture(3, TimeUnit.HOURS);

        assertTrue(object.isAfter);
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

        private ObjectWithInteger objectWithInteger;

        AddRunnable(ObjectWithInteger objectWithInteger) {
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

        private TimeService timeService;
        private ObjectWithData object;

        IsAfterRunnable(TimeService timeService, ObjectWithData object) {
            this.timeService = timeService;
            this.object = object;
        }

        @Override
        public void run() {
            if (object.localDateTime.isAfter(timeService.now())) {
                object.isAfter = true;
            }
        }
    }

}