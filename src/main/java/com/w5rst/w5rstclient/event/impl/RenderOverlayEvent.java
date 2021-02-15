package com.w5rst.w5rstclient.event.impl;

import com.w5rst.w5rstclient.event.Event;

public class RenderOverlayEvent extends Event {
    private float partialTicks;

    public RenderOverlayEvent fire(float partialTicks) {
        this.partialTicks = partialTicks;
        return this;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
