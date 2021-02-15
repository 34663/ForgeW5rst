package com.w5rst.w5rstclient.event.impl;

import com.w5rst.w5rstclient.event.Event;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private Packet<?> packet;
    private boolean outgoing;

    public PacketEvent fire(Packet<?> packet, boolean outgoing) {
        this.packet = packet;
        this.outgoing = outgoing;
        return this;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public boolean isOutgoing() {
        return this.outgoing;
    }

    public boolean isIncoming() {
        return !this.outgoing;
    }
}
