package io.haste.event;

import io.haste.TimeService;

import java.time.LocalDateTime;

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
        return timeService.now().isAfter(eventTime);
    }

}