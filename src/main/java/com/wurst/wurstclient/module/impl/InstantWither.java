package com.wurst.wurstclient.module.impl;

import com.wurst.wurstclient.Wurst;
import com.wurst.wurstclient.event.Event;
import com.wurst.wurstclient.event.impl.RightClickMouseEvent;
import com.wurst.wurstclient.event.impl.UpdateEvent;
import com.wurst.wurstclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
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
    private int currentSlot;

    public InstantWither(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        soulSand = false;
        skull = false;
        currentSlot = -1;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof RightClickMouseEvent) {
            if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getMaterial() == Material.AIR) {
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
                    soulSand = true;
                    slot1 = slot;
                    Wurst.getLogger().info(String.format("[ForgeWurst] Found SoulSand(%s)", slot));
                }
                if (itemStack.getItem() == itemSkull && !skull) {
                    skull = true;
                    slot2 = slot;
                    Wurst.getLogger().info(String.format("[ForgeWurst] Found Skull(%s)", slot));
                }
            }

            if (soulSand && slot1 != -1) {
                Wurst.getLogger().info("[ForgeWurst] SoulSand Process...");

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

                soulSand = false;
            }

            if (skull && slot2 != -1) {
                Wurst.getLogger().info("[ForgeWurst] Skull Process...");

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

                skull = false;
            }

            mc.player.inventory.currentItem = currentSlot;
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
