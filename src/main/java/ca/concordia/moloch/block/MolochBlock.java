package ca.concordia.moloch.block;

import ca.concordia.moloch.init.ModTileEntities;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class MolochBlock extends Block {
    public MolochBlock() {
        super(Block.Properties.create(Material.ROCK).hardnessAndResistance(100).setLightLevel(blockState -> 1));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.MOLOCH.get().create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos blockPos, PlayerEntity player,
            Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (world.isRemote)
            return ActionResultType.FAIL;

        TileEntity tile = world.getTileEntity(blockPos);

        if (!(tile instanceof MolochTileEntity))
            return ActionResultType.FAIL;

        NetworkHooks.openGui((ServerPlayerEntity) player, (MolochTileEntity) tile, blockPos);

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() == newState.getBlock())
            return;

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof MolochTileEntity) {
            tileEntity.remove();
        }
    }
}
