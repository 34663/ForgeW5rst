package com.w5rst.w5rstclient.module.impl;

import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.module.Module;
import com.w5rst.w5rstclient.event.impl.UpdateEvent;
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
