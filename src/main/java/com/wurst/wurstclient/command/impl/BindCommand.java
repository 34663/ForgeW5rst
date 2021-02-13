package com.wurst.wurstclient.command.impl;

import com.wurst.wurstclient.Wurst;
import com.wurst.wurstclient.command.Command;
import com.wurst.wurstclient.module.Module;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {
    public BindCommand(String[] names, String description) {
        super(names, description);
    }

    public void fire(String[] args) {
        if (args != null && args.length == 2) {
            Module module = Wurst.getModuleManager().getClazzString(args[0]);
            if (module != null) {
                int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                module.setKeyCode(key);
                Wurst.getSaveLoad().saveFile();
                mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(module.getName() + " bind to " + module.getKeyCode() + "."));
            } else {
                mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("Module was not found"));
            }
        } else {
            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(" .bind <Module> <Key>"));
        }
    }
}
