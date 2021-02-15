package com.w5rst.w5rstclient.event.impl;

import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.event.EventType;

public class UpdateEvent extends Event {
    public UpdateEvent fire(EventType type) {
        this.type = type;
        return this;
    }
}
