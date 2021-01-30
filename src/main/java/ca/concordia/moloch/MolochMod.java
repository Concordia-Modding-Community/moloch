package ca.concordia.moloch;

import ca.concordia.moloch.client.gui.MolochScreen;
import ca.concordia.moloch.init.ModBlocks;
import ca.concordia.moloch.init.ModContainers;
import ca.concordia.moloch.init.ModItems;
import ca.concordia.moloch.init.ModTileEntities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Resources.MOD_ID)
public class MolochMod {
    public MolochMod() {
        IEventBus forgeBus = FMLJavaModLoadingContext.get().getModEventBus();
        forgeBus.addListener(EventPriority.NORMAL, this::setup);

        new Config();

        ModBlocks.BLOCKS.register(forgeBus);
        ModTileEntities.TILE_ENTITIES.register(forgeBus);
        ModItems.ITEMS.register(forgeBus);
        ModContainers.CONTAINERS.register(forgeBus);
    }

    public void setup(final FMLCommonSetupEvent event) {
        ScreenManager.registerFactory(ModContainers.MOLOCH.get(), MolochScreen::new);
    }
}
