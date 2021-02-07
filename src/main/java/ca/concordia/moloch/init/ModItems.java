package ca.concordia.moloch.init;

import ca.concordia.moloch.MolochMod;
import ca.concordia.moloch.Resources;
import ca.concordia.moloch.item.OperatorKey;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Resources.MOD_ID);

    public static final RegistryObject<Item> MOLOCH_BLOCK = ITEMS.register("moloch",
            () -> new BlockItem(ModBlocks.MOLOCH.get(), new Item.Properties().group(MolochMod.ITEM_GROUP)));

    public static final RegistryObject<Item> OPERATOR_KEY = ITEMS.register("operator_key", OperatorKey::new);
}
