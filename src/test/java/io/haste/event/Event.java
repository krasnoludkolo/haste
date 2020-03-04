package io.haste.event;

import io.haste.TimeSource;

import java.time.ZonedDateTime;

class Event{

    private ZonedDateTime eventTime;
    private TimeSource timeSource;

    Event(ZonedDateTime eventTime, TimeSource timeSource) {
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