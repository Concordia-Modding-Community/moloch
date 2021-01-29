package ca.concordia.moloch;

import ca.concordia.moloch.init.ModBlocks;
import ca.concordia.moloch.init.ModItems;
import ca.concordia.moloch.init.ModTileEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Resources.MOD_ID)
public class MolochMod {
    public MolochMod() {
        IEventBus forgeBus = FMLJavaModLoadingContext.get().getModEventBus();

        new Config();

        ModBlocks.BLOCKS.register(forgeBus);
        ModTileEntities.TILE_ENTITIES.register(forgeBus);
        ModItems.ITEMS.register(forgeBus);
    }
}
