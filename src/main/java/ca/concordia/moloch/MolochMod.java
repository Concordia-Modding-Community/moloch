package ca.concordia.moloch;

import ca.concordia.moloch.client.gui.MolochOPScreen;
import ca.concordia.moloch.client.gui.MolochScreen;
import ca.concordia.moloch.init.ModBlocks;
import ca.concordia.moloch.init.ModContainers;
import ca.concordia.moloch.init.ModItems;
import ca.concordia.moloch.init.ModPacketHandler;
import ca.concordia.moloch.init.ModTileEntities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Resources.MOD_ID)
public class MolochMod {
    public MolochMod() {
        IEventBus forgeBus = FMLJavaModLoadingContext.get().getModEventBus();
        forgeBus.addListener(EventPriority.NORMAL, this::clientSetup);
        forgeBus.addListener(EventPriority.NORMAL, this::serverSetup);

        new Config();

        ModBlocks.BLOCKS.register(forgeBus);
        ModTileEntities.TILE_ENTITIES.register(forgeBus);
        ModItems.ITEMS.register(forgeBus);
        ModContainers.CONTAINERS.register(forgeBus);
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        ModPacketHandler.registerClient();
        ScreenManager.registerFactory(ModContainers.MOLOCH.get(), MolochScreen::new);
        ScreenManager.registerFactory(ModContainers.MOLOCH_OP.get(), MolochOPScreen::new);
    }

    public void serverSetup(final FMLDedicatedServerSetupEvent event) {
        ModPacketHandler.registerServer();
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup("moloch") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.MOLOCH_BLOCK.get());
        }
    };
}
