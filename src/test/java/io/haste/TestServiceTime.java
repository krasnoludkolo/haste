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
        Foo foo = new Foo(); // object with int value
        FooAdd runnable = new FooAdd(foo); // add one to object value
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        TestTimeService timeService = TimeService.createTimeServiceForTests(instant,zoneId);

        ScheduledFuture schedule1 = timeService.schedule(runnable, 1, TimeUnit.SECONDS);
        ScheduledFuture schedule2 = timeService.schedule(runnable, 2, TimeUnit.SECONDS);
        ScheduledFuture schedule3 = timeService.schedule(runnable, 3, TimeUnit.SECONDS);
        ScheduledFuture schedule4 = timeService.schedule(runnable, 5, TimeUnit.SECONDS);

        timeService.hackIntoFuture(4,TimeUnit.SECONDS);

        assertEquals(3,foo.getA());
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
        Foo foo = new Foo();
        FooAdd runnable = new FooAdd(foo);
        TestTimeService timeService = TestTimeService.withDefaultClock();

        ScheduledFuture schedule1 = timeService.schedule(runnable, 1, TimeUnit.HOURS);
        schedule1.cancel(true);
        timeService.hackIntoFuture(4,TimeUnit.HOURS);

        assertEquals(0,foo.getA());
    }

    @Test
    public void shouldRunOnceScheduledJob(){
        Foo foo = new Foo();
        FooAdd runnable = new FooAdd(foo);
        TestTimeService timeService = TimeService.createTimeServiceForTestsWithCurrentTime();

        timeService.schedule(runnable, 1, TimeUnit.SECONDS);
        timeService.hackIntoFuture(4,TimeUnit.SECONDS);
        timeService.hackIntoFuture(4,TimeUnit.SECONDS);

        assertEquals(1,foo.getA());
    }

    class Foo {
        private int a = 0;

        void add() {
            a++;
        }

        int getA() {
            return a;
        }
    }

    class FooAdd implements Runnable {

        private Foo foo;

        FooAdd(Foo foo) {
            this.foo = foo;
        }

        @Override
        public void run() {
            foo.add();
        }
    }
}