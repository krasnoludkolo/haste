[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.krasnoludkolo/haste/badge.png)](https://maven-badges.herokuapp.com/maven-central/io.github.krasnoludkolo/haste)
[![Build Status](https://travis-ci.org/krasnoludkolo/haste.svg?branch=master)](https://travis-ci.org/krasnoludkolo/haste)
[![codecov](https://codecov.io/gh/krasnoludkolo/haste/branch/master/graph/badge.svg)](https://codecov.io/gh/krasnoludkolo/haste)

# haste
A small library for testing time-related stuff

## Goals and motivation

In a few applications I was struggling with time aspect during tests. 
It's not hard to write some kind of proxy or mocks to provide a proper
date but it is annoying to write them every time. 
Here comes the idea to create an open source library to help write tests
 based on the passage of time and also to help write more testable systems.

## Usage

```xml
<dependency>
    <groupId>io.github.krasnoludkolo</groupId>
    <artifactId>haste</artifactId>
    <version>0.2.2</version>
</dependency>
```
```groovy
compile 'io.github.krasnoludkolo:haste:0.2.2'
```


## Features

### TL;DR
<i>Haste</i> provides:
- the implementation of [ScheduledExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html)
with ```advanceTimeBy``` method to simulate lapse of time.
- the interface ```TimeSource``` to create abstraction over time
### Full version

##### Test scheduled tasks
Some actions in your system may also plan another actions to be done in the future. 
E.g. when you add a sport fixture you may want to check the result after it has finished
When using normal java scheduler it is hard to scheduledExecutorServiceWithMovableTime results of scheduled jobs without e.g. mocking. 
Here comes the implementation of ScheduledExecutorService with ```advanceTimeBy``` method.
 
 ```java
class FooTest{
    
        private static final Runnable EMPTY_RUNNABLE = () -> {};
        private static final Callable<Integer> RETURN_ONE_CALLABLE = () -> 1;
    
        @Test
        void shouldExecuteAllScheduledJobs() throws ExecutionException {
            var executorService = Haste.ScheduledExecutionService.withFixedClockFromNow();
    
            var = executorService.schedule(RETURN_ONE_CALLABLE, 1, TimeUnit.SECONDS);
            var schedule2 = executorService.schedule(EMPTY_RUNNABLE, 2, TimeUnit.SECONDS);
            var schedule3 = executorService.schedule(EMPTY_RUNNABLE, 3, TimeUnit.SECONDS);
            var schedule4 = executorService.schedule(EMPTY_RUNNABLE, 5, TimeUnit.SECONDS);
    
            executorService.advanceTimeBy(4, TimeUnit.SECONDS);
    
            assertEquals(Integer.valueOf(1), schedule1.get()); //not null
            
            assertTrue(schedule1.isDone()); 
            assertTrue(schedule2.isDone());
            assertTrue(schedule3.isDone());
            
            assertFalse(schedule4.isDone());
        }

}
```

##### Get access to current time
With <i>Haste</i> comes the fallowing interface 
```java
public interface TimeSource {
    LocalDateTime now();
    //and more
}
```

###### Standalone time source

If you only need access to current time, without whole `ScheduledExecutionService` staff, you can use `MovableTimeSource` 
which extends `TimeSource` interface. It simply works like in example

```java
class MovableTimeSourceTest {

    @Test
    void shouldNowReturnTimeWithOffset() {
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock clock = Clock.fixed(instant, zoneId);
        MovableTimeSource timeSource = Haste.TimeSource.withFixedClock(clock);

        timeSource.advanceTimeBy(1, TimeUnit.HOURS);

        LocalDateTime now = timeSource.now();

        LocalDateTime expected = LocalDateTime.now(clock).plusHours(1);
        assertEquals(expected, now);

    }

}

```

###### ScheduledExecutionService as time source
```BlockingScheduledExecutionService``` from <i>Haste</i> implements that interface so you can obtain 'moved' 
time like in example

```java

class Event{

    private LocalDateTime eventTime;
    private TimeSource timeSource;

    Event(LocalDateTime eventTime, TimeSource timeSource) {
        if (timeSource.now().isAfter(eventTime)) {
            throw new IllegalArgumentException("Cannot make event in past");
        }
        this.eventTime = eventTime;
        this.timeSource = timeSource;
    }

    boolean hasAlreadyBegun(){
        return timeSource.now().isAfter(eventTime);
    }

}
```
```java

class EventTest {

    @Test
    void shouldEventStartsAfterStartDate() {
        BlockingScheduledExecutionService service = BlockingScheduledExecutionService.withFixedClockFromNow();
        LocalDateTime eventTime = LocalDateTime.now().plusHours(1);
        Event event = new Event(eventTime, service);
        
        service.advanceTimeBy(2, TimeUnit.HOURS);
        
        assertTrue(event.hasAlreadyBegun());
    }
}
```
## Disclaimer
Keep in mind that Haste is in early-alpha phase which means that some API details may change between versions.
