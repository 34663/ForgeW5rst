package com.w5rst.w5rstclient.module.impl;

import com.w5rst.w5rstclient.W5rst;
import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.event.impl.UpdateEvent;
import com.w5rst.w5rstclient.module.Module;
import com.w5rst.w5rstclient.utilities.Timer;
import com.w5rst.w5rstclient.utilities.Utils;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class AutoHighway extends Module {
    private final ArrayList<BlockPos> positions = new ArrayList<>();

    public AutoHighway(String name, int keyCode) {
        super(name, keyCode);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.positions.clear();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdateEvent) {
            if (event.isPost()) {
                if (mc.player.onGround && !mc.player.inventory.getCurrentItem().isEmpty() && mc.player.inventory.getCurrentItem().getItem() instanceof ItemBlock) {
                    String face = Utils.GetFacing().getName().toLowerCase();
                    float targetYaw = 0;
                    if (face.equalsIgnoreCase("north")) {
                        targetYaw = 180;
                    }
                    if (face.equalsIgnoreCase("south")) {
                        targetYaw = 0;
                    }
                    if (face.equalsIgnoreCase("west")) {
                        targetYaw = 90;
                    }
                    if (face.equalsIgnoreCase("east")) {
                        targetYaw = -90;
                    }
                    mc.player.rotationYaw = targetYaw;

                    final Vec3d playerPos = Utils.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    BlockPos originPos = new BlockPos(playerPos.x, playerPos.y + 0.5f, playerPos.z);

                    if (positions.isEmpty()) {
                        switch (Utils.GetFacing()) {
                            case EAST:
                                positions.add(originPos.down());
                                positions.add(originPos.down().east());
                                positions.add(originPos.down().east().north());
                                positions.add(originPos.down().east().south());
                                positions.add(originPos.down().east().north().north());
                                positions.add(originPos.down().east().south().south());
                                positions.add(originPos.down().east().north().north().north());
                                positions.add(originPos.down().east().south().south().south());
                                positions.add(originPos.down().east().north().north().north().up());
                                positions.add(originPos.down().east().south().south().south().up());
                                break;
                            case NORTH:
                                positions.add(originPos.down());
                                positions.add(originPos.down().north());
                                positions.add(originPos.down().north().east());
                                positions.add(originPos.down().north().west());
                                positions.add(originPos.down().north().east().east());
                                positions.add(originPos.down().north().west().west());
                                positions.add(originPos.down().north().east().east().east());
                                positions.add(originPos.down().north().west().west().west());
                                positions.add(originPos.down().north().east().east().east().up());
                                positions.add(originPos.down().north().west().west().west().up());
                                break;
                            case SOUTH:
                                positions.add(originPos.down());
                                positions.add(originPos.down().south());
                                positions.add(originPos.down().south().east());
                                positions.add(originPos.down().south().west());
                                positions.add(originPos.down().south().east().east());
                                positions.add(originPos.down().south().west().west());
                                positions.add(originPos.down().south().east().east().east());
                                positions.add(originPos.down().south().west().west().west());
                                positions.add(originPos.down().south().east().east().east().up());
                                positions.add(originPos.down().south().west().west().west().up());
                                break;
                            case WEST:
                                positions.add(originPos.down());
                                positions.add(originPos.down().west());
                                positions.add(originPos.down().west().north());
                                positions.add(originPos.down().west().south());
                                positions.add(originPos.down().west().north().north());
                                positions.add(originPos.down().west().south().south());
                                positions.add(originPos.down().west().north().north().north());
                                positions.add(originPos.down().west().south().south().south());
                                positions.add(originPos.down().west().north().north().north().up());
                                positions.add(originPos.down().west().south().south().south().up());
                                break;
                        }
                    }

                    if (this.positions.size() <= 64) {
                        for (BlockPos pos : this.positions) {
                            if (mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                                Utils.placeBlock(pos);
                            }
                        }
                        this.positions.clear();
                    }
                }
            }
        }
    }
}
