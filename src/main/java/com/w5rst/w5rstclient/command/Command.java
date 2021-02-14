package com.w5rst.w5rstclient.command;

import com.w5rst.w5rstclient.utilities.IMC;

public abstract class Command implements Fire, IMC {
    private final String[] names;

    private final String description;

    public Command(String[] names, String description) {
        this.names = names;
        this.description = description;
    }

    public String[] getNames() {
        return this.names;
    }

    public String getDescription() {
        return this.description;
    }
}