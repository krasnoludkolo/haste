package io.haste.event;

import io.haste.TimeSource;

import java.time.LocalDateTime;

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