package com.wurst.wurstclient.mixin.client;

import com.wurst.wurstclient.Wurst;
import com.wurst.wurstclient.event.impl.RightClickMouseEvent;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "rightClickMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I"), cancellable = true)
    private void rightClickMouse(CallbackInfo ci) {
        RightClickMouseEvent rightClickMouseEvent = new RightClickMouseEvent();
        if (rightClickMouseEvent.call().isCancelled()) {
            ci.cancel();
        }
    }
}
