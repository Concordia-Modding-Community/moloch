package ca.concordia.moloch.network;

import java.util.function.Supplier;

import ca.concordia.moloch.tileentity.MolochTileEntity;
import ca.concordia.moloch.tileentity.moloch.Progression;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateMoloch {
    private final BlockPos blockPos;
    private final ITextComponent name;
    private final Progression progression;

    public UpdateMoloch(BlockPos blockPos, ITextComponent name, Progression progression) {
        this.blockPos = blockPos;
        this.name = name;
        this.progression = progression;
    }

    public static void encode(UpdateMoloch msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.blockPos);
        buffer.writeString(ITextComponent.Serializer.toJson(msg.name));
        buffer.writeCompoundTag(msg.progression.serializeNBT());
    }

    public static UpdateMoloch decode(PacketBuffer buffer) {
        BlockPos blockPos = buffer.readBlockPos();
        ITextComponent name = ITextComponent.Serializer.getComponentFromJson(buffer.readString(32767));
        CompoundNBT progressionNBT = buffer.readCompoundTag();
        Progression progression = new Progression();

        progression.deserializeNBT(progressionNBT);

        return new UpdateMoloch(
            blockPos, 
            name,
            progression
        );
    }

    private static void updateMolochTileEntity(final UpdateMoloch msg, World world) {
        TileEntity tileEntity = world.getTileEntity(msg.blockPos);
    
        if(!(tileEntity instanceof MolochTileEntity)) {
            return;
        }

        MolochTileEntity molochTileEntity = (MolochTileEntity) tileEntity;

        molochTileEntity.setCustomName(msg.name);

        molochTileEntity.setProgression(msg.progression);
    }

    public static void handle(final UpdateMoloch msg, final Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide().isClient()) {
            handleClient(msg, ctx);
        } else {
            handleServer(msg, ctx);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(final UpdateMoloch msg, final Supplier<NetworkEvent.Context> ctx) {
        Minecraft instance = Minecraft.getInstance();

        updateMolochTileEntity(msg, instance.world);

        ctx.get().setPacketHandled(true);
    }

    private static void handleServer(final UpdateMoloch msg, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            World world = ctx.get().getSender().world;

            updateMolochTileEntity(msg, world);
        });

        ctx.get().setPacketHandled(true);
    }
}
