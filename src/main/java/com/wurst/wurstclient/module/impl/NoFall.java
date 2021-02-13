package com.wurst.wurstclient.module.impl;

import com.wurst.wurstclient.event.Event;
import com.wurst.wurstclient.event.impl.UpdateEvent;
import com.wurst.wurstclient.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoFall extends Module {
    public NoFall(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdateEvent) {
            if (mc.player.fallDistance > 3.0F) {
                mc.player.connection.sendPacket(new CPacketPlayer(true));
            }
        }
    }
}
