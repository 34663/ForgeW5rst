package com.w5rst.w5rstclient.module.impl;

import com.w5rst.w5rstclient.W5rst;
import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.event.impl.RightClickMouseEvent;
import com.w5rst.w5rstclient.event.impl.UpdateEvent;
import com.w5rst.w5rstclient.module.Module;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import java.util.ArrayList;

public class InstantWither extends Module {
    private final ArrayList<BlockPos> positions = new ArrayList<>();
    private boolean soulSand, skull;
    private int currentSlot, delay, sandStackCount, skullStackCount;

    public InstantWither(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        soulSand = false;
        skull = false;
        currentSlot = -1;
        delay = 0;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof RightClickMouseEvent) {
            if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getMaterial() == Material.AIR || delay > 0) {
                return;
            }

            BlockPos startPos = mc.objectMouseOver.getBlockPos().offset(mc.objectMouseOver.sideHit);
            EnumFacing front = mc.player.getHorizontalFacing();
            EnumFacing left = front.rotateYCCW();
            this.positions.clear();
            currentSlot = mc.player.inventory.currentItem;
            int i, slot1 = -1, slot2 = -1;
            int[][] offset;
            byte b;

            // ToolBar Check
            for (byte slot = 0; slot < 9; slot++) {
                ItemStack itemStack = this.mc.player.inventory.mainInventory.get(slot);
                Item itemSoulSand = Item.getItemFromBlock(Blocks.SOUL_SAND);
                Item itemSkull = Item.getItemById(397);

                if (itemStack.getItem() == itemSoulSand && !soulSand) {
                    sandStackCount += itemStack.getCount();
                    slot1 = slot;
                }

                if (itemStack.getItem() == itemSkull && !skull) {
                    skullStackCount += itemStack.getCount();
                    slot2 = slot;
                }
            }

            // ItemCheck
            if (sandStackCount >= 4) {
                soulSand = true;
            } else {
                W5rst.WriteChat("We don't have enough SoulSands.");
            }

            if (skullStackCount >= 4) {
                skull = true;
            } else {
                W5rst.WriteChat("We don't have enough Skulls.");
            }

            // Place Blocks
            if (soulSand && slot1 != -1) {
                offset = new int[][]{new int[3], {0, 1, 0}, {1, 1, 0}, {-1, 1, 0}};
                for (i = offset.length, b = 0; b < i; ) {
                    int[] pos = offset[b];
                    this.positions.add(startPos.up(pos[1]).offset(front, pos[2]).offset(left, pos[0]));
                    b++;
                }

                if (this.positions.size() <= 64) {
                    for (BlockPos pos : this.positions) {
                        if (mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                            mc.player.inventory.currentItem = slot1;
                            this.placeBlock(pos);
                        }
                    }
                }
            }

            this.positions.clear();
            if (skull && soulSand && slot2 != -1) {
                offset = new int[][]{new int[3], {1, 2, 0}, {0, 2, 0}, {-1, 2, 0}};
                for (i = offset.length, b = 0; b < i; ) {
                    int[] pos = offset[b];
                    this.positions.add(startPos.up(pos[1]).offset(front, pos[2]).offset(left, pos[0]));
                    b++;
                }

                if (this.positions.size() <= 64) {
                    for (BlockPos pos : this.positions) {
                        if (mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                            mc.player.inventory.currentItem = slot2;
                            this.placeBlock(pos);
                        }
                    }
                }
            }

            // End
            sandStackCount = 0;
            skullStackCount = 0;
            soulSand = false;
            skull = false;
            delay = 8;
            mc.player.inventory.currentItem = currentSlot;
        }
        if (event instanceof UpdateEvent) {
            delay--;
        }
    }

    public void placeBlock(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        final Vec3d posVec = new Vec3d(pos).add(0.5, 0.5, 0.5);

        for (EnumFacing facing : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(facing);
            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(pos), false)) {
                final Vec3d dirVec = new Vec3d(facing.getDirectionVec());
                final Vec3d hitVec = posVec.add(dirVec.scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= Math.pow(6.0, 2.0)) {
                    float[] rotations = getNeededRotations(hitVec);
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, facing.getOpposite(), hitVec, EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }
            }
        }
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
