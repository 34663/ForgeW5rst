package com.w5rst.w5rstclient.module.impl;

import com.w5rst.w5rstclient.W5rst;
import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.module.Module;
import com.w5rst.w5rstclient.utilities.IMC;
import com.w5rst.w5rstclient.event.impl.UpdateEvent;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

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

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdateEvent) {
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
                    pos = new BlockPos(IMC.mc.player.getPosition()).add(random.nextInt(bound) - range, random.nextInt(bound) - range, random.nextInt(bound) - range);
                } while (++attempts < 128 && --delay < 0 && !tryToPlaceBlock(pos));
            } catch (Exception e) {
                W5rst.WriteLine("[ForgeWurst] " + e.getMessage());
            }
        }
    }

    private boolean tryToPlaceBlock(BlockPos pos) {
        // Check if it can be replaced.
        if (!IMC.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }

        this.placeBlock(pos);
        return false;
    }

    public void placeBlock(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(IMC.mc.player.posX, IMC.mc.player.posY + IMC.mc.player.getEyeHeight(), IMC.mc.player.posZ);
        final Vec3d posVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);

        for (EnumFacing facing : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(facing);

            // Check if it can be placed.
            if (IMC.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(IMC.mc.world.getBlockState(pos), false)) {
                final Vec3d dirVec = new Vec3d(facing.getDirectionVec());
                final Vec3d hitVec = posVec.add(dirVec.scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                    if (distanceSqPosVec <= eyesPos.squareDistanceTo(posVec.add(dirVec))) {
                        if (IMC.mc.world.rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                            float[] rotations = getNeededRotations(hitVec);
                            // ServerSide Rotation
                            IMC.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], IMC.mc.player.onGround));

                            // Click
                            IMC.mc.playerController.processRightClickBlock(IMC.mc.player, IMC.mc.world, neighbor, facing.getOpposite(), hitVec, EnumHand.MAIN_HAND);

                            // Send Swing packets
                            IMC.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                            this.delay = 3.0F;
                        }
                    }
                }
            }
        }
    }

    private boolean checkHeldItem() {
        ItemStack stack = IMC.mc.player.inventory.getCurrentItem();
        return !stack.isEmpty() && stack.getItem() instanceof ItemBlock;
    }

    public static float[] getNeededRotations(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(IMC.mc.player.posX, IMC.mc.player.posY + IMC.mc.player.getEyeHeight(), IMC.mc.player.posZ);
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[]{ IMC.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - IMC.mc.player.rotationYaw), IMC.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - IMC.mc.player.rotationPitch) };
    }
}
