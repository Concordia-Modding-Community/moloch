package ca.concordia.moloch.init;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.tileentity.MolochTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister
            .create(ForgeRegistries.TILE_ENTITIES, Resources.MOD_ID);

    public static final RegistryObject<TileEntityType<?>> MOLOCH = TILE_ENTITIES.register("moloch",
            () -> TileEntityType.Builder.create(MolochTileEntity::new, ModBlocks.MOLOCH.get()).build(null));
}
