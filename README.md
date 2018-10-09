# haste
A lightweight library for time management in java applications.

## Goals and motivation

In few application I was struggling with time aspect during test. It's not hard to write some kind of proxy or mocks to provide properly date but is annoying to write then every time. 
Here comes idea to create open source library to help writing tests based on the passage of time and also to writing more testable systems.
## Features

### TL;DR
<i>Haste</i> provides following `TimeService` interface
```java
public interface TimeService {
    LocalDateTime now();
    ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit timeUnit);
}
```
which has dwo implementation:
* <i>production</i>: based on system clock and default java `ScheduledExecutorService`
* <i>test</i>: based on changeable clock and scheduler prepared for making 'time jumping' 

It gives more control over time during tests. 

### Full version

##### Time providing

Consider simple class:
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

So, how to test `hasAlreadyBegun()` method? You cannot simply create the object and call method because it requires future date. So you need to wait and finally check it which isn't really recommended for fast unit tests.
You can provide current date as parameter but if your system has many layers it could be messy.

Instead of it you can treat time as external dependency. Let's modify the above class
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
So, now you can simply use implementation of TimeService prepared for tests:
```java
class EventTest{
    
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
Some actions in your system may also plan some actions to be done in future. Eg. adding sport fixture you may want to check the result after it has finished
Using normal java scheduler is hard to test results of scheduler jobs without eg. mocking. 
Here comes 

`ScheduledFuture schedule(Runnable runnable, long delay, TimeUnit timeUnit);`
 
 method from `TimeService` provider. In 'production' implementation acts like normal java scheduler but in 'Test' implementation you can (almost) instantly see results of your actions. 
 
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

        timeService.hackIntoFuture(4,TimeUnit.HOURS);

        assertEquals(3,foo.getA());
        assertTrue(schedule1.isDone());
        assertTrue(schedule2.isDone());
        assertTrue(schedule3.isDone());
        assertFalse(schedule4.isDone());
    }

}
```

## Disclaimer
Keep in mind that Haste in early-alpha phase which means that some API details may change between versions.
