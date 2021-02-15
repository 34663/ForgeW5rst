package com.w5rst.w5rstclient.event.impl;

import com.w5rst.w5rstclient.event.Event;

public class MoveEvent extends Event {
    private double x;
    private double y;
    private double z;

    public MoveEvent fire(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
