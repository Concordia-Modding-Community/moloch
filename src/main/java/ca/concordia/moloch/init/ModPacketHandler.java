package ca.concordia.moloch.init;

import java.util.Optional;

import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.network.TestMoloch;
import ca.concordia.moloch.network.UpdateMoloch;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Resources.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
    );

    @OnlyIn(Dist.CLIENT)
    public static void registerClient() {
        int id = 0;

        HANDLER.registerMessage(id++, UpdateMoloch.class, UpdateMoloch::encode, UpdateMoloch::decode, UpdateMoloch::handleClient, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void registerServer() {
        int id = 0;

        HANDLER.registerMessage(id++, UpdateMoloch.class, UpdateMoloch::encode, UpdateMoloch::decode, UpdateMoloch::handleServer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }

    public static void sendToPlayers(ServerWorld world, Object msg) {
        for(ServerPlayerEntity player : world.getPlayers()) {
            HANDLER.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }
}
