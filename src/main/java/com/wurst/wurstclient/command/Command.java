package com.wurst.wurstclient.command;

import com.wurst.wurstclient.utilities.IMC;

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