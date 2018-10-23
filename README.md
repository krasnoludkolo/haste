[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.krasnoludkolo/haste/badge.png)](https://maven-badges.herokuapp.com/maven-central/io.github.krasnoludkolo/haste)
[![Build Status](https://travis-ci.org/krasnoludkolo/haste.svg?branch=master)](https://travis-ci.org/krasnoludkolo/haste)

# haste
A lightweight library for time management in java applications.

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
    <version>0.0.2</version>
</dependency>
```
```groovy
compile 'io.github.krasnoludkolo:haste:0.0.2'
```


## Features

### TL;DR
<i>Haste</i> provides the following `TimeService` interface
```java
public interface TimeService {
    LocalDateTime now();
    ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit timeUnit);
}
```
which has two implementations:
* <i>production</i>: based on system clock and default java `ScheduledExecutorService`
* <i>test</i>: based on changeable clock and scheduler prepared for making 'time jumping' 

It gives you more control over time during tests. 

### Full version

##### Time providing

Consider a simple class:
```java
class Event{
    private LocalDateTime eventTime;
    
    Event(LocalDateTime eventTime){
        if(LocalDateTime.now().isAfter(eventTime)){
            throw new IllegalArgumentException("Cannot make event in past");
        }
        this.eventTime = eventTime;
    }
    
    boolean hasAlreadyBegun(){
        return LocalDateTime.now().isAfter(eventTime);
    }
    
}
```

So, how to test `hasAlreadyBegun()` method? You cannot simply create the object and call method because it requires future date.
 So you need to wait and finally check it which isn't really recommended for fast unit tests.
You can provide current date as parameter but if your system has many layers it could be messy.

Instead you can treat time as external dependency. 
Let's modify the above class:
```java
class Event{
    private LocalDateTime eventTime;
    private TimeService timeService;
    
    Event(LocalDateTime eventTime, TimeService timeService){
        if(timeService.now().isAfter(eventTime)){
            throw new IllegalArgumentException("Cannot make event in past");
        }
        this.eventTime = eventTime;
        this.timeService = timeService;
    }
    
    boolean hasAlreadyBegun(){
        return timeService.now().isAfter(matchStartDate);
    }
    
}
```
So, now you can simply use the implementation of TimeService prepared for tests:
```java
public class EventTest{
    
    @Test
    public void shouldEventStartsAfterStartDate(){
        //given
        TestTimeService timeService = TimeService.createTimeServiceForTestsWithCurrentTime();
        LocalDateTime eventTime = LocalDateTime.now().plusHours(1);
        Event event = new Event(eventTime,timeService);
        //when
        timeService.hackIntoFuture(2,TimeUnit.HOURS); //Probably temporally method name ;)
        //then
        assertTrue(event.hasAlreadyBegun());
    }
}
```


##### Test scheduled runnable
Some actions in your system may also plan another actions to be done in thebo future. 
E.g. when you add a sport fixture you may want to check the result after it has finished
When using normal java scheduler it is hard to test results of scheduled jobs without e.g. mocking. 
Here comes the


`ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit timeUnit);`
 
 method from `TimeService`. In 'production' implementation it acts like a normal java scheduler but in 'Test' 
 implementation you can (almost) instantly see the results of your actions. 
 
 ```java
class FooTest{
    
    @Test
    public void shouldExecuteAllScheduledJobs(){
        Foo foo = new Foo(); // object with int value
        FooAdd runnable = new FooAdd(foo); // add one to object value
        Instant instant = Instant.ofEpochMilli(0);
        ZoneId zoneId = ZoneId.systemDefault();
        TestTimeService timeService = TimeService.createTimeServiceForTests(instant,zoneId);

        ScheduledFuture schedule1 = timeService.schedule(runnable, 1, TimeUnit.HOURS);
        ScheduledFuture schedule2 = timeService.schedule(runnable, 2, TimeUnit.HOURS);
        ScheduledFuture schedule3 = timeService.schedule(runnable, 3, TimeUnit.HOURS);
        ScheduledFuture schedule4 = timeService.schedule(runnable, 5, TimeUnit.HOURS);

        timeService.hackIntoFuture(4,TimeUnit.HOURS); //Probably temporally method name ;)

        assertEquals(3,foo.getA());
        assertTrue(schedule1.isDone());
        assertTrue(schedule2.isDone());
        assertTrue(schedule3.isDone());
        assertFalse(schedule4.isDone());
    }

}
```

## Disclaimer
Keep in mind that Haste is in early-alpha phase which means that some API details may change between versions.
