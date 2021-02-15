package com.w5rst.w5rstclient.event.impl;

import com.w5rst.w5rstclient.event.Event;
import com.w5rst.w5rstclient.event.EventType;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityCollisionEvent extends Event {
    private Block block;
    private BlockPos blockPos;
    private AxisAlignedBB boundingBox;

    public EntityCollisionEvent fire(final Block block, final BlockPos pos, final AxisAlignedBB boundingBox, EventType type) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
        this.type = type;
        return this;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
