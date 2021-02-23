package ca.concordia.moloch.network;

import java.util.function.Supplier;

import ca.concordia.moloch.tileentity.MolochTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class TestMoloch {
    private final BlockPos blockPos;

    public TestMoloch(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public static void encode(TestMoloch msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.blockPos);
    }

    public static TestMoloch decode(PacketBuffer buffer) {
        BlockPos blockPos = buffer.readBlockPos();

        return new TestMoloch(blockPos);
    }

    public static void handle(final TestMoloch msg, final Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide().isClient()) {
            return;
        }

        ctx.get().enqueueWork(() -> {
            ServerWorld world = ctx.get().getSender().getServerWorld();
            
            TileEntity tileEntity = world.getTileEntity(msg.blockPos);

            if(!(tileEntity instanceof MolochTileEntity)) {
                return;
            }

            MolochTileEntity molochTileEntity = (MolochTileEntity) tileEntity;

            molochTileEntity.getProgression().performReward(molochTileEntity.getPos(), world);
        });
    }
}
