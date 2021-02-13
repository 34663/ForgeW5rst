package com.wurst.wurstclient.module.impl;

import com.wurst.wurstclient.Wurst;
import com.wurst.wurstclient.module.Module;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class BuildRandom extends Module {
    private final Random random = new Random();
    private float delay;

    public BuildRandom(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.delay = 0;
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        int range = 5;
        int bound = range * 2 + 1;
        BlockPos pos;
        int attempts = 0;

        // Whether the item in your hand is a block or not.
        if (!checkHeldItem()) {
            return;
        }

        try {
            do {
                // Random pos
                pos = new BlockPos(mc.player.getPosition()).add(random.nextInt(bound) - range, random.nextInt(bound) - range, random.nextInt(bound) - range);
            } while (++attempts < 128 && --delay < 0 && !tryToPlaceBlock(pos));
        } catch (Exception e) {
            Wurst.getLogger().info("[ForgeWurst] " + e.getMessage());
        }
    }

    private boolean tryToPlaceBlock(BlockPos pos) {
        // Check if it can be replaced.
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }

        if (placeBlock(pos)) {
            return true;
        }

        return false;
    }

    public boolean placeBlock(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        final Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);

        for (EnumFacing facing : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(facing);

            // Check if it can be placed.
            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(pos), false)) {
                final Vec3d hitVec = posVec.add(new Vec3d(facing.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                    if (mc.world.rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                        float[] rotations = getNeededRotations(hitVec);
                        // ServerSide Rotation
                        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));

                        // Click
                        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, facing.getOpposite(), hitVec, EnumHand.MAIN_HAND);

                        // Send Swing packets
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        this.delay = 2.5F;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkHeldItem() {
        ItemStack stack = mc.player.inventory.getCurrentItem();
        return !stack.isEmpty() && stack.getItem() instanceof ItemBlock;
    }

    public static float[] getNeededRotations(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[]{ mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
    }
}
