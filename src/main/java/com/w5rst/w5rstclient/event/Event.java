package com.w5rst.w5rstclient.event;

import com.w5rst.w5rstclient.W5rst;

public abstract class Event {
    public boolean cancelled;

    public EventType type;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public EventType getType() {
        return this.type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public boolean isPre() {
        if (this.type == null)
            return false;
        return (this.type == EventType.PRE);
    }

    public boolean isPost() {
        if (this.type == null)
            return false;
        return (this.type == EventType.POST);
    }

    public Event call() {
        W5rst.getEventManager().call(this);
        return this;
    }
}