package io.haste.event;

import io.haste.TestTimeService;
import io.haste.TimeService;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class EventTest{

    @Test
    public void shouldEventStartsAfterStartDate(){
        //given
        TestTimeService timeService = TimeService.createTimeServiceForTestsWithCurrentTime();
        LocalDateTime eventTime = LocalDateTime.now().plusHours(1);
        Event event = new Event(eventTime,timeService);
        //when
        timeService.hackIntoFuture(2, TimeUnit.HOURS); //Probably temporally method name ;)
        //then
        assertTrue(event.hasAlreadyBegun());
    }
}
