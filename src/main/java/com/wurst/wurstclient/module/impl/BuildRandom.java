package com.wurst.wurstclient.module.impl;

import com.wurst.wurstclient.module.Module;
import com.wurst.wurstclient.utilities.RenderUtils;
import com.wurst.wurstclient.utilities.Timer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class BuildRandom extends Module {
    private final Random random = new Random();
    private int delay;

    public BuildRandom(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.delay = 0;
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.PlayerTickEvent event) {
        int range = 6;
        int bound = range * 2 + 1;
        BlockPos pos;
        int attempts = 0;

        // 手に持っているアイテムがブロックかどうか
        if (!checkHeldItem()) {
            return;
        }

        try {
            do {
                // ランダムなpos
                pos = new BlockPos(mc.player.getPosition()).add(random.nextInt(bound) - range, random.nextInt(bound) - range, random.nextInt(bound) - range);
            } while (++attempts < 128 && --delay < 0 && !tryToPlaceBlock(pos));
        } catch (Exception e) {
            System.out.println("[ForgeWurst] " + e.getMessage());
        }
    }

    private boolean tryToPlaceBlock(BlockPos pos) {
        // 置けるかチェック
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }

        if (!placeBlock(pos)) {
            return false;
        }
        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        this.delay = 4;
        return true;
    }

    public boolean placeBlock(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);

        for (EnumFacing facing : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(facing);

            // 置けるかチェック2
            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(pos), false)) {
                final Vec3d hitVec = posVec.add(new Vec3d(facing.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, facing.getOpposite(), hitVec, EnumHand.MAIN_HAND);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkHeldItem() {
        ItemStack stack = mc.player.inventory.getCurrentItem();
        return !stack.isEmpty() && stack.getItem() instanceof ItemBlock;
    }
}
