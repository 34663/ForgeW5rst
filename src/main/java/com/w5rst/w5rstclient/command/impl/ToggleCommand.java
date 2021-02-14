package com.w5rst.w5rstclient.command.impl;

import com.w5rst.w5rstclient.W5rst;
import com.w5rst.w5rstclient.module.Module;
import com.w5rst.w5rstclient.command.Command;

public class ToggleCommand extends Command {
    public ToggleCommand(String[] names, String description) {
        super(names, description);
    }

    @Override
    public void fire(String[] args) {
        if (args == null) {
            W5rst.WriteChat(" -toggle <Module>");
            return;
        }
        if (args.length == 1) {
            Module module = W5rst.getModuleManager().getClazzString(args[0]);
            if (module != null) {
                module.toggle();
                W5rst.WriteChat(module.getName() + " was toggled.");
            } else {
                W5rst.WriteChat("Module was not found");
            }
        } else {
            W5rst.WriteChat(" -toggle <Module>");
        }
    }
}
