package io.haste.event;

import io.haste.BlockingScheduledExecutionService;
import io.haste.Haste;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTest {

    @Test
    void shouldEventStartsAfterStartDate() {
        //given
        BlockingScheduledExecutionService service = Haste.withFixedClockFromNow();
        LocalDateTime eventTime = LocalDateTime.now().plusHours(1);
        Event event = new Event(eventTime, service);
        //when
        service.advanceTimeBy(2, TimeUnit.HOURS);
        //then
        assertTrue(event.hasAlreadyBegun());
    }
}
