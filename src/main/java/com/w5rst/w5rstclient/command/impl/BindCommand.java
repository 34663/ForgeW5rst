package com.w5rst.w5rstclient.command.impl;

import com.w5rst.w5rstclient.W5rst;
import com.w5rst.w5rstclient.command.Command;
import com.w5rst.w5rstclient.module.Module;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {
    public BindCommand(String[] names, String description) {
        super(names, description);
    }

    public void fire(String[] args) {
        if (args != null && args.length == 2) {
            Module module = W5rst.getModuleManager().getClazzString(args[0]);
            if (module != null) {
                int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                module.setKeyCode(key);
                W5rst.getSaveLoad().saveFile();
                W5rst.WriteChat(module.getName() + " bind to " + module.getKeyCode() + ".");
            } else {
                W5rst.WriteChat("Module was not found");
            }
        } else {
            W5rst.WriteChat(" .bind <Module> <Key>");
        }
    }
}
