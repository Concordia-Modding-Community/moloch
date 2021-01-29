package ca.concordia.moloch.init;

import ca.concordia.moloch.Resources;
import ca.concordia.moloch.block.MolochBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            Resources.MOD_ID);

    public static final RegistryObject<Block> MOLOCH = BLOCKS.register("moloch", MolochBlock::new);
}
