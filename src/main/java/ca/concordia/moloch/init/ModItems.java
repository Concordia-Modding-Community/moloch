package ca.concordia.moloch.init;

import ca.concordia.moloch.Resources;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            Resources.MOD_ID);

    public static final RegistryObject<Item> MOLOCH_BLOCK = ITEMS.register("moloch",
            () -> new BlockItem(ModBlocks.MOLOCH.get(), new Item.Properties().group(ItemGroup.MISC)));
}
