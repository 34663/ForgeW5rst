package com.w5rst.w5rstclient.mixin.client.block;

import com.w5rst.w5rstclient.event.EventType;
import com.w5rst.w5rstclient.event.impl.EntityCollisionEvent;
import com.w5rst.w5rstclient.utilities.IMC;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Block.class)
@SideOnly(Side.CLIENT)
public class MixinBlock {
    /**
     * @author weary
     * @reason weary
     */
    @Overwrite
    protected static void addCollisionBoxToList(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox) {
        if (blockBox != null) {
            EntityCollisionEvent entityCollisionEvent = new EntityCollisionEvent();
            AxisAlignedBB axisalignedbb = blockBox.offset(pos);

            if (IMC.mc.world != null) {
                entityCollisionEvent.fire(IMC.mc.world.getBlockState(pos).getBlock(), pos, axisalignedbb, EventType.PRE).call();
                axisalignedbb = entityCollisionEvent.getBoundingBox();
            }

            if (axisalignedbb != null && entityBox.intersects(axisalignedbb)) {
                collidingBoxes.add(axisalignedbb);
            }
        }
    }
}
