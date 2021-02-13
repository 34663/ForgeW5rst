package com.wurst.wurstclient.module;

import com.wurst.wurstclient.Wurst;
import com.wurst.wurstclient.event.Listener;
import com.wurst.wurstclient.utilities.IMC;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public abstract class Module implements IMC, Listener {
    public String name;
    public int keyCode;
    public boolean enabled;

    public Module(String name, int keyCode) {
        this.name = name;
        this.keyCode = keyCode;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (enabled) {
            MinecraftForge.EVENT_BUS.register(this);
            Wurst.getEventManager().register(this);
            onEnabled();
            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("Enabled " + this.getName()));
        } else {
            MinecraftForge.EVENT_BUS.unregister(this);
            Wurst.getEventManager().unregister(this);
            onDisabled();
            mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("Disabled " + this.getName()));
        }
    }

    public void onEnabled() {}
    public void onDisabled() {}

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}
