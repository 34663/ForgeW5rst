package com.w5rst.w5rstclient.mixin.client.entity;

import com.mojang.authlib.GameProfile;
import com.w5rst.w5rstclient.event.EventType;
import com.w5rst.w5rstclient.event.impl.MoveEvent;
import com.w5rst.w5rstclient.event.impl.UpdateEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
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
    private void PreUpdateWalkingPlayer(CallbackInfo ci) {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.fire(EventType.PRE).call();
        if (updateEvent.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    private void PostUpdateWalkingPlayer(CallbackInfo ci) {
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.fire(EventType.POST).call();
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        MoveEvent moveEvent = new MoveEvent();
        moveEvent.fire(x, y, z).call();
        if (moveEvent.isCancelled()) {
            return;
        }

        super.move(type, moveEvent.getX(), moveEvent.getY(), moveEvent.getZ());
        ci.cancel();
    }
}
