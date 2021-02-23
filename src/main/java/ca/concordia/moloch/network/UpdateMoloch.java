package ca.concordia.moloch.network;

import java.util.function.Supplier;

import ca.concordia.moloch.tileentity.MolochTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateMoloch {
    private final BlockPos blockPos;
    private final ITextComponent name;

    public UpdateMoloch(BlockPos blockPos, ITextComponent name) {
        this.blockPos = blockPos;
        this.name = name;
    }

    public static void encode(UpdateMoloch msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.blockPos);
        buffer.writeString(ITextComponent.Serializer.toJson(msg.name));
    }

    public static UpdateMoloch decode(PacketBuffer buffer) {
        return new UpdateMoloch(
            buffer.readBlockPos(), 
            ITextComponent.Serializer.getComponentFromJson(buffer.readString())
        );
    }

    public static void handle(final UpdateMoloch msg, final Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide().isClient()) {
            return;
        }

        ctx.get().enqueueWork(() -> {
            World world = ctx.get().getSender().world;
            
            TileEntity tileEntity = world.getTileEntity(msg.blockPos);

            if(!(tileEntity instanceof MolochTileEntity)) {
                return;
            }

            MolochTileEntity molochTileEntity = (MolochTileEntity) tileEntity;

            molochTileEntity.setCustomName(msg.name);
        });
    }
}
