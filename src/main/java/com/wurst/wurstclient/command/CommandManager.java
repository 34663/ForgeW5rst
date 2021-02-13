package com.wurst.wurstclient.command;

import com.wurst.wurstclient.command.impl.BindCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    public static final List<Command> commandMap = new ArrayList<>();
    public static final List<Command> list = new ArrayList<>();

    public void initialize() {
        addCommand(new BindCommand(new String[]{"bind"}, ""));
    }

    public void addCommand(Command command) {
        list.add(command);
        int var3 = (command.getNames()).length;
        for (int var4 = 0; var4 < var3; var4++)
            commandMap.add(command);
    }

    public static Command getCommand(String name) {
        for (Command command : commandMap) {
            for (String usage : command.getNames()) {
                if (usage.equalsIgnoreCase(name)) {
                    return command;
                }
            }
        }
        return null;
    }
}
