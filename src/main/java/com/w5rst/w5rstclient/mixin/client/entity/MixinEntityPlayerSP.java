package com.w5rst.w5rstclient.mixin.client.entity;

import com.mojang.authlib.GameProfile;
import com.w5rst.w5rstclient.event.impl.UpdateEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends EntityPlayer {
    public MixinEntityPlayerSP(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayer(CallbackInfo ci) {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.call();

        if (updateEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
