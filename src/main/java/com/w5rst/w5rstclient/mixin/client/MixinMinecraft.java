package com.w5rst.w5rstclient.mixin.client;

import com.w5rst.w5rstclient.event.impl.RightClickMouseEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
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
