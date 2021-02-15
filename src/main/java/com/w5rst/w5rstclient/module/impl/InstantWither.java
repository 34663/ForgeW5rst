package com.w5rst.w5rstclient.module.impl;

import com.w5rst.w5rstclient.W5rst;
import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.event.impl.RightClickMouseEvent;
import com.w5rst.w5rstclient.event.impl.UpdateEvent;
import com.w5rst.w5rstclient.module.Module;
import com.w5rst.w5rstclient.utilities.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

import java.util.ArrayList;

public class InstantWither extends Module {
    private final ArrayList<BlockPos> positions = new ArrayList<>();
    private final ArrayList<BlockPos> badpositions = new ArrayList<>();
    private boolean soulSand, skull;
    private int currentSlot, delay, sandStackCount, skullStackCount;

    public InstantWither(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.positions.clear();
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

            // StackCountCheck
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

            // BlockCheck
            this.badpositions.clear();
            BlockPos[] badPos = new BlockPos[]{startPos.up(0).offset(front, 0).offset(left, 1), startPos.up(0).offset(front, 0).offset(left, -1)};
            for (BlockPos pos : badPos) {
                IBlockState badBlockState = mc.world.getBlockState(pos);
                if (badBlockState.getMaterial() != Material.AIR) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Place Blocks
            this.positions.clear();
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
                            Utils.placeBlock(pos);
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
                            Utils.placeBlock(pos);
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
}
